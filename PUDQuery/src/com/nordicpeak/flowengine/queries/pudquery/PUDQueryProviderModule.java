package com.nordicpeak.flowengine.queries.pudquery;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import se.riges.lm.rmi.ValidateLM;
import se.riges.lm.rmi.exceptions.LMAccountException;
import se.riges.lm.rmi.exceptions.LMUnavailableException;
import se.riges.lm.rmi.interfaces.IEstate;
import se.unlogic.hierarchy.core.annotations.CheckboxSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.InstanceManagerDependency;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.annotations.XSLVariable;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.interfaces.SettingHandler;
import se.unlogic.hierarchy.core.settings.Setting;
import se.unlogic.hierarchy.core.settings.TextFieldSetting;
import se.unlogic.hierarchy.core.utils.FCKUtils;
import se.unlogic.openhierarchy.foregroundmodules.siteprofile.interfaces.SiteProfileHandler;
import se.unlogic.openhierarchy.foregroundmodules.siteprofile.interfaces.SiteProfileSettingProvider;
import se.unlogic.standardutils.dao.AnnotatedDAO;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.QueryParameterFactory;
import se.unlogic.standardutils.dao.SimpleAnnotatedDAOFactory;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.db.tableversionhandler.TableVersionHandler;
import se.unlogic.standardutils.db.tableversionhandler.UpgradeResult;
import se.unlogic.standardutils.db.tableversionhandler.XMLDBScriptProvider;
import se.unlogic.standardutils.json.JsonObject;
import se.unlogic.standardutils.json.JsonUtils;
import se.unlogic.standardutils.populators.BeanStringPopulator;
import se.unlogic.standardutils.populators.IntegerPopulator;
import se.unlogic.standardutils.populators.QueryParameterPopulator;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.TCPPortStringFormatValidator;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLGenerator;
import se.unlogic.webutils.http.HTTPUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.populators.annotated.AnnotatedRequestPopulator;
import se.unlogic.webutils.url.URLRewriter;

import com.nordicpeak.flowengine.enums.QueryState;
import com.nordicpeak.flowengine.interfaces.ImmutableQueryDescriptor;
import com.nordicpeak.flowengine.interfaces.ImmutableQueryInstanceDescriptor;
import com.nordicpeak.flowengine.interfaces.InstanceMetadata;
import com.nordicpeak.flowengine.interfaces.MutableQueryDescriptor;
import com.nordicpeak.flowengine.interfaces.MutableQueryInstanceDescriptor;
import com.nordicpeak.flowengine.interfaces.Query;
import com.nordicpeak.flowengine.interfaces.QueryInstance;
import com.nordicpeak.flowengine.queries.basequery.BaseQueryCRUDCallback;
import com.nordicpeak.flowengine.queries.basequery.BaseQueryProviderModule;
import com.nordicpeak.flowengine.utils.TextTagReplacer;

public class PUDQueryProviderModule extends BaseQueryProviderModule<PUDQueryInstance> implements BaseQueryCRUDCallback, SiteProfileSettingProvider {

	@XSLVariable(prefix="java.")
	protected String lmUserSettingName = "LM user";
	
	@XSLVariable(prefix="java.")
	protected String lmUserSettingDescription = "User to use for LM Search";
	
	@XSLVariable(prefix="java.")
	protected String searchPrefixSettingName = "LM search prefix";
	
	@XSLVariable(prefix="java.")
	protected String searchPrefixSettingDescription = "Search prefix used when searching for pud or address using LM Search";
	
	@ModuleSetting
	@CheckboxSettingDescriptor(name = "Enable PUD validation", description = "Controls whether the submitted property unit designation should be validated using LM Search service RMI server or not")
	protected boolean enablePUDValidation = false;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "LM Search service RMI server address", description = "The address to the LM RMI server", required = true)
	protected String lmRMIServerAddress = "localhost";

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "LM Search service RMI server port", description = "The port of the LM RMI server", required = true, formatValidator = TCPPortStringFormatValidator.class)
	protected int lmRMIServerPort = 1099;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "LM Search service RMI server name", description = "The name of the LM RMI server", required = true)
	protected String lmRMIServerName = "searchlm";

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "LM Search service URL", description = "The URL to the LM search service", required = true)
	protected String searchServiceURL;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Param for PUD search", description = "The parameter passed to service when searching for PUDs", required = true)
	protected String searchPUDParam = "registerenheter";

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Param for address search", description = "The parameter passed to service when searching for addresses", required = true)
	protected String searchAddressParam = "addresses";

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Search result limit", description = "Specifies maximum number of hits returned", required = true, formatValidator = IntegerPopulator.class)
	protected Integer searchResultLimit = 25;

	@ModuleSetting
	@TextFieldSettingDescriptor(id = "BaseMapQuery-lmUser", name = "Default LM Search user", description = "Specifies default user to use for LM Search", required = true)
	protected String defaultLMUser;

	@ModuleSetting
	@TextFieldSettingDescriptor(id = "BaseMapQuery-searchPrefix", name = "Default search prefix", description = "Specifies default search prefix when searching for pud or address using LM Search", required = false)
	protected String defaultSearchPrefix = "";

	protected ValidateLM searchLM;

	protected SiteProfileHandler siteProfileHandler;
	
	protected TextFieldSetting lmUserSetting;

	protected TextFieldSetting searchPrefixSetting;

	private AnnotatedDAO<PUDQuery> queryDAO;
	private AnnotatedDAO<PUDQueryInstance> queryInstanceDAO;

	private PUDQueryCRUD queryCRUD;

	private QueryParameterFactory<PUDQuery, Integer> queryIDParamFactory;
	private QueryParameterFactory<PUDQueryInstance, Integer> queryInstanceIDParamFactory;

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		if (enablePUDValidation) {

			searchLM = getSearchLMClient();

		} else {

			searchLM = null;

		}

	}

	@Override
	public void update(ForegroundModuleDescriptor moduleDescriptor, DataSource dataSource) throws Exception {

		super.update(moduleDescriptor, dataSource);

		searchLM = null;

		if (enablePUDValidation) {

			searchLM = getSearchLMClient();
		}

	}

	@Override
	protected void moduleConfigured() throws Exception {

		super.moduleConfigured();

		lmUserSetting = new TextFieldSetting("BaseMapQuery-lmUser", lmUserSettingName, lmUserSettingDescription, defaultLMUser, true);
		searchPrefixSetting = new TextFieldSetting("BaseMapQuery-searchPrefix", searchPrefixSettingName, searchPrefixSettingDescription, defaultSearchPrefix, false);

	}

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		// Automatic table version handling
		UpgradeResult upgradeResult = TableVersionHandler.upgradeDBTables(dataSource, PUDQueryProviderModule.class.getName(), new XMLDBScriptProvider(this.getClass().getResourceAsStream("DB script.xml")));

		if (upgradeResult.isUpgrade()) {

			log.info(upgradeResult.toString());
		}

		SimpleAnnotatedDAOFactory daoFactory = new SimpleAnnotatedDAOFactory(dataSource);

		List<QueryParameterPopulator<?>> queryParameterPopulators = new ArrayList<QueryParameterPopulator<?>>();

		queryParameterPopulators.add(PUDQueryCRUD.SEARCH_SERVICE_POPULATOR);

		List<BeanStringPopulator<?>> typePopulators = new ArrayList<BeanStringPopulator<?>>();
		typePopulators.add(PUDQueryCRUD.SEARCH_SERVICE_POPULATOR);

		queryDAO = daoFactory.getDAO(PUDQuery.class, queryParameterPopulators, typePopulators);
		queryInstanceDAO = daoFactory.getDAO(PUDQueryInstance.class);

		queryCRUD = new PUDQueryCRUD(queryDAO.getWrapper(Integer.class), new AnnotatedRequestPopulator<PUDQuery>(PUDQuery.class), "PUDQuery", "query", null, this);

		queryIDParamFactory = queryDAO.getParamFactory("queryID", Integer.class);
		queryInstanceIDParamFactory = queryInstanceDAO.getParamFactory("queryInstanceID", Integer.class);

	}

	@WebPublic(alias = "config")
	public ForegroundModuleResponse configureQuery(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return queryCRUD.update(req, res, user, uriParser);
	}

	@WebPublic(alias = "searchpud")
	public ForegroundModuleResponse searchPUD(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return sendSearchRequest(req, res, user, searchServiceURL + "/" + searchPUDParam);
	}

	@WebPublic(alias = "searchaddress")
	public ForegroundModuleResponse searchAddress(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return sendSearchRequest(req, res, user, searchServiceURL + "/" + searchAddressParam);

	}

	private ForegroundModuleResponse sendSearchRequest(HttpServletRequest req, HttpServletResponse res, User user, String search) throws IOException {

		String query = null;

		if (!StringUtils.isEmpty(query = req.getParameter("q"))) {

			query = URLEncoder.encode(query, "ISO-8859-1");
			
			String prefix = getCurrentSiteProfileSettingHandler(req, user).getString("BaseMapQuery-searchPrefix");
			
			if(!StringUtils.isEmpty(prefix)) {
				
				if(!query.toLowerCase().startsWith(prefix.toLowerCase())) {

					query = URLEncoder.encode(prefix + " ", "UTF-8") + query;
					
				}
				
			}
			
			sendSearchReqest(req, res, user, search, query, "q");

		} else if (!StringUtils.isEmpty(query = req.getParameter("fnrsimple"))) {

			sendSearchReqest(req, res, user, search, query, "fnrsimple");

		}

		return null;

	}

	private void sendSearchReqest(HttpServletRequest req, HttpServletResponse res, User user, String search, String query, String searchParam) throws IOException {

		String searchQuery = search + "?" + searchParam + "=" + query + "&limit=" + searchResultLimit + "&lmuser=" + getCurrentSiteProfileSettingHandler(req, user).getString("BaseMapQuery-lmUser");

		try {

			log.info("User " + user + " searching for property unit designation using query " + searchQuery);

			String response = HTTPUtils.sendHTTPGetRequest(searchQuery, null, null, null);

			HTTPUtils.sendReponse(getUnescapedText(response), JsonUtils.getContentType(), res);

		} catch (IOException e) {

			log.warn("Unable to get any search result from lm search service using query " + searchQuery + ". Caused by: " + e.getMessage());

			JsonObject error = new JsonObject();
			error.putField("Error", "true");

			HTTPUtils.sendReponse(error.toJson(), JsonUtils.getContentType(), res);
		}

	}

	@Override
	public Query createQuery(MutableQueryDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable {

		checkConfiguration();

		PUDQuery query = new PUDQuery();

		query.setQueryID(descriptor.getQueryID());

		this.queryDAO.add(query, transactionHandler, null);

		query.init(descriptor, getFullAlias() + "/config/" + descriptor.getQueryID());

		return query;

	}

	@Override
	public Query importQuery(MutableQueryDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable {

		PUDQuery query = new PUDQuery();
		
		query.setQueryID(descriptor.getQueryID());
		
		query.populate(descriptor.getImportParser().getNode(XMLGenerator.getElementName(query.getClass())));
		
		this.queryDAO.add(query, transactionHandler, null);
		
		return query;
	}
	
	@Override
	public Query getQuery(MutableQueryDescriptor descriptor) throws Throwable {

		checkConfiguration();

		PUDQuery query = this.getQuery(descriptor.getQueryID());

		if (query == null) {

			return null;
		}

		query.init(descriptor, getFullAlias() + "/config/" + descriptor.getQueryID());

		return query;

	}

	@Override
	public Query getQuery(MutableQueryDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable {

		checkConfiguration();

		PUDQuery query = this.getQuery(descriptor.getQueryID(), transactionHandler);

		if (query == null) {

			return null;
		}

		query.init(descriptor, getFullAlias() + "/config/" + descriptor.getQueryID());

		return query;

	}

	@Override
	public QueryInstance getQueryInstance(MutableQueryInstanceDescriptor descriptor, String instanceManagerID, HttpServletRequest req, User user, InstanceMetadata instanceMetadata) throws Throwable {

		checkConfiguration();

		PUDQueryInstance queryInstance = null;
		
		// Check if we should create a new instance or get an existing one
		if (descriptor.getQueryInstanceID() == null) {

			queryInstance = new PUDQueryInstance();

			queryInstance.setQuery(getQuery(descriptor.getQueryDescriptor().getQueryID()));

			if (queryInstance.getQuery() == null) {

				return null;
			}

			queryInstance.set(descriptor);
			queryInstance.copyQueryValues();

		} else {

			queryInstance = getQueryInstance(descriptor.getQueryInstanceID());

			if (queryInstance == null) {

				return null;
			}

			queryInstance.set(descriptor);

		}

		FCKUtils.setAbsoluteFileUrls(queryInstance.getQuery(), RequestUtils.getFullContextPathURL(req) + ckConnectorModuleAlias);
		
		URLRewriter.setAbsoluteLinkUrls(queryInstance.getQuery(), req);
		
		TextTagReplacer.replaceTextTags(queryInstance.getQuery(), instanceMetadata.getSiteProfile());
		
		return queryInstance;
		
	}

	@Override
	public boolean deleteQuery(ImmutableQueryDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable {

		PUDQuery query = getQuery(descriptor.getQueryID());

		if (query == null) {

			return false;
		}

		this.queryDAO.delete(query, transactionHandler);

		return true;

	}

	@Override
	public boolean deleteQueryInstance(ImmutableQueryInstanceDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable {

		PUDQueryInstance queryInstance = getQueryInstance(descriptor.getQueryInstanceID());

		if (queryInstance == null) {

			return false;
		}

		this.queryInstanceDAO.delete(queryInstance, transactionHandler);

		return true;

	}

	@Override
	public void copyQuery(MutableQueryDescriptor sourceQueryDescriptor, MutableQueryDescriptor copyQueryDescriptor, TransactionHandler transactionHandler) throws SQLException {

		PUDQuery query = getQuery(sourceQueryDescriptor.getQueryID(), transactionHandler);

		query.setQueryID(copyQueryDescriptor.getQueryID());

		queryDAO.add(query, transactionHandler, null);

	}

	@Override
	public void save(PUDQueryInstance queryInstance, TransactionHandler transactionHandler) throws Throwable {

		if (queryInstance.getQueryInstanceID() == null || !queryInstance.getQueryInstanceID().equals(queryInstance.getQueryInstanceDescriptor().getQueryInstanceID())) {

			queryInstance.setQueryInstanceID(queryInstance.getQueryInstanceDescriptor().getQueryInstanceID());

			this.queryInstanceDAO.add(queryInstance, transactionHandler, null);

		} else {

			this.queryInstanceDAO.update(queryInstance, transactionHandler, null);
		}

	}

	@Override
	public void populate(PUDQueryInstance queryInstance, HttpServletRequest req, User user, boolean allowPartialPopulation) throws ValidationException {

		Integer queryID = queryInstance.getQuery().getQueryID();

		String pud = req.getParameter("q" + queryID + "_propertyUnitDesignation");

		if (StringUtils.isEmpty(pud)) {

			if (!allowPartialPopulation && queryInstance.getQueryInstanceDescriptor().getQueryState() == QueryState.VISIBLE_REQUIRED) {

				queryInstance.reset();

				throw new ValidationException(new ValidationError("RequiredQuery"));
			}

			queryInstance.reset();

			return;
		}

		if (enablePUDValidation) {

			IEstate estate = getPropertyUnitDesignation(pud, user);

			if (estate == null) {
				throw new ValidationException(new ValidationError("PUDNotValid"));
			}

			queryInstance.setPropertyUnitNumber(estate.getEstateID());

		}

		queryInstance.setPropertyUnitDesignation(pud);
		queryInstance.getQueryInstanceDescriptor().setPopulated(true);

	}

	private String getUnescapedText(String text) {

		if (text != null) {

			Charset utf8charset = Charset.forName("UTF-8");

			Charset iso88591charset = Charset.forName("ISO-8859-1");

			ByteBuffer inputBuffer = ByteBuffer.wrap(text.getBytes());

			CharBuffer data = utf8charset.decode(inputBuffer);

			ByteBuffer outputBuffer = iso88591charset.encode(data);

			text = new String(outputBuffer.array());

		}

		return text;
	}

	@Override
	public String getTitlePrefix() {

		return moduleDescriptor.getName();
	}

	private PUDQueryInstance getQueryInstance(Integer queryInstanceID) throws SQLException {

		HighLevelQuery<PUDQueryInstance> query = new HighLevelQuery<PUDQueryInstance>(PUDQueryInstance.QUERY_RELATION);

		query.addParameter(queryInstanceIDParamFactory.getParameter(queryInstanceID));

		return queryInstanceDAO.get(query);

	}

	private PUDQuery getQuery(Integer queryID) throws SQLException {

		HighLevelQuery<PUDQuery> query = new HighLevelQuery<PUDQuery>();

		query.addParameter(queryIDParamFactory.getParameter(queryID));

		return queryDAO.get(query);
	}

	private PUDQuery getQuery(Integer queryID, TransactionHandler transactionHandler) throws SQLException {

		HighLevelQuery<PUDQuery> query = new HighLevelQuery<PUDQuery>();

		query.addParameter(queryIDParamFactory.getParameter(queryID));

		return queryDAO.get(query, transactionHandler);
	}

	private void checkConfiguration() {

		if (this.searchServiceURL == null) {

			throw new RuntimeException("No search service URL set for module/query provider " + moduleDescriptor);
		}

	}

	protected IEstate getPropertyUnitDesignation(String propertyUnitDesignation, User user) throws ValidationException {

		try {

			ValidateLM searchLM = getSearchLMClient();

			if (searchLM != null) {

				// TODO get municipality parameter from user
				return searchLM.validateEstate(propertyUnitDesignation, "Sundsvall");

			}

		} catch (RemoteException e) {

			log.error("Unable to connect to LM RMI server " + lmRMIServerAddress + ":" + lmRMIServerPort + " when trying to get pud requested by user " + user);

		} catch (LMAccountException e) {

			log.error("The account for the given municipality is not valid when trying to get pud requested by user " + user);

		} catch (LMUnavailableException e) {

			log.error("Server was unable to contact LM web service API when trying to get pud requested by user " + user);

		}

		throw new ValidationException(new ValidationError("UnableToValidatePUD"));
	}

	private ValidateLM getSearchLMClient() {

		try {

			if (searchLM != null) {

				return searchLM;
			}

			Registry registry = LocateRegistry.getRegistry(lmRMIServerAddress, lmRMIServerPort);

			return (ValidateLM) registry.lookup(lmRMIServerName);

		} catch (RemoteException e) {

			log.error("Unable to connect to LM RMI server " + lmRMIServerAddress + ":" + lmRMIServerPort + " (" + lmRMIServerName + ")");

		} catch (NotBoundException e) {

			log.error("LM RMI server " + lmRMIServerAddress + ":" + lmRMIServerPort + " (" + lmRMIServerName + ") not bounded");

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
	public List<Setting> getSiteProfileSettings() {

		return Arrays.asList((Setting) lmUserSetting, (Setting) searchPrefixSetting);
		
	}
	
	protected SettingHandler getCurrentSiteProfileSettingHandler(HttpServletRequest req, User user) {
		
		return siteProfileHandler != null ? siteProfileHandler.getCurrentSettingHandler(user, req, null) : this.moduleDescriptor.getMutableSettingHandler();
	}

	@Override
	public void unload() throws Exception {
		
		if(siteProfileHandler != null){
			
			siteProfileHandler.removeSettingProvider(this);
		}
		
		super.unload();
		
	}
	
}
