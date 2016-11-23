package se.unlogic.hierarchy.filtermodules.login;

import java.sql.SQLException;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.unlogic.hierarchy.core.annotations.CheckboxSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.EnumDropDownSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.beans.MutableUser;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.EventTarget;
import se.unlogic.hierarchy.core.interfaces.FilterChain;
import se.unlogic.hierarchy.filtermodules.AnnotatedFilterModule;
import se.unlogic.hierarchy.foregroundmodules.login.LoginEvent;
import se.unlogic.log4jutils.levels.LogLevel;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.webutils.http.URIParser;


public abstract class InlineLoginModule extends AnnotatedFilterModule {

	@ModuleSetting
	@CheckboxSettingDescriptor(name="Add user to session", description="Controls if the user object should be added to the session or if the login should only be valid for the current request.")
	protected boolean useSession;

	@ModuleSetting
	@CheckboxSettingDescriptor(name="Update last login",description="Controls if the last login field of the user should be updated")
	protected boolean updateLastLogin;

	@ModuleSetting
	@EnumDropDownSettingDescriptor(name="Login log level", description="The log level used to log logins via this module", required=true)
	private LogLevel loginLogLevel = LogLevel.INFO;

	@Override
	public void doFilter(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, FilterChain filterChain) throws Exception {

		String username = getUsername(req);
		String password = getPassword(req);

		if(!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)){

			User requestedUser = systemInterface.getUserHandler().getUserByUsernamePassword(username, password, true, true);

			if(requestedUser != null && !(user != null && user.equals(requestedUser))){

				if(updateLastLogin){

					setLastLogin(requestedUser);
				}

				if(useSession){

					req.getSession(true).setAttribute("user", requestedUser);
				}

				log.log(loginLogLevel.getLevel(), "User " + requestedUser + " logged in from address " + req.getRemoteHost());

				systemInterface.getEventHandler().sendEvent(User.class, new LoginEvent(requestedUser, req.getSession()), EventTarget.ALL);

				user = requestedUser;

			}else if(requestedUser == null){

				log.warn("Failed login attempt using username " + username + " from address " + req.getRemoteHost());
			}
		}

		filterChain.doFilter(req, res, user, uriParser);
	}

	protected abstract String getUsername(HttpServletRequest req);

	protected abstract String getPassword(HttpServletRequest req);

	protected void setLastLogin(User user) throws SQLException {

		user.setCurrentLogin(new Timestamp(System.currentTimeMillis()));

		if(user instanceof MutableUser){

			MutableUser mutableUser = (MutableUser) user;

			Timestamp lastLogin = user.getLastLogin();

			mutableUser.setLastLogin(user.getCurrentLogin());

			try {
				systemInterface.getUserHandler().updateUser(mutableUser, false, false, false);

			} catch (Exception e) {

				log.error("Unable to update last login for user " + user,e);
			}

			mutableUser.setLastLogin(lastLogin);
		}
	}
}
