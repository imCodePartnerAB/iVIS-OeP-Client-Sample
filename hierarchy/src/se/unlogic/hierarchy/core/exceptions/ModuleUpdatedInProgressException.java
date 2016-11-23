package se.unlogic.hierarchy.core.exceptions;

import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;


public class ModuleUpdatedInProgressException extends ModuleCacheException {

	private static final long serialVersionUID = -884962596267259950L;

	public ModuleUpdatedInProgressException(ModuleDescriptor moduleDescriptor) {

		super(moduleDescriptor);
	}
}
