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
import se.unlogic.hierarchy.core.interfaces.RootSectionInterface;
import se.unlogic.standardutils.time.TimeUtils;

public class RootSectionUpdater extends Thread {

	protected Logger log = Logger.getLogger(this.getClass());
	private RootSectionInterface rootSectionInterface;
	private SimpleSectionDescriptor simpleSectionDescriptor;
	
	public RootSectionUpdater(RootSectionInterface rootSectionInterface, SimpleSectionDescriptor simpleSectionDescriptor) {
		this.setDaemon(true);
		this.rootSectionInterface = rootSectionInterface;
		this.simpleSectionDescriptor = simpleSectionDescriptor;
	}

	@Override
	public void run() {
		long starttime = System.currentTimeMillis();
		
		log.info("Root section update thread started...");
		
		rootSectionInterface.update(simpleSectionDescriptor);
		
		log.info("Root section updated after waiting " + TimeUtils.millisecondsToString(System.currentTimeMillis() - starttime) + " ms");
	}
}
