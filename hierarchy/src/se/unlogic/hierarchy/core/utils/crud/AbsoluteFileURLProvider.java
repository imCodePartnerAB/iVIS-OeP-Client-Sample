package se.unlogic.hierarchy.core.utils.crud;

import se.unlogic.webutils.http.URIParser;


public interface AbsoluteFileURLProvider<T> {

	public String getAbsoluteFileURL(URIParser uriParser, T bean);
}
