package se.unlogic.hierarchy.foregroundmodules.userproviders;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.emailutils.populators.EmailPopulator;
import se.unlogic.hierarchy.core.annotations.CheckboxSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextAreaSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.beans.AttributeDescriptor;
import se.unlogic.hierarchy.core.beans.MutableUser;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.beans.UserTypeDescriptor;
import se.unlogic.hierarchy.core.exceptions.ModuleConfigurationException;
import se.unlogic.hierarchy.core.handlers.GroupHandler;
import se.unlogic.hierarchy.core.handlers.UserHandler;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.UserFormCallback;
import se.unlogic.hierarchy.core.interfaces.UserFormProvider;
import se.unlogic.hierarchy.core.interfaces.ViewFragment;
import se.unlogic.hierarchy.core.utils.AttributeDescriptorUtils;
import se.unlogic.hierarchy.core.utils.GenericFormCRUD;
import se.unlogic.hierarchy.core.utils.SimpleViewFragmentTransformer;
import se.unlogic.hierarchy.foregroundmodules.userproviders.cruds.UserFormCRUD;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.webutils.http.BeanRequestPopulator;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.populators.annotated.AnnotatedRequestPopulator;


public abstract class AnnotatedMutableUserFormProviderModule<UserType extends MutableUser> extends AnnotatedMutableUserProviderModule<UserType> implements UserFormProvider{

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Form XSL stylesheet", description = "The path in classpath relative from this class to the XSL stylesheet used to transform the add and update forms", required = true)
	protected String formStyleSheet = getDefaultFormStyleSheet();

	@ModuleSetting
	@CheckboxSettingDescriptor(name="Include debug data",description="Controls whether or not debug data should be included in the view fragments objects")
	protected boolean includeDebugData = false;

	@ModuleSetting
	@CheckboxSettingDescriptor(name="List as addable user type", description="Controls if this user type is listed as a form addable user type in the user handler")
	protected boolean listAsAddableType = true;

	@ModuleSetting
	@TextFieldSettingDescriptor(name="User type name", description="The name of this user type")
	protected String userTypeName = userClass.getSimpleName();

	@ModuleSetting(allowsNull = true)
	@TextAreaSettingDescriptor(name = "Supported attributes", description = "The attributes to show in the form. The format is [name][*/!]:[display name]:[max length]:[StringFormatValidator] (without brackets). Only the name is required. The * sign indicates if the attribute is required or not. The ! sign indicates that the attribute is read only")
	protected String supportedAttributes;

	protected List<AttributeDescriptor> attributes;

	protected UserTypeDescriptor userTypeDescriptor;

	protected GenericFormCRUD<UserType, User, ?, UserFormCallback> userFormCRUD;


	public AnnotatedMutableUserFormProviderModule(Class<UserType> userClass) {

		super(userClass);
	}

	protected abstract String getDefaultFormStyleSheet();

	@Override
	protected void moduleConfigured() throws Exception{

		super.moduleConfigured();

		userFormCRUD = createUserFormCRUD();

		this.userTypeDescriptor = new UserTypeDescriptor(userClass.getSimpleName().toString() + "-" + this.moduleDescriptor.getModuleID(), userTypeName);

		attributes = AttributeDescriptorUtils.parseAttributes(supportedAttributes);
	}

	protected GenericFormCRUD<UserType, User, ?, UserFormCallback> createUserFormCRUD() {

		SimpleViewFragmentTransformer fragmentTransformer;

		try{
			fragmentTransformer = new SimpleViewFragmentTransformer(formStyleSheet, systemInterface.getEncoding(), this.getClass(), moduleDescriptor, sectionInterface);

		}catch(Exception e){

			log.error("Unable to parse XSL stylesheet for user forms in module " + this.moduleDescriptor,e);
			return null;
		}

		fragmentTransformer.setDebugXML(this.includeDebugData);

		BeanRequestPopulator<UserType> populator = getPopulator();

		return new UserFormCRUD<UserType, AnnotatedMutableUserFormProviderModule<UserType>>(populator, "User", "user", this, fragmentTransformer);
	}

	protected BeanRequestPopulator<UserType> getPopulator() {

		return new AnnotatedRequestPopulator<UserType>(userClass, new EmailPopulator());
	}

	@Override
	public UserTypeDescriptor getAddableUserType() {

		if(listAsAddableType){

			return userTypeDescriptor;
		}

		return null;
	}

	public void updateUser(UserType user) throws SQLException{

		updateUser(user, true, true, true);
	}

	public GroupHandler getGroupHandler(){

		return systemInterface.getGroupHandler();
	}

	public UserHandler getUserHandler(){

		return systemInterface.getUserHandler();
	}

	@Override
	public ViewFragment getAddForm(HttpServletRequest req, User user, URIParser uriParser, ValidationException validationException, UserFormCallback callback) throws Exception {

		checkCRUD();

		return userFormCRUD.showAddForm(req, user, uriParser, validationException, callback);
	}

	@Override
	@SuppressWarnings("unchecked")
	public ViewFragment getUpdateForm(User bean, HttpServletRequest req, User user, URIParser uriParser, ValidationException validationException, UserFormCallback callback) throws Exception {

		checkCRUD();

		return userFormCRUD.showUpdateForm((UserType)bean, req, user, uriParser, validationException, callback);
	}

	@Override
	public User populate(HttpServletRequest req, User user, URIParser uriParser, UserFormCallback callback) throws Exception {

		checkCRUD();

		return userFormCRUD.populateFromAddRequest(req, user, uriParser, callback);
	}

	@Override
	@SuppressWarnings("unchecked")
	public User populate(User bean, HttpServletRequest req, User user, URIParser uriParser, UserFormCallback callback) throws Exception {

		checkCRUD();

		return userFormCRUD.populateFromUpdateRequest((UserType)bean, req, user, uriParser, callback);
	}

	@Override
	@SuppressWarnings("unchecked")
	public ViewFragment getBeanView(User requestedUser, HttpServletRequest req, User user, URIParser uriParser, UserFormCallback callback) throws Exception {

		checkCRUD();

		return userFormCRUD.showBean((UserType)requestedUser, req, user, uriParser, null, callback);
	}

	protected void checkCRUD() throws ModuleConfigurationException {

		if(userFormCRUD == null){

			throw new ModuleConfigurationException("Module is not properly configured, check module settings");
		}
	}

	public List<AttributeDescriptor> getSupportedAttributes(){

		return attributes;
	}

	@Override
	public void add(User user, UserFormCallback callback) throws Exception {

		addUser(user);
	}

	@Override
	public void update(User user, UserFormCallback callback) throws Exception {

		updateUser(user, false, callback.allowGroupAdministration(), true);
	}

	public ForegroundModuleDescriptor getModuleDescriptor(){

		return moduleDescriptor;
	}

	public boolean requirePasswordOnAdd(){

		return true;
	}
}
