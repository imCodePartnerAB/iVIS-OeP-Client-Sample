/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.beans;

import java.util.regex.Pattern;

import se.unlogic.hierarchy.core.enums.AliasType;

public class AliasMapping {

	private final String alias;
	private final AliasType aliasType;
	private final boolean exclude;
	private final Pattern pattern;

	public AliasMapping(String alias, AliasType aliasType) {
		this(alias, aliasType, false);
	}
	
	public AliasMapping(String alias, AliasType aliasType, boolean exclude) {

		if(aliasType.equals(AliasType.REGEXP)) {
			this.pattern = Pattern.compile(alias);
		} else {
			this.pattern = null;
		}
		
		this.alias = alias;
		this.aliasType = aliasType;
		this.exclude = exclude;
	}

	public String getAlias() {

		return alias;
	}

	public AliasType getAliasType() {

		return aliasType;
	}

	public boolean isExclude() {

		return exclude;
	}
	
	public Pattern getPattern() {
		return this.pattern;
	}
}
