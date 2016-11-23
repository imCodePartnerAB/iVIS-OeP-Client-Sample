/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.string;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import se.unlogic.standardutils.datatypes.SimpleEntry;
import se.unlogic.standardutils.reflection.ReflectionUtils;


public class BeanTagSourceFactory<T> implements TagSourceFactory<T>{

	private Class<T> beanClass;
	@SuppressWarnings("rawtypes")
	private HashMap<String,Entry<Method,Stringyfier>> tagMethodMap = new HashMap<String, Entry<Method,Stringyfier>>();
	@SuppressWarnings("rawtypes")
	private HashMap<String,Entry<Field,Stringyfier>> tagFieldMap = new HashMap<String, Entry<Field,Stringyfier>>();
	private HashSet<String> tagsSet = new HashSet<String>();

	public BeanTagSourceFactory(Class<T> beanClass) {

		this.beanClass = beanClass;
	}

	public void addMethodMapping(String tag, String methodName) throws NoSuchMethodException{

		addMethodMapping(tag, methodName, null);
	}

	@SuppressWarnings("rawtypes")
	public void addMethodMapping(String tag, String methodName, Stringyfier stringyfier) throws NoSuchMethodException{

		Method method = ReflectionUtils.getMethod(beanClass, methodName, Object.class);

		if(method == null){

			throw new NoSuchMethodException("Method " + methodName + " with no input parameters not found in " + beanClass);
		}

		addMethodMapping(tag, method, stringyfier);
	}

	@SuppressWarnings("rawtypes")
	protected void addMethodMapping(String tag, Method method, Stringyfier stringyfier){

		if(!method.isAccessible()){

			ReflectionUtils.fixMethodAccess(method);
		}

		tagMethodMap.put(tag, new SimpleEntry<Method,Stringyfier>(method, stringyfier));
		this.tagsSet.add(tag);
	}

	public void addFieldMapping(String tag, String fieldName) throws NoSuchFieldException{

		addFieldMapping(tag, fieldName, null);
	}

	@SuppressWarnings("rawtypes")
	public void addFieldMapping(String tag, String fieldName, Stringyfier stringyfier) throws NoSuchFieldException{

		Field field = ReflectionUtils.getField(beanClass, fieldName);

		if(field == null){

			throw new NoSuchFieldException("Field " + fieldName + " not found in " + beanClass);
		}

		addFieldMapping(tag, field, stringyfier);
	}

	@SuppressWarnings("rawtypes")
	protected void addFieldMapping(String tag, Field field, Stringyfier stringyfier){

		if(!field.isAccessible()){

			ReflectionUtils.fixFieldAccess(field);
		}

		tagFieldMap.put(tag, new SimpleEntry<Field,Stringyfier>(field, stringyfier));
		this.tagsSet.add(tag);
	}

	@SuppressWarnings("rawtypes")
	public void addAllFields(String fieldPrefix, String... excludedFields){

		List<String> exclusionList = null;

		if(excludedFields != null){

			exclusionList = Arrays.asList(excludedFields);
		}


		List<Field> fields = ReflectionUtils.getFields(beanClass);

		for(Field field : fields){

			if(exclusionList == null || !exclusionList.contains(field.getName())){

				if(!field.isAccessible()){

					ReflectionUtils.fixFieldAccess(field);
				}

				this.tagFieldMap.put(fieldPrefix + field.getName(), new SimpleEntry<Field,Stringyfier>(field, null));
				this.tagsSet.add(fieldPrefix + field.getName());
			}
		}
	}

	public <X extends T> BeanTagSource<T> getTagSource(X bean){

		return new BeanTagSource<T>(bean, tagMethodMap, tagFieldMap, tagsSet);
	}


	public HashSet<String> getTagsSet() {
		return new HashSet<String>(tagsSet);
	}

	public String getAvailableTags(){

		return StringUtils.toCommaSeparatedString(this.tagsSet);
	}
}
