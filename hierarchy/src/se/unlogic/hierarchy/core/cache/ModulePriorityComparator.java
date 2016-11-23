/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.cache;

import java.util.Comparator;

import se.unlogic.hierarchy.core.beans.ModuleMapping;
import se.unlogic.hierarchy.core.interfaces.MultipleAliasModuleDescriptor;

public class ModulePriorityComparator implements Comparator<ModuleMapping<? extends MultipleAliasModuleDescriptor>> {

	@Override
	public int compare(ModuleMapping<? extends MultipleAliasModuleDescriptor> o1, ModuleMapping<? extends MultipleAliasModuleDescriptor> o2) {
		
		int value1 = o1.getModuleDescriptor().getPriority();
		int value2 = o2.getModuleDescriptor().getPriority();
		
		return (value1<value2 ? -1 : (value1==value2 ? 0 : 1));
	}


}
