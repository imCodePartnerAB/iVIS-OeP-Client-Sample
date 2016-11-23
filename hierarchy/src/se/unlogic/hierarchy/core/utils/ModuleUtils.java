/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.utils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import se.unlogic.hierarchy.core.annotations.CheckboxSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.DataSourceDropDownSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.DropDownSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.EnumDropDownSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.EnumMultiListSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.GroupMultiListSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.HTMLEditorSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.MultiListSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.PasswordSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.RadioButtonSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.TextAreaSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.UserMultiListSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.XSLVariable;
import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.beans.LinkTag;
import se.unlogic.hierarchy.core.beans.ScriptTag;
import se.unlogic.hierarchy.core.beans.SettingDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleDataSourceDescriptor;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.beans.ValueDescriptor;
import se.unlogic.hierarchy.core.exceptions.DataSourceException;
import se.unlogic.hierarchy.core.interfaces.BackgroundModule;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModule;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.Module;
import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.MutableSettingHandler;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.interfaces.SystemInterface;
import se.unlogic.hierarchy.core.interfaces.VisibleModuleDescriptor;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.datatypes.SimpleEntry;
import se.unlogic.standardutils.enums.EnumUtils;
import se.unlogic.standardutils.populators.BeanStringPopulator;
import se.unlogic.standardutils.populators.DummyPopulator;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.DummyStringFormatValidator;
import se.unlogic.standardutils.validation.StringFormatValidator;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.standardutils.xsl.XSLVariableReader;

public class ModuleUtils {

	protected static Logger log = Logger.getLogger(ModuleUtils.class);


	public static <T extends BackgroundModule> Entry<BackgroundModuleDescriptor,T> findBackgroundModule(Class<T> moduleClass, boolean recursive, SectionInterface sectionInterface) {

		return findBackgroundModule(moduleClass, null, recursive, sectionInterface);
	}

	public static <T extends BackgroundModule> Entry<BackgroundModuleDescriptor,T> findBackgroundModule(Class<T> moduleClass, Integer moduleID, boolean recursive, SectionInterface sectionInterface) {

		return findBackgroundModule(moduleClass, false, moduleID, recursive, sectionInterface);
	}

	public static <T extends BackgroundModule> Entry<BackgroundModuleDescriptor,T> findAssignableBackgroundModule(Class<T> moduleClass, boolean recursive, SectionInterface sectionInterface) {

		return findBackgroundModule(moduleClass, null, recursive, sectionInterface);
	}

	public static <T extends BackgroundModule> Entry<BackgroundModuleDescriptor,T> findAssignableBackgroundModule(Class<T> moduleClass, Integer moduleID, boolean recursive, SectionInterface sectionInterface) {

		return findBackgroundModule(moduleClass, true, moduleID, recursive, sectionInterface);
	}

	@SuppressWarnings("unchecked")
	public static <T extends BackgroundModule> Entry<BackgroundModuleDescriptor,T> findBackgroundModule(Class<T> moduleClass, boolean assignable, Integer moduleID, boolean recursive, SectionInterface sectionInterface) {

		if (sectionInterface.getBackgroundModuleCache().size() != 0) {

			for (Entry<BackgroundModuleDescriptor, BackgroundModule> moduleEntry : sectionInterface.getBackgroundModuleCache().getCachedModules()) {

				if ((!assignable && moduleEntry.getValue().getClass().equals(moduleClass)) || (assignable && moduleClass.isAssignableFrom(moduleEntry.getValue().getClass()))) {

					if(moduleID != null){

						if(moduleEntry.getKey().getModuleID() == null || !moduleEntry.getKey().getModuleID().equals(moduleID) ){

							continue;
						}
					}

					return new SimpleEntry<BackgroundModuleDescriptor,T>(moduleEntry.getKey(),(T)moduleEntry.getValue());
				}
			}
		}

		if (recursive) {

			for (SectionInterface section : sectionInterface.getSectionCache().getSectionMap().values()) {

				Entry<BackgroundModuleDescriptor,T> moduleEntry = findBackgroundModule(moduleClass, assignable, moduleID, recursive, section);

				if (moduleEntry != null) {

					return moduleEntry;
				}
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> Entry<ForegroundModuleDescriptor,T> findForegroundModule(Class<T> moduleClass, boolean assignable, Integer moduleID, boolean recursive, SectionInterface sectionInterface) {

		if (sectionInterface.getForegroundModuleCache().size() != 0) {

			for (Entry<ForegroundModuleDescriptor, ForegroundModule> moduleEntry : sectionInterface.getForegroundModuleCache().getCachedModules()) {

				if ((!assignable && moduleEntry.getValue().getClass().equals(moduleClass)) || (assignable && moduleClass.isAssignableFrom(moduleEntry.getValue().getClass()))) {

					if(moduleID != null){

						if(moduleEntry.getKey().getModuleID() == null || !moduleEntry.getKey().getModuleID().equals(moduleID) ){

							continue;
						}
					}

					return new SimpleEntry<ForegroundModuleDescriptor,T>(moduleEntry.getKey(),(T)moduleEntry.getValue());
				}
			}
		}

		if (recursive) {

			for (SectionInterface section : sectionInterface.getSectionCache().getSectionMap().values()) {

				Entry<ForegroundModuleDescriptor,T> moduleEntry = findForegroundModule(moduleClass, assignable, moduleID, recursive, section);

				if (moduleEntry != null) {

					return moduleEntry;
				}
			}
		}

		return null;
	}

	public static <T> HashMap<ForegroundModuleDescriptor, T> findForegroundModules(Class<T> moduleClass, boolean recursive, boolean assignable, SectionInterface sectionInterface) {

		HashMap<ForegroundModuleDescriptor, T> foregroundModules = new HashMap<ForegroundModuleDescriptor, T>();

		findForegroundModules(moduleClass, recursive, assignable, sectionInterface, foregroundModules);

		return foregroundModules;
	}

	@SuppressWarnings("unchecked")
	public static <T> void findForegroundModules(Class<T> moduleClass, boolean recursive, boolean assignable, SectionInterface sectionInterface, Map<ForegroundModuleDescriptor, T> foregroundModules) {

		if (sectionInterface.getForegroundModuleCache().size() != 0) {

			for (Entry<ForegroundModuleDescriptor, ForegroundModule> moduleEntry : sectionInterface.getForegroundModuleCache().getCachedModules()) {

				if (moduleClass.equals(moduleEntry.getValue().getClass()) || (assignable && moduleClass.isAssignableFrom(moduleEntry.getValue().getClass()))) {

					foregroundModules.put(moduleEntry.getKey(), (T) moduleEntry.getValue());
				}
			}
		}

		if (recursive) {

			for (SectionInterface section : sectionInterface.getSectionCache().getSectionMap().values()) {

				findForegroundModules(moduleClass, recursive, assignable, section, foregroundModules);
			}
		}
	}

	public static void addSettings(List<SettingDescriptor> localSettings, List<? extends SettingDescriptor> superSettings) {

		if (!CollectionUtils.isEmpty(superSettings)) {

			localSettings.addAll(superSettings);
		}
	}

	public static <Type extends Enum<Type>> List<ValueDescriptor> getValueDescriptors(Type[] values) {

		ArrayList<ValueDescriptor> descriptors = new ArrayList<ValueDescriptor>();

		for (Type type : values) {

			descriptors.add(new ValueDescriptor(type.toString(), type.toString()));
		}

		return descriptors;
	}

	public static List<ScriptTag> getScripts(XSLVariableReader variableReader, SectionInterface sectionInterface, String staticContentPrefix, ModuleDescriptor moduleDescriptor) throws SAXException, IOException, ParserConfigurationException, ClassNotFoundException, URISyntaxException {

		String scriptVariable = variableReader.getValue("scripts");

		if (!StringUtils.isEmpty(scriptVariable)) {

			scriptVariable = scriptVariable.replace("\t", "");

			String linkPrefix;

			linkPrefix = "/static/" + staticContentPrefix + "/" + sectionInterface.getSectionDescriptor().getSectionID() + "/" + getModuleDescriptorStaticContentIdentifier(moduleDescriptor);

			if (scriptVariable.contains("\n")) {

				List<String> scripts = StringUtils.splitOnLineBreak(scriptVariable, true);

				ArrayList<ScriptTag> scriptTags = new ArrayList<ScriptTag>(scripts.size());

				for (String script : scripts) {

					if (!StringUtils.isEmpty(script)) {

						scriptTags.add(new ScriptTag(linkPrefix + script));
					}
				}

				if (scriptTags.isEmpty()) {

					return null;
				}

				return scriptTags;

			} else {

				return Collections.singletonList(new ScriptTag(linkPrefix + scriptVariable));
			}
		}

		return null;
	}

	private static String getModuleDescriptorStaticContentIdentifier(ModuleDescriptor moduleDescriptor) {

		if(moduleDescriptor instanceof ForegroundModuleDescriptor){

			return (moduleDescriptor.getModuleID() != null ? moduleDescriptor.getModuleID().toString() : ((ForegroundModuleDescriptor)moduleDescriptor).getAlias());

		}else{

			return Integer.toString(moduleDescriptor.getModuleID() != null ? moduleDescriptor.getModuleID() : moduleDescriptor.hashCode());
		}
	}

	public static List<ScriptTag> getGlobalScripts(XSLVariableReader variableReader) throws SAXException, IOException, ParserConfigurationException, ClassNotFoundException, URISyntaxException {

		String scriptVariable = variableReader.getValue("globalscripts");

		if (!StringUtils.isEmpty(scriptVariable)) {

			scriptVariable = scriptVariable.replace("\t", "");

			String linkPrefix = "/static/global";

			if (scriptVariable.contains("\n")) {

				List<String> scripts = StringUtils.splitOnLineBreak(scriptVariable, true);

				ArrayList<ScriptTag> scriptTags = new ArrayList<ScriptTag>(scripts.size());

				for (String script : scripts) {

					if (!StringUtils.isEmpty(script)) {

						scriptTags.add(new ScriptTag(linkPrefix + script));
					}
				}

				if (scriptTags.isEmpty()) {

					return null;
				}

				return scriptTags;

			} else {

				return Collections.singletonList(new ScriptTag(linkPrefix + scriptVariable));
			}
		}

		return null;
	}

	public static List<LinkTag> getLinks(XSLVariableReader variableReader, SectionInterface sectionInterface, String staticContentPrefix, ModuleDescriptor moduleDescriptor) throws SAXException, IOException, ParserConfigurationException, ClassNotFoundException, URISyntaxException {

		String linksVariable = variableReader.getValue("links");

		if (!StringUtils.isEmpty(linksVariable)) {

			linksVariable = linksVariable.replace("\t", "");

			String linkPrefix = "/static/" + staticContentPrefix + "/" + sectionInterface.getSectionDescriptor().getSectionID() + "/" + getModuleDescriptorStaticContentIdentifier(moduleDescriptor);

			if (linksVariable.contains("\n")) {

				List<String> links = StringUtils.splitOnLineBreak(linksVariable, true);

				ArrayList<LinkTag> linkTags = new ArrayList<LinkTag>(links.size());

				for (String link : links) {

					if (!StringUtils.isEmpty(link)) {

						linkTags.add(new LinkTag(linkPrefix + link));
					}
				}

				if (linkTags.isEmpty()) {

					return null;
				}

				return linkTags;

			} else {

				return Collections.singletonList(new LinkTag(linkPrefix + linksVariable));
			}
		}

		return null;
	}

	public static List<LinkTag> getGlobalLinks(XSLVariableReader variableReader) throws SAXException, IOException, ParserConfigurationException, ClassNotFoundException, URISyntaxException {

		String linksVariable = variableReader.getValue("globallinks");

		if (!StringUtils.isEmpty(linksVariable)) {

			linksVariable = linksVariable.replace("\t", "");

			String linkPrefix = "/static/global";

			if (linksVariable.contains("\n")) {

				List<String> links = StringUtils.splitOnLineBreak(linksVariable, true);

				ArrayList<LinkTag> linkTags = new ArrayList<LinkTag>(links.size());

				for (String link : links) {

					if (!StringUtils.isEmpty(link)) {

						linkTags.add(new LinkTag(linkPrefix + link));
					}
				}

				if (linkTags.isEmpty()) {

					return null;
				}

				return linkTags;

			} else {

				return Collections.singletonList(new LinkTag(linkPrefix + linksVariable));
			}
		}

		return null;
	}	
	
	public static void setModuleSettings(Object target, Class<?> maxDepth, MutableSettingHandler mutableSettingHandler, SystemInterface systemInterface) {

		Class<?> clazz = target.getClass();

		while (clazz != maxDepth) {

			Field[] fields = clazz.getDeclaredFields();

			for (Field field : fields) {

				ModuleSetting moduleSetting;

				if ((moduleSetting = field.getAnnotation(ModuleSetting.class)) != null) {

					try {
						String id;

						if(!StringUtils.isEmpty(moduleSetting.id())){

							id = moduleSetting.id();

						}else{

							id = field.getName();
						}

						if (Modifier.isFinal(field.getModifiers())) {

							log.warn("Field " + field.getName() + " in " + clazz + " is final, skipping");

						} else if (moduleSetting.beanStringPopulator() != DummyPopulator.class) {

							try {
								BeanStringPopulator<?> typePopulator = moduleSetting.beanStringPopulator().newInstance();

								if (!field.getType().isAssignableFrom(typePopulator.getType())) {

									log.warn("The type of type populator " + moduleSetting.beanStringPopulator() + " does not match the type of field " + field.getName() + " in " + clazz + ", skipping");

								} else {

									String value = mutableSettingHandler.getString(id);

									if (value != null || moduleSetting.allowsNull()) {
										ReflectionUtils.setFieldValue(field, typePopulator.getValue(value), target);
									}
								}

							} catch (InstantiationException e) {

								log.error("Unable to create instance of type populator " + moduleSetting.beanStringPopulator() + " for " + field.getName() + " in " + clazz + ", skipping");
							}

						} else if (field.getType() == String.class) {

							String value = mutableSettingHandler.getString(id);

							if (value != null || moduleSetting.allowsNull()) {
								ReflectionUtils.setFieldValue(field, value, target);
							}

						} else if (field.getType() == List.class && ReflectionUtils.checkGenericTypes(field, String.class)) {

							List<String> values = mutableSettingHandler.getStrings(id);

							if (values != null || moduleSetting.allowsNull()) {
								ReflectionUtils.setFieldValue(field, values, target);
							}

						} else if (field.getType() == Integer.class) {

							Integer value = mutableSettingHandler.getInt(id);

							if (value != null || moduleSetting.allowsNull()) {
								ReflectionUtils.setFieldValue(field, value, target);
							}

						} else if (field.getType() == int.class) {

							Integer value = mutableSettingHandler.getInt(id);

							if (value != null) {
								ReflectionUtils.setFieldValue(field, value, target);
							}

						} else if (field.getType() == Short.class) {

							Short value = mutableSettingHandler.getShort(id);

							if (value != null || moduleSetting.allowsNull()) {
								ReflectionUtils.setFieldValue(field, value, target);
							}

						} else if (field.getType() == short.class) {

							Short value = mutableSettingHandler.getShort(id);

							if (value != null) {
								ReflectionUtils.setFieldValue(field, value, target);
							}

						} else if (field.getType() == List.class && ReflectionUtils.checkGenericTypes(field, Integer.class)) {

							List<Integer> values = mutableSettingHandler.getInts(id);

							if (values != null || moduleSetting.allowsNull()) {
								ReflectionUtils.setFieldValue(field, values, target);
							}

						} else if (field.getType() == Long.class) {

							Long value = mutableSettingHandler.getLong(id);

							if (value != null || moduleSetting.allowsNull()) {
								ReflectionUtils.setFieldValue(field, value, target);
							}

						} else if (field.getType() == long.class) {

							Long value = mutableSettingHandler.getLong(id);

							if (value != null) {
								ReflectionUtils.setFieldValue(field, value, target);
							}

						} else if (field.getType() == List.class && ReflectionUtils.checkGenericTypes(field, Long.class)) {

							List<Long> values = mutableSettingHandler.getLongs(id);

							if (values != null || moduleSetting.allowsNull()) {
								ReflectionUtils.setFieldValue(field, values, target);
							}

						} else if (field.getType() == Double.class) {

							Double value = mutableSettingHandler.getDouble(id);

							if (value != null || moduleSetting.allowsNull()) {
								ReflectionUtils.setFieldValue(field, value, target);
							}

						} else if (field.getType() == double.class) {

							Double value = mutableSettingHandler.getDouble(id);

							if (value != null) {
								ReflectionUtils.setFieldValue(field, value, target);
							}

						} else if (field.getType() == List.class && ReflectionUtils.checkGenericTypes(field, Double.class)) {

							List<Double> values = mutableSettingHandler.getDoubles(id);

							if (values != null || moduleSetting.allowsNull()) {
								ReflectionUtils.setFieldValue(field, values, target);
							}

						} else if (field.getType() == Float.class) {

							Float value = mutableSettingHandler.getFloat(id);

							if (value != null || moduleSetting.allowsNull()) {
								ReflectionUtils.setFieldValue(field, value, target);
							}

						} else if (field.getType() == float.class) {

							Float value = mutableSettingHandler.getFloat(id);

							if (value != null) {
								ReflectionUtils.setFieldValue(field, value, target);
							}

						} else if (field.getType() == List.class && ReflectionUtils.checkGenericTypes(field, Float.class)) {

							List<Float> values = mutableSettingHandler.getFloats(id);

							if (values != null || moduleSetting.allowsNull()) {
								ReflectionUtils.setFieldValue(field, values, target);
							}

						} else if (field.getType() == Boolean.class) {

							Boolean value = mutableSettingHandler.getBoolean(id);

							if (value != null || moduleSetting.allowsNull()) {
								ReflectionUtils.setFieldValue(field, value, target);
							}

						} else if (field.getType() == boolean.class) {

							Boolean value = mutableSettingHandler.getBoolean(id);

							if (value != null) {
								ReflectionUtils.setFieldValue(field, value, target);
							}

						}else if (field.getType() == DataSource.class) {

							Integer value = mutableSettingHandler.getInt(id);

							if (value != null) {

								if(value == -1){

									//Use system datasource
									ReflectionUtils.setFieldValue(field, systemInterface.getDataSource(), target);

								}else{

									try{
										DataSource dataSource = systemInterface.getDataSourceCache().getDataSource(value);

										ReflectionUtils.setFieldValue(field, dataSource, target);

									}catch(DataSourceException e){

										log.debug("Unable to set modulesetting field " + field.getName() + " in class " + clazz, e);
									}
								}
							}

						} else if (field.getType().isEnum()) {

							String value = mutableSettingHandler.getString(id);

							Enum<?>[] enumValues = EnumUtils.getValuesFromField(field);

							for (Enum<?> enumValue : enumValues) {

								if (enumValue.toString().equals(value)) {

									ReflectionUtils.setFieldValue(field, enumValue, target);
									break;
								}
							}

						} else if (field.getType() == List.class && Enum.class.isAssignableFrom((Class<?>) ReflectionUtils.getGenericType(field))) {

							List<String> values = mutableSettingHandler.getStrings(id);

							if (values != null) {

								List<Enum<?>> valueList = new ArrayList<Enum<?>>(values.size());

								Enum<?>[] enumValues = (Enum<?>[]) ((Class<?>) ReflectionUtils.getGenericType(field)).getEnumConstants();

								for (Enum<?> enumValue : enumValues) {

									if (values.contains(enumValue.toString())) {
										valueList.add(enumValue);
									}
								}

								ReflectionUtils.setFieldValue(field, valueList, target);

							} else if(moduleSetting.allowsNull()) {

								ReflectionUtils.setFieldValue(field, null, target);

							}

						} else {
							log.warn("Field " + field.getName() + " in class " + clazz + " is of an unallowed type " + field.getType() + " or unallowed generic type " + field.getGenericType() + ", skipping");
						}
					} catch (IllegalArgumentException e) {

						log.error("Unable to set modulesetting field " + field.getName() + " in class " + clazz, e);

					} catch (IllegalAccessException e) {

						log.error("Unable to set modulesetting field " + field.getName() + " in class " + clazz, e);
					}
				}
			}
			clazz = clazz.getSuperclass();
		}
	}

	public static void setXSLVariables(XSLVariableReader variableReader, Object target, Class<?> maxDepth, ModuleDescriptor moduleDescriptor) {

		Class<?> clazz = target.getClass();

		while (clazz != maxDepth) {

			Field[] fields = clazz.getDeclaredFields();

			for (Field field : fields) {

				XSLVariable xslVariable = field.getAnnotation(XSLVariable.class);

				if (xslVariable != null) {

					if (!Modifier.isFinal(field.getModifiers()) && field.getType() == String.class) {

						String fullVariableName;

						if (!StringUtils.isEmpty(xslVariable.name())) {

							fullVariableName = xslVariable.prefix() + xslVariable.name();

						} else {

							fullVariableName = xslVariable.prefix() + field.getName();
						}

						String value = variableReader.getValue(fullVariableName);

						if (value != null && !StringUtils.isEmpty(value)) {
							try {
								log.debug("Setting XSL variable value for field " + field.getName() + " in class " + clazz);

								ReflectionUtils.setFieldValue(field, value, target);

							} catch (IllegalArgumentException e) {

								log.error("Unable to set XSL variable field " + field.getName() + " in class " + clazz, e);

							} catch (IllegalAccessException e) {

								log.error("Unable to set XSL variable field " + field.getName() + " in class " + clazz, e);
							}
						} else {
							log.warn("No value found for XSL variable " + fullVariableName + " found in XSL stylesheet for field " + field.getName() + " in module " + moduleDescriptor);
						}
					} else {
						log.warn("Field " + field.getName() + " in class " + clazz + " is either final or not a String, skipping");
					}
				}
			}
			clazz = clazz.getSuperclass();
		}
	}

	public static XSLVariableReader getXSLVariableReader(VisibleModuleDescriptor moduleDescriptor, SystemInterface systemInterface) {

		try {

			return XSLVariableReaderFactory.getVariableReader(moduleDescriptor, systemInterface);

		} catch (SAXException e) {

			log.error("Unable to create XSLVariableReader for module " + moduleDescriptor + ", XSL variables, script tags and link tags will not be set!", e);

		} catch (IOException e) {

			log.error("Unable to create XSLVariableReader for module " + moduleDescriptor + ", XSL variables, script tags and link tags will not be set!", e);

		} catch (ParserConfigurationException e) {

			log.error("Unable to create XSLVariableReader for module " + moduleDescriptor + ", XSL variables, script tags and link tags will not be set!", e);

		} catch (ClassNotFoundException e) {

			log.error("Unable to create XSLVariableReader for module " + moduleDescriptor + ", XSL variables, script tags and link tags will not be set!", e);

		} catch (URISyntaxException e) {

			log.error("Unable to create XSLVariableReader for module " + moduleDescriptor + ", XSL variables, script tags and link tags will not be set!", e);

		} catch (XPathExpressionException e) {

			log.error("Unable to create XSLVariableReader for module " + moduleDescriptor + ", XSL variables, script tags and link tags will not be set!", e);
		}

		return null;
	}

	public static List<SettingDescriptor> getAnnotatedSettingDescriptors(Module<?> moduleInstance, Class<?> maxDepth, SystemInterface systemInterface) throws IllegalArgumentException, IllegalAccessException, InstantiationException, SQLException {

		List<SettingDescriptor> settingDescriptors = new ArrayList<SettingDescriptor>();

		if (maxDepth == null) {

			maxDepth = Object.class;
		}

		Class<?> clazz = moduleInstance.getClass();

		while (clazz != maxDepth) {

			Field[] fields = clazz.getDeclaredFields();

			for (Field field : fields) {

				if (field.isAnnotationPresent(CheckboxSettingDescriptor.class)) {

					ReflectionUtils.fixFieldAccess(field);

					CheckboxSettingDescriptor annotation = field.getAnnotation(CheckboxSettingDescriptor.class);

					Boolean defaultValue = null;

					if (field.getType() == Boolean.class || field.getType() == boolean.class) {

						defaultValue = field.getBoolean(moduleInstance);

						if (defaultValue == null) {

							defaultValue = false;
						}

					} else {

						throw new RuntimeException("Error parsing @CheckboxSettingDescriptor annotated field " + field.getName() + " in class " + clazz + ". Only boolean and Boolan fields can be annotated with @CheckboxSettingDescriptor annotation!");
					}

					String id = annotation.id().equals("") ? field.getName() : annotation.id();

					settingDescriptors.add(SettingDescriptor.createCheckboxSetting(id, annotation.name(), annotation.description(), defaultValue));

				} else if (field.isAnnotationPresent(DropDownSettingDescriptor.class)) {

					ReflectionUtils.fixFieldAccess(field);

					DropDownSettingDescriptor annotation = field.getAnnotation(DropDownSettingDescriptor.class);

					List<ValueDescriptor> valueDescriptors = parseMultiValueSettingDescriptorAnnotation(annotation, annotation.values(), annotation.valueDescriptions(), field, clazz);

					String id = annotation.id().equals("") ? field.getName() : annotation.id();

					settingDescriptors.add(SettingDescriptor.createDropDownSetting(id, annotation.name(), annotation.description(), annotation.required(), getSettingDescriptorDefaultValue(field, moduleInstance), valueDescriptors));

				} else if (field.isAnnotationPresent(EnumDropDownSettingDescriptor.class)) {

					ReflectionUtils.fixFieldAccess(field);

					EnumDropDownSettingDescriptor annotation = field.getAnnotation(EnumDropDownSettingDescriptor.class);

					Enum<?>[] enumValues = EnumUtils.getValuesFromField(field);

					List<ValueDescriptor> valueDescriptors = new ArrayList<ValueDescriptor>(enumValues.length);

					for (Enum<?> enumValue : enumValues) {

						valueDescriptors.add(new ValueDescriptor(enumValue.toString(), enumValue.toString()));
					}

					String id = annotation.id().equals("") ? field.getName() : annotation.id();

					settingDescriptors.add(SettingDescriptor.createDropDownSetting(id, annotation.name(), annotation.description(), annotation.required(), getSettingDescriptorDefaultValue(field, moduleInstance), valueDescriptors));

				} else if (field.isAnnotationPresent(MultiListSettingDescriptor.class)) {

					ReflectionUtils.fixFieldAccess(field);

					MultiListSettingDescriptor annotation = field.getAnnotation(MultiListSettingDescriptor.class);

					List<ValueDescriptor> valueDescriptors = parseMultiValueSettingDescriptorAnnotation(annotation, annotation.values(), annotation.valueDescriptions(), field, clazz);

					String id = annotation.id().equals("") ? field.getName() : annotation.id();

					settingDescriptors.add(SettingDescriptor.createMultiListSetting(id, annotation.name(), annotation.description(), annotation.required(), getSettingDescriptorDefaultValue(field, moduleInstance), valueDescriptors));

				} else if (field.isAnnotationPresent(EnumMultiListSettingDescriptor.class)) {

					ReflectionUtils.fixFieldAccess(field);

					EnumMultiListSettingDescriptor annotation = field.getAnnotation(EnumMultiListSettingDescriptor.class);

					java.lang.reflect.Type type = ReflectionUtils.getGenericType(field);

					Enum<?>[] enumValues = (Enum[]) ((Class<?>) type).getEnumConstants();

					List<ValueDescriptor> valueDescriptors = new ArrayList<ValueDescriptor>(enumValues.length);

					for (Enum<?> enumValue : enumValues) {

						valueDescriptors.add(new ValueDescriptor(enumValue.toString(), enumValue.toString()));
					}

					String id = annotation.id().equals("") ? field.getName() : annotation.id();

					settingDescriptors.add(SettingDescriptor.createMultiListSetting(id, annotation.name(), annotation.description(), annotation.required(), getSettingDescriptorDefaultValue(field, moduleInstance), valueDescriptors));


				} else if (field.isAnnotationPresent(UserMultiListSettingDescriptor.class)) {

					ReflectionUtils.fixFieldAccess(field);

					UserMultiListSettingDescriptor annotation = field.getAnnotation(UserMultiListSettingDescriptor.class);

					ArrayList<ValueDescriptor> valueDescriptors = new ArrayList<ValueDescriptor>();

					List<User> users = systemInterface.getUserHandler().getUsers(false, false);

					if (users != null) {

						for (User user : users) {
							valueDescriptors.add(new ValueDescriptor(user.getFirstname() + " " + user.getLastname(), user.getUserID().toString()));
						}
					}

					String id = annotation.id().equals("") ? field.getName() : annotation.id();

					settingDescriptors.add(SettingDescriptor.createMultiListSetting(id, annotation.name(), annotation.description(), annotation.required(), getSettingDescriptorDefaultValue(field, moduleInstance), valueDescriptors));

				} else if (field.isAnnotationPresent(GroupMultiListSettingDescriptor.class)) {

					ReflectionUtils.fixFieldAccess(field);

					GroupMultiListSettingDescriptor annotation = field.getAnnotation(GroupMultiListSettingDescriptor.class);

					ArrayList<ValueDescriptor> valueDescriptors = new ArrayList<ValueDescriptor>();

					List<Group> groups = systemInterface.getGroupHandler().getGroups(false);

					if (groups != null) {

						for (Group group : groups) {
							valueDescriptors.add(new ValueDescriptor(group.getName(), group.getGroupID().toString()));
						}
					}

					String id = annotation.id().equals("") ? field.getName() : annotation.id();

					settingDescriptors.add(SettingDescriptor.createMultiListSetting(id, annotation.name(), annotation.description(), annotation.required(), getSettingDescriptorDefaultValue(field, moduleInstance), valueDescriptors));

				} else if (field.isAnnotationPresent(DataSourceDropDownSettingDescriptor.class)) {

					ReflectionUtils.fixFieldAccess(field);

					DataSourceDropDownSettingDescriptor annotation = field.getAnnotation(DataSourceDropDownSettingDescriptor.class);

					ArrayList<ValueDescriptor> valueDescriptors = new ArrayList<ValueDescriptor>();

					//List only enabled datasources?
					List<SimpleDataSourceDescriptor> dataSourceDescriptors = systemInterface.getCoreDaoFactory().getDataSourceDAO().getAll();

					valueDescriptors.add(new ValueDescriptor("System default", "-1"));

					if (dataSourceDescriptors != null) {

						for (SimpleDataSourceDescriptor dataSourceDescriptor : dataSourceDescriptors) {
							valueDescriptors.add(new ValueDescriptor(dataSourceDescriptor.getName(), dataSourceDescriptor.getDataSourceID().toString()));
						}
					}

					String id = annotation.id().equals("") ? field.getName() : annotation.id();

					settingDescriptors.add(SettingDescriptor.createMultiListSetting(id, annotation.name(), annotation.description(), annotation.required(), getSettingDescriptorDefaultValue(field, moduleInstance), valueDescriptors));

				} else if (field.isAnnotationPresent(PasswordSettingDescriptor.class)) {

					ReflectionUtils.fixFieldAccess(field);

					PasswordSettingDescriptor annotation = field.getAnnotation(PasswordSettingDescriptor.class);

					String id = annotation.id().equals("") ? field.getName() : annotation.id();

					settingDescriptors.add(SettingDescriptor.createPasswordFieldSetting(id, annotation.name(), annotation.description(), annotation.required(), getSettingDescriptorDefaultValue(field, moduleInstance), getSettingDescriptorStringFormatValidator(annotation.formatValidator())));

				} else if (field.isAnnotationPresent(RadioButtonSettingDescriptor.class)) {

					ReflectionUtils.fixFieldAccess(field);

					RadioButtonSettingDescriptor annotation = field.getAnnotation(RadioButtonSettingDescriptor.class);

					List<ValueDescriptor> valueDescriptors = parseMultiValueSettingDescriptorAnnotation(annotation, annotation.values(), annotation.valueDescriptions(), field, clazz);

					String id = annotation.id().equals("") ? field.getName() : annotation.id();

					settingDescriptors.add(SettingDescriptor.createRadioButtonSetting(id, annotation.name(), annotation.description(), annotation.required(), getSettingDescriptorDefaultValue(field, moduleInstance), valueDescriptors));

				} else if (field.isAnnotationPresent(TextAreaSettingDescriptor.class)) {

					ReflectionUtils.fixFieldAccess(field);

					TextAreaSettingDescriptor annotation = field.getAnnotation(TextAreaSettingDescriptor.class);

					String id = annotation.id().equals("") ? field.getName() : annotation.id();

					boolean splitOnLineBreak;
					
					String defaultValue;
					
					if(field.getType().isAssignableFrom(List.class)){
						
						splitOnLineBreak = true;
						defaultValue = getSettingDescriptorDefaultValues(field, moduleInstance);
						
					}else{
						
						splitOnLineBreak = false;
						defaultValue = getSettingDescriptorDefaultValue(field, moduleInstance);
					}					
					
					settingDescriptors.add(SettingDescriptor.createTextAreaSetting(id, annotation.name(), annotation.description(), annotation.required(), defaultValue, getSettingDescriptorStringFormatValidator(annotation.formatValidator()), splitOnLineBreak));

				} else if (field.isAnnotationPresent(TextFieldSettingDescriptor.class)) {

					ReflectionUtils.fixFieldAccess(field);

					TextFieldSettingDescriptor annotation = field.getAnnotation(TextFieldSettingDescriptor.class);

					String id = annotation.id().equals("") ? field.getName() : annotation.id();

					settingDescriptors.add(SettingDescriptor.createTextFieldSetting(id, annotation.name(), annotation.description(), annotation.required(), getSettingDescriptorDefaultValue(field, moduleInstance), getSettingDescriptorStringFormatValidator(annotation.formatValidator())));

				} else if (field.isAnnotationPresent(HTMLEditorSettingDescriptor.class)) {

					ReflectionUtils.fixFieldAccess(field);

					HTMLEditorSettingDescriptor annotation = field.getAnnotation(HTMLEditorSettingDescriptor.class);

					String id = annotation.id().equals("") ? field.getName() : annotation.id();

					settingDescriptors.add(SettingDescriptor.createHTMLEditorSetting(id, annotation.name(), annotation.description(), annotation.required(), getSettingDescriptorDefaultValue(field, moduleInstance), getSettingDescriptorStringFormatValidator(annotation.formatValidator())));
				}
			}

			clazz = clazz.getSuperclass();
		}

		return settingDescriptors;
	}

	private static StringFormatValidator getSettingDescriptorStringFormatValidator(Class<? extends StringFormatValidator> formatValidator) throws InstantiationException, IllegalAccessException {

		StringFormatValidator stringFormatValidator = null;

		if (formatValidator != DummyStringFormatValidator.class) {

			stringFormatValidator = formatValidator.newInstance();
		}

		return stringFormatValidator;
	}

	private static String getSettingDescriptorDefaultValue(Field field, Module<?> moduleInstance) throws IllegalArgumentException, IllegalAccessException {

		if (field.getType() == String.class) {

			return (String) field.get(moduleInstance);

		} else {

			Object value = field.get(moduleInstance);

			if (value != null) {

				return value.toString();
			}

			return null;
		}
	}

	private static String getSettingDescriptorDefaultValues(Field field, Module<?> moduleInstance) throws IllegalArgumentException, IllegalAccessException {

		Collection<?> values = (Collection<?>) field.get(moduleInstance);
		
		if(values != null){
			
			StringBuilder stringBuilder = new StringBuilder();
			
			for(Object value : values){
				
				if(value != null){
			
					if(stringBuilder.length() > 0){
						
						stringBuilder.append("\n");
					}
					
					stringBuilder.append(value.toString());
				}
			}
			
			return stringBuilder.toString();
		}
		
		return null;
	}	
	
	private static List<ValueDescriptor> parseMultiValueSettingDescriptorAnnotation(Annotation annotation, String[] values, String[] descriptions, Field field, Class<?> clazz) {

		if (values.length == 0) {

			throw new RuntimeException("Error parsing @" + annotation.getClass().getSimpleName() + " annotated field " + field.getName() + " in class " + clazz + ". At least one value and value description needs to be specified!");

		} else if (descriptions.length == 0) {

			throw new RuntimeException("Error parsing @" + annotation.getClass().getSimpleName() + " annotated field " + field.getName() + " in class " + clazz + ". At least one value and value description needs to be specified!");

		} else if (values.length != descriptions.length) {

			throw new RuntimeException("Error parsing @" + annotation.getClass().getSimpleName() + " annotated field " + field.getName() + " in class " + clazz + ". The number of values doesn't match the number of value descriptions!");
		}

		List<ValueDescriptor> valueDescriptors = new ArrayList<ValueDescriptor>(values.length);

		for (int i = 0; i < values.length; i++) {

			valueDescriptors.add(new ValueDescriptor(descriptions[i], values[i]));
		}

		return valueDescriptors;
	}

	public Element getModuleSettingsAsXML(Module<?> moduleInstance, Document doc, MutableSettingHandler mutableSettingHandler, Class<?> maxDepth) {

		if (maxDepth == null) {

			maxDepth = Object.class;
		}

		Element moduleSettings = doc.createElement("AnnotatedModuleSettings");
		Element moduleSetting;
		Element settingName;
		Element settingValue;

		Class<?> clazz = moduleInstance.getClass();

		while (clazz != AnnotatedForegroundModule.class) {

			Field[] fields = clazz.getDeclaredFields();

			for (Field field : fields) {

				if (field.getAnnotation(ModuleSetting.class) != null) {

					ReflectionUtils.fixFieldAccess(field);

					moduleSetting = doc.createElement("ModuleSetting");

					settingName = XMLUtils.createElement("Name", field.getName(), doc);

					try {

						if (field.getType() == List.class) {

							settingValue = doc.createElement("Values");
							List<?> values = mutableSettingHandler.getStrings(field.getName()) == null ? (List<?>) field.get(moduleInstance) : mutableSettingHandler.getStrings(field.getName());

							for (Object value : values) {
								settingValue.appendChild(XMLUtils.createElement("Value", value.toString(), doc));
							}

						} else {

							settingValue = XMLUtils.createElement("Value", mutableSettingHandler.getString(field.getName()) == null ? field.get(moduleInstance) : mutableSettingHandler.getString(field.getName()), doc);
						}
						moduleSetting.appendChild(settingName);
						moduleSetting.appendChild(settingValue);
						moduleSettings.appendChild(moduleSetting);

					} catch (IllegalAccessException e) {
						log.error("Unable to get XML for modulesetting field " + field.getName() + " in class " + clazz, e);
					} catch (IllegalArgumentException e) {
						log.error("Unable to get XML for modulesetting field " + field.getName() + " in class " + clazz, e);
					}
				}
			}

			clazz = clazz.getSuperclass();
		}

		return moduleSettings;
	}
}
