package se.unlogic.hierarchy.filtermodules.login;

import javax.servlet.http.HttpServletRequest;


public class HeaderLoginModule extends InlineLoginModule {

	@Override
	protected String getUsername(HttpServletRequest req) {

		return req.getHeader("username");
	}

	@Override
	protected String getPassword(HttpServletRequest req) {

		return req.getHeader("password");
	}
}
