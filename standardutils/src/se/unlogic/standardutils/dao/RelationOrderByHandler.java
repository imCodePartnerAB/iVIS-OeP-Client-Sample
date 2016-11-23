package se.unlogic.standardutils.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RelationOrderByHandler {

	@SuppressWarnings("rawtypes")
	private HashMap<Class<?>,List> relationOrderByCriteriaMap = new HashMap<Class<?>, List>();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized <X> void addRelationOrderByCriteria(Class<X> clazz, OrderByCriteria<X> orderByCriteria){
				
		if(orderByCriteria == null){
			
			throw new NullPointerException("orderByCriteria cannot be null");
		}
		
		List<OrderByCriteria> orderByBriteriaList = relationOrderByCriteriaMap.get(clazz);
		
		if(orderByBriteriaList == null){
			
			orderByBriteriaList = new ArrayList<OrderByCriteria>();
			relationOrderByCriteriaMap.put(clazz, orderByBriteriaList);
		}
		
		orderByBriteriaList.add(orderByCriteria);
	}
	
	@SuppressWarnings("unchecked")
	public <X> List<OrderByCriteria<X>> getRelationOrderByCriterias(Class<X> clazz){
		
		return relationOrderByCriteriaMap.get(clazz);
	}
}
