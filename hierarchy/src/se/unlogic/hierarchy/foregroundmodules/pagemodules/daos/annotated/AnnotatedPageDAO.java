/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.pagemodules.daos.annotated;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import se.unlogic.hierarchy.foregroundmodules.pagemodules.Page;
import se.unlogic.hierarchy.foregroundmodules.pagemodules.daos.PageDAO;
import se.unlogic.standardutils.dao.AnnotatedDAO;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.QueryParameter;
import se.unlogic.standardutils.dao.QueryParameterFactory;
import se.unlogic.standardutils.dao.SimpleAnnotatedDAOFactory;


public class AnnotatedPageDAO extends AnnotatedDAO<Page> implements PageDAO {

	private QueryParameterFactory<Page, Integer> pageIDParamFactory;
	private QueryParameterFactory<Page, Integer> sectionIDParamFactory;
	private QueryParameterFactory<Page, String> aliasParamFactory;
	private QueryParameter<Page, Boolean> enabledParameter;
	private QueryParameter<Page, Boolean> visibleInMenuParameter;

	public AnnotatedPageDAO(DataSource dataSource) {

		super(dataSource, Page.class, new SimpleAnnotatedDAOFactory(dataSource));

		pageIDParamFactory = this.getParamFactory("pageID", Integer.class);
		sectionIDParamFactory = this.getParamFactory("sectionID", Integer.class);
		aliasParamFactory = this.getParamFactory("alias", String.class);
		enabledParameter = this.getParamFactory("enabled", boolean.class).getParameter(true);
		visibleInMenuParameter = this.getParamFactory("visibleInMenu", boolean.class).getParameter(true);
	}

	@Override
	public List<Page> getEnabledPages(int sectionID) throws SQLException {

		HighLevelQuery<Page> query = new HighLevelQuery<Page>();

		query.addParameter(enabledParameter);
		query.addParameter(sectionIDParamFactory.getParameter(sectionID));

		return this.getAll(query);
	}

	@Override
	public Page getPage(int pageID, int sectionID) throws SQLException {

		HighLevelQuery<Page> query = new HighLevelQuery<Page>();

		query.addParameter(pageIDParamFactory.getParameter(pageID));
		query.addParameter(sectionIDParamFactory.getParameter(sectionID));

		return this.get(query);
	}

	@Override
	public Page getPage(int pageID) throws SQLException {

		HighLevelQuery<Page> query = new HighLevelQuery<Page>();

		query.addParameter(pageIDParamFactory.getParameter(pageID));

		return this.get(query);
	}

	@Override
	public Page getPage(String alias, int sectionID) throws SQLException {

		HighLevelQuery<Page> query = new HighLevelQuery<Page>();

		query.addParameter(aliasParamFactory.getParameter(alias));
		query.addParameter(sectionIDParamFactory.getParameter(sectionID));

		return this.get(query);
	}

	@Override
	public List<Page> getPages(int sectionID) throws SQLException {

		HighLevelQuery<Page> query = new HighLevelQuery<Page>();

		query.addParameter(sectionIDParamFactory.getParameter(sectionID));

		return this.getAll(query);
	}

	@Override
	public List<Page> getVisibleEnabledPages(int sectionID) throws SQLException {

		HighLevelQuery<Page> query = new HighLevelQuery<Page>();

		query.addParameter(enabledParameter);
		query.addParameter(visibleInMenuParameter);
		query.addParameter(sectionIDParamFactory.getParameter(sectionID));

		return this.getAll(query);
	}

	@Override
	public boolean sectionHasEnabledPages(int sectionID) throws SQLException {

		HighLevelQuery<Page> query = new HighLevelQuery<Page>();

		query.addParameter(sectionIDParamFactory.getParameter(sectionID));
		query.addParameter(enabledParameter);

		return this.getBoolean(query);
	}
}
