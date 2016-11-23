package com.imcode.oeplatform.flowengine.interfaces;

import java.util.List;

/**
 * Created by vitaly on 04.02.16.
 */
public interface FieldQuery {

    public List<? extends MutableField> getFields();

    public List<? extends MultiValueQueryInstance> getInstances();

}