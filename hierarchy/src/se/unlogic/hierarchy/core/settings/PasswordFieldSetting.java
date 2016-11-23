package se.unlogic.hierarchy.core.settings;

import se.unlogic.standardutils.validation.StringFormatValidator;


public class PasswordFieldSetting extends SingleValueNoAlternativesSetting {

	public PasswordFieldSetting(String id, String name, String description, String defaultValue, boolean required) {

		super(id, name, description, FormElement.PASSWORD, defaultValue, required, null);
	}	
	
	public PasswordFieldSetting(String id, String name, String description, String defaultValue, boolean required, StringFormatValidator formatValidator) {

		super(id, name, description, FormElement.PASSWORD, defaultValue, required, formatValidator);
	}

}
