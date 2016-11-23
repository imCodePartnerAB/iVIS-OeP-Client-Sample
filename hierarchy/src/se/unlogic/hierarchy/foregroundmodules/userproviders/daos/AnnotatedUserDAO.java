package se.unlogic.hierarchy.foregroundmodules.userproviders.daos;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.UserField;
import se.unlogic.standardutils.collections.CollectionUtils;
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
import se.unlogic.standardutils.populators.BeanStringPopulator;
import se.unlogic.standardutils.populators.CharacterPopulator;
import se.unlogic.standardutils.populators.IntegerPopulator;
import se.unlogic.standardutils.populators.QueryParameterPopulator;
import se.unlogic.standardutils.string.StringUtils;


public class AnnotatedUserDAO<UserType extends User> extends AnnotatedDAO<UserType>{

	private final QueryParameterFactory<UserType, Integer> userIDParamFactory;
	private final QueryParameterFactory<UserType, String> usernameParamFactory;
	private final QueryParameterFactory<UserType, String> passwordParamFactory;
	private final QueryParameterFactory<UserType, String> emailParamFactory;
	protected final QueryParameterFactory<UserType, String> firstnameParamFactory;
	protected final QueryParameterFactory<UserType, String> lastnameParamFactory;

	private final Field groupsRelation;
	private final Field attributesRelation;

	private final String usergroupTableName;
	private final String userAttributesTableName;

	private final String searchSQL;
	private final String emailSearchSQL;

	private final RowLimiter singleRowLimiter;

	public AnnotatedUserDAO(DataSource dataSource, Class<UserType> beanClass, AnnotatedDAOFactory daoFactory, List<QueryParameterPopulator<?>> queryParameterPopulators, List<BeanStringPopulator<?>> typePopulators, String usergroupTableName, Field groupsRelation, String userAttributesTableName, Field attributesRelation) {

		super(dataSource,beanClass,daoFactory,queryParameterPopulators,typePopulators);

		userIDParamFactory = getParamFactory("userID", Integer.class);
		usernameParamFactory = getParamFactory("username", String.class);
		passwordParamFactory = getParamFactory("password", String.class);
		emailParamFactory = getParamFactory("email", String.class);
		firstnameParamFactory = getParamFactory("firstname", String.class);
		lastnameParamFactory = getParamFactory("lastname", String.class);

		this.groupsRelation = groupsRelation;
		this.attributesRelation = attributesRelation;

		HighLevelQuery<UserType> query = new HighLevelQuery<UserType>();

		query.disableAutoRelations(true);

		this.usergroupTableName = usergroupTableName;
		this.userAttributesTableName = userAttributesTableName;

		this.searchSQL = getSearchSQL();
		this.emailSearchSQL = getEmailSearchSQL();

		singleRowLimiter = getSingleRowLimiter();
	}

	protected RowLimiter getSingleRowLimiter() {

		return new MySQLRowLimiter(1);
	}

	protected String getSearchSQL() {

		return "SELECT * FROM " + this.getTableName() + " WHERE @ ORDER BY " + this.firstnameParamFactory.getColumnName() + ", " + this.lastnameParamFactory.getColumnName();
	}

	protected String getEmailSearchSQL() {

		return "SELECT * FROM " + this.getTableName() + " WHERE email LIKE ? ORDER BY " + this.firstnameParamFactory.getColumnName() + ", " + this.lastnameParamFactory.getColumnName();
	}

	public UserType getUser(int userID, boolean groups, boolean attributes) throws SQLException {

		HighLevelQuery<UserType> query = new HighLevelQuery<UserType>();

		query.addParameter(userIDParamFactory.getParameter(userID));

		query.setRowLimiter(singleRowLimiter);

		setQueryRelations(query, groups, attributes);

		return this.get(query);
	}

	public List<UserType> getUsers(boolean groups, boolean attributes) throws SQLException {

		HighLevelQuery<UserType> query = new HighLevelQuery<UserType>();

		setQueryRelations(query, groups, attributes);

		return this.getAll(query);
	}

	public UserType findByUsername(String username, boolean groups, boolean attributes) throws SQLException {

		HighLevelQuery<UserType> query = new HighLevelQuery<UserType>();

		query.addParameter(usernameParamFactory.getParameter(username));

		query.setRowLimiter(singleRowLimiter);

		setQueryRelations(query, groups, attributes);

		return this.get(query);
	}

	public UserType findByUsernamePassword(String username, String password, boolean groups, boolean attributes) throws SQLException {

		HighLevelQuery<UserType> query = new HighLevelQuery<UserType>();

		query.addParameter(usernameParamFactory.getParameter(username));
		query.addParameter(passwordParamFactory.getParameter(password));

		query.setRowLimiter(singleRowLimiter);

		setQueryRelations(query, groups, attributes);

		return this.get(query);
	}

	public UserType findByEmail(String email, boolean groups, boolean attributes) throws SQLException {

		HighLevelQuery<UserType> query = new HighLevelQuery<UserType>();

		query.addParameter(emailParamFactory.getParameter(email));

		query.setRowLimiter(singleRowLimiter);

		setQueryRelations(query, groups, attributes);

		return this.get(query);
	}

	public UserType findByUserID(int userID, boolean groups, boolean attributes) throws SQLException {

		HighLevelQuery<UserType> query = new HighLevelQuery<UserType>();

		query.addParameter(userIDParamFactory.getParameter(userID));

		query.setRowLimiter(singleRowLimiter);

		setQueryRelations(query, groups, attributes);

		return this.get(query);
	}

	public void update(UserType user, boolean updateGroups, boolean attributes) throws SQLException {

		RelationQuery query = new RelationQuery();

		setQueryRelations(query, updateGroups, attributes);

		this.update(user, query);
	}

	public List<UserType> getUsers(Collection<Integer> userIDList, boolean groups, boolean attributes) throws SQLException {

		HighLevelQuery<UserType> query = new HighLevelQuery<UserType>();

		query.addParameter(userIDParamFactory.getWhereInParameter(userIDList));

		setQueryRelations(query, groups, attributes);

		return this.getAll(query);
	}

	public UserType findByEmailPassword(String email, String password, boolean groups, boolean attributes) throws SQLException {

		HighLevelQuery<UserType> query = new HighLevelQuery<UserType>();

		query.addParameter(emailParamFactory.getParameter(email));
		query.addParameter(passwordParamFactory.getParameter(password));

		query.setRowLimiter(singleRowLimiter);

		setQueryRelations(query, groups, attributes);

		return this.get(query);
	}

	public Integer getDisabledUserCount() throws SQLException {

		return new ObjectQuery<Integer>(dataSource, "SELECT COUNT(userID) FROM " + this.getTableName() + " WHERE enabled = false", IntegerPopulator.getPopulator()).executeQuery();
	}

	public Integer getUserCount() throws SQLException {

		return new ObjectQuery<Integer>(dataSource, "SELECT COUNT(userID) FROM " + this.getTableName(), IntegerPopulator.getPopulator()).executeQuery();
	}

	public int getUserCount(Integer groupID) throws SQLException {

		ObjectQuery<Integer> query = new ObjectQuery<Integer>(dataSource, "SELECT COUNT(userID) FROM " + usergroupTableName +" WHERE groupID = ?", IntegerPopulator.getPopulator());

		query.setInt(1, groupID);

		return query.executeQuery();
	}

	public List<Character> getUserFirstLetterIndex(UserField filteringField) throws SQLException {

		return new ArrayListQuery<Character>(dataSource, "SELECT DISTINCT UPPER(LEFT(" + filteringField.toString().toLowerCase() + ", 1)) as letter FROM " + this.getTableName() + " ORDER BY letter ", CharacterPopulator.getPopulator()).executeQuery();
	}

	public List<UserType> getUsers(UserField sortingField, Order order, boolean groups, boolean attributes) throws SQLException {

		HighLevelQuery<UserType> query = new HighLevelQuery<UserType>();

		query.addOrderByCriteria(this.getOrderByCriteria(sortingField.toString().toLowerCase(), order));

		setQueryRelations(query, groups, attributes);

		return getAll(query);
	}

	public List<UserType> getUsers(UserField filteringField, Order order, char startsWith, boolean groups, boolean attributes) throws SQLException {

		HighLevelQuery<UserType> query = new HighLevelQuery<UserType>();

		if(filteringField == UserField.EMAIL){

			query.addParameter(emailParamFactory.getParameter(startsWith + "%", QueryOperators.LIKE));

		}else if(filteringField == UserField.FIRSTNAME){

			query.addParameter(firstnameParamFactory.getParameter(startsWith + "%", QueryOperators.LIKE));

		}else if(filteringField == UserField.LASTNAME){

			query.addParameter(lastnameParamFactory.getParameter(startsWith + "%", QueryOperators.LIKE));

		}else if(filteringField == UserField.USERNAME){

			query.addParameter(usernameParamFactory.getParameter(startsWith + "%", QueryOperators.LIKE));
		}

		query.addOrderByCriteria(this.getOrderByCriteria(filteringField.toString().toLowerCase(), order));

		setQueryRelations(query, groups, attributes);

		return getAll(query);
	}
	
	public List<UserType> searchUserEmails(String queryTerm, boolean groups, boolean attributes, Integer maxHits) throws SQLException {

		if (queryTerm.contains(" ")) {
			return null;
		}

		String searchSQL;

		if (maxHits != null) {

			searchSQL = this.emailSearchSQL + " LIMIT " + maxHits;

		} else {

			searchSQL = this.emailSearchSQL;
		}

		LowLevelQuery<UserType> query = new LowLevelQuery<UserType>(searchSQL);

		query.addParameter(queryTerm + "%");

		setQueryRelations(query, groups, attributes);

		return this.getAll(query);
	}

	@SuppressWarnings("unchecked")
	public List<UserType> searchUsers(String queryTerm, boolean groups, boolean attributes, Integer maxHits) throws SQLException{

		String searchSQL;

		if(maxHits != null){

			searchSQL = this.searchSQL + " LIMIT " + maxHits;

		}else{

			searchSQL = this.searchSQL;
		}

		LowLevelQuery<UserType> query = new LowLevelQuery<UserType>();

		setQueryRelations(query, groups, attributes);

		String terms[] = queryTerm.split("[ ]+");
		
		StringBuilder paramBuilder = new StringBuilder();
		
		for (int i = 0; i < terms.length; i++) {
			paramBuilder.append("(firstname LIKE ? OR lastname LIKE ?) ");

			query.addParameter("%" + terms[i] + "%");
			query.addParameter("%" + terms[i] + "%");

			if (terms.length - i > 1) {
				paramBuilder.append(" AND ");
			}
		}
		
		query.setSql(searchSQL.replaceFirst("@", paramBuilder.toString()));

		List<UserType> users = this.getAll(query);

		// Also search email addresses
		if (terms.length == 1) {

			List<UserType> emailUsers = searchUserEmails(queryTerm, groups, attributes, maxHits);

			if (emailUsers != null) {
				users = new ArrayList<UserType>(CollectionUtils.combineAsSet(users, emailUsers));
			}
		}

		return users;
	}

	public List<UserType> getUsersByGroup(Integer groupID, boolean groups, boolean attributes) throws SQLException {

		LowLevelQuery<UserType> query = new LowLevelQuery<UserType>();

		query.setSql("SELECT " + this.getTableName() + ".* FROM " + this.getTableName() + " INNER JOIN " + usergroupTableName + " ON (" + this.getTableName() + ".userID=" + usergroupTableName + ".userID) WHERE " + usergroupTableName + ".groupID = ? ORDER BY " + this.firstnameParamFactory.getColumnName() + ", " + this.lastnameParamFactory.getColumnName());

		query.addParameter(groupID);

		setQueryRelations(query, groups, attributes);

		return getAll(query);
	}

	public List<UserType> getUsersByGroups(Collection<Integer> groupIDs, boolean attributes) throws SQLException {

		LowLevelQuery<UserType> query = new LowLevelQuery<UserType>();

		query.setSql("SELECT " + this.getTableName() + ".* FROM " + this.getTableName() + " INNER JOIN " + usergroupTableName + " ON (" + this.getTableName() + ".userID=" + usergroupTableName + ".userID) WHERE " + usergroupTableName + ".groupID IN (?" + StringUtils.repeatString(",?", groupIDs.size() - 1) + ") ORDER BY " + this.firstnameParamFactory.getColumnName() + ", " + this.lastnameParamFactory.getColumnName());

		query.addParameters(groupIDs);

		setQueryRelations(query, false, attributes);

		return getAll(query);
	}

	public List<UserType> getUsersByPasswordHashLength(int length) throws SQLException{

		LowLevelQuery<UserType> query = new LowLevelQuery<UserType>();

		query.setSql("SELECT * FROM " + this.getTableName() + " WHERE LENGTH(" + this.passwordParamFactory.getColumnName() + ") < ?");

		query.addParameter(length);

		return getAll(query);
	}

	protected void setQueryRelations(RelationQuery query, boolean groups, boolean attributes) {

		if(groups && groupsRelation != null){

			query.addRelation(groupsRelation);
		}

		if(attributes && attributesRelation != null){

			query.addRelation(attributesRelation);
		}
	}

	public List<UserType> getUsersByAttribute(String attributeName, String attributeValue, boolean groups, boolean attributes) throws SQLException {

		if(userAttributesTableName == null){

			return null;
		}

		return getAll(getUserByAttributeQuery(attributeName, attributeValue, "=", groups, attributes, false));
	}

	public UserType getUserByAttribute(String attributeName, String attributeValue, boolean groups, boolean attributes) throws SQLException {

		if(userAttributesTableName == null){

			return null;
		}

		return get(getUserByAttributeQuery(attributeName, attributeValue, "=", groups, attributes, true));
	}


	public List<UserType> getUsersByAttributeWildcard(String attributeName, String attributeValue, boolean groups, boolean attributes) throws SQLException {

		if(userAttributesTableName == null){

			return null;
		}

		return getAll(getUserByAttributeQuery(attributeName, attributeValue + "%", "LIKE", groups, attributes, false));
	}

	public List<UserType> getUsersByAttribute(String attributeName, boolean groups, boolean attributes) throws SQLException {

		if(userAttributesTableName == null){

			return null;
		}

		return getAll(getUsersByAttributeQuery(attributeName, groups, attributes));
	}

	protected LowLevelQuery<UserType> getUserByAttributeQuery(String attributeName, String attributeValue, String valueOperator, boolean groups, boolean attributes, boolean limit) {

		LowLevelQuery<UserType> query = new LowLevelQuery<UserType>();

		query.setSql("SELECT " + this.getTableName() + ".* FROM " + this.getTableName() + " INNER JOIN " + userAttributesTableName + " ON (" + this.getTableName() + ".userID=" + userAttributesTableName + ".userID) WHERE " + userAttributesTableName + ".name = ? AND " + userAttributesTableName + ".value " + valueOperator + " ?");

		if(limit){

			query.setSql(query.getSql() + " " + singleRowLimiter.getLimitSQL());

		}else{

			query.setSql(query.getSql() + " ORDER BY " + this.firstnameParamFactory.getColumnName() + ", " + this.lastnameParamFactory.getColumnName());
		}

		query.addParameter(attributeName);
		query.addParameter(attributeValue);

		setQueryRelations(query, groups, attributes);

		return query;
	}

	protected LowLevelQuery<UserType> getUsersByAttributeQuery(String attributeName, boolean groups, boolean attributes) {

		LowLevelQuery<UserType> query = new LowLevelQuery<UserType>();

		query.setSql("SELECT " + this.getTableName() + ".* FROM " + this.getTableName() + " INNER JOIN " + userAttributesTableName + " ON (" + this.getTableName() + ".userID=" + userAttributesTableName + ".userID) WHERE " + userAttributesTableName + ".name = ? AND " + userAttributesTableName + ".value IS NOT NULL ORDER BY " + this.firstnameParamFactory.getColumnName() + ", " + this.lastnameParamFactory.getColumnName());

		query.addParameter(attributeName);

		setQueryRelations(query, groups, attributes);

		return query;
	}

	public List<Character> getAttributeFirstLetterIndex(String attributeName) throws SQLException {

		if(userAttributesTableName == null){

			return null;
		}

		ArrayListQuery<Character> query = new ArrayListQuery<Character>(dataSource, "SELECT DISTINCT UPPER(LEFT(value, 1)) as letter FROM " + userAttributesTableName + " WHERE name = ? ORDER BY letter ", CharacterPopulator.getPopulator());

		query.setString(1, attributeName);

		return query.executeQuery();
	}

	public List<UserType> getUsersWithoutAttribute(String attributeName, boolean groups, boolean attributes) throws SQLException {

		LowLevelQuery<UserType> query = new LowLevelQuery<UserType>();

		query.setSql("SELECT * FROM " + this.getTableName() + " WHERE userID NOT IN (SELECT userID FROM " + userAttributesTableName + " WHERE name = ?) ORDER BY " + this.firstnameParamFactory.getColumnName() + ", " + this.lastnameParamFactory.getColumnName());

		query.addParameter(attributeName);

		setQueryRelations(query, groups, attributes);

		return getAll(query);
	}
}
