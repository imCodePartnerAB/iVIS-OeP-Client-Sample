/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.webutils.populators.annotated;


public class BeanRequestPopulationFailed extends RuntimeException {

	private static final long serialVersionUID = 1111119174088294859L;

	private final RequestMapping requestMapping;

	public BeanRequestPopulationFailed(RequestMapping requestMapping, Throwable throwable) {
		super(throwable);
		this.requestMapping = requestMapping;
	}

	public RequestMapping getRequestMapping() {
		return requestMapping;
	}
}
