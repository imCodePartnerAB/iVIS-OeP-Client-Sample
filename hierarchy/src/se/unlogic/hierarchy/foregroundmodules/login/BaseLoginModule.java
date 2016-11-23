/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.login;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.annotations.CheckboxSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextAreaSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.SimpleProviderDescriptor;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.EventTarget;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.LoginProvider;
import se.unlogic.hierarchy.core.interfaces.MutableSettingHandler;
import se.unlogic.hierarchy.core.interfaces.ProviderDescriptor;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.NonNegativeStringIntegerValidator;
import se.unlogic.standardutils.validation.PositiveStringIntegerValidator;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.URIParser;

public abstract class BaseLoginModule<UserType extends User> extends AnnotatedForegroundModule implements LoginProvider {

	@ModuleSetting(id = "userTimeout")
	@TextFieldSettingDescriptor(id = "userTimeout", name = "User session timeout", description = "Session timeout for normal users (in minutes)", required = true, formatValidator = PositiveStringIntegerValidator.class)
	protected int userSessionTimeout = 30;

	@ModuleSetting(id = "adminTimeout")
	@TextFieldSettingDescriptor(id = "adminTimeout", name = "Admin session timeout", description = "Session timeout for administrators (in minutes)", required = true, formatValidator = PositiveStringIntegerValidator.class)
	protected int adminSessionTimeout = 60;

	@ModuleSetting
	@TextAreaSettingDescriptor(name = "Logout module aliases", description = "The aliases of the logout modules (one per line)", required = true)
	protected String logoutModuleAliases = "/logout\n/logout/logout";

	@ModuleSetting(id = "default")
	@CheckboxSettingDescriptor(id = "default", name = "Add to login handler", description = "Controls if this module should add itself to the login handler as a login provider")
	protected boolean addToLoginHandler = true;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Login provider priority", description = "The priority of the login provider from this module (lower value means higher priority)", required = true, formatValidator = NonNegativeStringIntegerValidator.class)
	protected int priority = 100;

	@ModuleSetting(allowsNull = true)
	@TextFieldSettingDescriptor(name = "New password module alias", description = "The full alias of the new password module", required = false)
	protected String newPasswordModuleAlias;

	@ModuleSetting(allowsNull = true)
	@TextFieldSettingDescriptor(name = "Registration module alias", description = "The full alias of the registration module", required = false)
	protected String registrationModuleAlias;

	@ModuleSetting(allowsNull = true)
	@TextFieldSettingDescriptor(name = "Default redirect alias", description = "The full alias that users should be redirected to after login unless a redirect paramater is present in the URL. If this value is not set and no redirect paramater is present users will be redirected to the root of the context path.", required = false)
	protected String defaultRedirectAlias;

	@ModuleSetting
	@CheckboxSettingDescriptor(name = "Login retry lockout", description = "Should user be locked out after failed login attempts")
	protected boolean loginLockoutActivated = true;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Login lockout time", description = "Time in seconds that the user will be locked out after failed attempts")
	protected int loginLockoutTime = 1800;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Login retries", description = "Number of retries allowed in interval")
	protected int loginRetries = 10;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Login retry interval", description = "Interval in seconds for failed attempts before lockout")
	protected int loginRetryInterval = 600;

	protected RetryLimiter retryLimiter;

	protected List<String> logoutModuleAliasesList;
	
	protected ProviderDescriptor providerDescriptor;

	@Override
	protected void parseSettings(MutableSettingHandler mutableSettingHandler) throws Exception {

		super.parseSettings(mutableSettingHandler);

		if(logoutModuleAliases != null){

			logoutModuleAliasesList = Arrays.asList(logoutModuleAliases.split("\n"));
		}

		if(addToLoginHandler){

			this.sectionInterface.getSystemInterface().getLoginHandler().addProvider(this);

		}else{

			this.sectionInterface.getSystemInterface().getLoginHandler().removeProvider(this);
		}
	}

	@Override
	protected void moduleConfigured() throws Exception {

		retryLimiter = new RetryLimiter(loginLockoutActivated, loginLockoutTime, loginRetries, loginRetryInterval);
		
		providerDescriptor = new SimpleProviderDescriptor(moduleDescriptor);
	}

	@Override
	public void unload() throws Exception {

		this.sectionInterface.getSystemInterface().getLoginHandler().removeProvider(this);

		super.unload();
	}

	@Override
	public ForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return this.processRequest(req, res, user, uriParser);
	}

	@Override
	public SimpleForegroundModuleResponse processRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		String username = req.getParameter("username");
		String password = req.getParameter("password");

		if(!StringUtils.isEmpty(username) && password != null){

			if(retryLimiter.isLocked(username)){
				log.warn("Login refused for user " + username + " (account locked) accessing from address " + req.getRemoteHost());

				Document doc = this.createDocument(req, uriParser);

				XMLUtils.appendNewElement(doc, doc.getDocumentElement(), "AccountLocked", retryLimiter.getRemainingLockoutTime(username));

				return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb());
			}

			UserType loginUser = this.findByUsernamePassword(username, password);

			if(loginUser != null){

				retryLimiter.registerAuthSuccess(username);

				if(loginUser.isEnabled()){

					setLoggedIn(req, uriParser, loginUser);

					return this.sendRedirect(req, res, uriParser, loginUser);

				}else{
					log.warn("Login refused for user " + loginUser + " (account disabled) accessing from address " + req.getRemoteHost());

					Document doc = this.createDocument(req, uriParser);

					doc.getDocumentElement().appendChild(doc.createElement("AccountDisabled"));

					return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb());
				}
			}else{

				Document doc = this.createDocument(req, uriParser);

				if(retryLimiter.registerAuthFailure(username)){
					log.warn("Failed login attempt using username " + username + " from address " + req.getRemoteHost() + ", locking account");

					XMLUtils.appendNewElement(doc, doc.getDocumentElement(), "AccountLocked", retryLimiter.getRemainingLockoutTime(username));

					return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb());
				}

				log.warn("Failed login attempt using username " + username + " from address " + req.getRemoteHost());

				doc.getDocumentElement().appendChild(doc.createElement("LoginFailed"));

				return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb());
			}

		}else{

			log.info("User " + user + " requested login form");

			Document doc = this.createDocument(req, uriParser);

			doc.getDocumentElement().appendChild(doc.createElement("Login"));

			return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb());
		}
	}

	public void setLoggedIn(HttpServletRequest req, URIParser uriParser, UserType loginUser) throws Exception {

		// Set last login timestamp
		this.setLastLogin(loginUser);

		HttpSession session = req.getSession(true);

		session.setAttribute("user", loginUser);
		session.removeAttribute("usedRetries");

		// Set session timeout
		if(loginUser.isAdmin()){
			session.setMaxInactiveInterval(this.adminSessionTimeout * 60);
		}else{
			session.setMaxInactiveInterval(this.userSessionTimeout * 60);
		}

		log.info("User " + loginUser + " logged in from address " + req.getRemoteHost());

		systemInterface.getEventHandler().sendEvent(User.class, new LoginEvent(loginUser, session), EventTarget.ALL);
	}

	public SimpleForegroundModuleResponse sendRedirect(HttpServletRequest req, HttpServletResponse res, URIParser uriParser, UserType loginUser) throws Exception {

		String redirectParam = req.getParameter("redirect");

		if(redirectParam != null && redirectParam.startsWith("/")){

			res.sendRedirect(req.getContextPath() + redirectParam);

		}else if(defaultRedirectAlias != null){

			res.sendRedirect(req.getContextPath() + defaultRedirectAlias);

		}else{

			if(StringUtils.isEmpty(req.getContextPath())){

				res.sendRedirect("/");

			}else{

				res.sendRedirect(req.getContextPath());
			}
		}

		return null;
	}

	protected abstract void setLastLogin(UserType loginUser) throws Exception;

	protected abstract UserType findByUsernamePassword(String username, String password) throws Exception;

	protected Document createDocument(HttpServletRequest req, URIParser uriParser) {

		Document doc = XMLUtils.createDomDocument();
		Element document = doc.createElement("document");
		doc.appendChild(document);
		document.appendChild(this.moduleDescriptor.toXML(doc));
		XMLUtils.appendNewCDATAElement(doc, document, "newPasswordModuleAlias", this.newPasswordModuleAlias);
		XMLUtils.appendNewCDATAElement(doc, document, "registrationModuleAlias", this.registrationModuleAlias);
		XMLUtils.appendNewCDATAElement(doc, document, "uri", req.getContextPath() + uriParser.getFormattedURI());
		XMLUtils.appendNewCDATAElement(doc, document, "redirect", req.getParameter("redirect"));
		XMLUtils.appendNewCDATAElement(doc, document, "contextpath", req.getContextPath());

		return doc;
	}

	@Override
	public int getPriority() {

		return priority;
	}

	@Override
	public boolean supportsRequest(HttpServletRequest req, URIParser uriParser) throws Throwable {

		return true;
	}

	@Override
	public void handleRequest(HttpServletRequest req, HttpServletResponse res, URIParser uriParser, String redirectURI) throws Throwable {

		if(redirectURI != null){

			res.sendRedirect(this.getModuleURI(req) + "?redirect=" + URLEncoder.encode(redirectURI, "ISO-8859-1"));

		}else{

			redirectToDefaultMethod(req, res);
		}
	}

	public ForegroundModuleDescriptor getModuleDescriptor() {

		return moduleDescriptor;
	}

	@Override
	public ProviderDescriptor getProviderDescriptor() {

		return providerDescriptor;
	}
}
