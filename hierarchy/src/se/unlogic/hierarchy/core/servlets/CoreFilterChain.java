package se.unlogic.hierarchy.core.servlets;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.FilterChain;
import se.unlogic.hierarchy.core.interfaces.FilterModule;
import se.unlogic.hierarchy.core.interfaces.FilterModuleDescriptor;
import se.unlogic.webutils.http.URIParser;


public class CoreFilterChain implements FilterChain{

	private Logger log = Logger.getLogger(this.getClass());
	
	private CoreServlet coreServlet;
	private List<Entry<FilterModuleDescriptor,FilterModule>> filterModules;
	private int filterIndex;
	
	public CoreFilterChain(CoreServlet coreServlet, List<Entry<FilterModuleDescriptor,FilterModule>> filterModules) {

		this.coreServlet = coreServlet;
		this.filterModules = filterModules;
	}

	@Override
	public void doFilter(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws TransformerException, IOException {

		if(filterIndex < filterModules.size()){
			
			Entry<FilterModuleDescriptor,FilterModule> entry = filterModules.get(filterIndex);
			
			filterIndex++;
			
			try{
				
				entry.getValue().doFilter(req, res, user, uriParser, this);
				
			}catch(Throwable t){
				
				log.error(t + " thrown by filter module " + entry.getKey() + " for user " + user + " accesing from " + req.getRemoteAddr() + ", skipping filter.", t);
				
				doFilter(req, res, user, uriParser);
			}
			
		}else{
		
			coreServlet.processRequest(req, res, user, uriParser);
		}
	}

}
