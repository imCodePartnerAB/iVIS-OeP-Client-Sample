package se.unlogic.hierarchy.foregroundmodules.groupadmin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.beans.MutableGroup;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.InstanceHandler;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.utils.HierarchyAnnotatedDAOFactory;
import se.unlogic.hierarchy.foregroundmodules.groupadmin.cruds.GroupAccessCRUD;
import se.unlogic.hierarchy.foregroundmodules.groupadmin.cruds.GroupCRUD;
import se.unlogic.standardutils.dao.AnnotatedDAO;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.QueryParameterFactory;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.db.tableversionhandler.TableVersionHandler;
import se.unlogic.standardutils.db.tableversionhandler.UpgradeResult;
import se.unlogic.standardutils.db.tableversionhandler.XMLDBScriptProvider;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.populators.annotated.AnnotatedRequestPopulator;


/**
 * This is an extended version of the ordinary {@link GroupAdminModule} that allows administrators to be selected for each group.
 * <br>
 * This module also registers itself in the system wide {@link InstanceHandler} using the {@link GroupAccessHandler} interface so that other modules can lookup group admins.
 * 
 */
public class GroupAccessAdminModule extends GroupAdminModule implements GroupAccessHandler{

	protected AnnotatedDAO<GroupAdminMapping> groupAdminMappingDAO;

	protected QueryParameterFactory<GroupAdminMapping, Group> groupParamFactory;
	protected QueryParameterFactory<GroupAdminMapping, User> userParamFactory;

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		if(!systemInterface.getInstanceHandler().addInstance(GroupAccessHandler.class, this)){

			throw new RuntimeException("Unable to register module in global instance handler using key " + GroupAccessHandler.class.getSimpleName() + ", another instance is already registered using this key.");
		}
	}

	@Override
	public void unload() throws Exception {

		if(this.equals(systemInterface.getInstanceHandler().getInstance(GroupAccessHandler.class))){

			systemInterface.getInstanceHandler().removeInstance(GroupAccessHandler.class);
		}

		super.unload();
	}

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		//Automatic table version handling
		UpgradeResult upgradeResult = TableVersionHandler.upgradeDBTables(dataSource, GroupAccessAdminModule.class.getName(), new XMLDBScriptProvider(this.getClass().getResourceAsStream("dbscripts/GroupAccessAdminModule.xml")));

		if(upgradeResult.isUpgrade()){

			log.info(upgradeResult.toString());
		}

		groupAdminMappingDAO = new HierarchyAnnotatedDAOFactory(dataSource, getUserHandler(), getGroupHandler()).getDAO(GroupAdminMapping.class);
		groupParamFactory = groupAdminMappingDAO.getParamFactory("group", Group.class);
		userParamFactory = groupAdminMappingDAO.getParamFactory("user", User.class);

		super.createDAOs(dataSource);
	}

	@Override
	protected GroupCRUD<? extends GroupAdminModule> getGroupCRUD() {

		AnnotatedRequestPopulator<MutableGroup> populator = new AnnotatedRequestPopulator<MutableGroup>(MutableGroup.class);

		populator.setBeanFactory(new SimpleGroupFactory());

		return new GroupAccessCRUD(populator, "Group", "group", this);
	}

	public List<User> getGroupAdminUsers(Group group) throws SQLException{

		HighLevelQuery<GroupAdminMapping> query = new HighLevelQuery<GroupAdminMapping>();

		query.addExcludedField(GroupAdminMapping.GROUP_FIELD);

		query.addParameter(groupParamFactory.getParameter(group));

		List<GroupAdminMapping> mappings = groupAdminMappingDAO.getAll(query);

		if(mappings == null){

			return null;
		}

		List<User> users = new ArrayList<User>(mappings.size());

		for(GroupAdminMapping groupAdminMapping : mappings){

			if(groupAdminMapping.getUser() != null){

				users.add(groupAdminMapping.getUser());
			}
		}

		if(users.isEmpty()){

			return null;
		}

		return users;
	}

	@Override
	protected void appendShowFormData(Group group, Element showGroupElement, Document doc) throws SQLException {

		XMLUtils.append(doc, showGroupElement, "AdminUsers", getGroupAdminUsers(group));
	}

	public void deleteGroupAccessMappings(MutableGroup group, TransactionHandler transactionHandler) throws SQLException {

		HighLevelQuery<GroupAdminMapping> query = new HighLevelQuery<GroupAdminMapping>();

		query.addParameter(groupParamFactory.getParameter(group));

		if(transactionHandler != null){

			this.groupAdminMappingDAO.delete(query, transactionHandler);

		}else{

			this.groupAdminMappingDAO.delete(query);
		}
	}

	public void setGroupAccessMappings(MutableGroup group, HttpServletRequest req) throws SQLException {

		List<Integer> userIDs = NumberUtils.toInt(req.getParameterValues("admin_user"));

		if(userIDs == null){

			deleteGroupAccessMappings(group, null);
			return;
		}

		List<User> users = getUserHandler().getUsers(userIDs, false, false);

		if(users == null){

			deleteGroupAccessMappings(group, null);
			return;
		}

		List<GroupAdminMapping> adminMappings = new ArrayList<GroupAdminMapping>(users.size());

		for(User user : users){

			adminMappings.add(new GroupAdminMapping(group, user));
		}


		TransactionHandler transactionHandler = null;

		try{
			transactionHandler = new TransactionHandler(dataSource);

			deleteGroupAccessMappings(group, transactionHandler);
			groupAdminMappingDAO.addAll(adminMappings, transactionHandler, null);

			transactionHandler.commit();
		}finally{

			TransactionHandler.autoClose(transactionHandler);
		}
	}

	@Override
	public boolean isGroupAdmin(User user, Group group) throws SQLException {

		HighLevelQuery<GroupAdminMapping> query = new HighLevelQuery<GroupAdminMapping>();

		query.addParameter(groupParamFactory.getParameter(group));
		query.addParameter(userParamFactory.getParameter(user));

		return this.groupAdminMappingDAO.getBoolean(query);
	}

	@Override
	public List<Group> getUserAdminGroups(User user) throws SQLException {

		HighLevelQuery<GroupAdminMapping> query = new HighLevelQuery<GroupAdminMapping>();

		query.addExcludedField(GroupAdminMapping.USER_FIELD);
		query.addParameter(userParamFactory.getParameter(user));

		List<GroupAdminMapping> groupAdminMappings = groupAdminMappingDAO.getAll(query);

		if(groupAdminMappings == null){

			return null;
		}

		List<Group> groups = new ArrayList<Group>();

		for(GroupAdminMapping adminMapping : groupAdminMappings){

			if(adminMapping.getGroup() != null){

				groups.add(adminMapping.getGroup());
			}
		}

		if(groups.isEmpty()){

			return null;
		}

		return groups;
	}
}
