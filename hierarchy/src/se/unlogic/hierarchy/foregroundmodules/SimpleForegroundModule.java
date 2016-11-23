/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import se.unlogic.hierarchy.basemodules.BaseSectionModule;
import se.unlogic.hierarchy.core.beans.Breadcrumb;
import se.unlogic.hierarchy.core.beans.SettingDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleMenuItemDescriptor;
import se.unlogic.hierarchy.core.beans.ValueDescriptor;
import se.unlogic.hierarchy.core.enums.MenuItemType;
import se.unlogic.hierarchy.core.enums.URLType;
import se.unlogic.hierarchy.core.interfaces.BundleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModule;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.MenuItemDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.standardutils.enums.EnumUtils;

public abstract class SimpleForegroundModule extends BaseSectionModule<ForegroundModuleDescriptor> implements ForegroundModule {


	protected MenuItemType menuItemType = MenuItemType.MENUITEM;

	protected static final SettingDescriptor MENUITEM_TYPE_SETTING_DESCRIPTOR = SettingDescriptor.createDropDownSetting("menuItemType", "Menuitem type", "The type of menuitem this module should display itself as in the menu", true, "MENUITEM", new ValueDescriptor(MenuItemType.MENUITEM.toString(), MenuItemType.MENUITEM.toString()), new ValueDescriptor(MenuItemType.TITLE.toString(), MenuItemType.TITLE.toString()), new ValueDescriptor(MenuItemType.SECTION.toString(), MenuItemType.SECTION.toString()));

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);
		
		checkMenuSettings();
	}

	@Override
	public void update(ForegroundModuleDescriptor moduleDescriptor, DataSource dataSource) throws Exception {

		super.update(moduleDescriptor, dataSource);
		
		checkMenuSettings();
	}

	/**
	 * This method is called on {@link #init(ForegroundModuleDescriptor, SectionInterface, DataSource)} and when changes to the settings of this module are detected when the {@link #update(ForegroundModuleDescriptor, DataSource)} method is called.
	 * 
	 * @param mutableSettingHandler
	 */
	protected void checkMenuSettings() {

		String menuItemType = moduleDescriptor.getMutableSettingHandler().getString("menuItemType");

		if (menuItemType != null) {

			if (EnumUtils.isEnum(MenuItemType.class, menuItemType)) {

				this.menuItemType = MenuItemType.valueOf(menuItemType);

			} else {
				log.warn("Invalid setting value for setting \"menuItemType\" in module " + this.moduleDescriptor);
			}
		}
	}

	public Breadcrumb getDefaultBreadcrumb() {

		return new Breadcrumb(sectionInterface.getSectionDescriptor(), moduleDescriptor);
	}

	@Override
	public void unload() throws Exception {}

	@Override
	public List<? extends MenuItemDescriptor> getVisibleMenuItems() {

		if (this.moduleDescriptor.isVisibleInMenu()) {
			return this.getAllMenuItems();
		} else {
			return null;
		}
	}

	@Override
	public List<? extends MenuItemDescriptor> getAllMenuItems() {

		SimpleMenuItemDescriptor menuItemDescriptor = new SimpleMenuItemDescriptor();

		menuItemDescriptor.setName(this.moduleDescriptor.getName());
		menuItemDescriptor.setUrl(this.getFullAlias());
		menuItemDescriptor.setUrlType(URLType.RELATIVE_FROM_CONTEXTPATH);
		menuItemDescriptor.setUniqueID(this.moduleDescriptor.getModuleID().toString());
		menuItemDescriptor.setDescription(this.moduleDescriptor.getDescription());
		menuItemDescriptor.setItemType(menuItemType);
		menuItemDescriptor.setAccess(this.moduleDescriptor);

		return Collections.singletonList((MenuItemDescriptor) menuItemDescriptor);
	}	
	
	@Override
	public List<? extends BundleDescriptor> getAllBundles() {
		return this.getVisibleBundles();
	}

	@Override
	public List<? extends BundleDescriptor> getVisibleBundles() {
		return null;
	}

	protected String getModuleURI(HttpServletRequest req) {
		return req.getContextPath() + this.getFullAlias();
	}

	public String getFullAlias() {
		return this.sectionInterface.getSectionDescriptor().getFullAlias() + "/" + this.moduleDescriptor.getAlias();
	}

	@Override
	public List<SettingDescriptor> getSettings() {
		return Collections.singletonList(MENUITEM_TYPE_SETTING_DESCRIPTOR);
	}
}
