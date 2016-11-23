package se.unlogic.hierarchy.filtermodules;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;

import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.FilterChain;
import se.unlogic.webutils.http.URIParser;

public class SetAttributeByParameter extends AnnotatedFilterModule {

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Parameter name", description = "The name of the parameter to check for", required = true)
	private String paramName;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Parameter value", description = "The value of the parameter (if not set any value will trigger the attribute to be set)")
	private String paramValue;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Attribute name", description = "The name of the attribute to set", required = true)
	private String attributeName;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Attribute value", description = "The value of the attribute to be set", required = true)
	private String attributeValue;
	
	@Override
	public void doFilter(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, FilterChain filterChain) throws TransformerException, IOException {

		if(paramName != null && attributeName != null && attributeValue != null){
			
			String param = req.getParameter(paramName);
			
			if(paramValue == null && param != null){
				
				setAttribute(req);
				
			}else if(paramValue != null && param != null && paramValue.equals(param)){
				
				setAttribute(req);
			}
		}
		
		filterChain.doFilter(req, res, user, uriParser);
	}

	private void setAttribute(HttpServletRequest req) {

		req.setAttribute(attributeName, attributeValue);
	}
}
