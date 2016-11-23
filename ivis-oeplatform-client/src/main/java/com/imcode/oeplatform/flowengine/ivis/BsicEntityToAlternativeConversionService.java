package com.imcode.oeplatform.flowengine.ivis;

import com.imcode.entities.interfaces.JpaEntity;
import com.imcode.entities.interfaces.JpaNamedEntity;
import com.imcode.entities.interfaces.JpaPersonalizedEntity;
import com.imcode.oeplatform.flowengine.queries.linked.dropdownquery.LinkedDropDownAlternative;
import com.nordicpeak.flowengine.interfaces.MutableAlternative;
import com.sun.javafx.scene.layout.region.Margins;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;

/**
 * Created by vitaly on 28.09.15.
 */
@Deprecated
public class BsicEntityToAlternativeConversionService extends DefaultConversionService implements ConversionService{
//    public BsicEntityToAlternativeConversionService() {
//        Converter<JpaNamedEntity, MutableAlternative> namedEntityConverter = source -> new LinkedDropDownAlternative(convert(source.getId(), Integer.class), source.getName());
//        Converter<JpaPersonalizedEntity, MutableAlternative> personalizedEntityConverter = source -> new LinkedDropDownAlternative(convert(source.getId(), Integer.class), source.getName());
//
//        addConverter(namedEntityConverter);
//
//    }
}
