package se.unlogic.hierarchy.core.settings;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.xml.Elementable;
import se.unlogic.standardutils.xml.XMLUtils;

public abstract class Setting implements Elementable {

	protected final String id;
	protected final String name;
	protected final String description;
	protected final FormElement formElement;
	protected final boolean required;

	Setting(String id, String name, String description, FormElement formElement, boolean required) {

		if (StringUtils.isEmpty(id)) {

			throw new RuntimeException("ID cannot be null or empty");

		}

		if (StringUtils.isEmpty(name)) {

			throw new RuntimeException("name cannot be null or empty");

		}

		if (StringUtils.isEmpty(description)) {

			throw new RuntimeException("description cannot be null or empty");
		}

		if (formElement == null) {

			throw new NullPointerException("formElement cannot be null");
		}

		this.id = id;
		this.name = name;
		this.description = description;
		this.formElement = formElement;
		this.required = required;
	}

	public final String getId() {

		return id;
	}

	public final String getName() {

		return name;
	}

	public final String getDescription() {

		return description;
	}

	@Override
	public final Element toXML(Document doc) {

		Element attributeElement = doc.createElement("SettingDescriptor");

		XMLUtils.appendNewElement(doc, attributeElement, "ID", id);
		XMLUtils.appendNewElement(doc, attributeElement, "Name", name);
		XMLUtils.appendNewElement(doc, attributeElement, "Description", description);
		XMLUtils.appendNewElement(doc, attributeElement, "FormElement", formElement);

		XMLUtils.append(doc, attributeElement, "DefaultValues", "Value", getDefaultValues());
		XMLUtils.append(doc, attributeElement, "Options", getAlternatives());

		return attributeElement;
	}

	public abstract List<String> getDefaultValues();

	protected abstract List<Alternative> getAlternatives();

	public abstract List<String> parseAndValidate(List<String> values) throws InvalidFormatException;

	public final FormElement getFormElement() {

		return formElement;
	}

	public boolean isRequired() {

		return required;
	}

	@Override
	public final int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public final boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Setting other = (Setting) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public final String toString() {

		return name + " (ID: " + id + ")";
	}

	public boolean validateWithoutValues(){

		return false;
	}
}
