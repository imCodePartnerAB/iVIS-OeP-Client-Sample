package se.unlogic.hierarchy.core.beans;

import se.unlogic.standardutils.xml.GeneratedElementable;
import se.unlogic.standardutils.xml.XMLElement;

@XMLElement
public class UserTypeDescriptor extends GeneratedElementable implements Comparable<UserTypeDescriptor>{

	@XMLElement(fixCase=true)
	private final String userTypeID;

	@XMLElement(fixCase=true)
	private final String name;

	public UserTypeDescriptor(String userTypeID, String name) {

		if(userTypeID == null){
			
			throw new NullPointerException("userTypeID cannot ben null");
			
		}else if(name == null){
			
			throw new NullPointerException("name cannot ben null");
		}
		
		this.userTypeID = userTypeID;
		this.name = name;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((userTypeID == null) ? 0 : userTypeID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		UserTypeDescriptor other = (UserTypeDescriptor) obj;
		if (userTypeID == null) {
			if (other.userTypeID != null) {
				return false;
			}
		} else if (!userTypeID.equals(other.userTypeID)) {
			return false;
		}
		return true;
	}

	public String getUserTypeID() {

		return userTypeID;
	}

	public String getName() {

		return name;
	}
	
	@Override
	public String toString() {

		return name + " (ID: " + userTypeID + ")";
	}

	@Override
	public int compareTo(UserTypeDescriptor o) {

		return this.name.compareTo(o.name);
	}	
}
