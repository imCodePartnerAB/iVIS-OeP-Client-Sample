/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.filtermodules;

import javax.sql.DataSource;

import se.unlogic.hierarchy.basemodules.BaseModule;
import se.unlogic.hierarchy.core.interfaces.FilterModule;
import se.unlogic.hierarchy.core.interfaces.FilterModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SystemInterface;


public abstract class SimpleFilterModule extends BaseModule<FilterModuleDescriptor> implements FilterModule{
	
	protected SystemInterface systemInterface;
	
	@Override
	public void init(FilterModuleDescriptor moduleDescriptor, SystemInterface systemInterface, DataSource dataSource) throws Exception {

		this.moduleDescriptor = moduleDescriptor;
		this.systemInterface = systemInterface;
		this.dataSource = dataSource;
		
		this.createDAOs(dataSource);
	}	
}
