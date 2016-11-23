package se.unlogic.standardutils.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RelationParameterHandler {

	@SuppressWarnings("rawtypes")
	private HashMap<Class<?>,List> relationParameterMap = new HashMap<Class<?>, List>();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized <X> void addRelationParameter(Class<X> clazz, QueryParameter<X, ?> queryParameter){
		
		if(queryParameter == null){
			
			throw new NullPointerException("queryParameter cannot be null");
		}
		
		List<QueryParameter> parameterList = relationParameterMap.get(clazz);
		
		if(parameterList == null){
			
			parameterList = new ArrayList<QueryParameter>();
			relationParameterMap.put(clazz, parameterList);
		}
		
		parameterList.add(queryParameter);
	}
	
	@SuppressWarnings("unchecked")
	public <X> List<QueryParameter<X, ?>> getRelationParameters(Class<X> clazz){
		
		return relationParameterMap.get(clazz);
	}
}
