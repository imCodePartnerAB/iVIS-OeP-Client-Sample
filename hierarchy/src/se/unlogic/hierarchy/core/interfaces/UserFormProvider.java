package se.unlogic.hierarchy.core.interfaces;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.beans.UserTypeDescriptor;



public interface UserFormProvider extends AddForm<User, UserFormCallback>, UpdateForm<User, UserFormCallback>, BeanViewer<User, UserFormCallback> {

	/**
	 * @return The {@link UserTypeDescriptor} descriptor for the user type that can added via a web form using this user provider or null if this user provider does not support adding users via web forms.
	 */
	public UserTypeDescriptor getAddableUserType();

	public void add(User user, UserFormCallback callback) throws Exception;

	public void update(User user, UserFormCallback callback) throws Exception;
}
