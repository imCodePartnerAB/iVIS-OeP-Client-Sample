package se.unlogic.hierarchy.core.utils;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.handlers.GroupHandler;
import se.unlogic.hierarchy.core.handlers.UserHandler;
import se.unlogic.hierarchy.core.interfaces.SystemInterface;
import se.unlogic.hierarchy.core.populators.GroupQueryPopulator;
import se.unlogic.hierarchy.core.populators.GroupTypePopulator;
import se.unlogic.hierarchy.core.populators.UserQueryPopulator;
import se.unlogic.hierarchy.core.populators.UserTypePopulator;
import se.unlogic.standardutils.dao.SimpleAnnotatedDAOFactory;
import se.unlogic.standardutils.populators.BeanStringPopulator;
import se.unlogic.standardutils.populators.QueryParameterPopulator;


public class HierarchyAnnotatedDAOFactory extends SimpleAnnotatedDAOFactory {

	public HierarchyAnnotatedDAOFactory(DataSource dataSource, SystemInterface systemInterface) {

		this(dataSource, systemInterface.getUserHandler(), systemInterface.getGroupHandler());
	}

	public HierarchyAnnotatedDAOFactory(DataSource dataSource, UserHandler userHandler, GroupHandler groupHandler){

		this(dataSource, userHandler, groupHandler, false, false, false);
	}
	
	public HierarchyAnnotatedDAOFactory(DataSource dataSource, UserHandler userHandler, GroupHandler groupHandler, boolean userGroups, boolean userAttributes, boolean groupAttributes){

		super(dataSource);

		List<QueryParameterPopulator<?>> queryParameterPopulators = new ArrayList<QueryParameterPopulator<?>>(2);

		queryParameterPopulators.add(UserQueryPopulator.POPULATOR);
		queryParameterPopulators.add(GroupQueryPopulator.POPULATOR);

		this.queryParameterPopulators = queryParameterPopulators;

		List<BeanStringPopulator<?>> typePopulators = new ArrayList<BeanStringPopulator<?>>(2);

		typePopulators.add(new UserTypePopulator(userHandler, userGroups, userAttributes));
		typePopulators.add(new GroupTypePopulator(groupHandler, groupAttributes));

		this.beanStringPopulators = typePopulators;
	}	
}
