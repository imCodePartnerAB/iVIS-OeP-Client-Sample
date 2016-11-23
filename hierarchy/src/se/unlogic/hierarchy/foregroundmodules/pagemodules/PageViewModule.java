/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.pagemodules;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.beans.Breadcrumb;
import se.unlogic.hierarchy.core.beans.LinkTag;
import se.unlogic.hierarchy.core.beans.ScriptTag;
import se.unlogic.hierarchy.core.beans.SettingDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.SimpleMenuItemDescriptor;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.EventTarget;
import se.unlogic.hierarchy.core.enums.MenuItemType;
import se.unlogic.hierarchy.core.enums.URLType;
import se.unlogic.hierarchy.core.events.SearchableItemAddEvent;
import se.unlogic.hierarchy.core.events.SearchableItemDeleteEvent;
import se.unlogic.hierarchy.core.events.SearchableItemUpdateEvent;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.interfaces.BundleDescriptor;
import se.unlogic.hierarchy.core.interfaces.EventHandler;
import se.unlogic.hierarchy.core.interfaces.ForegroundModule;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.MenuItemDescriptor;
import se.unlogic.hierarchy.core.interfaces.Searchable;
import se.unlogic.hierarchy.core.interfaces.SearchableItem;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.interfaces.SystemInterface;
import se.unlogic.hierarchy.core.utils.AccessUtils;
import se.unlogic.hierarchy.core.utils.BaseFileAccessValidator;
import se.unlogic.hierarchy.core.utils.FCKConnector;
import se.unlogic.hierarchy.core.utils.ModuleUtils;
import se.unlogic.hierarchy.core.utils.SimpleFileAccessValidator;
import se.unlogic.hierarchy.foregroundmodules.pagemodules.daos.PageDAO;
import se.unlogic.hierarchy.foregroundmodules.pagemodules.daos.PageDAOFactory;
import se.unlogic.hierarchy.foregroundmodules.pagemodules.daos.annotated.AnnotatedPageDAOFactory;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.standardutils.xsl.XSLVariableReader;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.url.URLRewriter;

public class PageViewModule implements ForegroundModule, Searchable {

	protected Logger log = Logger.getLogger(this.getClass());
	private static final SettingDescriptor SETTINGDESCRIPTOR = SettingDescriptor.createTextFieldSetting("filestore", "Filestore path", "Path to the directory to be used as filestore", false, null, null);

	protected ConcurrentHashMap<String, Page> pageCache = new ConcurrentHashMap<String, Page>();

	private SystemInterface systemInterface;
	private SectionInterface sectionInterface;
	private EventHandler eventHandler;
	private ForegroundModuleDescriptor moduleDescriptor;
	private DataSource dataSource;
	private PageDAO pageDAO;

	private FCKConnector connector;

	private List<ScriptTag> scripts;
	private List<LinkTag> links;

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		this.moduleDescriptor = moduleDescriptor;
		this.dataSource = dataSource;

		createDAO();

		this.systemInterface = sectionInterface.getSystemInterface();
		this.sectionInterface = sectionInterface;
		this.eventHandler = sectionInterface.getSystemInterface().getEventHandler();

		this.connector = new FCKConnector(moduleDescriptor.getMutableSettingHandler().getString("filestore"));

		XSLVariableReader variableReader = ModuleUtils.getXSLVariableReader(moduleDescriptor, sectionInterface.getSystemInterface());

		if(variableReader != null){

			this.scripts = ModuleUtils.getScripts(variableReader, sectionInterface, "f", moduleDescriptor);
			this.links = ModuleUtils.getLinks(variableReader, sectionInterface, "f", moduleDescriptor);
		}

		this.cachePages();
	}

	@Override
	public void update(ForegroundModuleDescriptor moduleDescriptor, DataSource dataSource) throws Exception {

		this.moduleDescriptor = moduleDescriptor;

		if(this.dataSource != dataSource){

			this.dataSource = dataSource;

			createDAO();

			this.cachePages();
		}

		XSLVariableReader variableReader = ModuleUtils.getXSLVariableReader(moduleDescriptor, sectionInterface.getSystemInterface());

		if(variableReader != null){

			this.scripts = ModuleUtils.getScripts(variableReader, sectionInterface, "f", moduleDescriptor);
			this.links = ModuleUtils.getLinks(variableReader, sectionInterface, "f", moduleDescriptor);
		}

		this.connector = new FCKConnector(moduleDescriptor.getMutableSettingHandler().getString("filestore"));
	}

	private void createDAO() throws Exception {

		PageDAOFactory daoFactory = new AnnotatedPageDAOFactory();

		daoFactory.init(dataSource);

		this.pageDAO = daoFactory.getPageDAO();
	}

	protected synchronized void cachePages() throws SQLException {

		log.info("Caching pages in section " + this.sectionInterface.getSectionDescriptor());

		if(!this.pageCache.isEmpty()){

			this.pageCache.clear();
		}

		List<Page> pages = this.pageDAO.getEnabledPages(this.sectionInterface.getSectionDescriptor().getSectionID());

		if(pages != null){

			for(Page page : pages){

				log.debug("Caching page " + page);

				this.pageCache.put(page.getAlias(), page);
			}

			log.info( "Cached " + pages.size() + " pages in section " + this.sectionInterface.getSectionDescriptor());

		}else{

			log.info("Cached 0 pages in section " + this.sectionInterface.getSectionDescriptor());
		}
	}

	@Override
	public void unload() {}


	@Override
	public SimpleForegroundModuleResponse processRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		Page page = null;

		// Try to get the page if a valid pageID is supplied
		if (uriParser.size() > 1 && (page = this.pageCache.get(uriParser.get(1))) != null) {

			// Page found, check access
			if (AccessUtils.checkAccess(user, page)) {

				// Check if the user requested a file
				if (uriParser.size() > 2 && uriParser.get(2).equalsIgnoreCase("file")) {

					processFileRequest(page, req, res, user, uriParser);

					return null;

				} else {
					// User has access
					log.info("User " + user + " requested page " + page);

					Document doc = XMLUtils.createDomDocument();
					Element document = doc.createElement("document");
					doc.appendChild(document);

					// Add request info
					document.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser));

					// Add current module details
					document.appendChild(this.moduleDescriptor.toXML(doc));

					String text = page.getText();

					text = this.setAbsoluteFileUrls(page, uriParser);

					text = URLRewriter.setAbsoluteLinkUrls(text, req);

					// Set absolute paths and add the page
					document.appendChild(page.toXML(doc,text));

					// Add link to pageadmin if loaded and user has proper access
					ForegroundModuleDescriptor pageAdminDescriptor = PageAdminModule.getPageAdminModule(this.dataSource);

					SectionInterface pageAdminSectionInterface;

					if (pageAdminDescriptor != null && AccessUtils.checkRecursiveModuleAccess(user, pageAdminDescriptor, systemInterface) && (pageAdminSectionInterface = systemInterface.getSectionInterface(pageAdminDescriptor.getSectionID())) != null) {
						Element pageAdminModuleElement = doc.createElement("pageAdminModule");
						document.appendChild(pageAdminModuleElement);
						pageAdminModuleElement.appendChild(pageAdminDescriptor.toXML(doc));
						pageAdminModuleElement.appendChild(pageAdminSectionInterface.getSectionDescriptor().toXML(doc));
					}

					SimpleForegroundModuleResponse moduleResponse;

					if (page.hasBreadCrumb()) {
						moduleResponse = new SimpleForegroundModuleResponse(doc, page.getName(), new Breadcrumb(page.getName(), page.getDescription(), sectionInterface.getSectionDescriptor().getFullAlias() + "/" + this.moduleDescriptor.getAlias() + "/" + page.getAlias(), URLType.RELATIVE_FROM_CONTEXTPATH));
					} else {
						moduleResponse = new SimpleForegroundModuleResponse(doc, page.getName());
					}

					if(scripts != null){
						moduleResponse.addScripts(scripts);
					}

					if(links != null){
						moduleResponse.addLinks(links);
					}

					return moduleResponse;
				}

			} else {
				// Access denied
				// log.warn("Access denied for user " + user + " requesting page " + page + " from module " + this.moduleDescriptor + " in section " + this.sectionInterface.getSectionDescriptor() + " from adress " + req.getRemoteAddr());
				throw new AccessDeniedException("Access to page " + page + " denied");
			}
		} else {
			// Invalid pageID or page not found
			log.info("User " + user + " requested unknown page with alias " + uriParser.get(1) + " from module " + this.moduleDescriptor + " in section " + this.sectionInterface.getSectionDescriptor() + " from adress " + req.getRemoteAddr());
			throw new URINotFoundException(uriParser);
		}
	}

	private void processFileRequest(Page page, HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, AccessDeniedException {

		this.connector.processFileRequest(req, res, user, uriParser, moduleDescriptor, sectionInterface, 3, new SimpleFileAccessValidator(Page.RELATIVE_PATH_MARKER,page.getUnescapedText()));
	}


	@Override
	public List<MenuItemDescriptor> getAllMenuItems() {
		try {
			List<Page> pages = this.pageDAO.getPages(this.moduleDescriptor.getSectionID());

			if (pages != null) {
				ArrayList<MenuItemDescriptor> menuItems = new ArrayList<MenuItemDescriptor>();

				for (Page page : pages) {
					menuItems.add(createMenuItem(page));
				}

				return menuItems;
			}
		} catch (SQLException e) {
			log.error("Unable to get menuitems", e);
		}
		return null;
	}


	@Override
	public List<MenuItemDescriptor> getVisibleMenuItems() {

		if (!this.pageCache.isEmpty()) {

			ArrayList<MenuItemDescriptor> menuItems = new ArrayList<MenuItemDescriptor>();

			for (Page page : this.pageCache.values()) {

				if(page.isVisibleInMenu()){
					menuItems.add(createMenuItem(page));
				}
			}

			return menuItems;
		}

		return null;
	}

	private SimpleMenuItemDescriptor createMenuItem(Page page) {

		SimpleMenuItemDescriptor menuItem = new SimpleMenuItemDescriptor();

		menuItem.setUniqueID(page.getPageID().toString());
		menuItem.setItemType(MenuItemType.MENUITEM);
		menuItem.setName(page.getName());
		menuItem.setDescription(page.getDescription());
		menuItem.setAnonymousAccess(page.allowsAnonymousAccess());
		menuItem.setAdminAccess(page.allowsAdminAccess());
		menuItem.setUserAccess(page.allowsUserAccess());
		menuItem.setAllowedGroupIDs(page.getAllowedGroupIDs());
		menuItem.setAllowedUserIDs(page.getAllowedUserIDs());
		menuItem.setUrl(this.sectionInterface.getSectionDescriptor().getFullAlias() + "/" + this.moduleDescriptor.getAlias() + "/" + page.getAlias());
		menuItem.setUrlType(URLType.RELATIVE_FROM_CONTEXTPATH);

		if(this.moduleDescriptor.getModuleID() == null){
			menuItem.setModuleID(this.moduleDescriptor.getMutableSettingHandler().getInt(PageAdminModule.class.toString()));
		}

		return menuItem;
	}

	private String getAbsoluteFileURL(Page page, URIParser uriParser) {
		return uriParser.getCurrentURI(true) + "/" + this.moduleDescriptor.getAlias() + "/" + page.getAlias() + "/file";
	}

	public String setAbsoluteFileUrls(Page page, URIParser uriParser) {

		String text = page.getText();

		String absoluteFileURL = this.getAbsoluteFileURL(page, uriParser);

		for(String attribute : BaseFileAccessValidator.TAG_ATTRIBUTES){

			text = text.replace(attribute + "=\"" + Page.RELATIVE_PATH_MARKER, attribute + "=\"" + absoluteFileURL);
			text = text.replace(attribute + "='" + Page.RELATIVE_PATH_MARKER, attribute + "='" + absoluteFileURL);
		}


		return text;
	}


	@Override
	public List<SettingDescriptor> getSettings() {
		return Collections.singletonList(SETTINGDESCRIPTOR);
	}


	@Override
	public List<BundleDescriptor> getVisibleBundles() {
		return null;
	}


	@Override
	public List<BundleDescriptor> getAllBundles() {
		return null;
	}

	/**
	 * Adds a {@link Page} to the modules page cache.
	 * 
	 * @param page
	 */
	protected synchronized void addPage(Page page){

		if(this.pageCache.values().contains(page)){

			log.warn("Page " + page + " is already cached in section " + this.sectionInterface.getSectionDescriptor() + ", doing an update instead");

			this.updatePage(page);

			return;

		}

		log.debug("Adding page " + page + " to page cache in section " + this.sectionInterface.getSectionDescriptor());

		if(this.pageCache.get(page.getAlias()) != null){

			log.warn("A page with alias " + page.getAlias() + " is already cached and will be overwritten by page " + page + " in section " + this.sectionInterface.getSectionDescriptor());
		}

		this.pageCache.put(page.getAlias(), page);

		this.eventHandler.sendEvent(SearchableItem.class, new SearchableItemAddEvent(page, moduleDescriptor), EventTarget.ALL);
	}

	/**
	 * Updates a {@link Page} page in the modules page cache.
	 * 
	 * @param page
	 */
	protected synchronized void updatePage(Page page){

		if(this.pageCache.values().contains(page)){

			for(Entry<String,Page> entry : this.pageCache.entrySet()){

				if(entry.getValue().equals(page)){

					Page oldPage = entry.getValue();

					if(entry.getValue().getAlias().equals(page.getAlias())){

						entry.setValue(page);

					}else{

						this.pageCache.remove(entry.getKey());
						this.pageCache.put(page.getAlias(), page);
					}

					log.debug("Updated page " + oldPage + " in page cache with page " + page + " in section " + this.sectionInterface.getSectionDescriptor());

					this.eventHandler.sendEvent(SearchableItem.class, new SearchableItemUpdateEvent(page, moduleDescriptor), EventTarget.ALL);

					return;
				}
			}

		}else{

			log.warn("Unable to find previously cached copy of page " + page + " in section " + this.sectionInterface.getSectionDescriptor() + ", doing an add instead");

			this.addPage(page);
		}
	}

	/**
	 * Removes a {@link Page} from the modules page cache.
	 * 
	 * @param page
	 */
	protected synchronized void removePage(Page page){

		if(this.pageCache.values().contains(page)){

			Page cachedPage = this.pageCache.get(page.getAlias());

			if(page.equals(cachedPage)){

				this.pageCache.remove(page.getAlias());

			}else{

				for(Entry<String,Page> entry : this.pageCache.entrySet()){

					if(entry.getValue().equals(page)){

						this.pageCache.remove(entry.getKey());
					}
				}
			}

			log.debug("Page " + page + " removed from cache in section " + this.sectionInterface.getSectionDescriptor());

			eventHandler.sendEvent(SearchableItem.class, new SearchableItemDeleteEvent(page.getPageID().toString(), moduleDescriptor), EventTarget.ALL);

		}else{

			log.warn("Unable to find cached copy of page " + page + " in section " + this.sectionInterface.getSectionDescriptor());
		}
	}

	protected void reloadMenuitems(){

		this.sectionInterface.getMenuCache().moduleUpdated(this.moduleDescriptor, this);
	}

	protected SectionInterface getSectionInterface(){

		return this.sectionInterface;
	}

	protected ForegroundModuleDescriptor getForegroundModuleDescriptor(){

		return this.moduleDescriptor;
	}

	public Collection<Page> getCachedPages(){

		return this.pageCache.values();
	}

	@Override
	public List<SearchableItem> getSearchableItems() {

		return new ArrayList<SearchableItem>(pageCache.values());
	}
}
