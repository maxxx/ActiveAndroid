package com.activeandroid.filler;

import com.activeandroid.ExtendedModel;
import com.activeandroid.serializer.TypeSerializer;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Created by paul on 12/16/15.
 */
public class ModelFiller extends Filler<ExtendedModel> {
    public ModelFiller(String fieldName, Field field, TypeSerializer typeSerializer) {
        super(fieldName, field, typeSerializer);
    }

    @Override
    public void fill(HashMap<String, Object> map, ExtendedModel extendedModel) {
        ExtendedModel model = (ExtendedModel) getValue(extendedModel);
        if (model != null) {
            model.save();
            map.put(fieldName, model.getId());
        }
    }
}
