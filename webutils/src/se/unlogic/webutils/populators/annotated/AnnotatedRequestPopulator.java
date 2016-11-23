/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.webutils.populators.annotated;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.standardutils.annotations.NoAnnotatedFieldsOrMethodsFoundException;
import se.unlogic.standardutils.annotations.PopulateOnlyIfRequired;
import se.unlogic.standardutils.annotations.PopulateOnlyIfSet;
import se.unlogic.standardutils.annotations.RequiredIfNotSet;
import se.unlogic.standardutils.annotations.RequiredIfSet;
import se.unlogic.standardutils.annotations.UnsupportedFieldTypeException;
import se.unlogic.standardutils.annotations.UnsupportedMethodArgumentException;
import se.unlogic.standardutils.annotations.WebPopulate;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.factory.BeanFactory;
import se.unlogic.standardutils.populators.BeanStringPopulator;
import se.unlogic.standardutils.populators.BeanStringPopulatorRegistery;
import se.unlogic.standardutils.populators.DummyPopulator;
import se.unlogic.standardutils.populators.EnumPopulator;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.TooLongContentValidationError;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationErrorType;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.webutils.http.BeanRequestPopulator;


public class AnnotatedRequestPopulator<T> implements BeanRequestPopulator<T>{

	protected Class<T> beanClass;
	protected ArrayList<RequestMapping> requestMappings = new ArrayList<RequestMapping>();
	protected boolean containsRequestLists;
	protected BeanFactory<? extends T> beanFactory;

	public AnnotatedRequestPopulator(Class<T> beanClass) throws UnsupportedFieldTypeException{
		this(beanClass, (List<BeanStringPopulator<?>>)null);
	}

	public AnnotatedRequestPopulator(Class<T> beanClass, BeanStringPopulator<?>... typePopulators) throws UnsupportedFieldTypeException{
		this(beanClass, Arrays.asList(typePopulators));
	}

	@SuppressWarnings("unchecked")
	private AnnotatedRequestPopulator(Class<T> beanClass, List<BeanStringPopulator<?>> customPopulators) throws UnsupportedFieldTypeException{

		this.beanClass = beanClass;

		//cache fields
		List<Field> fields = ReflectionUtils.getFields(beanClass);

		for(Field field : fields){

			WebPopulate annotation = field.getAnnotation(WebPopulate.class);

			if(annotation != null){

				if(Modifier.isFinal(field.getModifiers())){

					throw new UnsupportedFieldTypeException("The annotated field " + field.getName() + " in " + beanClass + " is final!", field, annotation.getClass(), beanClass);

				}

				BeanStringPopulator<?> populator = null;

				if(annotation.populator() != DummyPopulator.class){

					try {
						populator = annotation.populator().newInstance();

					} catch (InstantiationException e) {

						throw new RuntimeException(e);

					} catch (IllegalAccessException e) {

						throw new RuntimeException(e);
					}
				}

				if(populator == null && customPopulators != null){
					populator = this.getPopulator(customPopulators,field,annotation);
				}

				if(populator == null){
					populator = this.getPopulator(BeanStringPopulatorRegistery.getBeanStringPopulators(),field,annotation);
				}

				if(populator == null){

					if(field.getType().isEnum()){

						populator = EnumPopulator.getInstanceFromField(field);

					}else if(List.class.isAssignableFrom(field.getType()) && ReflectionUtils.getGenericlyTypeCount(field) == 1 && ((Class<?>)ReflectionUtils.getGenericType(field)).isEnum()){

						populator = EnumPopulator.getInstanceFromListField(field);
						containsRequestLists = true;
					}
				}

				if(populator == null){

					throw new UnsupportedFieldTypeException("Unable to find suitable BeanStringPopulator for the annotated field " + field.getName() + " in class " + beanClass + " with type " + field.getType(), field, annotation.getClass(), beanClass);
				}

				if(!StringUtils.isEmpty(annotation.paramName())){

					this.requestMappings.add(new FieldRequestMapping(field, populator, annotation, annotation.paramName()));
				}else{
					this.requestMappings.add(new FieldRequestMapping(field, populator, annotation, field.getName()));
				}

				ReflectionUtils.fixFieldAccess(field);
			}
		}

		List<Method> methods = ReflectionUtils.getMethods(beanClass);

		//This list is used to store the names of checked and valid methods so that methods are not added twice in case of method overriding
		List<String> addedMethodNames = new ArrayList<String>(methods.size());

		for(Method method : methods){

			WebPopulate annotation = method.getAnnotation(WebPopulate.class);

			if(annotation != null && !addedMethodNames.contains(method.getName())){

				if(method.getParameterTypes().length != 1){

					throw new UnsupportedMethodArgumentException("The annotated method " + method.getName() + " in " + beanClass + " requires an invalid number of arguments, only 1 argument is allowed for methods annotated with the @WebPopulate annotation!", method, annotation.getClass(), beanClass);

				}

				BeanStringPopulator<?> populator = null;

				if(annotation.populator() != DummyPopulator.class){

					try {
						populator = annotation.populator().newInstance();

					} catch (InstantiationException e) {

						throw new RuntimeException(e);

					} catch (IllegalAccessException e) {

						throw new RuntimeException(e);
					}
				}

				if(populator == null && customPopulators != null){
					populator = this.getPopulator(customPopulators,method,annotation);
				}

				if(populator == null){
					populator = this.getPopulator(BeanStringPopulatorRegistery.getBeanStringPopulators(),method,annotation);
				}

				if(populator == null){

					if(method.getParameterTypes()[0].isEnum()){

						populator = EnumPopulator.getInstanceFromMethod(method);

					}else if(List.class.isAssignableFrom(method.getParameterTypes()[0]) && ReflectionUtils.getGenericlyTypeCount(method) == 1 && ((Class<?>)ReflectionUtils.getGenericType(method)).isEnum()){

						populator = EnumPopulator.getInstanceFromListMethod(method);
						containsRequestLists = true;
					}
				}

				if(populator == null){

					throw new UnsupportedMethodArgumentException("Unable to find suitable BeanStringPopulator for the annotated method " + method.getName() + " in class " + beanClass + " with type " + method.getParameterTypes()[0], method, annotation.getClass(), beanClass);
				}

				if(!StringUtils.isEmpty(annotation.paramName())){

					this.requestMappings.add(new MethodRequestMapping(method, populator, annotation, annotation.paramName()));

				}else if(method.getName().startsWith("set") && method.getName().length() > 3){

					this.requestMappings.add(new MethodRequestMapping(method, populator, annotation, StringUtils.toFirstLetterLowercase(method.getName().substring(3))));

				}else{

					this.requestMappings.add(new MethodRequestMapping(method, populator, annotation, method.getName()));
				}

				ReflectionUtils.fixMethodAccess(method);

				addedMethodNames.add(method.getName());
			}
		}

		if(this.requestMappings.isEmpty()){
			throw new NoAnnotatedFieldsOrMethodsFoundException(beanClass,WebPopulate.class);
		}
	}

	private BeanStringPopulator<?> getPopulator(Collection<BeanStringPopulator<?>> populators, Field field, WebPopulate annotation) {

		boolean list = false;

		String populatorID = annotation.populatorID();

		Object clazz;

		if(List.class.isAssignableFrom(field.getType()) && ReflectionUtils.getGenericlyTypeCount(field) == 1){
			clazz = ReflectionUtils.getGenericType(field);
			list = true;
		}else{
			clazz = field.getType();
		}

		return getBeanStringPopulator(clazz, populators, populatorID, list);
	}

	private BeanStringPopulator<?> getPopulator(Collection<BeanStringPopulator<?>> populators, Method method, WebPopulate annotation) {

		boolean list = false;

		String populatorID = annotation.populatorID();

		Object clazz;

		if(List.class.isAssignableFrom(method.getParameterTypes()[0]) && ReflectionUtils.getGenericlyTypeCount(method) == 1){
			clazz = ReflectionUtils.getGenericType(method);
			list = true;
		}else{
			clazz = method.getParameterTypes()[0];
		}

		return getBeanStringPopulator(clazz, populators, populatorID, list);
	}

	protected BeanStringPopulator<?> getBeanStringPopulator(Object clazz, Collection<BeanStringPopulator<?>> populators, String populatorID, boolean list) {

		for(BeanStringPopulator<?> populator : populators){

			if(clazz.equals(populator.getType())){

				if((StringUtils.isEmpty(populatorID) && populator.getPopulatorID() == null) || populatorID.equals(populator.getPopulatorID())){

					if(list){
						this.containsRequestLists = true;
					}

					return populator;
				}
			}
		}

		return null;
	}

	public T populate(HttpServletRequest req) throws ValidationException {

		return this.populate(null, req);
	}

	@SuppressWarnings({ "unchecked" })
	public T populate(T bean, HttpServletRequest req) throws ValidationException {

		ArrayList<ValidationError> errorList = new ArrayList<ValidationError>();

		HashMap<RequestMapping,String> valueMap = new HashMap<RequestMapping, String>();

		HashMap<RequestMapping,List<String>> listValueMap = null;

		if(containsRequestLists){
			listValueMap = new HashMap<RequestMapping, List<String>>();
		}

		for(RequestMapping requestMapping : this.requestMappings){

			if(requestMapping.isList()){

				String[] rawValues = req.getParameterValues(requestMapping.getParamName());

				ArrayList<String> checkedValues = new ArrayList<String>();

				if(rawValues != null){

					if(requestMapping.isSplitOnLineBreak()){

						outer: for(String value : rawValues){

							ArrayList<String> splitValues = StringUtils.splitOnLineBreak(value,false);

							if(splitValues != null){

								for(String splitValue : splitValues){

									if(!this.checkValue(splitValue, null, checkedValues, errorList, requestMapping,req)){
										break outer;
									}
								}
							}
						}

					}else{

						for(String value : rawValues){

							if(!this.checkValue(value, null, checkedValues, errorList, requestMapping,req)){
								break;
							}
						}
					}


					if(errorList.isEmpty()){

						if(checkedValues.isEmpty()){

							listValueMap.put(requestMapping, null);

						}else{

							listValueMap.put(requestMapping, checkedValues);
						}
					}

				}else{
					//Generate error if nesscary
					this.checkValue(null, null, checkedValues, errorList, requestMapping, req);

					//Null the list-member in the bean if the arriving requestField is an empty list
					listValueMap.put(requestMapping, null);
				}

			}else{
				this.checkValue(req.getParameter(requestMapping.getParamName()), valueMap, null, errorList, requestMapping,req);
			}
		}

		if(errorList.isEmpty()){

			RequestMapping currentMapping = null;

			try {

				if(bean == null){

					bean = getNewBeanInstance();
				}

				for(Entry<RequestMapping,String> valueEntry : valueMap.entrySet()){

					currentMapping = valueEntry.getKey();

					if(isSkip(currentMapping, req)){

						continue;
					}

					if(!(valueEntry.getValue() == null)){

						currentMapping.setValue(bean, currentMapping.getBeanStringPopulator().getValue(valueEntry.getValue()));

					}else if(currentMapping.isPrimitive()){

						//Handle primitive BeanTypePopulators gently
						currentMapping.setValue(bean, currentMapping.getBeanStringPopulator().getValue(null));

					}else{

						currentMapping.setValue(bean, null);
					}
				}

				if(listValueMap != null){

					for(Entry<RequestMapping,List<String>> valueEntry : listValueMap.entrySet()){

						currentMapping = valueEntry.getKey();

						if(isSkip(currentMapping, req)){

							continue;
						}

						if(valueEntry.getValue() != null){

							@SuppressWarnings("rawtypes")
							List typedList = CollectionUtils.getGenericList(currentMapping.getBeanStringPopulator().getType(), listValueMap.size());

							for(String value : valueEntry.getValue()){

								Object typedValue = currentMapping.getBeanStringPopulator().getValue(value);

								if(currentMapping.isNoDuplicates() && typedList.contains(typedValue)) {
									continue;
								}

								typedList.add(typedValue);
							}

							currentMapping.setValue(bean, typedList);

						}else{

							currentMapping.setValue(bean, null);
						}
					}
				}

				return bean;

			} catch (IllegalArgumentException e) {

				throw new BeanRequestPopulationFailed(currentMapping,e);
			}

		}else{
			throw new ValidationException(errorList);
		}
	}

	protected T getNewBeanInstance() {

		if(beanFactory != null){

			return beanFactory.newInstance();
		}

		try {
			return beanClass.newInstance();

		}catch (InstantiationException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}
	}

	private boolean checkValue(String value, HashMap<RequestMapping, String> valueMap, List<String> valueList, ArrayList<ValidationError> errorList, RequestMapping requestMapping, HttpServletRequest req) {

		if(isSkip(requestMapping,req)){

			return true;
		}

		if(StringUtils.isEmpty(value)){

			if(requestMapping.isRequired() || this.checkRequired(requestMapping,req)){

				if(requestMapping.getType() == boolean.class || requestMapping.getType() == Boolean.class){

					if(valueMap != null){
						valueMap.put(requestMapping, "false");
					}else{
						valueList.add("false");
					}

				}else{

					errorList.add(new ValidationError(requestMapping.getParamName(), ValidationErrorType.RequiredField));
				}

			}else{

				if(valueMap != null){
					valueMap.put(requestMapping, null);
				}else{
					valueList.add(null);
				}
			}

			return true;

		}else{

			if(requestMapping.getMinLength() > 0 && value.length() < requestMapping.getMinLength()){

				errorList.add(new ValidationError(requestMapping.getParamName(), ValidationErrorType.TooShort));

			}else if(requestMapping.getMaxLength() > 0 && value.length() > requestMapping.getMaxLength()){

				errorList.add(new TooLongContentValidationError(requestMapping.getParamName(), value.length(), requestMapping.getMaxLength()));

			}else if(!requestMapping.getBeanStringPopulator().validateFormat(value)){

				errorList.add(new ValidationError(requestMapping.getParamName(), ValidationErrorType.InvalidFormat));

			}else if(errorList.isEmpty()){

				if(requestMapping.isTrim()){
					value = value.trim();
				}

				if(valueMap != null){
					valueMap.put(requestMapping, value);
				}else{
					valueList.add(value);
				}

				return true;
			}
		}

		return false;
	}

	protected boolean isSkip(RequestMapping requestMapping, HttpServletRequest req) {

		if(requestMapping.getAnnotation(PopulateOnlyIfRequired.class) != null && !requestMapping.isRequired() && !this.checkRequired(requestMapping,req)){

			return true;

		}

		PopulateOnlyIfSet populateOnlyIfSet = requestMapping.getAnnotation(PopulateOnlyIfSet.class);

		if(populateOnlyIfSet != null && !(populateOnlyIfSet.paramNames().length == 1 && populateOnlyIfSet.paramNames()[0].equals(""))){

			int paramIndex = 1;

			String[] paramValues = null;

			if(!(populateOnlyIfSet.paramValues().length == 1 && populateOnlyIfSet.paramValues()[0].equals(""))){

				paramValues = populateOnlyIfSet.paramValues();
			}

			for(String paramName : populateOnlyIfSet.paramNames()){

				String paramValue = req.getParameter(paramName);

				if(paramValues != null && paramValues.length >= paramIndex && !paramValues[paramIndex-1].equals("")){

					if(paramValue == null || !paramValues[paramIndex-1].equals(paramValue)){
						return true;
					}

				}else if(StringUtils.isEmpty(req.getParameter(paramName))){

					return true;
				}

				paramIndex++;
			}
		}

		return false;
	}

	protected boolean checkRequired(RequestMapping mapping, HttpServletRequest req) {

		RequiredIfSet requiredIfSet = mapping.getAnnotation(RequiredIfSet.class);

		if(requiredIfSet != null){

			String paramName = requiredIfSet.paramName();

			String paramValue = req.getParameter(paramName);

			if(StringUtils.isEmpty(requiredIfSet.value())){

				if(!StringUtils.isEmpty(paramValue)){

					return true;
				}

			}else if(!StringUtils.isEmpty(paramValue)){

				if(requiredIfSet.value().equals(paramValue)){

					return true;
				}
			}
		}

		RequiredIfNotSet requiredNotSet = mapping.getAnnotation(RequiredIfNotSet.class);

		if(requiredNotSet != null && !(requiredNotSet.paramNames().length == 1 && requiredNotSet.paramNames()[0].equals(""))){

			int paramIndex = 1;

			String[] paramValues = null;

			if(!(requiredNotSet.paramValues().length == 1 && requiredNotSet.paramValues()[0].equals(""))){

				paramValues = requiredNotSet.paramValues();
			}

			for(String paramName : requiredNotSet.paramNames()){

				String paramValue = req.getParameter(paramName);

				if(paramValues != null && paramValues.length >= paramIndex && !paramValues[paramIndex-1].equals("")){

					if(paramValue != null && paramValues[paramIndex-1].equals(paramValue)){

						return false;
					}

				}else if(!StringUtils.isEmpty(req.getParameter(paramName))){

					return false;
				}

				paramIndex++;
			}

			return true;
		}

		return false;
	}


	public void setBeanFactory(BeanFactory<? extends T> beanFactory) {

		this.beanFactory = beanFactory;
	}


	public ArrayList<RequestMapping> getRequestMappings() {

		return requestMappings;
	}
}
