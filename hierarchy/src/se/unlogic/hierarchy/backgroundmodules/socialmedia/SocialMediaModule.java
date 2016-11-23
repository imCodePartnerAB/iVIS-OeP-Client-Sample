package se.unlogic.hierarchy.backgroundmodules.socialmedia;

import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.backgroundmodules.AnnotatedBackgroundModule;
import se.unlogic.hierarchy.core.annotations.CheckboxSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.beans.SimpleBackgroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleResponse;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.URIParser;

public class SocialMediaModule extends AnnotatedBackgroundModule {

	@ModuleSetting
	@CheckboxSettingDescriptor(name="Use Facebook",description="Show Facebook like button")
	protected boolean showFacebook = true;
	
	@ModuleSetting
	@CheckboxSettingDescriptor(name="Use Twitter",description="Show Twitter tweet button")
	protected boolean showTwitter = true;
	
	@Override
	public BackgroundModuleResponse processBackgroundRequest(HttpServletRequest req, User user, URIParser uriParser) throws Exception {
		
		Document doc = XMLUtils.createDomDocument();
		Element document = doc.createElement("Document");
		
		XMLUtils.appendNewElement(doc, document, "staticcontentPath", req.getContextPath() + "/static/b/" + this.moduleDescriptor.getSectionID() + "/" + this.moduleDescriptor.getModuleID());
		XMLUtils.appendNewElement(doc, document, "protocol", req.getScheme());
		
		Object requestedURL = req.getAttribute("URLFilter.originalURL");

		if(requestedURL != null && requestedURL instanceof String) {
			document.appendChild(XMLUtils.createElement("url", requestedURL, doc));
		}else{
			document.appendChild(XMLUtils.createElement("url", req.getRequestURL().toString(), doc));
		}
		
		if(showFacebook) {
			document.appendChild(XMLUtils.createElement("showFacebook", "true", doc));
		}
		
		if(showTwitter) {
			document.appendChild(XMLUtils.createElement("showTwitter", "true", doc));
		}
		
		doc.appendChild(document);
				
		return new SimpleBackgroundModuleResponse(doc);
	}
	
}
