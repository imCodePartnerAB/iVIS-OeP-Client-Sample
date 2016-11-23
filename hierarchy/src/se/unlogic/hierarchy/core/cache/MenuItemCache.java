/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.cache;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import se.unlogic.hierarchy.core.beans.Bundle;
import se.unlogic.hierarchy.core.beans.MenuItem;
import se.unlogic.hierarchy.core.beans.ModuleMenuItem;
import se.unlogic.hierarchy.core.beans.SectionMenu;
import se.unlogic.hierarchy.core.beans.SectionMenuItem;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.beans.VirtualMenuItem;
import se.unlogic.hierarchy.core.daos.factories.CoreDaoFactory;
import se.unlogic.hierarchy.core.daos.interfaces.MenuIndexDAO;
import se.unlogic.hierarchy.core.daos.interfaces.VirtualMenuItemDAO;
import se.unlogic.hierarchy.core.enums.MenuItemType;
import se.unlogic.hierarchy.core.enums.URLType;
import se.unlogic.hierarchy.core.interfaces.BundleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModule;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleCacheListener;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.MenuItemDescriptor;
import se.unlogic.hierarchy.core.interfaces.MenuSorter;
import se.unlogic.hierarchy.core.interfaces.SectionCacheListener;
import se.unlogic.hierarchy.core.interfaces.SectionDescriptor;
import se.unlogic.hierarchy.core.sections.Section;
import se.unlogic.hierarchy.core.utils.AccessUtils;
import se.unlogic.standardutils.collections.KeyAlreadyCachedException;
import se.unlogic.standardutils.collections.KeyNotCachedException;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.webutils.http.URIParser;

public class MenuItemCache implements ForegroundModuleCacheListener, SectionCacheListener {

	private static final MenuItemComparator COMPARATOR = new MenuItemComparator();
	
	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final Lock r = rwl.readLock();
	private final Lock w = rwl.writeLock();
	
	private final HashMap<ForegroundModuleDescriptor, List<ModuleMenuItem>> moduleMenuItemMap = new HashMap<ForegroundModuleDescriptor, List<ModuleMenuItem>>();
	private final HashMap<ForegroundModuleDescriptor, List<Bundle>> bundleMenuItemMap = new HashMap<ForegroundModuleDescriptor, List<Bundle>>();
	private final HashMap<SectionDescriptor, SectionMenuItem> sectionMenuItemMap = new HashMap<SectionDescriptor, SectionMenuItem>();
	private final ArrayList<VirtualMenuItem> virtualMenuItems = new ArrayList<VirtualMenuItem>();

	private final TreeSet<MenuItem> menuItemSet = new TreeSet<MenuItem>(COMPARATOR);
	private final MenuIndexDAO menuIndexDAO;
	private final VirtualMenuItemDAO virtualMenuItemDAO;
	private final Logger log = Logger.getLogger(this.getClass());

	private SectionDescriptor sectionDescriptor;	
	
	public MenuItemCache(CoreDaoFactory coreDaoFactory, SectionDescriptor sectionDesciptor) {
		this.menuIndexDAO = coreDaoFactory.getMenuIndexDAO();
		this.virtualMenuItemDAO = coreDaoFactory.getVirtualMenuItemDAO();
		this.sectionDescriptor = sectionDesciptor;

		try {
			this.cacheVirtualMenuItems();
		} catch (SQLException e) {
			log.error("Error caching virtual menuitems for section " + sectionDesciptor);
		}
	}

	public void setSectionDescriptor(SectionDescriptor sectionDesciptor) {
		try {
			w.lock();
			this.sectionDescriptor = sectionDesciptor;
		} finally {
			w.unlock();
		}
	}

	public void cacheVirtualMenuItems() throws SQLException {

		try {
			w.lock();
			// Remove all old virtual menuitems from cache
			if (!this.virtualMenuItems.isEmpty()) {
				this.menuItemSet.removeAll(this.virtualMenuItems);
				this.virtualMenuItems.clear();
			}

			// Read virtual menuitems from database
			ArrayList<VirtualMenuItem> virtualMenuItems = this.virtualMenuItemDAO.getMenuItemsInSection(sectionDescriptor.getSectionID());

			if (virtualMenuItems != null) {
				for (VirtualMenuItem virtualMenuItem : virtualMenuItems) {

					log.debug("Adding to virtual menuitem " + virtualMenuItem + " to menuitem cache for section " + sectionDescriptor);

					//TODO add handling to detect relative URL's
					virtualMenuItem.setUrlType(URLType.FULL);
					virtualMenuItem.setSectionID(sectionDescriptor.getSectionID());
					this.virtualMenuItems.add(virtualMenuItem);

				}

				if (!this.virtualMenuItems.isEmpty()) {
					menuIndexDAO.populateVirtualMenuIndex(this.virtualMenuItems);
					this.menuItemSet.addAll(this.virtualMenuItems);
				}
			}

		} finally {
			w.unlock();
		}
	}

	public void rebuildIndex() {
		w.lock();
		try {
			log.debug("Rebuilding menu index for menucache for section " + sectionDescriptor);
			this.menuItemSet.clear();

			for (List<? extends MenuItem> mList : this.moduleMenuItemMap.values()) {
				this.menuItemSet.addAll(mList);
			}

			this.menuItemSet.addAll(this.sectionMenuItemMap.values());

			this.menuItemSet.addAll(this.virtualMenuItems);

			for (List<? extends MenuItem> mList : this.bundleMenuItemMap.values()) {
				this.menuItemSet.addAll(mList);
			}

		} finally {
			w.unlock();
		}
	}

	public void saveIndex() {
		w.lock();
		try {
			menuIndexDAO.updateMenuIndex(this.menuItemSet);
		} catch (SQLException e) {
			log.error("Error " + e + " saveing menuindex.");
		} finally {
			w.unlock();
		}
	}

	@Override
	public void moduleCached(ForegroundModuleDescriptor moduleDescriptor, ForegroundModule moduleInstance) {
		w.lock();
		try {
			if (moduleDescriptor.isVisibleInMenu()) {

				List<? extends MenuItemDescriptor> menuItems = moduleInstance.getVisibleMenuItems();

				if (menuItems != null) {

					List<ModuleMenuItem> moduleMenuItems = new ArrayList<ModuleMenuItem>();

					for (MenuItemDescriptor menuItemDescriptor : menuItems) {

						if (this.validate(menuItemDescriptor, moduleDescriptor, null)) {
							ModuleMenuItem m = new ModuleMenuItem(menuItemDescriptor, moduleDescriptor, false);
							moduleMenuItems.add(m);
						}
					}

					menuIndexDAO.populateModuleMenuIndex(moduleMenuItems);
					this.moduleMenuItemMap.put(moduleDescriptor, moduleMenuItems);
					this.menuItemSet.addAll(moduleMenuItems);
				}

				List<? extends BundleDescriptor> bundleDescriptors = moduleInstance.getVisibleBundles();

				if (bundleDescriptors != null) {

					List<Bundle> bundles = new ArrayList<Bundle>();

					for (BundleDescriptor bundleDescriptor : bundleDescriptors) {

						if (this.validate(bundleDescriptor, moduleDescriptor)) {
							Bundle b = new Bundle(bundleDescriptor, moduleDescriptor);
							bundles.add(b);
						}
					}

					menuIndexDAO.populateBundleMenuIndex(bundles);

					this.bundleMenuItemMap.put(moduleDescriptor, bundles);
					this.menuItemSet.addAll(bundles);
				}

			}
		} catch (SQLException e) {
			log.error("Error " + e + " getting menuIndex for menuitems from module " + moduleDescriptor);
		} finally {
			w.unlock();
		}
	}

	@Override
	public void moduleUnloaded(ForegroundModuleDescriptor moduleDescriptor, ForegroundModule moduleInstance) {
		w.lock();
		try {
			if (this.moduleMenuItemMap.containsKey(moduleDescriptor)) {
				this.menuItemSet.removeAll(this.moduleMenuItemMap.get(moduleDescriptor));
				this.moduleMenuItemMap.remove(moduleDescriptor);
			}

			if (this.bundleMenuItemMap.containsKey(moduleDescriptor)) {
				this.menuItemSet.removeAll(this.bundleMenuItemMap.get(moduleDescriptor));
				this.bundleMenuItemMap.remove(moduleDescriptor);
			}
		} finally {
			w.unlock();
		}
	}

	@Override
	public void moduleUpdated(ForegroundModuleDescriptor moduleDescriptor, ForegroundModule moduleInstance) {
		w.lock();
		try {
			if (moduleDescriptor.isVisibleInMenu()) {
				List<? extends MenuItemDescriptor> menuItems = moduleInstance.getVisibleMenuItems();

				// Remove any previous menuitems
				List<ModuleMenuItem> oldMenuItems = this.moduleMenuItemMap.get(moduleDescriptor);

				if (oldMenuItems != null) {
					this.menuItemSet.removeAll(oldMenuItems);
				}

				// Check if there is any new menuitems to add
				if (menuItems == null || menuItems.isEmpty()) {
					this.moduleMenuItemMap.remove(moduleDescriptor);
				} else {
					ArrayList<ModuleMenuItem> moduleMenuItems = new ArrayList<ModuleMenuItem>();

					for (MenuItemDescriptor menuItemDescriptor : menuItems) {

						if (this.validate(menuItemDescriptor, moduleDescriptor, null)) {
							ModuleMenuItem m = new ModuleMenuItem(menuItemDescriptor, moduleDescriptor, false);
							moduleMenuItems.add(m);
						}
					}

					menuIndexDAO.populateModuleMenuIndex(moduleMenuItems);

					this.moduleMenuItemMap.put(moduleDescriptor, moduleMenuItems);
					this.menuItemSet.addAll(moduleMenuItems);
				}

				// Remove any previous bundles
				List<Bundle> oldBundles = this.bundleMenuItemMap.get(moduleDescriptor);

				if (oldBundles != null) {
					this.menuItemSet.removeAll(oldBundles);
				}

				// Get new bundles
				List<? extends BundleDescriptor> bundleDescriptors = moduleInstance.getVisibleBundles();

				if (bundleDescriptors == null || bundleDescriptors.isEmpty()) {
					this.bundleMenuItemMap.remove(moduleDescriptor);
				} else {
					List<Bundle> bundles = new ArrayList<Bundle>();

					for (BundleDescriptor bundleDescriptor : bundleDescriptors) {

						if (this.validate(bundleDescriptor, moduleDescriptor)) {
							Bundle b = new Bundle(bundleDescriptor, moduleDescriptor);
							bundles.add(b);
						}
					}

					menuIndexDAO.populateBundleMenuIndex(bundles);

					this.bundleMenuItemMap.put(moduleDescriptor, bundles);
					this.menuItemSet.addAll(bundles);
				}

			} else {
				if (this.moduleMenuItemMap.containsKey(moduleDescriptor)) {
					this.menuItemSet.removeAll(this.moduleMenuItemMap.get(moduleDescriptor));
					this.moduleMenuItemMap.remove(moduleDescriptor);
				}

				if (this.bundleMenuItemMap.containsKey(moduleDescriptor)) {
					this.menuItemSet.removeAll(this.bundleMenuItemMap.get(moduleDescriptor));
					this.bundleMenuItemMap.remove(moduleDescriptor);
				}
			}
		} catch (SQLException e) {
			log.error("Error " + e + " getting menuIndex for menuitems from module " + moduleDescriptor);
		} finally {
			w.unlock();
		}
	}

	private boolean validate(BundleDescriptor bundleDescriptor, ForegroundModuleDescriptor moduleDescriptor) {

		if (StringUtils.isEmpty(bundleDescriptor.getName())) {

			log.warn("Received bundle descriptor with no name set from module " + moduleDescriptor + ", ignoring bundle.");
			return false;

		} else if (StringUtils.isEmpty(bundleDescriptor.getDescription())) {

			log.warn("Received bundle descriptor with no description set from module " + moduleDescriptor + ", ignoring bundle.");
			return false;

		} else if (StringUtils.isEmpty(bundleDescriptor.getUniqueID())) {

			log.warn("Received bundle descriptor with no unique ID set from module " + moduleDescriptor + ", ignoring bundle.");
			return false;

		} else if (bundleDescriptor.getItemType() == null) {

			log.warn("Received bundle descriptor with no item type set from module " + moduleDescriptor + ", ignoring bundle.");
			return false;

		} else if (((bundleDescriptor.getItemType() != MenuItemType.BLANK) && (bundleDescriptor.getItemType() != MenuItemType.TITLE)) && StringUtils.isEmpty(bundleDescriptor.getUrl())) {

			log.warn("Received bundle descriptor with item type " + bundleDescriptor.getItemType() + " but no url set from module " + moduleDescriptor + ", ignoring bundle.");
			return false;
		}

		if(bundleDescriptor.getMenuItemDescriptors() != null){

			for (MenuItemDescriptor menuItemDescriptor : bundleDescriptor.getMenuItemDescriptors()) {

				if (!this.validate(menuItemDescriptor, moduleDescriptor, bundleDescriptor)) {
					return false;
				}
			}
		}

		return true;
	}

	private boolean validate(MenuItemDescriptor menuItemDescriptor, ForegroundModuleDescriptor moduleDescriptor, BundleDescriptor bundleDescriptor) {

		String ignoreString = "menuitem";

		if(moduleDescriptor.getModuleID() == null && menuItemDescriptor.getModuleID() == null){

			log.warn("Received menuitem descriptor with no moduleID set from virtual module " + moduleDescriptor + ", ignoring " + ignoreString + ".");
			return false;
		}

		String bundleString = "";

		if (bundleDescriptor != null) {
			bundleString = " in bundle " + bundleDescriptor.getName();
			ignoreString = "bundle";
		}

		if (StringUtils.isEmpty(menuItemDescriptor.getName())) {

			log.warn("Received menuitem descriptor with no name set from module " + moduleDescriptor + bundleString + ", ignoring " + ignoreString + ".");
			return false;

		} else if (StringUtils.isEmpty(menuItemDescriptor.getDescription())) {

			log.warn("Received menuitem descriptor with no description set from module " + moduleDescriptor + bundleString + ", ignoring " + ignoreString + ".");
			return false;

		} else if (bundleDescriptor == null && StringUtils.isEmpty(menuItemDescriptor.getUniqueID())) {

			log.warn("Received menuitem descriptor with no unique ID set from module " + moduleDescriptor + bundleString + ", ignoring " + ignoreString + ".");
			return false;

		} else if (menuItemDescriptor.getItemType() == null) {

			log.warn("Received menuitem descriptor with no item type set from module " + moduleDescriptor + bundleString + ", ignoring " + ignoreString + ".");
			return false;

		} else if (((menuItemDescriptor.getItemType() != MenuItemType.BLANK) && (menuItemDescriptor.getItemType() != MenuItemType.TITLE)) && StringUtils.isEmpty(menuItemDescriptor.getUrl())) {

			log.warn("Received menuitem descriptor with item type " + menuItemDescriptor.getItemType() + " but no url set from module " + moduleDescriptor + bundleString + ", ignoring " + ignoreString + ".");
			return false;
		}

		return true;
	}

	public void clearModules() {
		w.lock();
		try {
			log.debug("Removing all module menuitems from menuitem cache for section " + this.sectionDescriptor);
			if (this.moduleMenuItemMap.size() > 0) {
				for (List<? extends MenuItem> menuItemList : this.moduleMenuItemMap.values()) {
					this.menuItemSet.removeAll(menuItemList);
				}

				this.moduleMenuItemMap.clear();
			}
		} finally {
			w.unlock();
		}
	}

	public int size() {
		return moduleMenuItemMap.size();
	}

	@Override
	public String toString() {
		return moduleMenuItemMap.toString();
	}

	@SuppressWarnings("unchecked")
	public HashMap<ForegroundModuleDescriptor, ArrayList<MenuItem>> getModuleMenuItemMap() {
		r.lock();
		try {
			return (HashMap<ForegroundModuleDescriptor, ArrayList<MenuItem>>) moduleMenuItemMap.clone();
		} finally {
			r.unlock();
		}
	}

	@SuppressWarnings("unchecked")
	public TreeSet<MenuItem> getMenuItemSet() {
		r.lock();
		try {
			return (TreeSet<MenuItem>) menuItemSet.clone();
		} finally {
			r.unlock();
		}
	}

	@Override
	public void sectionCached(SectionDescriptor sectionDescriptor, Section sectionInstance) throws KeyAlreadyCachedException {
		w.lock();
		try {
			if (sectionDescriptor.isVisibleInMenu()) {
				log.debug("Adding to section " + sectionDescriptor + " to menuitem cache for section " + this.sectionDescriptor);

				SectionMenuItem menuItem = this.createMenuItem(sectionDescriptor);

				menuIndexDAO.populateSectionMenuIndex(menuItem);

				this.sectionMenuItemMap.put(sectionDescriptor, menuItem);
				this.menuItemSet.add(menuItem);

				log.debug("Menuitem " + menuItem + " for section " + sectionDescriptor + " added to menuitem cache for section " + this.sectionDescriptor);
			}
		} catch (SQLException e) {
			log.error("Error " + e + " getting menuIndex for menuitem from section " + sectionDescriptor);
		} finally {
			w.unlock();
		}
	}

	private SectionMenuItem createMenuItem(SectionDescriptor sectionDescriptor) {

		SectionMenuItem menuItem = new SectionMenuItem();

		menuItem.setAdminAccess(sectionDescriptor.allowsAdminAccess());
		menuItem.setUserAccess(sectionDescriptor.allowsUserAccess());
		menuItem.setAnonymousAccess(sectionDescriptor.allowsAnonymousAccess());
		menuItem.setDescription(sectionDescriptor.getDescription());
		menuItem.setName(sectionDescriptor.getName());
		menuItem.setUrl(sectionDescriptor.getFullAlias());
		menuItem.setUrlType(URLType.RELATIVE_FROM_CONTEXTPATH);
		menuItem.setSectionID(this.sectionDescriptor.getSectionID());
		menuItem.setSubSectionID(sectionDescriptor.getSectionID());
		menuItem.setAllowedGroupIDs(sectionDescriptor.getAllowedGroupIDs());
		menuItem.setAllowedUserIDs(sectionDescriptor.getAllowedUserIDs());

		return menuItem;
	}

	@Override
	public void sectionUnloaded(SectionDescriptor sectionDescriptor, Section sectionInstance) throws KeyNotCachedException {
		w.lock();
		try {
			if (this.sectionMenuItemMap.containsKey(sectionDescriptor)) {
				log.debug("Removing " + sectionDescriptor + " from menuitem cache for section " + this.sectionDescriptor);
				this.menuItemSet.remove(this.sectionMenuItemMap.get(sectionDescriptor));
				this.sectionMenuItemMap.remove(sectionDescriptor);
			}

		} finally {
			w.unlock();
		}
	}

	@Override
	public void sectionUpdated(SectionDescriptor sectionDescriptor, Section sectionInstance) throws KeyNotCachedException {
		w.lock();
		try {
			if (sectionDescriptor.isVisibleInMenu()) {
				log.debug("Adding to section " + sectionDescriptor + " to menuitem cache for section " + this.sectionDescriptor);
				SectionMenuItem menuItem = this.createMenuItem(sectionDescriptor);

				// Remove any previous menuitems
				SectionMenuItem oldMenuItem = this.sectionMenuItemMap.get(sectionDescriptor);

				if (oldMenuItem != null) {
					this.menuItemSet.remove(oldMenuItem);
				}

				menuIndexDAO.populateSectionMenuIndex(menuItem);
				this.sectionMenuItemMap.put(sectionDescriptor, menuItem);
				this.menuItemSet.add(menuItem);

				log.debug("Menuitem " + menuItem + " for section " + sectionDescriptor + " added to menuitem cache for section " + this.sectionDescriptor);

			} else {
				if (this.sectionMenuItemMap.containsKey(sectionDescriptor)) {
					this.menuItemSet.remove(this.sectionMenuItemMap.get(sectionDescriptor));
					this.sectionMenuItemMap.remove(sectionDescriptor);
				}
			}
		} catch (SQLException e) {
			log.error("Error " + e + " getting menuIndex for menuitem from section " + sectionDescriptor + " in section " + this.sectionDescriptor);
		} finally {
			w.unlock();
		}
	}

	public SectionMenu getUserMenu(User user, SectionMenu subMenu, URIParser uriParser) {

		try {
			r.lock();

			ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>(this.menuItemSet.size());

			for (MenuItem menuItem : this.menuItemSet) {

				if (AccessUtils.checkAccess(user, menuItem)) {

					if(subMenu != null && menuItem instanceof SectionMenuItem && ((SectionMenuItem)menuItem).getSubSectionID() == subMenu.getSectionID()){

						menuItems.add(((SectionMenuItem)menuItem).clone(subMenu,true));
						subMenu = null;
						continue;
					}

					if(isSelected(menuItem,uriParser)){

						menuItems.add(menuItem.getSelectedClone());

					}else{

						menuItems.add(menuItem);
					}


					if (menuItem instanceof Bundle) {

						Bundle bundle = (Bundle) menuItem;

						if (bundle.getModuleMenuItems() != null) {

							for (ModuleMenuItem moduleMenuItem : bundle.getModuleMenuItems()) {

								if (AccessUtils.checkAccess(user, moduleMenuItem)) {

									if(isSelected(moduleMenuItem,uriParser)){

										menuItems.add(moduleMenuItem.getSelectedClone());

									}else{

										menuItems.add(moduleMenuItem);
									}
								}
							}
						}
					}
				}
			}

			return new SectionMenu(menuItems, this.sectionDescriptor);
		} finally {
			r.unlock();
		}
	}

	private boolean isSelected(MenuItem menuItem, URIParser uriParser) {

		if(uriParser == null){

			return false;
		}

		if(menuItem.getUrl() != null){

			if(menuItem.getUrlType() == URLType.FULL && (uriParser.getRequestURL().equals(menuItem.getUrl()) || uriParser.getRequestURL().startsWith(menuItem.getUrl() + "/"))){

				return true;

			}else if(menuItem.getUrlType() == URLType.RELATIVE_FROM_CONTEXTPATH && (uriParser.getFormattedURI().equals(menuItem.getUrl()) || uriParser.getFormattedURI().startsWith(menuItem.getUrl() + "/"))){

				return true;
			}
		}

		return false;
	}

	public void sortMenu(MenuSorter menuSorter){

		w.lock();
		try {

			menuSorter.sort(new ArrayList<MenuItem>(menuItemSet));

			rebuildIndex();
			saveIndex();

		} finally {
			w.unlock();
		}
	}

}
