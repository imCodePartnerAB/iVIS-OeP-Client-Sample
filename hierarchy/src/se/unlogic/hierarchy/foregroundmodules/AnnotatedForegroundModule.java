/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import se.unlogic.hierarchy.basemodules.AnnotatedSectionModule;
import se.unlogic.hierarchy.basemodules.ReflectionInstanceListener;
import se.unlogic.hierarchy.core.annotations.EnumDropDownSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.beans.Breadcrumb;
import se.unlogic.hierarchy.core.beans.SimpleMenuItemDescriptor;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.MenuItemType;
import se.unlogic.hierarchy.core.enums.URLType;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.exceptions.ModuleConfigurationException;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.interfaces.BundleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModule;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.MenuItemDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.webutils.http.URIParser;

public abstract class AnnotatedForegroundModule extends AnnotatedSectionModule<ForegroundModuleDescriptor> implements ForegroundModule {

	protected static final Class<?>[] PARAMETER_TYPES = { HttpServletRequest.class, HttpServletResponse.class, User.class, URIParser.class };
	protected static final Class<?> RETURN_TYPE = ForegroundModuleResponse.class;

	protected final HashMap<String, MethodMapping> methodMap = new HashMap<String, MethodMapping>();

	@ModuleSetting
	@EnumDropDownSettingDescriptor(name = "Menuitem type", description = "The type of menuitem this module should display itself as in the menu", required = true)
	protected MenuItemType menuItemType = MenuItemType.MENUITEM;

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		Method[] methods = this.getClass().getMethods();

		for (Method method : methods) {

			WebPublic annotation;

			if ((annotation = method.getAnnotation(WebPublic.class)) == null) {

				log.debug("Method " + method.getName() + " is NOT web public, skipping.");

			} else {

				if (!this.getReturnType().isAssignableFrom(method.getReturnType())) {

					log.error("Method " + method.getName() + " has invalid responsetype, skipping.");

				} else if (!Arrays.equals(method.getParameterTypes(), this.getParameterTypes())) {

					log.error("Method " + method.getName() + " has invalid parametertypes, skipping.");

				} else {

					MethodMapping methodMapping = new MethodMapping(method, annotation);

					if (!StringUtils.isEmpty(annotation.alias())) {

						log.debug("Caching method " + method.getName() + " with alias " + annotation.alias());
						this.methodMap.put(annotation.alias(), methodMapping);

					} else if (annotation.toLowerCase()) {

						String alias = method.getName().toLowerCase();

						log.debug("Caching method " + method.getName() + " with alias " + alias);

						this.methodMap.put(alias, methodMapping);

					} else {
						log.debug("Caching method " + method.getName());

						this.methodMap.put(method.getName(), methodMapping);
					}
				}
			}
		}

		super.init(moduleDescriptor, sectionInterface, dataSource);
	}

	@Override
	public ForegroundModuleResponse processRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {

		if (dependencyLock != null) {

			dependencyReadLock.lock();

			try {
				if (hasRequiredDependencies) {

					checkRequiredDependencies();
				}

				return processForegroundRequest(req, res, user, uriParser);

			} finally {

				dependencyReadLock.unlock();
			}

		} else {

			return processForegroundRequest(req, res, user, uriParser);
		}

	}

	public void checkRequiredDependencies() throws ModuleConfigurationException {

		for (ReflectionInstanceListener<?> instanceListener : this.instanceListeners) {

			if (instanceListener.isRequired() && !instanceListener.hasInstance()) {

				throw new ModuleConfigurationException("Missing required dependency " + instanceListener.getRawKey().getSimpleName());
			}
		}
	}

	protected ForegroundModuleResponse processForegroundRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {

		String method = getMethod(req, uriParser);

		if (method != null) {

			MethodMapping methodMapping = this.methodMap.get(method);

			if (methodMapping == null) {

				return this.methodNotFound(req, res, user, uriParser);

			} else {

				if (user == null && methodMapping.getAnnotation().requireLogin()) {

					throw new AccessDeniedException("Login required");
				}

				try {
					ForegroundModuleResponse moduleResponse = this.invoke(methodMapping.getMethod(), req, res, user, uriParser);

					if (moduleResponse != null) {

						if (this.scripts != null && methodMapping.getAnnotation().autoAppendScripts()) {

							moduleResponse.addScripts(scripts);
						}

						if (this.links != null && methodMapping.getAnnotation().autoAppendLinks()) {

							moduleResponse.addLinks(links);
						}
					}

					return moduleResponse;

				} catch (InvocationTargetException e) {

					if (e.getCause() != null && e.getCause() instanceof Exception) {
						throw (Exception) e.getCause();
					} else {
						throw e;
					}
				}
			}

		} else {
			ForegroundModuleResponse moduleResponse = this.defaultMethod(req, res, user, uriParser);

			if (moduleResponse != null) {

				setLinksAndScripts(moduleResponse);
			}

			return moduleResponse;
		}
	}

	protected String getMethod(HttpServletRequest req, URIParser uriParser) {

		if (uriParser.size() > 1) {

			return uriParser.get(1);
		}

		return null;
	}

	protected Class<?> getReturnType() {

		return RETURN_TYPE;
	}

	protected Class<?>[] getParameterTypes() {

		return PARAMETER_TYPES;
	}

	protected ForegroundModuleResponse invoke(Method method, HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {

		try {
			return (ForegroundModuleResponse) method.invoke(this, req, res, user, uriParser);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	public ForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {

		return null;
	}

	public ForegroundModuleResponse methodNotFound(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {

		throw new URINotFoundException(uriParser);
	}

	public void redirectToMethod(HttpServletRequest req, HttpServletResponse res, String alias) throws IOException {

		res.sendRedirect(this.getModuleURI(req) + alias);
	}

	public void redirectToDefaultMethod(HttpServletRequest req, HttpServletResponse res) throws IOException {

		res.sendRedirect(this.getModuleURI(req));
	}

	public void redirectToDefaultMethod(HttpServletRequest req, HttpServletResponse res, String anchor) throws IOException {

		res.sendRedirect(this.getModuleURI(req) + "#" + anchor);
	}

	protected String getModuleURI(HttpServletRequest req) {

		return req.getContextPath() + this.getFullAlias();
	}

	public String getFullAlias() {

		return this.sectionInterface.getSectionDescriptor().getFullAlias() + "/" + this.moduleDescriptor.getAlias();
	}

	@Override
	protected String getStaticContentPrefix() {

		return "f";
	}

	@Override
	public List<? extends MenuItemDescriptor> getVisibleMenuItems() {

		if (this.moduleDescriptor.isVisibleInMenu()) {
			return this.getAllMenuItems();
		} else {
			return null;
		}
	}

	@Override
	public List<? extends MenuItemDescriptor> getAllMenuItems() {

		SimpleMenuItemDescriptor menuItemDescriptor = new SimpleMenuItemDescriptor();

		menuItemDescriptor.setName(this.moduleDescriptor.getName());
		menuItemDescriptor.setUrl(this.getFullAlias());
		menuItemDescriptor.setUrlType(URLType.RELATIVE_FROM_CONTEXTPATH);
		menuItemDescriptor.setUniqueID(this.moduleDescriptor.getModuleID().toString());
		menuItemDescriptor.setDescription(this.moduleDescriptor.getDescription());
		menuItemDescriptor.setItemType(menuItemType);
		menuItemDescriptor.setAccess(this.moduleDescriptor);

		return Collections.singletonList((MenuItemDescriptor) menuItemDescriptor);
	}

	@Override
	public List<? extends BundleDescriptor> getAllBundles() {

		return this.getVisibleBundles();
	}

	@Override
	public List<? extends BundleDescriptor> getVisibleBundles() {

		return null;
	}

	public Breadcrumb getDefaultBreadcrumb() {

		return new Breadcrumb(sectionInterface.getSectionDescriptor(), moduleDescriptor);
	}
}
