/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.webutils.populators;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.standardutils.enums.EnumUtils;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationErrorType;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.webutils.http.BeanRequestPopulator;

public class EnumRequestPopulator<EnumType extends Enum<EnumType>> implements BeanRequestPopulator<EnumType> {

	private Class<EnumType> classType;
	private String fieldName;

	public EnumRequestPopulator(Class<EnumType> classType) {
		super();

		checkClass(classType);
		fieldName = classType.getSimpleName();
	}

	public EnumRequestPopulator(Class<EnumType> classType, String fieldName) {

		checkClass(classType);

		if (StringUtils.isEmpty(fieldName)) {
			throw new NullPointerException("fieldName can not be null or empty!");
		} else {
			this.fieldName = fieldName;
		}
	}

	private void checkClass(Class<EnumType> classType) {

		if (classType == null) {
			throw new NullPointerException("Classtype can not be null!");
		} else {
			this.classType = classType;
		}
	}

	public EnumType populate(HttpServletRequest req) throws ValidationException {

		String stringValue = req.getParameter(fieldName);

		if (StringUtils.isEmpty(stringValue)) {
			throw new ValidationException(new ValidationError(fieldName, ValidationErrorType.RequiredField));
		}

		EnumType enumValue = EnumUtils.toEnum(classType, stringValue);

		if (enumValue == null) {
			throw new ValidationException(new ValidationError(fieldName, ValidationErrorType.InvalidFormat));
		}

		return enumValue;
	}

	public EnumType populate(EnumType bean, HttpServletRequest req) throws ValidationException {
		return this.populate(req);
	}

	public static <Type extends Enum<Type>> EnumRequestPopulator<Type> getGenericInstance(Class<Type> type) {

		return new EnumRequestPopulator<Type>(type);
	}
}
