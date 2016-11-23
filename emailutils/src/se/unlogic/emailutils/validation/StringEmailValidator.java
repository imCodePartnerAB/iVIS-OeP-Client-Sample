/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.emailutils.validation;

import se.unlogic.emailutils.framework.EmailUtils;
import se.unlogic.standardutils.validation.StringFormatValidator;

public class StringEmailValidator implements StringFormatValidator {

	public boolean validateFormat(String value) {

		return EmailUtils.isValidEmailAddress(value);
	}

}
