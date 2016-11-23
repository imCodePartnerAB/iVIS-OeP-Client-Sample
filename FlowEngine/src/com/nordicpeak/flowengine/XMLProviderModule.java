package com.nordicpeak.flowengine;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.annotations.EventListener;
import se.unlogic.hierarchy.core.annotations.InstanceManagerDependency;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextAreaSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.EventSource;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.standardutils.dao.RelationQuery;
import se.unlogic.standardutils.date.PooledSimpleDateFormat;
import se.unlogic.standardutils.xml.XMLUtils;

import com.nordicpeak.flowengine.beans.FlowInstanceEvent;
import com.nordicpeak.flowengine.dao.FlowEngineDAOFactory;
import com.nordicpeak.flowengine.enums.EventType;
import com.nordicpeak.flowengine.events.SubmitEvent;
import com.nordicpeak.flowengine.interfaces.ImmutableFlowInstance;
import com.nordicpeak.flowengine.interfaces.QueryHandler;
import com.nordicpeak.flowengine.interfaces.XMLProvider;
import com.nordicpeak.flowengine.managers.FlowInstanceManager;


public class XMLProviderModule extends AnnotatedForegroundModule implements XMLProvider {

	public static final RelationQuery EVENT_ATTRIBUTE_RELATION_QUERY = new RelationQuery(FlowInstanceEvent.ATTRIBUTES_RELATION);
	
	private static final PooledSimpleDateFormat DATE_TIME_FORMATTER = new PooledSimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

	@ModuleSetting(allowsNull=true)
	@TextAreaSettingDescriptor(name="Supported actionID's", description="The action ID's which will trigger export XML to be generated and stored when a submit event is detected")
	private List<String> supportedActionIDs;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "XML dir", description = "The directory where XML files be stored ")
	protected String xmlDir;

	@InstanceManagerDependency(required = true)
	protected QueryHandler queryHandler;

	private FlowEngineDAOFactory daoFactory;
	
	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		if(!systemInterface.getInstanceHandler().addInstance(XMLProvider.class, this)){

			throw new RuntimeException("Unable to register module " + this.moduleDescriptor + " in global instance handler using key " + XMLProvider.class.getSimpleName() + ", another instance is already registered using this key.");
		}
	}

	@Override
	public void unload() throws Exception {

		if(this.equals(systemInterface.getInstanceHandler().getInstance(XMLProvider.class))){

			systemInterface.getInstanceHandler().removeInstance(XMLProvider.class);
		}

		super.unload();
	}
	
	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		daoFactory = new FlowEngineDAOFactory(dataSource, systemInterface.getUserHandler(), systemInterface.getGroupHandler());
	}
	
	@EventListener(channel=FlowInstanceManager.class, priority = 50)
	public void processEvent(SubmitEvent event, EventSource eventSource) throws SQLException{

		if(this.supportedActionIDs == null || xmlDir == null){

			log.warn("Module " + this.moduleDescriptor + " not properly configured, refusing to generate export XML for flow instance " + event.getFlowInstanceManager().getFlowInstance());
		}

		if(event.getEvent().getEventType() != EventType.SUBMITTED || event.getActionID() == null || !supportedActionIDs.contains(event.getActionID())){

			return;
		}

		ImmutableFlowInstance flowInstance = event.getFlowInstanceManager().getFlowInstance();

		log.info("Generating export XML for flow instance " + flowInstance);

		try{
			Document doc = XMLUtils.createDomDocument();

			Element flowInstanceElement = doc.createElement("FlowInstance");
			doc.appendChild(flowInstanceElement);

			flowInstanceElement.setAttribute("xmlns", "http://www.oeplatform.org/version/1.2/schemas/flowinstance");
			flowInstanceElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			flowInstanceElement.setAttribute("xsi:schemaLocation", "http://www.oeplatform.org/version/1.2/schemas/flowinstance schema-" + event.getFlowInstanceManager().getFlowID() + ".xsd");

			Element headerElement = XMLUtils.appendNewElement(doc, flowInstanceElement, "Header");

			Element flowElement = XMLUtils.appendNewElement(doc, headerElement, "Flow");
			XMLUtils.appendNewElement(doc, flowElement, "FamilyID", flowInstance.getFlow().getFlowFamily().getFlowFamilyID());
			XMLUtils.appendNewElement(doc, flowElement, "Version", flowInstance.getFlow().getVersion());
			XMLUtils.appendNewElement(doc, flowElement, "FlowID", flowInstance.getFlow().getFlowID());
			XMLUtils.appendNewCDATAElement(doc, flowElement, "Name", flowInstance.getFlow().getName());

			XMLUtils.appendNewElement(doc, headerElement, "FlowInstanceID", flowInstance.getFlowInstanceID());

			Element statusElement = XMLUtils.appendNewElement(doc, headerElement, "Status");
			XMLUtils.appendNewElement(doc, statusElement, "ID", flowInstance.getStatus().getStatusID());
			XMLUtils.appendNewCDATAElement(doc, statusElement, "Name", flowInstance.getStatus().getName());

			appendUser(flowInstance.getPoster(), "Poster", doc, headerElement);

			XMLUtils.appendNewElement(doc, headerElement, "Posted", DATE_TIME_FORMATTER.format(flowInstance.getAdded()));

			if(flowInstance.getUpdated() != null){

				appendUser(flowInstance.getEditor(), "Editor", doc, headerElement);

				XMLUtils.appendNewElement(doc, headerElement, "Updated", DATE_TIME_FORMATTER.format(flowInstance.getUpdated()));
			}

			Element valuesElement = XMLUtils.appendNewElement(doc, flowInstanceElement, "Values");

			List<Element> queryElements = event.getFlowInstanceManager().getExportXMLElements(doc, queryHandler);

			if(queryElements != null){

				for(Element queryElement : queryElements){

					valuesElement.appendChild(queryElement);
				}
			}

			File xmlFile = getFile(flowInstance.getFlowInstanceID(), event.getEvent().getEventID());

			xmlFile.getParentFile().mkdirs();

			XMLUtils.writeXMLFile(doc, xmlFile, true, systemInterface.getEncoding());
			
			event.getEvent().getAttributeHandler().setAttribute("xml", "true");
			daoFactory.getFlowInstanceEventDAO().update(event.getEvent(), EVENT_ATTRIBUTE_RELATION_QUERY);

		}catch(Exception e){

			log.error("Error generating export XML for flow instance " + flowInstance + " submitted by user " + event.getEvent().getPoster(), e);
		}
	}

	private void appendUser(User user, String elementName, Document doc, Element headerElement) {

		if(user != null){

			Element userElement = XMLUtils.appendNewElement(doc, headerElement, elementName);
			XMLUtils.appendNewCDATAElement(doc, userElement, "Firstname", user.getFirstname());
			XMLUtils.appendNewCDATAElement(doc, userElement, "Lastname", user.getLastname());
			XMLUtils.appendNewCDATAElement(doc, userElement, "Email", user.getEmail());
			XMLUtils.appendNewElement(doc, userElement, "ID", user.getUserID());
		}
	}

	@Override
	public File getXML(Integer flowInstanceID, Integer eventID) {

		File file = getFile(flowInstanceID, eventID);
		
		if(file.exists()){
			
			return file;
		}
		
		return null;
	}

	private File getFile(Integer flowInstanceID, Integer eventID) {

		return new File(xmlDir + File.separator + flowInstanceID + File.separator + eventID + ".xml");
	}
}
