/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents a SQL query and is used together with a AnnotatedDAO typed with a matching type.
 * <p>
 * 
 * A high level abstracts away all and also verifies that the query parameters and order by criterias are of the correct type to avoid SQL exceptions.
 * <p>
 * 
 * @param <T> The type of bean this query is supposed to return
 * 
 * @author Robert "Unlogic" Olofsson (unlogic@unlogic.se)
 */
public class HighLevelQuery<T> extends RelationQuery {

	private List<QueryParameter<T, ?>> parameters;
	private List<OrderByCriteria<T>> orderByCriterias;
	private RowLimiter rowLimiter;

	public HighLevelQuery() {

	}

	public HighLevelQuery(List<QueryParameter<T, ?>> parameters, List<OrderByCriteria<T>> orderByCriterias) {

		super();
		this.parameters = parameters;
		this.orderByCriterias = orderByCriterias;
	}

	public HighLevelQuery(Field... relations) {

		this.addRelations(relations);
	}

	public HighLevelQuery(QueryParameter<T, ?> parameter, Field... relations) {

		this.addParameter(parameter);
		this.addRelations(relations);
	}

	public HighLevelQuery(QueryParameter<T, ?>[] parameters) {

		this.addParameter(parameters);
	}

	public HighLevelQuery(RelationQuery relationQuery, Class<T> clazz) {

		super(relationQuery);
		
		if(this.getRelationParameterHandler() != null){
			
			List<QueryParameter<T, ?>> relationParameters = getRelationParameterHandler().getRelationParameters(clazz);
			
			if(relationParameters != null){
				
				this.parameters = new ArrayList<QueryParameter<T,?>>(relationParameters);
			}
		}
		
		if(this.getRelationOrderByHandler() != null){
			
			List<OrderByCriteria<T>> relationOrderByCriterias = getRelationOrderByHandler().getRelationOrderByCriterias(clazz);
			
			if(relationOrderByCriterias != null){
				
				this.orderByCriterias = new ArrayList<OrderByCriteria<T>>(relationOrderByCriterias);
			}
		}
		
		if(this.getRelationRowLimiterHandler() != null){
			
			this.rowLimiter = getRelationRowLimiterHandler().getRowLimitier(clazz);
		}
	}

	public void setParameters(List<QueryParameter<T, ?>> parameters) {

		this.parameters = parameters;
	}

	public synchronized void addParameter(QueryParameter<T, ?> parameter) {

		if (this.parameters == null) {

			this.parameters = new ArrayList<QueryParameter<T, ?>>();
		}

		this.parameters.add(parameter);
	}

	public synchronized void addParameter(QueryParameter<T, ?>... parameters) {

		if (this.parameters == null) {

			this.parameters = new ArrayList<QueryParameter<T, ?>>();
		}

		this.parameters.addAll(Arrays.asList(parameters));
	}

	public List<OrderByCriteria<T>> getOrderByCriterias() {

		return orderByCriterias;
	}

	public void setOrderByCriterias(List<OrderByCriteria<T>> orderByCriterias) {

		this.orderByCriterias = orderByCriterias;
	}

	public List<QueryParameter<T, ?>> getParameters() {

		return parameters;
	}

	public synchronized void addOrderByCriteria(OrderByCriteria<T> criteria) {

		if (this.orderByCriterias == null) {

			this.orderByCriterias = new ArrayList<OrderByCriteria<T>>();
		}

		this.orderByCriterias.add(criteria);
	}

	public synchronized void addOrderByCriteria(OrderByCriteria<T>... criterias) {

		if (this.orderByCriterias == null) {

			this.orderByCriterias = new ArrayList<OrderByCriteria<T>>();
		}

		this.orderByCriterias.addAll(Arrays.asList(criterias));
	}

	@SuppressWarnings("unchecked")
	@Override
	public HighLevelQuery<T> clone() {

		try {
			HighLevelQuery<T> clone = (HighLevelQuery<T>) super.clone();

			if (this.orderByCriterias != null) {

				clone.orderByCriterias = new ArrayList<OrderByCriteria<T>>(orderByCriterias);
			}

			if (this.parameters != null) {

				clone.parameters = new ArrayList<QueryParameter<T, ?>>(parameters);
			}

			return clone;

		} catch (CloneNotSupportedException e) {

			throw new RuntimeException(e);
		}
	}

	public RowLimiter getRowLimiter() {

		return rowLimiter;
	}

	public void setRowLimiter(RowLimiter rowLimiter) {

		this.rowLimiter = rowLimiter;
	}
}
