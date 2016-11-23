/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.test;

import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.CachedXSLTDescriptor;
import se.unlogic.hierarchy.foregroundmodules.SimpleForegroundModule;
import se.unlogic.standardutils.operation.ProgressMeter;
import se.unlogic.webutils.http.URIParser;

public class XSLReload extends SimpleForegroundModule {

	@Override
	public SimpleForegroundModuleResponse processRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		ProgressMeter meter = new ProgressMeter();
		meter.setStartTime();
		List<? extends Entry<CachedXSLTDescriptor, ? extends Exception>> errors = this.sectionInterface.getSystemInterface().getCoreXSLTCacheHandler().reloadStylesheets();

		int errorCount = 0;

		if(errors != null){
			errorCount = errors.size();
		}

		meter.setEndTime();
		log.info("XSL Stylesheet succesfully reloadded in " + meter.getTimeSpent() + "ms");
		return new SimpleForegroundModuleResponse("<div class=\"contentitem\"><h1> XSL stylesheets reloaded in " + meter.getTimeSpent() + " ms with " + errorCount + " errors</h1></div>",getDefaultBreadcrumb());
	}

}
