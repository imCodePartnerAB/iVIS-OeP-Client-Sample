package se.unlogic.openhierarchy.foregroundmodules.siteprofile.validationerrors;

import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.xml.XMLElement;

@XMLElement(name = "validationError")
public class DomainAlreadyInUseValidationError extends ValidationError {

	@XMLElement
	private final String domain;

	@XMLElement
	private final String profileName;

	public DomainAlreadyInUseValidationError(String domain, String profileName) {

		super("DomainAlreadyInUse");
		this.domain = domain;
		this.profileName = profileName;
	}

	public String getDomain() {

		return domain;
	}

	public String getProfileName() {

		return profileName;
	}

}
