package se.unlogic.hierarchy.core.settings;

import java.util.Collections;
import java.util.List;

import se.unlogic.standardutils.collections.CollectionUtils;


class SingleValueMultiAlternativesSetting extends SingleValueSetting {

	private final List<Alternative> alternatives;
	
	public SingleValueMultiAlternativesSetting(String id, String name, String description, FormElement formElement, String defaultValue, List<Alternative> alternatives, boolean required) {

		super(id, name, description, formElement, defaultValue, required);
		
		if(CollectionUtils.isEmpty(alternatives)){
			
			throw new RuntimeException("alternatives cannot be empty");
		}
		
		this.alternatives = alternatives;
		
		if(defaultValue != null){
			
			boolean matchingAlernativeFound = false;
			
			for(Alternative alternative : alternatives){
				
				if(alternative.getValue().equals(defaultValue)){
					
					matchingAlernativeFound = true;
					break;
				}
			}
			
			if(!matchingAlernativeFound){
			
				throw new RuntimeException("Unable to find default value " + defaultValue + " in list of alternatives");
			}			
		}
	}

	@Override
	protected List<Alternative> getAlternatives() {

		return alternatives;
	}

	@Override
	public List<String> parseAndValidate(List<String> values) throws InvalidFormatException {

		String value = values.get(0);
		
		for(Alternative alternative : alternatives){
			
			if(alternative.getValue().equals(value)){
				
				return Collections.singletonList(value);
			}
		}
		
		throw new InvalidFormatException();
	}
}
