/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.webutils.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.standardutils.populators.BeanStringPopulator;
import se.unlogic.standardutils.populators.StringPopulator;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.TooLongContentValidationError;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationErrorType;

public class ValidationUtils {

	public static String validateNotEmptyParameter(String fieldName, HttpServletRequest req, Collection<ValidationError> errors) {

		String parameter = req.getParameter(fieldName);

		if (StringUtils.isEmpty(parameter)) {
			errors.add(new ValidationError(fieldName, ValidationErrorType.RequiredField));
			return null;
		} else {
			return parameter;
		}

	}

	public static <T> T validateParameter(String fieldName, HttpServletRequest req, boolean required, BeanStringPopulator<T> typePopulator, Collection<ValidationError> errors) {

		return validateParameter(fieldName, req, required, null, null, typePopulator, errors);
	}

	public static String validateParameter(String fieldName, HttpServletRequest req, boolean required, Integer minLength, Integer maxLength, Collection<ValidationError> errors) {

		return validateParameter(fieldName, req, required, minLength, maxLength, StringPopulator.getPopulator(), errors);
	}

	public static <T> T validateParameter(String fieldName, HttpServletRequest req, boolean required, Integer minLength, Integer maxLength, BeanStringPopulator<T> typePopulator, Collection<ValidationError> errors) {

		return validateParameter(fieldName, null, req, required, minLength, maxLength, typePopulator, errors);
	}

	public static <T> T validateParameter(String fieldName, String displayName, HttpServletRequest req, boolean required, Integer minLength, Integer maxLength, BeanStringPopulator<T> typePopulator, Collection<ValidationError> errors) {

		String value = req.getParameter(fieldName);

		if (StringUtils.isEmpty(value)) {

			if (required) {

				errors.add(new ValidationError(fieldName, displayName, ValidationErrorType.RequiredField));

			}

			return null;
		}

		value = value.trim();

		if (maxLength != null && value.length() > maxLength) {

			errors.add(new TooLongContentValidationError(fieldName, value.length(), maxLength));
			return null;

		} else if (minLength != null && value.length() < minLength) {

			errors.add(new ValidationError(fieldName, displayName, ValidationErrorType.TooShort));
			return null;
		}

		if (!typePopulator.validateFormat(value)) {

			errors.add(new ValidationError(fieldName, displayName, ValidationErrorType.InvalidFormat));
			return null;
		}

		return typePopulator.getValue(value);
	}

	public static <T> List<T> validateParameters(String fieldName, HttpServletRequest req, boolean required, BeanStringPopulator<T> typePopulator, Collection<ValidationError> errors) {

		return validateParameters(fieldName, req, required, null, null, typePopulator, errors);
	}

	public static <T> List<T> validateParameters(String fieldName, HttpServletRequest req, boolean required, Integer minLength, Integer maxLength, BeanStringPopulator<T> typePopulator, Collection<ValidationError> errors) {

		String[] parameterValues = req.getParameterValues(fieldName);

		if (parameterValues == null || parameterValues.length == 0 || !ValidationUtils.checkParameterValueLengths(parameterValues, 1)) {

			if (required) {

				errors.add(new ValidationError(fieldName, ValidationErrorType.RequiredField));

			}

			return null;
		}

		List<T> values = new ArrayList<T>();

		for (String value : parameterValues) {

			value = value.trim();

			if (maxLength != null && value.length() > maxLength) {

				errors.add(new ValidationError(fieldName, ValidationErrorType.TooLong));
				return null;

			} else if (minLength != null && value.length() < minLength) {

				errors.add(new ValidationError(fieldName, ValidationErrorType.TooShort));
				return null;
			}

			if (!typePopulator.validateFormat(value)) {

				errors.add(new ValidationError(fieldName, ValidationErrorType.InvalidFormat));
				return null;
			}

			values.add(typePopulator.getValue(value));
		}

		return values;

	}

	private static boolean checkParameterValueLengths(String[] parameterValues, int minLength) {

		for (String parameterValue : parameterValues) {
			if (parameterValue.length() < minLength) {
				return false;
			}
		}
		return true;
	}
}
