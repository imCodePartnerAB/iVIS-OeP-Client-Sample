package se.unlogic.hierarchy.core.interfaces;

import java.io.Serializable;

import se.unlogic.hierarchy.core.enums.EventSource;
import se.unlogic.hierarchy.core.enums.EventTarget;


public interface EventHandler {

	public <E extends Serializable> void sendEvent(Class<?> channel, E event, EventTarget eventTarget);
	
	public <E extends Serializable> void sendEvent(Class<?> channel, E event, Object sender, EventTarget eventTarget);
	
	public <E extends Serializable> void sendEvent(Class<?> channel, E event, Object sender, EventTarget eventTarget, EventSource eventSource);

	/**
	 * Listeners added using this method are treated as locally scope and will only receive events with a local target scope.
	 * 
	 * @param key
	 * @param eventType
	 * @param listener
	 * @return
	 */
	public <L extends Serializable, E extends L> boolean addEventListener(Class<?> channel, Class<E> eventType, EventListener<L> listener);

	/**
	 * Listeners added using this method are treated as locally scope and will only receive events with a local target scope.
	 * 
	 * @param eventType
	 * @param listener
	 * @param keys
	 */
	public <L extends Serializable, E extends L> void addEventListener(Class<E> eventType, EventListener<L> listener, Class<?>... channels);

	public <L extends Serializable, E extends L> boolean removeEventListener(Class<?> channel, Class<E> eventType, EventListener<L> listener);

	public <L extends Serializable, E extends L> void removeEventListener(Class<E> eventType, EventListener<L> listener, Class<?>... channels);

	public void removeEventListener(EventListener<?> listener);
	
	/**
	 * Listeners added using this method will receive all events sent no matter, channel, event type or scope.<br>
	 * <br>
	 * This method is mainly intended for listeners that forward events between OpenHierarchy installations. 
	 * 
	 * @param listener
	 * @return
	 */
	public boolean addGlobalEventListener(GlobalEventListener listener);
	
	public boolean removeGlobalEventListener(GlobalEventListener listener);
}