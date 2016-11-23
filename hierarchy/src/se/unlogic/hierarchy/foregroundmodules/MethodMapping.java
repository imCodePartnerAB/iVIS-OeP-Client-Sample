/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules;

import java.lang.reflect.Method;

import se.unlogic.hierarchy.core.annotations.WebPublic;

public class MethodMapping {

	private final Method method;
	private final WebPublic annotation;

	public MethodMapping(Method method, WebPublic annotation) {

		super();
		this.method = method;
		this.annotation = annotation;
	}

	public Method getMethod() {

		return method;
	}

	public WebPublic getAnnotation() {

		return annotation;
	}
}
