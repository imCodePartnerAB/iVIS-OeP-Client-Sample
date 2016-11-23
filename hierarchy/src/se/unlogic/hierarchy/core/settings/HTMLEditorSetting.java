package se.unlogic.hierarchy.core.settings;

import se.unlogic.standardutils.validation.StringFormatValidator;


public class HTMLEditorSetting extends SingleValueNoAlternativesSetting {

	public HTMLEditorSetting(String id, String name, String description, String defaultValue, boolean required) {

		super(id, name, description, FormElement.HTML_EDITOR, defaultValue, required, null);
	}	
	
	public HTMLEditorSetting(String id, String name, String description, String defaultValue, boolean required, StringFormatValidator formatValidator) {

		super(id, name, description, FormElement.HTML_EDITOR, defaultValue, required, formatValidator);
	}

}
