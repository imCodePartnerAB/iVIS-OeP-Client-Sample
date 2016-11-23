package se.unlogic.hierarchy.core.exceptions;

import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;

public class ModuleUpdateException extends ModuleCacheException {

	private static final long serialVersionUID = -3254713586341904167L;

	public ModuleUpdateException(ModuleDescriptor moduleDescriptor, Throwable throwable) {

		super(moduleDescriptor, throwable);
	}

}
