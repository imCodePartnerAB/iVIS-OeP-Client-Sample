package com.imcode.oeplatform.flowengine.populators.entity.application;

import com.imcode.entities.interfaces.JpaEntity;
import com.imcode.entities.interfaces.JpaNamedEntity;
import com.imcode.entities.interfaces.JpaPersonalizedEntity;
import com.imcode.services.NamedService;
import com.imcode.services.PersonalizedService;
import imcode.services.IvisServiceFactory;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * Created by vitaly on 14.09.15.
 */
public class IvisConverterFactory implements ConverterFactory<String, JpaEntity>, ConditionalConverter {
    private final IvisServiceFactory serviceFactory;

    public IvisConverterFactory(IvisServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public <T extends JpaEntity> Converter<String, T> getConverter(Class<T> targetType) {
        if (JpaPersonalizedEntity.class.isAssignableFrom(targetType)) {
            PersonalizedService<T> service = serviceFactory.getServiceFor(targetType);
            return service::findFirstByPersonalId;
        }

        if (JpaNamedEntity.class.isAssignableFrom(targetType)) {
            NamedService<T> service = serviceFactory.getServiceFor(targetType);
            return service::findFirstByName;
        }

        throw new IllegalArgumentException("No such converter for class \"" + targetType + "\"");
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (String.class.isAssignableFrom(sourceType.getType())) {
            Class<?> targetClass = targetType.getType();
            if (JpaEntity.class.isAssignableFrom(targetClass)) {
                return serviceFactory.hasServiceFor(targetClass);
            }
        }

        return false;
    }
}
