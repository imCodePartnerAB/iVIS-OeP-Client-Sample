/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.enums;

public enum AliasType {
	ALWAYS,
	WHEN_REQUEST_STARTS_WITH,
	WHEN_REQUEST_ENDS_WITH,
	WHEN_REQUEST_CONTAINS,
	WHEN_REQUEST_EQUALS,
	REGEXP;
}
