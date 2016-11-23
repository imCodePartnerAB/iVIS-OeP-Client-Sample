package se.unlogic.hierarchy.foregroundmodules.test;

import java.sql.Timestamp;
import java.util.Collection;

import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.standardutils.i18n.Language;


public class ImmutableUser extends User {

	private static final long serialVersionUID = -740237756077874340L;

	@Override
	public boolean isAdmin() {

		return false;
	}

	@Override
	public String getEmail() {

		return "john.immutable@test";
	}

	@Override
	public String getFirstname() {

		return "John";
	}

	@Override
	public Timestamp getCurrentLogin() {

		return null;
	}

	@Override
	public Timestamp getLastLogin() {

		return null;
	}

	@Override
	public String getLastname() {

		return "Johnsson";
	}

	@Override
	public String getPassword() {

		return "213";
	}

	@Override
	public String getUsername() {

		return "john";
	}

	@Override
	public Integer getUserID() {

		return 55566655;
	}

	@Override
	public boolean isEnabled() {

		return true;
	}

	@Override
	public Timestamp getAdded() {

		return new Timestamp(System.currentTimeMillis());
	}

	@Override
	public Collection<Group> getGroups() {

		return null;
	}

	@Override
	public Language getLanguage() {

		return null;
	}

	@Override
	public String getPreferedDesign() {

		return null;
	}

	@Override
	public void setCurrentLogin(Timestamp currentLogin) {}

	@Override
	public boolean hasFormProvider() {

		return false;
	}
}
