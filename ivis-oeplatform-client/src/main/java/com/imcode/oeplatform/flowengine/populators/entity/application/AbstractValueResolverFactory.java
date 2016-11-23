package com.imcode.oeplatform.flowengine.populators.entity.application;

import com.imcode.oeplatform.flowengine.populators.entity.ValueResolverFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vitaly on 10.09.15.
 */
@Deprecated
public class AbstractValueResolverFactory {
    //    private static final ValueResolver<String> DEFAULT_VALUE_RESOLVER = String::toString;
//    @SuppressWarnings("unchecked")
//    private static ValueResolverFactory<String> createStringValueResolverFactory() {
//        ValueResolverFactory<String> factory = new ValueResolverFactory<String>() {
//            @Override
//            public <T> ValueResolver<T> getValueResolver(Class<T> entityClass) {
//                return null;
//            }
//        };
//    private ValueResolverFactory defaultValueResolverFactory = entityClass ->

//    private Map<Class<?>, ValueResolverFactory> factoryMap;
//
//
//    public AbstractValueResolverFactory() {
//        factoryMap = new HashMap<>();
//    }
//
//    public <T> void addFactory(Class<T> targetType, ValueResolverFactory<T> factory) {
//        factoryMap.put(targetType, factory);
//    }
//
//    @SuppressWarnings("unchecked")
//    public <T> ValueResolverFactory<T> removeFactory(Class<T> targetType) {
//        return (ValueResolverFactory<T>) factoryMap.remove(targetType);
//    }
//
//    @SuppressWarnings("unchecked")
//    public <T> ValueResolverFactory<T> getFactory(Class<T> targerClass) {
//        return (ValueResolverFactory<T>) factoryMap.get(targerClass);
//    }
}
