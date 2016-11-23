/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.sections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;

import org.apache.log4j.Logger;

import se.unlogic.hierarchy.core.beans.Breadcrumb;
import se.unlogic.hierarchy.core.beans.MenuItem;
import se.unlogic.hierarchy.core.beans.SectionMenu;
import se.unlogic.hierarchy.core.beans.SectionMenuItem;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.cache.BackgroundModuleCache;
import se.unlogic.hierarchy.core.cache.BackgroundModuleXSLTCache;
import se.unlogic.hierarchy.core.cache.ForegroundModuleCache;
import se.unlogic.hierarchy.core.cache.ForegroundModuleXSLTCache;
import se.unlogic.hierarchy.core.cache.MenuItemCache;
import se.unlogic.hierarchy.core.cache.SectionCache;
import se.unlogic.hierarchy.core.comparators.PriorityComparator;
import se.unlogic.hierarchy.core.enums.HTTPProtocol;
import se.unlogic.hierarchy.core.enums.ResponseType;
import se.unlogic.hierarchy.core.enums.SectionStatus;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.exceptions.ForegroundNullResponseException;
import se.unlogic.hierarchy.core.exceptions.ProtocolRedirectException;
import se.unlogic.hierarchy.core.exceptions.RequestException;
import se.unlogic.hierarchy.core.exceptions.SectionDefaultURINotFoundException;
import se.unlogic.hierarchy.core.exceptions.SectionDefaultURINotSetException;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.exceptions.UnhandledModuleException;
import se.unlogic.hierarchy.core.interfaces.BackgroundModule;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.ForegroundModule;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.FullSectionInterface;
import se.unlogic.hierarchy.core.interfaces.FullSystemInterface;
import se.unlogic.hierarchy.core.interfaces.ModuleAccessDeniedHandler;
import se.unlogic.hierarchy.core.interfaces.RootSectionInterface;
import se.unlogic.hierarchy.core.interfaces.SectionAccessDeniedHandler;
import se.unlogic.hierarchy.core.interfaces.SectionDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.utils.AccessUtils;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.enums.Order;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.webutils.http.URIParser;

public class Section implements RootSectionInterface, FullSectionInterface {

	private static final PriorityComparator PRIORITY_COMPARATOR = new PriorityComparator(Order.ASC);
	
	protected Logger log = Logger.getLogger(this.getClass());

	private ForegroundModuleCache foregroundModuleCache;
	private BackgroundModuleCache backgroundModuleCache;
	private MenuItemCache menuCache;
	private ForegroundModuleXSLTCache foregroundModuleXSLTCache;
	private BackgroundModuleXSLTCache backgroundModuleXSLTCache;
	private SectionCache sectionCache;
	private FullSystemInterface systemInterface;
	private SectionDescriptor sectionDescriptor;
	private SectionInterface parentSectionInterface;
	private SectionStatus sectionStatus;

	private ArrayList<SectionAccessDeniedHandler> sectionAccessDeniedHandlers;
	private ArrayList<ModuleAccessDeniedHandler> moduleAccessDeniedHandlers;
	
	protected final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	protected final Lock r = rwl.readLock();
	protected final Lock w = rwl.writeLock();
	
	private final ReentrantReadWriteLock sectionAccessDeniedHandlerLock = new ReentrantReadWriteLock();
	private final Lock sectionAccessDeniedHandlerReadLock = sectionAccessDeniedHandlerLock.readLock();
	private final Lock sectionAccessDeniedHandlerWriteLock = sectionAccessDeniedHandlerLock.writeLock();
	
	private final ReentrantReadWriteLock moduleAccessDeniedHandlerLock = new ReentrantReadWriteLock();
	private final Lock moduleAccessDeniedHandlerReadLock = moduleAccessDeniedHandlerLock.readLock();
	private final Lock moduleAccessDeniedHandlerWriteLock = moduleAccessDeniedHandlerLock.writeLock();		

	public Section(SectionDescriptor sectionDescriptor, SectionInterface parentSectionInterface, FullSystemInterface systemInterface) {

		w.lock();
		try {
			log.info("Section " + sectionDescriptor + " starting...");

			sectionStatus = SectionStatus.STARTING;
			
			this.sectionDescriptor = sectionDescriptor;

			this.parentSectionInterface = parentSectionInterface;

			this.systemInterface = systemInterface;

			menuCache = new MenuItemCache(systemInterface.getCoreDaoFactory(), sectionDescriptor);
			foregroundModuleXSLTCache = new ForegroundModuleXSLTCache(systemInterface.getApplicationFileSystemPath());
			foregroundModuleCache = new ForegroundModuleCache(this);
			foregroundModuleCache.addCacheListener(menuCache);
			foregroundModuleCache.addCacheListener(foregroundModuleXSLTCache);
			foregroundModuleCache.addCacheListener(systemInterface.getGlobalForegroundModuleCacheListener());

			backgroundModuleXSLTCache = new BackgroundModuleXSLTCache(systemInterface.getApplicationFileSystemPath());
			backgroundModuleCache = new BackgroundModuleCache(this);
			backgroundModuleCache.addCacheListener(backgroundModuleXSLTCache);
			backgroundModuleCache.addCacheListener(systemInterface.getGlobalBackgroundModuleCacheListener());

			sectionCache = new SectionCache(this);
			sectionCache.addCacheListener(menuCache);
			sectionCache.addCacheListener(systemInterface.getGlobalSectionCacheListener());

			sectionStatus = SectionStatus.STARTED;
			
			systemInterface.addSection(this);

			log.info("Section " + this.sectionDescriptor + " started");
		} finally {
			w.unlock();
		}
	}

	public void cacheModuleAndSections(){
		
		w.lock();
		try {		
		
			log.info("Section " + this.sectionDescriptor + " caching modules...");
	
			try {
				this.foregroundModuleCache.cacheModules(false);
				this.backgroundModuleCache.cacheModules(false);
			} catch (Exception e) {
				log.error("Error caching modules for section " + this.sectionDescriptor, e);
			}
	
			log.info("Section " + this.sectionDescriptor + " caching subscections...");
	
			try {
				this.sectionCache.cacheSections();
			} catch (Exception e) {
				log.error("Error caching subsections for section " + this.sectionDescriptor, e);
			}
		
		} finally {
			w.unlock();
		}		
	}
	
	@Override
	public void update(SectionDescriptor sectionDescriptor) {

		w.lock();
		try {
			this.sectionDescriptor = sectionDescriptor;
			this.menuCache.setSectionDescriptor(sectionDescriptor);

			//TODO reload the menu cache of this section all subsections

		} finally {
			w.unlock();
		}
	}

	public void unload() {

		w.lock();
		try {
			log.info("Unloading section " + this.sectionDescriptor);

			sectionStatus = SectionStatus.STOPPING;
			
			this.foregroundModuleCache.unload();
			this.backgroundModuleCache.unload();
			this.sectionCache.unload();

			systemInterface.removeSection(this);

			sectionStatus = SectionStatus.STOPPED;
			
			log.info("Section " + this.sectionDescriptor + " unloaded");
		} finally {
			w.unlock();
		}
	}

	public ForegroundModuleResponse processRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, HTTPProtocol enforcedHTTPProtocol) throws RequestException {

		r.lock();

		if (this.sectionDescriptor.getRequiredProtocol() != null) {

			enforcedHTTPProtocol = this.sectionDescriptor.getRequiredProtocol();
		}

		boolean isDefaultURI = false;

		try {
			if (uriParser.size() == 0) {
				if (user == null) {

					if(StringUtils.isEmpty(sectionDescriptor.getAnonymousDefaultURI())){

						throw new SectionDefaultURINotSetException(sectionDescriptor, false);

					}

					uriParser.addToURI(sectionDescriptor.getAnonymousDefaultURI());
					isDefaultURI = true;

				} else {

					if(StringUtils.isEmpty(sectionDescriptor.getUserDefaultURI())){

						throw new SectionDefaultURINotSetException(sectionDescriptor, true);
					}

					uriParser.addToURI(sectionDescriptor.getUserDefaultURI());
					isDefaultURI = true;
				}
			}

			Entry<SectionDescriptor, Section> sectionCacheEntry;

			if (uriParser.size() >= 1 && (sectionCacheEntry = this.sectionCache.getEntry(uriParser.get(0))) != null) {

				if (AccessUtils.checkAccess(user, sectionCacheEntry.getKey())) {

					ForegroundModuleResponse moduleResponse = sectionCacheEntry.getValue().processRequest(req, res, user, uriParser.getNextLevel(), enforcedHTTPProtocol);

					if (moduleResponse != null && !res.isCommitted()) {

						// Check if the user has changed
						if (moduleResponse.isUserChanged()) {
							user = (User) req.getSession(true).getAttribute("user");
						}

						moduleResponse.setMenu(this.menuCache.getUserMenu(user,moduleResponse.getMenu(),uriParser));

						if (this.sectionDescriptor.hasBreadCrumb() && !moduleResponse.isExcludeSectionBreadcrumbs()) {
							moduleResponse.addBreadcrumbFirst(getBreadcrumb());
						}

						List<BackgroundModuleResponse> backgroundModuleResponses = this.getBackgroundModuleResponses(req, user, uriParser);

						if (backgroundModuleResponses != null) {

							moduleResponse.addBackgroundModuleResponses(backgroundModuleResponses);
						}
					}

					return moduleResponse;

				} else {
					
					sectionAccessDeniedHandlerReadLock.lock();
					
					try{

						if(sectionAccessDeniedHandlers != null){
							
							for(SectionAccessDeniedHandler handler : sectionAccessDeniedHandlers){
								
								try {
									
									if(handler.supportsRequest(req, user, uriParser, sectionCacheEntry.getKey())) {

										handler.handleRequest(req, res, user, uriParser, sectionCacheEntry.getKey());
										
										break;
									}
									
								} catch (Throwable e) {
									
									log.error("Error in section access denied handler " + handler, e);
								}
							}
							
							if(res.isCommitted()){
								
								return null;
							}
						}						
						
					}finally{
						
						sectionAccessDeniedHandlerReadLock.unlock();
					}
					
					throw new AccessDeniedException(sectionCacheEntry.getKey());
				}
			}

			Entry<ForegroundModuleDescriptor, ForegroundModule> moduleCacheEntry;

			if (uriParser.size() >= 1 && (moduleCacheEntry = this.foregroundModuleCache.getEntry(uriParser.get(0))) != null) {

				if (AccessUtils.checkAccess(user, moduleCacheEntry.getKey())) {

					HTTPProtocol requiredHTTPProtocol = moduleCacheEntry.getKey().getRequiredProtocol();
					HTTPProtocol currentHTTPProtocol = req.isSecure() ? HTTPProtocol.HTTPS : HTTPProtocol.HTTP;

					try {

						// Switch to module required protocol
						if (requiredHTTPProtocol != null && !currentHTTPProtocol.equals(requiredHTTPProtocol)) {

							res.sendRedirect(requiredHTTPProtocol.toString().toLowerCase() + "://" + req.getServerName() + req.getContextPath() + uriParser.getFormattedURI());

							return null;

							// Switch to section required protocol
						}else if (requiredHTTPProtocol == null && enforcedHTTPProtocol != null && !currentHTTPProtocol.equals(enforcedHTTPProtocol)) {

							res.sendRedirect(enforcedHTTPProtocol.toString().toLowerCase() + "://" + req.getServerName() + req.getContextPath() + uriParser.getFormattedURI());

							return null;
						}

					} catch (IOException e) {

						throw new ProtocolRedirectException(this.sectionDescriptor, moduleCacheEntry.getKey(), e);
					}

					try {
						ForegroundModuleResponse moduleResponse = moduleCacheEntry.getValue().processRequest(req, res, user, uriParser);

						if (!res.isCommitted()) {

							if (moduleResponse != null) {

								// Check if the user has changed
								if (moduleResponse.isUserChanged()) {
									user = (User) req.getSession(true).getAttribute("user");
								}

								moduleResponse.setMenu(this.menuCache.getUserMenu(user,null,uriParser));

								if (moduleResponse.getResponseType() == ResponseType.XML_FOR_SEPARATE_TRANSFORMATION && moduleResponse.getTransformer() == null) {
									moduleResponse.setTransformer(this.foregroundModuleXSLTCache.getModuleTranformer(moduleCacheEntry.getKey()));
								}

								moduleResponse.setModuleDescriptor(moduleCacheEntry.getKey());

								if (this.sectionDescriptor.hasBreadCrumb() && !moduleResponse.isExcludeSectionBreadcrumbs()) {
									moduleResponse.addBreadcrumbFirst(getBreadcrumb());
								}

								List<BackgroundModuleResponse> backgroundModuleResponses = this.getBackgroundModuleResponses(req, user, uriParser);

								if (backgroundModuleResponses != null) {

									moduleResponse.addBackgroundModuleResponses(backgroundModuleResponses);
								}

							} else {

								throw new ForegroundNullResponseException();
							}
						}

						return moduleResponse;

					} catch (RequestException e) {

						e.setSectionDescriptor(sectionDescriptor);
						e.setModuleDescriptor(moduleCacheEntry.getKey());

						throw e;

					} catch (Throwable t) {

						throw new UnhandledModuleException(this.sectionDescriptor, moduleCacheEntry.getKey(), t);

					}
				} else {

					moduleAccessDeniedHandlerReadLock.lock();
					
					try{
						if(moduleAccessDeniedHandlers != null){
							
							for(ModuleAccessDeniedHandler handler : moduleAccessDeniedHandlers){
								
								try {
									
									if(handler.supportsRequest(req, user, uriParser, moduleCacheEntry.getKey())) {

										handler.handleRequest(req, res, user, uriParser, moduleCacheEntry.getKey());
										
										break;
									}
									
								} catch (Throwable e) {
									
									log.error("Error in module access denied handler " + handler, e);
								}
							}
							
							if(res.isCommitted()){
								
								return null;
							}
						}					
						
					}finally{
						
						moduleAccessDeniedHandlerReadLock.unlock();
					}				
					
					throw new AccessDeniedException(this.sectionDescriptor, moduleCacheEntry.getKey());
				}
			}

			if(isDefaultURI){

				throw new SectionDefaultURINotFoundException(sectionDescriptor, uriParser, user != null);
			}

			throw new URINotFoundException(this.sectionDescriptor, uriParser);

		} catch (RequestException e) {

			e.setMenu(this.menuCache.getUserMenu(user,e.getMenu(),uriParser));

			List<BackgroundModuleResponse> backgroundModuleResponses = this.getBackgroundModuleResponses(req, user, uriParser);

			if (backgroundModuleResponses != null) {

				e.addBackgroundModuleResponses(backgroundModuleResponses);
			}

			throw e;

		} finally {
			r.unlock();
		}
	}

	@Override
	public Breadcrumb getBreadcrumb() {
		return new Breadcrumb(this.sectionDescriptor);
	}

	private List<BackgroundModuleResponse> getBackgroundModuleResponses(HttpServletRequest req, User user, URIParser uriParser) {

		List<BackgroundModuleResponse> bgResponses = null;

		List<Entry<BackgroundModuleDescriptor, BackgroundModule>> backgroundModuleEntries = this.backgroundModuleCache.getEntries(uriParser.getRemainingURI(), user);

		if (!CollectionUtils.isEmpty(backgroundModuleEntries)) {

			for (Entry<BackgroundModuleDescriptor, BackgroundModule> bgEntry : backgroundModuleEntries) {

				try {
					BackgroundModuleResponse response = bgEntry.getValue().processRequest(req, user, uriParser);

					if (response != null) {

						if (CollectionUtils.isEmpty(response.getSlots())) {

							response.setSlots(bgEntry.getKey().getSlots());
						}

						if (response.getResponseType() == ResponseType.XML_FOR_SEPARATE_TRANSFORMATION && response.getTransformer() == null) {

							Transformer transformer = this.backgroundModuleXSLTCache.getModuleTranformer(bgEntry.getKey());

							if (transformer != null) {

								response.setTransformer(transformer);
							}
						}

						response.setModuleDescriptor(bgEntry.getKey());

						if (bgResponses == null) {

							bgResponses = new ArrayList<BackgroundModuleResponse>();
						}

						bgResponses.add(response);
					}

				} catch (Throwable t) {

					log.error("Error thrown from background module " + bgEntry.getKey() + " in section " + this.sectionDescriptor + " while processing request for user " + user, t);
				}
			}
		}
		return bgResponses;
	}

	@Override
	public int getReadLockCount() {

		return rwl.getReadLockCount();
	}

	public int getSectionAccessDeniedHandlerLockCount(){
		
		return sectionAccessDeniedHandlerLock.getReadLockCount();
	}
	
	public int getModuleAccessDeniedHandlerLockCount(){
		
		return moduleAccessDeniedHandlerLock.getReadLockCount();
	}	
	
	@Override
	public ForegroundModuleCache getForegroundModuleCache() {

		return foregroundModuleCache;
	}

	@Override
	public BackgroundModuleCache getBackgroundModuleCache() {

		return backgroundModuleCache;
	}

	@Override
	public MenuItemCache getMenuCache() {

		return menuCache;
	}

	@Override
	public ForegroundModuleXSLTCache getModuleXSLTCache() {

		return foregroundModuleXSLTCache;
	}

	@Override
	public SectionCache getSectionCache() {

		return sectionCache;
	}

	@Override
	public FullSystemInterface getSystemInterface() {

		return systemInterface;
	}

	@Override
	public SectionDescriptor getSectionDescriptor() {

		return sectionDescriptor;
	}

	@Override
	public SectionInterface getParentSectionInterface() {

		return parentSectionInterface;
	}

	public SectionMenu getFullMenu(User user, URIParser uriParser) {

		r.lock();

		try{

			SectionMenu sectionMenu = menuCache.getUserMenu(user, null, uriParser);

			if(!sectionMenu.getMenuItems().isEmpty()){

				int index = 0;

				while(index < sectionMenu.getMenuItems().size()){

					MenuItem menuItem = sectionMenu.getMenuItems().get(index);

					//Check if this menuitem represents a section
					if(menuItem instanceof SectionMenuItem){

						Integer sectionID = ((SectionMenuItem)menuItem).getSubSectionID();

						Entry<SectionDescriptor, Section> entry = sectionCache.getEntry(sectionID);

						if(entry != null){

							Section subSection = sectionCache.getSectionInstance(entry.getKey());

							if(subSection != null){

								URIParser sentURIParser;

								if(uriParser != null && menuItem.isSelected()){

									sentURIParser = uriParser.getNextLevel();

								}else{

									sentURIParser = null;
								}

								//Replace previous menuitem for this index with new one containing the submenus for the relevant section
								SectionMenu subSectionMenu = subSection.getFullMenu(user, sentURIParser);

								menuItem = ((SectionMenuItem)menuItem).clone(subSectionMenu, false);

								sectionMenu.getMenuItems().set(index, menuItem);
							}
						}
					}

					index++;
				}
			}

			return sectionMenu;

		}finally{

			r.unlock();
		}
	}

	@Override
	public SectionStatus getSectionStatus() {

		return sectionStatus;
	}

	@Override
	public boolean addSectionAccessDeniedHandler(SectionAccessDeniedHandler handler){
		
		if(handler == null){
			
			return false;
		}
		
		sectionAccessDeniedHandlerWriteLock.lock();
		
		try {

			if(sectionAccessDeniedHandlers == null){
				
				sectionAccessDeniedHandlers = new ArrayList<SectionAccessDeniedHandler>();
				return sectionAccessDeniedHandlers.add(handler);
			
			}else{
			
				if (!sectionAccessDeniedHandlers.contains(handler)) {

					sectionAccessDeniedHandlers.add(handler);

					Collections.sort(sectionAccessDeniedHandlers, PRIORITY_COMPARATOR);

					return true;
				}

				return false;
			}
			
		} finally {
			sectionAccessDeniedHandlerWriteLock.unlock();
		}
	}
	
	@Override
	public boolean removeSectionAccessDeniedHandler(SectionAccessDeniedHandler handler){
		
		sectionAccessDeniedHandlerWriteLock.lock();
		
		try {

			if(sectionAccessDeniedHandlers == null){
				
				return false;
				
			}else {
				
				return sectionAccessDeniedHandlers.remove(handler);
			}
			
		} finally {
			
			if(sectionAccessDeniedHandlers != null && sectionAccessDeniedHandlers.isEmpty()){
				
				sectionAccessDeniedHandlers = null;
			}
			
			sectionAccessDeniedHandlerWriteLock.unlock();
		}
	}
	
	@Override
	public boolean addModuleAccessDeniedHandler(ModuleAccessDeniedHandler handler){
		
		if(handler == null){
			
			return false;
		}
		
		moduleAccessDeniedHandlerWriteLock.lock();
		
		try {

			if(moduleAccessDeniedHandlers == null){
				
				moduleAccessDeniedHandlers = new ArrayList<ModuleAccessDeniedHandler>();
				return moduleAccessDeniedHandlers.add(handler);
			
			}else{
			
				if (!moduleAccessDeniedHandlers.contains(handler)) {

					moduleAccessDeniedHandlers.add(handler);

					Collections.sort(moduleAccessDeniedHandlers, PRIORITY_COMPARATOR);

					return true;
				}

				return false;
			}
			
		} finally {
			moduleAccessDeniedHandlerWriteLock.unlock();
		}
	}
	
	@Override
	public boolean removeModuleAccessDeniedHandler(ModuleAccessDeniedHandler handler){
		
		moduleAccessDeniedHandlerWriteLock.lock();
		
		try {

			if(moduleAccessDeniedHandlers == null){
				
				return false;
				
			}else {
				
				return moduleAccessDeniedHandlers.remove(handler);
			}
			
		} finally {
			
			if(moduleAccessDeniedHandlers != null && moduleAccessDeniedHandlers.isEmpty()){
				
				moduleAccessDeniedHandlers = null;
			}
			
			moduleAccessDeniedHandlerWriteLock.unlock();
		}
	}	
}
