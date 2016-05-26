package com.activeandroid;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import com.activeandroid.content.ContentProvider;
import com.activeandroid.filler.Filler;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public abstract class ExtendedModel extends Model implements Parcelable {
    @JsonIgnore
    public TableInfo mTableInfo;
    @JsonIgnore
    public String idName;
    public static HashMap<Class, ArrayList<Filler>> fastSaveCache = new HashMap<>();
    public static Constructor<ContentValues> contentValuesConstructor;

    public ExtendedModel() {
        super();
        mTableInfo = Cache.getTableInfo(getClass());
        idName = mTableInfo.getIdName();
    }


    public ExtendedModel(Long id) {
        this();
        setAaId(id);
    }

    private static Field idField;

    public void setAaId(Long id) {
        try {
            if (idField == null) {
                idField = Model.class.getDeclaredField("mId");
                idField.setAccessible(true);
            }
            idField.set(this, id);
        } catch (Exception e) {
            throw new RuntimeException("Reflection failed to get the Active Android ID", e);
        }
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
}