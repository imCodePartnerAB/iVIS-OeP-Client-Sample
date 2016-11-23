package se.unlogic.hierarchy.foregroundmodules.groupproviders;

import se.unlogic.hierarchy.core.beans.SimpleAttribute;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Key;
import se.unlogic.standardutils.dao.annotations.ManyToOne;
import se.unlogic.standardutils.dao.annotations.Table;

@Table(name = "simple_group_attributes")
public class SimpleGroupAttribute extends SimpleAttribute {

	private static final long serialVersionUID = 5772311959780159370L;
	
	@DAOManaged(columnName="groupID")
	@Key
	@ManyToOne
	protected SimpleGroup group;

	public SimpleGroupAttribute() {}

	public SimpleGroupAttribute(String name, String value) {

		super(name, value);
	}

	public SimpleGroup getGroup() {

		return group;
	}

	public void setUser(SimpleGroup group) {

		this.group = group;
	}
}
