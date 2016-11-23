package se.unlogic.hierarchy.core.settings;

import se.unlogic.standardutils.validation.StringFormatValidator;


public class TextAreaSetting extends SingleValueNoAlternativesSetting {

	public TextAreaSetting(String id, String name, String description, String defaultValue, boolean required) {

		super(id, name, description, FormElement.TEXT_AREA, defaultValue, required, null);
	}	
	
	public TextAreaSetting(String id, String name, String description, String defaultValue, boolean required, StringFormatValidator formatValidator) {

		super(id, name, description, FormElement.TEXT_AREA, defaultValue, required, formatValidator);
	}

}
