/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

import se.unlogic.standardutils.callback.Callback;
import se.unlogic.standardutils.streams.StreamUtils;

/**
 * Utility class for handling files and folders
 * 
 * @author Robert "Unlogic" Olofsson (unlogic@unlogic.se)
 * 
 */
public class FileUtils {

	public static String toAsciiFilename(String string) {

		return string.replaceAll("[^0-9a-zA-Z-.]", "_");
	}

	public static String toValidHttpFilename(String string) {

		return string.replaceAll("[^0-9a-öA-Ö-+. ()-+!@é&%$§=]|[~]", "_");
	}

	public final static byte[] getRawBytes(File f) throws IOException {

		FileInputStream fin = new FileInputStream(f);
		byte[] buffer = new byte[(int) f.length()];
		fin.read(buffer);
		fin.close();
		return buffer;
	}

	public static String getFileExtension(File file) {

		return getFileExtension(file.getName());
	}

	public static String getFileExtension(String filename) {

		int dotIndex = filename.lastIndexOf(".");

		if (dotIndex == -1 || (dotIndex + 1) == filename.length()) {
			return null;
		} else {
			return filename.substring(dotIndex + 1);
		}
	}

	public static boolean fileExists(String path) {

		if(path == null){
			
			return false;
		}
		
		File file = new File(path);

		return file.exists();
	}

	public static boolean isReadable(String path) {

		return isReadable(new File(path));
	}

	public static boolean isReadable(File file) {

		if (file.exists() && file.canRead()) {
			return true;
		}

		return false;
	}

	/**
	 * Removes all files in the given directory matching the given filter
	 * 
	 * @param directory the directory to be cleared
	 * @param filter {@link FileFilter} used to filter files
	 * @param recursive controls weather files should be deleted from sub directories too
	 */
	public static int deleteFiles(String directory, FileFilter filter, boolean recursive) {

		return deleteFiles(new File(directory), filter, recursive);
	}

	public static int deleteFiles(File dir, FileFilter filter, boolean recursive) {

		if (dir.exists() && dir.isDirectory()) {

			int deletedFiles = 0;

			File[] files = dir.listFiles(filter);

			for (File file : files) {

				if (file.isDirectory()) {

					if (recursive) {

						deletedFiles += deleteFiles(file, filter, recursive);
					}

				} else if(filter == null || filter.accept(file)) {

					if (file.delete()) {

						deletedFiles++;
					}
				}
			}

			return deletedFiles;
		}

		return 0;
	}

	public static int replace(File dir, String filename, File replacementFile, boolean recursive, boolean caseSensitive, Callback<File> callback) {

		if (dir.exists() && dir.isDirectory()) {

			int replacedFiles = 0;

			File[] files = dir.listFiles();

			for (File file : files) {

				if (file.isDirectory()) {

					if (recursive) {

						replacedFiles += replace(file, filename, replacementFile, recursive, caseSensitive, callback);
					}

				} else {

					if (caseSensitive) {

						if (!file.getName().equals(filename)) {

							continue;
						}

					} else if (!file.getName().equalsIgnoreCase(filename)) {

						continue;
					}

					if (file.canWrite()) {

						try {
							if (callback != null) {
								callback.callback(file);
							}

							replaceFile(file, replacementFile);

							replacedFiles++;

						} catch (IOException e) {}
					}
				}
			}

			return replacedFiles;
		}

		return 0;
	}

	public static void replaceFile(File target, File replacement) throws IOException {

		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;

		try {
			inputStream = new FileInputStream(replacement);
			outputStream = new FileOutputStream(target);

			inputStream.getChannel().transferTo(0, replacement.length(), outputStream.getChannel());

			StreamUtils.transfer(inputStream, outputStream);

		} finally {
			StreamUtils.closeStream(inputStream);
			StreamUtils.closeStream(outputStream);
		}
	}

	public static boolean deleteDirectory(String directoryPath) {

		return deleteDirectory(new File(directoryPath));
	}

	public static boolean deleteDirectory(File directory) {

		if (directory.exists()) {

			File[] files = directory.listFiles();

			for (File file : files) {

				if (file.isDirectory()) {
					deleteDirectory(file);
				} else {
					file.delete();
				}
			}
		}
		return directory.delete();
	}

	public static boolean deleteFile(String path) {

		return deleteFile(new File(path));
	}

	public static boolean deleteFile(File file) {

		if (file != null && file.exists()) {
			return file.delete();
		}

		return false;
	}

	public static void writeFile(String filePath, byte[] data) throws IOException {

		writeFile(new File(filePath), new ByteArrayInputStream(data), true);
	}

	public static void writeFile(File file, byte[] data) throws IOException {

		writeFile(file, new ByteArrayInputStream(data), true);
	}

	public static void writeFile(String filePath, InputStream inputStream, boolean closeInputStream) throws IOException {

		writeFile(new File(filePath), inputStream, closeInputStream);
	}

	public static void writeFile(File file, InputStream inputStream, boolean closeInputStream) throws IOException {

		FileOutputStream fileOutputStream = null;

		try {
			fileOutputStream = new FileOutputStream(file);

			StreamUtils.transfer(inputStream, fileOutputStream);

		} finally {

			if (closeInputStream) {
				StreamUtils.closeStream(inputStream);
			}

			StreamUtils.closeStream(fileOutputStream);
		}
	}

	public static void downloadFile(String url, File destination) throws MalformedURLException, IOException {

		downloadFile(new URL(url), destination);
	}

	public static void downloadFile(URL url, File destination) throws IOException {

		ReadableByteChannel channel = null;
		FileOutputStream fileOutputStream = null;

		try {
			channel = Channels.newChannel(url.openStream());

			destination.getParentFile().mkdirs();

			fileOutputStream = new FileOutputStream(destination);

			fileOutputStream.getChannel().transferFrom(channel, 0, BinarySizes.GigaByte);
		} finally {

			StreamUtils.closeStream(fileOutputStream);
			ChannelUtils.closeChannel(channel);
		}
	}

	public static void writeFile(String file, String content) throws IOException {

		FileOutputStream fileOutputStream = null;
		ByteArrayInputStream inputStream = null;

		try {
			fileOutputStream = new FileOutputStream(file);
			inputStream = new ByteArrayInputStream(content.getBytes());

			StreamUtils.transfer(inputStream, fileOutputStream);

		} finally {

			StreamUtils.closeStream(inputStream);
			StreamUtils.closeStream(fileOutputStream);
		}
	}

	public static String replaceFileExtension(String filename, String newExtension) {

		int dotIndex = filename.lastIndexOf(".");

		if (dotIndex == -1 || (dotIndex + 1) == filename.length()) {

			return filename + "." + newExtension;

		} else {

			return filename.substring(0, dotIndex) + "." + newExtension;
		}
	}

	public static void copyFile(File sourceFile, File destFile) throws IOException {

		if (!destFile.exists()) {
			destFile.createNewFile();
		}

		FileInputStream inputStream = null;
		FileChannel source = null;

		FileOutputStream outputStream = null;
		FileChannel destination = null;

		try {
			inputStream = new FileInputStream(sourceFile);
			source = inputStream.getChannel();

			outputStream = new FileOutputStream(destFile);
			destination = outputStream.getChannel();

			//The return value from the transferFrom method call below should be checked so that the whole file is transfered
			long bytesTransfered = destination.transferFrom(source, 0, source.size());

			if(bytesTransfered != sourceFile.length()){

				throw new RuntimeException("Only " + bytesTransfered + " out of " + sourceFile.length() + " bytes transfered!");
			}

		} finally {

			ChannelUtils.closeChannel(source);
			StreamUtils.closeStream(inputStream);

			ChannelUtils.closeChannel(destination);
			StreamUtils.closeStream(outputStream);
		}
	}

	public static void moveFile(File sourceFile, File destFile) throws IOException {

		if(!sourceFile.renameTo(destFile)){

			throw new IOException("Moving of file " + sourceFile + " to " + destFile + " failed");
		}
	}

	public static void createMissingDirectories(File file) {

		if (!file.getParentFile().exists()) {

			file.getParentFile().mkdirs();
		}
	}
}
