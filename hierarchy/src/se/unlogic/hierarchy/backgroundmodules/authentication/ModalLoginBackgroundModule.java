package se.unlogic.hierarchy.backgroundmodules.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.backgroundmodules.AnnotatedBackgroundModule;
import se.unlogic.hierarchy.core.annotations.CheckboxSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleBackgroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;

public class ModalLoginBackgroundModule extends AnnotatedBackgroundModule {

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Login module alias", description = "The full alias of the login module (relative from contextpath)", required = true)
	protected String loginModuleURI;

	@ModuleSetting
	@CheckboxSettingDescriptor(name="Use modal registration", description="Control if the registration form should be displayed as a modal form or not")
	protected boolean useModalRegistration = true;
	
	private String hashCode;
	
	@Override
	public void init(BackgroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {
		
		super.init(moduleDescriptor, sectionInterface, dataSource);
		
		hashCode = String.valueOf(moduleDescriptor.hashCode());
	}

	@Override
	public void update(BackgroundModuleDescriptor moduleDescriptor, DataSource dataSource) throws Exception {
		
		super.update(moduleDescriptor, dataSource);
		
		hashCode = String.valueOf(moduleDescriptor.hashCode());
	}

	@Override
	public BackgroundModuleResponse processBackgroundRequest(HttpServletRequest req, User user, URIParser uriParser) throws Exception {
		
		Document doc = this.createDocument(req, uriParser);
		Element document = doc.getDocumentElement();
		
		XMLUtils.appendNewElement(doc, document, "loginModuleURI", this.loginModuleURI);
		XMLUtils.appendNewElement(doc, document, "useModalRegistration", this.useModalRegistration);
		XMLUtils.appendNewElement(doc, document, "hashCode", this.hashCode);
		
		SimpleBackgroundModuleResponse moduleResponse = new SimpleBackgroundModuleResponse(doc);
		
		if(this.scripts != null){
			moduleResponse.addScripts(this.scripts);
		}
		if(this.links != null) {
			moduleResponse.addLinks(this.links);
		}
		
		return moduleResponse;
	}

	protected Document createDocument(HttpServletRequest req, URIParser uriParser) {
		
		Document doc = XMLUtils.createDomDocument();
		Element document = doc.createElement("Document");
		document.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser));
		document.appendChild(this.moduleDescriptor.toXML(doc));
		doc.appendChild(document);
		
		return doc;
	}

}
