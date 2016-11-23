/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.interfaces;

import javax.sql.DataSource;

import se.unlogic.emailutils.framework.EmailHandler;
import se.unlogic.hierarchy.core.cache.CoreXSLTCacheHandler;
import se.unlogic.hierarchy.core.cache.DataSourceCache;
import se.unlogic.hierarchy.core.cache.FilterModuleCache;
import se.unlogic.hierarchy.core.daos.factories.CoreDaoFactory;
import se.unlogic.hierarchy.core.enums.SystemStatus;
import se.unlogic.hierarchy.core.handlers.GroupHandler;
import se.unlogic.hierarchy.core.handlers.LoginHandler;
import se.unlogic.hierarchy.core.handlers.UserHandler;
import se.unlogic.standardutils.i18n.Language;

public interface SystemInterface {

	public abstract UserHandler getUserHandler();

	public abstract GroupHandler getGroupHandler();

	public abstract RootSectionInterface getRootSection();

	public abstract String getApplicationFileSystemPath();

	public abstract CoreXSLTCacheHandler getCoreXSLTCacheHandler();

	public Language getDefaultLanguage();

	public abstract DataSource getDataSource();

	public abstract DataSourceCache getDataSourceCache();

	public CoreDaoFactory getCoreDaoFactory();

	public EmailHandler getEmailHandler();

	public LoginHandler getLoginHandler();

	public SystemStatus getSystemStatus();

	public boolean isModuleXMLDebug();

	public void setModuleXMLDebug(boolean moduleXMLDebug);

	public String getModuleXMLDebugFile();

	public void setModuleXMLDebugFile(String moduleXMLDebugFile);

	public boolean isSystemXMLDebug();

	public void setSystemXMLDebug(boolean systemXMLDebug);

	public String getSystemXMLDebugFile();

	public void setSystemXMLDebugFile(String systemXMLDebugFile);

	public String getEncoding();

	public void setEncoding(String encoding);

	public boolean isBackgroundModuleXMLDebug();

	public void setBackgroundModuleXMLDebug(boolean backgroundModuleXMLDebug);

	public String getBackgroundModuleXMLDebugFile();

	public void setBackgroundModuleXMLDebugFile(String backgroundModuleXMLDebugFile);

	public boolean addBackgroundModuleCacheListener(BackgroundModuleCacheListener listener);

	public boolean removeBackgroundModuleCacheListener(BackgroundModuleCacheListener listener);

	public boolean addForegroundModuleCacheListener(ForegroundModuleCacheListener listener);

	public boolean removeForegroundModuleCacheListener(ForegroundModuleCacheListener listener);

	public boolean addSectionCacheListener(SectionCacheListener listener);

	public boolean removeSectionCacheListener(SectionCacheListener listener);

	public FilterModuleCache getFilterModuleCache();

	public void addStartupListener(SystemStartupListener startupListener);

	public InstanceHandler getInstanceHandler();

	public EventHandler getEventHandler();

	public SectionInterface getSectionInterface(Integer sectionID);

	public SessionListenerHandler getSessionListenerHandler();
}
