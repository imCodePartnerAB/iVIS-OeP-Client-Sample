package se.unlogic.hierarchy.foregroundmodules.loginselector;

import se.unlogic.standardutils.xml.GeneratedElementable;
import se.unlogic.standardutils.xml.XMLElement;

@XMLElement
public class ProviderConfiguration extends GeneratedElementable implements Comparable<ProviderConfiguration>{

	@XMLElement
	private final String providerID;

	@XMLElement
	private final String description;

	@XMLElement
	private final String buttonText;

	@XMLElement
	private final Integer sortIndex;

	public ProviderConfiguration(String providerID, String description, String buttonText, Integer sortIndex) {

		super();
		this.providerID = providerID;
		this.description = description;
		this.buttonText = buttonText;
		this.sortIndex = sortIndex;
	}

	public String getProviderID() {

		return providerID;
	}

	public String getDescription() {

		return description;
	}

	public String getButtonText() {

		return buttonText;
	}

	public Integer getSortIndex() {

		return sortIndex;
	}

	@Override
	public int compareTo(ProviderConfiguration o) {

		return sortIndex.compareTo(o.getSortIndex());
	}
}
