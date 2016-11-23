package se.unlogic.hierarchy.core.cache;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import se.unlogic.hierarchy.core.exceptions.CachePreconditionException;
import se.unlogic.hierarchy.core.exceptions.DataSourceDisabledException;
import se.unlogic.hierarchy.core.exceptions.DataSourceInstantiationException;
import se.unlogic.hierarchy.core.exceptions.DataSourceNotFoundException;
import se.unlogic.hierarchy.core.exceptions.DataSourceNotFoundInContextException;
import se.unlogic.hierarchy.core.exceptions.ModuleAlreadyBeingCachedException;
import se.unlogic.hierarchy.core.exceptions.ModuleAlreadyCachedException;
import se.unlogic.hierarchy.core.exceptions.ModuleInitializationException;
import se.unlogic.hierarchy.core.exceptions.ModuleInstasiationException;
import se.unlogic.hierarchy.core.exceptions.ModuleNotCachedException;
import se.unlogic.hierarchy.core.exceptions.ModuleUnloadException;
import se.unlogic.hierarchy.core.exceptions.ModuleUpdateException;
import se.unlogic.hierarchy.core.exceptions.ModuleUpdatedInProgressException;
import se.unlogic.hierarchy.core.interfaces.AttributeHandler;
import se.unlogic.hierarchy.core.interfaces.Module;
import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SystemInterface;
import se.unlogic.standardutils.datatypes.SimpleEntry;
import se.unlogic.standardutils.reflection.ReflectionUtils;

public abstract class BaseModuleCache<DescriptorType extends ModuleDescriptor, ModuleType extends Module<?>, ListenerType> {

	protected final Logger log = Logger.getLogger(this.getClass());

	protected final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	protected final Lock r = rwl.readLock();
	protected final Lock w = rwl.writeLock();

	protected HashMap<DescriptorType, ModuleType> instanceMap = new HashMap<DescriptorType, ModuleType>();
	protected HashSet<DescriptorType> cacheInProgressSet = new HashSet<DescriptorType>();
	protected HashSet<DescriptorType> updateInProgressSet = new HashSet<DescriptorType>();
	protected CopyOnWriteArraySet<ListenerType> cacheListeners = new CopyOnWriteArraySet<ListenerType>();

	protected final SystemInterface systemInterface;

	public BaseModuleCache(SystemInterface systemInterface) {

		super();
		this.systemInterface = systemInterface;
	}

	public ModuleType cache(DescriptorType descriptor) throws ModuleAlreadyCachedException, ModuleAlreadyBeingCachedException, ModuleInstasiationException, ModuleInitializationException, CachePreconditionException {

		boolean addedToCacheInProgressSet = false;

		try{
			w.lock();
			try{
				if(instanceMap.containsKey(descriptor)){
					throw new ModuleAlreadyCachedException(descriptor);
				}

				checkCachePreconditions(descriptor);

				if(!(addedToCacheInProgressSet = this.cacheInProgressSet.add(descriptor))){
					throw new ModuleAlreadyBeingCachedException(descriptor);
				}
			}finally{
				w.unlock();
			}

			ModuleType instance = createModuleInstance(descriptor);

			initializeModule(descriptor, instance);

			w.lock();
			try{
				this.instanceMap.put(descriptor, instance);

				this.moduleCached(descriptor, instance);
			}finally{
				w.unlock();
			}

			return instance;

		}finally{

			if(addedToCacheInProgressSet){
				w.lock();
				try{
					this.cacheInProgressSet.remove(descriptor);
				}finally{
					w.unlock();
				}
			}
		}
	}

	public void update(DescriptorType descriptor) throws ModuleNotCachedException, CachePreconditionException, ModuleUpdateException, ModuleUpdatedInProgressException {

		boolean addedToUpdateInProgressSet = false;

		try{
			ModuleType instance;

			w.lock();
			try{
				instance = instanceMap.get(descriptor);

				if(instance == null){
					throw new ModuleNotCachedException(descriptor);
				}

				checkUpdatePreconditions(descriptor);

				if(!(addedToUpdateInProgressSet = this.updateInProgressSet.add(descriptor))){
					throw new ModuleUpdatedInProgressException(descriptor);
				}

			}finally{
				w.unlock();
			}

			updateModule(descriptor, instance);

			w.lock();
			try{
				//Update key in map, not because it's hashcode or equals has changed but for all other values it contains
				this.instanceMap.remove(descriptor);
				this.instanceMap.put(descriptor, instance);
				this.moduleUpdated(descriptor, instance);
			}finally{
				w.unlock();
			}

		}finally{

			if(addedToUpdateInProgressSet){
				w.lock();
				try{
					this.updateInProgressSet.remove(descriptor);
				}finally{
					w.unlock();
				}
			}
		}
	}

	public void unload(DescriptorType descriptor) throws ModuleNotCachedException, ModuleUnloadException, ModuleUpdatedInProgressException {

		ModuleType instance;

		w.lock();
		try{
			instance = instanceMap.get(descriptor);

			if(instance == null){

				throw new ModuleNotCachedException(descriptor);

			}else if(updateInProgressSet.contains(descriptor)){

				throw new ModuleUpdatedInProgressException(descriptor);
			}

			this.instanceMap.remove(descriptor);
			this.moduleUnloaded(descriptor, instance);
		}finally{
			w.unlock();
		}

		unloadModule(descriptor, instance);
	}

	public void unload() {

		//Lock whole cache and unload all modules

		w.lock();
		try {
			Iterator<Entry<DescriptorType, ModuleType>> iterator = this.instanceMap.entrySet().iterator();

			while (iterator.hasNext()) {

				Entry<DescriptorType, ModuleType> entry = iterator.next();

				this.unloadModuleSilently(entry.getKey(), entry.getValue());
				this.moduleUnloaded(entry.getKey(), entry.getValue());
			}

		} finally {
			w.unlock();
		}
	}

	protected abstract void moduleCached(DescriptorType descriptor, ModuleType instance);

	protected abstract void moduleUpdated(DescriptorType descriptor, ModuleType instance);

	protected abstract void moduleUnloaded(DescriptorType descriptor, ModuleType instance);

	protected abstract void checkCachePreconditions(DescriptorType descriptor) throws CachePreconditionException;

	protected abstract void checkUpdatePreconditions(DescriptorType descriptor) throws CachePreconditionException;

	@SuppressWarnings("unchecked")
	protected ModuleType createModuleInstance(DescriptorType descriptor) throws ModuleInstasiationException {

		try{
			return (ModuleType)ReflectionUtils.getInstance(descriptor.getClassname());

		}catch(Throwable t){

			throw new ModuleInstasiationException(descriptor, t);
		}
	}

	protected abstract void initializeModule(DescriptorType descriptor, ModuleType instance) throws ModuleInitializationException;

	protected abstract void updateModule(DescriptorType descriptor, ModuleType instance) throws ModuleUpdateException;

	protected abstract void unloadModule(DescriptorType descriptor, ModuleType instance) throws ModuleUnloadException;

	protected abstract void unloadModuleSilently(DescriptorType descriptor, ModuleType instance);

	protected DataSource getDataSource(Integer dataSourceID) throws SQLException, DataSourceNotFoundException, DataSourceInstantiationException, DataSourceDisabledException, DataSourceNotFoundInContextException {

		if(dataSourceID != null){

			return systemInterface.getDataSourceCache().getDataSource(dataSourceID);

		}else{

			return systemInterface.getDataSource();
		}
	}

	public boolean addCacheListener(ListenerType cacheListener) {

		return this.cacheListeners.add(cacheListener);
	}

	public boolean removeCacheListener(ListenerType cacheListener) {

		return this.cacheListeners.remove(cacheListener);
	}

	public boolean isCached(DescriptorType descriptor) {

		r.lock();
		try{
			return this.instanceMap.containsKey(descriptor);
		}finally{
			r.unlock();
		}
	}

	public boolean isBeingCached(DescriptorType descriptor) {

		r.lock();
		try{
			return this.cacheInProgressSet.contains(descriptor);
		}finally{
			r.unlock();
		}
	}

	public ArrayList<Entry<DescriptorType, ModuleType>> getCachedModules() {

		r.lock();
		try {
			Set<Entry<DescriptorType, ModuleType>> entrySet = this.instanceMap.entrySet();

			ArrayList<Entry<DescriptorType, ModuleType>> entryList = new ArrayList<Entry<DescriptorType, ModuleType>>(entrySet.size());

			for(Entry<DescriptorType, ModuleType> entry : entrySet){

				entryList.add(new SimpleEntry<DescriptorType, ModuleType>(entry));
			}

			return entryList;

		} finally {
			r.unlock();
		}
	}

	public ArrayList<DescriptorType> getCachedModuleDescriptors() {

		r.lock();
		try {
			return new ArrayList<DescriptorType>(this.instanceMap.keySet());
		} finally {
			r.unlock();
		}
	}

	public Entry<DescriptorType, ModuleType> getEntry(Integer moduleID) {

		r.lock();
		try {
			for(Entry<DescriptorType,ModuleType> entry : instanceMap.entrySet()){

				Integer currentModuleID = entry.getKey().getModuleID();

				if (currentModuleID != null && currentModuleID.equals(moduleID)) {

					return new SimpleEntry<DescriptorType,ModuleType>(entry);
				}
			}

			return null;

		} finally {
			r.unlock();
		}
	}

	public ModuleType getModule(DescriptorType descriptor) {

		r.lock();
		try {
			return instanceMap.get(descriptor);

		} finally {
			r.unlock();
		}
	}

	public int size() {

		return instanceMap.size();
	}

	public Entry<DescriptorType, ModuleType> getEntryByHashCode(int hashCode) {

		r.lock();
		try {
			for (Entry<DescriptorType, ModuleType> cacheEntry : this.instanceMap.entrySet()) {

				int currentModuleHashCode = cacheEntry.getKey().hashCode();

				if (currentModuleHashCode == hashCode) {

					return new SimpleEntry<DescriptorType, ModuleType>(cacheEntry);
				}
			}
		} finally {
			r.unlock();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends ModuleType> Entry<DescriptorType, T> getModuleEntryByClass(Class<T> clazz) {

		r.lock();
		try {
			for (Entry<DescriptorType, ModuleType> cacheEntry : this.instanceMap.entrySet()) {

				if(cacheEntry.getValue().getClass().isAssignableFrom(clazz)) {

					return new SimpleEntry<DescriptorType, T>(cacheEntry.getKey(), (T) cacheEntry.getValue());
				}

			}
		} finally {
			r.unlock();
		}

		return null;
	}

	public DescriptorType getDescriptorByHashCode(int hashCode){

		Entry<DescriptorType, ModuleType> entry = getEntryByHashCode(hashCode);

		if(entry != null){

			return entry.getKey();
		}

		return null;
	}

	public Entry<DescriptorType, ModuleType> getModuleEntryByAttribute(String name, String value) {

		r.lock();
		try {
			for (Entry<DescriptorType, ModuleType> cacheEntry : this.instanceMap.entrySet()) {

				AttributeHandler attributeHandler = cacheEntry.getKey().getAttributeHandler();

				if(attributeHandler != null){

					String attributeValue = attributeHandler.getString(name);

					if(value.equals(attributeValue)){

						return new SimpleEntry<DescriptorType, ModuleType>(cacheEntry);
					}
				}
			}
		} finally {
			r.unlock();
		}

		return null;
	}
}
