/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.mime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import se.unlogic.standardutils.io.FileUtils;

public class MimeUtils {

	private static String UNKNOWN_MIME_TYPE = "application/x-unknown";
	private static Properties MIME_TYPES = new Properties();;

	static{
		try {
			MIME_TYPES.load(MimeUtils.class.getResourceAsStream("mimetypes.properties"));
		} catch (IOException e) {}
	}

	public static String getMimeType(File file){
		return getMimeType(file.getName());
	}

	public static String getMimeType(String filename){

		String fileExtension = FileUtils.getFileExtension(filename);

		if(fileExtension == null){
			return UNKNOWN_MIME_TYPE;
		}else{
			return MIME_TYPES.getProperty(fileExtension.toLowerCase(),UNKNOWN_MIME_TYPE);
		}
	}

	public static int getMimeTypeCount(){
		return MIME_TYPES.size();
	}

	public static Set<Entry<Object, Object>> getMIME_TYPES(){
		return MIME_TYPES.entrySet();
	}

	public static void loadMimeTypes(InputStream inputStream) throws IOException{
		MIME_TYPES.clear();
		MIME_TYPES.load(inputStream);
	}

	/* J2SE 1.6 only!
	public void loadMimeTypes(Reader reader) throws IOException {
		MimeTypes.load(reader);
	}
	 */

	public void loadMimeTypesFromXML(InputStream in) throws IOException, InvalidPropertiesFormatException {
		MIME_TYPES.loadFromXML(in);
	}
}
