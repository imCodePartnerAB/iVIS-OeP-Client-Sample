package com.nordicpeak.flowengine.evaluators.querystateevaluator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import se.unlogic.hierarchy.core.annotations.InstanceManagerDependency;
import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.dao.AnnotatedDAO;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.QueryParameterFactory;
import se.unlogic.standardutils.dao.SimpleAnnotatedDAOFactory;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.db.tableversionhandler.TableVersionHandler;
import se.unlogic.standardutils.db.tableversionhandler.UpgradeResult;
import se.unlogic.standardutils.db.tableversionhandler.XMLDBScriptProvider;
import se.unlogic.standardutils.xml.XMLGenerator;
import se.unlogic.webutils.http.URIParser;

import com.nordicpeak.flowengine.beans.EvaluationResponse;
import com.nordicpeak.flowengine.beans.QueryModification;
import com.nordicpeak.flowengine.enums.ModificationAction;
import com.nordicpeak.flowengine.enums.QueryState;
import com.nordicpeak.flowengine.evaluators.baseevaluator.BaseEvaluationProviderModule;
import com.nordicpeak.flowengine.exceptions.queryinstance.IllegalQueryInstanceAccessException;
import com.nordicpeak.flowengine.interfaces.EvaluationCallback;
import com.nordicpeak.flowengine.interfaces.Evaluator;
import com.nordicpeak.flowengine.interfaces.ImmutableAlternative;
import com.nordicpeak.flowengine.interfaces.ImmutableEvaluatorDescriptor;
import com.nordicpeak.flowengine.interfaces.MutableEvaluatorDescriptor;
import com.nordicpeak.flowengine.interfaces.Query;
import com.nordicpeak.flowengine.interfaces.QueryHandler;
import com.nordicpeak.flowengine.interfaces.QueryInstance;
import com.nordicpeak.flowengine.queries.fixedalternativesquery.FixedAlternativesQuery;
import com.nordicpeak.flowengine.queries.fixedalternativesquery.FixedAlternativesQueryInstance;


public class QueryStateEvaluationProviderModule extends BaseEvaluationProviderModule<QueryStateEvaluator> {

	@Override
	public Evaluator importEvaluator(MutableEvaluatorDescriptor descriptor, TransactionHandler transactionHandler, Query query) throws Throwable {

		QueryStateEvaluator evaluator = new QueryStateEvaluator();

		evaluator.setEvaluatorID(descriptor.getEvaluatorID());

		evaluator.populate(descriptor.getImportParser().getNode(XMLGenerator.getElementName(evaluator.getClass())));

		if(evaluator.getRequiredAlternativeIDs() != null && query instanceof FixedAlternativesQuery && ((FixedAlternativesQuery)query).getAlternativeConversionMap() != null){
			
			Map<Integer,Integer> conversionMap = ((FixedAlternativesQuery)query).getAlternativeConversionMap();
			
			List<Integer> newIDList = new ArrayList<Integer>(evaluator.getRequiredAlternativeIDs().size());
			
			for(Integer alternativeID : evaluator.getRequiredAlternativeIDs()){
				
				Integer newID = conversionMap.get(alternativeID);
				
				if(newID != null){
					
					newIDList.add(newID);
				}
			}
			
			evaluator.setRequiredAlternativeIDs(newIDList);
		}
		
		this.evaluatorDAO.add(evaluator, transactionHandler, null);

		return evaluator;
	}

	@InstanceManagerDependency(required=true)
	protected QueryHandler queryHandler;

	protected AnnotatedDAO<QueryStateEvaluator> evaluatorDAO;
	protected QueryParameterFactory<QueryStateEvaluator, Integer> evaluatorIDParamFactory;

	protected QueryStateEvaluatorCRUD evaluatorCRUD;

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		//Automatic table version handling
		UpgradeResult upgradeResult = TableVersionHandler.upgradeDBTables(dataSource, QueryStateEvaluationProviderModule.class.getName(), new XMLDBScriptProvider(this.getClass().getResourceAsStream("DB script.xml")));

		if (upgradeResult.isUpgrade()) {

			log.info(upgradeResult.toString());
		}

		evaluatorDAO = new SimpleAnnotatedDAOFactory(dataSource).getDAO(QueryStateEvaluator.class);
		evaluatorIDParamFactory = evaluatorDAO.getParamFactory("evaluatorID", Integer.class);

		evaluatorCRUD = new QueryStateEvaluatorCRUD(QueryStateEvaluator.class, evaluatorDAO.getWrapper("evaluatorID", Integer.class), this);
	}

	@WebPublic(alias = "config")
	public ForegroundModuleResponse configureEvaluator(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return this.evaluatorCRUD.update(req, res, user, uriParser);
	}

	@Override
	public EvaluationResponse evaluate(QueryInstance queryInstance, QueryStateEvaluator evaluator, EvaluationCallback callback) {

		if(evaluator.getRequiredAlternativeIDs() == null || evaluator.getEvaluatorDescriptor().getTargetQueryIDs() == null){

			return null;
		}

		if(!(queryInstance instanceof FixedAlternativesQueryInstance)){

			log.warn("Query instance " + queryInstance + " does not implement interface " + FixedAlternativesQueryInstance.class.getName());

			return null;
		}

		List<? extends ImmutableAlternative> alternatives = ((FixedAlternativesQueryInstance)queryInstance).getAlternatives();

		if(CollectionUtils.isEmpty(alternatives)){

			return restoreDefaultQueryStates(queryInstance, evaluator, callback);

		}else if(evaluator.getSelectionMode() == SelectionMode.ANY){

			for(ImmutableAlternative alternative : alternatives){

				if(evaluator.getRequiredAlternativeIDs().contains(alternative.getAlternativeID())){

					return applyEvaluatorQueryStates(queryInstance, evaluator, callback);
				}
			}

			return restoreDefaultQueryStates(queryInstance, evaluator, callback);

		}else if(evaluator.getSelectionMode() == SelectionMode.ALL){

			outer: for(Integer alternativeID : evaluator.getRequiredAlternativeIDs()){

				for(ImmutableAlternative alternative : alternatives){

					if(alternative.getAlternativeID().equals(alternativeID)){

						continue outer;
					}
				}

				return restoreDefaultQueryStates(queryInstance, evaluator, callback);
			}

			return applyEvaluatorQueryStates(queryInstance, evaluator, callback);

		}else{

			throw new RuntimeException("Unknown selection mode: " + evaluator.getSelectionMode());
		}
	}

	private EvaluationResponse applyEvaluatorQueryStates(QueryInstance queryInstance, QueryStateEvaluator evaluator, EvaluationCallback callback) {

		List<QueryModification> queryModifications = null;

		QueryState evaluatorQueryState = evaluator.getQueryState();

		for(Integer queryID : evaluator.getEvaluatorDescriptor().getTargetQueryIDs()){

			try{
				QueryInstance targetInstance = callback.getQueryInstance(queryID);

				if(targetInstance == null){

					continue;
				}

				if(targetInstance.getQueryInstanceDescriptor().getQueryState() != evaluatorQueryState){

					targetInstance.getQueryInstanceDescriptor().setQueryState(evaluatorQueryState);

					if(evaluatorQueryState == QueryState.HIDDEN){

						queryModifications = CollectionUtils.addAndInstantiateIfNeeded(queryModifications, new QueryModification(targetInstance, ModificationAction.HIDE));

					}else if(evaluatorQueryState == QueryState.VISIBLE){

						queryModifications = CollectionUtils.addAndInstantiateIfNeeded(queryModifications, new QueryModification(targetInstance, ModificationAction.SHOW));

					}else if(evaluatorQueryState == QueryState.VISIBLE_REQUIRED){

						queryModifications = CollectionUtils.addAndInstantiateIfNeeded(queryModifications, new QueryModification(targetInstance, ModificationAction.MAKE_REQUIRED));

					}else{

						throw new RuntimeException("Unknown query state: " + evaluatorQueryState);
					}
				}

			}catch(IllegalQueryInstanceAccessException e){

				throw new RuntimeException(e);
			}
		}

		return new EvaluationResponse(queryModifications, null);
	}

	private EvaluationResponse restoreDefaultQueryStates(QueryInstance queryInstance, QueryStateEvaluator evaluator, EvaluationCallback callback) {

		if(evaluator.isDoNotResetQueryState()){
			
			return null;
		}
		
		List<QueryModification> queryModifications = null;

		for(Integer queryID : evaluator.getEvaluatorDescriptor().getTargetQueryIDs()){

			try{
				QueryInstance targetInstance = callback.getQueryInstance(queryID);

				if(targetInstance == null){

					continue;
				}

				QueryState defaultQueryState = targetInstance.getQueryInstanceDescriptor().getQueryDescriptor().getDefaultQueryState();

				if(targetInstance.getQueryInstanceDescriptor().getQueryState() != defaultQueryState){

					targetInstance.getQueryInstanceDescriptor().setQueryState(defaultQueryState);

					if(defaultQueryState == QueryState.HIDDEN){

						queryModifications = CollectionUtils.addAndInstantiateIfNeeded(queryModifications, new QueryModification(targetInstance, ModificationAction.HIDE));

					}else if(defaultQueryState == QueryState.VISIBLE){

						queryModifications = CollectionUtils.addAndInstantiateIfNeeded(queryModifications, new QueryModification(targetInstance, ModificationAction.SHOW));

					}else if(defaultQueryState == QueryState.VISIBLE_REQUIRED){

						queryModifications = CollectionUtils.addAndInstantiateIfNeeded(queryModifications, new QueryModification(targetInstance, ModificationAction.MAKE_REQUIRED));

					}else{

						throw new RuntimeException("Unknown query state: " + defaultQueryState);
					}
				}

			}catch(IllegalQueryInstanceAccessException e){

				throw new RuntimeException(e);
			}
		}

		return new EvaluationResponse(queryModifications, null);
	}

	@Override
	public Evaluator createEvaluator(MutableEvaluatorDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable {

		QueryStateEvaluator evaluator = new QueryStateEvaluator();

		evaluator.setEvaluatorID(descriptor.getEvaluatorID());
		evaluator.setQueryState(QueryState.VISIBLE);
		evaluator.setSelectionMode(SelectionMode.ANY);

		this.evaluatorDAO.add(evaluator, transactionHandler, null);

		evaluator.init(descriptor, getFullAlias() + "/config/" + descriptor.getEvaluatorID());

		return evaluator;
	}

	@Override
	public Evaluator getEvaluator(MutableEvaluatorDescriptor descriptor) throws Throwable {

		QueryStateEvaluator evaluator = evaluatorDAO.get(new HighLevelQuery<QueryStateEvaluator>(evaluatorIDParamFactory.getParameter(descriptor.getEvaluatorID())));

		if(evaluator != null){

			evaluator.init(descriptor, getFullAlias() + "/config/" + descriptor.getEvaluatorID());
		}

		return evaluator;
	}

	@Override
	public boolean deleteEvaluator(ImmutableEvaluatorDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable {

		evaluatorDAO.delete(new HighLevelQuery<QueryStateEvaluator>(evaluatorIDParamFactory.getParameter(descriptor.getEvaluatorID())), transactionHandler);

		return true;
	}

	@Override
	public boolean supportsQueryType(Class<? extends Query> queryClass) {

		return FixedAlternativesQuery.class.isAssignableFrom(queryClass);
	}

	public QueryHandler getQueryHandler() {

		return this.queryHandler;
	}

	@Override
	public void copyEvaluation(MutableEvaluatorDescriptor sourceEvaluatorDescriptor, MutableEvaluatorDescriptor copyEvaluatorDescriptor, Query sourceQuery, Query copyQuery, TransactionHandler transactionHandler) throws SQLException {

		QueryStateEvaluator evaluator = evaluatorDAO.get(new HighLevelQuery<QueryStateEvaluator>(evaluatorIDParamFactory.getParameter(sourceEvaluatorDescriptor.getEvaluatorID())));

		evaluator.setEvaluatorID(copyEvaluatorDescriptor.getEvaluatorID());

		if(evaluator.getRequiredAlternativeIDs() != null){

			List<? extends ImmutableAlternative> sourceAlternatives = ((FixedAlternativesQuery)sourceQuery).getAlternatives();
			List<? extends ImmutableAlternative> copyAlternatives = ((FixedAlternativesQuery)copyQuery).getAlternatives();

			List<Integer> newRequiredAlternativeIDs = new ArrayList<Integer>(evaluator.getRequiredAlternativeIDs().size());

			for(Integer requiredAlternativeID : evaluator.getRequiredAlternativeIDs()){

				int alternativeIndex = 0;

				while(alternativeIndex < sourceAlternatives.size()){

					if(sourceAlternatives.get(alternativeIndex).getAlternativeID().equals(requiredAlternativeID)){

						newRequiredAlternativeIDs.add(copyAlternatives.get(alternativeIndex).getAlternativeID());

						break;
					}

					alternativeIndex++;
				}
			}

			evaluator.setRequiredAlternativeIDs(newRequiredAlternativeIDs);
		}

		this.evaluatorDAO.add(evaluator, transactionHandler, null);
	}
}
