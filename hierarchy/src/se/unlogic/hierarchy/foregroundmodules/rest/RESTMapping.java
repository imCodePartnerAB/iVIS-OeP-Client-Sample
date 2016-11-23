package se.unlogic.hierarchy.foregroundmodules.rest;

import java.lang.reflect.Method;

public class RESTMapping {

	private final Method method;
	private final RESTMethod annotation;
	private final URIComponentHandler componentHandler;
	private final int paramCount;

	public RESTMapping(Method method, RESTMethod annotation, URIComponentHandler componentHandler) {

		super();
		this.method = method;
		this.annotation = annotation;
		this.componentHandler = componentHandler;
		
		paramCount = method.getParameterTypes().length;
	}

	public Method getMethod() {

		return method;
	}

	public RESTMethod getAnnotation() {

		return annotation;
	}

	public URIComponentHandler getComponentHandler() {

		return componentHandler;
	}

	public int getParamCount() {

		return paramCount;
	}
}
