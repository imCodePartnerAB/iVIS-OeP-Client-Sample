/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.userprofile;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.emailutils.populators.EmailPopulator;
import se.unlogic.hierarchy.core.annotations.CheckboxSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.EnumDropDownSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextAreaSettingDescriptor;
import se.unlogic.hierarchy.core.beans.AttributeDescriptor;
import se.unlogic.hierarchy.core.beans.MutableUser;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.MutableAttributeHandler;
import se.unlogic.hierarchy.core.utils.AttributeDescriptorUtils;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.validation.ValidationUtils;

public class UserProfileModule extends AnnotatedForegroundModule {

	@ModuleSetting
	@EnumDropDownSettingDescriptor(name = "Username field", description = "Controls the behaivor of the username field.", required = true)
	protected FieldMode usernameFieldMode = FieldMode.DISABLED;

	@ModuleSetting
	@EnumDropDownSettingDescriptor(name = "Firstname field", description = "Controls the behaivor of the firstname field.", required = true)
	protected FieldMode firstnameFieldMode = FieldMode.DISABLED;

	@ModuleSetting
	@EnumDropDownSettingDescriptor(name = "Lastname field", description = "Controls the behaivor of the lastname field.", required = true)
	protected FieldMode lastnameFieldMode = FieldMode.DISABLED;

	@ModuleSetting
	@EnumDropDownSettingDescriptor(name = "E-mail field", description = "Controls the behaivor of the email field.", required = true)
	protected FieldMode emailFieldMode = FieldMode.DISABLED;

	@ModuleSetting
	@CheckboxSettingDescriptor(name = "Change password", description = "Controls if the users can change password")
	protected boolean allowPasswordChanging = false;

	@ModuleSetting(allowsNull = true)
	@TextAreaSettingDescriptor(name = "Supported attributes", description = "The attributes to show in the form. The format is [name][*/!]:[display name]:[max length]:[StringFormatValidator] (without brackets). Only the name is required. The * sign indicates if the attribute is required or not. The ! sign indicates that the attribute is read only")
	protected String supportedAttributes;

	protected List<AttributeDescriptor> attributes;

	@Override
	protected void moduleConfigured() throws Exception {

		super.moduleConfigured();

		attributes = AttributeDescriptorUtils.parseAttributes(supportedAttributes);
	}

	@Override
	public SimpleForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		User requestedUser;

		if(user == null || (requestedUser = systemInterface.getUserHandler().getUser(user.getUserID(), true, true)) == null){

			Document doc = this.createDocument(req, uriParser);
			Element updateUserElement = doc.createElement("UserNotFound");
			doc.getFirstChild().appendChild(updateUserElement);

			return new SimpleForegroundModuleResponse(doc, this.moduleDescriptor.getName(), getDefaultBreadcrumb());
		}

		boolean mutable = requestedUser instanceof MutableUser;

		MutableAttributeHandler attributeHandler;

		if(mutable){

			attributeHandler = ((MutableUser)requestedUser).getAttributeHandler();

		}else{

			attributeHandler = null;
		}

		boolean userUpdated = false;

		ValidationException validationException = null;

		if(mutable && req.getMethod().equalsIgnoreCase("POST")){

			MutableUser mutableUser = (MutableUser)requestedUser;

			// Populate user fields
			ArrayList<ValidationError> validationErrors = new ArrayList<ValidationError>();

			if(isPopulatable(usernameFieldMode)){

				String username = ValidationUtils.validateParameter("username", req, usernameFieldMode == FieldMode.REQUIRED, null, 40, validationErrors);

				if(username != null){

					User usernameMatch = systemInterface.getUserHandler().getUserByUsername(username, false, false);

					if(usernameMatch != null && !usernameMatch.equals(mutableUser)){

						validationErrors.add(new ValidationError("UsernameAlreadyTaken"));
					}
				}

				if(validationErrors.isEmpty()){

					mutableUser.setUsername(username);
				}
			}

			if(isPopulatable(emailFieldMode)){

				String email = ValidationUtils.validateParameter("email", req, emailFieldMode == FieldMode.REQUIRED, null, 255, new EmailPopulator(), validationErrors);

				if(email != null){

					User emailMatch = systemInterface.getUserHandler().getUserByEmail(email, false, false);

					if(emailMatch != null && !emailMatch.equals(mutableUser)){
						validationErrors.add(new ValidationError("EmailAlreadyTaken"));
					}
				}

				if(validationErrors.isEmpty()){

					mutableUser.setEmail(email);
				}
			}

			if(isPopulatable(firstnameFieldMode)){

				String firstname = ValidationUtils.validateParameter("firstname", req, firstnameFieldMode == FieldMode.REQUIRED, null, 30, validationErrors);

				if(validationErrors.isEmpty()){

					mutableUser.setFirstname(firstname);
				}
			}

			if(isPopulatable(lastnameFieldMode)){

				String lastname = ValidationUtils.validateParameter("lastname", req, lastnameFieldMode == FieldMode.REQUIRED, null, 40, validationErrors);

				if(validationErrors.isEmpty()){

					mutableUser.setLastname(lastname);
				}
			}

			boolean passwordChanged = false;

			if(allowPasswordChanging && req.getParameter("changepassword") != null){

				String password = ValidationUtils.validateParameter("password", req, true, null, 255, validationErrors);

				if(password != null){

					String passwordConfirmation = req.getParameter("passwordconfirmation");

					if(passwordConfirmation == null || !passwordConfirmation.equals(password)){

						validationErrors.add(new ValidationError("PasswordConfirmationMissMatch"));
					}
				}

				if(validationErrors.isEmpty()){

					mutableUser.setPassword(password);
					passwordChanged = true;
				}
			}

			AttributeDescriptorUtils.populateAttributes(attributeHandler, attributes, req, validationErrors);

			if(!validationErrors.isEmpty()){

				validationException = new ValidationException(validationErrors);

			}else{

				log.info("User " + user + " updating user profile, new values " + requestedUser);
				req.getSession(true).setAttribute("user", requestedUser);
				this.systemInterface.getUserHandler().updateUser(mutableUser, passwordChanged, false, true);

				userUpdated = true;
			}
		}

		log.info("User " + requestedUser + " viewing user profile");

		Document doc = this.createDocument(req, uriParser);
		Element updateUserElement = doc.createElement("UpdateUser");
		doc.getFirstChild().appendChild(updateUserElement);

		if(userUpdated){
			updateUserElement.appendChild(doc.createElement("UserUpdated"));
		}

		if(!mutable){
			XMLUtils.appendNewElement(doc, updateUserElement, "UserNotMutable");
		}

		XMLUtils.appendNewElement(doc, updateUserElement, "UsernameFieldMode", usernameFieldMode);
		XMLUtils.appendNewElement(doc, updateUserElement, "FirstnameFieldMode", firstnameFieldMode);
		XMLUtils.appendNewElement(doc, updateUserElement, "LastnameFieldMode", lastnameFieldMode);
		XMLUtils.appendNewElement(doc, updateUserElement, "EmailFieldMode", emailFieldMode);

		if(allowPasswordChanging){

			XMLUtils.appendNewElement(doc, updateUserElement, "AllowPasswordChanging");
		}

		XMLUtils.append(doc, updateUserElement, "AttrbuteDescriptors", this.attributes);

		Element userElement = requestedUser.toXML(doc);
		XMLUtils.append(doc, userElement, attributeHandler);
		updateUserElement.appendChild(userElement);

		if(validationException != null){
			updateUserElement.appendChild(validationException.toXML(doc));
			updateUserElement.appendChild(RequestUtils.getRequestParameters(req, doc));
		}

		return new SimpleForegroundModuleResponse(doc, userUpdated, this.moduleDescriptor.getName(), getDefaultBreadcrumb());
	}

	private boolean isPopulatable(FieldMode fieldMode) {

		return fieldMode == FieldMode.OPTIONAL || fieldMode == FieldMode.REQUIRED;
	}

	public Document createDocument(HttpServletRequest req, URIParser uriParser) {

		Document doc = XMLUtils.createDomDocument();
		Element document = doc.createElement("Document");
		document.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser));
		document.appendChild(this.moduleDescriptor.toXML(doc));
		doc.appendChild(document);
		return doc;
	}
}
