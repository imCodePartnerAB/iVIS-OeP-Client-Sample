/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.usersessionadmin;

import java.util.Comparator;

import se.unlogic.hierarchy.core.beans.User;

public class UserNameComparator implements Comparator<User>{

	@Override
	public int compare(User user1, User user2) {
		
		//Check firstname
		int returnValue = user1.getFirstname().compareTo(user2.getFirstname());
		
		if(returnValue != 0){
			return returnValue;
		}
		
		//Check lastname
		returnValue = user1.getLastname().compareTo(user2.getLastname());
		
		if(returnValue != 0){
			return returnValue;
		}
		
		//Check username
		returnValue = user1.getUsername().compareTo(user2.getUsername());
		
		return returnValue;		
	}
}
