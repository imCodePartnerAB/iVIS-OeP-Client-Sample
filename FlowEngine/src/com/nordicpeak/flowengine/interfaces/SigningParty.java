package com.nordicpeak.flowengine.interfaces;

import se.unlogic.standardutils.xml.Elementable;


public interface SigningParty extends Elementable{

	String getEmail();

	String getName();
	
	String getSocialSecurityNumber();
	
}
