package com.imcode.oeplatform.flowengine.queries;

/**
 * Created by vitaly on 18.11.15.
 */
@Deprecated
public interface Valuable<T> {
    public T getValue();

    public void setValue(T value);
}
