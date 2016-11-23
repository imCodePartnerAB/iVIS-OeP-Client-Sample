package se.unlogic.webutils.fileicons;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

import se.unlogic.standardutils.io.FileUtils;
import se.unlogic.standardutils.streams.StreamUtils;


public class FileIconHandler {

	private static final String DEFAULT_ICON = "icons/file.png";
	
	public static URL getIconByFilename(String filename){
		
		String fileExtension = FileUtils.getFileExtension(filename);
		
		if(fileExtension == null){
			
			return getDefaultIcon();
		}
		
		return getIcon(fileExtension);
	}

	public static URL getIcon(String fileExtension) {

		URL url = FileIconHandler.class.getResource("icons/" + fileExtension.toLowerCase() + ".png");
		
		if(url == null){
			
			return getDefaultIcon();
		}
		
		return url;
	}

	public static URL getDefaultIcon() {

		return FileIconHandler.class.getResource(DEFAULT_ICON);
	}
	
	public static void streamIcon(String filename, HttpServletResponse res) throws IOException{
		
		URL url = getIconByFilename(filename);
		
		InputStream inputStream = null;
		OutputStream outputStream = null;
		
		try{
			inputStream = url.openStream();
			outputStream = res.getOutputStream();
			
			StreamUtils.transfer(inputStream, outputStream);
			
			outputStream.flush();
		}finally{
			
			StreamUtils.closeStream(inputStream);
			StreamUtils.closeStream(outputStream);
		}
	}
}
