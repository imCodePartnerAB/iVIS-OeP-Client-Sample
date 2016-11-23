/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.emailutils.populators;

import se.unlogic.emailutils.framework.EmailUtils;
import se.unlogic.standardutils.populators.BaseStringPopulator;
import se.unlogic.standardutils.populators.BeanStringPopulator;
import se.unlogic.standardutils.validation.StringFormatValidator;

public class EmailPopulator extends BaseStringPopulator<String> implements BeanStringPopulator<String>{

	public EmailPopulator() {
		super("email");
	}

	public EmailPopulator(String populatorID) {
		super(populatorID);
	}

	public EmailPopulator(String populatorID, StringFormatValidator formatValidator) {
		super(populatorID, formatValidator);
	}

	public String getValue(String value) {
		return value;
	}

	@Override
	public boolean validateDefaultFormat(String value) {
		return EmailUtils.isValidEmailAddress(value);
	}

	public Class<? extends String> getType() {
		return String.class;
	}
}
