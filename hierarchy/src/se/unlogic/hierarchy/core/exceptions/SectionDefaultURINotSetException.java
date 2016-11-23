package se.unlogic.hierarchy.core.exceptions;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.interfaces.SectionDescriptor;

public class SectionDefaultURINotSetException extends RequestException {

	private static final long serialVersionUID = 59002803146750330L;
	private static final Priority PRIORITY = Level.WARN;
	private final boolean loggedIn;

	public SectionDefaultURINotSetException(SectionDescriptor sectionDescriptor, boolean loggedIn) {

		super(sectionDescriptor);
		this.loggedIn = loggedIn;
	}

	@Override
	public String toString() {

		if(loggedIn){
		
			return "No default URI for logged in users set in section " + this.getSectionDescriptor() + ".";
			
		}else{
			
			return "No default URI for non-logged in users set in section " + this.getSectionDescriptor() + ".";
		}
	}

	@Override
	public Element toXML(Document doc) {

		return doc.createElement("SectionDefaultURINotSetException");
	}

	@Override
	public Integer getStatusCode() {

		return 404;
	}

	@Override
	public Priority getPriority() {

		return PRIORITY;
	}

	@Override
	public Throwable getThrowable() {

		return null;
	}
}
