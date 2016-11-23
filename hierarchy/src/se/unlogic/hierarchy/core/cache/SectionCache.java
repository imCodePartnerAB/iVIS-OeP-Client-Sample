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
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import se.unlogic.hierarchy.core.beans.SimpleSectionDescriptor;
import se.unlogic.hierarchy.core.daos.interfaces.SectionDAO;
import se.unlogic.hierarchy.core.interfaces.FullSectionInterface;
import se.unlogic.hierarchy.core.interfaces.SectionCacheListener;
import se.unlogic.hierarchy.core.interfaces.SectionDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.sections.Section;
import se.unlogic.standardutils.collections.KeyAlreadyCachedException;
import se.unlogic.standardutils.collections.KeyNotCachedException;
import se.unlogic.standardutils.collections.StrictHashMap;
import se.unlogic.standardutils.datatypes.SimpleEntry;

public class SectionCache {

	private final Logger log = Logger.getLogger(this.getClass());

	private final FullSectionInterface sectionInterface;
	private final SectionDAO sectionDAO;
	private final StrictHashMap<SectionDescriptor, Section> sectionInstanceMap = new StrictHashMap<SectionDescriptor, Section>();
	private final ArrayList<SectionCacheListener> cacheListeners = new ArrayList<SectionCacheListener>();
	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final Lock r = rwl.readLock();
	private final Lock w = rwl.writeLock();

	public SectionCache(FullSectionInterface sectionInterface) {

		this.sectionInterface = sectionInterface;
		this.sectionDAO = sectionInterface.getSystemInterface().getCoreDaoFactory().getSectionDAO();
	}

	public void cacheSections() throws KeyNotCachedException, KeyAlreadyCachedException, SQLException {

		w.lock();
		try {

			// Get all enabled modules from the database
			ArrayList<SimpleSectionDescriptor> sectionBeanList = sectionDAO.getEnabledSubSections(this.sectionInterface.getSectionDescriptor(), false);

			if (sectionBeanList != null && sectionBeanList.size() > 0) {

				log.debug("Checking section cache for subsections to unload in section " + this.sectionInterface.getSectionDescriptor() + "...");

				// Check if the cache is empty
				if (!this.sectionInstanceMap.isEmpty()) {

					// Loop thru the cache and remove all modules not found in the database
					ArrayList<SectionDescriptor> keys = new ArrayList<SectionDescriptor>(sectionInstanceMap.keySet());

					for (SectionDescriptor sectionDescriptor : keys) {

						if (!sectionBeanList.contains(sectionDescriptor)) {
							log.info("Subsection" + sectionDescriptor + " not found in new subsection list for section " + this.sectionInterface.getSectionDescriptor() + ", removing from cache...");

							this.unload(sectionDescriptor);
						}
					}

					log.debug("Finished checking cache for subsections to unload in section " + this.sectionInterface.getSectionDescriptor());
				} else {
					log.debug("Cache is empty, no subsections to unload in section " + this.sectionInterface.getSectionDescriptor());
				}

				// Add new modules to cache and update cached modules
				log.debug("Adding new subsections to cache and refreshing cached subsections for section " + this.sectionInterface.getSectionDescriptor() + "...");

				for (SimpleSectionDescriptor simpleSectionDescriptor : sectionBeanList) {
					if (this.sectionInstanceMap.containsKey(simpleSectionDescriptor)) {
						// Module already cached, update module
						this.update(simpleSectionDescriptor);
					} else {
						// Cache new module
						log.info("Adding subsection " + simpleSectionDescriptor + " to cache for section " + this.sectionInterface.getSectionDescriptor() + "...");
						try {
							this.cache(simpleSectionDescriptor);
						} catch (Exception e) {
							log.error("Error caching new instance of subsection " + simpleSectionDescriptor + " for section " + this.sectionInterface.getSectionDescriptor(), e);
						}
					}
				}
				log.debug("Finished adding new subsections and updating cached subsections in section " + this.sectionInterface.getSectionDescriptor());

			} else {
				log.debug("No subsections found in database, for section " + this.sectionInterface.getSectionDescriptor());
				this.unload();
			}
		} finally {
			w.unlock();
		}
	}

	public SectionInterface cache(SimpleSectionDescriptor simpleSectionDescriptor) {

		w.lock();
		try {
			Section sectionInstance = new Section(simpleSectionDescriptor, this.sectionInterface, this.sectionInterface.getSystemInterface());

			this.sectionInstanceMap.put(simpleSectionDescriptor, sectionInstance);

			for (SectionCacheListener cl : this.cacheListeners) {
				cl.sectionCached(simpleSectionDescriptor, sectionInstance);
			}

			sectionInstance.cacheModuleAndSections();
			
			log.debug("Subsection" + simpleSectionDescriptor + " added to section cache of section " + this.sectionInterface.getSectionDescriptor());
			
			return sectionInstance;
			
		} catch (KeyAlreadyCachedException e) {
			throw e;
		} finally {
			w.unlock();
		}
	}

	public void update(SectionDescriptor sectionDescriptor) throws KeyNotCachedException {

		w.lock();
		try {
			Section sectionInstance = this.sectionInstanceMap.get(sectionDescriptor);

			if (sectionInstance != null) {
				sectionInstance.update(sectionDescriptor);

				this.sectionInstanceMap.update(sectionDescriptor, sectionInstance);

				for (SectionCacheListener cl : this.cacheListeners) {
					cl.sectionUpdated(sectionDescriptor, sectionInstance);
				}
			} else {
				throw new KeyNotCachedException(sectionDescriptor);
			}
		} finally {
			w.unlock();
		}
	}

	public void unload(SectionDescriptor sectionDescriptor) throws KeyNotCachedException {

		w.lock();
		try {
			Section sectionInstance = this.sectionInstanceMap.get(sectionDescriptor);
			this.sectionInstanceMap.remove(sectionDescriptor);

			for (SectionCacheListener cl : this.cacheListeners) {
				cl.sectionUnloaded(sectionDescriptor, sectionInstance);
			}

			sectionInstance.unload();
		} finally {
			w.unlock();
		}
	}

	public void unload() {

		w.lock();
		try {
			ArrayList<SectionDescriptor> sectionList = new ArrayList<SectionDescriptor>(this.sectionInstanceMap.keySet());

			for (SectionDescriptor sectionDescriptor : sectionList) {
				try {
					this.unload(sectionDescriptor);
				} catch (KeyNotCachedException e) {
					log.error("Error unloading subsection " + sectionDescriptor + " from subsection cache for section " + this.sectionInterface.getSectionDescriptor() + ", " + e);
				}
			}

		} finally {
			w.unlock();
		}
	}

	public Section getSectionInstance(SectionDescriptor sectionDescriptor) {

		r.lock();
		try {
			return sectionInstanceMap.get(sectionDescriptor);
		} finally {
			r.unlock();
		}
	}

	public boolean isWriteLocked() {

		return rwl.isWriteLocked();
	}

	public int size() {

		r.lock();
		try {
			return sectionInstanceMap.size();
		} finally {
			r.unlock();
		}
	}

	public boolean isCached(SimpleSectionDescriptor simpleSectionDescriptor) {

		r.lock();
		try {
			return this.sectionInstanceMap.containsKey(simpleSectionDescriptor);
		} finally {
			r.unlock();
		}
	}

	@SuppressWarnings("unchecked")
	public ArrayList<SectionCacheListener> getCacheListeners() {

		r.lock();
		try {
			return (ArrayList<SectionCacheListener>) cacheListeners.clone();
		} finally {
			r.unlock();
		}
	}

	public void addCacheListener(SectionCacheListener cacheListener) {

		w.lock();
		try {
			this.cacheListeners.add(cacheListener);
		} finally {
			w.unlock();
		}
	}

	public void removeCacheListener(SectionCacheListener cacheListener) {

		w.lock();
		try {
			this.cacheListeners.remove(cacheListener);
		} finally {
			w.unlock();
		}
	}

	public ArrayList<SectionDescriptor> getCachedSections() {

		return new ArrayList<SectionDescriptor>(sectionInstanceMap.keySet());
	}

	public Entry<SectionDescriptor, Section> getEntry(String alias) {

		r.lock();
		try {

			for (Entry<SectionDescriptor, Section> cacheEntry : this.sectionInstanceMap.entrySet()) {
				
				if (cacheEntry.getKey().getAlias().equals(alias)) {
					
					return new SimpleEntry<SectionDescriptor, Section>(cacheEntry);
				}
			}

		} finally {
			r.unlock();
		}

		return null;
	}

	public Entry<SectionDescriptor, Section> getEntry(Integer sectionID) {

		r.lock();
		try {

			for (Entry<SectionDescriptor, Section> cacheEntry : this.sectionInstanceMap.entrySet()) {

				if (sectionID.equals(cacheEntry.getKey().getSectionID())) {
					
					return new SimpleEntry<SectionDescriptor, Section>(cacheEntry);
				}
			}

		} finally {
			r.unlock();
		}

		return null;
	}

	/**
	 * Return a copy of the map holding all cached sections
	 * 
	 * @return
	 */
	public Map<SectionDescriptor, Section> getSectionMap() {

		r.lock();
		try {		
		
			HashMap<SectionDescriptor, Section> map = new HashMap<SectionDescriptor, Section>(sectionInstanceMap.size());

			map.putAll(this.sectionInstanceMap);

			return map;			
			
		} finally {
			r.unlock();
		}			
	}

	public List<Section> getSections() {

		r.lock();
		try {		
		
			return new ArrayList<Section>(sectionInstanceMap.values());
			
		} finally {
			r.unlock();
		}	
	}
}
