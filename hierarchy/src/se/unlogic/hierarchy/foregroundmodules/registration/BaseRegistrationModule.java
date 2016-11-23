/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.registration;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.emailutils.framework.InvalidEmailAddressException;
import se.unlogic.emailutils.framework.NoEmailSendersFoundException;
import se.unlogic.emailutils.framework.SimpleEmail;
import se.unlogic.emailutils.framework.UnableToProcessEmailException;
import se.unlogic.hierarchy.core.annotations.CheckboxSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.GroupMultiListSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.HTMLEditorSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextAreaSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.UserMultiListSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.annotations.XSLVariable;
import se.unlogic.hierarchy.core.beans.AttributeDescriptor;
import se.unlogic.hierarchy.core.beans.SettingDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.comparators.PriorityComparator;
import se.unlogic.hierarchy.core.enums.CRUDAction;
import se.unlogic.hierarchy.core.enums.EventTarget;
import se.unlogic.hierarchy.core.events.CRUDEvent;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.exceptions.UnableToAddUserException;
import se.unlogic.hierarchy.core.exceptions.UnableToDeleteUserException;
import se.unlogic.hierarchy.core.exceptions.UnableToUpdateUserException;
import se.unlogic.hierarchy.core.interfaces.AttributeHandler;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.MutableAttributeHandler;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.interfaces.ViewFragment;
import se.unlogic.hierarchy.core.utils.AttributeDescriptorUtils;
import se.unlogic.hierarchy.core.utils.ModuleUtils;
import se.unlogic.hierarchy.core.utils.ViewFragmentUtils;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.purecaptcha.CaptchaHandler;
import se.unlogic.purecaptcha.DefaultCaptchaHandler;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.enums.Order;
import se.unlogic.standardutils.string.AnnotatedBeanTagSourceFactory;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.string.TagReplacer;
import se.unlogic.standardutils.string.TagSource;
import se.unlogic.standardutils.string.TagSourceFactory;
import se.unlogic.standardutils.time.MillisecondTimeUnits;
import se.unlogic.standardutils.timer.RunnableTimerTask;
import se.unlogic.standardutils.validation.PositiveStringIntegerValidator;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationErrorType;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.SessionUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.url.URLRewriter;

public abstract class BaseRegistrationModule<UserType extends User, ConfirmationType extends Confirmation> extends AnnotatedForegroundModule implements Runnable {

	protected static final String CAPTCHA_PASSED_SESSION_PREFIX = BaseRegistrationModule.class + "_captcha_passed_";
	
	protected static final PriorityComparator PRIORITY_COMPARATOR = new PriorityComparator(Order.ASC);
	
	protected static final String DEAFULT_SENDER_EMAIL_ADDRESS = "someone@somesite";
	protected static final String DEAFULT_SENDER_EMAIL_NAME = "John Doe";
	protected static final String CONFIRMATION_LINK = "$confirmation-link";
	protected static final String CONFIRMATION_TIMEOUT = "$confirmation-timeout";

	protected final ReentrantReadWriteLock pluginReadWriteLock = new ReentrantReadWriteLock();
	protected final Lock pluginReadLock = pluginReadWriteLock.readLock();
	protected final Lock pluginWriteLock = pluginReadWriteLock.writeLock();
	
	@ModuleSetting
	@CheckboxSettingDescriptor(name = "Require manual confirmation", description = "Controls whether or not new accounts have to be manually enabled (note that this setting disables e-mail confirmation)")
	protected boolean requireManualConfirmation = false;	
	
	@ModuleSetting
	@CheckboxSettingDescriptor(name = "Require e-mail confirmation", description = "Controls whether or not new accounts have to be verified thru an e-mail confirmation")
	protected boolean requireEmailConfirmation = true;

	@ModuleSetting
	@CheckboxSettingDescriptor(name = "Require captcha confirmation", description = "Controls whether account creation requires captcha confirmation")
	protected boolean requireCaptchaConfirmation = true;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Captcha characters", description = "The number of characters used when generating captcha images", required = false, formatValidator = PositiveStringIntegerValidator.class)
	protected int captchaCharacters = 5;
	
	@ModuleSetting
	@HTMLEditorSettingDescriptor(name = "Registration message", description = "The message that is displayed above the registration form", required = true)
	@XSLVariable(name = "defaultRegistrationMessage")
	protected String registrationMessage = "This string should be set by your XSL stylesheet";

	@ModuleSetting
	@HTMLEditorSettingDescriptor(name = "Registered message", description = "The message that is displayed after registration", required = true)
	@XSLVariable(name = "defaultRegisteredMessage")
	protected String registereredMessage = "This string should be set by your XSL stylesheet";

	@ModuleSetting
	@HTMLEditorSettingDescriptor(name = "Account enabled message", description = "The text that is displayed after the user has confirmed his account", required = true)
	@XSLVariable(name = "defaultAccountEnabledMessage")
	protected String accountEnabledMessage = "This string should be set by your XSL stylesheet";

	@ModuleSetting(allowsNull = true)
	@TextAreaSettingDescriptor(name = "Supported attributes", description = "The attributes to show in the form. The format is [name][*/!]:[display name]:[max length]:[StringFormatValidator] (without brackets). Only the name is required. The * sign indicates if the attribute is required or not. The ! sign indicates that the attribute is read only")
	protected String supportedAttributes;
		
	@ModuleSetting
	@CheckboxSettingDescriptor(name = "Require user conditions confirmation", description = "Controls whether or not new accounts have to confirmate user conditions")
	protected boolean requireUserConditionConfirmation;
	
	@ModuleSetting
	@HTMLEditorSettingDescriptor(name = "User conditions", description = "The text that is displayed as user conditions", required = false)
	@XSLVariable(name = "defaultUserConditions")
	protected String userConditions = "This string should be set by your XSL stylesheet";

	//Workaround since default values with i18n support isn't available in module setting annotations
	@XSLVariable
	protected String defaultEmailSubject = "This string should be set by your XSL stylesheet";

	//Workaround since default values with i18n support isn't available in module setting annotations
	@XSLVariable
	protected String defaultEmailText = "This string should be set by your XSL stylesheet";

	@ModuleSetting
	@XSLVariable(name = "defaultEmailSubject")
	protected String emailSubject = "This string should be set by your XSL stylesheet";

	@ModuleSetting
	@XSLVariable(name = "defaultEmailText")
	protected String emailText = "This string should be set by your XSL stylesheet";

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Email address", description = "The address from which emails from this module are said to be sent from", required = true)
	protected String emailSenderAddress = "someone@somesite";

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Email sender name", description = "The name of the sender from which emails from this module are said to be sent from", required = true)
	protected String emailSenderName = "John Doe";

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Confirmation timeout", description = "The amount of time in days before unanswered confirmations are deleted", required = true)
	protected int confirmationTimeout = 7;

	@ModuleSetting
	@CheckboxSettingDescriptor(name = "Enable new account notification", description = "Controls whether or not new account notification is enabled.")
	protected boolean newAccountNotification;

	@ModuleSetting(allowsNull = true)
	@UserMultiListSettingDescriptor(name = "Users to notify", description = "Users to be notified when new accounts are activated")
	protected List<Integer> newAccountSubscriberUsers;

	@ModuleSetting(allowsNull = true)
	@GroupMultiListSettingDescriptor(name = "Groups to notify", description = "Groups to be notified when new accounts are activated")
	protected List<Integer> newAccountSubscriberGroups;

	//Workaround since default values with i18n support isn't available in module setting annotations
	@XSLVariable
	protected String defaultNewAccountNotificationSubject = "This string should be set by your XSL stylesheet";

	//Workaround since default values with i18n support isn't available in module setting annotations
	@XSLVariable
	protected String defaultNewAccountNotificationText = "This string should be set by your XSL stylesheet";

	@ModuleSetting
	@XSLVariable(name = "defaultNewAccountNotificationSubject")
	protected String newAccountNotificationSubject = "This string should be set by your XSL stylesheet";

	@ModuleSetting
	@XSLVariable(name = "defaultNewAccountNotificationText")
	protected String newAccountNotificationText = "This string should be set by your XSL stylesheet";

	protected Timer timer;

	protected CaptchaHandler captchaHandler;

	protected TagSourceFactory<? super UserType> userTagSourceFactory;
	protected TagSourceFactory<User> subscriberTagSourceFactory;
	
	protected List<AttributeDescriptor> attributes;

	@SuppressWarnings("rawtypes")
	protected List<RegistrationPlugin> registrationPlugins;
	
	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		timer = new Timer("Timer for foreground module " + moduleDescriptor, true);

		timer.schedule(new RunnableTimerTask(this), MillisecondTimeUnits.SECOND * 30, MillisecondTimeUnits.MINUTE);


		userTagSourceFactory = createUserTagSourceFactory();
		subscriberTagSourceFactory = createSubscriberTagSourceFactory();
	}

	@Override
	protected void moduleConfigured() throws Exception {
		
		super.moduleConfigured();
	
		captchaHandler = createCaptchaHandler();
		
		attributes = AttributeDescriptorUtils.parseAttributes(supportedAttributes);
	}

	protected TagSourceFactory<? super UserType> createUserTagSourceFactory() {

		return new AnnotatedBeanTagSourceFactory<User>(User.class, "$user.");
	}

	protected TagSourceFactory<User> createSubscriberTagSourceFactory() {

		return new AnnotatedBeanTagSourceFactory<User>(User.class, "$subscriber.");
	}

	protected CaptchaHandler createCaptchaHandler() {

		return captchaHandler = new DefaultCaptchaHandler(this.getClass().getName() + ":" + this.moduleDescriptor.getModuleID(), 5 * MillisecondTimeUnits.MINUTE, false, captchaCharacters);
	}

	@Override
	public void unload() throws Exception {

		timer.cancel();

		if(registrationPlugins != null){
			
			registrationPlugins.clear();
		}
		
		super.unload();
	}

	@Override
	public void run() {

		this.deleteOldConfirmations();
	}

	protected abstract void deleteOldConfirmations();

	@Override
	public SimpleForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		try{
			pluginReadLock.lock();
			
			ValidationException validationException = null;

			RegistrationPluginHandler pluginHandler;
			
			if(this.registrationPlugins != null){
				
				pluginHandler = new RegistrationPluginHandler(this.registrationPlugins);
				
			}else{
				
				pluginHandler = null;
			}
			
			if (req.getMethod().equalsIgnoreCase("POST")) {
				try {
					UserType newUser = populate(req);

					populateAttributes(newUser, req);
					
					this.validatePopulation(newUser, req);

					//Populate registration plugins
					if(pluginHandler != null){
						
						pluginHandler.populate(req);
						
						if(pluginHandler.hasValidationErrors()){
							
							throw new PluginValidationException();
						}
					}
					
					this.setUserDefaultAccess(newUser, req);

					log.info("Registering user " + newUser + " requesting from " + req.getRemoteAddr());

					ConfirmationType confirmation = null;

					ArrayList<ValidationError> validationErrors = new ArrayList<ValidationError>();

					try {

						this.addUser(newUser);

						SessionUtils.removeAttribute(CAPTCHA_PASSED_SESSION_PREFIX + this.moduleDescriptor.getModuleID(), req);
						
						if(requireManualConfirmation){
							
							//Trigger plugin user added event
							if(pluginHandler != null){
								
								pluginHandler.userAdded(newUser);
							}
							
							systemInterface.getEventHandler().sendEvent(User.class, new CRUDEvent<User>(CRUDAction.ADD, newUser), EventTarget.ALL);
							
							if (newAccountNotification) {
								sendNewAccountNotifcations(newUser);
							}							
							
							return showRegisteredMessage(req, uriParser, newUser);
							
						}else if (this.requireEmailConfirmation) {

							confirmation = createConfirmation(newUser.getUserID(), UUID.randomUUID().toString(), req.getRemoteHost());

							this.addConfirmation(confirmation);

							this.sendEmailConfirmation(newUser, confirmation, req, uriParser);

							//Trigger plugin user added event
							if(pluginHandler != null){
								
								pluginHandler.userAdded(newUser);
							}
							
							systemInterface.getEventHandler().sendEvent(User.class, new CRUDEvent<User>(CRUDAction.ADD, newUser), EventTarget.ALL);
							
							return showRegisteredMessage(req, uriParser, newUser);
							
						} else {
					
							this.enableUserAccount(newUser);

							//Trigger plugin user added event
							if(pluginHandler != null){
								
								pluginHandler.userAdded(newUser);
							}
							
							systemInterface.getEventHandler().sendEvent(User.class, new CRUDEvent<User>(CRUDAction.ADD, newUser), EventTarget.ALL);
							
							return accountActivated(req, res, uriParser, newUser, false);
						}

					} catch (Exception e) {

						this.deleteUser(newUser);

						if (confirmation != null) {

							this.deleteConfirmation(confirmation);
						}

						if (e instanceof UnableToProcessEmailException) {

							validationErrors.add(new ValidationError("UnableToProcessEmail"));
							log.error("Error adding user", e);

						} else if (e instanceof InvalidEmailAddressException) {

							validationErrors.add(new ValidationError("InvalidEmailAddress"));
							log.error("Error adding user", e);

						} else if (e instanceof NoEmailSendersFoundException) {

							validationErrors.add(new ValidationError("NoEmailSendersFound"));
							log.error("Error adding user", e);

						} else {
							throw e;
						}
					}

					if (!validationErrors.isEmpty()) {

						throw new ValidationException(validationErrors);
					}

				} catch (ValidationException e) {
					validationException = e;
				}catch (PluginValidationException e) {}
			}

			log.info("User " + user + " requesting registration form");

			Document doc = this.createDocument(req, uriParser);
			Element addUserElement = doc.createElement("AddUser");
			doc.getFirstChild().appendChild(addUserElement);

			addUserElement.appendChild(XMLUtils.createElement("registrationMessage", URLRewriter.setAbsoluteLinkUrls(this.registrationMessage, req), doc));

			if (this.requireCaptchaConfirmation && SessionUtils.getAttribute(CAPTCHA_PASSED_SESSION_PREFIX + this.moduleDescriptor.getModuleID(), req) == null) {
				addUserElement.appendChild(doc.createElement("requireCaptchaConfirmation"));
			}

			if (this.requireUserConditionConfirmation) {
				addUserElement.appendChild(doc.createElement("requireUserConditionConfirmation"));
			}

			appendRegistrationFormData(doc, addUserElement);

			XMLUtils.append(doc, addUserElement, "AttrbuteDescriptors", attributes);
					
			if (validationException != null) {
				
				addUserElement.appendChild(validationException.toXML(doc));
				addUserElement.appendChild(RequestUtils.getRequestParameters(req, doc));
			
			}else if(pluginHandler != null && pluginHandler.hasValidationErrors()){
				
				addUserElement.appendChild(RequestUtils.getRequestParameters(req, doc));
			}

			SimpleForegroundModuleResponse moduleResponse = new SimpleForegroundModuleResponse(doc, this.moduleDescriptor.getName(), this.getDefaultBreadcrumb());
			
			if(pluginHandler != null){
				
				List<ViewFragment> viewFragments = pluginHandler.getViewFragments(req, uriParser);
				
				Element pluginsElement = XMLUtils.appendNewElement(doc, addUserElement, "PluginFragments");
				
				for(ViewFragment viewFragment : viewFragments){
					
					ViewFragmentUtils.appendLinksAndScripts(moduleResponse, viewFragment);
					
					pluginsElement.appendChild(viewFragment.toXML(doc));
				}
			}
			
			return moduleResponse;			
			
		}finally{
			
			pluginReadLock.unlock();
		}
	}

	private SimpleForegroundModuleResponse showRegisteredMessage(HttpServletRequest req, URIParser uriParser, User newUser) {

		Document doc = this.createDocument(req, uriParser);
		Element userAddedElement = doc.createElement("UserAdded");
		doc.getFirstChild().appendChild(userAddedElement);

		userAddedElement.appendChild(XMLUtils.createElement("registereredMessage", URLRewriter.setAbsoluteLinkUrls(this.registereredMessage, req), doc));

		userAddedElement.appendChild(newUser.toXML(doc));

		return new SimpleForegroundModuleResponse(doc, this.moduleDescriptor.getName(), this.getDefaultBreadcrumb());	
	}

	protected void populateAttributes(UserType newUser, HttpServletRequest req) throws ValidationException {

		if(this.attributes != null){

			AttributeHandler attributeHandler = newUser.getAttributeHandler();
			
			if(attributeHandler != null && attributeHandler instanceof MutableAttributeHandler){

				List<ValidationError> validationErrors = new ArrayList<ValidationError>(attributes.size());
				
				AttributeDescriptorUtils.populateAttributes((MutableAttributeHandler) attributeHandler, attributes, req, validationErrors);
				
				if(!validationErrors.isEmpty()){
					
					throw new ValidationException(validationErrors);
				}
			}
		}
	}

	public void appendRegistrationFormData(Document doc, Element addUserElement) {}

	@WebPublic(alias = "userconditions")
	public ForegroundModuleResponse showUserConditions(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		if (this.requireUserConditionConfirmation) {

			res.getWriter().write("<html><body>" + this.userConditions + "</body></html>");
			res.getWriter().close();

			return null;
		}

		throw new URINotFoundException(uriParser);

	}

	protected abstract ConfirmationType createConfirmation(Integer userID, String string, String remoteHost);

	protected abstract void deleteUser(UserType newUser) throws SQLException, UnableToDeleteUserException;

	protected abstract void addConfirmation(ConfirmationType confirmation) throws SQLException;

	protected abstract void addUser(UserType newUser) throws SQLException, UnableToAddUserException;

	protected void validatePopulation(UserType newUser, HttpServletRequest req) throws ValidationException, SQLException {

		ArrayList<ValidationError> validationErrors = new ArrayList<ValidationError>();

		if (findUserByUsername(newUser.getUsername()) != null) {
			validationErrors.add(new ValidationError("UsernameAlreadyTaken"));
		}

		this.validateEmailFields(validationErrors, req, newUser);

		String passwordConfirmation = req.getParameter("passwordConfirmation");

		if (StringUtils.isEmpty(req.getParameter("password"))) {

			validationErrors.add(new ValidationError("password", ValidationErrorType.RequiredField));

		} else if (StringUtils.isEmpty(passwordConfirmation) || !newUser.getPassword().equalsIgnoreCase(passwordConfirmation.trim())) {

			validationErrors.add(new ValidationError("PasswordConfirmationMismatch"));
		}

		if (this.requireCaptchaConfirmation && SessionUtils.getAttribute(CAPTCHA_PASSED_SESSION_PREFIX + this.moduleDescriptor.getModuleID(), req) == null) {

			String captchaConfirmation = req.getParameter("captchaConfirmation");

			if (StringUtils.isEmpty(captchaConfirmation) || !captchaHandler.isValidCode(req, captchaConfirmation)) {

				validationErrors.add(new ValidationError("InvalidCaptchaConfirmation"));
				
			}else{
				
				SessionUtils.setAttribute(CAPTCHA_PASSED_SESSION_PREFIX + this.moduleDescriptor.getModuleID(), true, req);
			}
		}

		if (this.requireUserConditionConfirmation && !Boolean.valueOf(req.getParameter("userConditionConfirmation"))) {
			validationErrors.add(new ValidationError("NoUserConditionConfirmation"));
		}

		if (!validationErrors.isEmpty()) {

			throw new ValidationException(validationErrors);
		}
	}

	protected void validateEmailFields(ArrayList<ValidationError> validationErrors, HttpServletRequest req, UserType newUser) throws SQLException {

		String emailConfirmation = req.getParameter("emailConfirmation");

		if (newUser.getEmail() != null && (StringUtils.isEmpty(emailConfirmation) || !newUser.getEmail().equalsIgnoreCase(emailConfirmation.trim()))) {

			validationErrors.add(new ValidationError("EmailConfirmationMismatch"));

		} else if (newUser.getEmail() == null && !StringUtils.isEmpty(emailConfirmation)) {

			validationErrors.add(new ValidationError("EmailConfirmationMismatch"));

		} else if (newUser.getEmail() != null && findUserByEmail(newUser.getEmail()) != null) {

			validationErrors.add(new ValidationError("EmailAlreadyTaken"));
		}
	}

	protected abstract User findUserByEmail(String email) throws SQLException;

	protected abstract User findUserByUsername(String username) throws SQLException;

	protected abstract UserType populate(HttpServletRequest req) throws ValidationException;

	@WebPublic
	public SimpleForegroundModuleResponse confirm(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		if (uriParser.size() != 3) {
			throw new URINotFoundException(uriParser);
		}

		ConfirmationType confirmation = this.getConfirmation(uriParser.get(2));

		if (confirmation != null) {

			UserType newUser = this.findUserByID(confirmation.getUserID());

			log.info("User " + newUser + " activating account from address " + req.getRemoteAddr());

			if (!newUser.isEnabled()) {
				this.enableUserAccount(newUser);
			}

			this.deleteConfirmation(confirmation);

			return accountActivated(req, res, uriParser, newUser, false);
		}

		throw new URINotFoundException(uriParser);
	}

	protected SimpleForegroundModuleResponse accountActivated(HttpServletRequest req, HttpServletResponse res, URIParser uriParser, UserType newUser, boolean userChanged) throws Exception {

		if (newAccountNotification) {
			sendNewAccountNotifcations(newUser);
		}

		Document doc = this.createDocument(req, uriParser);
		Element userEnabledElement = doc.createElement("AccountEnabled");
		doc.getFirstChild().appendChild(userEnabledElement);

		userEnabledElement.appendChild(newUser.toXML(doc));

		userEnabledElement.appendChild(XMLUtils.createElement("accountEnabledMessage", URLRewriter.setAbsoluteLinkUrls(this.accountEnabledMessage, req), doc));

		SimpleForegroundModuleResponse moduleRespone = new SimpleForegroundModuleResponse(doc, this.moduleDescriptor.getName(), this.getDefaultBreadcrumb());

		moduleRespone.setUserChanged(userChanged);

		return moduleRespone;
	}

	private void sendNewAccountNotifcations(UserType newUser) {

		Collection<User> users = null;

		if (newAccountSubscriberUsers != null) {

			users = systemInterface.getUserHandler().getUsers(newAccountSubscriberUsers, false, false);
		}

		if (newAccountSubscriberGroups != null) {

			if (users == null) {

				users = systemInterface.getUserHandler().getUsersByGroups(newAccountSubscriberGroups, false);

			} else {

				Collection<User> groupUsers = systemInterface.getUserHandler().getUsersByGroups(newAccountSubscriberGroups, false);

				if (groupUsers != null) {

					users = new HashSet<User>(users);
					users.addAll(groupUsers);
				}
			}
		}

		if (CollectionUtils.isEmpty(users)) {

			return;
		}

		for (User subscriber : users) {

			try {
				TagReplacer tagReplacer = getNewAccountNotificationTagReplacer(newUser, subscriber);

				SimpleEmail email = new SimpleEmail();

				email.setSenderName(emailSenderName);
				email.setSenderAddress(this.emailSenderAddress);
				email.setSubject(tagReplacer.replace(this.newAccountNotificationSubject));
				email.setMessage(tagReplacer.replace(this.newAccountNotificationText));
				email.addRecipient(subscriber.getEmail());

				this.systemInterface.getEmailHandler().send(email);

			} catch (InvalidEmailAddressException e) {

				log.error("Error sending new account notification to user " + subscriber, e);

			} catch (NoEmailSendersFoundException e) {

				log.error("Error sending new account notification to user " + subscriber, e);

			} catch (UnableToProcessEmailException e) {

				log.error("Error sending new account notification to user " + subscriber, e);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected TagReplacer getNewAccountNotificationTagReplacer(UserType user, User subscriber) {

		pluginReadLock.lock();
		
		try{
			if(registrationPlugins != null){
				
				List<TagSource> tagSources = new ArrayList<TagSource>(registrationPlugins.size() + 2);
				
				tagSources.add(userTagSourceFactory.getTagSource(user));
				tagSources.add(subscriberTagSourceFactory.getTagSource(subscriber));
				
				for(RegistrationPlugin registrationPlugin : registrationPlugins){
					
					try{
						List<TagSource> pluginTagSources = registrationPlugin.getTagSources(user);
						
						if(pluginTagSources != null){
							
							tagSources.addAll(pluginTagSources);
						}
						
					}catch(Exception e){
						
						log.error("Error getting tag source from registration plugin " + registrationPlugin, e);
					}
				}
				
				return new TagReplacer(tagSources);
			}
			
		}finally{
			
			pluginReadLock.unlock();	
		}
		
		return new TagReplacer(userTagSourceFactory.getTagSource(user), subscriberTagSourceFactory.getTagSource(subscriber));
	}

	protected abstract void deleteConfirmation(ConfirmationType confirmation) throws SQLException;

	protected abstract void enableUserAccount(UserType newUser) throws SQLException, UnableToUpdateUserException;

	protected abstract UserType findUserByID(Integer userID) throws SQLException;

	protected abstract ConfirmationType getConfirmation(String id) throws SQLException;

	@WebPublic(alias = "captcha")
	public SimpleForegroundModuleResponse getCaptchaImage(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		captchaHandler.getCaptchaImage(req, res);

		return null;
	}

	protected void sendEmailConfirmation(UserType newUser, ConfirmationType confirmation, HttpServletRequest req, URIParser uriParser) throws InvalidEmailAddressException, NoEmailSendersFoundException, UnableToProcessEmailException {

		TagReplacer tagReplacer = this.getConfirmationTagReplacer(newUser, confirmation, req);

		SimpleEmail email = new SimpleEmail();

		email.setSenderName(emailSenderName);
		email.setSenderAddress(this.emailSenderAddress);
		email.setSubject(tagReplacer.replace(this.emailSubject));
		email.addRecipient(newUser.getEmail());

		String message = tagReplacer.replace(this.emailText);

		message = message.replace(CONFIRMATION_LINK, uriParser.getRequestURL() + "/confirm/" + confirmation.getLinkID());
		message = message.replace(CONFIRMATION_TIMEOUT, confirmationTimeout + "");

		email.setMessage(message);

		this.sectionInterface.getSystemInterface().getEmailHandler().send(email);
	}

	protected abstract TagReplacer getConfirmationTagReplacer(UserType newUser, ConfirmationType confirmation, HttpServletRequest req);

	protected void setUserDefaultAccess(UserType newUser, HttpServletRequest req) {

	}

	public Document createDocument(HttpServletRequest req, URIParser uriParser) {

		Document doc = XMLUtils.createDomDocument();
		Element document = doc.createElement("Document");
		document.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser));
		document.appendChild(this.moduleDescriptor.toXML(doc));
		doc.appendChild(document);
		return doc;
	}

	@Override
	public List<SettingDescriptor> getSettings() {

		ArrayList<SettingDescriptor> combinedSettings = new ArrayList<SettingDescriptor>();

		ModuleUtils.addSettings(combinedSettings, super.getSettings());

		combinedSettings.add(SettingDescriptor.createTextFieldSetting("emailSubject", "Confirmation email subject", "The subject of the confirmation email. The following tags can be used: " + getConfirmationTags(), true, defaultEmailSubject, null));
		combinedSettings.add(SettingDescriptor.createTextAreaSetting("emailText", "Email text", "The text of the confirmation e-mail. The following tags can be used: " + getConfirmationTags(), true, defaultEmailText, null));

		combinedSettings.add(SettingDescriptor.createTextFieldSetting("newAccountNotificationSubject", "New account notification subject", "The subject of the new account notification email. The following tags can be used: " + getNewAccountNotificationTags(), true, defaultNewAccountNotificationSubject, null));
		combinedSettings.add(SettingDescriptor.createTextAreaSetting("newAccountNotificationText", "New account notification text", "The text of the new account notification e-mail. The following tags can be used: " + getNewAccountNotificationTags(), true, defaultNewAccountNotificationText, null));

		return combinedSettings;
	}

	protected String getNewAccountNotificationTags() {

		return userTagSourceFactory.getAvailableTags() + ", " + subscriberTagSourceFactory.getAvailableTags();
	}

	protected abstract String getConfirmationTags();
	
	@SuppressWarnings("rawtypes")
	public boolean addRegistrationPlugin(RegistrationPlugin<?> registrationPlugin){
		
		try{
			pluginWriteLock.lock();
			
			boolean added;
			
			if(registrationPlugins == null){
				
				registrationPlugins = new ArrayList<RegistrationPlugin>();
				added = registrationPlugins.add(registrationPlugin);
				
			}else{
				
				if(!registrationPlugins.contains(registrationPlugin)){
					
					added = registrationPlugins.add(registrationPlugin);
					
				}else {
					
					return false;
				}
			}
			
			if(added){
				
				log.info("Registration plugin " + registrationPlugin + " added to module " + moduleDescriptor);
				
				Collections.sort(registrationPlugins, PRIORITY_COMPARATOR);
			}
			
			return added;
			
		}finally{
			
			pluginWriteLock.unlock();
		}
	}
	
	public boolean removeRegistrationPlugin(RegistrationPlugin<?> registrationPlugin){
		
		try{
			pluginWriteLock.lock();
			
			if(registrationPlugins == null){
				
				return false;
			}
			
			boolean removed = registrationPlugins.remove(registrationPlugin);			
			
			if(removed){
				
				log.info("Registration plugin " + registrationPlugin + " removed from module " + moduleDescriptor);
				
				if(registrationPlugins.isEmpty()){
					
					registrationPlugins = null;
				}
			}
			
			return removed;
			
		}finally{
			
			pluginWriteLock.unlock();
		}		
	}
}
