/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.interfaces;

import se.unlogic.hierarchy.core.enums.HTTPProtocol;
import se.unlogic.standardutils.xml.Elementable;

public interface ForegroundModuleDescriptor extends VisibleModuleDescriptor, Elementable {
	
	boolean isVisibleInMenu();
	
	String getAlias();
	
	String getDescription();
	
	HTTPProtocol getRequiredProtocol();
}
