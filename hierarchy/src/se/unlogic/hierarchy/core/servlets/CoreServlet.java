/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.servlets;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.xml.transform.TransformerException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.emailutils.framework.EmailHandler;
import se.unlogic.emailutils.framework.StopableEmailHandler;
import se.unlogic.hierarchy.core.beans.Breadcrumb;
import se.unlogic.hierarchy.core.beans.LinkTag;
import se.unlogic.hierarchy.core.beans.ScriptTag;
import se.unlogic.hierarchy.core.beans.SimpleDataSourceDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleSectionDescriptor;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.cache.CoreXSLTCacheHandler;
import se.unlogic.hierarchy.core.cache.DataSourceCache;
import se.unlogic.hierarchy.core.cache.FilterModuleCache;
import se.unlogic.hierarchy.core.daos.factories.CoreDaoFactory;
import se.unlogic.hierarchy.core.enums.DataSourceType;
import se.unlogic.hierarchy.core.enums.ResponseType;
import se.unlogic.hierarchy.core.enums.SystemStatus;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.exceptions.RequestException;
import se.unlogic.hierarchy.core.globallisteners.GlobalBackgroundModuleCacheListener;
import se.unlogic.hierarchy.core.globallisteners.GlobalForegroundModuleCacheListener;
import se.unlogic.hierarchy.core.globallisteners.GlobalSectionCacheListener;
import se.unlogic.hierarchy.core.handlers.GroupHandler;
import se.unlogic.hierarchy.core.handlers.LoginHandler;
import se.unlogic.hierarchy.core.handlers.SystemEventHandler;
import se.unlogic.hierarchy.core.handlers.SystemInstanceHandler;
import se.unlogic.hierarchy.core.handlers.SystemSessionListenerHandler;
import se.unlogic.hierarchy.core.handlers.UserHandler;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleCacheListener;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.CachedXSLTDescriptor;
import se.unlogic.hierarchy.core.interfaces.EventHandler;
import se.unlogic.hierarchy.core.interfaces.FilterChain;
import se.unlogic.hierarchy.core.interfaces.FilterModule;
import se.unlogic.hierarchy.core.interfaces.FilterModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleCacheListener;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.FullSystemInterface;
import se.unlogic.hierarchy.core.interfaces.InstanceHandler;
import se.unlogic.hierarchy.core.interfaces.ModuleResponse;
import se.unlogic.hierarchy.core.interfaces.SectionCacheListener;
import se.unlogic.hierarchy.core.interfaces.SystemStartupListener;
import se.unlogic.hierarchy.core.sections.Section;
import se.unlogic.hierarchy.core.utils.DBCPUtils;
import se.unlogic.log4jutils.logging.RelativePathHandler;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.db.DBUtils;
import se.unlogic.standardutils.enums.EnumUtils;
import se.unlogic.standardutils.i18n.Language;
import se.unlogic.standardutils.io.FileUtils;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.settings.SettingNode;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.time.TimeUtils;
import se.unlogic.standardutils.xml.XMLParser;
import se.unlogic.standardutils.xml.XMLTransformer;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;

public class CoreServlet extends BaseServlet implements FullSystemInterface {

	private static final long serialVersionUID = 2610603465755792663L;
	public static final String VERSION_PREFIX = "OpenHierarchy 1.2.5";
	public static final String VERSION;

	static {

		String tempVersion;

		try {
			tempVersion = VERSION_PREFIX + " (rev. " + StringUtils.readStreamAsString(CoreServlet.class.getResourceAsStream("/META-INF/svnrevision.txt")) + ")";

		} catch (Exception e) {

			tempVersion = VERSION_PREFIX + " (rev. unknown)";
		}

		VERSION = tempVersion;
	}

	private CoreXSLTCacheHandler xsltCacheHandler;
	private Language defaultLanguage;
	private String applicationFileSystemPath;
	private DataSource dataSource;
	private Section rootSection;
	private DataSourceCache dataSourceCache;
	private UserHandler userHandler;
	private GroupHandler groupHandler;
	private LoginHandler loginHandler;
	private CoreDaoFactory coreDaoFactory;
	private StopableEmailHandler stopableEmailHandler;
	private DataSourceType dataSourceType;
	private GlobalSectionCacheListener globalSectionCacheListener;
	private SystemInstanceHandler systemInstanceHandler;
	private GlobalForegroundModuleCacheListener globalForegroundModuleCacheListener;
	private GlobalBackgroundModuleCacheListener globalBackgroundModuleCacheListener;
	private ConcurrentHashMap<Integer, Section> sectionMap;
	private FilterModuleCache filterModuleCache;
	private SystemEventHandler eventHandler;
	private ArrayList<SystemStartupListener> startupListeners;
	private SystemSessionListenerHandler systemSessionListenerHandler;

	private boolean systemXMLDebug;
	private String systemXMLDebugFile;

	private boolean moduleXMLDebug;
	private String moduleXMLDebugFile;

	private boolean backgroundModuleXMLDebug;
	private String backgroundModuleXMLDebugFile;

	private String encoding;

	@Override
	public void init() throws ServletException {

		this.init(false);
	}

	@Override
	public void init(boolean throwExceptions) throws ServletException {

		try {
			// Store starttime
			long startTime = System.currentTimeMillis();

			//Get application file system path
			this.applicationFileSystemPath = this.getServletContext().getRealPath("/");

			// Search for config
			File configurationFile;

			String configPrefix = System.getenv("OpenHierarchy_configPrefix");

			if (!StringUtils.isEmpty(configPrefix) && FileUtils.fileExists(this.applicationFileSystemPath + "WEB-INF/" + configPrefix + ".config.xml")) {

				configurationFile = new File(this.applicationFileSystemPath + "WEB-INF/" + configPrefix + ".config.xml");

			} else {

				configurationFile = new File(this.applicationFileSystemPath + "WEB-INF/config.xml");
			}

			// Check if config exists
			if (!configurationFile.exists()) {

				setSystemStatus(SystemStatus.CONFIG_NOT_FOUND);
				return;

			} else {

				setSystemStatus(SystemStatus.STARTING);

				// Store relative paths
				RelativePathHandler.setPath("webroot", this.applicationFileSystemPath + "WEB-INF" + File.separator);

				// Initialize logging
				if (!StringUtils.isEmpty(configPrefix) && FileUtils.fileExists(this.applicationFileSystemPath + "WEB-INF/" + configPrefix + ".log4j.xml")) {

					DOMConfigurator.configure(this.applicationFileSystemPath + "WEB-INF/" + configPrefix + ".log4j.xml");

				} else {

					DOMConfigurator.configure(this.applicationFileSystemPath + "WEB-INF/log4j.xml");
				}

				this.log = Logger.getLogger(CoreServlet.class);

				this.log.fatal("***** " + VERSION + " starting... *****");

				// Parse configuration
				SettingNode config = new XMLParser(configurationFile);

				// Set system data source
				dataSourceType = DataSourceType.valueOf(config.getString("/Config/DataSource/Type"));

				if (dataSourceType == DataSourceType.SystemManaged) {

					SimpleDataSourceDescriptor dataSourceDescriptor = new SimpleDataSourceDescriptor();

					dataSourceDescriptor.setUrl(config.getString("/Config/DataSource/Url"));
					dataSourceDescriptor.setDriver(config.getString("/Config/DataSource/Driver"));
					dataSourceDescriptor.setUsername(config.getString("/Config/DataSource/Username"));
					dataSourceDescriptor.setPassword(config.getString("/Config/DataSource/Password"));
					dataSourceDescriptor.setRemoveAbandoned(config.getPrimitiveBoolean("/Config/DataSource/RemoveAbandoned"));
					dataSourceDescriptor.setRemoveTimeout(config.getInteger("/Config/DataSource/RemoveAbandonedTimeout"));
					dataSourceDescriptor.setTestOnBorrow(config.getPrimitiveBoolean("/Config/DataSource/TestOnBorrow"));
					dataSourceDescriptor.setValidationQuery(config.getString("/Config/DataSource/ValidationQuery"));
					dataSourceDescriptor.setMaxActive(config.getInteger("/Config/DataSource/MaxActive"));
					dataSourceDescriptor.setMaxIdle(config.getInteger("/Config/DataSource/MaxIdle"));
					dataSourceDescriptor.setLogAbandoned(config.getPrimitiveBoolean("/Config/DataSource/LogAbandoned"));
					dataSourceDescriptor.setMinIdle(config.getInteger("/Config/DataSource/MinIdle"));
					dataSourceDescriptor.setMaxWait(config.getInteger("/Config/DataSource/MaxWait"));

					this.dataSource = DBCPUtils.createConnectionPool(dataSourceDescriptor);

				} else if (dataSourceType == DataSourceType.ContainerManaged) {

					this.dataSource = DBUtils.getDataSource(config.getString("/Config/DataSource/Url"));

				} else {
					throw new RuntimeException("Unknown datasource type " + dataSourceType + " in config.xml");
				}

				this.encoding = config.getString("/Config/Encoding");

				if (StringUtils.isEmpty(encoding)) {

					throw new RuntimeException("No encoding found in config.xml");
				}

				String defaultLanguage = config.getString("/Config/DefaultLanguage");

				if (StringUtils.isEmpty(defaultLanguage) || (this.defaultLanguage = EnumUtils.toEnum(Language.class, defaultLanguage)) == null) {

					throw new RuntimeException("No or invalid default language specified in config.xml");
				}

				this.xsltCacheHandler = new CoreXSLTCacheHandler(config, this.defaultLanguage, getApplicationFileSystemPath());

				this.userHandler = new UserHandler();
				this.groupHandler = new GroupHandler();

				String coreDAOFactoryClassString = config.getString("/Config/CoreDAOFactory");

				if (StringUtils.isEmpty(coreDAOFactoryClassString)) {

					throw new RuntimeException("No core DAO factory class specified in config.xml");
				}

				Class<?> coreDAOFactoryClass = Class.forName(coreDAOFactoryClassString);

				if (!CoreDaoFactory.class.isAssignableFrom(coreDAOFactoryClass)) {

					throw new RuntimeException("The core DAO factory class specified in config.xml is not a valid. The specified class must extend se.unlogic.hierarchy.core.daos.factories.CoreDaoFactory.");
				}

				coreDaoFactory = (CoreDaoFactory) coreDAOFactoryClass.newInstance();
				coreDaoFactory.init(dataSource);

				this.dataSourceCache = new DataSourceCache(coreDaoFactory);
				this.stopableEmailHandler = new StopableEmailHandler();

				this.moduleXMLDebug = config.getPrimitiveBoolean("/Config/ModuleXMLDebug");
				this.moduleXMLDebugFile = config.getString("/Config/ModuleXMLDebugFile");
				this.systemXMLDebug = config.getPrimitiveBoolean("/Config/SystemXMLDebug");
				this.systemXMLDebugFile = config.getString("/Config/SystemXMLDebugFile");
				this.backgroundModuleXMLDebug = config.getPrimitiveBoolean("/Config/BackgroundModuleXMLDebug");
				this.backgroundModuleXMLDebugFile = config.getString("/Config/BackgroundModuleXMLDebugFile");

				this.globalSectionCacheListener = new GlobalSectionCacheListener();
				this.globalForegroundModuleCacheListener = new GlobalForegroundModuleCacheListener();
				this.globalBackgroundModuleCacheListener = new GlobalBackgroundModuleCacheListener();
				this.sectionMap = new ConcurrentHashMap<Integer, Section>();
				this.systemInstanceHandler = new SystemInstanceHandler();
				this.eventHandler = new SystemEventHandler();
				this.loginHandler = new LoginHandler();

				this.startupListeners = new ArrayList<SystemStartupListener>();

				Method addListenerMethod = ReflectionUtils.getMethod(ServletContext.class, "addListener", Void.TYPE, EventListener.class);

				if(addListenerMethod != null){

					try{
						SystemSessionListenerHandler systemSessionListenerHandler = new SystemSessionListenerHandler();

						addListenerMethod.invoke(this.getServletContext(), systemSessionListenerHandler);

						this.systemSessionListenerHandler = systemSessionListenerHandler;

					}catch(Exception e){

						log.error("Error adding session listener handler to servlet context", e);
					}
				}

				//Cache filter modules
				this.filterModuleCache = new FilterModuleCache(this);

				try {
					filterModuleCache.cacheModules(false);

				} catch (Exception e) {

					log.error("Error caching filter modules", e);
				}

				// Get root sectionDescriptor
				SimpleSectionDescriptor simpleSectionDescriptor = coreDaoFactory.getSectionDAO().getRootSection(false);

				// Instantiate root section
				rootSection = new Section(simpleSectionDescriptor, null, this);

				rootSection.cacheModuleAndSections();

				setSystemStatus(SystemStatus.STARTED);

				triggerStartupListeners();

				this.log.fatal(VERSION + " successfully started in " + TimeUtils.millisecondsToString(System.currentTimeMillis() - startTime) + " ms");
			}
		} catch (Throwable e) {
			setSystemStatus(SystemStatus.FAIL_SAFE);
			if (this.log != null) {
				this.log.error(VERSION + " startup failed!", e);
				this.log.fatal(VERSION + " is in failsafe mode");
			} else {
				System.out.println(VERSION + " startup failed!");
				e.printStackTrace();
				System.out.println(VERSION + " is in failsafe mode!");
			}

			if (throwExceptions) {
				if (e instanceof RuntimeException) {
					throw (RuntimeException) e;
				} else {
					throw new RuntimeException(e);
				}
			}
		}
	}

	private synchronized void triggerStartupListeners() {

		if (!startupListeners.isEmpty()){

			log.info("Detected " + startupListeners.size() + " system startup listeners");

			for(SystemStartupListener listener : startupListeners){

				try{
					log.info("Triggering system startup listener " + listener);

					listener.systemStarted();

				}catch(Throwable t){

					log.error("Error in system startup listener " + listener, t);
				}
			}

			log.info("All system startup listeners triggered");

			startupListeners = null;

		}else{

			startupListeners = null;
		}
	}

	@Override
	public void addStartupListener(SystemStartupListener startupListener) {

		//Trigger listener right away if the system is already started
		if (this.getSystemStatus() == SystemStatus.STARTED){

			startupListener.systemStarted();
			return;
		}

		synchronized(this){

			if (this.getSystemStatus() == SystemStatus.STARTING){

				startupListeners.add(startupListener);
			}
		}
	}

	@Override
	protected void processRequest(HttpServletRequest req, HttpServletResponse res) throws TransformerException, IOException {

		User user = this.getUser(req);

		// Parse the URI
		URIParser uriParser = new URIParser(req, req.getServletPath(), null);

		List<Entry<FilterModuleDescriptor, FilterModule>> filterModules = this.filterModuleCache.getEntries(uriParser.getRemainingURI(), user);

		if (filterModules != null) {

			FilterChain filterChain = new CoreFilterChain(this, filterModules);

			filterChain.doFilter(req, res, user, uriParser);

		} else {

			processRequest(req, res, user, uriParser);
		}
	}

	protected void processRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws TransformerException, IOException {

		ForegroundModuleResponse moduleResponse = null;
		RequestException exception = null;

		try {
			try {
				moduleResponse = this.rootSection.processRequest(req, res, user, uriParser, rootSection.getSectionDescriptor().getRequiredProtocol());

			} catch (AccessDeniedException e) {

				if (user == null) {

					loginHandler.processLoginRequest(req, res, uriParser, true);
				}

				if (!res.isCommitted()) {
					throw e;
				}
			}

		} catch (RequestException e) {

			log.log(e.getPriority(), e.toString() + " Requested by user " + user + " accessing from " + req.getRemoteAddr(), e.getThrowable());
			exception = e;
		}

		// Check if the response has been committed
		if (!res.isCommitted()) {

			// Set request attribute to tell the URLFilter to ignore this response
			req.setAttribute("processed", true);

			// Response has not been committed create xml document
			Document doc = XMLUtils.createDomDocument();

			// Create root element
			Element document = doc.createElement("document");
			doc.appendChild(document);

			if (exception != null) {

				// Append exception
				Element errors = doc.createElement("errors");
				doc.getDocumentElement().appendChild(errors);
				errors.appendChild(exception.toXML(doc));

				if (exception.getStatusCode() != null) {
					res.setStatus(exception.getStatusCode());
				}

				this.appendLinks(doc, exception.getBackgroundModuleResponses());
				this.appendScripts(doc, exception.getBackgroundModuleResponses());

				this.addBackgroundModuleResponses(exception.getBackgroundModuleResponses(), doc, user, req);

			} else {

				// Check if the user has changed
				if (moduleResponse.isUserChanged()) {

					try {
						HttpSession session = req.getSession(false);

						if (session != null) {
							user = (User) session.getAttribute("user");
						}

					} catch (IllegalStateException e) {

						user = null;
					}
				}

				if (moduleResponse.isExcludeSystemTransformation()) {
					doc = moduleResponse.getDocument();
					document = doc.getDocumentElement();
				}

				this.appendLinks(doc, moduleResponse);
				this.appendScripts(doc, moduleResponse);

				if (isValidResponse(moduleResponse)) {

					if (moduleResponse.getResponseType() == ResponseType.HTML) {

						// Append module html response
						Element moduleres = doc.createElement("moduleHTMLResponse");
						document.appendChild(moduleres);
						moduleres.appendChild(doc.createCDATASection(moduleResponse.getHtml()));

					} else if (moduleResponse.getResponseType() == ResponseType.XML_FOR_CORE_TRANSFORMATION) {

						// Append module response
						Element moduleres = doc.createElement("moduleXMLResponse");
						document.appendChild(moduleres);
						moduleres.appendChild(doc.adoptNode(moduleResponse.getElement()));

					} else if (moduleResponse.getResponseType() == ResponseType.XML_FOR_SEPARATE_TRANSFORMATION) {

						if (moduleResponse.getTransformer() != null) {

							// Write xml to debug file if xml debug output is enabled
							if (moduleXMLDebug && !StringUtils.isEmpty(moduleXMLDebugFile)) {
								this.log.debug("XML debug mode enabled, writing module XML to " + moduleXMLDebugFile + " for module " + moduleResponse.getModuleDescriptor());

								try {
									XMLUtils.writeXMLFile(moduleResponse.getDocument(), this.applicationFileSystemPath + "WEB-INF/" + moduleXMLDebugFile, true, encoding);

									this.log.debug("Finished writing module XML to " + this.applicationFileSystemPath + "WEB-INF/" + moduleXMLDebugFile);

								} catch (Exception e) {

									this.log.error("Error writing module XML to " + this.applicationFileSystemPath + "WEB-INF/" + moduleXMLDebugFile, e);
								}
							}

							// Transform output
							try {

								this.log.debug("Module XML transformation starting");

								if (moduleResponse.isExcludeSystemTransformation()) {

									// Set response parameters
									res.setContentType("text/html");

									// Set standard HTTP/1.1 no-cache headers.
									res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, proxy-revalidate");

									XMLTransformer.transformToWriter(moduleResponse.getTransformer(), doc, res.getWriter(), encoding);

									return;

								} else {

									StringWriter stringWriter = new StringWriter();

									XMLTransformer.transformToWriter(moduleResponse.getTransformer(), moduleResponse.getDocument(), stringWriter, encoding);

									// Append module response
									Element moduleres = doc.createElement("moduleTransformedResponse");
									document.appendChild(moduleres);

									this.log.debug("Module XML transformation finished, appending result...");

									moduleres.appendChild(doc.createCDATASection(stringWriter.toString()));

									this.log.debug("Result appended");

								}

							} catch (Exception e) {

								this.log.error("Tranformation of module response from module" + moduleResponse.getModuleDescriptor() + " failed, requested by user " + user + " accesing from " + req.getRemoteAddr(), e);

								Element errors = doc.createElement("errors");
								document.appendChild(errors);

								Element separateTransformationFailedElement = doc.createElement("separateTransformationFailed");
								errors.appendChild(separateTransformationFailedElement);
								separateTransformationFailedElement.appendChild(XMLUtils.createCDATAElement("exception", e.toString(), doc));
								separateTransformationFailedElement.appendChild(moduleResponse.getModuleDescriptor().toXML(doc));
							}
						} else {
							this.log.error("Module response for separate transformation without attached stylesheet returned by module " + moduleResponse.getModuleDescriptor() + " requested by user " + user + " accesing from " + req.getRemoteAddr());
							Element errors = doc.createElement("errors");
							document.appendChild(errors);
							Element separateTransformationWithoutStylesheetElement = doc.createElement("separateTransformationWithoutStylesheet");
							errors.appendChild(separateTransformationWithoutStylesheetElement);
							separateTransformationWithoutStylesheetElement.appendChild(moduleResponse.getModuleDescriptor().toXML(doc));
						}
					}
				} else {
					// No response, append error
					this.log.error("Invalid module response from module" + moduleResponse.getModuleDescriptor() + ", requested by user " + user + " accesing from " + req.getRemoteAddr());
					Element errors = doc.createElement("errors");
					document.appendChild(errors);
					Element invalidModuleResonseElement = doc.createElement("invalidModuleResonse");
					errors.appendChild(invalidModuleResonseElement);
					invalidModuleResonseElement.appendChild(moduleResponse.getModuleDescriptor().toXML(doc));
				}

				this.addBackgroundModuleResponses(moduleResponse.getBackgroundModuleResponses(), doc, user, req);
			}

			XMLUtils.appendNewCDATAElement(doc, document, "version", VERSION);

			document.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser, false, true));

			// Append root section
			document.appendChild(this.rootSection.getSectionDescriptor().toXML(doc));

			// Append userinfo and menuitems
			if (user != null) {
				document.appendChild(user.toXML(doc));
			}

			//Get style sheet so that the correct menu type can be added
			CachedXSLTDescriptor xslDescriptor;

			if (this.xsltCacheHandler.getXslDescriptorCount() == 1) {

				//Only default stylesheet loaded, skip i18n support.

				xslDescriptor = this.xsltCacheHandler.getDefaultXsltDescriptor();

			} else {

				Language language = this.getLanguage(req, user);

				String preferedDesign = this.getPreferedDesign(req, user);

				xslDescriptor = this.xsltCacheHandler.getBestMatchingXSLTDescriptor(language, preferedDesign);
			}

			Element menus = doc.createElement("menus");
			document.appendChild(menus);

			if (moduleResponse != null) {

				if (moduleResponse.getTitle() != null) {
					document.appendChild(XMLUtils.createCDATAElement("title", moduleResponse.getTitle(), doc));
				}

				if (xslDescriptor.usesFullMenu()) {

					menus.appendChild(rootSection.getFullMenu(user, uriParser).toXML(doc));

				} else {

					if (moduleResponse.getMenu() != null) {

						menus.appendChild(moduleResponse.getMenu().toXML(doc));
					}
				}

				if (!moduleResponse.getBreadcrumbs().isEmpty()) {

					Element breadcrumbsElement = doc.createElement("breadcrumbs");
					document.appendChild(breadcrumbsElement);

					for (Breadcrumb breadcrumb : moduleResponse.getBreadcrumbs()) {

						if (breadcrumb != null) {

							breadcrumbsElement.appendChild(breadcrumb.toXML(doc));
						}
					}
				}

			} else if (exception != null) {

				if (xslDescriptor.usesFullMenu()) {

					menus.appendChild(rootSection.getFullMenu(user, uriParser).toXML(doc));

				} else {

					if (exception.getMenu() != null) {

						menus.appendChild(exception.getMenu().toXML(doc));
					}
				}

				//TODO add breadcrumbs to exceptions
			}

			// Write xml to debug file if xml debug output is enabled
			if (systemXMLDebug && !StringUtils.isEmpty(systemXMLDebugFile)) {
				this.log.debug("XML debug mode enabled, writing system XML to " + systemXMLDebugFile);

				try {
					FileWriter xmldebugstream = new FileWriter(new File(this.applicationFileSystemPath + "WEB-INF/" + systemXMLDebugFile));
					XMLUtils.toString(doc, encoding, xmldebugstream, false);
					xmldebugstream.close();

					this.log.debug("Finished writing system XML to " + this.applicationFileSystemPath + "WEB-INF/" + systemXMLDebugFile);

				} catch (Exception e) {

					this.log.error("Error writing system XML to " + this.applicationFileSystemPath + "WEB-INF/" + systemXMLDebugFile, e);
				}
			}

			// Set encoding
			res.setCharacterEncoding(encoding);

			// Set response parameters
			res.setContentType("text/html");

			// Set standard HTTP/1.1 no-cache headers.
			res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, proxy-revalidate");

			// Transform output
			try {
				this.log.debug("System XML transformation starting");

				XMLTransformer.transformToWriter(xslDescriptor.getTransformer(), doc, res.getWriter(), encoding);

				this.log.debug("System XML transformation finished, response transformed and committed");

			} catch (TransformerException e) {

				this.log.error("System XML transformation failed, " + e);
				throw e;

			} catch (IOException e) {

				if (!res.isCommitted()) {

					log.error("Error writing response", e);
					throw e;

				} else {

					this.log.debug("Response already committed");
				}
			}catch(IllegalStateException e){

				this.log.debug("Response already committed");
			}

		} else {
			if (exception != null) {
				this.log.warn("Error " + exception + " after response has been committed");
			} else {
				this.log.debug("Response already committed");
			}
		}
	}

	private Language getLanguage(HttpServletRequest req, User user) {

		Language language = null;

		Object object = null;

		// Get from request attribute
		try {
			object = req.getAttribute("language");
			if (object != null) {
				language = (Language) object;
			}
		} catch (ClassCastException e) {
			log.warn("Invalid class " + object.getClass() + " found in request attribute \"language\" of user " + user, e);
		}

		// Get from session attribute
		HttpSession session;

		if (language == null && (session = req.getSession()) != null) {

			try {
				object = session.getAttribute("language");
				if (object != null) {
					language = (Language) object;
				}
			} catch (IllegalStateException e) {} catch (ClassCastException e) {
				log.warn("Invalid class " + object.getClass() + " found in session attribute \"language\" of user " + user, e);
			}
		}

		// Get from user
		if (language == null && user != null) {

			language = user.getLanguage();

		}

		return language;

	}

	private String getPreferedDesign(HttpServletRequest req, User user) {

		String preferedDesign = null;

		Object object = null;

		// Get from request attribute
		object = req.getAttribute("preferedDesign");

		if (object != null) {
			preferedDesign = object.toString();
		}

		// Get from session attribute
		HttpSession session;

		if (preferedDesign == null && (session = req.getSession()) != null) {

			try {
				object = session.getAttribute("preferedDesign");

				if (object != null) {
					preferedDesign = object.toString();
				}

			} catch (IllegalStateException e) {}
		}

		// Get from user
		if (preferedDesign == null && user != null) {

			preferedDesign = user.getPreferedDesign();

		}

		return preferedDesign;

	}

	private void appendLinks(Document doc, ForegroundModuleResponse moduleResponse) {

		Element links = doc.createElement("links");
		doc.getDocumentElement().appendChild(links);

		LinkedHashSet<LinkTag> linkSet = new LinkedHashSet<LinkTag>();

		if (moduleResponse.getLinks() != null) {
			linkSet.addAll(moduleResponse.getLinks());
		}

		if (moduleResponse.getBackgroundModuleResponses() != null) {

			for (BackgroundModuleResponse backgroundModuleResponse : moduleResponse.getBackgroundModuleResponses()) {

				if (backgroundModuleResponse.getLinks() != null) {

					linkSet.addAll(backgroundModuleResponse.getLinks());
				}
			}
		}

		XMLUtils.append(doc, links, linkSet);
	}

	private void appendLinks(Document doc, List<BackgroundModuleResponse> moduleResponses) {

		if (moduleResponses != null) {

			Element links = doc.createElement("links");
			doc.getDocumentElement().appendChild(links);

			LinkedHashSet<LinkTag> linkSet = new LinkedHashSet<LinkTag>();

			// Only add links from background modules that are not previously added
			for (BackgroundModuleResponse moduleResponse : moduleResponses) {

				if (moduleResponse.getLinks() != null) {

					linkSet.addAll(moduleResponse.getLinks());
				}
			}

			XMLUtils.append(doc, links, linkSet);
		}
	}

	private void appendScripts(Document doc, ForegroundModuleResponse moduleResponse) {

		Element scripts = doc.createElement("scripts");
		doc.getDocumentElement().appendChild(scripts);

		LinkedHashSet<ScriptTag> scriptsSet = new LinkedHashSet<ScriptTag>();

		if (moduleResponse.getScripts() != null) {

			scriptsSet.addAll(moduleResponse.getScripts());
		}

		if (moduleResponse.getBackgroundModuleResponses() != null) {

			for (BackgroundModuleResponse backgroundModuleResponse : moduleResponse.getBackgroundModuleResponses()) {

				if (backgroundModuleResponse.getScripts() != null) {

					scriptsSet.addAll(backgroundModuleResponse.getScripts());
				}
			}
		}

		XMLUtils.append(doc, scripts, scriptsSet);
	}

	private void appendScripts(Document doc, List<BackgroundModuleResponse> moduleResponses) {

		if (moduleResponses != null) {

			Element scripts = doc.createElement("scripts");
			doc.getDocumentElement().appendChild(scripts);

			LinkedHashSet<ScriptTag> scriptsSet = new LinkedHashSet<ScriptTag>();

			for (BackgroundModuleResponse backgroundModuleResponse : moduleResponses) {

				if (backgroundModuleResponse.getScripts() != null) {

					scriptsSet.addAll(backgroundModuleResponse.getScripts());
				}
			}

			XMLUtils.append(doc, scripts, scriptsSet);
		}
	}

	private void addBackgroundModuleResponses(List<BackgroundModuleResponse> backgroundModuleResponses, Document doc, User user, HttpServletRequest req) {

		if (backgroundModuleResponses != null) {

			Element backgroundsModuleResponsesElement = doc.createElement("backgroundsModuleResponses");
			doc.getFirstChild().appendChild(backgroundsModuleResponsesElement);

			Document debugDoc;

			// Write xml to file if debug enabled
			if (backgroundModuleXMLDebug && !StringUtils.isEmpty(this.backgroundModuleXMLDebugFile)) {

				debugDoc = XMLUtils.createDomDocument();
				debugDoc.appendChild(debugDoc.createElement("BackgroundModuleDebug"));

			} else {

				debugDoc = null;
			}

			for (BackgroundModuleResponse moduleResponse : backgroundModuleResponses) {

				if (isValidResponse(moduleResponse)) {

					if (moduleResponse.getResponseType() == ResponseType.HTML) {

						// Append module html response
						Element responseElement = doc.createElement("response");
						backgroundsModuleResponsesElement.appendChild(responseElement);

						Element htmlElement = doc.createElement("HTML");
						responseElement.appendChild(htmlElement);

						htmlElement.appendChild(doc.createCDATASection(moduleResponse.getHtml()));

						this.appendSlots(moduleResponse, doc, responseElement);

					} else if (moduleResponse.getResponseType() == ResponseType.XML_FOR_CORE_TRANSFORMATION) {

						// Append module response
						Element responseElement = doc.createElement("response");
						backgroundsModuleResponsesElement.appendChild(responseElement);

						Element xmlElement = doc.createElement("XML");
						responseElement.appendChild(xmlElement);

						xmlElement.appendChild(doc.adoptNode(moduleResponse.getElement()));

						this.appendSlots(moduleResponse, doc, responseElement);

					} else if (moduleResponse.getResponseType() == ResponseType.XML_FOR_SEPARATE_TRANSFORMATION) {

						if (moduleResponse.getTransformer() != null) {

							//Append XML to debug document
							if (debugDoc != null) {

								this.log.debug("Background XML debug mode enabled, appending XML from module " + moduleResponse.getModuleDescriptor() + " to XML debug document");

								try {
									Element documentElement = (Element) debugDoc.importNode(moduleResponse.getDocument().getDocumentElement(), true);

									if(moduleResponse.getModuleDescriptor() != null){

										documentElement.setAttribute("moduleID", moduleResponse.getModuleDescriptor().getModuleID() + "");
										documentElement.setAttribute("name", moduleResponse.getModuleDescriptor().getName());
									}

									debugDoc.getDocumentElement().appendChild(documentElement);

								} catch (Exception e) {

									this.log.error("Error appending XML from module " + moduleResponse.getModuleDescriptor() + " to  XML debug document", e);
								}
							}

							// Transform output
							try {
								StringWriter stringWriter = new StringWriter();
								this.log.debug("Background module XML transformation starting");

								XMLTransformer.transformToWriter(moduleResponse.getTransformer(), moduleResponse.getDocument(), stringWriter, encoding);

								this.log.debug("Background module XML transformation finished, appending result...");

								// Append module response

								Element responseElement = doc.createElement("response");
								backgroundsModuleResponsesElement.appendChild(responseElement);

								Element htmlElement = doc.createElement("HTML");
								responseElement.appendChild(htmlElement);

								htmlElement.appendChild(doc.createCDATASection(stringWriter.toString()));

								this.appendSlots(moduleResponse, doc, responseElement);

								this.log.debug("Result appended");
							} catch (Exception e) {
								this.log.error("Tranformation of background module response from module" + moduleResponse.getModuleDescriptor() + " failed while processing request from user " + user + " accessing from " + req.getRemoteAddr(), e);
							}
						} else {
							this.log.error("Background module response for separate transformation without attached stylesheet returned by module " + moduleResponse.getModuleDescriptor() + " while processing request from user " + user + " accessing from " + req.getRemoteAddr());
						}
					}
				}
			}

			//Write background module XML debug to file
			if(debugDoc != null && debugDoc.getDocumentElement().hasChildNodes()){

				log.debug("Writing background module XML debug to file " + backgroundModuleXMLDebugFile);

				try {
					XMLUtils.writeXMLFile(debugDoc, this.applicationFileSystemPath + "WEB-INF/" + backgroundModuleXMLDebugFile, true, encoding);

				} catch (Exception e) {

					log.error("Error writing background module XML debug to file " + backgroundModuleXMLDebugFile, e);
				}
			}
		}
	}

	private void appendSlots(BackgroundModuleResponse moduleResponse, Document doc, Element moduleres) {

		if (!CollectionUtils.isEmpty(moduleResponse.getSlots())) {

			Element slotsElement = doc.createElement("slots");
			moduleres.appendChild(slotsElement);

			for (String slot : moduleResponse.getSlots()) {

				slotsElement.appendChild(XMLUtils.createCDATAElement("slot", slot, doc));
			}
		}
	}

	private boolean isValidResponse(ModuleResponse moduleResponse) {

		ResponseType responseType = moduleResponse.getResponseType();

		if (responseType == null) {
			return false;
		} else if (responseType == ResponseType.XML_FOR_CORE_TRANSFORMATION && moduleResponse.getElement() != null) {
			return true;
		} else if (responseType == ResponseType.HTML && moduleResponse.getHtml() != null) {
			return true;
		} else if (responseType == ResponseType.XML_FOR_SEPARATE_TRANSFORMATION && moduleResponse.getDocument() != null) {
			return true;
		} else {
			return false;
		}
	}

	private User getUser(HttpServletRequest req) {

		// Get the session
		HttpSession session = req.getSession(false);

		if (session != null) {
			try {
				// Get the user from the session
				Object object = session.getAttribute("user");

				if (object != null) {

					if (object instanceof User) {
						User user = (User) object;

						// Check if the session is attached to the user or if it has been lost during serialization
						if (user.getSession() == null) {
							this.log.info("Session for user " + user + " removed from user object during serialization, reconnecting session to user.");
							session.removeAttribute("user");
							session.setAttribute("user", user);
						}

						return user;

					} else {
						this.log.warn("Unknown object type set in session attribute \"user\" in request from " + req.getRemoteAddr() + ". Removing attribute from session.");
						session.removeAttribute("user");
					}
				}
			} catch (IllegalStateException e) {}
		}

		return null;
	}

	@Override
	public UserHandler getUserHandler() {

		return this.userHandler;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see se.unlogic.hierarchy.core.beans.SystemInterface#getRootSection()
	 */
	@Override
	public Section getRootSection() {

		return this.rootSection;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see se.unlogic.hierarchy.core.beans.SystemInterface#getApplicationFileSystemPath()
	 */
	@Override
	public String getApplicationFileSystemPath() {

		return this.applicationFileSystemPath;
	}

	@Override
	public Language getDefaultLanguage() {

		return this.defaultLanguage;
	}

	public void setDefaultLanguage(Language defaultLanguage) {

		this.defaultLanguage = defaultLanguage;
	}

	@Override
	public LoginHandler getLoginHandler(){

		return loginHandler;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see se.unlogic.hierarchy.core.beans.SystemInterface#getDataSource()
	 */
	@Override
	public DataSource getDataSource() {

		return this.dataSource;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see se.unlogic.hierarchy.core.beans.SystemInterface#getDataSourceCache()
	 */
	@Override
	public DataSourceCache getDataSourceCache() {

		return this.dataSourceCache;
	}

	@Override
	public void destroy() {

		try {
			setSystemStatus(SystemStatus.STOPPING);

			long startTime = System.currentTimeMillis();

			this.log.fatal("Stopping " + VERSION + "...");

			if (this.rootSection != null) {
				this.rootSection.unload();
			}

			if (this.stopableEmailHandler != null) {

				this.stopableEmailHandler.stop();

				if (this.stopableEmailHandler.hasSenders()) {

					log.warn(this.stopableEmailHandler.getSenderCount() + " email sender(s) present after section and module shutdown, manually removing sender.");

					this.stopableEmailHandler.removeSenders();
				}

				log.info("Email handler stopped");
			}

			if (this.userHandler != null) {
				this.userHandler.clear();
			}

			if (this.systemInstanceHandler != null) {
				this.systemInstanceHandler.clear();
			}

			if (this.eventHandler != null) {
				this.eventHandler.clear();
			}

			if(this.systemSessionListenerHandler != null){
				this.systemSessionListenerHandler.clear();
			}

			if (this.dataSourceCache != null) {
				this.dataSourceCache.unload();
			}

			if (this.dataSourceType != null && dataSourceType == DataSourceType.SystemManaged) {

				try {
					((BasicDataSource) this.dataSource).close();
				} catch (SQLException e) {
					log.error("Error closing system datasource " + dataSource, e);
				}
			}

			setSystemStatus(SystemStatus.STOPPED);
			this.log.fatal("***** " + VERSION + " stopped in " + TimeUtils.millisecondsToString(System.currentTimeMillis() - startTime) + " ms *****");
		} catch (Exception e) {

			if (this.log != null) {
				this.log.error("Error shutting down system!", e);
			}
		}
	}

	@Override
	public CoreDaoFactory getCoreDaoFactory() {

		return this.coreDaoFactory;
	}

	@Override
	public EmailHandler getEmailHandler() {

		return this.stopableEmailHandler;
	}

	@Override
	public boolean isModuleXMLDebug() {

		return moduleXMLDebug;
	}

	@Override
	public void setModuleXMLDebug(boolean moduleXMLDebug) {

		this.moduleXMLDebug = moduleXMLDebug;
	}

	@Override
	public String getModuleXMLDebugFile() {

		return moduleXMLDebugFile;
	}

	@Override
	public void setModuleXMLDebugFile(String moduleXMLDebugFile) {

		this.moduleXMLDebugFile = moduleXMLDebugFile;
	}

	@Override
	public boolean isSystemXMLDebug() {

		return systemXMLDebug;
	}

	@Override
	public void setSystemXMLDebug(boolean systemXMLDebug) {

		this.systemXMLDebug = systemXMLDebug;
	}

	@Override
	public String getSystemXMLDebugFile() {

		return systemXMLDebugFile;
	}

	@Override
	public void setSystemXMLDebugFile(String systemXMLDebugFile) {

		this.systemXMLDebugFile = systemXMLDebugFile;
	}

	@Override
	public String getEncoding() {

		return encoding;
	}

	@Override
	public void setEncoding(String encoding) {

		this.encoding = encoding;
	}

	@Override
	public boolean isBackgroundModuleXMLDebug() {

		return backgroundModuleXMLDebug;
	}

	@Override
	public void setBackgroundModuleXMLDebug(boolean backgroundModuleXMLDebug) {

		this.backgroundModuleXMLDebug = backgroundModuleXMLDebug;
	}

	@Override
	public boolean addBackgroundModuleCacheListener(BackgroundModuleCacheListener listener) {

		return globalBackgroundModuleCacheListener.add(listener);
	}

	@Override
	public boolean removeBackgroundModuleCacheListener(BackgroundModuleCacheListener listener) {

		return globalBackgroundModuleCacheListener.remove(listener);
	}

	@Override
	public boolean addForegroundModuleCacheListener(ForegroundModuleCacheListener listener) {

		return globalForegroundModuleCacheListener.add(listener);
	}

	@Override
	public boolean removeForegroundModuleCacheListener(ForegroundModuleCacheListener listener) {

		return globalForegroundModuleCacheListener.remove(listener);
	}

	@Override
	public boolean addSectionCacheListener(SectionCacheListener listener) {

		return globalSectionCacheListener.add(listener);
	}

	@Override
	public boolean removeSectionCacheListener(SectionCacheListener listener) {

		return globalSectionCacheListener.remove(listener);
	}

	@Override
	public GlobalSectionCacheListener getGlobalSectionCacheListener() {

		return globalSectionCacheListener;
	}

	@Override
	public GlobalForegroundModuleCacheListener getGlobalForegroundModuleCacheListener() {

		return globalForegroundModuleCacheListener;
	}

	@Override
	public GlobalBackgroundModuleCacheListener getGlobalBackgroundModuleCacheListener() {

		return globalBackgroundModuleCacheListener;
	}

	@Override
	public CoreXSLTCacheHandler getCoreXSLTCacheHandler() {

		return this.xsltCacheHandler;
	}

	@Override
	public FilterModuleCache getFilterModuleCache() {

		return this.filterModuleCache;
	}

	@Override
	public InstanceHandler getInstanceHandler() {

		return this.systemInstanceHandler;
	}

	@Override
	public EventHandler getEventHandler() {

		return eventHandler;
	}

	@Override
	public String getBackgroundModuleXMLDebugFile() {

		return backgroundModuleXMLDebugFile;
	}

	@Override
	public void setBackgroundModuleXMLDebugFile(String backgroundModuleXMLDebugFile) {

		this.backgroundModuleXMLDebugFile = backgroundModuleXMLDebugFile;
	}

	@Override
	public GroupHandler getGroupHandler() {

		return groupHandler;
	}

	@Override
	public Section getSectionInterface(Integer sectionID){

		return sectionMap.get(sectionID);
	}

	@Override
	public void addSection(Section section) {

		if(sectionMap.putIfAbsent(section.getSectionDescriptor().getSectionID(), section) != null){

			log.warn("Section " + section.getSectionDescriptor() + " is already present in section map");
		}
	}

	@Override
	public void removeSection(Section section){

		if(!sectionMap.remove(section.getSectionDescriptor().getSectionID(), section)){

			log.warn("Unable to find section " + section.getSectionDescriptor() + " in section map");
		}
	}

	@Override
	public SystemSessionListenerHandler getSessionListenerHandler() {

		return systemSessionListenerHandler;
	}
}
