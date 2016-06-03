package com.activeandroid;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.util.Convert;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.lang.reflect.*;
import java.util.*;

@Table(name = "many", id = "_id")
public class Many<T extends ExtendedModel> extends ExtendedModel implements List<T> {
    @JsonIgnore
    private Class<T> clazz;
    //@JsonIgnore
    private List<T> data = new ArrayList<>();

    // список НЕ корректно сохраняется\загружается в базе, логика связи подразумевает отношение один ко многим
    //List<Long> ids = new ArrayList<>();
    @JsonIgnore
    @Column(name = "items")
    String ids = ""; // "1, 2, 3..."
    @JsonIgnore
    @Column(name = "class_name")
    String class_name = "";

    public Many() {
        super();

        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            if (types != null && types.length > 0) {
                type = types[0];
                if (type instanceof Class) {
                    initWith((Class<T>) type);
                }
            }
        }
        initClass();
        reload();
    }

    /**
     * sometimes clazz == null after load, can't find reason
     * also "data" is empty, must fill here
     */
    @Override
    public void afterLoad() {
        super.afterLoad();
        initClass();
        reload();
    }

    public Many(Class<T> clazz) {
        super();
        initWith(clazz);
        reload();
    }

    protected Many(Parcel in) {
        super(in);
        this.ids = in.readString();
        this.class_name = in.readString();
        initClass();
        this.data = new ArrayList<>();
        // fill data
        reload();
    }

    /**
     * init clazz if class_name exists
     */
    private void initClass() {
        if (clazz == null && !TextUtils.isEmpty(class_name)) {
            try {
                clazz = (Class<T>) Class.forName(class_name);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public Many(Many many) {
        super(many.getId());
        this.ids = many.ids;
        this.clazz = many.clazz;
        this.class_name = many.class_name;
        // fill data
        reload();
    }

    public void initWith(Class<T> clazz) {
        this.clazz = clazz;
        this.class_name = clazz.getName();
    }

    /**
     * also update ids here!
     */
    @Override
    public Long save() {
        ids = "";
        ArrayList<String> temp = new ArrayList<>();
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                T item = data.get(i);
                item.save();
                temp.add(Long.toString(item.getId()));
            }
        }
        ids = Convert.listToString(temp);

        // situation when data comes from JSON
        if (clazz == null && !data.isEmpty()) {
            T obj = data.get(0);
            initWith((Class<T>) obj.getClass());
        } else if (class_name == null) {
            class_name = clazz.getName();
        }
        return super.save();
    }

    /**
     * актуализация данных в data согласно ids
     */
    public synchronized void reload() {
        if (data == null || data.isEmpty()) {
            if (clazz != null) {
                if (!ids.isEmpty()) {
                    ArrayList<String> idsList = Convert.strToList(ids, ", ");
                    String where = "";
                    for (String arg : idsList) {
                        where += where.isEmpty() ? "_id = ?" : " OR _id = ?";
                    }
                    data = new Select().from(clazz).where(where, Convert.listToArr(idsList)).execute();
                }
            } else {
                Log.e("Many", "reload() called with clazz == null");
            }
            if (data == null) {
                data = new ArrayList<>();
            }
        }
    }

    public synchronized void setList(List<T> list) {
        this.data = list;
        save();
    }

    @Override
    public void add(int location, T object) {
        if (object.getId() == null) {
            Log.e("Many", "add() in Many called for nonsaved object! " + object.toString());
            object.save();
        }
        data.add(location, object);
        save();
    }

    @Override
    public boolean add(T object) {
        if (object.getId() == null) {
            Log.e("Many", "add() in Many called for nonsaved object! " + object.toString());
            object.save();
        }
        boolean res = data.add(object);
        save();
        return res;
    }

    @Override
    public boolean addAll(int location, Collection<? extends T> collection) {
        boolean res = data.addAll(location, collection);
        save();
        return res;
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        boolean res = data.addAll(collection);
        save();
        return res;
    }

    @Override
    public void clear() {
        data.clear();
        save();
    }

    @Override
    public boolean contains(Object object) {
        return data.contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return data.containsAll(collection);
    }

    @Override
    public T get(int location) {
        return data.get(location);
    }

    @Override
    public int indexOf(Object object) {
        return data.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    //@NonNull
    @Override
    public Iterator<T> iterator() {
        return data.iterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        return data.lastIndexOf(object);
    }

    @Override
    public ListIterator<T> listIterator() {
        return data.listIterator();
    }

    //@NonNull
    @Override
    public ListIterator<T> listIterator(int location) {
        return data.listIterator(location);
    }

    /**
     * @return the element previously at the specified position
     */
    @Override
    public T remove(int location) {
        data.remove(location);

        save();

        if (data.size() > location) {
            return data.get(location);
        } else {
            return null;
        }
    }

    @Override
    public boolean remove(Object object) {
        boolean res = data.remove(object);
        save();
        return res;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean res = data.removeAll(collection);
        save();
        return res;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return data.retainAll(collection);
    }

    @Override
    public T set(int location, T object) {
        T res = data.set(location, object);
        save();
        return res;
    }

    @Override
    public int size() {
        return data.size();
    }

    //@NonNull
    @Override
    public List<T> subList(int start, int end) {
        return data.subList(start, end);
    }

    //@NonNull
    @Override
    public Object[] toArray() {
        return data.toArray();
    }

    //@NonNull
    @Override
    public <T1> T1[] toArray(T1[] array) {
        return data.toArray(array);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.ids);
        dest.writeString(this.class_name);
    }

    public static final Parcelable.Creator<Many> CREATOR = new Parcelable.Creator<Many>() {
        @Override
        public Many createFromParcel(Parcel source) {
            return new Many(source);
        }

        @Override
        public Many[] newArray(int size) {
            return new Many[size];
        }
    };


    public final Parcelable.Creator<T> SUB_CREATOR = new Parcelable.Creator<T>() {
        public Constructor<T> constructor;

        {
        }

        @Override
        public T createFromParcel(Parcel source) {
            try {
                return constructor().newInstance(source);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            return null;
        }

        private Constructor<T> constructor() {
            if (constructor == null) {
                try {
                    initClass();
                    constructor = clazz.getConstructor(Parcel.class);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
            return constructor;
        }

        @Override
        public T[] newArray(int size) {
            initClass();
            return (T[]) Array.newInstance(clazz, size);
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Many)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        Many<?> many = (Many<?>) o;

        return data != null ? data.equals(many.data) : many.data == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(final List<T> data) {
        this.data = data;
        save();
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public void setClazz(final Class<T> clazz) {
        this.clazz = clazz;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(final String class_name) {
        this.class_name = class_name;
    }
}
