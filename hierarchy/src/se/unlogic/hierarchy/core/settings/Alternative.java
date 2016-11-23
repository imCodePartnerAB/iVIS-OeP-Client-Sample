package se.unlogic.hierarchy.core.settings;

import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.xml.GeneratedElementable;
import se.unlogic.standardutils.xml.XMLElement;

@XMLElement
public final class Alternative extends GeneratedElementable {

	@XMLElement
	private final String name;

	@XMLElement
	private final String value;

	public Alternative(String name, String value) {

		if (StringUtils.isEmpty(name)) {

			throw new RuntimeException("name cannot be null");
		}

		if (StringUtils.isEmpty(value)) {

			throw new RuntimeException("value cannot be null");
		}

		this.name = name;
		this.value = value;
	}

	public String getName() {

		return name;
	}

	public String getValue() {

		return value;
	}
}
