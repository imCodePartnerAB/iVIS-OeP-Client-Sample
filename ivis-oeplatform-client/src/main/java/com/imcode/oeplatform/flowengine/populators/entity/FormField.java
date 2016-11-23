package com.imcode.oeplatform.flowengine.populators.entity;

public interface FormField<T> {
    String getValue();

    T getField();
}
