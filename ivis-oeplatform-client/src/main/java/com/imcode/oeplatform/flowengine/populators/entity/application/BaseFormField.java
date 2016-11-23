package com.imcode.oeplatform.flowengine.populators.entity.application;

import com.imcode.oeplatform.flowengine.populators.entity.FormField;

@Deprecated
public abstract class BaseFormField<T> implements FormField<T> {
    protected T field;

    public BaseFormField(T field) {
        this.field = field;
    }

    @Override
    public T getField() {
        return field;
    }
}
