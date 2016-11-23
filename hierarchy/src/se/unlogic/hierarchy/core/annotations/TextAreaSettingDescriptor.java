/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import se.unlogic.standardutils.validation.DummyStringFormatValidator;
import se.unlogic.standardutils.validation.StringFormatValidator;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TextAreaSettingDescriptor {

	String id() default "";
	String name();
	String description();
	boolean required() default false;
	Class<? extends StringFormatValidator> formatValidator() default DummyStringFormatValidator.class;
	//TODO add support for static default value
}
