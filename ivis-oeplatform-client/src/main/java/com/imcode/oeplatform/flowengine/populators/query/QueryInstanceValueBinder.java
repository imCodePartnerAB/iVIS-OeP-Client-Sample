package com.imcode.oeplatform.flowengine.populators.query;

import com.nordicpeak.flowengine.interfaces.ImmutableQueryDescriptor;
import com.nordicpeak.flowengine.interfaces.MutableQueryInstanceDescriptor;
import com.nordicpeak.flowengine.interfaces.QueryInstance;

import java.util.function.Consumer;

/**
 * Created by vitaly on 19.11.15.
 */
public interface QueryInstanceValueBinder<T extends QueryInstance> {
    interface ExceptionHandler {
        void handle(Throwable t);
    }

    boolean match(QueryInstance queryInstance);

    void bindValues(T queryInstance);

    default ImmutableQueryDescriptor getQueryDescriptor(QueryInstance queryInstance) {
        ImmutableQueryDescriptor queryDescriptor = null;
        MutableQueryInstanceDescriptor queryInstanceDescriptor = queryInstance.getQueryInstanceDescriptor();

        if (queryInstanceDescriptor != null) {
            queryDescriptor = queryInstanceDescriptor.getQueryDescriptor();
        }

        return queryDescriptor;
    }

}
