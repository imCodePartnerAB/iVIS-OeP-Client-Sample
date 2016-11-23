/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.beans;

import se.unlogic.hierarchy.core.enums.DataSourceType;
import se.unlogic.standardutils.xml.Elementable;

public interface DataSourceDescriptor extends Elementable{

	public abstract String getName();

	public abstract Integer getDataSourceID();

	public abstract String getUrl();
	
	public abstract String getDefaultCatalog();

	public abstract DataSourceType getType();

	public abstract boolean isEnabled();

	public abstract String getDriver();

	public abstract String getUsername();

	public abstract String getPassword();

	public abstract boolean removeAbandoned();

	public abstract Integer getRemoveTimeout();

	public abstract boolean testOnBorrow();

	public abstract String getValidationQuery();

	public abstract Integer getMaxActive();

	public abstract Integer getMaxIdle();

	public abstract Integer getMaxWait();

	public abstract boolean logAbandoned();

	public abstract Integer getMinIdle();

}
