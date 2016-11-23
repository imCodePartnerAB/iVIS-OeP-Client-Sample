/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.imagegallery;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import se.unlogic.fileuploadutils.MultipartRequest;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.beans.Breadcrumb;
import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.beans.SettingDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.beans.ValueDescriptor;
import se.unlogic.hierarchy.core.enums.URLType;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.interfaces.AccessInterface;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.MutableSettingHandler;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.utils.AccessUtils;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.hierarchy.foregroundmodules.imagegallery.beans.Comment;
import se.unlogic.hierarchy.foregroundmodules.imagegallery.beans.Gallery;
import se.unlogic.hierarchy.foregroundmodules.imagegallery.beans.Picture;
import se.unlogic.hierarchy.foregroundmodules.imagegallery.daos.CommentDao;
import se.unlogic.hierarchy.foregroundmodules.imagegallery.daos.GalleryDao;
import se.unlogic.hierarchy.foregroundmodules.imagegallery.daos.PictureDao;
import se.unlogic.hierarchy.foregroundmodules.imagegallery.populators.GalleryPopulator;
import se.unlogic.standardutils.dao.querys.UpdateQuery;
import se.unlogic.standardutils.datatypes.SimpleEntry;
import se.unlogic.standardutils.db.DBUtils;
import se.unlogic.standardutils.image.ImageUtils;
import se.unlogic.standardutils.io.BinarySizes;
import se.unlogic.standardutils.io.FileUtils;
import se.unlogic.standardutils.mime.MimeUtils;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.streams.StreamUtils;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.threads.MutexKeyProvider;
import se.unlogic.standardutils.time.TimeUtils;
import se.unlogic.standardutils.validation.PositiveStringIntegerValidator;
import se.unlogic.standardutils.validation.StringIntegerValidator;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationErrorType;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.standardutils.zip.ZipUtils;
import se.unlogic.webutils.http.HTTPUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;

public class GalleryModule extends AnnotatedForegroundModule implements AccessInterface {

	//TODO let users specify alias manually
	//TODO support multiple file uploads without using a zip file (HTML5)

	private static final ArrayList<SettingDescriptor> SETTINGDESCRIPTORS = new ArrayList<SettingDescriptor>();

	static {
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("numOfThumbsPerPage", "Thumbnails per page", "The number of thumbnails per page (default is 15)", false, "15", new StringIntegerValidator(1, null)));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("smallImageMaxHeight", "Small image max height ", "The max height of the small thumbnails (default is 93)", false, "93", new StringIntegerValidator(1, null)));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("smallImageMaxWidth", "Small image max width ", "The max width of the small thumbnails (default is 125)", false, "125", new StringIntegerValidator(1, null)));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("mediumImageMaxHeight", "Medium image max height ", "The max height of the medium thumbnails (default is 500)", false, "500", new StringIntegerValidator(1, null)));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("mediumImageMaxWidth", "Medium image max width ", "The max width of the medium thumbnails (default is 500)", false, "500", new StringIntegerValidator(1, null)));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createCheckboxSetting("allowAnonymousComments", "Allow anonymous comments", "Control wheter or not logged in user can post comments", false));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("diskThreshold", "Max upload size", "Maxmium upload size in megabytes allowed in a single post request", false, "100", new StringIntegerValidator(1, null)));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createDropDownSetting("thumbQuality", "Thumbnail quality", "Selects which algorithm that should be used when generating thumbnails", true, Image.SCALE_SMOOTH + "", new ValueDescriptor("SCALE_SMOOTH", Image.SCALE_SMOOTH + ""), new ValueDescriptor("SCALE_AREA_AVERAGING", Image.SCALE_AREA_AVERAGING + ""), new ValueDescriptor("SCALE_FAST", Image.SCALE_FAST + ""), new ValueDescriptor("SCALE_REPLICATE", Image.SCALE_REPLICATE + "")));
	}

	private static final GalleryPopulator GalleryPopulator = new GalleryPopulator();
	protected static final SimpleFileFilter fileFilter = new SimpleFileFilter();

	protected GalleryDao galleryDao;
	protected PictureDao pictureDao;
	protected CommentDao commentDao;

	@ModuleSetting(allowsNull = true)
	@TextFieldSettingDescriptor(name="Base path",description="Path to directory in which gallery directories will automatically created.\r\nIf base path is omitted, auto creation of gallery directories is disabled.\r\nAuto created directories will never use (merge with) existing direcories.",required=false)
	protected String path;

	@ModuleSetting
	protected Integer numOfThumbsPerPage = 15;

	@ModuleSetting
	protected Integer smallImageMaxHeight = 93;

	@ModuleSetting
	protected Integer smallImageMaxWidth = 125;

	@ModuleSetting
	protected Integer mediumImageMaxHeight = 500;

	@ModuleSetting
	protected Integer mediumImageMaxWidth = 500;

	@ModuleSetting
	protected Boolean allowAnonymousComments = false;

	@ModuleSetting
	protected Integer diskThreshold = 100;

	@ModuleSetting
	@TextFieldSettingDescriptor(name="RAM threshold",description="Maximum size of files in KB to be buffered in RAM during file uploads. Files exceeding the threshold are written to disk instead.",required=true,formatValidator=PositiveStringIntegerValidator.class)
	protected Integer ramThreshold = 500;

	@ModuleSetting(allowsNull = true)
	protected List<Integer> adminGroupIDs;

	@ModuleSetting(allowsNull = true)
	protected List<Integer> adminUserIDs;

	protected Integer thumbQuality = Image.SCALE_SMOOTH;

	protected MutexKeyProvider<Gallery> mutexKeyProvider = new MutexKeyProvider<Gallery>();

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		System.setProperty("java.awt.headless", "true");

		this.checkTables(dataSource);

		this.galleryDao = new GalleryDao(dataSource);
		this.pictureDao = new PictureDao(dataSource);
		this.commentDao = new CommentDao(dataSource, sectionInterface.getSystemInterface().getUserHandler());

		this.checkSettings(moduleDescriptor.getMutableSettingHandler());
	}

	@Override
	public void update(ForegroundModuleDescriptor moduleDescriptor, DataSource dataSource) throws Exception {

		super.update(moduleDescriptor, dataSource);

		this.checkTables(dataSource);

		this.galleryDao = new GalleryDao(dataSource);
		this.pictureDao = new PictureDao(dataSource);
		this.commentDao = new CommentDao(dataSource, sectionInterface.getSystemInterface().getUserHandler());

		this.checkSettings(moduleDescriptor.getMutableSettingHandler());
	}

	// TODO This should be done in a DAOFactory but is done here until we have one for this module...
	private void checkTables(DataSource dataSource) throws SQLException, IOException {

		log.debug("Checking for gallery tables in datasource " + dataSource + "...");

		if (!DBUtils.tableExists(this.dataSource, "galleries")) {

			log.info(this.moduleDescriptor + " creating galleries table in datasource " + dataSource);

			String sql = StringUtils.readStreamAsString(this.getClass().getResourceAsStream("daos/dbscripts/GalleryTable.sql"));

			new UpdateQuery(this.dataSource.getConnection(), true, sql).executeUpdate();
		}

		if (!DBUtils.tableExists(this.dataSource, "gallerygroups")) {

			log.info(this.moduleDescriptor + " creating gallerygroups table in datasource " + dataSource);

			String sql = StringUtils.readStreamAsString(this.getClass().getResourceAsStream("daos/dbscripts/GalleryGroupsTable.sql"));

			new UpdateQuery(this.dataSource.getConnection(), true, sql).executeUpdate();
		}

		if (!DBUtils.tableExists(this.dataSource, "galleryusers")) {

			log.info(this.moduleDescriptor + " creating galleryusers table in datasource " + dataSource);

			String sql = StringUtils.readStreamAsString(this.getClass().getResourceAsStream("daos/dbscripts/GalleryUsersTable.sql"));

			new UpdateQuery(this.dataSource.getConnection(), true, sql).executeUpdate();
		}

		if (!DBUtils.tableExists(this.dataSource, "galleryuploadgroups")) {

			log.info(this.moduleDescriptor + " creating galleryuploadgroups table in datasource " + dataSource);

			String sql = StringUtils.readStreamAsString(this.getClass().getResourceAsStream("daos/dbscripts/GalleryUploadGroupsTable.sql"));

			new UpdateQuery(this.dataSource.getConnection(), true, sql).executeUpdate();
		}

		if (!DBUtils.tableExists(this.dataSource, "galleryuploadusers")) {

			log.info(this.moduleDescriptor + " creating galleryuploadusers table in datasource " + dataSource);

			String sql = StringUtils.readStreamAsString(this.getClass().getResourceAsStream("daos/dbscripts/GalleryUploadUsersTable.sql"));

			new UpdateQuery(this.dataSource.getConnection(), true, sql).executeUpdate();
		}

		if (!DBUtils.tableExists(this.dataSource, "pictures")) {

			log.info(this.moduleDescriptor + " creating pictures table in datasource " + dataSource);

			String sql = StringUtils.readStreamAsString(this.getClass().getResourceAsStream("daos/dbscripts/PicturesTable.sql"));

			new UpdateQuery(this.dataSource.getConnection(), true, sql).executeUpdate();
		}

		if (!DBUtils.tableExists(this.dataSource, "picturecomments")) {

			log.info(this.moduleDescriptor + " creating picturecomments table in datasource " + dataSource);

			String sql = StringUtils.readStreamAsString(this.getClass().getResourceAsStream("daos/dbscripts/PictureCommentsTable.sql"));

			new UpdateQuery(this.dataSource.getConnection(), true, sql).executeUpdate();
		}
	}

	private void checkSettings(MutableSettingHandler mutableSettingHandler) {

		Integer thumbQuality = mutableSettingHandler.getInt("thumbQuality");

		if (thumbQuality != null && (thumbQuality == Image.SCALE_AREA_AVERAGING || thumbQuality == Image.SCALE_FAST || thumbQuality == Image.SCALE_REPLICATE || thumbQuality == Image.SCALE_SMOOTH)) {
			this.thumbQuality = thumbQuality;
		} else if (thumbQuality != null) {
			log.warn("Illegal thumbQuality setting value detected, " + thumbQuality + " in module " + this.moduleDescriptor);
		}
	}

	@Override
	public SimpleForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		log.info("User " + user + " listing galleries");

		ArrayList<Gallery> galleryList = this.galleryDao.getAll();

		Document doc = this.createDocument(req, uriParser, user);
		Element galleriesElement = doc.createElement("galleries");
		doc.getFirstChild().appendChild(galleriesElement);

		if (galleryList != null) {
			for (Gallery gallery : galleryList) {
				if (checkBooleanAccess(user, gallery)) {
					Node galleryNode = gallery.toXML(doc);

					// get gallery random image
					SimpleEntry<String, String> pictureEntry = getRandomImage(gallery);

					if (pictureEntry != null) {
						galleryNode.appendChild(XMLUtils.createElement("randomFile", pictureEntry.getKey(), doc));
						galleryNode.appendChild(XMLUtils.createElement("numPics", pictureEntry.getValue(), doc));
					} else {
						galleryNode.appendChild(XMLUtils.createElement("numPics", "0", doc));
					}

					galleriesElement.appendChild(galleryNode);
				}
			}
		}

		return new SimpleForegroundModuleResponse(doc, this.moduleDescriptor.getName(), this.getDefaultBreadcrumb());

	}

	protected SimpleEntry<String, String> getRandomImage(Gallery gallery) {

		SimpleEntry<String, String> pictureEntry = null;
		// check that url in gallery exist
		File dir = new File(gallery.getUrl());
		boolean read = dir.canRead();

		if (!read) {
			return null;

		}
		File[] files = dir.listFiles(fileFilter);

		if (files.length == 0) {
			return null;
		}

		Random rand = new Random();
		int idx = rand.nextInt(files.length);

		pictureEntry = new SimpleEntry<String, String>(files[idx].getName(), String.valueOf(files.length));

		return pictureEntry;
	}

	protected Document createDocument(HttpServletRequest req, URIParser uriParser, User user) {

		Document doc = XMLUtils.createDomDocument();
		Element document = doc.createElement("document");
		document.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser));
		document.appendChild(this.moduleDescriptor.toXML(doc));
		document.appendChild(this.sectionInterface.getSectionDescriptor().toXML(doc));
		document.appendChild(XMLUtils.createElement("isAdmin", this.checkBooleanAdminAccess(user).toString(), doc));
		doc.appendChild(document);
		return doc;

	}

	@WebPublic
	public SimpleForegroundModuleResponse gallery(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException, SQLException, URINotFoundException, AccessDeniedException {

		//This was done to keep compability with old liks pointing to showGallery
		return showGallery(req, res, user, uriParser);
	}

	@WebPublic
	public SimpleForegroundModuleResponse showGallery(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException, SQLException, URINotFoundException, AccessDeniedException {

		Gallery gallery = null;

		if (uriParser.size() < 3 || uriParser.size() > 4 || (gallery = this.galleryDao.get(uriParser.get(2).toString())) == null) {

			throw new URINotFoundException(uriParser);

		} else {

			this.checkAccess(user, gallery);

			Integer page = 1;

			if (uriParser.size() == 4) {
				page = NumberUtils.toInt(uriParser.get(3));

				if (page == null || page <= 0) {
					throw new URINotFoundException(uriParser);
				}
			}

			log.info("User " + user + " requested page " + page + " in gallery " + gallery);

			// check if path is valid
			File dir = new File(gallery.getUrl());

			if (!dir.canRead()) {
				throw new URINotFoundException(uriParser);
			}

			// check that requested page exist
			// get all filenames, only allow images
			File[] allFiles = dir.listFiles(fileFilter);

			int num = allFiles.length;

			// find number of pages in gallery
			int pagesInGallery = num % numOfThumbsPerPage == 0 ? num / numOfThumbsPerPage : (num / numOfThumbsPerPage) + 1;

			if (num == 0) {

				Document doc = this.createDocument(req, uriParser, user);

				Element galleryElement = doc.createElement("showGallery");
				doc.getFirstChild().appendChild(galleryElement);

				Node gNode = gallery.toXML(doc);
				gNode.appendChild(XMLUtils.createElement("pages", "1", doc));
				gNode.appendChild(XMLUtils.createElement("currentPage", "1", doc));
				gNode.appendChild(XMLUtils.createElement("numPics", "0", doc));

				galleryElement.appendChild(XMLUtils.createElement("hasUploadAccess", this.checkBooleanUploadAccess(user, gallery).toString(), doc));

				galleryElement.appendChild(gNode);

				return new SimpleForegroundModuleResponse(doc, gallery.getName(), getDefaultBreadcrumb(), getGalleryBreadcrumb(gallery, req));

			} else if (page <= pagesInGallery) {

				Arrays.sort(allFiles);

				// find next and previous page
				Integer nextPage = page == pagesInGallery ? null : page + 1;
				Integer prevPage = page == 1 ? null : page - 1;

				// calculate start- and endindex
				int startIndex = (numOfThumbsPerPage * page) - numOfThumbsPerPage;
				int endIndex = startIndex + numOfThumbsPerPage;
				if (page == pagesInGallery) {
					endIndex = allFiles.length;
				}

				// create XML-document containing information about the requested gallery and images
				Document doc = this.createDocument(req, uriParser, user);

				Element galleryElement = doc.createElement("showGallery");
				doc.getFirstChild().appendChild(galleryElement);

				galleryElement.appendChild(XMLUtils.createElement("hasUploadAccess", this.checkBooleanUploadAccess(user, gallery).toString(), doc));

				Element filesElement = doc.createElement("files");

				Node gNode = gallery.toXML(doc);
				gNode.appendChild(XMLUtils.createElement("pages", String.valueOf(pagesInGallery), doc));
				gNode.appendChild(XMLUtils.createElement("currentPage", String.valueOf(page), doc));
				gNode.appendChild(XMLUtils.createElement("numPics", String.valueOf(num), doc));

				if (nextPage != null) {
					gNode.appendChild(XMLUtils.createElement("nextPage", nextPage.toString(), doc));
				}
				if (prevPage != null) {
					gNode.appendChild(XMLUtils.createElement("prevPage", prevPage.toString(), doc));
				}

				// find images for requested page
				for (int i = startIndex; i < endIndex; i++) {
					Element fileElement = doc.createElement("file");
					String filename = allFiles[i].getName();
					fileElement.appendChild(XMLUtils.createElement("filename", filename, doc));

					Element commentsElement = doc.createElement("comments");

					ArrayList<Comment> comments = commentDao.getByFilenameAndGallery(filename, gallery);

					if (comments != null) {
						for (Comment comment : comments) {
							commentsElement.appendChild(comment.toXML(doc));
						}
						fileElement.appendChild(commentsElement);
					}
					filesElement.appendChild(fileElement);
				}

				gNode.appendChild(filesElement);
				galleryElement.appendChild(gNode);

				return new SimpleForegroundModuleResponse(doc, gallery.getName(), getDefaultBreadcrumb(), getGalleryBreadcrumb(gallery, req));

			} else {

				throw new URINotFoundException(uriParser);
			}
		}
	}

	private Breadcrumb getGalleryBreadcrumb(Gallery gallery, HttpServletRequest req) {

		return new Breadcrumb(gallery.getName(), gallery.getDescription(), this.getFullAlias() + "/showGallery/" + gallery.getAlias(), URLType.RELATIVE_FROM_CONTEXTPATH);

	}

	@WebPublic
	public SimpleForegroundModuleResponse image(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException, SQLException, URINotFoundException, AccessDeniedException {

		return this.showImage(req, res, user, uriParser);
	}

	@WebPublic
	public SimpleForegroundModuleResponse showImage(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException, SQLException, URINotFoundException, AccessDeniedException {

		Gallery gallery = null;

		if (uriParser.size() != 4 || (gallery = this.galleryDao.get(uriParser.get(2).toString())) == null) {

			throw new URINotFoundException(uriParser);

		} else {

			this.checkAccess(user, gallery);

			// check if path is valid
			File dir = null;
			try {
				dir = new File(gallery.getUrl());
			} catch (Exception ex) {
				throw new URINotFoundException(uriParser);
			}

			// get all filenames
			String filename = uriParser.get(3);

			// get all files
			File[] allFiles = dir.listFiles(fileFilter);

			Arrays.sort(allFiles);

			// check if filename exist
			boolean found = false;
			int currentIdx = -1;

			for (int i = 0; i < allFiles.length; i++) {
				if (allFiles[i].getName().equals(filename)) {
					found = true;
					currentIdx = i;
					break;
				}
			}
			int picsInGallery = allFiles.length;

			if (found) {

				log.info("User " + user + " requested image " + filename + " in gallery " + gallery);

				HttpSession session = req.getSession(true);

				// find next and previous picture
				String nextPicture = currentIdx < picsInGallery - 1 ? allFiles[currentIdx + 1].getName() : null;
				String prevPicture = currentIdx > 0 ? allFiles[currentIdx - 1].getName() : null;

				// create XML-document containing information about the requested gallery and images
				Document doc = this.createDocument(req, uriParser, user);

				Element pictureElement = doc.createElement("showImage");
				doc.getFirstChild().appendChild(pictureElement);

				Node gNode = gallery.toXML(doc);
				gNode.appendChild(XMLUtils.createElement("numPics", String.valueOf(picsInGallery), doc));
				gNode.appendChild(XMLUtils.createElement("currentPic", String.valueOf(currentIdx + 1), doc));

				if (currentIdx == 0) {
					currentIdx++;
				}

				pictureElement.appendChild(XMLUtils.createElement("currentPage", (((currentIdx) / numOfThumbsPerPage) + 1) + "", doc));

				if (nextPicture != null) {
					gNode.appendChild(XMLUtils.createElement("nextImage", nextPicture.toString(), doc));
				}
				if (prevPicture != null) {
					gNode.appendChild(XMLUtils.createElement("prevImage", prevPicture.toString(), doc));
				}

				Element fileElement = doc.createElement("file");
				fileElement.appendChild(XMLUtils.createElement("filename", filename, doc));

				Element commentsElement = doc.createElement("comments");
				ArrayList<Comment> comments = commentDao.getByFilenameAndGallery(filename, gallery);

				//TODO move this to a separate method and add captcha support
				if (req.getMethod().equalsIgnoreCase("POST")) {

					String commentStatus = req.getParameter("viewComments");

					if (commentStatus != null) {
						if (commentStatus.equals("true")) {
							session.setAttribute(this.moduleDescriptor.getModuleID() + ".showAll", true);
						} else {
							session.setAttribute(this.moduleDescriptor.getModuleID() + ".showAll", false);
						}
					}

					String commentText = req.getParameter("commentText");
					if (commentText != null && (this.allowAnonymousComments || user != null)) {
						try {
							if (StringUtils.isEmpty(commentText)) {
								throw new ValidationException(new ValidationError("commentText", ValidationErrorType.RequiredField));
							}

							Comment comment = new Comment();
							comment.setComment(commentText);
							comment.setDate(new Timestamp(System.currentTimeMillis()));
							comment.setPictureID(pictureDao.getPictureIDByFilenameAndGallery(filename, gallery));

							if (user != null) {
								comment.setUser(user);
							}

							log.info("User " + user + " adding comment " + comment + " to image " + filename + " in gallery " + gallery);

							commentDao.add(comment);

							res.sendRedirect(req.getRequestURI());
						} catch (ValidationException e) {
							fileElement.appendChild(e.toXML(doc));
						}
					}
				}

				// check if user want´s to show all comments or not
				Boolean showAll = (Boolean) session.getAttribute(this.moduleDescriptor.getModuleID() + ".showAll");
				if (showAll == null || showAll) {
					commentsElement.appendChild(XMLUtils.createElement("showAll", "true", doc));

					if (comments != null) {

						for (Comment comment : comments) {
							commentsElement.appendChild(comment.toXML(doc));
						}
					}
				}

				if (comments != null) {
					commentsElement.appendChild(XMLUtils.createElement("commentsNum", String.valueOf(comments.size()), doc));
				}

				fileElement.appendChild(commentsElement);

				if (this.allowAnonymousComments || user != null) {
					fileElement.appendChild(doc.createElement("commentsAllowed"));
				}

				gNode.appendChild(fileElement);
				pictureElement.appendChild(gNode);

				return new SimpleForegroundModuleResponse(doc, filename, getDefaultBreadcrumb(), getGalleryBreadcrumb(gallery, req), getImageBreadcrumb(gallery, filename, req));
			} else {
				throw new URINotFoundException(uriParser);
			}
		}
	}

	private Breadcrumb getImageBreadcrumb(Gallery gallery, String filename, HttpServletRequest req) {

		return new Breadcrumb(filename, filename, this.getFullAlias() + "/showImage/" + gallery.getAlias() + "/" + filename, URLType.RELATIVE_FROM_CONTEXTPATH);
	}

	@WebPublic
	public SimpleForegroundModuleResponse smallThumb(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException, SQLException, URINotFoundException, AccessDeniedException {

		Gallery gallery = null;

		// check that the requested gallery exist
		if (uriParser.size() != 4 || (gallery = this.galleryDao.get(uriParser.get(2))) == null) {
			throw new URINotFoundException(uriParser);
		} else {

			this.checkAccess(user, gallery);

			String filename = uriParser.get(3).toString();

			// check if file is readable
			if (FileUtils.isReadable(gallery.getUrl() + File.separator + filename) && SimpleFileFilter.isValidFilename(filename)) {

				log.debug("User " + user + " requesting small thumb of image " + filename + " in gallery " + gallery);

				// check if there are thumbnails created
				Picture picture = pictureDao.getByFilename(filename, gallery.getGalleryID(), true, false);

				if (picture == null) {
					picture = createThumbs(filename, gallery);
				}

				try {
					writePicture(picture, false, res);
				} catch (Exception e) {
					log.info("Caught exception " + e + " while sending picture " + picture + " in gallery " + gallery + " to " + user);
				}

			} else {
				log.info("The picture " + filename + " does not exist in gallery " + gallery);
				throw new URINotFoundException(uriParser);
			}
		}

		return null;
	}

	@WebPublic
	public SimpleForegroundModuleResponse mediumThumb(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException, SQLException, URINotFoundException, AccessDeniedException {

		Gallery gallery = null;

		// check that the requested gallery exist
		if (uriParser.size() != 4 || (gallery = this.galleryDao.get(uriParser.get(2))) == null) {
			throw new URINotFoundException(uriParser);
		} else {

			this.checkAccess(user, gallery);

			String filename = uriParser.get(3).toString();

			if (FileUtils.isReadable(gallery.getUrl() + File.separator + filename) && SimpleFileFilter.isValidFilename(filename)) {

				log.debug("User " + user + " requesting medium thumb of image " + filename + " in gallery " + gallery);

				// check if there are thumbnails created
				Picture picture = pictureDao.getByFilename(filename, gallery.getGalleryID(), false, true);

				if (picture == null) {
					picture = createThumbs(filename, gallery);
				}

				try {
					writePicture(picture, true, res);
				} catch (Exception e) {
					log.info("Caught exception " + e + " while sending picture " + picture + " in gallery " + gallery + " to " + user);
				}

			} else {
				log.info("The picture " + filename + " does not exist in gallery " + gallery);
				throw new URINotFoundException(uriParser);
			}
		}

		return null;
	}

	protected Picture createThumbs(String filename, Gallery gallery) throws SQLException, IOException {

		log.info("Creating thumbs for picture " + filename + " in gallery " + gallery);

		Picture picture = new Picture();

		BufferedImage image = ImageUtils.getImage(gallery.getUrl() + File.separator + filename);

		BufferedImage smallImage = ImageUtils.scaleImage(image, this.smallImageMaxHeight, this.smallImageMaxWidth, thumbQuality, BufferedImage.TYPE_INT_RGB);
		BufferedImage mediumImage = ImageUtils.scaleImage(image, this.mediumImageMaxHeight, this.mediumImageMaxWidth, thumbQuality, BufferedImage.TYPE_INT_RGB);

		ByteArrayOutputStream smallThumbStream = new ByteArrayOutputStream();

		ImageIO.write(smallImage, "jpg", smallThumbStream);

		byte[] smallThumbByteArray = smallThumbStream.toByteArray();

		ByteArrayOutputStream mediumThumbStream = new ByteArrayOutputStream();

		ImageIO.write(mediumImage, "jpg", mediumThumbStream);

		byte[] mediumThumbByteArray = mediumThumbStream.toByteArray();

		picture.setSmallThumb(new SerialBlob(smallThumbByteArray));
		picture.setMediumThumb(new SerialBlob(mediumThumbByteArray));

		picture.setFilename(filename);
		picture.setGalleryID(gallery.getGalleryID());

		pictureDao.set(picture);

		return picture;
	}

	protected void checkAccess(User user, Gallery gallery) throws AccessDeniedException {

		if (!AccessUtils.checkAccess(user, gallery) && !AccessUtils.checkAccess(user, this)) {
			throw new AccessDeniedException("Permission to gallery " + gallery + " denied");
		}
	}

	protected void checkAdminAccess(User user) throws AccessDeniedException {

		if (!AccessUtils.checkAccess(user, this)) {
			throw new AccessDeniedException("Gallery admin access denied");
		}
	}

	private void checkUploadAccess(User user, Gallery gallery) throws AccessDeniedException {

		if (!AccessUtils.checkAccess(user, this) && !AccessUtils.checkAccess(user, new GalleryUploadAccessWrapper(gallery))) {
			throw new AccessDeniedException("Gallery upload access denied in gallery " + gallery);
		}
	}

	protected boolean checkBooleanAccess(User user, Gallery gallery) {

		return AccessUtils.checkAccess(user, gallery) || this.checkBooleanAdminAccess(user);
	}

	protected Boolean checkBooleanAdminAccess(User user) {

		return AccessUtils.checkAccess(user, this);
	}

	private Boolean checkBooleanUploadAccess(User user, Gallery gallery) {

		return AccessUtils.checkAccess(user, new GalleryUploadAccessWrapper(gallery));
	}

	public static void writePicture(Picture picture, boolean mediumThumb, HttpServletResponse res) throws SQLException, IOException {

		// send thumb to user
		Blob blob = null;
		if (mediumThumb) {
			blob = picture.getMediumThumb();
		} else {
			blob = picture.getSmallThumb();
		}

		HTTPUtils.setContentLength(blob.length(), res);

		res.setContentType("image/jpeg");
		res.setHeader("Content-Disposition", "inline; filename=\"" + FileUtils.toValidHttpFilename(picture.getFilename()) + "\"");

		InputStream in = null;
		OutputStream out = null;

		try{
			in = blob.getBinaryStream();
			out = res.getOutputStream();

			StreamUtils.transfer(in, out);
		}finally{

			StreamUtils.closeStream(in);
			StreamUtils.closeStream(out);
		}
	}

	@WebPublic
	public SimpleForegroundModuleResponse getImage(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws SQLException, URINotFoundException, AccessDeniedException {

		Gallery gallery = null;

		// check that the requested gallery exist
		if (uriParser.size() != 4 || (gallery = this.galleryDao.get(uriParser.get(2))) == null) {
			throw new URINotFoundException(uriParser);
		} else {

			this.checkAccess(user, gallery);

			String filename = uriParser.get(3).toString();

			// check if file is readable
			if (FileUtils.isReadable(gallery.getUrl() + File.separator + filename) && SimpleFileFilter.isValidFilename(filename)) {

				FileInputStream in = null;
				OutputStream out = null;

				try {
					log.debug("User " + user + " requesting image " + filename + " in gallery " + gallery);

					File file = new File(gallery.getUrl() + File.separator + filename);

					in = new FileInputStream(file);

					HTTPUtils.setContentLength(file.length(), res);

					res.setContentType(MimeUtils.getMimeType(file));

					res.setHeader("Content-Disposition", "inline; filename=\"" + FileUtils.toValidHttpFilename(filename) + "\"");

					out = res.getOutputStream();

					StreamUtils.transfer(in, out);

				} catch (RuntimeException e) {

					log.debug("Caught exception " + e + " while sending picture " + filename + " in gallery " + gallery + " to " + user);

				} catch (IOException e) {

					log.debug("Caught exception " + e + " while sending picture " + filename + " in gallery " + gallery + " to " + user);

				}finally{

					StreamUtils.closeStream(in);
					StreamUtils.closeStream(out);
				}

			} else {
				log.info("The picture " + filename + " in gallery " + gallery + " requested by user " + user + " does not exist");
				throw new URINotFoundException(uriParser);
			}
		}

		return null;
	}

	@Override
	public List<SettingDescriptor> getSettings() {

		ArrayList<SettingDescriptor> combinedSettings = new ArrayList<SettingDescriptor>();

		List<? extends SettingDescriptor> superSettings = super.getSettings();

		if (superSettings != null) {
			combinedSettings.addAll(superSettings);
		}

		combinedSettings.addAll(SETTINGDESCRIPTORS);

		// Generate group multilist settingdescriptor
		ArrayList<ValueDescriptor> groupValueDescriptors = new ArrayList<ValueDescriptor>();

		for (Group group : sectionInterface.getSystemInterface().getGroupHandler().getGroups(false)) {
			groupValueDescriptors.add(new ValueDescriptor(group.getName(), group.getGroupID().toString()));
		}

		combinedSettings.add(SettingDescriptor.createMultiListSetting("adminGroupIDs", "Admin groups", "Groups that are allowed to administrate the gallery module", false, null, groupValueDescriptors));

		// Generate user multilist settingdescriptor
		ArrayList<ValueDescriptor> userValueDescriptors = new ArrayList<ValueDescriptor>();

		for (User user : sectionInterface.getSystemInterface().getUserHandler().getUsers(false, false)) {
			userValueDescriptors.add(new ValueDescriptor(user.getFirstname() + " " + user.getLastname(), user.getUserID().toString()));
		}

		combinedSettings.add(SettingDescriptor.createMultiListSetting("adminUserIDs", "Admin users", "Users that are allowed to administrate the gallery module", false, null, userValueDescriptors));

		return combinedSettings;
	}

	@WebPublic
	public SimpleForegroundModuleResponse deleteGallery(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException, SQLException, URINotFoundException, AccessDeniedException {

		this.checkAdminAccess(user);

		Gallery gallery = null;

		if (uriParser.size() < 3 || (gallery = this.galleryDao.get(uriParser.get(2))) == null) {

			throw new URINotFoundException(uriParser);

		} else {

			log.info("User " + user + " deleting gallery " + gallery);

			this.galleryDao.delete(gallery);

			this.redirectToDefaultMethod(req, res);

			return null;
		}
	}

	@WebPublic
	public SimpleForegroundModuleResponse regenerateGalleryThubms(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException, SQLException, URINotFoundException, AccessDeniedException {

		this.checkAdminAccess(user);

		Gallery gallery = null;

		if (uriParser.size() < 3 || (gallery = this.galleryDao.get(uriParser.get(2))) == null) {

			throw new URINotFoundException(uriParser);

		} else {

			Long currentTime = System.currentTimeMillis();

			log.info("User " + user + " regenerating thumbs for gallery " + gallery + "...");

			this.createGalleryThumbs(gallery, true);

			log.info("User " + user + " regenerated thumbs for gallery " + gallery + " in " + TimeUtils.millisecondsToString(System.currentTimeMillis() - currentTime));

			this.redirectToDefaultMethod(req, res);

			return null;
		}
	}

	@WebPublic(alias="download")
	public SimpleForegroundModuleResponse downloadGallery(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws SQLException, URINotFoundException {

		Gallery gallery = null;

		if (uriParser.size() < 3 || (gallery = this.galleryDao.get(uriParser.get(2))) == null) {

			throw new URINotFoundException(uriParser);

		}

		// check if path is valid
		File dir = new File(gallery.getUrl());

		if (!dir.canRead()) {
			throw new URINotFoundException(uriParser);
		}

		log.info("User " + user + " downloading gallery " + gallery + "...");

		long startTime = System.currentTimeMillis();

		File[] files = dir.listFiles(fileFilter);

		ZipOutputStream zipOutputStream = null;

		try {
			res.setContentType("application/zip");
			res.setHeader("Content-Disposition", "inline; filename=\"" + FileUtils.toValidHttpFilename(gallery.getName()) + ".zip\"");
			res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, proxy-revalidate");

			zipOutputStream = new ZipOutputStream(res.getOutputStream());
			zipOutputStream.setLevel(ZipOutputStream.STORED);

			ZipUtils.addFiles(files, zipOutputStream);

			zipOutputStream.flush();

			log.info("Sent gallery " + gallery + " containing " + files.length + " files to user " + user + " in " + TimeUtils.millisecondsToString(System.currentTimeMillis() - startTime));

		} catch (IOException e) {

			log.info("Error sending gallery " + gallery + " to user " + user);

		} finally {

			StreamUtils.closeStream(zipOutputStream);
		}

		return null;
	}

	@WebPublic
	public synchronized SimpleForegroundModuleResponse regenerateThumbs(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		this.checkAdminAccess(user);

		Long currentTime = System.currentTimeMillis();

		log.info("User " + user + " regenerating thumbs for all galleries...");

		ArrayList<Gallery> galleries = this.galleryDao.getAll();

		for (Gallery gallery : galleries) {
			this.createGalleryThumbs(gallery, true);
		}

		log.info("User " + user + " regenerated thumbs for all galleries in " + TimeUtils.millisecondsToString(System.currentTimeMillis() - currentTime));

		this.redirectToDefaultMethod(req, res);

		return null;
	}

	@WebPublic
	public SimpleForegroundModuleResponse updateComment(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException, SQLException, URINotFoundException, AccessDeniedException {

		this.checkAdminAccess(user);

		Comment comment = null;

		if (uriParser.size() < 3 || (comment = this.commentDao.get(Integer.parseInt((uriParser.get(2))))) == null) {

			throw new URINotFoundException(uriParser);

		} else {

			String commentText = req.getParameter("comment");

			if (!StringUtils.isEmpty(commentText)) {

				log.info("User " + user + " updating comment " + comment + " new text " + commentText);

				comment.setComment(commentText);

				this.commentDao.update(comment);
			}

			Picture picture = this.pictureDao.get(comment.getPictureID(), false, false);

			Gallery gallery = this.galleryDao.get(picture.getGalleryID());

			res.sendRedirect(this.getModuleURI(req) + "/showImage/" + URLEncoder.encode(gallery.getAlias(), "UTF-8") + "/" + URLEncoder.encode(picture.getFilename(), "UTF-8"));

			return null;
		}
	}

	@WebPublic
	public SimpleForegroundModuleResponse deleteComment(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException, SQLException, URINotFoundException, AccessDeniedException {

		this.checkAdminAccess(user);

		Comment comment = null;

		if (uriParser.size() < 3 || (comment = this.commentDao.get(Integer.parseInt((uriParser.get(2))))) == null) {

			throw new URINotFoundException(uriParser);

		} else {

			log.info("User " + user + " deleting comment " + comment);

			Picture picture = this.pictureDao.get(comment.getPictureID(), false, false);

			Gallery gallery = this.galleryDao.get(picture.getGalleryID());

			this.commentDao.delete(comment);

			res.sendRedirect(this.getModuleURI(req) + "/showImage/" + URLEncoder.encode(gallery.getAlias(), "UTF-8") + "/" + URLEncoder.encode(picture.getFilename(), "UTF-8"));

			return null;
		}
	}

	@WebPublic
	public SimpleForegroundModuleResponse deleteImage(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException, SQLException, URINotFoundException, ValidationException, AccessDeniedException {

		this.checkAdminAccess(user);

		Gallery gallery = null;

		if (req.getMethod().equalsIgnoreCase("POST")) {

			if (uriParser.size() < 3 || (gallery = this.galleryDao.get(uriParser.get(2))) == null) {
				throw new URINotFoundException(uriParser);
			}

			String[] filenames;

			if ((filenames = req.getParameterValues("delete")) != null) {

				List<File> filesToDelete = new ArrayList<File>();

				for (String filename : filenames) {

					File f = new File(gallery.getUrl() + "/" + filename);

					// Make sure the picture (file) exists and isn't write protected
					if (!f.exists() || !f.canWrite() || f.isDirectory()) {
						throw new URINotFoundException(uriParser);
					}

					filesToDelete.add(f);

				}

				for (File f : filesToDelete) {

					log.info("User " + user + " deleting image " + f);

					// delete picture
					boolean success = f.delete();

					// delete thumbs from database
					Picture picture = pictureDao.getByFilename(f.getName(), gallery.getGalleryID(), false, false);
					pictureDao.delete(picture);

					if (!success) {
						throw new ValidationException(new ValidationError("UnableToParseRequest"));
					}
				}

			}

			res.sendRedirect(this.getModuleURI(req) + "/showGallery/" + URLEncoder.encode(gallery.getAlias(), "UTF-8"));

		} else if (uriParser.size() < 4 || (gallery = this.galleryDao.get(uriParser.get(2))) == null) {

			throw new URINotFoundException(uriParser);

		} else {

			String filename = uriParser.get(3);

			File f = new File(gallery.getUrl() + "/" + filename);

			// Make sure the picture (file) exists and isn't write protected
			if (!f.exists() || !f.canWrite() || f.isDirectory()) {
				throw new URINotFoundException(uriParser);
			}

			log.info("User " + user + " deleting image " + f);

			// delete picture
			boolean success = f.delete();

			// delete thumbs from database
			Picture picture = pictureDao.getByFilename(filename, gallery.getGalleryID(), false, false);
			pictureDao.delete(picture);

			if (!success) {
				throw new ValidationException(new ValidationError("UnableToParseRequest"));
			}

			res.sendRedirect(this.getModuleURI(req) + "/showGallery/" + URLEncoder.encode(gallery.getAlias(), "UTF-8"));

		}

		return null;
	}

	@SuppressWarnings("deprecation")
	@WebPublic
	public SimpleForegroundModuleResponse addGallery(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		this.checkAdminAccess(user);

		ValidationException validationException = null;

		MultipartRequest requestWrapper = null;

		if (req.getMethod().equalsIgnoreCase("POST")) {
			try {
				requestWrapper = new MultipartRequest(this.ramThreshold * BinarySizes.KiloByte, this.diskThreshold * BinarySizes.MegaByte, req);

				Gallery gallery = GalleryPopulator.populate(requestWrapper);

				log.info("User " + user + " adding gallery " + gallery);

				gallery.setGalleryID(this.galleryDao.add(gallery));

				Boolean zipUpload = requestWrapper.getParameter("uploadCheck") != null;

				if (zipUpload) {

					try {

						FileItem fileItem = requestWrapper.getFile(0);

						this.uploadGalleryZip(fileItem, gallery);

					} catch (FileUploadException e) {
						this.galleryDao.delete(gallery);
						throw new ValidationException(new ValidationError("UnableToParseRequest"));
					} catch(IOException e) {
						this.galleryDao.delete(gallery);
						throw new ValidationException(new ValidationError("UnableToParseRequest"));
					} finally {
						if (requestWrapper != null) {
							requestWrapper.deleteFiles();
						}
					}

				}

				// create thumbs
				this.createGalleryThumbs(gallery, false);

				res.sendRedirect(this.getModuleURI(req));
				return null;

			} catch (ValidationException e) {
				validationException = e;
			}
		}

		Document doc = this.createDocument(req, uriParser, user);

		Element addGalleryElement = doc.createElement("addGallery");
		doc.getFirstChild().appendChild(addGalleryElement);
		if(!StringUtils.isEmpty(this.path)) {
			addGalleryElement.appendChild(XMLUtils.createElement("path",this.path, doc));
		}

		if (validationException != null) {
			addGalleryElement.appendChild(validationException.toXML(doc));
			addGalleryElement.appendChild(RequestUtils.getRequestParameters(requestWrapper, doc));

		}

		AccessUtils.appendGroupsAndUsers(doc, addGalleryElement, sectionInterface.getSystemInterface().getUserHandler(), sectionInterface.getSystemInterface().getGroupHandler());

		return new SimpleForegroundModuleResponse(doc, this.moduleDescriptor.getName(), this.getDefaultBreadcrumb());
	}

	@SuppressWarnings("deprecation")
	@WebPublic
	public SimpleForegroundModuleResponse addImages(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		Gallery gallery = null;

		if (uriParser.size() < 3 || (gallery = this.galleryDao.get(uriParser.get(2))) == null) {

			throw new URINotFoundException(uriParser);

		} else {

			this.checkUploadAccess(user, gallery);

			ValidationException validationException = null;

			if (req.getMethod().equalsIgnoreCase("POST")) {

				MultipartRequest requestWrapper = null;

				try {
					requestWrapper = new MultipartRequest(this.ramThreshold * BinarySizes.KiloByte, this.diskThreshold * BinarySizes.MegaByte, req);

					FileItem fileItem = requestWrapper.getFile(0);

					if (fileItem.getName() != null && fileItem.getName().toLowerCase().endsWith(".zip")) {

						log.info("User " + user + " adding images from zip file to gallery " + gallery);

						int imageCount = this.uploadGalleryZip(fileItem, gallery);

						log.info("User " + user + " added " + imageCount + " images to gallery " + gallery);

					} else if (fileFilter.accept(fileItem)) {

						log.info("User " + user + " adding image to gallery " + gallery);

						File file = new File(gallery.getUrl() + "/" + fileItem.getName());
						fileItem.write(file);

						log.info("User " + user + " added 1 image to gallery " + gallery);

					} else {
						throw new ValidationException(new ValidationError("UnableToParseRequest"));
					}

					// create thumbs
					this.createGalleryThumbs(gallery, false);

					res.sendRedirect(this.getModuleURI(req));
					return null;

				} catch (ValidationException e) {
					validationException = e;
				} catch (FileSizeLimitExceededException e) {
					validationException = new ValidationException(new ValidationError("FileSizeLimitExceeded"));
				} catch (FileUploadException e) {
					validationException = new ValidationException(new ValidationError("UnableToParseRequest"));
				} finally {
					if (requestWrapper != null) {
						requestWrapper.deleteFiles();
					}
				}
			}

			Document doc = this.createDocument(req, uriParser, user);

			Element addImageElement = doc.createElement("addImages");
			doc.getFirstChild().appendChild(addImageElement);
			addImageElement.appendChild(gallery.toXML(doc));

			XMLUtils.appendNewElement(doc, addImageElement, "diskThreshold", diskThreshold);

			if (validationException != null) {
				addImageElement.appendChild(validationException.toXML(doc));
				addImageElement.appendChild(RequestUtils.getRequestParameters(req, doc));
			}

			AccessUtils.appendGroupsAndUsers(doc, addImageElement, sectionInterface.getSystemInterface().getUserHandler(), sectionInterface.getSystemInterface().getGroupHandler());

			return new SimpleForegroundModuleResponse(doc, this.moduleDescriptor.getName(), this.getDefaultBreadcrumb());
		}

	}

	@SuppressWarnings("deprecation")
	@WebPublic
	public SimpleForegroundModuleResponse updateGallery(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException, SQLException, URINotFoundException, AccessDeniedException {

		this.checkAdminAccess(user);

		Gallery gallery = null;

		if (uriParser.size() != 3 || (gallery = this.galleryDao.get(uriParser.get(2))) == null) {
			throw new URINotFoundException(uriParser);

		} else {

			ValidationException validationException = null;

			if (req.getMethod().equalsIgnoreCase("POST")) {
				try {
					gallery = GalleryPopulator.populate(gallery, req);

					log.info("User " + user + " updating gallery " + gallery);

					this.galleryDao.update(gallery);

					res.sendRedirect(this.getModuleURI(req));
					return null;

				} catch (ValidationException e) {
					validationException = e;
				}
			}

			Document doc = this.createDocument(req, uriParser, user);

			Element updateGalleryElement = doc.createElement("updateGallery");
			doc.getFirstChild().appendChild(updateGalleryElement);
			if(!StringUtils.isEmpty(this.path)) {
				updateGalleryElement.appendChild(XMLUtils.createElement("path",this.path, doc));
			}

			updateGalleryElement.appendChild(gallery.toXML(doc));

			if (validationException != null) {
				updateGalleryElement.appendChild(validationException.toXML(doc));
				updateGalleryElement.appendChild(RequestUtils.getRequestParameters(req, doc));
			}

			AccessUtils.appendGroupsAndUsers(doc, updateGalleryElement, sectionInterface.getSystemInterface().getUserHandler(), sectionInterface.getSystemInterface().getGroupHandler());

			return new SimpleForegroundModuleResponse(doc, this.moduleDescriptor.getName(), this.getDefaultBreadcrumb());
		}
	}

	@WebPublic
	public synchronized SimpleForegroundModuleResponse checkForNewImages(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		this.checkAdminAccess(user);

		log.info("User " + user + " checking for new images in all galleries");

		ArrayList<Gallery> galleries = this.galleryDao.getAll();

		for (Gallery gallery : galleries) {
			this.createGalleryThumbs(gallery, false);
		}

		this.redirectToDefaultMethod(req, res);

		return null;
	}

	protected int uploadGalleryZip(FileItem fileItem, Gallery gallery) throws FileNotFoundException, IOException, Exception {

		File file = null;

		int fileCount = 0;

		try {

			file = File.createTempFile("galleryupload-" + System.currentTimeMillis(), ".zip");
			fileItem.write(file);
			ZipFile zipFile = new ZipFile(file);
			Enumeration<? extends ZipEntry> e = zipFile.entries();

			while (e.hasMoreElements()) {
				ZipEntry ze = e.nextElement();

				if (!ze.isDirectory() && SimpleFileFilter.isValidFilename(ze.getName())) {

					String filename = FilenameUtils.getName(ze.getName());

					if (new File(gallery.getUrl() + "/" + filename).exists()) {

						log.info("Skipping file " + filename + ", already exists in gallery " + gallery + " in directory " + gallery.getUrl());

						continue;
					}

					log.info("Adding file " + filename + " to gallery " + gallery + " in directory " + gallery.getUrl());

					FileOutputStream fileOutputStream = null;
					InputStream zipEntryInputStream = null;

					try {
						fileOutputStream = new FileOutputStream(gallery.getUrl() + "/" + filename);
						zipEntryInputStream = zipFile.getInputStream(ze);

						StreamUtils.transfer(zipEntryInputStream, fileOutputStream);

						fileCount++;
					} finally {

						StreamUtils.closeStream(zipEntryInputStream);
						StreamUtils.closeStream(fileOutputStream);
					}
				}
			}
		} finally {
			if (file != null) {
				file.delete();
			}
		}

		return fileCount;

	}

	protected void createGalleryThumbs(Gallery gallery, boolean overwrite) throws URINotFoundException, SQLException, IOException {

		synchronized (this.mutexKeyProvider.getKey(gallery)) {

			// get all files from the gallery on filesystem
			File dir = new File(gallery.getUrl());

			if (!dir.canRead()) {
				return;
			}

			// get all images in gallerydirectory
			File[] allFiles = dir.listFiles(fileFilter);

			if (allFiles != null) {

				for (File file : allFiles) {

					if (file.exists() && file.canRead()) {

						if (overwrite) {

							this.createThumbs(file.getName(), gallery);
						} else {
							Picture picture = this.pictureDao.getByFilename(file.getName(), gallery.getGalleryID(), false, false);

							if (picture == null) {

								try {

									this.createThumbs(file.getName(), gallery);

								} catch (RuntimeException e) {
									log.error("Error " + e + " creating thumb for file " + file.getName() + " in gallery " + gallery);
								}
							}
						}

					} else {
						log.warn("Unable to read file " + file + " in gallery " + gallery);
					}
				}
			}
		}
	}

	@Override
	public boolean allowsAdminAccess() {

		return false;
	}

	@Override
	public boolean allowsAnonymousAccess() {

		return false;
	}

	@Override
	public boolean allowsUserAccess() {

		return false;
	}

	@Override
	public Collection<Integer> getAllowedGroupIDs() {

		return this.adminGroupIDs;
	}

	@Override
	public Collection<Integer> getAllowedUserIDs() {

		return this.adminUserIDs;
	}
}
