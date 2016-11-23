package se.unlogic.emailutils.populators;

import se.unlogic.emailutils.framework.EmailUtils;
import se.unlogic.standardutils.populators.BaseStringPopulator;
import se.unlogic.standardutils.populators.BeanStringPopulator;
import se.unlogic.standardutils.validation.StringFormatValidator;

public class CommaSeparatedEmailPopulator extends BaseStringPopulator<String> implements BeanStringPopulator<String>{

	public CommaSeparatedEmailPopulator() {
		super("emails");
	}

	public CommaSeparatedEmailPopulator(String populatorID) {
		super(populatorID);
	}

	public CommaSeparatedEmailPopulator(String populatorID, StringFormatValidator formatValidator) {
		super(populatorID, formatValidator);
	}

	public String getValue(String value) {
		return value;
	}

	@Override
	public boolean validateDefaultFormat(String value) {
		
		String[] values = value.split(",");
		
		for(String email : values) {
			if(!EmailUtils.isValidEmailAddress(email.trim())) {
				return false;
			}
		}
		
		return true;
	}

	public Class<? extends String> getType() {
		return String.class;
	}
}
