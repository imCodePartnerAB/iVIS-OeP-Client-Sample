/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.backgroundmodules;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.hierarchy.basemodules.AnnotatedSectionModule;
import se.unlogic.hierarchy.basemodules.ReflectionInstanceListener;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.BackgroundModule;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleResponse;
import se.unlogic.webutils.http.URIParser;

public abstract class AnnotatedBackgroundModule extends AnnotatedSectionModule<BackgroundModuleDescriptor> implements BackgroundModule{

	@Override
	protected String getStaticContentPrefix() {

		return "b";
	}

	@Override
	public BackgroundModuleResponse processRequest(HttpServletRequest req, User user, URIParser uriParser) throws Exception {

		BackgroundModuleResponse moduleResponse;
		
		if(dependencyLock != null){
			
			dependencyReadLock.lock();
			
			try{
				if(hasRequiredDependencies){
					
					for(ReflectionInstanceListener<?> instanceListener : this.instanceListeners){
						
						if(instanceListener.isRequired() && !instanceListener.hasInstance()){
							
							log.warn("Background module " + moduleDescriptor + " is missing required dependency " + instanceListener.getRawKey().getSimpleName() + ", ignoring request from user " + user);
							return null;
						}
					}
				}			
				
				moduleResponse = processBackgroundRequest(req, user, uriParser);
				
			}finally{

				dependencyReadLock.unlock();
			}
			
		}else{
			
			moduleResponse = processBackgroundRequest(req, user, uriParser);
		}
		
		if(moduleResponse != null){
			setLinksAndScripts(moduleResponse);	
		}
		
		return moduleResponse;
	}

	protected abstract BackgroundModuleResponse processBackgroundRequest(HttpServletRequest req, User user, URIParser uriParser) throws Exception;
}