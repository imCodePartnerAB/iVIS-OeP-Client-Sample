package se.unlogic.standardutils.populators;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.regex.Pattern;

import se.unlogic.standardutils.dao.BeanResultSetPopulator;
import se.unlogic.standardutils.numbers.NumberUtils;

public class SocialSecurityPopulator extends BaseStringPopulator<String> implements BeanResultSetPopulator<String>, BeanStringPopulator<String>{

	//http://sv.wikipedia.org/wiki/Personnummer#Sverige
	Pattern pattern10 = Pattern.compile("[0-9]{6}[-+][0-9]{4}");
	Pattern pattern12 = Pattern.compile("(19|20)[0-9]{6}-[0-9]{4}");
	
	
	public SocialSecurityPopulator() {
		super();
	}

	private static final SocialSecurityPopulator POPULATOR = new SocialSecurityPopulator();

	public String populate(ResultSet rs) throws SQLException {
		return rs.getString(1);
	}

	public static SocialSecurityPopulator getPopulator(){
		return POPULATOR;
	}

	public String getValue(String value) {
		return value;
	}

	@Override
	public boolean validateDefaultFormat(String value) {
		
		int year = Calendar.getInstance().get(Calendar.YEAR);
		
		// Syntax check
		if(!this.pattern12.matcher(value).matches()) {
			
			if(this.pattern10.matcher(value).matches()) {
				int currentCentury = year/100;
				int currentDec = year%100;
				int decennium = Integer.valueOf(value.substring(0, 2));
				
				if(decennium > currentDec){
					currentCentury -= 1;
				}
				
				if(value.contains("+")){
					currentCentury -= 1;
					value = value.replace("+", "-");
				}
				
				value = Integer.toString(currentCentury) + value;
			} else	{
				return false;
			}
		}
		
//		System.out.println(value + "<");
		
		// Not in the future check
		if(Integer.valueOf(value.substring(0, 4)) > year) {
			return false;
		}
		
		// Valid checksum by Luhn algorithm?
		return NumberUtils.isValidCC(this.format(value));

	}

	public Class<? extends String> getType() {
		return String.class;
	}
	
	/**
	 * Converts 12 digit "personnummer" to 10 digit personnummer
	 * Strips the dash character if present
	 * @param value
	 * @return
	 */
	protected String format(String value) {
		String formattedValue;
		if((formattedValue = value.replace("-", "")).length() == 12) {
			return formattedValue.substring(2);
		}
		return formattedValue;
	}
	
	public static void main(String args[]){
		String ss[] = new String[]{
				"930924-8616",		//t
				"19930924-8616",	//t
				"20930924-8616",	//f
				"930924+8616",	//t
				};
		
		SocialSecurityPopulator ssp = new SocialSecurityPopulator();
		
		for(String s : ss){
			System.out.println(s + " = " + ssp.validateDefaultFormat(s));
		}
		
	}
}
