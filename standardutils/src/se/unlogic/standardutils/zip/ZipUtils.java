package se.unlogic.standardutils.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import se.unlogic.standardutils.streams.StreamUtils;


public class ZipUtils {

	public static void addFile(File file, ZipOutputStream outputStream) throws IOException{

		ZipEntry zipEntry = new ZipEntry(file.getName());

		FileInputStream inputStream = null;

		try{
			inputStream = new FileInputStream(file);

			outputStream.putNextEntry(zipEntry);

			StreamUtils.transfer(inputStream, outputStream);

			outputStream.closeEntry();
		}finally{

			StreamUtils.closeStream(inputStream);
		}
	}

	public static void addEntry(String filename, InputStream inputStream, ZipOutputStream outputStream) throws IOException{

		ZipEntry zipEntry = new ZipEntry(filename);

		try{
			outputStream.putNextEntry(zipEntry);

			StreamUtils.transfer(inputStream, outputStream);

			outputStream.closeEntry();
		}finally{

			StreamUtils.closeStream(inputStream);
		}
	}

	public static void addFiles(File[] files, ZipOutputStream outputStream) throws IOException{

		for(File file : files){

			addFile(file, outputStream);
		}
	}
}
