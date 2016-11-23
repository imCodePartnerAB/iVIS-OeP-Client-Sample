package se.unlogic.emailutils.validation;

import se.unlogic.emailutils.framework.EmailUtils;
import se.unlogic.standardutils.validation.StringFormatValidator;


public class StringMultiEmailValidator implements StringFormatValidator {

	public boolean validateFormat(String value) {

		String[] emails = value.split("[,;:]");

		for(String email : emails) {
			
			if(!EmailUtils.isValidEmailAddress(email.trim())) {
				
				return false;
			}
			
		}
		
		return true;

	}
	
}
