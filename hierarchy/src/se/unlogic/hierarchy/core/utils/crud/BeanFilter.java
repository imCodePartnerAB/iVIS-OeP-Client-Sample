package se.unlogic.hierarchy.core.utils.crud;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.webutils.http.URIParser;


public interface BeanFilter<T> {

	public void beanLoaded(T bean, HttpServletRequest req, URIParser uriParser, User user);
	
	public void beansLoaded(List<? extends T> beans, HttpServletRequest req, URIParser uriParser, User user);
	
	public void addBean(T bean, HttpServletRequest req, URIParser uriParser, User user);
	
	public void beanAdded(T bean, HttpServletRequest req, URIParser uriParser, User user);
	
	public void updateBean(T bean, HttpServletRequest req, URIParser uriParser, User user);
	
	public void beanUpdated(T bean, HttpServletRequest req, URIParser uriParser, User user);
	
	public void deleteBean(T bean, HttpServletRequest req, URIParser uriParser, User user);
	
	public void beanDeleted(T bean, HttpServletRequest req, URIParser uriParser, User user);
}
