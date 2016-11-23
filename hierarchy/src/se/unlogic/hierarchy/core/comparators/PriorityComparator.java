package se.unlogic.hierarchy.core.comparators;

import java.util.Comparator;

import se.unlogic.hierarchy.core.interfaces.Prioritized;
import se.unlogic.standardutils.enums.Order;

public class PriorityComparator implements Comparator<Prioritized> {

	private final Order order;
	
	public PriorityComparator(Order order) {

		super();
		this.order = order;
	}

	@Override
	public int compare(Prioritized o1, Prioritized o2) {

		if(order == Order.ASC){
			
			return ((Integer)o1.getPriority()).compareTo(o2.getPriority());
			
		}else{
			
			return ((Integer)o2.getPriority()).compareTo(o1.getPriority());
		}
	}
}
