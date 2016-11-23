package se.unlogic.hierarchy.foregroundmodules.groupproviders;

import java.util.List;

import se.unlogic.hierarchy.core.beans.Group;

public abstract class BaseGroupProviderModule<GroupType extends SimpleGroup> extends AnnotatedMutableGroupProviderModule<GroupType> {

	public BaseGroupProviderModule(Class<GroupType> groupClass) {

		super(groupClass);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean isProviderFor(Group group) {

		if (groupClass.equals(group.getClass()) && ((GroupType) group).getProviderID() != null && ((GroupType) group).getProviderID().equals(this.moduleDescriptor.getModuleID())) {

			return true;
		}

		return false;
	}

	@Override
	protected GroupType setGroupProviderID(GroupType group) {

		if (group != null) {

			group.setProviderID(this.moduleDescriptor.getModuleID());
		}

		return group;
	}

	@Override
	protected List<GroupType> setGroupProviderID(List<GroupType> groups) {

		if (groups != null) {

			for (GroupType group : groups) {

				group.setProviderID(this.moduleDescriptor.getModuleID());
			}
		}

		return groups;
	}
	
}
