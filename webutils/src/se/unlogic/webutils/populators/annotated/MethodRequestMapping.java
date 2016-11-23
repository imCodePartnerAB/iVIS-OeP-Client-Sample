/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.webutils.populators.annotated;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import se.unlogic.standardutils.annotations.NoDuplicates;
import se.unlogic.standardutils.annotations.SplitOnLineBreak;
import se.unlogic.standardutils.annotations.WebPopulate;
import se.unlogic.standardutils.populators.BeanStringPopulator;

public class MethodRequestMapping extends RequestMapping{

	protected final Method method;
	protected final Class<?> paramClass;

	public MethodRequestMapping(Method method, BeanStringPopulator<?> typePopulator, WebPopulate annotation, String paramName) {
		super(typePopulator,annotation,paramName,method.getParameterTypes()[0] == List.class,method.isAnnotationPresent(SplitOnLineBreak.class),method.isAnnotationPresent(NoDuplicates.class));
		
		this.method = method;
		this.paramClass = method.getParameterTypes()[0];
	}

	@Override
	public void setValue(Object bean, Object value) {

		try {
			method.invoke(bean, value);
			
		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);
			
		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
			
		} catch (InvocationTargetException e) {

			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isPrimitive() {

		return paramClass.isPrimitive();
	}

	@Override
	public Class<?> getType() {

		return paramClass;
	}
	
	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {

		return method.getAnnotation(annotationClass);
	}	
}
