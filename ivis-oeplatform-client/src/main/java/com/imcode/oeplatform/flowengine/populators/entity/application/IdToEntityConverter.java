package com.imcode.oeplatform.flowengine.populators.entity.application;

import com.imcode.entities.interfaces.JpaEntity;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import se.unlogic.standardutils.reflection.MethodNotFoundException;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

/**
 * Created by vitaly on 29.09.15.
 */
public class IdToEntityConverter implements ConditionalGenericConverter {

    private final ConversionService conversionService;


    public IdToEntityConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }


    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(Object.class, Object.class));
    }


    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        Class<?> targetClass = targetType.getType();

        if (!JpaEntity.class.isAssignableFrom(targetClass)) {
            return false;
        }

        Method idSetter = getIdSetter(targetClass);

        return (idSetter != null &&
                this.conversionService.canConvert(sourceType, TypeDescriptor.valueOf(getEntityIdClass(targetClass))));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }

        final Class<?> entityClass = targetType.getType();
        Method idSetter = getIdSetter(entityClass);
        Object id = this.conversionService.convert(
                source, sourceType, TypeDescriptor.valueOf(getEntityIdClass(entityClass)));

        Object entity = null;

        try {
            entity = entityClass.newInstance();
            ReflectionUtils.invokeMethod(idSetter, entity, id);
        } catch (Exception ignore) {

        }

        return entity;
    }

    protected Method getIdSetter(Class<?> entityClass) {
//        String finderMethod = "find" + getEntityName(entityClass);
//        Method[] methods;
//        boolean localOnlyFiltered;
        Method setter = null;
        try {
//            try {
//            methods = entityClass.getDeclaredMethods();
//            localOnlyFiltered = true;
            Method getter = entityClass.getMethod("getId");

            setter = entityClass.getMethod("setId", getter.getReturnType());
//            } catch (SecurityException ex) {
            // Not allowed to access non-public methods...
            // Fallback: check locally declared public methods only.

//                method = entityClass.getMethod("setId", Serializable.class);

//            localOnlyFiltered = false;

//            }
        } catch (NoSuchMethodException ignore) {
        }

        return setter;
//        for (Method method : methods) {
//            if (Modifier.isStatic(method.getModifiers()) && method.getName().equals(finderMethod) &&
//                    method.getParameterTypes().length == 1 && method.getReturnType().equals(entityClass) &&
//                    (localOnlyFiltered || method.getDeclaringClass().equals(entityClass))) {
//                return method;
//            }
//        }
//        return null;
    }

    private Class<?> getEntityIdClass(Class<?> domainType) {
        return ResolvableType.forClass(domainType).as(JpaEntity.class).resolveGeneric(0);
    }


//    private String getEntityName(Class<?> entityClass) {
//        String shortName = ClassUtils.getShortName(entityClass);
//        int lastDot = shortName.lastIndexOf('.');
//        if (lastDot != -1) {
//            return shortName.substring(lastDot + 1);
//        } else {
//            return shortName;
//        }
}
