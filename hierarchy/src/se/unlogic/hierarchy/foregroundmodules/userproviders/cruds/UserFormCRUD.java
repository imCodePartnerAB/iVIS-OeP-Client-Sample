package se.unlogic.hierarchy.foregroundmodules.userproviders.cruds;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.beans.MutableUser;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.MutableAttributeHandler;
import se.unlogic.hierarchy.core.interfaces.UserFormCallback;
import se.unlogic.hierarchy.core.utils.AttributeDescriptorUtils;
import se.unlogic.hierarchy.core.utils.GenericFormCRUD;
import se.unlogic.hierarchy.core.utils.ViewFragmentTransformer;
import se.unlogic.hierarchy.foregroundmodules.userproviders.AnnotatedMutableUserFormProviderModule;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationErrorType;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.BeanRequestPopulator;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;


public class UserFormCRUD<UserType extends MutableUser, ModuleCallback extends AnnotatedMutableUserFormProviderModule<UserType>> extends GenericFormCRUD<UserType, User, ModuleCallback, UserFormCallback> {

	public UserFormCRUD(BeanRequestPopulator<UserType> populator, String typeElementName, String typeLogName, ModuleCallback moduleCallback, ViewFragmentTransformer viewTransformer) {

		super(populator, typeElementName, typeLogName, moduleCallback, viewTransformer);
	}

	@Override
	protected void appendAddFormData(Document doc, Element addTypeElement, User user, HttpServletRequest req, URIParser uriParser, UserFormCallback formCallback) throws Exception {

		appendFormData(doc, addTypeElement, user, req, uriParser, formCallback);
	}

	@Override
	protected void appendUpdateFormData(UserType bean, Document doc, Element updateTypeElement, User user, HttpServletRequest req, URIParser uriParser, UserFormCallback formCallback) throws Exception {

		appendFormData(doc, updateTypeElement, user, req, uriParser, formCallback);
	}

	protected void appendFormData(Document doc, Element targetElement, User user, HttpServletRequest req, URIParser uriParser, UserFormCallback formCallback) {

		if(formCallback.allowGroupAdministration()){

			XMLUtils.append(doc, targetElement, "Groups", formCallback.getAvailableGroups());
		}

		if(formCallback.allowAdminFlagAccess()){

			XMLUtils.appendNewElement(doc, targetElement, "AllowAdminFlagAccess");
		}

		XMLUtils.append(doc, targetElement, "AttrbuteDescriptors", moduleCallback.getSupportedAttributes());
	}

	@Override
	protected void appendShowFormData(UserType bean, Document doc, Element showTypeElement, User user, HttpServletRequest req, URIParser uriParser, UserFormCallback formCallback) throws SQLException, IOException, Exception {

		XMLUtils.append(doc, showTypeElement, "AttrbuteDescriptors", moduleCallback.getSupportedAttributes());
	}

	@Override
	protected void validateAddPopulation(UserType bean, HttpServletRequest req, User user, URIParser uriParser, UserFormCallback formCallback) throws ValidationException, SQLException, Exception {

		if(moduleCallback.requirePasswordOnAdd() && StringUtils.isEmpty(bean.getPassword())){

			throw new ValidationException(new ValidationError("password",ValidationErrorType.RequiredField));
		}

		validatePopulation(bean, req, user, uriParser, formCallback);

		if(formCallback.allowGroupAdministration()){

			setUserGroups(bean, req, formCallback);
		}
	}

	@Override
	protected void validateUpdatePopulation(UserType bean, HttpServletRequest req, User user, URIParser uriParser, UserFormCallback formCallback) throws ValidationException, SQLException, Exception {

		validatePopulation(bean, req, user, uriParser, formCallback);

		if(formCallback.allowGroupAdministration()){

			setUserGroups(bean, req, formCallback);
		}
	}

	protected void validatePopulation(UserType bean, HttpServletRequest req, User user, URIParser uriParser, UserFormCallback formCallback) throws ValidationException {

		ArrayList<ValidationError> validationErrors = new ArrayList<ValidationError>(3);

		//The null checks below are there because not all user types require these fields

		if(bean.getUsername() != null){

			User usernameMatch = this.moduleCallback.getUserHandler().getUserByUsername(bean.getUsername(), false, false);

			if (usernameMatch != null && !usernameMatch.equals(bean)) {

				validationErrors.add(new ValidationError("UsernameAlreadyTaken"));
			}
		}

		if(bean.getEmail() != null){

			User emailMatch = this.moduleCallback.getUserHandler().getUserByEmail(bean.getEmail(), false, false);

			if (emailMatch != null && !emailMatch.equals(bean)) {
				validationErrors.add(new ValidationError("EmailAlreadyTaken"));
			}
		}

		if(req.getParameter("password") != null && bean.getPassword() != null && !bean.getPassword().equals(req.getParameter("passwordconfirmation"))){

			validationErrors.add(new ValidationError("PasswordConfirmationMissMatch"));
		}

		setAttributes(bean, req, formCallback, validationErrors);

		if(!validationErrors.isEmpty()){

			throw new ValidationException(validationErrors);
		}

		if(!formCallback.allowAdminFlagAccess()){

			bean.setAdmin(false);
		}
	}

	protected void setUserGroups(UserType bean, HttpServletRequest req, UserFormCallback formCallback) throws SQLException {

		ArrayList<Integer> groupIDList = NumberUtils.toInt(req.getParameterValues("group"));

		if (groupIDList != null) {

			List<Group> groups = new ArrayList<Group>(groupIDList.size());

			for(Integer groupID : groupIDList){

				Group group = formCallback.getGroup(groupID);

				if(group != null){

					groups.add(group);
				}
			}

			if(!groups.isEmpty()){

				bean.setGroups(groups);
				return;
			}
		}

		bean.setGroups(null);
	}

	protected void setAttributes(UserType bean, HttpServletRequest req, UserFormCallback formCallback, List<ValidationError> validationErrors) {

		if(moduleCallback.getSupportedAttributes() == null){

			return;
		}

		MutableAttributeHandler attributeHandler = bean.getAttributeHandler();

		if(attributeHandler == null){

			return;
		}

		AttributeDescriptorUtils.populateAttributes(attributeHandler, moduleCallback.getSupportedAttributes(), req, validationErrors);
	}

	@Override
	public UserType populateFromUpdateRequest(UserType bean, HttpServletRequest req, User user, URIParser uriParser, UserFormCallback formCallback) throws Exception {

		String oldPassword = bean.getPassword();

		bean = super.populateFromUpdateRequest(bean, req, user, uriParser, formCallback);

		String newPassword = bean.getPassword();

		if(newPassword == null){

			bean.setPassword(oldPassword);

		}else{

			if(oldPassword == null || !oldPassword.equals(newPassword)){

				bean.setPassword(moduleCallback.getHashedPassword(bean.getPassword()));
			}
		}

		return bean;
	}

	@Override
	protected void appendBean(UserType bean, Element targetElement, Document doc) {

		Element userElement = bean.toXML(doc);
		targetElement.appendChild(userElement);

		MutableAttributeHandler attributeHandler = bean.getAttributeHandler();

		if(attributeHandler != null && !attributeHandler.isEmpty()){

			userElement.appendChild(attributeHandler.toXML(doc));
		}
	}

	@Override
	protected Document createDocument(HttpServletRequest req, URIParser uriParser, User user) {

		Document doc = XMLUtils.createDomDocument();
		Element document = doc.createElement("Document");
		document.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser));
		document.appendChild(moduleCallback.getModuleDescriptor().toXML(doc));
		doc.appendChild(document);

		return doc;
	}
}
