package se.unlogic.hierarchy.core.utils;

import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;


public interface ForegroundModuleTrackerListener<T> {

	public void moduleCached(ForegroundModuleDescriptor descriptor, T instance);
	
	public void moduleUnloaded(ForegroundModuleDescriptor descriptor, T instance);
}
