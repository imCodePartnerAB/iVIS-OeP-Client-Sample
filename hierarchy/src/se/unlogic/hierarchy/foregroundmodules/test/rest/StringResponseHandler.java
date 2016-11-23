package se.unlogic.hierarchy.foregroundmodules.test.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import se.unlogic.hierarchy.foregroundmodules.rest.ResponseHandler;


public class StringResponseHandler implements ResponseHandler<String> {

	@Override
	public Class<? extends String> getType() {

		return String.class;
	}

	@Override
	public void handleResponse(String type, HttpServletResponse res) throws IOException {

		res.setContentType("text/html");
		
		res.getWriter().write(type);
		res.getWriter().flush();
	}

}
