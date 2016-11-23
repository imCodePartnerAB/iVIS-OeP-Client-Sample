package se.unlogic.hierarchy.core.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.comparators.PriorityComparator;
import se.unlogic.hierarchy.core.interfaces.LoginProvider;
import se.unlogic.standardutils.enums.Order;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.webutils.http.URIParser;


public class LoginHandler {

	private static final PriorityComparator PRIORITY_COMPARATOR = new PriorityComparator(Order.ASC);

	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final Lock r = rwl.readLock();
	private final Lock w = rwl.writeLock();

	private final Logger log = Logger.getLogger(this.getClass());
	private final ArrayList<LoginProvider> loginProviders = new ArrayList<LoginProvider>();

	public boolean addProvider(LoginProvider loginProvider) {

		if(loginProvider == null){

			return false;
		}

		w.lock();
		try {

			if (!loginProviders.contains(loginProvider)) {

				loginProviders.add(loginProvider);

				Collections.sort(loginProviders, PRIORITY_COMPARATOR);

				return true;

			}else{

				Collections.sort(loginProviders, PRIORITY_COMPARATOR);

				return false;
			}

		} finally {
			w.unlock();
		}
	}

	public ArrayList<LoginProvider> getProviders() {

		r.lock();
		try {
			return new ArrayList<LoginProvider>(this.loginProviders);
		} finally {
			r.unlock();
		}
	}

	public LoginProvider getProvider(String providerID) {

		r.lock();
		try {
			
			for(LoginProvider loginProvider : loginProviders){
				
				if(loginProvider.getProviderDescriptor().getID().equals(providerID)){
					
					return loginProvider;
				}
			}
			
		} finally {
			r.unlock();
		}
		
		return null;
	}	
	
	public boolean removeProvider(LoginProvider loginProvider) {

		w.lock();
		try {

			return this.loginProviders.remove(loginProvider);

		} finally {
			w.unlock();
		}
	}

	public void processLoginRequest(HttpServletRequest req, HttpServletResponse res, URIParser uriParser, boolean redirectBack){

		r.lock();
		try {

			for(LoginProvider loginProvider : loginProviders){

				try {

					if(!loginProvider.supportsRequest(req, uriParser)){

						continue;
					}

				} catch (Throwable e) {

					log.error("Error in login provider " + loginProvider + " while checking support of request from " + req.getRemoteAddr(), e);
				}

				try {
					
					String redirectURI;
					
					if(redirectBack){
						
						redirectURI = getRedirectURI(req, uriParser);
						
					}else{
						
						redirectURI = null;
					}
					
					loginProvider.handleRequest(req, res, uriParser, redirectURI);

					return;

				} catch (Throwable e) {

					if(res.isCommitted()){

						log.error("Error in login provider " + loginProvider + " after response has been commited while handling request from " + req.getRemoteAddr(), e);

						return;
					}

					log.error("Error in login provider " + loginProvider + " while handling request from " + req.getRemoteAddr(), e);
				}
			}

		} finally {
			r.unlock();
		}
	}

	public String getRedirectURI(HttpServletRequest req, URIParser uriParser) {

		if(!StringUtils.isEmpty(uriParser.getFormattedURI())){

			String redirect = uriParser.getFormattedURI();

			if(!StringUtils.isEmpty(req.getQueryString())){

				redirect += "?" + req.getQueryString();
			}

			return redirect;
		}	
		
		return null;
	}

	public List<LoginProvider> getSupportedLoginProviders(HttpServletRequest req, URIParser uriParser){

		ArrayList<LoginProvider> supportedProviders = new ArrayList<LoginProvider>(this.loginProviders.size());

		r.lock();
		try {

			for(LoginProvider loginProvider : loginProviders){

				try {

					if(loginProvider.supportsRequest(req, uriParser)){

						supportedProviders.add(loginProvider);
					}

				} catch (Throwable e) {

					log.error("Error in login provider " + loginProvider + " while checking support of request from " + req.getRemoteAddr(), e);
				}
			}

		} finally {
			r.unlock();
		}

		return supportedProviders;
	}

	public LoginProvider getSupportedLoginProvider(HttpServletRequest req, URIParser uriParser){

		r.lock();
		try {

			for(LoginProvider loginProvider : loginProviders){

				try {

					if(loginProvider.supportsRequest(req, uriParser)){

						return loginProvider;
					}

				} catch (Throwable e) {

					log.error("Error in login provider " + loginProvider + " while checking support of request from " + req.getRemoteAddr(), e);
				}
			}

		} finally {
			r.unlock();
		}

		return null;
	}

	public boolean loginUser(HttpServletRequest req, URIParser uriParser, User user){

		r.lock();
		try {

			for(LoginProvider loginProvider : loginProviders){

				try {

					if(loginProvider.loginUser(req, uriParser, user)){

						return true;
					}

				} catch (Throwable e) {

					log.error("Error in login provider " + loginProvider + " while logging in user " + user, e);
				}
			}

		} finally {
			r.unlock();
		}

		return false;
	}

	public void sortProviders() {

		w.lock();
		try {

			Collections.sort(loginProviders, PRIORITY_COMPARATOR);

		} finally {
			w.unlock();
		}
	}
}
