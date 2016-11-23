package se.unlogic.hierarchy.core.settings;

import java.util.List;


public class MultiCheckboxSetting extends MultiValueSetting {

	public MultiCheckboxSetting(String id, String name, String description, FormElement formElement, List<Alternative> alternatives, List<String> defaultValues, boolean required) {

		super(id, name, description, FormElement.DROP_DOWN_MULTI, alternatives, defaultValues, required);
	}

}
