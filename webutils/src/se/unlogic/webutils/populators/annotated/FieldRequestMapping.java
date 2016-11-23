/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.webutils.populators.annotated;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import se.unlogic.standardutils.annotations.NoDuplicates;
import se.unlogic.standardutils.annotations.SplitOnLineBreak;
import se.unlogic.standardutils.annotations.WebPopulate;
import se.unlogic.standardutils.populators.BeanStringPopulator;

public class FieldRequestMapping extends RequestMapping{

	protected final Field field;

	public FieldRequestMapping(Field field, BeanStringPopulator<?> typePopulator, WebPopulate annotation, String paramName) {
		super(typePopulator,annotation,paramName,field.getType() == List.class,field.isAnnotationPresent(SplitOnLineBreak.class),field.isAnnotationPresent(NoDuplicates.class));
		
		this.field = field;
	}

	@Override
	public void setValue(Object bean, Object value) {

		try {
			field.set(bean, value);
			
		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);
			
		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isPrimitive() {

		return field.getType().isPrimitive();
	}

	@Override
	public Class<?> getType() {

		return field.getType();
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {

		return field.getAnnotation(annotationClass);
	}
}
