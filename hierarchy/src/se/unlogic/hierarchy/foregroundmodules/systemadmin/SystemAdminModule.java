/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.systemadmin;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import se.unlogic.fileuploadutils.MultipartRequest;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.annotations.XSLVariable;
import se.unlogic.hierarchy.core.beans.BaseModuleDescriptor;
import se.unlogic.hierarchy.core.beans.BaseVisibleModuleDescriptor;
import se.unlogic.hierarchy.core.beans.Breadcrumb;
import se.unlogic.hierarchy.core.beans.SettingDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleBackgroundModuleDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleDataSourceDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleFilterModuleDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.SimpleSectionDescriptor;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.beans.ValueDescriptor;
import se.unlogic.hierarchy.core.cache.BaseModuleCache;
import se.unlogic.hierarchy.core.daos.factories.CoreDaoFactory;
import se.unlogic.hierarchy.core.daos.interfaces.BackgroundModuleDAO;
import se.unlogic.hierarchy.core.daos.interfaces.DataSourceDAO;
import se.unlogic.hierarchy.core.daos.interfaces.FilterModuleDAO;
import se.unlogic.hierarchy.core.daos.interfaces.ForegroundModuleDAO;
import se.unlogic.hierarchy.core.daos.interfaces.ModuleDAO;
import se.unlogic.hierarchy.core.daos.interfaces.SectionDAO;
import se.unlogic.hierarchy.core.enums.CRUDAction;
import se.unlogic.hierarchy.core.enums.DisplayType;
import se.unlogic.hierarchy.core.enums.EventTarget;
import se.unlogic.hierarchy.core.enums.HTTPProtocol;
import se.unlogic.hierarchy.core.enums.ModuleType;
import se.unlogic.hierarchy.core.enums.PathType;
import se.unlogic.hierarchy.core.events.CRUDEvent;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.handlers.SimpleSettingHandler;
import se.unlogic.hierarchy.core.interfaces.BackgroundModule;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.FilterModule;
import se.unlogic.hierarchy.core.interfaces.FilterModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModule;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.utils.AccessUtils;
import se.unlogic.hierarchy.core.utils.usergrouplist.UserGroupListConnector;
import se.unlogic.hierarchy.core.validationerrors.DuplicateModuleAliasValidationError;
import se.unlogic.hierarchy.core.validationerrors.DuplicateModuleIDValidationError;
import se.unlogic.hierarchy.core.validationerrors.FileSizeLimitExceededValidationError;
import se.unlogic.hierarchy.core.validationerrors.InvalidFileExtensionValidationError;
import se.unlogic.hierarchy.core.validationerrors.RequestSizeLimitExceededValidationError;
import se.unlogic.hierarchy.core.validationerrors.UnableToParseFileValidationError;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.collections.KeyNotCachedException;
import se.unlogic.standardutils.enums.EnumUtils;
import se.unlogic.standardutils.io.BinarySizes;
import se.unlogic.standardutils.io.FileUtils;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.streams.StreamUtils;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.PositiveStringIntegerValidator;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationErrorType;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLParser;
import se.unlogic.standardutils.xml.XMLParserPopulateable;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.populators.annotated.AnnotatedRequestPopulator;
import se.unlogic.webutils.url.URLRewriter;

public class SystemAdminModule extends AnnotatedForegroundModule {

	private static final ModuleInfoBeanComparator MODULE_COMPARATOR = new ModuleInfoBeanComparator();

	private static final AnnotatedRequestPopulator<SimpleSectionDescriptor> SECTION_DESCRIPTOR_POPULATOR = new AnnotatedRequestPopulator<SimpleSectionDescriptor>(SimpleSectionDescriptor.class);
	private static final AnnotatedRequestPopulator<SimpleForegroundModuleDescriptor> FOREGROUND_MODULE_DESCRIPTOR_POPULATOR = new AnnotatedRequestPopulator<SimpleForegroundModuleDescriptor>(SimpleForegroundModuleDescriptor.class);
	private static final AnnotatedRequestPopulator<SimpleBackgroundModuleDescriptor> BACKGROUND_MODULE_DESCRIPTOR_POPULATOR = new AnnotatedRequestPopulator<SimpleBackgroundModuleDescriptor>(SimpleBackgroundModuleDescriptor.class);
	private static final AnnotatedRequestPopulator<SimpleFilterModuleDescriptor> FILTER_MODULE_DESCRIPTOR_POPULATOR = new AnnotatedRequestPopulator<SimpleFilterModuleDescriptor>(SimpleFilterModuleDescriptor.class);

	@ModuleSetting(allowsNull = true)
	@TextFieldSettingDescriptor(name = "Editor CSS", description = "Path to the desired CSS stylesheet for FCKEditor (relative from the contextpath)", required = false)
	protected String cssPath;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Max upload size", description = "Maxmium upload size in megabytes allowed in a single post request", required = true, formatValidator = PositiveStringIntegerValidator.class)
	protected Integer maxRequestSize = 5;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Max file size", description = "Maxmium file size in megabytes allowed", required = true, formatValidator = PositiveStringIntegerValidator.class)
	protected Integer maxFileSize = 1;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "RAM threshold", description = "Maximum size of files in KB to be buffered in RAM during file uploads. Files exceeding the threshold are written to disk instead.", required = true, formatValidator = PositiveStringIntegerValidator.class)
	protected Integer ramThreshold = 500;

	private SectionDAO sectionDAO;
	private ForegroundModuleDAO foregroundModuleDAO;
	private BackgroundModuleDAO backgroundModuleDAO;
	private FilterModuleDAO filterModuleDAO;
	private DataSourceDAO dataSourceDAO;

	private UserGroupListConnector userGroupListConnector;

	@XSLVariable
	protected String addForegroundModuleBreadCrumbText = "Add foreground module";

	@XSLVariable
	protected String updateForegroundModuleBreadCrumbText = "Edit foreground module: ";

	@XSLVariable
	protected String copyForegroundModuleBreadCrumbText = "Copy foreground module: ";

	@XSLVariable
	protected String moveForegroundModuleBreadCrumbText = "Move foreground module: ";

	@XSLVariable
	protected String addBackgroundModuleBreadCrumbText = "Add background module";

	@XSLVariable
	protected String updateBackgroundModuleBreadCrumbText = "Edit background module: ";

	@XSLVariable
	protected String copyBackgroundModuleBreadCrumbText = "Copy background module: ";

	@XSLVariable
	protected String moveBackgroundModuleBreadCrumbText = "Move background module: ";

	@XSLVariable
	private String addFilterModuleBreadCrumbText = "Add filter module";

	@XSLVariable
	private String updateFilterModuleBreadCrumbText = "Update filter module";

	@XSLVariable
	protected String addSectionBreadCrumbText = "Add section";

	@XSLVariable
	protected String updateSectionBreadCrumbText = "Edit section: ";

	@XSLVariable
	protected String moveSectionBreadCrumbText = "Move section: ";

	@XSLVariable
	protected String importModulesBreadCrumbText = "Import modules into section: ";

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		CoreDaoFactory coreDaoFactory = systemInterface.getCoreDaoFactory();

		this.sectionDAO = coreDaoFactory.getSectionDAO();
		this.foregroundModuleDAO = coreDaoFactory.getForegroundModuleDAO();
		this.backgroundModuleDAO = coreDaoFactory.getBackgroundModuleDAO();
		this.filterModuleDAO = coreDaoFactory.getFilterModuleDAO();
		this.dataSourceDAO = coreDaoFactory.getDataSourceDAO();

		userGroupListConnector = new UserGroupListConnector(systemInterface);
	}

	@Override
	public SimpleForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		log.info("User " + user + " listing system tree");

		Document doc = this.createDocument(req, uriParser);

		SimpleSectionDescriptor rootSection = this.sectionDAO.getRootSection(true);

		Element sectionsElement = doc.createElement("sections");
		doc.getFirstChild().appendChild(sectionsElement);

		this.appendSection(sectionsElement, doc, rootSection, true);

		return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb());
	}

	private void appendSection(Element parentSection, Document doc, SimpleSectionDescriptor simpleSectionDescriptor, boolean modules) throws SQLException {

		Element sectionElement = simpleSectionDescriptor.toXML(doc);
		parentSection.appendChild(sectionElement);

		SectionInterface sectionInterface = systemInterface.getSectionInterface(simpleSectionDescriptor.getSectionID());

		if (sectionInterface == null) {
			// Append section status
			sectionElement.setAttribute("cached", "false");

			Element modulesElement = doc.createElement("modules");
			sectionElement.appendChild(modulesElement);

			// Append modules from database
			List<SimpleForegroundModuleDescriptor> foregroundModules = this.foregroundModuleDAO.getModules(simpleSectionDescriptor.getSectionID());

			if (foregroundModules != null) {

				for (ForegroundModuleDescriptor moduleDescriptor : foregroundModules) {

					modulesElement.appendChild(ModuleInfoBean.toXML(doc, moduleDescriptor, ModuleType.FOREGROUND, true, false));
				}
			}

			List<SimpleBackgroundModuleDescriptor> backgroundModules = this.backgroundModuleDAO.getModules(simpleSectionDescriptor.getSectionID());

			if (backgroundModules != null) {

				for (BackgroundModuleDescriptor moduleDescriptor : backgroundModules) {

					modulesElement.appendChild(ModuleInfoBean.toXML(doc, moduleDescriptor, ModuleType.FOREGROUND, true, false));
				}
			}

		} else {
			// Append section status
			sectionElement.setAttribute("cached", "true");

			if (modules) {
				// Append modules from both cache and database
				ArrayList<ModuleInfoBean> moduleInfoList = new ArrayList<ModuleInfoBean>();

				// Loop thru all foreground modules in the database
				List<SimpleForegroundModuleDescriptor> dbForegroundModuleList = this.foregroundModuleDAO.getModules(simpleSectionDescriptor.getSectionID());

				if (dbForegroundModuleList != null) {
					for (SimpleForegroundModuleDescriptor mb : dbForegroundModuleList) {
						ModuleInfoBean moduleInfoBean = new ModuleInfoBean();

						moduleInfoBean.setModuleBean(mb);
						moduleInfoBean.setInDatabase(true);
						moduleInfoBean.setModuleType(ModuleType.FOREGROUND);

						if (sectionInterface.getForegroundModuleCache().getModule(mb) != null) {
							moduleInfoBean.setCached(true);
						}

						moduleInfoList.add(moduleInfoBean);
					}
				}

				// Loop thru all foreground modules in the cache
				ArrayList<ForegroundModuleDescriptor> cachedForegroundModuleList = sectionInterface.getForegroundModuleCache().getCachedModuleDescriptors();

				if (cachedForegroundModuleList != null) {
					for (ForegroundModuleDescriptor mb : cachedForegroundModuleList) {
						if (dbForegroundModuleList == null || !dbForegroundModuleList.contains(mb)) {
							ModuleInfoBean moduleInfoBean = new ModuleInfoBean();

							moduleInfoBean.setModuleBean(mb);
							moduleInfoBean.setCached(true);
							moduleInfoBean.setModuleType(ModuleType.FOREGROUND);
							moduleInfoList.add(moduleInfoBean);
						}
					}
				}

				// Loop thru all background modules in the database
				List<SimpleBackgroundModuleDescriptor> dbBackgroundModuleList = this.backgroundModuleDAO.getModules(simpleSectionDescriptor.getSectionID());

				if (dbBackgroundModuleList != null) {
					for (SimpleBackgroundModuleDescriptor mb : dbBackgroundModuleList) {
						ModuleInfoBean moduleInfoBean = new ModuleInfoBean();

						moduleInfoBean.setModuleBean(mb);
						moduleInfoBean.setInDatabase(true);
						moduleInfoBean.setModuleType(ModuleType.BACKGROUND);

						if (sectionInterface.getBackgroundModuleCache().getModule(mb) != null) {
							moduleInfoBean.setCached(true);
						}

						moduleInfoList.add(moduleInfoBean);
					}
				}

				// Loop thru all background modules in the cache
				ArrayList<BackgroundModuleDescriptor> cachedBackgroundModuleList = sectionInterface.getBackgroundModuleCache().getCachedModuleDescriptors();

				if (cachedBackgroundModuleList != null) {
					for (BackgroundModuleDescriptor mb : cachedBackgroundModuleList) {
						if (dbBackgroundModuleList == null || !dbBackgroundModuleList.contains(mb)) {

							ModuleInfoBean moduleInfoBean = new ModuleInfoBean();

							moduleInfoBean.setModuleBean(mb);
							moduleInfoBean.setCached(true);
							moduleInfoBean.setModuleType(ModuleType.BACKGROUND);
							moduleInfoList.add(moduleInfoBean);
						}
					}
				}

				if (simpleSectionDescriptor.getParentSectionID() == null) {

					// Loop thru all filter modules in the database
					List<SimpleFilterModuleDescriptor> dbFilterModuleList = this.filterModuleDAO.getModules();

					if (dbFilterModuleList != null) {
						for (SimpleFilterModuleDescriptor mb : dbFilterModuleList) {
							ModuleInfoBean moduleInfoBean = new ModuleInfoBean();

							moduleInfoBean.setModuleBean(mb);
							moduleInfoBean.setInDatabase(true);
							moduleInfoBean.setModuleType(ModuleType.FILTER);

							if (systemInterface.getFilterModuleCache().getModule(mb) != null) {
								moduleInfoBean.setCached(true);
							}

							moduleInfoList.add(moduleInfoBean);
						}
					}

					// Loop thru all filter modules in the cache
					List<FilterModuleDescriptor> cachedFilterModuleList = systemInterface.getFilterModuleCache().getCachedModuleDescriptors();

					if (cachedFilterModuleList != null) {
						for (FilterModuleDescriptor mb : cachedFilterModuleList) {
							if (dbFilterModuleList == null || !dbFilterModuleList.contains(mb)) {

								ModuleInfoBean moduleInfoBean = new ModuleInfoBean();

								moduleInfoBean.setModuleBean(mb);
								moduleInfoBean.setCached(true);
								moduleInfoBean.setModuleType(ModuleType.FILTER);
								moduleInfoList.add(moduleInfoBean);
							}
						}
					}
				}

				Collections.sort(moduleInfoList, MODULE_COMPARATOR);

				Element modulesElement = doc.createElement("modules");
				sectionElement.appendChild(modulesElement);

				for (ModuleInfoBean moduleInfoBean : moduleInfoList) {
					modulesElement.appendChild(moduleInfoBean.toXML(doc));
				}
			}

		}

		if (simpleSectionDescriptor.getSubSectionsList() != null) {
			Element subSectionsElement = doc.createElement("subsections");
			sectionElement.appendChild(subSectionsElement);

			for (SimpleSectionDescriptor subSectionBean : simpleSectionDescriptor.getSubSectionsList()) {
				this.appendSection(subSectionsElement, doc, subSectionBean, modules);
			}
		}
	}

	public Document createDocument(HttpServletRequest req, URIParser uriParser) {

		Document doc = XMLUtils.createDomDocument();
		Element document = doc.createElement("document");
		document.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser));
		document.appendChild(this.moduleDescriptor.toXML(doc));
		XMLUtils.appendNewElement(doc, document, "cssPath", this.cssPath);
		doc.appendChild(document);
		return doc;
	}

	private Breadcrumb getModuleBreadcrumb(HttpServletRequest req, ModuleDescriptor module, String method, String message) {

		return new Breadcrumb(message + module.getName(), message + module.getName(), this.getFullAlias() + "/" + method + "/" + module.getModuleID());
	}

	private Breadcrumb getSectionBreadcrumb(HttpServletRequest req, SectionDescriptor section, String method, String message) {

		return new Breadcrumb(message + section.getName(), message + section.getName(), this.getFullAlias() + "/" + method + "/" + section.getSectionID());
	}

	@WebPublic(alias = "addbmodule")
	public SimpleForegroundModuleResponse addBackgroundModule(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, SQLException, IOException {

		SimpleSectionDescriptor simpleSectionDescriptor = null;

		if (uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (simpleSectionDescriptor = this.sectionDAO.getSection(Integer.parseInt(uriParser.get(2)), false)) != null) {

			ValidationException validationException = null;

			if (req.getMethod().equalsIgnoreCase("POST")) {

				try {
					SimpleBackgroundModuleDescriptor simpleModuleDescriptor = BACKGROUND_MODULE_DESCRIPTOR_POPULATOR.populate(req);
					simpleModuleDescriptor.setSectionID(simpleSectionDescriptor.getSectionID());

					this.log.info("User " + user + " adding background module " + simpleModuleDescriptor + " to section " + simpleSectionDescriptor);

					this.backgroundModuleDAO.add(simpleModuleDescriptor);

					res.sendRedirect(this.getModuleURI(req));
					return null;

				} catch (ValidationException e) {
					validationException = e;
				}
			}

			Document doc = this.createDocument(req, uriParser);
			Element addModuleElement = doc.createElement("addBackgroundModule");
			doc.getFirstChild().appendChild(addModuleElement);

			addModuleElement.appendChild(simpleSectionDescriptor.toXML(doc));

			addModuleElement.appendChild(this.getDataSources(doc));
			addModuleElement.appendChild(getPathTypes(doc));

			if (validationException != null) {
				addModuleElement.appendChild(validationException.toXML(doc));
				addModuleElement.appendChild(RequestUtils.getRequestParameters(req, doc));
			}

			return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb(), new Breadcrumb(this.addBackgroundModuleBreadCrumbText, this.addBackgroundModuleBreadCrumbText, getFullAlias() + "/addModule/" + simpleSectionDescriptor.getSectionID()));
		} else {
			throw new URINotFoundException(uriParser);
		}
	}

	@WebPublic(alias = "updatebmodule")
	public SimpleForegroundModuleResponse updateBackgroundModule(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, SQLException, IOException {

		SimpleBackgroundModuleDescriptor simpleModuleDescriptor = null;

		if (uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (simpleModuleDescriptor = this.backgroundModuleDAO.getModule(Integer.parseInt(uriParser.get(2)))) != null) {

			ValidationException validationException = null;

			if (req.getMethod().equalsIgnoreCase("POST")) {

				// Populate module descriptor
				List<ValidationError> validationErrors = new ArrayList<ValidationError>();

				try {
					simpleModuleDescriptor = BACKGROUND_MODULE_DESCRIPTOR_POPULATOR.populate(simpleModuleDescriptor, req);
				} catch (ValidationException e) {
					validationErrors.addAll(e.getErrors());
				}

				// Check module specific settings
				// TODO handle runtime exceptions...
				BackgroundModule moduleInstance = this.getBackgroundModuleInstance(simpleModuleDescriptor);

				HashMap<String, List<String>> settingValues = null;

				if (moduleInstance != null) {

					settingValues = parseModuleSettings(moduleInstance.getSettings(), req, validationErrors);
				}

				if (validationErrors.isEmpty()) {
					this.log.info("User " + user + " updating background module " + simpleModuleDescriptor);

					//Only update module settings if the module is started
					if (moduleInstance != null) {
						simpleModuleDescriptor.setMutableSettingHandler(new SimpleSettingHandler(settingValues));
					}

					this.backgroundModuleDAO.update(simpleModuleDescriptor);

					this.systemInterface.getEventHandler().sendEvent(SimpleBackgroundModuleDescriptor.class, new CRUDEvent<SimpleBackgroundModuleDescriptor>(CRUDAction.UPDATE, simpleModuleDescriptor), EventTarget.ALL);

					// Check if the module is cached
					SectionInterface sectionInterface = systemInterface.getSectionInterface(simpleModuleDescriptor.getSectionID());

					if (sectionInterface != null && sectionInterface.getBackgroundModuleCache().isCached(simpleModuleDescriptor)) {

						// Module is cached update it
						try {
							sectionInterface.getBackgroundModuleCache().update(simpleModuleDescriptor);
						} catch (Exception e) {
							this.log.error("Error updating background module " + simpleModuleDescriptor + " in section " + sectionInterface.getSectionDescriptor() + " while updating module requested by user " + user, e);
						}
					}

					res.sendRedirect(this.getModuleURI(req));
					return null;
				} else {
					validationException = new ValidationException(validationErrors);
				}

			}

			Document doc = this.createDocument(req, uriParser);
			Element updateModuleElement = doc.createElement("updateBackgroundModule");
			doc.getFirstChild().appendChild(updateModuleElement);

			BackgroundModule moduleInstance = this.getBackgroundModuleInstance(simpleModuleDescriptor);

			// check if the module is cached
			if (moduleInstance != null) {
				updateModuleElement.setAttribute("started", "true");

				// Get module specific settings
				List<? extends SettingDescriptor> moduleSettings = moduleInstance.getSettings();

				if (moduleSettings != null && !moduleSettings.isEmpty()) {

					Element moduleSettingDescriptorsElement = doc.createElement("moduleSettingDescriptors");
					updateModuleElement.appendChild(moduleSettingDescriptorsElement);

					for (SettingDescriptor settingDescriptor : moduleSettings) {
						moduleSettingDescriptorsElement.appendChild(settingDescriptor.toXML(doc));

						rewriteURLs(simpleModuleDescriptor, settingDescriptor, req);
					}
				}
			}

			updateModuleElement.appendChild(simpleModuleDescriptor.toXML(doc, true, false));

			updateModuleElement.appendChild(this.getDataSources(doc));
			updateModuleElement.appendChild(getPathTypes(doc));

			if (validationException != null) {
				updateModuleElement.appendChild(validationException.toXML(doc));
				updateModuleElement.appendChild(RequestUtils.getRequestParameters(req, doc));
			}

			AccessUtils.appendAllowedGroupsAndUsers(doc, updateModuleElement, simpleModuleDescriptor, systemInterface.getUserHandler(), systemInterface.getGroupHandler());

			return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb(), getModuleBreadcrumb(req, simpleModuleDescriptor, "updateModule", updateBackgroundModuleBreadCrumbText));
		} else {
			throw new URINotFoundException(uriParser);
		}
	}

	@WebPublic(alias = "deletebmodule")
	public SimpleForegroundModuleResponse deleteBackgroundModule(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws SQLException, URINotFoundException, IOException {

		SimpleBackgroundModuleDescriptor simpleModuleDescriptor = null;

		if (uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (simpleModuleDescriptor = this.backgroundModuleDAO.getModule(Integer.parseInt(uriParser.get(2)))) != null) {

			this.log.info("User " + user + " deleting background module " + simpleModuleDescriptor);

			// Get the sectioninterface of the section containing the module
			SectionInterface sectionInterface = systemInterface.getSectionInterface(simpleModuleDescriptor.getSectionID());

			// Check if the module is cached
			if (sectionInterface != null && sectionInterface.getBackgroundModuleCache().isCached(simpleModuleDescriptor)) {

				// Module is cached unload it
				try {
					sectionInterface.getBackgroundModuleCache().unload(simpleModuleDescriptor);
				} catch (Exception e) {
					this.log.error("Error unloading background module " + simpleModuleDescriptor + " from section " + sectionInterface.getSectionDescriptor() + " while deleting module requested by user " + user);
				}
			}

			// Delete section from database
			this.backgroundModuleDAO.delete(simpleModuleDescriptor);

			res.sendRedirect(this.getModuleURI(req));

			return null;

		} else {
			throw new URINotFoundException(uriParser);
		}
	}

	@WebPublic(alias = "movebmodule")
	public SimpleForegroundModuleResponse moveBackgroundModule(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws SQLException, URINotFoundException, IOException {

		SimpleBackgroundModuleDescriptor module = null;

		if ((uriParser.size() == 3 || uriParser.size() == 4) && NumberUtils.isInt(uriParser.get(2)) && (module = this.backgroundModuleDAO.getModule(Integer.parseInt(uriParser.get(2)))) != null) {

			if (uriParser.size() == 3) {

				// Show section tree
				Document doc = this.createDocument(req, uriParser);
				Element moveModuleElement = doc.createElement("moveBackgroundModule");
				doc.getFirstChild().appendChild(moveModuleElement);

				moveModuleElement.appendChild(module.toXML(doc));

				Element sectionsElement = doc.createElement("sections");
				moveModuleElement.appendChild(sectionsElement);

				this.appendSection(sectionsElement, doc, this.sectionDAO.getRootSection(true), false);

				return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb(), getModuleBreadcrumb(req, module, "moveModule", moveBackgroundModuleBreadCrumbText));
			} else {

				SimpleSectionDescriptor simpleSectionDescriptor = null;

				if (NumberUtils.isInt(uriParser.get(3)) && (simpleSectionDescriptor = this.sectionDAO.getSection(Integer.parseInt(uriParser.get(3)), true)) != null) {

					if (module.getSectionID() == simpleSectionDescriptor.getSectionID()) {

						this.log.info("User " + user + " trying to move background module " + module + " to the section it already belongs to, ignoring move");

						res.sendRedirect(this.getModuleURI(req));

						return null;

					} else {

						// TODO check alias

						this.log.info("User " + user + " moving background module " + module + " to section " + simpleSectionDescriptor);

						// Update module in database
						Integer oldSectionID = module.getSectionID();
						module.setSectionID(simpleSectionDescriptor.getSectionID());
						this.backgroundModuleDAO.update(module);

						// Check if the section the that the module belonged to is cached
						SectionInterface oldSectionInterface = systemInterface.getSectionInterface(oldSectionID);

						boolean enabled = false;

						if (oldSectionInterface != null && oldSectionInterface.getBackgroundModuleCache().isCached(module)) {

							enabled = true;

							try {
								oldSectionInterface.getBackgroundModuleCache().unload(module);
							} catch (Exception e) {
								this.log.error("Error unloading background module " + module + " from section " + oldSectionInterface.getSectionDescriptor() + " while moving module to section " + simpleSectionDescriptor + " by user " + user, e);
							}
						}

						// If the module was enabled and the reciving section is cached then cache the module there
						if (enabled) {
							SectionInterface newSectionInterface = systemInterface.getSectionInterface(module.getSectionID());

							if (newSectionInterface != null) {
								try {
									newSectionInterface.getBackgroundModuleCache().cache(module);
								} catch (Exception e) {
									this.log.error("Error caching background module " + module + " in section " + simpleSectionDescriptor + " while moving module from section " + oldSectionInterface.getSectionDescriptor() + " by user " + user, e);
								}
							}
						}

						res.sendRedirect(this.getModuleURI(req));

						return null;
					}
				}
			}
		}

		throw new URINotFoundException(uriParser);
	}

	@WebPublic(alias = "copybmodule")
	public SimpleForegroundModuleResponse copyBackgroundModule(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws SQLException, URINotFoundException, IOException {

		SimpleBackgroundModuleDescriptor module = null;

		if ((uriParser.size() == 3 || uriParser.size() == 4) && NumberUtils.isInt(uriParser.get(2)) && (module = this.backgroundModuleDAO.getModule(Integer.parseInt(uriParser.get(2)))) != null) {

			if (uriParser.size() == 3) {

				// Show section tree
				Document doc = this.createDocument(req, uriParser);
				Element copyModuleElement = doc.createElement("copyBackgroundModule");
				doc.getFirstChild().appendChild(copyModuleElement);

				copyModuleElement.appendChild(module.toXML(doc));

				Element sectionsElement = doc.createElement("sections");
				copyModuleElement.appendChild(sectionsElement);

				this.appendSection(sectionsElement, doc, this.sectionDAO.getRootSection(true), false);

				return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb(), getModuleBreadcrumb(req, module, "copyModule", copyBackgroundModuleBreadCrumbText));
			} else {

				SimpleSectionDescriptor simpleSectionDescriptor = null;

				if (NumberUtils.isInt(uriParser.get(3)) && (simpleSectionDescriptor = this.sectionDAO.getSection(Integer.parseInt(uriParser.get(3)), true)) != null) {

					this.log.info("User " + user + " copying background module " + module + " to section " + simpleSectionDescriptor);

					// Clear moduleID
					module.setModuleID(null);

					// Set new sectionID
					module.setSectionID(simpleSectionDescriptor.getSectionID());

					// Add module into database
					this.backgroundModuleDAO.add(module);

					res.sendRedirect(this.getModuleURI(req));

					return null;
				}
			}
		}

		throw new URINotFoundException(uriParser);
	}

	@WebPublic(alias = "downloadbmodule")
	public SimpleForegroundModuleResponse downloadBackgroundModuleDescriptor(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, SQLException, IOException, TransformerFactoryConfigurationError, TransformerException {

		SimpleBackgroundModuleDescriptor simpleModuleDescriptor = null;

		if (uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (simpleModuleDescriptor = this.backgroundModuleDAO.getModule(Integer.parseInt(uriParser.get(2)))) != null) {

			log.info("User " + user + " downloading module descriptor for background module " + simpleModuleDescriptor);

			sendModuleDescriptor(simpleModuleDescriptor, "bgmodule", res, user);

			return null;
		}

		throw new URINotFoundException(uriParser);
	}

	@WebPublic(alias = "downloadfmodule")
	public SimpleForegroundModuleResponse downloadForegroundModuleDescriptor(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, SQLException, IOException, TransformerFactoryConfigurationError, TransformerException {

		SimpleForegroundModuleDescriptor simpleModuleDescriptor = null;

		if (uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (simpleModuleDescriptor = this.foregroundModuleDAO.getModule(Integer.parseInt(uriParser.get(2)))) != null) {

			log.info("User " + user + " downloading module descriptor for foreground module " + simpleModuleDescriptor);

			sendModuleDescriptor(simpleModuleDescriptor, "fgmodule", res, user);

			return null;
		}

		throw new URINotFoundException(uriParser);
	}

	@WebPublic(alias = "downloadfiltermodule")
	public SimpleForegroundModuleResponse downloadFilterModuleDescriptor(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, SQLException, IOException, TransformerFactoryConfigurationError, TransformerException {

		SimpleFilterModuleDescriptor simpleModuleDescriptor = null;

		if (uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (simpleModuleDescriptor = this.filterModuleDAO.getModule(Integer.parseInt(uriParser.get(2)))) != null) {

			log.info("User " + user + " downloading module descriptor for filter module " + simpleModuleDescriptor);

			sendModuleDescriptor(simpleModuleDescriptor, "flmodule", res, user);

			return null;
		}

		throw new URINotFoundException(uriParser);
	}

	private void sendModuleDescriptor(BaseModuleDescriptor moduleDescriptor, String fileSuffix, HttpServletResponse res, User user) throws TransformerFactoryConfigurationError, TransformerException, IOException {

		res.setCharacterEncoding(systemInterface.getEncoding());
		res.setContentType("text/xml");
		res.setHeader("Content-Disposition", "attachment;filename=\"" + FileUtils.toValidHttpFilename(moduleDescriptor.getName()) + "." + fileSuffix + "\"");

		Document doc = XMLUtils.createDomDocument();

		doc.appendChild(moduleDescriptor.toXML(doc, true, true));

		XMLUtils.writeXML(doc, res.getOutputStream(), true, systemInterface.getEncoding());
	}

	@WebPublic(alias = "addfiltermodule")
	public SimpleForegroundModuleResponse addFilterModule(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, SQLException, IOException {

		ValidationException validationException = null;

		if (req.getMethod().equalsIgnoreCase("POST")) {

			try {
				SimpleFilterModuleDescriptor simpleModuleDescriptor = FILTER_MODULE_DESCRIPTOR_POPULATOR.populate(req);

				this.log.info("User " + user + " adding filter module " + simpleModuleDescriptor);

				this.filterModuleDAO.add(simpleModuleDescriptor);

				res.sendRedirect(this.getModuleURI(req));
				return null;

			} catch (ValidationException e) {
				validationException = e;
			}
		}

		Document doc = this.createDocument(req, uriParser);
		Element addModuleElement = doc.createElement("addFilterModule");
		doc.getFirstChild().appendChild(addModuleElement);

		addModuleElement.appendChild(this.getDataSources(doc));
		addModuleElement.appendChild(getPathTypes(doc));

		if (validationException != null) {
			addModuleElement.appendChild(validationException.toXML(doc));
			addModuleElement.appendChild(RequestUtils.getRequestParameters(req, doc));
		}

		return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb(), new Breadcrumb(this.addFilterModuleBreadCrumbText, this.addFilterModuleBreadCrumbText, getFullAlias() + "/addfiltermodule/"));
	}

	@WebPublic(alias = "updatefiltermodule")
	public SimpleForegroundModuleResponse updateFilterModule(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, SQLException, IOException {

		SimpleFilterModuleDescriptor simpleModuleDescriptor = null;

		if (uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (simpleModuleDescriptor = this.filterModuleDAO.getModule(Integer.parseInt(uriParser.get(2)))) != null) {

			ValidationException validationException = null;

			if (req.getMethod().equalsIgnoreCase("POST")) {

				// Populate module descriptor
				List<ValidationError> validationErrors = new ArrayList<ValidationError>();

				try {
					simpleModuleDescriptor = FILTER_MODULE_DESCRIPTOR_POPULATOR.populate(simpleModuleDescriptor, req);

				} catch (ValidationException e) {
					validationErrors.addAll(e.getErrors());
				}

				// Check module specific settings
				// TODO handle runtime exceptions...
				FilterModule moduleInstance = systemInterface.getFilterModuleCache().getModule(simpleModuleDescriptor);

				HashMap<String, List<String>> settingValues = null;

				if (moduleInstance != null) {

					settingValues = parseModuleSettings(moduleInstance.getSettings(), req, validationErrors);
				}

				if (validationErrors.isEmpty()) {
					this.log.info("User " + user + " updating filter module " + simpleModuleDescriptor);

					//Only update module settings if the module is started
					if (moduleInstance != null) {
						simpleModuleDescriptor.setMutableSettingHandler(new SimpleSettingHandler(settingValues));
					}

					this.filterModuleDAO.update(simpleModuleDescriptor);

					// Check if the module is cached
					if (systemInterface.getFilterModuleCache().isCached(simpleModuleDescriptor)) {

						// Module is cached update it
						try {
							systemInterface.getFilterModuleCache().update(simpleModuleDescriptor);
						} catch (Exception e) {
							this.log.error("Error updating filter module while updating module requested by user " + user, e);
						}
					}

					res.sendRedirect(this.getModuleURI(req));
					return null;
				} else {
					validationException = new ValidationException(validationErrors);
				}

			}

			Document doc = this.createDocument(req, uriParser);
			Element updateModuleElement = doc.createElement("updateFilterModule");
			doc.getFirstChild().appendChild(updateModuleElement);

			FilterModule moduleInstance = systemInterface.getFilterModuleCache().getModule(simpleModuleDescriptor);

			// check if the module is cached
			if (moduleInstance != null) {
				updateModuleElement.setAttribute("started", "true");

				// Get module specific settings
				List<? extends SettingDescriptor> moduleSettings = moduleInstance.getSettings();

				if (moduleSettings != null && !moduleSettings.isEmpty()) {

					Element moduleSettingDescriptorsElement = doc.createElement("moduleSettingDescriptors");
					updateModuleElement.appendChild(moduleSettingDescriptorsElement);

					for (SettingDescriptor settingDescriptor : moduleSettings) {
						moduleSettingDescriptorsElement.appendChild(settingDescriptor.toXML(doc));

						rewriteURLs(simpleModuleDescriptor, settingDescriptor, req);
					}
				}
			}

			updateModuleElement.appendChild(simpleModuleDescriptor.toXML(doc, true, false));

			updateModuleElement.appendChild(this.getDataSources(doc));
			updateModuleElement.appendChild(getPathTypes(doc));

			if (validationException != null) {
				updateModuleElement.appendChild(validationException.toXML(doc));
				updateModuleElement.appendChild(RequestUtils.getRequestParameters(req, doc));
			}

			AccessUtils.appendAllowedGroupsAndUsers(doc, updateModuleElement, simpleModuleDescriptor, systemInterface.getUserHandler(), systemInterface.getGroupHandler());

			return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb(), getModuleBreadcrumb(req, simpleModuleDescriptor, "updatefiltermodule", updateFilterModuleBreadCrumbText));
		} else {
			throw new URINotFoundException(uriParser);
		}
	}

	@WebPublic(alias = "deletefiltermodule")
	public SimpleForegroundModuleResponse deleteFilterModule(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws SQLException, URINotFoundException, IOException {

		SimpleFilterModuleDescriptor simpleModuleDescriptor = null;

		if (uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (simpleModuleDescriptor = this.filterModuleDAO.getModule(Integer.parseInt(uriParser.get(2)))) != null) {

			this.log.info("User " + user + " deleting filter module " + simpleModuleDescriptor);

			// Check if the module is cached
			if (systemInterface.getFilterModuleCache().isCached(simpleModuleDescriptor)) {

				// Module is cached unload it
				try {
					systemInterface.getFilterModuleCache().unload(simpleModuleDescriptor);
				} catch (Exception e) {
					this.log.error("Error unloading filter module " + simpleModuleDescriptor + " while deleting module requested by user " + user);
				}
			}

			// Delete section from database
			this.filterModuleDAO.delete(simpleModuleDescriptor);

			res.sendRedirect(this.getModuleURI(req));

			return null;

		} else {
			throw new URINotFoundException(uriParser);
		}
	}

	@WebPublic(alias = "copyfiltermodule")
	public SimpleForegroundModuleResponse copyFilterModule(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws SQLException, URINotFoundException, IOException {

		SimpleFilterModuleDescriptor module = null;

		if ((uriParser.size() == 3 || uriParser.size() == 4) && NumberUtils.isInt(uriParser.get(2)) && (module = this.filterModuleDAO.getModule(Integer.parseInt(uriParser.get(2)))) != null) {

			this.log.info("User " + user + " copying filter module " + module);

			// Clear moduleID
			module.setModuleID(null);

			// Add module into database
			this.filterModuleDAO.add(module);

			res.sendRedirect(this.getModuleURI(req));

			return null;
		}

		throw new URINotFoundException(uriParser);
	}

	@WebPublic(alias = "addfmodule")
	public SimpleForegroundModuleResponse addForegroundModule(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, SQLException, IOException {

		SimpleSectionDescriptor simpleSectionDescriptor = null;

		if (uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (simpleSectionDescriptor = this.sectionDAO.getSection(Integer.parseInt(uriParser.get(2)), false)) != null) {

			ValidationException validationException = null;

			if (req.getMethod().equalsIgnoreCase("POST")) {

				try {
					SimpleForegroundModuleDescriptor simpleModuleDescriptor = FOREGROUND_MODULE_DESCRIPTOR_POPULATOR.populate(req);
					simpleModuleDescriptor.setSectionID(simpleSectionDescriptor.getSectionID());

					SimpleForegroundModuleDescriptor aliasMatch = this.foregroundModuleDAO.getModule(simpleSectionDescriptor.getSectionID(), simpleModuleDescriptor.getAlias());

					if (aliasMatch != null) {
						throw new ValidationException(new ValidationError("alias", ValidationErrorType.Other, "duplicateModuleAlias"));
					}

					this.log.info("User " + user + " adding foreground module " + simpleModuleDescriptor + " to section " + simpleSectionDescriptor);

					this.foregroundModuleDAO.add(simpleModuleDescriptor);

					res.sendRedirect(this.getModuleURI(req));
					return null;

				} catch (ValidationException e) {
					validationException = e;
				}
			}

			Document doc = this.createDocument(req, uriParser);
			Element addModuleElement = doc.createElement("addForegroundModule");
			doc.getFirstChild().appendChild(addModuleElement);

			addModuleElement.appendChild(simpleSectionDescriptor.toXML(doc));

			addModuleElement.appendChild(this.getDataSources(doc));
			addModuleElement.appendChild(getPathTypes(doc));
			addModuleElement.appendChild(HTTPProtocol.getProtocols(doc));

			if (validationException != null) {
				addModuleElement.appendChild(validationException.toXML(doc));
				addModuleElement.appendChild(RequestUtils.getRequestParameters(req, doc));
			}

			return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb(), new Breadcrumb(this.addForegroundModuleBreadCrumbText, this.addForegroundModuleBreadCrumbText, getFullAlias() + "/addModule/" + simpleSectionDescriptor.getSectionID()));
		} else {
			throw new URINotFoundException(uriParser);
		}
	}

	@WebPublic(alias = "updatefmodule")
	public SimpleForegroundModuleResponse updateForegroundModule(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, SQLException, IOException {

		SimpleForegroundModuleDescriptor simpleModuleDescriptor = null;

		if (uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (simpleModuleDescriptor = this.foregroundModuleDAO.getModule(Integer.parseInt(uriParser.get(2)))) != null) {

			ValidationException validationException = null;

			if (req.getMethod().equalsIgnoreCase("POST")) {

				// Populate module descriptor
				List<ValidationError> validationErrors = new ArrayList<ValidationError>();

				try {
					simpleModuleDescriptor = FOREGROUND_MODULE_DESCRIPTOR_POPULATOR.populate(simpleModuleDescriptor, req);
				} catch (ValidationException e) {
					validationErrors.addAll(e.getErrors());
				}

				// Check for alias dupes
				SimpleForegroundModuleDescriptor aliasMatch = this.foregroundModuleDAO.getModule(simpleModuleDescriptor.getSectionID(), simpleModuleDescriptor.getAlias());

				if (aliasMatch != null && !aliasMatch.getModuleID().equals(simpleModuleDescriptor.getModuleID())) {
					validationErrors.add(new ValidationError("alias", ValidationErrorType.Other, "duplicateModuleAlias"));
				}

				// Check module specific settings
				// TODO handle runtime exceptions...
				ForegroundModule moduleInstance = this.getForegroundModuleInstance(simpleModuleDescriptor);

				HashMap<String, List<String>> settingValues = null;

				if (moduleInstance != null) {

					settingValues = parseModuleSettings(moduleInstance.getSettings(), req, validationErrors);
				}

				if (validationErrors.isEmpty()) {
					this.log.info("User " + user + " updating foreground module " + simpleModuleDescriptor);

					//Only update module settings if the module is started
					if (moduleInstance != null) {
						simpleModuleDescriptor.setMutableSettingHandler(new SimpleSettingHandler(settingValues));
					}

					this.foregroundModuleDAO.update(simpleModuleDescriptor);

					this.systemInterface.getEventHandler().sendEvent(SimpleForegroundModuleDescriptor.class, new CRUDEvent<SimpleForegroundModuleDescriptor>(CRUDAction.UPDATE, simpleModuleDescriptor), EventTarget.ALL);

					// Check if the module is cached
					SectionInterface sectionInterface = systemInterface.getSectionInterface(simpleModuleDescriptor.getSectionID());

					if (sectionInterface != null && sectionInterface.getForegroundModuleCache().isCached(simpleModuleDescriptor)) {

						// Module is cached update it
						try {
							sectionInterface.getForegroundModuleCache().update(simpleModuleDescriptor);
						} catch (Exception e) {
							this.log.error("Error updating foreground module " + simpleModuleDescriptor + " in section " + sectionInterface.getSectionDescriptor() + " while updating module requested by user " + user, e);
						}
					}

					res.sendRedirect(this.getModuleURI(req));
					return null;
				} else {
					validationException = new ValidationException(validationErrors);
				}

			}

			Document doc = this.createDocument(req, uriParser);
			Element updateModuleElement = doc.createElement("updateForegroundModule");
			doc.getFirstChild().appendChild(updateModuleElement);

			ForegroundModule moduleInstance = this.getForegroundModuleInstance(simpleModuleDescriptor);

			// check if the module is cached
			if (moduleInstance != null) {
				updateModuleElement.setAttribute("started", "true");

				// Get module specific settings
				List<? extends SettingDescriptor> moduleSettings = moduleInstance.getSettings();

				if (moduleSettings != null && !moduleSettings.isEmpty()) {

					Element moduleSettingDescriptorsElement = doc.createElement("moduleSettingDescriptors");
					updateModuleElement.appendChild(moduleSettingDescriptorsElement);

					for (SettingDescriptor settingDescriptor : moduleSettings) {
						moduleSettingDescriptorsElement.appendChild(settingDescriptor.toXML(doc));

						rewriteURLs(simpleModuleDescriptor, settingDescriptor, req);
					}
				}
			}

			updateModuleElement.appendChild(simpleModuleDescriptor.toXML(doc, true, false));

			updateModuleElement.appendChild(this.getDataSources(doc));
			updateModuleElement.appendChild(getPathTypes(doc));
			updateModuleElement.appendChild(HTTPProtocol.getProtocols(doc));

			if (validationException != null) {
				updateModuleElement.appendChild(validationException.toXML(doc));
				updateModuleElement.appendChild(RequestUtils.getRequestParameters(req, doc));
			}

			AccessUtils.appendAllowedGroupsAndUsers(doc, updateModuleElement, simpleModuleDescriptor, systemInterface.getUserHandler(), systemInterface.getGroupHandler());

			return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb(), getModuleBreadcrumb(req, simpleModuleDescriptor, "updateModule", updateForegroundModuleBreadCrumbText));
		} else {
			throw new URINotFoundException(uriParser);
		}
	}

	public static void rewriteURLs(ModuleDescriptor moduleDescriptor, SettingDescriptor settingDescriptor, HttpServletRequest req) {

		if (settingDescriptor.getDisplayType() == DisplayType.HTML_EDITOR) {

			String value = moduleDescriptor.getMutableSettingHandler().getString(settingDescriptor.getId());

			if (value != null) {

				value = URLRewriter.setAbsoluteLinkUrls(value, req);

				moduleDescriptor.getMutableSettingHandler().setSetting(settingDescriptor.getId(), value);
			}
		}
	}

	@WebPublic(alias = "deletefmodule")
	public SimpleForegroundModuleResponse deleteForegroundModule(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws SQLException, URINotFoundException, IOException {

		SimpleForegroundModuleDescriptor simpleModuleDescriptor = null;

		if (uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (simpleModuleDescriptor = this.foregroundModuleDAO.getModule(Integer.parseInt(uriParser.get(2)))) != null) {

			this.log.info("User " + user + " deleting foreground module " + simpleModuleDescriptor);

			// Get the sectioninterface of the section containing the module
			SectionInterface sectionInterface = systemInterface.getSectionInterface(simpleModuleDescriptor.getSectionID());

			// Check if the module is cached
			if (sectionInterface != null && sectionInterface.getForegroundModuleCache().isCached(simpleModuleDescriptor)) {

				// Module is cached unload it
				try {
					sectionInterface.getForegroundModuleCache().unload(simpleModuleDescriptor);
				} catch (Exception e) {
					this.log.error("Error unloading foreground module " + simpleModuleDescriptor + " from section " + sectionInterface.getSectionDescriptor() + " while deleting module requested by user " + user);
				}
			}

			// Delete section from database
			this.foregroundModuleDAO.delete(simpleModuleDescriptor);

			res.sendRedirect(this.getModuleURI(req));

			return null;

		} else {
			throw new URINotFoundException(uriParser);
		}
	}

	@WebPublic(alias = "movefmodule")
	public SimpleForegroundModuleResponse moveForegroundModule(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws SQLException, URINotFoundException, IOException {

		SimpleForegroundModuleDescriptor module = null;

		if ((uriParser.size() == 3 || uriParser.size() == 4) && NumberUtils.isInt(uriParser.get(2)) && (module = this.foregroundModuleDAO.getModule(Integer.parseInt(uriParser.get(2)))) != null) {

			if (uriParser.size() == 3) {

				// Show section tree
				Document doc = this.createDocument(req, uriParser);
				Element moveModuleElement = doc.createElement("moveForegroundModule");
				doc.getFirstChild().appendChild(moveModuleElement);

				moveModuleElement.appendChild(module.toXML(doc));

				Element sectionsElement = doc.createElement("sections");
				moveModuleElement.appendChild(sectionsElement);

				this.appendSection(sectionsElement, doc, this.sectionDAO.getRootSection(true), false);

				return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb(), getModuleBreadcrumb(req, module, "moveModule", moveForegroundModuleBreadCrumbText));
			} else {

				SimpleSectionDescriptor simpleSectionDescriptor = null;

				if (NumberUtils.isInt(uriParser.get(3)) && (simpleSectionDescriptor = this.sectionDAO.getSection(Integer.parseInt(uriParser.get(3)), true)) != null) {

					if (module.getSectionID() == simpleSectionDescriptor.getSectionID()) {

						this.log.info("User " + user + " trying to move foreground module " + module + " to the section it already belongs to, ignoring move");

						res.sendRedirect(this.getModuleURI(req));

						return null;

					} else {

						// TODO check alias

						this.log.info("User " + user + " moving foreground module " + module + " to section " + simpleSectionDescriptor);

						// Update module in database
						Integer oldSectionID = module.getSectionID();
						module.setSectionID(simpleSectionDescriptor.getSectionID());
						this.foregroundModuleDAO.update(module);

						// Check if the section the that the module belonged to is cached
						SectionInterface oldSectionInterface = systemInterface.getSectionInterface(oldSectionID);

						boolean enabled = false;

						if (oldSectionInterface != null && oldSectionInterface.getForegroundModuleCache().isCached(module)) {

							enabled = true;

							try {
								oldSectionInterface.getForegroundModuleCache().unload(module);
							} catch (Exception e) {
								this.log.error("Error unloading foreground module " + module + " from section " + oldSectionInterface.getSectionDescriptor() + " while moving module to section " + simpleSectionDescriptor + " by user " + user, e);
							}
						}

						// If the module was enabled and the reciving section is cached then cache the module there
						if (enabled) {
							SectionInterface newSectionInterface = systemInterface.getSectionInterface(module.getSectionID());

							if (newSectionInterface != null) {
								try {
									newSectionInterface.getForegroundModuleCache().cache(module);
								} catch (Exception e) {
									this.log.error("Error caching foreground module " + module + " in section " + simpleSectionDescriptor + " while moving module from section " + oldSectionInterface.getSectionDescriptor() + " by user " + user, e);
								}
							}
						}

						res.sendRedirect(this.getModuleURI(req));

						return null;
					}
				}
			}
		}

		throw new URINotFoundException(uriParser);
	}

	@WebPublic(alias = "copyfmodule")
	public SimpleForegroundModuleResponse copyForegroundModule(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws SQLException, URINotFoundException, IOException {

		SimpleForegroundModuleDescriptor module = null;

		if ((uriParser.size() == 3 || uriParser.size() == 4) && NumberUtils.isInt(uriParser.get(2)) && (module = this.foregroundModuleDAO.getModule(Integer.parseInt(uriParser.get(2)))) != null) {

			if (uriParser.size() == 3) {

				// Show section tree
				Document doc = this.createDocument(req, uriParser);
				Element copyModuleElement = doc.createElement("copyForegroundModule");
				doc.getFirstChild().appendChild(copyModuleElement);

				copyModuleElement.appendChild(module.toXML(doc));

				Element sectionsElement = doc.createElement("sections");
				copyModuleElement.appendChild(sectionsElement);

				this.appendSection(sectionsElement, doc, this.sectionDAO.getRootSection(true), false);

				return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb(), getModuleBreadcrumb(req, module, "copyModule", copyForegroundModuleBreadCrumbText));
			} else {

				SimpleSectionDescriptor simpleSectionDescriptor = null;

				if (NumberUtils.isInt(uriParser.get(3)) && (simpleSectionDescriptor = this.sectionDAO.getSection(Integer.parseInt(uriParser.get(3)), true)) != null) {

					this.log.info("User " + user + " copying foreground module " + module + " to section " + simpleSectionDescriptor);

					// Clear moduleID
					module.setModuleID(null);

					// Set new sectionID
					module.setSectionID(simpleSectionDescriptor.getSectionID());

					// Check if alias already exists
					if (this.foregroundModuleDAO.getModule(module.getSectionID(), module.getAlias()) != null) {

						// Alias already exists add number after alias
						for (int copyNumber = 1; copyNumber < Integer.MAX_VALUE; copyNumber++) {

							if (this.foregroundModuleDAO.getModule(module.getSectionID(), module.getAlias() + copyNumber) == null) {
								module.setAlias(module.getAlias() + copyNumber);
								break;
							}
						}
					}

					// Add module into database
					this.foregroundModuleDAO.add(module);

					res.sendRedirect(this.getModuleURI(req));

					return null;
				}
			}
		}

		throw new URINotFoundException(uriParser);
	}

	public static HashMap<String, List<String>> parseModuleSettings(List<? extends SettingDescriptor> settings, HttpServletRequest req, List<ValidationError> validationErrors) {

		if (settings != null && !settings.isEmpty()) {

			HashMap<String, List<String>> settingValues = new HashMap<String, List<String>>();

			for (SettingDescriptor setting : settings) {

				if (setting.getDisplayType() == DisplayType.MULTILIST) {

					String[] values = req.getParameterValues("modulesetting." + setting.getId());

					if (values == null) {

						if (setting.isRequired()) {
							validationErrors.add(new ValidationError(setting.getName(), ValidationErrorType.RequiredField));
						}

						continue;
					}

					ArrayList<String> validValues = new ArrayList<String>();

					for (String value : values) {

						for (ValueDescriptor valueDescriptor : setting.getAllowedValues()) {

							if (valueDescriptor.getValue().equals(value)) {

								validValues.add(value);

								break;
							}
						}
					}

					if (validValues.isEmpty() && setting.isRequired()) {
						validationErrors.add(new ValidationError(setting.getName(), ValidationErrorType.InvalidFormat));
						continue;
					}

					settingValues.put(setting.getId(), validValues);

				} else if (setting.getDisplayType() == DisplayType.TEXTAREA && setting.isSplitOnLineBreak()) {

					String unsplitValue = req.getParameter("modulesetting." + setting.getId());

					if (StringUtils.isEmpty(unsplitValue)) {

						if (setting.isRequired()) {
							validationErrors.add(new ValidationError(setting.getName(), ValidationErrorType.RequiredField));
						}

						continue;
					}

					List<String> values = StringUtils.splitOnLineBreak(unsplitValue, false);

					if (values == null) {

						if (setting.isRequired()) {
							validationErrors.add(new ValidationError(setting.getName(), ValidationErrorType.RequiredField));
						}

						continue;
					}

					if (setting.getFormatValidator() != null) {

						for (String value : values) {

							if (!setting.getFormatValidator().validateFormat(value)) {
								validationErrors.add(new ValidationError(setting.getName(), ValidationErrorType.InvalidFormat));
								continue;
							}
						}
					}

					settingValues.put(setting.getId(), values);

				} else {

					String value = req.getParameter("modulesetting." + setting.getId());

					if (StringUtils.isEmpty(value)) {

						if (setting.isRequired() && setting.getDisplayType() != DisplayType.CHECKBOX) {

							validationErrors.add(new ValidationError(setting.getName(), ValidationErrorType.RequiredField));

							continue;

						} else if (setting.getDisplayType() == DisplayType.CHECKBOX) {

							value = "false";

						} else {
							continue;
						}

					} else if (setting.getAllowedValues() != null && !setting.getAllowedValues().isEmpty()) {

						boolean match = false;

						for (ValueDescriptor valueDescriptor : setting.getAllowedValues()) {

							if (valueDescriptor.getValue().equals(value)) {
								match = true;
								break;
							}
						}

						if (!match) {
							validationErrors.add(new ValidationError(setting.getName(), ValidationErrorType.InvalidFormat));
							continue;
						}

					} else if (setting.getFormatValidator() != null) {

						if (!setting.getFormatValidator().validateFormat(value)) {
							validationErrors.add(new ValidationError(setting.getName(), ValidationErrorType.InvalidFormat));
							continue;
						}
					}

					if (setting.getDisplayType() == DisplayType.HTML_EDITOR) {

						value = URLRewriter.removeAbsoluteLinkUrls(value, req);
					}

					//Don't save default values
					//if(setting.getDefaultValue() != null && setting.getDefaultValue().equals(value)){
					//
					//	continue;
					//}

					settingValues.put(setting.getId(), Collections.singletonList(value));
				}
			}

			return settingValues;
		}

		return null;
	}

	@WebPublic(alias = "startbmodule")
	public SimpleForegroundModuleResponse startBackgroundModule(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException, SQLException, URINotFoundException {

		SimpleBackgroundModuleDescriptor simpleModuleDescriptor = null;

		if (uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (simpleModuleDescriptor = this.backgroundModuleDAO.getModule(Integer.parseInt(uriParser.get(2)))) != null) {

			// Check if the section the module belongs to is loaded
			SectionInterface sectionInterface = systemInterface.getSectionInterface(simpleModuleDescriptor.getSectionID());

			if (sectionInterface == null) {

				this.log.info("User " + user + " tried to cache background module " + simpleModuleDescriptor + " in non-cached section " + this.sectionDAO.getSection(simpleModuleDescriptor.getSectionID(), false));

			} else if (sectionInterface.getBackgroundModuleCache().isCached(simpleModuleDescriptor)) {

				this.log.info("User " + user + " tried to cache background module " + simpleModuleDescriptor + " which is already cached in section " + sectionInterface.getSectionDescriptor());

			} else {
				try {
					this.log.info("User " + user + " caching background module " + simpleModuleDescriptor + " in section " + sectionInterface.getSectionDescriptor());
					sectionInterface.getBackgroundModuleCache().cache(simpleModuleDescriptor);
				} catch (Exception e) {
					this.log.error("Error caching background module " + simpleModuleDescriptor + " in section " + sectionInterface.getSectionDescriptor() + " requested by user " + user, e);
				}
			}

			res.sendRedirect(this.getModuleURI(req));

			return null;

		} else {
			throw new URINotFoundException(uriParser);
		}
	}

	@WebPublic(alias = "stopbmodule")
	public SimpleForegroundModuleResponse stopBackgroundModule(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, SQLException, IOException {

		SimpleBackgroundModuleDescriptor simpleModuleDescriptor = null;

		if (uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (simpleModuleDescriptor = this.backgroundModuleDAO.getModule(Integer.parseInt(uriParser.get(2)))) != null) {

			// Check if the section the module belongs to is loaded
			SectionInterface sectionInterface = systemInterface.getSectionInterface(simpleModuleDescriptor.getSectionID());

			if (sectionInterface == null) {

				this.log.info("User " + user + " tried to unload background module " + simpleModuleDescriptor + " in non-cached section " + this.sectionDAO.getSection(simpleModuleDescriptor.getSectionID(), false));

			} else if (!sectionInterface.getBackgroundModuleCache().isCached(simpleModuleDescriptor)) {

				this.log.info("User " + user + " tried to unload uncached background module " + simpleModuleDescriptor + " in section " + sectionInterface.getSectionDescriptor());

			} else {
				try {
					this.log.info("User " + user + " unloading background module " + simpleModuleDescriptor + " in section " + sectionInterface.getSectionDescriptor());
					sectionInterface.getBackgroundModuleCache().unload(simpleModuleDescriptor);
				} catch (Exception e) {
					this.log.error("Error unloading background module " + simpleModuleDescriptor + " in section " + sectionInterface.getSectionDescriptor() + " requested by user " + user);
				}
			}

			res.sendRedirect(this.getModuleURI(req));

			return null;

		} else {
			throw new URINotFoundException(uriParser);
		}
	}

	@WebPublic(alias = "startfiltermodule")
	public SimpleForegroundModuleResponse startFilterModule(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException, SQLException, URINotFoundException {

		SimpleFilterModuleDescriptor simpleModuleDescriptor = null;

		if (uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (simpleModuleDescriptor = this.filterModuleDAO.getModule(Integer.parseInt(uriParser.get(2)))) != null) {

			if (systemInterface.getFilterModuleCache().isCached(simpleModuleDescriptor)) {

				this.log.info("User " + user + " tried to cache filter module " + simpleModuleDescriptor + " which is already cached in section " + sectionInterface.getSectionDescriptor());

			} else {
				try {
					this.log.info("User " + user + " caching filter module " + simpleModuleDescriptor + " in section " + sectionInterface.getSectionDescriptor());
					systemInterface.getFilterModuleCache().cache(simpleModuleDescriptor);

				} catch (Exception e) {

					this.log.error("Error caching filter module " + simpleModuleDescriptor + " in section " + sectionInterface.getSectionDescriptor() + " requested by user " + user, e);
				}
			}

			res.sendRedirect(this.getModuleURI(req));

			return null;

		} else {
			throw new URINotFoundException(uriParser);
		}
	}

	@WebPublic(alias = "stopfiltermodule")
	public SimpleForegroundModuleResponse stopFilterModule(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, SQLException, IOException {

		SimpleFilterModuleDescriptor simpleModuleDescriptor = null;

		if (uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (simpleModuleDescriptor = this.filterModuleDAO.getModule(Integer.parseInt(uriParser.get(2)))) != null) {

			if (!systemInterface.getFilterModuleCache().isCached(simpleModuleDescriptor)) {

				this.log.info("User " + user + " tried to unload uncached filter module " + simpleModuleDescriptor + " in section " + sectionInterface.getSectionDescriptor());

			} else {
				try {
					this.log.info("User " + user + " unloading filter module " + simpleModuleDescriptor + " in section " + sectionInterface.getSectionDescriptor());
					systemInterface.getFilterModuleCache().unload(simpleModuleDescriptor);

				} catch (Exception e) {

					this.log.error("Error unloading filter module " + simpleModuleDescriptor + " in section " + sectionInterface.getSectionDescriptor() + " requested by user " + user);
				}
			}

			res.sendRedirect(this.getModuleURI(req));

			return null;

		} else {
			throw new URINotFoundException(uriParser);
		}
	}

	@WebPublic(alias = "startfmodule")
	public SimpleForegroundModuleResponse startForegroundModule(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException, SQLException, URINotFoundException {

		SimpleForegroundModuleDescriptor simpleModuleDescriptor = null;

		if (uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (simpleModuleDescriptor = this.foregroundModuleDAO.getModule(Integer.parseInt(uriParser.get(2)))) != null) {

			// Check if the section the module belongs to is loaded
			SectionInterface sectionInterface = systemInterface.getSectionInterface(simpleModuleDescriptor.getSectionID());

			if (sectionInterface == null) {

				this.log.info("User " + user + " tried to cache foreground module " + simpleModuleDescriptor + " in non-cached section " + this.sectionDAO.getSection(simpleModuleDescriptor.getSectionID(), false));

			} else if (sectionInterface.getForegroundModuleCache().isCached(simpleModuleDescriptor)) {

				this.log.info("User " + user + " tried to cache foreground module " + simpleModuleDescriptor + " which is already cached in section " + sectionInterface.getSectionDescriptor());

			} else {
				try {
					this.log.info("User " + user + " caching foreground module " + simpleModuleDescriptor + " in section " + sectionInterface.getSectionDescriptor());
					sectionInterface.getForegroundModuleCache().cache(simpleModuleDescriptor);
				} catch (Exception e) {
					this.log.error("Error caching foreground module " + simpleModuleDescriptor + " in section " + sectionInterface.getSectionDescriptor() + " requested by user " + user, e);
				}
			}

			res.sendRedirect(this.getModuleURI(req));

			return null;

		} else {
			throw new URINotFoundException(uriParser);
		}
	}

	@WebPublic(alias = "stopfmodule")
	public SimpleForegroundModuleResponse stopForegroundModule(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, SQLException, IOException {

		SimpleForegroundModuleDescriptor simpleModuleDescriptor = null;

		if (uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (simpleModuleDescriptor = this.foregroundModuleDAO.getModule(Integer.parseInt(uriParser.get(2)))) != null) {

			// Check if the section the module belongs to is loaded
			SectionInterface sectionInterface = systemInterface.getSectionInterface(simpleModuleDescriptor.getSectionID());

			if (sectionInterface == null) {

				this.log.info("User " + user + " tried to unload foreground module " + simpleModuleDescriptor + " in non-cached section " + this.sectionDAO.getSection(simpleModuleDescriptor.getSectionID(), false));

			} else if (!sectionInterface.getForegroundModuleCache().isCached(simpleModuleDescriptor)) {

				this.log.info("User " + user + " tried to unload uncached foreground module " + simpleModuleDescriptor + " in section " + sectionInterface.getSectionDescriptor());

			} else {
				try {
					this.log.info("User " + user + " unloading foreground module " + simpleModuleDescriptor + " in section " + sectionInterface.getSectionDescriptor());
					sectionInterface.getForegroundModuleCache().unload(simpleModuleDescriptor);
				} catch (Exception e) {
					this.log.error("Error unloading foreground module " + simpleModuleDescriptor + " in section " + sectionInterface.getSectionDescriptor() + " requested by user " + user);
				}
			}

			res.sendRedirect(this.getModuleURI(req));

			return null;

		} else {
			throw new URINotFoundException(uriParser);
		}
	}

	@WebPublic(alias = "stopvfmodule")
	public SimpleForegroundModuleResponse stopVirtualForegroundModule(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, SQLException, IOException {

		Integer sectionID;

		if (uriParser.size() == 4 && (sectionID = NumberUtils.toInt(uriParser.get(2))) != null) {

			// Check if the section the module belongs to is loaded
			SectionInterface sectionInterface = systemInterface.getSectionInterface(sectionID);

			Entry<ForegroundModuleDescriptor, ForegroundModule> moduleEntry;

			if (sectionInterface == null) {

				this.log.info("User " + user + " tried to unload a virtual foreground module in non-cached section " + this.sectionDAO.getSection(sectionID, false));

			} else if ((moduleEntry = sectionInterface.getForegroundModuleCache().getEntry(uriParser.get(3))) == null) {

				this.log.info("User " + user + " tried to unload unknown virtual foreground module with alias " + uriParser.get(3) + " in section " + sectionInterface.getSectionDescriptor());

			} else {
				try {
					this.log.info("User " + user + " unloading virtual foreground module " + moduleEntry.getKey() + " from section " + sectionInterface.getSectionDescriptor());
					sectionInterface.getForegroundModuleCache().unload(moduleEntry.getKey());
				} catch (Exception e) {
					this.log.error("Error unloading virtual foreground module " + moduleEntry.getKey() + " in section " + sectionInterface.getSectionDescriptor() + " requested by user " + user);
				}
			}

			res.sendRedirect(this.getModuleURI(req));

			return null;

		} else {
			throw new URINotFoundException(uriParser);
		}
	}

	@WebPublic
	public SimpleForegroundModuleResponse addSection(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, SQLException, IOException {

		SimpleSectionDescriptor parentSectionBean = null;

		if (uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (parentSectionBean = this.sectionDAO.getSection(Integer.parseInt(uriParser.get(2)), false)) != null) {

			ValidationException validationException = null;

			if (req.getMethod().equalsIgnoreCase("POST")) {

				try {
					SimpleSectionDescriptor newSectionBean = SECTION_DESCRIPTOR_POPULATOR.populate(req);
					newSectionBean.setParentSectionID(parentSectionBean.getSectionID());

					SimpleSectionDescriptor sectionMatch = this.sectionDAO.getSection(parentSectionBean.getSectionID(), newSectionBean.getAlias());

					if (sectionMatch != null) {
						throw new ValidationException(new ValidationError("alias", ValidationErrorType.Other, "duplicateSectionAlias"));
					}

					this.log.info("User " + user + " adding section " + newSectionBean + " to section " + parentSectionBean);

					this.sectionDAO.add(newSectionBean);

					res.sendRedirect(this.getModuleURI(req));
					return null;

				} catch (ValidationException e) {
					validationException = e;
				}
			}

			Document doc = this.createDocument(req, uriParser);
			Element addSectionElement = doc.createElement("addSection");
			doc.getFirstChild().appendChild(addSectionElement);

			addSectionElement.appendChild(parentSectionBean.toXML(doc));

			if (validationException != null) {
				addSectionElement.appendChild(validationException.toXML(doc));
				addSectionElement.appendChild(RequestUtils.getRequestParameters(req, doc));
			}

			addSectionElement.appendChild(HTTPProtocol.getProtocols(doc));

			return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb(), new Breadcrumb(this.addSectionBreadCrumbText, this.addSectionBreadCrumbText, getFullAlias() + "/addSection/" + parentSectionBean.getSectionID()));
		} else {
			throw new URINotFoundException(uriParser);
		}
	}

	@WebPublic
	public SimpleForegroundModuleResponse updateSection(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, SQLException, IOException {

		SimpleSectionDescriptor simpleSectionDescriptor = null;

		if (uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (simpleSectionDescriptor = this.sectionDAO.getSection(Integer.parseInt(uriParser.get(2)), true)) != null) {

			ValidationException validationException = null;

			if (req.getMethod().equalsIgnoreCase("POST")) {

				try {
					simpleSectionDescriptor = SECTION_DESCRIPTOR_POPULATOR.populate(simpleSectionDescriptor, req);

					SimpleSectionDescriptor sectionMatch = this.sectionDAO.getSection(simpleSectionDescriptor.getParentSectionID(), simpleSectionDescriptor.getAlias());

					if (sectionMatch != null && !sectionMatch.getSectionID().equals(simpleSectionDescriptor.getSectionID())) {
						throw new ValidationException(new ValidationError("alias", ValidationErrorType.Other, "duplicateSectionAlias"));
					}

					this.log.info("User " + user + " updating section " + simpleSectionDescriptor);

					this.sectionDAO.update(simpleSectionDescriptor);

					this.sectionDAO.getReverseFullAlias(simpleSectionDescriptor);

					// Check if the section is the root section
					if (simpleSectionDescriptor.getParentSectionID() == null) {

						// Section is root section, update root section in background using background thread to prevent thread locks
						RootSectionUpdater rootSectionUpdater = new RootSectionUpdater(this.sectionInterface.getSystemInterface().getRootSection(), simpleSectionDescriptor);
						rootSectionUpdater.setDaemon(true);
						rootSectionUpdater.start();

					} else {

						// Section is not root section, check if the section is cached
						SectionInterface sectionInterface = systemInterface.getSectionInterface(simpleSectionDescriptor.getParentSectionID());

						if (sectionInterface != null && sectionInterface.getSectionCache().isCached(simpleSectionDescriptor)) {

							// Section is cached, check if it is a parentsection to this module
							if (this.isParentSection(simpleSectionDescriptor.getSectionID())) {

								// The section is a parent section to this module, use background thread to avoid threadlock
								SectionUpdater sectionUpdater = new SectionUpdater(sectionInterface.getSectionCache(), simpleSectionDescriptor, user);
								sectionUpdater.isDaemon();
								sectionUpdater.start();

							} else {

								// The section is not a parent section to this module, update it using this thread
								try {
									sectionInterface.getSectionCache().update(simpleSectionDescriptor);
								} catch (KeyNotCachedException e) {
									this.log.debug("Unable to update section " + simpleSectionDescriptor + " requested by user " + user + ", section unloaded.");
								}
							}
						}
					}

					res.sendRedirect(this.getModuleURI(req));
					return null;

				} catch (ValidationException e) {
					validationException = e;
				}
			}

			Document doc = this.createDocument(req, uriParser);
			Element updateSectionElement = doc.createElement("updateSection");
			doc.getFirstChild().appendChild(updateSectionElement);

			updateSectionElement.appendChild(simpleSectionDescriptor.toXML(doc));

			if (validationException != null) {
				updateSectionElement.appendChild(validationException.toXML(doc));
				updateSectionElement.appendChild(RequestUtils.getRequestParameters(req, doc));
			}

			AccessUtils.appendAllowedGroupsAndUsers(doc, updateSectionElement, simpleSectionDescriptor, systemInterface.getUserHandler(), systemInterface.getGroupHandler());
			updateSectionElement.appendChild(HTTPProtocol.getProtocols(doc));

			return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb(), getSectionBreadcrumb(req, simpleSectionDescriptor, "updateSection", updateSectionBreadCrumbText));
		} else {
			throw new URINotFoundException(uriParser);
		}
	}

	private boolean isParentSection(Integer sectionID) {

		SectionInterface parentSectionInterface = this.sectionInterface;

		while (parentSectionInterface != null) {

			if (parentSectionInterface.getSectionDescriptor().getSectionID().equals(sectionID)) {
				return true;
			} else {
				parentSectionInterface = parentSectionInterface.getParentSectionInterface();
			}
		}

		return false;
	}

	@WebPublic
	public SimpleForegroundModuleResponse deleteSection(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws NumberFormatException, SQLException, URINotFoundException, IOException {

		SimpleSectionDescriptor simpleSectionDescriptor = null;

		if (uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (simpleSectionDescriptor = this.sectionDAO.getSection(Integer.parseInt(uriParser.get(2)), false)) != null) {

			if (simpleSectionDescriptor.getParentSectionID() == null) {
				this.log.warn("User " + user + " tried to delete root section " + simpleSectionDescriptor);
			} else {
				this.log.info("User " + user + " deleting section " + simpleSectionDescriptor);

				// Get the sectioninterface of the parentsection
				SectionInterface sectionInterface = systemInterface.getSectionInterface(simpleSectionDescriptor.getParentSectionID());

				// Check if the section is cached
				if (sectionInterface != null && sectionInterface.getSectionCache().isCached(simpleSectionDescriptor)) {

					// Section is cached, check if it is a parentsection to this module
					if (this.isParentSection(simpleSectionDescriptor.getSectionID())) {

						// The section is a parent section to this module, use background thread to avoid threadlock
						SectionUnloader sectionUnloader = new SectionUnloader(sectionInterface.getSectionCache(), simpleSectionDescriptor, user);
						sectionUnloader.isDaemon();
						sectionUnloader.start();

					} else {

						// The section is not a parent section to this module, unload it using this thread
						try {
							sectionInterface.getSectionCache().unload(simpleSectionDescriptor);
						} catch (KeyNotCachedException e) {
							this.log.debug("Unable to unload section " + simpleSectionDescriptor + " requested by user " + user + ", section already unloaded");
						}
					}
				}

				// Delete section from database
				this.sectionDAO.delete(simpleSectionDescriptor);

				//Send event
				//TODO sent event for subsections too
				this.systemInterface.getEventHandler().sendEvent(SimpleSectionDescriptor.class, new CRUDEvent<SimpleSectionDescriptor>(CRUDAction.DELETE, simpleSectionDescriptor), EventTarget.ALL);
			}

			res.sendRedirect(this.getModuleURI(req));

			return null;

		} else {
			throw new URINotFoundException(uriParser);
		}
	}

	@WebPublic
	public SimpleForegroundModuleResponse startSection(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws SQLException, IOException, URINotFoundException {

		SimpleSectionDescriptor simpleSectionDescriptor = null;

		if (uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (simpleSectionDescriptor = this.sectionDAO.getSection(Integer.parseInt(uriParser.get(2)), true)) != null) {

			// Check if this is the root section
			if (simpleSectionDescriptor.getParentSectionID() == null) {

				this.log.warn("User " + user + " tried to cache root section " + simpleSectionDescriptor);

				// Check if the section is already cached
			} else if (systemInterface.getSectionInterface(simpleSectionDescriptor.getSectionID()) != null) {

				this.log.info("User " + user + " tried to cache section " + simpleSectionDescriptor + " which is already cached");

			} else {

				// Get parent section
				SectionInterface sectionInterface = systemInterface.getSectionInterface(simpleSectionDescriptor.getParentSectionID());

				// Check if the parent section is cached
				if (sectionInterface == null) {

					this.log.info("User " + user + " tried to cache section " + simpleSectionDescriptor + " which has a non cached parent section");

				} else {

					this.log.info("User " + user + " caching section " + simpleSectionDescriptor + " in section " + sectionInterface.getSectionDescriptor());

					// Cache section
					sectionInterface.getSectionCache().cache(simpleSectionDescriptor);

				}
			}

			res.sendRedirect(this.getModuleURI(req));

			return null;

		} else {
			throw new URINotFoundException(uriParser);
		}
	}

	@WebPublic
	public SimpleForegroundModuleResponse stopSection(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException, NumberFormatException, SQLException, URINotFoundException {

		SimpleSectionDescriptor simpleSectionDescriptor = null;

		if (uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (simpleSectionDescriptor = this.sectionDAO.getSection(Integer.parseInt(uriParser.get(2)), false)) != null) {

			// Check if this is the root section
			if (simpleSectionDescriptor.getParentSectionID() == null) {

				this.log.warn("User " + user + " tried to unload root section " + simpleSectionDescriptor);

				// Check if the section already is stopped
			} else if (systemInterface.getSectionInterface(simpleSectionDescriptor.getSectionID()) == null) {

				this.log.warn("User " + user + " tried to unload section " + simpleSectionDescriptor + " which is already stopped");

			} else {

				// Get parent section
				SectionInterface sectionInterface = systemInterface.getSectionInterface(simpleSectionDescriptor.getParentSectionID());

				// Check if the parent section is cached
				if (sectionInterface == null) {

					this.log.warn("Unable to unload section " + simpleSectionDescriptor + " because it's parent section is not cached");

				} else {

					// Section is cached, check if it is a parentsection to this module
					if (this.isParentSection(simpleSectionDescriptor.getSectionID())) {

						// The section is a parent section to this module, use background thread to avoid threadlock
						SectionUnloader sectionUnloader = new SectionUnloader(sectionInterface.getSectionCache(), simpleSectionDescriptor, user);
						sectionUnloader.isDaemon();
						sectionUnloader.start();

					} else {

						// The section is not a parent section to this module, unload it using this thread
						try {
							this.log.info("User " + user + " unloading section " + simpleSectionDescriptor + " from section " + sectionInterface.getSectionDescriptor());

							sectionInterface.getSectionCache().unload(simpleSectionDescriptor);
						} catch (KeyNotCachedException e) {
							this.log.debug("Unable to unload section " + simpleSectionDescriptor + " requested by user " + user + ", section already unloaded");
						}
					}
				}
			}

			res.sendRedirect(this.getModuleURI(req));

			return null;

		} else {
			throw new URINotFoundException(uriParser);
		}
	}

	@WebPublic
	public SimpleForegroundModuleResponse moveSection(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws SQLException, URINotFoundException, IOException {

		SimpleSectionDescriptor simpleSectionDescriptor = null;

		if ((uriParser.size() == 3 || uriParser.size() == 4) && NumberUtils.isInt(uriParser.get(2)) && (simpleSectionDescriptor = this.sectionDAO.getSection(Integer.parseInt(uriParser.get(2)), false)) != null) {

			if (uriParser.size() == 3) {

				// Show section tree
				Document doc = this.createDocument(req, uriParser);
				Element moveModuleElement = doc.createElement("moveSection");
				doc.getFirstChild().appendChild(moveModuleElement);

				moveModuleElement.appendChild(simpleSectionDescriptor.toXML(doc));

				Element sectionsElement = doc.createElement("sections");
				moveModuleElement.appendChild(sectionsElement);

				this.appendSection(sectionsElement, doc, this.sectionDAO.getRootSection(true), false);

				return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb(), getSectionBreadcrumb(req, simpleSectionDescriptor, "moveSection", moveSectionBreadCrumbText));
			} else {

				SimpleSectionDescriptor newParentSectionBean = null;

				if (NumberUtils.isInt(uriParser.get(3)) && (newParentSectionBean = this.sectionDAO.getSection(Integer.parseInt(uriParser.get(3)), true)) != null) {

					if (simpleSectionDescriptor.getParentSectionID() == newParentSectionBean.getSectionID()) {

						this.log.info("User " + user + " trying to move section " + simpleSectionDescriptor + " to the section it already belongs to, ignoring move");

						res.sendRedirect(this.getModuleURI(req));

						return null;

					} else {

						// TODO check alias for duplicates

						this.log.info("User " + user + " moving section " + simpleSectionDescriptor + " to section " + newParentSectionBean);

						// Update section in database
						Integer oldParentSectionID = simpleSectionDescriptor.getParentSectionID();
						simpleSectionDescriptor.setParentSectionID(newParentSectionBean.getSectionID());
						this.sectionDAO.update(simpleSectionDescriptor);

						// Set new fullalias
						simpleSectionDescriptor.setFullAlias(newParentSectionBean.getFullAlias() + "/" + simpleSectionDescriptor.getAlias());

						// Check if the section is a parent to this module
						if (this.isParentSection(simpleSectionDescriptor.getSectionID())) {

							// The selected section is a parent to this section, use background thread instead

							// Check if the target section is cached
							SectionInterface targetSectionInterface = systemInterface.getSectionInterface(simpleSectionDescriptor.getParentSectionID());

							if (targetSectionInterface != null) {

								// Target section is cached move section

								SectionMover sectionMover = new SectionMover(systemInterface.getSectionInterface(oldParentSectionID).getSectionCache(), targetSectionInterface.getSectionCache(), simpleSectionDescriptor, user);
								sectionMover.start();
							} else {

								// Target section is not cached unload section
								SectionUnloader sectionUnloader = new SectionUnloader(systemInterface.getSectionInterface(oldParentSectionID).getSectionCache(), simpleSectionDescriptor, user);
								sectionUnloader.start();
							}

						} else {
							// Check if the parent section of the section is cached
							SectionInterface oldSectionInterface = systemInterface.getSectionInterface(oldParentSectionID);

							boolean enabled = false;

							if (oldSectionInterface != null && oldSectionInterface.getSectionCache().isCached(simpleSectionDescriptor)) {

								enabled = true;

								try {
									oldSectionInterface.getSectionCache().unload(simpleSectionDescriptor);
								} catch (KeyNotCachedException e) {
									this.log.debug("Error unloading section " + simpleSectionDescriptor + " from section " + oldSectionInterface.getSectionDescriptor() + " during module move to section " + newParentSectionBean + " requested by user " + user + ", sectiond already unloaded");
								}
							}

							// If the module is enabled and the receiving section is cached then cache the module there
							if (enabled) {
								SectionInterface newParentSectionInterface = systemInterface.getSectionInterface(simpleSectionDescriptor.getParentSectionID());

								if (newParentSectionInterface != null) {
									try {
										newParentSectionInterface.getSectionCache().cache(simpleSectionDescriptor);
									} catch (Exception e) {
										this.log.error("Error caching section " + simpleSectionDescriptor + " in section " + newParentSectionInterface.getSectionDescriptor() + " during section move from section " + oldSectionInterface.getSectionDescriptor() + " by user " + user, e);
									}
								}
							}
						}

						res.sendRedirect(this.getModuleURI(req));

						return null;
					}
				}
			}
		}

		throw new URINotFoundException(uriParser);
	}

	@WebPublic(alias = "import")
	public SimpleForegroundModuleResponse importModules(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, SQLException, IOException, InstantiationException, IllegalAccessException {

		SimpleSectionDescriptor sectionDescriptor = null;

		if (uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (sectionDescriptor = this.sectionDAO.getSection(Integer.parseInt(uriParser.get(2)), true)) != null) {

			ValidationException validationException = null;

			if (req.getMethod().equalsIgnoreCase("POST") && MultipartRequest.isMultipartRequest(req)) {

				MultipartRequest multipartRequest = null;

				try {
					multipartRequest = new MultipartRequest(ramThreshold * BinarySizes.KiloByte, maxRequestSize * BinarySizes.MegaByte, maxFileSize * BinarySizes.MegaByte, req);

					List<SimpleForegroundModuleDescriptor> foregroundModuleDescriptors = null;
					List<SimpleBackgroundModuleDescriptor> backgroundModuleDescriptors = null;
					List<SimpleFilterModuleDescriptor> filterModuleDescriptors = null;

					List<ValidationError> errors = new ArrayList<ValidationError>();

					for (FileItem fileItem : multipartRequest.getFiles()) {

						if (StringUtils.isEmpty(fileItem.getName()) || fileItem.getSize() == 0) {

							continue;
						}

						if (fileItem.getName().endsWith(".fgmodule")) {

							try {

								foregroundModuleDescriptors = CollectionUtils.addAndInstantiateIfNeeded(foregroundModuleDescriptors, parseModuleDescriptor(fileItem, SimpleForegroundModuleDescriptor.class));

							} catch (ValidationException e) {

								errors.addAll(e.getErrors());
							}

						} else if (fileItem.getName().endsWith(".bgmodule")) {

							try {

								backgroundModuleDescriptors = CollectionUtils.addAndInstantiateIfNeeded(backgroundModuleDescriptors, parseModuleDescriptor(fileItem, SimpleBackgroundModuleDescriptor.class));

							} catch (ValidationException e) {

								errors.addAll(e.getErrors());
							}

						} else if (fileItem.getName().endsWith(".flmodule")) {

							if (sectionDescriptor.getParentSectionID() != null) {

								errors = CollectionUtils.addAndInstantiateIfNeeded(errors, new ValidationError("FilterModuleImportInSubsection"));
								continue;
							}

							try {
								filterModuleDescriptors = CollectionUtils.addAndInstantiateIfNeeded(filterModuleDescriptors, parseModuleDescriptor(fileItem, SimpleFilterModuleDescriptor.class));

							} catch (ValidationException e) {

								errors.addAll(e.getErrors());
							}

						} else {

							errors.add(new InvalidFileExtensionValidationError(fileItem.getName()));
						}
					}

					if (!errors.isEmpty()) {

						throw new ValidationException(errors);

					} else if (foregroundModuleDescriptors == null && backgroundModuleDescriptors == null && filterModuleDescriptors == null) {

						throw new ValidationException(new ValidationError("NoDescriptorsfound"));

					} else {

						boolean preserveModuleIDs = multipartRequest.getParameter("preserveModuleIDs") != null;
						
						boolean preserveDataSourceIDs = multipartRequest.getParameter("preserveDataSourceIDs") != null;
						
						StartMode startMode = EnumUtils.toEnum(StartMode.class, multipartRequest.getParameter("startMode"));

						if (!preserveModuleIDs) {

							clearModuleIDs(foregroundModuleDescriptors);
							clearModuleIDs(backgroundModuleDescriptors);
							clearModuleIDs(filterModuleDescriptors);

						} else {

							checkModuleIDs(foregroundModuleDescriptors, foregroundModuleDAO, errors);
							checkModuleIDs(backgroundModuleDescriptors, backgroundModuleDAO, errors);
							checkModuleIDs(filterModuleDescriptors, filterModuleDAO, errors);
						}

						if(!preserveDataSourceIDs){
							
							clearDataSourceIDs(foregroundModuleDescriptors);
							clearDataSourceIDs(backgroundModuleDescriptors);
							clearDataSourceIDs(filterModuleDescriptors);
						}
						
						if (foregroundModuleDescriptors != null) {

							for (ForegroundModuleDescriptor moduleDescriptor : foregroundModuleDescriptors) {

								ForegroundModuleDescriptor conflictingDescriptor = foregroundModuleDAO.getModule(sectionDescriptor.getSectionID(), moduleDescriptor.getAlias());

								if (conflictingDescriptor != null) {

									errors.add(new DuplicateModuleAliasValidationError(moduleDescriptor, conflictingDescriptor, ModuleType.FOREGROUND));
								}
							}
						}

						if (!errors.isEmpty()) {

							throw new ValidationException(errors);
						}

						setSectionID(foregroundModuleDescriptors, sectionDescriptor.getSectionID());
						setSectionID(backgroundModuleDescriptors, sectionDescriptor.getSectionID());

						log.info("User " + user + " importing " + CollectionUtils.getSize(foregroundModuleDescriptors, backgroundModuleDescriptors, filterModuleDescriptors) + " modules into section " + sectionDescriptor);

						addModules(foregroundModuleDescriptors, foregroundModuleDAO);
						addModules(backgroundModuleDescriptors, backgroundModuleDAO);
						addModules(filterModuleDescriptors, filterModuleDAO);

						if (startMode != null) {

							SectionInterface sectionInterface = systemInterface.getSectionInterface(sectionDescriptor.getSectionID());

							if (sectionInterface != null) {

								cacheModules(foregroundModuleDescriptors, sectionInterface.getForegroundModuleCache(), startMode, sectionDescriptor, user);
								cacheModules(backgroundModuleDescriptors, sectionInterface.getBackgroundModuleCache(), startMode, sectionDescriptor, user);
								cacheModules(filterModuleDescriptors, systemInterface.getFilterModuleCache(), startMode, sectionDescriptor, user);
							}
						}

						res.sendRedirect(this.getModuleURI(req));
						return null;
					}

				} catch (ValidationException e) {

					validationException = e;

				} catch (SizeLimitExceededException e) {

					validationException = new ValidationException(new RequestSizeLimitExceededValidationError(e.getActualSize(), e.getPermittedSize()));

				} catch (FileSizeLimitExceededException e) {

					validationException = new ValidationException(new FileSizeLimitExceededValidationError(e.getFileName(), e.getActualSize(), e.getPermittedSize()));

				} catch (FileUploadException e) {

					validationException = new ValidationException(new ValidationError("UnableToParseRequest"));

				} finally {

					if (multipartRequest != null) {

						multipartRequest.deleteFiles();
					}
				}
			}

			log.info("User " + user + " requesting import modules form in section " + sectionDescriptor);

			Document doc = this.createDocument(req, uriParser);
			Element importModulesElement = doc.createElement("ImportModules");
			doc.getFirstChild().appendChild(importModulesElement);

			importModulesElement.appendChild(sectionDescriptor.toXML(doc));

			XMLUtils.appendNewElement(doc, importModulesElement, "Started", systemInterface.getSectionInterface(sectionDescriptor.getSectionID()) != null);

			if (validationException != null) {
				importModulesElement.appendChild(validationException.toXML(doc));
			}

			return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb(), getSectionBreadcrumb(req, sectionDescriptor, "importModules", importModulesBreadCrumbText));
		} else {
			throw new URINotFoundException(uriParser);
		}
	}

	private <T extends ModuleDescriptor> void cacheModules(List<? extends T> moduleDescriptors, BaseModuleCache<T, ?, ?> moduleCache, StartMode startMode, SectionDescriptor sectionDescriptor, User user) {

		if (moduleDescriptors != null) {

			for (T moduleDescriptor : moduleDescriptors) {

				String typeName = moduleDescriptor.getType().toString().toLowerCase();

				if (startMode == StartMode.ALL || moduleDescriptor.isEnabled()) {

					this.log.info("User " + user + " caching " + typeName + " module " + moduleDescriptor + " in section " + sectionDescriptor);

					try {
						moduleCache.cache(moduleDescriptor);

					} catch (Throwable t) {

						this.log.error("Error caching " + typeName + " module " + moduleDescriptor + " in section " + sectionDescriptor + " requested by user " + user, t);
					}
				}
			}
		}
	}

	private void setSectionID(List<? extends BaseVisibleModuleDescriptor> moduleDescriptors, Integer sectionID) {

		if (moduleDescriptors != null) {

			for (BaseVisibleModuleDescriptor moduleDescriptor : moduleDescriptors) {

				moduleDescriptor.setSectionID(sectionID);
			}
		}
	}

	private <T extends ModuleDescriptor> void addModules(List<T> moduleDescriptors, ModuleDAO<T> moduleDAO) throws SQLException {

		if (moduleDescriptors != null) {

			String typeName = moduleDescriptor.getType().toString().toLowerCase();

			for (T moduleDescriptor : moduleDescriptors) {

				log.info("Importing " + typeName + " module " + moduleDescriptor);

				moduleDAO.add(moduleDescriptor);
			}
		}
	}

	private void checkModuleIDs(List<? extends BaseModuleDescriptor> moduleDescriptors, ModuleDAO<?> moduleDAO, List<ValidationError> errors) throws SQLException {

		if (moduleDescriptors != null) {

			for (BaseModuleDescriptor moduleDescriptor : moduleDescriptors) {

				ModuleDescriptor conflictingDescriptor;

				if (moduleDescriptor.getModuleID() != null && (conflictingDescriptor = moduleDAO.getModule(moduleDescriptor.getModuleID())) != null) {

					errors.add(new DuplicateModuleIDValidationError(moduleDescriptor, conflictingDescriptor, moduleDescriptor.getType()));
				}
			}
		}
	}

	private void clearModuleIDs(List<? extends BaseModuleDescriptor> moduleDescriptors) {

		if (moduleDescriptors != null) {

			for (BaseModuleDescriptor moduleDescriptor : moduleDescriptors) {

				moduleDescriptor.setModuleID(null);
			}
		}
	}

	private void clearDataSourceIDs(List<? extends BaseModuleDescriptor> moduleDescriptors) {

		if (moduleDescriptors != null) {

			for (BaseModuleDescriptor moduleDescriptor : moduleDescriptors) {

				moduleDescriptor.setDataSourceID(null);
			}
		}
	}
	
	private <T extends XMLParserPopulateable> T parseModuleDescriptor(FileItem fileItem, Class<T> descriptorClazz) throws ValidationException, InstantiationException, IllegalAccessException {

		InputStream inputStream = null;

		try {
			inputStream = fileItem.getInputStream();

			Document doc = XMLUtils.parseXML(inputStream, false, false);

			T descriptor = descriptorClazz.newInstance();

			Element docElement = doc.getDocumentElement();

			if (!docElement.getTagName().equals("module")) {

				log.info("Error parsing descriptor " + fileItem.getName() + ", unable to find module element");

				throw new ValidationException(new UnableToParseFileValidationError(fileItem.getName()));
			}

			descriptor.populate(new XMLParser(docElement));

			return descriptor;

		} catch (SAXException e) {

			log.info("Error parsing descriptor " + fileItem.getName(), e);

			throw new ValidationException(new UnableToParseFileValidationError(fileItem.getName()));

		} catch (IOException e) {

			log.info("Error parsing descriptor " + fileItem.getName(), e);

			throw new ValidationException(new UnableToParseFileValidationError(fileItem.getName()));

		} catch (ParserConfigurationException e) {

			log.info("Error parsing descriptor " + fileItem.getName(), e);

			throw new ValidationException(new UnableToParseFileValidationError(fileItem.getName()));

		} catch (ValidationException e) {

			log.info("Error parsing descriptor " + fileItem.getName(), e);

			throw new ValidationException(new UnableToParseFileValidationError(fileItem.getName()));

		} finally {

			StreamUtils.closeStream(inputStream);
		}
	}

	private static Element getPathTypes(Document doc) {

		// List pathtypes
		Element pathTypes = doc.createElement("pathTypes");

		for (PathType pathType : PathType.values()) {
			pathTypes.appendChild(XMLUtils.createElement("pathType", pathType.toString(), doc));
		}

		return pathTypes;
	}

	private Element getDataSources(Document doc) throws SQLException {

		// List datasources
		Element dataSources = doc.createElement("dataSources");

		ArrayList<SimpleDataSourceDescriptor> dataSourceList = this.dataSourceDAO.getAll();

		if (dataSourceList != null) {
			for (SimpleDataSourceDescriptor dataSourceBean : dataSourceList) {
				dataSources.appendChild(dataSourceBean.toXML(doc));
			}
		}

		return dataSources;
	}

	protected ForegroundModule getForegroundModuleInstance(ForegroundModuleDescriptor moduleDescriptor) {

		// Check if the modules section is cached
		SectionInterface sectionInterface = systemInterface.getSectionInterface(moduleDescriptor.getSectionID());

		if (sectionInterface != null) {

			return sectionInterface.getForegroundModuleCache().getModule(moduleDescriptor);
		} else {
			return null;
		}
	}

	protected BackgroundModule getBackgroundModuleInstance(BackgroundModuleDescriptor moduleDescriptor) {

		// Check if the modules section is cached
		SectionInterface sectionInterface = systemInterface.getSectionInterface(moduleDescriptor.getSectionID());

		if (sectionInterface != null) {

			return sectionInterface.getBackgroundModuleCache().getModule(moduleDescriptor);
		} else {
			return null;
		}
	}

	@WebPublic(alias = "users")
	public ForegroundModuleResponse getUsers(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {

		return userGroupListConnector.getUsers(req, res, user, uriParser);
	}

	@WebPublic(alias = "groups")
	public ForegroundModuleResponse getGroups(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {

		return userGroupListConnector.getGroups(req, res, user, uriParser);
	}

	//	/**
	//	 * @param hTTPProtocol
	//	 * @param sectionInterface
	//	 * @return false if any child has a protocol not equal to protocol, otherwise false
	//	 * @throws SQLException
	//	 */
	//	private boolean validateHTTPProtocol(HTTPProtocol hTTPProtocol, SimpleSectionDescriptor sectionDescriptor) throws SQLException {
	//
	//		ArrayList<SimpleForegroundModuleDescriptor> moduleDescriptors = this.foregroundModuleDAO.getModules(sectionDescriptor);
	//
	//		if (!CollectionUtils.isEmpty(moduleDescriptors)) {
	//
	//			for (ForegroundModuleDescriptor moduleDescriptor : moduleDescriptors) {
	//
	//				// Section module has a conflicting protocol
	//				if (moduleDescriptor.getRequiredProtocol() != null && !moduleDescriptor.getRequiredProtocol().equals(hTTPProtocol)) {
	//					return false;
	//				}
	//
	//			}
	//		}
	//
	//		List<SimpleSectionDescriptor> subSections = this.sectionDAO.getSubSections(sectionDescriptor, false);
	//
	//		if (!CollectionUtils.isEmpty(subSections)) {
	//
	//			for (SimpleSectionDescriptor section : subSections) {
	//
	//				// Sub section has conflicting protocol
	//				if (section.getRequiredProtocol() != null && !section.getRequiredProtocol().equals(hTTPProtocol)) {
	//					return false;
	//				}
	//
	//				if (!validateHTTPProtocol(hTTPProtocol, section)) {
	//					return false;
	//				}
	//			}
	//		}
	//
	//		return true;
	//	}
}
