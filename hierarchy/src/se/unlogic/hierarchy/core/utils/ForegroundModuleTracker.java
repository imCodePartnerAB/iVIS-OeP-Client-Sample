package se.unlogic.hierarchy.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import se.unlogic.hierarchy.core.enums.SystemStatus;
import se.unlogic.hierarchy.core.interfaces.ForegroundModule;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleCacheListener;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.interfaces.SystemInterface;
import se.unlogic.hierarchy.core.interfaces.SystemStartupListener;
import se.unlogic.standardutils.datatypes.SimpleEntry;

/**
 * @author Robert "Unlogic" Olofsson
 *
 * This class finds and keeps track of any started foreground modules implementing the given class or interface
 *
 * @param <T>
 */
public class ForegroundModuleTracker<T> implements ForegroundModuleCacheListener, SystemStartupListener {

	protected final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	protected final Lock readLock = readWriteLock.readLock();
	protected final Lock writeLock = readWriteLock.writeLock();
	
	protected final Class<T> targetClass;
	protected final Integer moduleID;
	protected final SectionInterface baseSection;
	protected final SystemInterface systemInterface;
	protected final boolean recursive;
	protected final boolean assignable;
	protected Entry<ForegroundModuleDescriptor, T> moduleEntry;

	protected List<ForegroundModuleTrackerListener<T>> listeners;
	
	public ForegroundModuleTracker(Class<T> targetClass, int moduleID, SystemInterface systemInterface, SectionInterface baseSection, boolean recursive, boolean assignable) {
		
		this(targetClass, moduleID, systemInterface, baseSection, recursive, assignable, null);
	}
	
	public ForegroundModuleTracker(Class<T> targetClass, int moduleID, SystemInterface systemInterface, SectionInterface baseSection, boolean recursive, boolean assignable, ForegroundModuleTrackerListener<T> listener) {

		this.targetClass = targetClass;
		this.moduleID = moduleID;
		this.systemInterface = systemInterface;
		this.baseSection = baseSection;
		this.recursive = recursive;
		this.assignable = assignable;

		if(listener != null){
			
			addListener(listener);
		}
		
		if(systemInterface.getSystemStatus() == SystemStatus.STARTING){

			systemInterface.addStartupListener(this);

		}else{

			systemStarted();
		}
	}

	@Override
	public void systemStarted() {

		writeLock.lock();
		
		try{
			
		}finally{
			
			writeLock.unlock();
		}		
		
		//Scan all loaded foreground modules
		moduleEntry = ModuleUtils.findForegroundModule(targetClass, assignable, moduleID, recursive, baseSection);

		if(moduleEntry != null && listeners != null){
			
			for(ForegroundModuleTrackerListener<T> listener : listeners){
				
				listener.moduleCached(moduleEntry.getKey(), moduleEntry.getValue());
			}
		}
		
		//Add global foreground module listener
		systemInterface.addForegroundModuleCacheListener(this);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void moduleCached(ForegroundModuleDescriptor moduleDescriptor, ForegroundModule moduleInstance) {

		writeLock.lock();
		
		try{
			if(moduleID.equals(moduleDescriptor.getModuleID()) && (moduleInstance.getClass().equals(targetClass) || (assignable && targetClass.isAssignableFrom(moduleInstance.getClass())))){

				this.moduleEntry = new SimpleEntry<ForegroundModuleDescriptor, T>(moduleDescriptor, (T)moduleInstance);
				
				if(listeners != null){
					
					for(ForegroundModuleTrackerListener<T> listener : listeners){
						
						listener.moduleCached(moduleDescriptor, (T)moduleInstance);
					}
				}
			}			
			
		}finally{
			
			writeLock.unlock();
		}		
	}

	@Override
	public void moduleUpdated(ForegroundModuleDescriptor moduleDescriptor, ForegroundModule moduleInstance) {

		moduleCached(moduleDescriptor, moduleInstance);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void moduleUnloaded(ForegroundModuleDescriptor moduleDescriptor, ForegroundModule moduleInstance) {

		writeLock.lock();
		
		try{
			if(this.moduleEntry != null && moduleID.equals(moduleDescriptor.getModuleID())){
				
				this.moduleEntry = null;
				
				if(listeners != null){
					
					for(ForegroundModuleTrackerListener<T> listener : listeners){
						
						listener.moduleUnloaded(moduleDescriptor, (T)moduleInstance);
					}
				}				
			}			
			
		}finally{
			
			writeLock.unlock();
		}		
	}

	public void shutdown() {

		writeLock.lock();
		
		try{
			systemInterface.removeForegroundModuleCacheListener(this);
			this.moduleEntry = null;
			this.listeners = null;
			
		}finally{
			
			writeLock.unlock();
		}
	}

	public ForegroundModuleDescriptor getDescriptor(){
		
		readLock.lock();
		
		try{
			if(moduleEntry == null){
				
				return null;
			}
			
			return moduleEntry.getKey();		
			
		}finally{
			
			readLock.unlock();
		}
	}
	
	public T getInstance(){
		
		readLock.lock();
		
		try{
			if(moduleEntry == null){
				
				return null;
			}
			
			return moduleEntry.getValue();		
			
		}finally{
			
			readLock.unlock();
		}
	}
	
	public Entry<ForegroundModuleDescriptor, T> getEntry(){
		
		return moduleEntry;
	}

	@Override
	public String toString() {

		return "ForegroundModuleTracker tracking class " + this.targetClass.getName();
	}

	public Integer getModuleID() {

		return moduleID;
	}
	
	public void addListener(ForegroundModuleTrackerListener<T> listener){
		
		writeLock.lock();
		
		try{
			if(this.listeners == null){
				
				listeners = new ArrayList<ForegroundModuleTrackerListener<T>>();
			}

			listeners.add(listener);
			
		}finally{
			
			writeLock.unlock();
		}		
	}
	
	public void remoceListener(ForegroundModuleTrackerListener<T> listener){
		
		writeLock.lock();
		
		try{
			if(this.listeners != null){
							
				listeners.remove(listener);
				
				if(listeners.isEmpty()){
					
					listeners = null;
				}
			}
			
		}finally{
			
			writeLock.unlock();
		}		
	}	
}
