package se.unlogic.hierarchy.foregroundmodules.groupproviders.dao;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.standardutils.dao.AnnotatedDAO;
import se.unlogic.standardutils.dao.AnnotatedDAOFactory;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.LowLevelQuery;
import se.unlogic.standardutils.dao.MySQLRowLimiter;
import se.unlogic.standardutils.dao.QueryOperators;
import se.unlogic.standardutils.dao.QueryParameterFactory;
import se.unlogic.standardutils.dao.RelationQuery;
import se.unlogic.standardutils.dao.RowLimiter;
import se.unlogic.standardutils.dao.querys.ArrayListQuery;
import se.unlogic.standardutils.dao.querys.ObjectQuery;
import se.unlogic.standardutils.enums.Order;
import se.unlogic.standardutils.populators.CharacterPopulator;
import se.unlogic.standardutils.populators.IntegerPopulator;

public class AnnotatedGroupDAO<GroupType extends Group> extends AnnotatedDAO<GroupType> {

	private final QueryParameterFactory<GroupType, Integer> groupIDParamFactory;
	private final QueryParameterFactory<GroupType, String> nameParamFactory;

	private final Field attributesRelation;

	private final String groupAttributesTableName;

	public AnnotatedGroupDAO(DataSource dataSource, Class<GroupType> beanClass, AnnotatedDAOFactory daoFactory, Field attributesRelation, String groupAttributesTableName) {

		super(dataSource, beanClass, daoFactory);

		groupIDParamFactory = this.getParamFactory("groupID", Integer.class);
		nameParamFactory = this.getParamFactory("name", String.class);
		this.attributesRelation = attributesRelation;
		this.groupAttributesTableName = groupAttributesTableName;
	}

	public GroupType getGroup(int groupID, boolean attributes) throws SQLException {

		HighLevelQuery<GroupType> query = new HighLevelQuery<GroupType>();

		query.addParameter(groupIDParamFactory.getParameter(groupID));

		setQueryRelations(query, attributes);

		return this.get(query);
	}

	public List<GroupType> getGroups(boolean attributes) throws SQLException {

		if (attributes) {

			HighLevelQuery<GroupType> query = new HighLevelQuery<GroupType>();

			setQueryRelations(query, attributes);

			return this.getAll(query);
		}

		return this.getAll();
	}

	public List<GroupType> searchGroups(String queryTerm, boolean attributes, Integer maxHits) throws SQLException {

		HighLevelQuery<GroupType> query = new HighLevelQuery<GroupType>();

		query.addParameter(nameParamFactory.getParameter("%" + queryTerm + "%", QueryOperators.LIKE));

		setQueryRelations(query, attributes);

		if(maxHits != null){

			query.setRowLimiter(getRowLimiter(maxHits));
		}

		return this.getAll(query);
	}

	protected RowLimiter getRowLimiter(Integer maxHits) {

		return new MySQLRowLimiter(maxHits);
	}

	public List<GroupType> searchGroups(String queryTerm, boolean attributes, String attributeName, Integer maxHits) throws SQLException {

		if(groupAttributesTableName == null){

			return null;
		}

		LowLevelQuery<GroupType> query = new LowLevelQuery<GroupType>();

		query.setSql("SELECT " + this.getTableName() + ".* FROM " + this.getTableName() + " INNER JOIN " + groupAttributesTableName + " ON (" + this.getTableName() + ".groupID=" + groupAttributesTableName + ".groupID) WHERE " + this.getTableName() + ".name LIKE ? AND " + groupAttributesTableName + ".name = ? ORDER BY name");

		if(maxHits != null){

			query.setSql(query.getSql() + " LIMIT " + maxHits);
		}

		query.addParameter("%" + queryTerm + "%");
		query.addParameter(attributeName);

		setQueryRelations(query, attributes);

		return getAll(query);
	}

	public List<GroupType> getGroups(Collection<Integer> groupIDList, boolean attributes) throws SQLException {

		HighLevelQuery<GroupType> query = new HighLevelQuery<GroupType>();

		query.addParameter(groupIDParamFactory.getWhereInParameter(groupIDList));

		setQueryRelations(query, attributes);

		return this.getAll(query);
	}

	public List<GroupType> getGroupsByAttribute(String attributeName, String attributeValue, boolean attributes) throws SQLException {

		if (groupAttributesTableName == null) {

			return null;
		}

		return getAll(getGroupByAttributeQuery(attributeName, attributeValue, "=", attributes));
	}

	public GroupType getGroupByAttribute(String attributeName, String attributeValue, boolean attributes) throws SQLException {

		if(groupAttributesTableName == null){

			return null;
		}

		return get(getGroupByAttributeQuery(attributeName, attributeValue, "=", attributes));
	}

	public GroupType getGroupByAttributes(List<Entry<String, String>> attributeEntries, boolean attributes) throws SQLException {

		if(groupAttributesTableName == null){

			return null;
		}

		return get(getGroupByAttributeQuery(attributeEntries, "=", attributes));
	}

	public Integer getGroupCount() throws SQLException {

		return new ObjectQuery<Integer>(dataSource, "SELECT COUNT(groupID) FROM " + this.getTableName(), IntegerPopulator.getPopulator()).executeQuery();
	}

	public Integer getDisabledGroupCount() throws SQLException {

		return new ObjectQuery<Integer>(dataSource, "SELECT COUNT(groupID) FROM " + this.getTableName() + " WHERE enabled = false", IntegerPopulator.getPopulator()).executeQuery();
	}

	public List<GroupType> getGroups(Order order, char startsWith, boolean attributes) throws SQLException {

		HighLevelQuery<GroupType> query = new HighLevelQuery<GroupType>();

		query.addParameter(nameParamFactory.getParameter(startsWith + "%", QueryOperators.LIKE));

		query.addOrderByCriteria(this.getOrderByCriteria("name", order));

		setQueryRelations(query, attributes);

		return this.getAll(query);
	}

	public List<Character> getGroupFirstLetterIndex() throws SQLException {

		return new ArrayListQuery<Character>(dataSource, "SELECT DISTINCT UPPER(LEFT(name, 1)) as letter FROM " + this.getTableName() + " ORDER BY letter ", CharacterPopulator.getPopulator()).executeQuery();
	}

	private void setQueryRelations(RelationQuery query, boolean attributes) {

		if (attributes && attributesRelation != null) {

			query.addRelation(attributesRelation);
		}
	}

	@Override
	public void add(GroupType group) throws SQLException {

		RelationQuery query = new RelationQuery();

		this.setQueryRelations(query, true);

		this.add(group, query);
	}

	public void update(GroupType group, boolean updateAttributes) throws SQLException {

		RelationQuery query = new RelationQuery();

		this.setQueryRelations(query, updateAttributes);

		this.update(group, query);
	}

	protected LowLevelQuery<GroupType> getGroupByAttributeQuery(String attributeName, String attributeValue, String valueOperator, boolean attributes) {

		LowLevelQuery<GroupType> query = new LowLevelQuery<GroupType>();

		query.setSql("SELECT " + this.getTableName() + ".* FROM " + this.getTableName() + " INNER JOIN " + groupAttributesTableName + " ON (" + this.getTableName() + ".groupID=" + groupAttributesTableName + ".groupID) WHERE " + groupAttributesTableName + ".name = ? AND " + groupAttributesTableName + ".value " + valueOperator + " ?");

		query.addParameter(attributeName);
		query.addParameter(attributeValue);

		setQueryRelations(query, attributes);

		return query;
	}

	protected LowLevelQuery<GroupType> getGroupByAttributeQuery(List<Entry<String, String>> attributeEntries, String valueOperator, boolean attributes) {

		LowLevelQuery<GroupType> query = new LowLevelQuery<GroupType>();

		StringBuilder sql = new StringBuilder("SELECT " + this.getTableName() + ".* FROM " + this.getTableName() + " ");

		for(int i = 0; i < attributeEntries.size(); i++) {

			sql.append("INNER JOIN " + groupAttributesTableName + " a" + i + " ON (" + this.getTableName() + ".groupID = a" + i + ".groupID) ");

		}

		sql.append("WHERE ");

		int index = 0;

		for(Entry<String, String> attributeEntry : attributeEntries) {

			sql.append("(a" + index + ".name = '" + attributeEntry.getKey() + "' AND a" + index + ".value " + valueOperator + " '" + attributeEntry.getValue() + "')");

			if(index < attributeEntries.size()-1) {
				sql.append(" AND ");
			}

			index++;
		}

		query.setSql(sql.toString());

		setQueryRelations(query, attributes);

		return query;
	}

	public List<GroupType> getGroupsByAttribute(String attributeName, boolean attributes) throws SQLException {

		if(groupAttributesTableName == null){

			return null;
		}

		LowLevelQuery<GroupType> query = new LowLevelQuery<GroupType>();

		query.setSql("SELECT " + this.getTableName() + ".* FROM " + this.getTableName() + " INNER JOIN " + groupAttributesTableName + " ON (" + this.getTableName() + ".groupID=" + groupAttributesTableName + ".groupID) WHERE " + groupAttributesTableName + ".name = ? ORDER BY name");

		query.addParameter(attributeName);

		setQueryRelations(query, attributes);

		return getAll(query);
	}
}
