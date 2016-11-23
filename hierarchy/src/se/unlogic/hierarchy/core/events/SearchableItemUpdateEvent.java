package se.unlogic.hierarchy.core.events;

import java.util.List;

import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SearchableItem;

/**
 * This event is used to signal search modules regarding new and/or updated items.
 * 
 * @author Unlogic
 * 
 */
public class SearchableItemUpdateEvent extends SearchableItemAddEvent {

	private static final long serialVersionUID = 6412380388031754882L;

	public SearchableItemUpdateEvent(SearchableItem item, ForegroundModuleDescriptor moduleDescriptor) {

		super(item, moduleDescriptor);
	}

	public SearchableItemUpdateEvent(List<SearchableItem> items, ForegroundModuleDescriptor moduleDescriptor) {

		super(items, moduleDescriptor);
	}

}
