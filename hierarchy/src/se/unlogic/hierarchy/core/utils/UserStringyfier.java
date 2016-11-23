package se.unlogic.hierarchy.core.utils;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.standardutils.string.Stringyfier;


public class UserStringyfier implements Stringyfier<User> {

	private boolean displayFullName;

	public UserStringyfier(boolean displayFullName) {

		super();
		this.displayFullName = displayFullName;
	}

	@Override
	public String format(User user) {

		if(user == null){
			return null;
		}

		if(displayFullName){

			return user.getFirstname() + " " + user.getLastname();

		}else{

			return user.getUsername();
		}
	}


	public boolean isDisplayFullName() {

		return displayFullName;
	}


	public void setDisplayFullName(boolean displayFullName) {

		this.displayFullName = displayFullName;
	}
}
