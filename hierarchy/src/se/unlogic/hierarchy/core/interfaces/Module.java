/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.interfaces;

import java.util.List;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.SettingDescriptor;

public interface Module<DescriptorType> {

	void update(DescriptorType descriptor, DataSource dataSource) throws Exception;
	void unload() throws Exception;
	List<SettingDescriptor> getSettings();
}
