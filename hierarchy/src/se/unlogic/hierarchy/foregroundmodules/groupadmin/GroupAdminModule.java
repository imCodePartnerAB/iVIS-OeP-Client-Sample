package se.unlogic.hierarchy.foregroundmodules.groupadmin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextAreaSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.beans.AttributeDescriptor;
import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.beans.MutableGroup;
import se.unlogic.hierarchy.core.beans.MutableUser;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.exceptions.UnableToUpdateUserException;
import se.unlogic.hierarchy.core.handlers.GroupHandler;
import se.unlogic.hierarchy.core.handlers.UserHandler;
import se.unlogic.hierarchy.core.interfaces.AttributeHandler;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.utils.AttributeDescriptorUtils;
import se.unlogic.hierarchy.core.utils.CRUDCallback;
import se.unlogic.hierarchy.core.utils.usergrouplist.UserGroupListConnector;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.hierarchy.foregroundmodules.groupadmin.cruds.GroupCRUD;
import se.unlogic.hierarchy.foregroundmodules.groupproviders.SimpleGroup;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.populators.annotated.AnnotatedRequestPopulator;


public class GroupAdminModule extends AnnotatedForegroundModule implements CRUDCallback<User> {

	@ModuleSetting(allowsNull = true)
	@TextAreaSettingDescriptor(name="Supported attributes", description="The attributes to show in the form. The format is [name][*/!]:[display name]:[max length]:[StringFormatValidator] (without brackets). Only the name is required. The * sign indicates if the attribute is required or not. The ! sign indicates that the attribute is read only")
	protected String supportedAttributes;

	protected List<AttributeDescriptor> attributes;

	protected GroupCRUD<? extends GroupAdminModule> groupCRUD;

	protected UserGroupListConnector userGroupListConnector;

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		this.groupCRUD = getGroupCRUD();

		this.userGroupListConnector = new UserGroupListConnector(systemInterface);
	}

	@Override
	protected void moduleConfigured() throws Exception {

		super.moduleConfigured();

		attributes = AttributeDescriptorUtils.parseAttributes(supportedAttributes);

	}

	protected GroupCRUD<? extends GroupAdminModule> getGroupCRUD() {

		AnnotatedRequestPopulator<MutableGroup> populator = new AnnotatedRequestPopulator<MutableGroup>(MutableGroup.class);

		populator.setBeanFactory(new SimpleGroupFactory());

		return new GroupCRUD<GroupAdminModule>(populator, "Group", "group", this);
	}

	@Override
	public ForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return list(req, res, user, uriParser, null);
	}

	public ForegroundModuleResponse list(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, List<ValidationError> validationErrors) throws Exception {

		log.info("User " + user + " listing groups");

		Document doc = createDocument(req, uriParser, user);
		Element groupListElement = doc.createElement("Groups");
		doc.getFirstChild().appendChild(groupListElement);

		if (validationErrors != null) {
			XMLUtils.append(doc, groupListElement, validationErrors);
		}

		XMLUtils.append(doc, groupListElement, systemInterface.getGroupHandler().getGroups(false));

		XMLUtils.appendNewElement(doc, groupListElement, "canAddGroup", canAddGroup());

		return new SimpleForegroundModuleResponse(doc, this.moduleDescriptor.getName(), getDefaultBreadcrumb());
	}

	@Override
	public Document createDocument(HttpServletRequest req, URIParser uriParser, User user) {

		Document doc = XMLUtils.createDomDocument();
		Element document = doc.createElement("Document");
		document.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser));
		document.appendChild(this.sectionInterface.getSectionDescriptor().toXML(doc));
		document.appendChild(this.moduleDescriptor.toXML(doc));
		doc.appendChild(document);
		return doc;
	}

	@WebPublic(alias="show")
	public ForegroundModuleResponse showGroup(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		Group group;

		if (uriParser.size() != 3 || !NumberUtils.isInt(uriParser.get(2)) || (group = systemInterface.getGroupHandler().getGroup(NumberUtils.toInt(uriParser.get(2)), true)) == null) {

			return list(req, res, user, uriParser, Collections.singletonList(new ValidationError("RequestedGroupNotFound")));
		}

		log.info("User " + user + " viewing group " + group);

		Document doc = this.createDocument(req, uriParser, user);
		Element showGroupElement = doc.createElement("ShowGroup");

		doc.getFirstChild().appendChild(showGroupElement);

		Element groupElement = group.toXML(doc);
		showGroupElement.appendChild(groupElement);

		AttributeHandler attributeHandler = group.getAttributeHandler();

		if(attributeHandler != null && !attributeHandler.isEmpty()){

			groupElement.appendChild(attributeHandler.toXML(doc));
		}

		appendShowFormData(group, showGroupElement, doc);

		XMLUtils.appendNewElement(doc, showGroupElement, "canAddGroup", canAddGroup());

		XMLUtils.append(doc, showGroupElement, "GroupUsers", systemInterface.getUserHandler().getUsersByGroup(group.getGroupID(), false, false));
		XMLUtils.append(doc, showGroupElement, "AttributeDescriptors", attributes);

		SimpleForegroundModuleResponse moduleResponse = new SimpleForegroundModuleResponse(doc, getTitlePrefix());

		moduleResponse.addBreadcrumbFirst(getDefaultBreadcrumb());

		return moduleResponse;
	}

	protected void appendShowFormData(Group group, Element showGroupElement, Document doc) throws SQLException{};

	@WebPublic(alias="setusers")
	public ForegroundModuleResponse setUser(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		Group group;

		if (uriParser.size() != 3 || !NumberUtils.isInt(uriParser.get(2)) || (group = systemInterface.getGroupHandler().getGroup(NumberUtils.toInt(uriParser.get(2)), false)) == null) {

			return list(req, res, user, uriParser, Collections.singletonList(new ValidationError("UpdateFailedGroupNotFound")));
		}

		if(req.getMethod().equalsIgnoreCase("POST")){

			setUsers(group, req);

			log.info("User " + user + " updated users of group " + group);

			redirectToDefaultMethod(req, res);

			return null;
		}

		log.info("User " + user + " updating users of group " + group);

		Document doc = this.createDocument(req, uriParser, user);
		Element showGroupElement = doc.createElement("SetGroupUsers");

		doc.getFirstChild().appendChild(showGroupElement);

		showGroupElement.appendChild(group.toXML(doc));

		UserHandler userHandler = systemInterface.getUserHandler();

		XMLUtils.append(doc, showGroupElement, "GroupUsers", userHandler.getUsersByGroup(group.getGroupID(), false, false));
		XMLUtils.append(doc, showGroupElement, "Users", userHandler.getUsers(true, false));

		SimpleForegroundModuleResponse moduleResponse = new SimpleForegroundModuleResponse(doc, getTitlePrefix());

		moduleResponse.addBreadcrumbFirst(getDefaultBreadcrumb());

		return moduleResponse;
	}

	public void setUsers(Group group, HttpServletRequest req){

		UserHandler userHandler = systemInterface.getUserHandler();

		List<User> currentUsers = userHandler.getUsersByGroup(group.getGroupID(), true, false);

		List<Integer> postedUsersID = NumberUtils.toInt(req.getParameterValues("user"));

		List<User> postedUsers = null;

		if(postedUsersID != null){

			postedUsers = userHandler.getUsers(postedUsersID, true, false);
		}

		if(currentUsers != null && postedUsers == null){

			//Remove users
			removeUsersFromGroup(currentUsers, group);

		}else if(currentUsers == null && postedUsers != null){

			//Add users
			addUsersToGroup(postedUsers, group);

		}else if(currentUsers != null && postedUsers != null){

			List<User> removeList = new ArrayList<User>(currentUsers.size());
			List<User> addList = new ArrayList<User>(postedUsers.size());

			Iterator<User> currentUserIterator = currentUsers.iterator();

			while(currentUserIterator.hasNext()){

				User user = currentUserIterator.next();

				if(!postedUsers.contains(user)){

					removeList.add(user);

					currentUserIterator.remove();
				}
			}

			Iterator<User> postedUserIterator = postedUsers.iterator();

			while(postedUserIterator.hasNext()){

				User user = postedUserIterator.next();

				if(!currentUsers.contains(user)){

					addList.add(user);
				}
			}

			removeUsersFromGroup(removeList, group);
			addUsersToGroup(addList, group);
		}
	}

	public void addUsersToGroup(List<User> users, Group group){

		for(User currentUser : users){

			if(currentUser instanceof MutableUser){

				if(currentUser.getGroups() != null){

					currentUser.getGroups().add(group);

				}else{

					((MutableUser)currentUser).setGroups(CollectionUtils.getList(group));
				}


				try {
					systemInterface.getUserHandler().updateUser(currentUser,false,true, false);

					log.info("User " + currentUser + " added to group " + group);

				} catch (UnableToUpdateUserException e) {

					log.error("Error adding user " + currentUser + " to group " + group,e);
				}
			}
		}
	}

	public void removeUsersFromGroup(List<User> users, Group bean){

		if(users != null){

			for(User currentUser : users){

				if(currentUser instanceof MutableUser && currentUser.getGroups() != null){

					currentUser.getGroups().remove(bean);

					try {
						systemInterface.getUserHandler().updateUser(currentUser,false,true, false);

						log.info("User " + currentUser + " removed from group " + bean);

					} catch (UnableToUpdateUserException e) {

						log.error("Error removing user " + currentUser + " from group " + bean,e);
					}
				}
			}
		}
	}

	@Override
	public String getTitlePrefix() {

		return moduleDescriptor.getName();
	}

	public boolean canAddGroup() {

		return systemInterface.getGroupHandler().canAddGroupClass(SimpleGroup.class);
	}

	@WebPublic
	public ForegroundModuleResponse add(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return groupCRUD.add(req, res, user, uriParser);
	}

	@WebPublic
	public ForegroundModuleResponse update(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return groupCRUD.update(req, res, user, uriParser);
	}

	@WebPublic
	public ForegroundModuleResponse delete(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return groupCRUD.delete(req, res, user, uriParser);
	}

	@WebPublic(alias = "users")
	public ForegroundModuleResponse getUsers(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {

		return userGroupListConnector.getUsers(req, res, user, uriParser);
	}

	public GroupHandler getGroupHandler() {

		return systemInterface.getGroupHandler();
	}

	public UserHandler getUserHandler() {

		return systemInterface.getUserHandler();
	}

	public List<AttributeDescriptor> getSupportedAttributes(){

		return attributes;
	}
}
