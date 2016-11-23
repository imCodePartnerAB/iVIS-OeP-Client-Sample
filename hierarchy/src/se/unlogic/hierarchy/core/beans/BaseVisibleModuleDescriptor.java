package se.unlogic.hierarchy.core.beans;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.enums.PathType;
import se.unlogic.standardutils.annotations.WebPopulate;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.populators.EnumPopulator;
import se.unlogic.standardutils.populators.PositiveStringIntegerPopulator;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLParser;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.standardutils.xml.XMLValidationUtils;

public abstract class BaseVisibleModuleDescriptor extends BaseModuleDescriptor {

	private static final long serialVersionUID = 7287756341751909759L;

	@DAOManaged
	protected Integer sectionID;

	@DAOManaged
	@WebPopulate(maxLength = 255)
	protected String xslPath;

	@DAOManaged
	@WebPopulate
	protected PathType xslPathType;

	@DAOManaged
	@WebPopulate(maxLength = 255)
	protected String staticContentPackage;

	public Integer getSectionID() {

		return sectionID;
	}

	public void setSectionID(Integer section) {

		this.sectionID = section;
	}

	public String getStaticContentPackage() {

		return staticContentPackage;
	}

	public void setStaticContentPackage(String staticContentPackage) {

		this.staticContentPackage = staticContentPackage;
	}

	public String getXslPath() {

		return xslPath;
	}

	public void setXslPath(String xslPath) {

		this.xslPath = xslPath;
	}

	public PathType getXslPathType() {

		return xslPathType;
	}

	public void setXslPathType(PathType xslPathType) {

		this.xslPathType = xslPathType;
	}

	public boolean hasStyleSheet() {

		if (this.getXslPath() != null && this.getXslPathType() != null) {

			if (this.getXslPathType() == PathType.Filesystem) {
				return true;
			} else if (this.getXslPathType() == PathType.RealtiveFilesystem) {
				return true;
			} else if (this.getXslPathType() == PathType.Classpath) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Element toXML(Document doc) {

		Element moduleElement = super.toXML(doc);

		if(this.sectionID != null){
			moduleElement.appendChild(XMLUtils.createElement("sectionID", this.sectionID.toString(), doc));
		}

		if (this.xslPath != null) {
			moduleElement.appendChild(XMLUtils.createElement("xslPath", this.xslPath, doc));
		}

		if (this.xslPathType != null) {
			moduleElement.appendChild(XMLUtils.createElement("xslPathType", this.xslPathType.toString(), doc));
		}

		if (staticContentPackage != null) {
			moduleElement.appendChild(XMLUtils.createCDATAElement("staticContentPackage", this.staticContentPackage, doc));
		}

		return moduleElement;
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
		
		this.sectionID = XMLValidationUtils.validateParameter("sectionID", xmlParser, true, PositiveStringIntegerPopulator.getPopulator(), errors);
		this.staticContentPackage = xmlParser.getString("staticContentPackage");
		this.xslPathType = XMLValidationUtils.validateParameter("xslPathType", xmlParser, false, new EnumPopulator<PathType>(PathType.class), errors);
		this.xslPath = xmlParser.getString("xslPath");
		
		if(!errors.isEmpty()){
			
			throw new ValidationException(errors);
		}
	}
}
