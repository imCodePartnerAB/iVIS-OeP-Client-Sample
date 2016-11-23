/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AnnotatedDAOWrapper<BeanType, KeyType> implements CRUDDAO<BeanType, KeyType>{

	protected final HighLevelQuery<BeanType> DISABLED_AUTO_RELATIONS_QUERY = new HighLevelQuery<BeanType>();

	protected AnnotatedDAO<BeanType> annotatedDAO;
	protected QueryParameterFactory<BeanType, KeyType> parameterFactory;

	protected ArrayList<Field> relations = new ArrayList<Field>();
	protected RelationQuery relationQuery;

	protected boolean useRelationsOnAdd;
	protected boolean useRelationsOnUpdate;
	protected boolean useRelationsOnGet;
	protected boolean useRelationsOnGetAll;
	protected boolean disableAutoRelations;

	public AnnotatedDAOWrapper(AnnotatedDAO<BeanType> annotatedDAO, String keyField, Class<KeyType> keyClass) {

		super();
		this.annotatedDAO = annotatedDAO;
		this.parameterFactory = annotatedDAO.getParamFactory(keyField, keyClass);
	}

	public AnnotatedDAOWrapper(AnnotatedDAO<BeanType> annotatedDAO, Field keyField, Class<KeyType> keyClass) {

		super();
		this.annotatedDAO = annotatedDAO;
		this.parameterFactory = annotatedDAO.getParamFactory(keyField, keyClass);
	}

	public List<BeanType> getAll() throws SQLException{

		return getAll(null);
	}

	public List<BeanType> getAll(TransactionHandler transactionHandler) throws SQLException{

		HighLevelQuery<BeanType> query = getGetAllQuery();

		if(transactionHandler != null){

			return annotatedDAO.getAll(query,transactionHandler);

		}else{

			return annotatedDAO.getAll(query);
		}
	}



	public void add(BeanType bean) throws SQLException {

		add(bean, null);
	}

	public void add(BeanType bean, TransactionHandler transactionHandler) throws SQLException {

		RelationQuery relationQuery = this.getUpdateQuery();

		if(transactionHandler != null){

			this.annotatedDAO.add(bean,transactionHandler,relationQuery);

		}else{

			this.annotatedDAO.add(bean,relationQuery);
		}
	}

	public void delete(BeanType bean) throws SQLException {

		this.annotatedDAO.delete(bean);
	}

	public void delete(BeanType bean, TransactionHandler transactionHandler) throws SQLException {

		this.annotatedDAO.delete(bean, transactionHandler);
	}

	public Integer deleteByID(KeyType beanID) throws SQLException {

		return this.deleteByID(beanID, null);
	}

	public Integer deleteByID(KeyType beanID, TransactionHandler transactionHandler) throws SQLException {

		HighLevelQuery<BeanType> query = getGetQuery(beanID);

		if(transactionHandler != null){

			return annotatedDAO.delete(query,transactionHandler);

		}else{

			return annotatedDAO.delete(query);
		}
	}

	public BeanType get(KeyType beanID) throws SQLException {

		return this.get(beanID, null);
	}

	public BeanType get(KeyType beanID, TransactionHandler transactionHandler) throws SQLException {

		HighLevelQuery<BeanType> query = getGetQuery(beanID);

		if(transactionHandler != null){

			return annotatedDAO.get(query,transactionHandler);

		}else{

			return annotatedDAO.get(query);
		}
	}

	public void update(BeanType bean) throws SQLException {

		update(bean, null);
	}

	public void update(BeanType bean, TransactionHandler transactionHandler) throws SQLException {

		RelationQuery relationQuery = getUpdateQuery();

		if(transactionHandler != null){

			this.annotatedDAO.update(bean,transactionHandler,relationQuery);

		}else{

			this.annotatedDAO.update(bean,relationQuery);
		}
	}

	private HighLevelQuery<BeanType> getGetAllQuery() {

		if(useRelationsOnGetAll && !this.relations.isEmpty()){

			HighLevelQuery<BeanType> query = new HighLevelQuery<BeanType>();

			query.addRelations(relations);
			query.disableAutoRelations(disableAutoRelations);

			return query;

		}else if(disableAutoRelations){

			return DISABLED_AUTO_RELATIONS_QUERY;
		}

		return null;
	}

	private HighLevelQuery<BeanType> getGetQuery(KeyType value) {

		HighLevelQuery<BeanType> query = new HighLevelQuery<BeanType>();

		if(useRelationsOnGet && !this.relations.isEmpty()){

			query.addRelations(relations);

		}if(disableAutoRelations){

			query.disableAutoRelations(disableAutoRelations);
		}

		query.addParameter(parameterFactory.getParameter(value));

		return query;
	}

	private RelationQuery getUpdateQuery() {

		if(useRelationsOnAdd && this.relationQuery != null){

			return this.relationQuery;

		}else if(disableAutoRelations){

			return DISABLED_AUTO_RELATIONS_QUERY;

		}

		return null;
	}

	public void addRelation(Field field){

		this.relations.add(field);

		this.relationQuery = new RelationQuery(relations);
		this.relationQuery.disableAutoRelations(disableAutoRelations);
	}

	public void addRelations(Field... fields){

		this.relations.addAll(Arrays.asList(fields));

		this.relationQuery = new RelationQuery(relations);
		this.relationQuery.disableAutoRelations(disableAutoRelations);
	}

	public boolean isUseRelationsOnAdd() {

		return useRelationsOnAdd;
	}


	public void setUseRelationsOnAdd(boolean useRelationsOnAdd) {

		this.useRelationsOnAdd = useRelationsOnAdd;
	}


	public boolean isUseRelationsOnUpdate() {

		return useRelationsOnUpdate;
	}


	public void setUseRelationsOnUpdate(boolean useRelationsOnUpdate) {

		this.useRelationsOnUpdate = useRelationsOnUpdate;
	}


	public boolean isUseRelationsOnGet() {

		return useRelationsOnGet;
	}


	public void setUseRelationsOnGet(boolean useRelationsOnGet) {

		this.useRelationsOnGet = useRelationsOnGet;
	}


	public AnnotatedDAO<BeanType> getAnnotatedDAO() {

		return annotatedDAO;
	}


	public boolean isUseRelationsOnGetAll() {

		return useRelationsOnGetAll;
	}


	public void setUseRelationsOnGetAll(boolean useRelationsOnGetAll) {

		this.useRelationsOnGetAll = useRelationsOnGetAll;
	}


	public QueryParameterFactory<BeanType, KeyType> getParameterFactory() {

		return parameterFactory;
	}

	public TransactionHandler createTransaction() throws SQLException {

		return annotatedDAO.createTransaction();
	}

	public boolean isDisableAutoRelations() {

		return disableAutoRelations;
	}


	public void disableAutoRelations(boolean disableAutoRelations) {

		this.disableAutoRelations = disableAutoRelations;

		if(this.relationQuery != null){

			this.relationQuery.disableAutoRelations(disableAutoRelations);
		}
	}
}
