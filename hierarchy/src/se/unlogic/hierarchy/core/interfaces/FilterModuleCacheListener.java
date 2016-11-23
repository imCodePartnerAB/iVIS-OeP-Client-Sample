/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.interfaces;

import se.unlogic.standardutils.collections.KeyAlreadyCachedException;
import se.unlogic.standardutils.collections.KeyNotCachedException;


public interface FilterModuleCacheListener {

	public void moduleCached(FilterModuleDescriptor moduleDescriptor, FilterModule moduleInstance) throws KeyAlreadyCachedException;
	public void moduleUpdated(FilterModuleDescriptor moduleDescriptor, FilterModule moduleInstance) throws KeyNotCachedException;
	public void moduleUnloaded(FilterModuleDescriptor moduleDescriptor, FilterModule moduleInstance) throws KeyNotCachedException;
}
