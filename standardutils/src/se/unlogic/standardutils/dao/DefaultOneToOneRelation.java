/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.OneToOne;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.string.StringUtils;


public class DefaultOneToOneRelation<LocalType,RemoteType,LocalKeyType> implements OneToOneRelation<LocalType, RemoteType> {

	private final Field field;
	private Field remoteField;
	private final Field localKeyField;
	private final Field remoteKeyField;

	private final AnnotatedDAOFactory daoFactory;
	private AnnotatedDAO<RemoteType> annotatedDAO;
	private QueryParameterFactory<RemoteType, LocalKeyType> queryParameterFactory;
	private final Class<RemoteType> remoteClass;
	private final Class<LocalKeyType> localKeyClass;
	private boolean initialized;
	private boolean preAdd;

	public DefaultOneToOneRelation(Class<LocalType> beanClass, Class<RemoteType> remoteClass, Class<LocalKeyType> localKeyClass, Field field, Field localKeyField, AnnotatedDAOFactory daoFactory, DAOManaged daoManaged) {
		super();
		this.remoteClass = remoteClass;
		this.field = field;
		this.daoFactory = daoFactory;
		this.localKeyField = localKeyField;
		this.localKeyClass = localKeyClass;

		ReflectionUtils.fixFieldAccess(this.localKeyField);

		//find remote field
		List<Field> fields = ReflectionUtils.getFields(remoteClass);

		for(Field remoteField : fields){

			if(remoteField.getType().equals(beanClass) && remoteField.isAnnotationPresent(DAOManaged.class) && remoteField.isAnnotationPresent(OneToOne.class)){

				this.remoteField = remoteField;

				ReflectionUtils.fixFieldAccess(this.remoteField);

				break;
			}
		}

		if(this.remoteField == null){

			throw new RuntimeException("Unable to to find corresponding @OneToOne field in class " + remoteClass + " for @OneToOne annotated field " + field.getName() + " in " + beanClass);
		}

		OneToOne localAnnotation = field.getAnnotation(OneToOne.class);
		OneToOne remoteAnnotation = remoteField.getAnnotation(OneToOne.class);

		//find remote key field
		remoteKeyField = getKeyField(remoteField.getAnnotation(OneToOne.class), remoteClass, remoteField);

		if(!localKeyField.getType().equals(remoteKeyField.getType())){

			throw new RuntimeException("Incompatible types between key field " + localKeyField.getName() + " in class " + beanClass + " and key field " + remoteKeyField.getName() + " in class " + remoteClass +". @OneToOne annotation requires key fields to be of same type.");
		}

		if(localAnnotation.preAdd() && remoteAnnotation.preAdd()){

			throw new RuntimeException("Invalid preAdd values for @OneToOne annotation on field " + field.getName() + " in class " + beanClass + " and field " + remoteField.getName() + " in class " + remoteClass +". @OneToOne relations can only have preAdd set to true on one side of the relation.");
		}

		ReflectionUtils.fixFieldAccess(remoteKeyField);

		this.preAdd = localAnnotation.preAdd();


	}

	/* (non-Javadoc)
	 * @see se.unlogic.utils.dao.OneToOneRelation#setValue(LocalType, java.sql.Connection, java.lang.reflect.Field[])
	 */
	@SuppressWarnings("unchecked")
	public void getRemoteValue(LocalType bean, Connection connection, RelationQuery relationQuery) throws SQLException{

		if(!initialized){
			init();
		}

		try {
			HighLevelQuery<RemoteType> query = new HighLevelQuery<RemoteType>(relationQuery,remoteClass);

			query.addParameter(queryParameterFactory.getParameter((LocalKeyType) localKeyField.get(bean)));

			field.set(bean, annotatedDAO.get(query, connection));

		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see se.unlogic.utils.dao.OneToOneRelation#add(LocalType, java.sql.Connection, java.lang.reflect.Field[])
	 */
	@SuppressWarnings("unchecked")
	public void add(LocalType bean, Connection connection, RelationQuery relationQuery) throws SQLException{

		if(!initialized){
			init();
		}

		try {
			RemoteType remoteBean = (RemoteType) field.get(bean);

			if(remoteBean != null){

				if(!preAdd){

					setRemoteKeyFieldValue(remoteBean, bean);
				}

				annotatedDAO.add(remoteBean, connection, relationQuery);

				if(preAdd){

					setLocalKeyFieldValue(bean, remoteBean);
				}
			}

		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}
	}

	private void setRemoteKeyFieldValue(RemoteType remoteBean, LocalType bean) throws IllegalArgumentException, IllegalAccessException {

		remoteKeyField.set(remoteBean, localKeyField.get(bean));
	}

	private void setLocalKeyFieldValue(LocalType bean, RemoteType remoteBean) throws IllegalArgumentException, IllegalAccessException {

		localKeyField.set(bean, remoteKeyField.get(remoteBean));
	}

	/* (non-Javadoc)
	 * @see se.unlogic.utils.dao.OneToOneRelation#update(LocalType, java.sql.Connection, java.lang.reflect.Field[])
	 */
	@SuppressWarnings("unchecked")
	public void update(LocalType bean, Connection connection, RelationQuery relationQuery) throws SQLException{

		if(!initialized){
			this.init();
		}

		try {
			RemoteType remoteBean = (RemoteType) field.get(bean);

			if(remoteBean != null){

				if(!preAdd){

					setRemoteKeyFieldValue(remoteBean, bean);
				}

				this.annotatedDAO.addOrUpdate(remoteBean, connection, relationQuery);

				if(preAdd){

					setLocalKeyFieldValue(bean, remoteBean);
				}

			}else{

				//Delete remote bean
				HighLevelQuery<RemoteType> deleteQuery = new HighLevelQuery<RemoteType>(relationQuery,remoteClass);

				deleteQuery.addParameter(queryParameterFactory.getParameter((LocalKeyType) localKeyField.get(bean)));

				annotatedDAO.delete(deleteQuery, connection);
			}

		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}
	}

	private void init() {

		if(annotatedDAO == null){
			annotatedDAO = this.daoFactory.getDAO(remoteClass);
			queryParameterFactory = annotatedDAO.getParamFactory(remoteKeyField, localKeyClass);
		}

		this.initialized = true;
	}

	public static Field getKeyField(OneToOne oneToOneAnnotation, Class<?> clazz, Field annotatedField){

		if(!StringUtils.isEmpty(oneToOneAnnotation.keyField())){

			try {
				Field keyField = clazz.getDeclaredField(oneToOneAnnotation.keyField());

				DAOManaged keyDAOPopulate = keyField.getAnnotation(DAOManaged.class);

				if(keyDAOPopulate == null){

					throw new RuntimeException("Specified keyField " + oneToOneAnnotation.keyField() + " for @OneToOne annotation for field " + annotatedField.getName() + "  in " + clazz + " is missing the @DAOManaged annotation");
				}

				return keyField;


			} catch (SecurityException e) {

				throw new RuntimeException("Unable to find specified keyField " + oneToOneAnnotation.keyField() + " for @OneToOne annotation for field " + annotatedField.getName() + "  in " + clazz);

			} catch (NoSuchFieldException e) {

				throw new RuntimeException("Unable to find specified keyField " + oneToOneAnnotation.keyField() + " for @OneToOne annotation for field " + annotatedField.getName() + "  in " + clazz);
			}

		}else{

			ArrayList<Field> keyFields = RelationUtils.getKeyFields(clazz);

			if(keyFields.size() == 0){

				throw new RuntimeException("Unable to find any @Key annotated fields in " + clazz);

			}else if(keyFields.size() > 1){

				throw new RuntimeException("keyField needs to be specified for @OneToOne annotated field " + annotatedField.getName() + " in " + clazz + " since the class contains multiple @Key annotated fields");
			}

			return keyFields.get(0);
		}
	}

	public static <LT,RT,LKT> OneToOneRelation<LT, RT> getGenericInstance(Class<LT> beanClass, Class<RT> remoteClass, Class<LKT> localKeyClass, Field field, Field localKeyField, AnnotatedDAOFactory daoFactory, DAOManaged daoManaged){

		return new DefaultOneToOneRelation<LT,RT,LKT>(beanClass,remoteClass,localKeyClass,field,localKeyField,daoFactory,daoManaged);
	}

	public boolean preAdd() {

		return preAdd;
	}
}
