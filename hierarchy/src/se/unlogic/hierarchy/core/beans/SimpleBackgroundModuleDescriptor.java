/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.beans;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.enums.ModuleType;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SystemInterface;
import se.unlogic.standardutils.annotations.NoDuplicates;
import se.unlogic.standardutils.annotations.SplitOnLineBreak;
import se.unlogic.standardutils.annotations.WebPopulate;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.OneToMany;
import se.unlogic.standardutils.dao.annotations.SimplifiedRelation;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.populators.NonNegativeStringIntegerPopulator;
import se.unlogic.standardutils.populators.PositiveStringIntegerPopulator;
import se.unlogic.standardutils.populators.StringPopulator;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLParser;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.standardutils.xml.XMLValidationUtils;

@Table(name = "openhierarchy_background_modules")
public class SimpleBackgroundModuleDescriptor extends BaseVisibleModuleDescriptor implements BackgroundModuleDescriptor {

	private static final long serialVersionUID = -2949709405791503907L;

	@DAOManaged
	@OneToMany(autoAdd = true, autoUpdate = true, autoGet = true)
	@SimplifiedRelation(table = "openhierarchy_background_module_aliases", remoteKeyColumnName = "moduleID", remoteValueColumnName = "alias", preserveListOrder = true, indexColumn = "listIndex")
	@WebPopulate(required = true, paramName = "alias")
	@SplitOnLineBreak
	@NoDuplicates
	private List<String> aliases;

	@DAOManaged
	@OneToMany(autoAdd = true, autoUpdate = true, autoGet = true)
	@SimplifiedRelation(table = "openhierarchy_background_module_slots", remoteKeyColumnName = "moduleID", remoteValueColumnName = "slot")
	@WebPopulate(paramName = "slots")
	@SplitOnLineBreak
	@NoDuplicates
	private List<String> slots;

	@DAOManaged
	@WebPopulate(required = true, populator = NonNegativeStringIntegerPopulator.class)
	private int priority;

	@Override
	public List<String> getAliases() {

		return aliases;
	}

	public void setAliases(List<String> aliases) {

		this.aliases = aliases;
	}

	@Override
	public List<String> getSlots() {

		return slots;
	}

	public void setSlots(List<String> slots) {

		this.slots = slots;
	}

	@Override
	public Element toXML(Document doc) {

		Element moduleElement = super.toXML(doc);

		Element aliasElement = doc.createElement("aliases");
		moduleElement.appendChild(aliasElement);

		for(String alias : this.aliases){

			aliasElement.appendChild(XMLUtils.createCDATAElement("alias", alias, doc));
		}

		if(!CollectionUtils.isEmpty(this.slots)){

			Element slotsElement = doc.createElement("slots");
			moduleElement.appendChild(slotsElement);

			for(String slot : this.slots){

				slotsElement.appendChild(XMLUtils.createCDATAElement("slot", slot, doc));
			}
		}

		moduleElement.appendChild(XMLUtils.createElement("priority", this.priority, doc));

		return moduleElement;
	}

	@Override
	public void saveSettings(SystemInterface systemInterface) throws SQLException {

		systemInterface.getCoreDaoFactory().getBackgroundModuleSettingDAO().set(this);
	}

	@Override
	public void saveAttributes(SystemInterface systemInterface) throws SQLException {

		systemInterface.getCoreDaoFactory().getBackgroundModuleAttributeDAO().set(this);
	}

	@Override
	public int getPriority() {

		return priority;
	}

	public void setPriority(int priority) {

		this.priority = priority;
	}

	@Override
	public void populate(XMLParser xmlParser) throws ValidationException {

		List<ValidationError> errors = null;

		try{
			super.populate(xmlParser);

		}catch(ValidationException e){

			errors = e.getErrors();
		}

		if(errors == null){

			errors = new ArrayList<ValidationError>(2);
		}

		aliases = XMLValidationUtils.validateParameters("aliases/alias", xmlParser, true, StringPopulator.getPopulator(), errors);
		slots = XMLValidationUtils.validateParameters("slots/slot", xmlParser, true, StringPopulator.getPopulator(), errors);
		priority = XMLValidationUtils.validateParameter("priority", xmlParser, true, PositiveStringIntegerPopulator.getPopulator(), errors);

		if(!errors.isEmpty()){

			throw new ValidationException(errors);
		}
	}

	@Override
	public ModuleType getType() {

		return ModuleType.BACKGROUND;
	}
}
