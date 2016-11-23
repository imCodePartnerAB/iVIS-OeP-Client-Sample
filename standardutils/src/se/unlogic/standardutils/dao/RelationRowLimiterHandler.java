package se.unlogic.standardutils.dao;

import java.util.HashMap;


public class RelationRowLimiterHandler {

	private HashMap<Class<?>,RowLimiter> relationRowLimiterMap = new HashMap<Class<?>, RowLimiter>();
	
	public synchronized void addRelationParameter(Class<?> clazz, RowLimiter rowLimiter){
		
		if(rowLimiter == null){
			
			throw new NullPointerException("rowLimiter cannot be null");
		}
		
		relationRowLimiterMap.put(clazz, rowLimiter);
	}
	
	public RowLimiter getRowLimitier(Class<?> clazz){
		
		return relationRowLimiterMap.get(clazz);
	}
}
