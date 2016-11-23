package com.imcode.oeplatform.flowengine.queries;

import com.nordicpeak.flowengine.interfaces.Query;
import com.nordicpeak.flowengine.interfaces.QueryInstance;

import java.util.List;

/**
 * Created by vitaly on 18.11.15.
 */
@Deprecated
public interface DependentQueryInstance extends QueryInstance {
    List<? extends DependentFieldValue> getValues();

//    <T extends DependentQuery> void setValues(List<T> values);

    <T extends DependentQuery> T getQuery();


}
