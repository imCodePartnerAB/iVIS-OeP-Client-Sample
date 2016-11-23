package se.unlogic.hierarchy.core.enums;

import java.util.Comparator;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.standardutils.collections.ExternalMethodComparator;
import se.unlogic.standardutils.enums.Order;
import se.unlogic.standardutils.string.StringUtils;



public enum UserField {
	FIRSTNAME,
	LASTNAME,
	USERNAME,
	EMAIL;
	
	private Comparator<User> ascComparator;
	private Comparator<User> descComparator;
	
	private UserField(){
		
		String methodName = "get" + StringUtils.toSentenceCase(this.toString());
		
		ascComparator = new ExternalMethodComparator<User, String>(User.class, String.class, methodName,Order.ASC, String.CASE_INSENSITIVE_ORDER);
		descComparator = new ExternalMethodComparator<User, String>(User.class, String.class, methodName,Order.DESC, String.CASE_INSENSITIVE_ORDER);
	}

	
	public Comparator<User> getComparator(Order order) {
	
		if(order == Order.ASC){
			
			return ascComparator;
		}
		
		return descComparator;
	}
}
