package se.unlogic.hierarchy.foregroundmodules.groupadmin;

import java.lang.reflect.Field;

import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Key;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.xml.GeneratedElementable;
import se.unlogic.standardutils.xml.XMLElement;

@Table(name = "group_administrators")
public class GroupAdminMapping extends GeneratedElementable {

	public static final Field GROUP_FIELD = ReflectionUtils.getField(GroupAdminMapping.class, "group");
	public static final Field USER_FIELD = ReflectionUtils.getField(GroupAdminMapping.class, "user");

	@DAOManaged(columnName = "groupID")
	@Key
	@XMLElement
	private Group group;

	@DAOManaged(columnName = "userID")
	@Key
	@XMLElement
	private User user;

	public GroupAdminMapping() {}

	public GroupAdminMapping(Group group, User user) {

		super();
		this.group = group;
		this.user = user;
	}

	public Group getGroup() {

		return group;
	}

	public void setGroup(Group group) {

		this.group = group;
	}

	public User getUser() {

		return user;
	}

	public void setUser(User user) {

		this.user = user;
	}
}
