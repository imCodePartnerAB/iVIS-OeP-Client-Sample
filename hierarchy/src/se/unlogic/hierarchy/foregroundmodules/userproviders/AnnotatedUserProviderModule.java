package se.unlogic.hierarchy.foregroundmodules.userproviders;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import se.unlogic.hierarchy.core.annotations.DropDownSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.UserField;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.interfaces.UserProvider;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.hierarchy.foregroundmodules.userproviders.daos.AnnotatedUserDAO;
import se.unlogic.standardutils.dao.SimpleAnnotatedDAOFactory;
import se.unlogic.standardutils.enums.Order;
import se.unlogic.standardutils.hash.HashAlgorithms;
import se.unlogic.standardutils.hash.HashUtils;
import se.unlogic.standardutils.populators.BeanStringPopulator;
import se.unlogic.standardutils.populators.QueryParameterPopulator;
import se.unlogic.standardutils.validation.NonNegativeStringIntegerValidator;
import se.unlogic.webutils.http.URIParser;


public abstract class AnnotatedUserProviderModule<UserType extends User> extends AnnotatedForegroundModule implements UserProvider{

	@ModuleSetting
	@DropDownSettingDescriptor(name="Password algorithm",description="The algorithm used for password hashing",required=true,values={"MD2", "MD5", "MySQL", "SHA-1", "SHA-256", "SHA-384", "SHA-512"},valueDescriptions={"MD2", "MD5", "MySQL", "SHA-1", "SHA-256", "SHA-384", "SHA-512"})
	protected String passwordAlgorithm = HashAlgorithms.SHA1;

	@ModuleSetting
	@TextFieldSettingDescriptor(name="Priority",description="The priority of this user provider compared to other providers. A lower value means a higher priority. Valid values are 0 - " + Integer.MAX_VALUE + ".",required=true,formatValidator=NonNegativeStringIntegerValidator.class)
	protected int priority = 0;

	protected AnnotatedUserDAO<UserType> userDAO;

	protected Class<UserType> userClass;

	public AnnotatedUserProviderModule(Class<UserType> userClass) {

		super();
		this.userClass = userClass;
	}

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		this.sectionInterface.getSystemInterface().getUserHandler().addProvider(this);
	}

	@Override
	public void unload() {

		this.sectionInterface.getSystemInterface().getUserHandler().removeProvider(this);
	}

	@Override
	public ForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return null;
	}

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		SimpleAnnotatedDAOFactory daoFactory = new SimpleAnnotatedDAOFactory(dataSource);

		userDAO = new AnnotatedUserDAO<UserType>(dataSource, userClass, daoFactory, getQueryParameterPopulators(), getBeanStringPopulators(), getUsergroupTablename(), getGroupsRelation(), getUserAttributesTableName(), getAttributesRelation());
		daoFactory.addDAO(userClass, userDAO);
	}

	protected abstract String getUsergroupTablename();

	protected abstract String getUserAttributesTableName();

	protected abstract List<BeanStringPopulator<?>> getBeanStringPopulators();

	protected abstract List<QueryParameterPopulator<?>> getQueryParameterPopulators();

	protected abstract Field getGroupsRelation();

	protected abstract Field getAttributesRelation();

	@Override
	public UserType getUser(Integer userID, boolean groups, boolean attributes) throws SQLException {

		return setupUser(userDAO.getUser(userID,groups, attributes), attributes);
	}

	@Override
	public List<UserType> getUsers(boolean groups, boolean attributes) throws SQLException {

		return setupUsers(userDAO.getUsers(groups, attributes), attributes);
	}

	@Override
	public UserType getUserByUsername(String username, boolean groups, boolean attributes) throws SQLException {

		return setupUser(userDAO.findByUsername(username, groups, attributes), attributes);
	}

	@Override
	public UserType getUserByUsernamePassword(String username, String password, boolean groups, boolean attributes) throws SQLException {

		return setupUser(userDAO.findByUsernamePassword(username, this.getHashedPassword(password), groups, attributes), attributes);
	}

	@Override
	public UserType getUserByEmail(String email, boolean groups, boolean attributes) throws SQLException {

		return setupUser(userDAO.findByEmail(email, groups, attributes), attributes);
	}

	@Override
	public UserType getUserByEmailPassword(String email, String password, boolean groups, boolean attributes) throws SQLException {

		return setupUser(userDAO.findByEmailPassword(email, this.getHashedPassword(password), groups, attributes), attributes);
	}

	@Override
	public int getUserCount() throws SQLException {

		return userDAO.getUserCount();
	}

	@Override
	public int getUserCountByGroup(Integer groupID) throws SQLException {

		return userDAO.getUserCount(groupID);
	}

	@Override
	public int getDisabledUserCount() throws SQLException {

		return userDAO.getDisabledUserCount();
	}

	@Override
	public List<Character> getUserFirstLetterIndex(UserField filteringField) throws SQLException {

		return userDAO.getUserFirstLetterIndex(filteringField);
	}

	@Override
	public List<? extends User> getUsers(UserField sortingField, Order order, boolean groups, boolean attributes) throws SQLException {

		return setupUsers(userDAO.getUsers(sortingField, order, groups, attributes), attributes);
	}

	@Override
	public List<? extends User> getUsers(UserField filteringField, char startsWith, Order order, boolean groups, boolean attributes) throws SQLException {

		return setupUsers(userDAO.getUsers(filteringField, order, startsWith, groups, attributes), attributes);
	}

	@Override
	public int getPriority() {

		return priority;
	}

	public String getHashedPassword(String password){

		if(passwordAlgorithm.equals("MySQL")){

			return HashUtils.mysqlPasswordHash(password);

		}else{

			return HashUtils.hash(password, passwordAlgorithm, "UTF-8");
		}
	}

	@Override
	public List<? extends User> getUsers(Collection<Integer> userIDs, boolean groups, boolean attributes) throws SQLException {

		return setupUsers(userDAO.getUsers(userIDs, groups, attributes), attributes);
	}

	protected UserType setupUser(UserType user, boolean setupAttributes){

		return user;
	}

	protected List<UserType> setupUsers(List<UserType> users, boolean setupAttributes){

		return users;
	}

	@Override
	public DataSource getDataSource(){

		return this.dataSource;
	}

	@Override
	public List<? extends UserType> getUsersByGroup(Integer groupID, boolean groups, boolean attributes) throws SQLException {

		return setupUsers(this.userDAO.getUsersByGroup(groupID, groups, attributes), attributes);
	}

	@Override
	public List<? extends UserType> getUsersByGroups(Collection<Integer> groupIDs, boolean attributes) throws SQLException {

		return setupUsers(this.userDAO.getUsersByGroups(groupIDs, attributes), attributes);
	}

	@Override
	public List<? extends UserType> getUserByAttribute(String attributeName, boolean groups, boolean attributes) throws SQLException {

		return setupUsers(this.userDAO.getUsersByAttribute(attributeName, groups, attributes), attributes);
	}

	@Override
	public List<? extends UserType> getUsersByAttribute(String attributeName, String attributeValue, boolean groups, boolean attributes) throws SQLException {

		return setupUsers(this.userDAO.getUsersByAttribute(attributeName, attributeValue, groups, attributes), attributes);
	}

	@Override
	public UserType getUserByAttribute(String attributeName, String attributeValue, boolean groups, boolean attributes) throws SQLException {

		return setupUser(this.userDAO.getUserByAttribute(attributeName, attributeValue, groups, attributes), attributes);
	}

	@Override
	public List<? extends User> searchUsers(String query, boolean groups, boolean attributes, Integer maxHits) throws SQLException {

		return userDAO.searchUsers(query, groups, attributes, maxHits);
	}

	@Override
	public List<? extends User> getUsersWithoutAttribute(String attributeName, boolean groups, boolean attributes) throws SQLException {

		return setupUsers(this.userDAO.getUsersWithoutAttribute(attributeName, groups, attributes), attributes);
	}
}
