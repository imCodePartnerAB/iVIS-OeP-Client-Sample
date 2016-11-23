package se.unlogic.hierarchy.core.utils.crud;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.webutils.http.URIParser;


public class IntegerBeanIDParser implements BeanIDParser<Integer> {

	private static final IntegerBeanIDParser INSTANCE = new IntegerBeanIDParser(); 
	
	private IntegerBeanIDParser(){}
	
	@Override
	public Integer getBeanID(URIParser uriParser, HttpServletRequest req, String getMode) {

		return uriParser.getInt(2);
	}

	public static BeanIDParser<Integer> getInstance() {

		return INSTANCE;
	}
}
