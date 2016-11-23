/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.populators;

import se.unlogic.standardutils.validation.StringFormatValidator;


public class PrimitiveFloatPopulator extends FloatPopulator {

	public PrimitiveFloatPopulator() {
		super();
	}

	public PrimitiveFloatPopulator(String populatorID, StringFormatValidator formatValidator) {
		super(populatorID, formatValidator);
	}

	public PrimitiveFloatPopulator(String populatorID) {
		super(populatorID);
	}

	@Override
	public Class<? extends Float> getType() {

		return float.class;
	}

	@Override
	public Float getValue(String value) {

		if(value == null){
			return 0f;
		}

		return super.getValue(value);
	}
}
