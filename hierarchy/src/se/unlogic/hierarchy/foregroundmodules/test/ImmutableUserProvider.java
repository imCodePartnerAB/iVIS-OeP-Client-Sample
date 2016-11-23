package se.unlogic.hierarchy.foregroundmodules.test;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.UserField;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.interfaces.UserProvider;
import se.unlogic.hierarchy.foregroundmodules.SimpleForegroundModule;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.enums.Order;
import se.unlogic.webutils.http.URIParser;


public class ImmutableUserProvider extends SimpleForegroundModule implements UserProvider {

	private static final ImmutableUser IMMUTABLE_USER = new ImmutableUser();
	private static final ImmutableGroup IMMUTABLE_GROUP = new ImmutableGroup();

	@Override
	public ForegroundModuleResponse processRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return null;
	}

	@Override
	public User getUser(Integer userID, boolean groups, boolean attributes) throws SQLException {

		log.info("Got request for userID " + userID);

		if(IMMUTABLE_USER.getUserID().equals(userID)){

			log.info("Returning immutable user " + IMMUTABLE_USER);
			return IMMUTABLE_USER;
		}

		log.info("Requested userID " + userID + " does not match userID of immutable user " + IMMUTABLE_USER.getUserID());

		return null;
	}

	@Override
	public List<? extends User> getUsers(boolean groups, boolean attributes) throws SQLException {

		return Collections.singletonList(IMMUTABLE_USER);
	}

	@Override
	public List<? extends User> getUsers(Collection<Integer> userIDs, boolean groups, boolean attributes) throws SQLException {

		if(userIDs.contains(IMMUTABLE_USER.getUserID())){

			return Collections.singletonList(IMMUTABLE_USER);
		}

		return null;
	}

	@Override
	public User getUserByUsername(String username, boolean groups, boolean attributes) throws SQLException {

		if(username.equals(IMMUTABLE_USER.getUsername())){

			return IMMUTABLE_USER;
		}

		return null;
	}

	@Override
	public User getUserByUsernamePassword(String username, String password, boolean groups, boolean attributes) throws SQLException {


		return null;
	}

	@Override
	public User getUserByEmail(String email, boolean groups, boolean attributes) throws SQLException {


		return null;
	}

	@Override
	public User getUserByEmailPassword(String email, String password, boolean groups, boolean attributes) throws SQLException {


		return null;
	}

	@Override
	public int getUserCount() throws SQLException {

		return 1;
	}

	@Override
	public int getDisabledUserCount() throws SQLException {

		return 0;
	}

	@Override
	public List<Character> getUserFirstLetterIndex(UserField filteringField) throws SQLException {

		return Collections.singletonList("J".charAt(0));
	}

	@Override
	public List<? extends User> getUsers(UserField filteringField, char startsWith, Order order, boolean groups, boolean attributes) throws SQLException {

		if("J".equalsIgnoreCase(Character.toString(startsWith))){

			return Collections.singletonList(IMMUTABLE_USER);
		}

		return null;
	}

	@Override
	public List<? extends User> getUsers(UserField filteringField, Order order, boolean groups, boolean attributes) throws SQLException {

		return Collections.singletonList(IMMUTABLE_USER);
	}

	public Group getGroup(Integer groupID) throws SQLException {

		log.info("Got request for groupID " + groupID);

		if(IMMUTABLE_GROUP.getGroupID().equals(groupID)){

			log.info("Returning immutable group " + IMMUTABLE_GROUP);

			return IMMUTABLE_GROUP;
		}

		log.info("Requested groupID " + groupID + " does not match groupID of immutable group " + IMMUTABLE_GROUP.getGroupID());

		return null;
	}

	public List<? extends Group> getGroups() throws SQLException {

		return CollectionUtils.getList(IMMUTABLE_GROUP);
	}

	public List<? extends Group> getGroups(List<Integer> groupIDs) throws SQLException {

		if(groupIDs.contains(IMMUTABLE_GROUP.getGroupID())){

			return Collections.singletonList(IMMUTABLE_GROUP);
		}

		return null;
	}

	public int getGroupCount() throws SQLException {

		return 1;
	}

	public int getDisabledGroupCount() throws SQLException {


		return 0;
	}

	public List<? extends Group> getGroups(Order order, char startsWith) throws SQLException {

		if("I".equalsIgnoreCase(Character.toString(startsWith))){

			return Collections.singletonList(IMMUTABLE_GROUP);
		}

		return null;
	}

	public List<Character> getGroupFirstLetterIndex() throws SQLException {

		return CollectionUtils.getList(IMMUTABLE_GROUP.getName().charAt(0));
	}

	@Override
	public int getPriority() {

		return 0;
	}

	@Override
	public boolean isProviderFor(User user) {

		return user.equals(IMMUTABLE_USER);
	}

	public boolean isProviderFor(Group group) {

		return group.equals(IMMUTABLE_GROUP);
	}

	@Override
	public DataSource getDataSource() {


		return null;
	}

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		systemInterface.getUserHandler().addProvider(this);
	}

	@Override
	public void unload() {

		systemInterface.getUserHandler().removeProvider(this);
	}

	@Override
	public List<? extends User> getUsersByGroup(Integer groupID, boolean groups, boolean attributes) throws SQLException {

		return null;
	}

	@Override
	public List<? extends User> getUsersByGroups(Collection<Integer> groupIDs, boolean attributes) throws SQLException {


		return null;
	}

	@Override
	public List<? extends User> getUsersByAttribute(String attributeName, String attributeValue, boolean groups, boolean attributes) throws SQLException {

		return null;
	}

	@Override
	public User getUserByAttribute(String attributeName, String attributeValue, boolean groups, boolean attributes) throws SQLException {

		return null;
	}

	@Override
	public List<? extends User> searchUsers(String query, boolean groups, boolean attributes, Integer maxHits) throws SQLException {

		return null;
	}

	@Override
	public List<? extends User> getUserByAttribute(String attributeName, boolean groups, boolean attributes) {

		return null;
	}

	@Override
	public List<? extends User> getUsersWithoutAttribute(String attributeName, boolean groups, boolean attributes) throws SQLException {

		return null;
	}

	@Override
	public int getUserCountByGroup(Integer groupID) throws SQLException {

		return 0;
	}
}
