package com.nordicpeak.flowengine.managers;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.dao.RelationQuery;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.json.JsonArray;
import se.unlogic.standardutils.json.JsonObject;
import se.unlogic.standardutils.json.JsonUtils;
import se.unlogic.standardutils.time.TimeUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;

import com.nordicpeak.flowengine.beans.EvaluationResponse;
import com.nordicpeak.flowengine.beans.Flow;
import com.nordicpeak.flowengine.beans.FlowInstance;
import com.nordicpeak.flowengine.beans.PDFQueryResponse;
import com.nordicpeak.flowengine.beans.QueryDescriptor;
import com.nordicpeak.flowengine.beans.QueryInstanceDescriptor;
import com.nordicpeak.flowengine.beans.QueryModification;
import com.nordicpeak.flowengine.beans.QueryResponse;
import com.nordicpeak.flowengine.beans.Status;
import com.nordicpeak.flowengine.beans.Step;
import com.nordicpeak.flowengine.dao.FlowEngineDAOFactory;
import com.nordicpeak.flowengine.enums.FlowDirection;
import com.nordicpeak.flowengine.enums.QueryState;
import com.nordicpeak.flowengine.exceptions.evaluation.EvaluationException;
import com.nordicpeak.flowengine.exceptions.evaluationprovider.EvaluationProviderErrorException;
import com.nordicpeak.flowengine.exceptions.evaluationprovider.EvaluationProviderNotFoundException;
import com.nordicpeak.flowengine.exceptions.evaluationprovider.EvaluatorNotFoundInEvaluationProviderException;
import com.nordicpeak.flowengine.exceptions.flowinstance.InvalidFlowInstanceStepException;
import com.nordicpeak.flowengine.exceptions.flowinstance.MissingQueryInstanceDescriptor;
import com.nordicpeak.flowengine.exceptions.flowinstancemanager.DuplicateFlowInstanceManagerIDException;
import com.nordicpeak.flowengine.exceptions.flowinstancemanager.FlowInstanceManagerClosedException;
import com.nordicpeak.flowengine.exceptions.queryinstance.QueryModificationException;
import com.nordicpeak.flowengine.exceptions.queryinstance.UnableToGetQueryInstanceFormHTMLException;
import com.nordicpeak.flowengine.exceptions.queryinstance.UnableToGetQueryInstancePDFContentException;
import com.nordicpeak.flowengine.exceptions.queryinstance.UnableToGetQueryInstanceShowHTMLException;
import com.nordicpeak.flowengine.exceptions.queryinstance.UnableToPopulateQueryInstanceException;
import com.nordicpeak.flowengine.exceptions.queryinstance.UnableToResetQueryInstanceException;
import com.nordicpeak.flowengine.exceptions.queryinstance.UnableToSaveQueryInstanceException;
import com.nordicpeak.flowengine.exceptions.queryprovider.QueryInstanceNotFoundInQueryProviderException;
import com.nordicpeak.flowengine.exceptions.queryprovider.QueryNotFoundInQueryProviderException;
import com.nordicpeak.flowengine.exceptions.queryprovider.QueryProviderErrorException;
import com.nordicpeak.flowengine.exceptions.queryprovider.QueryProviderNotFoundException;
import com.nordicpeak.flowengine.interfaces.EvaluationHandler;
import com.nordicpeak.flowengine.interfaces.Evaluator;
import com.nordicpeak.flowengine.interfaces.FlowEngineInterface;
import com.nordicpeak.flowengine.interfaces.ImmutableEvaluatorDescriptor;
import com.nordicpeak.flowengine.interfaces.ImmutableFlowInstance;
import com.nordicpeak.flowengine.interfaces.ImmutableQueryInstance;
import com.nordicpeak.flowengine.interfaces.ImmutableStep;
import com.nordicpeak.flowengine.interfaces.InstanceMetadata;
import com.nordicpeak.flowengine.interfaces.MutableQueryInstanceDescriptor;
import com.nordicpeak.flowengine.interfaces.QueryHandler;
import com.nordicpeak.flowengine.interfaces.QueryInstance;
import com.nordicpeak.flowengine.utils.TextTagReplacer;

public class MutableFlowInstanceManager implements Serializable, HttpSessionBindingListener, FlowInstanceManager {

	private static final RelationQuery FLOW_INSTANCE_SAVE_RELATIONS = new RelationQuery(FlowInstance.ATTRIBUTES_RELATION);

	// Nested class to keep track of active flow instance managers in a protected fashion
	public final static class FlowInstanceManagerRegistery implements Serializable {

		private static final long serialVersionUID = -2452906097547060782L;

		private static FlowInstanceManagerRegistery REGISTERY;

		private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
		private final Lock r = readWriteLock.readLock();
		private final Lock w = readWriteLock.writeLock();

		private final ArrayList<MutableFlowInstanceManager> sessionBoundInstanceManagers = new ArrayList<MutableFlowInstanceManager>();
		private transient ArrayList<MutableFlowInstanceManager> nonSessionBoundInstanceManagers = new ArrayList<MutableFlowInstanceManager>();

		private FlowInstanceManagerRegistery() {};

		public static synchronized FlowInstanceManagerRegistery getInstance() {

			if(REGISTERY == null){
				REGISTERY = new FlowInstanceManagerRegistery();
			}

			return REGISTERY;
		}

		private Object readResolve() {

			if(REGISTERY == null){
				REGISTERY = this;

				if(REGISTERY.nonSessionBoundInstanceManagers == null){

					nonSessionBoundInstanceManagers = new ArrayList<MutableFlowInstanceManager>();
				}
			}

			return REGISTERY;
		}

		private void addSessionBoundInstance(MutableFlowInstanceManager mutableFlowInstanceManager) {

			try{
				w.lock();
				this.nonSessionBoundInstanceManagers.remove(mutableFlowInstanceManager);
				this.sessionBoundInstanceManagers.add(mutableFlowInstanceManager);
			}finally{
				w.unlock();
			}
		}

		private void removeSessionBoundInstance(MutableFlowInstanceManager mutableFlowInstanceManager) {

			try{
				w.lock();
				this.sessionBoundInstanceManagers.remove(mutableFlowInstanceManager);
			}finally{
				w.unlock();
			}
		}

		public void addNonSessionBoundInstance(MutableFlowInstanceManager mutableFlowInstanceManager) {

			try{
				w.lock();
				this.nonSessionBoundInstanceManagers.add(mutableFlowInstanceManager);
			}finally{
				w.unlock();
			}
		}

		public void removeNonSessionBoundInstance(MutableFlowInstanceManager mutableFlowInstanceManager) {

			try{
				w.lock();
				this.nonSessionBoundInstanceManagers.remove(mutableFlowInstanceManager);
			}finally{
				w.unlock();
			}
		}

		public ArrayList<MutableFlowInstanceManager> getSessionBoundInstances() {

			try{
				r.lock();
				return new ArrayList<MutableFlowInstanceManager>(this.sessionBoundInstanceManagers);
			}finally{
				r.unlock();
			}
		}

		public ArrayList<MutableFlowInstanceManager> getNonSessionBoundInstances() {

			try{
				r.lock();
				return new ArrayList<MutableFlowInstanceManager>(this.nonSessionBoundInstanceManagers);
			}finally{
				r.unlock();
			}
		}

		public boolean isActiveInstance(String id) {

			try{
				r.lock();

				for(MutableFlowInstanceManager manager : sessionBoundInstanceManagers){

					if(manager.getInstanceManagerID().equals(id)){

						return true;
					}
				}

				for(MutableFlowInstanceManager manager : nonSessionBoundInstanceManagers){

					if(manager.getInstanceManagerID().equals(id)){

						return true;
					}
				}

				return false;

			}finally{
				r.unlock();
			}
		}

		public int closeInstances(int flowInstanceID, QueryHandler queryHandler) {

			int closedCounter = 0;

			try{
				w.lock();

				for(MutableFlowInstanceManager manager : sessionBoundInstanceManagers){

					if(manager.getFlowInstanceID() != null && manager.getFlowInstanceID().equals(flowInstanceID)){

						manager.close(queryHandler);

						closedCounter++;
					}
				}

				for(MutableFlowInstanceManager manager : nonSessionBoundInstanceManagers){

					if(manager.getFlowInstanceID() != null && manager.getFlowInstanceID().equals(flowInstanceID)){

						manager.close(queryHandler);

						closedCounter++;
					}
				}

				return closedCounter;

			}finally{
				w.unlock();
			}
		}

		public int closeInstances(Flow flow, QueryHandler queryHandler) {

			int closedCounter = 0;

			try{
				w.lock();

				for(MutableFlowInstanceManager manager : sessionBoundInstanceManagers){

					if(manager.getFlowInstance().getFlow().getFlowID().equals(flow.getFlowID()) && !manager.isClosed()){

						manager.close(queryHandler);

						closedCounter++;
					}
				}

				for(MutableFlowInstanceManager manager : nonSessionBoundInstanceManagers){

					if(manager.getFlowInstance().getFlow().getFlowID().equals(flow.getFlowID()) && !manager.isClosed()){

						manager.close(queryHandler);

						closedCounter++;
					}
				}

				return closedCounter;

			}finally{
				w.unlock();
			}
		}
	}

	private static final long serialVersionUID = 7224301693975233582L;

	private final FlowInstanceManagerRegistery registery = FlowInstanceManagerRegistery.getInstance();

	private final FlowInstance flowInstance;
	private final List<ManagedStep> managedSteps;
	private Integer currentStepIndex;
	private boolean closed;
	private String instanceManagerID;
	private long created = System.currentTimeMillis();

	private boolean hasUnsavedChanges;

	private boolean concurrentModificationLock;

	/**
	 * Creates a new flow instance for the given flow and user
	 *
	 * @param flow The flow to create the instance from. The flow must include it's steps, default flow states, query descriptors and evaluator descriptors.
	 * @param instanceManagerID
	 * @param instanceMetadata
	 * @throws QueryNotFoundInQueryProviderException
	 * @throws QueryProviderNotFoundException
	 * @throws QueryProviderErrorException
	 * @throws DuplicateFlowInstanceManagerIDException
	 * @throws QueryInstanceNotFoundInQueryProviderException
	 * @throws EvaluatorNotFoundInEvaluationProviderException
	 * @throws EvaluationProviderErrorException
	 * @throws EvaluationProviderNotFoundException
	 */
	public MutableFlowInstanceManager(Flow flow, QueryHandler queryHandler, EvaluationHandler evaluationHandler, String instanceManagerID, HttpServletRequest req, User user, InstanceMetadata instanceMetadata) throws QueryProviderNotFoundException, QueryProviderErrorException, DuplicateFlowInstanceManagerIDException, QueryInstanceNotFoundInQueryProviderException, EvaluationProviderNotFoundException, EvaluationProviderErrorException, EvaluatorNotFoundInEvaluationProviderException {

		//Create new FlowInstance with default "new" state
		this.flowInstance = new FlowInstance();

		setID(instanceManagerID);

		TextTagReplacer.replaceTextTags(flow, instanceMetadata.getSiteProfile());

		flowInstance.setFlow(flow);

		//Parse steps
		managedSteps = new ArrayList<ManagedStep>(flow.getSteps().size());

		for(Step step : flow.getSteps()){

			if(step.getQueryDescriptors() == null){

				managedSteps.add(new ManagedStep(step, new ArrayList<ManagedQueryInstance>(0)));
				continue;
			}

			List<ManagedQueryInstance> managedQueryInstances = new ArrayList<ManagedQueryInstance>(step.getQueryDescriptors().size());

			for(QueryDescriptor queryDescriptor : step.getQueryDescriptors()){

				QueryInstanceDescriptor queryInstanceDescriptor = new QueryInstanceDescriptor(queryDescriptor);

				queryInstanceDescriptor.setQueryDescriptor(queryDescriptor);

				queryInstanceDescriptor.copyQueryDescriptorValues();

				QueryInstance queryInstance = queryHandler.getQueryInstance(queryInstanceDescriptor, instanceManagerID, req, user, instanceMetadata);

				List<Evaluator> evaluators = null;

				if(!CollectionUtils.isEmpty(queryDescriptor.getEvaluatorDescriptors())){

					evaluators = evaluationHandler.getEvaluators(queryDescriptor.getEvaluatorDescriptors());
				}

				managedQueryInstances.add(new ManagedQueryInstance(queryInstance, evaluators));
			}

			managedSteps.add(new ManagedStep(step, managedQueryInstances));
		}

		currentStepIndex = 0;

		flowInstance.setStepID(managedSteps.get(currentStepIndex).getStep().getStepID());
	}

	/**
	 * Opens an existing flow instance for the given user
	 *
	 * @param flowInstance The flow instance to be managed, must include it's flow, steps, default flow states, query descriptors, query instance descriptors relation and evaluator descriptors.
	 * @param instanceMetadata
	 * @throws MissingQueryInstanceDescriptor
	 * @throws QueryNotFoundInQueryProviderException
	 * @throws QueryProviderNotFoundException
	 * @throws InvalidFlowInstanceStepException
	 * @throws QueryProviderErrorException
	 * @throws DuplicateFlowInstanceManagerIDException
	 * @throws QueryInstanceNotFoundInQueryProviderException
	 * @throws EvaluatorNotFoundInEvaluationProviderException
	 * @throws EvaluationProviderErrorException
	 * @throws EvaluationProviderNotFoundException
	 */
	public MutableFlowInstanceManager(FlowInstance flowInstance, QueryHandler queryHandler, EvaluationHandler evaluationHandler, String instanceManagerID, HttpServletRequest req, User user, InstanceMetadata instanceMetadata) throws MissingQueryInstanceDescriptor, QueryProviderNotFoundException, InvalidFlowInstanceStepException, QueryProviderErrorException, DuplicateFlowInstanceManagerIDException, QueryInstanceNotFoundInQueryProviderException, EvaluationProviderNotFoundException, EvaluationProviderErrorException, EvaluatorNotFoundInEvaluationProviderException {

		this.flowInstance = flowInstance;

		setID(instanceManagerID);

		TextTagReplacer.replaceTextTags(flowInstance.getFlow(), instanceMetadata.getSiteProfile());

		managedSteps = new ArrayList<ManagedStep>(flowInstance.getFlow().getSteps().size());

		for(Step step : flowInstance.getFlow().getSteps()){

			if(step.getQueryDescriptors() == null){

				managedSteps.add(new ManagedStep(step, new ArrayList<ManagedQueryInstance>(0)));
				continue;
			}

			List<ManagedQueryInstance> managedQueryInstances = new ArrayList<ManagedQueryInstance>(step.getQueryDescriptors().size());

			for(QueryDescriptor queryDescriptor : step.getQueryDescriptors()){

				if(CollectionUtils.isEmpty(queryDescriptor.getQueryInstanceDescriptors())){

					throw new MissingQueryInstanceDescriptor(flowInstance, queryDescriptor);
				}

				QueryInstanceDescriptor queryInstanceDescriptor = queryDescriptor.getQueryInstanceDescriptors().get(0);

				//Reverse bean relations to avoid recursion problems when generating XML
				queryInstanceDescriptor.setQueryDescriptor(queryDescriptor);
				queryDescriptor.setQueryInstanceDescriptors(null);

				QueryInstance queryInstance = queryHandler.getQueryInstance(queryInstanceDescriptor, instanceManagerID, req, user, instanceMetadata);

				List<Evaluator> evaluators = null;

				if(!CollectionUtils.isEmpty(queryDescriptor.getEvaluatorDescriptors())){

					evaluators = evaluationHandler.getEvaluators(queryDescriptor.getEvaluatorDescriptors());
				}

				managedQueryInstances.add(new ManagedQueryInstance(queryInstance, evaluators));
			}

			managedSteps.add(new ManagedStep(step, managedQueryInstances));
		}

		if(flowInstance.isFullyPopulated() || flowInstance.getStepID() == null){

			currentStepIndex = 0;

		}else{

			int index = 0;

			for(ManagedStep managedStep : managedSteps){

				if(managedStep.getStep().getStepID().equals(flowInstance.getStepID())){

					currentStepIndex = index;
					break;
				}

				index++;
			}

			if(currentStepIndex == null){

				throw new InvalidFlowInstanceStepException(flowInstance);
			}
		}
	}

	private void setID(String instanceManagerID) throws DuplicateFlowInstanceManagerIDException {

		if(instanceManagerID == null){

			throw new NullPointerException("instanceManagerID cannot be null");

		}else if(registery.isActiveInstance(instanceManagerID)){

			throw new DuplicateFlowInstanceManagerIDException(flowInstance, instanceManagerID);
		}

		this.instanceManagerID = instanceManagerID;
	}

	public synchronized ManagerResponse getCurrentStepFormHTML(QueryHandler queryHandler, HttpServletRequest req, User user, String baseQueryRequestURL) throws UnableToGetQueryInstanceFormHTMLException, FlowInstanceManagerClosedException {

		checkState();

		ManagedStep currentStep = managedSteps.get(currentStepIndex);

		ArrayList<QueryResponse> queryResponses = new ArrayList<QueryResponse>(currentStep.getManagedQueryInstances().size());

		for(ManagedQueryInstance managedQueryInstance : currentStep.getManagedQueryInstances()){

			if(managedQueryInstance.getQueryInstance().getQueryInstanceDescriptor().getQueryState() != QueryState.HIDDEN){

				try{
					queryResponses.add(managedQueryInstance.getQueryInstance().getFormHTML(req, user, null, queryHandler, requiresAjaxPosting(managedQueryInstance.getQueryInstance(), currentStep), getQueryRequestURL(managedQueryInstance, baseQueryRequestURL)));
				}catch(Throwable e){
					throw new UnableToGetQueryInstanceFormHTMLException(managedQueryInstance.getQueryInstance().getQueryInstanceDescriptor(), e);
				}
			} else {
				queryResponses.add(new QueryResponse(managedQueryInstance.getQueryInstance().getQueryInstanceDescriptor().getQueryDescriptor()));
			}
		}

		return new ManagerResponse(currentStep.getStep().getStepID(), currentStepIndex, queryResponses, false, concurrentModificationLock);
	}

	public boolean requiresAjaxPosting(QueryInstance queryInstance, ManagedStep currentStep) {

		//Check if this query should enable ajax posting
		if(queryInstance.getQueryInstanceDescriptor().getQueryDescriptor().getEvaluatorDescriptors() != null){

			for(ImmutableEvaluatorDescriptor evaluatorDescriptor : queryInstance.getQueryInstanceDescriptor().getQueryDescriptor().getEvaluatorDescriptors()){

				if(evaluatorDescriptor.isEnabled() && evaluatorDescriptor.getTargetQueryIDs() != null){

					for(Integer queryID : evaluatorDescriptor.getTargetQueryIDs()){

						for(ManagedQueryInstance mQueryInstance : currentStep.getManagedQueryInstances()){

							if(mQueryInstance.getQueryInstance().getQueryInstanceDescriptor().getQueryDescriptor().getQueryID().equals(queryID)){

								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}

	public synchronized ManagerResponse getCurrentStepShowHTML(HttpServletRequest req, User user, FlowEngineInterface flowEngineInterface, boolean onlyPopulatedQueries, String baseUpdateURL, String baseQueryRequestURL) throws UnableToGetQueryInstanceShowHTMLException, FlowInstanceManagerClosedException {

		checkState();

		return getStepShowHTML(currentStepIndex, req, user, flowEngineInterface, onlyPopulatedQueries, baseUpdateURL, baseQueryRequestURL);
	}

	@Override
	public synchronized List<ManagerResponse> getFullShowHTML(HttpServletRequest req, User user, FlowEngineInterface flowEngineInterface, boolean onlyPopulatedQueries, String baseUpdateURL, String baseQueryRequestURL) throws UnableToGetQueryInstanceShowHTMLException, FlowInstanceManagerClosedException {

		checkState();

		List<ManagerResponse> managerResponses = new ArrayList<ManagerResponse>(this.managedSteps.size());

		for(int stepIndex = 0; stepIndex < this.managedSteps.size(); stepIndex++){

			managerResponses.add(getStepShowHTML(stepIndex, req, user, flowEngineInterface, onlyPopulatedQueries, baseUpdateURL, baseQueryRequestURL));
		}

		return managerResponses;
	}

	private synchronized ManagerResponse getStepShowHTML(int stepIndex, HttpServletRequest req, User user, FlowEngineInterface flowEngineInterface, boolean onlyPopulatedQueries, String baseUpdateURL, String baseQueryRequestURL) throws UnableToGetQueryInstanceShowHTMLException, FlowInstanceManagerClosedException {

		ManagedStep managedStep = managedSteps.get(stepIndex);

		String stepUpdateURL;

		if(baseUpdateURL == null){

			stepUpdateURL = null;

		}else{

			stepUpdateURL = baseUpdateURL + "?step=" + managedStep.getStep().getStepID();
		}

		ArrayList<QueryResponse> queryResponses = new ArrayList<QueryResponse>(managedStep.getManagedQueryInstances().size());

		for(ManagedQueryInstance managedQueryInstance : managedStep.getManagedQueryInstances()){

			if(managedQueryInstance.getQueryInstance().getQueryInstanceDescriptor().getQueryState() != QueryState.HIDDEN && !(onlyPopulatedQueries && !managedQueryInstance.getQueryInstance().getQueryInstanceDescriptor().isPopulated())){

				try{
					queryResponses.add(managedQueryInstance.getQueryInstance().getShowHTML(req, user, flowEngineInterface.getQueryHandler(), stepUpdateURL, getQueryRequestURL(managedQueryInstance, baseQueryRequestURL)));
				}catch(Throwable e){
					throw new UnableToGetQueryInstanceShowHTMLException(managedQueryInstance.getQueryInstance().getQueryInstanceDescriptor(), e);
				}
			}
		}

		return new ManagerResponse(managedStep.getStep().getStepID(), stepIndex, queryResponses, false, concurrentModificationLock);
	}

	private String getQueryRequestURL(ManagedQueryInstance managedQueryInstance, String baseQueryRequestURL) {

		return baseQueryRequestURL + managedQueryInstance.getQueryInstance().getQueryInstanceDescriptor().getQueryDescriptor().getQueryID();
	}

	public synchronized String populateQueryInCurrentStep(HttpServletRequest req, User user, int queryID, QueryHandler queryHandler, EvaluationHandler evaluationHandler) throws FlowInstanceManagerClosedException, UnableToPopulateQueryInstanceException, EvaluationException, QueryModificationException{

		checkState();

		ManagedQueryInstance managedQueryInstance = null;

		int queryIndex = 0;

		for(ManagedQueryInstance managedInstance : managedSteps.get(currentStepIndex).getManagedQueryInstances()){

			if(managedInstance.getQueryInstance().getQueryInstanceDescriptor().getQueryDescriptor().getQueryID().equals(queryID)){

				managedQueryInstance = managedInstance;

				break;
			}

			queryIndex++;
		}

		if(managedQueryInstance == null){

			return null;//"Query not found in current step error exception something....";

		}else if(managedQueryInstance.getQueryInstance().getQueryInstanceDescriptor().getQueryState() == QueryState.HIDDEN){

			return null;//"Query is currently hidden and we dont populate hidden queries error exception something....";
		}

		List<ValidationError> validationErrors = null;

		try{
			managedQueryInstance.getQueryInstance().populate(req, user, true, queryHandler, flowInstance.getAttributeHandler());

		}catch(RuntimeException e){

			throw new UnableToPopulateQueryInstanceException(managedQueryInstance.getQueryInstance().getQueryInstanceDescriptor(), e);

		}catch(ValidationException e){

			validationErrors = e.getErrors();
		}

		this.hasUnsavedChanges = true;

		List<QueryModification> queryModifications = null;

		if(managedQueryInstance.getEvaluators() != null){

			ManagedEvaluationCallback evaluationCallback = new ManagedEvaluationCallback(managedSteps, currentStepIndex, queryIndex);

			for(Evaluator evaluator : managedQueryInstance.getEvaluators()){

				if(!evaluator.getEvaluatorDescriptor().isEnabled()){

					continue;
				}

				try{
					EvaluationResponse response = evaluator.evaluate(managedQueryInstance.getQueryInstance(), evaluationCallback, evaluationHandler);

					if(response != null && !CollectionUtils.isEmpty(response.getValidationErrors())){

						if(validationErrors == null){

							validationErrors = response.getValidationErrors();

						}else{

							validationErrors.addAll(response.getValidationErrors());
						}
					}

					if(response != null && !CollectionUtils.isEmpty(response.getModifications())){

						if(queryModifications == null){

							queryModifications = response.getModifications();

						}else{

							queryModifications.addAll(response.getModifications());
						}
					}

				}catch(RuntimeException e){

					throw new EvaluationException(evaluator.getEvaluatorDescriptor(),e);
				}
			}
		}

		JsonObject response = new JsonObject();

		if(queryModifications != null) {

			String contextPath = req.getContextPath();

			JsonArray modifications = new JsonArray();

			for(QueryModification queryModification : queryModifications) {

				if(!isInCurrentStep(queryModification.getQueryInstance())){

					continue;
				}

				try {

					modifications.addNode(queryModification.toJson(req, user, queryHandler, this, managedSteps.get(currentStepIndex), contextPath));

				} catch (Throwable e) {

					throw new QueryModificationException(queryModification.getQueryInstance().getQueryInstanceDescriptor(), e);
				}

			}

			response.putField("QueryModifications", modifications);

		}

		if(validationErrors != null) {

			response.putField("ValidationErrors", JsonUtils.encode(validationErrors));

		}

		return response.toJson();
	}

	private boolean isInCurrentStep(QueryInstance queryInstance) {

		for(ManagedQueryInstance managedInstance : managedSteps.get(currentStepIndex).getManagedQueryInstances()){

			if(managedInstance.getQueryInstance().equals(queryInstance)){

				return true;
			}
		}

		return false;
	}

	public synchronized ManagerResponse populateCurrentStep(HttpServletRequest req, User user, FlowDirection flowDirection, QueryHandler queryHandler, EvaluationHandler evaluationHandler, String baseQueryRequestURL) throws UnableToPopulateQueryInstanceException, UnableToResetQueryInstanceException, UnableToGetQueryInstanceFormHTMLException, FlowInstanceManagerClosedException, EvaluationException {

		checkState();

		boolean allowPartialPopulation;

		if(flowDirection == FlowDirection.BACKWARD){

			//We are going backwards from the last non-populated step, allow partial population
			allowPartialPopulation = true;

		}else{

			allowPartialPopulation = false;
		}

		HashMap<Integer, List<ValidationError>> validationErrorMap = new HashMap<Integer, List<ValidationError>>();

		ManagedEvaluationCallback evaluationCallback = new ManagedEvaluationCallback(managedSteps, currentStepIndex, 0);

		//Iterate over all questions in the current step and populate them
		for(ManagedQueryInstance managedQueryInstance : managedSteps.get(currentStepIndex).getManagedQueryInstances()){

			QueryInstance queryInstance = managedQueryInstance.getQueryInstance();

			try{
				if(queryInstance.getQueryInstanceDescriptor().getQueryState() != QueryState.HIDDEN){

					try{
						queryInstance.populate(req, user, allowPartialPopulation, queryHandler, flowInstance.getAttributeHandler());

					}catch(RuntimeException e){
						throw new UnableToPopulateQueryInstanceException(queryInstance.getQueryInstanceDescriptor(), e);
					}

				}else{

					try{
						queryInstance.reset(flowInstance.getAttributeHandler());

					}catch(RuntimeException e){
						throw new UnableToResetQueryInstanceException(queryInstance.getQueryInstanceDescriptor(), e);
					}
				}

			}catch(ValidationException e){
				validationErrorMap.put(queryInstance.getQueryInstanceDescriptor().getQueryDescriptor().getQueryID(), e.getErrors());
			}

			if(managedQueryInstance.getEvaluators() != null){

				for(Evaluator evaluator : managedQueryInstance.getEvaluators()){

					if(!evaluator.getEvaluatorDescriptor().isEnabled()){

						continue;
					}

					try{
						EvaluationResponse response = evaluator.evaluate(queryInstance, evaluationCallback, evaluationHandler);

						if(response != null && !CollectionUtils.isEmpty(response.getValidationErrors())){

							List<ValidationError> validationErrors = validationErrorMap.get(queryInstance.getQueryInstanceDescriptor().getQueryDescriptor().getQueryID());

							if(validationErrors == null){

								validationErrorMap.put(queryInstance.getQueryInstanceDescriptor().getQueryDescriptor().getQueryID(), response.getValidationErrors());

							}else{

								validationErrors.addAll(response.getValidationErrors());
							}
						}
					}catch(RuntimeException e){

						throw new EvaluationException(evaluator.getEvaluatorDescriptor(),e);
					}
				}
			}

			evaluationCallback.incrementQueryIndex();
		}

		this.hasUnsavedChanges = true;

		//If we have any validation errors stay in the current step, else follow the requested flow direction
		if(!validationErrorMap.isEmpty()){

			ManagedStep currentStep = managedSteps.get(currentStepIndex);

			ArrayList<QueryResponse> queryResponses = new ArrayList<QueryResponse>(currentStep.getManagedQueryInstances().size());

			for(ManagedQueryInstance managedQueryInstance : currentStep.getManagedQueryInstances()){

				QueryInstance queryInstance = managedQueryInstance.getQueryInstance();

				if(queryInstance.getQueryInstanceDescriptor().getQueryState() != QueryState.HIDDEN){

					try{
						queryResponses.add(queryInstance.getFormHTML(req, user, validationErrorMap.get(queryInstance.getQueryInstanceDescriptor().getQueryDescriptor().getQueryID()), queryHandler, requiresAjaxPosting(managedQueryInstance.getQueryInstance(), currentStep), null));
					}catch(Throwable e){
						throw new UnableToGetQueryInstanceFormHTMLException(queryInstance.getQueryInstanceDescriptor(), e);
					}
				} else {
					queryResponses.add(new QueryResponse(managedQueryInstance.getQueryInstance().getQueryInstanceDescriptor().getQueryDescriptor()));
				}
			}

			flowInstance.setFullyPopulated(false);

			return new ManagerResponse(currentStep.getStep().getStepID(), currentStepIndex, queryResponses, true, concurrentModificationLock);
		}

		if(flowDirection == FlowDirection.STAY_PUT){

			if(currentStepIndex == (managedSteps.size() - 1)){

				flowInstance.setFullyPopulated(true);
			}

		}else if(flowDirection == FlowDirection.FORWARD){

			if(currentStepIndex < (managedSteps.size() - 1)){

				currentStepIndex++;
				flowInstance.setFullyPopulated(false);

			}else{

				flowInstance.setFullyPopulated(true);
			}

		}else if(flowDirection == FlowDirection.BACKWARD && currentStepIndex > 0){

			currentStepIndex--;
			flowInstance.setFullyPopulated(false);
		}

		flowInstance.setStepID(managedSteps.get(currentStepIndex).getStep().getStepID());

		return getCurrentStepFormHTML(queryHandler, req, user, baseQueryRequestURL);
	}

	public synchronized void saveInstance(FlowEngineInterface flowEngineInterface, User user) throws SQLException, UnableToSaveQueryInstanceException, FlowInstanceManagerClosedException {

		checkState();

		//Start transaction
		TransactionHandler transactionHandler = null;

		boolean isAdd = false;

		try{
			FlowEngineDAOFactory daoFactory = flowEngineInterface.getDAOFactory();

			transactionHandler = daoFactory.getTransactionHandler();

			if(flowInstance.getFlowInstanceID() == null){

				isAdd = true;

				flowInstance.setPoster(user);
				flowInstance.setAdded(TimeUtils.getCurrentTimestamp());

				//Add flow instance to database
				daoFactory.getFlowInstanceDAO().add(flowInstance, transactionHandler, FLOW_INSTANCE_SAVE_RELATIONS);

				for(ManagedStep managedStep : this.managedSteps){

					for(ManagedQueryInstance managedQueryInstance : managedStep.getManagedQueryInstances()){

						//Less nice cast for now...
						QueryInstanceDescriptor queryInstanceDescriptor = (QueryInstanceDescriptor)managedQueryInstance.getQueryInstance().getQueryInstanceDescriptor();

						//Set flowInstanceID on QueryInstanceDescriptor
						queryInstanceDescriptor.setFlowInstanceID(flowInstance.getFlowInstanceID());

						//Add QueryInstanceDescriptor
						daoFactory.getQueryInstanceDescriptorDAO().add(queryInstanceDescriptor, transactionHandler, null);
					}
				}

			}else{

				flowInstance.setUpdated(TimeUtils.getCurrentTimestamp());
				flowInstance.setEditor(user);

				//Update flow instance
				daoFactory.getFlowInstanceDAO().update(flowInstance, transactionHandler, FLOW_INSTANCE_SAVE_RELATIONS);

				//Update all query instance descriptors
				for(ManagedStep managedStep : this.managedSteps){

					for(ManagedQueryInstance managedQueryInstance : managedStep.getManagedQueryInstances()){

						//Less nice cast for now...
						QueryInstanceDescriptor queryInstanceDescriptor = (QueryInstanceDescriptor)managedQueryInstance.getQueryInstance().getQueryInstanceDescriptor();

						//Update QueryInstanceDescriptor
						daoFactory.getQueryInstanceDescriptorDAO().update(queryInstanceDescriptor, transactionHandler, FLOW_INSTANCE_SAVE_RELATIONS);
					}
				}
			}

			//Loop over each step
			for(ManagedStep managedStep : managedSteps){

				//Call save on each query instance
				for(ManagedQueryInstance managedQueryInstance : managedStep.getManagedQueryInstances()){

					try{
						managedQueryInstance.getQueryInstance().save(transactionHandler, flowEngineInterface.getQueryHandler());
					}catch(Throwable e){
						throw new UnableToSaveQueryInstanceException(managedQueryInstance.getQueryInstance().getQueryInstanceDescriptor(), e);
					}
				}
			}

			//Commit transaction
			transactionHandler.commit();

			this.hasUnsavedChanges = false;

		}finally{

			//Clear all autogenerated ID's if add operation fails
			if(isAdd && !transactionHandler.isCommited()){

				flowInstance.setFlowInstanceID(null);

				for(ManagedStep managedStep : this.managedSteps){

					for(ManagedQueryInstance managedQueryInstance : managedStep.getManagedQueryInstances()){

						//Less nice cast for now...
						QueryInstanceDescriptor queryInstanceDescriptor = (QueryInstanceDescriptor)managedQueryInstance.getQueryInstance().getQueryInstanceDescriptor();

						//Clear flowInstanceID on QueryInstanceDescriptor
						queryInstanceDescriptor.setQueryInstanceID(null);
					}
				}
			}

			TransactionHandler.autoClose(transactionHandler);
		}
	}

	public synchronized void close(QueryHandler queryHandler) {

		if(closed){

			return;
		}

		for(ManagedStep step : managedSteps){

			for(ManagedQueryInstance managedQueryInstance : step.getManagedQueryInstances()){

				managedQueryInstance.getQueryInstance().close(queryHandler);
			}
		}

		this.closed = true;
	}

	public synchronized void checkState() throws FlowInstanceManagerClosedException {

		if(closed){

			throw new FlowInstanceManagerClosedException(this.getFlowInstance(), this.instanceManagerID);
		}
	}

	public boolean setStep(Integer stepID){

		if(!isFullyPopulated()){

			return false;
		}

		int index = 0;

		while(index < this.managedSteps.size()){

			if(managedSteps.get(index).getStep().getStepID().equals(stepID)){

				if(isFullyPopulated() || index <= currentStepIndex){

					this.currentStepIndex = index;

					flowInstance.setFullyPopulated(false);

					return true;

				}else{

					return false;
				}
			}

			index++;
		}

		return false;
	}

	public synchronized boolean isPreviouslySaved() {

		return this.flowInstance.getFlowInstanceID() != null;
	}

	@Override
	public Integer getFlowInstanceID() {

		return flowInstance.getFlowInstanceID();
	}

	@Override
	public Integer getFlowID() {

		return flowInstance.getFlow().getFlowID();
	}

	@Override
	public Status getFlowState() {

		return flowInstance.getStatus();
	}

	public synchronized void setFlowState(Status flowState) {

		if(flowInstance.getStatus() == null || !flowInstance.getStatus().equals(flowState)) {
			flowInstance.setLastStatusChange(TimeUtils.getCurrentTimestamp());
		}

		flowInstance.setStatus(flowState);

	}

	public boolean isFullyPopulated() {

		return flowInstance.isFullyPopulated();
	}

	@Override
	public ImmutableFlowInstance getFlowInstance() {

		return flowInstance;
	}

	public boolean isClosed() {

		return closed;
	}

	public String getInstanceManagerID() {

		return instanceManagerID;
	}

	public long getCreated() {

		return created;
	}

	@Override
	public void valueBound(HttpSessionBindingEvent sessionBindingEvent) {

		this.registery.addSessionBoundInstance(this);
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent sessionBindingEvent) {

		this.registery.removeSessionBoundInstance(this);
	}

	public ImmutableStep getCurrentStep() {

		return this.managedSteps.get(currentStepIndex).getStep();
	}

	public Integer getCurrentStepIndex() {

		return currentStepIndex;
	}

	@Override
	public String toString(){

		return flowInstance.toString();
	}

	@Override
	public ImmutableQueryInstance getQueryInstance(int queryID) {

		for(ManagedStep managedStep : this.managedSteps){

			for(ManagedQueryInstance managedQueryInstance : managedStep.getManagedQueryInstances()){

				if(managedQueryInstance.getQueryInstance().getQueryInstanceDescriptor().getQueryDescriptor().getQueryID() == queryID){

					return managedQueryInstance.getQueryInstance();
				}
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ImmutableQueryInstance> T getQuery(Class<T> queryInstanceClass) {

		for(ManagedStep managedStep : this.managedSteps){

			for(ManagedQueryInstance managedQueryInstance : managedStep.getManagedQueryInstances()){

				if(queryInstanceClass.isAssignableFrom(managedQueryInstance.getQueryInstance().getClass())){

					return (T)managedQueryInstance.getQueryInstance();
				}
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ImmutableQueryInstance> T getQuery(Class<T> queryInstanceClass, String name) {

		for(ManagedStep managedStep : this.managedSteps){

			for(ManagedQueryInstance managedQueryInstance : managedStep.getManagedQueryInstances()){

				if(queryInstanceClass.isAssignableFrom(managedQueryInstance.getQueryInstance().getClass()) && managedQueryInstance.getQueryInstance().getQueryInstanceDescriptor().getQueryDescriptor().getName().equals(name)){

					return (T)managedQueryInstance.getQueryInstance();
				}
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> getQueries(Class<T> queryInstanceClass) {

		List<T> queryList = new ArrayList<T>();

		for(ManagedStep managedStep : this.managedSteps){

			for(ManagedQueryInstance managedQueryInstance : managedStep.getManagedQueryInstances()){

				if(queryInstanceClass.isAssignableFrom(managedQueryInstance.getQueryInstance().getClass())){

					queryList.add((T) managedQueryInstance.getQueryInstance());
				}
			}
		}

		if(queryList.isEmpty()){

			return null;
		}

		return queryList;
	}

	@Override
	public List<PDFManagerResponse> getPDFContent(FlowEngineInterface flowEngineInterface) throws FlowInstanceManagerClosedException, UnableToGetQueryInstancePDFContentException {

		List<PDFManagerResponse> managerResponses = new ArrayList<PDFManagerResponse>(this.managedSteps.size());

		for(int stepIndex=0; stepIndex < this.managedSteps.size(); stepIndex++){

			ManagedStep managedStep = managedSteps.get(stepIndex);

			ArrayList<PDFQueryResponse> queryResponses = new ArrayList<PDFQueryResponse>(managedStep.getManagedQueryInstances().size());

			for(ManagedQueryInstance managedQueryInstance : managedStep.getManagedQueryInstances()){

				MutableQueryInstanceDescriptor queryInstanceDescriptor = managedQueryInstance.getQueryInstance().getQueryInstanceDescriptor();

				if(queryInstanceDescriptor.getQueryState() != QueryState.HIDDEN && queryInstanceDescriptor.isPopulated()){

					try{
						queryResponses.add(managedQueryInstance.getQueryInstance().getPDFContent(flowEngineInterface.getQueryHandler()));
					}catch(Throwable e){
						throw new UnableToGetQueryInstancePDFContentException(queryInstanceDescriptor, e);
					}
				}
			}

			managerResponses.add(new PDFManagerResponse(managedStep.getStep().getStepID(), stepIndex, queryResponses));
		}

		return managerResponses;
	}

	public boolean hasUnsavedChanges() {

		return hasUnsavedChanges;
	}

	public boolean isConcurrentModificationLocked() {

		return concurrentModificationLock;
	}

	public void setConcurrentModificationLock(boolean concurrentModificationLock) {

		this.concurrentModificationLock = concurrentModificationLock;
	}

	@Override
	public List<Element> getExportXMLElements(Document doc, QueryHandler queryHandler) throws Exception {

		List<Element> elements = new ArrayList<Element>(this.managedSteps.size() * 5);

		for(ManagedStep managedStep : this.managedSteps){

			for(ManagedQueryInstance managedQueryInstance : managedStep.getManagedQueryInstances()){

				if(managedQueryInstance.getQueryInstance().getQueryInstanceDescriptor().getQueryDescriptor().isExported() && managedQueryInstance.getQueryInstance().getQueryInstanceDescriptor().isPopulated()){

					Element queryElement = managedQueryInstance.getQueryInstance().toExportXML(doc, queryHandler);

					if(queryElement != null){

						elements.add(queryElement);
					}
				}
			}
		}

		return elements;
	}
}
