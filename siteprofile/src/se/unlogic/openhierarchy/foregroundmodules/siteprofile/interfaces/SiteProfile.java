package se.unlogic.openhierarchy.foregroundmodules.siteprofile.interfaces;

import java.io.Serializable;
import java.util.List;

import se.unlogic.hierarchy.core.interfaces.SettingHandler;
import se.unlogic.standardutils.xml.Elementable;

public interface SiteProfile extends Serializable, Elementable{

	public Integer getProfileID();

	public String getName();

	public List<String> getDomains();

	public String getDesign();

	public SettingHandler getSettingHandler();

}