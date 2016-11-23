package com.imcode.oeplatform.flowengine.queries;

/**
 * Created by vitaly on 18.11.15.
 */
@Deprecated
public interface DependentFieldValue extends Valuable<String> {

    String getValue();

    void setValue(String value);

    DependentField getQueryField();
}
