package se.unlogic.hierarchy.core.settings;

import java.util.Collections;
import java.util.List;

abstract class SingleValueSetting extends Setting {

	private final String defaultValue;
	
	public SingleValueSetting(String id, String name, String description, FormElement formElement, String defaultValue, boolean required) {

		super(id, name, description, formElement, required);
		this.defaultValue = defaultValue;
	}

	@Override
	public List<String> getDefaultValues() {

		if(defaultValue != null){
		
			return Collections.singletonList(defaultValue);
		}
		
		return null;
	}
}
