package se.unlogic.hierarchy.core.interfaces;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.webutils.http.URIParser;


public interface FilterChain {

	public void doFilter(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws TransformerException, IOException;
}
