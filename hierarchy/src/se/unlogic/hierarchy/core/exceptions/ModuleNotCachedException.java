package se.unlogic.hierarchy.core.exceptions;

import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;


public class ModuleNotCachedException extends ModuleCacheException {

	private static final long serialVersionUID = -6490501332015989047L;

	public ModuleNotCachedException(ModuleDescriptor moduleDescriptor) {

		super(moduleDescriptor);
	}

}
