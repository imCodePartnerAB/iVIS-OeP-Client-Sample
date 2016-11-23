/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.interfaces;

import java.util.List;

import se.unlogic.hierarchy.core.enums.MenuItemType;
import se.unlogic.hierarchy.core.enums.URLType;

public interface BundleDescriptor extends AccessInterface {

	public List<? extends MenuItemDescriptor> getMenuItemDescriptors();

	public String getName();

	public String getDescription();

	public String getUrl();

	public URLType getUrlType();

	public MenuItemType getItemType();

	public String getUniqueID();
}
