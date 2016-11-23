package se.unlogic.hierarchy.core.settings;

import java.util.Collections;
import java.util.List;





public class SingleCheckBoxSetting extends SingleValueNoAlternativesSetting {

	public SingleCheckBoxSetting(String id, String name, String description, boolean defaultValue) {

		super(id, name, description, FormElement.CHECK, Boolean.toString(true), false, null);
	}

	@Override
	public boolean validateWithoutValues() {

		return true;
	}

	@Override
	public List<String> parseAndValidate(List<String> values) throws InvalidFormatException {

		if(values == null){

			return Collections.singletonList("false");
		}

		return Collections.singletonList(Boolean.toString(Boolean.parseBoolean(values.get(0))));
	}
}
