package se.unlogic.standardutils.validation;

import se.unlogic.standardutils.arrays.ArrayUtils;


public class ArrayStringFormatValidator implements StringFormatValidator {

	private final String[] validStrings;
	
	public ArrayStringFormatValidator(String... validStrings) {

		super();
		
		if(validStrings == null){
			
			throw new NullPointerException("validStrings cannot be null");
		}
		
		this.validStrings = validStrings;
	}	

	public boolean validateFormat(String value) {

		if(value != null && ArrayUtils.contains(validStrings, value)){
			
			return true;
		}
		
		return false;
	}
}
