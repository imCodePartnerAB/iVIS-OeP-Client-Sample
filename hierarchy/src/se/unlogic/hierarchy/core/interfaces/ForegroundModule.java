/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.interfaces;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.webutils.http.URIParser;


public interface ForegroundModule extends SectionModule<ForegroundModuleDescriptor> {

	ForegroundModuleResponse processRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable;

	List<? extends MenuItemDescriptor> getVisibleMenuItems();
	List<? extends MenuItemDescriptor> getAllMenuItems();
	List<? extends BundleDescriptor> getVisibleBundles();
	List<? extends BundleDescriptor> getAllBundles();

}
