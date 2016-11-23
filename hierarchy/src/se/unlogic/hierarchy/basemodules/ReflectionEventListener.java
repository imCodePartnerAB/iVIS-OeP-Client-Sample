package se.unlogic.hierarchy.basemodules;

import java.io.Serializable;
import java.lang.reflect.Method;

import se.unlogic.hierarchy.core.enums.EventSource;
import se.unlogic.hierarchy.core.interfaces.EventListener;

public class ReflectionEventListener<T extends Serializable> implements EventListener<T> {

	protected final Class<?> channel;
	protected final Class<T> eventType;
	protected final Object target;
	protected final Method method;
	protected final int priority;

	public ReflectionEventListener(Class<?> channel, Class<T> eventType, Object target, Method method, int priority) {

		super();
		this.channel = channel;
		this.eventType = eventType;
		this.target = target;
		this.method = method;
		this.priority = priority;
	}

	@Override
	public void processEvent(T event, EventSource eventSource) {

		try {
			method.invoke(target, event, eventSource);

		} catch (Exception e) {

			if (e instanceof RuntimeException) {

				throw (RuntimeException) e;
			}

			throw new RuntimeException(e);
		}
	}

	public Class<?> getChannel() {

		return channel;
	}

	public Class<T> getEventType() {

		return eventType;
	}

	@SuppressWarnings("rawtypes")
	public Class getRawEventType() {

		return eventType;
	}

	@Override
	public int getPriority() {

		return priority;
	}
}
