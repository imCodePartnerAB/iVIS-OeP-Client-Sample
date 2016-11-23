package se.unlogic.standardutils.date;

import java.text.ParseException;
import java.util.Date;

public interface ThreadSafeDateFormat {

	public Date parse(String date) throws ParseException;

	public String format(Date date);

}