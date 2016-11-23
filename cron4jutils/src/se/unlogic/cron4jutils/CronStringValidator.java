package se.unlogic.cron4jutils;

import it.sauronsoftware.cron4j.SchedulingPattern;
import se.unlogic.standardutils.validation.StringFormatValidator;


public class CronStringValidator implements StringFormatValidator {

	public boolean validateFormat(String value) {

		return SchedulingPattern.validate(value);
	}

}
