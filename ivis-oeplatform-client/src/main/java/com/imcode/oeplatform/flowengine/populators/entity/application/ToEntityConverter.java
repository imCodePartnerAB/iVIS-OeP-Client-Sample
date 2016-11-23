package com.imcode.oeplatform.flowengine.populators.entity.application;

import com.imcode.entities.interfaces.JpaEntity;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

/**
 * Created by vitaly on 28.09.15.
 */
@Deprecated
public class  ToEntityConverter implements ConditionalGenericConverter {
    private final GenericConversionService conversionService;

    public ToEntityConverter(GenericConversionService conversionService) {
        this.conversionService = conversionService;
        conversionService.addConverter(this);
    }
//        public ToEntityConverter(Repositories repositories, ConversionService conversionService) {
//            this.repositoryInvokerFactory = new DefaultRepositoryInvokerFactory(repositories, conversionService);
//        }

    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(Object.class, JpaEntity.class));
    }

    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source != null && StringUtils.hasText(source.toString())) {
            if (sourceType.equals(targetType)) {
                return source;
            } else {
                Class domainType = targetType.getType();
                Class<? extends Serializable> idClass = getEntityIdClass(domainType);
                Serializable id = null;

                if (!idClass.isInstance(source)) {
                    id = conversionService.convert(source, idClass);
                } else {
                    id = (Serializable) source;
                }
                JpaEntity entity = null;

                try {
                    entity = (JpaEntity) domainType.newInstance();
                    entity.setId((Serializable) source);
                } catch (Exception e) {
                    throw new RuntimeException("Can't create instance of type " + targetType);
                }
                return entity;
//                    RepositoryInformation info = DomainClassConverter.this.repositories.getRepositoryInformationFor(domainType);
//                    RepositoryInvoker invoker = this.repositoryInvokerFactory.getInvokerFor(domainType);
//                    return invoker.invokeFindOne((Serializable) DomainClassConverter.this.conversionService.convert(source, info.getIdType()));
            }
        } else {
            return null;
        }
    }

    private Class<? extends Serializable> getEntityIdClass(Class<JpaEntity> domainType) {
        return (Class<? extends Serializable>) ResolvableType.forClass(domainType).as(JpaEntity.class).resolveGeneric(0);
    }

    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (sourceType.isAssignableTo(targetType)) {
            return false;
        } else if (!JpaEntity.class.isAssignableFrom(targetType.getType())) {
            return false;
        } else {
            Class domainType = targetType.getType();
            Class<? extends Serializable> idClass = getEntityIdClass(domainType);
            if (!conversionService.canConvert(sourceType.getType(), idClass)) {
                return false;
            }
//                DomainClassConverter.this.repositories.getRepositoryInformationFor(targetType.getType()).getIdType();
//                if (!sourceType.equals(TypeDescriptor.valueOf(rawIdType)) && !DomainClassConverter.this.conversionService.canConvert(sourceType.getType(), rawIdType)) {
//                    throw new DomainClassConverter.ConversionMatchAbbreviationException(null);
//                } else {
            return true;
//                }
        }
    }
}