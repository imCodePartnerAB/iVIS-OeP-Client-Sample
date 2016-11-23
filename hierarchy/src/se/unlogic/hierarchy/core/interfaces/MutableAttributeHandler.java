/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.interfaces;


public interface MutableAttributeHandler extends AttributeHandler{

	/**
	 * This method is used to set or update an attribute in the AttributeHandler. The {@link Object#toString()} method will be used to retrieve the value of the
	 * setting since the MutableSettingHandler is built on Strings.
	 * 
	 * @param name
	 *            the name of the setting to be set or updated
	 * @param value
	 *            the new value of the setting
	 */
	public boolean setAttribute(String name, Object value);

	/**
	 * Removes a setting from the MutableSettingHandler
	 * 
	 * @param id
	 *            the id of the setting to be removed
	 */
	public void removeAttribute(String name);

	/**
	 * Removes all attributes
	 */
	public void clear();

	public int getMaxNameLength();

	public int getMaxValueLength();
}
