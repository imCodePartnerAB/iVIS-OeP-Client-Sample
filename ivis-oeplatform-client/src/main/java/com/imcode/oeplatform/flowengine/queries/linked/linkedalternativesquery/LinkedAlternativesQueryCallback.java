package com.imcode.oeplatform.flowengine.queries.linked.linkedalternativesquery;

import java.sql.SQLException;
import java.util.List;


public interface LinkedAlternativesQueryCallback<Q extends LinkedAlternativesQuery> {

	List<? extends LinkedAlternativesQueryInstance> getQueryInstances(Q query, List<Integer> queryInstanceIDs) throws SQLException;
}
