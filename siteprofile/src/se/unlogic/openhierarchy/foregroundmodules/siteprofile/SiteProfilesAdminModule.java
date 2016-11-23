package se.unlogic.openhierarchy.foregroundmodules.siteprofile;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.handlers.SimpleSettingHandler;
import se.unlogic.hierarchy.core.interfaces.CachedXSLTDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.interfaces.SettingHandler;
import se.unlogic.hierarchy.core.settings.FormElement;
import se.unlogic.hierarchy.core.settings.InvalidFormatException;
import se.unlogic.hierarchy.core.settings.Setting;
import se.unlogic.hierarchy.core.utils.CRUDCallback;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.openhierarchy.foregroundmodules.siteprofile.beans.GlobalSettingValue;
import se.unlogic.openhierarchy.foregroundmodules.siteprofile.beans.Profile;
import se.unlogic.openhierarchy.foregroundmodules.siteprofile.beans.ProfileSettingValue;
import se.unlogic.openhierarchy.foregroundmodules.siteprofile.interfaces.SiteProfileHandler;
import se.unlogic.openhierarchy.foregroundmodules.siteprofile.interfaces.SiteProfileSettingProvider;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.dao.AnnotatedDAO;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.QueryParameterFactory;
import se.unlogic.standardutils.dao.SimpleAnnotatedDAOFactory;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.db.tableversionhandler.TableVersionHandler;
import se.unlogic.standardutils.db.tableversionhandler.UpgradeResult;
import se.unlogic.standardutils.db.tableversionhandler.XMLDBScriptProvider;
import se.unlogic.standardutils.factory.BeanFactory;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationErrorType;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.url.URLRewriter;


public class SiteProfilesAdminModule extends AnnotatedForegroundModule implements SiteProfileHandler, CRUDCallback<User>, BeanFactory<GlobalSettingValue>{

	private AnnotatedDAO<GlobalSettingValue> globalSettingValueDAO;
	private AnnotatedDAO<Profile> profileDAO;

	private SettingHandler globalSettingHandler;

	private LinkedHashMap<Integer, Profile> profileIDMap;
	private HashMap<String, Profile> profileDomainMap;

	private HashSet<SiteProfileSettingProvider> settingProviders = new HashSet<SiteProfileSettingProvider>();

	private QueryParameterFactory<GlobalSettingValue, String> globalSettingIDParamFactory;
	
	private ProfileCRUD profileCRUD;

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		cacheGlobalSettings();
		cacheProfiles();

		if(!systemInterface.getInstanceHandler().addInstance(SiteProfileHandler.class, this)){

			throw new RuntimeException("Unable to register module in global instance handler using key " + SiteProfileHandler.class.getSimpleName() + ", another instance is already registered using this key.");
		}
	}

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		UpgradeResult upgradeResult = TableVersionHandler.upgradeDBTables(dataSource, this.getClass().getName(), new XMLDBScriptProvider(this.getClass().getResourceAsStream("dbscripts/DB script.xml")));

		if(upgradeResult.isUpgrade()){

			log.info(upgradeResult.toString());
		}

		SimpleAnnotatedDAOFactory daoFactory = new SimpleAnnotatedDAOFactory(dataSource);

		profileDAO = daoFactory.getDAO(Profile.class);
		globalSettingValueDAO = daoFactory.getDAO(GlobalSettingValue.class);

		globalSettingIDParamFactory = globalSettingValueDAO.getParamFactory("settingID", String.class);
		
		this.profileCRUD = new ProfileCRUD(profileDAO.getAdvancedWrapper(Integer.class), this);
	}

	protected void cacheGlobalSettings() throws SQLException {

		List<GlobalSettingValue> globalSettingValues = globalSettingValueDAO.getAll();

		if(globalSettingValues == null){

			globalSettingHandler = new SimpleSettingHandler();
			return;
		}

		HashMap<String,List<String>> settingsMap =  getSettingsToMap(globalSettingValues);

		this.globalSettingHandler = new SimpleSettingHandler(settingsMap);
	}

	protected void cacheProfiles() throws SQLException {

		List<Profile> profiles = profileDAO.getAll();

		if(profiles == null){

			this.profileIDMap = new LinkedHashMap<Integer, Profile>();
			this.profileDomainMap = new HashMap<String, Profile>();

			return;
		}

		LinkedHashMap<Integer, Profile> profileIDMap = new LinkedHashMap<Integer, Profile>();
		HashMap<String, Profile> profileDomainMap = new HashMap<String, Profile>();

		for(Profile profile : profiles){

			List<ProfileSettingValue> settingValues = profile.getSettingValues();

			HashMap<String,List<String>> settingsMap;

			if(settingValues == null){

				settingsMap = new HashMap<String, List<String>>();

			}else{

				profile.setSettingValues(null);
				settingsMap = getSettingsToMap(settingValues);
			}

			profile.setSettingHandler(new FallbackSettingHandler(new SimpleSettingHandler(settingsMap), globalSettingHandler));

			profileIDMap.put(profile.getProfileID(), profile);

			if(profile.getDomains() != null){

				for(String domain : profile.getDomains()){

					profileDomainMap.put(domain, profile);
				}
			}
		}

		this.profileIDMap = profileIDMap;
		this.profileDomainMap = profileDomainMap;
	}

	@Override
	protected void moduleConfigured() {

		//TODO set breadcrumbs
	}

	@Override
	public void unload() throws Exception {

		if(this.equals(systemInterface.getInstanceHandler().getInstance(SiteProfileHandler.class))){

			systemInterface.getInstanceHandler().removeInstance(SiteProfileHandler.class);
		}

		this.settingProviders.clear();

		super.unload();
	}

	@Override
	public ForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {

		return profileCRUD.list(req, res, user, uriParser, null);
	}

	@WebPublic(toLowerCase=true)
	public ForegroundModuleResponse addProfile(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return profileCRUD.add(req, res, user, uriParser);
	}

	@WebPublic(toLowerCase=true)
	public ForegroundModuleResponse updateProfile(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return profileCRUD.update(req, res, user, uriParser);
	}

	@WebPublic(toLowerCase=true)
	public ForegroundModuleResponse deleteProfile(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return profileCRUD.delete(req, res, user, uriParser);
	}

	@WebPublic(alias="globalsettings")
	public ForegroundModuleResponse updateGlobalSettings(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		List<ValidationError> validationErrors = null;

		if(req.getMethod().equalsIgnoreCase("POST")){

			validationErrors = new ArrayList<ValidationError>();

			List<GlobalSettingValue> settingValues = getSettingValues(req, validationErrors, this, true);

			if(validationErrors.isEmpty()){

				log.info("User " + user + " updating global settings with " + CollectionUtils.getSize(settingValues) + " values.");

				TransactionHandler transactionHandler = null;

				try{
					transactionHandler = globalSettingValueDAO.createTransaction();

					this.globalSettingValueDAO.delete((HighLevelQuery<GlobalSettingValue>)null, transactionHandler);

					if(settingValues != null){

						this.globalSettingValueDAO.addAll(settingValues, transactionHandler, null);
					}

					transactionHandler.commit();

					cacheGlobalSettings();
					cacheProfiles();
					
					redirectToDefaultMethod(req, res);

				}finally{

					TransactionHandler.autoClose(transactionHandler);
				}
			}
		}

		log.info("User " + user + " requested update global settings form");

		Document doc = this.createDocument(req, uriParser, user);
		Element updateGlobalSettingsElement = doc.createElement("UpdateGlobalSettings");
		doc.getFirstChild().appendChild(updateGlobalSettingsElement);

		if(validationErrors != null){
			
			updateGlobalSettingsElement.appendChild(RequestUtils.getRequestParameters(req, doc));
			XMLUtils.append(doc, updateGlobalSettingsElement, "ValidationErrors", validationErrors);
		}
		
		Set<Setting> settings = getProfileSettings();
		
		XMLUtils.append(doc, updateGlobalSettingsElement, "SettingDescriptors", settings);
		appendSettingHandler(doc, updateGlobalSettingsElement, req, globalSettingHandler, settings);

		return new SimpleForegroundModuleResponse(doc);
	}

	@Override
	public boolean addSettingProvider(SiteProfileSettingProvider settingProvider) {

		return settingProviders.add(settingProvider);
	}

	@Override
	public boolean removeSettingProvider(SiteProfileSettingProvider settingProvider) {

		return settingProviders.remove(settingProvider);
	}

	@Override
	public SettingHandler getGlobalSettingHandler() {

		return globalSettingHandler;
	}

	@Override
	public Profile getCurrentProfile(User user, HttpServletRequest req, URIParser uriParser) {

		return profileDomainMap.get(req.getServerName());
	}

	@Override
	public SettingHandler getCurrentSettingHandler(User user, HttpServletRequest req, URIParser uriParser) {

		Profile profile = getCurrentProfile(user, req, uriParser);

		if(profile != null){

			return profile.getSettingHandler();
		}

		return globalSettingHandler;
	}

	protected static HashMap<String,List<String>> getSettingsToMap(List<? extends GlobalSettingValue> settings) {

		HashMap<String, List<String>> settingMap = new HashMap<String, List<String>>(settings.size());

		List<String> list = null;

		for(GlobalSettingValue setting : settings){

			list = settingMap.get(setting.getSettingID());

			if(list == null){

				list = new ArrayList<String>();
				settingMap.put(setting.getSettingID(), list);
			}

			list.add(setting.getValue());
		}

		return settingMap;
	}

	@Override
	public Document createDocument(HttpServletRequest req, URIParser uriParser, User user) {

		Document doc = XMLUtils.createDomDocument();
		Element document = doc.createElement("Document");
		document.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser));
		document.appendChild(this.moduleDescriptor.toXML(doc));
		doc.appendChild(document);
		return doc;
	}

	@Override
	public String getTitlePrefix() {

		return this.moduleDescriptor.getName();
	}

	@Override
	public Profile getProfile(Integer profileID) {

		return profileIDMap.get(profileID);
	}

	@Override
	public List<Profile> getProfiles() {

		return new ArrayList<Profile>(profileIDMap.values());
	}

	public Profile getProfile(String domain) {

		return profileDomainMap.get(domain);
	}

	public <T extends GlobalSettingValue> List<T> getSettingValues(HttpServletRequest req, List<ValidationError> validationErrors, BeanFactory<T> factory, boolean checkRequired) {

		Set<Setting> settings = getProfileSettings();

		if(settings == null){

			return null;
		}

		List<T> settingValues = new ArrayList<T>(settings.size() * 2);

		outer: for(Setting setting : settings){

			String[] rawValues = req.getParameterValues("setting-" + setting.getId());

			if(!StringUtils.isEmpty(rawValues) || setting.validateWithoutValues()){

				try{
					if(setting.getFormElement() == FormElement.HTML_EDITOR){
						
						rawValues[0] = URLRewriter.removeAbsoluteLinkUrls(rawValues[0], req);
					}
					
					List<String> parsedValues = setting.parseAndValidate(Arrays.asList(rawValues));

					if(parsedValues == null){

						log.warn("Setting " + setting + " returned no values after parsing and validating");

					}else{

						for(String value : parsedValues){

							if(value.length() > 4096){

								validationErrors.add(new ValidationError("setting-" + setting.getId(), setting.getName(), ValidationErrorType.TooLong));

								continue outer;
							}
						}

						if(validationErrors.isEmpty()){

							int index = 1;

							for(String value : parsedValues){

								T settingValue = factory.newInstance();

								settingValue.setSettingID(setting.getId());
								settingValue.setValue(value);
								settingValue.setIndex(index++);

								settingValues.add(settingValue);
							}
						}
					}

				}catch(InvalidFormatException e){

					validationErrors.add(new ValidationError("setting-" + setting.getId(), setting.getName(), ValidationErrorType.InvalidFormat));
				}

			}else if(checkRequired && setting.isRequired()){

				validationErrors.add(new ValidationError("setting-" + setting.getId(), setting.getName(), ValidationErrorType.RequiredField));
			}
		}

		if(settingValues.isEmpty()){

			return null;
		}

		return settingValues;
	}

	protected Set<Setting> getProfileSettings() {

		LinkedHashSet<Setting> combinedSettings = new LinkedHashSet<Setting>();

		for(SiteProfileSettingProvider settingProvider : settingProviders){

			List<Setting> settingList = settingProvider.getSiteProfileSettings();

			if(settingList != null){

				combinedSettings.addAll(settingList);
			}
		}

		if(combinedSettings.isEmpty()){

			return null;
		}

		return combinedSettings;
	}

	@Override
	public GlobalSettingValue newInstance() {

		return new GlobalSettingValue();
	}

	public void appendDesigns(Document doc, Element element) {

		Element designsElement = doc.createElement("Designs");
		element.appendChild(designsElement);

		Collection<CachedXSLTDescriptor> cachedXSLTDescriptors = systemInterface.getCoreXSLTCacheHandler().getCachedXSLTDescriptors(systemInterface.getDefaultLanguage());

		for(CachedXSLTDescriptor cachedXSLTDescriptor : cachedXSLTDescriptors) {

			Element designElement = doc.createElement("Design");

			XMLUtils.appendNewElement(doc, designElement, "name", cachedXSLTDescriptor.getName());

			designsElement.appendChild(designElement);

		}
	}
	
	public void appendSettingHandler(Document doc, Element targetElement, HttpServletRequest req, SettingHandler settingHandler, Set<Setting> settings){
		
		if(settings != null){
			
			Element settingsElement = doc.createElement("settings");
			targetElement.appendChild(settingsElement);
			
			for(Setting setting : settings){
				
				List<String> values = settingHandler.getStrings(setting.getId());
				
				if(values == null){
					
					continue;
				}
				
				Element settingElement = doc.createElement("setting");
				settingsElement.appendChild(settingElement);
				
				XMLUtils.appendNewElement(doc, settingElement, "id", setting.getId());
				
				if(setting.getFormElement() == FormElement.HTML_EDITOR){
					
					XMLUtils.appendNewElement(doc, settingElement, "value", URLRewriter.setAbsoluteLinkUrls(values.get(0), req));
					
				}else{
					
					for (String value : values) {
						settingElement.appendChild(XMLUtils.createCDATAElement("value", value, doc));
					}
				}
			}
		}
	}

	@Override
	public void ensureGlobalSettingValues(List<Setting> settings) throws SQLException {

		if(settings != null) {
			
			log.info("Ensuring global setting values for " + CollectionUtils.getSize(settings) + " settings");
			
			List<GlobalSettingValue> settingValues = new ArrayList<GlobalSettingValue>();
			
			for(Setting setting : settings) {
				
				if(setting.isRequired() && !CollectionUtils.isEmpty(setting.getDefaultValues())) {
					
					HighLevelQuery<GlobalSettingValue> query = new HighLevelQuery<GlobalSettingValue>();
					
					query.addParameter(globalSettingIDParamFactory.getParameter(setting.getId()));
					
					if(globalSettingValueDAO.getAll(query) == null) {
						
						int index = 1;
						
						for(String value : setting.getDefaultValues()){
						
							GlobalSettingValue settingValue = new GlobalSettingValue();
	
							settingValue.setSettingID(setting.getId());
							settingValue.setValue(value);
							settingValue.setIndex(index++);
	
							settingValues.add(settingValue);
						
						}
						
					}
					
				}
				
			}
			
			if(!settingValues.isEmpty()) {
				
				globalSettingValueDAO.addAll(settingValues, null);
				
				cacheGlobalSettings();
				cacheProfiles();
				
			}
			
		}
		
	}
}
