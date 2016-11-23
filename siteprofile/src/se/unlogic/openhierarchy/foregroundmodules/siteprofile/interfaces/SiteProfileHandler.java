package se.unlogic.openhierarchy.foregroundmodules.siteprofile.interfaces;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.SettingHandler;
import se.unlogic.hierarchy.core.settings.Setting;
import se.unlogic.openhierarchy.foregroundmodules.siteprofile.beans.Profile;
import se.unlogic.webutils.http.URIParser;


public interface SiteProfileHandler {

	public boolean addSettingProvider(SiteProfileSettingProvider settingProvider);
	
	public boolean removeSettingProvider(SiteProfileSettingProvider settingProvider);
	
	public void ensureGlobalSettingValues(List<Setting> settings) throws SQLException;
	
	public SettingHandler getGlobalSettingHandler();
	
	/**
	 * A method for retrieving the profile matching the current request.
	 * 
	 * @param user
	 * @param req
	 * @param uriParser
	 * @return null or the {@link Profile} matching the current request. 
	 */
	public SiteProfile getCurrentProfile(User user, HttpServletRequest req, URIParser uriParser);
	
	/**
	 * A method for retrieving the {@link SettingHandler} matching the current request.
	 * 
	 * @param user
	 * @param req
	 * @param uriParser
	 * @return a {@link SettingHandler} matching the current request. Returns the global {@link SettingHandler} if no mathing profile is found.
	 */
	public SettingHandler getCurrentSettingHandler(User user, HttpServletRequest req, URIParser uriParser);

	public List<? extends SiteProfile> getProfiles();

	public SiteProfile getProfile(Integer profileID);
}
