package se.unlogic.hierarchy.foregroundmodules.useradmin;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.annotations.CheckboxSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.EnumDropDownSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.annotations.XSLVariable;
import se.unlogic.hierarchy.core.beans.Breadcrumb;
import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.beans.UserTypeDescriptor;
import se.unlogic.hierarchy.core.enums.CRUDAction;
import se.unlogic.hierarchy.core.enums.EventTarget;
import se.unlogic.hierarchy.core.enums.UserField;
import se.unlogic.hierarchy.core.events.CRUDEvent;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.exceptions.UnableToDeleteUserException;
import se.unlogic.hierarchy.core.handlers.GroupHandler;
import se.unlogic.hierarchy.core.handlers.UserHandler;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.SectionDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.interfaces.UserFormCallback;
import se.unlogic.hierarchy.core.interfaces.UserFormProvider;
import se.unlogic.hierarchy.core.interfaces.ViewFragment;
import se.unlogic.hierarchy.core.utils.ViewFragmentUtils;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.hierarchy.foregroundmodules.login.LoginEvent;
import se.unlogic.standardutils.enums.Order;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;


public class UserAdminModule extends AnnotatedForegroundModule implements UserFormCallback {

	@XSLVariable(prefix="java.")
	protected String addUserBreadCrumbText = "Add user";

	@XSLVariable(prefix="java.")
	protected String updateUserBreadCrumbText = "Edit user: ";

	@XSLVariable(prefix="java.")
	protected String listUserTypesBreadCrumbText = "Select user type";

	@ModuleSetting
	@EnumDropDownSettingDescriptor(name="User sorting",description="Controls which field user should be indexed and sorted by",required=true)
	protected UserField filteringField = UserField.FIRSTNAME;

	@ModuleSetting
	@CheckboxSettingDescriptor(name="Admin administration",description="Allow administration of users with admin flag set and the possbility to add set the admin flag on users")
	private boolean allowAdminAdministration = true;

	@ModuleSetting
	@CheckboxSettingDescriptor(name="Group administration",description="Allow administration of user groups")
	private boolean allowGroupAdministration = true;

	@ModuleSetting
	@CheckboxSettingDescriptor(name="Allow user switching",description="Controls if user switching is allowed (not this may have side effects for modules that store data in the session object)")
	private boolean allowUserSwitching = false;

	protected UserHandler userHandler;
	protected GroupHandler groupHandler;

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		this.userHandler = systemInterface.getUserHandler();
		this.groupHandler = systemInterface.getGroupHandler();
	}

	@Override
	public ForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return list(req, res, user, uriParser, null);
	}

	public ForegroundModuleResponse list(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, ValidationError validationError) throws Exception {

		log.info("User " + user + " requested user statistics");

		Document doc = this.createDocument(req, uriParser, user);

		Element userStatistics = doc.createElement("UserStatistics");

		doc.getFirstChild().appendChild(userStatistics);

		addFirstLetterIndex(userStatistics,doc);

		XMLUtils.appendNewElement(doc, userStatistics, "userCount", userHandler.getUserCount());
		XMLUtils.appendNewElement(doc, userStatistics, "disabledUserCount", userHandler.getDisabledUserCount());
		XMLUtils.appendNewElement(doc, userStatistics, "groupCount", groupHandler.getGroupCount());
		XMLUtils.appendNewElement(doc, userStatistics, "disabledGroupCount", groupHandler.getDisabledGroupCount());
		XMLUtils.appendNewElement(doc, userStatistics, "userProviderCount", userHandler.getUserProviderCount());

		if (validationError != null) {
			userStatistics.appendChild(validationError.toXML(doc));
		}

		XMLUtils.appendNewElement(doc, userStatistics, "canAddUser", userHandler.hasFormAddableUserTypes());

		return new SimpleForegroundModuleResponse(doc, this.moduleDescriptor.getName(), this.getDefaultBreadcrumb());
	}

	private void addFirstLetterIndex(Element targetElement, Document doc) {

		Element lettersElement = doc.createElement("Letters");
		targetElement.appendChild(lettersElement);

		XMLUtils.appendNewElement(doc, lettersElement, "filteringField", filteringField);

		Set<Character> characters = userHandler.getUserFirstLetterIndex(filteringField);

		for(Character character : characters){

			lettersElement.appendChild(XMLUtils.createCDATAElement("Letter", character, doc));
		}
	}

	public Breadcrumb getCurrentLetterBreadCrumb(User user) {

		String currentLetter = this.getFirstLetter(user,filteringField);

		return new Breadcrumb(currentLetter.toString(), this.getFullAlias() + "/letter/" + currentLetter);
	}

	private String getFirstLetter(User user, UserField filteringField) {

		if(filteringField == UserField.EMAIL){

			return getFirstLetterUppercase(user.getEmail());

		}else if(filteringField == UserField.FIRSTNAME){

			return getFirstLetterUppercase(user.getFirstname());

		}else if(filteringField == UserField.LASTNAME){

			return getFirstLetterUppercase(user.getLastname().substring(0, 1));

		}else if(filteringField == UserField.USERNAME){

			return getFirstLetterUppercase(user.getUsername().substring(0, 1));
		}

		log.warn("Unknown user filtering field " + filteringField);

		return "";
	}

	public String getFirstLetterUppercase(String string){

		return string.substring(0, 1).toUpperCase();
	}

	@WebPublic(alias="letter")
	public ForegroundModuleResponse showLetter(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		if(uriParser.size() != 3 || uriParser.get(2).length() != 1){

			throw new URINotFoundException(uriParser);
		}

		Character currentLetter = uriParser.get(2).toUpperCase().charAt(0);

		log.info("User " + user + " listing users starting by letter " + currentLetter);

		Document doc = this.createDocument(req, uriParser, user);

		Element showLetterElement = doc.createElement("ShowLetter");
		doc.getFirstChild().appendChild(showLetterElement);

		XMLUtils.appendNewElement(doc, showLetterElement, "currentLetter", currentLetter);

		addFirstLetterIndex(showLetterElement,doc);

		List<User> users = userHandler.getUsers(filteringField, currentLetter, Order.ASC, false, true);

		XMLUtils.append(doc, showLetterElement, "Users", users);

		XMLUtils.appendNewElement(doc, showLetterElement, "canAddUser", userHandler.hasFormAddableUserTypes());

		XMLUtils.appendNewElement(doc, showLetterElement, "allowAdminAdministration", allowAdminAdministration);
		XMLUtils.appendNewElement(doc, showLetterElement, "allowUserSwitching", allowUserSwitching);

		if(allowUserSwitching){

			XMLUtils.appendNewElement(doc, showLetterElement, "allowUserSwitching");
		}

		SimpleForegroundModuleResponse moduleResponse = new SimpleForegroundModuleResponse(doc, this.moduleDescriptor.getName() + " (" + currentLetter + ")");

		moduleResponse.addBreadcrumbFirst(new Breadcrumb(currentLetter.toString(), this.getFullAlias() + "/letter/" + currentLetter));
		moduleResponse.addBreadcrumbFirst(getDefaultBreadcrumb());

		return moduleResponse;
	}

	@WebPublic(alias="email-list")
	public ForegroundModuleResponse generateEmailList(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		log.info("User " + user + " requesting email list of all users");

		res.setCharacterEncoding("ISO-8859-1");
		res.setContentType("text/plain");
		res.setHeader("Content-Disposition", "attachment; filename=\"email-list.txt\"");
		res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, proxy-revalidate");

		PrintWriter writer = res.getWriter();

		List<User> users = userHandler.getUsers(UserField.EMAIL, Order.ASC, false, true);

		if(users != null){

			for(User currentUser : users){

				if(currentUser.getEmail() != null){

					writer.append(currentUser.getEmail());
					writer.append("; ");
				}
			}
		}

		try {
			res.getWriter().flush();
			res.getWriter().close();
		} catch (IOException e) {}

		return null;
	}

	public Document createDocument(HttpServletRequest req, URIParser uriParser, User user) {

		Document doc = XMLUtils.createDomDocument();
		Element document = doc.createElement("Document");
		document.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser));
		document.appendChild(this.moduleDescriptor.toXML(doc));
		doc.appendChild(document);
		return doc;
	}

	public void redirectToCurrentLetter(User user, HttpServletRequest req, HttpServletResponse res) throws IOException{

		res.sendRedirect(this.getModuleURI(req) + "/letter/" + URLEncoder.encode(getFirstLetter(user, filteringField), "UTF-8"));
	}


	public boolean allowAdminAdministration() {

		return allowAdminAdministration;
	}


	@Override
	public boolean allowGroupAdministration() {

		return allowGroupAdministration;
	}

	public SectionDescriptor getSectionInterface() {

		return sectionInterface.getSectionDescriptor();
	}

	public ForegroundModuleDescriptor getModuleDescriptor() {

		return moduleDescriptor;
	}

	@WebPublic(alias="show")
	public ForegroundModuleResponse showUser(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		User requestedUser;

		if (uriParser.size() != 3 || !NumberUtils.isInt(uriParser.get(2)) || (requestedUser = userHandler.getUser(NumberUtils.toInt(uriParser.get(2)),true, true)) == null) {

			return list(req, res, user, uriParser, new ValidationError("RequestedUserNotFound"));
		}

		log.info("User " + user + " viewing user " + requestedUser);

		Document doc = this.createDocument(req, uriParser, user);

		Element showUserElement = doc.createElement("ShowUser");
		doc.getFirstChild().appendChild(showUserElement);

		showUserElement.appendChild(requestedUser.toXML(doc));

		XMLUtils.appendNewElement(doc, showUserElement, "allowAdminAdministration", allowAdminAdministration);
		XMLUtils.appendNewElement(doc, showUserElement, "allowUserSwitching", allowUserSwitching);

		String name = getBeanName(requestedUser);

		SimpleForegroundModuleResponse moduleResponse = new SimpleForegroundModuleResponse(doc, name);

		UserFormProvider formProvider = systemInterface.getUserHandler().getUserFormProvider(requestedUser);

		if(formProvider != null){

			ViewFragment viewFragment = formProvider.getBeanView(requestedUser, req, user, uriParser, this);

			showUserElement.appendChild(viewFragment.toXML(doc));

			ViewFragmentUtils.appendLinksAndScripts(moduleResponse, viewFragment);
		}

		moduleResponse.addBreadcrumbFirst(new Breadcrumb(name, this.getFullAlias() + "/show/" + requestedUser.getUserID()));

		moduleResponse.addBreadcrumbFirst(getCurrentLetterBreadCrumb(requestedUser));
		moduleResponse.addBreadcrumbFirst(getDefaultBreadcrumb());

		return moduleResponse;
	}

	@WebPublic(alias="switch")
	public ForegroundModuleResponse switchUser(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		if(!allowUserSwitching){

			throw new AccessDeniedException("User switching is disabled");
		}

		User requestedUser;

		if (uriParser.size() != 3 || !NumberUtils.isInt(uriParser.get(2)) || (requestedUser = userHandler.getUser(NumberUtils.toInt(uriParser.get(2)),true, true)) == null) {

			return list(req, res, user, uriParser, new ValidationError("RequestedUserNotFound"));
		}

		log.info("User " + user + " switching to user " + requestedUser);

		HttpSession session = req.getSession(true);

		session.setAttribute("user", requestedUser);

		systemInterface.getEventHandler().sendEvent(User.class, new LoginEvent(requestedUser, req.getSession()), EventTarget.ALL);

		if(StringUtils.isEmpty(req.getContextPath())){

			res.sendRedirect("/");

		}else{

			res.sendRedirect(req.getContextPath());
		}

		return null;
	}

	@WebPublic(alias="listtypes")
	public ForegroundModuleResponse listUserTypes(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		List<UserTypeDescriptor> userTypeDescriptors = userHandler.getFormAddableUserTypes();

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

		ValidationException validationException = null;

		if(req.getMethod().equalsIgnoreCase("POST")){

			try{
				User newUser = formProvider.populate(req, user, uriParser, this);

				log.info("User " + user + " adding user " + newUser);

				formProvider.add(newUser, this);

				systemInterface.getEventHandler().sendEvent(User.class, new CRUDEvent<User>(CRUDAction.ADD, newUser), EventTarget.ALL);

				redirectToCurrentLetter(newUser, req, res);

				return null;

			}catch(ValidationException e){

				validationException = e;
			}
		}

		log.info("User " + user + " requested add user form for userTypeID " + userTypeID);

		Document doc = this.createDocument(req, uriParser, user);
		Element addUserElement = doc.createElement("AddUser");
		doc.getFirstChild().appendChild(addUserElement);

		ViewFragment viewFragment = formProvider.getAddForm(req, user, uriParser, validationException, this);

		addUserElement.appendChild(viewFragment.toXML(doc));

		SimpleForegroundModuleResponse moduleResponse = new SimpleForegroundModuleResponse(doc);

		moduleResponse.addBreadcrumbFirst(new Breadcrumb(this, this.addUserBreadCrumbText, "/add/" + userTypeID));
		moduleResponse.addBreadcrumbFirst(getDefaultBreadcrumb());

		ViewFragmentUtils.appendLinksAndScripts(moduleResponse, viewFragment);

		return moduleResponse;
	}

	@WebPublic
	public ForegroundModuleResponse update(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		Integer userID;
		User requestedUser;

		if (uriParser.size() != 3 || (userID = uriParser.getInt(2)) == null || (requestedUser = systemInterface.getUserHandler().getUser(userID, true, true)) == null) {

			return list(req, res, user, uriParser, new ValidationError("RequestedUserNotFound"));
		}

		UserFormProvider formProvider = systemInterface.getUserHandler().getUserFormProvider(requestedUser);

		if(formProvider == null){

			return list(req, res, user, uriParser, new ValidationError("UpdateFailedUserNotUpdatable"));
		}

		ValidationException validationException = null;

		if(req.getMethod().equalsIgnoreCase("POST")){

			try{
				requestedUser = formProvider.populate(requestedUser, req, user, uriParser, this);

				log.info("User " + user + " updating user " + requestedUser);

				formProvider.update(requestedUser, this);

				systemInterface.getEventHandler().sendEvent(User.class, new CRUDEvent<User>(CRUDAction.UPDATE, requestedUser), EventTarget.ALL);

				redirectToCurrentLetter(requestedUser, req, res);

				return null;

			}catch(ValidationException e){

				validationException = e;
			}
		}

		log.info("User " + user + " requested update user form for " + requestedUser);

		Document doc = this.createDocument(req, uriParser, user);
		Element updateUserElement = doc.createElement("UpdateUser");
		doc.getFirstChild().appendChild(updateUserElement);

		updateUserElement.appendChild(requestedUser.toXML(doc));

		ViewFragment viewFragment = formProvider.getUpdateForm(requestedUser, req, user, uriParser, validationException, this);

		updateUserElement.appendChild(viewFragment.toXML(doc));

		SimpleForegroundModuleResponse moduleResponse = new SimpleForegroundModuleResponse(doc);

		moduleResponse.addBreadcrumbFirst(new Breadcrumb(this, this.updateUserBreadCrumbText + getBeanName(requestedUser), "/update/" + requestedUser.getUserID()));
		moduleResponse.addBreadcrumbFirst(getDefaultBreadcrumb());

		ViewFragmentUtils.appendLinksAndScripts(moduleResponse, viewFragment);

		return moduleResponse;
	}

	@WebPublic
	public ForegroundModuleResponse delete(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		Integer userID;

		if(uriParser.size() != 3 || (userID = uriParser.getInt(2)) == null){

			throw new URINotFoundException(uriParser);
		}

		User requestedUser = userHandler.getUser(userID, false, false);

		if(requestedUser == null){

			return list(req, res, user, uriParser, new ValidationError("RequestedUserNotFound"));
		}

		try{
			log.info("User " + user + " deleting user " + requestedUser);

			userHandler.deleteUser(requestedUser);

			systemInterface.getEventHandler().sendEvent(User.class, new CRUDEvent<User>(CRUDAction.DELETE, requestedUser), EventTarget.ALL);

			redirectToCurrentLetter(requestedUser, req, res);

			return null;

		}catch(UnableToDeleteUserException e){

			log.info("Unable to delete user " + requestedUser);

			return list(req, res, user, uriParser, new ValidationError("DeleteFailedException"));
		}
	}

	@Override
	public List<Group> getAvailableGroups() {

		return groupHandler.getGroups(false);
	}

	@Override
	public Group getGroup(Integer groupID) {

		return groupHandler.getGroup(groupID, false);
	}

	@Override
	public boolean allowAdminFlagAccess() {

		return allowAdminAdministration;
	}

	protected String getBeanName(User user) {

		return user.getFirstname() + " " + user.getLastname();
	}
}
