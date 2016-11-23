package com.imcode.oeplatform.flowengine.interfaces;

import com.nordicpeak.flowengine.interfaces.QueryInstance;

import java.util.List;

/**
 * Created by vitaly on 04.02.16.
 */
public interface MultiValueQueryInstance extends QueryInstance{
    List<? extends FieldValue> getValues();

    FieldQuery getQuery();
}
