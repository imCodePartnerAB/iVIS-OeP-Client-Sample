package se.unlogic.hierarchy.core.utils.crud;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.utils.FCKUtils;
import se.unlogic.webutils.http.URIParser;


public class HTMLContentRewriteBeanFilter<T> implements BeanFilter<T> {

	private final AbsoluteFileURLProvider<T> absoluteFileURLProvider;
	
	public HTMLContentRewriteBeanFilter(AbsoluteFileURLProvider<T> absoluteFileURLProvider) {

		this.absoluteFileURLProvider = absoluteFileURLProvider;
	}

	@Override
	public void beanLoaded(T bean, HttpServletRequest req, URIParser uriParser, User user) {

		FCKUtils.setAbsoluteFileUrls(bean, absoluteFileURLProvider.getAbsoluteFileURL(uriParser, bean));
	}

	@Override
	public void beansLoaded(List<? extends T> beans, HttpServletRequest req, URIParser uriParser, User user) {

		for(T bean : beans){
			
			FCKUtils.setAbsoluteFileUrls(bean, absoluteFileURLProvider.getAbsoluteFileURL(uriParser, bean));
		}
	}

	@Override
	public void addBean(T bean, HttpServletRequest req, URIParser uriParser, User user) {

		FCKUtils.removeAbsoluteFileUrls(bean, absoluteFileURLProvider.getAbsoluteFileURL(uriParser, bean));
	}

	@Override
	public void beanAdded(T bean, HttpServletRequest req, URIParser uriParser, User user) {}

	@Override
	public void updateBean(T bean, HttpServletRequest req, URIParser uriParser, User user) {
		
		FCKUtils.removeAbsoluteFileUrls(bean, absoluteFileURLProvider.getAbsoluteFileURL(uriParser, bean));
	}

	@Override
	public void beanUpdated(T bean, HttpServletRequest req, URIParser uriParser, User user) {}

	@Override
	public void deleteBean(T bean, HttpServletRequest req, URIParser uriParser, User user) {}

	@Override
	public void beanDeleted(T bean, HttpServletRequest req, URIParser uriParser, User user) {}
}
