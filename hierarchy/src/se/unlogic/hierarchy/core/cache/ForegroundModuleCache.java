package se.unlogic.hierarchy.core.cache;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import se.unlogic.hierarchy.core.daos.interfaces.ForegroundModuleDAO;
import se.unlogic.hierarchy.core.exceptions.AliasCollisonException;
import se.unlogic.hierarchy.core.exceptions.CachePreconditionException;
import se.unlogic.hierarchy.core.exceptions.InvalidModuleAliasException;
import se.unlogic.hierarchy.core.exceptions.InvalidModuleDescriptionException;
import se.unlogic.hierarchy.core.exceptions.InvalidModuleNameException;
import se.unlogic.hierarchy.core.exceptions.ModuleInitializationException;
import se.unlogic.hierarchy.core.exceptions.ModuleUnloadException;
import se.unlogic.hierarchy.core.exceptions.ModuleUpdateException;
import se.unlogic.hierarchy.core.interfaces.ForegroundModule;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleCacheListener;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.standardutils.collections.KeyAlreadyCachedException;
import se.unlogic.standardutils.collections.KeyNotCachedException;
import se.unlogic.standardutils.datatypes.SimpleEntry;
import se.unlogic.standardutils.string.StringUtils;


public class ForegroundModuleCache extends BaseModuleCache<ForegroundModuleDescriptor, ForegroundModule, ForegroundModuleCacheListener> {

	private final SectionInterface sectionInterface;
	private final ForegroundModuleDAO moduleDAO;
	private final HashMap<String, ForegroundModuleDescriptor> aliasMap = new HashMap<String, ForegroundModuleDescriptor>();

	public ForegroundModuleCache(SectionInterface sectionInterface) {

		super(sectionInterface.getSystemInterface());
		this.sectionInterface = sectionInterface;
		this.moduleDAO = sectionInterface.getSystemInterface().getCoreDaoFactory().getForegroundModuleDAO();
	}

	public void cacheModules(boolean syncWithDB) throws KeyNotCachedException, KeyAlreadyCachedException, SQLException {
		w.lock();
		try {
			// Get all enabled modules from the database
			List<? extends ForegroundModuleDescriptor> moduleDescriptorBeanList = moduleDAO.getEnabledModules(this.sectionInterface.getSectionDescriptor().getSectionID());

			if (moduleDescriptorBeanList != null && moduleDescriptorBeanList.size() > 0) {

				if(syncWithDB) {

					log.debug("Checking cache for modules to unload in section " + this.sectionInterface.getSectionDescriptor() + "...");

					// Check if the cache is empty
					if (!this.instanceMap.isEmpty()) {

						// Loop thru the cache and remove all modules not found in the database
						ArrayList<ForegroundModuleDescriptor> keys = new ArrayList<ForegroundModuleDescriptor>(instanceMap.keySet());

						for (ForegroundModuleDescriptor moduleDescriptor : keys) {

							if (!moduleDescriptorBeanList.contains(moduleDescriptor)) {
								log.info("Module " + moduleDescriptor + " not found in new Module list for section " + this.sectionInterface.getSectionDescriptor() + ", removing from cache...");

								try {
									this.unload(moduleDescriptor);
								} catch (Throwable e) {
									log.error("Error unloading module " + moduleDescriptor + " from module cache in section " + this.sectionInterface.getSectionDescriptor(), e);
								}
							}
						}

						log.debug("Finished checking cache for modules to unload in section " + this.sectionInterface.getSectionDescriptor());
					} else {
						log.debug("Cache is empty, no modules to unload in section " + this.sectionInterface.getSectionDescriptor());
					}

				}

				// Add new modules to cache and update cached modules
				log.debug("Adding new modules to cache and refreshing cached modules for section " + this.sectionInterface.getSectionDescriptor() + "...");

				for (ForegroundModuleDescriptor moduleDescriptor : moduleDescriptorBeanList) {
					if (this.instanceMap.containsKey(moduleDescriptor)) {
						// Module already cached, update module
						try {
							this.update(moduleDescriptor);
						} catch (Throwable e) {
							log.error("Error updating module " + moduleDescriptor + " in section " + this.sectionInterface.getSectionDescriptor(), e);
						}
					} else {
						// Cache new module
						log.info("Adding module " + moduleDescriptor + " to cache for section " + this.sectionInterface.getSectionDescriptor() + "...");
						try {
							this.cache(moduleDescriptor);
						} catch (Throwable e) {
							log.error("Error caching new instance of module " + moduleDescriptor + "for section " + this.sectionInterface.getSectionDescriptor(), e);
						}
					}
				}
				log.debug("Finished adding new modules and updating cached modules in section " + this.sectionInterface.getSectionDescriptor());

			} else {
				log.debug("No modules found in database, for section " + this.sectionInterface.getSectionDescriptor());
				if(syncWithDB){
					this.unload();
				}
			}
		} finally {
			w.unlock();
		}
	}

	@Override
	protected void moduleCached(ForegroundModuleDescriptor descriptor, ForegroundModule instance) {

		log.debug("Cached foreground module " + descriptor + " in section " + this.sectionInterface.getSectionDescriptor());

		this.aliasMap.put(descriptor.getAlias(), descriptor);

		for(ForegroundModuleCacheListener cacheListener: this.cacheListeners){

			try{
				cacheListener.moduleCached(descriptor, instance);
			}catch(Exception e){
				log.error("Error in cachelistener " + cacheListener + " while caching foreground module " + descriptor, e);
			}
		}
	}

	@Override
	protected void moduleUpdated(ForegroundModuleDescriptor descriptor, ForegroundModule instance) {

		log.debug("Updated foreground module " + descriptor + " in section " + this.sectionInterface.getSectionDescriptor());

		removeFromAliasMap(descriptor);
		this.aliasMap.put(descriptor.getAlias(), descriptor);

		for(ForegroundModuleCacheListener cacheListener: this.cacheListeners){

			try{
				cacheListener.moduleUpdated(descriptor, instance);
			}catch(Exception e){
				log.error("Error in cachelistener " + cacheListener + " while updating foreground module " + descriptor, e);
			}
		}
	}

	@Override
	protected void moduleUnloaded(ForegroundModuleDescriptor descriptor, ForegroundModule instance) {

		log.debug("Unloaded foreground module " + descriptor + " in section " + this.sectionInterface.getSectionDescriptor());

		removeFromAliasMap(descriptor);

		for(ForegroundModuleCacheListener cacheListener: this.cacheListeners){

			try{
				cacheListener.moduleUnloaded(descriptor, instance);
			}catch(Exception e){
				log.error("Error in cachelistener " + cacheListener + " while unloading foreground module " + descriptor, e);
			}
		}
	}

	protected void removeFromAliasMap(ForegroundModuleDescriptor descriptor) {

		aliasMap.values().remove(descriptor);
	}

	@Override
	protected void checkCachePreconditions(ForegroundModuleDescriptor descriptor) throws CachePreconditionException {

		validateDescritor(descriptor);

		ForegroundModuleDescriptor conflictingDescriptor = aliasMap.get(descriptor.getAlias());

		if(conflictingDescriptor != null){

			throw new AliasCollisonException(descriptor, conflictingDescriptor);
		}

		for(ForegroundModuleDescriptor moduleDescriptor : cacheInProgressSet){

			if(moduleDescriptor.getAlias().equals(descriptor.getAlias()) && !moduleDescriptor.equals(descriptor)){

				throw new AliasCollisonException(descriptor, moduleDescriptor);
			}
		}

		for(ForegroundModuleDescriptor moduleDescriptor : updateInProgressSet){

			if(moduleDescriptor.getAlias().equals(descriptor.getAlias())){

				throw new AliasCollisonException(descriptor, moduleDescriptor);
			}
		}
	}

	@Override
	protected void checkUpdatePreconditions(ForegroundModuleDescriptor descriptor) throws CachePreconditionException {

		validateDescritor(descriptor);

		ForegroundModuleDescriptor conflictingDescriptor = aliasMap.get(descriptor.getAlias());

		if(conflictingDescriptor != null && !conflictingDescriptor.equals(descriptor)){

			throw new AliasCollisonException(descriptor, conflictingDescriptor);
		}

		for(ForegroundModuleDescriptor moduleDescriptor : cacheInProgressSet){

			if(moduleDescriptor.getAlias().equals(descriptor.getAlias())){

				throw new AliasCollisonException(descriptor, moduleDescriptor);
			}
		}

		for(ForegroundModuleDescriptor moduleDescriptor : updateInProgressSet){

			if(moduleDescriptor.getAlias().equals(descriptor.getAlias()) && !moduleDescriptor.equals(descriptor)){

				throw new AliasCollisonException(descriptor, moduleDescriptor);
			}
		}
	}

	private void validateDescritor(ForegroundModuleDescriptor descriptor) throws InvalidModuleAliasException, InvalidModuleNameException, InvalidModuleDescriptionException {

		if(StringUtils.isEmpty(descriptor.getAlias())){

			throw new InvalidModuleAliasException(descriptor);

		}else if(StringUtils.isEmpty(descriptor.getName())){

			throw new InvalidModuleNameException(descriptor);

		}else if(StringUtils.isEmpty(descriptor.getDescription())){

			throw new InvalidModuleDescriptionException(descriptor);
		}
	}

	@Override
	protected void initializeModule(ForegroundModuleDescriptor descriptor, ForegroundModule instance) throws ModuleInitializationException {

		try{
			instance.init(descriptor, sectionInterface, getDataSource(descriptor.getDataSourceID()));

		}catch(Exception e){

			throw new ModuleInitializationException(descriptor, e);
		}
	}

	@Override
	protected void updateModule(ForegroundModuleDescriptor descriptor, ForegroundModule instance) throws ModuleUpdateException {

		try{
			instance.update(descriptor, getDataSource(descriptor.getDataSourceID()));

		}catch(Exception e){

			throw new ModuleUpdateException(descriptor, e);
		}
	}

	@Override
	protected void unloadModule(ForegroundModuleDescriptor descriptor, ForegroundModule instance) throws ModuleUnloadException {

		try{
			instance.unload();

		}catch(Exception e){

			throw new ModuleUnloadException(descriptor, e);
		}
	}

	@Override
	protected void unloadModuleSilently(ForegroundModuleDescriptor descriptor, ForegroundModule instance) {

		try{
			unloadModule(descriptor, instance);
		}catch(ModuleUnloadException e){
			log.error("Error unloading foreground module " + descriptor + " in section " + sectionInterface.getSectionDescriptor(), e);
		}
	}

	public Entry<ForegroundModuleDescriptor, ForegroundModule> getEntry(String alias) {

		r.lock();
		try {
			ForegroundModuleDescriptor descriptor = aliasMap.get(alias);

			if(descriptor == null){

				return null;
			}

			ForegroundModule instance = instanceMap.get(descriptor);

			if(instance == null){

				return null;
			}

			return new SimpleEntry<ForegroundModuleDescriptor, ForegroundModule>(descriptor, instance);

		} finally {
			r.unlock();
		}
	}
}
