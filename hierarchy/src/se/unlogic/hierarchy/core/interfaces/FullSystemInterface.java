/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.interfaces;

import se.unlogic.hierarchy.core.globallisteners.GlobalBackgroundModuleCacheListener;
import se.unlogic.hierarchy.core.globallisteners.GlobalForegroundModuleCacheListener;
import se.unlogic.hierarchy.core.globallisteners.GlobalSectionCacheListener;
import se.unlogic.hierarchy.core.sections.Section;


public interface FullSystemInterface extends SystemInterface {

	public GlobalSectionCacheListener getGlobalSectionCacheListener();

	public GlobalForegroundModuleCacheListener getGlobalForegroundModuleCacheListener();

	public GlobalBackgroundModuleCacheListener getGlobalBackgroundModuleCacheListener();
	
	@Override
	public Section getSectionInterface(Integer sectionID);
	
	public void addSection(Section section);
	
	public void removeSection(Section section);	
}
