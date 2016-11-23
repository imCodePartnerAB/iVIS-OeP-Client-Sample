/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.cache;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;

import org.apache.log4j.Logger;

import se.unlogic.hierarchy.core.enums.PathType;
import se.unlogic.hierarchy.core.exceptions.ResourceNotFoundException;
import se.unlogic.hierarchy.core.interfaces.BackgroundModule;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleCacheListener;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ModuleTransformerCache;
import se.unlogic.standardutils.xml.ClassPathURIResolver;
import se.unlogic.standardutils.xsl.FileXSLTransformer;
import se.unlogic.standardutils.xsl.URIXSLTransformer;
import se.unlogic.standardutils.xsl.XSLTransformer;

public class BackgroundModuleXSLTCache implements BackgroundModuleCacheListener, ModuleTransformerCache<BackgroundModuleDescriptor>{

	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final Lock r = rwl.readLock();
	private final Lock w = rwl.writeLock();
	private final HashMap<BackgroundModuleDescriptor,XSLTransformer> xslMap = new HashMap<BackgroundModuleDescriptor,XSLTransformer>();
	private final String applicationFileSystemPath;
	private final Logger log = Logger.getLogger(this.getClass());

	public BackgroundModuleXSLTCache(String applicationFileSystemPath) {
		this.applicationFileSystemPath = applicationFileSystemPath;
	}

	public void clearModules() {
		w.lock();
		try{
			log.debug("Clearing module XSL cache");
			this.xslMap.clear();
		}finally{
			w.unlock();
		}
	}

	@Override
	public void moduleCached(BackgroundModuleDescriptor mb, BackgroundModule moduleInstance) {
		w.lock();
		try{
			XSLTransformer xsltCacher = this.getCachedXslStyleSheet(mb);

			if(xsltCacher != null){
				log.debug("Caching XSL stylesheet for " + mb);
				this.xslMap.put(mb, xsltCacher);
			}
		} catch (TransformerConfigurationException e) {
			log.error("Error caching XSL stylesheet for " + mb + ", " + e);
		} catch (ClassNotFoundException e) {
			log.error("Error caching XSL stylesheet for " + mb + ", " + e);
		} catch (URISyntaxException e) {
			log.error("Error caching XSL stylesheet for " + mb + ", " + e);
		} catch (ResourceNotFoundException e) {
			log.error("Error caching XSL stylesheet for " + mb + ", " + e);
		}finally{
			w.unlock();
		}
	}

	@Override
	public void moduleUnloaded(BackgroundModuleDescriptor mb, BackgroundModule moduleInstance){
		w.lock();
		try{
			if(this.xslMap.containsKey(mb)){
				log.debug("Unloading XSL stylesheet for " + mb);
				this.xslMap.remove(mb);
			}
		}finally{
			w.unlock();
		}
	}

	@Override
	public void moduleUpdated(BackgroundModuleDescriptor mb, BackgroundModule moduleInstance){
		w.lock();
		try{
			if(mb.hasStyleSheet()){
				if(this.xslMap.containsKey(mb)){
					BackgroundModuleDescriptor oldmb = null;

					for(BackgroundModuleDescriptor mbx : this.xslMap.keySet()){
						if(mbx.equals(mb)){
							oldmb = mbx;
							break;
						}
					}

					if(oldmb.getXslPathType().equals(mb.getXslPathType()) && oldmb.getXslPath().equals(mb.getXslPath())){
						try {
							log.debug("Reloading XSL stylesheet for " + mb);
							this.xslMap.get(mb).reloadStyleSheet();
						} catch (TransformerConfigurationException e) {
							log.error("Error reloading XSL stylesheet for " + mb + ", " + e);
						}
					}else{
						this.moduleCached(mb, moduleInstance);
					}
				}else{
					this.moduleCached(mb, moduleInstance);
				}
			}else{
				this.moduleUnloaded(mb, moduleInstance);
			}
		}finally{
			w.unlock();
		}
	}

	private XSLTransformer getCachedXslStyleSheet(BackgroundModuleDescriptor mb) throws TransformerConfigurationException, ClassNotFoundException, URISyntaxException, ResourceNotFoundException{

		if(mb.getXslPath() != null && mb.getXslPathType() != null){

			if(mb.getXslPathType() == PathType.Filesystem){

				return new FileXSLTransformer(new File(mb.getXslPath()),ClassPathURIResolver.getInstance());

			}else if(mb.getXslPathType() == PathType.RealtiveFilesystem){

				return new FileXSLTransformer(new File(this.applicationFileSystemPath + mb.getXslPath()),ClassPathURIResolver.getInstance());

			}else if(mb.getXslPathType() == PathType.Classpath){

				URL styleSheetURL = Class.forName(mb.getClassname()).getResource(mb.getXslPath());

				if(styleSheetURL == null){

					throw new ResourceNotFoundException(mb.getClassname(), mb.getXslPath());

				}else{

					return new URIXSLTransformer(Class.forName(mb.getClassname()).getResource(mb.getXslPath()).toURI(),ClassPathURIResolver.getInstance(), true);
				}
			}
		}
		return null;
	}

	public Transformer getModuleTranformer(BackgroundModuleDescriptor mb) throws TransformerConfigurationException{
		r.lock();
		try{
			XSLTransformer xsltCacher = this.xslMap.get(mb);

			if(xsltCacher != null){
				return xsltCacher.getTransformer();
			}else{
				return null;
			}
		}finally{
			r.unlock();
		}
	}
}
