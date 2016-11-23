package com.nordicpeak.flowengine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.xml.transform.TransformerException;

import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.fileuploadutils.MultipartRequest;
import se.unlogic.hierarchy.core.annotations.InstanceManagerDependency;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.beans.Breadcrumb;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.CRUDAction;
import se.unlogic.hierarchy.core.enums.EventTarget;
import se.unlogic.hierarchy.core.events.CRUDEvent;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.exceptions.ModuleConfigurationException;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.interfaces.EventHandler;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.SystemInterface;
import se.unlogic.hierarchy.core.interfaces.ViewFragment;
import se.unlogic.hierarchy.core.utils.ViewFragmentUtils;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.openhierarchy.foregroundmodules.siteprofile.interfaces.SiteProfile;
import se.unlogic.openhierarchy.foregroundmodules.siteprofile.interfaces.SiteProfileHandler;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.collections.ReverseListIterator;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.QueryParameterFactory;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.io.BinarySizes;
import se.unlogic.standardutils.io.FileUtils;
import se.unlogic.standardutils.json.JsonObject;
import se.unlogic.standardutils.mime.MimeUtils;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.streams.StreamUtils;
import se.unlogic.standardutils.time.TimeUtils;
import se.unlogic.standardutils.validation.PositiveStringIntegerValidator;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.HTTPUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.SessionUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.http.enums.ContentDisposition;

import com.nordicpeak.flowengine.beans.DefaultInstanceMetadata;
import com.nordicpeak.flowengine.beans.DefaultStatusMapping;
import com.nordicpeak.flowengine.beans.Flow;
import com.nordicpeak.flowengine.beans.FlowFamily;
import com.nordicpeak.flowengine.beans.FlowInstance;
import com.nordicpeak.flowengine.beans.FlowInstanceEvent;
import com.nordicpeak.flowengine.beans.FlowType;
import com.nordicpeak.flowengine.beans.QueryDescriptor;
import com.nordicpeak.flowengine.beans.QueryInstanceDescriptor;
import com.nordicpeak.flowengine.beans.QueryResponse;
import com.nordicpeak.flowengine.beans.Status;
import com.nordicpeak.flowengine.beans.Step;
import com.nordicpeak.flowengine.dao.FlowEngineDAOFactory;
import com.nordicpeak.flowengine.enums.ContentType;
import com.nordicpeak.flowengine.enums.EventType;
import com.nordicpeak.flowengine.enums.FlowAction;
import com.nordicpeak.flowengine.enums.FlowDirection;
import com.nordicpeak.flowengine.enums.QueryState;
import com.nordicpeak.flowengine.enums.ShowMode;
import com.nordicpeak.flowengine.events.SigningEvent;
import com.nordicpeak.flowengine.events.SubmitEvent;
import com.nordicpeak.flowengine.exceptions.FlowEngineException;
import com.nordicpeak.flowengine.exceptions.evaluation.EvaluationException;
import com.nordicpeak.flowengine.exceptions.evaluationprovider.EvaluationProviderErrorException;
import com.nordicpeak.flowengine.exceptions.evaluationprovider.EvaluationProviderException;
import com.nordicpeak.flowengine.exceptions.evaluationprovider.EvaluationProviderNotFoundException;
import com.nordicpeak.flowengine.exceptions.evaluationprovider.EvaluatorNotFoundInEvaluationProviderException;
import com.nordicpeak.flowengine.exceptions.flow.FlowDefaultStatusNotFound;
import com.nordicpeak.flowengine.exceptions.flow.FlowDisabledException;
import com.nordicpeak.flowengine.exceptions.flow.FlowException;
import com.nordicpeak.flowengine.exceptions.flow.FlowLimitExceededException;
import com.nordicpeak.flowengine.exceptions.flow.FlowNoLongerAvailableException;
import com.nordicpeak.flowengine.exceptions.flow.FlowNotPublishedException;
import com.nordicpeak.flowengine.exceptions.flowinstance.FlowInstanceNoLongerAvailableException;
import com.nordicpeak.flowengine.exceptions.flowinstance.InvalidFlowInstanceStepException;
import com.nordicpeak.flowengine.exceptions.flowinstance.MissingQueryInstanceDescriptor;
import com.nordicpeak.flowengine.exceptions.flowinstancemanager.DuplicateFlowInstanceManagerIDException;
import com.nordicpeak.flowengine.exceptions.flowinstancemanager.FlowInstanceManagerClosedException;
import com.nordicpeak.flowengine.exceptions.flowinstancemanager.QueryInstanceNotFoundInFlowInstanceManagerException;
import com.nordicpeak.flowengine.exceptions.queryinstance.QueryModificationException;
import com.nordicpeak.flowengine.exceptions.queryinstance.QueryRequestException;
import com.nordicpeak.flowengine.exceptions.queryinstance.QueryRequestsNotSupported;
import com.nordicpeak.flowengine.exceptions.queryinstance.UnableToGetQueryInstanceFormHTMLException;
import com.nordicpeak.flowengine.exceptions.queryinstance.UnableToGetQueryInstanceShowHTMLException;
import com.nordicpeak.flowengine.exceptions.queryinstance.UnableToPopulateQueryInstanceException;
import com.nordicpeak.flowengine.exceptions.queryinstance.UnableToResetQueryInstanceException;
import com.nordicpeak.flowengine.exceptions.queryinstance.UnableToSaveQueryInstanceException;
import com.nordicpeak.flowengine.exceptions.queryprovider.QueryInstanceNotFoundInQueryProviderException;
import com.nordicpeak.flowengine.exceptions.queryprovider.QueryProviderErrorException;
import com.nordicpeak.flowengine.exceptions.queryprovider.QueryProviderException;
import com.nordicpeak.flowengine.exceptions.queryprovider.QueryProviderNotFoundException;
import com.nordicpeak.flowengine.interfaces.EvaluationHandler;
import com.nordicpeak.flowengine.interfaces.FlowEngineInterface;
import com.nordicpeak.flowengine.interfaces.FlowInstanceAccessController;
import com.nordicpeak.flowengine.interfaces.FlowProcessCallback;
import com.nordicpeak.flowengine.interfaces.FlowSubmitSurveyProvider;
import com.nordicpeak.flowengine.interfaces.ImmutableFlow;
import com.nordicpeak.flowengine.interfaces.ImmutableFlowInstance;
import com.nordicpeak.flowengine.interfaces.ImmutableFlowInstanceEvent;
import com.nordicpeak.flowengine.interfaces.ImmutableQueryInstance;
import com.nordicpeak.flowengine.interfaces.InstanceMetadata;
import com.nordicpeak.flowengine.interfaces.InvoiceLine;
import com.nordicpeak.flowengine.interfaces.MultiSigningProvider;
import com.nordicpeak.flowengine.interfaces.MultiSigningQuery;
import com.nordicpeak.flowengine.interfaces.OperatingStatus;
import com.nordicpeak.flowengine.interfaces.PDFProvider;
import com.nordicpeak.flowengine.interfaces.PaymentProvider;
import com.nordicpeak.flowengine.interfaces.PaymentQuery;
import com.nordicpeak.flowengine.interfaces.QueryHandler;
import com.nordicpeak.flowengine.interfaces.QueryRequestProcessor;
import com.nordicpeak.flowengine.interfaces.SigningCallback;
import com.nordicpeak.flowengine.interfaces.SigningProvider;
import com.nordicpeak.flowengine.interfaces.XMLProvider;
import com.nordicpeak.flowengine.managers.FlowInstanceManager;
import com.nordicpeak.flowengine.managers.ImmutableFlowInstanceManager;
import com.nordicpeak.flowengine.managers.ManagerResponse;
import com.nordicpeak.flowengine.managers.MutableFlowInstanceManager;
import com.nordicpeak.flowengine.managers.MutableFlowInstanceManager.FlowInstanceManagerRegistery;
import com.nordicpeak.flowengine.validationerrors.FileUploadValidationError;

public abstract class BaseFlowModule extends AnnotatedForegroundModule implements FlowEngineInterface {

	public static final Field[] FLOW_INSTANCE_RELATIONS = {FlowInstance.EVENTS_RELATION, FlowInstanceEvent.ATTRIBUTES_RELATION, FlowInstance.FLOW_RELATION, FlowInstance.FLOW_STATE_RELATION, Flow.FLOW_TYPE_RELATION, Flow.FLOW_FAMILY_RELATION, FlowType.ALLOWED_GROUPS_RELATION, FlowType.ALLOWED_USERS_RELATION, Flow.STEPS_RELATION, Flow.FLOW_FAMILY_RELATION, FlowFamily.MANAGER_GROUPS_RELATION, FlowFamily.MANAGER_USERS_RELATION, Step.QUERY_DESCRIPTORS_RELATION, QueryDescriptor.EVALUATOR_DESCRIPTORS_RELATION, Flow.DEFAULT_FLOW_STATE_MAPPINGS_RELATION, DefaultStatusMapping.FLOW_STATE_RELATION, QueryDescriptor.QUERY_INSTANCE_DESCRIPTORS_RELATION, FlowInstance.ATTRIBUTES_RELATION};
	
	public static final ValidationError PREVIEW_NOT_ENABLED_VALIDATION_ERROR = new ValidationError("PreviewNotEnabledForCurrentFlow");
	public static final ValidationError PREVIEW_ONLY_WHEN_FULLY_POPULATED_VALIDATION_ERROR = new ValidationError("PreviewOnlyAvailableWhenFlowFullyPopulated");
	public static final ValidationError SUBMIT_ONLY_WHEN_FULLY_POPULATED_VALIDATION_ERROR = new ValidationError("SubmitOnlyAvailableWhenFlowFullyPopulated");

	public static final ValidationError UNABLE_TO_POPULATE_QUERY_INSTANCE_VALIDATION_ERROR = new ValidationError("UnableToPopulateQueryInstance");
	public static final ValidationError UNABLE_TO_RESET_QUERY_INSTANCE_VALIDATION_ERROR = new ValidationError("UnableToResetQueryInstance");
	public static final ValidationError UNABLE_TO_SAVE_QUERY_INSTANCE_VALIDATION_ERROR = new ValidationError("UnableToSaveQueryInstance");

	public static final ValidationError FLOW_INSTANCE_NOT_FOUND_VALIDATION_ERROR = new ValidationError("RequestedFlowInstanceNotFound");
	public static final ValidationError INVALID_LINK_VALIDATION_ERROR = new ValidationError("InvalidLinkRequested");
	public static final ValidationError FLOW_DISABLED_VALIDATION_ERROR = new ValidationError("FlowDisabled");

	public static final ValidationError FLOW_INSTANCE_PREVIEW_VALIDATION_ERROR = new ValidationError("FlowInstancePreviewError");
	public static final ValidationError UNABLE_TO_LOAD_FLOW_INSTANCE_VALIDATION_ERROR = new ValidationError("FlowInstanceLoadError");

	public static final ValidationError SIGNING_PROVIDER_NOT_FOUND_VALIDATION_ERROR = new ValidationError("SigningProviderNotFoundError");
	public static final ValidationError PAYMENT_PROVIDER_NOT_FOUND_VALIDATION_ERROR = new ValidationError("PaymentProviderNotFoundError");
	public static final ValidationError MULTI_SIGNING_PROVIDER_NOT_FOUND_VALIDATION_ERROR = new ValidationError("MultiSigningProviderNotFoundError");

	@ModuleSetting(allowsNull = true)
	@TextFieldSettingDescriptor(name = "Temp dir", description = "Directory for temporary files. Should be on the same filesystem as the file store for best performance. If not set system default temp directory will be used")
	protected String tempDir;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Max request size", description = "Maxmium allowed request size in megabytes", required = true, formatValidator = PositiveStringIntegerValidator.class)
	protected Integer maxRequestSize = 1000;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "RAM threshold", description = "Maximum size of files in KB to be buffered in RAM during file uploads. Files exceeding the threshold are written to disk instead.", required = true, formatValidator = PositiveStringIntegerValidator.class)
	protected Integer ramThreshold = 500;

	@InstanceManagerDependency(required = true)
	protected QueryHandler queryHandler;

	@InstanceManagerDependency(required = true)
	protected EvaluationHandler evaluationHandler;

	@InstanceManagerDependency
	protected SiteProfileHandler profileHandler;

	@InstanceManagerDependency
	protected OperatingMessageModule operatingMessageModule;
	
	protected EventHandler eventHandler;

	protected FlowEngineDAOFactory daoFactory;

	protected QueryParameterFactory<Flow, Integer> flowIDParamFactory;
	protected QueryParameterFactory<FlowInstance, Integer> flowInstanceIDParamFactory;
	protected QueryParameterFactory<QueryInstanceDescriptor, Integer> queryInstanceDescriptorFlowInstanceIDParamFactory;

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		super.createDAOs(dataSource);

		this.daoFactory = new FlowEngineDAOFactory(dataSource, systemInterface.getUserHandler(), systemInterface.getGroupHandler());
		flowIDParamFactory = daoFactory.getFlowDAO().getParamFactory("flowID", Integer.class);
		flowInstanceIDParamFactory = daoFactory.getFlowInstanceDAO().getParamFactory("flowInstanceID", Integer.class);
		queryInstanceDescriptorFlowInstanceIDParamFactory = daoFactory.getQueryInstanceDescriptorDAO().getParamFactory("flowInstanceID", Integer.class);

		eventHandler = systemInterface.getEventHandler();
	}

	protected MutableFlowInstanceManager getSavedMutableFlowInstanceManager(int flowID, int flowInstanceID, FlowInstanceAccessController callback, HttpSession session, User user, URIParser uriParser, HttpServletRequest req, boolean loadFromDBIfNeeded, boolean checkPublishDate, boolean checkEnabled) throws FlowNoLongerAvailableException, SQLException, FlowInstanceNoLongerAvailableException, AccessDeniedException, FlowNotPublishedException, FlowDisabledException, DuplicateFlowInstanceManagerIDException, MissingQueryInstanceDescriptor, QueryProviderNotFoundException, InvalidFlowInstanceStepException, QueryProviderErrorException, QueryInstanceNotFoundInQueryProviderException, FlowDisabledException, EvaluationProviderNotFoundException, EvaluationProviderErrorException, EvaluatorNotFoundInEvaluationProviderException {

		if (session == null) {

			throw new RuntimeException("Session cannot be null");
		}

		synchronized (session) {

			//TODO check if the status has changed since this instance was opened!

			// Check if the user already has an instance of this flow open in
			// his session
			MutableFlowInstanceManager instanceManager = getMutableFlowInstanceManagerFromSession(flowID, flowInstanceID, session);

			if (instanceManager != null) {

				checkFlow(instanceManager, session, checkPublishDate, checkEnabled);

				FlowInstance dbFlowInstance;

				// Check if the flow instance still exists in DB
				if ((dbFlowInstance = this.getFlowInstance(instanceManager.getFlowInstanceID(), null, FlowInstance.FLOW_RELATION, FlowInstance.FLOW_STATE_RELATION, Flow.FLOW_TYPE_RELATION, Flow.FLOW_FAMILY_RELATION, FlowFamily.MANAGER_GROUPS_RELATION, FlowFamily.MANAGER_USERS_RELATION)) == null) {

					this.removeMutableFlowInstanceManagerFromSession(instanceManager, session);

					throw new FlowInstanceNoLongerAvailableException(instanceManager.getFlowInstance());
				}

				// Check the status of flow instance if it is still user mutable etc...
				try {
					callback.checkFlowInstanceAccess(dbFlowInstance, user);

				} catch (AccessDeniedException e) {

					this.removeMutableFlowInstanceManagerFromSession(instanceManager, session);
					throw e;
				}

				if (dbFlowInstance.getUpdated() != null && (instanceManager.getFlowInstance().getUpdated() == null || instanceManager.getFlowInstance().getUpdated().before(dbFlowInstance.getUpdated()))) {

					instanceManager.setConcurrentModificationLock(true);
				}

				if (log.isDebugEnabled()) {

					log.debug("Found flow instance " + instanceManager.getFlowInstance() + " in session of user " + user);
				}

				return instanceManager;

			} else if (!loadFromDBIfNeeded) {

				return null;
			}

			// User does not have the requested flow instance open, get flow instance from DB and create a new instance manager
			FlowInstance flowInstance = getFlowInstance(flowInstanceID);

			if (flowInstance == null) {

				return null;
			}

			callback.checkFlowInstanceAccess(flowInstance, user);

			if (checkEnabled && (!flowInstance.getFlow().isEnabled() || isOperatingStatusDisabled(flowInstance.getFlow()))) {

				throw new FlowDisabledException(flowInstance.getFlow());
			}

			if (checkPublishDate && !flowInstance.getFlow().isPublished()) {

				throw new FlowNotPublishedException(flowInstance.getFlow());
			}

			log.info("Opening copy of flow instance " + flowInstance + " for user " + user);

			InstanceMetadata instanceMetadata = new DefaultInstanceMetadata(getCurrentSiteProfile(req, user, uriParser));

			// TODO handle IllegalStateException's from session object
			instanceManager = new MutableFlowInstanceManager(flowInstance, queryHandler, evaluationHandler, getNewInstanceManagerID(user), req, user, instanceMetadata);

			// TODO handle IllegalStateException's from session object
			addMutableFlowInstanceManagerToSession(flowID, flowInstanceID, instanceManager, session);

			return instanceManager;
		}
	}

	protected MutableFlowInstanceManager getUnsavedMutableFlowInstanceManager(int flowID, FlowInstanceAccessController callback, HttpSession session, User user, URIParser uriParser, HttpServletRequest req, boolean createInstanceIfNeeded, boolean checkPublishDate, boolean checkEnabled) throws FlowNoLongerAvailableException, SQLException, AccessDeniedException, FlowNotPublishedException, FlowDisabledException, DuplicateFlowInstanceManagerIDException, QueryProviderNotFoundException, QueryProviderErrorException, QueryInstanceNotFoundInQueryProviderException, FlowDisabledException, EvaluationProviderNotFoundException, EvaluationProviderErrorException, EvaluatorNotFoundInEvaluationProviderException, FlowLimitExceededException {

		if (session == null) {

			throw new RuntimeException("Session cannot be null");
		}

		synchronized (session) {

			// Check if the user already has an instance of this flow open in
			// his session
			MutableFlowInstanceManager instanceManager = (MutableFlowInstanceManager) session.getAttribute(Constants.FLOW_INSTANCE_SESSION_PREFIX + flowID + ":" + null);

			if (instanceManager != null) {

				checkFlow(instanceManager, session, checkPublishDate, checkEnabled);

				log.debug("Found existing instance of flow " + instanceManager.getFlowInstance().getFlow() + " in session of user " + user);

				return instanceManager;

			} else if (!createInstanceIfNeeded) {

				return null;
			}

			// User has no instance open of the requested flow, get flow from DB
			// and create a new instance manager
			Flow flow = getFlow(flowID);

			if (flow == null || !flow.isInternal()) {

				return null;
			}

			checkFlowLimit(user, flow);

			callback.checkNewFlowInstanceAccess(flow, user);

			if (checkEnabled && (!flow.isEnabled() || isOperatingStatusDisabled(flow))) {

				throw new FlowDisabledException(flow);
			}

			if (checkPublishDate && !flow.isPublished()) {

				throw new FlowNotPublishedException(flow);
			}

			log.info("Creating new instance of flow " + flow + " for user " + user);

			InstanceMetadata instanceMetadata = new DefaultInstanceMetadata(getCurrentSiteProfile(req, user, uriParser));

			// TODO handle IllegalStateException's from session object
			instanceManager = new MutableFlowInstanceManager(flow, queryHandler, evaluationHandler, getNewInstanceManagerID(user), req, user, instanceMetadata);

			// TODO handle IllegalStateException's from session object
			session.setAttribute(Constants.FLOW_INSTANCE_SESSION_PREFIX + flowID + ":" + null, instanceManager);

			return instanceManager;
		}
	}

	protected void checkFlowLimit(User user, Flow flow) throws FlowLimitExceededException, SQLException {

	}

	protected void checkFlow(MutableFlowInstanceManager instanceManager, HttpSession session, boolean checkPublishDate, boolean checkEnabled) throws FlowNoLongerAvailableException, SQLException, FlowDisabledException, FlowNotPublishedException {

		Flow flow;

		// Check if the flow still exists in DB!
		if ((flow = getBareFlow(instanceManager.getFlowID())) == null) {

			this.removeMutableFlowInstanceManagerFromSession(instanceManager, session);

			throw new FlowNoLongerAvailableException(instanceManager.getFlowInstance().getFlow());
		}

		if (checkEnabled && (!flow.isEnabled() || isOperatingStatusDisabled(instanceManager.getFlowInstance().getFlow()))) {

			this.removeMutableFlowInstanceManagerFromSession(instanceManager, session);

			throw new FlowDisabledException(flow);
		}

		if (checkPublishDate && !flow.isPublished()) {

			this.removeMutableFlowInstanceManagerFromSession(instanceManager, session);

			throw new FlowNotPublishedException(instanceManager.getFlowInstance().getFlow());
		}
	}
	
	public boolean isOperatingStatusDisabled(ImmutableFlow flow) {
		
		if(operatingMessageModule != null) {
			
			OperatingStatus operatingStatus = operatingMessageModule.getOperatingStatus(flow.getFlowFamily().getFlowFamilyID());
			
			if(operatingStatus != null && operatingStatus.isDisabled()) {
				
				return true;
			}
		}
		
		return false;
	}

	public ImmutableFlowInstanceManager getImmutableFlowInstanceManager(int flowInstanceID, FlowInstanceAccessController accessController, User user, boolean checkEnabled, HttpServletRequest req) throws AccessDeniedException, SQLException, FlowDisabledException, DuplicateFlowInstanceManagerIDException, MissingQueryInstanceDescriptor, QueryProviderNotFoundException, InvalidFlowInstanceStepException, QueryProviderErrorException, QueryInstanceNotFoundInQueryProviderException {

		// User does not have the requested flow instance open, get flow
		// instance from DB and create a new instance manager
		FlowInstance flowInstance = getFlowInstance(flowInstanceID);

		if (flowInstance == null) {

			return null;
		}

		accessController.checkFlowInstanceAccess(flowInstance, user);

		if (checkEnabled && (!flowInstance.getFlow().isEnabled() || isOperatingStatusDisabled(flowInstance.getFlow()))) {

			throw new FlowDisabledException(flowInstance.getFlow());
		}

		return new ImmutableFlowInstanceManager(flowInstance, queryHandler, req, new DefaultInstanceMetadata(getCurrentSiteProfile(req, user, null)));
	}

	public static void addMutableFlowInstanceManagerToSession(int flowID, Integer flowInstanceID, MutableFlowInstanceManager instanceManager, HttpSession session) {

		session.setAttribute(Constants.FLOW_INSTANCE_SESSION_PREFIX + flowID + ":" + flowInstanceID, instanceManager);
	}

	public static MutableFlowInstanceManager getMutableFlowInstanceManagerFromSession(int flowID, Integer flowInstanceID, HttpSession session) {

		return (MutableFlowInstanceManager) session.getAttribute(Constants.FLOW_INSTANCE_SESSION_PREFIX + flowID + ":" + flowInstanceID);
	}

	public void removeMutableFlowInstanceManagerFromSession(MutableFlowInstanceManager instanceManager, HttpSession session) {

		removeFlowInstanceManagerFromSession(instanceManager.getFlowID(), instanceManager.getFlowInstanceID(), session);

		if (!instanceManager.isClosed()) {

			instanceManager.close(this.getQueryHandler());
		}
	}

	public static void removeFlowInstanceManagerFromSession(int flowID, Integer flowInstanceID, HttpSession session) {

		if (session == null) {

			return;
		}

		SessionUtils.removeAttribute(Constants.FLOW_INSTANCE_SESSION_PREFIX + flowID + ":" + flowInstanceID, session);
	}

	public ForegroundModuleResponse processFlowRequest(MutableFlowInstanceManager instanceManager, FlowProcessCallback callback, FlowInstanceAccessController accessController, HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, boolean enableSaving) throws UnableToGetQueryInstanceFormHTMLException, SQLException, IOException, UnableToGetQueryInstanceShowHTMLException, ModuleConfigurationException, FlowInstanceManagerClosedException, FlowDefaultStatusNotFound, EvaluationException {

		MultipartRequest multipartRequest = null;

		try {
			if (MultipartRequest.isMultipartRequest(req)) {

				log.debug("Parsing multipart request from user " + user + " for flow instance " + instanceManager.getFlowInstance());
				multipartRequest = new MultipartRequest(this.ramThreshold * BinarySizes.KiloByte, this.maxRequestSize * BinarySizes.MegaByte, tempDir, req);
				req = multipartRequest;
			}

			FlowAction flowAction = parseAction(req);

			synchronized (instanceManager) {

				ManagerResponse managerResponse = null;

				if (req.getMethod().equals("POST")) {

					if (flowAction == null) {

						Integer queryID = NumberUtils.toInt(req.getParameter("queryID"));

						if (queryID != null) {

							log.info("User " + user + " populating single query with ID " + queryID + " in flow instance " + instanceManager);

							String response = null;

							try {
								response = instanceManager.populateQueryInCurrentStep(req, user, queryID, queryHandler, evaluationHandler);
							} catch (QueryModificationException e) {
								log.error("Error populating queryID " + queryID + " in flow instance " + instanceManager, e);
							}

							if (response != null) {

								res.setHeader("AjaxPostValid", "true");

							} else {

								response = new JsonObject().toJson();

							}

							HTTPUtils.sendReponse(response, "application/json;charset=" + systemInterface.getEncoding(), systemInterface.getEncoding(), res);

							return null;
						}
					}

					FlowDirection flowDirection = parseFlowDirection(req, flowAction);

					managerResponse = instanceManager.populateCurrentStep(req, user, flowDirection, queryHandler, evaluationHandler, getMutableQueryRequestBaseURL(req, instanceManager));

					if (managerResponse.hasValidationErrors()) {

						// Show form for current step
						log.info("Validation errors detected in POST from user " + user + " for flow instance " + instanceManager);
						return showCurrentStepForm(instanceManager, callback, req, res, user, uriParser, managerResponse, null, flowAction);
					}
				}

				if (instanceManager.isConcurrentModificationLocked()) {

					if (flowAction == FlowAction.SAVE_AND_CLOSE || flowAction == FlowAction.SAVE_AND_SUBMIT || flowAction == FlowAction.SAVE) {

						flowAction = null;

					} else if (flowAction == FlowAction.SAVE_AND_PREVIEW) {

						flowAction = FlowAction.PREVIEW;
					}
				}

				if (flowAction == null) {

					Integer stepID = NumberUtils.toInt(req.getParameter("step"));

					if (stepID != null) {

						if (instanceManager.setStep(stepID)) {

							log.info("User " + user + " changing step in flow instance " + instanceManager + " to step " + instanceManager.getCurrentStep());

						} else {

							log.info("Invalid step change requested by user " + user + " for flow instance " + instanceManager + " which is in step " + instanceManager.getCurrentStep());
						}
					}

					// Show form for current step
					return showCurrentStepForm(instanceManager, callback, req, res, user, uriParser, managerResponse, null, null);

				} else if (flowAction == FlowAction.CLOSE_AND_REOPEN) {

					log.info("User " + user + " closing and reopening flow instance " + instanceManager.getFlowInstance());

					try {
						instanceManager.close(queryHandler);
					} catch (Exception e) {

						log.error("Error closing flow instance " + instanceManager.getFlowInstance() + " in session of user " + user, e);
					}

					removeMutableFlowInstanceManagerFromSession(instanceManager, req.getSession());

					reOpenFlowInstance(instanceManager.getFlowID(), instanceManager.getFlowInstanceID(), req, user, uriParser);

					res.sendRedirect(uriParser.getRequestURL());

					return null;

				} else if (flowAction == FlowAction.SAVE) {

					if (user != null) {

						boolean previouslySaved = instanceManager.isPreviouslySaved();

						save(instanceManager, user, req, callback.getSaveActionID(), EventType.UPDATED);

						// Check if we need to redirect to new url
						if (!previouslySaved && enableSaving) {

							res.sendRedirect(uriParser.getRequestURL() + "/" + instanceManager.getFlowInstanceID() + "?saved=1");
							return null;
						}
					}

					return showCurrentStepForm(instanceManager, callback, req, res, user, uriParser, managerResponse, null, flowAction);

				} else if (flowAction == FlowAction.PREVIEW || flowAction == FlowAction.SAVE_AND_PREVIEW) {

					if (flowAction == FlowAction.SAVE_AND_PREVIEW && user != null) {

						boolean previouslySaved = instanceManager.isPreviouslySaved();

						save(instanceManager, user, req, callback.getSaveActionID(), EventType.UPDATED);

						// Check if we need to redirect to new url
						if (!previouslySaved) {

							if (enableSaving) {

								res.sendRedirect(uriParser.getRequestURL() + "/" + instanceManager.getFlowInstanceID() + "?preview=1");

							} else {

								res.sendRedirect(uriParser.getRequestURL() + "?preview=1");
							}

							return null;
						}
					}

					// Check if preview is enabled for the flow
					if (!instanceManager.getFlowInstance().getFlow().usesPreview()) {

						// Show form for current step
						log.info("Preview denied for user " + user + " requesting flow instance " + instanceManager.getFlowInstance() + ", flow does NOT have preview enabled");
						return showCurrentStepForm(instanceManager, callback, req, res, user, uriParser, managerResponse, PREVIEW_NOT_ENABLED_VALIDATION_ERROR, flowAction);

					} else if (!instanceManager.isFullyPopulated()) {

						log.info("Preview denied for user " + user + " requesting flow instance " + instanceManager.getFlowInstance() + ", instance is NOT fully populated");
						return showCurrentStepForm(instanceManager, callback, req, res, user, uriParser, managerResponse, PREVIEW_ONLY_WHEN_FULLY_POPULATED_VALIDATION_ERROR, flowAction);
					}

					return showPreview(req, user, uriParser, instanceManager, callback, flowAction, getBaseUpdateURL(req, uriParser, user, instanceManager.getFlowInstance(), accessController), null);

				} else if (flowAction == FlowAction.SAVE_AND_SUBMIT) {

					// Check if instance is fully populated, save and then
					// display submitted message
					if (!instanceManager.isFullyPopulated()) {

						log.info("Save & submit denied for user " + user + " requesting flow instance " + instanceManager.getFlowInstance() + ", instance is NOT fully populated");
						return showCurrentStepForm(instanceManager, callback, req, res, user, uriParser, managerResponse, SUBMIT_ONLY_WHEN_FULLY_POPULATED_VALIDATION_ERROR, flowAction);
					}

					if (enableSaving && instanceManager.getFlowInstance().getFlow().requiresSigning()) {

						SigningProvider signingProvider = getSigningProvider();

						if (signingProvider == null) {

							if (instanceManager.getFlowInstance().getFlow().usesPreview()) {

								return showPreview(req, user, uriParser, instanceManager, callback, flowAction, getBaseUpdateURL(req, uriParser, user, instanceManager.getFlowInstance(), accessController), SIGNING_PROVIDER_NOT_FOUND_VALIDATION_ERROR);

							} else {

								return showCurrentStepForm(instanceManager, callback, req, res, user, uriParser, managerResponse, SIGNING_PROVIDER_NOT_FOUND_VALIDATION_ERROR, flowAction);
							}
						}

						boolean previouslySaved = instanceManager.isPreviouslySaved();
						boolean savedDuringCurrentRequest;

						if (instanceManager.hasUnsavedChanges()) {

							log.info("User " + user + " saving and preparing to sign flow instance " + instanceManager.getFlowInstance());

							save(instanceManager, user, req, callback.getSaveActionID(), EventType.UPDATED);
							savedDuringCurrentRequest = true;

						} else {

							log.info("User " + user + " preparing to sign flow instance " + instanceManager.getFlowInstance());

							savedDuringCurrentRequest = false;
						}

						if (!previouslySaved) {

							res.sendRedirect(getSigningURL(instanceManager, req));

							return null;
						}

						try {

							SigningCallback signingCallback;

							if (requiresMultiSigning(instanceManager)) {

								signingCallback = getSigningCallback(instanceManager, null, callback.getMultiSigningActionID(), getCurrentSiteProfile(req, user, uriParser), false);

							} else if (requiresPayment(instanceManager)) {

								signingCallback = getSigningCallback(instanceManager, null, callback.getPaymentActionID(), getCurrentSiteProfile(req, user, uriParser), false);

							} else {

								signingCallback = getSigningCallback(instanceManager, EventType.SUBMITTED, callback.getSubmitActionID(), getCurrentSiteProfile(req, user, uriParser), true);
							}

							ViewFragment viewFragment = signingProvider.sign(req, res, user, instanceManager, signingCallback, savedDuringCurrentRequest);

							if (res.isCommitted()) {

								return null;

							} else if (viewFragment == null) {

								log.warn("Signing provider returned no view fragment and not committed not direct response for signing of flow instance " + instanceManager + " by user " + user);

								redirectToSignError(req, res, uriParser, instanceManager, previouslySaved);

								return null;
							}

							return showSigningForm(instanceManager, req, res, user, uriParser, viewFragment);

						} catch (Exception e) {

							log.error("Error ivoking signing provider " + signingProvider + " for flow instance " + instanceManager + " requested by user " + user, e);

							redirectToSignError(req, res, uriParser, instanceManager, previouslySaved);

							return null;
						}

					} else if (enableSaving && requiresPayment(instanceManager)) {

						PaymentProvider paymentProvider = getPaymentProvider();

						try {

							if (paymentProvider == null) {

								if (instanceManager.getFlowInstance().getFlow().usesPreview()) {

									return showPreview(req, user, uriParser, instanceManager, callback, flowAction, getBaseUpdateURL(req, uriParser, user, instanceManager.getFlowInstance(), accessController), PAYMENT_PROVIDER_NOT_FOUND_VALIDATION_ERROR);

								} else {

									return showCurrentStepForm(instanceManager, callback, req, res, user, uriParser, managerResponse, PAYMENT_PROVIDER_NOT_FOUND_VALIDATION_ERROR, flowAction);
								}
							}

							ViewFragment viewFragment = paymentProvider.pay(req, res, user, instanceManager, new BaseFlowModuleInlinePaymentCallback(this, callback.getSubmitActionID()));

							if (res.isCommitted()) {

								return null;

							} else if (viewFragment == null) {

								log.warn("Payment provider returned no view fragment and not committed not direct response for pay of flow instance " + instanceManager + " by user " + user);

								redirectToPaymentError(multipartRequest, res, uriParser, instanceManager, instanceManager.isPreviouslySaved());

								return null;
							}

							return showInlinePaymentForm(instanceManager, req, res, user, uriParser, viewFragment);

						} catch (Exception e) {

							log.error("Error ivoking payment provider " + paymentProvider + " for flow instance " + instanceManager + " requested by user " + user, e);

							redirectToPaymentError(multipartRequest, res, uriParser, instanceManager, instanceManager.isPreviouslySaved());

							return null;
						}

					}

					log.info("User " + user + " saving and submitting flow instance " + instanceManager.getFlowInstance());

					FlowInstanceEvent event = save(instanceManager, user, req, callback.getSubmitActionID(), EventType.SUBMITTED);

					if (enableSaving) {

						sendSubmitEvent(instanceManager, event, callback.getSubmitActionID(), getCurrentSiteProfile(req, user, uriParser));
					}

					removeFlowInstanceManagerFromSession(instanceManager.getFlowID(), instanceManager.getFlowInstanceID(), req.getSession(false));

					closeSubmittedFlowInstanceManager(instanceManager, req);

					redirectToSubmitMethod(instanceManager, req, res);

					return null;

				} else if (flowAction == FlowAction.SAVE_AND_CLOSE && user != null) {

					log.info("User " + user + " saving and closing flow instance " + instanceManager.getFlowInstance());

					save(instanceManager, user, req, callback.getSaveActionID(), EventType.UPDATED);

					removeMutableFlowInstanceManagerFromSession(instanceManager, req.getSession(false));

					onFlowInstanceClosedRedirect(instanceManager, req, res);

					return null;

				} else {

					throw new RuntimeException("Unhandled " + FlowAction.class.getSimpleName() + " enum value " + flowAction);
				}
			}

		} catch (FileUploadException e) {

			if (!(e instanceof FileSizeLimitExceededException) && !(e instanceof SizeLimitExceededException)) {

				log.warn("Unable to parse request for flow instance " + instanceManager + " from user " + user, e);
			}

			return showCurrentStepForm(instanceManager, callback, req, res, user, uriParser, null, new FileUploadValidationError(this.maxRequestSize), null);

		} catch (UnableToPopulateQueryInstanceException e) {

			log.error("Error populating flow instance " + instanceManager + " from user " + user, e);

			return showCurrentStepForm(instanceManager, callback, req, res, user, uriParser, null, UNABLE_TO_POPULATE_QUERY_INSTANCE_VALIDATION_ERROR, null);

		} catch (UnableToResetQueryInstanceException e) {

			log.error("Error populating flow instance " + instanceManager + " from user " + user, e);

			return showCurrentStepForm(instanceManager, callback, req, res, user, uriParser, null, UNABLE_TO_RESET_QUERY_INSTANCE_VALIDATION_ERROR, null);

		} catch (UnableToSaveQueryInstanceException e) {

			log.error("Unable to save flow instance " + instanceManager + " from user " + user, e);

			return showCurrentStepForm(instanceManager, callback, req, res, user, uriParser, null, UNABLE_TO_SAVE_QUERY_INSTANCE_VALIDATION_ERROR, null);

		} finally {

			if (multipartRequest != null) {

				multipartRequest.deleteFiles();
			}
		}
	}

	protected void reOpenFlowInstance(Integer flowID, Integer flowInstanceID, HttpServletRequest req, User user, URIParser uriParser) {

	}

	protected void closeSubmittedFlowInstanceManager(MutableFlowInstanceManager instanceManager, HttpServletRequest req) {

		instanceManager.close(queryHandler);
	}

	protected SigningCallback getSigningCallback(MutableFlowInstanceManager instanceManager, EventType eventType, String actionID, SiteProfile siteProfile, boolean addSubmitEvent) {

		return new BaseFlowModuleSigningCallback(this, actionID, eventType, siteProfile, addSubmitEvent);
	}

	protected void redirectToSignError(HttpServletRequest req, HttpServletResponse res, URIParser uriParser, MutableFlowInstanceManager instanceManager, boolean previouslySaved) throws IOException {

		if (!previouslySaved) {

			res.sendRedirect(uriParser.getRequestURL() + "/" + instanceManager.getFlowInstanceID() + "?preview=1&signprovidererror=1");

		} else {

			res.sendRedirect(uriParser.getRequestURL() + "?preview=1&signprovidererror=1");
		}
	}

	protected void redirectToPaymentError(HttpServletRequest req, HttpServletResponse res, URIParser uriParser, MutableFlowInstanceManager instanceManager, boolean previouslySaved) throws IOException {

		if (!previouslySaved) {

			res.sendRedirect(uriParser.getRequestURL() + "/" + instanceManager.getFlowInstanceID() + "?preview=1&paymentprovidererror=1");

		} else {

			res.sendRedirect(uriParser.getRequestURL() + "?preview=1&paymentprovidererror=1");
		}
	}

	protected SigningProvider getSigningProvider() {

		return null;
	}

	protected PaymentProvider getPaymentProvider() {

		return null;
	}

	protected MultiSigningProvider getMultiSigningProvider() {

		return null;
	}

	protected abstract void redirectToSubmitMethod(MutableFlowInstanceManager instanceManager, HttpServletRequest req, HttpServletResponse res) throws IOException;

	protected abstract void onFlowInstanceClosedRedirect(FlowInstanceManager instanceManager, HttpServletRequest req, HttpServletResponse res) throws IOException;

	protected SiteProfile getCurrentSiteProfile(HttpServletRequest req, User user, URIParser uriParser) {

		if (this.profileHandler != null) {

			return profileHandler.getCurrentProfile(user, req, uriParser);
		}

		return null;
	}

	protected void processQueryRequest(FlowInstanceManager instanceManager, int queryID, HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws QueryInstanceNotFoundInFlowInstanceManagerException, QueryRequestsNotSupported, QueryRequestException {

		ImmutableQueryInstance queryInstance;

		QueryRequestProcessor queryRequestProcessor;

		synchronized (instanceManager) {

			queryInstance = instanceManager.getQueryInstance(queryID);

			if (queryInstance == null) {

				//TODO change hierarchy for this exception
				throw new QueryInstanceNotFoundInFlowInstanceManagerException(queryID, instanceManager.getFlowInstance());
			}

			try {
				queryRequestProcessor = queryInstance.getQueryRequestProcessor(req, user, queryHandler);

			} catch (Exception e) {

				throw new QueryRequestException(queryInstance.getQueryInstanceDescriptor(), e);
			}

			if (queryRequestProcessor == null) {

				throw new QueryRequestsNotSupported(queryInstance.getQueryInstanceDescriptor());
			}
		}

		try {
			queryRequestProcessor.processRequest(req, res, user, uriParser);

		} catch (Exception e) {

			throw new QueryRequestException(queryInstance.getQueryInstanceDescriptor(), e);

		} finally {

			if (queryRequestProcessor != null) {

				try {
					queryRequestProcessor.close();

				} catch (Exception e) {

					log.error("Error closing query request processor from query " + queryInstance.getQueryInstanceDescriptor() + " in flow instance " + instanceManager.getFlowInstance() + " requested by user " + user, e);
				}
			}
		}
	}

	protected abstract String getBaseUpdateURL(HttpServletRequest req, URIParser uriParser, User user, ImmutableFlowInstance flowInstance, FlowInstanceAccessController accessController);

	protected String getMutableQueryRequestBaseURL(HttpServletRequest req, MutableFlowInstanceManager instanceManager) {

		String baseURL = req.getContextPath() + this.getFullAlias() + "/mquery/" + instanceManager.getFlowID();

		if (instanceManager.isPreviouslySaved()) {

			baseURL += "/" + instanceManager.getFlowInstanceID() + "/q/";

		} else {

			baseURL += "/q/";
		}

		return baseURL;
	}

	protected String getImmutableQueryRequestBaseURL(HttpServletRequest req, FlowInstanceManager instanceManager) {

		return req.getContextPath() + this.getFullAlias() + "/iquery/" + instanceManager.getFlowID() + "/" + instanceManager.getFlowInstanceID() + "/q/";
	}

	protected FlowInstanceEvent save(MutableFlowInstanceManager instanceManager, User user, HttpServletRequest req, String actionID, EventType eventType) throws FlowInstanceManagerClosedException, UnableToSaveQueryInstanceException, SQLException, FlowDefaultStatusNotFound {

		HttpSession session = null;

		if (req != null) {
			session = req.getSession(false);
		}

		boolean wasPreviouslySaved = instanceManager.isPreviouslySaved();

		log.info("User " + user + " saving flow instance " + instanceManager.getFlowInstance());

		setFlowStatus(instanceManager, actionID);

		if (!wasPreviouslySaved) {

			SiteProfile siteProfile = getCurrentSiteProfile(req, user, null);

			if (siteProfile != null) {

				FlowInstance flowInstance = (FlowInstance) instanceManager.getFlowInstance();

				flowInstance.setProfileID(siteProfile.getProfileID());

			}

		}

		instanceManager.saveInstance(this, user);

		CRUDAction crudAction;

		if (!wasPreviouslySaved) {

			rebindFlowInstance(session, instanceManager);

			crudAction = CRUDAction.ADD;

		} else {

			crudAction = CRUDAction.UPDATE;
		}

		FlowInstanceEvent event = null;

		if (!instanceManager.getFlowInstance().getStatus().getContentType().equals(ContentType.NEW)) {

			event = addFlowInstanceEvent(instanceManager.getFlowInstance(), eventType, null, user);
		}

		eventHandler.sendEvent(FlowInstance.class, new CRUDEvent<FlowInstance>(crudAction, (FlowInstance) instanceManager.getFlowInstance()), EventTarget.ALL);

		return event;
	}

	protected void setFlowStatus(MutableFlowInstanceManager instanceManager, String actionID) throws FlowDefaultStatusNotFound {

		if (actionID != null) {

			instanceManager.setFlowState((Status) instanceManager.getFlowInstance().getFlow().getDefaultState(actionID));

			if (instanceManager.getFlowState() == null) {

				throw new FlowDefaultStatusNotFound(actionID, instanceManager.getFlowInstance().getFlow());
			}

		}
	}

	protected void rebindFlowInstance(HttpSession session, MutableFlowInstanceManager instanceManager) {

		if (session == null) {

			return;
		}

		FlowInstanceManagerRegistery registery = FlowInstanceManagerRegistery.getInstance();

		try {
			registery.addNonSessionBoundInstance(instanceManager);
			SessionUtils.removeAttribute(Constants.FLOW_INSTANCE_SESSION_PREFIX + instanceManager.getFlowID() + ":" + null, session);
			SessionUtils.setAttribute(Constants.FLOW_INSTANCE_SESSION_PREFIX + instanceManager.getFlowID() + ":" + instanceManager.getFlowInstanceID(), instanceManager, session);
		} finally {
			registery.removeNonSessionBoundInstance(instanceManager);
		}
	}

	protected FlowDirection parseFlowDirection(HttpServletRequest req, FlowAction flowAction) {

		if (flowAction == null) {

			if (req.getParameter("forward") != null) {

				return FlowDirection.FORWARD;

			} else if (req.getParameter("back") != null) {

				return FlowDirection.BACKWARD;
			}
		}

		return FlowDirection.STAY_PUT;
	}

	protected FlowAction parseAction(HttpServletRequest req) {

		if (req.getParameter("save") != null) {

			return FlowAction.SAVE;

		} else if (req.getParameter("save-submit") != null) {

			return FlowAction.SAVE_AND_SUBMIT;

		} else if (req.getParameter("preview") != null) {

			return FlowAction.PREVIEW;

		} else if (req.getParameter("save-preview") != null) {

			return FlowAction.SAVE_AND_PREVIEW;

		} else if (req.getParameter("save-close") != null) {

			return FlowAction.SAVE_AND_CLOSE;

		} else if (req.getParameter("close-reopen") != null) {

			return FlowAction.CLOSE_AND_REOPEN;
		}

		return null;
	}

	protected ForegroundModuleResponse showCurrentStepForm(MutableFlowInstanceManager instanceManager, FlowProcessCallback callback, HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, ManagerResponse managerResponse, ValidationError validationError, FlowAction lastFlowAction) throws FlowInstanceManagerClosedException, UnableToGetQueryInstanceFormHTMLException {

		log.info("User " + user + " requested form for step " + (instanceManager.getCurrentStepIndex() + 1) + ". " + instanceManager.getCurrentStep() + " in flow instance " + instanceManager.getFlowInstance());

		if (managerResponse == null) {

			managerResponse = instanceManager.getCurrentStepFormHTML(queryHandler, req, user, getMutableQueryRequestBaseURL(req, instanceManager));
		}

		Document doc = createDocument(req, uriParser, user);
		Element flowInstanceManagerFormElement = doc.createElement("FlowInstanceManagerForm");
		doc.getDocumentElement().appendChild(flowInstanceManagerFormElement);

		flowInstanceManagerFormElement.appendChild(instanceManager.getFlowInstance().toXML(doc));
		flowInstanceManagerFormElement.appendChild(managerResponse.toXML(doc));

		if (lastFlowAction == null && req.getParameter("saved") != null) {
			lastFlowAction = FlowAction.SAVE;
		}

		XMLUtils.appendNewElement(doc, flowInstanceManagerFormElement, "lastFlowAction", lastFlowAction);

		if (validationError != null) {

			flowInstanceManagerFormElement.appendChild(validationError.toXML(doc));
		}

		if (user != null) {
			XMLUtils.appendNewElement(doc, flowInstanceManagerFormElement, "loggedIn");
		}

		callback.appendFormData(doc, flowInstanceManagerFormElement, instanceManager, user);

		SimpleForegroundModuleResponse moduleResponse = new SimpleForegroundModuleResponse(doc, instanceManager.getFlowInstance().getFlow().getName(), this.getDefaultBreadcrumb(), this.getFlowBreadcrumb(instanceManager.getFlowInstance()));

		appendLinksAndScripts(moduleResponse, managerResponse);

		return moduleResponse;
	}

	protected ForegroundModuleResponse showPreview(HttpServletRequest req, User user, URIParser uriParser, MutableFlowInstanceManager instanceManager, FlowProcessCallback callback, FlowAction lastFlowAction, String baseUpdateURL, ValidationError validationError) throws UnableToGetQueryInstanceShowHTMLException, FlowInstanceManagerClosedException {

		log.info("User " + user + " requested preview of flow instance " + instanceManager.getFlowInstance());

		List<ManagerResponse> managerResponses = instanceManager.getFullShowHTML(req, user, this, true, baseUpdateURL, getMutableQueryRequestBaseURL(req, instanceManager));

		Document doc = createDocument(req, uriParser, user);
		Element flowInstanceManagerPreviewElement = doc.createElement("FlowInstanceManagerPreview");
		doc.getDocumentElement().appendChild(flowInstanceManagerPreviewElement);

		flowInstanceManagerPreviewElement.appendChild(instanceManager.getFlowInstance().toXML(doc));

		XMLUtils.append(doc, flowInstanceManagerPreviewElement, "ManagerResponses", managerResponses);

		XMLUtils.appendNewElement(doc, flowInstanceManagerPreviewElement, "lastFlowAction", lastFlowAction);

		if (user != null) {
			XMLUtils.appendNewElement(doc, flowInstanceManagerPreviewElement, "loggedIn");
		}

		if (validationError != null) {

			flowInstanceManagerPreviewElement.appendChild(validationError.toXML(doc));
		}

		callback.appendFormData(doc, flowInstanceManagerPreviewElement, instanceManager, user);

		// TODO breadcrumbs
		SimpleForegroundModuleResponse moduleResponse = new SimpleForegroundModuleResponse(doc, instanceManager.getFlowInstance().getFlow().getName());

		appendLinksAndScripts(moduleResponse, managerResponses);

		return moduleResponse;
	}

	protected ForegroundModuleResponse showSigningForm(MutableFlowInstanceManager instanceManager, HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, ViewFragment viewFragment) throws FlowInstanceManagerClosedException, UnableToGetQueryInstanceFormHTMLException {

		log.info("User " + user + " requested sign form flow instance " + instanceManager.getFlowInstance());

		Document doc = createDocument(req, uriParser, user);
		Element signFormElement = doc.createElement("SigningForm");
		doc.getDocumentElement().appendChild(signFormElement);

		signFormElement.appendChild(instanceManager.getFlowInstance().toXML(doc));
		signFormElement.appendChild(viewFragment.toXML(doc));

		//TODO fix add breadcrumbs
		SimpleForegroundModuleResponse moduleResponse = new SimpleForegroundModuleResponse(doc, instanceManager.getFlowInstance().getFlow().getName(), this.getDefaultBreadcrumb());

		ViewFragmentUtils.appendLinksAndScripts(moduleResponse, viewFragment);

		return moduleResponse;
	}

	protected ForegroundModuleResponse showInlinePaymentForm(MutableFlowInstanceManager instanceManager, HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, ViewFragment viewFragment) throws FlowInstanceManagerClosedException, UnableToGetQueryInstanceFormHTMLException {

		log.info("User " + user + " requested payment form flow instance " + instanceManager.getFlowInstance());

		Document doc = createDocument(req, uriParser, user);
		Element paymentFormElement = doc.createElement("InlinePaymentForm");
		doc.getDocumentElement().appendChild(paymentFormElement);

		paymentFormElement.appendChild(instanceManager.getFlowInstance().toXML(doc));
		paymentFormElement.appendChild(viewFragment.toXML(doc));

		//TODO fix add breadcrumbs
		SimpleForegroundModuleResponse moduleResponse = new SimpleForegroundModuleResponse(doc, instanceManager.getFlowInstance().getFlow().getName(), this.getDefaultBreadcrumb());

		ViewFragmentUtils.appendLinksAndScripts(moduleResponse, viewFragment);

		return moduleResponse;
	}

	protected ForegroundModuleResponse showImmutableFlowInstance(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, FlowInstanceAccessController accessController, FlowProcessCallback callback, ShowMode showMode) throws UnableToGetQueryInstanceShowHTMLException, FlowInstanceManagerClosedException, AccessDeniedException, SQLException, ModuleConfigurationException {

		Integer flowInstanceID = null;
		ImmutableFlowInstanceManager instanceManager;

		try {
			if (uriParser.size() == 3 && (flowInstanceID = uriParser.getInt(2)) != null) {

				//Get saved instance from DB or session
				instanceManager = getImmutableFlowInstanceManager(flowInstanceID, accessController, user, true, req);

				if (instanceManager == null) {

					log.info("User " + user + " requested non-existing flow instance with ID " + flowInstanceID + ", listing flows");
					return callback.list(req, res, user, uriParser, Collections.singletonList(FLOW_INSTANCE_NOT_FOUND_VALIDATION_ERROR));
				}

			} else {

				log.info("User " + user + " requested invalid URL, listing flows");
				return callback.list(req, res, user, uriParser, Collections.singletonList(INVALID_LINK_VALIDATION_ERROR));
			}

		} catch (FlowDisabledException e) {

			log.info("User " + user + " requested flow " + e.getFlow() + " which is not enabled.");
			return callback.list(req, res, user, uriParser, Collections.singletonList(FLOW_DISABLED_VALIDATION_ERROR));

		} catch (FlowEngineException e) {

			log.info("Error generating preview of flowInstanceID " + flowInstanceID + " for user " + user, e);
			return callback.list(req, res, user, uriParser, Collections.singletonList(FLOW_INSTANCE_PREVIEW_VALIDATION_ERROR));
		}

		String elementName;

		if (showMode == ShowMode.SUBMIT) {

			elementName = "FlowInstanceManagerSubmitted";

		} else {

			elementName = "ImmutableFlowInstanceManagerPreview";
		}

		Breadcrumb breadcrumb;

		if (showMode == ShowMode.SUBMIT) {

			breadcrumb = this.getFlowInstanceSubmitBreadcrumb(instanceManager.getFlowInstance(), req, uriParser);

		} else {

			breadcrumb = this.getFlowInstancePreviewBreadcrumb(instanceManager.getFlowInstance(), req, uriParser);
		}

		return showFlowInstance(req, res, user, uriParser, instanceManager, accessController, elementName, breadcrumb, showMode);
	}

	protected ForegroundModuleResponse showFlowInstance(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, FlowInstanceManager instanceManager, FlowInstanceAccessController accessController, String elementName, Breadcrumb breadcrumb, ShowMode showMode) throws UnableToGetQueryInstanceShowHTMLException, FlowInstanceManagerClosedException, AccessDeniedException, SQLException, ModuleConfigurationException {

		log.info("User " + user + " requested preview of flow instance " + instanceManager.getFlowInstance());

		String baseUpdateURL = getBaseUpdateURL(req, uriParser, user, instanceManager.getFlowInstance(), accessController);

		List<ManagerResponse> managerResponses = instanceManager.getFullShowHTML(req, user, this, true, baseUpdateURL, getImmutableQueryRequestBaseURL(req, instanceManager));

		Document doc = createDocument(req, uriParser, user);
		Element flowInstanceManagerElement = doc.createElement(elementName);
		doc.getDocumentElement().appendChild(flowInstanceManagerElement);

		flowInstanceManagerElement.appendChild(instanceManager.getFlowInstance().toXML(doc));

		XMLUtils.append(doc, flowInstanceManagerElement, "ManagerResponses", managerResponses);

		if (instanceManager.getFlowInstance().getEvents() != null) {

			for (ImmutableFlowInstanceEvent event : new ReverseListIterator<ImmutableFlowInstanceEvent>(instanceManager.getFlowInstance().getEvents())) {

				if (event.getEventType() == EventType.SUBMITTED || event.getEventType() == EventType.SIGNED) {

					String pdfLink = getEventPDFLink(instanceManager, event, req, user);

					if (pdfLink != null) {

						XMLUtils.appendNewCDATAElement(doc, flowInstanceManagerElement, "PDFLink", pdfLink);

						break;

					}
				}
			}
		}

		SimpleForegroundModuleResponse moduleResponse = new SimpleForegroundModuleResponse(doc, instanceManager.getFlowInstance().getFlow().getName(), this.getDefaultBreadcrumb());
		
		if (instanceManager.getFlowInstance().getFlow().showsSubmitSurvey() && showMode == ShowMode.SUBMIT) {

			FlowSubmitSurveyProvider instance = systemInterface.getInstanceHandler().getInstance(FlowSubmitSurveyProvider.class);

			if (instance != null) {

				try {

					ViewFragment viewFragment = instance.getSurveyFormFragment(req, user, (FlowInstanceManager) instanceManager);

					if (viewFragment != null) {
						
						ViewFragmentUtils.appendLinksAndScripts(moduleResponse, viewFragment);
						
						XMLUtils.appendNewElement(doc, flowInstanceManagerElement, "SubmitSurveyHTML", viewFragment.getHTML());
					}

				} catch (TransformerException e) {

					log.error("Unable to get view fragment for flow submit survey", e);
				}
			}

		}

		if (breadcrumb != null) {

			moduleResponse.addBreadcrumbLast(breadcrumb);
		}

		appendLinksAndScripts(moduleResponse, managerResponses);

		return moduleResponse;
	}

	protected Breadcrumb getFlowInstancePreviewBreadcrumb(ImmutableFlowInstance flowInstance, HttpServletRequest req, URIParser uriParser) {

		return null;
	}

	protected Breadcrumb getFlowInstanceSubmitBreadcrumb(ImmutableFlowInstance flowInstance, HttpServletRequest req, URIParser uriParser) {

		return null;
	}

	protected String getEventPDFLink(FlowInstanceManager instanceManager, ImmutableFlowInstanceEvent event, HttpServletRequest req, User user) {

		return null;
	}

	/**
	 * @param flowInstanceID
	 * @param accessController
	 * @param user
	 * @return The deleted flow instance or null if the flow instance could not be found.
	 * @throws URINotFoundException
	 * @throws SQLException
	 * @throws AccessDeniedException
	 * @throws IOException
	 */
	public FlowInstance deleteFlowInstance(Integer flowInstanceID, FlowInstanceAccessController accessController, User user) throws SQLException, AccessDeniedException {

		HighLevelQuery<FlowInstance> query = new HighLevelQuery<FlowInstance>();

		query.addRelations(FlowInstance.FLOW_STATE_RELATION, FlowInstance.FLOW_RELATION, Flow.STEPS_RELATION, Flow.FLOW_TYPE_RELATION, FlowType.ALLOWED_GROUPS_RELATION, FlowType.ALLOWED_USERS_RELATION, Step.QUERY_DESCRIPTORS_RELATION, QueryDescriptor.QUERY_INSTANCE_DESCRIPTORS_RELATION);
		query.addParameter(flowInstanceIDParamFactory.getParameter(flowInstanceID));
		query.addRelationParameter(QueryInstanceDescriptor.class, queryInstanceDescriptorFlowInstanceIDParamFactory.getParameter(flowInstanceID));

		FlowInstance flowInstance = daoFactory.getFlowInstanceDAO().get(query);

		if (flowInstance == null) {

			return null;
		}

		accessController.checkFlowInstanceAccess(flowInstance, user);

		log.info("User " + user + " deleting flow instance " + flowInstance);

		TransactionHandler transactionHandler = null;

		try {
			transactionHandler = daoFactory.getFlowInstanceDAO().createTransaction();

			daoFactory.getFlowInstanceDAO().delete(flowInstance, transactionHandler);

			if (flowInstance.getFlow().getSteps() != null) {

				for (Step step : flowInstance.getFlow().getSteps()) {

					if (step.getQueryDescriptors() != null) {

						for (QueryDescriptor queryDescriptor : step.getQueryDescriptors()) {

							if (queryDescriptor.getQueryInstanceDescriptors() != null) {

								QueryInstanceDescriptor instanceDescriptor = queryDescriptor.getQueryInstanceDescriptors().get(0);

								instanceDescriptor.setQueryDescriptor(queryDescriptor);

								try {
									queryHandler.deleteQueryInstance(queryDescriptor.getQueryInstanceDescriptors().get(0), transactionHandler);

								} catch (Exception e) {

									log.error("Error deleting query instance " + queryDescriptor.getQueryInstanceDescriptors().get(0), e);
								}
							}
						}
					}
				}
			}

			transactionHandler.commit();

		} finally {

			TransactionHandler.autoClose(transactionHandler);
		}

		daoFactory.getFlowInstanceDAO().delete(flowInstance);

		int closedCount = FlowInstanceManagerRegistery.getInstance().closeInstances(flowInstanceID, queryHandler);

		if (closedCount > 0) {
			log.info("Closed " + closedCount + " flow instance managers for flow instance " + flowInstance);
		}

		this.eventHandler.sendEvent(FlowInstance.class, new CRUDEvent<FlowInstance>(CRUDAction.DELETE, flowInstance), EventTarget.ALL);

		return flowInstance;
	}

	protected void appendLinksAndScripts(SimpleForegroundModuleResponse moduleResponse, List<ManagerResponse> managerResponses) {

		for (ManagerResponse managerResponse : managerResponses) {

			appendLinksAndScripts(moduleResponse, managerResponse);
		}
	}

	protected void appendLinksAndScripts(SimpleForegroundModuleResponse moduleResponse, ManagerResponse managerResponse) {

		if (managerResponse.getQueryResponses() != null) {

			for (QueryResponse queryResponse : managerResponse.getQueryResponses()) {

				if (queryResponse.getLinks() != null) {

					moduleResponse.addLinks(queryResponse.getLinks());
				}

				if (queryResponse.getScripts() != null) {

					moduleResponse.addScripts(queryResponse.getScripts());
				}
			}
		}
	}

	protected void sendSubmitEvent(FlowInstanceManager instanceManager, FlowInstanceEvent event, String actionID, SiteProfile siteProfile) {

		eventHandler.sendEvent(FlowInstanceManager.class, new SubmitEvent(instanceManager, event, moduleDescriptor, actionID, siteProfile), EventTarget.ALL);
	}

	public FlowInstanceEvent addFlowInstanceEvent(ImmutableFlowInstance flowInstance, EventType eventType, String details, User user) throws SQLException {

		FlowInstanceEvent flowInstanceEvent = new FlowInstanceEvent();
		flowInstanceEvent.setFlowInstance((FlowInstance) flowInstance);
		flowInstanceEvent.setEventType(eventType);
		flowInstanceEvent.setDetails(details);
		flowInstanceEvent.setPoster(user);
		flowInstanceEvent.setStatus(flowInstance.getStatus().getName());
		flowInstanceEvent.setStatusDescription(flowInstance.getStatus().getDescription());
		flowInstanceEvent.setAdded(TimeUtils.getCurrentTimestamp());

		daoFactory.getFlowInstanceEventDAO().add(flowInstanceEvent);

		return flowInstanceEvent;
	}

	public Document createDocument(HttpServletRequest req, URIParser uriParser, User user) {

		Document doc = XMLUtils.createDomDocument();
		Element documentElement = doc.createElement("Document");
		documentElement.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser));
		documentElement.appendChild(this.moduleDescriptor.toXML(doc));

		doc.appendChild(documentElement);
		return doc;
	}

	protected Flow getFlow(Integer flowID) throws SQLException {

		HighLevelQuery<Flow> query = new HighLevelQuery<Flow>(Flow.FLOW_TYPE_RELATION, Flow.FLOW_FAMILY_RELATION, Flow.STEPS_RELATION, Step.QUERY_DESCRIPTORS_RELATION, QueryDescriptor.EVALUATOR_DESCRIPTORS_RELATION, Flow.DEFAULT_FLOW_STATE_MAPPINGS_RELATION, DefaultStatusMapping.FLOW_STATE_RELATION);

		query.addParameter(flowIDParamFactory.getParameter(flowID));

		return daoFactory.getFlowDAO().get(query);
	}

	public FlowInstance getFlowInstance(int flowInstanceID) throws SQLException {

		HighLevelQuery<FlowInstance> query = new HighLevelQuery<FlowInstance>(FLOW_INSTANCE_RELATIONS);

		query.addParameter(flowInstanceIDParamFactory.getParameter(flowInstanceID));

		query.addRelationParameter(QueryInstanceDescriptor.class, queryInstanceDescriptorFlowInstanceIDParamFactory.getParameter(flowInstanceID));

		return daoFactory.getFlowInstanceDAO().get(query);
	}

	protected FlowInstance getFlowInstance(int flowInstanceID, List<Field> excludedFields, Field... relations) throws SQLException {

		HighLevelQuery<FlowInstance> query = new HighLevelQuery<FlowInstance>(relations);

		if (excludedFields != null) {
			query.addExcludedFields(excludedFields);
		}

		query.addParameter(flowInstanceIDParamFactory.getParameter(flowInstanceID));

		return daoFactory.getFlowInstanceDAO().get(query);
	}

	private static String getNewInstanceManagerID(User user) {

		if (user != null && user.getUserID() != null) {

			return "userid-" + user.getUserID() + "-uuid-" + UUID.randomUUID();
		}

		return "anonymous-uuid-" + UUID.randomUUID();
	}

	@Override
	public QueryHandler getQueryHandler() {

		return queryHandler;
	}

	@Override
	public SystemInterface getSystemInterface() {

		return systemInterface;
	}

	@Override
	public FlowEngineDAOFactory getDAOFactory() {

		return daoFactory;
	}

	public ForegroundModuleResponse processMutableQueryRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, FlowInstanceAccessController accessController, boolean checkPublishDate, boolean checkEnabled) throws ModuleConfigurationException, SQLException, AccessDeniedException, IOException, FlowDefaultStatusNotFound, EvaluationException, URINotFoundException, QueryRequestException, QueryProviderException, EvaluationProviderException, InvalidFlowInstanceStepException, MissingQueryInstanceDescriptor, DuplicateFlowInstanceManagerIDException {

		Integer flowID = null;

		if (uriParser.size() < 5 || (flowID = NumberUtils.toInt(uriParser.get(2))) == null) {

			throw new URINotFoundException(uriParser);
		}

		Integer queryID = null;
		MutableFlowInstanceManager instanceManager = null;

		try {
			if (uriParser.get(3).equals("q") && NumberUtils.isInt(uriParser.get(4))) {

				queryID = NumberUtils.toInt(uriParser.get(4));

				//Get instance from session
				instanceManager = getUnsavedMutableFlowInstanceManager(flowID, accessController, req.getSession(true), user, uriParser, req, false, checkEnabled, checkPublishDate);

			} else if (uriParser.size() > 5 && NumberUtils.isInt(uriParser.get(3)) && uriParser.get(4).equals("q") && NumberUtils.isInt(uriParser.get(5))) {

				Integer flowInstanceID = NumberUtils.toInt(uriParser.get(3));
				queryID = NumberUtils.toInt(uriParser.get(5));

				//Get saved instance from DB or session
				instanceManager = getSavedMutableFlowInstanceManager(flowID, flowInstanceID, accessController, req.getSession(true), user, uriParser, req, true, checkEnabled, checkPublishDate);

			}

			if (instanceManager == null || queryID == null) {

				throw new URINotFoundException(uriParser);
			}

			processQueryRequest(instanceManager, queryID, req, res, user, uriParser);

		} catch (QueryInstanceNotFoundInFlowInstanceManagerException e) {

			throw new URINotFoundException(uriParser);

		} catch (FlowException e) {

			throw new URINotFoundException(uriParser);

		} catch (FlowInstanceNoLongerAvailableException e) {

			throw new URINotFoundException(uriParser);

		} catch (QueryRequestsNotSupported e) {

			throw new URINotFoundException(uriParser);

		} catch (QueryRequestException e) {

			log.error("Error processing query request for query " + e.getQueryInstanceDescriptor() + " in flow instance " + instanceManager.getFlowInstance() + " requested by user " + user, e);

			throw e;
		}

		return null;
	}

	public ForegroundModuleResponse processImmutableQueryRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, FlowInstanceAccessController accessController, boolean checkEnabled) throws ModuleConfigurationException, SQLException, AccessDeniedException, IOException, FlowDefaultStatusNotFound, EvaluationException, URINotFoundException, QueryRequestException, QueryProviderException, EvaluationProviderException, InvalidFlowInstanceStepException, MissingQueryInstanceDescriptor, DuplicateFlowInstanceManagerIDException {

		Integer flowInstanceID = null;
		Integer queryID = null;

		if (uriParser.size() < 6 || (flowInstanceID = NumberUtils.toInt(uriParser.get(3))) == null || !uriParser.get(4).equals("q") || (queryID = NumberUtils.toInt(uriParser.get(5))) == null) {

			throw new URINotFoundException(uriParser);
		}

		ImmutableFlowInstanceManager instanceManager = null;

		try {
			instanceManager = this.getImmutableFlowInstanceManager(flowInstanceID, accessController, user, checkEnabled, req);

			if (instanceManager == null || queryID == null) {

				throw new URINotFoundException(uriParser);
			}

			processQueryRequest(instanceManager, queryID, req, res, user, uriParser);

		} catch (QueryInstanceNotFoundInFlowInstanceManagerException e) {

			throw new URINotFoundException(uriParser);

		} catch (FlowException e) {

			throw new URINotFoundException(uriParser);

		} catch (QueryRequestsNotSupported e) {

			throw new URINotFoundException(uriParser);

		} catch (QueryRequestException e) {

			log.error("Error processing query request for query " + e.getQueryInstanceDescriptor() + " in flow instance " + instanceManager.getFlowInstance() + " requested by user " + user, e);

			throw e;
		}

		return null;
	}

	@WebPublic(alias = "icon")
	public ForegroundModuleResponse getFlowIcon(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, AccessDeniedException, ModuleConfigurationException, SQLException, IOException {

		Integer flowID = null;

		if (uriParser.size() == 3 && (flowID = NumberUtils.toInt(uriParser.get(2))) != null) {

			Flow flow = getBareFlow(flowID);

			if (flow != null) {

				InputStream in = null;
				OutputStream out = null;

				try {
					if (flow.getIcon() != null) {

						HTTPUtils.setContentLength(flow.getIcon().length(), res);

						res.setContentType(MimeUtils.getMimeType(flow.getIconFileName()));
						res.setHeader("Content-Disposition", "inline; filename=\"" + FileUtils.toValidHttpFilename(flow.getIconFileName()) + "\"");

						in = flow.getIcon().getBinaryStream();

					} else {

						in = BaseFlowModule.class.getResourceAsStream("staticcontent/pics/flow_default.png");
						res.setContentType(MimeUtils.getMimeType("flow_default.png"));
						res.setHeader("Content-Disposition", "inline; filename=\"flow_default.png\"");
					}

					out = res.getOutputStream();

					StreamUtils.transfer(in, out);

				} catch (RuntimeException e) {

					log.debug("Caught exception " + e + " while sending image " + flow.getIconFileName() + " to " + user);

				} catch (IOException e) {

					log.debug("Caught exception " + e + " while sending image " + flow.getIconFileName() + " to " + user);

				} finally {

					StreamUtils.closeStream(in);
					StreamUtils.closeStream(out);
				}

				return null;
			}
		}

		throw new URINotFoundException(uriParser);
	}

	public ForegroundModuleResponse showMultiSignMessage(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, FlowInstanceAccessController accessController, FlowProcessCallback callback) throws FlowInstanceManagerClosedException, UnableToGetQueryInstanceShowHTMLException, AccessDeniedException, ModuleConfigurationException, SQLException, URINotFoundException {

		Integer flowInstanceID = null;
		ImmutableFlowInstanceManager instanceManager;

		try {

			if (uriParser.size() == 3 && (flowInstanceID = uriParser.getInt(2)) != null) {

				//Get saved instance from DB or session
				instanceManager = getImmutableFlowInstanceManager(flowInstanceID, accessController, user, true, req);

				if (instanceManager == null) {

					log.info("User " + user + " requested non-existing flow instance with ID " + flowInstanceID + ", listing flows");
					return callback.list(req, res, user, uriParser, Collections.singletonList(FLOW_INSTANCE_NOT_FOUND_VALIDATION_ERROR));
				}

			} else {

				log.info("User " + user + " requested invalid URL, listing flows");
				return callback.list(req, res, user, uriParser, Collections.singletonList(INVALID_LINK_VALIDATION_ERROR));
			}

		} catch (FlowDisabledException e) {

			log.info("User " + user + " requested flow " + e.getFlow() + " which is not enabled.");
			return callback.list(req, res, user, uriParser, Collections.singletonList(FLOW_DISABLED_VALIDATION_ERROR));

		} catch (FlowEngineException e) {

			log.info("Error generating preview of flowInstanceID " + flowInstanceID + " for user " + user, e);
			return callback.list(req, res, user, uriParser, Collections.singletonList(UNABLE_TO_LOAD_FLOW_INSTANCE_VALIDATION_ERROR));
		}

		if (instanceManager.getFlowState().getContentType() != ContentType.WAITING_FOR_MULTISIGN) {

			//TODO show correct view
			throw new URINotFoundException(uriParser);
		}

		MultiSigningProvider multiSigningProvider = getMultiSigningProvider();

		if (multiSigningProvider == null) {

			return callback.list(req, res, user, uriParser, Collections.singletonList(MULTI_SIGNING_PROVIDER_NOT_FOUND_VALIDATION_ERROR));
		}

		ViewFragment viewFragment;

		try {
			viewFragment = multiSigningProvider.getSigningStatus(req, user, uriParser, instanceManager);
		} catch (Exception e) {
			viewFragment = null;
			log.error("Error getting view fragment from multi signing provider " + multiSigningProvider, e);
		}

		if (viewFragment == null) {

			log.warn("No viewfragement returned from multi signing provider " + multiSigningProvider);
		}

		log.info("User " + user + " requested multi signing status for flow instance " + instanceManager.getFlowInstance());

		Document doc = createDocument(req, uriParser, user);
		Element multiSigningStatusElement = doc.createElement("MultiSigningStatusForm");
		doc.getDocumentElement().appendChild(multiSigningStatusElement);

		multiSigningStatusElement.appendChild(instanceManager.getFlowInstance().toXML(doc));

		SimpleForegroundModuleResponse moduleResponse = new SimpleForegroundModuleResponse(doc, this.getDefaultBreadcrumb());

		if (viewFragment != null) {

			multiSigningStatusElement.appendChild(viewFragment.toXML(doc));
			ViewFragmentUtils.appendLinksAndScripts(moduleResponse, viewFragment);
		}

		//TODO fix add breadcrumbs

		return moduleResponse;

	}

	public ForegroundModuleResponse showPaymentForm(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, FlowInstanceAccessController accessController, FlowProcessCallback callback) throws ModuleConfigurationException, SQLException, AccessDeniedException, URINotFoundException {

		Integer flowInstanceID = null;
		ImmutableFlowInstanceManager instanceManager;

		try {

			if (uriParser.size() == 3 && (flowInstanceID = uriParser.getInt(2)) != null) {

				//Get saved instance from DB or session
				instanceManager = getImmutableFlowInstanceManager(flowInstanceID, accessController, user, true, req);

				if (instanceManager == null) {

					log.info("User " + user + " requested non-existing flow instance with ID " + flowInstanceID + ", listing flows");
					return callback.list(req, res, user, uriParser, Collections.singletonList(FLOW_INSTANCE_NOT_FOUND_VALIDATION_ERROR));
				}

			} else {

				log.info("User " + user + " requested invalid URL, listing flows");
				return callback.list(req, res, user, uriParser, Collections.singletonList(INVALID_LINK_VALIDATION_ERROR));
			}

		} catch (FlowDisabledException e) {

			log.info("User " + user + " requested flow " + e.getFlow() + " which is not enabled.");
			return callback.list(req, res, user, uriParser, Collections.singletonList(FLOW_DISABLED_VALIDATION_ERROR));

		} catch (FlowEngineException e) {

			log.info("Error generating preview of flowInstanceID " + flowInstanceID + " for user " + user, e);
			return callback.list(req, res, user, uriParser, Collections.singletonList(UNABLE_TO_LOAD_FLOW_INSTANCE_VALIDATION_ERROR));
		}

		if (instanceManager.getFlowState().getContentType() != ContentType.WAITING_FOR_PAYMENT) {

			//TODO show correct view
			throw new URINotFoundException(uriParser);
		}

		PaymentProvider paymentProvider = getPaymentProvider();

		if (paymentProvider == null) {

			return callback.list(req, res, user, uriParser, Collections.singletonList(PAYMENT_PROVIDER_NOT_FOUND_VALIDATION_ERROR));
		}

		ViewFragment viewFragment;

		try {

			viewFragment = paymentProvider.pay(req, res, user, instanceManager, new BaseFlowModuleStandalonePaymentCallback(this, callback.getSubmitActionID()));

		} catch (Exception e) {

			viewFragment = null;
			log.error("Error getting view fragment from payment provider " + paymentProvider, e);

		}

		if (res.isCommitted()) {

			return null;

		} else if (viewFragment == null) {

			log.warn("No view fragment returned from payment provider " + paymentProvider);
		}

		log.info("User " + user + " requested payment form for flow instance " + instanceManager.getFlowInstance());

		Document doc = createDocument(req, uriParser, user);
		Element paymentFormElement = doc.createElement("StandalonePaymentForm");
		doc.getDocumentElement().appendChild(paymentFormElement);

		paymentFormElement.appendChild(instanceManager.getFlowInstance().toXML(doc));

		//TODO fix add breadcrumbs
		SimpleForegroundModuleResponse moduleResponse = new SimpleForegroundModuleResponse(doc, this.getDefaultBreadcrumb());

		if (viewFragment != null) {

			paymentFormElement.appendChild(viewFragment.toXML(doc));
			ViewFragmentUtils.appendLinksAndScripts(moduleResponse, viewFragment);
		}

		return moduleResponse;
	}

	protected Flow getBareFlow(Integer flowID) throws SQLException {

		HighLevelQuery<Flow> query = new HighLevelQuery<Flow>();

		query.addParameter(flowIDParamFactory.getParameter(flowID));

		return daoFactory.getFlowDAO().get(query);
	}

	@Override
	public EvaluationHandler getEvaluationHandler() {

		return evaluationHandler;
	}

	public Breadcrumb getFlowBreadcrumb(ImmutableFlowInstance flowInstance) {

		return new Breadcrumb(this, flowInstance.getFlow().getName(), "/flow/" + flowInstance.getFlow().getFlowID());
	}

	public String getTempDir() {

		return tempDir;
	}

	public int getMaxRequestSize() {

		return maxRequestSize;
	}

	public int getRamThreshold() {

		return ramThreshold;
	}

	public void sendEventPDF(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, FlowInstanceAccessController accessController, PDFProvider pdfProvider) throws URINotFoundException, SQLException, IOException, AccessDeniedException {

		Integer flowInstanceID;
		Integer eventID;

		if (uriParser.size() != 4 || (flowInstanceID = uriParser.getInt(2)) == null || (eventID = uriParser.getInt(3)) == null || pdfProvider == null) {

			throw new URINotFoundException(uriParser);
		}

		FlowInstance flowInstance = getFlowInstance(flowInstanceID);

		if (flowInstance == null) {

			throw new URINotFoundException(uriParser);
		}

		accessController.checkFlowInstanceAccess(flowInstance, user);

		File pdfFile = pdfProvider.getPDF(flowInstanceID, eventID);

		if (pdfFile == null) {

			throw new URINotFoundException(uriParser);
		}

		log.info("Sending PDF for flow instance " + flowInstance + ", event " + eventID + " to user " + user);
		HTTPUtils.sendFile(pdfFile, flowInstance.getFlow().getName() + " - " + flowInstance.getFlowInstanceID() + ".pdf", req, res, ContentDisposition.ATTACHMENT);
	}

	public void sendEventXML(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, FlowInstanceAccessController accessController, XMLProvider xmlProvider) throws URINotFoundException, SQLException, IOException, AccessDeniedException {

		Integer flowInstanceID;
		Integer eventID;

		if (uriParser.size() != 4 || (flowInstanceID = uriParser.getInt(2)) == null || (eventID = uriParser.getInt(3)) == null || xmlProvider == null) {

			throw new URINotFoundException(uriParser);
		}

		FlowInstance flowInstance = getFlowInstance(flowInstanceID);

		if (flowInstance == null) {

			throw new URINotFoundException(uriParser);
		}

		accessController.checkFlowInstanceAccess(flowInstance, user);

		File pdfFile = xmlProvider.getXML(flowInstanceID, eventID);

		if (pdfFile == null) {

			throw new URINotFoundException(uriParser);
		}

		log.info("Sending export XML for flow instance " + flowInstance + ", event " + eventID + " to user " + user);
		HTTPUtils.sendFile(pdfFile, flowInstance.getFlow().getName() + " - " + flowInstance.getFlowInstanceID() + ".xml", req, res, ContentDisposition.ATTACHMENT);
	}

	public FlowInstanceEvent createSigningEvent(MutableFlowInstanceManager instanceManager, User user, EventType eventType, String actionID) throws FlowInstanceManagerClosedException, UnableToSaveQueryInstanceException, SQLException {

		return addFlowInstanceEvent(instanceManager.getFlowInstance(), eventType, null, user);
	}

	public void signingComplete(MutableFlowInstanceManager instanceManager, FlowInstanceEvent event, HttpServletRequest req, SiteProfile siteProfile, String actionID) {

		if (!requiresMultiSigning(instanceManager) && !requiresPayment(instanceManager)) {

			sendSubmitEvent(instanceManager, event, actionID, siteProfile);

		}

		//TODO Unused?
		systemInterface.getEventHandler().sendEvent(FlowInstanceManager.class, new SigningEvent(instanceManager, event, moduleDescriptor, siteProfile, actionID), EventTarget.ALL);

		systemInterface.getEventHandler().sendEvent(FlowInstance.class, new CRUDEvent<FlowInstance>(CRUDAction.UPDATE, (FlowInstance) instanceManager.getFlowInstance()), EventTarget.ALL);

		removeMutableFlowInstanceManagerFromSession(instanceManager, req.getSession());
	}

	public void standalonePaymentComplete(ImmutableFlowInstanceManager instanceManager, HttpServletRequest req, User user, String actionID) throws FlowInstanceManagerClosedException, UnableToSaveQueryInstanceException, FlowDefaultStatusNotFound, SQLException {

		FlowInstance flowInstance = (FlowInstance) instanceManager.getFlowInstance();

		addFlowInstanceEvent(flowInstance, EventType.PAYED, null, user);

		Status nextStatus = flowInstance.getFlow().getDefaultState(actionID);

		if (nextStatus == null) {

			throw new FlowDefaultStatusNotFound(actionID, instanceManager.getFlowInstance().getFlow());
		}

		flowInstance.setStatus(nextStatus);
		flowInstance.setLastStatusChange(TimeUtils.getCurrentTimestamp());
		this.daoFactory.getFlowInstanceDAO().update(flowInstance);

		FlowInstanceEvent event = addFlowInstanceEvent(instanceManager.getFlowInstance(), EventType.SUBMITTED, null, user);

		sendSubmitEvent(instanceManager, event, actionID, null);

		systemInterface.getEventHandler().sendEvent(FlowInstance.class, new CRUDEvent<FlowInstance>(CRUDAction.UPDATE, (FlowInstance) instanceManager.getFlowInstance()), EventTarget.ALL);

	}

	public void inlinePaymentComplete(MutableFlowInstanceManager instanceManager, HttpServletRequest req, User user, String actionID) throws FlowInstanceManagerClosedException, UnableToSaveQueryInstanceException, FlowDefaultStatusNotFound, SQLException {

		save(instanceManager, user, req, actionID, EventType.PAYED);

		FlowInstanceEvent event = addFlowInstanceEvent(instanceManager.getFlowInstance(), EventType.SUBMITTED, null, user);

		sendSubmitEvent(instanceManager, event, actionID, null);

		systemInterface.getEventHandler().sendEvent(FlowInstance.class, new CRUDEvent<FlowInstance>(CRUDAction.UPDATE, (FlowInstance) instanceManager.getFlowInstance()), EventTarget.ALL);

	}

	public void abortSigning(MutableFlowInstanceManager instanceManager) {

	}

	public abstract String getSignFailURL(MutableFlowInstanceManager instanceManager, HttpServletRequest req);

	public String getSignSuccessURL(MutableFlowInstanceManager instanceManager, HttpServletRequest req) {

		if (requiresMultiSigning(instanceManager)) {

			return RequestUtils.getFullContextPathURL(req) + this.getFullAlias() + "/multisign/" + instanceManager.getFlowInstanceID();

		} else if (requiresPayment(instanceManager)) {

			return RequestUtils.getFullContextPathURL(req) + this.getFullAlias() + "/pay/" + instanceManager.getFlowInstanceID();
		}

		return RequestUtils.getFullContextPathURL(req) + this.getFullAlias() + "/submitted/" + instanceManager.getFlowInstanceID();
	}

	public abstract String getSigningURL(MutableFlowInstanceManager instanceManager, HttpServletRequest req);

	public String getPaymentSuccessURL(FlowInstanceManager instanceManager, HttpServletRequest req) {

		return RequestUtils.getFullContextPathURL(req) + this.getFullAlias() + "/submitted/" + instanceManager.getFlowInstanceID();
	}

	public boolean requiresMultiSigning(FlowInstanceManager instanceManager) {

		//TODO check content type

		List<MultiSigningQuery> multiSigningQueries = instanceManager.getQueries(MultiSigningQuery.class);

		if (multiSigningQueries != null) {

			for (MultiSigningQuery multiSigningQuery : multiSigningQueries) {

				if (multiSigningQuery.getQueryInstanceDescriptor().getQueryState() != QueryState.HIDDEN && !CollectionUtils.isEmpty(multiSigningQuery.getSigningParties())) {

					return true;
				}

			}

		}

		return false;
	}

	public boolean requiresPayment(FlowInstanceManager instanceManager) {

		//TODO check content type

		List<PaymentQuery> paymentQueries = instanceManager.getQueries(PaymentQuery.class);

		if (paymentQueries != null) {

			int amount = 0;

			for (PaymentQuery paymentQuery : paymentQueries) {

				if (paymentQuery.getQueryInstanceDescriptor().getQueryState() != QueryState.HIDDEN && !CollectionUtils.isEmpty(paymentQuery.getInvoiceLines())) {

					for (InvoiceLine invoiceLine : paymentQuery.getInvoiceLines()) {

						amount += invoiceLine.getQuanitity() * invoiceLine.getUnitPrice();

					}

				}

			}

			if (amount > 0) {

				return true;
			}

		}

		return false;
	}
	
	public boolean submitSurveyEnabled() {
		
		return systemInterface.getInstanceHandler().getInstance(FlowSubmitSurveyProvider.class) != null;
		
	}
}
