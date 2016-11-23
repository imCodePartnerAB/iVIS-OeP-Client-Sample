package com.imcode.oeplatform.flowengine.populators.entity.application;

import com.imcode.entities.interfaces.JpaNamedEntity;
import com.imcode.entities.interfaces.JpaPersonalizedEntity;
import com.imcode.oeplatform.flowengine.populators.entity.ValueResolver;
import com.imcode.oeplatform.flowengine.populators.entity.ValueResolverFactory;
import com.imcode.services.NamedService;
import com.imcode.services.PersonalizedService;
import imcode.services.IvisServiceFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vitaly on 14.09.15.
 */
@Deprecated
class IvisValueResolverFactory implements ValueResolverFactory {
    private final IvisServiceFactory serviceFactory;
    private final Map<Class, ValueResolver> simpleValueResolvers;

    IvisValueResolverFactory(IvisServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
        simpleValueResolvers = new HashMap<>();
        addValueResolverHelper(String.class, String::trim);
        addValueResolverHelper(Integer.class, Integer::valueOf);
        addValueResolverHelper(Boolean.class, s -> !s.trim().isEmpty());
    }

    private <T> void addValueResolverHelper(Class<T> clazz, ValueResolver<? super T> valueResolver) {
        simpleValueResolvers.put(clazz, valueResolver);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ValueResolver<T> getValueResolver(Class<T> entityClass) {
        if (JpaPersonalizedEntity.class.isAssignableFrom(entityClass)) {
            PersonalizedService<T> service = serviceFactory.getServiceFor(entityClass);
            return new PersonalizedVlueResolver(service);
        }

        if (JpaNamedEntity.class.isAssignableFrom(entityClass)) {
            NamedService<T> service = serviceFactory.getServiceFor(entityClass);
            return new NamedVlueResolver(service);
        }

        ValueResolver<T> defaultValueResolver = simpleValueResolvers.get(entityClass);

        if (defaultValueResolver == null)
            throw new IllegalArgumentException("No such valuet resolver for class \"" + entityClass + "\"");

        return defaultValueResolver;
    }
}
