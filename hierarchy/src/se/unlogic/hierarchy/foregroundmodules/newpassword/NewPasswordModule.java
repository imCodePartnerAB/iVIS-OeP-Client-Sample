package se.unlogic.hierarchy.foregroundmodules.newpassword;

import java.util.ArrayList;
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
import se.unlogic.emailutils.populators.EmailPopulator;
import se.unlogic.hierarchy.core.annotations.CheckboxSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.HTMLEditorSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextAreaSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.annotations.XSLVariable;
import se.unlogic.hierarchy.core.beans.MutableUser;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.exceptions.UnableToUpdateUserException;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.purecaptcha.CaptchaHandler;
import se.unlogic.purecaptcha.DefaultCaptchaHandler;
import se.unlogic.standardutils.populators.StringPopulator;
import se.unlogic.standardutils.random.RandomUtils;
import se.unlogic.standardutils.string.AnnotatedBeanTagSourceFactory;
import se.unlogic.standardutils.string.SingleTagSourceFactory;
import se.unlogic.standardutils.string.TagReplacer;
import se.unlogic.standardutils.time.MillisecondTimeUnits;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.validation.ValidationUtils;


public class NewPasswordModule extends AnnotatedForegroundModule {

	protected AnnotatedBeanTagSourceFactory<User> USER_TAG_SOURCE_FACTORY = new AnnotatedBeanTagSourceFactory<User>(User.class, "$user.");
	protected SingleTagSourceFactory<String> PASSWORD_TAG_SOURCE_FACTORY = new SingleTagSourceFactory<String>("$password");

	@ModuleSetting
	@CheckboxSettingDescriptor(name="Require username",description="Controls if username is required along with e-mail address in order to request a new password")
	protected boolean requireUsername;

	@ModuleSetting
	@CheckboxSettingDescriptor(name="Require captcha confirmation",description="Controls whether account creation requires captcha confirmation")
	protected boolean requireCaptchaConfirmation = true;

	@XSLVariable
	@ModuleSetting
	@TextFieldSettingDescriptor(name="Email subject",description="The subject of the e-mails sent.")
	protected String subject = "New password";

	@XSLVariable
	@ModuleSetting
	@TextAreaSettingDescriptor(name="Email message",description="The body of the e-mails sent.")
	protected String message = "Hello $user.firstname,\n\nHere is your new password for mysite: $password\n\n/Mysite";

	@ModuleSetting
	@TextFieldSettingDescriptor(name="Email sender address",description="The sender address of the e-mails sent.",formatValidator=EmailPopulator.class)
	protected String senderAddress = "not@set.foo";

	@ModuleSetting
	@TextFieldSettingDescriptor(name="Email sender name",description="The sender name of the e-mails sent.")
	protected String senderName = "John Doe";

	@ModuleSetting
	@HTMLEditorSettingDescriptor(name="New password message",description="The message displayed above the new password form")
	@XSLVariable()
	protected String newPasswordFormMessage = "Fill in the form below to get a new password e-mailed to you";

	@ModuleSetting
	@HTMLEditorSettingDescriptor(name="Password sent message",description="The message displayed after a new password has been e-mailed to the user")
	@XSLVariable()
	protected String newPasswordSentMessage = "A new password has been e-mailed to you";

	protected CaptchaHandler captchaHandler;

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		captchaHandler = new DefaultCaptchaHandler(this.getClass().getName() + ":" + this.moduleDescriptor.getModuleID(), 2 * MillisecondTimeUnits.MINUTE, false);
	}

	@Override
	public ForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) {

		ValidationException validationException = null;

		if(req.getMethod().equalsIgnoreCase("POST")){

			try{
				List<ValidationError> validationErrors = new ArrayList<ValidationError>();

				String email = ValidationUtils.validateParameter("email", req, true, 6, 255, StringPopulator.getPopulator(), validationErrors);

				String username = null;

				if(requireUsername){

					username = ValidationUtils.validateParameter("username", req, true, 1, 20, StringPopulator.getPopulator(), validationErrors);
				}

				String captchaConfirmation = req.getParameter("captchaConfirmation");

				if(requireCaptchaConfirmation && !captchaHandler.isValidCode(req, captchaConfirmation)){

					validationErrors.add(new ValidationError("InvalidCaptchaConfirmation"));
				}

				if(!validationErrors.isEmpty()){

					throw new ValidationException(validationErrors);
				}

				User requestedUser = systemInterface.getUserHandler().getUserByEmail(email, false, true);

				if(requestedUser == null || (requireUsername && !requestedUser.getUsername().equalsIgnoreCase(username))){

					throw new ValidationException(new ValidationError("UserNotFound"));

				}else if(!(requestedUser instanceof MutableUser)){

					throw new ValidationException(new ValidationError("UserNotMutable"));

				}else if(!requestedUser.isEnabled()){

					throw new ValidationException(new ValidationError("UserNotEnabled"));
				}

				String newPassword = RandomUtils.getRandomString(7, 10);

				((MutableUser)requestedUser).setPassword(newPassword);

				try {
					systemInterface.getUserHandler().updateUser(requestedUser, true, false, false);

				} catch (UnableToUpdateUserException e) {

					throw new ValidationException(new ValidationError("UnableToUpdateUser"));
				}

				log.info("Sending new password to user " + user);

				if(!sendNewPasswordMail(requestedUser,newPassword)){

					throw new ValidationException(new ValidationError("ErrorSendingMail"));

				}else{

					Document doc = createDocument(req, uriParser);

					Element newPasswordSentElement = doc.createElement("NewPasswordSent");
					doc.getFirstChild().appendChild(newPasswordSentElement);

					XMLUtils.appendNewCDATAElement(doc, newPasswordSentElement, "newPasswordSentMessage", this.newPasswordSentMessage);

					return new SimpleForegroundModuleResponse(doc, getDefaultBreadcrumb());
				}

			}catch(ValidationException e){

				validationException = e;
			}
		}

		log.info("User " + user + " requesting new password form");

		Document doc = createDocument(req, uriParser);

		Element newPasswordFormElement = doc.createElement("NewPasswordForm");

		doc.getFirstChild().appendChild(newPasswordFormElement);

		if(validationException != null){

			newPasswordFormElement.appendChild(validationException.toXML(doc));
			newPasswordFormElement.appendChild(RequestUtils.getRequestParameters(req, doc,"username","email","captchaConfirmation"));
		}

		XMLUtils.appendNewCDATAElement(doc, newPasswordFormElement, "newPasswordFormMessage", this.newPasswordFormMessage);

		if(requireUsername){
			newPasswordFormElement.appendChild(doc.createElement("requireUsername"));
		}

		if(requireCaptchaConfirmation){
			newPasswordFormElement.appendChild(doc.createElement("requireCaptchaConfirmation"));
		}

		return new SimpleForegroundModuleResponse(doc, getDefaultBreadcrumb());
	}

	protected Document createDocument(HttpServletRequest req, URIParser uriParser) {

		Document doc = XMLUtils.createDomDocument();
		Element document = doc.createElement("Document");
		doc.appendChild(document);

		document.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser));
		document.appendChild(moduleDescriptor.toXML(doc));

		return doc;
	}

	protected boolean sendNewPasswordMail(User user, String newPassword) {

		TagReplacer tagReplacer = new TagReplacer(USER_TAG_SOURCE_FACTORY.getTagSource(user),PASSWORD_TAG_SOURCE_FACTORY.getTagSource(newPassword));

		try {
			SimpleEmail email = new SimpleEmail();
			email.setSenderName(senderName);
			email.setSenderAddress(senderAddress);
			email.addRecipient(user.getEmail());
			email.setSubject(tagReplacer.replace(subject));
			email.setMessage(tagReplacer.replace(message));
			email.setMessageContentType(SimpleEmail.TEXT);

			systemInterface.getEmailHandler().send(email);

			return true;

		} catch (InvalidEmailAddressException e) {

			log.error("Error sending new password to user " + user,e);

		} catch (NoEmailSendersFoundException e) {

			log.error("Error sending new password to user " + user,e);

		} catch (UnableToProcessEmailException e) {

			log.error("Error sending new password to user " + user,e);
		}

		return false;
	}

	@WebPublic(alias="captcha")
	public SimpleForegroundModuleResponse getCaptchaImage(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		captchaHandler.getCaptchaImage(req, res);

		return null;
	}
}
