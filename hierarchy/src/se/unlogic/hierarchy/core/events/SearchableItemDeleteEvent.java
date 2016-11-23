package se.unlogic.hierarchy.core.events;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SearchableItem;

/**
 * This event is used to signal search modules regarding deleted items.
 * 
 * @author Unlogic
 * 
 */
public class SearchableItemDeleteEvent extends BaseSearchableEvent implements Serializable{

	private static final long serialVersionUID = 2371864432121147623L;
	
	private final List<String> itemIDs;

	public SearchableItemDeleteEvent(String itemID, ForegroundModuleDescriptor moduleDescriptor) {

		super(moduleDescriptor);
		this.itemIDs = Collections.singletonList(itemID);
	}

	public SearchableItemDeleteEvent(List<String> itemIDs, ForegroundModuleDescriptor moduleDescriptor) {

		super(moduleDescriptor);
		this.itemIDs = itemIDs;
	}

	public SearchableItemDeleteEvent(ForegroundModuleDescriptor moduleDescriptor, List<SearchableItem> searchableItems) {

		super(moduleDescriptor);

		itemIDs = new ArrayList<String>(searchableItems.size());

		for(SearchableItem item : searchableItems){

			itemIDs.add(item.getID());
		}
	}

	@Override
	public ForegroundModuleDescriptor getModuleDescriptor() {

		return moduleDescriptor;
	}

	public List<String> getItemIDs() {

		return itemIDs;
	}

}
