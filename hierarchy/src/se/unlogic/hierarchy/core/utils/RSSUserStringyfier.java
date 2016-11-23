package se.unlogic.hierarchy.core.utils;

import se.unlogic.hierarchy.core.beans.User;


public class RSSUserStringyfier extends UserStringyfier {

	public RSSUserStringyfier(boolean displayFullName) {

		super(displayFullName);
	}

	@Override
	public String format(User user) {

		String output = super.format(user);

		if(output != null && user.getEmail() != null){

			return user.getEmail() + "(" + output + ")";
		}

		return output;
	}
}
