package com.activeandroid.filler;

import com.activeandroid.ExtendedModel;
import com.activeandroid.serializer.TypeSerializer;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Created by paul on 12/16/15.
 */
public class IgnoreFiller extends Filler {
    public IgnoreFiller(String fieldName, Field field, TypeSerializer typeSerializer) {
        super(fieldName, field, typeSerializer);
    }

    @Override
    public void fill(HashMap map, ExtendedModel extendedModel) {

    }
}
