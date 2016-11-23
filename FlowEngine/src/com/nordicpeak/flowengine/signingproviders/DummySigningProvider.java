package com.nordicpeak.flowengine.signingproviders;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.annotations.InstanceManagerDependency;
import se.unlogic.hierarchy.core.beans.SimpleViewFragment;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.interfaces.ViewFragment;
import se.unlogic.hierarchy.core.utils.SimpleViewFragmentTransformer;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.standardutils.dao.RelationQuery;
import se.unlogic.standardutils.xml.XMLUtils;

import com.nordicpeak.flowengine.SigningConfirmedResponse;
import com.nordicpeak.flowengine.beans.FlowInstanceEvent;
import com.nordicpeak.flowengine.dao.FlowEngineDAOFactory;
import com.nordicpeak.flowengine.exceptions.flow.FlowDefaultStatusNotFound;
import com.nordicpeak.flowengine.exceptions.flowinstancemanager.FlowInstanceManagerClosedException;
import com.nordicpeak.flowengine.exceptions.queryinstance.UnableToSaveQueryInstanceException;
import com.nordicpeak.flowengine.interfaces.PDFProvider;
import com.nordicpeak.flowengine.interfaces.SigningCallback;
import com.nordicpeak.flowengine.interfaces.SigningProvider;
import com.nordicpeak.flowengine.managers.MutableFlowInstanceManager;


public class DummySigningProvider extends AnnotatedForegroundModule implements SigningProvider {

	public static final RelationQuery EVENT_ATTRIBUTE_RELATION_QUERY = new RelationQuery(FlowInstanceEvent.ATTRIBUTES_RELATION);

	@InstanceManagerDependency
	private PDFProvider pdfProvider;
	
	private FlowEngineDAOFactory daoFactory;

	protected SimpleViewFragmentTransformer fragmentTransformer;
	
	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		daoFactory = new FlowEngineDAOFactory(dataSource, systemInterface.getUserHandler(), systemInterface.getGroupHandler());
	}
	
	@Override
	protected void moduleConfigured() {

		if(moduleDescriptor.getXslPath() != null) {
		
			try {
	
				fragmentTransformer = new SimpleViewFragmentTransformer(moduleDescriptor.getXslPath(), systemInterface.getEncoding(), this.getClass(), moduleDescriptor, sectionInterface);
	
			} catch (Exception e) {
	
				log.error("Unable to parse XSL stylesheet for dummy signing form in module " + this.moduleDescriptor, e);
	
			}
		
		}

	}
	
	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);
		
		if(!systemInterface.getInstanceHandler().addInstance(SigningProvider.class, this)){

			throw new RuntimeException("Unable to register module " + this.moduleDescriptor + " in global instance handler using key " + SigningProvider.class.getSimpleName() + ", another instance is already registered using this key.");
		}		
	}

	@Override
	public void unload() throws Exception {

		if(this.equals(systemInterface.getInstanceHandler().getInstance(SigningProvider.class))){

			systemInterface.getInstanceHandler().removeInstance(SigningProvider.class);
		}		
		
		super.unload();
	}	
	
	@Override
	public ViewFragment sign(HttpServletRequest req, HttpServletResponse res, User user, MutableFlowInstanceManager instanceManager, SigningCallback signingCallback, boolean savedDuringCurrentRequest) throws IOException, FlowInstanceManagerClosedException, UnableToSaveQueryInstanceException, SQLException, FlowDefaultStatusNotFound {

		String signingURL = signingCallback.getSigningURL(instanceManager, req);
		
		if(req.getParameter("idp") != null && !savedDuringCurrentRequest){
			
			log.info("User " + user + " signed flow instance " + instanceManager);
			
			SigningConfirmedResponse response = signingCallback.signingConfirmed(instanceManager, user);
			
			FlowInstanceEvent signingEvent = response.getSigningEvent();
			
			signingEvent.getAttributeHandler().setAttribute("signingProvider", this.getClass().getName());
			
			daoFactory.getFlowInstanceEventDAO().update(signingEvent, EVENT_ATTRIBUTE_RELATION_QUERY);

			FlowInstanceEvent pdfEvent = response.getSubmitEvent() != null ? response.getSubmitEvent() : signingEvent;
			
			if(pdfProvider != null){
				
				try {
					if(pdfProvider.saveTemporaryPDF(instanceManager.getFlowInstanceID(), pdfEvent)){
						
						log.info("Temporary PDF for flow instance " + instanceManager + " requested by user " + user + " saved for event " + pdfEvent);
						
					}else{
						
						log.warn("Unable to find temporary PDF for flow instance " + instanceManager + " submitted by user " + user);
					}
					
				} catch (Exception e) {

					log.error("Error saving temporary PDF for flow instance " + instanceManager + " submitted by user " + user, e);
				}
			}			
			
			signingCallback.signingComplete(instanceManager, pdfEvent, req);
			
			res.sendRedirect(signingCallback.getSignSuccessURL(instanceManager, req));
			
			return null;			
			
		}else if(req.getParameter("fail") != null){
			
			log.info("Signing of flow instance " + instanceManager + " by user " + user + " failed.");
			
			signingCallback.abortSigning(instanceManager);
			
			if(pdfProvider != null){
				
				try {
					pdfProvider.deleteTemporaryPDF(instanceManager.getFlowInstanceID());
					
				} catch (Exception e) {

					log.error("Error deleteing temporary PDF for flow instance " + instanceManager + " submitted by user " + user, e);
				}
			}				
			
			res.sendRedirect(signingCallback.getSignFailURL(instanceManager, req));
			
			return null;			
		}
		
		if(pdfProvider != null && (savedDuringCurrentRequest || !pdfProvider.hasTemporaryPDF(instanceManager.getFlowInstanceID()))){
			
			try {
				pdfProvider.createTemporaryPDF(instanceManager, true, signingCallback.getSiteProfile(), user);
				
			} catch (Exception e) {

				log.error("Error generating temporary PDF for flow instance " + instanceManager + " submitted by user " + user, e);
			}
		}
		
		log.info("User " + user + " requested sign form for flow instance " + instanceManager);
		
		if(fragmentTransformer != null) {
		
			Document doc = XMLUtils.createDomDocument();
			Element document = doc.createElement("Document");
			doc.appendChild(document);

			Element signElement = doc.createElement("SignForm");
			document.appendChild(signElement);

			XMLUtils.appendNewElement(doc, signElement, "signingURL", signingURL);

			try {

				return fragmentTransformer.createViewFragment(doc);

			} catch (Exception e) {

				res.sendRedirect(signingCallback.getSignFailURL(instanceManager, req));

				return null;

			}
		
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("<div>");
		stringBuilder.append("<h1>Dummy signer</h1>");
		stringBuilder.append("<p><a href=\"" + signingCallback.getSigningURL(instanceManager, req) + "&idp=1\">Click here to sign flow instance " + instanceManager.getFlowInstance().getFlow().getName() + " #" + instanceManager.getFlowInstanceID() + "</a></p>");
		stringBuilder.append("<p><a href=\"" + signingCallback.getSigningURL(instanceManager, req) + "&fail=1\">Click here to simulate a failed signing</a></p>");
		stringBuilder.append("</div>");
		
		return new SimpleViewFragment(stringBuilder.toString());
		
	}
}
