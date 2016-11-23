/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.systemadmin;

import java.util.Comparator;

public class ModuleInfoBeanComparator implements Comparator<ModuleInfoBean> {

	@Override
	public int compare(ModuleInfoBean m1, ModuleInfoBean m2) {
		
		return String.CASE_INSENSITIVE_ORDER.compare(m1.getModuleBean().getName(), m2.getModuleBean().getName());
	}

}
