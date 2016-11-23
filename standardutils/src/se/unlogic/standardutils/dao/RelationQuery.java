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

import se.unlogic.standardutils.collections.CollectionUtils;

public class RelationQuery implements Cloneable {

	private List<Field> relations;
	private List<Field> excludedRelations;
	private List<Field> excludedFields;

	private boolean disableAutoRelations;

	private RelationParameterHandler relationParameterHandler;
	private RelationOrderByHandler relationOrderByHandler;
	private RelationRowLimiterHandler relationRowLimiterHandler;

	public RelationQuery() {

	}

	public RelationQuery(List<Field> relations) {

		this.relations = relations;
	}

	public RelationQuery(Field... relations) {

		this.addRelations(relations);
	}

	public RelationQuery(RelationQuery relationQuery) {

		if (relationQuery == null) {

			return;
		}

		this.addRelations(relationQuery);
		this.addExcludedRelations(relationQuery);
		this.addExcludedFields(relationQuery);
		this.disableAutoRelations(relationQuery.isDisableAutoRelations());
		this.relationParameterHandler = relationQuery.getRelationParameterHandler();
		this.relationOrderByHandler = relationQuery.getRelationOrderByHandler();
		this.relationRowLimiterHandler = relationQuery.getRelationRowLimiterHandler();
	}

	public List<Field> getRelations() {

		return relations;
	}

	public void setRelations(List<Field> relations) {

		this.relations = relations;
	}

	public void addRelation(Field relation) {

		if (this.relations == null) {

			this.relations = new ArrayList<Field>();
		}

		this.relations.add(relation);
	}

	public void addRelations(Field... relations) {

		if (this.relations == null) {

			this.relations = new ArrayList<Field>();
		}

		this.relations.addAll(Arrays.asList(relations));
	}

	public static boolean hasRelations(RelationQuery query) {

		if (query == null || query.getRelations() == null || query.getRelations().isEmpty()) {
			return false;
		}

		return true;
	}

	public boolean hasRelations() {

		return hasRelations(this);
	}

	public void addRelations(RelationQuery relationQuery) {

		if (hasRelations(relationQuery)) {

			this.addRelations(relationQuery.getRelations());
		}
	}

	public void addRelations(List<Field> relations) {

		if (this.relations == null) {

			this.relations = relations;

		} else {

			this.relations.addAll(relations);
		}
	}

	public List<Field> getExcludedRelations() {

		return excludedRelations;
	}

	public void setExcludedRelations(List<Field> excludedRelations) {

		this.excludedRelations = excludedRelations;
	}

	public void addExcludedRelation(Field relation) {

		if (this.excludedRelations == null) {

			this.excludedRelations = new ArrayList<Field>();
		}

		this.excludedRelations.add(relation);
	}

	public void addExcludedRelations(Field... excludedRelations) {

		if (this.excludedRelations == null) {

			this.excludedRelations = new ArrayList<Field>();
		}

		this.excludedRelations.addAll(Arrays.asList(excludedRelations));
	}

	public static boolean hasExcludedRelations(RelationQuery query) {

		if (query == null || query.getExcludedRelations() == null || query.getExcludedRelations().isEmpty()) {
			return false;
		}

		return true;
	}

	public boolean hasExcludedRelations() {

		return hasExcludedRelations(this);
	}

	public void addExcludedRelations(RelationQuery relationQuery) {

		if (hasExcludedRelations(relationQuery)) {

			this.addExcludedRelations(relationQuery.getExcludedRelations());
		}
	}

	public void addExcludedRelations(List<Field> excludedRelations) {

		if (this.excludedRelations == null) {

			this.excludedRelations = excludedRelations;

		} else {

			this.excludedRelations.addAll(excludedRelations);
		}
	}

	public boolean containsRelation(Field field) {

		if (this.relations != null) {

			return this.relations.contains(field);
		}

		return false;
	}

	public boolean containsExcludedRelation(Field field) {

		if (this.excludedRelations != null) {

			return this.excludedRelations.contains(field);
		}

		return false;
	}

	public boolean isDisableAutoRelations() {

		return disableAutoRelations;
	}

	public void disableAutoRelations(boolean disableAutoRelations) {

		this.disableAutoRelations = disableAutoRelations;
	}

	@Override
	public RelationQuery clone() throws CloneNotSupportedException {

		RelationQuery clone = (RelationQuery) super.clone();

		if (this.excludedFields != null) {

			clone.excludedFields = new ArrayList<Field>(excludedFields);
		}

		if (this.excludedRelations != null) {

			clone.excludedRelations = new ArrayList<Field>(excludedRelations);
		}

		if (this.relations != null) {

			clone.relations = new ArrayList<Field>(relations);
		}

		return clone;
	}

	public List<Field> getExcludedFields() {

		return excludedFields;
	}

	/**
	 * Sets the fields which will be excluded from select and update statements.
	 * 
	 * @param excludedFields
	 */
	public void setExcludedFields(List<Field> excludedFields) {

		this.excludedFields = excludedFields;
	}

	/**
	 * Adds a field which will be excluded from select and update statements.
	 * 
	 * @param excludedFields
	 */
	public void addExcludedField(Field field) {

		excludedFields = CollectionUtils.addAndInstantiateIfNeeded(excludedFields, field);
	}

	/**
	 * Adds field(s) which will be excluded from select and update statements.
	 * 
	 * @param excludedFields
	 */
	public void addExcludedFields(Field... fields) {

		excludedFields = CollectionUtils.addAndInstantiateIfNeeded(excludedFields, Arrays.asList(fields));
	}

	/**
	 * Adds fields which will be excluded from select and update statements.
	 * 
	 * @param excludedFields
	 */
	public void addExcludedFields(List<Field> fields) {

		excludedFields = CollectionUtils.addAndInstantiateIfNeeded(excludedFields, fields);
	}

	public boolean hasExcludedFields() {

		return hasExcludedFields(this);
	}

	public static boolean hasExcludedFields(RelationQuery query) {

		if (query == null || query.getExcludedFields() == null || query.getExcludedFields().isEmpty()) {
			return false;
		}

		return true;
	}

	public boolean containsExcludedField(Field field) {

		if (this.excludedFields != null) {

			return excludedFields.contains(field);
		}

		return false;
	}

	public void addExcludedFields(RelationQuery relationQuery) {

		if (hasExcludedFields(relationQuery)) {

			this.addExcludedFields(relationQuery.getExcludedFields());
		}
	}

	public RelationParameterHandler getRelationParameterHandler() {

		return relationParameterHandler;
	}

	public void setRelationParameterHandler(RelationParameterHandler relationParameterHandler) {

		this.relationParameterHandler = relationParameterHandler;
	}

	public <X> void addRelationParameter(Class<X> clazz, QueryParameter<X, ?> queryParameter) {

		checkRelationParameterHandler();

		this.relationParameterHandler.addRelationParameter(clazz, queryParameter);
	}

	public <X> List<QueryParameter<X, ?>> getRelationParameters(Class<X> clazz) {

		if (relationParameterHandler == null) {

			return null;
		}

		return relationParameterHandler.getRelationParameters(clazz);
	}

	public <X> void addRelationOrderByCriteria(Class<X> clazz, OrderByCriteria<X> orderByCriteria) {

		checkRelationOrderByHandler();

		this.relationOrderByHandler.addRelationOrderByCriteria(clazz, orderByCriteria);
	}

	public <X> List<OrderByCriteria<X>> getRelationOrderByCriterias(Class<X> clazz) {

		if (relationOrderByHandler == null) {

			return null;
		}

		return relationOrderByHandler.getRelationOrderByCriterias(clazz);
	}

	public void addRelationRowLimiter(Class<?> clazz, RowLimiter rowLimiter) {

		checkRelationRowLimiterHandler();

		this.relationRowLimiterHandler.addRelationParameter(clazz, rowLimiter);
	}

	public RowLimiter getRelationRowLimiter(Class<?> clazz) {

		if (this.relationRowLimiterHandler == null) {

			return null;
		}

		return relationRowLimiterHandler.getRowLimitier(clazz);
	}

	private void checkRelationParameterHandler() {

		if (relationParameterHandler == null) {

			this.relationParameterHandler = new RelationParameterHandler();
		}
	}

	private void checkRelationOrderByHandler() {

		if (this.relationOrderByHandler == null) {

			this.relationOrderByHandler = new RelationOrderByHandler();
		}
	}

	private void checkRelationRowLimiterHandler() {

		if (this.relationRowLimiterHandler == null) {

			this.relationRowLimiterHandler = new RelationRowLimiterHandler();
		}
	}

	public RelationOrderByHandler getRelationOrderByHandler() {

		return relationOrderByHandler;
	}

	public void setRelationOrderByHandler(RelationOrderByHandler relationOrderByHandler) {

		this.relationOrderByHandler = relationOrderByHandler;
	}

	public RelationRowLimiterHandler getRelationRowLimiterHandler() {

		return relationRowLimiterHandler;
	}

	public void setRelationRowLimiterHandler(RelationRowLimiterHandler relationRowLimiterHandler) {

		this.relationRowLimiterHandler = relationRowLimiterHandler;
	}
}
