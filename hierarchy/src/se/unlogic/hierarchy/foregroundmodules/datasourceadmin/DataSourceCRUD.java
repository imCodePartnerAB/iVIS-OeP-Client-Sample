/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.datasourceadmin;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.unlogic.hierarchy.core.beans.Breadcrumb;
import se.unlogic.hierarchy.core.beans.SimpleDataSourceDescriptor;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.cache.DataSourceCache;
import se.unlogic.hierarchy.core.enums.DataSourceType;
import se.unlogic.hierarchy.core.enums.URLType;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.utils.IntegerBasedCRUD;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.context.ContextUtils;
import se.unlogic.standardutils.dao.CRUDDAO;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.webutils.http.BeanRequestPopulator;
import se.unlogic.webutils.http.URIParser;

public class DataSourceCRUD extends IntegerBasedCRUD<SimpleDataSourceDescriptor,DataSourceAdminModule> {

	private final DataSourceCache dataSourceCache;

	public DataSourceCRUD(CRUDDAO<SimpleDataSourceDescriptor, Integer> crudDAO, BeanRequestPopulator<SimpleDataSourceDescriptor> populator, String typeElementName, String typeLogName,	DataSourceAdminModule adminModule, DataSourceCache dataSourceCache) {
		super(crudDAO, populator, typeElementName, typeLogName, "", adminModule);
		this.dataSourceCache = dataSourceCache;
	}

	@Override
	protected ForegroundModuleResponse beanUpdated(SimpleDataSourceDescriptor bean, HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		if(bean.isEnabled()){
			this.dataSourceCache.update(bean);
		}else{
			this.dataSourceCache.stop(bean.getDataSourceID());
		}

		return super.beanUpdated(bean, req, res, user, uriParser);
	}



	@Override
	protected void validateAddPopulation(SimpleDataSourceDescriptor bean, HttpServletRequest req, User user, URIParser uriParser) throws ValidationException, SQLException, Exception {

		if(bean.isEnabled()) {
			if(bean.getType().equals(DataSourceType.ContainerManaged) && !ContextUtils.isBound(bean.getUrl())) {
				throw new ValidationException(new ValidationError("nameNotBound"));
			}
			else if(!ReflectionUtils.isAvailable(bean.getDriver())) {
				throw new ValidationException(new ValidationError("driverNotAvailable"));
			}
		}
	}

	@Override
	protected void validateUpdatePopulation(SimpleDataSourceDescriptor bean, HttpServletRequest req, User user, URIParser uriParser) throws ValidationException, SQLException, Exception {

		if(bean.isEnabled()) {
			if(bean.getType().equals(DataSourceType.ContainerManaged) && !ContextUtils.isBound(bean.getUrl())) {
				throw new ValidationException(new ValidationError("nameNotBound"));
			}
			else if(!ReflectionUtils.isAvailable(bean.getDriver())) {
				throw new ValidationException(new ValidationError("driverNotAvailable"));
			}
		}
	}

	@Override
	protected ForegroundModuleResponse beanDeleted(SimpleDataSourceDescriptor bean, HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		this.dataSourceCache.delete(bean.getDataSourceID());

		return super.beanDeleted(bean, req, res, user, uriParser);
	}

	@Override
	protected List<Breadcrumb> getAddBreadcrumbs(HttpServletRequest req, User user, URIParser uriParser) {

		return CollectionUtils.getList(this.callback.getDefaultBreadcrumb(),new Breadcrumb(callback.getAddDataSourceBreadcrumbText(), callback.getAddDataSourceBreadcrumbText(), uriParser.getFormattedURI(), URLType.RELATIVE_FROM_CONTEXTPATH));
	}

	@Override
	protected List<Breadcrumb> getUpdateBreadcrumbs(SimpleDataSourceDescriptor bean, HttpServletRequest req, User user, URIParser uriParser) {

		return CollectionUtils.getList(this.callback.getDefaultBreadcrumb(),new Breadcrumb(callback.getUpdateDataSourceBreadcrumbText() + bean.getName(), callback.getUpdateDataSourceBreadcrumbText() + bean.getName(), uriParser.getFormattedURI(), URLType.RELATIVE_FROM_CONTEXTPATH));
	}

	@Override
	public ForegroundModuleResponse list(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, List<ValidationError> validationErrors) throws Exception {

		return callback.list(req, res, user, uriParser, validationErrors);
	}
}
