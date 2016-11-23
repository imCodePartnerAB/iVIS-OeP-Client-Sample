package com.nordicpeak.flowengine.evaluators.querystateevaluator;

import java.util.ArrayList;
import java.util.List;

import se.unlogic.standardutils.annotations.WebPopulate;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Key;
import se.unlogic.standardutils.dao.annotations.OneToMany;
import se.unlogic.standardutils.dao.annotations.SimplifiedRelation;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.populators.EnumPopulator;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLElement;
import se.unlogic.standardutils.xml.XMLParser;
import se.unlogic.standardutils.xml.XMLValidationUtils;

import com.nordicpeak.flowengine.enums.QueryState;
import com.nordicpeak.flowengine.evaluators.baseevaluator.BaseEvaluator;

@Table(name = "query_state_evaluators")
@XMLElement
public class QueryStateEvaluator extends BaseEvaluator {

	private static final long serialVersionUID = -7745436926817563261L;

	@DAOManaged
	@Key
	@XMLElement
	private Integer evaluatorID;

	@DAOManaged
	@WebPopulate(required = true)
	@XMLElement
	private SelectionMode selectionMode;

	@DAOManaged
	@OneToMany(autoAdd = true, autoGet = true, autoUpdate = true)
	@SimplifiedRelation(table = "query_state_evaluator_alternatives", remoteValueColumnName = "alternativeID")
	@WebPopulate(paramName="alternativeID")
	@XMLElement(fixCase=true, childName="alternativeID")
	private List<Integer> requiredAlternativeIDs;

	@DAOManaged
	@WebPopulate(required = true)
	@XMLElement
	private QueryState queryState;

	@DAOManaged
	@WebPopulate
	@XMLElement
	private boolean doNotResetQueryState;
	
	@Override
	public Integer getEvaluatorID() {

		return evaluatorID;
	}

	public void setEvaluatorID(Integer evaluatorID) {

		this.evaluatorID = evaluatorID;
	}

	public List<Integer> getRequiredAlternativeIDs() {

		return requiredAlternativeIDs;
	}

	public void setRequiredAlternativeIDs(List<Integer> requiredAlternativeIDs) {

		this.requiredAlternativeIDs = requiredAlternativeIDs;
	}

	public QueryState getQueryState() {

		return queryState;
	}

	public void setQueryState(QueryState queryState) {

		this.queryState = queryState;
	}

	public SelectionMode getSelectionMode() {

		return selectionMode;
	}

	public void setSelectionMode(SelectionMode selectionMode) {

		this.selectionMode = selectionMode;
	}

	@Override
	public void populate(XMLParser xmlParser) throws ValidationException {

		List<ValidationError> errors = new ArrayList<ValidationError>();
		
		selectionMode = XMLValidationUtils.validateParameter("selectionMode", xmlParser, true, QueryStateEvaluatorCRUD.SELECTIONMODE_POPULATOR, errors);
		queryState = XMLValidationUtils.validateParameter("queryState", xmlParser, true, new EnumPopulator<QueryState>(QueryState.class), errors);
		
		requiredAlternativeIDs  = xmlParser.getIntegers("RequiredAlternativeIDs/alternativeID");
		
		doNotResetQueryState = xmlParser.getPrimitiveBoolean("doNotResetQueryState");
	}

	
	public boolean isDoNotResetQueryState() {
	
		return doNotResetQueryState;
	}

	
	public void setDoNotResetQueryState(boolean doNotResetQueryState) {
	
		this.doNotResetQueryState = doNotResetQueryState;
	}
	
	@Override
	public String toString(){
		
		return evaluatorDescriptor.toString();
	}
}
