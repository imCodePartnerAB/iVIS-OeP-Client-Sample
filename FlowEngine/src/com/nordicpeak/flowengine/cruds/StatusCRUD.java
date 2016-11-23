package com.nordicpeak.flowengine.cruds;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.CRUDAction;
import se.unlogic.hierarchy.core.enums.EventTarget;
import se.unlogic.hierarchy.core.events.CRUDEvent;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.utils.AccessUtils;
import se.unlogic.hierarchy.core.utils.IntegerBasedCRUD;
import se.unlogic.standardutils.dao.CRUDDAO;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.QueryParameterFactory;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.populators.annotated.AnnotatedRequestPopulator;

import com.nordicpeak.flowengine.FlowAdminModule;
import com.nordicpeak.flowengine.beans.DefaultStatusMapping;
import com.nordicpeak.flowengine.beans.Flow;
import com.nordicpeak.flowengine.beans.FlowAction;
import com.nordicpeak.flowengine.beans.FlowType;
import com.nordicpeak.flowengine.beans.Status;


public class StatusCRUD extends IntegerBasedCRUD<Status, FlowAdminModule> {

	private QueryParameterFactory<FlowAction, String> flowActionIDParamFactory;
	private QueryParameterFactory<DefaultStatusMapping, String> defaultStatusMappingActionIDParamFactory;
	private QueryParameterFactory<DefaultStatusMapping, Flow> defaultStatusMappingFlowParamFactory;
	private QueryParameterFactory<DefaultStatusMapping, Status> defaultStatusMappingStatusParamFactory;
	
	public StatusCRUD(CRUDDAO<Status, Integer> crudDAO, FlowAdminModule callback) {

		super(crudDAO, new AnnotatedRequestPopulator<Status>(Status.class), "Status", "status", "", callback);
		
		flowActionIDParamFactory = callback.getDAOFactory().getFlowActionDAO().getParamFactory("actionID", String.class);
		defaultStatusMappingActionIDParamFactory = callback.getDAOFactory().getDefaultStatusMappingDAO().getParamFactory("actionID", String.class);
		defaultStatusMappingFlowParamFactory = callback.getDAOFactory().getDefaultStatusMappingDAO().getParamFactory("flow", Flow.class);
		defaultStatusMappingStatusParamFactory = callback.getDAOFactory().getDefaultStatusMappingDAO().getParamFactory("status", Status.class);
	}

	@Override
	protected void checkAddAccess(User user, HttpServletRequest req, URIParser uriParser) throws AccessDeniedException, URINotFoundException, SQLException {

		Flow flow = callback.getRequestedFlow(req, user, uriParser);

		if(flow == null){

			throw new URINotFoundException(uriParser);
		}

		checkFlowTypeAccess(user, flow.getFlowType());
		
		req.setAttribute("flow", flow);
	}

	@Override
	protected void checkUpdateAccess(Status bean, User user, HttpServletRequest req, URIParser uriParser) throws AccessDeniedException, URINotFoundException, SQLException {

		checkFlowTypeAccess(user, bean.getFlow().getFlowType());
	}	
	
	@Override
	protected void checkDeleteAccess(Status bean, User user, HttpServletRequest req, URIParser uriParser) throws AccessDeniedException, URINotFoundException, SQLException {

		checkFlowTypeAccess(user, bean.getFlow().getFlowType());
		
		if(callback.getFlowInstanceCount(bean) > 0){
			
			throw new AccessDeniedException("Unable to delete status " + bean + " since it has one or more flow instances connected to it.");
		}
	}
	
	private void checkFlowTypeAccess(User user, FlowType flowType) throws AccessDeniedException {

		if(!AccessUtils.checkAccess(user, flowType)){

			throw new AccessDeniedException("User does not have access to flow type " + flowType);
		}
	}
	
	@Override
	protected ForegroundModuleResponse beanAdded(Status bean, HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return beanEvent(bean, req, res, CRUDAction.ADD);
	}
	
	@Override
	protected ForegroundModuleResponse beanUpdated(Status bean, HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return beanEvent(bean, req, res, CRUDAction.UPDATE);
	}	
		
	@Override
	protected ForegroundModuleResponse beanDeleted(Status bean, HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return beanEvent(bean, req, res, CRUDAction.DELETE);
	}
	
	private ForegroundModuleResponse beanEvent(Status bean, HttpServletRequest req, HttpServletResponse res, CRUDAction action) throws IOException{
		
		callback.getEventHandler().sendEvent(Status.class, new CRUDEvent<Status>(action, bean), EventTarget.ALL);

		callback.redirectToMethod(req, res, "/showflow/" + bean.getFlow().getFlowID() + "#statuses");
		
		return null;
	}
	
	
	
	@Override
	protected void appendAddFormData(Document doc, Element addTypeElement, User user, HttpServletRequest req, URIParser uriParser) throws Exception {

		XMLUtils.append(doc, addTypeElement, "FlowActions", callback.getDAOFactory().getFlowActionDAO().getAll());
	}	

	@Override
	protected void appendUpdateFormData(Status bean, Document doc, Element updateTypeElement, User user, HttpServletRequest req, URIParser uriParser) throws Exception {

		XMLUtils.append(doc, updateTypeElement, "FlowActions", callback.getDAOFactory().getFlowActionDAO().getAll());
	}	
	
	@Override
	protected void validateAddPopulation(Status bean, HttpServletRequest req, User user, URIParser uriParser) throws ValidationException, SQLException, Exception {

		bean.setFlow((Flow) req.getAttribute("flow"));
		
		setDefaultStatusMappings(bean, req);
	}

	@Override
	protected void validateUpdatePopulation(Status bean, HttpServletRequest req, User user, URIParser uriParser) throws ValidationException, SQLException, Exception {

		setDefaultStatusMappings(bean, req);
	}
	
	private void setDefaultStatusMappings(Status bean, HttpServletRequest req) throws SQLException {

		String[] actions = req.getParameterValues("actionID");
		
		if(actions != null){
						
			List<FlowAction> flowActions = getFlowActions(Arrays.asList(actions));
			
			if(flowActions != null){
								
				ArrayList<DefaultStatusMapping> defaultStatusMappings = new ArrayList<DefaultStatusMapping>(flowActions.size());
				bean.setDefaultStatusMappings(defaultStatusMappings);
				
				for(FlowAction flowAction : flowActions){
					
					defaultStatusMappings.add(new DefaultStatusMapping(flowAction.getActionID(),bean.getFlow(),bean));
				}
				
				return;
			}
		}
		
		bean.setDefaultStatusMappings(null);
	}

	private List<FlowAction> getFlowActions(List<String> actionIDs) throws SQLException {

		HighLevelQuery<FlowAction> query = new HighLevelQuery<FlowAction>();
		
		query.addParameter(flowActionIDParamFactory.getWhereInParameter(actionIDs));
		
		return callback.getDAOFactory().getFlowActionDAO().getAll(query);
	}

	@Override
	protected void addBean(Status bean, HttpServletRequest req, User user, URIParser uriParser) throws Exception {

		TransactionHandler transactionHandler = null;
		
		try{
			transactionHandler = callback.getDAOFactory().getTransactionHandler();
			
			callback.getDAOFactory().getStatusDAO().add(bean, transactionHandler, null);
			
			if(bean.getDefaulStatusMappings() != null){
				
				clearPreviousActionMappings(bean, transactionHandler);
				callback.getDAOFactory().getDefaultStatusMappingDAO().addAll(bean.getDefaulStatusMappings(), transactionHandler, null);
			}
			
			transactionHandler.commit();
			
		}finally{
			
			TransactionHandler.autoClose(transactionHandler);
		}
	}

	@Override
	protected void updateBean(Status bean, HttpServletRequest req, User user, URIParser uriParser) throws Exception {

		TransactionHandler transactionHandler = null;
		
		try{
			transactionHandler = callback.getDAOFactory().getTransactionHandler();
			
			callback.getDAOFactory().getStatusDAO().update(bean, transactionHandler, null);
			
			clearStatusActionMappings(bean, transactionHandler);
			
			if(bean.getDefaulStatusMappings() != null){
			
				clearPreviousActionMappings(bean, transactionHandler);
				callback.getDAOFactory().getDefaultStatusMappingDAO().addAll(bean.getDefaulStatusMappings(), transactionHandler, null);
			}
			
			transactionHandler.commit();
			
		}finally{
			
			TransactionHandler.autoClose(transactionHandler);
		}
	}

	private void clearPreviousActionMappings(Status bean, TransactionHandler transactionHandler) throws SQLException {

		ArrayList<String> actionIDs = new ArrayList<String>(bean.getDefaulStatusMappings().size());
		
		for(DefaultStatusMapping defaultStatusMapping : bean.getDefaulStatusMappings()){
			
			actionIDs.add(defaultStatusMapping.getActionID());
		}
		
		HighLevelQuery<DefaultStatusMapping> query = new HighLevelQuery<DefaultStatusMapping>();
		
		query.addParameter(defaultStatusMappingFlowParamFactory.getParameter(bean.getFlow()));
		query.addParameter(defaultStatusMappingActionIDParamFactory.getWhereInParameter(actionIDs));
		
		callback.getDAOFactory().getDefaultStatusMappingDAO().delete(query,transactionHandler);
	}	

	private void clearStatusActionMappings(Status bean, TransactionHandler transactionHandler) throws SQLException {

		HighLevelQuery<DefaultStatusMapping> query = new HighLevelQuery<DefaultStatusMapping>();
		
		query.addParameter(defaultStatusMappingStatusParamFactory.getParameter(bean));
		
		callback.getDAOFactory().getDefaultStatusMappingDAO().delete(query,transactionHandler);
	}
	
	
	@Override
	public ForegroundModuleResponse list(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, List<ValidationError> validationErrors) throws Exception {

		return callback.list(req, res, user, uriParser, validationErrors);
	}
}
