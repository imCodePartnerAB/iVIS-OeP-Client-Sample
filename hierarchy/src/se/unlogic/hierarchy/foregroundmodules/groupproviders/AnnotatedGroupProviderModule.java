package se.unlogic.hierarchy.foregroundmodules.groupproviders;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.GroupProvider;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.hierarchy.foregroundmodules.groupproviders.dao.AnnotatedGroupDAO;
import se.unlogic.standardutils.dao.SimpleAnnotatedDAOFactory;
import se.unlogic.standardutils.enums.Order;
import se.unlogic.standardutils.validation.NonNegativeStringIntegerValidator;
import se.unlogic.webutils.http.URIParser;


public abstract class AnnotatedGroupProviderModule<GroupType extends Group> extends AnnotatedForegroundModule implements GroupProvider{

	@TextFieldSettingDescriptor(name="Priority",description="The priority of this group provider compared to other providers. A higher value means a higher priority. Valid values are 0 - " + Integer.MAX_VALUE + ".",required=true,formatValidator=NonNegativeStringIntegerValidator.class)
	protected int priority = 0;

	protected AnnotatedGroupDAO<GroupType> groupDAO;

	protected Class<GroupType> groupClass;

	public AnnotatedGroupProviderModule( Class<GroupType> groupClass) {

		super();
		this.groupClass = groupClass;
	}

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		this.sectionInterface.getSystemInterface().getGroupHandler().addProvider(this);
	}

	@Override
	public void unload() {

		this.sectionInterface.getSystemInterface().getGroupHandler().removeProvider(this);
	}

	@Override
	public ForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return null;
	}

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		SimpleAnnotatedDAOFactory daoFactory = new SimpleAnnotatedDAOFactory(dataSource);

		groupDAO = new AnnotatedGroupDAO<GroupType>(dataSource, groupClass, daoFactory, getAttributesRelation(), getGroupAttributesTableName());
	}

	protected abstract String getGroupAttributesTableName();

	protected abstract Field getAttributesRelation();

	@Override
	public Group getGroup(Integer groupID, boolean attributes) throws SQLException {

		return setGroupProviderID(groupDAO.getGroup(groupID, attributes));
	}

	@Override
	public List<? extends Group> searchGroups(String query, boolean attributes, Integer maxHits) throws SQLException {

		return setGroupProviderID(groupDAO.searchGroups(query, attributes, maxHits));
	}

	public List<? extends Group> searchGroupsWithAttribute(String query, boolean attributes, String attributeName, Integer maxHits) throws SQLException{

		return setGroupProviderID(groupDAO.searchGroups(query, attributes, attributeName, maxHits));
	}

	@Override
	public List<? extends Group> getGroups(boolean attributes) throws SQLException {

		return setGroupProviderID(groupDAO.getGroups(attributes));
	}

	@Override
	public List<? extends Group> getGroupsByAttribute(String attributeName, boolean attributes) throws SQLException {

		return setGroupProviderID(groupDAO.getGroupsByAttribute(attributeName, attributes));
	}

	@Override
	public List<? extends Group> getGroupsByAttribute(String attributeName, String attributeValue, boolean attributes) throws SQLException {

		return setGroupProviderID(groupDAO.getGroupsByAttribute(attributeName, attributeValue, attributes));
	}

	@Override
	public Group getGroupByAttribute(String attributeName, String attributeValue, boolean attributes) throws SQLException {

		return setGroupProviderID(groupDAO.getGroupByAttribute(attributeName, attributeValue, attributes));
	}

	@Override
	public Group getGroupByAttributes(List<Entry<String, String>> attributeEntries, boolean attributes) throws SQLException {

		return setGroupProviderID(groupDAO.getGroupByAttributes(attributeEntries, attributes));
	}

	@Override
	public int getGroupCount() throws SQLException {

		return groupDAO.getGroupCount();
	}

	@Override
	public int getDisabledGroupCount() throws SQLException {

		return groupDAO.getDisabledGroupCount();
	}

	@Override
	public List<GroupType> getGroups(Order order, char startsWith, boolean attributes) throws SQLException {

		return setGroupProviderID(groupDAO.getGroups(order, startsWith, attributes));
	}

	@Override
	public List<Character> getGroupFirstLetterIndex() throws SQLException {

		return groupDAO.getGroupFirstLetterIndex();
	}

	@Override
	public int getPriority() {

		return priority;
	}

	@Override
	public List<? extends Group> getGroups(Collection<Integer> groupIDs, boolean attributes) throws SQLException {

		return setGroupProviderID(groupDAO.getGroups(groupIDs, attributes));
	}

	protected GroupType setGroupProviderID(GroupType group){

		return group;
	}

	protected List<GroupType> setGroupProviderID(List<GroupType> groups){

		return groups;
	}

	@Override
	public DataSource getDataSource(){

		return this.dataSource;
	}
}
