package se.unlogic.hierarchy.core.cache;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import se.unlogic.hierarchy.core.daos.interfaces.FilterModuleDAO;
import se.unlogic.hierarchy.core.exceptions.CachePreconditionException;
import se.unlogic.hierarchy.core.exceptions.ModuleInitializationException;
import se.unlogic.hierarchy.core.exceptions.ModuleUnloadException;
import se.unlogic.hierarchy.core.exceptions.ModuleUpdateException;
import se.unlogic.hierarchy.core.interfaces.FilterModule;
import se.unlogic.hierarchy.core.interfaces.FilterModuleCacheListener;
import se.unlogic.hierarchy.core.interfaces.FilterModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SystemInterface;
import se.unlogic.standardutils.collections.KeyAlreadyCachedException;
import se.unlogic.standardutils.collections.KeyNotCachedException;


public class FilterModuleCache extends WildcardModuleCache<FilterModuleDescriptor, FilterModule, FilterModuleCacheListener> {

	private final FilterModuleDAO moduleDAO;

	public FilterModuleCache(SystemInterface systemInterface) {

		super(systemInterface);
		moduleDAO = systemInterface.getCoreDaoFactory().getFilterModuleDAO();
	}

	public void cacheModules(boolean unload) throws KeyNotCachedException, KeyAlreadyCachedException, SQLException {

		w.lock();
		try {
			// Get all enabled modules from the database
			List<? extends FilterModuleDescriptor> moduleDescriptorBeanList = moduleDAO.getEnabledModules();

			if (moduleDescriptorBeanList != null && moduleDescriptorBeanList.size() > 0) {

				if(unload) {

					log.debug("Checking cache for filter modules to unload...");

					// Check if the cache is empty
					if (!this.instanceMap.isEmpty()) {

						// Loop thru the cache and remove all modules not found in the database
						ArrayList<FilterModuleDescriptor> keys = new ArrayList<FilterModuleDescriptor>(instanceMap.keySet());

						for (FilterModuleDescriptor moduleDescriptor : keys) {

							if (!moduleDescriptorBeanList.contains(moduleDescriptor)) {
								log.info("Filter module " + moduleDescriptor + " not found in new module list, removing from cache...");

								try {
									this.unload(moduleDescriptor);
								} catch (Throwable e) {
									log.error("Error unloading filter module " + moduleDescriptor + " from module cache", e);
								}
							}
						}

						log.debug("Finished checking cache for filter modules to unload");
					} else {
						log.debug("Cache is empty, no filter modules to unload");
					}

				}

				// Add new modules to cache and update cached modules
				log.debug("Adding new filter modules to cache and refreshing cached filter modules");

				for (FilterModuleDescriptor moduleDescriptor : moduleDescriptorBeanList) {
					if (this.instanceMap.containsKey(moduleDescriptor)) {
						// Module already cached, update module
						try {
							this.update(moduleDescriptor);
						} catch (Throwable e) {
							log.error("Error updating filter module " + moduleDescriptor, e);
						}
					} else {
						// Cache new module
						log.info("Adding filter module " + moduleDescriptor + " to cache");
						try {
							this.cache(moduleDescriptor);
						} catch (Throwable e) {
							log.error(
									"Error caching new instance of filter module " + moduleDescriptor,e);
						}
					}
				}
				log.debug("Finished adding new filter modules and updating cached filter modules");

			} else {
				log.debug("No filter modules found in database");
				this.unload();
			}
		} finally {
			w.unlock();
		}
	}

	@Override
	protected void moduleCached(FilterModuleDescriptor descriptor, FilterModule instance) {

		log.debug("Cached filter module " + descriptor);

		this.setAliasMappings(descriptor);

		for(FilterModuleCacheListener cacheListener: this.cacheListeners){

			try{
				cacheListener.moduleCached(descriptor, instance);
			}catch(Exception e){
				log.error("Error in cachelistener " + cacheListener + " while caching filter module " + descriptor, e);
			}
		}
	}

	@Override
	protected void moduleUpdated(FilterModuleDescriptor descriptor, FilterModule instance) {

		log.debug("Updated backround module " + descriptor);

		this.removeAliasMappings(descriptor);
		this.setAliasMappings(descriptor);

		for(FilterModuleCacheListener cacheListener: this.cacheListeners){

			try{
				cacheListener.moduleUpdated(descriptor, instance);
			}catch(Exception e){
				log.error("Error in cachelistener " + cacheListener + " while updating filter module " + descriptor, e);
			}
		}
	}

	@Override
	protected void moduleUnloaded(FilterModuleDescriptor descriptor, FilterModule instance) {

		log.debug("Unloaded filter module " + descriptor);

		this.removeAliasMappings(descriptor);

		for(FilterModuleCacheListener cacheListener: this.cacheListeners){

			try{
				cacheListener.moduleUnloaded(descriptor, instance);
			}catch(Exception e){
				log.error("Error in cachelistener " + cacheListener + " while unloading filter module " + descriptor, e);
			}
		}
	}

	@Override
	protected void checkCachePreconditions(FilterModuleDescriptor descriptor) throws CachePreconditionException {

		validateDescritor(descriptor);
	}

	@Override
	protected void checkUpdatePreconditions(FilterModuleDescriptor descriptor) throws CachePreconditionException {

		validateDescritor(descriptor);
	}

	@Override
	protected void initializeModule(FilterModuleDescriptor descriptor, FilterModule instance) throws ModuleInitializationException {

		try{
			instance.init(descriptor, systemInterface, getDataSource(descriptor.getDataSourceID()));

		}catch(Exception e){

			throw new ModuleInitializationException(descriptor, e);
		}
	}

	@Override
	protected void updateModule(FilterModuleDescriptor descriptor, FilterModule instance) throws ModuleUpdateException {

		try{
			instance.update(descriptor, getDataSource(descriptor.getDataSourceID()));

		}catch(Exception e){

			throw new ModuleUpdateException(descriptor, e);
		}
	}

	@Override
	protected void unloadModule(FilterModuleDescriptor descriptor, FilterModule instance) throws ModuleUnloadException {

		try{
			instance.unload();

		}catch(Exception e){

			throw new ModuleUnloadException(descriptor, e);
		}
	}

	@Override
	protected void unloadModuleSilently(FilterModuleDescriptor descriptor, FilterModule instance) {

		try{
			unloadModule(descriptor, instance);
		}catch(ModuleUnloadException e){
			log.error("Error unloading filter module " + descriptor, e);
		}
	}

}
