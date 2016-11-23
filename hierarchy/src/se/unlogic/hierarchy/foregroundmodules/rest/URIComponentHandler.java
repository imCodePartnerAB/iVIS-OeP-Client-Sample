package se.unlogic.hierarchy.foregroundmodules.rest;

import java.util.List;

import se.unlogic.webutils.http.URIParser;


public class URIComponentHandler {

	private final List<URIComponent> components;

	public URIComponentHandler(List<URIComponent> components) {

		this.components = components;
	}
	
	public boolean matches(URIParser uriParser, Object[] paramArray){
		
		for(URIComponent component : components){
			
			if(!component.matches(uriParser.get(0), paramArray)){
				
				return false;
			}
			
			uriParser = uriParser.getNextLevel();
		}
		
		return true;
	}
	
	public int getComponentCount(){
		
		return components.size();
	}
}
