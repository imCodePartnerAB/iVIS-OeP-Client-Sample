package se.unlogic.hierarchy.foregroundmodules.test.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.w3c.dom.Document;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.foregroundmodules.rest.AnnotatedRESTModule;
import se.unlogic.hierarchy.foregroundmodules.rest.RESTMethod;
import se.unlogic.hierarchy.foregroundmodules.rest.URIParam;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.URIParser;


public class RESTTestModule extends AnnotatedRESTModule {

	private String stringValue;
	private Integer integerValue;
	
	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		addResponseHandler(new StringResponseHandler());
		addResponseHandler(new XMLResponseHandler(sectionInterface.getSystemInterface().getEncoding()));
		
		super.init(moduleDescriptor, sectionInterface, dataSource);
	}

	//Two simple methods for retrieving and setting a string value, these methods use a response handler to write their response
	@RESTMethod(alias="string", method="get")
	public String stringGet(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable{
		
		if(stringValue == null){
			
			return "No value set";
		}
		
		return "Current value" + stringValue;
	}
	
	@RESTMethod(alias="string/{value}", method="put")
	public String stringPut(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, @URIParam(name="value") String value) throws Throwable{
		
		stringValue = value;
		
		return "Value " + stringValue + " saved";
	}	
	
	//Two simple methods for retrieving and setting a integer value, these methods are declared as void and write their response directly to servlet response object
	@RESTMethod(alias="integer", method="get")
	public void intGet(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable{
		
		res.setContentType("text/html");
		
		if(integerValue == null){
			
			res.getWriter().write("No value set");

		}else{
		
			res.getWriter().write("Current value: " + integerValue);
		}
		
		res.getWriter().flush();
	}
	
	@RESTMethod(alias="integer/{value}", method="put")
	public void intPut(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, @URIParam(name="value") Integer value) throws Throwable{
		
		integerValue = value;
		
		res.setContentType("text/html");
		
		res.getWriter().write("Value " + integerValue + " saved");
		
		res.getWriter().flush();
	}
	
	//A simple method that adds the values of the two submitted parameters and writes the response using a respose handler
	@RESTMethod(alias="calculate/{param1}/{param2}", method="get")
	public String calculate(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, @URIParam(name="param1") Integer param1, @URIParam(name="param2") Integer param2) throws Throwable{
		
		return "The sum of the parameters is: " + (param1 + param2);
	}
	
	//This method returns an XML generated based on the input parameters
	@RESTMethod(alias="xml/{element}/{value}", method="get")
	public Document generateXML(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, @URIParam(name="element") String elementName, @URIParam(name="value") String elementValue) throws Throwable{
		
		Document doc = XMLUtils.createDomDocument();
		
		doc.appendChild(XMLUtils.createCDATAElement(XMLUtils.toValidElementName(elementName), elementValue, doc));
		
		return doc;
	}	
}
