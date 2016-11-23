package se.unlogic.hierarchy.core.settings;

import java.util.Collections;
import java.util.List;

import se.unlogic.standardutils.validation.StringFormatValidator;


class SingleValueNoAlternativesSetting extends SingleValueSetting {

	protected final StringFormatValidator formatValidator;
	
	public SingleValueNoAlternativesSetting(String id, String name, String description, FormElement formElement, String defaultValue, boolean required, StringFormatValidator formatValidator) {

		super(id, name, description, formElement, defaultValue, required);
		this.formatValidator = formatValidator;
	}

	@Override
	protected final List<Alternative> getAlternatives() {

		return null;
	}

	@Override
	public List<String> parseAndValidate(List<String> values) throws InvalidFormatException {

		String value = values.get(0);
		
		if(formatValidator != null){
			
			if(!formatValidator.validateFormat(value)){
				
				throw new InvalidFormatException();
			}
		}
		
		return Collections.singletonList(value);
	}
}
