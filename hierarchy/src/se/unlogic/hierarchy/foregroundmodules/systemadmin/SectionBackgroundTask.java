/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.systemadmin;

import org.apache.log4j.Logger;

import se.unlogic.hierarchy.core.beans.SimpleSectionDescriptor;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.cache.SectionCache;

public abstract class SectionBackgroundTask extends Thread {

	protected Logger log = Logger.getLogger(this.getClass());
	protected SectionCache sectionCache;
	protected SimpleSectionDescriptor simpleSectionDescriptor;
	protected User user;

	public SectionBackgroundTask(SectionCache sectionCache,	SimpleSectionDescriptor simpleSectionDescriptor, User user) {
		super();
		this.setDaemon(true);
		this.sectionCache = sectionCache;
		this.simpleSectionDescriptor = simpleSectionDescriptor;
		this.user = user;
	}

	public SectionCache getSectionCache() {
		return sectionCache;
	}

	public SimpleSectionDescriptor getSectionDescriptorBean() {
		return simpleSectionDescriptor;
	}

	public User getUser() {
		return user;
	}
	
	@Override
	public abstract void run();
}
