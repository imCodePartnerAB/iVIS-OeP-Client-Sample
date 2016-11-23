package se.unlogic.hierarchy.foregroundmodules.invitation;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.emailutils.framework.InvalidEmailAddressException;
import se.unlogic.emailutils.framework.NoEmailSendersFoundException;
import se.unlogic.emailutils.framework.SimpleEmail;
import se.unlogic.emailutils.framework.UnableToProcessEmailException;
import se.unlogic.hierarchy.core.annotations.EnumDropDownSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.PathType;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.exceptions.ModuleConfigurationException;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.utils.CRUDCallback;
import se.unlogic.hierarchy.core.utils.IntegerBasedCRUD;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.hierarchy.foregroundmodules.invitation.beans.BaseInvitation;
import se.unlogic.hierarchy.foregroundmodules.invitation.beans.BaseInvitationType;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;


public abstract class BaseInvitationAdminModule<I extends BaseInvitation, IT extends BaseInvitationType> extends AnnotatedForegroundModule implements CRUDCallback<User>{

	public static final String RECIPIENT_FIRSTNAME = "$recipient-firstname";
	public static final String RECIPIENT_LASTNAME = "$recipient-lastname";
	public static final String RECIPIENT_EMAIL = "$recipient-email";
	public static final String INVITATION_LINK = "$invitation-link";

	@ModuleSetting
	@TextFieldSettingDescriptor(name="Invitation module name", description="Name to be used for the automatically added invitation module",required=true)
	protected String invitationModuleName = "Invitations";

	@ModuleSetting
	@TextFieldSettingDescriptor(name="Invitation module alias", description="Alias to be used for the automatically added invitation module",required=true)
	protected String invitationModuleAlias = "invitation";

	@ModuleSetting
	@TextFieldSettingDescriptor(name="Invitation module XSL path", description="Path to XSL stylesheet used for the automatically added invitation module",required=true)
	protected String invitationModuleXSLPath = "InvitationModule.en.xsl";

	@ModuleSetting
	@EnumDropDownSettingDescriptor(name="Invitation module XSL path type", description="Path type of the XSL stylesheet used for the automatically added invitation module",required=true)
	protected PathType invitationModuleXSLPathType = PathType.Classpath;

	@ModuleSetting(allowsNull = true)
	@TextFieldSettingDescriptor(name = "Editor CSS", description = "Path to the desired CSS stylesheet for FCKEditor (relative from the contextpath)", required = false)
	protected String cssPath;

	protected SimpleForegroundModuleDescriptor invitationModuleDescriptor;

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		checkInvitationModule();
	}


	@Override
	public void update(ForegroundModuleDescriptor moduleDescriptor, DataSource dataSource) throws Exception {

		super.update(moduleDescriptor, dataSource);

		checkInvitationModule();
	}

	@Override
	public void unload() throws Exception {

		if(invitationModuleDescriptor != null){

			unloadInvitationModule();
		}

		super.unload();
	}

	protected synchronized void checkInvitationModule() {

		if(!isProperlyConfigured()){

			if(invitationModuleDescriptor == null){

				log.warn("Module not properly configured, refusing to create instance of invitation module");

			}else{

				log.warn("Module not properly configured, stopping current instance of invitation module");

				unloadInvitationModule();
			}

		}else{

			if(invitationModuleDescriptor == null){

				SimpleForegroundModuleDescriptor invitationModuleDescriptor = new SimpleForegroundModuleDescriptor();
				invitationModuleDescriptor.setSectionID(systemInterface.getRootSection().getSectionDescriptor().getSectionID());
				invitationModuleDescriptor.setClassname(getInvitationModuleClass());
				invitationModuleDescriptor.setAdminAccess(true);
				invitationModuleDescriptor.setUserAccess(true);
				invitationModuleDescriptor.setAnonymousAccess(true);
				invitationModuleDescriptor.setVisibleInMenu(false);
				invitationModuleDescriptor.setDataSourceID(moduleDescriptor.getDataSourceID());
				invitationModuleDescriptor.setAlias(invitationModuleAlias);
				invitationModuleDescriptor.setName(invitationModuleName);
				invitationModuleDescriptor.setDescription(invitationModuleName);
				invitationModuleDescriptor.setXslPath(invitationModuleXSLPath);
				invitationModuleDescriptor.setXslPathType(invitationModuleXSLPathType);
				invitationModuleDescriptor.setMutableSettingHandler(moduleDescriptor.getMutableSettingHandler());

				try{
					systemInterface.getRootSection().getForegroundModuleCache().cache(invitationModuleDescriptor);

					this.invitationModuleDescriptor = invitationModuleDescriptor;
				}catch(Exception e){
					log.error("Error caching invitation module", e);
				}

			}else{

				log.info("Updating invitation module");

				invitationModuleDescriptor.setDataSourceID(moduleDescriptor.getDataSourceID());
				invitationModuleDescriptor.setAlias(invitationModuleAlias);
				invitationModuleDescriptor.setName(invitationModuleName);
				invitationModuleDescriptor.setDescription(invitationModuleName);
				invitationModuleDescriptor.setXslPath(invitationModuleXSLPath);
				invitationModuleDescriptor.setXslPathType(invitationModuleXSLPathType);

				try{
					systemInterface.getRootSection().getForegroundModuleCache().update(invitationModuleDescriptor);
				}catch(Exception e){
					log.error("Error updating invitation module", e);
				}
			}
		}
	}

	private boolean isProperlyConfigured() {

		if(StringUtils.isEmpty(invitationModuleName) || StringUtils.isEmpty(invitationModuleAlias) || StringUtils.isEmpty(invitationModuleXSLPath) || invitationModuleXSLPathType == null){

			return false;
		}

		return true;
	}

	protected void unloadInvitationModule() {

		try{
			if(systemInterface.getRootSection().getForegroundModuleCache().isCached(invitationModuleDescriptor)){
				systemInterface.getRootSection().getForegroundModuleCache().unload(invitationModuleDescriptor);
			}
			this.invitationModuleDescriptor = null;
		}catch(Exception e){
			log.error("Error unloading invitation module", e);
		}
	}

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		super.createDAOs(dataSource);
	}

	@Override
	public ForegroundModuleResponse processRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {

		if(moduleDescriptor == null){

			throw new ModuleConfigurationException("Module is not properly configured");
		}

		return super.processRequest(req, res, user, uriParser);
	}

	@Override
	public ForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return list(req, res, user, uriParser, null);
	}

	public SimpleForegroundModuleResponse list(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, List<ValidationError> validationErrors) throws SQLException {

		log.info("User " + user + " listing invitations and invitation types");

		Document doc = this.createDocument(req, uriParser, user);

		Element invitationListElement = doc.createElement("InvitationList");
		doc.getFirstChild().appendChild(invitationListElement);

		XMLUtils.append(doc, invitationListElement, "InvitationTypes", getInvitationTypes());

		XMLUtils.append(doc, invitationListElement, "Invitations", getInvitations());

		if (validationErrors != null) {
			XMLUtils.append(doc, invitationListElement, validationErrors);
		}

		return new SimpleForegroundModuleResponse(doc, this.moduleDescriptor.getName(), this.getDefaultBreadcrumb());
	}

	protected abstract String getInvitationModuleClass();
	public abstract List<IT> getInvitationTypes() throws SQLException;
	public abstract List<I> getInvitations() throws SQLException;

	@Override
	public Document createDocument(HttpServletRequest req, URIParser uriParser, User user) {

		Document doc = XMLUtils.createDomDocument();
		Element documentElement = doc.createElement("Document");
		documentElement.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser));
		documentElement.appendChild(this.sectionInterface.getSectionDescriptor().toXML(doc));
		documentElement.appendChild(this.moduleDescriptor.toXML(doc));
		XMLUtils.appendNewElement(doc, documentElement, "cssPath", this.cssPath);
		doc.appendChild(documentElement);
		return doc;
	}

	protected abstract IntegerBasedCRUD<IT, ?> getInvitationTypeCRUD();

	protected abstract IntegerBasedCRUD<I, ?> getInvitationCRUD();

	@WebPublic
	public ForegroundModuleResponse addInvitationType(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return getInvitationTypeCRUD().add(req, res, user, uriParser);
	}

	@WebPublic
	public ForegroundModuleResponse updateInvitationType(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return getInvitationTypeCRUD().update(req, res, user, uriParser);
	}

	@WebPublic
	public ForegroundModuleResponse deleteInvitationType(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return getInvitationTypeCRUD().delete(req, res, user, uriParser);
	}

	@WebPublic
	public ForegroundModuleResponse addInvitation(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return getInvitationCRUD().add(req, res, user, uriParser);
	}

	@WebPublic
	public ForegroundModuleResponse updateInvitation(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return getInvitationCRUD().update(req, res, user, uriParser);
	}

	@WebPublic
	public ForegroundModuleResponse deleteInvitation(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return getInvitationCRUD().delete(req, res, user, uriParser);
	}

	@Override
	public String getTitlePrefix() {

		return null;
	}

	@WebPublic
	public SimpleForegroundModuleResponse sendInvitation(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		I invitation = this.getInvitationCRUD().getRequestedBean(req, res, user, uriParser, null);

		if (invitation == null) {
			return this.list(req, res, user, uriParser, Collections.singletonList(new ValidationError("SendFailedInvitationNotFound")));
		}

		this.sendInvitation(invitation, user, req);

		this.redirectToDefaultMethod(req, res);

		return null;
	}

	@WebPublic(toLowerCase=true)
	public SimpleForegroundModuleResponse sendUnsentInvitations(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		List<I> invitations = this.getInvitations();

		if (invitations != null) {
			
			for(I invitation : invitations){
				
				if(invitation.getLastSent() == null){
				
					sendInvitation(invitation, user, req);
				}
			}
		}		

		this.redirectToDefaultMethod(req, res);
		
		return null;
	}	
	
	@WebPublic(toLowerCase=true)
	public SimpleForegroundModuleResponse resendSentInvitations(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		List<I> invitations = this.getInvitations();

		if (invitations != null) {
			
			for(I invitation : invitations){
				
				if(invitation.getLastSent() != null){
				
					sendInvitation(invitation, user, req);
				}
			}
		}		

		this.redirectToDefaultMethod(req, res);
		
		return null;
	}	
	
	public void sendInvitation(I invitation, User user, HttpServletRequest req) throws SQLException {

		try {

			log.info("User " + user + " sending invitation " + invitation);

			IT invitationType = invitation.getInvitationType();

			SimpleEmail email = new SimpleEmail();
			email.setMessageContentType(SimpleEmail.HTML);

			email.setSenderName(invitationType.getSenderName());
			email.setSenderAddress(invitationType.getSenderEmail());
			email.addRecipient(invitation.getEmail());
			email.setSubject(invitationType.getSubject());
			email.setMessage(this.getInvitationMessage(invitation, user, req));

			systemInterface.getEmailHandler().send(email);

			invitation.setLastSent(new Timestamp(System.currentTimeMillis()));
			invitation.setSendCount(invitation.getSendCount() + 1);

			updateInvitation(invitation);

		} catch (NoEmailSendersFoundException e) {

			log.error("Unable to send invitation " + invitation + ", no email senders found!",e);

		} catch (UnableToProcessEmailException e) {

			log.error("Unable to send invitation " + invitation + ", unable to process generated email!",e);

		} catch (InvalidEmailAddressException e) {

			log.error("Unable to send invitation " + invitation + ", " + e.getAddress() + " is not a valid email address!",e);
		}
	}

	protected abstract void updateInvitation(I invitation) throws SQLException;

	protected String getInvitationMessage(I invitation, User user, HttpServletRequest req) {

		String message = invitation.getInvitationType().getMessage();

		message = message.replace(RECIPIENT_FIRSTNAME, invitation.getFirstname());
		message = message.replace(RECIPIENT_LASTNAME, invitation.getLastname());
		message = message.replace(RECIPIENT_EMAIL, invitation.getEmail());
		message = message.replace(INVITATION_LINK, getInvitationURL(invitation, req));

		return message;
	}

	public String getInvitationURL(I invitation, HttpServletRequest req) {

		return RequestUtils.getFullContextPathURL(req) + "/" + this.invitationModuleAlias + "/" + invitation.getInvitationID() + "/" + invitation.getLinkID().toString();
	}


	/**
	 * @param invitation
	 * @return returns true if the email address used in the invitation is already in use by another user or invitation
	 * @throws SQLException
	 */
	public abstract boolean checkIfEmailInUse(I invitation) throws SQLException;

	public IT getInvitationType(Integer invitationTypeID) throws AccessDeniedException, SQLException{

		return getInvitationTypeCRUD().getBean(invitationTypeID);
	}
}
