package com.nordicpeak.flowengine.interfaces;

import java.util.List;


public interface MultiSigningQuery extends QueryInstance {

	List<? extends SigningParty> getSigningParties();
	
}
