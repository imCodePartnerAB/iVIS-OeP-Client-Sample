package se.unlogic.hierarchy.core.exceptions;

import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;


public class InvalidModuleDescriptionException extends CachePreconditionException {

	private static final long serialVersionUID = -341556442376295901L;

	public InvalidModuleDescriptionException(ModuleDescriptor moduleDescriptor) {

		super(moduleDescriptor);
	}
}
