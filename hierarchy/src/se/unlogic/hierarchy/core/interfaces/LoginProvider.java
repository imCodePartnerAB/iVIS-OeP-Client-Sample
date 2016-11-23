package se.unlogic.hierarchy.core.interfaces;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.webutils.http.URIParser;


public interface LoginProvider extends Prioritized{

	public ProviderDescriptor getProviderDescriptor();
	
	public boolean supportsRequest(HttpServletRequest req, URIParser uriParser) throws Throwable;

	public void handleRequest(HttpServletRequest req, HttpServletResponse res, URIParser uriParser, String redirectURI) throws Throwable;

	public boolean loginUser(HttpServletRequest req, URIParser uriParser, User user) throws Exception;
}
