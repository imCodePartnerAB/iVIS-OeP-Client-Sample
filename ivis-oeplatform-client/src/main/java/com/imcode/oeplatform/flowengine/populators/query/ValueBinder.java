package com.imcode.oeplatform.flowengine.populators.query;

import com.nordicpeak.flowengine.interfaces.QueryInstance;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by vitaly on 19.11.15.
 */
public class ValueBinder implements QueryInstanceValueBinder<QueryInstance> {
    private final Set<QueryInstanceValueBinder> binders = new HashSet<>();

    @Override
    public boolean match(QueryInstance queryInstance) {
        return true;
    }

    @Override
    public void bindValues(QueryInstance queryInstance) {
        Optional<QueryInstanceValueBinder> binder = binders.stream().filter(b -> b.match(queryInstance)).findFirst();
        if (binder.isPresent()) {
            binder.get().bindValues(queryInstance);
        }
    }

    public ValueBinder addBinder(QueryInstanceValueBinder<?> binder) {
        binders.add(binder);
        return this;
    }
}
