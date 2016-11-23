package se.unlogic.webutils.http;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class CapturingFilterChain implements FilterChain {

	protected ServletRequest request;
	protected ServletResponse response;

	public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {

		this.request = request;
		this.response = response;
	}

	public ServletRequest getRequest() {

		return request;
	}

	public ServletResponse getResponse() {

		return response;
	}
}
