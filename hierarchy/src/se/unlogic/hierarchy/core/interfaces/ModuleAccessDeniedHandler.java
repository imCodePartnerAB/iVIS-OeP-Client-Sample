package se.unlogic.hierarchy.core.interfaces;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.webutils.http.URIParser;


public interface ModuleAccessDeniedHandler extends Prioritized{

	public boolean supportsRequest(HttpServletRequest req, User user, URIParser uriParser, ModuleDescriptor moduleDescriptor) throws Throwable;

	public void handleRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, ModuleDescriptor moduleDescriptor) throws Throwable;	
}
