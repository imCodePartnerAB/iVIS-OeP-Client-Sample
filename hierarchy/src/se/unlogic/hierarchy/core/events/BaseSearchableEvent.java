package se.unlogic.hierarchy.core.events;

import java.io.Serializable;

import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;

public abstract class BaseSearchableEvent implements Serializable{

	private static final long serialVersionUID = 8595555844256611806L;
	
	protected final ForegroundModuleDescriptor moduleDescriptor;

	public BaseSearchableEvent(ForegroundModuleDescriptor moduleDescriptor) {

		super();
		this.moduleDescriptor = moduleDescriptor;
	}

	public ForegroundModuleDescriptor getModuleDescriptor() {

		return moduleDescriptor;
	}

}
