package se.unlogic.hierarchy.core.exceptions;

import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;

public class ModuleUnloadException extends ModuleCacheException {

	private static final long serialVersionUID = 8238905467673702539L;

	public ModuleUnloadException(ModuleDescriptor moduleDescriptor, Throwable throwable) {

		super(moduleDescriptor, throwable);
	}
}
