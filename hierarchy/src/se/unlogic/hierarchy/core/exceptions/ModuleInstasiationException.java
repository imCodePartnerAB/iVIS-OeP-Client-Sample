package se.unlogic.hierarchy.core.exceptions;

import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;


public class ModuleInstasiationException extends ModuleCacheException {

	private static final long serialVersionUID = -5556534936991494904L;

	public ModuleInstasiationException(ModuleDescriptor moduleDescriptor, Throwable throwable) {

		super(moduleDescriptor, throwable);
	}
}
