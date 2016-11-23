package se.unlogic.hierarchy.core.exceptions;

import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;


public class ModuleInitializationException extends ModuleCacheException {

	private static final long serialVersionUID = -6876723453179662400L;

	public ModuleInitializationException(ModuleDescriptor moduleDescriptor, Throwable throwable) {

		super(moduleDescriptor, throwable);
	}
}
