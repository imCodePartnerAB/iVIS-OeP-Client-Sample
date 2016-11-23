/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.globallisteners;

import java.util.concurrent.CopyOnWriteArrayList;

import se.unlogic.hierarchy.core.interfaces.SectionCacheListener;
import se.unlogic.hierarchy.core.interfaces.SectionDescriptor;
import se.unlogic.hierarchy.core.sections.Section;
import se.unlogic.standardutils.collections.KeyAlreadyCachedException;
import se.unlogic.standardutils.collections.KeyNotCachedException;


public class GlobalSectionCacheListener implements SectionCacheListener {

	private CopyOnWriteArrayList<SectionCacheListener> cacheListeners = new CopyOnWriteArrayList<SectionCacheListener>();

	@Override
	public void sectionCached(SectionDescriptor sectionDescriptor, Section sectionInstance) throws KeyAlreadyCachedException {

		for(SectionCacheListener cacheListener : cacheListeners){
			
			cacheListener.sectionCached(sectionDescriptor, sectionInstance);
		}
	}

	@Override
	public void sectionUpdated(SectionDescriptor sectionDescriptor, Section sectionInstance) throws KeyNotCachedException {

		for(SectionCacheListener cacheListener : cacheListeners){
			
			cacheListener.sectionUpdated(sectionDescriptor, sectionInstance);
		}
	}	
	
	@Override
	public void sectionUnloaded(SectionDescriptor sectionDescriptor, Section sectionInstance) throws KeyNotCachedException {

		for(SectionCacheListener cacheListener : cacheListeners){
			
			cacheListener.sectionUnloaded(sectionDescriptor, sectionInstance);
		}
	}

	public boolean add(SectionCacheListener listener) {

		return cacheListeners.add(listener);
	}

	public boolean remove(SectionCacheListener listener) {

		return cacheListeners.remove(listener);
	}

	public void clear() {

		cacheListeners.clear();
	}
}
