package se.unlogic.webutils.http;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class ResponseUtils {

	public static void sendRedirect(HttpServletResponse res, String redirect) {

		try{
			res.sendRedirect(redirect);
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}

}
