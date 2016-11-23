/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.populators;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;

import se.unlogic.standardutils.dao.BeanResultSetPopulator;
import se.unlogic.standardutils.dao.querys.PreparedStatementQuery;
import se.unlogic.standardutils.enums.EnumUtils;
import se.unlogic.standardutils.reflection.ReflectionUtils;

public class EnumPopulator<EnumType extends Enum<EnumType>> extends BaseStringPopulator<EnumType> implements BeanResultSetPopulator<EnumType>, BeanStringPopulator<EnumType>, QueryParameterPopulator<EnumType> {

	protected Class<EnumType> classType;

	public EnumPopulator(Class<EnumType> classType) {
		super();

		checkClass(classType);
	}

	private void checkClass(Class<EnumType> classType) {

		if (classType == null) {
			throw new NullPointerException("Classtype can not be null!");
		} else {
			this.classType = classType;
		}
	}

	public EnumType populate(ResultSet rs) throws SQLException {

		return EnumUtils.toEnum(classType, rs.getString(1));
	}

	public EnumType getValue(String value) {

		return EnumUtils.toEnum(classType, value);
	}

	@Override
	public boolean validateDefaultFormat(String value) {

		return EnumUtils.isEnum(classType, value);
	}

	public Class<? extends EnumType> getType() {

		return classType;
	}

	public void populate(PreparedStatementQuery query, int paramIndex, Object bean) throws SQLException {

		if(bean != null){

			query.setString(paramIndex, bean.toString());

		}else{

			query.setString(paramIndex, null);
		}

	}

	public static <Type extends Enum<Type>> EnumPopulator<Type> getGenericInstance(Class<Type> type) {

		return new EnumPopulator<Type>(type);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static EnumPopulator<?> getInstanceFromField(Field field){

		Enum enumInstance = EnumUtils.getInstanceFromField(field);

		return EnumPopulator.getGenericInstance(enumInstance.getClass());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static EnumPopulator<?> getInstanceFromListField(Field field){

		Object[] enumValues = ((Class<?>)ReflectionUtils.getGenericType(field)).getEnumConstants();

		Enum enumInstance = (Enum) enumValues[0];

		return EnumPopulator.getGenericInstance(enumInstance.getClass());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static EnumPopulator<?> getInstanceFromMethod(Method method){

		Object[] enumValues = method.getParameterTypes()[0].getEnumConstants();

		Enum enumInstance = (Enum) enumValues[0];

		return EnumPopulator.getGenericInstance(enumInstance.getClass());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static EnumPopulator getInstanceFromClass(Class clazz){

		return EnumPopulator.getGenericInstance(clazz);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static EnumPopulator<?> getInstanceFromListMethod(Method method) {

		Object[] enumValues = ((Class<?>)ReflectionUtils.getGenericType(method)).getEnumConstants();

		Enum enumInstance = (Enum) enumValues[0];

		return EnumPopulator.getGenericInstance(enumInstance.getClass());
	}
}
