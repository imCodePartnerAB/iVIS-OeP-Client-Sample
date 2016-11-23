package se.unlogic.hierarchy.foregroundmodules.useradmin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.annotations.InstanceManagerDependency;
import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.annotations.XSLVariable;
import se.unlogic.hierarchy.core.beans.Breadcrumb;
import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.beans.UserTypeDescriptor;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.exceptions.UnableToDeleteUserException;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.UserFormProvider;
import se.unlogic.hierarchy.core.interfaces.ViewFragment;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.hierarchy.foregroundmodules.groupadmin.GroupAccessHandler;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;


/**
 * This is a simplified user administration module that only allows the current user to administrate users which are members of one or groups which the current user is administrator for. The group administrator access is looked up using {@link GroupAccessHandler} interface.
 * 
 * @author Robert "Unlogic" Olofsson
 *
 */
public class GroupAccessUserAdminModule extends AnnotatedForegroundModule{

	@XSLVariable(prefix="java.")
	protected String addUserBreadCrumbText = "Add user";

	@XSLVariable(prefix="java.")
	protected String updateUserBreadCrumbText = "Edit user: ";

	@XSLVariable(prefix="java.")
	protected String listUserTypesBreadCrumbText = "Select user type";

	@InstanceManagerDependency(required = true)
	protected GroupAccessHandler accessHandler;

	@Override
	public ForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {

		return list(req, res, user, uriParser, null);
	}

	public ForegroundModuleResponse list(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, ValidationError validationError) throws Exception {

		if(user == null){

			throw new AccessDeniedException("Login required to administrate users");
		}

		log.info("User " + user + " listing users");

		Document doc = this.createDocument(req, uriParser, user);

		Element userListElement = doc.createElement("UserList");

		doc.getFirstChild().appendChild(userListElement);

		List<Group> groupAccess = accessHandler.getUserAdminGroups(user);

		if(groupAccess != null){

			XMLUtils.append(doc, userListElement, "GroupAccess", groupAccess);

			List<Integer> groupIDs = new ArrayList<Integer>(groupAccess.size());

			for(Group group : groupAccess){

				groupIDs.add(group.getGroupID());
			}

			XMLUtils.append(doc, userListElement, "Users", systemInterface.getUserHandler().getUsersByGroups(groupIDs, true));

			XMLUtils.appendNewElement(doc, userListElement, "canAddUser", systemInterface.getUserHandler().hasFormAddableUserTypes());
		}

		if (validationError != null) {
			userListElement.appendChild(validationError.toXML(doc));
		}

		return new SimpleForegroundModuleResponse(doc, this.moduleDescriptor.getName(), this.getDefaultBreadcrumb());
	}

	@WebPublic(alias="show")
	public ForegroundModuleResponse showUser(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		User requestedUser;

		if (uriParser.size() != 3 || !NumberUtils.isInt(uriParser.get(2)) || (requestedUser = systemInterface.getUserHandler().getUser(NumberUtils.toInt(uriParser.get(2)),true, true)) == null) {

			return list(req, res, user, uriParser, new ValidationError("RequestedUserNotFound"));
		}

		List<Group> groupAccess = accessHandler.getUserAdminGroups(user);

		if(!checkUserAccess(requestedUser, groupAccess)){

			throw new AccessDeniedException("User does not have access to show user " + requestedUser);
		}

		log.info("User " + user + " viewing user " + requestedUser);

		Document doc = this.createDocument(req, uriParser, user);

		Element showUserElement = doc.createElement("ShowUser");
		doc.getFirstChild().appendChild(showUserElement);

		showUserElement.appendChild(requestedUser.toXML(doc));

		String name = getBeanName(requestedUser);

		SimpleForegroundModuleResponse moduleResponse = new SimpleForegroundModuleResponse(doc, name);

		UserFormProvider formProvider = systemInterface.getUserHandler().getUserFormProvider(requestedUser);

		if(formProvider != null){

			ViewFragment viewFragment = formProvider.getBeanView(requestedUser, req, user, uriParser, new GroupListUserFormCallback(groupAccess));

			showUserElement.appendChild(viewFragment.toXML(doc));

			appendLinksAndScripts(moduleResponse, viewFragment);
		}

		moduleResponse.addBreadcrumbFirst(new Breadcrumb(name, this.getFullAlias() + "/show/" + requestedUser.getUserID()));
		moduleResponse.addBreadcrumbFirst(getDefaultBreadcrumb());

		return moduleResponse;
	}

	@WebPublic(alias="listtypes")
	public ForegroundModuleResponse listUserTypes(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		List<UserTypeDescriptor> userTypeDescriptors = systemInterface.getUserHandler().getFormAddableUserTypes();

		if(userTypeDescriptors == null){

			return list(req, res, user, uriParser, new ValidationError("NoFormAddableUserTypesAvailable"));

		}else if(userTypeDescriptors.size() == 1){

			redirectToMethod(req, res, "/add/" + userTypeDescriptors.get(0).getUserTypeID());

			return null;
		}

		Document doc = this.createDocument(req, uriParser, user);
		Element listUserTypesElement = doc.createElement("ListUserTypes");
		doc.getFirstChild().appendChild(listUserTypesElement);

		XMLUtils.append(doc, listUserTypesElement, "UsersTypeDescriptors", userTypeDescriptors);

		SimpleForegroundModuleResponse moduleResponse = new SimpleForegroundModuleResponse(doc);

		moduleResponse.addBreadcrumbFirst(new Breadcrumb(this, this.listUserTypesBreadCrumbText, "/listtypes"));
		moduleResponse.addBreadcrumbFirst(getDefaultBreadcrumb());

		return moduleResponse;
	}

	@WebPublic
	public ForegroundModuleResponse add(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		String userTypeID;
		UserFormProvider formProvider;

		if (uriParser.size() != 3 || (userTypeID = uriParser.get(2)) == null || (formProvider = systemInterface.getUserHandler().getUserFormProvider(userTypeID)) == null) {

			return list(req, res, user, uriParser, new ValidationError("RequestedUserTypeNotFound"));
		}

		List<Group> groupAccess = accessHandler.getUserAdminGroups(user);

		if(groupAccess == null){

			throw new AccessDeniedException("User does not have access to add users");
		}

		GroupListUserFormCallback formCallback = new GroupListUserFormCallback(groupAccess);

		ValidationException validationException = null;
		ValidationError validationError = null;

		if(req.getMethod().equalsIgnoreCase("POST")){

			try{
				User newUser = formProvider.populate(req, user, uriParser, formCallback);

				validationError = checkRequiredGroups(newUser, formCallback);

				//Workaround
				validationException = new ValidationException(new ArrayList<ValidationError>(0));

				if(validationError == null){

					log.info("User " + user + " adding user " + newUser);

					formProvider.add(newUser, formCallback);

					redirectToDefaultMethod(req, res);

					return null;
				}

			}catch(ValidationException e){

				validationException = e;
			}
		}

		log.info("User " + user + " requested add user form for userTypeID " + userTypeID);

		Document doc = this.createDocument(req, uriParser, user);
		Element addUserElement = doc.createElement("AddUser");
		doc.getFirstChild().appendChild(addUserElement);

		ViewFragment viewFragment = formProvider.getAddForm(req, user, uriParser, validationException, formCallback);

		addUserElement.appendChild(viewFragment.toXML(doc));
		XMLUtils.append(doc, addUserElement, validationError);

		SimpleForegroundModuleResponse moduleResponse = new SimpleForegroundModuleResponse(doc);

		moduleResponse.addBreadcrumbFirst(new Breadcrumb(this, this.addUserBreadCrumbText, "/add/" + userTypeID));
		moduleResponse.addBreadcrumbFirst(getDefaultBreadcrumb());

		appendLinksAndScripts(moduleResponse, viewFragment);

		return moduleResponse;
	}

	@WebPublic
	public ForegroundModuleResponse update(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		Integer userID;
		User requestedUser;

		if (uriParser.size() != 3 || (userID = uriParser.getInt(2)) == null || (requestedUser = systemInterface.getUserHandler().getUser(userID, true, true)) == null) {

			return list(req, res, user, uriParser, new ValidationError("RequestedUserNotFound"));
		}

		List<Group> groupAccess = accessHandler.getUserAdminGroups(user);

		if(groupAccess == null || !checkUserAccess(requestedUser, groupAccess)){

			throw new AccessDeniedException("User does not have access to update user " + requestedUser);
		}

		GroupListUserFormCallback formCallback = new GroupListUserFormCallback(groupAccess);

		UserFormProvider formProvider = systemInterface.getUserHandler().getUserFormProvider(requestedUser);

		if(formProvider == null){

			return list(req, res, user, uriParser, new ValidationError("UpdateFailedUserNotUpdatable"));
		}

		ValidationException validationException = null;
		ValidationError validationError = null;

		if(req.getMethod().equalsIgnoreCase("POST")){

			try{
				Collection<Group> oldGroups = requestedUser.getGroups();

				requestedUser = formProvider.populate(requestedUser, req, user, uriParser, formCallback);

				validationError = checkRequiredGroups(requestedUser, formCallback);

				if(validationError == null){

					//Transfer protected groups
					if(oldGroups != null){

						for(Group oldGroup : oldGroups){

							if(formCallback.getAvailableGroups().contains(oldGroup)){

								//This is a non-protected group, continue
								continue;
							}

							//Add protected group back to user
							if(!requestedUser.getGroups().contains(oldGroup)){

								requestedUser.getGroups().add(oldGroup);
							}
						}
					}

					log.info("User " + user + " updating user " + requestedUser);

					formProvider.update(requestedUser, formCallback);

					redirectToDefaultMethod(req, res);

					return null;
				}

			}catch(ValidationException e){

				validationException = e;
			}
		}

		log.info("User " + user + " requested update user form for " + requestedUser);

		Document doc = this.createDocument(req, uriParser, user);
		Element updateUserElement = doc.createElement("UpdateUser");
		doc.getFirstChild().appendChild(updateUserElement);

		updateUserElement.appendChild(requestedUser.toXML(doc));

		ViewFragment viewFragment = formProvider.getUpdateForm(requestedUser, req, user, uriParser, validationException, formCallback);

		updateUserElement.appendChild(viewFragment.toXML(doc));
		XMLUtils.append(doc, updateUserElement, validationError);

		SimpleForegroundModuleResponse moduleResponse = new SimpleForegroundModuleResponse(doc);

		moduleResponse.addBreadcrumbFirst(new Breadcrumb(this, this.updateUserBreadCrumbText + getBeanName(requestedUser), "/update/" + requestedUser.getUserID()));
		moduleResponse.addBreadcrumbFirst(getDefaultBreadcrumb());

		appendLinksAndScripts(moduleResponse, viewFragment);

		return moduleResponse;
	}

	private ValidationError checkRequiredGroups(User user, GroupListUserFormCallback formCallback) throws ValidationException {

		if(user.getGroups() != null){

			for(Group requiredGroup : formCallback.getAvailableGroups()){

				if(user.getGroups().contains(requiredGroup)){

					return null;
				}
			}
		}

		return new ValidationError("AtLeastOneGroupRequired");
	}

	private void appendLinksAndScripts(SimpleForegroundModuleResponse moduleResponse, ViewFragment viewFragment) {

		if(viewFragment.getLinks() != null){

			moduleResponse.addLinks(viewFragment.getLinks());
		}

		if(viewFragment.getScripts() != null){

			moduleResponse.addScripts(viewFragment.getScripts());
		}
	}

	@WebPublic
	public ForegroundModuleResponse delete(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		Integer userID;

		if(uriParser.size() != 3 || (userID = uriParser.getInt(2)) == null){

			throw new URINotFoundException(uriParser);
		}

		User requestedUser = systemInterface.getUserHandler().getUser(userID, true, false);

		if(requestedUser == null){

			return list(req, res, user, uriParser, new ValidationError("RequestedUserNotFound"));
		}

		List<Group> groupAccess = accessHandler.getUserAdminGroups(user);

		if(groupAccess == null || !checkUserAccess(requestedUser, groupAccess)){

			throw new AccessDeniedException("User does not have access to delete user " + requestedUser);
		}

		try{
			log.info("User " + user + " deleting user " + requestedUser);

			systemInterface.getUserHandler().deleteUser(requestedUser);

			redirectToDefaultMethod(req, res);

			return null;

		}catch(UnableToDeleteUserException e){

			log.info("Unable to delete user " + requestedUser);

			return list(req, res, user, uriParser, new ValidationError("DeleteFailedException"));
		}
	}

	private boolean checkUserAccess(User requestedUser, List<Group> groupAccess) {

		if(requestedUser.getGroups() != null && groupAccess != null){

			for(Group group : groupAccess){

				if(requestedUser.getGroups().contains(group)){

					return true;
				}
			}
		}

		return false;
	}

	protected String getBeanName(User user) {

		return user.getFirstname() + " " + user.getLastname();
	}

	public Document createDocument(HttpServletRequest req, URIParser uriParser, User user) {

		Document doc = XMLUtils.createDomDocument();
		Element document = doc.createElement("Document");
		document.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser));
		document.appendChild(this.moduleDescriptor.toXML(doc));
		doc.appendChild(document);
		return doc;
	}
}
