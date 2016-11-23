package se.unlogic.hierarchy.core.utils.crud;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.interfaces.AccessInterface;
import se.unlogic.hierarchy.core.utils.AccessUtils;
import se.unlogic.webutils.http.URIParser;


public class AddUpdateDeleteAccessFilter<T> implements AccessFilter<T> {

	protected final AccessInterface accessInterface;

	public AddUpdateDeleteAccessFilter(AccessInterface accessInterface) {

		super();
		this.accessInterface = accessInterface;
	}

	@Override
	public void checkAddAccess(User user, HttpServletRequest req, URIParser uriParser) throws AccessDeniedException, URINotFoundException, SQLException {

		if(!AccessUtils.checkAccess(user, accessInterface)){

			throw new AccessDeniedException("Add access denied");
		}
	}

	@Override
	public void checkUpdateAccess(T bean, User user, HttpServletRequest req, URIParser uriParser) throws AccessDeniedException, URINotFoundException, SQLException {

		if(!AccessUtils.checkAccess(user, accessInterface)){

			throw new AccessDeniedException("Update access denied");
		}
	}

	@Override
	public void checkDeleteAccess(T bean, User user, HttpServletRequest req, URIParser uriParser) throws AccessDeniedException, URINotFoundException, SQLException {

		if(!AccessUtils.checkAccess(user, accessInterface)){

			throw new AccessDeniedException("Delete access denied");
		}
	}

	@Override
	public void checkShowAccess(T bean, User user, HttpServletRequest req, URIParser uriParser) throws AccessDeniedException, URINotFoundException, SQLException {}

	@Override
	public void checkListAccess(User user, HttpServletRequest req, URIParser uriParser) throws AccessDeniedException, URINotFoundException, SQLException {}
}
