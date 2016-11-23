package com.nordicpeak.flowengine;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.annotations.InstanceManagerDependency;
import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.beans.Breadcrumb;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.EventTarget;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.exceptions.ModuleConfigurationException;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.openhierarchy.foregroundmodules.siteprofile.interfaces.SiteProfile;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.QueryOperators;
import se.unlogic.standardutils.dao.QueryParameterFactory;
import se.unlogic.standardutils.date.DateUtils;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;

import com.nordicpeak.flowengine.accesscontrollers.SessionAccessController;
import com.nordicpeak.flowengine.accesscontrollers.UserFlowInstanceAccessController;
import com.nordicpeak.flowengine.beans.ExternalMessage;
import com.nordicpeak.flowengine.beans.ExternalMessageAttachment;
import com.nordicpeak.flowengine.beans.Flow;
import com.nordicpeak.flowengine.beans.FlowInstance;
import com.nordicpeak.flowengine.beans.FlowInstanceEvent;
import com.nordicpeak.flowengine.beans.Status;
import com.nordicpeak.flowengine.beans.UserFlowInstanceBrowserProcessCallback;
import com.nordicpeak.flowengine.cruds.ExternalMessageCRUD;
import com.nordicpeak.flowengine.enums.ContentType;
import com.nordicpeak.flowengine.enums.EventType;
import com.nordicpeak.flowengine.enums.SenderType;
import com.nordicpeak.flowengine.enums.ShowMode;
import com.nordicpeak.flowengine.events.ExternalMessageAddedEvent;
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
import com.nordicpeak.flowengine.interfaces.MultiSigningProvider;
import com.nordicpeak.flowengine.interfaces.PDFProvider;
import com.nordicpeak.flowengine.interfaces.PaymentProvider;
import com.nordicpeak.flowengine.interfaces.SigningProvider;
import com.nordicpeak.flowengine.interfaces.XMLProvider;
import com.nordicpeak.flowengine.managers.FlowInstanceManager;
import com.nordicpeak.flowengine.managers.MutableFlowInstanceManager;

public class UserFlowInstanceModule extends BaseFlowBrowserModule {

	protected static final Field [] FLOW_INSTANCE_OVERVIEW_RELATIONS = { FlowInstance.EXTERNAL_MESSAGES_RELATION, ExternalMessage.ATTACHMENTS_RELATION, FlowInstance.FLOW_RELATION, FlowInstance.FLOW_STATE_RELATION, FlowInstance.EVENTS_RELATION, FlowInstanceEvent.ATTRIBUTES_RELATION, FlowInstance.MANAGERS_RELATION, Flow.FLOW_FAMILY_RELATION};

	public static final String SESSION_ACCESS_CONTROLLER_TAG = UserFlowInstanceModule.class.getName();

	public static final String SUBMIT_COMPLETION_ACTION_ID = UserFlowInstanceModule.class.getName() + ".submitcompletion";

	public static final UserFlowInstanceAccessController UPDATE_ACCESS_CONTROLLER = new UserFlowInstanceAccessController(true, false);
	public static final UserFlowInstanceAccessController DELETE_ACCESS_CONTROLLER = new UserFlowInstanceAccessController(false, true);
	public static final UserFlowInstanceAccessController PREVIEW_ACCESS_CONTROLLER = new UserFlowInstanceAccessController(false, false);

	private QueryParameterFactory<FlowInstance, User> flowInstancePosterParamFactory;
	private QueryParameterFactory<FlowInstanceEvent, FlowInstance> flowInstanceEventFlowInstanceParamFactory;
	private QueryParameterFactory<FlowInstanceEvent, User> flowInstanceEventPosterParamFactory;
	private QueryParameterFactory<FlowInstanceEvent, Timestamp> flowInstanceEventAddedParamFactory;

	@InstanceManagerDependency
	protected PDFProvider pdfProvider;

	@InstanceManagerDependency
	protected SigningProvider signingProvider;

	@InstanceManagerDependency
	protected MultiSigningProvider multiSigningProvider;

	@InstanceManagerDependency
	protected PaymentProvider paymentProvider;

	@InstanceManagerDependency
	protected XMLProvider xmlProvider;

	private FlowProcessCallback defaultFlowProcessCallback;

	private FlowProcessCallback completeFlowProcessCallback;

	private ExternalMessageCRUD externalMessageCRUD;

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		super.createDAOs(dataSource);

		flowInstancePosterParamFactory = daoFactory.getFlowInstanceDAO().getParamFactory("poster", User.class);
		flowInstanceEventFlowInstanceParamFactory = daoFactory.getFlowInstanceEventDAO().getParamFactory("flowInstance", FlowInstance.class);
		flowInstanceEventPosterParamFactory = daoFactory.getFlowInstanceEventDAO().getParamFactory("poster", User.class);
		flowInstanceEventAddedParamFactory = daoFactory.getFlowInstanceEventDAO().getParamFactory("added", Timestamp.class);

		defaultFlowProcessCallback = new UserFlowInstanceBrowserProcessCallback(FlowBrowserModule.SAVE_ACTION_ID, FlowBrowserModule.SUBMIT_ACTION_ID, FlowBrowserModule.MULTI_SIGNING_ACTION_ID, FlowBrowserModule.PAYMENT_ACTION_ID, this);
		completeFlowProcessCallback = new UserFlowInstanceBrowserProcessCallback(null, SUBMIT_COMPLETION_ACTION_ID, null, null, this);

		externalMessageCRUD = new ExternalMessageCRUD(daoFactory.getExternalMessageDAO(), daoFactory.getExternalMessageAttachmentDAO(), this);
	}

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		if(!systemInterface.getInstanceHandler().addInstance(UserFlowInstanceModule.class, this)){

			throw new RuntimeException("Unable to register module in global instance handler using key " + UserFlowInstanceModule.class.getSimpleName() + ", another instance is already registered using this key.");
		}

	}

	@Override
	public void unload() throws Exception {

		super.unload();

		if(this.equals(systemInterface.getInstanceHandler().getInstance(UserFlowInstanceModule.class))){

			systemInterface.getInstanceHandler().removeInstance(UserFlowInstanceModule.class);
		}

	}

	@Override
	public ForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return this.list(req, res, user, uriParser, (List<ValidationError>)null);
	}

	public ForegroundModuleResponse list(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, ValidationError validationError) throws ModuleConfigurationException, SQLException {

		return list(req, res, user, uriParser, CollectionUtils.getGenericSingletonList(validationError));
	}

	@Override
	public ForegroundModuleResponse list(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, List<ValidationError> validationErrors) throws ModuleConfigurationException, SQLException {

		log.info("User " + user + " listing its flow instances");

		Document doc = this.createDocument(req, uriParser, user);

		Element listFlowInstancesElement = doc.createElement("ListFlowInstances");

		doc.getDocumentElement().appendChild(listFlowInstancesElement);

		List<FlowInstance> flowInstances = this.getFlowInstances(user, true);

		if(!CollectionUtils.isEmpty(flowInstances)) {

			Element savedFlowInstancesElement = XMLUtils.appendNewElement(doc, listFlowInstancesElement, "SavedFlowInstances");
			Element submittedFlowInstancesElement = XMLUtils.appendNewElement(doc, listFlowInstancesElement, "SubmittedFlowInstances");
			Element archivedFlowInstancesElement = XMLUtils.appendNewElement(doc, listFlowInstancesElement, "ArchivedFlowInstances");

			for(FlowInstance flowInstance : flowInstances) {

				Status status = flowInstance.getStatus();

				if((status.getContentType() == ContentType.NEW || status.getContentType() == ContentType.WAITING_FOR_MULTISIGN || status.getContentType() == ContentType.WAITING_FOR_PAYMENT)) {

					savedFlowInstancesElement.appendChild(flowInstance.toXML(doc));

				} else if(status.getContentType() == ContentType.SUBMITTED || status.getContentType() == ContentType.IN_PROGRESS || status.getContentType() == ContentType.WAITING_FOR_COMPLETION) {

					Element flowInstanceElement = flowInstance.toXML(doc);

					List<FlowInstanceEvent> events = this.getNewFlowInstanceEvents(flowInstance, user);

					if(events != null) {

						for(FlowInstanceEvent event : events) {
							event.setShortDate(DateUtils.dateAndShortMonthToString(new Date(event.getAdded().getTime()), new Locale(systemInterface.getDefaultLanguage().getLanguageCode())));
						}

						XMLUtils.append(doc, flowInstanceElement, "newEvents", events);

					}

					submittedFlowInstancesElement.appendChild(flowInstanceElement);

				} else if(status.getContentType() == ContentType.ARCHIVED) {

					archivedFlowInstancesElement.appendChild(flowInstance.toXML(doc));

				} else {

					log.warn("The status of flow instance " + flowInstance + " has an unknown content type of " + status.getContentType());
				}

			}

			req.getSession().removeAttribute("LastFlowInstanceEventUpdate");

		}

		if(validationErrors != null){

			XMLUtils.append(doc, listFlowInstancesElement, validationErrors);
		}

		return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb());
	}

	@WebPublic(alias = "overview")
	public ForegroundModuleResponse showFlowInstanceOverview(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws ModuleConfigurationException, SQLException, AccessDeniedException, IOException  {

		FlowInstance flowInstance;

		if (uriParser.size() == 4 && NumberUtils.isInt(uriParser.get(3)) && (flowInstance = getFlowInstance(Integer.valueOf(uriParser.get(3)), CollectionUtils.getList(ExternalMessageAttachment.DATA_FIELD), FLOW_INSTANCE_OVERVIEW_RELATIONS)) != null) {

			PREVIEW_ACCESS_CONTROLLER.checkFlowInstanceAccess(flowInstance, user);

			Document doc = this.createDocument(req, uriParser, user);

			Element showFlowInstanceOverviewElement = doc.createElement("ShowFlowInstanceOverview");

			doc.getDocumentElement().appendChild(showFlowInstanceOverviewElement);

			if(req.getMethod().equalsIgnoreCase("POST")) {

				ExternalMessage externalMessage = externalMessageCRUD.add(req, res, uriParser, user, doc, showFlowInstanceOverviewElement, flowInstance);

				if(externalMessage != null) {

					FlowInstanceEvent flowInstanceEvent = this.addFlowInstanceEvent(flowInstance, EventType.CUSTOMER_MESSAGE_SENT, null, user);

					systemInterface.getEventHandler().sendEvent(FlowInstance.class, new ExternalMessageAddedEvent(flowInstance, flowInstanceEvent, moduleDescriptor, getCurrentSiteProfile(req, user, uriParser), externalMessage, SenderType.USER), EventTarget.ALL);

					res.sendRedirect(req.getContextPath() + uriParser.getFormattedURI() + "#new-message");

					return null;

				}

			}

			showFlowInstanceOverviewElement.appendChild(flowInstance.toXML(doc));

			if(user != null) {
				showFlowInstanceOverviewElement.appendChild(user.toXML(doc));
			}

			return new SimpleForegroundModuleResponse(doc, flowInstance.getFlow().getName(), this.getDefaultBreadcrumb());
		}

		return list(req, res, user, uriParser, FLOW_INSTANCE_NOT_FOUND_VALIDATION_ERROR);

	}

	@WebPublic(alias = "flowinstance")
	public ForegroundModuleResponse processFlowRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, AccessDeniedException, ModuleConfigurationException, SQLException, IOException, FlowDefaultStatusNotFound, EvaluationException {

		Integer flowID = null;
		Integer flowInstanceID = null;
		MutableFlowInstanceManager instanceManager;

		try{
			if(uriParser.size() == 4 && (flowID = NumberUtils.toInt(uriParser.get(2))) != null &&  (flowInstanceID = NumberUtils.toInt(uriParser.get(3))) != null){

				//Get saved instance from DB or session
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

		} catch (FlowInstanceNoLongerAvailableException e) {

			log.info("User " + user + " requested flow instance " + e.getFlowInstance() + " which is no longer available.");
			return list(req, res, user, uriParser, FLOW_INSTANCE_NO_LONGER_AVAILABLE_VALIDATION_ERROR);

		} catch (FlowEngineException e) {

			log.error("Unable to get flow instance manager for flowID " + flowID + " and flowInstanceID " + flowInstanceID + " requested by user " + user,e);
			return list(req, res, user, uriParser, ERROR_GETTING_FLOW_INSTANCE_MANAGER_VALIDATION_ERROR);
		}


		try {

			if(instanceManager.getFlowInstance().getStatus().getContentType().equals(ContentType.WAITING_FOR_COMPLETION)) {

				return processFlowRequest(instanceManager, completeFlowProcessCallback, UPDATE_ACCESS_CONTROLLER, req, res, user, uriParser, true);

			}

			return processFlowRequest(instanceManager, defaultFlowProcessCallback, UPDATE_ACCESS_CONTROLLER, req, res, user, uriParser, true);

		} catch (FlowInstanceManagerClosedException e) {

			log.info("User " + user + " requested flow instance manager for flow instance " + e.getFlowInstance() + " which has already been closed. Removing flow instance manager from session.");

			removeMutableFlowInstanceManagerFromSession(instanceManager, req.getSession(false));

			redirectToMethod(req, res, "/flowinstance/" + flowID + "/" + flowInstanceID);

			return null;

		} catch (QueryInstanceHTMLException e) {

			return processFlowRequestException(instanceManager, req, res, user, uriParser, e);

		}catch (RuntimeException e){

			return processFlowRequestException(instanceManager, req, res, user, uriParser, e);
		}
	}

	@WebPublic(alias = "delete")
	public ForegroundModuleResponse deleteFlowInstance(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, SQLException, AccessDeniedException, IOException {

		Integer flowInstanceID = null;

		if(uriParser.size() == 3 && (flowInstanceID = NumberUtils.toInt(uriParser.get(2))) != null &&  deleteFlowInstance(flowInstanceID, DELETE_ACCESS_CONTROLLER, user) != null) {

			this.redirectToDefaultMethod(req, res);

			return null;
		}

		throw new URINotFoundException(uriParser);
	}

	@WebPublic(alias = "externalattachment")
	public ForegroundModuleResponse getExternalMessageAttachment(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws ModuleConfigurationException, SQLException, AccessDeniedException, IOException, FlowDefaultStatusNotFound, EvaluationException, URINotFoundException {

		return externalMessageCRUD.getRequestedMessageAttachment(req, res, user, uriParser, PREVIEW_ACCESS_CONTROLLER);
	}

	@WebPublic(alias = "mquery")
	public ForegroundModuleResponse processMutableQueryRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws ModuleConfigurationException, SQLException, AccessDeniedException, IOException, FlowDefaultStatusNotFound, EvaluationException, URINotFoundException, QueryRequestException, QueryProviderException, EvaluationProviderException, InvalidFlowInstanceStepException, MissingQueryInstanceDescriptor, DuplicateFlowInstanceManagerIDException {

		return processMutableQueryRequest(req, res, user, uriParser, UPDATE_ACCESS_CONTROLLER, true, true);
	}

	@WebPublic(alias = "iquery")
	public ForegroundModuleResponse processImmutableQueryRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws ModuleConfigurationException, SQLException, AccessDeniedException, IOException, FlowDefaultStatusNotFound, EvaluationException, URINotFoundException, QueryRequestException, QueryProviderException, EvaluationProviderException, InvalidFlowInstanceStepException, MissingQueryInstanceDescriptor, DuplicateFlowInstanceManagerIDException {

		return processImmutableQueryRequest(req, res, user, uriParser, PREVIEW_ACCESS_CONTROLLER, true);
	}

	@Override
	public Breadcrumb getFlowBreadcrumb(ImmutableFlowInstance flowInstance) {

		return new Breadcrumb(this, flowInstance.getFlow().getName(), "/flowinstance/" + flowInstance.getFlow().getFlowID() + "/" + flowInstance.getFlowInstanceID());
	}

	protected List<FlowInstance> getFlowInstances(User user, boolean getEvents) throws SQLException {

		HighLevelQuery<FlowInstance> query = new HighLevelQuery<FlowInstance>();

		query.addRelations(FlowInstance.FLOW_RELATION, FlowInstance.FLOW_STATE_RELATION);

		if(getEvents) {

			query.addRelation(FlowInstance.EVENTS_RELATION);

		}

		query.addParameter(flowInstancePosterParamFactory.getParameter(user));

		return daoFactory.getFlowInstanceDAO().getAll(query);
	}

	protected List<FlowInstanceEvent> getNewFlowInstanceEvents(FlowInstance flowInstance, User user) throws SQLException {

		HighLevelQuery<FlowInstanceEvent> query = new HighLevelQuery<FlowInstanceEvent>();

		query.addParameter(flowInstanceEventFlowInstanceParamFactory.getParameter(flowInstance));
		query.addParameter(flowInstanceEventPosterParamFactory.getParameter(user, QueryOperators.NOT_EQUALS));

		if(user.getLastLogin() != null){
			query.addParameter(flowInstanceEventAddedParamFactory.getParameter(user.getLastLogin(), QueryOperators.BIGGER_THAN));
		}


		return daoFactory.getFlowInstanceEventDAO().getAll(query);

	}

	@WebPublic(alias = "submitted")
	public ForegroundModuleResponse showSubmittedMessage(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws FlowInstanceManagerClosedException, UnableToGetQueryInstanceShowHTMLException, AccessDeniedException, ModuleConfigurationException, SQLException, URINotFoundException{

		try {
			return super.showImmutableFlowInstance(req, res, user, uriParser, new SessionAccessController(req.getSession(), SESSION_ACCESS_CONTROLLER_TAG), this.defaultFlowProcessCallback, ShowMode.SUBMIT);

		} catch (AccessDeniedException e) {

			throw new URINotFoundException(uriParser);
		}
	}

	@WebPublic(alias = "multisign")
	public ForegroundModuleResponse showMultiSignMessage(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws FlowInstanceManagerClosedException, UnableToGetQueryInstanceShowHTMLException, AccessDeniedException, ModuleConfigurationException, SQLException, URINotFoundException {

		return super.showMultiSignMessage(req, res, user, uriParser, PREVIEW_ACCESS_CONTROLLER, this.defaultFlowProcessCallback);
	}

	@WebPublic(alias = "pay")
	public ForegroundModuleResponse showPaymentForm(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws FlowInstanceManagerClosedException, UnableToGetQueryInstanceShowHTMLException, AccessDeniedException, ModuleConfigurationException, SQLException, URINotFoundException {

		return super.showPaymentForm(req, res, user, uriParser, PREVIEW_ACCESS_CONTROLLER, this.defaultFlowProcessCallback);
	}

	@Override
	protected void redirectToSubmitMethod(MutableFlowInstanceManager flowInstanceManager, HttpServletRequest req, HttpServletResponse res) throws IOException {

		HttpSession session = req.getSession();

		if(session != null){

			try {
				SessionAccessController.setSessionAttribute(flowInstanceManager.getFlowInstanceID(), session, SESSION_ACCESS_CONTROLLER_TAG);
			} catch (IllegalStateException e) {}
		}

		redirectToMethod(req, res, "/submitted/" + flowInstanceManager.getFlowInstanceID());
	}

	@Override
	protected void onFlowInstanceClosedRedirect(FlowInstanceManager flowInstanceManager, HttpServletRequest req, HttpServletResponse res) throws IOException {

		redirectToMethod(req, res, "/overview/" + flowInstanceManager.getFlowID() + "/" + flowInstanceManager.getFlowInstanceID());

	}

	@WebPublic(alias = "preview")
	public ForegroundModuleResponse showPreview(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws FlowInstanceManagerClosedException, UnableToGetQueryInstanceShowHTMLException, AccessDeniedException, ModuleConfigurationException, SQLException{

		return super.showImmutableFlowInstance(req, res, user, uriParser, PREVIEW_ACCESS_CONTROLLER, defaultFlowProcessCallback, ShowMode.PREVIEW);
	}

	@Override
	protected Breadcrumb getFlowInstancePreviewBreadcrumb(ImmutableFlowInstance flowInstance, HttpServletRequest req, URIParser uriParser) {

		//TODO add prefix
		return new Breadcrumb(this, flowInstance.getFlow().getName(), "/preview/" + flowInstance.getFlowInstanceID());
	}

	@Override
	protected Breadcrumb getFlowInstanceSubmitBreadcrumb(ImmutableFlowInstance flowInstance, HttpServletRequest req, URIParser uriParser) {

		//TODO add prefix
		return new Breadcrumb(this, flowInstance.getFlow().getName(), "/submit/" + flowInstance.getFlowInstanceID());
	}

	@WebPublic(alias = "pdf")
	public ForegroundModuleResponse getEventPDF(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, SQLException, IOException, AccessDeniedException {

		sendEventPDF(req, res, user, uriParser, PREVIEW_ACCESS_CONTROLLER, pdfProvider);

		return null;
	}

	@WebPublic(alias = "xml")
	public ForegroundModuleResponse getEventXML(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, SQLException, IOException, AccessDeniedException {

		sendEventXML(req, res, user, uriParser, PREVIEW_ACCESS_CONTROLLER, xmlProvider);

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
	protected SigningProvider getSigningProvider() {

		return signingProvider;
	}

	@Override
	protected MultiSigningProvider getMultiSigningProvider() {

		return multiSigningProvider;
	}

	@Override
	protected PaymentProvider getPaymentProvider() {

		return paymentProvider;
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

		if(event.getAttributeHandler().getPrimitiveBoolean("pdf")){

			return this.getModuleURI(req) + "/pdf/" + instanceManager.getFlowInstanceID() + "/" + event.getEventID();
		}

		return null;
	}

	@Override
	public boolean requiresMultiSigning(FlowInstanceManager instanceManager) {

		if(instanceManager.getFlowState().getContentType() == ContentType.WAITING_FOR_COMPLETION) {

			return false;
		}

		return super.requiresMultiSigning(instanceManager);
	}

	@Override
	public boolean requiresPayment(FlowInstanceManager instanceManager) {

		if(instanceManager.getFlowState().getContentType() == ContentType.WAITING_FOR_COMPLETION) {

			return false;
		}

		return super.requiresPayment(instanceManager);
	}
}
