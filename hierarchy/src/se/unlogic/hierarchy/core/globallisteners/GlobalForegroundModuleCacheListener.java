/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.globallisteners;

import java.util.concurrent.CopyOnWriteArrayList;

import se.unlogic.hierarchy.core.interfaces.ForegroundModule;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleCacheListener;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;


public class GlobalForegroundModuleCacheListener implements ForegroundModuleCacheListener {

	private CopyOnWriteArrayList<ForegroundModuleCacheListener> cacheListeners = new CopyOnWriteArrayList<ForegroundModuleCacheListener>();

	@Override
	public void moduleCached(ForegroundModuleDescriptor moduleDescriptor, ForegroundModule moduleInstance)  {

		for(ForegroundModuleCacheListener cacheListener : cacheListeners){

			cacheListener.moduleCached(moduleDescriptor, moduleInstance);
		}
	}

	@Override
	public void moduleUpdated(ForegroundModuleDescriptor moduleDescriptor, ForegroundModule moduleInstance)  {

		for(ForegroundModuleCacheListener cacheListener : cacheListeners){

			cacheListener.moduleUpdated(moduleDescriptor, moduleInstance);
		}
	}

	@Override
	public void moduleUnloaded(ForegroundModuleDescriptor moduleDescriptor, ForegroundModule moduleInstance) {

		for(ForegroundModuleCacheListener cacheListener : cacheListeners){

			cacheListener.moduleUnloaded(moduleDescriptor, moduleInstance);
		}
	}

	public boolean add(ForegroundModuleCacheListener listener) {

		return cacheListeners.add(listener);
	}

	public boolean remove(ForegroundModuleCacheListener listener) {

		return cacheListeners.remove(listener);
	}

	public void clear() {

		cacheListeners.clear();
	}
}
