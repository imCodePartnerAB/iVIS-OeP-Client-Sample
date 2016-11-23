package se.unlogic.hierarchy.core.events;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SearchableItem;

/**
 * This event is used to signal search modules regarding new and/or updated items.
 * 
 * @author Unlogic
 * 
 */
public class SearchableItemAddEvent extends BaseSearchableEvent implements Serializable {

	private static final long serialVersionUID = 7886742947562452835L;
	
	private final List<SearchableItem> items;

	public SearchableItemAddEvent(List<SearchableItem> items, ForegroundModuleDescriptor moduleDescriptor) {

		super(moduleDescriptor);
		this.items = items;
	}

	public SearchableItemAddEvent(SearchableItem item, ForegroundModuleDescriptor moduleDescriptor) {

		super(moduleDescriptor);
		this.items = Collections.singletonList(item);
	}

	@Override
	public ForegroundModuleDescriptor getModuleDescriptor() {

		return moduleDescriptor;
	}

	public List<SearchableItem> getItems() {

		return items;
	}

}
