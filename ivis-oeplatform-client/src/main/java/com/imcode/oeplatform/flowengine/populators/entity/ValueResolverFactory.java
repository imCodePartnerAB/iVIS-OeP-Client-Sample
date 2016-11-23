package com.imcode.oeplatform.flowengine.populators.entity;

/**
 * Created by vitaly on 10.09.15.
 */
public interface ValueResolverFactory {
    <T> ValueResolver<T> getValueResolver(Class<T> entityClass);
}
