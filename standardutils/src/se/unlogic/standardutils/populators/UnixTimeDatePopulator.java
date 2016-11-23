package se.unlogic.standardutils.populators;

import java.sql.Date;
import java.text.ParseException;

import se.unlogic.standardutils.date.DateUtils;
import se.unlogic.standardutils.date.ThreadSafeDateFormat;
import se.unlogic.standardutils.validation.StringFormatValidator;

public class UnixTimeDatePopulator extends BaseStringPopulator<Date> {

	private final ThreadSafeDateFormat dateFormat;

	public UnixTimeDatePopulator() {

		super();

		this.dateFormat = DateUtils.DATE_FORMATTER;
	}

	public UnixTimeDatePopulator(ThreadSafeDateFormat dateFormat) {

		super();

		this.dateFormat = dateFormat;
	}

	public UnixTimeDatePopulator(String populatorID, ThreadSafeDateFormat dateFormat) {

		super(populatorID);

		this.dateFormat = dateFormat;
	}

	public UnixTimeDatePopulator(String populatorID, ThreadSafeDateFormat dateFormat, StringFormatValidator formatValidator) {

		super(populatorID,formatValidator);
		this.dateFormat = dateFormat;
	}

	public Class<? extends Date> getType() {

		return Date.class;
	}

	public Date getValue(String value) {

		try {
			
			java.util.Date utilDate = this.dateFormat.parse(value);

			return new Date(utilDate.getTime());

		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean validateDefaultFormat(String value) {

		try {
			
			java.util.Date date = dateFormat.parse(value);
			
			if(date != null && date.getTime() < 0) {
				return false;
			}
			
		} catch (ParseException e) {
			return false;
		} catch (RuntimeException e) {
			return false;
		}
		
		return true;		
		
	}
	
}
