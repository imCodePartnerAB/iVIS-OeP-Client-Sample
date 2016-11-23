package se.unlogic.hierarchy.core.exceptions;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionDescriptor;
import se.unlogic.standardutils.xml.XMLUtils;

public class ProtocolRedirectException extends RequestException {

	private static final long serialVersionUID = 7405881733397084909L;

	private static final Priority PRIORITY = Level.INFO;

	private Exception e;
	
	public ProtocolRedirectException(SectionDescriptor sectionDescriptor, Exception e) {
		super(sectionDescriptor);
	
		this.e = e;
	}
	
	public ProtocolRedirectException(SectionDescriptor sectionDescriptor, ForegroundModuleDescriptor moduleDescriptor, Exception e) {

		super(sectionDescriptor, moduleDescriptor);
	}


	@Override
	public String toString() {

		if(this.getModuleDescriptor() != null){
			
			return "exception " + e + " thrown when redirecting to required HTTP protocol of module " + this.getModuleDescriptor() + " in section " + this.getSectionDescriptor();
			
		}else{
			
			return "exception " + e + " thrown when redirecting to required HTTP protocol of section " + this.getSectionDescriptor();
		}
	}

	@Override
	public Element toXML(Document doc) {
		Element sectionRedirectExceptionElement = doc.createElement("ProtocolRedirectException");

		sectionRedirectExceptionElement.appendChild(XMLUtils.createCDATAElement("Exception", this.e.toString(), doc));
		sectionRedirectExceptionElement.appendChild(this.getSectionDescriptor().toXML(doc));
		
		if(this.getModuleDescriptor() != null){
			sectionRedirectExceptionElement.appendChild(this.getModuleDescriptor().toXML(doc));
		}

		return sectionRedirectExceptionElement;
	}

	@Override
	public Integer getStatusCode() {
		return null;
	}

	@Override
	public Priority getPriority() {
		return PRIORITY;
	}

	@Override
	public Exception getThrowable() {
		return e;
	}
}
