package se.unlogic.hierarchy.core.exceptions;

import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;

public class AliasCollisonException extends CachePreconditionException {

	private static final long serialVersionUID = 1863088004808522990L;

	private final ForegroundModuleDescriptor conflictingDescriptor;

	public AliasCollisonException(ModuleDescriptor moduleDescriptor, ForegroundModuleDescriptor conflictingDescriptor) {

		super(moduleDescriptor);

		this.conflictingDescriptor = conflictingDescriptor;
	}

	public ForegroundModuleDescriptor getConflictingDescriptor() {

		return conflictingDescriptor;
	}

	@Override
	public String toString() {

		return "Alias of module " + conflictingDescriptor + " conflicts with cached module " + moduleDescriptor;
	}
}
