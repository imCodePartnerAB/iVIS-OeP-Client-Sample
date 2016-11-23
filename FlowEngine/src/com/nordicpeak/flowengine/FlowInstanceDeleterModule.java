package com.nordicpeak.flowengine;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import se.unlogic.hierarchy.core.annotations.InstanceManagerDependency;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.CRUDAction;
import se.unlogic.hierarchy.core.events.CRUDEvent;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.QueryOperators;
import se.unlogic.standardutils.dao.QueryParameterFactory;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.random.RandomUtils;
import se.unlogic.webutils.http.URIParser;

import com.nordicpeak.flowengine.beans.FlowInstance;
import com.nordicpeak.flowengine.beans.QueryInstanceDescriptor;
import com.nordicpeak.flowengine.dao.FlowEngineDAOFactory;
import com.nordicpeak.flowengine.interfaces.QueryHandler;


public class FlowInstanceDeleterModule extends AnnotatedForegroundModule {

	@InstanceManagerDependency(required = true)
	protected QueryHandler queryHandler;
	
	protected FlowEngineDAOFactory daoFactory;

	protected QueryParameterFactory<FlowInstance, Integer> flowInstanceSiteProfileIDParamFactory;
	protected QueryParameterFactory<QueryInstanceDescriptor, Integer> queryInstanceDescriptorFlowInstanceIDParamFactory;
	
	private int randomNumber = RandomUtils.getRandomInt(10000, 10000000);

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		this.daoFactory = new FlowEngineDAOFactory(dataSource, systemInterface.getUserHandler(), systemInterface.getGroupHandler());
		
		flowInstanceSiteProfileIDParamFactory = daoFactory.getFlowInstanceDAO().getParamFactory("profileID", Integer.class);
		queryInstanceDescriptorFlowInstanceIDParamFactory = daoFactory.getQueryInstanceDescriptorDAO().getParamFactory("flowInstanceID", Integer.class);
	}	
	
	@Override
	public synchronized ForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {

		Integer submittedID = NumberUtils.toInt(req.getParameter("code"));
		
		if(submittedID == null || submittedID != randomNumber){
			
			return new SimpleForegroundModuleResponse(getHTML("Flow instance deleter (use with caution!)","Submit the code " + randomNumber + " to delete all flow instances. Add the parameter siteProfileID to to exclude a site profile."));
		}
	
		Integer siteProfileID = NumberUtils.toInt(req.getParameter("siteProfileID"));
		
		int deletedCount = 0;
		
		log.info("User " + user + " deleting all flow instances...");
		
		TransactionHandler transactionHandler = null;
		
		try{
			transactionHandler = new TransactionHandler(dataSource);
			
			HighLevelQuery<FlowInstance> query = null;
			
			if(siteProfileID != null){
				
				query = new HighLevelQuery<FlowInstance>();
				
				query.addParameter(flowInstanceSiteProfileIDParamFactory.getParameter(siteProfileID, QueryOperators.NOT_EQUALS));
			}
			
			List<FlowInstance> flowInstances = this.daoFactory.getFlowInstanceDAO().getAll(query, transactionHandler);

			if(flowInstances != null){
				
				for(FlowInstance flowInstance : flowInstances){
					
					HighLevelQuery<QueryInstanceDescriptor> descriptorQuery = new HighLevelQuery<QueryInstanceDescriptor>(queryInstanceDescriptorFlowInstanceIDParamFactory.getParameter(flowInstance.getFlowInstanceID()));
					
					descriptorQuery.addRelation(QueryInstanceDescriptor.QUERY_DESCRIPTOR_RELATION);
					
					List<QueryInstanceDescriptor> queryInstanceDescriptors = daoFactory.getQueryInstanceDescriptorDAO().getAll(descriptorQuery, transactionHandler);
					
					if(queryInstanceDescriptors != null){
						
						for(QueryInstanceDescriptor queryInstanceDescriptor : queryInstanceDescriptors){
							
							log.info("Deleting " + queryInstanceDescriptor);
							this.queryHandler.deleteQueryInstance(queryInstanceDescriptor, transactionHandler);
						}
					}
					
					log.info("Deteing " + flowInstance);
					daoFactory.getFlowInstanceDAO().delete(flowInstance, transactionHandler);
				}
			
				deletedCount = flowInstances.size();
				
				systemInterface.getEventHandler().sendEvent(FlowInstance.class, new CRUDEvent<FlowInstance>(FlowInstance.class, CRUDAction.DELETE, flowInstances), se.unlogic.hierarchy.core.enums.EventTarget.ALL);
			}
			
			transactionHandler.commit();
			
		}finally{
			
			randomNumber = RandomUtils.getRandomInt(10000, 10000000);
			
			TransactionHandler.autoClose(transactionHandler);
		}
		
		return new SimpleForegroundModuleResponse(getHTML("Flow instances deleted",deletedCount + " flow instances deleted."));
	}

	private String getHTML(String title, String message) {

		return "<div class=\"contentitem\"><h1>" + title + "</h1><p>" + message + "</p></div>";
	}
}
