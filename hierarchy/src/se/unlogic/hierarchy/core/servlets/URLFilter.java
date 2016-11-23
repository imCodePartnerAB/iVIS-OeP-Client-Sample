/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.servlets;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.unlogic.webutils.http.StatusCapturingResponse;

// Filter that caches all 404'not coming from the core servlet

public class URLFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {

		if (req instanceof HttpServletRequest && res instanceof HttpServletResponse && !((HttpServletRequest) req).getRequestURI().startsWith(((HttpServletRequest) req).getContextPath() + "/core") && !((HttpServletRequest) req).getRequestURI().equals(((HttpServletRequest) req).getContextPath() + "/")) {

			StatusCapturingResponse statusCapturingResponse = null;
			
			if(((HttpServletRequest) req).getMethod().equalsIgnoreCase("GET")) {
				
				statusCapturingResponse = new StatusCapturingResponse((HttpServletResponse) res, 404);

				filterChain.doFilter(req, statusCapturingResponse);
				
			}
			
			boolean processed = req.getAttribute("processed") != null;
			
			if ((statusCapturingResponse == null || statusCapturingResponse.getStatus() == 404) && !processed) {

				if (((HttpServletRequest) req).getContextPath().equals("")) {
					req.getRequestDispatcher("/core" + ((HttpServletRequest) req).getRequestURI()).forward(req, res);
				} else {
					req.getRequestDispatcher("/core" + ((HttpServletRequest) req).getRequestURI().substring(((HttpServletRequest) req).getContextPath().length())).forward(req, res);
				}

			} else if(!processed) {

				filterChain.doFilter(req, res);
			}
			
		} else {

			filterChain.doFilter(req, res);
		}
	}

	@Override
	public void destroy() {

	}
}
