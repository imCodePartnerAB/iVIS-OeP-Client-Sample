/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.time;

import java.sql.Timestamp;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import se.unlogic.standardutils.date.PooledSimpleDateFormat;

public class TimeUtils {

	public static PooledSimpleDateFormat HOUR_FORMATTER = new PooledSimpleDateFormat("HH");
	public static PooledSimpleDateFormat MINUTE_FORMATTER = new PooledSimpleDateFormat("mm");
	public static PooledSimpleDateFormat SECOND_FORMATTER = new PooledSimpleDateFormat("ss");
	public static PooledSimpleDateFormat TIME_FORMATTER = new PooledSimpleDateFormat("HH:mm");
	public static PooledSimpleDateFormat TIME_SECONDS_FORMATTER = new PooledSimpleDateFormat("HH:mm:ss");

	public static String secondsToString(long time) {

		return millisecondsToString(time * 1000);
	}

	public static String millisecondsToString(long time) {

		int milliseconds = (int)(time % 1000);
		int seconds = (int)((time / 1000) % 60);
		int minutes = (int)((time / 60000) % 60);
		int hours = (int)((time / 3600000) % 24);
		String millisecondsStr = (milliseconds < 10 ? "00" : (milliseconds < 100 ? "0" : "")) + milliseconds;
		String secondsStr = (seconds < 10 ? "0" : "") + seconds;
		String minutesStr = (minutes < 10 ? "0" : "") + minutes;
		String hoursStr = (hours < 10 ? "0" : "") + hours;
		return new String(hoursStr + ":" + minutesStr + ":" + secondsStr + "." + millisecondsStr);
	}

	public static String millisecondsToShortString(long time) {

		int minutes = (int)((time / 60000) % 60);
		int hours = (int)((time / 3600000) % 24);

		String minutesStr = (minutes < 10 ? "0" : "") + minutes;
		String hoursStr = (hours < 10 ? "0" : "") + hours;

		return new String(hoursStr + ":" + minutesStr);
	}

	public static int getMinutes(long time) {

		return Integer.valueOf(MINUTE_FORMATTER.format(new Date(time)));
	}

	public static int getSeconds(long time) {

		return Integer.valueOf(SECOND_FORMATTER.format(new Date(time)));
	}

	public static int getHour(long time) {

		return Integer.valueOf(HOUR_FORMATTER.format(new Date(time)));
	}

	public static String hourAndMinutesToString(int hours, int minutes) {

		String minutesStr = (minutes < 10 ? "0" : "") + minutes;
		String hoursStr = (hours < 10 ? "0" : "") + hours;

		return new String(hoursStr + ":" + minutesStr);

	}

	public static Timestamp getCurrentTimestamp() {

		return new Timestamp(System.currentTimeMillis());
	}

	public static Timestamp getTimeStamp(XMLGregorianCalendar xmlCalendar) {

		if(xmlCalendar == null){

			return null;
		}

		return new Timestamp(xmlCalendar.toGregorianCalendar().getTimeInMillis());
	}
}
