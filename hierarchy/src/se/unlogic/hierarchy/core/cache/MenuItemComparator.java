/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.cache;

import java.util.Comparator;

import se.unlogic.hierarchy.core.beans.MenuItem;

public class MenuItemComparator implements Comparator<MenuItem> {

	@Override
	public int compare(MenuItem m1, MenuItem m2) {
		
		return m1.getMenuIndex().compareTo(m2.getMenuIndex());
	}
}
