package se.unlogic.standardutils.date;

import java.sql.Timestamp;

import se.unlogic.standardutils.string.Stringyfier;


public class DateTimeStringyfier implements Stringyfier<Timestamp> {

	public String format(Timestamp bean) {

		return DateUtils.DATE_TIME_FORMATTER.format(bean);
	}

}
