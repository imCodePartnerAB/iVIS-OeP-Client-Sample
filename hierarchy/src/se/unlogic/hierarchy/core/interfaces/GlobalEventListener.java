package se.unlogic.hierarchy.core.interfaces;

import java.io.Serializable;

import se.unlogic.hierarchy.core.enums.EventSource;
import se.unlogic.hierarchy.core.enums.EventTarget;


public interface GlobalEventListener extends Prioritized{

	public void processEvent(Class<?> key, Serializable event, EventTarget eventTarget, EventSource eventSource);
}
