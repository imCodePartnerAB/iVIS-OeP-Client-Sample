package se.unlogic.openhierarchy.foregroundmodules.siteprofile.beans;

import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Key;
import se.unlogic.standardutils.dao.annotations.ManyToOne;
import se.unlogic.standardutils.dao.annotations.Table;

@Table(name="site_profile_settings")
public class ProfileSettingValue extends GlobalSettingValue {

	@DAOManaged(columnName="profileID")
	@Key
	@ManyToOne
	private Profile profile;


	public Profile getProfile() {

		return profile;
	}


	public void setProfile(Profile profile) {

		this.profile = profile;
	}
}
