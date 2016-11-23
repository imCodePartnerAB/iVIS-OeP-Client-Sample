package se.unlogic.hierarchy.core.interfaces;


public interface SessionListenerHandler {

	public boolean addListener(SessionListener listener);

	public boolean removeListener(SessionListener listener);

}