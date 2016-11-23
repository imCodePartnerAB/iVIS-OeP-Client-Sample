package se.unlogic.hierarchy.core.settings;

import java.util.List;


public class RadioButtonSetting extends SingleValueMultiAlternativesSetting {

	public RadioButtonSetting(String id, String name, String description, String defaultValue, List<Alternative> alternatives) {

		super(id, name, description, FormElement.RADIO, defaultValue, alternatives, true);
	}

}
