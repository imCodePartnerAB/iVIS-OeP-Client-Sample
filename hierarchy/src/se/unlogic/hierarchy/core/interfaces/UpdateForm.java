package se.unlogic.hierarchy.core.interfaces;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.webutils.http.URIParser;


public interface UpdateForm<T, C> {

	public ViewFragment getUpdateForm(T bean, HttpServletRequest req, User user, URIParser uriParser, ValidationException validationException, C callback) throws Exception;

	public T populate(T bean,  HttpServletRequest req, User user, URIParser uriParser, C callback) throws ValidationException, Exception;
}
