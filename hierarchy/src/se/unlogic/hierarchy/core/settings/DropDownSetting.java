package se.unlogic.hierarchy.core.settings;

import java.util.List;


public class DropDownSetting extends SingleValueMultiAlternativesSetting {

	public DropDownSetting(String id, String name, String description, String defaultValue, List<Alternative> alternatives, boolean required) {

		super(id, name, description, FormElement.DROP_DOWN, defaultValue, alternatives, required);
	}

}
