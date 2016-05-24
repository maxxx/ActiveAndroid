package com.activeandroid.filler;

import com.activeandroid.Cache;
import com.activeandroid.ExtendedModel;
import com.activeandroid.TableInfo;
import com.activeandroid.serializer.TypeSerializer;
import com.activeandroid.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;

public class Filler<T> {
    final String fieldName;
    final Field field;
    final TypeSerializer typeSerializer;

    public Filler(String fieldName, Field field, TypeSerializer typeSerializer) {
        this.field = field;
        this.fieldName = fieldName;
        this.typeSerializer = typeSerializer;
    }

    public void fill(HashMap<String, Object> map, ExtendedModel extendedModel) {
        map.put(fieldName, getValue(extendedModel));
    }

    public Object getValue(ExtendedModel extendedModel) {
        Object value = null;
        try {
            value = field.get(extendedModel);
        } catch (IllegalAccessException e) {
            field.setAccessible(true);
            try {
                value = field.get(extendedModel);
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            }
        }

        if (value != null) {
            if (typeSerializer != null) {
                value = typeSerializer.serialize(value);
            }
        }
        return value;
    }

    public static Filler getInstance(Field field, TableInfo tableInfo) {
        String fieldName = tableInfo.getColumnName(field);

        Class<?> fieldType = field.getType();

        TypeSerializer typeSerializer = Cache.getParserForType(fieldType);
        if (typeSerializer != null) {
            fieldType = typeSerializer.getSerializedType();
        }

        if (fieldType.equals(Byte.class) || fieldType.equals(byte.class)) {
            return new Filler<Byte>(fieldName, field, typeSerializer);
        } else if (fieldType.equals(Short.class) || fieldType.equals(short.class)) {
            return new Filler<Short>(fieldName, field, typeSerializer);
        } else if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
            return new Filler<Integer>(fieldName, field, typeSerializer);
        } else if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
            return new Filler<Long>(fieldName, field, typeSerializer);
        } else if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
            return new Filler<Float>(fieldName, field, typeSerializer);
        } else if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
            return new Filler<Double>(fieldName, field, typeSerializer);
        } else if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
            return new Filler<Boolean>(fieldName, field, typeSerializer);
        } else if (fieldType.equals(Character.class) || fieldType.equals(char.class)) {
            return new Filler<Character>(fieldName, field, typeSerializer);
        } else if (fieldType.equals(String.class)) {
            return new Filler<String>(fieldName, field, typeSerializer);
        } else if (fieldType.equals(Byte[].class) || fieldType.equals(byte[].class)) {
            return new Filler<Byte[]>(fieldName, field, typeSerializer);
        } else if (ReflectionUtils.isModel(fieldType)) {
            return new ModelFiller(fieldName, field, typeSerializer);
        } else if (ReflectionUtils.isSubclassOf(fieldType, Enum.class)) {
            return new EnumFiller(fieldName, field, typeSerializer);
        } else {
            return new IgnoreFiller(fieldName, field, typeSerializer);
        }
    }
}