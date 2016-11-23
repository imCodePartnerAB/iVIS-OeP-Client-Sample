/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.interfaces;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.unlogic.standardutils.xml.Elementable;

public interface MutableSettingHandler extends Serializable, Elementable, SettingHandler{

	/**
	 * This method is used to set or update settings in the MutableSettingHandler. The {@link Object#toString()} method will be used to retrieve the value of the
	 * setting since the MutableSettingHandler is built on Strings.
	 * 
	 * @param id
	 *            the id of the setting to be set or updated
	 * @param value
	 *            the new value of the setting
	 */
	public void setSetting(String id, Object value);

	/**
	 * This method is used to set or update settings in the MutableSettingHandler. The {@link Object#toString()} method will be used to retrieve the value of the
	 * setting since the MutableSettingHandler is built on Strings.
	 * 
	 * @param id
	 *            the id of the setting to be set or updated
	 * @param value
	 *            the new value of the setting
	 */
	public void setSetting(String id, List<?> values);

	/**
	 * Removes a setting from the MutableSettingHandler
	 * 
	 * @param id
	 *            the id of the setting to be removed
	 */
	public void removeSetting(String id);

	/**
	 * Replaces all settings in the setting handler
	 * 
	 * @param settings
	 */
	public void setSettings(Map<String, List<String>> settings);

	/**
	 * Removes all modulesettings
	 */
	public void clear();

	/**
	 * Replaces the specified settings with new values
	 * 
	 * @param settingValues
	 */
	public abstract void replaceSettings(HashMap<String, List<String>> settingValues);
}
