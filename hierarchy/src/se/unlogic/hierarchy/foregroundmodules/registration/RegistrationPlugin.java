package se.unlogic.hierarchy.foregroundmodules.registration;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.Prioritized;
import se.unlogic.hierarchy.core.interfaces.ViewFragment;
import se.unlogic.standardutils.string.TagSource;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.webutils.http.URIParser;


public interface RegistrationPlugin<T> extends Prioritized{

	public ViewFragment getForm(HttpServletRequest req, URIParser uriParser, ValidationException validationException) throws Exception;

	public T populate(HttpServletRequest req) throws ValidationException, Exception;
	
	public void userAdded(User user, T data) throws Exception;
	
	public List<TagSource> getTagSources(User user) throws SQLException;
}
