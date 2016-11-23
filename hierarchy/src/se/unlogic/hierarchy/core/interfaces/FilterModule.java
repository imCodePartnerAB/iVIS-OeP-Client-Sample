package se.unlogic.hierarchy.core.interfaces;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.webutils.http.URIParser;


public interface FilterModule extends Module<FilterModuleDescriptor>{

	void init(FilterModuleDescriptor moduleDescriptor, SystemInterface systemInterface, DataSource dataSource) throws Exception;	
	public void doFilter(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, FilterChain filterChain) throws Exception;
}
