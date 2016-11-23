package se.unlogic.hierarchy.core.exceptions;

import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;


public class InvalidModuleAliasException extends CachePreconditionException {

	private static final long serialVersionUID = -8408583314780273834L;

	public InvalidModuleAliasException(ModuleDescriptor moduleDescriptor) {

		super(moduleDescriptor);
	}
}
