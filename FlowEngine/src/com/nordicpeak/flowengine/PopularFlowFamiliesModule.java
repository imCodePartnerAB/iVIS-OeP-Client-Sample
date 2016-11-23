package com.nordicpeak.flowengine;

import it.sauronsoftware.cron4j.Scheduler;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.backgroundmodules.AnnotatedBackgroundModule;
import se.unlogic.hierarchy.core.annotations.InstanceManagerDependency;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleBackgroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.EventSource;
import se.unlogic.hierarchy.core.events.CRUDEvent;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.EventListener;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.standardutils.collections.MethodComparator;
import se.unlogic.standardutils.dao.AnnotatedDAO;
import se.unlogic.standardutils.dao.LowLevelQuery;
import se.unlogic.standardutils.dao.SimpleAnnotatedDAOFactory;
import se.unlogic.standardutils.dao.querys.ArrayListQuery;
import se.unlogic.standardutils.dao.querys.ObjectQuery;
import se.unlogic.standardutils.enums.Order;
import se.unlogic.standardutils.populators.IntegerPopulator;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.time.MillisecondTimeUnits;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.URIParser;

import com.nordicpeak.flowengine.beans.Flow;
import com.nordicpeak.flowengine.beans.FlowFamily;


public class PopularFlowFamiliesModule extends AnnotatedBackgroundModule implements EventListener<CRUDEvent<Flow>>, Runnable{

	private static final Comparator<FlowFamily> FAMILY_COMPARATOR = new MethodComparator<FlowFamily>(FlowFamily.class, "getFlowInstanceCount", Order.DESC);

	@ModuleSetting
	@TextFieldSettingDescriptor(name="Intervall size", description="Controls how any hours back in time that the statistics should be based on")
	private int interval = 72;

	@ModuleSetting
	@TextFieldSettingDescriptor(name="Flow count", description="Controls how many flows this module should display")
	private int flowCount = 5;

	@InstanceManagerDependency(required=true)
	private FlowBrowserModule flowBrowserModule;

	private AnnotatedDAO<Flow> flowDAO;

	private ArrayList<Flow> popularFlows;

	private Scheduler scheduler;

	@SuppressWarnings("unchecked")
	@Override
	public void init(BackgroundModuleDescriptor descriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(descriptor, sectionInterface, dataSource);

		cacheFlows();

		systemInterface.getEventHandler().addEventListener(CRUDEvent.class, this, Flow.class);

		scheduler = new Scheduler();
		scheduler.schedule("0 * * * *", this);
		scheduler.start();
	}

	@Override
	public void update(BackgroundModuleDescriptor descriptor, DataSource dataSource) throws Exception {

		super.update(descriptor, dataSource);

		cacheFlows();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void unload() throws Exception {

		try {
			scheduler.stop();
		} catch (IllegalStateException e) {
			log.error("Error stopping scheduler", e);
		}

		systemInterface.getEventHandler().removeEventListener(Flow.class, CRUDEvent.class, this);

		super.unload();
	}

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		SimpleAnnotatedDAOFactory daoFactory = new SimpleAnnotatedDAOFactory(dataSource);
		flowDAO = daoFactory.getDAO(Flow.class);
	}

	@Override
	protected BackgroundModuleResponse processBackgroundRequest(HttpServletRequest req, User user, URIParser uriParser) throws Exception {

		if(popularFlows == null){

			return null;
		}

		Document doc = XMLUtils.createDomDocument();
		Element documentElement = doc.createElement("Document");
		doc.appendChild(documentElement);
		documentElement.appendChild(this.moduleDescriptor.toXML(doc));

		XMLUtils.appendNewCDATAElement(doc, documentElement, "browserModuleURL", req.getContextPath() + flowBrowserModule.getFullAlias() + "/overview/");
		XMLUtils.append(doc, documentElement, "Flows", popularFlows);

		if(user != null){
			XMLUtils.appendNewElement(doc, documentElement, "loggedIn");
		}

		return new SimpleBackgroundModuleResponse(doc);
	}

	private void cacheFlows(){

		log.info("Caching the " + flowCount + " most popular flow families over the past " + interval + " hours...");

		try{
			//Get ID of all families with at least one published flow
			List<Integer> familyIDs = new ArrayListQuery<Integer>(dataSource, "SELECT DISTINCT flowFamilyID FROM flowengine_flows WHERE publishDate <= CURDATE() AND (unPublishDate IS NULL OR unPublishDate > CURDATE());", IntegerPopulator.getPopulator()).executeQuery();

			if(familyIDs != null){

				ArrayList<FlowFamily> flowFamilies = new ArrayList<FlowFamily>(familyIDs.size());

				for(Integer flowFamilyID : familyIDs){

					//Get all flow IDs for this family
					List<Integer> flowIDs = new ArrayListQuery<Integer>(dataSource, "SELECT flowID FROM flowengine_flows WHERE flowFamilyID = " + flowFamilyID, IntegerPopulator.getPopulator()).executeQuery();

					if(flowIDs != null){

						//Get all flow instances added within the configured interval of hours
						ObjectQuery<Integer> instanceCountQuery = new ObjectQuery<Integer>(dataSource, "SELECT COUNT(flowInstanceID) FROM flowengine_flow_instances WHERE flowID IN (" + StringUtils.toCommaSeparatedString(flowIDs) + ") AND added >= ?", IntegerPopulator.getPopulator());

						instanceCountQuery.setTimestamp(1, new Timestamp(System.currentTimeMillis() - (MillisecondTimeUnits.HOUR * interval)));

						FlowFamily flowFamily = new FlowFamily();
						flowFamily.setFlowFamilyID(flowFamilyID);
						flowFamily.setFlowInstanceCount(instanceCountQuery.executeQuery());
						flowFamilies.add(flowFamily);
					}
				}

				Collections.sort(flowFamilies, FAMILY_COMPARATOR);

				ArrayList<Flow> flows = new ArrayList<Flow>(this.flowCount);

				//Get latest published flow for each family until flowCount has been reached
				for(FlowFamily flowFamily : flowFamilies){

					LowLevelQuery<Flow> flowQuery = new LowLevelQuery<Flow>("SELECT * FROM flowengine_flows WHERE flowFamilyID = ? AND publishDate <= CURDATE() AND (unPublishDate IS NULL OR unPublishDate > CURDATE()) ORDER BY version DESC LIMIT 1;");
					flowQuery.addParameter(flowFamily.getFlowFamilyID());

					Flow flow = flowDAO.get(flowQuery);

					if(flow != null){

						flow.setFlowFamily(flowFamily);
						flows.add(flow);

						if(flows.size() == flowCount){

							break;
						}
					}
				}

				if(!flows.isEmpty()){

					log.info("Cached the following " + flows.size() + " flows: " + StringUtils.toCommaSeparatedString(flows));

					popularFlows = flows;
					return;
				}
			}

			log.info("No flows cached.");

		}catch(SQLException e){

			log.error("Error caching flows", e);
		}

		popularFlows = null;
	}

	@Override
	public void processEvent(CRUDEvent<Flow> event, EventSource source) {

		cacheFlows();
	}

	@Override
	public void run() {

		cacheFlows();
	}

	@Override
	public int getPriority() {

		return 0;
	}
}
