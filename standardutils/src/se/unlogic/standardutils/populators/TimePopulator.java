/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.populators;

import java.sql.Time;
import java.text.ParseException;

import se.unlogic.standardutils.date.DateUtils;
import se.unlogic.standardutils.date.ThreadSafeDateFormat;
import se.unlogic.standardutils.time.TimeUtils;
import se.unlogic.standardutils.validation.StringFormatValidator;

public class TimePopulator extends BaseStringPopulator<Time> {

	private final ThreadSafeDateFormat dateFormat;

	public TimePopulator() {

		super();

		this.dateFormat = TimeUtils.TIME_FORMATTER;
	}

	public TimePopulator(ThreadSafeDateFormat dateFormat) {

		super();

		this.dateFormat = dateFormat;
	}

	public TimePopulator(String populatorID, ThreadSafeDateFormat dateFormat) {

		super(populatorID);

		this.dateFormat = dateFormat;
	}

	public TimePopulator(String populatorID, ThreadSafeDateFormat dateFormat, StringFormatValidator formatValidator) {

		super(populatorID,formatValidator);
		this.dateFormat = dateFormat;
	}

	public Class<? extends Time> getType() {

		return Time.class;
	}

	public Time getValue(String value) {

		try {
			java.util.Date utilDate = this.dateFormat.parse(value);

			return new Time(utilDate.getTime());

		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean validateDefaultFormat(String value) {

		return DateUtils.isValidDate(this.dateFormat, value);
	}
}
