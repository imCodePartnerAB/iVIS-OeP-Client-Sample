package se.unlogic.hierarchy.filtermodules;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;

import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.FilterChain;
import se.unlogic.webutils.http.URIParser;


public class StartsWithRedirectModule extends AnnotatedFilterModule {

	@ModuleSetting
	@TextFieldSettingDescriptor(name="Prefix",description="The prefix to remove from the URL by redirecting")
	protected String prefix;
	
	@Override
	public void doFilter(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, FilterChain filterChain) throws TransformerException, IOException {

		if(prefix == null){
			
			log.info("Filter module " + moduleDescriptor + " has no prefix set, redirecting disabled");
			
		}else if(req.getServerName().startsWith(prefix)){
			
			String protocol = req.getScheme();
			String serverName = req.getServerName();
			String contextPath = req.getContextPath();

			String port = "";
			
			if (req.getServerPort() != 80 && req.getServerPort() != 443) {
				port = ":" + req.getServerPort();
			}
			
			String redirectURL = protocol + "://" + serverName.substring(prefix.length()) + port + contextPath + uriParser.getFormattedURI();
			
			try {
				res.sendRedirect(redirectURL);
				
			} catch (IOException e) {
				
				log.info("Error redirecting user " + user + " to " + redirectURL);
			}
			
			return;
		}
		
		filterChain.doFilter(req, res, user, uriParser);
	}
}
