package se.unlogic.hierarchy.core.cache;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import se.unlogic.hierarchy.core.daos.interfaces.BackgroundModuleDAO;
import se.unlogic.hierarchy.core.exceptions.CachePreconditionException;
import se.unlogic.hierarchy.core.exceptions.ModuleInitializationException;
import se.unlogic.hierarchy.core.exceptions.ModuleUnloadException;
import se.unlogic.hierarchy.core.exceptions.ModuleUpdateException;
import se.unlogic.hierarchy.core.interfaces.BackgroundModule;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleCacheListener;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.standardutils.collections.KeyAlreadyCachedException;
import se.unlogic.standardutils.collections.KeyNotCachedException;


public class BackgroundModuleCache extends WildcardModuleCache<BackgroundModuleDescriptor, BackgroundModule, BackgroundModuleCacheListener> {

	private final SectionInterface sectionInterface;
	private final BackgroundModuleDAO moduleDAO;

	public BackgroundModuleCache(SectionInterface sectionInterface) {

		super(sectionInterface.getSystemInterface());

		this.sectionInterface = sectionInterface;
		moduleDAO = sectionInterface.getSystemInterface().getCoreDaoFactory().getBackgroundModuleDAO();
	}

	public void cacheModules(boolean syncWithDB) throws KeyNotCachedException, KeyAlreadyCachedException, SQLException {

		w.lock();
		try {
			// Get all enabled modules from the database
			List<? extends BackgroundModuleDescriptor> moduleDescriptorBeanList = moduleDAO.getEnabledModules(this.sectionInterface.getSectionDescriptor().getSectionID());

			if (moduleDescriptorBeanList != null && moduleDescriptorBeanList.size() > 0) {

				if(syncWithDB) {

					log.debug("Checking cache for background modules to unload in section " + this.sectionInterface.getSectionDescriptor() + "...");

					// Check if the cache is empty
					if (!this.instanceMap.isEmpty()) {

						// Loop thru the cache and remove all modules not found in the database
						ArrayList<BackgroundModuleDescriptor> keys = new ArrayList<BackgroundModuleDescriptor>(instanceMap.keySet());

						for (BackgroundModuleDescriptor moduleDescriptor : keys) {

							if (!moduleDescriptorBeanList.contains(moduleDescriptor)) {
								log.info("Background module " + moduleDescriptor + " not found in new module list for section " + this.sectionInterface.getSectionDescriptor()
										+ ", removing from cache...");

								try {
									this.unload(moduleDescriptor);
								} catch (Throwable e) {
									log.error("Error unloading background module " + moduleDescriptor + " from background module cache in section "
											+ this.sectionInterface.getSectionDescriptor(), e);
								}
							}
						}

						log.debug("Finished checking cache for background modules to unload in section " + this.sectionInterface.getSectionDescriptor());
					} else {
						log.debug("Cache is empty, no background modules to unload in section " + this.sectionInterface.getSectionDescriptor());
					}

				}

				// Add new modules to cache and update cached modules
				log.debug("Adding new background modules to cache and refreshing cached background modules for section " + this.sectionInterface.getSectionDescriptor() + "...");

				for (BackgroundModuleDescriptor moduleDescriptor : moduleDescriptorBeanList) {
					if (this.instanceMap.containsKey(moduleDescriptor)) {
						// Module already cached, update module
						try {
							this.update(moduleDescriptor);
						} catch (Throwable e) {
							log.error("Error updating background module " + moduleDescriptor + " in section " + this.sectionInterface.getSectionDescriptor(), e);
						}
					} else {
						// Cache new module
						log.info("Adding background module " + moduleDescriptor + " to cache for section " + this.sectionInterface.getSectionDescriptor() + "...");
						try {
							this.cache(moduleDescriptor);
						} catch (Throwable e) {
							log.error(
									"Error caching new instance of background module " + moduleDescriptor + "for section " + this.sectionInterface.getSectionDescriptor(),
									e);
						}
					}
				}
				log.debug("Finished adding new background modules and updating cached background modules in section " + this.sectionInterface.getSectionDescriptor());

			} else {
				log.debug("No background modules found in database, for section " + this.sectionInterface.getSectionDescriptor());

				if(syncWithDB){
					this.unload();
				}
			}
		} finally {
			w.unlock();
		}
	}

	@Override
	protected void moduleCached(BackgroundModuleDescriptor descriptor, BackgroundModule instance) {

		log.debug("Cached background module " + descriptor + " in section " + this.sectionInterface.getSectionDescriptor());

		this.setAliasMappings(descriptor);

		for(BackgroundModuleCacheListener cacheListener: this.cacheListeners){

			try{
				cacheListener.moduleCached(descriptor, instance);
			}catch(Exception e){
				log.error("Error in cachelistener " + cacheListener + " while caching background module " + descriptor, e);
			}
		}
	}

	@Override
	protected void moduleUpdated(BackgroundModuleDescriptor descriptor, BackgroundModule instance) {

		log.debug("Updated backround module " + descriptor + " in section " + this.sectionInterface.getSectionDescriptor());

		this.removeAliasMappings(descriptor);
		this.setAliasMappings(descriptor);

		for(BackgroundModuleCacheListener cacheListener: this.cacheListeners){

			try{
				cacheListener.moduleUpdated(descriptor, instance);
			}catch(Exception e){
				log.error("Error in cachelistener " + cacheListener + " while updating background module " + descriptor, e);
			}
		}
	}

	@Override
	protected void moduleUnloaded(BackgroundModuleDescriptor descriptor, BackgroundModule instance) {

		log.debug("Unloaded background module " + descriptor + " in section " + this.sectionInterface.getSectionDescriptor());

		this.removeAliasMappings(descriptor);

		for(BackgroundModuleCacheListener cacheListener: this.cacheListeners){

			try{
				cacheListener.moduleUnloaded(descriptor, instance);
			}catch(Exception e){
				log.error("Error in cachelistener " + cacheListener + " while unloading background module " + descriptor, e);
			}
		}
	}

	@Override
	protected void checkCachePreconditions(BackgroundModuleDescriptor descriptor) throws CachePreconditionException {

		validateDescritor(descriptor);
	}

	@Override
	protected void checkUpdatePreconditions(BackgroundModuleDescriptor descriptor) throws CachePreconditionException {

		validateDescritor(descriptor);
	}

	@Override
	protected void initializeModule(BackgroundModuleDescriptor descriptor, BackgroundModule instance) throws ModuleInitializationException {

		try{
			instance.init(descriptor, sectionInterface, getDataSource(descriptor.getDataSourceID()));

		}catch(Exception e){

			throw new ModuleInitializationException(descriptor, e);
		}
	}

	@Override
	protected void updateModule(BackgroundModuleDescriptor descriptor, BackgroundModule instance) throws ModuleUpdateException {

		try{
			instance.update(descriptor, getDataSource(descriptor.getDataSourceID()));

		}catch(Exception e){

			throw new ModuleUpdateException(descriptor, e);
		}
	}

	@Override
	protected void unloadModule(BackgroundModuleDescriptor descriptor, BackgroundModule instance) throws ModuleUnloadException {

		try{
			instance.unload();

		}catch(Exception e){

			throw new ModuleUnloadException(descriptor, e);
		}
	}

	@Override
	protected void unloadModuleSilently(BackgroundModuleDescriptor descriptor, BackgroundModule instance) {

		try{
			unloadModule(descriptor, instance);
		}catch(ModuleUnloadException e){
			log.error("Error unloading background module " + descriptor + " in section " + sectionInterface.getSectionDescriptor(), e);
		}
	}
}
