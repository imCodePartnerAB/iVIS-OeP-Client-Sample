package se.unlogic.hierarchy.core.utils;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import se.unlogic.fileuploadutils.MultipartRequest;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.date.PooledSimpleDateFormat;
import se.unlogic.standardutils.image.ImageUtils;
import se.unlogic.standardutils.io.BinarySizes;
import se.unlogic.standardutils.io.FileUtils;
import se.unlogic.standardutils.json.JsonArray;
import se.unlogic.standardutils.json.JsonNode;
import se.unlogic.standardutils.json.JsonObject;
import se.unlogic.standardutils.json.OrderedJsonObject;
import se.unlogic.standardutils.streams.StreamUtils;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.webutils.http.HTTPUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.http.enums.ContentDisposition;

public class FCKConnector {

	private final Logger log = Logger.getLogger(FCKConnector.class);

	private static final PooledSimpleDateFormat RFC1123_DATE_FORMATTER = new PooledSimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US, TimeZone.getTimeZone("GMT"));

	private static final String FILE_ICON_PACKAGE = "/ckeditor/filemanager/images/fileicons";

	private String filestorePath;

	private int diskThreshold = 50;

	private int ramThreshold = 10;

	/**
	 * @param filestorePath path to filestore
	 * @param diskThreshold Maximum size of the request in MB
	 * @param ramThreshold Maximum size of files in KB to be buffered in RAM during file uploads. Files exceeding the threshold are written to disk instead
	 */
	public FCKConnector(String filestorePath, int diskThreshold, int ramThreshold) {

		this.filestorePath = filestorePath;
		this.diskThreshold = diskThreshold;
		this.ramThreshold = ramThreshold;
	}

	public FCKConnector(String filestorePath) {

		this.filestorePath = filestorePath;
	}

	public void processRequest(HttpServletRequest req, HttpServletResponse res, URIParser uriParser, User user, ForegroundModuleDescriptor moduleDescriptor) throws IOException {

		this.processRequest(req, res, uriParser, user, moduleDescriptor, "/file");

	}

	public void processRequest(HttpServletRequest req, HttpServletResponse res, URIParser uriParser, User user, ForegroundModuleDescriptor moduleDescriptor, String fileMethodAlias) throws IOException {

		JsonNode response = null;

		boolean putTextarea = false;

		if (StringUtils.isEmpty(this.filestorePath)) {

			response = this.sendError("CONFIGURATION_ERROR");

		} else {

			if (req.getMethod().equalsIgnoreCase("POST")) {

				response = this.add(req, res, user, uriParser);
				putTextarea = true;

			} else {

				String mode = req.getParameter("mode");

				if (!StringUtils.isEmpty(mode)) {

					if (mode.equals("getinfo")) {

						response = this.getInfo(req, res, user, uriParser, moduleDescriptor, fileMethodAlias);

					} else if (mode.equals("getfolder")) {

						response = this.getFolder(req, res, user, uriParser, moduleDescriptor, fileMethodAlias);

					} else if (mode.equals("rename")) {

						response = this.rename(req, res, user, uriParser);

					} else if (mode.equals("delete")) {

						response = this.delete(req, res, user, uriParser);

					} else if (mode.equals("addfolder")) {

						response = this.addFolder(req, res, user, uriParser);

					} else if (mode.equals("download")) {

						response = this.download(req, res, user, uriParser);

					} else if (mode.equals("preview")) {

						this.preview(req, res, user, uriParser);

						return;

					} else if (mode.equals("getstateid")) {

						response = this.getStateID(req, res, user, uriParser);

					} else {

						response = this.sendError("MODE_ERROR");

					}

				}

			}

		}

		if (response != null){

			String responseStr = response.toJson();

			if (putTextarea) {
				responseStr = "<textarea>" + responseStr + "</textarea>";
				res.setContentType("text/html");
			} else {
				res.setContentType("application/json;charset=ISO-8859-1");
			}

			res.setCharacterEncoding("ISO-8859-1");

			PrintWriter pw = res.getWriter();
			pw.print(responseStr);
			pw.flush();
			pw.close();

		}

	}

	public void processFileRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, int uriIndex, FileAccessValidator fileAccessValidator) throws URINotFoundException {

		if (uriParser.size() < (uriIndex + 1)) {

			throw new URINotFoundException(uriParser);

		} else if (StringUtils.isEmpty(this.filestorePath)) {

			throw new URINotFoundException(uriParser);
		} else {

			String filePath = null;

			if (uriParser.size() > (uriIndex + 1)) {

				StringBuilder stringBuilder = new StringBuilder();

				for (int i = uriIndex; i < uriParser.size(); i++) {
					stringBuilder.append("/" + uriParser.get(i));
				}

				filePath = stringBuilder.toString();
			} else {
				filePath = "/" + uriParser.get(uriIndex);
			}

			if (filePath.contains("..")) {

				throw new URINotFoundException(uriParser);

			} else if (fileAccessValidator != null && !fileAccessValidator.checkAccess(filePath)) {

				throw new URINotFoundException(uriParser);
			}

			File file = new File(this.filestorePath + filePath);

			if (file.exists() && file.canRead() && !file.isDirectory()) {

				try {
					HTTPUtils.sendFile(file, req, res, ContentDisposition.INLINE);

				} catch (IOException e) {

					log.info("Error sending file " + file + " to user " + user);
				}

			} else {
				// File not found in file system
				throw new URINotFoundException(uriParser);
			}
		}
	}

	private JsonNode add(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) {

		try {

			req = this.parseRequest(req);

			String currentPath = req.getParameter("currentpath");

			if(!StringUtils.isEmpty(currentPath)) {

				Collection<FileItem> files = this.getFiles(req);

				if(!CollectionUtils.isEmpty(files)) {

					currentPath = URLDecoder.decode(StringUtils.parseUTF8(currentPath), "UTF-8");

					JsonObject fileInfo = null;

					for(FileItem fileItem : files) {

						String filename = FilenameUtils.getName(URLDecoder.decode(StringUtils.parseUTF8(fileItem.getName()), "UTF-8"));

						fileInfo = new JsonObject();

						filename = FileUtils.toValidHttpFilename(filename);

						filename = this.calculateFilename(this.filestorePath + currentPath, filename, 0);

						File file = new File(this.filestorePath + currentPath + filename);

						fileItem.write(file);

						log.info("User " + user + " added file " + file);
					}

					fileInfo.putField("Path", currentPath);
					fileInfo.putField("Name", "");
					fileInfo.putField("Error", "");
					fileInfo.putField("Code", "0");

					return fileInfo;
				}

				return this.sendError("INVALID_FILE_UPLOAD");

			}

		} catch (SizeLimitExceededException e) {

			return this.sendError("UPLOAD_FILES_SMALLER_THAN", this.diskThreshold + " MB");

		} catch (FileSizeLimitExceededException e) {

			return this.sendError("UPLOAD_FILES_SMALLER_THAN", this.diskThreshold + " MB");

		} catch (FileUploadException e) {

			return this.sendError("INVALID_FILE_UPLOAD");

		} catch (Exception e) {

			return this.sendError("INVALID_FILE_UPLOAD");

		} finally {

			if (req != null && req instanceof MultipartRequest) {
				((MultipartRequest)req).deleteFiles();
			}

		}

		return this.sendError("UNKOWN_ERROR");

	}

	private JsonNode rename(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) {

		String oldFilename = req.getParameter("old");
		String newFilename = req.getParameter("new");

		if(!StringUtils.isEmpty(oldFilename) && !StringUtils.isEmpty(newFilename)) {

			String path = null;

			File fileFrom = null;

			File fileTo = null;

			try {

				oldFilename = URLDecoder.decode(StringUtils.parseUTF8(oldFilename), "UTF-8");
				newFilename = URLDecoder.decode(StringUtils.parseUTF8(newFilename), "UTF-8");

				if(oldFilename.endsWith("/")) {
					oldFilename = oldFilename.substring(0,((oldFilename).length() - 1));
				}

				int position = oldFilename.lastIndexOf("/");

				path = URLDecoder.decode(oldFilename.substring(0, position + 1), "UTF-8");

				String filenameParts[] = oldFilename.split("/");

				String filename = filenameParts[filenameParts.length - 1];

				fileFrom = new File(this.filestorePath + oldFilename);
				fileTo = new File(this.filestorePath + path + newFilename);

				if (fileTo.exists()) {

					if (fileTo.isDirectory()) {

						return this.sendError("DIRECTORY_ALREADY_EXISTS", newFilename, oldFilename);

					} else {

						return this.sendError("FILE_ALREADY_EXISTS", newFilename, oldFilename);

					}

				} else if (!fileFrom.renameTo(fileTo)) {

					return this.sendError("ERROR_RENAMING_DIRECTORY", newFilename, oldFilename);

				}

				if(fileFrom.isDirectory()){

					log.info("User " + user + " renamed directory " + fileFrom + " to " + fileTo);

				}else{

					log.info("User " + user + " renamed file " + fileFrom + " to " + fileTo);
				}

				JsonObject array = new JsonObject();

				array.putField("Error", "");
				array.putField("Code", "0");
				array.putField("Old Path", oldFilename);
				array.putField("Old Name", filename);
				array.putField("New Path", path + newFilename + (fileTo.isDirectory() ? "/" : ""));
				array.putField("New Name", newFilename);

				return array;

			} catch (Exception e) {

				if(fileFrom.isDirectory()) {

					return this.sendError("ERROR_RENAMING_DIRECTORY", newFilename, oldFilename);

				} else {

					return this.sendError("ERROR_RENAMING_FILE", newFilename, oldFilename);

				}

			}

		}

		return this.sendError("UNKOWN_ERROR");

	}

	private JsonNode addFolder(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws UnsupportedEncodingException {

		String name = req.getParameter("name");

		String path = req.getParameter("path");

		if(!StringUtils.isEmpty(name) && !StringUtils.isEmpty(path)) {

			name = URLDecoder.decode(StringUtils.parseUTF8(name), "UTF-8");

			String filename = FileUtils.toValidHttpFilename(name);

			JsonObject array = null;

			if (filename.length() == 0) {

				return this.sendError("UNABLE_TO_CREATE_DIRECTORY", name);

			} else {

				File file = new File(this.filestorePath + path + filename);

				try {

					if (file.isDirectory()) {

						return this.sendError("DIRECTORY_ALREADY_EXISTS", filename);

					} else if (!file.mkdir()) {

						return this.sendError("UNABLE_TO_CREATE_DIRECTORY", filename);

					} else {

						log.info("User " + user + " created directory " + file);

						array = new JsonObject();
						array.putField("Parent", path);
						array.putField("Name", filename);
						array.putField("Error", "");
						array.putField("Code", "0");

						return array;

					}

				} catch (SecurityException e) {

					return this.sendError("UNABLE_TO_CREATE_DIRECTORY", filename);

				}

			}

		}

		return this.sendError("UNKOWN_ERROR");

	}

	private JsonNode getFolder(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, ForegroundModuleDescriptor moduleDescriptor, String fileMethodAlias) throws UnsupportedEncodingException {

		String path = req.getParameter("path");

		if(!StringUtils.isEmpty(path)) {

			OrderedJsonObject array = null;

			path = URLDecoder.decode(StringUtils.parseUTF8(path), "UTF-8");

			File directory = new File(this.filestorePath + path);

			if(!directory.isDirectory()) {

				return this.sendError("DIRECTORY_NOT_EXIST", path);

			} else {

				if (!directory.canRead()){

					return this.sendError("UNABLE_TO_OPEN_DIRECTORY", path);

				} else {

					File file = null;

					array = new OrderedJsonObject();

					String[] files = directory.list();

					JsonObject data = null;
					JsonObject properties = null;

					for (String file2 : files) {

						data = new JsonObject();
						properties = new JsonObject();

						file = new File(filestorePath + path + file2);

						if (file.isDirectory()) {

							String staticContentPath = req.getContextPath() + "/static/f/" + moduleDescriptor.getSectionID() + "/" + moduleDescriptor.getModuleID();

							properties.putField("Date Created", (String) null);
							properties.putField("Date Modified", (String) null);
							properties.putField("Height", (String) null);
							properties.putField("Width", (String) null);
							properties.putField("Size", (String) null);

							data.putField("Path", path + file2 + "/");
							data.putField("Filename", file2);
							data.putField("File Type", "dir");
							data.putField("Preview", staticContentPath + FILE_ICON_PACKAGE + "/_Open.png");
							data.putField("Error", "");
							data.putField("Code", "0");
							data.putField("Properties", properties);

							array.putField(path + file2 + "/", data);

						} else {

							Map<String, Object> fileInfo = this.getFileInfo(req, uriParser, path + file2, moduleDescriptor, fileMethodAlias);

							data.putField("Path", path + file2);
							data.putField("Filename", (String) fileInfo.get("filename"));
							data.putField("File Type", (String) fileInfo.get("filetype"));
							data.putField("Preview", (String) fileInfo.get("preview"));
							data.putField("FileURL", (String) fileInfo.get("fileURL"));
							data.putField("Properties", (JsonObject) fileInfo.get("properties"));
							data.putField("Error", "");
							data.putField("Code", "0");

							array.putField(path + file2, data);

						}

					}

				}

			}

			return array;

		}

		return this.sendError("UNKOWN_ERROR");
	}

	private JsonNode getInfo(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, ForegroundModuleDescriptor moduleDescriptor, String fileMethodAlias) throws UnsupportedEncodingException {

		String path = req.getParameter("path");

		if(!StringUtils.isEmpty(path)) {

			path = URLDecoder.decode(StringUtils.parseUTF8(path), "UTF-8");

			Map<String, Object> fileInfo = this.getFileInfo(req, uriParser, path, moduleDescriptor, fileMethodAlias);

			JsonObject array = new JsonObject();

			array.putField("Path", path);
			array.putField("Filename", (String) fileInfo.get("filename"));
			array.putField("File Type", (String) fileInfo.get("filetype"));
			array.putField("Preview", (String) fileInfo.get("preview"));
			array.putField("FileURL", (String) fileInfo.get("fileURL"));
			array.putField("Properties", (JsonObject) fileInfo.get("properties"));
			array.putField("Error", "");
			array.putField("Code", "0");

			return array;

		}

		return this.sendError("UNKOWN_ERROR");

	}

	private JsonNode delete(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws UnsupportedEncodingException {

		String path = req.getParameter("path");

		if(!StringUtils.isEmpty(path)) {

			path = URLDecoder.decode(StringUtils.parseUTF8(req.getParameter("path")), "UTF-8");

			JsonObject array = null;

			File file = new File(this.filestorePath + path);

			if (file.isDirectory()) {

				array = new JsonObject();

				if (FileUtils.deleteDirectory(this.filestorePath + path)) {

					array.putField("Error", "");
					array.putField("Code", "0");
					array.putField("Path", path);

					log.info("User " + user + " deleted directory " + file);

				} else {

					return this.sendError("ERROR_DELETING_DIRECTORY", path);

				}

			} else if (file.exists()) {

				array = new JsonObject();

				if (file.delete()) {

					array.putField("Error", "");
					array.putField("Code", "0");
					array.putField("Path", path);

					log.info("User " + user + " deleted file " + file);

				} else {

					return this.sendError("ERROR_DELETING_FILE", path);

				}

				return array;

			} else {

				return this.sendError("INVALID_DIRECTORY_OR_FILE", path);

			}

			return array;

		}

		return this.sendError("UNKOWN_ERROR");

	}

	private JsonNode download(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws UnsupportedEncodingException {

		String path = req.getParameter("path");

		if(!StringUtils.isEmpty(path)) {

			path = URLDecoder.decode(StringUtils.parseUTF8(req.getParameter("path")), "UTF-8");

			File file = new File(this.filestorePath + path);

			if (file.exists()) {

				res.setHeader("Content-Transfer-Encoding", "Binary");
				res.setHeader("Content-length", "" + file.length());
				res.setHeader("Content-Type", "application/octet-stream");
				res.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

				this.sendFile(res, file, user);

				return null;

			} else {

				return this.sendError("FILE_DOES_NOT_EXIST", path);

			}

		}

		return this.sendError("UNKOWN_ERROR");

	}

	private void preview(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws UnsupportedEncodingException {

		String path = req.getParameter("path");

		if(!StringUtils.isEmpty(path)) {

			path = URLDecoder.decode(StringUtils.parseUTF8(path), "UTF-8");

			File file = new File(this.filestorePath + path);

			if(file.exists()) {

				res.setHeader("Content-type", "image/" + FileUtils.getFileExtension(file) + ";charset=ISO-8859-1");
				res.setHeader("Content-Transfer-Encoding", "Binary");
				res.setHeader("Content-length", "" + file.length());
				res.setHeader("Content-Disposition", "inline; filename=\"" + FileUtils.toValidHttpFilename(file.getName()) + "\"");
				res.setCharacterEncoding("ISO-8859-1");

				this.sendFile(res, file, user);

			} else {

				this.sendError("FILE_DOES_NOT_EXIST", path);

			}

		}

	}

	private String calculateFilename(String path, String filename, int i) {

		File file = new File(path + filename);

		String i2 = "";

		String[] tmp = null;

		if (!file.exists()) {

			return filename;

		} else {

			if (i != 0) {
				i2 = "" + i;
			}

			tmp = filename.split(i2 + "\\.");

			i++;

			filename = filename.replace(i2 + "." + tmp[tmp.length - 1], i + "." + tmp[tmp.length - 1]);

			return this.calculateFilename(path, filename, i);

		}

	}

	private void sendFile(HttpServletResponse res, File file, User user) {

		InputStream in = null;
		OutputStream out = null;

		try {

			in = new FileInputStream(file);
			out = res.getOutputStream();

			StreamUtils.transfer(in, out);

		} catch (IOException e) {

			log.info("Error sending file " + file + " to user " + user);

		} finally {

			StreamUtils.closeStream(in);
			StreamUtils.closeStream(out);

		}

	}

	private Map<String, Object> getFileInfo(HttpServletRequest req, URIParser uriParser, String path, ForegroundModuleDescriptor moduleDescriptor, String fileMethodAlias) throws UnsupportedEncodingException {

		Map<String, Object> fileInfo = new HashMap<String, Object>();

		JsonObject fileProperties = new JsonObject();

		File file = new File(this.filestorePath + path);

		fileProperties.putField("Date Modified", RFC1123_DATE_FORMATTER.format(new Date(file.lastModified())));

		String filename = file.getName();

		fileInfo.put("filename", filename);

		if (file.isFile()) {
			fileInfo.put("filetype", filename.substring(filename.lastIndexOf(".") + 1));
		} else {
			fileInfo.put("filetype", "dir");
		}

		String staticContentPath = req.getContextPath() + "/static/f/" + moduleDescriptor.getSectionID() + "/" + moduleDescriptor.getModuleID();

		String moduleURI = uriParser.getCurrentURI(true) + "/" + moduleDescriptor.getAlias();

		fileInfo.put("filemtime", "" + file.lastModified());
		fileInfo.put("filectime", "" + file.lastModified());
		fileInfo.put("preview", staticContentPath + FILE_ICON_PACKAGE + "/default.png");
		fileInfo.put("fileURL", moduleURI + fileMethodAlias + path);

		if (file.isDirectory()) {

			fileInfo.put("preview", staticContentPath + FILE_ICON_PACKAGE + "/_Open.png");

		} else if (ImageUtils.isImage(filename)) {

			fileInfo.put("preview", moduleURI +  req.getContextPath() + uriParser.getFormattedURI() + "?mode=preview&path=" + URLEncoder.encode(path, "UTF-8"));

			try {
				Dimension dimension = ImageUtils.getImageDimensions(this.filestorePath + path);

				if(dimension != null) {
					fileProperties.putField("Height", "" + dimension.getHeight());
					fileProperties.putField("Width", "" + dimension.getWidth());
				}
			} catch (FileNotFoundException e) {
			} catch (IOException e) {}

			fileProperties.putField("Size", "" + file.length());

		} else {

			String fileType = ((String) fileInfo.get("filetype")).toLowerCase();

			URL resource = FCKConnector.class.getResource("/com/ckeditor" + FILE_ICON_PACKAGE + "/" + fileType + ".png");

			if(resource != null) {

				fileInfo.put("preview", staticContentPath + FILE_ICON_PACKAGE + "/" + fileType + ".png");
				fileProperties.putField("Size", file.length() + "");

			}

		}

		fileInfo.put("properties", fileProperties);

		return fileInfo;

	}

	private JsonNode getStateID(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) {

		JsonObject stateID = new JsonObject();

		stateID.putField("StateID", (this.filestorePath + "_" + (user != null ? user.getUsername() : "null")).hashCode() + "");
		stateID.putField("Error", "");
		stateID.putField("Code", "0");

		return stateID;
	}

	private HttpServletRequest parseRequest(HttpServletRequest req) throws SizeLimitExceededException, FileSizeLimitExceededException, FileUploadException {

		if(MultipartRequest.isMultipartRequest(req)){

			return new MultipartRequest(ramThreshold * BinarySizes.KiloByte, diskThreshold * BinarySizes.MegaByte, req);

		}

		return req;
	}

	private Collection<FileItem> getFiles(HttpServletRequest req) {

		if(!(req instanceof MultipartRequest)){

			return null;
		}

		MultipartRequest multipartRequest = (MultipartRequest)req;

		if (multipartRequest.getFileCount() > 0) {

			Collection<FileItem> files = multipartRequest.getFiles();

			Iterator<FileItem> fileIterator = files.iterator();

			while (fileIterator.hasNext()) {

				FileItem fileItem = fileIterator.next();

				if (StringUtils.isEmpty(fileItem.getName()) || fileItem.getSize() == 0) {

					fileIterator.remove();
					continue;
				}

			}

			return files;
		}

		return null;
	}

	private JsonObject sendError(String message, String... params) {

		JsonObject error = new JsonObject();

		error.putField("Error", message);
		error.putField("Code", "-1");

		if(params != null) {

			JsonArray errorParams = new JsonArray();

			for(String param : params) {
				errorParams.addNode(param);
			}

			error.putField("Params", errorParams);

		}

		return error;
	}

	public String getFilestorePath() {

		return filestorePath;
	}

	public void setFilestorePath(String filestorePath) {

		this.filestorePath = filestorePath;
	}

	public int getDiskThreshold() {

		return diskThreshold;
	}

	public void setDiskThreshold(int diskThreshold) {

		this.diskThreshold = diskThreshold;
	}

	public int getRamThreshold() {

		return ramThreshold;
	}

	public void setRamThreshold(int ramThreshold) {

		this.ramThreshold = ramThreshold;
	}

}
