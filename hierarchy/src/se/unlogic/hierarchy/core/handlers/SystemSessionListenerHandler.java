package se.unlogic.hierarchy.core.handlers;

import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

import se.unlogic.hierarchy.core.interfaces.SessionListener;
import se.unlogic.hierarchy.core.interfaces.SessionListenerHandler;


public class SystemSessionListenerHandler implements HttpSessionListener, SessionListenerHandler {

	protected Logger log = Logger.getLogger(this.getClass());

	private final CopyOnWriteArraySet<SessionListener> sessionListeners = new CopyOnWriteArraySet<SessionListener>();

	@Override
	public boolean addListener(SessionListener listener){

		return sessionListeners.add(listener);
	}

	@Override
	public boolean removeListener(SessionListener listener){

		return sessionListeners.remove(listener);
	}

	@Override
	public void sessionCreated(HttpSessionEvent event) {

		for(SessionListener listener : sessionListeners){

			try{
				listener.sessionCreated(event);

			}catch(Exception e){

				log.error("Error notifying session listener " + listener, e);
			}
		}
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {

		for(SessionListener listener : sessionListeners){

			try{
				listener.sessionDestroyed(event);

			}catch(Exception e){

				log.error("Error notifying session listener " + listener, e);
			}
		}
	}

	public void clear(){

		for(SessionListener sessionListener : sessionListeners){

			log.warn("Session listener " + sessionListener + " not removed from handler on shutdown.");
		}
	}
}
