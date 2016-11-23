package com.nordicpeak.flowengine.cruds;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.CRUDAction;
import se.unlogic.hierarchy.core.enums.EventTarget;
import se.unlogic.hierarchy.core.events.CRUDEvent;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.utils.IntegerBasedCRUD;
import se.unlogic.standardutils.dao.CRUDDAO;
import se.unlogic.standardutils.dao.querys.ObjectQuery;
import se.unlogic.standardutils.populators.IntegerPopulator;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.populators.annotated.AnnotatedRequestPopulator;

import com.nordicpeak.flowengine.FlowAdminModule;
import com.nordicpeak.flowengine.beans.Flow;
import com.nordicpeak.flowengine.beans.Step;


public class StepCRUD extends IntegerBasedCRUD<Step, FlowAdminModule> {

	public StepCRUD(CRUDDAO<Step, Integer> crudDAO, FlowAdminModule callback) {

		super(crudDAO, new AnnotatedRequestPopulator<Step>(Step.class), "Step", "step", "", callback);
	}

	@Override
	protected void checkAddAccess(User user, HttpServletRequest req, URIParser uriParser) throws AccessDeniedException, URINotFoundException, SQLException {

		Flow flow = callback.getRequestedFlow(req, user, uriParser);

		if(flow == null){

			throw new URINotFoundException(uriParser);
		}

		callback.checkFlowStructureManipulationAccess(user, flow);
		
		req.setAttribute("flow", flow);
	}

	@Override
	protected void validateAddPopulation(Step bean, HttpServletRequest req, User user, URIParser uriParser) throws ValidationException, SQLException, Exception {

		bean.setFlow((Flow) req.getAttribute("flow"));
		bean.setSortIndex(getCurrentMaxSortIndex(bean.getFlow()) + 1);
	}	
	
	@Override
	protected void checkUpdateAccess(Step bean, User user, HttpServletRequest req, URIParser uriParser) throws AccessDeniedException, URINotFoundException, SQLException {

		callback.checkFlowStructureManipulationAccess(user, bean.getFlow());
	}	
	
	@Override
	protected void checkDeleteAccess(Step bean, User user, HttpServletRequest req, URIParser uriParser) throws AccessDeniedException, URINotFoundException, SQLException {

		callback.checkFlowStructureManipulationAccess(user, bean.getFlow());
	}	
	
	private Integer getCurrentMaxSortIndex(Flow flow) throws SQLException {

		ObjectQuery<Integer> query = new ObjectQuery<Integer>(callback.getDataSource(), "SELECT MAX(sortIndex) FROM " + callback.getDAOFactory().getStepDAO().getTableName() + " WHERE flowID = ?", IntegerPopulator.getPopulator());
		
		query.setInt(1, flow.getFlowID());
		
		Integer sortIndex = query.executeQuery();
		
		if(sortIndex == null){
			
			return 0;
		}
		
		return sortIndex;
	}	
	
	@Override
	protected ForegroundModuleResponse beanAdded(Step bean, HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return beanEvent(bean, req, res, CRUDAction.ADD);
	}

	@Override
	protected ForegroundModuleResponse beanUpdated(Step bean, HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return beanEvent(bean, req, res, CRUDAction.UPDATE);
	}

	@Override
	protected ForegroundModuleResponse beanDeleted(Step bean, HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return beanEvent(bean, req, res, CRUDAction.DELETE);
	}

	private ForegroundModuleResponse beanEvent(Step bean, HttpServletRequest req, HttpServletResponse res, CRUDAction action) throws IOException{
		
		callback.getEventHandler().sendEvent(Step.class, new CRUDEvent<Step>(action, bean), EventTarget.ALL);

		callback.redirectToMethod(req, res, "/showflow/" + bean.getFlow().getFlowID() + "#steps");
		
		return null;
	}
	
	@Override
	public ForegroundModuleResponse list(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, List<ValidationError> validationErrors) throws Exception {

		return callback.list(req, res, user, uriParser, validationErrors);
	}
}
