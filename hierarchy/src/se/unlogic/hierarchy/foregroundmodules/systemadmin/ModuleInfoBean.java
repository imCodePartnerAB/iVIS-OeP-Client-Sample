/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.systemadmin;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.enums.ModuleType;
import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;
import se.unlogic.standardutils.xml.Elementable;
import se.unlogic.standardutils.xml.XMLUtils;

public class ModuleInfoBean implements Elementable{

	private ModuleDescriptor moduleDescriptor;
	private boolean inDatabase;
	private boolean cached;
	private ModuleType moduleType;

	public ModuleDescriptor getModuleBean() {

		return moduleDescriptor;
	}

	public void setModuleBean(ModuleDescriptor moduleDescriptorBean) {

		this.moduleDescriptor = moduleDescriptorBean;
	}

	public boolean isInDatabase() {

		return inDatabase;
	}

	public void setInDatabase(boolean inDatabase) {

		this.inDatabase = inDatabase;
	}

	public boolean isCached() {

		return cached;
	}

	public void setCached(boolean cached) {

		this.cached = cached;
	}

	public ModuleType getModuleType() {

		return moduleType;
	}

	public void setModuleType(ModuleType moduleType) {

		this.moduleType = moduleType;
	}

	@Override
	public Element toXML(Document doc) {

		Element moduleInfo = this.moduleDescriptor.toXML(doc);

		moduleInfo.appendChild(XMLUtils.createElement("inDatabase", Boolean.toString(this.inDatabase), doc));
		moduleInfo.appendChild(XMLUtils.createElement("cached", Boolean.toString(this.cached), doc));
		moduleInfo.appendChild(XMLUtils.createElement("moduleType", moduleType.toString(), doc));
		
		return moduleInfo;
	}
	
	public static Element toXML(Document doc, ModuleDescriptor moduleDescriptor, ModuleType moduleType, boolean inDB, boolean cached) {

		Element moduleInfo = moduleDescriptor.toXML(doc);

		moduleInfo.appendChild(XMLUtils.createElement("inDatabase", Boolean.toString(inDB), doc));
		moduleInfo.appendChild(XMLUtils.createElement("cached", Boolean.toString(cached), doc));
		moduleInfo.appendChild(XMLUtils.createElement("moduleType", moduleType.toString(), doc));
		
		return moduleInfo;
	}	
}
