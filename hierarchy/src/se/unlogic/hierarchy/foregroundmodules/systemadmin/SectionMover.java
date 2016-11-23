/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.systemadmin;

import se.unlogic.hierarchy.core.beans.SimpleSectionDescriptor;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.cache.SectionCache;
import se.unlogic.standardutils.collections.KeyAlreadyCachedException;
import se.unlogic.standardutils.collections.KeyNotCachedException;
import se.unlogic.standardutils.time.TimeUtils;

public class SectionMover extends SectionBackgroundTask {

	private SectionCache targetSectionCache;
	
	public SectionMover(SectionCache sourceSectionCache, SectionCache targetSectionCache, SimpleSectionDescriptor simpleSectionDescriptor, User user) {
		super(sourceSectionCache, simpleSectionDescriptor, user);
		this.targetSectionCache = targetSectionCache;
	}

	@Override
	public void run() {
		try{
			long starttime = System.currentTimeMillis();
			
			log.info("Section mover thread for section " + this.getSectionDescriptorBean() + " started...");
			
			sectionCache.unload(simpleSectionDescriptor);
			targetSectionCache.cache(simpleSectionDescriptor);
			
			log.info("Section " + this.getSectionDescriptorBean() + " moved by user " + this.getUser() + " using background thread in " + TimeUtils.millisecondsToString(System.currentTimeMillis() - starttime) + " ms");
		}catch(KeyNotCachedException e){
			log.info("Unable to move section " + this.getSectionDescriptorBean() + " requested by user " + this.getUser() + ", section not cached in source section");
		}catch (KeyAlreadyCachedException e) {
			log.info("Unable to move section " + this.getSectionDescriptorBean() + " requested by user " + this.getUser() + ", section already cached in target section");
		}
	}
}
