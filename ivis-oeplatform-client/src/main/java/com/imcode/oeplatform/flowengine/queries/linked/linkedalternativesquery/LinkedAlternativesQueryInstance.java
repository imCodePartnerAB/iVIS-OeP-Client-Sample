package com.imcode.oeplatform.flowengine.queries.linked.linkedalternativesquery;

import com.imcode.oeplatform.flowengine.interfaces.LinkedMutableElement;
import com.imcode.oeplatform.flowengine.queries.linked.dropdownquery.LinkedDropDownAlternative;
import com.nordicpeak.flowengine.interfaces.ImmutableAlternative;
import com.nordicpeak.flowengine.interfaces.QueryInstance;

import java.util.List;


public interface LinkedAlternativesQueryInstance extends QueryInstance{

	/**
	 * @return the alternatives that have been selected
	 */
	List<? extends LinkedDropDownAlternative> getAlternatives();

//	String getEntityClassname();
}
