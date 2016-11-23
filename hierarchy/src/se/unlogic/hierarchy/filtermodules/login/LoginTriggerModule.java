package se.unlogic.hierarchy.filtermodules.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.FilterChain;
import se.unlogic.hierarchy.filtermodules.AnnotatedFilterModule;
import se.unlogic.webutils.http.URIParser;


public class LoginTriggerModule extends AnnotatedFilterModule {

	@ModuleSetting
	@TextFieldSettingDescriptor(name="Parameter name", description="The name of the parameter that triggers this module", required=true)
	private String parameterName = "triggerlogin";

	@Override
	public void doFilter(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, FilterChain filterChain) throws Exception {

		if(user == null && req.getParameter(parameterName) != null){

			log.info("Triggering login for user " + user + " requesting from " + req.getRemoteAddr());
			systemInterface.getLoginHandler().processLoginRequest(req, res, uriParser, true);

			if (res.isCommitted()) {
				return;
			}
		}

		filterChain.doFilter(req, res, user, uriParser);
	}
}
