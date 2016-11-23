package se.unlogic.hierarchy.core.events;

import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;


public class SearchableItemClearEvent extends BaseSearchableEvent {

	private static final long serialVersionUID = -1873804935445156592L;

	public SearchableItemClearEvent(ForegroundModuleDescriptor moduleDescriptor) {

		super(moduleDescriptor);
	}

}
