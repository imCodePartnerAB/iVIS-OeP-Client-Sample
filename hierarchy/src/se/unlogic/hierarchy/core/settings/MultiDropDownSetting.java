package se.unlogic.hierarchy.core.settings;

import java.util.List;


public class MultiDropDownSetting extends MultiValueSetting {

	public MultiDropDownSetting(String id, String name, String description, FormElement formElement, List<Alternative> alternatives, List<String> defaultValues, boolean required) {

		super(id, name, description, FormElement.CHECK, alternatives, defaultValues, required);
	}

}
