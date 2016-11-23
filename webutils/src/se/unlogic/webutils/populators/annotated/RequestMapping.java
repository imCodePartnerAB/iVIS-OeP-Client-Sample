/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.webutils.populators.annotated;

import java.lang.annotation.Annotation;

import se.unlogic.standardutils.annotations.WebPopulate;
import se.unlogic.standardutils.populators.BeanStringPopulator;

public abstract class RequestMapping {

	protected BeanStringPopulator<?> beanStringPopulator;
	protected String paramName;
	protected boolean list;
	protected boolean splitOnLineBreak;
	protected boolean noDuplicates;

	protected boolean required;
	protected long maxLength;
	protected long minLength;
	protected boolean trim;

	public RequestMapping(BeanStringPopulator<?> typePopulator, WebPopulate annotation, String paramName, boolean list, boolean splitOnLineBreak, boolean noDuplicates) {

		super();
		this.beanStringPopulator = typePopulator;
		this.paramName = paramName;
		this.list = list;
		this.splitOnLineBreak = splitOnLineBreak;
		this.noDuplicates = noDuplicates;

		required = annotation.required();
		maxLength = annotation.maxLength();
		minLength = annotation.minLength();
		trim = annotation.trim();
	}

	public BeanStringPopulator<?> getBeanStringPopulator() {

		return beanStringPopulator;
	}

	public String getParamName() {

		return paramName;
	}

	public boolean isList() {

		return list;
	}

	public abstract void setValue(Object bean, Object value);

	public abstract boolean isPrimitive();

	public abstract Class<?> getType();

	public abstract <T extends Annotation> T getAnnotation(Class<T> annotationClass);
	
	public boolean isRequired() {

		return required;
	}

	public void setRequired(boolean required) {

		this.required = required;
	}

	public long getMaxLength() {

		return maxLength;
	}

	public void setMaxLength(long maxLength) {

		this.maxLength = maxLength;
	}

	public long getMinLength() {

		return minLength;
	}

	public void setMinLength(long minLength) {

		this.minLength = minLength;
	}

	public boolean isTrim() {

		return trim;
	}

	public void setTrim(boolean trim) {

		this.trim = trim;
	}

	public void setTypePopulator(BeanStringPopulator<?> typePopulator) {

		this.beanStringPopulator = typePopulator;
	}

	public void setParamName(String paramName) {

		this.paramName = paramName;
	}

	public void setList(boolean list) {

		this.list = list;
	}

	
	public boolean isSplitOnLineBreak() {
	
		return splitOnLineBreak;
	}

	
	public void setSplitOnLineBreak(boolean splitOnLineBreak) {
	
		this.splitOnLineBreak = splitOnLineBreak;
	}

	public boolean isNoDuplicates() {
		return noDuplicates;
	}	
}
