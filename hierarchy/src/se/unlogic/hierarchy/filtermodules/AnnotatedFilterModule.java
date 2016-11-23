/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.filtermodules;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import se.unlogic.hierarchy.basemodules.FieldInstanceListener;
import se.unlogic.hierarchy.basemodules.MethodInstanceListener;
import se.unlogic.hierarchy.basemodules.ReflectionInstanceListener;
import se.unlogic.hierarchy.core.annotations.InstanceManagerDependency;
import se.unlogic.hierarchy.core.beans.SettingDescriptor;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.FilterChain;
import se.unlogic.hierarchy.core.interfaces.FilterModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.MutableSettingHandler;
import se.unlogic.hierarchy.core.interfaces.SystemInterface;
import se.unlogic.hierarchy.core.utils.ModuleUtils;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.webutils.http.URIParser;

public abstract class AnnotatedFilterModule extends SimpleFilterModule {

	protected List<ReflectionInstanceListener<?>> instanceListeners;
	protected boolean hasRequiredDependencies;

	protected ReentrantReadWriteLock dependencyLock;
	protected Lock readLock;
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(FilterModuleDescriptor moduleDescriptor, SystemInterface systemInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, systemInterface, dataSource);

		parseSettings(moduleDescriptor.getMutableSettingHandler());
		
		ReentrantReadWriteLock dependencyLock = new ReentrantReadWriteLock();

		instanceListeners = getInstanceListeners(dependencyLock.writeLock());

		if(instanceListeners != null){

			for(ReflectionInstanceListener<?> instanceListener : instanceListeners){

				log.debug("Adding instance listener for class " + instanceListener.getRawKey());
				systemInterface.getInstanceHandler().addInstanceListener(instanceListener.getRawKey(), instanceListener);

				if(!hasRequiredDependencies && instanceListener.isRequired()){

					hasRequiredDependencies = true;
				}
			}

			this.dependencyLock = dependencyLock;
			readLock = dependencyLock.readLock();
		}		
	}

	@Override
	public void doFilter(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, FilterChain filterChain) throws Exception {

		if(dependencyLock != null){

			readLock.lock();

			try{
				if(hasRequiredDependencies && !checkRequiredDependencies()){

					filterChain.doFilter(req, res, user, uriParser);
				}

				processFilterRequest(req, res, user, uriParser, filterChain);

			}finally{

				readLock.unlock();
			}

		}else{

			processFilterRequest(req, res, user, uriParser, filterChain);
		}
	}

	public void processFilterRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, FilterChain filterChain){};

	public boolean checkRequiredDependencies() {

		for(ReflectionInstanceListener<?> instanceListener : this.instanceListeners){

			if(instanceListener.isRequired() && !instanceListener.hasInstance()){

				log.error("Module " + moduleDescriptor + " is missing required dependency " + instanceListener.getRawKey().getSimpleName());
				
				return false;
			}
		}
		
		return true;
	}	
	
	@Override
	public void update(FilterModuleDescriptor moduleDescriptor, DataSource dataSource) throws Exception {

		super.update(moduleDescriptor, dataSource);

		parseSettings(moduleDescriptor.getMutableSettingHandler());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void unload() throws Exception {

		if(instanceListeners != null){

			for(ReflectionInstanceListener<?> instanceListener : instanceListeners){

				log.debug("Removing instance listener for class " + instanceListener.getRawKey());
				systemInterface.getInstanceHandler().removeInstanceListener(instanceListener.getRawKey(), instanceListener);
			}
		}
		
		super.unload();
	}

	protected void parseSettings(MutableSettingHandler mutableSettingHandler) {

		ModuleUtils.setModuleSettings(this,AnnotatedFilterModule.class, mutableSettingHandler, systemInterface);
	}	
	
	@Override
	public List<SettingDescriptor> getSettings() {

		ArrayList<SettingDescriptor> settingDescriptors = new ArrayList<SettingDescriptor>();

		ModuleUtils.addSettings(settingDescriptors, super.getSettings());

		try {
			ModuleUtils.addSettings(settingDescriptors, ModuleUtils.getAnnotatedSettingDescriptors(this,AnnotatedFilterModule.class, systemInterface));

		} catch (Exception e){

			throw new RuntimeException(e);
		}

		return settingDescriptors;
	}
	
	@SuppressWarnings("rawtypes")
	private List<ReflectionInstanceListener<?>> getInstanceListeners(Lock writeLock) {

		List<ReflectionInstanceListener<?>> instanceListeners = new ArrayList<ReflectionInstanceListener<?>>();

		List<Field> fields = ReflectionUtils.getFields(this.getClass());

		for(Field field : fields){

			InstanceManagerDependency annotation = field.getAnnotation(InstanceManagerDependency.class);

			if(annotation == null){

				continue;
			}

			instanceListeners.add(new FieldInstanceListener(this, field, annotation.required(),writeLock));
		}

		List<Method> methods = ReflectionUtils.getMethods(this.getClass());

		for(Method method : methods){

			InstanceManagerDependency annotation = method.getAnnotation(InstanceManagerDependency.class);

			if(annotation == null){

				continue;
			}

			instanceListeners.add(new MethodInstanceListener(this, method, annotation.required(),writeLock));
		}

		if(instanceListeners.isEmpty()){

			return null;
		}

		return instanceListeners;
	}	
}
