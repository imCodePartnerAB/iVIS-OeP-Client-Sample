package se.unlogic.hierarchy.core.utils;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.webutils.http.URIParser;


public interface AdvancedCRUDCallback<UserType extends User> extends CRUDCallback<UserType> {

	public String getAbsoluteFileURL(URIParser uriParser, Object bean);
}
