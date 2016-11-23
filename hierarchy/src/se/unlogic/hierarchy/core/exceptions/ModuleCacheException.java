package se.unlogic.hierarchy.core.exceptions;

import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;

public abstract class ModuleCacheException extends Exception {

	private static final long serialVersionUID = -5093421324891401050L;

	protected final ModuleDescriptor moduleDescriptor;

	public ModuleCacheException(ModuleDescriptor moduleDescriptor) {

		super();
		this.moduleDescriptor = moduleDescriptor;
	}

	public ModuleCacheException(ModuleDescriptor moduleDescriptor, Throwable throwable) {

		super(throwable);
		this.moduleDescriptor = moduleDescriptor;
	}

	public ModuleDescriptor getModuleDescriptor() {

		return moduleDescriptor;
	}

}
