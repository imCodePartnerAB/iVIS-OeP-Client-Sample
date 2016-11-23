package se.unlogic.hierarchy.core.interfaces;

import javax.servlet.http.HttpSessionEvent;


public interface SessionListener {

	public void sessionCreated(HttpSessionEvent event) throws Exception;

	public void sessionDestroyed(HttpSessionEvent event) throws Exception;
}
