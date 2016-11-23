package se.unlogic.standardutils.date;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import se.unlogic.standardutils.factory.BeanFactory;


public class SimpleDateFormatFactory implements BeanFactory<SimpleDateFormat> {

	protected String format;
	protected Locale locale;
	protected TimeZone timeZone;

	public SimpleDateFormatFactory(String format) {

		super();
		this.format = format;
	}



	public SimpleDateFormatFactory(String format, Locale locale) {

		this(format);
		this.locale = locale;
	}

	public SimpleDateFormatFactory(String format, Locale locale, TimeZone timeZone) {

		this(format,locale);

		this.timeZone = timeZone;
	}



	public SimpleDateFormat newInstance() {

		SimpleDateFormat simpleDateFormat;

		if(locale != null){

			simpleDateFormat = new SimpleDateFormat(format, locale);

		}else{

			simpleDateFormat = new SimpleDateFormat(format);
		}

		if(timeZone != null){

			simpleDateFormat.setTimeZone(timeZone);
		}

		return simpleDateFormat;
	}
}
