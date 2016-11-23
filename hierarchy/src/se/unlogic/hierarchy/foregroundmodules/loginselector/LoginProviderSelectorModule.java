package se.unlogic.hierarchy.foregroundmodules.loginselector;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.annotations.CheckboxSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.GroupMultiListSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.UserMultiListSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.SimpleProviderDescriptor;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.interfaces.AccessInterface;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.LoginProvider;
import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.MutableSettingHandler;
import se.unlogic.hierarchy.core.interfaces.ProviderDescriptor;
import se.unlogic.hierarchy.core.utils.AccessUtils;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.populators.IntegerPopulator;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.NonNegativeStringIntegerValidator;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.validation.ValidationUtils;


public class LoginProviderSelectorModule extends AnnotatedForegroundModule implements LoginProvider, AccessInterface{

	@ModuleSetting(allowsNull = true)
	@GroupMultiListSettingDescriptor(name="Admin groups",description="Groups allowed to administrate this module")
	protected List<Integer> adminGroupIDs;

	@ModuleSetting(allowsNull = true)
	@UserMultiListSettingDescriptor(name="Admin users",description="Users allowed to administrate this module")
	protected List<Integer> adminUserIDs;
	
	@ModuleSetting(allowsNull = true)
	@TextFieldSettingDescriptor(name = "Editor CSS", description = "Path to the desired CSS stylesheet for FCKEditor (relative from the contextpath)", required = false)
	protected String cssPath;
	
	@ModuleSetting
	@CheckboxSettingDescriptor(name = "Add to login handler", description = "Controls if this module should add itself to the login handler as a login provider")
	protected boolean addToLoginHandler = true;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Login provider priority", description = "The priority of the login provider from this module (lower value means higher priority)", required = true, formatValidator = NonNegativeStringIntegerValidator.class)
	protected int priority = 100;
	
	protected ProviderDescriptor providerDescriptor;
	
	protected List<ProviderConfiguration> providerConfigurations;
	
	@Override
	public void update(ForegroundModuleDescriptor descriptor, DataSource dataSource) throws Exception {

		//Re-add provider configurations to module settings to prevent the settings from being lost when the module is updated via SystemAdminModule
		if(providerConfigurations != null){
			
			saveProviderConfiguration(providerConfigurations, descriptor);
		}
		
		super.update(descriptor, dataSource);
	}
	
	@Override
	protected void parseSettings(MutableSettingHandler mutableSettingHandler) throws Exception {

		super.parseSettings(mutableSettingHandler);
		
		List<String> supportedProviderIDs = mutableSettingHandler.getStrings("supportedProviders");
		
		if(CollectionUtils.isEmpty(supportedProviderIDs)){
			
			this.providerConfigurations = null;
			
		}else{
			
			List<ProviderConfiguration> providerConfigurations = new ArrayList<ProviderConfiguration>(supportedProviderIDs.size());
			
			for(String providerID : supportedProviderIDs){
				
				String description = mutableSettingHandler.getString(providerID + "-description");
				String buttonText = mutableSettingHandler.getString(providerID + "-button");
				Integer sortIndex = mutableSettingHandler.getInt(providerID + "-sortIndex");
				
				if(sortIndex == null){
					
					sortIndex = 255;
				}
				
				providerConfigurations.add(new ProviderConfiguration(providerID, description, buttonText, sortIndex));
			}
			
			Collections.sort(providerConfigurations);
			
			this.providerConfigurations = providerConfigurations;
		}
		
		if(addToLoginHandler){

			this.sectionInterface.getSystemInterface().getLoginHandler().addProvider(this);

		}else{

			this.sectionInterface.getSystemInterface().getLoginHandler().removeProvider(this);
		}		
	}
	
	@Override
	protected void moduleConfigured() throws Exception {

		super.moduleConfigured();
		
		providerDescriptor = new SimpleProviderDescriptor(moduleDescriptor);
	}
	
	@Override
	public ForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) {
		
		log.info("User " + user + " listing login providers");

		Document doc = this.createDocument(req, uriParser, user);

		Element selectProviderElement = doc.createElement("SelectProvider");
		doc.getFirstChild().appendChild(selectProviderElement);
		
		if(this.providerConfigurations != null){
			
			Element loginProvidersElement = doc.createElement("LoginProviders");
			
			for(ProviderConfiguration providerConfiguration : this.providerConfigurations){
				
				LoginProvider loginProvider = systemInterface.getLoginHandler().getProvider(providerConfiguration.getProviderID());
				
				if(loginProvider != null && supportsRequest(loginProvider, req, uriParser)){
					
					loginProvidersElement.appendChild(providerConfiguration.toXML(doc));
				}
			}
			
			if(loginProvidersElement.hasChildNodes()){
				
				selectProviderElement.appendChild(loginProvidersElement);
			}
		}
		
		XMLUtils.appendNewElement(doc, selectProviderElement, "Redirect", req.getParameter("redirect"));
		XMLUtils.appendNewElement(doc, selectProviderElement, "FullAlias", this.getFullAlias());
		
		if(AccessUtils.checkAccess(user, this)){
			
			XMLUtils.appendNewElement(doc, selectProviderElement, "IsAdmin");
		}
		
		return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), getDefaultBreadcrumb());
	}
	
	@WebPublic
	public synchronized ForegroundModuleResponse login(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {
		
		String providerID = req.getParameter("provider");
		
		if(!StringUtils.isEmpty(providerID) && isSupportedProvider(providerID)){
			
			LoginProvider loginProvider = systemInterface.getLoginHandler().getProvider(providerID);
			
			if(providerID != null && supportsRequest(loginProvider, req, uriParser)){
				
				String redirect = req.getParameter("redirect");
				
				loginProvider.handleRequest(req, res, uriParser, redirect);
			}
		}
		
		return null;
	}
	
	private boolean supportsRequest(LoginProvider loginProvider, HttpServletRequest req, URIParser uriParser) {

		try {
			return loginProvider.supportsRequest(req, uriParser);
			
		} catch (Throwable e) {

			log.error("Error in login provider " + loginProvider + " while checking support of request from " + req.getRemoteAddr(), e);
		}
		
		return false;
	}

	private boolean isSupportedProvider(String providerID) {

		if(this.providerConfigurations != null){
			
			for(ProviderConfiguration providerConfiguration : this.providerConfigurations){
				
				if(providerConfiguration.getProviderID().equals(providerID)){
					
					return true;
				}
			}
		}
		
		return false;
	}

	@WebPublic(alias="config")
	public synchronized ForegroundModuleResponse configure(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException, AccessDeniedException, SQLException {

		if(!AccessUtils.checkAccess(user, this)){
			throw new AccessDeniedException("Module administration denied");
		}
		
		List<ValidationError> validationErrors = null;
		
		if(req.getMethod().equals("POST")){
			
			validationErrors = new ArrayList<ValidationError>();
			
			List<ProviderConfiguration> providerConfigurations = null;
			
			String[] providerIDs = req.getParameterValues("providerID");
			
			if(providerIDs != null){
				
				providerConfigurations = new ArrayList<ProviderConfiguration>(providerIDs.length);
				
				for(String providerID : providerIDs){
					
					LoginProvider loginProvider = systemInterface.getLoginHandler().getProvider(providerID);
					
					if(loginProvider != null){
						
						String description = ValidationUtils.validateParameter(providerID + "-description", req, true, 1, 65535, validationErrors);
						String buttonText = ValidationUtils.validateParameter(providerID + "-button", req, true, 1, 255, validationErrors);
						Integer sortIndex = ValidationUtils.validateParameter(providerID + "-sortIndex", req, true, IntegerPopulator.getPopulator(), validationErrors);
						
						providerConfigurations.add(new ProviderConfiguration(providerID, description, buttonText, sortIndex));
					}
				}
			}
			
			if(validationErrors.isEmpty()){
				
				log.info("User " + user + " updating supported login providers");
				
				if(!CollectionUtils.isEmpty(providerConfigurations)){
					
					saveProviderConfiguration(providerConfigurations, moduleDescriptor);
					
					Collections.sort(providerConfigurations);
					
					this.providerConfigurations = providerConfigurations;
				
				}else{
					
					moduleDescriptor.getMutableSettingHandler().removeSetting("supportedProviders");
					
					moduleDescriptor.saveSettings(systemInterface);	
					
					this.providerConfigurations = null;
				}
				
				redirectToDefaultMethod(req, res);
				
				return null;
			}
		}
		
		log.info("User " + user + " requested configuration form");

		Document doc = this.createDocument(req, uriParser, user);

		Element configurationElement = doc.createElement("Configure");
		doc.getFirstChild().appendChild(configurationElement);

		Element loginProvidersElement = doc.createElement("LoginProviders");
		
		for(LoginProvider loginProvider : systemInterface.getLoginHandler().getProviders()){
			
			if(loginProvider == this){
				
				continue;
			}
			
			loginProvidersElement.appendChild(loginProvider.getProviderDescriptor().toXML(doc));
		}

		if(loginProvidersElement.hasChildNodes()){
			
			configurationElement.appendChild(loginProvidersElement);
		}
		
		XMLUtils.append(doc, configurationElement, "ProviderConfigurations", providerConfigurations);
		
		if(validationErrors != null){
			
			XMLUtils.append(doc, configurationElement, "ValidationErrors", validationErrors);
			configurationElement.appendChild(RequestUtils.getRequestParameters(req, doc));
		}
		
		return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb());
	}

	private void saveProviderConfiguration(List<ProviderConfiguration> providerConfigurations, ModuleDescriptor moduleDescriptor) throws SQLException {

		MutableSettingHandler settingHandler = moduleDescriptor.getMutableSettingHandler();
		
		List<String> supportedProviderIDs = new ArrayList<String>(providerConfigurations.size());
		
		for(ProviderConfiguration providerConfiguration : providerConfigurations){
			
			settingHandler.setSetting(providerConfiguration.getProviderID() + "-description", providerConfiguration.getDescription());
			settingHandler.setSetting(providerConfiguration.getProviderID() + "-button", providerConfiguration.getButtonText());
			settingHandler.setSetting(providerConfiguration.getProviderID() + "-sortIndex", providerConfiguration.getSortIndex());
			
			supportedProviderIDs.add(providerConfiguration.getProviderID());
		}
		
		settingHandler.setSetting("supportedProviders", supportedProviderIDs);
		
		moduleDescriptor.saveSettings(systemInterface);	
	}

	@Override
	public void handleRequest(HttpServletRequest req, HttpServletResponse res, URIParser uriParser, String redirectURI) throws Throwable {

		if(redirectURI != null){

			res.sendRedirect(this.getModuleURI(req) + "?redirect=" + URLEncoder.encode(redirectURI, "ISO-8859-1"));

		}else{

			redirectToDefaultMethod(req, res);
		}
	}

	@Override
	public boolean loginUser(HttpServletRequest req, URIParser uriParser, User user) throws Exception {

		return false;
	}

	public Document createDocument(HttpServletRequest req, URIParser uriParser, User user) {

		Document doc = XMLUtils.createDomDocument();
		Element documentElement = doc.createElement("Document");
		doc.appendChild(documentElement);
		documentElement.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser));
		documentElement.appendChild(this.moduleDescriptor.toXML(doc));

		XMLUtils.appendNewElement(doc, documentElement, "cssPath", cssPath);

		if(AccessUtils.checkAccess(user, this)){
			documentElement.appendChild(doc.createElement("IsAdmin"));
		}

		return doc;
	}
	
	@Override
	public int getPriority() {
		
		return priority;
	}
	
	@Override
	public ProviderDescriptor getProviderDescriptor() {
		
		return providerDescriptor;
	}
	
	@Override
	public boolean allowsAdminAccess() {
		
		return false;
	}
	
	@Override
	public boolean allowsUserAccess() {
		
		return false;
	}
	
	@Override
	public boolean allowsAnonymousAccess() {
		
		return false;
	}
	
	@Override
	public Collection<Integer> getAllowedGroupIDs() {
		
		return adminGroupIDs;
	}
	
	@Override
	public Collection<Integer> getAllowedUserIDs() {
		
		return adminUserIDs;
	}

	@Override
	public boolean supportsRequest(HttpServletRequest req, URIParser uriParser) throws Throwable {

		return providerConfigurations != null;
	}
}
