package com.nordicpeak.flowengine.signingproviders;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.annotations.InstanceManagerDependency;
import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.beans.LinkTag;
import se.unlogic.hierarchy.core.beans.ScriptTag;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.interfaces.ViewFragment;
import se.unlogic.hierarchy.core.utils.ModuleViewFragmentTransformer;
import se.unlogic.hierarchy.core.utils.ViewFragmentModule;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.openhierarchy.foregroundmodules.siteprofile.interfaces.SiteProfile;
import se.unlogic.openhierarchy.foregroundmodules.siteprofile.interfaces.SiteProfileHandler;
import se.unlogic.standardutils.bool.BooleanUtils;
import se.unlogic.standardutils.dao.AnnotatedDAO;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.QueryParameterFactory;
import se.unlogic.standardutils.dao.SimpleAnnotatedDAOFactory;
import se.unlogic.standardutils.db.tableversionhandler.TableVersionHandler;
import se.unlogic.standardutils.db.tableversionhandler.UpgradeResult;
import se.unlogic.standardutils.db.tableversionhandler.XMLDBScriptProvider;
import se.unlogic.standardutils.time.TimeUtils;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.HTTPUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.http.enums.ContentDisposition;

import com.nordicpeak.flowengine.FlowBrowserModule;
import com.nordicpeak.flowengine.beans.DefaultInstanceMetadata;
import com.nordicpeak.flowengine.beans.FlowInstance;
import com.nordicpeak.flowengine.enums.ContentType;
import com.nordicpeak.flowengine.enums.EventType;
import com.nordicpeak.flowengine.exceptions.flowinstance.InvalidFlowInstanceStepException;
import com.nordicpeak.flowengine.exceptions.flowinstance.MissingQueryInstanceDescriptor;
import com.nordicpeak.flowengine.exceptions.flowinstancemanager.DuplicateFlowInstanceManagerIDException;
import com.nordicpeak.flowengine.exceptions.queryprovider.QueryInstanceNotFoundInQueryProviderException;
import com.nordicpeak.flowengine.exceptions.queryprovider.QueryProviderErrorException;
import com.nordicpeak.flowengine.exceptions.queryprovider.QueryProviderNotFoundException;
import com.nordicpeak.flowengine.interfaces.ImmutableFlowInstanceEvent;
import com.nordicpeak.flowengine.interfaces.MultiSigningProvider;
import com.nordicpeak.flowengine.interfaces.PDFProvider;
import com.nordicpeak.flowengine.interfaces.QueryHandler;
import com.nordicpeak.flowengine.interfaces.SigningParty;
import com.nordicpeak.flowengine.managers.ImmutableFlowInstanceManager;
import com.nordicpeak.flowengine.utils.MultiSignUtils;

public class DummyMultiSigningProviderModule extends AnnotatedForegroundModule implements MultiSigningProvider, ViewFragmentModule<ForegroundModuleDescriptor> {

	protected ModuleViewFragmentTransformer<ForegroundModuleDescriptor> viewFragmentTransformer;

	protected AnnotatedDAO<Signature> signatureDAO;

	protected QueryParameterFactory<Signature, Integer> flowInstanceIDParamFactory;
	protected QueryParameterFactory<Signature, String> socialSecurityNumberParamFactory;

	@InstanceManagerDependency
	protected PDFProvider pdfProvider;

	@InstanceManagerDependency(required = true)
	protected QueryHandler queryHandler;

	@InstanceManagerDependency(required = true)
	protected FlowBrowserModule browserModule;

	@InstanceManagerDependency
	protected SiteProfileHandler profileHandler;

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		viewFragmentTransformer = new ModuleViewFragmentTransformer<ForegroundModuleDescriptor>(sectionInterface.getModuleXSLTCache(), this, systemInterface.getEncoding());

		if(!systemInterface.getInstanceHandler().addInstance(MultiSigningProvider.class, this)) {

			throw new RuntimeException("Unable to register module " + this.moduleDescriptor + " in global instance handler using key " + MultiSigningProvider.class.getSimpleName() + ", another instance is already registered using this key.");
		}
	}

	@Override
	public void unload() throws Exception {

		if(this.equals(systemInterface.getInstanceHandler().getInstance(MultiSigningProvider.class))) {

			systemInterface.getInstanceHandler().removeInstance(MultiSigningProvider.class);
		}

		super.unload();
	}

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		//Automatic table version handling
		UpgradeResult upgradeResult = TableVersionHandler.upgradeDBTables(dataSource, DummyMultiSigningProviderModule.class.getName(), new XMLDBScriptProvider(this.getClass().getResourceAsStream("DB script.xml")));

		if(upgradeResult.isUpgrade()) {

			log.info(upgradeResult.toString());
		}

		signatureDAO = new SimpleAnnotatedDAOFactory(dataSource).getDAO(Signature.class);

		flowInstanceIDParamFactory = signatureDAO.getParamFactory("flowInstanceID", Integer.class);
		socialSecurityNumberParamFactory = signatureDAO.getParamFactory("socialSecurityNumber", String.class);
	}

	@Override
	public ViewFragment getSigningStatus(HttpServletRequest req, User user, URIParser uriParser, ImmutableFlowInstanceManager instanceManager) throws Exception {

		Set<SigningParty> signingParties = MultiSignUtils.getSigningParties(instanceManager);

		if(signingParties == null) {

			throw new RuntimeException("No signing parties found for flow instance " + instanceManager);
		}

		Document doc = createDocument(req, uriParser);

		Element signingStatusElement = doc.createElement("SigningStatus");
		doc.getDocumentElement().appendChild(signingStatusElement);

		XMLUtils.appendNewElement(doc, signingStatusElement, "SigningLink", RequestUtils.getFullContextPathURL(req) + getFullAlias() + "/sign/" + instanceManager.getFlowInstanceID());

		for (SigningParty signingParty : signingParties) {

			Element signingPartyElement = XMLUtils.append(doc, signingStatusElement, signingParty);

			XMLUtils.append(doc, signingPartyElement, getSignature(instanceManager.getFlowInstanceID(), signingParty.getSocialSecurityNumber()));
		}

		return viewFragmentTransformer.createViewFragment(doc);
	}

	@WebPublic(alias = "sign", requireLogin = true)
	public ForegroundModuleResponse signFlowInstance(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {

		Integer flowInstanceID = uriParser.getInt(2);
		ImmutableFlowInstanceManager instanceManager;

		if(flowInstanceID == null || (instanceManager = getFlowInstanceManager(flowInstanceID, req, user, uriParser)) == null) {

			//Flow instance not found
			throw new URINotFoundException(uriParser);
		}

		if(instanceManager.getFlowState().getContentType() != ContentType.WAITING_FOR_MULTISIGN) {

			//TODO allow page to be accessed but no signing if the status does not have content type WAITING_FOR_MULTISIGN
			//Wrong status
			throw new URINotFoundException(uriParser);
		}

		SigningParty signingParty = getMatchingSigningParty(user, instanceManager);

		if(signingParty == null) {

			//User does not have access to sign this flow instance
			throw new AccessDeniedException("User does not have access to flow instance " + instanceManager);
		}

		Signature signature = getSignature(instanceManager.getFlowInstanceID(), signingParty.getSocialSecurityNumber());

		if(BooleanUtils.toBoolean(req.getParameter("sign")) && signature == null) {

			log.info("User " + user + " signing flow instance " + instanceManager);

			//add signature
			signature = new Signature();
			signature.setAdded(TimeUtils.getCurrentTimestamp());
			signature.setFlowInstanceID(instanceManager.getFlowInstanceID());
			signature.setSocialSecurityNumber(signingParty.getSocialSecurityNumber());

			signatureDAO.add(signature);

			browserModule.addFlowInstanceEvent(instanceManager.getFlowInstance(), EventType.SIGNED, null, user);

			if(isFullySigned(instanceManager)) {

				log.info("Multi-party signing of flowinstance " + instanceManager + " complete");

				browserModule.multiSigningComplete(instanceManager, getCurrentSiteProfile(req, user, uriParser));
			}
		}

		Document doc = createDocument(req, uriParser);

		Element signFlowInstanceElement = doc.createElement("SignFlowInstance");
		doc.getDocumentElement().appendChild(signFlowInstanceElement);

		signFlowInstanceElement.appendChild(instanceManager.getFlowInstance().toXML(doc));
		signFlowInstanceElement.appendChild(signingParty.toXML(doc));
		XMLUtils.append(doc, signFlowInstanceElement, signature);

		return new SimpleForegroundModuleResponse(doc, getDefaultBreadcrumb());
	}

	@WebPublic(alias = "pdf", requireLogin = true)
	public ForegroundModuleResponse getPDF(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {

		Integer flowInstanceID = uriParser.getInt(2);
		ImmutableFlowInstanceManager instanceManager;

		if(flowInstanceID == null || (instanceManager = getFlowInstanceManager(flowInstanceID, req, user, uriParser)) == null) {

			//Flow instance not found
			throw new URINotFoundException(uriParser);
		}

		if(instanceManager.getFlowState().getContentType() != ContentType.WAITING_FOR_MULTISIGN) {

			//Wrong status
			throw new URINotFoundException(uriParser);
		}

		SigningParty signingParty = getMatchingSigningParty(user, instanceManager);

		if(signingParty == null) {

			//User does not have access to sign this flow instance
			throw new AccessDeniedException("User does not have access to flow instance " + instanceManager);
		}

		if(instanceManager.getFlowInstance().getEvents() != null) {

			for (ImmutableFlowInstanceEvent event : instanceManager.getFlowInstance().getEvents()) {

				File pdfFile = pdfProvider.getPDF(flowInstanceID, event.getEventID());

				if(pdfFile != null) {

					log.info("Sending PDF for flow instance " + instanceManager + ", event " + event + " to user " + user);
					HTTPUtils.sendFile(pdfFile, instanceManager.getFlowInstance().getFlow().getName() + " - " + instanceManager.getFlowInstance().getFlowInstanceID() + ".pdf", req, res, ContentDisposition.ATTACHMENT);
					
					return null;
				}
			}
		}

		throw new URINotFoundException(uriParser);
	}

	private SigningParty getMatchingSigningParty(User user, ImmutableFlowInstanceManager instanceManager) {

		String socialSecurityNumber = user.getAttributeHandler().getString("citizenIdentifier");

		if(socialSecurityNumber == null) {

			return null;
		}

		Set<SigningParty> signingParties = MultiSignUtils.getSigningParties(instanceManager);

		if(signingParties != null) {

			for (SigningParty signingParty : signingParties) {

				if(signingParty.getSocialSecurityNumber().equals(socialSecurityNumber)) {

					return signingParty;
				}
			}
		}

		return null;
	}

	private boolean isFullySigned(ImmutableFlowInstanceManager instanceManager) throws SQLException {

		Set<SigningParty> signingParties = MultiSignUtils.getSigningParties(instanceManager);

		for (SigningParty signingParty : signingParties) {

			if(getSignature(instanceManager.getFlowInstanceID(), signingParty.getSocialSecurityNumber()) == null) {

				return false;
			}
		}

		return true;
	}

	private ImmutableFlowInstanceManager getFlowInstanceManager(Integer flowInstanceID, HttpServletRequest req, User user, URIParser uriParser) throws DuplicateFlowInstanceManagerIDException, MissingQueryInstanceDescriptor, QueryProviderNotFoundException, InvalidFlowInstanceStepException, QueryProviderErrorException, QueryInstanceNotFoundInQueryProviderException, SQLException {

		FlowInstance flowInstance = browserModule.getFlowInstance(flowInstanceID);

		if(flowInstance != null) {

			return new ImmutableFlowInstanceManager(flowInstance, queryHandler, req, new DefaultInstanceMetadata(getCurrentSiteProfile(req, user, uriParser)));
		}

		return null;
	}

	protected SiteProfile getCurrentSiteProfile(HttpServletRequest req, User user, URIParser uriParser) {

		if(this.profileHandler != null) {

			return profileHandler.getCurrentProfile(user, req, uriParser);
		}

		return null;
	}

	private Signature getSignature(Integer flowInstanceID, String socialSecurityNumber) throws SQLException {

		HighLevelQuery<Signature> query = new HighLevelQuery<Signature>();

		query.addParameter(flowInstanceIDParamFactory.getParameter(flowInstanceID));
		query.addParameter(socialSecurityNumberParamFactory.getParameter(socialSecurityNumber));

		return signatureDAO.get(query);
	}

	@Override
	public ForegroundModuleDescriptor getModuleDescriptor() {

		return moduleDescriptor;
	}

	@Override
	public List<LinkTag> getLinkTags() {

		return links;
	}

	@Override
	public List<ScriptTag> getScriptTags() {

		return scripts;

	}

	public Document createDocument(HttpServletRequest req, URIParser uriParser) {

		Document doc = XMLUtils.createDomDocument();
		Element documentElement = doc.createElement("Document");
		doc.appendChild(documentElement);
		documentElement.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser));
		documentElement.appendChild(this.moduleDescriptor.toXML(doc));

		return doc;
	}
}
