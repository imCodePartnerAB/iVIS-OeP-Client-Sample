/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.backgroundmodules;

import java.util.List;

import se.unlogic.hierarchy.basemodules.BaseSectionModule;
import se.unlogic.hierarchy.core.beans.SettingDescriptor;
import se.unlogic.hierarchy.core.interfaces.BackgroundModule;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleDescriptor;


public abstract class SimpleBackgroundModule extends BaseSectionModule<BackgroundModuleDescriptor> implements BackgroundModule{

	@Override
	public List<SettingDescriptor> getSettings() {

		return null;
	}
}
