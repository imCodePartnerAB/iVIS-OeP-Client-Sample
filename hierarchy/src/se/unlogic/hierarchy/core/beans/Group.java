/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.beans;

import java.io.Serializable;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.interfaces.AttributeHandler;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.xml.Elementable;
import se.unlogic.standardutils.xml.XMLUtils;

public abstract class Group implements Serializable, Elementable {

	private static final long serialVersionUID = -3034316839387156132L;

	public abstract Integer getGroupID();

	public abstract String getName();

	public abstract String getDescription();

	public abstract boolean isEnabled();

	@Override
	public final Element toXML(Document doc) {

		Element groupElement = doc.createElement("group");

		if (this.getGroupID() != null) {
			groupElement.appendChild(XMLUtils.createCDATAElement("groupID", this.getGroupID().toString(), doc));
		}

		if (this.getName() != null) {
			groupElement.appendChild(XMLUtils.createCDATAElement("name", this.getName(), doc));
		}

		if (this.getDescription() != null) {
			groupElement.appendChild(XMLUtils.createCDATAElement("description", this.getDescription(), doc));
		}

		groupElement.appendChild(XMLUtils.createCDATAElement("enabled", Boolean.toString(this.isEnabled()), doc));

		List<Element> additionalXML = this.getAdditionalXML(doc);

		if (!CollectionUtils.isEmpty(additionalXML)) {

			for (Element element : additionalXML) {

				groupElement.appendChild(element);
			}
		}

		XMLUtils.appendNewElement(doc, groupElement, "isMutable", this instanceof MutableGroup);

		return groupElement;
	}

	protected List<Element> getAdditionalXML(Document doc) {

		return null;
	}

	@Override
	public int hashCode() {

		Integer groupID = this.getGroupID();

		final int prime = 31;
		int result = 1;
		result = prime * result + ((groupID == null) ? super.hashCode() : groupID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		Integer groupID = this.getGroupID();

		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (!(obj instanceof Group)) {
			return false;
		}
		
		Group other = (Group) obj;

		Integer otherGroupID = other.getGroupID();

		if (groupID == null) {
			
			if (otherGroupID != null) {
				
				return false;
			}
			
		} else if (!groupID.equals(otherGroupID)) {
		
			return false;
		}
		
		return true;
	}
	
	/**
	 * @return An {@link AttributeHandler} or null if the current implementation does not support this feature.
	 */
	public AttributeHandler getAttributeHandler(){

		return null;
	}	
}
