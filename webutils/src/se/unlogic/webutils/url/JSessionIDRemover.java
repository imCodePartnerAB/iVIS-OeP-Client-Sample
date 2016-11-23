package se.unlogic.webutils.url;

import se.unlogic.webutils.http.URIParser;


public class JSessionIDRemover {

	public static String remove(URIParser uriParser, int index) {

		if(uriParser.size() < (index+1)){
			return null;
		}

		String uriPart = uriParser.get(index);

		int start = uriPart.indexOf(";jsessionid=");

		if(start == -1){

			return uriPart;
		}

		return uriPart.substring(0, start);
	}

}
