package se.unlogic.hierarchy.core.utils.crud;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.webutils.http.URIParser;


public interface BeanIDParser<T> {

	public T getBeanID(URIParser uriParser, HttpServletRequest req, String getMode);
}
