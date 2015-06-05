package com.tamr.field.batch;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by jellin on 5/14/15.
 */
public class MapFieldSetMapper implements FieldSetMapper<Map> {
    @Override
    public Map mapFieldSet(FieldSet fieldSet) throws BindException {
       Map result =  new HashMap<>((Map) fieldSet.getProperties());
        result.put("uuid", UUID.randomUUID().toString());
        return result;

    }
}
