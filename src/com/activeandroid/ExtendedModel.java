package com.activeandroid;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.content.ContentProvider;
import com.activeandroid.filler.Filler;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public abstract class ExtendedModel extends Model implements Parcelable {
    public static HashMap<Class, ArrayList<Filler>> fastSaveCache = new HashMap<>();
    public static Constructor<ContentValues> contentValuesConstructor;

    public ExtendedModel() {
        super();
    }

    public ExtendedModel(Long id) {
        this();
        setAaId(id);
    }

    public void setAaId(Long id) {
        mId = id;
    }

    protected Long fastSave() {
        // return super.save();
        HashMap<String, Object> mapValues = new HashMap<>();
        Class<? extends ExtendedModel> clazz = this.getClass();

        ArrayList<Filler> list = fastSaveCache.get(clazz);
        if (list == null) {
            //Log.d("FAST SAVE", "CREATE NEW FILLERS FOR CLASS " + clazz.getName());
            Collection<Field> fields = mTableInfo.getFields();
            list = new ArrayList<>(fields.size());
            for (Field field : fields) {
                list.add(Filler.getInstance(field, mTableInfo));
            }
            fastSaveCache.put(clazz, list);
        }

        for (int i = 0; i < list.size(); i++) {
            list.get(i).fill(mapValues, this);
        }

        try {
            if (contentValuesConstructor == null) {
                contentValuesConstructor = ContentValues.class.getDeclaredConstructor(HashMap.class);
                contentValuesConstructor.setAccessible(true);
            }
            ContentValues values = contentValuesConstructor.newInstance(mapValues);
            SQLiteDatabase db = Cache.openDatabase();
            if (getId() == null) {
                setAaId(db.insert(mTableInfo.getTableName(), null, values));
            } else {
                db.update(mTableInfo.getTableName(), values, idName + "=" + getId(), null);
            }
            Cache.getContext().getContentResolver()
                    .notifyChange(ContentProvider.createUri(mTableInfo.getType(), getId()), null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return getId();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Long id = getId();
        dest.writeLong(id != null ? id : -1L);
    }

    protected ExtendedModel(Parcel in) {
        this();
        Long id = in.readLong();
        if (!id.equals(-1L)) {
            setAaId(id);
        }
    }

    /**
     * извне всегда должен использоваться save() . Для кастомном имплементации у наследников - меняем fastSave
     *
     * @return
     */
    @Override
    public Long save() {
        return fastSave();
    }

    public void afterLoad() {

    }

    public static void removeAll(Class<? extends ExtendedModel> clazz) {
        new Delete().from(clazz).execute();
    }

    public static <T extends ExtendedModel> List<T> loadAll(Class<T> clazz) {
        return new Select().from(clazz).execute();
    }
}