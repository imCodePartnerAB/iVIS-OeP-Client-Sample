package se.unlogic.standardutils.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;

import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Key;


public class RelationUtils {

	public static ArrayList<Field> getKeyFields(Class<?> clazz){

		ArrayList<Field> keyFields = new ArrayList<Field>();

		Field[] fields = clazz.getDeclaredFields();

		for(Field field : fields){

			if(field.isAnnotationPresent(Key.class) && field.isAnnotationPresent(DAOManaged.class)){

				keyFields.add(field);
			}
		}

		return keyFields;
	}
}
