/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.string;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;


public class BeanTagSource<T> implements TagSource{

	private final T bean;
	private final Set<String> tagSet;
	@SuppressWarnings("rawtypes")
	private final HashMap<String, Entry<Method,Stringyfier>> tagMethodMap;
	@SuppressWarnings("rawtypes")
	private final HashMap<String,Entry<Field,Stringyfier>> tagFieldMap;

	@SuppressWarnings("rawtypes")
	public BeanTagSource(T bean, HashMap<String, Entry<Method,Stringyfier>> tagMethodMap, HashMap<String, Entry<Field,Stringyfier>> tagFieldMap, Set<String> tagSet) {

		super();
		this.bean = bean;
		this.tagMethodMap = tagMethodMap;
		this.tagFieldMap = tagFieldMap;
		this.tagSet = tagSet;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String getTagValue(String tag) {

		Entry<Field,Stringyfier> fieldEntry = this.tagFieldMap.get(tag);

		if(fieldEntry != null){

			try {
				Object value = fieldEntry.getKey().get(bean);

				if(value != null){

					if(fieldEntry.getValue() != null){

						return fieldEntry.getValue().format(value);
					}

					return value.toString();
				}

				return null;

			} catch (IllegalArgumentException e) {

				throw new RuntimeException(e);

			} catch (IllegalAccessException e) {

				throw new RuntimeException(e);
			}
		}

		Entry<Method,Stringyfier> methodEntry = tagMethodMap.get(tag);

		if(methodEntry != null){

			try {
				Object value = methodEntry.getKey().invoke(bean);

				if(value != null){

					if(methodEntry.getValue() != null){

						return methodEntry.getValue().format(value);
					}

					return value.toString();
				}

				return null;

			} catch (IllegalArgumentException e) {

				throw new RuntimeException(e);

			} catch (IllegalAccessException e) {

				throw new RuntimeException(e);

			} catch (InvocationTargetException e) {

				throw new RuntimeException(e);
			}
		}

		return null;
	}

	public Set<String> getTags() {

		return this.tagSet;
	}
}
