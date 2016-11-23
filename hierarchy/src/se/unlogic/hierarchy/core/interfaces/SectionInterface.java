/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.interfaces;

import se.unlogic.hierarchy.core.beans.Breadcrumb;
import se.unlogic.hierarchy.core.cache.BackgroundModuleCache;
import se.unlogic.hierarchy.core.cache.ForegroundModuleCache;
import se.unlogic.hierarchy.core.cache.ForegroundModuleXSLTCache;
import se.unlogic.hierarchy.core.cache.MenuItemCache;
import se.unlogic.hierarchy.core.cache.SectionCache;
import se.unlogic.hierarchy.core.enums.SectionStatus;

public interface SectionInterface {

	public ForegroundModuleCache getForegroundModuleCache();

	public BackgroundModuleCache getBackgroundModuleCache();

	public MenuItemCache getMenuCache();

	public ForegroundModuleXSLTCache getModuleXSLTCache();

	public SectionCache getSectionCache();

	public SystemInterface getSystemInterface();

	public SectionDescriptor getSectionDescriptor();

	public int getReadLockCount();

	public SectionInterface getParentSectionInterface();

	public Breadcrumb getBreadcrumb();

	public SectionStatus getSectionStatus();

	public abstract boolean removeModuleAccessDeniedHandler(ModuleAccessDeniedHandler handler);

	public abstract boolean addModuleAccessDeniedHandler(ModuleAccessDeniedHandler handler);

	public abstract boolean removeSectionAccessDeniedHandler(SectionAccessDeniedHandler handler);

	public abstract boolean addSectionAccessDeniedHandler(SectionAccessDeniedHandler handler);
}
