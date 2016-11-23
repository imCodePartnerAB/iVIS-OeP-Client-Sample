/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.interfaces;

import java.util.ArrayList;
import java.util.List;

import se.unlogic.hierarchy.core.beans.Breadcrumb;
import se.unlogic.hierarchy.core.beans.SectionMenu;

public interface ForegroundModuleResponse extends ModuleResponse {

	public void setMenu(SectionMenu sectionMenu);
	
	public SectionMenu getMenu();

	String getTitle();

	ArrayList<Breadcrumb> getBreadcrumbs();

	void setModuleDescriptor(ForegroundModuleDescriptor moduleDescriptor);

	void addBreadcrumbFirst(Breadcrumb breadcrumb);

	void addBackgroundModuleResponses(List<BackgroundModuleResponse> backgroundModuleResponses);

	List<BackgroundModuleResponse> getBackgroundModuleResponses();

	boolean isUserChanged();

	void excludeSystemTransformation(boolean excludeSystemTransformation);
	
	boolean isExcludeSystemTransformation();
	
	boolean isExcludeSectionBreadcrumbs();
	
}
