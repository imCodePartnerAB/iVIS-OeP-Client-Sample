package se.unlogic.hierarchy.core.beans;

import java.sql.Timestamp;
import java.util.List;

import se.unlogic.hierarchy.core.interfaces.MutableAttributeHandler;
import se.unlogic.standardutils.i18n.Language;

public abstract class MutableUser extends User{

	private static final long serialVersionUID = -2835724310469931136L;

	public abstract void setUserID(Integer userID);

	public abstract void setGroups(List<Group> groups);

	public abstract void setLastLogin(Timestamp lastLogin);

	public abstract void setEmail(String email);

	public abstract void setFirstname(String firstname);

	public abstract void setLastname(String lastname);

	public abstract void setPassword(String password);

	public abstract void setUsername(String username);

	public abstract void setLanguage(Language language);

	public abstract void setAdmin(boolean admin);

	public abstract void setEnabled(boolean enabled);

	/**
	 * @return A {@link MutableAttributeHandler} or null if the current implementation does not support this feature.
	 */
	@Override
	public MutableAttributeHandler getAttributeHandler(){

		return null;
	}
}