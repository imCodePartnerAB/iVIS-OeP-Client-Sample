package se.unlogic.hierarchy.core.interfaces;

import se.unlogic.hierarchy.core.enums.EventSource;


public interface EventListener<EventType> extends Prioritized{

	public void processEvent(EventType event, EventSource eventSource);
}
