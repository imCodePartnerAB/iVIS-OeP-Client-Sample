package se.unlogic.hierarchy.core.beans;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.enums.ModuleType;
import se.unlogic.hierarchy.core.interfaces.FilterModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SystemInterface;
import se.unlogic.standardutils.annotations.NoDuplicates;
import se.unlogic.standardutils.annotations.SplitOnLineBreak;
import se.unlogic.standardutils.annotations.WebPopulate;
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

@Table(name = "openhierarchy_filter_modules")
public class SimpleFilterModuleDescriptor extends BaseModuleDescriptor implements FilterModuleDescriptor {

	private static final long serialVersionUID = -8158756503129841482L;

	@DAOManaged
	@WebPopulate(required = true, populator = NonNegativeStringIntegerPopulator.class)
	private int priority;

	@DAOManaged
	@OneToMany(autoAdd = true, autoUpdate = true, autoGet = true)
	@SimplifiedRelation(table = "openhierarchy_filter_module_aliases", remoteKeyColumnName = "moduleID", remoteValueColumnName = "alias", preserveListOrder = true, indexColumn = "listIndex")
	@WebPopulate(required = true, paramName = "alias")
	@SplitOnLineBreak
	@NoDuplicates
	private List<String> aliases;

	@Override
	public void saveSettings(SystemInterface systemInterface) throws SQLException {

		systemInterface.getCoreDaoFactory().getFilterModuleSettingDAO().set(this);
	}

	@Override
	public void saveAttributes(SystemInterface systemInterface) throws SQLException {

		systemInterface.getCoreDaoFactory().getFilterModuleAttributeDAO().set(this);
	}

	@Override
	public Element toXML(Document doc) {

		Element moduleElement = super.toXML(doc);

		Element aliasElement = doc.createElement("aliases");
		moduleElement.appendChild(aliasElement);

		if(this.aliases != null){

			for(String alias : this.aliases){

				aliasElement.appendChild(XMLUtils.createCDATAElement("alias", alias, doc));
			}
		}


		moduleElement.appendChild(XMLUtils.createElement("priority", this.priority, doc));

		return moduleElement;
	}

	@Override
	public List<String> getAliases() {

		return aliases;
	}

	public void setAliases(List<String> aliases) {

		this.aliases = aliases;
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
		priority = XMLValidationUtils.validateParameter("priority", xmlParser, true, PositiveStringIntegerPopulator.getPopulator(), errors);

		if(!errors.isEmpty()){

			throw new ValidationException(errors);
		}
	}

	@Override
	public ModuleType getType() {

		return ModuleType.FILTER;
	}
}
