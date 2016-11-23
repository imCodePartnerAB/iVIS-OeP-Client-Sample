/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.xml;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.standardutils.annotations.NoAnnotatedFieldsFoundException;
import se.unlogic.standardutils.arrays.ArrayUtils;
import se.unlogic.standardutils.date.DateUtils;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.string.DummyStringyfier;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.string.Stringyfier;

public class XMLGenerator {

	private static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private static ReadLock readLock = readWriteLock.readLock();
	private static WriteLock writeLock = readWriteLock.writeLock();

	private static WeakHashMap<Class<?>, ClassXMLInfo> FIELD_MAP = new WeakHashMap<Class<?>, ClassXMLInfo>();

	@SuppressWarnings("unchecked")
	public static Element toXML(Object bean, Document doc) {

		ClassXMLInfo classInfo = getClassInfo(bean.getClass());

		Element classElement = doc.createElement(classInfo.getElementName());

		XMLGeneratorDocument generatorDocument;
		
		if(doc instanceof XMLGeneratorDocument){
			
			generatorDocument = (XMLGeneratorDocument) doc;
			
		}else{
			
			generatorDocument = null;
		}
		
		for (FieldXMLInfo fieldInfo : classInfo.getFields()) {

			if(generatorDocument != null && generatorDocument.isIgnoredField(fieldInfo.getField())){
				
				continue;
			}
			
			Object fieldValue;
			try {
				fieldValue = fieldInfo.getField().get(bean);
			} catch (IllegalArgumentException e) {

				throw new RuntimeException(e);

			} catch (IllegalAccessException e) {

				throw new RuntimeException(e);
			}

			if (fieldValue == null) {

				continue;

			} else if (!fieldInfo.isList() && fieldInfo.getValueFormatter() != null) {

				fieldValue = fieldInfo.getValueFormatter().format(fieldValue);

			} else if (fieldValue instanceof Date) {

				fieldValue = DateUtils.DATE_TIME_FORMATTER.format((Date) fieldValue);
			}

			if (fieldInfo.getFieldType() == FieldType.ATTRIBUTE) {

				classElement.setAttribute(fieldInfo.getName(), fieldValue.toString());

			} else if (fieldInfo.isList()) {

				Collection<?> fieldValues = (Collection<?>) fieldValue;

				if (fieldValues.isEmpty()) {

					continue;
				}

				Element subElement;

				if (fieldInfo.skipSubElement()) {

					subElement = classElement;

				} else {

					subElement = doc.createElement(fieldInfo.getName());
				}

				for (Object value : fieldValues) {

					if (value != null) {

						parseValue(fieldInfo, value, subElement, doc, generatorDocument);
					}
				}

				if (!fieldInfo.skipSubElement() && subElement.hasChildNodes()) {
					classElement.appendChild(subElement);
				}

			} else if (fieldInfo.isArray()) {

				Object[] fieldValues = (Object[]) fieldValue;

				if (ArrayUtils.isEmpty(fieldValues)) {

					continue;
				}

				Element subElement;

				if (fieldInfo.skipSubElement()) {

					subElement = classElement;

				} else {

					subElement = doc.createElement(fieldInfo.getName());
				}

				for (Object value : fieldValues) {

					if (value != null) {

						parseValue(fieldInfo, value, subElement, doc, generatorDocument);
					}
				}

				if (!fieldInfo.skipSubElement() && subElement.hasChildNodes()) {
					classElement.appendChild(subElement);
				}

			} else if (fieldInfo.isElementable()) {

				Element subElement = ((Elementable) fieldValue).toXML(doc);

				if (subElement != null) {

					if (fieldInfo.getName() != null) {

						subElement = (Element) doc.renameNode(subElement, null, fieldInfo.getName());
					}
					
					if (fieldInfo.getChildName() != null) {

						Element middleElement = doc.createElement(fieldInfo.getChildName());
						classElement.appendChild(middleElement);
						middleElement.appendChild(subElement);

					} else {

						classElement.appendChild(subElement);
					}
					
					triggerElementableListener(generatorDocument, subElement, fieldValue);
					
				}
			} else if (fieldInfo.isCDATA()) {

				classElement.appendChild(XMLUtils.createCDATAElement(fieldInfo.getName(), fieldValue.toString(), doc));

			} else {
				classElement.appendChild(XMLUtils.createElement(fieldInfo.getName(), fieldValue.toString(), doc));
			}
		}

		return classElement;
	}

	public static String getElementName(Class<?> clazz){
		
		return getClassInfo(clazz).getElementName();
	}
	
	@SuppressWarnings("unchecked")
	private static ClassXMLInfo getClassInfo(Class<?> clazz) {

		ClassXMLInfo classInfo = null;
		
		try {
			readLock.lock();
			classInfo = FIELD_MAP.get(clazz);
		} finally {
			readLock.unlock();
		}		
		
		if(classInfo != null){
			
			return classInfo;
		}
		
		XMLElement xmlElement = clazz.getAnnotation(XMLElement.class);

		if (xmlElement == null) {

			throw new MissingXMLAnnotationException(clazz);
		}

		String elementName = xmlElement.name();

		if (StringUtils.isEmpty(elementName)) {

			elementName = clazz.getSimpleName();
		}

		if (xmlElement.fixCase()) {

			elementName = StringUtils.toFirstLetterUppercase(elementName);
		}

		List<FieldXMLInfo> annotatedFields = new ArrayList<FieldXMLInfo>();

		Class<?> currentClazz = clazz;

		while (currentClazz != Object.class) {

			Field[] fields = currentClazz.getDeclaredFields();

			for (Field field : fields) {

				XMLElement elementAnnotation = field.getAnnotation(XMLElement.class);

				if (elementAnnotation != null) {

					String name = elementAnnotation.name();

					if (StringUtils.isEmpty(name)) {

						name = field.getName();

						if (elementAnnotation.fixCase()) {

							name = StringUtils.toFirstLetterUppercase(name);
						}
					}

					@SuppressWarnings("rawtypes")
					Stringyfier valueFormatter = null;

					if (elementAnnotation.valueFormatter() != DummyStringyfier.class) {

						try {
							valueFormatter = elementAnnotation.valueFormatter().newInstance();

						} catch (InstantiationException e) {

							throw new RuntimeException(e);

						} catch (IllegalAccessException e) {

							throw new RuntimeException(e);
						}
					}

					if (Collection.class.isAssignableFrom(field.getType())) {

						boolean elementable = false;

						if (ReflectionUtils.isGenericlyTyped(field) && Elementable.class.isAssignableFrom((Class<?>) ReflectionUtils.getGenericType(field))) {

							elementable = true;
						}

						String childName = elementAnnotation.childName();

						if (StringUtils.isEmpty(childName)) {

							childName = "value";
						}

						annotatedFields.add(new FieldXMLInfo(name, field, FieldType.ELEMENT, elementAnnotation.cdata(), elementable, true, false, childName, elementAnnotation.skipChildParentElement(), valueFormatter));

					} else if (field.getType().isArray()) {

						boolean elementable = false;

						if (Elementable.class.isAssignableFrom(field.getType())) {

							elementable = true;
						}

						String childName = elementAnnotation.childName();

						if (StringUtils.isEmpty(childName)) {

							childName = "value";
						}

						annotatedFields.add(new FieldXMLInfo(name, field, FieldType.ELEMENT, elementAnnotation.cdata(), elementable, false, true, childName, elementAnnotation.skipChildParentElement(), valueFormatter));

					} else {

						boolean elementable = Elementable.class.isAssignableFrom(field.getType());

						String childName = null;
						
						if (!StringUtils.isEmpty(elementAnnotation.childName())) {

							childName = elementAnnotation.childName();
						
						}
						
						if(elementable && StringUtils.isEmpty(elementAnnotation.name())) {

							name = null;
						}

						annotatedFields.add(new FieldXMLInfo(name, field, FieldType.ELEMENT, elementAnnotation.cdata(), elementable, false, false, childName, false, valueFormatter));
					}

					ReflectionUtils.fixFieldAccess(field);
				}

				XMLAttribute attributeAnnotation = field.getAnnotation(XMLAttribute.class);

				if (attributeAnnotation != null) {

					String name = attributeAnnotation.name();

					if (StringUtils.isEmpty(name)) {

						name = field.getName();
					}

					@SuppressWarnings("rawtypes")
					Stringyfier valueFormatter = null;

					if (attributeAnnotation.valueFormatter() != DummyStringyfier.class) {

						try {
							valueFormatter = attributeAnnotation.valueFormatter().newInstance();

						} catch (InstantiationException e) {

							throw new RuntimeException(e);

						} catch (IllegalAccessException e) {

							throw new RuntimeException(e);
						}
					}

					annotatedFields.add(new FieldXMLInfo(name, field, FieldType.ATTRIBUTE, false, false, false, false, null, false, valueFormatter));

					ReflectionUtils.fixFieldAccess(field);
				}
			}

			currentClazz = currentClazz.getSuperclass();
		}

		if (annotatedFields.isEmpty()) {

			throw new NoAnnotatedFieldsFoundException(clazz, XMLElement.class, XMLAttribute.class);
		}

		classInfo = new ClassXMLInfo(elementName, annotatedFields);

		try {
			writeLock.lock();
			FIELD_MAP.put(clazz, classInfo);
		} finally {
			writeLock.unlock();
		}
		
		return classInfo;
	}

	@SuppressWarnings("unchecked")
	private static void triggerElementableListener(XMLGeneratorDocument doc, Element subElement, Object fieldValue) {

		if(doc != null){
			
			@SuppressWarnings("rawtypes")
			ElementableListener elementableListener = doc.getElementableListener(fieldValue.getClass());
			
			if(elementableListener != null){
				
				elementableListener.elementGenerated(doc.getDocument(), subElement, fieldValue);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void parseValue(FieldXMLInfo fieldInfo, Object value, Element subElement, Document doc, XMLGeneratorDocument generatorDocument) {

		if (fieldInfo.getValueFormatter() != null) {

			value = fieldInfo.getValueFormatter().format(value);

		} else if (value instanceof Date) {

			value = DateUtils.DATE_TIME_FORMATTER.format((Date) value);
		}

		if (fieldInfo.isElementable()) {

			Element subSubElement = ((Elementable) value).toXML(doc);

			if (subSubElement != null) {

				subElement.appendChild(subSubElement);
			}
			
			triggerElementableListener(generatorDocument, subSubElement, value);
			
		} else {

			if (fieldInfo.isCDATA()) {

				subElement.appendChild(XMLUtils.createCDATAElement(fieldInfo.getChildName(), value, doc));

			} else {

				subElement.appendChild(XMLUtils.createElement(fieldInfo.getChildName(), value, doc));
			}
		}
	}
}
