package se.unlogic.openhierarchy.foregroundmodules.siteprofile;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import se.unlogic.hierarchy.backgroundmodules.AnnotatedBackgroundModule;
import se.unlogic.hierarchy.core.annotations.InstanceManagerDependency;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleBackgroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.interfaces.SettingHandler;
import se.unlogic.hierarchy.core.settings.HTMLEditorSetting;
import se.unlogic.hierarchy.core.settings.Setting;
import se.unlogic.openhierarchy.foregroundmodules.siteprofile.interfaces.SiteProfileHandler;
import se.unlogic.openhierarchy.foregroundmodules.siteprofile.interfaces.SiteProfileSettingProvider;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.url.URLRewriter;


public class SiteProfileHTMLBackgroundModule extends AnnotatedBackgroundModule implements SiteProfileSettingProvider{

	@ModuleSetting
	@TextFieldSettingDescriptor(name="Profile setting ID", description="The ID of the site profile setting generated and used by this module. If multiple modules are to share the same setting then they should use the same ID", required=true)
	protected String profileSettingID;
	
	@ModuleSetting
	@TextFieldSettingDescriptor(name="Profile setting name", description="The name of the site profile setting generated and used by this module (by default the module name is used).", required=true)
	protected String profileSettingName;
	
	@ModuleSetting
	@TextFieldSettingDescriptor(name="Profile setting description", description="The description of the site profile setting generated and used by this module.", required=true)	
	protected String profileSettingDescription = "No description set";
	
	protected SiteProfileHandler siteProfileHandler;
	
	protected HTMLEditorSetting htmlEditorSetting;
	
	@Override
	public void init(BackgroundModuleDescriptor descriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		profileSettingID = "bgmodule-" + descriptor.getModuleID();
		profileSettingName = descriptor.getName();
		
		super.init(descriptor, sectionInterface, dataSource);
	}
	
	@Override
	protected BackgroundModuleResponse processBackgroundRequest(HttpServletRequest req, User user, URIParser uriParser) throws Exception {

		SettingHandler settingHandler = siteProfileHandler.getCurrentSettingHandler(user, req, uriParser);
		
		String html = settingHandler.getString(profileSettingID);
		
		if(html != null){
			
			html = URLRewriter.setAbsoluteLinkUrls(html, req);
			
			return new SimpleBackgroundModuleResponse(html);
		}
		
		return null;
	}

	@InstanceManagerDependency(required=true)
	public void setSiteProfileHandler(SiteProfileHandler siteProfileHandler) {
	
		if(siteProfileHandler != null){
			
			siteProfileHandler.addSettingProvider(this);
			
		}else{
			
			this.siteProfileHandler.removeSettingProvider(this);
		}
		
		this.siteProfileHandler = siteProfileHandler;
	}

	@Override
	protected void moduleConfigured() {

		htmlEditorSetting = new HTMLEditorSetting(profileSettingID, profileSettingName, profileSettingDescription, null, false);
	}

	@Override
	public List<Setting> getSiteProfileSettings() {

		return Collections.singletonList((Setting)htmlEditorSetting);
	}
	
	@Override
	public void unload() throws Exception {
		
		if(siteProfileHandler != null){
			
			siteProfileHandler.removeSettingProvider(this);			
		}
		
		super.unload();
		
	}
	
}
