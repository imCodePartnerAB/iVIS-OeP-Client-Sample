package com.imcode.oeplatform.flowengine.queries;

import com.nordicpeak.flowengine.interfaces.Query;

import java.util.List;

/**
 * Created by vitaly on 19.11.15.
 */
@Deprecated
public interface DependentQuery extends Query{
    List<? extends DependentField> getFields();
}
