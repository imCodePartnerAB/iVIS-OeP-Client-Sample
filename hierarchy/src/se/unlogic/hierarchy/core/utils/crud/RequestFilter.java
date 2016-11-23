package se.unlogic.hierarchy.core.utils.crud;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.standardutils.validation.ValidationException;


public interface RequestFilter {

	public HttpServletRequest parseRequest(HttpServletRequest req, User user) throws ValidationException, Exception;
	
	public void releaseRequest(HttpServletRequest req, User user);
}
