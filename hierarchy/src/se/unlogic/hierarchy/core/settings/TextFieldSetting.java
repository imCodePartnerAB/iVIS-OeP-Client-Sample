package se.unlogic.hierarchy.core.settings;

import se.unlogic.standardutils.validation.StringFormatValidator;


public class TextFieldSetting extends SingleValueNoAlternativesSetting {

	public TextFieldSetting(String id, String name, String description, String defaultValue, boolean required) {

		super(id, name, description, FormElement.TEXT_FIELD, defaultValue, required, null);
	}	
	
	public TextFieldSetting(String id, String name, String description, String defaultValue, boolean required, StringFormatValidator formatValidator) {

		super(id, name, description, FormElement.TEXT_FIELD, defaultValue, required, formatValidator);
	}

}
