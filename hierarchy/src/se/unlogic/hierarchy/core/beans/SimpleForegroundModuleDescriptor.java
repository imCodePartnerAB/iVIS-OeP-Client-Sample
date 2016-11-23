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

import se.unlogic.hierarchy.core.enums.HTTPProtocol;
import se.unlogic.hierarchy.core.enums.ModuleType;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SystemInterface;
import se.unlogic.standardutils.annotations.WebPopulate;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.populators.EnumPopulator;
import se.unlogic.standardutils.populators.StringPopulator;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLParser;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.standardutils.xml.XMLValidationUtils;

@Table(name = "openhierarchy_foreground_modules")
public class SimpleForegroundModuleDescriptor extends BaseVisibleModuleDescriptor implements ForegroundModuleDescriptor {

	private static final long serialVersionUID = -979304448104554019L;

	@DAOManaged
	@WebPopulate(required = true, maxLength = 45)
	protected String alias;

	@DAOManaged
	@WebPopulate(required = true, maxLength = 255)
	protected String description;

	@DAOManaged
	@WebPopulate
	protected boolean visibleInMenu;

	@WebPopulate
	protected HTTPProtocol requiredProtocol;

	/*
	 * (non-Javadoc)
	 *
	 * @see se.unlogic.hierarchy.core.beans.ModuleDescriptor#isVisibleInMenu()
	 */
	@Override
	public boolean isVisibleInMenu() {

		return visibleInMenu;
	}

	public void setVisibleInMenu(boolean visibleInMenu) {

		this.visibleInMenu = visibleInMenu;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see se.unlogic.hierarchy.core.beans.ModuleDescriptor#getAlias()
	 */
	@Override
	public String getAlias() {

		return alias;
	}

	public void setAlias(String alias) {

		this.alias = alias;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see se.unlogic.hierarchy.core.beans.ModuleDescriptor#getDescription()
	 */
	@Override
	public String getDescription() {

		return description;
	}

	public void setDescription(String description) {

		this.description = description;
	}

	@Override
	public HTTPProtocol getRequiredProtocol() {

		return this.requiredProtocol;

	}

	public HTTPProtocol setRequiredProtocol(HTTPProtocol hTTPProtocol) {

		return this.requiredProtocol = hTTPProtocol;

	}


	/*
	 * (non-Javadoc)
	 *
	 * @see se.unlogic.hierarchy.core.beans.ModuleDescriptor#toXML(org.w3c.dom.Document )
	 */

	@Override
	public Element toXML(Document doc) {

		Element moduleElement = super.toXML(doc);

		XMLUtils.appendNewCDATAElement(doc, moduleElement, "description", description);
		XMLUtils.appendNewCDATAElement(doc, moduleElement, "alias", alias);

		XMLUtils.appendNewElement(doc, moduleElement, "requiredProtocol", requiredProtocol);
		XMLUtils.appendNewElement(doc, moduleElement, "visibleInMenu", visibleInMenu);

		return moduleElement;
	}

	@Override
	public String toString() {

		return name + " (ID: " + this.moduleID + ", alias: " + alias + ")";
	}

	@Override
	public void saveSettings(SystemInterface systemInterface) throws SQLException {

		systemInterface.getCoreDaoFactory().getForegroundModuleSettingDAO().set(this);
	}

	@Override
	public void saveAttributes(SystemInterface systemInterface) throws SQLException {

		systemInterface.getCoreDaoFactory().getForegroundModuleAttributeDAO().set(this);
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

		this.description = XMLValidationUtils.validateParameter("description", xmlParser, true, 1, 255, StringPopulator.getPopulator(), errors);
		this.alias = XMLValidationUtils.validateParameter("alias", xmlParser, true, 1, 255, StringPopulator.getPopulator(), errors);
		this.requiredProtocol = XMLValidationUtils.validateParameter("requiredProtocol", xmlParser, false, new EnumPopulator<HTTPProtocol>(HTTPProtocol.class), errors);
		this.visibleInMenu = xmlParser.getBoolean("visibleInMenu");

		if(!errors.isEmpty()){

			throw new ValidationException(errors);
		}
	}

	@Override
	public ModuleType getType() {

		return ModuleType.FOREGROUND;
	}
}
