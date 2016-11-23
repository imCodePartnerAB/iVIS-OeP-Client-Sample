package com.imcode.oeplatform.flowengine.populators.entity;

import java.util.function.Function;

/**
 * Created by vitaly on 09.09.15.
 */
@FunctionalInterface
public interface ValueResolver<T> extends Function<String, T> {
    default T getNew() {
        return null;
    }
}
