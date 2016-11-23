/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.populators;

import java.sql.ResultSet;
import java.sql.SQLException;

import se.unlogic.standardutils.dao.BeanResultSetPopulator;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.validation.StringFormatValidator;

public class FloatPopulator extends BaseStringPopulator<Float> implements BeanResultSetPopulator<Float> {

	private static final FloatPopulator POPULATOR = new FloatPopulator();

	public static FloatPopulator getPopulator(){
		return POPULATOR;
	}

	private int columnIndex = 1;

	public FloatPopulator() {
		super();
	}

	public FloatPopulator(int columnIndex) {
		super();

		this.columnIndex = columnIndex;
	}

	public FloatPopulator(String populatorID, StringFormatValidator formatValidator) {
		super(populatorID, formatValidator);
	}

	public FloatPopulator(String populatorID) {
		super(populatorID);
	}

	public Float populate(ResultSet rs) throws SQLException {
		return rs.getFloat(columnIndex);
	}

	public Float getValue(String value) {

		return Float.valueOf(value);
	}

	@Override
	public boolean validateDefaultFormat(String value) {

		return NumberUtils.isFloat(value);
	}

	public Class<? extends Float> getType() {

		return Float.class;
	}
}
