package se.unlogic.hierarchy.foregroundmodules.registration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.ViewFragment;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.webutils.http.URIParser;

public class RegistrationPluginHandler {

	private Logger log = Logger.getLogger(this.getClass());
	
	@SuppressWarnings("rawtypes")
	private final List<RegistrationPlugin> registrationPlugins;
	
	@SuppressWarnings("rawtypes")
	private final Map<RegistrationPlugin, ValidationException> validationErrorMap;
	
	@SuppressWarnings("rawtypes")
	private final Map<RegistrationPlugin, Object> pluginDataMap;

	@SuppressWarnings("rawtypes")
	public RegistrationPluginHandler(List<RegistrationPlugin> registrationPlugins) {

		this.registrationPlugins = registrationPlugins;
		validationErrorMap = new HashMap<RegistrationPlugin, ValidationException>(registrationPlugins.size());
		pluginDataMap = new HashMap<RegistrationPlugin, Object>(registrationPlugins.size());
	}

	public void populate(HttpServletRequest req) {

		for(RegistrationPlugin<?> registrationPlugin : registrationPlugins){
			
			try {
				Object data = registrationPlugin.populate(req);
				
				if(data != null){
					pluginDataMap.put(registrationPlugin, data);
				}
				
			}catch (ValidationException e) {
					
				validationErrorMap.put(registrationPlugin, e);
				
			} catch (Exception e) {
				
				log.error("Error populating registration plugin " + registrationPlugin, e);
			}
		}
	}

	public boolean hasValidationErrors() {

		return !validationErrorMap.isEmpty();
	}

	public List<ViewFragment> getViewFragments(HttpServletRequest req, URIParser uriParser) {

		List<ViewFragment> viewFragments = new ArrayList<ViewFragment>(registrationPlugins.size());
		
		for(RegistrationPlugin<?> registrationPlugin : registrationPlugins){
			
			try {
				ViewFragment viewFragment = registrationPlugin.getForm(req, uriParser, validationErrorMap.get(registrationPlugin));
				
				if(viewFragment != null){
					
					viewFragments.add(viewFragment);
				}
				
			} catch (Exception e) {

				log.error("Error getting view fragment from registration plugin " + registrationPlugin, e);
			}
		}
		
		return viewFragments;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void userAdded(User user){
		
		for(RegistrationPlugin registrationPlugin : registrationPlugins){
			
			try {
				registrationPlugin.userAdded(user, pluginDataMap.get(registrationPlugin));
				
			} catch (Exception e) {

				log.error("Error in registration plugin " + registrationPlugin, e);
			}
		}
	}
}
