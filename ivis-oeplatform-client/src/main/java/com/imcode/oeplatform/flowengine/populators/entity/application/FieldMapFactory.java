package com.imcode.oeplatform.flowengine.populators.entity.application;

import com.imcode.oeplatform.flowengine.populators.entity.FieldMapper;
import com.imcode.oeplatform.flowengine.populators.entity.FormField;
import com.nordicpeak.flowengine.interfaces.QueryInstance;

import java.util.*;

@Deprecated
public class FieldMapFactory {

    List<FieldMapper> fieldMapperList;
    //todo create cache search
//    Map<Class, FieldMapper> fieldMapperMap;


    public FieldMapFactory() {
        this.fieldMapperList = new LinkedList<>();
    }

    public FieldMapFactory(Collection<FieldMapper> fieldMappers) {
        this();
        this.fieldMapperList.addAll(fieldMappers);
    }

    public FieldMapFactory(FieldMapper... fieldMappers) {
        this();
        this.fieldMapperList.addAll(Arrays.asList(fieldMappers));
    }

    public void addMapper(FieldMapper mapper) {
        fieldMapperList.add(mapper);
    }

    public void addMappers(FieldMapper... mappers) {
        fieldMapperList.addAll(Arrays.asList(mappers));
    }

    public void removeMappers(Class<FieldMapper> mapperClass) {
        fieldMapperList.removeIf(mapper -> mapper.getClass() == mapperClass);
    }

    public boolean hasMapper(Class<? extends QueryInstance> queryClass) {
        return getMapper(queryClass) != null;
    }

    public boolean hasMapper(QueryInstance query) {
        return hasMapper(query.getClass());
    }

    protected FieldMapper getMapper(Class<? extends QueryInstance> queryClass) {

        //Find for exact match
        for (FieldMapper fieldMapper : fieldMapperList) {
            if (fieldMapper.mach(queryClass)) {
                return fieldMapper;
            }
        }

        return null;
    }

    protected FieldMapper getMapper(QueryInstance query) {
        return getMapper(query.getClass());
    }

    public Map<String, FormField> createFormFieldMap(QueryInstance query) {
        Map<String, FormField> map = null;
        FieldMapper mapper = getMapper(query);

        if (mapper == null) {
            return null;
        }

        map = mapper.map(query);

        return map;
    }
}
