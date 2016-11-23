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
import java.util.List;


public class AdvancedAnnotatedDAOWrapper<BeanType, KeyType> implements CRUDDAO<BeanType, KeyType>{

	protected AnnotatedDAO<BeanType> annotatedDAO;
	protected QueryParameterFactory<BeanType, KeyType> parameterFactory;

	protected RelationQuery addQuery = new RelationQuery();
	protected RelationQuery updateQuery = new RelationQuery();
	protected HighLevelQuery<BeanType> getQuery = new HighLevelQuery<BeanType>();
	protected HighLevelQuery<BeanType> getAllQuery = new HighLevelQuery<BeanType>();


	public AdvancedAnnotatedDAOWrapper(AnnotatedDAO<BeanType> annotatedDAO, String keyField, Class<KeyType> keyClass) {

		super();
		this.annotatedDAO = annotatedDAO;
		this.parameterFactory = annotatedDAO.getParamFactory(keyField, keyClass);

	}

	public AdvancedAnnotatedDAOWrapper(AnnotatedDAO<BeanType> annotatedDAO, Field keyField, Class<KeyType> keyClass) {

		super();
		this.annotatedDAO = annotatedDAO;
		this.parameterFactory = annotatedDAO.getParamFactory(keyField, keyClass);
	}

	public List<BeanType> getAll() throws SQLException{

		return getAll(null);
	}

	public List<BeanType> getAll(TransactionHandler transactionHandler) throws SQLException{

		if(transactionHandler != null){

			return annotatedDAO.getAll(getAllQuery,transactionHandler);

		}else{

			return annotatedDAO.getAll(getAllQuery);
		}
	}

	public void add(BeanType bean) throws SQLException {

		add(bean, null);
	}

	public void add(BeanType bean, TransactionHandler transactionHandler) throws SQLException {

		if(transactionHandler != null){

			this.annotatedDAO.add(bean,transactionHandler,addQuery);

		}else{

			this.annotatedDAO.add(bean,addQuery);
		}
	}

	public void delete(BeanType bean) throws SQLException {

		this.annotatedDAO.delete(bean);
	}

	public void delete(BeanType bean, TransactionHandler transactionHandler) throws SQLException {

		this.annotatedDAO.delete(bean, transactionHandler);
	}

	public BeanType get(KeyType beanID) throws SQLException {

		return this.get(beanID, null);
	}

	public BeanType get(KeyType beanID, TransactionHandler transactionHandler) throws SQLException {

		HighLevelQuery<BeanType> query = getQuery.clone();

		query.addParameter(parameterFactory.getParameter(beanID));

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

		if(transactionHandler != null){

			this.annotatedDAO.update(bean,transactionHandler,updateQuery);

		}else{

			this.annotatedDAO.update(bean,updateQuery);
		}
	}

	public AnnotatedDAO<BeanType> getAnnotatedDAO() {

		return annotatedDAO;
	}


	public QueryParameterFactory<BeanType, KeyType> getParameterFactory() {

		return parameterFactory;
	}


	public RelationQuery getAddQuery() {

		return addQuery;
	}


	public RelationQuery getUpdateQuery() {

		return updateQuery;
	}


	public HighLevelQuery<BeanType> getGetQuery() {

		return getQuery;
	}


	public HighLevelQuery<BeanType> getGetAllQuery() {

		return getAllQuery;
	}

	public TransactionHandler createTransaction() throws SQLException {

		return annotatedDAO.createTransaction();
	}
}
