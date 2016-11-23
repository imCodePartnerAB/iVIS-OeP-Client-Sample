package se.unlogic.hierarchy.foregroundmodules.test;

import se.unlogic.hierarchy.core.beans.Group;


public class ImmutableGroup extends Group {

	private static final long serialVersionUID = -3528084670107719679L;

	@Override
	public Integer getGroupID() {

		return 999;
	}

	@Override
	public String getName() {

		return "Immutable group";
	}

	@Override
	public String getDescription() {

		return "You can touch this group";
	}

	@Override
	public boolean isEnabled() {

		return true;
	}
}
