package se.unlogic.hierarchy.core.exceptions;

import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;

public class ModuleAlreadyCachedException extends ModuleCacheException {

	private static final long serialVersionUID = 3916231916826157395L;

	public ModuleAlreadyCachedException(ModuleDescriptor moduleDescriptor) {

		super(moduleDescriptor);
	}

}
