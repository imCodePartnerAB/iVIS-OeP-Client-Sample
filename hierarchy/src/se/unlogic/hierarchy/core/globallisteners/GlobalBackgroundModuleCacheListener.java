/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.globallisteners;

import java.util.concurrent.CopyOnWriteArrayList;

import se.unlogic.hierarchy.core.interfaces.BackgroundModule;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleCacheListener;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleDescriptor;
import se.unlogic.standardutils.collections.KeyAlreadyCachedException;
import se.unlogic.standardutils.collections.KeyNotCachedException;


public class GlobalBackgroundModuleCacheListener implements BackgroundModuleCacheListener {

	private CopyOnWriteArrayList<BackgroundModuleCacheListener> cacheListeners = new CopyOnWriteArrayList<BackgroundModuleCacheListener>();

	@Override
	public void moduleCached(BackgroundModuleDescriptor moduleDescriptor, BackgroundModule moduleInstance) throws KeyAlreadyCachedException {

		for(BackgroundModuleCacheListener cacheListener : cacheListeners){
			
			cacheListener.moduleCached(moduleDescriptor, moduleInstance);
		}
	}

	@Override
	public void moduleUpdated(BackgroundModuleDescriptor moduleDescriptor, BackgroundModule moduleInstance) throws KeyNotCachedException {

		for(BackgroundModuleCacheListener cacheListener : cacheListeners){
			
			cacheListener.moduleUpdated(moduleDescriptor, moduleInstance);
		}
	}	
	
	@Override
	public void moduleUnloaded(BackgroundModuleDescriptor moduleDescriptor, BackgroundModule moduleInstance) throws KeyNotCachedException {

		for(BackgroundModuleCacheListener cacheListener : cacheListeners){
			
			cacheListener.moduleUnloaded(moduleDescriptor, moduleInstance);
		}
	}

	public boolean add(BackgroundModuleCacheListener listener) {

		return cacheListeners.add(listener);
	}

	public boolean remove(BackgroundModuleCacheListener listener) {

		return cacheListeners.remove(listener);
	}

	public void clear() {

		cacheListeners.clear();
	}
}
