package com.imcode.oeplatform.flowengine.populators.entity;

import com.nordicpeak.flowengine.interfaces.QueryInstance;

import java.util.Map;

public interface FieldMapper<T extends QueryInstance> {
    Map<String, FormField> map(T query);

    boolean mach(Class queryClass);

    default boolean mach(T query) {
        return mach(query.getClass());
    }

}
