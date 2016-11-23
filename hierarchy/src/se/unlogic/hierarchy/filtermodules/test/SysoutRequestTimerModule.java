package se.unlogic.hierarchy.filtermodules.test;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.FilterChain;
import se.unlogic.hierarchy.filtermodules.SimpleFilterModule;
import se.unlogic.standardutils.time.TimeUtils;
import se.unlogic.webutils.http.URIParser;


public class SysoutRequestTimerModule extends SimpleFilterModule {

	@Override
	public void doFilter(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, FilterChain filterChain) throws TransformerException, IOException {

		long startTime = System.currentTimeMillis();
		
		filterChain.doFilter(req, res, user, uriParser);

		System.out.println("Request " + uriParser.getFormattedURI() + " processed in: " + TimeUtils.millisecondsToString(System.currentTimeMillis() - startTime));
	}
}
