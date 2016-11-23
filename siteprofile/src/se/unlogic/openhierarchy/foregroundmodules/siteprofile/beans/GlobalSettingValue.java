package se.unlogic.openhierarchy.foregroundmodules.siteprofile.beans;

import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Key;
import se.unlogic.standardutils.dao.annotations.OrderBy;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.xml.GeneratedElementable;

@Table(name = "site_profile_global_settings")
public class GlobalSettingValue extends GeneratedElementable {

	@DAOManaged
	@Key
	@OrderBy
	private String settingID;

	@DAOManaged(columnName="sortIndex")
	@Key
	@OrderBy
	private Integer index;

	@DAOManaged
	private String value;

	public Integer getIndex() {

		return index;
	}

	public void setIndex(Integer index) {

		this.index = index;
	}

	public String getValue() {

		return value;
	}

	public void setValue(String value) {

		this.value = value;
	}

	public String getSettingID() {

		return settingID;
	}

	public void setSettingID(String settingID) {

		this.settingID = settingID;
	}
}
