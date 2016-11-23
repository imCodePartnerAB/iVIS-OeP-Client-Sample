package se.unlogic.hierarchy.core.beans;

import se.unlogic.hierarchy.foregroundmodules.userprofile.AttributeMode;
import se.unlogic.standardutils.populators.StringPopulator;
import se.unlogic.standardutils.validation.StringFormatValidator;
import se.unlogic.standardutils.xml.GeneratedElementable;
import se.unlogic.standardutils.xml.XMLElement;

@XMLElement
public class AttributeDescriptor extends GeneratedElementable {

	@XMLElement(fixCase = true)
	private final String name;

	@XMLElement(fixCase = true)
	private final String displayName;

	@XMLElement(fixCase = true)
	private final AttributeMode attributeMode;

	@XMLElement(fixCase = true)
	private final Integer maxLength;

	private final StringFormatValidator validator;

	private final StringPopulator stringPopulator;

	public AttributeDescriptor(String name, String displayName, AttributeMode mode, Integer maxLength, StringFormatValidator validator) {

		if(name == null){

			throw new NullPointerException("name cannot be null");

		}else if(mode == null){

			throw new NullPointerException("attributeMode cannot be null");
		}

		this.name = name;
		this.displayName = displayName;
		this.attributeMode = mode;
		this.maxLength = maxLength;
		this.validator = validator;

		if(validator == null){

			stringPopulator = StringPopulator.getPopulator();

		}else{

			stringPopulator = new StringPopulator(validator);
		}
	}

	public String getDisplayName() {

		return displayName;
	}

	public String getName() {

		return name;
	}

	public StringFormatValidator getValidator() {

		return validator;
	}

	public Integer getMaxLength() {

		return maxLength;
	}

	public AttributeMode getAttributeMode() {

		return attributeMode;
	}

	public StringPopulator getStringPopulator() {

		return stringPopulator;
	}

	@Override
	public String toString() {

		return name + " (display name: " + displayName + ", mode: " + attributeMode + ")";
	}
}
