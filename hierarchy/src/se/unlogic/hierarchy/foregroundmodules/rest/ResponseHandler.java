package se.unlogic.hierarchy.foregroundmodules.rest;

import javax.servlet.http.HttpServletResponse;


public interface ResponseHandler<T> {

	public Class<? extends T> getType();
	
	public void handleResponse(T type, HttpServletResponse res) throws Exception;
}
