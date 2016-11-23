package se.unlogic.hierarchy.core.utils.crud;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.url.URLRewriter;


public class URLRewriteBeanFilter<T> implements BeanFilter<T> {

	@Override
	public void beanLoaded(T bean, HttpServletRequest req, URIParser uriParser, User user) {

		URLRewriter.setAbsoluteLinkUrls(bean, req);
	}

	@Override
	public void beansLoaded(List<? extends T> beans, HttpServletRequest req, URIParser uriParser, User user) {

		URLRewriter.setAbsoluteLinkUrls(beans, req);
	}

	@Override
	public void addBean(T bean, HttpServletRequest req, URIParser uriParser, User user) {

		URLRewriter.removeAbsoluteLinkUrls(bean, req);
	}

	@Override
	public void beanAdded(T bean, HttpServletRequest req, URIParser uriParser, User user) {}

	@Override
	public void updateBean(T bean, HttpServletRequest req, URIParser uriParser, User user) {
		
		URLRewriter.removeAbsoluteLinkUrls(bean, req);
	}

	@Override
	public void beanUpdated(T bean, HttpServletRequest req, URIParser uriParser, User user) {}

	@Override
	public void deleteBean(T bean, HttpServletRequest req, URIParser uriParser, User user) {}

	@Override
	public void beanDeleted(T bean, HttpServletRequest req, URIParser uriParser, User user) {}
}
