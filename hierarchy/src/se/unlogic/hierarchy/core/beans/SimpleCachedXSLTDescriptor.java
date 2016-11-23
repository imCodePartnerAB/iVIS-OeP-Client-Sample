/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.beans;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;

import se.unlogic.hierarchy.core.interfaces.CachedXSLTDescriptor;
import se.unlogic.standardutils.i18n.Language;
import se.unlogic.standardutils.xsl.XSLTransformer;


public class SimpleCachedXSLTDescriptor implements CachedXSLTDescriptor {

	private XSLTransformer cachedXSLT;
	private Language language;
	private String name;
	private boolean isDefault;
	private boolean useFullMenu;

	public SimpleCachedXSLTDescriptor(XSLTransformer cachedXSLT, Language language, String name, boolean isDefault, boolean useFullMenu) {

		super();
		this.cachedXSLT = cachedXSLT;
		this.language = language;
		this.name = name;
		this.isDefault = isDefault;
		this.setUseFullMenu(useFullMenu);
	}

	@Override
	public XSLTransformer getCachedXSLT() {

		return cachedXSLT;
	}

	public void setCachedXSLT(XSLTransformer cachedXSLT) {

		this.cachedXSLT = cachedXSLT;
	}

	@Override
	public Language getLanguage() {

		return language;
	}

	public void setLanguage(Language language) {

		this.language = language;
	}

	@Override
	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	@Override
	public boolean isDefault() {

		return isDefault;
	}

	public void setDefault(boolean isdefault) {

		this.isDefault = isdefault;
	}

	@Override
	public Transformer getTransformer() throws TransformerConfigurationException {

		return cachedXSLT.getTransformer();
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((language == null) ? 0 : language.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SimpleCachedXSLTDescriptor other = (SimpleCachedXSLTDescriptor) obj;
		if (language == null) {
			if (other.language != null) {
				return false;
			}
		} else if (!language.equals(other.language)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString(){

		return name + " in " + language.toString() + " (wrapping " + cachedXSLT + ")";
	}

	@Override
	public boolean usesFullMenu() {

		return useFullMenu;
	}

	public void setUseFullMenu(boolean useFullMenu) {

		this.useFullMenu = useFullMenu;
	}
}
