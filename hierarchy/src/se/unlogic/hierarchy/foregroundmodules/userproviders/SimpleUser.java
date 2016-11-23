/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.userproviders;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import se.unlogic.hierarchy.core.beans.BaseUser;
import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.handlers.SourceAttributeHandler;
import se.unlogic.hierarchy.core.interfaces.AttributeSource;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.OneToMany;
import se.unlogic.standardutils.dao.annotations.SimplifiedRelation;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.reflection.ReflectionUtils;

@Table(name = "simple_users")
public class SimpleUser extends BaseUser implements AttributeSource{

	private static final long serialVersionUID = 8229695484564353912L;
	
	public static final Field GROUPS_RELATION = ReflectionUtils.getField(SimpleUser.class, "groups");
	public static final Field ATTRIBUTES_RELATION = ReflectionUtils.getField(SimpleUser.class, "attributes");

	private SourceAttributeHandler attributeHandler;

	@DAOManaged
	@OneToMany
	@SimplifiedRelation(table = "simple_user_groups", remoteKeyColumnName = "userID", remoteValueColumnName = "groupID")
	private List<Group> groups;

	@DAOManaged
	@OneToMany
	private List<SimpleUserAttribute> attributes;

	@Override
	public List<Group> getGroups() {

		return groups;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.unlogic.hierarchy.core.beans.MutableUser#setGroups(java.util.Collection)
	 */
	@Override
	public void setGroups(List<Group> groups) {

		this.groups = groups;
	}

	@Override
	public synchronized SourceAttributeHandler getAttributeHandler() {

		if(attributeHandler == null){
			
			this.attributeHandler = new SourceAttributeHandler(this, 255, 1024);
		}
		
		return attributeHandler;
	}

	@Override
	public List<SimpleUserAttribute> getAttributes() {

		return attributes;
	}

	public void setAttributes(List<SimpleUserAttribute> attributes) {

		this.attributes = attributes;
	}

	@Override
	public void addAttribute(String name, String value) {

		if(this.attributes == null){
			
			attributes = new ArrayList<SimpleUserAttribute>();
		}
		
		attributes.add(new SimpleUserAttribute(name, value));
	}
}
