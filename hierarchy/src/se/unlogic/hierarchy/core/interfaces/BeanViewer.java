package se.unlogic.hierarchy.core.interfaces;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.webutils.http.URIParser;


public interface BeanViewer<T,C> {

	public ViewFragment getBeanView(T bean, HttpServletRequest req, User user, URIParser uriParser, C callback) throws Exception;
}
