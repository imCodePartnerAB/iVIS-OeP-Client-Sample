package se.unlogic.hierarchy.core.exceptions;

import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;

public class InvalidModuleNameException extends CachePreconditionException {

	private static final long serialVersionUID = 2267237727243611526L;

	public InvalidModuleNameException(ModuleDescriptor moduleDescriptor) {

		super(moduleDescriptor);
	}
}
