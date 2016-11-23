/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.exceptions;

public class ResourceNotFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 104987109730650042L;
	private String classname;
	private String path;
	
	public ResourceNotFoundException(String classname, String path) {
		super();
		this.classname = classname;
		this.path = path;
	}

	public String getClassname() {
		return classname;
	}

	public String getPath() {
		return path;
	}

	@Override
	public String toString() {
		return "Resource with path " + path + " not found in classpath " + classname;
	}
}
