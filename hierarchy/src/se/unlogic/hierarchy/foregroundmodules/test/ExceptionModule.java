package se.unlogic.hierarchy.foregroundmodules.test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.exceptions.ModuleConfigurationException;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.webutils.http.URIParser;


public class ExceptionModule extends AnnotatedForegroundModule {

	@Override
	public ForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		throw new NullPointerException("This is a null pointer exeption simulating and unhandled exception from a module.");	
	}
	
	@WebPublic
	public ForegroundModuleResponse accessDenied(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {
		
		throw new AccessDeniedException("Access denied to something somwhere.");
	}
	
	@WebPublic
	public ForegroundModuleResponse notFound(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {
		
		throw new URINotFoundException(uriParser);
	}
	
	@WebPublic
	public ForegroundModuleResponse nullResponse(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {
		
		return null;
	}
	
	@WebPublic
	public ForegroundModuleResponse configurationException(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {
		
		throw new ModuleConfigurationException("No setting for whatever set.");
	}
}
