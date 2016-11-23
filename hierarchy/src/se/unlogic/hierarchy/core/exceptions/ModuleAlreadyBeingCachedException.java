package se.unlogic.hierarchy.core.exceptions;

import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;

public class ModuleAlreadyBeingCachedException extends ModuleCacheException {

	private static final long serialVersionUID = 2032088779222619072L;

	public ModuleAlreadyBeingCachedException(ModuleDescriptor moduleDescriptor) {

		super(moduleDescriptor);
	}
}
