/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.unlogic.hierarchy.core.beans.SimpleBundleDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.SimpleMenuItemDescriptor;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.MenuItemType;
import se.unlogic.hierarchy.core.enums.URLType;
import se.unlogic.hierarchy.core.interfaces.MenuItemDescriptor;
import se.unlogic.hierarchy.foregroundmodules.SimpleForegroundModule;
import se.unlogic.webutils.http.URIParser;

public class MenuItemTestModule extends SimpleForegroundModule {

	@Override
	public SimpleForegroundModuleResponse processRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("<div class=\"contentitem\">");
		stringBuilder.append("<h1>MenuItem test</h1>");

		stringBuilder.append("</div>");

		return new SimpleForegroundModuleResponse(stringBuilder.toString(), getDefaultBreadcrumb());
	}

	@Override
	public List<SimpleBundleDescriptor> getVisibleBundles() {

		// create a SimpleBundleDescriptor. This is returned by each module
		SimpleBundleDescriptor simpleBundleDescriptor = new SimpleBundleDescriptor();
		simpleBundleDescriptor.setName("Test bundle 1");
		simpleBundleDescriptor.setDescription("This is Bundle1");
		simpleBundleDescriptor.setItemType(MenuItemType.TITLE);
		simpleBundleDescriptor.setUniqueID("foo-bundle");
		simpleBundleDescriptor.setAccess(this.moduleDescriptor);

		ArrayList<MenuItemDescriptor> menuItemDescriptors = new ArrayList<MenuItemDescriptor>();

		// create MenuItems to store in bundle.
		for (int i = 0; i < 5; i++) {
			SimpleMenuItemDescriptor menuItem = new SimpleMenuItemDescriptor();
			menuItem.setName("MenuItem: " + i + " in Bundle1");
			menuItem.setDescription("This is MenuItem1");
			menuItem.setItemType(MenuItemType.MENUITEM);
			menuItem.setUrlType(URLType.RELATIVE_FROM_CONTEXTPATH);
			menuItem.setUrl(this.getFullAlias());
			menuItem.setAccess(this.moduleDescriptor);

			menuItemDescriptors.add(menuItem);
		}

		simpleBundleDescriptor.setMenuItemDescriptors(menuItemDescriptors);

		return Collections.singletonList(simpleBundleDescriptor);
	}

	@Override
	public List<? extends MenuItemDescriptor> getVisibleMenuItems() {

		// create a SimpleMenuItemDescriptor. This is returned by each module.
		SimpleMenuItemDescriptor menuItemDescriptor = new SimpleMenuItemDescriptor();
		menuItemDescriptor.setName("Test menuitem");
		menuItemDescriptor.setDescription("This is a test menuitem");
		menuItemDescriptor.setItemType(MenuItemType.MENUITEM);
		menuItemDescriptor.setUrlType(URLType.RELATIVE_FROM_CONTEXTPATH);
		menuItemDescriptor.setUrl(this.getFullAlias());
		menuItemDescriptor.setAccess(this.moduleDescriptor);
		menuItemDescriptor.setUniqueID("foo");

		return Collections.singletonList(menuItemDescriptor);
	}
}
