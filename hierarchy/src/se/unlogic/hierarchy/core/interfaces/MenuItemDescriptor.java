/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.interfaces;

import se.unlogic.hierarchy.core.enums.MenuItemType;
import se.unlogic.hierarchy.core.enums.URLType;

public interface MenuItemDescriptor extends AccessInterface {

	public String getName();

	public String getDescription();

	public String getUniqueID();

	public String getUrl();

	public URLType getUrlType();

	public MenuItemType getItemType();

	/**
	 * This is an optional method that allows a module to specify the moduleID of another module as the owner of it's menuitems. This can be useful when using virtual modules.
	 * 
	 * @return null or a valid moduleID
	 */
	public Integer getModuleID();
}
