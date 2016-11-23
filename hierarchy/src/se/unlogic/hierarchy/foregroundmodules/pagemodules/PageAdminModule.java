/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.pagemodules;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.annotations.CheckboxSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.beans.Breadcrumb;
import se.unlogic.hierarchy.core.beans.SettingDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.SimpleSectionDescriptor;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.daos.interfaces.SectionDAO;
import se.unlogic.hierarchy.core.enums.PathType;
import se.unlogic.hierarchy.core.enums.SystemStatus;
import se.unlogic.hierarchy.core.enums.URLType;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.RootSectionInterface;
import se.unlogic.hierarchy.core.interfaces.SectionCacheListener;
import se.unlogic.hierarchy.core.interfaces.SectionDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.sections.Section;
import se.unlogic.hierarchy.core.utils.AccessUtils;
import se.unlogic.hierarchy.core.utils.BaseFileAccessValidator;
import se.unlogic.hierarchy.core.utils.FCKConnector;
import se.unlogic.hierarchy.core.utils.ModuleUtils;
import se.unlogic.hierarchy.core.utils.usergrouplist.UserGroupListConnector;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.hierarchy.foregroundmodules.pagemodules.daos.PageDAO;
import se.unlogic.hierarchy.foregroundmodules.pagemodules.daos.PageDAOFactory;
import se.unlogic.hierarchy.foregroundmodules.pagemodules.daos.annotated.AnnotatedPageDAOFactory;
import se.unlogic.hierarchy.foregroundmodules.systemadmin.RootSectionUpdater;
import se.unlogic.hierarchy.foregroundmodules.systemadmin.SectionUpdater;
import se.unlogic.standardutils.collections.KeyAlreadyCachedException;
import se.unlogic.standardutils.collections.KeyNotCachedException;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.validation.PositiveStringIntegerValidator;
import se.unlogic.standardutils.validation.StringIntegerValidator;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationErrorType;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.url.URLRewriter;

public class PageAdminModule extends AnnotatedForegroundModule implements SectionCacheListener {

	private static HashMap<DataSource, ForegroundModuleDescriptor> dataSourceInstanceMap = new HashMap<DataSource, ForegroundModuleDescriptor>();
	private static final ReentrantReadWriteLock mapLock = new ReentrantReadWriteLock();
	private static final Lock mapReadLock = mapLock.readLock();
	private static final Lock mapWriteLock = mapLock.writeLock();

	private static final ArrayList<SettingDescriptor> SETTINGDESCRIPTORS = new ArrayList<SettingDescriptor>(4);

	static {
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("csspath", "Editor CSS", "Path to the desired CSS stylesheet for FCKEditor (relative from the contextpath)", false, null, null));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("filestore", "Filestore path", "Path to the directory to be used as filestore", false, null, null));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("diskThreshold", "Max upload size", "Maxmium upload size in megabytes allowed in a single post request", false, "100", new StringIntegerValidator(1, null)));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("pageViewModuleName", "View module name", "The name used for page view modules created by this module", true, "Page viewer", null));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("pageViewModuleAlias", "View module alias", "The alias used for page view modules created by this module", true, "page", null));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createDropDownSetting("pageViewModuleXSLPathType", "View module XSL path type", "The path type used for page view modules created by this module", true, PathType.Classpath.toString(), ModuleUtils.getValueDescriptors(PathType.values())));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("pageViewModuleXSLPath", "View module XSL path", "Path to the XSL stylesheet used by the page view modules created by this module", true, "PageViewModule.en.xsl", null));
	}

	@ModuleSetting
	@CheckboxSettingDescriptor(name = "Disable preview", description = "Show pages in their corresponding section instead of preview if possible")
	private boolean disablePreview = true;

	private static final PagePopulator POPULATOR = new PagePopulator();

	private ConcurrentHashMap<Integer, PageViewModule> viewerModuleMap = new ConcurrentHashMap<Integer, PageViewModule>();

	private PageDAO pageDAO;
	private SectionDAO sectionDAO;
	private FCKConnector connector;

	@ModuleSetting(allowsNull = true)
	protected String filestore;

	@ModuleSetting(allowsNull = true)
	protected String csspath;

	@ModuleSetting
	protected Integer diskThreshold = 100;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "RAM threshold", description = "Maximum size of files in KB to be buffered in RAM during file uploads. Files exceeding the threshold are written to disk instead.", required = true, formatValidator = PositiveStringIntegerValidator.class)
	protected Integer ramThreshold = 500;

	@ModuleSetting
	protected String pageViewModuleName = "Page viewer";

	@ModuleSetting
	protected String pageViewModuleAlias = "page";

	@ModuleSetting
	protected String pageViewModuleXSLPathType = PathType.Classpath.toString();

	@ModuleSetting
	protected String pageViewModuleXSLPath = "PageViewModule.en.xsl";

	private UserGroupListConnector userGroupListConnector;

	private void addInstanceToMap() {

		mapWriteLock.lock();
		try {
			dataSourceInstanceMap.put(this.dataSource, this.moduleDescriptor);
		} finally {
			mapWriteLock.unlock();
		}
	}

	private void removeInstanceFromMap() {

		mapWriteLock.lock();
		try {
			dataSourceInstanceMap.remove(this.dataSource);
		} finally {
			mapWriteLock.unlock();
		}
	}

	public static ForegroundModuleDescriptor getPageAdminModule(DataSource dataSource) {

		mapReadLock.lock();
		try {
			return dataSourceInstanceMap.get(dataSource);
		} finally {
			mapReadLock.unlock();
		}
	}

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		this.sectionDAO = this.sectionInterface.getSystemInterface().getCoreDaoFactory().getSectionDAO();
		this.connector = new FCKConnector(this.filestore, this.diskThreshold, this.ramThreshold);
		this.addInstanceToMap();

		this.moduleDescriptor.getMutableSettingHandler().setSetting(PageAdminModule.class.toString(), moduleDescriptor.getModuleID());

		//Add page view modules to all started sections that contain enabled pages
		createViewModules(systemInterface.getRootSection(), true);

		systemInterface.addSectionCacheListener(this);
	}

	@Override
	public void update(ForegroundModuleDescriptor moduleDescriptor, DataSource dataSource) throws Exception {

		super.update(moduleDescriptor, dataSource);

		this.connector.setDiskThreshold(this.diskThreshold);
		this.connector.setRamThreshold(this.ramThreshold);
		this.connector.setFilestorePath(filestore);

		this.addInstanceToMap();

		this.moduleDescriptor.getMutableSettingHandler().setSetting(PageAdminModule.class.toString(), moduleDescriptor.getModuleID());

		//Update view modules
		updateViewModules(systemInterface.getRootSection());
	}

	@Override
	public void unload() throws Exception {

		super.unload();
		this.removeInstanceFromMap();

		if (systemInterface.getSystemStatus() != SystemStatus.STOPPING) {

			unloadViewModules();
		}

		systemInterface.removeSectionCacheListener(this);
	}

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		super.createDAOs(dataSource);

		PageDAOFactory daoFactory = new AnnotatedPageDAOFactory();

		daoFactory.init(dataSource);

		this.pageDAO = daoFactory.getPageDAO();

		userGroupListConnector = new UserGroupListConnector(systemInterface);
	}

	private void createViewModules(SectionInterface sectionInterface, boolean recursive) {

		try {
			if (this.pageDAO.sectionHasEnabledPages(sectionInterface.getSectionDescriptor().getSectionID())) {

				log.info("Adding page view module to section " + sectionInterface.getSectionDescriptor());

				SimpleForegroundModuleDescriptor descriptor = new SimpleForegroundModuleDescriptor();

				descriptor.setAdminAccess(true);
				descriptor.setUserAccess(true);
				descriptor.setAnonymousAccess(true);

				descriptor.setAlias(this.pageViewModuleAlias);
				descriptor.setXslPathType(PathType.valueOf(this.pageViewModuleXSLPathType));
				descriptor.setXslPath(pageViewModuleXSLPath);
				descriptor.setName(this.pageViewModuleName);
				descriptor.setDescription(this.pageViewModuleName);
				descriptor.setClassname(PageViewModule.class.getName());

				descriptor.setMutableSettingHandler(moduleDescriptor.getMutableSettingHandler());

				descriptor.setVisibleInMenu(true);
				descriptor.setEnabled(true);
				descriptor.setSectionID(sectionInterface.getSectionDescriptor().getSectionID());
				descriptor.setStaticContentPackage("staticcontent");

				descriptor.setDataSourceID(this.moduleDescriptor.getDataSourceID());

				PageViewModule pageViewModule = (PageViewModule) sectionInterface.getForegroundModuleCache().cache(descriptor);

				this.viewerModuleMap.put(sectionInterface.getSectionDescriptor().getSectionID(), pageViewModule);
			}

		} catch (Exception e) {

			log.error("Error adding page view module to section " + sectionInterface.getSectionDescriptor(), e);
		}

		if (recursive) {

			for (SectionInterface childSection : sectionInterface.getSectionCache().getSectionMap().values()) {

				createViewModules(childSection, recursive);
			}
		}
	}

	private void updateViewModules(RootSectionInterface rootSection) {

		for (PageViewModule pageViewModule : viewerModuleMap.values()) {

			try {
				SimpleForegroundModuleDescriptor descriptor = (SimpleForegroundModuleDescriptor) pageViewModule.getForegroundModuleDescriptor();

				descriptor.setAlias(this.pageViewModuleAlias);
				descriptor.setXslPathType(PathType.valueOf(this.pageViewModuleXSLPathType));
				descriptor.setXslPath(pageViewModuleXSLPath);
				descriptor.setName(this.pageViewModuleName);
				descriptor.setDescription(this.pageViewModuleName);
				descriptor.setMutableSettingHandler(this.moduleDescriptor.getMutableSettingHandler());
				descriptor.setDataSourceID(this.moduleDescriptor.getDataSourceID());

				pageViewModule.getSectionInterface().getForegroundModuleCache().update(descriptor);

			} catch (KeyNotCachedException e) {

				log.debug("Unable to update page view module in section " + sectionInterface.getSectionDescriptor() + ", module not cached", e);
				this.viewerModuleMap.remove(sectionInterface.getSectionDescriptor().getSectionID());

			} catch (Exception e) {

				log.error("Error updating page view module in section " + sectionInterface.getSectionDescriptor(), e);
			}
		}
	}

	private void unloadViewModules() {

		for (PageViewModule pageViewModule : viewerModuleMap.values()) {

			try {
				pageViewModule.getSectionInterface().getForegroundModuleCache().unload(pageViewModule.getForegroundModuleDescriptor());

			} catch (KeyNotCachedException e) {

				log.debug("Unable to unload page view module in section " + pageViewModule.getSectionInterface().getSectionDescriptor() + ", module not cached", e);

			} catch (Exception e) {

				log.error("Error unloading page view module in section " + pageViewModule.getSectionInterface().getSectionDescriptor(), e);
			}
		}

	}

	@Override
	public SimpleForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws SQLException {

		Document doc = this.createDocument(req, uriParser);

		SimpleSectionDescriptor rootSection = this.sectionDAO.getRootSection(true);

		Element sectionsElement = doc.createElement("sections");
		doc.getFirstChild().appendChild(sectionsElement);

		this.appendSection(sectionsElement, doc, rootSection, true);

		return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb());
	}

	private void appendSection(Element parentSection, Document doc, SimpleSectionDescriptor simpleSectionDescriptor, boolean includePages) throws SQLException {

		Element sectionElement = simpleSectionDescriptor.toXML(doc);
		parentSection.appendChild(sectionElement);

		if (includePages) {
			List<Page> pages = this.pageDAO.getPages(simpleSectionDescriptor.getSectionID());

			if (pages != null) {
				Element pagesElement = doc.createElement("pages");
				sectionElement.appendChild(pagesElement);

				for (Page page : pages) {
					pagesElement.appendChild(page.toXML(doc));
				}
			}
		}

		if (simpleSectionDescriptor.getSubSectionsList() != null) {
			Element subSectionsElement = doc.createElement("subsections");
			sectionElement.appendChild(subSectionsElement);

			for (SimpleSectionDescriptor subSectionBean : simpleSectionDescriptor.getSubSectionsList()) {
				this.appendSection(subSectionsElement, doc, subSectionBean, includePages);
			}
		}
	}

	public Document createDocument(HttpServletRequest req, URIParser uriParser) {

		Document doc = XMLUtils.createDomDocument();
		Element document = doc.createElement("document");
		document.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser));
		document.appendChild(this.moduleDescriptor.toXML(doc));
		doc.appendChild(document);
		return doc;
	}

	@WebPublic
	public SimpleForegroundModuleResponse add(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws KeyNotCachedException, KeyAlreadyCachedException, SQLException, URINotFoundException, IOException {

		SimpleSectionDescriptor section = null;

		// Check that a valid sectionID is provided
		if (uriParser.size() != 3 || !NumberUtils.isInt(uriParser.get(2)) || (section = this.sectionDAO.getSection(Integer.parseInt(uriParser.get(2)), false)) == null) {

			// No or invalid sectionID provided
			throw new URINotFoundException(uriParser);

		} else {

			// Valid sectionID provided
			ValidationException validationException = null;

			if (req.getMethod().equalsIgnoreCase("POST")) {
				try {
					// Populate page
					Page page = POPULATOR.populate(req);

					if (pageDAO.getPage(page.getAlias(), section.getSectionID()) != null) {
						throw new ValidationException(new ValidationError("alias", ValidationErrorType.Other, "duplicatePageAlias"));
					}

					log.info("User " + user + " adding page " + page + " in section " + section);

					// Replace absolute file paths
					this.removeAbsoluteFileUrls(page, uriParser);
					URLRewriter.removeAbsoluteLinkUrls(page, req);

					// Set sectionID
					page.setSectionID(section.getSectionID());

					// Save the page
					pageDAO.add(page);

					pageAdded(page);

					// Redirect user
					res.sendRedirect(getModuleURI(req) + "/show/" + page.getPageID());
					return null;
				} catch (ValidationException e) {
					validationException = e;
				}
			}

			Document doc = this.createDocument(req, uriParser);
			Element document = (Element) doc.getFirstChild();

			Element addPageForm = doc.createElement("addPageForm");
			document.appendChild(addPageForm);
			addPageForm.appendChild(section.toXML(doc));

			AccessUtils.appendAllowedGroupsAndUsers(doc, addPageForm, section, systemInterface.getUserHandler(), systemInterface.getGroupHandler());

			if (this.csspath != null) {
				addPageForm.appendChild(XMLUtils.createCDATAElement("cssPath", csspath, doc));
			}

			// Append any errors
			if (validationException != null) {
				addPageForm.appendChild(validationException.toXML(doc));
				addPageForm.appendChild(RequestUtils.getRequestParameters(req, doc));
			}
			;

			return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb());
		}
	}

	private Page getPage(URIParser uriParser, User user) throws NumberFormatException, URINotFoundException, SQLException {

		Page page = null;

		if (NumberUtils.isInt(uriParser.get(2)) && (page = pageDAO.getPage(Integer.parseInt(uriParser.get(2)))) != null) {
			return page;
		} else {
			throw new URINotFoundException(uriParser);
		}
	}

	private Breadcrumb getPageBreadcrumb(HttpServletRequest req, Page page, String method) {

		return new Breadcrumb(page.getName(), page.getDescription(), getFullAlias() + "/" + method + "/" + page.getPageID(), URLType.RELATIVE_FROM_CONTEXTPATH);
	}

	@WebPublic(alias = "firstpage")
	public SimpleForegroundModuleResponse setFirstpage(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		//This is a primitive stopgap measure until this module rewritten to be more update and with more section administration capabilities
		Page page = this.getPage(uriParser, user);

		SectionDAO sectionDAO = this.systemInterface.getCoreDaoFactory().getSectionDAO();

		SimpleSectionDescriptor sectionDescriptor = sectionDAO.getSection(page.getSectionID(), true);

		log.info("User " + user + " making page " + page + " firstpage in section " + sectionDescriptor);

		String pageAlias = "/" + this.pageViewModuleAlias + "/" + page.getAlias();

		sectionDescriptor.setAnonymousDefaultURI(pageAlias);
		sectionDescriptor.setUserDefaultURI(pageAlias);

		sectionDAO.update(sectionDescriptor);

		//Check if root section
		if (sectionDescriptor.getParentSectionID() == null) {

			//Update root section
			RootSectionUpdater rootSectionUpdater = new RootSectionUpdater(this.sectionInterface.getSystemInterface().getRootSection(), sectionDescriptor);
			rootSectionUpdater.setDaemon(true);
			rootSectionUpdater.start();

			res.sendRedirect(req.getContextPath() + sectionDescriptor.getFullAlias() + pageAlias);
			return null;

		} else {
			SectionInterface sectionInterface = systemInterface.getSectionInterface(sectionDescriptor.getParentSectionID());

			if (sectionInterface != null && sectionInterface.getSectionCache().isCached(sectionDescriptor)) {

				SectionUpdater sectionUpdater = new SectionUpdater(sectionInterface.getSectionCache(), sectionDescriptor, user);
				sectionUpdater.isDaemon();
				sectionUpdater.start();

				if (AccessUtils.checkAccess(user, sectionDescriptor)) {

					res.sendRedirect(req.getContextPath() + sectionDescriptor.getFullAlias() + pageAlias);
					return null;
				}
			}
		}

		redirectToDefaultMethod(req, res);

		return null;
	}

	@WebPublic
	public SimpleForegroundModuleResponse show(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		Page page = this.getPage(uriParser, user);

		log.info("User " + user + " viewing page " + page);

		this.setAbsoluteFileUrls(page, uriParser);
		URLRewriter.setAbsoluteLinkUrls(page, req);

		Document doc = this.createDocument(req, uriParser);

		Element preview = doc.createElement("preview");
		doc.getFirstChild().appendChild(preview);

		// Append the page
		preview.appendChild(page.toXML(doc));

		// Get the page module for this section
		PageViewModule pageViewModule = this.viewerModuleMap.get(page.getSectionID());

		if (pageViewModule != null) {

			if (disablePreview && page.isEnabled() && AccessUtils.checkAccess(user, page) && AccessUtils.checkRecursiveModuleAccess(user, pageViewModule.getForegroundModuleDescriptor(), systemInterface)) {

				res.sendRedirect(req.getContextPath() + pageViewModule.getSectionInterface().getSectionDescriptor().getFullAlias() + "/" + pageViewModule.getForegroundModuleDescriptor().getAlias() + "/" + page.getAlias());

				return null;
			}

			preview.appendChild(pageViewModule.getForegroundModuleDescriptor().toXML(doc));
			preview.appendChild(pageViewModule.getSectionInterface().getSectionDescriptor().toXML(doc));
		} else {
			// No page view module loaded in the page section, get section from db
			preview.appendChild(sectionDAO.getSection(page.getSectionID(), false).toXML(doc));
		}

		return new SimpleForegroundModuleResponse(doc, page.getName(), getDefaultBreadcrumb(), getPageBreadcrumb(req, page, "show"));
	}

	@WebPublic
	public SimpleForegroundModuleResponse move(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		Page page = this.getPage(uriParser, user);

		SimpleSectionDescriptor section = null;

		if (uriParser.size() == 3) {

			// Show section tree
			Document doc = this.createDocument(req, uriParser);
			Element movePage = doc.createElement("movePageForm");
			doc.getFirstChild().appendChild(movePage);
			movePage.appendChild(page.toXML(doc));

			SimpleSectionDescriptor rootSection = this.sectionDAO.getRootSection(true);

			Element sectionsElement = doc.createElement("sections");
			movePage.appendChild(sectionsElement);

			this.appendSection(sectionsElement, doc, rootSection, false);

			return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb(), getPageBreadcrumb(req, page, "show"));

		} else if (uriParser.size() == 4 && NumberUtils.isInt(uriParser.get(3)) && (section = this.sectionDAO.getSection(Integer.parseInt(uriParser.get(3)), false)) != null) {

			//TODO check alias for dupes!

			// Store old sectionID
			Integer oldSectionID = page.getSectionID();

			// Move the page to selected section
			log.info("User " + user + " moving page " + page + " to section " + section);
			page.setSectionID(section.getSectionID());
			this.pageDAO.update(page);

			pageRemoved(page, oldSectionID, page.isEnabled(), page.isVisibleInMenu());
			pageAdded(page);

			res.sendRedirect(getModuleURI(req));
			return null;

		} else {
			throw new URINotFoundException(uriParser);
		}
	}

	@WebPublic
	public SimpleForegroundModuleResponse copy(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		Page page = this.getPage(uriParser, user);

		SimpleSectionDescriptor section = null;

		if (uriParser.size() == 3) {

			// Show section tree
			Document doc = this.createDocument(req, uriParser);
			Element movePage = doc.createElement("copyPageForm");
			doc.getFirstChild().appendChild(movePage);
			movePage.appendChild(page.toXML(doc));

			SimpleSectionDescriptor rootSection = this.sectionDAO.getRootSection(true);

			Element sectionsElement = doc.createElement("sections");
			movePage.appendChild(sectionsElement);

			this.appendSection(sectionsElement, doc, rootSection, false);

			return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb(), getPageBreadcrumb(req, page, "show"));

		} else if (uriParser.size() == 4 && NumberUtils.isInt(uriParser.get(3)) && (section = this.sectionDAO.getSection(Integer.parseInt(uriParser.get(3)), false)) != null) {

			// Copy the page to selected section
			log.info("User " + user + " copying page " + page + " to section " + section);

			page.setPageID(null);
			page.setSectionID(section.getSectionID());

			// Check if alias already exists
			if (this.pageDAO.getPage(page.getAlias(), page.getSectionID()) != null) {

				// Alias already exists add number after alias
				for (int copyNumber = 1; copyNumber < Integer.MAX_VALUE; copyNumber++) {

					if (this.pageDAO.getPage(page.getAlias() + copyNumber, page.getSectionID()) == null) {
						page.setAlias(page.getAlias() + copyNumber);
						page.setName(page.getName() + " (" + copyNumber + ")");
						break;
					}
				}
			}

			this.pageDAO.add(page);
			this.pageAdded(page);

			res.sendRedirect(getModuleURI(req));
			return null;

		} else {
			throw new URINotFoundException(uriParser);
		}
	}

	@WebPublic
	public SimpleForegroundModuleResponse update(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		Page page = this.getPage(uriParser, user);

		ValidationException validationException = null;

		if (req.getMethod().equalsIgnoreCase("POST")) {

			try {
				boolean wasEanbled = page.isEnabled();
				boolean wasVisibleInMenu = page.isVisibleInMenu();

				// Populate page
				page = POPULATOR.populate(page, req);

				Page aliasMatch = this.pageDAO.getPage(page.getAlias(), page.getSectionID());

				if (aliasMatch != null && !aliasMatch.getPageID().equals(page.getPageID())) {
					throw new ValidationException(new ValidationError("alias", ValidationErrorType.Other, "duplicatePageAlias"));
				}

				log.info("User " + user + " updating page " + page);

				// Replace absolute file paths
				this.removeAbsoluteFileUrls(page, uriParser);
				URLRewriter.removeAbsoluteLinkUrls(page, req);

				// Save changes
				pageDAO.update(page);
				pageUpdated(page, wasEanbled, wasVisibleInMenu);

				// Redirect user
				res.sendRedirect(getModuleURI(req) + "/show/" + page.getPageID());
				return null;

			} catch (ValidationException e) {
				validationException = e;
			}
		}

		Document doc = this.createDocument(req, uriParser);
		Element document = (Element) doc.getFirstChild();

		Element updatePageForm = doc.createElement("updatePageForm");
		document.appendChild(updatePageForm);

		AccessUtils.appendAllowedGroupsAndUsers(doc, updatePageForm, page, systemInterface.getUserHandler(), systemInterface.getGroupHandler());

		// Append any errors
		if (validationException != null) {
			updatePageForm.appendChild(validationException.toXML(doc));
			updatePageForm.appendChild(RequestUtils.getRequestParameters(req, doc));
		}

		this.setAbsoluteFileUrls(page, uriParser);
		URLRewriter.setAbsoluteLinkUrls(page, req);

		// Append the page
		updatePageForm.appendChild(page.toXML(doc));

		// Append the section
		updatePageForm.appendChild(sectionDAO.getSection(page.getSectionID(), false).toXML(doc));

		if (this.csspath != null) {
			updatePageForm.appendChild(XMLUtils.createCDATAElement("cssPath", csspath, doc));
		}

		return new SimpleForegroundModuleResponse(doc, page.getName(), this.getDefaultBreadcrumb(), getPageBreadcrumb(req, page, "show"));
	}

	@WebPublic
	public SimpleForegroundModuleResponse delete(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		Page page = this.getPage(uriParser, user);

		// Delete the page
		log.info("User " + user + " deleting page " + page);
		pageDAO.delete(page);

		this.pageRemoved(page, page.getSectionID(), page.isEnabled(), page.isVisibleInMenu());

		// Redirect user
		if (req.getParameter("returnto") != null) {

			SectionInterface sectionInterface = systemInterface.getSectionInterface(page.getSectionID());

			if (sectionInterface != null) {

				res.sendRedirect(req.getContextPath() + sectionInterface.getSectionDescriptor().getFullAlias());

				return null;
			}
		}

		res.sendRedirect(getModuleURI(req));

		return null;
	}

	@WebPublic
	public SimpleForegroundModuleResponse connector(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws TransformerFactoryConfigurationError, TransformerException, IOException {

		this.connector.processRequest(req, res, uriParser, user, moduleDescriptor);

		return null;
	}

	@WebPublic
	public SimpleForegroundModuleResponse file(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		this.connector.processFileRequest(req, res, user, uriParser, moduleDescriptor, sectionInterface, 2, null);

		return null;
	}

	private String getAbsoluteFileURL(URIParser uriParser) {

		return uriParser.getCurrentURI(true) + "/" + this.moduleDescriptor.getAlias() + "/file";
	}

	public void removeAbsoluteFileUrls(Page page, URIParser uriParser) {

		String text = page.getText();

		String absoluteFileURL = this.getAbsoluteFileURL(uriParser);

		for (String attribute : BaseFileAccessValidator.TAG_ATTRIBUTES) {

			text = text.replace(attribute + "=\"" + absoluteFileURL, attribute + "=\"" + Page.RELATIVE_PATH_MARKER);
			text = text.replace(attribute + "='" + absoluteFileURL, attribute + "='" + Page.RELATIVE_PATH_MARKER);
		}

		page.setText(text);
	}

	public void setAbsoluteFileUrls(Page page, URIParser uriParser) {

		String text = page.getText();

		String absoluteFileURL = this.getAbsoluteFileURL(uriParser);

		for (String attribute : BaseFileAccessValidator.TAG_ATTRIBUTES) {

			text = text.replace(attribute + "=\"" + Page.RELATIVE_PATH_MARKER, attribute + "=\"" + absoluteFileURL);
			text = text.replace(attribute + "='" + Page.RELATIVE_PATH_MARKER, attribute + "='" + absoluteFileURL);
		}

		page.setText(text);
	}

	@Override
	public List<SettingDescriptor> getSettings() {

		List<SettingDescriptor> settings = super.getSettings();

		settings.addAll(SETTINGDESCRIPTORS);

		return settings;
	}

	@Override
	public synchronized void sectionCached(SectionDescriptor sectionDescriptor, Section sectionInstance) throws KeyAlreadyCachedException {

		this.createViewModules(sectionInstance, false);
	}

	@Override
	public void sectionUpdated(SectionDescriptor sectionDescriptor, Section sectionInstance) throws KeyNotCachedException {

	}

	@Override
	public synchronized void sectionUnloaded(SectionDescriptor sectionDescriptor, Section sectionInstance) throws KeyNotCachedException {

		this.viewerModuleMap.remove(sectionDescriptor.getSectionID());
	}

	public synchronized void pageAdded(Page page) {

		if (!page.isEnabled()) {

			return;
		}

		PageViewModule viewModule = this.viewerModuleMap.get(page.getSectionID());

		if (viewModule == null) {

			SectionInterface sectionInterface = systemInterface.getSectionInterface(page.getSectionID());

			if (sectionInterface != null) {

				this.createViewModules(sectionInterface, false);
			}

		} else {

			viewModule.addPage(page);

			if (page.isVisibleInMenu()) {

				viewModule.reloadMenuitems();
			}
		}
	}

	public synchronized void pageUpdated(Page page, boolean wasEnabled, boolean wasVisibleInMenu) throws Exception {

		if (!page.isEnabled() && !wasEnabled) {

			return;

		} else if (page.isEnabled() && !wasEnabled) {

			this.pageAdded(page);

		} else if (page.isEnabled() && wasEnabled) {

			PageViewModule viewModule = this.viewerModuleMap.get(page.getSectionID());

			if (viewModule != null) {

				viewModule.updatePage(page);

				if (page.isVisibleInMenu() || wasVisibleInMenu) {

					viewModule.reloadMenuitems();
				}
			}

		} else if (!page.isEnabled() && wasEnabled) {

			this.pageRemoved(page, page.getSectionID(), wasEnabled, wasVisibleInMenu);
		}
	}

	public synchronized void pageRemoved(Page page, Integer sectionID, boolean wasEnabled, boolean wasVisibleInMenu) throws Exception {

		if (!wasEnabled) {

			return;
		}

		PageViewModule viewModule = this.viewerModuleMap.get(sectionID);

		if (viewModule != null) {

			if (this.pageDAO.sectionHasEnabledPages(sectionID)) {

				viewModule.removePage(page);

				if (wasVisibleInMenu) {

					viewModule.reloadMenuitems();
				}

			} else {

				log.info("Removing page view module from section " + viewModule.getSectionInterface().getSectionDescriptor());

				viewModule.getSectionInterface().getForegroundModuleCache().unload(viewModule.getForegroundModuleDescriptor());

				this.viewerModuleMap.remove(viewModule);
			}
		}
	}

	public ModuleDescriptor getModuleDescriptor() {

		return this.moduleDescriptor;
	}

	@WebPublic(alias = "users")
	public ForegroundModuleResponse getUsers(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {

		return userGroupListConnector.getUsers(req, res, user, uriParser);
	}

	@WebPublic(alias = "groups")
	public ForegroundModuleResponse getGroups(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {

		return userGroupListConnector.getGroups(req, res, user, uriParser);
	}
}
