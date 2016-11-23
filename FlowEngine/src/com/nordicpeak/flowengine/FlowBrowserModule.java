package com.nordicpeak.flowengine;

import it.sauronsoftware.cron4j.Scheduler;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.annotations.CheckboxSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.InstanceManagerDependency;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextAreaSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.beans.Breadcrumb;
import se.unlogic.hierarchy.core.beans.SettingDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.beans.ValueDescriptor;
import se.unlogic.hierarchy.core.enums.CRUDAction;
import se.unlogic.hierarchy.core.enums.EventSource;
import se.unlogic.hierarchy.core.enums.EventTarget;
import se.unlogic.hierarchy.core.events.CRUDEvent;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.exceptions.ModuleConfigurationException;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.interfaces.EventListener;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.utils.ModuleUtils;
import se.unlogic.openhierarchy.foregroundmodules.siteprofile.interfaces.SiteProfile;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.collections.MethodComparator;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.QueryParameterFactory;
import se.unlogic.standardutils.dao.querys.ArrayListQuery;
import se.unlogic.standardutils.dao.querys.ObjectQuery;
import se.unlogic.standardutils.enums.Order;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.populators.IntegerPopulator;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.threads.ReflectedRunnable;
import se.unlogic.standardutils.time.MillisecondTimeUnits;
import se.unlogic.standardutils.time.TimeUtils;
import se.unlogic.standardutils.validation.PositiveStringIntegerValidator;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;

import com.nordicpeak.flowengine.accesscontrollers.SessionAccessController;
import com.nordicpeak.flowengine.accesscontrollers.UserFlowInstanceAccessController;
import com.nordicpeak.flowengine.beans.Flow;
import com.nordicpeak.flowengine.beans.FlowFamily;
import com.nordicpeak.flowengine.beans.FlowInstance;
import com.nordicpeak.flowengine.beans.FlowInstanceEvent;
import com.nordicpeak.flowengine.beans.FlowType;
import com.nordicpeak.flowengine.beans.Status;
import com.nordicpeak.flowengine.comparators.FlowNameComparator;
import com.nordicpeak.flowengine.enums.EventType;
import com.nordicpeak.flowengine.enums.ShowMode;
import com.nordicpeak.flowengine.exceptions.FlowEngineException;
import com.nordicpeak.flowengine.exceptions.evaluation.EvaluationException;
import com.nordicpeak.flowengine.exceptions.evaluationprovider.EvaluationProviderException;
import com.nordicpeak.flowengine.exceptions.flow.FlowDefaultStatusNotFound;
import com.nordicpeak.flowengine.exceptions.flow.FlowDisabledException;
import com.nordicpeak.flowengine.exceptions.flow.FlowLimitExceededException;
import com.nordicpeak.flowengine.exceptions.flow.FlowNoLongerAvailableException;
import com.nordicpeak.flowengine.exceptions.flow.FlowNotPublishedException;
import com.nordicpeak.flowengine.exceptions.flowinstance.FlowInstanceNoLongerAvailableException;
import com.nordicpeak.flowengine.exceptions.flowinstance.InvalidFlowInstanceStepException;
import com.nordicpeak.flowengine.exceptions.flowinstance.MissingQueryInstanceDescriptor;
import com.nordicpeak.flowengine.exceptions.flowinstancemanager.DuplicateFlowInstanceManagerIDException;
import com.nordicpeak.flowengine.exceptions.flowinstancemanager.FlowInstanceManagerClosedException;
import com.nordicpeak.flowengine.exceptions.queryinstance.QueryInstanceHTMLException;
import com.nordicpeak.flowengine.exceptions.queryinstance.QueryRequestException;
import com.nordicpeak.flowengine.exceptions.queryinstance.UnableToGetQueryInstanceShowHTMLException;
import com.nordicpeak.flowengine.exceptions.queryprovider.QueryProviderException;
import com.nordicpeak.flowengine.interfaces.FlowInstanceAccessController;
import com.nordicpeak.flowengine.interfaces.FlowProcessCallback;
import com.nordicpeak.flowengine.interfaces.ImmutableFlowInstance;
import com.nordicpeak.flowengine.interfaces.ImmutableFlowInstanceEvent;
import com.nordicpeak.flowengine.interfaces.MultiSigningProvider;
import com.nordicpeak.flowengine.interfaces.OperatingStatus;
import com.nordicpeak.flowengine.interfaces.PDFProvider;
import com.nordicpeak.flowengine.interfaces.PaymentProvider;
import com.nordicpeak.flowengine.interfaces.SigningProvider;
import com.nordicpeak.flowengine.managers.FlowInstanceManager;
import com.nordicpeak.flowengine.managers.MutableFlowInstanceManager;
import com.nordicpeak.flowengine.search.FlowIndexer;
import com.nordicpeak.flowengine.utils.TextTagReplacer;

public class FlowBrowserModule extends BaseFlowBrowserModule implements FlowProcessCallback, FlowInstanceAccessController, EventListener<CRUDEvent<?>>, Runnable {

	public static final UserFlowInstanceAccessController PREVIEW_ACCESS_CONTROLLER = new UserFlowInstanceAccessController(false, false);

	private static final Comparator<FlowFamily> FAMILY_COMPARATOR = new MethodComparator<FlowFamily>(FlowFamily.class, "getFlowInstanceCount", Order.DESC);

	public static final String SAVE_ACTION_ID = FlowBrowserModule.class.getName() + ".save";
	public static final String SUBMIT_ACTION_ID = FlowBrowserModule.class.getName() + ".submit";
	public static final String PAYMENT_ACTION_ID = FlowBrowserModule.class.getName() + ".pay";
	public static final String MULTI_SIGNING_ACTION_ID = FlowBrowserModule.class.getName() + ".multisigning";	
	
	public static final String SESSION_ACCESS_CONTROLLER_TAG = FlowBrowserModule.class.getName();

	private static final FlowNameComparator FLOW_NAME_COMPARATOR = new FlowNameComparator();

	@ModuleSetting
	@CheckboxSettingDescriptor(name = "Show all flowtypes", description = "List all flowtypes in this module")
	protected boolean listAllFlowTypes = false;

	@ModuleSetting
	protected List<Integer> flowTypeIDs;

	@ModuleSetting
	@TextAreaSettingDescriptor(name = "Recommended tags (one search tag per line)", description = "Recommended tag listed between flow search form (one search tag per line)", required = false)
	protected String recommendedTags;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Max search hits", description = "Maximum number of hits to get from index when searching", formatValidator = PositiveStringIntegerValidator.class, required = true)
	protected int maxHitCount = 10;

	@ModuleSetting(allowsNull = true)
	@TextFieldSettingDescriptor(name = "User favourite module alias", description = "Full alias of the user favourite module", required = false)
	protected String userFavouriteModuleAlias;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Intervall size", description = "Controls how any hours back in time that the popular statistics should be based on")
	private int popularInterval = 72;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Flow count", description = "Controls how many popular flows this module should display for each flow type")
	private int popularFlowCount = 5;

	@ModuleSetting
	@CheckboxSettingDescriptor(name = "Open external flows in new window", description = "Controls whether to open external flows in new window or not")
	protected boolean openExternalFlowsInNewWindow = true;

	@ModuleSetting
	@CheckboxSettingDescriptor(name = "Show related flows", description = "Controls whether to show related flows in flowoverview")
	protected boolean showRelatedFlows = true;

	@ModuleSetting
	@CheckboxSettingDescriptor(name = "User category filter", description = "Controls whether to use category filter when listing flows")
	protected boolean useCategoryFilter = false;
	
	@InstanceManagerDependency
	protected PDFProvider pdfProvider;

	@InstanceManagerDependency
	protected SigningProvider signingProvider;
	
	@InstanceManagerDependency
	protected MultiSigningProvider multiSigningProvider;

	@InstanceManagerDependency
	protected PaymentProvider paymentProvider;
	
	private List<String> searchTags;

	private QueryParameterFactory<FlowType, Integer> flowTypeIDParamFactory;

	private List<FlowType> flowTypes;
	private HashMap<Integer, Flow> flowMap;
	protected LinkedHashMap<Integer, Flow> latestPublishedFlowVersionsMap;

	protected final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	protected final Lock r = rwl.readLock();
	protected final Lock w = rwl.writeLock();

	private Scheduler scheduler;

	private FlowIndexer flowIndexer;

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		cacheFlows();

		this.eventHandler.addEventListener(FlowType.class, CRUDEvent.class, this);
		this.eventHandler.addEventListener(Flow.class, CRUDEvent.class, this);
		
		scheduler = new Scheduler();
		scheduler.schedule("0 0 * * *", this);
		scheduler.schedule("0 * * * *", new ReflectedRunnable(this, "calculatePopularFlows"));
		scheduler.start();

		searchTags = StringUtils.splitOnLineBreak(recommendedTags, true);

		if (!systemInterface.getInstanceHandler().addInstance(FlowBrowserModule.class, this)) {

			throw new RuntimeException("Unable to register module in global instance handler using key " + FlowBrowserModule.class.getSimpleName() + ", another instance is already registered using this key.");
		}
	}

	@Override
	public void update(ForegroundModuleDescriptor descriptor, DataSource dataSource) throws Exception {

		super.update(descriptor, dataSource);

		searchTags = StringUtils.splitOnLineBreak(recommendedTags, true);

		cacheFlows();
	}

	@Override
	public void unload() throws Exception {

		super.unload();

		try {
			scheduler.stop();
		} catch (IllegalStateException e) {
			log.error("Error stopping scheduler", e);
		}

		this.eventHandler.removeEventListener(FlowType.class, CRUDEvent.class, this);
		this.eventHandler.removeEventListener(Flow.class, CRUDEvent.class, this);

		if (this.equals(systemInterface.getInstanceHandler().getInstance(FlowBrowserModule.class))) {

			systemInterface.getInstanceHandler().removeInstance(FlowBrowserModule.class);
		}
	}

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		super.createDAOs(dataSource);

		flowTypeIDParamFactory = daoFactory.getFlowTypeDAO().getParamFactory("flowTypeID", Integer.class);
	}

	@Override
	public ForegroundModuleResponse processRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {

		if (!listAllFlowTypes && flowTypeIDs == null) {

			throw new ModuleConfigurationException("No flowTypeIDs set in module settings for module ");
		}

		if (flowTypes == null) {

			throw new ModuleConfigurationException("The configured flow types was not found in the database");
		}

		return super.processRequest(req, res, user, uriParser);
	}

	@Override
	public ForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return list(req, res, user, uriParser, (List<ValidationError>)null);
	}

	public ForegroundModuleResponse list(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, ValidationError validationError) throws ModuleConfigurationException, SQLException {

		return list(req, res, user, uriParser, CollectionUtils.getGenericSingletonList(validationError));
	}

	@Override
	public ForegroundModuleResponse list(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, List<ValidationError> validationErrors) throws ModuleConfigurationException, SQLException {

		r.lock();

		try {
			log.info("User " + user + " listing flows");

			Document doc = this.createDocument(req, uriParser, user);

			Element showFlowTypesElement = doc.createElement("ShowFlowTypes");

			doc.getDocumentElement().appendChild(showFlowTypesElement);

			XMLUtils.append(doc, showFlowTypesElement, flowTypes);

			SiteProfile siteProfile = getCurrentSiteProfile(req, user, uriParser);

			if (latestPublishedFlowVersionsMap != null && siteProfile != null) {

				for (Flow flow : latestPublishedFlowVersionsMap.values()) {

					showFlowTypesElement.appendChild(flow.toXML(doc, siteProfile));

				}

			} else {

				XMLUtils.append(doc, showFlowTypesElement, getLatestPublishedFlowVersions());

			}

			if (user != null) {
				XMLUtils.appendNewElement(doc, showFlowTypesElement, "loggedIn");
				XMLUtils.append(doc, showFlowTypesElement, daoFactory.getUserFavouriteDAO().getAll(user, req.getSession(), latestPublishedFlowVersionsMap));
			}

			XMLUtils.append(doc, showFlowTypesElement, "recommendedTags", "Tag", searchTags);
			XMLUtils.appendNewElement(doc, showFlowTypesElement, "userFavouriteModuleAlias", userFavouriteModuleAlias);

			if(useCategoryFilter) {
				XMLUtils.appendNewElement(doc, showFlowTypesElement, "useCategoryFilter", true);
			}
			
			if (validationErrors != null) {

				XMLUtils.append(doc, showFlowTypesElement, validationErrors);
			}

			return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb());

		} finally {

			r.unlock();
		}
	}

	@WebPublic(alias = "flowoverview")
	public ForegroundModuleResponse showFlowOverview(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws ModuleConfigurationException, SQLException, AccessDeniedException, IOException, FlowDefaultStatusNotFound, EvaluationException {

		Flow flow;

		if (uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (flow = flowMap.get(Integer.valueOf(uriParser.get(2)))) != null) {

			return showFlowOverview(flow, req, res, user, uriParser);
		}

		return list(req, res, user, uriParser, FLOW_NOT_FOUND_VALIDATION_ERROR);

	}

	@WebPublic(alias = "overview")
	public ForegroundModuleResponse showLatestPublishedFlowOverview(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws ModuleConfigurationException, SQLException {

		Flow flow;

		if (uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (flow = getLatestPublishedFlowVersion(Integer.valueOf(uriParser.get(2)))) != null) {

			return showFlowOverview(flow, req, res, user, uriParser);
		}

		return list(req, res, user, uriParser, FLOW_NOT_FOUND_VALIDATION_ERROR);
	}

	private ForegroundModuleResponse showFlowOverview(Flow flow, HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws ModuleConfigurationException, SQLException {

		if (!flow.isPublished() || !flow.isEnabled()) {

			log.info("User " + user + " requested flow " + flow + " which is no longer available.");

			return list(req, res, user, uriParser, FLOW_NO_LONGER_AVAILABLE_VALIDATION_ERROR);

		}

		Document doc = this.createDocument(req, uriParser, user);

		Element showFlowOverviewElement = doc.createElement("ShowFlowOverview");

		doc.getDocumentElement().appendChild(showFlowOverviewElement);

		showFlowOverviewElement.appendChild(flow.toXML(doc, this.getCurrentSiteProfile(req, user, uriParser)));

		XMLUtils.append(doc, showFlowOverviewElement, "FlowTypeFlows", getLatestPublishedFlowVersions());

		if (user != null) {
			XMLUtils.appendNewElement(doc, showFlowOverviewElement, "loggedIn");
			XMLUtils.append(doc, showFlowOverviewElement, daoFactory.getUserFavouriteDAO().getAll(user, req.getSession(), latestPublishedFlowVersionsMap));
		}

		XMLUtils.appendNewElement(doc, showFlowOverviewElement, "userFavouriteModuleAlias", userFavouriteModuleAlias);
		XMLUtils.appendNewElement(doc, showFlowOverviewElement, "openExternalFlowsInNewWindow", openExternalFlowsInNewWindow);
		XMLUtils.appendNewElement(doc, showFlowOverviewElement, "showRelatedFlows", showRelatedFlows);

		if(operatingMessageModule != null) {
			
			OperatingStatus operatingStatus = operatingMessageModule.getOperatingStatus(flow.getFlowFamily().getFlowFamilyID());
			
			if(operatingStatus != null) {
				showFlowOverviewElement.appendChild(operatingStatus.toXML(doc));
			}
			
		}
		
		return new SimpleForegroundModuleResponse(doc, flow.getName(), this.getDefaultBreadcrumb());

	}

	@WebPublic(alias = "flow")
	public ForegroundModuleResponse processFlowRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws ModuleConfigurationException, SQLException, AccessDeniedException, IOException, FlowDefaultStatusNotFound, EvaluationException {

		Integer flowID = null;
		Integer flowInstanceID = null;
		MutableFlowInstanceManager instanceManager;

		try {
			if (uriParser.size() == 3 && (flowID = NumberUtils.toInt(uriParser.get(2))) != null) {

				//Create new instance or get instance from session
				instanceManager = getUnsavedMutableFlowInstanceManager(flowID, this, req.getSession(true), user, uriParser, req, true, true, true);

				if (instanceManager == null) {

					log.info("User " + user + " requested non-existing flow with ID " + flowID + ", listing flows");
					return list(req, res, user, uriParser, FLOW_NOT_FOUND_VALIDATION_ERROR);
				}

			} else if (uriParser.size() == 4 && (flowID = NumberUtils.toInt(uriParser.get(2))) != null && (flowInstanceID = NumberUtils.toInt(uriParser.get(3))) != null) {

				//Get saved instance from DB or session
				instanceManager = getSavedMutableFlowInstanceManager(flowID, flowInstanceID, this, req.getSession(true), user, uriParser, req, false, true, true);

				if (instanceManager == null) {

					log.info("User " + user + " requested non-existing flow instance with ID " + flowInstanceID + " and flow ID " + flowID + ", listing flows");
					return list(req, res, user, uriParser, FLOW_INSTANCE_NOT_FOUND_VALIDATION_ERROR);
				}

			} else {

				log.info("User " + user + " requested invalid URL, listing flows");
				return list(req, res, user, uriParser, INVALID_LINK_VALIDATION_ERROR);
			}

		} catch (FlowNoLongerAvailableException e) {

			log.info("User " + user + " requested flow " + e.getFlow() + " which is no longer available.");
			return list(req, res, user, uriParser, FLOW_NO_LONGER_AVAILABLE_VALIDATION_ERROR);

		} catch (FlowNotPublishedException e) {

			log.info("User " + user + " requested flow " + e.getFlow() + " which is no longer published.");
			return list(req, res, user, uriParser, FLOW_NO_LONGER_PUBLISHED_VALIDATION_ERROR);

		} catch (FlowDisabledException e) {

			log.info("User " + user + " requested flow " + e.getFlow() + " which is not enabled.");
			return list(req, res, user, uriParser, FLOW_DISABLED_VALIDATION_ERROR);

		} catch (FlowInstanceNoLongerAvailableException e) {

			log.info("User " + user + " requested flow instance " + e.getFlowInstance() + " which is no longer available.");
			return list(req, res, user, uriParser, FLOW_INSTANCE_NO_LONGER_AVAILABLE_VALIDATION_ERROR);

		} catch (FlowLimitExceededException e) {

			log.info("User " + user + " has reached the flow instance limit for flow " + e.getFlow());

			return handleFlowLimitExceededException(req, res, user, uriParser, e);

		} catch (FlowEngineException e) {

			log.error("Unable to get flow instance manager for flowID " + flowID + " and flowInstanceID " + flowInstanceID + " requested by user " + user, e);
			return list(req, res, user, uriParser, ERROR_GETTING_FLOW_INSTANCE_MANAGER_VALIDATION_ERROR);
		}

		try {
			return processFlowRequest(instanceManager, this, this, req, res, user, uriParser, true);

		} catch (FlowInstanceManagerClosedException e) {

			log.info("User " + user + " requested flow instance manager for flow instance " + e.getFlowInstance() + " which has already been closed. Removing flow instance manager from session.");

			removeMutableFlowInstanceManagerFromSession(instanceManager, req.getSession(false));

			if (flowInstanceID != null) {

				redirectToMethod(req, res, "/flow/" + flowID + "/" + flowInstanceID);

			} else {

				redirectToMethod(req, res, "/flow/" + flowID);
			}

			return null;

		} catch (QueryInstanceHTMLException e) {

			return processFlowRequestException(instanceManager, req, res, user, uriParser, e);

		} catch (RuntimeException e) {

			return processFlowRequestException(instanceManager, req, res, user, uriParser, e);
		}
	}

	@WebPublic(alias = "search")
	public ForegroundModuleResponse search(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws ModuleConfigurationException, SQLException, AccessDeniedException, IOException, FlowDefaultStatusNotFound, EvaluationException {

		FlowIndexer flowIndexer = this.flowIndexer;

		if (flowIndexer != null) {

			this.flowIndexer.search(req, res, user);

		} else {

			FlowIndexer.sendEmptyResponse(res);
		}

		return null;
	}

	@WebPublic(alias = "mquery")
	public ForegroundModuleResponse processMutableQueryRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws ModuleConfigurationException, SQLException, AccessDeniedException, IOException, FlowDefaultStatusNotFound, EvaluationException, URINotFoundException, QueryRequestException, QueryProviderException, EvaluationProviderException, InvalidFlowInstanceStepException, MissingQueryInstanceDescriptor, DuplicateFlowInstanceManagerIDException {

		return processMutableQueryRequest(req, res, user, uriParser, this, true, true);
	}

	@WebPublic(alias = "iquery")
	public ForegroundModuleResponse processImmutableQueryRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws ModuleConfigurationException, SQLException, AccessDeniedException, IOException, FlowDefaultStatusNotFound, EvaluationException, URINotFoundException, QueryRequestException, QueryProviderException, EvaluationProviderException, InvalidFlowInstanceStepException, MissingQueryInstanceDescriptor, DuplicateFlowInstanceManagerIDException {

		return processImmutableQueryRequest(req, res, user, uriParser, new SessionAccessController(req.getSession(), SESSION_ACCESS_CONTROLLER_TAG), true);
	}

	@Override
	public void checkNewFlowInstanceAccess(Flow flow, User user) throws AccessDeniedException {

		if (!listAllFlowTypes && !this.flowTypeIDs.contains(flow.getFlowType().getFlowTypeID())) {

			throw new AccessDeniedException("Access denied to flow " + flow + " belonging to flow type " + flow.getFlowType());

		} else if (flow.requiresAuthentication() && user == null) {

			throw new AccessDeniedException("Flow " + flow + " requires autentication");
		}
	}

	@Override
	public void checkFlowInstanceAccess(ImmutableFlowInstance flowInstance, User user) throws AccessDeniedException {

		if (!listAllFlowTypes && !this.flowTypeIDs.contains(flowInstance.getFlow().getFlowType().getFlowTypeID())) {

			throw new AccessDeniedException("Access to flow instance " + flowInstance + " belonging to flow type " + flowInstance.getFlow().getFlowType() + " is not allowed via this module");

		} else if (flowInstance.getPoster() == null || user == null || !flowInstance.getPoster().equals(user)) {

			throw new AccessDeniedException("Access denied to flow instance " + flowInstance + ", the current user in owner of the requested instance.");

		} else if (!flowInstance.getStatus().isUserMutable()) {

			//TODO throw better exception here
			throw new AccessDeniedException("Access denied to flow instance " + flowInstance + ", the requested instance is not in a user mutable state.");
		}
	}

	public void cacheFlows() throws SQLException {

		w.lock();

		try {

			if (!listAllFlowTypes && CollectionUtils.isEmpty(flowTypeIDs)) {

				log.warn("No flowTypeIDs set, unable to cache flows.");

				this.flowTypes = null;
				this.flowMap = null;
				this.latestPublishedFlowVersionsMap = null;
				this.flowIndexer = null;

				return;
			}

			HighLevelQuery<FlowType> query = new HighLevelQuery<FlowType>(FlowType.FLOWS_RELATION, FlowType.CATEGORIES_RELATION, Flow.CATEGORY_RELATION, Flow.FLOW_FAMILY_RELATION, Flow.TAGS_RELATION, Flow.CHECKS_RELATION, Flow.STEPS_RELATION);

			if (listAllFlowTypes) {

				log.info("Caching flows for all flowTypes");

			} else {

				log.info("Caching flows for flowTypeIDs " + StringUtils.toCommaSeparatedString(flowTypeIDs));

				query.addParameter(flowTypeIDParamFactory.getWhereInParameter(flowTypeIDs));

			}

			List<FlowType> flowTypes = daoFactory.getFlowTypeDAO().getAll(query);

			if (flowTypes == null) {

				log.warn("The configured flowTypeIDs were not found in the database.");

				this.flowTypes = null;
				this.flowMap = null;
				this.latestPublishedFlowVersionsMap = null;
				this.flowIndexer = null;

			} else {

				HashMap<Integer, Flow> flowMap = new HashMap<Integer, Flow>();

				for (FlowType flowType : flowTypes) {

					if (flowType.getFlows() != null) {

						for (Flow flow : flowType.getFlows()) {

							if (TextTagReplacer.hasTextTags(flow)) {

								flow.setHasTextTags(true);
							}

							flow.setFlowType(flowType);
							flowMap.put(flow.getFlowID(), flow);
						}

						flowType.setFlows(null);
					}
				}

				this.flowTypes = flowTypes;

				if (!flowMap.isEmpty()) {

					this.flowMap = flowMap;
					this.latestPublishedFlowVersionsMap = getLatestPublishedFlowVersionsMap(flowMap.values());
					this.calculatePopularFlows();

					createFlowIndexer();

				} else {

					this.flowMap = null;
					this.latestPublishedFlowVersionsMap = null;
					this.flowIndexer = null;
					this.flowIndexer = null;
				}

			}

		} finally {

			w.unlock();
		}
	}

	private void createFlowIndexer() {

		try {
			if (this.latestPublishedFlowVersionsMap == null) {

				this.flowIndexer = null;

			} else {

				this.flowIndexer = new FlowIndexer(latestPublishedFlowVersionsMap.values(), maxHitCount);
			}

		} catch (IOException e) {

			log.error("Error indexing flows, searching disabled", e);

			this.flowIndexer = null;
		}
	}

	private void calculatePopularFlows() {

		log.info("Calculating popular flows...");

		w.lock();

		try {

			if (this.flowTypes == null || this.latestPublishedFlowVersionsMap == null) {

				return;
			}

			List<FlowFamily> popularFamilies = new ArrayList<FlowFamily>(latestPublishedFlowVersionsMap.size());

			for (FlowType flowType : this.flowTypes) {

				List<FlowFamily> flowTypePopularFamilies = getPopularFamilies(flowType);

				if (popularFamilies != null && flowTypePopularFamilies != null) {

					popularFamilies.addAll(flowTypePopularFamilies);
				}
			}

			for (Flow flow : this.latestPublishedFlowVersionsMap.values()) {

				flow.setPopular(popularFamilies.contains(flow.getFlowFamily()));
			}

		} catch (SQLException e) {

			log.error("Error calculating popular flow", e);

		} finally {

			w.unlock();
		}

	}

	private List<FlowFamily> getPopularFamilies(FlowType flowType) throws SQLException {

		//Get ID of all families for this flow type with at least one published flow
		List<Integer> familyIDs = new ArrayListQuery<Integer>(dataSource, "SELECT DISTINCT flowFamilyID FROM flowengine_flows WHERE flowTypeID = " + flowType.getFlowTypeID() + " AND publishDate <= CURDATE() AND (unPublishDate IS NULL OR unPublishDate > CURDATE());", IntegerPopulator.getPopulator()).executeQuery();

		if (familyIDs != null) {

			ArrayList<FlowFamily> flowFamilies = new ArrayList<FlowFamily>(familyIDs.size());

			for (Integer flowFamilyID : familyIDs) {

				//Get all flow IDs for this family
				List<Integer> flowIDs = new ArrayListQuery<Integer>(dataSource, "SELECT flowID FROM flowengine_flows WHERE flowFamilyID = " + flowFamilyID, IntegerPopulator.getPopulator()).executeQuery();

				if (flowIDs != null) {

					//Get all flow instances added within the configured popularInterval of hours
					ObjectQuery<Integer> instanceCountQuery = new ObjectQuery<Integer>(dataSource, "SELECT COUNT(flowInstanceID) FROM flowengine_flow_instances WHERE flowID IN (" + StringUtils.toCommaSeparatedString(flowIDs) + ") AND added >= ?", IntegerPopulator.getPopulator());

					instanceCountQuery.setTimestamp(1, new Timestamp(System.currentTimeMillis() - (MillisecondTimeUnits.HOUR * popularInterval)));

					FlowFamily flowFamily = new FlowFamily();
					flowFamily.setFlowFamilyID(flowFamilyID);
					flowFamily.setFlowInstanceCount(instanceCountQuery.executeQuery());
					flowFamilies.add(flowFamily);
				}
			}

			if (flowFamilies.size() > this.popularFlowCount) {

				Collections.sort(flowFamilies, FAMILY_COMPARATOR);

				return flowFamilies.subList(0, popularFlowCount);
			}

			return flowFamilies;
		}

		return null;
	}

	private LinkedHashMap<Integer, Flow> getLatestPublishedFlowVersionsMap(Collection<Flow> flows) {

		HashMap<Integer, Flow> latestPublishedFlowVersionsMap = new HashMap<Integer, Flow>();

		for (Flow flow : flows) {

			if (!flow.isPublished()) {

				continue;
			}

			Flow mapFlow = latestPublishedFlowVersionsMap.get(flow.getFlowFamily().getFlowFamilyID());

			if (mapFlow == null || mapFlow.getVersion() < flow.getVersion()) {

				latestPublishedFlowVersionsMap.put(flow.getFlowFamily().getFlowFamilyID(), flow);
			}
		}

		if (latestPublishedFlowVersionsMap.isEmpty()) {

			return null;
		}

		ArrayList<Flow> flowList = new ArrayList<Flow>(latestPublishedFlowVersionsMap.values());

		Collections.sort(flowList, FLOW_NAME_COMPARATOR);

		LinkedHashMap<Integer, Flow> flowMap = new LinkedHashMap<Integer, Flow>();

		for (Flow flow : flowList) {

			flowMap.put(flow.getFlowFamily().getFlowFamilyID(), flow);

		}

		return flowMap;

	}

	protected ForegroundModuleResponse handleFlowLimitExceededException(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, FlowLimitExceededException e) throws IOException {

		return null;
	}

	@Override
	public void processEvent(CRUDEvent<?> event, EventSource source) {

		try {
			log.info("Received crud event regarding " + event.getAction() + " of " + event.getBeans().size() + " beans with " + event.getBeanClass());
			cacheFlows();
		} catch (SQLException e) {
			log.error("Error caching flows", e);
		}
	}

	public Collection<Flow> getLatestPublishedFlowVersions() {

		if (latestPublishedFlowVersionsMap == null) {

			return null;
		}

		return latestPublishedFlowVersionsMap.values();
	}

	public Flow getLatestPublishedFlowVersion(Integer flowFamilyID) {

		if (latestPublishedFlowVersionsMap == null) {

			return null;
		}

		return latestPublishedFlowVersionsMap.get(flowFamilyID);
	}

	public LinkedHashMap<Integer, Flow> getLatestPublishedFlowVersionMap() {

		return latestPublishedFlowVersionsMap;
	}

	@Override
	protected Flow getBareFlow(Integer flowID) throws SQLException {

		r.lock();

		try {

			if (flowMap != null) {

				return flowMap.get(flowID);
			}

			return null;

		} finally {

			r.unlock();
		}
	}

	@Override
	public void run() {

		r.lock();

		try {

			log.info("Refreshing list of latest published flow versions...");

			if (flowMap != null) {

				this.latestPublishedFlowVersionsMap = getLatestPublishedFlowVersionsMap(flowMap.values());

				createFlowIndexer();
			}

		} finally {

			r.unlock();
		}
	}

	@Override
	public List<SettingDescriptor> getSettings() {

		ArrayList<SettingDescriptor> settingDescriptors = new ArrayList<SettingDescriptor>();

		try {

			List<FlowType> flowTypes = daoFactory.getFlowTypeDAO().getAll();

			List<ValueDescriptor> valueDescriptors = new ArrayList<ValueDescriptor>();

			if (flowTypes != null) {

				for (FlowType flowType : flowTypes) {

					valueDescriptors.add(new ValueDescriptor(flowType.getName(), flowType.getFlowTypeID()));

				}

			}

			settingDescriptors.add(SettingDescriptor.createMultiListSetting("flowTypeIDs", "Flow types", "Flow types to show in this module", false, "", valueDescriptors));

		} catch (SQLException e) {

			log.error("Unable to create setting descriptor for flowTypes", e);

		}

		ModuleUtils.addSettings(settingDescriptors, super.getSettings());

		return settingDescriptors;

	}

	@Override
	public boolean isMutable(ImmutableFlowInstance flowInstance, User user) {

		if (flowInstance.getStatus() == null || flowInstance.getStatus().isUserMutable()) {

			return true;
		}

		return false;
	}

	@Override
	public String getSubmitActionID() {

		return SUBMIT_ACTION_ID;
	}

	@Override
	public String getSaveActionID() {

		return SAVE_ACTION_ID;
	}

	@Override
	public String getPaymentActionID() {

		return PAYMENT_ACTION_ID;
	}

	@Override
	public String getMultiSigningActionID() {

		return MULTI_SIGNING_ACTION_ID;
	}
	
	public String getUserFavouriteModuleAlias() {

		return userFavouriteModuleAlias;
	}

	@Override
	public void appendFormData(Document doc, Element baseElement, MutableFlowInstanceManager instanceManager, User user) {

	}

	@WebPublic(alias = "pdf")
	public ForegroundModuleResponse getEventPDF(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, SQLException, IOException, AccessDeniedException {

		sendEventPDF(req, res, user, uriParser, new SessionAccessController(req.getSession(), SESSION_ACCESS_CONTROLLER_TAG), pdfProvider);

		return null;
	}

	@WebPublic(alias = "submitted")
	public ForegroundModuleResponse showSubmittedMessage(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws FlowInstanceManagerClosedException, UnableToGetQueryInstanceShowHTMLException, AccessDeniedException, ModuleConfigurationException, SQLException, URINotFoundException {

		try {
			return super.showImmutableFlowInstance(req, res, user, uriParser, new SessionAccessController(req.getSession(), SESSION_ACCESS_CONTROLLER_TAG), this, ShowMode.SUBMIT);

		} catch (AccessDeniedException e) {

			throw new URINotFoundException(uriParser);
		}
	}
	
	@WebPublic(alias = "multisign")
	public ForegroundModuleResponse showMultiSignMessage(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws FlowInstanceManagerClosedException, UnableToGetQueryInstanceShowHTMLException, AccessDeniedException, ModuleConfigurationException, SQLException, URINotFoundException {

		return super.showMultiSignMessage(req, res, user, uriParser, new SessionAccessController(req.getSession(), SESSION_ACCESS_CONTROLLER_TAG), this);
	}

	@WebPublic(alias = "pay")
	public ForegroundModuleResponse showPaymentForm(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws FlowInstanceManagerClosedException, UnableToGetQueryInstanceShowHTMLException, AccessDeniedException, ModuleConfigurationException, SQLException, URINotFoundException {

		return super.showPaymentForm(req, res, user, uriParser, new SessionAccessController(req.getSession(), SESSION_ACCESS_CONTROLLER_TAG), this);
	}
	
	@Override
	protected void redirectToSubmitMethod(MutableFlowInstanceManager flowInstanceManager, HttpServletRequest req, HttpServletResponse res) throws IOException {

		SessionAccessController.setSessionAttribute(flowInstanceManager.getFlowInstanceID(), req.getSession(), SESSION_ACCESS_CONTROLLER_TAG);

		redirectToMethod(req, res, "/submitted/" + flowInstanceManager.getFlowInstanceID());
	}

	@Override
	protected void onFlowInstanceClosedRedirect(FlowInstanceManager flowInstanceManager, HttpServletRequest req, HttpServletResponse res) throws IOException {

		redirectToDefaultMethod(req, res);

	}

	@Override
	protected Breadcrumb getFlowInstanceSubmitBreadcrumb(ImmutableFlowInstance flowInstance, HttpServletRequest req, URIParser uriParser) {

		//TODO add prefix
		return new Breadcrumb(this, flowInstance.getFlow().getName(), "/submitted/" + flowInstance.getFlowInstanceID());
	}

	@Override
	protected String getBaseUpdateURL(HttpServletRequest req, URIParser uriParser, User user, ImmutableFlowInstance flowInstance, FlowInstanceAccessController accessController) {

		if (!accessController.isMutable(flowInstance, user)) {

			return null;
		}

		return req.getContextPath() + uriParser.getFormattedURI();
	}

	@Override
	protected SigningProvider getSigningProvider() {

		return signingProvider;
	}

	@Override
	protected PaymentProvider getPaymentProvider() {

		return paymentProvider;
	}

	@Override
	public String getSignFailURL(MutableFlowInstanceManager instanceManager, HttpServletRequest req) {

		return RequestUtils.getFullContextPathURL(req) + this.getFullAlias() + "/flow/" + instanceManager.getFlowID() + "/" + instanceManager.getFlowInstanceID() + "?preview=1&signprovidererror=1";
	}

	@Override
	public String getSigningURL(MutableFlowInstanceManager instanceManager, HttpServletRequest req) {

		return RequestUtils.getFullContextPathURL(req) + this.getFullAlias() + "/flow/" + instanceManager.getFlowID() + "/" + instanceManager.getFlowInstanceID() + "?save-submit=1";
	}

	@Override
	public void signingComplete(MutableFlowInstanceManager instanceManager, FlowInstanceEvent event, HttpServletRequest req, SiteProfile siteProfile, String actionID) {

		super.signingComplete(instanceManager, event, req, siteProfile, actionID);

		SessionAccessController.setSessionAttribute(instanceManager.getFlowInstanceID(), req.getSession(), SESSION_ACCESS_CONTROLLER_TAG);
	}

	@Override
	public String getPaymentSuccessURL(FlowInstanceManager instanceManager, HttpServletRequest req) {

		SessionAccessController.setSessionAttribute(instanceManager.getFlowInstanceID(), req.getSession(), SESSION_ACCESS_CONTROLLER_TAG);
		
		return super.getPaymentSuccessURL(instanceManager, req);
	}
	
	@Override
	protected String getEventPDFLink(FlowInstanceManager instanceManager, ImmutableFlowInstanceEvent event, HttpServletRequest req, User user) {

		if (event.getAttributeHandler().getPrimitiveBoolean("pdf")) {

			return this.getModuleURI(req) + "/pdf/" + instanceManager.getFlowInstanceID() + "/" + event.getEventID();
		}

		return null;
	}

	@Override
	protected void reOpenFlowInstance(Integer flowID, Integer flowInstanceID, HttpServletRequest req, User user, URIParser uriParser) {

		try {
			getSavedMutableFlowInstanceManager(flowID, flowInstanceID, this, req.getSession(true), user, uriParser, req, true, true, true);

		} catch (Exception e) {

			log.error("Error reopening flow instance with ID " + flowInstanceID + " for user " + user, e);
		}
	}

	@Override
	protected MultiSigningProvider getMultiSigningProvider() {

		return multiSigningProvider;
	}
	
	public void multiSigningComplete(FlowInstanceManager instanceManager, SiteProfile siteProfile){
		
		boolean requiresPayment = requiresPayment(instanceManager);
		
		EventType eventType;
		String actionID;
		
		if(requiresPayment){
			
			actionID = FlowBrowserModule.PAYMENT_ACTION_ID;
			eventType = EventType.STATUS_UPDATED;
			
		}else{
			
			actionID = FlowBrowserModule.SUBMIT_ACTION_ID;
			eventType = EventType.SUBMITTED;
			
		}
		
		Status nextStatus = (Status) instanceManager.getFlowInstance().getFlow().getDefaultState(actionID);
		
		if(nextStatus == null){
			
			log.error("Unable to find status for actionID " + actionID + " for flow instance " + instanceManager + ", flow instance will be left with wrong status.");
			return;
		}
		
		try {
			FlowInstance flowInstance = (FlowInstance) instanceManager.getFlowInstance();
			
			flowInstance.setStatus(nextStatus);
			flowInstance.setLastStatusChange(TimeUtils.getCurrentTimestamp());
			this.daoFactory.getFlowInstanceDAO().update(flowInstance);
			
			FlowInstanceEvent event = addFlowInstanceEvent(flowInstance, eventType, null, null);
			
			eventHandler.sendEvent(FlowInstance.class, new CRUDEvent<FlowInstance>(CRUDAction.UPDATE, flowInstance), EventTarget.ALL);
			
			//TODO send multi sign complete event
			
			if(!requiresPayment){
				
				sendSubmitEvent(instanceManager, event, actionID, siteProfile);
			}
			
		} catch (SQLException e) {

			log.error("Error changing status and adding event for flow instance " + instanceManager + ", flow instance will be left with wrong status.", e);
		}
	}
	
	@Override
	public int getPriority() {

		return 0;
	}	
}
