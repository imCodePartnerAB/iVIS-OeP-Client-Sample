package com.nordicpeak.flowengine.queries.fileuploadquery;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.annotations.FCKContent;
import se.unlogic.standardutils.annotations.NoDuplicates;
import se.unlogic.standardutils.annotations.SplitOnLineBreak;
import se.unlogic.standardutils.annotations.WebPopulate;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Key;
import se.unlogic.standardutils.dao.annotations.OneToMany;
import se.unlogic.standardutils.dao.annotations.SimplifiedRelation;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.io.BinarySizeFormater;
import se.unlogic.standardutils.populators.PositiveStringIntegerPopulator;
import se.unlogic.standardutils.populators.StringPopulator;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLElement;
import se.unlogic.standardutils.xml.XMLParser;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.standardutils.xml.XMLValidationUtils;
import se.unlogic.webutils.annotations.URLRewrite;

import com.nordicpeak.flowengine.annotations.TextTagReplace;
import com.nordicpeak.flowengine.queries.basequery.BaseQuery;

@Table(name = "file_upload_queries")
@XMLElement
public class FileUploadQuery extends BaseQuery {

	private static final long serialVersionUID = -842191226937409416L;

	public static Field ALLOWED_FILE_EXTENSIONS_RELATION = ReflectionUtils.getField(FileUploadQuery.class, "allowedFileExtensions");

	@DAOManaged
	@Key
	@XMLElement
	private Integer queryID;

	@FCKContent
	@TextTagReplace
	@URLRewrite
	@DAOManaged
	@WebPopulate(maxLength = 65535)
	@XMLElement(cdata=true)
	private String description;

	@FCKContent
	@TextTagReplace
	@URLRewrite
	@DAOManaged
	@WebPopulate(maxLength = 65535)
	@XMLElement
	private String helpText;

	@DAOManaged
	@WebPopulate(populator = PositiveStringIntegerPopulator.class)
	@XMLElement
	private Integer maxFileCount;

	@DAOManaged
	@WebPopulate(populator = PositiveStringIntegerPopulator.class)
	@XMLElement
	private Integer maxFileSize;

	@DAOManaged
	@OneToMany(autoUpdate = true, autoAdd = true)
	@SimplifiedRelation(table = "file_upload_query_extensions", remoteValueColumnName = "extension")
	@WebPopulate
	@SplitOnLineBreak
	@NoDuplicates
	@XMLElement
	private List<String> allowedFileExtensions;

	@DAOManaged
	@OneToMany
	@XMLElement
	private List<FileUploadQueryInstance> instances;

	public static long getSerialversionuid() {

		return serialVersionUID;
	}

	@Override
	public Integer getQueryID() {

		return queryID;
	}

	@Override
	public String getDescription() {

		return description;
	}

	public List<FileUploadQueryInstance> getInstances() {

		return instances;
	}

	public void setInstances(List<FileUploadQueryInstance> instances) {

		this.instances = instances;
	}

	public void setQueryID(int queryID) {

		this.queryID = queryID;
	}

	public void setDescription(String description) {

		this.description = description;
	}

	public String getHelpText() {

		return helpText;
	}

	public void setHelpText(String helpText) {

		this.helpText = helpText;
	}

	@Override
	public String toString() {

		if(this.queryDescriptor != null){

			return queryDescriptor.getName() + " (queryID: " + queryID + ")";
		}

		return "FileUploadQuery (queryID: " + queryID + ")";
	}

	public Integer getMaxFileCount() {

		return maxFileCount;
	}

	public void setMaxFileCount(Integer maxLength) {

		this.maxFileCount = maxLength;
	}

	public List<String> getAllowedFileExtensions() {

		return allowedFileExtensions;
	}

	public void setAllowedFileExtensions(List<String> allowedFileExtensions) {

		this.allowedFileExtensions = allowedFileExtensions;
	}

	public Integer getMaxFileSize() {

		return maxFileSize;
	}

	public void setMaxFileSize(Integer maxFileSize) {

		this.maxFileSize = maxFileSize;
	}

	@Override
	public String getXSDTypeName() {

		return "FileUploadQuery" + queryID;
	}

	@Override
	public Element toXML(Document doc) {

		Element queryElement = super.toXML(doc);

		if(maxFileSize != null) {
			XMLUtils.appendNewElement(doc, queryElement, "FormatedMaxSize", BinarySizeFormater.getFormatedSize(maxFileSize));
		}

		return queryElement;
	}
	
	@Override
	public void toXSD(Document doc) {

		Element complexTypeElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:complexType");
		complexTypeElement.setAttribute("name", getXSDTypeName());

		Element complexContentElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:complexContent");
		complexTypeElement.appendChild(complexContentElement);

		Element extensionElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:extension");
		extensionElement.setAttribute("base", "Query");
		complexContentElement.appendChild(extensionElement);

		Element sequenceElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:sequence");
		extensionElement.appendChild(sequenceElement);

		Element nameElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema","xs:element");
		nameElement.setAttribute("name", "Name");
		nameElement.setAttribute("type", "xs:string");
		nameElement.setAttribute("minOccurs", "1");
		nameElement.setAttribute("maxOccurs", "1");
		nameElement.setAttribute("fixed", queryDescriptor.getName());
		sequenceElement.appendChild(nameElement);

		Element fileElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:element");
		fileElement.setAttribute("name", "File");
		fileElement.setAttribute("type", "FileType" + queryID);
		fileElement.setAttribute("minOccurs", "1");
		fileElement.setAttribute("maxOccurs", this.maxFileCount != null ? maxFileCount.toString() : "unbounded");

		sequenceElement.appendChild(fileElement);

		doc.getDocumentElement().appendChild(complexTypeElement);

		appendXSDType(doc);
	}

	public void appendXSDType(Document doc) {

		Element complexTypeElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:complexType");
		complexTypeElement.setAttribute("name", "FileType" + queryID);

		Element sequenceElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:sequence");
		complexTypeElement.appendChild(sequenceElement);

		addElementType(doc, sequenceElement, "ID", "xs:string");
		addElementType(doc, sequenceElement, "Name", "xs:string");
		addElementType(doc, sequenceElement, "Size" ,"xs:long");
		addElementType(doc, sequenceElement, "EncodedData", "xs:string");

		doc.getDocumentElement().appendChild(complexTypeElement);
	}

	private void addElementType(Document doc, Element sequenceElement, String name, String type) {

		Element element = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:element");
		element.setAttribute("name", name);
		element.setAttribute("type", type);
		element.setAttribute("minOccurs", "1");
		element.setAttribute("maxOccurs", "1");

		sequenceElement.appendChild(element);
	}

	@Override
	public void populate(XMLParser xmlParser) throws ValidationException {

		List<ValidationError> errors = new ArrayList<ValidationError>();
		
		description = XMLValidationUtils.validateParameter("description", xmlParser, false, 1, 65535, StringPopulator.getPopulator(), errors);
		helpText = XMLValidationUtils.validateParameter("helpText", xmlParser, false, 1, 65535, StringPopulator.getPopulator(), errors);
		
		maxFileCount = XMLValidationUtils.validateParameter("maxFileCount", xmlParser, false, PositiveStringIntegerPopulator.getPopulator(), errors);
		maxFileSize = XMLValidationUtils.validateParameter("maxFileSize", xmlParser, false, PositiveStringIntegerPopulator.getPopulator(), errors);
		
		allowedFileExtensions = xmlParser.getStrings("allowedFileExtensions/value");
		
		if(!errors.isEmpty()){

			throw new ValidationException(errors);
		}
		
	}
}
