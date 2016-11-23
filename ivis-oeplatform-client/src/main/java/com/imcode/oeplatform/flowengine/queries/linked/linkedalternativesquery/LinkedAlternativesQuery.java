package com.imcode.oeplatform.flowengine.queries.linked.linkedalternativesquery;

import com.imcode.oeplatform.flowengine.interfaces.LinkedMutableElement;
import com.imcode.oeplatform.flowengine.queries.linked.dropdownquery.LinkedDropDownAlternative;
import com.imcode.services.GenericService;
import com.nordicpeak.flowengine.interfaces.ImmutableAlternative;
import com.nordicpeak.flowengine.interfaces.Query;
import com.nordicpeak.flowengine.interfaces.QueryHandler;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;


public interface LinkedAlternativesQuery extends Query{

	public List<? extends LinkedDropDownAlternative> getAlternatives();

	public List<? extends LinkedAlternativesQueryInstance> getInstances(List<Integer> queryInstanceIDs, QueryHandler queryHandler) throws SQLException;

	String getEntityClassname();

	GenericService getEntityService();

	void setEntityService(GenericService entityService);

//	public String getFreeTextAlternative();
//
//	public Map<Integer,Integer> getAlternativeConversionMap();
}
