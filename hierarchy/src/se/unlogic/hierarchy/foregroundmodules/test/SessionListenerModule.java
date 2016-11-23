package se.unlogic.hierarchy.foregroundmodules.test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionEvent;
import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.interfaces.SessionListener;
import se.unlogic.hierarchy.foregroundmodules.SimpleForegroundModule;
import se.unlogic.webutils.http.URIParser;


public class SessionListenerModule extends SimpleForegroundModule implements SessionListener{

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		if(systemInterface.getSessionListenerHandler() != null){

			systemInterface.getSessionListenerHandler().addListener(this);
		}
	}

	@Override
	public void unload() throws Exception {

		if(systemInterface.getSessionListenerHandler() != null){

			systemInterface.getSessionListenerHandler().removeListener(this);
		}

		super.unload();
	}

	@Override
	public ForegroundModuleResponse processRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return null;
	}

	@Override
	public void sessionCreated(HttpSessionEvent event) {

		log.info("Session created: " + event.getSession().getId());
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {

		log.info("Session destroyed: " + event.getSession().getId());
	}
}
