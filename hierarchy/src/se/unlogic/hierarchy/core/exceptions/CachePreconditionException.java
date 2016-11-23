package se.unlogic.hierarchy.core.exceptions;

import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;


public abstract class CachePreconditionException extends ModuleCacheException {

	private static final long serialVersionUID = 7950009608258287393L;

	public CachePreconditionException(ModuleDescriptor moduleDescriptor) {

		super(moduleDescriptor);
	}

	public CachePreconditionException(ModuleDescriptor moduleDescriptor, Throwable throwable) {

		super(moduleDescriptor, throwable);
	}
}
