package se.unlogic.hierarchy.core.utils;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.standardutils.dao.CRUDDAO;
import se.unlogic.standardutils.xml.Elementable;
import se.unlogic.webutils.http.BeanRequestPopulator;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.url.URLRewriter;

public class AdvancedIntegerBasedCRUD<BeanType extends Elementable, CallbackType extends AdvancedCRUDCallback<User>> extends IntegerBasedCRUD<BeanType, CallbackType> {

	protected boolean hasFCKContentAnnotations;
	protected boolean hasURLRewriteAnnotations;

	public AdvancedIntegerBasedCRUD(Class<BeanType> beanClass, CRUDDAO<BeanType, Integer> crudDAO, BeanRequestPopulator<BeanType> populator, String typeElementName, String typeLogName, String listMethodAlias, CallbackType callback) {

		super(crudDAO, populator, typeElementName, typeLogName, listMethodAlias, callback);

		checkAnnoatations(beanClass);
	}

	public AdvancedIntegerBasedCRUD(Class<BeanType> beanClass, CRUDDAO<BeanType, Integer> crudDAO, BeanRequestPopulator<BeanType> populator, String typeElementName, String typeElementPluralName, String typeLogName, String typeLogPluralName, String listMethodAlias, CallbackType callback) {

		super(crudDAO, populator, typeElementName, typeElementPluralName, typeLogName, typeLogPluralName, listMethodAlias, callback);

		checkAnnoatations(beanClass);
	}

	protected void checkAnnoatations(Class<BeanType> beanClass) {

		if (FCKUtils.getAnnotatedFields(beanClass) != null) {

			this.hasFCKContentAnnotations = true;
		}

		if (URLRewriter.getAnnotatedFields(beanClass) != null) {

			hasURLRewriteAnnotations = true;
		}
	}

	@Override
	public BeanType getRequestedBean(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, String getMode) throws SQLException, AccessDeniedException {

		BeanType bean = super.getRequestedBean(req, res, user, uriParser, getMode);

		if (bean != null) {

			if (hasFCKContentAnnotations) {

				FCKUtils.setAbsoluteFileUrls(bean, callback.getAbsoluteFileURL(uriParser, bean));
			}

			if(hasURLRewriteAnnotations){

				URLRewriter.setAbsoluteLinkUrls(bean, req);
			}
		}

		return bean;
	}

	@Override
	protected void addBean(BeanType bean, HttpServletRequest req, User user, URIParser uriParser) throws Exception {

		if (hasFCKContentAnnotations) {

			FCKUtils.removeAbsoluteFileUrls(bean, callback.getAbsoluteFileURL(uriParser, bean));
		}

		if(hasURLRewriteAnnotations){

			URLRewriter.removeAbsoluteLinkUrls(bean, req);
		}

		super.addBean(bean, req, user, uriParser);
	}

	@Override
	protected void updateBean(BeanType bean, HttpServletRequest req, User user, URIParser uriParser) throws Exception {

		if (hasFCKContentAnnotations) {

			FCKUtils.removeAbsoluteFileUrls(bean, callback.getAbsoluteFileURL(uriParser, bean));
		}

		if(hasURLRewriteAnnotations){

			URLRewriter.removeAbsoluteLinkUrls(bean, req);
		}

		super.updateBean(bean, req, user, uriParser);
	}
}
