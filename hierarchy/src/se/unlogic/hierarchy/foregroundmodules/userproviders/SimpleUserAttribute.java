package se.unlogic.hierarchy.foregroundmodules.userproviders;

import se.unlogic.hierarchy.core.beans.SimpleAttribute;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Key;
import se.unlogic.standardutils.dao.annotations.ManyToOne;
import se.unlogic.standardutils.dao.annotations.Table;

@Table(name = "simple_user_attributes")
public class SimpleUserAttribute extends SimpleAttribute {

	private static final long serialVersionUID = 5772311959780159370L;
	
	@DAOManaged(columnName="userID")
	@Key
	@ManyToOne
	protected SimpleUser user;

	public SimpleUserAttribute() {

	}

	public SimpleUserAttribute(String name, String value) {

		super(name, value);
	}

	public SimpleUser getUser() {

		return user;
	}

	public void setUser(SimpleUser user) {

		this.user = user;
	}
}
