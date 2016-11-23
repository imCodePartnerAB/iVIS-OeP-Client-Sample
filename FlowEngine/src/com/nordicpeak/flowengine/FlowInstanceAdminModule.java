package com.nordicpeak.flowengine;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.annotations.CheckboxSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.InstanceManagerDependency;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.annotations.XSLVariable;
import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
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
import se.unlogic.hierarchy.core.interfaces.SystemStartupListener;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.LowLevelQuery;
import se.unlogic.standardutils.dao.QueryParameterFactory;
import se.unlogic.standardutils.dao.RelationQuery;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.dao.querys.ArrayListQuery;
import se.unlogic.standardutils.date.DateUtils;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.populators.IntegerPopulator;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.time.TimeUtils;
import se.unlogic.standardutils.validation.PositiveStringIntegerValidator;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;

import com.nordicpeak.flowengine.accesscontrollers.ManagerFlowInstanceAccessController;
import com.nordicpeak.flowengine.beans.ExternalMessage;
import com.nordicpeak.flowengine.beans.ExternalMessageAttachment;
import com.nordicpeak.flowengine.beans.Flow;
import com.nordicpeak.flowengine.beans.FlowFamily;
import com.nordicpeak.flowengine.beans.FlowInstance;
import com.nordicpeak.flowengine.beans.FlowInstanceEvent;
import com.nordicpeak.flowengine.beans.InternalMessage;
import com.nordicpeak.flowengine.beans.InternalMessageAttachment;
import com.nordicpeak.flowengine.beans.QueryDescriptor;
import com.nordicpeak.flowengine.beans.Status;
import com.nordicpeak.flowengine.beans.Step;
import com.nordicpeak.flowengine.beans.UserBookmark;
import com.nordicpeak.flowengine.cruds.ExternalMessageCRUD;
import com.nordicpeak.flowengine.cruds.InternalMessageCRUD;
import com.nordicpeak.flowengine.enums.ContentType;
import com.nordicpeak.flowengine.enums.EventType;
import com.nordicpeak.flowengine.enums.Priority;
import com.nordicpeak.flowengine.enums.SenderType;
import com.nordicpeak.flowengine.enums.ShowMode;
import com.nordicpeak.flowengine.events.ExternalMessageAddedEvent;
import com.nordicpeak.flowengine.events.ManagersChangedEvent;
import com.nordicpeak.flowengine.events.StatusChangedByManagerEvent;
import com.nordicpeak.flowengine.exceptions.FlowEngineException;
import com.nordicpeak.flowengine.exceptions.evaluation.EvaluationException;
import com.nordicpeak.flowengine.exceptions.evaluationprovider.EvaluationProviderException;
import com.nordicpeak.flowengine.exceptions.flow.FlowDefaultStatusNotFound;
import com.nordicpeak.flowengine.exceptions.flow.FlowDisabledException;
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
import com.nordicpeak.flowengine.interfaces.PDFProvider;
import com.nordicpeak.flowengine.interfaces.XMLProvider;
import com.nordicpeak.flowengine.managers.FlowInstanceManager;
import com.nordicpeak.flowengine.managers.MutableFlowInstanceManager;
import com.nordicpeak.flowengine.search.FlowInstanceIndexer;
import com.nordicpeak.flowengine.validationerrors.UnauthorizedManagerUserValidationError;

public class FlowInstanceAdminModule extends BaseFlowBrowserModule implements FlowProcessCallback, SystemStartupListener, EventListener<CRUDEvent<?>> {

	protected static final Field[] FLOW_INSTANCE_OVERVIEW_RELATIONS = { FlowInstance.INTERNAL_MESSAGES_RELATION, InternalMessage.ATTACHMENTS_RELATION, FlowInstance.EXTERNAL_MESSAGES_RELATION, ExternalMessage.ATTACHMENTS_RELATION, FlowInstance.FLOW_RELATION, Flow.FLOW_FAMILY_RELATION, FlowFamily.MANAGER_GROUPS_RELATION, FlowFamily.MANAGER_USERS_RELATION, FlowInstance.FLOW_STATE_RELATION, FlowInstance.EVENTS_RELATION, FlowInstanceEvent.ATTRIBUTES_RELATION, FlowInstance.MANAGERS_RELATION};

	@SuppressWarnings("rawtypes")
	private static final Class[] EVENT_LISTENER_CLASSES = new Class[] { FlowFamily.class, Flow.class, FlowInstance.class };

	protected static final String FLOW_MANAGER_SQL = "SELECT flowID FROM flowengine_flows WHERE enabled = true AND flowFamilyID IN (SELECT ff.flowFamilyID FROM flowengine_flow_families ff LEFT JOIN flowengine_flow_family_manager_users ffu on ff.flowFamilyID = ffu.flowFamilyID LEFT JOIN flowengine_flow_family_manager_groups ffg on ff.flowFamilyID = ffg.flowFamilyID WHERE ffu.userID = ?";
	protected static final String FLOW_INSTANCE_BOOKMARKS_SQL = "SELECT ffi.* FROM flowengine_flow_instances ffi LEFT JOIN flowengine_flow_instance_bookmarks ffib ON ffi.flowInstanceID = ffib.flowInstanceID WHERE ffib.userID = ? AND ffi.flowID IN (";
	protected static final String ACTIVE_FLOWS = "SELECT ffi.* FROM flowengine_flow_instances ffi LEFT JOIN flowengine_flow_statuses ffs ON ffi.statusID = ffs.statusID WHERE ffi.flowID IN ($flowIDs) AND ffs.contentType NOT IN ('" + ContentType.NEW + "', '" + ContentType.ARCHIVED + "')";

	public static final ManagerFlowInstanceAccessController UPDATE_ACCESS_CONTROLLER = new ManagerFlowInstanceAccessController(true, false);
	public static final ManagerFlowInstanceAccessController DELETE_ACCESS_CONTROLLER = new ManagerFlowInstanceAccessController(false, true);
	public static final ManagerFlowInstanceAccessController GENERAL_ACCESS_CONTROLLER = new ManagerFlowInstanceAccessController(false, false);

	public static final ValidationError STATUS_NOT_FOUND_VALIDATION_ERROR = new ValidationError("StatusNotFoundValidationError");
	public static final ValidationError INVALID_STATUS_VALIDATION_ERROR = new ValidationError("InvalidStatusValidationError");
	public static final ValidationError FLOW_INSTANCE_PREVIEW_VALIDATION_ERROR = new ValidationError("FlowInstancePreviewError");
	public static final ValidationError FLOW_INSTANCE_MANAGER_CLOSED_VALIDATION_ERROR = new ValidationError("FlowInstanceManagerClosedError");

	public static final ValidationError ONE_OR_MORE_SELECTED_MANAGER_USERS_NOT_FOUND_VALIDATION_ERROR = new ValidationError("OneOrMoreSelectedManagerUsersNotFoundError");

	@XSLVariable(prefix = "java.")
	private String noManagersSelected = "No managers selected";

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "High priority lapsed managing time", description = "The precent of the managing time of the current status that has to have elapsed for an instance to be classified as high priority", required = true, formatValidator = PositiveStringIntegerValidator.class)
	protected int highPriorityThreshold = 90;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Medium priority lapsed managing time", description = "The precent of the managing time of the current status that has to have elapsed for an instance to be classified as medium priority", required = true, formatValidator = PositiveStringIntegerValidator.class)
	protected int mediumPriorityThreshold = 60;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Max search hits", description = "Maximum number of hits to get from index when searching", formatValidator = PositiveStringIntegerValidator.class, required = true)
	protected int maxHitCount = 20;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Max unfiltered hits", description = "Maximum number of hits to get from index before access check", formatValidator = PositiveStringIntegerValidator.class)
	protected int maxUnfilteredHitCount = 100;

	@ModuleSetting
	@CheckboxSettingDescriptor(name="Enable site profile support", description="Controls if site profile support is enabled")
	protected boolean enableSiteProfileSupport;

	@ModuleSetting
	@CheckboxSettingDescriptor(name="Enable logging of flow instance indexing", description="Enables logging of indexing of flow instances")
	protected boolean logFlowInstanceIndexing;

	@InstanceManagerDependency
	protected PDFProvider pdfProvider;

	@InstanceManagerDependency
	protected XMLProvider xmlProvider;

	protected ExternalMessageCRUD externalMessageCRUD;
	protected InternalMessageCRUD internalMessageCRUD;

	private FlowInstanceIndexer flowInstanceIndexer;

	private QueryParameterFactory<Status, Integer> statusIDParamFactory;
	private QueryParameterFactory<Status, Flow> statusFlowParamFactory;
	private QueryParameterFactory<UserBookmark, FlowInstance> bookmarkFlowInstanceParamFactory;
	private QueryParameterFactory<UserBookmark, User> bookmarkUserParamFactory;

	@Override
	public void update(ForegroundModuleDescriptor descriptor, DataSource dataSource) throws Exception {

		super.update(descriptor, dataSource);

		this.flowInstanceIndexer.setMaxHitCount(maxHitCount);
	}

	@Override
	public void unload() throws Exception {

		eventHandler.removeEventListener(CRUDEvent.class, this, EVENT_LISTENER_CLASSES);

		flowInstanceIndexer.close();

		super.unload();
	}

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		super.createDAOs(dataSource);

		externalMessageCRUD = new ExternalMessageCRUD(daoFactory.getExternalMessageDAO(), daoFactory.getExternalMessageAttachmentDAO(), this);
		internalMessageCRUD = new InternalMessageCRUD(daoFactory.getInternalMessageDAO(), daoFactory.getInternalMessageAttachmentDAO(), this);

		statusIDParamFactory = daoFactory.getStatusDAO().getParamFactory("statusID", Integer.class);
		statusFlowParamFactory = daoFactory.getStatusDAO().getParamFactory("flow", Flow.class);
		bookmarkFlowInstanceParamFactory = daoFactory.getUserBookmarkDAO().getParamFactory("flowInstance", FlowInstance.class);
		bookmarkUserParamFactory = daoFactory.getUserBookmarkDAO().getParamFactory("user", User.class);

		FlowInstanceIndexer oldIndexer = flowInstanceIndexer;

		flowInstanceIndexer = new FlowInstanceIndexer(daoFactory, maxHitCount, maxUnfilteredHitCount, systemInterface);

		//Listener that triggers indexing of flow instances
		systemInterface.addStartupListener(this);

		if(oldIndexer != null){

			oldIndexer.close();
		}

		eventHandler.addEventListener(CRUDEvent.class, this, EVENT_LISTENER_CLASSES);
	}

	@Override
	protected void moduleConfigured() throws Exception {

		this.flowInstanceIndexer.setLogIndexing(this.logFlowInstanceIndexing);
	}

	@Override
	public ForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return this.list(req, res, user, uriParser, (List<ValidationError>)null);
	}

	public ForegroundModuleResponse list(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, ValidationError validationError) throws SQLException {

		return list(req, res, user, uriParser, CollectionUtils.getGenericSingletonList(validationError));
	}

	@Override
	public ForegroundModuleResponse list(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, List<ValidationError> validationErrors) throws SQLException {

		log.info("User " + user + " listing flow instances");

		List<Integer> flowIDs = getUserFlowIDs(user);

		if(flowIDs == null){
			Document doc = createDocument(req, uriParser, user);

			Element overviewElement = doc.createElement("OverviewElement");
			doc.getDocumentElement().appendChild(overviewElement);

			return new SimpleForegroundModuleResponse(doc, this.getDefaultBreadcrumb());
		}

		List<FlowInstance> activeFlowInstances = getActiveFlowInstances(user, flowIDs);
		List<FlowInstance> bookmarkedFlows = getFlowInstanceBookmarks(user, flowIDs);

		Document doc = createDocument(req, uriParser, user);

		Element overviewElement = doc.createElement("OverviewElement");
		doc.getDocumentElement().appendChild(overviewElement);

		XMLUtils.append(doc, overviewElement, validationErrors);

		Element bookmarkedInstances = doc.createElement("BookmarkedInstances");
		overviewElement.appendChild(bookmarkedInstances);

		XMLUtils.append(doc, bookmarkedInstances, bookmarkedFlows);

		if(activeFlowInstances != null){

			Element prioritizedInstances = doc.createElement("PrioritizedInstances");
			overviewElement.appendChild(prioritizedInstances);

			Element userAssignedInstances = doc.createElement("UserAssignedInstances");
			overviewElement.appendChild(userAssignedInstances);

			Element activeInstances = doc.createElement("ActiveInstances");
			overviewElement.appendChild(activeInstances);

			Element unassignedInstances = doc.createElement("UnassignedInstances");
			overviewElement.appendChild(unassignedInstances);

			for(FlowInstance instance : activeFlowInstances){

				if(instance.getManagers() == null){

					unassignedInstances.appendChild(instance.toXML(doc));

				}else if(instance.getManagers().contains(user)){

					userAssignedInstances.appendChild(instance.toXML(doc));

				}else{

					activeInstances.appendChild(instance.toXML(doc));
				}

				if(instance.getStatus().getManagingTime() != null){

					int daysLapsed = DateUtils.getWorkingDays(instance.getLastStatusChange(), new Date());

					float percent = (daysLapsed / instance.getStatus().getManagingTime()) * 100;

					if(percent >= highPriorityThreshold){

						instance.setPriority(Priority.HIGH);
						prioritizedInstances.appendChild(instance.toXML(doc));

					}else if(percent >= mediumPriorityThreshold){

						instance.setPriority(Priority.MEDIUM);
						prioritizedInstances.appendChild(instance.toXML(doc));

					}

				}
			}
		}

		if(enableSiteProfileSupport && this.profileHandler != null){

			XMLUtils.append(doc, overviewElement, "SiteProfiles", this.profileHandler.getProfiles());
		}

		return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb());
	}

	@WebPublic(alias = "overview")
	public ForegroundModuleResponse showFlowInstanceOverview(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws ModuleConfigurationException, SQLException, AccessDeniedException, IOException, URINotFoundException {

		FlowInstance flowInstance;

		if(uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (flowInstance = getFlowInstance(Integer.valueOf(uriParser.get(2)), CollectionUtils.getList(ExternalMessageAttachment.DATA_FIELD, InternalMessageAttachment.DATA_FIELD), getFlowInstanceOverviewRelations())) != null && !flowInstance.getStatus().getContentType().equals(ContentType.NEW)){

			GENERAL_ACCESS_CONTROLLER.checkFlowInstanceAccess(flowInstance, user);

			Document doc = this.createDocument(req, uriParser, user);

			Element showFlowInstanceOverviewElement = doc.createElement("ShowFlowInstanceOverview");

			doc.getDocumentElement().appendChild(showFlowInstanceOverviewElement);

			if(req.getMethod().equalsIgnoreCase("POST")){

				if(req.getParameter("externalmessage") != null){

					ExternalMessage externalMessage = externalMessageCRUD.add(req, res, uriParser, user, doc, showFlowInstanceOverviewElement, flowInstance);

					if(externalMessage != null){

						FlowInstanceEvent flowInstanceEvent = this.addFlowInstanceEvent(flowInstance, EventType.MANAGER_MESSAGE_SENT, null, user);

						systemInterface.getEventHandler().sendEvent(FlowInstance.class, new ExternalMessageAddedEvent(flowInstance, flowInstanceEvent, moduleDescriptor, getCurrentSiteProfile(req, user, uriParser), externalMessage, SenderType.MANAGER), EventTarget.ALL);

						res.sendRedirect(req.getContextPath() + uriParser.getFormattedURI() + "#new-message");

						return null;

					}

				}else if(req.getParameter("internalmessage") != null){

					InternalMessage internalMessage = internalMessageCRUD.add(req, res, uriParser, user, doc, showFlowInstanceOverviewElement, flowInstance);

					if(internalMessage != null){

						res.sendRedirect(req.getContextPath() + uriParser.getFormattedURI() + "#new-note");

						return null;

					}
				}
			}

			showFlowInstanceOverviewElement.appendChild(flowInstance.toXML(doc));

			if(user != null){
				showFlowInstanceOverviewElement.appendChild(user.toXML(doc));
			}

			if(enableSiteProfileSupport && flowInstance.getProfileID() != null && this.profileHandler != null){

				XMLUtils.append(doc, showFlowInstanceOverviewElement, profileHandler.getProfile(flowInstance.getProfileID()));
			}

			appendBookmark(doc, showFlowInstanceOverviewElement, flowInstance, user);

			appendOverviewData(doc, showFlowInstanceOverviewElement, flowInstance, user);

			return new SimpleForegroundModuleResponse(doc, flowInstance.getFlow().getName(), this.getDefaultBreadcrumb());
		}

		return list(req, res, user, uriParser, FLOW_INSTANCE_NOT_FOUND_VALIDATION_ERROR);
	}

	protected Field[] getFlowInstanceOverviewRelations() {

		return FLOW_INSTANCE_OVERVIEW_RELATIONS;
	}

	protected void appendOverviewData(Document doc, Element showFlowInstanceOverviewElement, FlowInstance flowInstance, User user) throws SQLException {}

	@WebPublic(alias = "status")
	public ForegroundModuleResponse updateStatus(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws ModuleConfigurationException, SQLException, AccessDeniedException, IOException {

		Field[] relations = { FlowInstance.FLOW_RELATION, Flow.FLOW_FAMILY_RELATION, FlowFamily.MANAGER_GROUPS_RELATION, FlowFamily.MANAGER_USERS_RELATION, FlowInstance.FLOW_STATE_RELATION, Flow.STATUSES_RELATION };

		FlowInstance flowInstance;

		if(uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (flowInstance = getFlowInstance(Integer.valueOf(uriParser.get(2)), null, relations)) != null && !flowInstance.getStatus().getContentType().equals(ContentType.NEW)){

			log.info("User " + user + " requesting update status form for instance " + flowInstance);

			GENERAL_ACCESS_CONTROLLER.checkFlowInstanceAccess(flowInstance, user);

			Document doc = this.createDocument(req, uriParser, user);

			Element updateInstanceStatusElement = doc.createElement("UpdateInstanceStatus");

			doc.getDocumentElement().appendChild(updateInstanceStatusElement);

			if(req.getMethod().equalsIgnoreCase("POST")){

				Integer statusID = NumberUtils.toInt(req.getParameter("statusID"));

				Status status;

				if(statusID == null || (status = getStatus(flowInstance.getFlow(), statusID)) == null){

					updateInstanceStatusElement.appendChild(STATUS_NOT_FOUND_VALIDATION_ERROR.toXML(doc));

				}else if(status.getContentType() == ContentType.NEW){

					updateInstanceStatusElement.appendChild(INVALID_STATUS_VALIDATION_ERROR.toXML(doc));

				}else{

					Status previousStatus = flowInstance.getStatus();

					if(!previousStatus.equals(status)){

						log.info("User " + user + " changing status of instance " + flowInstance + " from " + previousStatus + " to " + status);

						flowInstance.setStatus(status);
						flowInstance.setLastStatusChange(TimeUtils.getCurrentTimestamp());
						this.daoFactory.getFlowInstanceDAO().update(flowInstance);

						FlowInstanceEvent flowInstanceEvent = addFlowInstanceEvent(flowInstance, EventType.STATUS_UPDATED, null, user);

						eventHandler.sendEvent(FlowInstance.class, new CRUDEvent<FlowInstance>(CRUDAction.UPDATE, flowInstance), EventTarget.ALL);

						eventHandler.sendEvent(FlowInstance.class, new StatusChangedByManagerEvent(flowInstance, flowInstanceEvent, moduleDescriptor, getCurrentSiteProfile(req, user, uriParser), previousStatus, user), EventTarget.ALL);
					}

					redirectToMethod(req, res, "/overview/" + flowInstance.getFlowInstanceID());

					return null;
				}
			}

			updateInstanceStatusElement.appendChild(flowInstance.toXML(doc));

			XMLUtils.append(doc, updateInstanceStatusElement, user);

			appendBookmark(doc, updateInstanceStatusElement, flowInstance, user);

			return new SimpleForegroundModuleResponse(doc, flowInstance.getFlow().getName(), this.getDefaultBreadcrumb());
		}

		return list(req, res, user, uriParser, FLOW_INSTANCE_NOT_FOUND_VALIDATION_ERROR);
	}

	private Status getStatus(Flow flow, Integer statusID) throws SQLException {

		HighLevelQuery<Status> query = new HighLevelQuery<Status>();

		query.addParameter(statusIDParamFactory.getParameter(statusID));
		query.addParameter(statusFlowParamFactory.getParameter(flow));

		return daoFactory.getStatusDAO().get(query);
	}

	@WebPublic(alias = "managers")
	public ForegroundModuleResponse updateManagers(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws ModuleConfigurationException, SQLException, AccessDeniedException, IOException {

		Field[] relations = { FlowInstance.FLOW_RELATION, Flow.FLOW_FAMILY_RELATION, FlowFamily.MANAGER_GROUPS_RELATION, FlowFamily.MANAGER_USERS_RELATION, FlowInstance.FLOW_STATE_RELATION, FlowInstance.MANAGERS_RELATION };

		FlowInstance flowInstance;

		if(uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (flowInstance = getFlowInstance(Integer.valueOf(uriParser.get(2)), null, relations)) != null && !flowInstance.getStatus().getContentType().equals(ContentType.NEW)){

			log.info("User " + user + " requesting update managers form for instance " + flowInstance);

			GENERAL_ACCESS_CONTROLLER.checkFlowInstanceAccess(flowInstance, user);

			Document doc = this.createDocument(req, uriParser, user);

			Element updateInstanceManagersElement = doc.createElement("UpdateInstanceManagers");

			doc.getDocumentElement().appendChild(updateInstanceManagersElement);

			List<User> availableManagers = getAvailableManagers(flowInstance);

			if(req.getMethod().equalsIgnoreCase("POST")){

				List<Integer> userIDs = NumberUtils.toInt(req.getParameterValues("userID"));

				String detailString;

				try{

					List<User> previousManagers = flowInstance.getManagers();

					if(userIDs != null){

						List<User> users = systemInterface.getUserHandler().getUsers(userIDs, false, false);

						if(CollectionUtils.getSize(users) < userIDs.size()){

							throw new ValidationException(ONE_OR_MORE_SELECTED_MANAGER_USERS_NOT_FOUND_VALIDATION_ERROR);
						}

						StringBuilder stringBuilder = new StringBuilder();

						for(User selectedManager : users){

							if(!availableManagers.contains(selectedManager)){

								throw new ValidationException(new UnauthorizedManagerUserValidationError(selectedManager));
							}

							if(stringBuilder.length() > 0){

								stringBuilder.append(", ");
							}

							stringBuilder.append(selectedManager.getFirstname());
							stringBuilder.append(" ");
							stringBuilder.append(selectedManager.getLastname());

						}

						detailString = stringBuilder.toString();
						flowInstance.setManagers(users);

					}else{

						detailString = noManagersSelected;
						flowInstance.setManagers(null);
					}

					if(!CollectionUtils.equals(previousManagers, flowInstance.getManagers())){

						log.info("User " + user + " setting managers of instance " + flowInstance + " to " + flowInstance.getManagers());

						RelationQuery relationQuery = new RelationQuery(FlowInstance.MANAGERS_RELATION);

						this.daoFactory.getFlowInstanceDAO().update(flowInstance, relationQuery);

						FlowInstanceEvent flowInstanceEvent = addFlowInstanceEvent(flowInstance, EventType.MANAGERS_UPDATED, detailString, user);

						eventHandler.sendEvent(FlowInstance.class, new CRUDEvent<FlowInstance>(CRUDAction.UPDATE, flowInstance), EventTarget.ALL);

						eventHandler.sendEvent(FlowInstance.class, new ManagersChangedEvent(flowInstance, flowInstanceEvent, moduleDescriptor, getCurrentSiteProfile(req, user, uriParser), previousManagers, user), EventTarget.ALL);
					}

					redirectToMethod(req, res, "/overview/" + flowInstance.getFlowInstanceID());

					return null;

				}catch(ValidationException e){

					XMLUtils.append(doc, updateInstanceManagersElement, e.getErrors());
				}
			}

			updateInstanceManagersElement.appendChild(flowInstance.toXML(doc));

			XMLUtils.append(doc, updateInstanceManagersElement, user);

			XMLUtils.append(doc, updateInstanceManagersElement, "AvailableManagers", availableManagers);

			appendBookmark(doc, updateInstanceManagersElement, flowInstance, user);

			return new SimpleForegroundModuleResponse(doc, flowInstance.getFlow().getName(), this.getDefaultBreadcrumb());
		}

		return list(req, res, user, uriParser, FLOW_INSTANCE_NOT_FOUND_VALIDATION_ERROR);
	}

	@WebPublic(alias = "delete")
	public ForegroundModuleResponse deleteFlowInstance(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws ModuleConfigurationException, SQLException, AccessDeniedException, IOException {

		FlowInstance flowInstance;

		if(uriParser.size() == 3 && uriParser.getInt(2) != null && (flowInstance = getFlowInstance(uriParser.getInt(2))) != null && !flowInstance.getStatus().getContentType().equals(ContentType.NEW)){

			checkDeleteAccess(flowInstance, user);

			log.info("User " + user + " deleting flow instance " + flowInstance);

			TransactionHandler transactionHandler = null;

			try{
				transactionHandler = new TransactionHandler(dataSource);

				for(Step step : flowInstance.getFlow().getSteps()){

					if(step.getQueryDescriptors() != null){

						for(QueryDescriptor queryDescriptor : step.getQueryDescriptors()){

							if(queryDescriptor.getQueryInstanceDescriptors() != null){

								try {
									queryDescriptor.getQueryInstanceDescriptors().get(0).setQueryDescriptor(queryDescriptor);
									this.queryHandler.deleteQueryInstance(queryDescriptor.getQueryInstanceDescriptors().get(0), transactionHandler);

								} catch (QueryProviderException e) {

									log.error("Unable to delete " + queryDescriptor + " belonging to flow instance " + flowInstance, e);
								}
							}
						}
					}
				}

				daoFactory.getFlowInstanceDAO().delete(flowInstance, transactionHandler);

				transactionHandler.commit();

			}finally{

				TransactionHandler.autoClose(transactionHandler);
			}

			eventHandler.sendEvent(FlowInstance.class, new CRUDEvent<FlowInstance>(CRUDAction.DELETE, flowInstance), EventTarget.ALL);

			redirectToDefaultMethod(req, res);

			return null;
		}

		return list(req, res, user, uriParser, FLOW_INSTANCE_NOT_FOUND_VALIDATION_ERROR);
	}

	protected void checkDeleteAccess(FlowInstance flowInstance, User user) throws AccessDeniedException {

		DELETE_ACCESS_CONTROLLER.checkFlowInstanceAccess(flowInstance, user);
	}

	@WebPublic(alias = "bookmark")
	public ForegroundModuleResponse toggleBookmark(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws ModuleConfigurationException, SQLException, AccessDeniedException, IOException {

		Field[] relations = { FlowInstance.FLOW_RELATION, Flow.FLOW_FAMILY_RELATION, FlowFamily.MANAGER_GROUPS_RELATION, FlowFamily.MANAGER_USERS_RELATION, FlowInstance.FLOW_STATE_RELATION};

		FlowInstance flowInstance;

		if(uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (flowInstance = getFlowInstance(Integer.valueOf(uriParser.get(2)), null, relations)) != null && !flowInstance.getStatus().getContentType().equals(ContentType.NEW)){

			GENERAL_ACCESS_CONTROLLER.checkFlowInstanceAccess(flowInstance, user);

			UserBookmark bookmark = getBookmark(user, flowInstance);

			if(bookmark == null){

				log.info("User " + user + " adding bookmark for instance " + flowInstance);

				bookmark = new UserBookmark();
				bookmark.setFlowInstance(flowInstance);
				bookmark.setUser(user);
				daoFactory.getUserBookmarkDAO().add(bookmark);

			}else{

				log.info("User " + user + " removing bookmark for instance " + flowInstance);
				daoFactory.getUserBookmarkDAO().delete(bookmark);
				bookmark = null;
			}

			res.setContentType("text/html");
			res.setCharacterEncoding(systemInterface.getEncoding());
			res.getWriter().write(bookmark != null ? "1" : "0");
			res.getWriter().flush();

			return null;
		}

		return list(req, res, user, uriParser, FLOW_INSTANCE_NOT_FOUND_VALIDATION_ERROR);
	}

	@WebPublic(alias = "mquery")
	public ForegroundModuleResponse processMutableQueryRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws ModuleConfigurationException, SQLException, AccessDeniedException, IOException, FlowDefaultStatusNotFound, EvaluationException, URINotFoundException, QueryRequestException, QueryProviderException, EvaluationProviderException, InvalidFlowInstanceStepException, MissingQueryInstanceDescriptor, DuplicateFlowInstanceManagerIDException {

		return processMutableQueryRequest(req, res, user, uriParser, UPDATE_ACCESS_CONTROLLER, true, true);
	}

	@WebPublic(alias = "iquery")
	public ForegroundModuleResponse processImmutableQueryRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws ModuleConfigurationException, SQLException, AccessDeniedException, IOException, FlowDefaultStatusNotFound, EvaluationException, URINotFoundException, QueryRequestException, QueryProviderException, EvaluationProviderException, InvalidFlowInstanceStepException, MissingQueryInstanceDescriptor, DuplicateFlowInstanceManagerIDException {

		return processImmutableQueryRequest(req, res, user, uriParser, GENERAL_ACCESS_CONTROLLER, true);
	}

	private UserBookmark getBookmark(User user, FlowInstance flowInstance) throws SQLException {

		HighLevelQuery<UserBookmark> query = new HighLevelQuery<UserBookmark>(UserBookmark.FLOW_INSTANCE_RELATION);

		query.addParameter(bookmarkFlowInstanceParamFactory.getParameter(flowInstance));
		query.addParameter(bookmarkUserParamFactory.getParameter(user));

		return daoFactory.getUserBookmarkDAO().get(query);
	}

	private List<User> getAvailableManagers(FlowInstance flowInstance) {

		List<User> availableManagers = null;

		if(flowInstance.getFlow().getFlowFamily().getAllowedUserIDs() != null){

			availableManagers = systemInterface.getUserHandler().getUsers(flowInstance.getFlow().getFlowFamily().getAllowedUserIDs(), false, false);
		}

		if(flowInstance.getFlow().getFlowFamily().getAllowedGroupIDs() != null){

			List<User> groupUsers = systemInterface.getUserHandler().getUsersByGroups(flowInstance.getFlow().getFlowFamily().getAllowedGroupIDs(), false);

			if(groupUsers != null){

				if(availableManagers == null){

					availableManagers = new ArrayList<User>(groupUsers.size());

				}

				for(User groupUser : groupUsers){

					if(!availableManagers.contains(groupUser)){
						availableManagers.add(groupUser);
					}

				}

			}
		}

		return availableManagers;
	}

	@WebPublic(alias = "preview")
	public ForegroundModuleResponse processPreviewRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, AccessDeniedException, ModuleConfigurationException, SQLException, IOException, UnableToGetQueryInstanceShowHTMLException, FlowInstanceManagerClosedException {

		return this.showImmutableFlowInstance(req, res, user, uriParser, GENERAL_ACCESS_CONTROLLER, this, ShowMode.PREVIEW);
	}

	@WebPublic(alias = "flowinstance")
	public ForegroundModuleResponse processFlowRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, AccessDeniedException, ModuleConfigurationException, SQLException, IOException, FlowDefaultStatusNotFound, EvaluationException {

		Integer flowID = null;
		Integer flowInstanceID = null;
		MutableFlowInstanceManager instanceManager;

		try{
			if(uriParser.size() == 4 && (flowID = NumberUtils.toInt(uriParser.get(2))) != null && (flowInstanceID = NumberUtils.toInt(uriParser.get(3))) != null){

				// Get saved instance from DB or session
				instanceManager = getSavedMutableFlowInstanceManager(flowID, flowInstanceID, UPDATE_ACCESS_CONTROLLER, req.getSession(true), user, uriParser, req, true, true, true);

				if(instanceManager == null){

					log.info("User " + user + " requested non-existing flow instance with ID " + flowInstanceID + " and flow ID " + flowID + ", listing flows");
					return list(req, res, user, uriParser, FLOW_INSTANCE_NOT_FOUND_VALIDATION_ERROR);
				}

			}else{

				log.info("User " + user + " requested invalid URL, listing flows");
				return list(req, res, user, uriParser, INVALID_LINK_VALIDATION_ERROR);
			}

		}catch(FlowNoLongerAvailableException e){

			log.info("User " + user + " requested flow " + e.getFlow() + " which is no longer available.");
			return list(req, res, user, uriParser, FLOW_NO_LONGER_AVAILABLE_VALIDATION_ERROR);

		}catch(FlowNotPublishedException e){

			log.info("User " + user + " requested flow " + e.getFlow() + " which is no longer published.");
			return list(req, res, user, uriParser, FLOW_NO_LONGER_PUBLISHED_VALIDATION_ERROR);

		}catch(FlowDisabledException e){

			log.info("User " + user + " requested flow " + e.getFlow() + " which is not enabled.");
			return list(req, res, user, uriParser, FLOW_DISABLED_VALIDATION_ERROR);

		}catch(FlowInstanceNoLongerAvailableException e){

			log.info("User " + user + " requested flow instance " + e.getFlowInstance() + " which is no longer available.");
			return list(req, res, user, uriParser, FLOW_INSTANCE_NO_LONGER_AVAILABLE_VALIDATION_ERROR);

		}catch(FlowEngineException e){

			log.error("Unable to get flow instance manager for flowID " + flowID + " and flowInstanceID " + flowInstanceID + " requested by user " + user, e);
			return list(req, res, user, uriParser, ERROR_GETTING_FLOW_INSTANCE_MANAGER_VALIDATION_ERROR);
		}

		try{

			return processFlowRequest(instanceManager, this, UPDATE_ACCESS_CONTROLLER, req, res, user, uriParser, true);

		}catch(FlowInstanceManagerClosedException e){

			log.info("User " + user + " requested flow instance manager for flow instance " + e.getFlowInstance() + " which has already been closed. Removing flow instance manager from session.");

			removeMutableFlowInstanceManagerFromSession(instanceManager, req.getSession(false));

			return list(req, res, user, uriParser, FLOW_INSTANCE_MANAGER_CLOSED_VALIDATION_ERROR);

		}catch(QueryInstanceHTMLException e){

			return processFlowRequestException(instanceManager, req, res, user, uriParser, e);

		}catch(RuntimeException e){

			return processFlowRequestException(instanceManager, req, res, user, uriParser, e);
		}
	}

	@WebPublic(alias = "search")
	public ForegroundModuleResponse search(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws ModuleConfigurationException, SQLException, AccessDeniedException, IOException, FlowDefaultStatusNotFound, EvaluationException {

		flowInstanceIndexer.search(req, res, user);

		return null;
	}

	@WebPublic(alias = "externalattachment")
	public ForegroundModuleResponse getExternalMessageAttachment(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws ModuleConfigurationException, SQLException, AccessDeniedException, IOException, FlowDefaultStatusNotFound, EvaluationException, URINotFoundException {

		return externalMessageCRUD.getRequestedMessageAttachment(req, res, user, uriParser, GENERAL_ACCESS_CONTROLLER);
	}

	@WebPublic(alias = "internalattachment")
	public ForegroundModuleResponse getInternalMessageAttachment(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws ModuleConfigurationException, SQLException, AccessDeniedException, IOException, FlowDefaultStatusNotFound, EvaluationException, URINotFoundException {

		return internalMessageCRUD.getRequestedMessageAttachment(req, res, user, uriParser, GENERAL_ACCESS_CONTROLLER);
	}

	protected List<Integer> getUserFlowIDs(User user) throws SQLException {

		String sql;

		if(!CollectionUtils.isEmpty(user.getGroups())){

			sql = FLOW_MANAGER_SQL + " OR ffg.groupID IN (?" + StringUtils.repeatString(", ?", user.getGroups().size() - 1) + "))";

		}else{

			sql = FLOW_MANAGER_SQL + ")";
		}

		ArrayListQuery<Integer> query = new ArrayListQuery<Integer>(dataSource, sql, IntegerPopulator.getPopulator());

		query.setInt(1, user.getUserID());

		if(!CollectionUtils.isEmpty(user.getGroups())){

			int paramPosition = 2;

			for(Group group : user.getGroups()){

				query.setInt(paramPosition++, group.getGroupID());
			}
		}

		return query.executeQuery();
	}

	private List<FlowInstance> getFlowInstanceBookmarks(User user, List<Integer> flowIDs) throws SQLException {

		String sql = FLOW_INSTANCE_BOOKMARKS_SQL + StringUtils.toCommaSeparatedString(flowIDs) + ")";

		LowLevelQuery<FlowInstance> query = new LowLevelQuery<FlowInstance>(sql);

		query.addParameter(user.getUserID());

		query.addRelations(FlowInstance.FLOW_RELATION, FlowInstance.FLOW_STATE_RELATION, FlowInstance.MANAGERS_RELATION, FlowInstance.EVENTS_RELATION);

		return this.daoFactory.getFlowInstanceDAO().getAll(query);
	}

	protected List<FlowInstance> getActiveFlowInstances(User user, List<Integer> flowIDs) throws SQLException {

		String sql = ACTIVE_FLOWS.replace("$flowIDs", StringUtils.toCommaSeparatedString(flowIDs));

		LowLevelQuery<FlowInstance> query = new LowLevelQuery<FlowInstance>(sql);

		query.addRelations(FlowInstance.FLOW_RELATION, FlowInstance.FLOW_STATE_RELATION, FlowInstance.MANAGERS_RELATION, FlowInstance.EVENTS_RELATION);

		return this.daoFactory.getFlowInstanceDAO().getAll(query);
	}

	@Override
	public String getSubmitActionID() {

		return null;
	}

	@Override
	public String getSaveActionID() {

		return null;
	}

	@Override
	public String getPaymentActionID() {

		return null;
	}

	@Override
	public String getMultiSigningActionID() {

		return null;
	}

	@Override
	public void appendFormData(Document doc, Element baseElement, MutableFlowInstanceManager instanceManager, User user) {

		appendBookmark(doc, baseElement, (FlowInstance)instanceManager.getFlowInstance(), user);
	}

	public void appendBookmark(Document doc, Element baseElement, FlowInstance flowInstance, User user) {

		try{
			if(getBookmark(user, flowInstance) != null){

				XMLUtils.appendNewElement(doc, baseElement, "Bookmarked");
			}

		}catch(SQLException e){

			log.error("Error getting ookmark for user " + user + " and flow instance " + flowInstance, e);
		}
	}

	@Override
	public void systemStarted() {

		this.flowInstanceIndexer.cacheFlowInstances();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void processEvent(CRUDEvent<?> event, EventSource source) {

		log.debug("Received crud event regarding " + event.getAction() + " of " + event.getBeans().size() + " beans with " + event.getBeanClass());

		if(FlowFamily.class.isAssignableFrom(event.getBeanClass())){

			switch(event.getAction()){

				case ADD:
					flowInstanceIndexer.addFlowFamilies((List<FlowFamily>)event.getBeans());
					break;

				case UPDATE:
					flowInstanceIndexer.updateFlowFamilies((List<FlowFamily>)event.getBeans());
					break;

				case DELETE:
					flowInstanceIndexer.deleteFlowFamilies((List<FlowFamily>)event.getBeans());
					break;
			}

		}else if(Flow.class.isAssignableFrom(event.getBeanClass())){

			switch(event.getAction()){

				case ADD:
					flowInstanceIndexer.addFlows((List<Flow>)event.getBeans());
					break;

				case UPDATE:
					flowInstanceIndexer.updateFlows((List<Flow>)event.getBeans());
					break;

				case DELETE:
					flowInstanceIndexer.deleteFlows((List<Flow>)event.getBeans());
					break;
			}

		}else if(FlowInstance.class.isAssignableFrom(event.getBeanClass())){

			switch(event.getAction()){

				case ADD:
					flowInstanceIndexer.addFlowInstances((List<FlowInstance>)event.getBeans());
					break;

				case UPDATE:
					flowInstanceIndexer.updateFlowInstances((List<FlowInstance>)event.getBeans());
					break;

				case DELETE:
					flowInstanceIndexer.deleteFlowInstances((List<FlowInstance>)event.getBeans());
					break;
			}

		}else{

			log.warn("Received CRUD event for unsupported class " + event.getBeanClass());
		}
	}

	@Override
	protected void redirectToSubmitMethod(MutableFlowInstanceManager flowInstance, HttpServletRequest req, HttpServletResponse res) throws IOException {

		redirectToMethod(req, res, "/submitted/" + flowInstance.getFlowInstanceID());
	}

	@Override
	protected void onFlowInstanceClosedRedirect(FlowInstanceManager flowInstanceManager, HttpServletRequest req, HttpServletResponse res) throws IOException {

		redirectToMethod(req, res, "/overview/" + flowInstanceManager.getFlowInstanceID());

	}

	@WebPublic(alias = "submitted")
	public ForegroundModuleResponse showSubmittedMessage(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws FlowInstanceManagerClosedException, UnableToGetQueryInstanceShowHTMLException, AccessDeniedException, ModuleConfigurationException, SQLException{

		return super.showImmutableFlowInstance(req, res, user, uriParser, GENERAL_ACCESS_CONTROLLER, this, ShowMode.SUBMIT);
	}

	@WebPublic(alias = "pdf")
	public ForegroundModuleResponse getEventPDF(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, SQLException, IOException, AccessDeniedException {

		sendEventPDF(req, res, user, uriParser, GENERAL_ACCESS_CONTROLLER, pdfProvider);

		return null;
	}

	@WebPublic(alias = "xml")
	public ForegroundModuleResponse getEventXML(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, SQLException, IOException, AccessDeniedException {

		sendEventXML(req, res, user, uriParser, GENERAL_ACCESS_CONTROLLER, xmlProvider);

		return null;
	}

	@Override
	protected String getBaseUpdateURL(HttpServletRequest req, URIParser uriParser, User user, ImmutableFlowInstance flowInstance, FlowInstanceAccessController accessController) {

		if (!accessController.isMutable(flowInstance, user)) {

			return null;
		}

		return getModuleURI(req) + "/flowinstance/" + flowInstance.getFlow().getFlowID() + "/" + flowInstance.getFlowInstanceID();
	}

	@Override
	protected String getEventPDFLink(FlowInstanceManager instanceManager, ImmutableFlowInstanceEvent event, HttpServletRequest req, User user) {

		if(event.getAttributeHandler().getPrimitiveBoolean("pdf")){

			return this.getModuleURI(req) + "/pdf/" + instanceManager.getFlowInstanceID() + "/" + event.getEventID();
		}

		return null;
	}

	@Override
	public String getSignFailURL(MutableFlowInstanceManager instanceManager, HttpServletRequest req) {

		return RequestUtils.getFullContextPathURL(req) + this.getFullAlias() + "/flowinstance/" + instanceManager.getFlowID() + "/" + instanceManager.getFlowInstanceID() + "?preview=1&signprovidererror=1";
	}

	@Override
	public String getSigningURL(MutableFlowInstanceManager instanceManager, HttpServletRequest req) {

		return RequestUtils.getFullContextPathURL(req) + this.getFullAlias() + "/flowinstance/" + instanceManager.getFlowID() + "/" + instanceManager.getFlowInstanceID() + "?save-submit=1";
	}

	@Override
	public int getPriority() {

		return 0;
	}
}
