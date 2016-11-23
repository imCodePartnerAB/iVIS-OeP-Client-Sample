/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.groupproviders;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import se.unlogic.hierarchy.core.beans.MutableGroup;
import se.unlogic.hierarchy.core.handlers.SourceAttributeHandler;
import se.unlogic.hierarchy.core.interfaces.AttributeSource;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Key;
import se.unlogic.standardutils.dao.annotations.OneToMany;
import se.unlogic.standardutils.dao.annotations.OrderBy;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.reflection.ReflectionUtils;

@Table(name="simple_groups")
public class SimpleGroup extends MutableGroup implements AttributeSource{

	private static final long serialVersionUID = -3158065010977262102L;

	public static final Field ATTRIBUTES_RELATION = ReflectionUtils.getField(SimpleGroup.class, "attributes");

	private SourceAttributeHandler attributeHandler;	
	
	@Key
	@DAOManaged(autoGenerated=true)
	protected Integer groupID;

	@DAOManaged
	@OrderBy
	protected String name;

	@DAOManaged
	protected String description;

	@DAOManaged
	protected boolean enabled;

	protected Integer providerID;

	@DAOManaged
	@OneToMany
	private List<SimpleGroupAttribute> attributes;	
	
	@Override
	public boolean isEnabled() {

		return this.enabled;
	}

	/* (non-Javadoc)
	 * @see se.unlogic.hierarchy.core.beans.MutableGroup#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {

		this.enabled = enabled;
	}

	@Override
	public Integer getGroupID() {

		return this.groupID;
	}

	@Override
	public void setGroupID(Integer groupID) {

		this.groupID = groupID;
	}

	@Override
	public String getName() {

		return this.name;
	}

	/* (non-Javadoc)
	 * @see se.unlogic.hierarchy.core.beans.MutableGroup#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {

		this.name = name;
	}

	@Override
	public String getDescription() {

		return this.description;
	}

	/* (non-Javadoc)
	 * @see se.unlogic.hierarchy.core.beans.MutableGroup#setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(String description) {

		this.description = description;
	}

	@Override
	public String toString() {

		return this.name + " (" + groupID + ")";
	}


	public Integer getProviderID() {

		return providerID;
	}


	public void setProviderID(Integer providerID) {

		this.providerID = providerID;
	}
	
	@Override
	public synchronized SourceAttributeHandler getAttributeHandler() {

		if(attributeHandler == null){
			
			this.attributeHandler = new SourceAttributeHandler(this, 255, 1024);
		}
		
		return attributeHandler;
	}

	@Override
	public List<SimpleGroupAttribute> getAttributes() {

		return attributes;
	}

	public void setAttributes(List<SimpleGroupAttribute> attributes) {

		this.attributes = attributes;
	}

	@Override
	public void addAttribute(String name, String value) {

		if(this.attributes == null){
			
			attributes = new ArrayList<SimpleGroupAttribute>();
		}
		
		attributes.add(new SimpleGroupAttribute(name, value));
	}	
}
