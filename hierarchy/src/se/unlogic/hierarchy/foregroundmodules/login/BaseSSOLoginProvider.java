package se.unlogic.hierarchy.foregroundmodules.login;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import se.unlogic.hierarchy.core.annotations.CheckboxSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.RadioButtonSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.TextAreaSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.beans.MutableUser;
import se.unlogic.hierarchy.core.beans.SimpleProviderDescriptor;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.EventTarget;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.LoginProvider;
import se.unlogic.hierarchy.core.interfaces.ProviderDescriptor;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.NonNegativeStringIntegerValidator;
import se.unlogic.standardutils.validation.PositiveStringIntegerValidator;
import se.unlogic.webutils.http.URIParser;

public abstract class BaseSSOLoginProvider extends AnnotatedForegroundModule implements LoginProvider {

	protected static final String LOGINFAILED_ATTRIBUTE = "SSOLoginFailed";

	@ModuleSetting
	@CheckboxSettingDescriptor(name = "Add to login handler", description = "Controls if this module should add itself to the login handler as a login provider")
	protected boolean addToLoginHandler = true;

	@ModuleSetting
	@RadioButtonSettingDescriptor(required = true, name = "Get user by", description = "Type of identification to use when getting user from userhandler", valueDescriptions = { "Username", "Email", "Attribute" }, values = { "USERNAME", "EMAIL", "ATTRIBUTE" })
	protected String getUserBy = "USERNAME";

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "User attribute to get user by", description = "The attribute to use when getting user from userhandler")
	protected String getUserAttribute;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Session timeout", description = "Session timeout in minutes", formatValidator = PositiveStringIntegerValidator.class)
	protected Integer userSessionTimeout = 30;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Session timeout (admin)", description = "Session timeout for admins, in minutes", formatValidator = PositiveStringIntegerValidator.class)
	protected int adminSessionTimeout = 60;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Login provider priority", description = "The priority of the login provider from this module (lower value means higher priority)", required = true, formatValidator = NonNegativeStringIntegerValidator.class)
	protected int priority = 100;

	@ModuleSetting(allowsNull = true)
	@TextFieldSettingDescriptor(name = "Default redirect alias", description = "The full alias that users should be redirected to after login unless a redirect paramater is present in the URL. If this value is not set and no redirect paramater is present users will be redirected to the root of the context path.", required = false)
	protected String defaultRedirectAlias;

	@ModuleSetting(allowsNull = true)
	@TextAreaSettingDescriptor(name="Allowed addresses", description="If this field is set the only addresses specified here will be allowed to use this module")
	protected List<String> allowedAddresses;

	@ModuleSetting(allowsNull = true)
	@TextAreaSettingDescriptor(name="Ignored addresses", description="Addresses from which requests will be ignored by this module")
	protected List<String> ignoredAddresses;

	@ModuleSetting(allowsNull = true)
	@TextAreaSettingDescriptor(name="Ignored header values", description="Requests with these headers set will be ignored")
	protected List<String> ignoredHeaders;

	protected ProviderDescriptor providerDescriptor;
	
	@Override
	protected void moduleConfigured() throws Exception {

		if (addToLoginHandler) {

			sectionInterface.getSystemInterface().getLoginHandler().addProvider(this);

		} else {

			sectionInterface.getSystemInterface().getLoginHandler().removeProvider(this);
		}

		providerDescriptor = new SimpleProviderDescriptor(moduleDescriptor);
	}

	@Override
	public ForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {

		if (supportsRequest(req, uriParser)) {

			String userIdentification = getUserIdentification(req, res, user, uriParser);

			if (userIdentification != null) {

				User loginUser = null;

				if (getUserBy.equals("USERNAME")) {

					loginUser = systemInterface.getUserHandler().getUserByUsername(userIdentification, true, true);

				} else if (getUserBy.equals("EMAIL")) {

					loginUser = systemInterface.getUserHandler().getUserByEmail(userIdentification, true, true);

				} else if (getUserBy.equals("ATTRIBUTE") && getUserAttribute != null) {

					loginUser = systemInterface.getUserHandler().getUserByAttribute(getUserAttribute, userIdentification, true, true);

				}

				if (loginUser != null) {

					setLoggedIn(req, uriParser, loginUser);

					systemInterface.getEventHandler().sendEvent(User.class, new LoginEvent(loginUser, req.getSession(true)), EventTarget.ALL);

					sendLoggedInRedirect(req, res, loginUser, uriParser);

					return null;

				} else {

					log.warn("Failed SSO login using user identification " + userIdentification + " from address " + req.getRemoteHost());

				}

			}

			req.setAttribute(LOGINFAILED_ATTRIBUTE, true);

		}

		if (!res.isCommitted()) {

			systemInterface.getLoginHandler().processLoginRequest(req, res, uriParser, false);

		}

		return null;

	}

	protected abstract String getUserIdentification(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable;

	protected void sendLoggedInRedirect(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException {

		res.sendRedirect(getRedirectURI(req));

	}

	protected void setLoggedIn(HttpServletRequest req, URIParser uriParser, User loginUser) throws SQLException {

		// Set last login timestamp
		this.setLastLogin(loginUser);

		HttpSession session = req.getSession(true);

		session.setAttribute("user", loginUser);

		// Set session timeout
		if (loginUser.isAdmin()) {
			session.setMaxInactiveInterval(this.adminSessionTimeout * 60);
		} else {
			session.setMaxInactiveInterval(this.userSessionTimeout * 60);
		}

		log.info("User " + loginUser + " SSO logged in from address " + req.getRemoteHost());

		systemInterface.getEventHandler().sendEvent(User.class, new LoginEvent(loginUser, session), EventTarget.ALL);

	}

	protected void setLastLogin(User user) throws SQLException {

		user.setCurrentLogin(new Timestamp(System.currentTimeMillis()));

		if (user instanceof MutableUser) {

			MutableUser mutableUser = (MutableUser) user;

			//TODO there must be a smarter way of solving this
			Timestamp lastLogin = user.getLastLogin();

			mutableUser.setLastLogin(user.getCurrentLogin());

			try {
				systemInterface.getUserHandler().updateUser(mutableUser, false, false, false);

			} catch (Exception e) {

				log.error("Unable to update user " + user, e);
			}

			mutableUser.setLastLogin(lastLogin);
		}
	}

	protected String getRedirectURI(HttpServletRequest req) {

		String redirectParam = req.getParameter("redirect");

		if (redirectParam != null && redirectParam.startsWith("/")) {

			return req.getContextPath() + redirectParam;

		} else if (defaultRedirectAlias != null) {

			return req.getContextPath() + defaultRedirectAlias;

		} else {

			if (StringUtils.isEmpty(req.getContextPath())) {

				return "/";

			} else {

				return req.getContextPath();
			}
		}

	}

	@Override
	public void handleRequest(HttpServletRequest req, HttpServletResponse res, URIParser uriParser, String redirectURI) throws Throwable {

		if(redirectURI != null){

			res.sendRedirect(this.getModuleURI(req) + "?redirect=" + URLEncoder.encode(redirectURI, "ISO-8859-1"));

		}else{

			redirectToDefaultMethod(req, res);
		}
	}

	@Override
	public boolean supportsRequest(HttpServletRequest req, URIParser uriParser) throws Throwable {

		if(ignoredHeaders != null){

			for(String header : ignoredHeaders){

				if(req.getHeader(header) != null){

					return false;
				}
			}
		}

		if(allowedAddresses != null && !allowedAddresses.contains(req.getRemoteAddr())){

			return false;
		}

		if(ignoredAddresses != null && ignoredAddresses.contains(req.getRemoteAddr())){

			return false;
		}

		return req.getAttribute(LOGINFAILED_ATTRIBUTE) == null;
	}

	@Override
	public boolean loginUser(HttpServletRequest req, URIParser uriParser, User user) throws Exception {

		return false;
	}

	@Override
	public int getPriority() {

		return priority;
	}

	@Override
	public void unload() throws Exception {

		this.sectionInterface.getSystemInterface().getLoginHandler().removeProvider(this);

		super.unload();
	}

	@Override
	public ProviderDescriptor getProviderDescriptor() {

		return providerDescriptor;
	}
}
