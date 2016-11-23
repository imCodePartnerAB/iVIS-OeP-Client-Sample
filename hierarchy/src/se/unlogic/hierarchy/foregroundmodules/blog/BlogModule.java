/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.blog;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.emailutils.populators.EmailPopulator;
import se.unlogic.hierarchy.core.annotations.CheckboxSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextAreaSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.annotations.XSLVariable;
import se.unlogic.hierarchy.core.beans.Breadcrumb;
import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.beans.SettingDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleBundleDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.SimpleMenuItemDescriptor;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.beans.ValueDescriptor;
import se.unlogic.hierarchy.core.enums.CRUDAction;
import se.unlogic.hierarchy.core.enums.EventTarget;
import se.unlogic.hierarchy.core.enums.MenuItemType;
import se.unlogic.hierarchy.core.enums.URLType;
import se.unlogic.hierarchy.core.events.CRUDEvent;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.interfaces.AccessInterface;
import se.unlogic.hierarchy.core.interfaces.BundleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.MenuItemDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.utils.AccessUtils;
import se.unlogic.hierarchy.core.utils.BaseFileAccessValidator;
import se.unlogic.hierarchy.core.utils.FCKConnector;
import se.unlogic.hierarchy.core.utils.RSSUserStringyfier;
import se.unlogic.hierarchy.core.utils.SimpleFileAccessValidator;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.hierarchy.foregroundmodules.blog.beans.ArchiveEntry;
import se.unlogic.hierarchy.foregroundmodules.blog.beans.BlogPost;
import se.unlogic.hierarchy.foregroundmodules.blog.beans.Comment;
import se.unlogic.hierarchy.foregroundmodules.blog.beans.TagEntry;
import se.unlogic.hierarchy.foregroundmodules.blog.daos.BlogDAOFactory;
import se.unlogic.hierarchy.foregroundmodules.blog.daos.BlogPostDAO;
import se.unlogic.hierarchy.foregroundmodules.blog.daos.CommentDAO;
import se.unlogic.hierarchy.foregroundmodules.blog.daos.mysql.MySQLBlogDAOFactory;
import se.unlogic.hierarchy.foregroundmodules.blog.populators.BlogPostPopulator;
import se.unlogic.purecaptcha.CaptchaHandler;
import se.unlogic.purecaptcha.DefaultCaptchaHandler;
import se.unlogic.standardutils.enums.EnumUtils;
import se.unlogic.standardutils.enums.Month;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.populators.NonNegativeStringIntegerPopulator;
import se.unlogic.standardutils.rss.RSSChannel;
import se.unlogic.standardutils.rss.RSSGenerator;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.time.MillisecondTimeUnits;
import se.unlogic.standardutils.validation.PositiveStringIntegerValidator;
import se.unlogic.standardutils.validation.StringIntegerValidator;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationErrorType;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.HTTPUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.populators.StringHTTPURLPopulator;
import se.unlogic.webutils.populators.annotated.AnnotatedRequestPopulator;

public class BlogModule extends AnnotatedForegroundModule implements AccessInterface, RSSChannel {

	public static final String RELATIVE_PATH_MARKER = "/@";
	private static final BlogPostPopulator BLOG_POST_POPULATOR = new BlogPostPopulator();
	private static final AnnotatedRequestPopulator<Comment> COMMENT_POPULATOR = new AnnotatedRequestPopulator<Comment>(Comment.class, new StringHTTPURLPopulator(), new EmailPopulator());

	private static final ArrayList<SettingDescriptor> SETTINGDESCRIPTORS = new ArrayList<SettingDescriptor>();

	static {
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("blogID", "Blog ID", "Change this field if you have multiple blogs using the same database.", false, "default", null));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("cssPath", "Editor CSS", "Path to the desired CSS stylesheet for FCKEditor (relative from the contextpath)", false, null, null));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("filestorePath", "Filestore path", "Path to the directory to be used as filestore", false, null, null));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("diskThreshold", "Max upload size", "Maxmium upload size in megabytes allowed in a single post request", false, "100", new StringIntegerValidator(1, null)));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("ramThreshold", "RAM threshold", "How many megabytes of RAM to use as buffer during file uploads. If the threshold is exceeded the files are written to disk instead.", false, "20", new StringIntegerValidator(1, null)));

		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("blogPostsPerPage", "Blog posts per page", "The number of blog posts to be showed per page", true, "5", new StringIntegerValidator(1, null)));

		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("maxCommentLength", "Max comment length", "The maximum numbers of characters allowed in comments", true, "1000", new StringIntegerValidator(1, null)));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createCheckboxSetting("allowAnonymousComments", "Allow anonymous comments", "Controls whether or not anonymous user can post comments", false));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createCheckboxSetting("allowComments", "Allow comments", "Controls whether or not comments should be allowed", true));
		
		SETTINGDESCRIPTORS.add(SettingDescriptor.createCheckboxSetting("displayFullName", "Show first and lastname", "Controls if first and lastname are displayed instead of username", false));

		SETTINGDESCRIPTORS.add(SettingDescriptor.createCheckboxSetting("showMainMenuitem", "Show blog menuitem", "Controls whether or not the main blog menuitem is shown in the menu", true));

		SETTINGDESCRIPTORS.add(SettingDescriptor.createCheckboxSetting("showTagsBundle", "Show tag bundle", "Controls whether or not the tag bundle is shown in the menu", false));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("tagBundleLimit", "Max number of tags in bundle", "The maximum numbers of tags to be shown in the tags menu bundle", true, "10", new StringIntegerValidator(1, null)));

		SETTINGDESCRIPTORS.add(SettingDescriptor.createCheckboxSetting("showArchiveBundle", "Show archive bundle", "Controls whether or not the arcive bundle is shown in the menu", false));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("archiveBundleLimit", "Max number of months in bundle", "The maximum numbers of months to be shown in the archive menu bundle", true, "10", new StringIntegerValidator(1, null)));
	}

	@XSLVariable
	private String tagBundleName = "Tags";

	@XSLVariable
	private String tagBundleDescription = "Popular tags";

	@XSLVariable
	private String tagBundleMenuitemDescription = "Show all blog posts tagged with tag ";

	@XSLVariable
	private String archiveBundleName = "Archive";

	@XSLVariable
	private String archiveBundleDescription = "Blog post archive";

	@XSLVariable
	private String archiveBundleMenuitemDescription = "Show all blog posts from ";

	@XSLVariable
	private String pageBreadcrumbText = "Page ";

	@XSLVariable
	private String archiveBreadcrumbText = "Archive ";

	@XSLVariable
	private String tagsBreadcrumbText = "Tags ";

	@XSLVariable
	private String tagBreadcrumbText = "Tag ";

	@XSLVariable
	private String addBlogPostBreadcrumbText = "Add blog post";

	@XSLVariable
	private String updateBlogPostBreadcrumbText = "Edit blog post ";

	@XSLVariable
	private String updateCommentBreadcrumbText = "Edit comment";

	@XSLVariable
	private String january;

	@XSLVariable
	private String february;

	@XSLVariable
	private String march;

	@XSLVariable
	private String april;

	@XSLVariable
	private String may;

	@XSLVariable
	private String june;

	@XSLVariable
	private String july;

	@XSLVariable
	private String august;

	@XSLVariable
	private String september;

	@XSLVariable
	private String october;

	@XSLVariable
	private String november;

	@XSLVariable
	private String december;

	@ModuleSetting
	private String blogID = "default";

	@ModuleSetting
	private String cssPath;

	@ModuleSetting
	private String filestorePath;

	@ModuleSetting
	private Integer blogPostsPerPage = 5;

	@ModuleSetting
	private Integer maxCommentLength = 1000;

	@ModuleSetting
	private Boolean allowComments = true;
	
	@ModuleSetting
	private Boolean allowAnonymousComments = false;

	@ModuleSetting
	private boolean showMainMenuitem = true;

	@ModuleSetting
	private boolean showTagsBundle;

	@ModuleSetting
	private Integer tagBundleLimit = 10;

	@ModuleSetting(allowsNull = true)
	private List<Integer> adminGroupIDs;

	@ModuleSetting(allowsNull = true)
	private List<Integer> adminUserIDs;

	@ModuleSetting
	private Integer diskThreshold = 100;

	@ModuleSetting
	@TextFieldSettingDescriptor(name="RAM threshold",description="Maximum size of files in KB to be buffered in RAM during file uploads. Files exceeding the threshold are written to disk instead.",required=true,formatValidator=PositiveStringIntegerValidator.class)
	protected Integer ramThreshold = 500;

	@ModuleSetting
	protected boolean displayFullName;

	@ModuleSetting
	private boolean showArchiveBundle;

	@ModuleSetting
	private Integer archiveBundleLimit = 10;
	
	@ModuleSetting
	@CheckboxSettingDescriptor(name="Add module to instance handle", description="Controls if this module should register itself in the global instance handler on startup")
	private boolean addToInstanceHandler = true;

	/* Feed Settings */
	@ModuleSetting
	@CheckboxSettingDescriptor(name = "Enable RSS feed support", description = "Enables or disables the RSS feed support for this blog")
	private boolean enableRSSFeed = false;

	@TextFieldSettingDescriptor(name = "RSS link to the blog", description = "The absolute URL to this blog to be used in the RSS channel", required = true)
	@ModuleSetting
	private String feedLink = "http://subdomain.domain.tld/blog";

	@TextFieldSettingDescriptor(name = "RSS Title", description = "Defines the title of the channel. Typically the name of this blog", required = true)
	@ModuleSetting
	private String feedTitle = "My Blog";

	@TextAreaSettingDescriptor(name = "RSS Description", description = "Describes the channel. Typically a description of this blog", required = true)
	@ModuleSetting
	private String feedDescription = "Description of My Blog...";

	@TextFieldSettingDescriptor(name = "RSS Items", description = "Specifies the number of latest items kept in the channel, where 0 is unlimited", required = true, formatValidator = NonNegativeStringIntegerPopulator.class)
	@ModuleSetting
	private Integer feedItemsPerChannel = 15;

	@TextFieldSettingDescriptor(name = "RSS Managing Editor", description = "Defines the e-mail address to the editor of the content of the feed", required = false)
	@ModuleSetting
	private String feedManagingEditor;

	@TextFieldSettingDescriptor(name = "RSS Webmaster", description = "Defines the e-mail address to the webmaster of the feed", required = false)
	@ModuleSetting
	private String feedWebmaster;

	@TextAreaSettingDescriptor(name = "RSS Copyright", description = "Notifies about copyrighted material", required = false)
	@ModuleSetting
	private String feedCopyright;

	@TextFieldSettingDescriptor(name = "RSS Language", description = "Specifies the language the feed is written in", required = true)
	@ModuleSetting
	private String feedLanguage = "sv-se";

	@TextFieldSettingDescriptor(name = "RSS TTL", description = "Specifies the number of minutes the feed can stay cached before refreshing it from the source", required = true, formatValidator = PositiveStringIntegerValidator.class)
	@ModuleSetting
	private Integer feedTTL = 60;

	@TextFieldSettingDescriptor(name = "RSS item description max length", description = "Specifies the max allowed length for rss item description, where 0 is unlimited", required = true, formatValidator = StringIntegerValidator.class)
	@ModuleSetting
	private Integer feedItemDescriptionMaxLength = 247;
	
	private BlogPostDAO blogPostDAO;
	private CommentDAO commentDAO;

	private CaptchaHandler captchaHandler;
	private RSSGenerator rssGenerator;
	private RSSUserStringyfier rssUserStringyfier;
	private FCKConnector connector;

	private String cachedFeedXML;

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		if(addToInstanceHandler && !systemInterface.getInstanceHandler().addInstance(BlogModule.class, this)){

			log.warn("Unable to register module " + this.moduleDescriptor + " in global instance handler using key " + BlogModule.class.getSimpleName() + ", another instance is already registered using this key.");
		}
		
		this.connector = new FCKConnector(this.filestorePath, this.diskThreshold, this.ramThreshold);

		captchaHandler = new DefaultCaptchaHandler(this.getClass().getName() + ":" + this.moduleDescriptor.getModuleID(), 5 * MillisecondTimeUnits.MINUTE, false);

		rssGenerator = new RSSGenerator(this, feedItemDescriptionMaxLength);
		
		this.rssUserStringyfier = new RSSUserStringyfier(displayFullName);

		this.cacheRSSFeed();
	}
	
	@Override
	public void update(ForegroundModuleDescriptor moduleDescriptor, DataSource dataSource) throws Exception {

		super.update(moduleDescriptor, dataSource);

		this.connector.setDiskThreshold(this.diskThreshold);
		this.connector.setRamThreshold(this.ramThreshold);
		this.connector.setFilestorePath(filestorePath);

		rssGenerator = new RSSGenerator(this, feedItemDescriptionMaxLength);
		
		this.rssUserStringyfier = new RSSUserStringyfier(displayFullName);

		this.cacheRSSFeed();
		
		if(addToInstanceHandler){
			
			BlogModule blogModule = systemInterface.getInstanceHandler().getInstance(BlogModule.class);
			
			if(blogModule == null){
				
				systemInterface.getInstanceHandler().addInstance(BlogModule.class, this);
				
			}else if(!blogModule.equals(this)){
				
				log.warn("Unable to register module " + this.moduleDescriptor + " in global instance handler using key " + BlogModule.class.getSimpleName() + ", another instance is already registered using this key.");
			}
			
		}else{
			
			if(this.equals(systemInterface.getInstanceHandler().getInstance(BlogModule.class))){
				
				systemInterface.getInstanceHandler().removeInstance(BlogModule.class);
			}
		}
	}
	
	@Override
	public void unload() throws Exception {

		if(this.equals(systemInterface.getInstanceHandler().getInstance(BlogModule.class))){
		
			systemInterface.getInstanceHandler().removeInstance(BlogModule.class);
		}
		
		super.unload();
	}

	private synchronized void cacheRSSFeed() {

		if (enableRSSFeed) {

			try {
				this.log.info("Generating RSS feed for blog " + this.moduleDescriptor.getName() + ", with blog id: " + this.blogID);


				List<BlogPost> blogPosts = this.getLatestPosts(this.feedItemsPerChannel, this.blogID);

				if(blogPosts != null){

					for(BlogPost post :blogPosts){

						post.setLink(this.feedLink);
						post.setUserStringyfier(rssUserStringyfier);
					}
				}

				this.cachedFeedXML = this.rssGenerator.getStringRss(blogPosts, "ISO-8859-1");

				return;
			} catch (TransformerFactoryConfigurationError e) {
				this.log.error("Error while creating RSS feed. Feed will not be available", e);
			} catch (TransformerException e) {
				this.log.error("Error while creating RSS feed. Feed will not be available", e);
			} catch (SQLException e) {
				this.log.error("Error while creating RSS feed. Feed will not be available", e);
			}
		}

		this.cachedFeedXML = null;
	}

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		BlogDAOFactory daoFactory = new MySQLBlogDAOFactory();

		daoFactory.init(dataSource, systemInterface.getUserHandler());

		this.blogPostDAO = daoFactory.getBlogPostDAO();
		this.commentDAO = daoFactory.getCommentDAO();
	}

	@Override
	public List<SettingDescriptor> getSettings() {

		ArrayList<SettingDescriptor> combinedSettings = new ArrayList<SettingDescriptor>();

		combinedSettings.addAll(SETTINGDESCRIPTORS);

		// Generate group multilist settingdescriptor
		ArrayList<ValueDescriptor> groupValueDescriptors = new ArrayList<ValueDescriptor>();

		List<Group> groups = this.systemInterface.getGroupHandler().getGroups(false);

		if (groups != null) {

			for (Group group : groups) {
				groupValueDescriptors.add(new ValueDescriptor(group.getName(), group.getGroupID().toString()));
			}
		}

		combinedSettings.add(SettingDescriptor.createMultiListSetting("adminGroupIDs", "Admin groups", "Groups that are allowed to administrate the gallery module", false, null, groupValueDescriptors));

		// Generate user multilist settingdescriptor
		ArrayList<ValueDescriptor> userValueDescriptors = new ArrayList<ValueDescriptor>();

		List<User> users = this.systemInterface.getUserHandler().getUsers(false, false);

		if (users != null) {

			for (User user : users) {
				userValueDescriptors.add(new ValueDescriptor(user.getFirstname() + " " + user.getLastname(), user.getUserID().toString()));
			}
		}

		combinedSettings.add(SettingDescriptor.createMultiListSetting("adminUserIDs", "Admin users", "Users that are allowed to administrate the gallery module", false, null, userValueDescriptors));

		List<? extends SettingDescriptor> superSettings = super.getSettings();

		if (superSettings != null) {
			combinedSettings.addAll(superSettings);
		}

		return combinedSettings;
	}

	@Override
	public SimpleForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, IOException, DOMException, SQLException {

		return this.showPage(req, res, user, uriParser);
	}

	@WebPublic(alias = "page")
	public SimpleForegroundModuleResponse showPage(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, IOException, DOMException, SQLException {

		Integer page;

		if (uriParser.size() == 3) {

			page = NumberUtils.toInt(uriParser.get(2));

			if (page == null || page < 1) {
				page = 1;
			}

		} else if (uriParser.size() > 3) {

			throw new URINotFoundException(uriParser);

		} else {

			page = 1;
		}

		List<BlogPost> posts = this.blogPostDAO.getPosts((this.blogPostsPerPage * (page - 1)), this.blogPostsPerPage, blogID);

		if (posts == null && page > 1) {
			this.redirectToDefaultMethod(req, res);
			return null;
		}

		Document doc = this.createDocument(req, uriParser, user);

		Element pageElement = doc.createElement("ShowPage");
		doc.getDocumentElement().appendChild(pageElement);

		log.info("User " + user + " requested page " + page + " in blog " + this.blogID);

		if (posts != null) {

			int postCount = this.blogPostDAO.getPostCount(blogID);

			if (((float) postCount / (float) blogPostsPerPage) > 0) {
				pageElement.appendChild(XMLUtils.createElement("pageNumber", page + "", doc));
			}

			if (((float) postCount / (float) blogPostsPerPage) <= page) {
				pageElement.appendChild(doc.createElement("lastPage"));
			}

			Element postsElement = doc.createElement("Posts");
			pageElement.appendChild(postsElement);

			for (BlogPost blogPost : posts) {
				this.setAbsoluteFileUrls(blogPost, uriParser, true);
				postsElement.appendChild(blogPost.toXML(doc));
			}
		}

		if (page == 1) {

			return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb());

		} else {

			return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb(), new Breadcrumb(this.pageBreadcrumbText + page, this.getFullAlias() + "/page/" + page));
		}
	}

	@WebPublic
	public SimpleForegroundModuleResponse post(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, IOException, SQLException {

		return this.showPost(req, res, user, uriParser, null);
	}

	public SimpleForegroundModuleResponse showPost(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, ValidationException validationException) throws URINotFoundException, IOException, SQLException {

		if (uriParser.size() < 3) {
			throw new URINotFoundException(uriParser);
		}

		if (uriParser.size() >= 4 && uriParser.get(3).equalsIgnoreCase("file")) {

			// File request
			BlogPost blogPost = this.blogPostDAO.getPost(uriParser.get(2), false, blogID);

			if (blogPost == null) {
				throw new URINotFoundException(uriParser);
			}

			this.setAbsoluteFileUrls(blogPost, uriParser, true);

			this.connector.processFileRequest(req, res, user, uriParser, moduleDescriptor, sectionInterface, 4, new SimpleFileAccessValidator(this.getAbsoluteViewFileURL(uriParser, blogPost), blogPost.getMessage()));

			return null;

		} else {

			// Post request
			BlogPost blogPost = this.blogPostDAO.getPost(uriParser.get(2), true, blogID);

			if (blogPost == null) {
				throw new URINotFoundException(uriParser);
			}

			log.info("User " + user + " requested blog post " + blogPost + " in blog " + this.blogID);

			this.setAbsoluteFileUrls(blogPost, uriParser, true);

			Document doc = this.createDocument(req, uriParser, user);

			Element viewPostElement = doc.createElement("ShowPost");
			doc.getFirstChild().appendChild(viewPostElement);

			viewPostElement.appendChild(XMLUtils.createElement("maxCommentLength", maxCommentLength + "", doc));

			if (user != null) {
				viewPostElement.appendChild(doc.createElement("isLoggedIn"));
			}

			if (allowAnonymousComments) {
				viewPostElement.appendChild(doc.createElement("allowAnonymousComments"));
			}

			viewPostElement.appendChild(blogPost.toXML(doc));

			if (validationException != null) {
				viewPostElement.appendChild(validationException.toXML(doc));
				viewPostElement.appendChild(RequestUtils.getRequestParameters(req, doc));
			} else {
				this.blogPostDAO.incrementReadCount(blogPost.getPostID());
			}

			return new SimpleForegroundModuleResponse(doc, blogPost.getTitle(), this.getDefaultBreadcrumb(), new Breadcrumb(blogPost.getTitle(), blogPost.getTitle(), this.getFullAlias() + "/post/" + blogPost.getAlias()));
		}
	}

	@WebPublic(alias = "addpost")
	public SimpleForegroundModuleResponse addPost(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, IOException, AccessDeniedException, SQLException {

		this.checkAdminAccess(user);

		ValidationException validationException = null;

		if (req.getMethod().equalsIgnoreCase("POST")) {
			try {
				BlogPost blogPost = BLOG_POST_POPULATOR.populate(req);

				if (this.blogPostDAO.getPost(blogPost.getAlias(), false, blogID) != null) {
					throw new ValidationException(Collections.singletonList(new ValidationError("AliasAlreadyTaken")));
				}

				blogPost.setPoster(user);
				blogPost.setAdded(new Timestamp(System.currentTimeMillis()));
				blogPost.setReadCount(0);

				log.info("User " + user + " adding blog post " + blogPost);

				this.removeAbsoluteFileUrls(blogPost, uriParser);

				this.blogPostDAO.add(blogPost, blogID);

				this.reloadMenuItems();

				this.cacheRSSFeed();

				systemInterface.getEventHandler().sendEvent(BlogPost.class, new CRUDEvent<BlogPost>(CRUDAction.ADD, blogPost), EventTarget.ALL);
				
				this.redirectToDefaultMethod(req, res);

				return null;

			} catch (ValidationException e) {
				validationException = e;
			}
		}

		Document doc = this.createDocument(req, uriParser, user);
		Element addBlogPostElement = doc.createElement("AddBlogPost");
		doc.getFirstChild().appendChild(addBlogPostElement);

		this.appendTags(doc, addBlogPostElement);

		if (validationException != null) {
			addBlogPostElement.appendChild(validationException.toXML(doc));
			addBlogPostElement.appendChild(RequestUtils.getRequestParameters(req, doc));
		}

		if (cssPath != null) {
			addBlogPostElement.appendChild(XMLUtils.createElement("cssPath", cssPath, doc));
		}

		return new SimpleForegroundModuleResponse(doc, addBlogPostBreadcrumbText, this.getDefaultBreadcrumb(), new Breadcrumb(addBlogPostBreadcrumbText, this.getFullAlias() + "/addpost"));
	}

	@WebPublic(alias = "updatepost")
	public SimpleForegroundModuleResponse updatePost(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, IOException, AccessDeniedException, SQLException {

		this.checkAdminAccess(user);

		BlogPost blogPost;

		if (uriParser.size() != 3 || !NumberUtils.isInt(uriParser.get(2)) || (blogPost = this.blogPostDAO.getPost(Integer.parseInt(uriParser.get(2)), true, blogID)) == null) {

			throw new URINotFoundException(uriParser);
		}

		ValidationException validationException = null;

		if (req.getMethod().equalsIgnoreCase("POST")) {
			try {
				String oldAlias = blogPost.getAlias();

				blogPost = BLOG_POST_POPULATOR.populate(blogPost, req);

				if (!blogPost.getAlias().equals(oldAlias) && this.blogPostDAO.getPost(blogPost.getAlias(), false, blogID) != null) {
					throw new ValidationException(Collections.singletonList(new ValidationError("AliasAlreadyTaken")));
				}

				blogPost.setEditor(user);
				blogPost.setUpdated(new Timestamp(System.currentTimeMillis()));

				log.info("User " + user + " updating blog post " + blogPost);

				this.removeAbsoluteFileUrls(blogPost, uriParser);

				this.blogPostDAO.update(blogPost);

				this.reloadMenuItems();

				this.cacheRSSFeed();

				systemInterface.getEventHandler().sendEvent(BlogPost.class, new CRUDEvent<BlogPost>(CRUDAction.UPDATE, blogPost), EventTarget.ALL);
				
				res.sendRedirect(this.getModuleURI(req) + "/post/" + URLEncoder.encode(blogPost.getAlias(), "UTF-8"));

				return null;

			} catch (ValidationException e) {
				validationException = e;
			}
		}

		Document doc = this.createDocument(req, uriParser, user);
		Element updatePostElement = doc.createElement("UpdateBlogPost");
		doc.getFirstChild().appendChild(updatePostElement);

		this.setAbsoluteFileUrls(blogPost, uriParser, false);
		updatePostElement.appendChild(blogPost.toXML(doc));

		this.appendTags(doc, updatePostElement);

		if (validationException != null) {
			updatePostElement.appendChild(validationException.toXML(doc));
			updatePostElement.appendChild(RequestUtils.getRequestParameters(req, doc));
		}

		if (cssPath != null) {
			updatePostElement.appendChild(XMLUtils.createElement("cssPath", cssPath, doc));
		}

		return new SimpleForegroundModuleResponse(doc, updateBlogPostBreadcrumbText + blogPost.getTitle(), this.getDefaultBreadcrumb(), new Breadcrumb(updateBlogPostBreadcrumbText + blogPost.getTitle(), this.getFullAlias() + "/updatepost/" + blogPost.getPostID()));
	}

	private void appendTags(Document doc, Element targetElement) throws SQLException {

		List<TagEntry> tags = this.blogPostDAO.getTagEntries(blogID);

		if (tags != null) {
			XMLUtils.append(doc, targetElement, "tags", tags);
		}
	}

	private String getAbsoluteEditFileURL(URIParser uriParser) {

		return uriParser.getCurrentURI(true) + "/" + this.moduleDescriptor.getAlias() + "/file";
	}

	private String getAbsoluteViewFileURL(URIParser uriParser, BlogPost blogPost) {

		return uriParser.getCurrentURI(true) + "/" + this.moduleDescriptor.getAlias() + "/post/" + blogPost.getAlias() + "/file";
	}

	public void removeAbsoluteFileUrls(BlogPost blogPost, URIParser uriParser) {

		String message = blogPost.getMessage();

		String absoluteFileURL = this.getAbsoluteEditFileURL(uriParser);

		for (String attribute : BaseFileAccessValidator.TAG_ATTRIBUTES) {

			message = message.replace(attribute + "=\"" + absoluteFileURL, attribute + "=\"" + RELATIVE_PATH_MARKER);
			message = message.replace(attribute + "='" + absoluteFileURL, attribute + "='" + RELATIVE_PATH_MARKER);
		}

		blogPost.setMessage(message);
	}

	public void setAbsoluteFileUrls(BlogPost blogPost, URIParser uriParser, boolean view) {

		String message = blogPost.getMessage();

		String absoluteFileURL;

		if (view) {
			absoluteFileURL = this.getAbsoluteViewFileURL(uriParser, blogPost);
		} else {
			absoluteFileURL = this.getAbsoluteEditFileURL(uriParser);
		}

		for (String attribute : BaseFileAccessValidator.TAG_ATTRIBUTES) {

			message = message.replace(attribute + "=\"" + RELATIVE_PATH_MARKER, attribute + "=\"" + absoluteFileURL);
			message = message.replace(attribute + "='" + RELATIVE_PATH_MARKER, attribute + "='" + absoluteFileURL);
		}

		blogPost.setMessage(message);
	}

	@WebPublic(alias = "deletepost")
	public SimpleForegroundModuleResponse deletePost(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, IOException, AccessDeniedException, NumberFormatException, SQLException {

		this.checkAdminAccess(user);

		BlogPost blogPost;

		if (uriParser.size() != 3 || !NumberUtils.isInt(uriParser.get(2)) || (blogPost = this.blogPostDAO.getPost(Integer.parseInt(uriParser.get(2)), false, blogID)) == null) {
			throw new URINotFoundException(uriParser);
		}

		log.info("User " + user + " deleteing blog post " + blogPost);

		this.blogPostDAO.delete(blogPost, blogID);

		this.reloadMenuItems();

		this.cacheRSSFeed();

		systemInterface.getEventHandler().sendEvent(BlogPost.class, new CRUDEvent<BlogPost>(CRUDAction.DELETE, blogPost), EventTarget.ALL);
		
		redirectToDefaultMethod(req, res);

		return null;
	}

	@WebPublic(alias = "addcomment")
	public SimpleForegroundModuleResponse addComment(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, IOException, AccessDeniedException, SQLException {

		BlogPost blogPost;

		if (uriParser.size() != 3 || (blogPost = this.blogPostDAO.getPost(uriParser.get(2), false, blogID)) == null) {

			throw new URINotFoundException(uriParser);

		} else if (!this.allowComments) {
			throw new AccessDeniedException("Access denied, comments are disabled");
		} else if (!this.allowAnonymousComments && user == null) {
			throw new AccessDeniedException("Access denied, anonymous comments are disabled");
		} 

		Comment comment;

		if (user != null) {

			String message = req.getParameter("message");

			if (StringUtils.isEmpty(message)) {
				return this.showPost(req, res, user, uriParser, new ValidationException(new ValidationError("message", ValidationErrorType.RequiredField)));
			} else if (message.length() > this.maxCommentLength) {
				return this.showPost(req, res, user, uriParser, new ValidationException(new ValidationError("message", ValidationErrorType.TooLong)));
			}

			comment = new Comment();
			comment.setPoster(user);
			comment.setPostID(blogPost.getPostID());
			comment.setAdded(new Timestamp(System.currentTimeMillis()));
			comment.setMessage(message);

			log.info("User " + user + " adding comment " + comment + " to blog post " + blogPost);

			this.commentDAO.add(comment);

			res.sendRedirect(this.getModuleURI(req) + "/post/" + URLEncoder.encode(blogPost.getAlias(), "UTF-8") + "#comment" + comment.getCommentID());

			return null;

		} else {

			try {
				comment = COMMENT_POPULATOR.populate(req);

				if (comment.getMessage().length() > this.maxCommentLength) {
					throw new ValidationException(new ValidationError("message", ValidationErrorType.TooLong));
				}

				String captchaConfirmation = req.getParameter("captchaConfirmation");

				if (!captchaHandler.isValidCode(req, captchaConfirmation)) {

					throw new ValidationException(new ValidationError("InvalidCaptchaConfirmation"));
				}

				comment.setPostID(blogPost.getPostID());

				comment.setAdded(new Timestamp(System.currentTimeMillis()));

				log.info("Anonmous user from " + req.getRemoteAddr() + " adding comment " + comment + " to blog post " + blogPost);

				this.commentDAO.add(comment);

				res.sendRedirect(this.getModuleURI(req) + "/post/" + URLEncoder.encode(blogPost.getAlias(), "UTF-8") + "#comment" + comment.getCommentID());

				return null;

			} catch (ValidationException validationException) {

				return this.showPost(req, res, user, uriParser, validationException);
			}
		}
	}

	@WebPublic(alias = "captcha")
	public SimpleForegroundModuleResponse getCaptchaImage(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		captchaHandler.getCaptchaImage(req, res);

		return null;
	}

	@WebPublic(alias = "updatecomment")
	public SimpleForegroundModuleResponse updateComment(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, IOException, AccessDeniedException, SQLException {

		Comment comment;

		if (uriParser.size() != 3 || !NumberUtils.isInt(uriParser.get(2)) || (comment = this.commentDAO.get(NumberUtils.toInt(uriParser.get(2)))) == null) {

			throw new URINotFoundException(uriParser);

		}

		this.checkCommentAcces(user, comment);

		ValidationException validationException = null;

		if (req.getMethod().equalsIgnoreCase("POST")) {

			String message = req.getParameter("message");

			if (StringUtils.isEmpty(message)) {

				validationException = new ValidationException(new ValidationError("message", ValidationErrorType.RequiredField));

			} else if (message.length() > this.maxCommentLength) {

				validationException = new ValidationException(new ValidationError("TooLongComment"));

			} else {
				comment.setMessage(message);
				comment.setUpdated(new Timestamp(System.currentTimeMillis()));
				comment.setEditor(user);

				log.info("User " + user + " from updating comment " + comment);

				this.commentDAO.update(comment);

				BlogPost blogPost = this.blogPostDAO.getPost(comment.getPostID(), false, blogID);

				res.sendRedirect(this.getModuleURI(req) + "/post/" + URLEncoder.encode(blogPost.getAlias(), "UTF-8") + "#comment" + comment.getCommentID());

				return null;
			}
		}

		Document doc = this.createDocument(req, uriParser, user);
		Element updateCommentElement = doc.createElement("UpdateComment");
		doc.getFirstChild().appendChild(updateCommentElement);

		updateCommentElement.appendChild(comment.toXML(doc));

		if (validationException != null) {
			updateCommentElement.appendChild(validationException.toXML(doc));
			updateCommentElement.appendChild(RequestUtils.getRequestParameters(req, doc));
		}

		return new SimpleForegroundModuleResponse(doc, this.updateCommentBreadcrumbText, this.getDefaultBreadcrumb(), new Breadcrumb(this.updateCommentBreadcrumbText, this.getFullAlias() + "/updatecomment/" + comment.getCommentID()));
	}

	@WebPublic(alias = "deletecomment")
	public SimpleForegroundModuleResponse deleteComment(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, IOException, AccessDeniedException, SQLException {

		Comment comment;

		if (uriParser.size() != 3 || !NumberUtils.isInt(uriParser.get(2)) || (comment = this.commentDAO.get(NumberUtils.toInt(uriParser.get(2)))) == null) {

			throw new URINotFoundException(uriParser);

		}

		this.checkCommentAcces(user, comment);

		log.info("User " + user + " from deleteing comment " + comment);

		this.commentDAO.delete(comment);

		BlogPost blogPost = this.blogPostDAO.getPost(comment.getPostID(), false, blogID);

		res.sendRedirect(this.getModuleURI(req) + "/post/" + URLEncoder.encode(blogPost.getAlias(), "UTF-8"));

		return null;
	}

	private void checkCommentAcces(User user, Comment comment) throws AccessDeniedException {

		if (user == null || (!user.equals(comment.getPoster()) && !this.checkBooleanAdminAccess(user))) {

			throw new AccessDeniedException("Edit access to comment " + comment + " denied");
		}
	}

	@WebPublic(alias = "tag")
	public SimpleForegroundModuleResponse showTag(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, IOException, AccessDeniedException, SQLException {

		if (uriParser.size() != 3) {
			throw new URINotFoundException(uriParser);
		}

		List<BlogPost> posts = this.blogPostDAO.getPosts(uriParser.get(2), blogID);

		if (posts == null) {
			throw new URINotFoundException(uriParser);
		}

		log.info("User " + user + " requested tag " + uriParser.get(2) + " in blog " + this.blogID);

		Document doc = this.createDocument(req, uriParser, user);

		Element tagPostsElement = doc.createElement("ShowTagPosts");
		doc.getDocumentElement().appendChild(tagPostsElement);

		if (user != null) {
			tagPostsElement.appendChild(doc.createElement("isLoggedIn"));
		}

		if (allowAnonymousComments) {
			tagPostsElement.appendChild(doc.createElement("allowAnonymousComments"));
		}
		
		for (BlogPost blogPost : posts) {
			this.setAbsoluteFileUrls(blogPost, uriParser, true);
			tagPostsElement.appendChild(blogPost.toXML(doc));
		}

		return new SimpleForegroundModuleResponse(doc, this.tagBreadcrumbText + uriParser.get(2), this.getDefaultBreadcrumb(), new Breadcrumb(this.tagBreadcrumbText + uriParser.get(2), this.getFullAlias() + "/tag/" + uriParser.get(2)));
	}

	@WebPublic(alias = "tags")
	public SimpleForegroundModuleResponse showTags(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, IOException, AccessDeniedException, SQLException {

		log.info("User " + user + " listing tags in blog " + this.blogID);

		List<TagEntry> tags = this.blogPostDAO.getTagEntries(blogID);

		Document doc = this.createDocument(req, uriParser, user);

		Element tagsElement = doc.createElement("ShowTags");
		doc.getDocumentElement().appendChild(tagsElement);

		XMLUtils.append(doc, tagsElement, "tags", tags);

		return new SimpleForegroundModuleResponse(doc, this.tagsBreadcrumbText, this.getDefaultBreadcrumb(), new Breadcrumb(this.tagsBreadcrumbText, this.getFullAlias() + "/tags"));

	}

	@WebPublic(alias = "archive")
	public SimpleForegroundModuleResponse showArchive(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, IOException, AccessDeniedException, SQLException {

		if (uriParser.size() == 2) {

			log.info("User " + user + " requested arhive in blog " + this.blogID);

			List<ArchiveEntry> archiveEntries = this.blogPostDAO.getArchiveEntries(blogID);

			Document doc = this.createDocument(req, uriParser, user);

			Element archiveElement = doc.createElement("ShowArchive");
			doc.getDocumentElement().appendChild(archiveElement);

			XMLUtils.append(doc, archiveElement, archiveEntries);

			return new SimpleForegroundModuleResponse(doc, this.archiveBreadcrumbText, this.getDefaultBreadcrumb(), new Breadcrumb(this.archiveBreadcrumbText, this.getFullAlias() + "/archive"));

		}
		//		else if (uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2))) {
		//
		//			Integer year = NumberUtils.toInt(uriParser.get(2));
		//
		//			List<BlogPost> posts = this.blogPostDAO.getPosts(year);
		//
		//			Document doc = this.createDocument(req, uriParser, user);
		//
		//			Element archiveYearElement = doc.createElement("ShowArchiveYear");
		//			doc.getDocumentElement().appendChild(archiveYearElement);
		//
		//			archiveYearElement.appendChild(XMLUtils.createElement("year", year.toString(), doc));
		//
		//			if (posts != null) {
		//
		//				Element postsElement = doc.createElement("BlogPosts");
		//				archiveYearElement.appendChild(postsElement);
		//
		//				for (BlogPost blogPost : posts) {
		//					this.setAbsoluteFileUrls(blogPost, uriParser, true);
		//					postsElement.appendChild(blogPost.toXML(doc));
		//				}
		//			}
		//
		//			return new ModuleResponse(doc, this.getDefaultBreadcrumb());
		//
		//		}
		else if (uriParser.size() == 4 && NumberUtils.isInt(uriParser.get(2)) && EnumUtils.isEnum(Month.class, uriParser.get(3).toUpperCase())) {

			Integer year = NumberUtils.toInt(uriParser.get(2));
			Month month = Month.valueOf(uriParser.get(3).toUpperCase());

			log.info("User " + user + " requested archive for " + month.toString().toLowerCase() + " " + year + " in blog " + this.blogID);

			List<BlogPost> posts = this.blogPostDAO.getPosts(year, month, blogID);

			Document doc = this.createDocument(req, uriParser, user);

			Element archiveYearElement = doc.createElement("ShowArchiveMonth");
			doc.getDocumentElement().appendChild(archiveYearElement);

			archiveYearElement.appendChild(XMLUtils.createElement("year", year.toString(), doc));
			archiveYearElement.appendChild(XMLUtils.createElement("month", month.toString().toLowerCase(), doc));

			if (posts != null) {

				Element postsElement = doc.createElement("blogPosts");
				archiveYearElement.appendChild(postsElement);

				for (BlogPost blogPost : posts) {
					this.setAbsoluteFileUrls(blogPost, uriParser, true);
					postsElement.appendChild(blogPost.toXML(doc));
				}
			}

			return new SimpleForegroundModuleResponse(doc, this.archiveBreadcrumbText + month.toString().toLowerCase() + " " + year, this.getDefaultBreadcrumb(), new Breadcrumb(this.archiveBreadcrumbText + month.toString().toLowerCase() + " " + year, this.getFullAlias() + "/archive/" + year + "/" + month.toString().toLowerCase()));
		}

		throw new URINotFoundException(uriParser);
	}

	protected Document createDocument(HttpServletRequest req, URIParser uriParser, User user) {

		Document doc = XMLUtils.createDomDocument();
		Element document = doc.createElement("Document");
		document.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser));
		document.appendChild(this.moduleDescriptor.toXML(doc));

		if (checkBooleanAdminAccess(user)) {
			document.appendChild(doc.createElement("isAdmin"));
		}

		if (enableRSSFeed) {
			document.appendChild(doc.createElement("RSSEnabled"));
		}

		if (allowComments) {
			document.appendChild(doc.createElement("allowComments"));
		}
		
		document.appendChild(XMLUtils.createElement("displayFullName", this.displayFullName, doc));

		doc.appendChild(document);
		return doc;

	}

	private Boolean checkBooleanAdminAccess(User user) {

		return AccessUtils.checkAccess(user, this);
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

	public void reloadMenuItems() {

		this.sectionInterface.getMenuCache().moduleUpdated(this.moduleDescriptor, this);
	}

	@Override
	public List<? extends BundleDescriptor> getVisibleBundles() {

		return getVisibleBundles(showArchiveBundle, showTagsBundle);
	}
	
	public List<? extends BundleDescriptor> getVisibleBundles(boolean showArchiveBundle, boolean showTagsBundle) {
		
		if (showArchiveBundle || showTagsBundle) {

			ArrayList<SimpleBundleDescriptor> bundles = new ArrayList<SimpleBundleDescriptor>();

			if (showArchiveBundle) {

				try {
					List<ArchiveEntry> archiveEntries = this.blogPostDAO.getLatestArchiveEntries(this.archiveBundleLimit, blogID);

					if (archiveEntries != null) {

						SimpleBundleDescriptor archiveBundleDescriptor = new SimpleBundleDescriptor();

						archiveBundleDescriptor.setAccess(this.moduleDescriptor);
						archiveBundleDescriptor.setName(this.archiveBundleName);
						archiveBundleDescriptor.setDescription(this.archiveBundleDescription);
						archiveBundleDescriptor.setItemType(MenuItemType.TITLE);
						archiveBundleDescriptor.setUrlType(URLType.RELATIVE_FROM_CONTEXTPATH);
						archiveBundleDescriptor.setUrl(this.getFullAlias() + "/archive");
						archiveBundleDescriptor.setUniqueID("archiveBundle");

						ArrayList<SimpleMenuItemDescriptor> archiveMenuItems = new ArrayList<SimpleMenuItemDescriptor>();

						for (ArchiveEntry archiveEntry : archiveEntries) {

							SimpleMenuItemDescriptor menuItemDescriptor = new SimpleMenuItemDescriptor();

							menuItemDescriptor.setAccess(this.moduleDescriptor);
							menuItemDescriptor.setName(this.getMonth(archiveEntry.getMonth()) + " " + archiveEntry.getYear() + " (" + archiveEntry.getPostCount() + ")");
							menuItemDescriptor.setItemType(MenuItemType.MENUITEM);
							menuItemDescriptor.setUrlType(URLType.RELATIVE_FROM_CONTEXTPATH);
							menuItemDescriptor.setUrl(this.getFullAlias() + "/archive/" + archiveEntry.getYear() + "/" + archiveEntry.getMonth().toString().toLowerCase());

							menuItemDescriptor.setDescription(this.archiveBundleMenuitemDescription + archiveEntry.getMonth() + " " + archiveEntry.getYear());

							archiveMenuItems.add(menuItemDescriptor);
						}

						archiveBundleDescriptor.setMenuItemDescriptors(archiveMenuItems);
						bundles.add(archiveBundleDescriptor);
					}
				} catch (SQLException e) {
					log.error("Unable to get archive entries", e);
				}
			}

			if (showTagsBundle) {

				try {
					List<TagEntry> tagEntries = this.blogPostDAO.getTagEntries(this.tagBundleLimit, blogID);

					if (tagEntries != null) {

						SimpleBundleDescriptor tagBundleDescriptor = new SimpleBundleDescriptor();

						tagBundleDescriptor.setAccess(this.moduleDescriptor);
						tagBundleDescriptor.setName(this.tagBundleName);
						tagBundleDescriptor.setDescription(this.tagBundleDescription);
						tagBundleDescriptor.setItemType(MenuItemType.TITLE);
						tagBundleDescriptor.setUrlType(URLType.RELATIVE_FROM_CONTEXTPATH);
						tagBundleDescriptor.setUrl(this.getFullAlias() + "/tags");
						tagBundleDescriptor.setUniqueID("tagBundle");

						ArrayList<SimpleMenuItemDescriptor> tagMenuItems = new ArrayList<SimpleMenuItemDescriptor>();

						for (TagEntry tagEntry : tagEntries) {

							SimpleMenuItemDescriptor menuItemDescriptor = new SimpleMenuItemDescriptor();

							menuItemDescriptor.setAccess(this.moduleDescriptor);
							menuItemDescriptor.setName(tagEntry.getTagName() + " (" + tagEntry.getPostCount() + ")");
							menuItemDescriptor.setItemType(MenuItemType.MENUITEM);
							menuItemDescriptor.setUrlType(URLType.RELATIVE_FROM_CONTEXTPATH);
							menuItemDescriptor.setUrl(this.getFullAlias() + "/tag/" + tagEntry.getTagName());

							menuItemDescriptor.setDescription(this.tagBundleMenuitemDescription + tagEntry.getTagName());

							tagMenuItems.add(menuItemDescriptor);
						}

						tagBundleDescriptor.setMenuItemDescriptors(tagMenuItems);

						bundles.add(tagBundleDescriptor);
					}
				} catch (SQLException e) {
					log.error("Unable to get tag entries", e);
				}
			}

			return bundles;

		} else {
			return null;
		}
		
	}

	private String getMonth(Month month) {

		String monthName = null;

		if (month == Month.JANUARY) {

			monthName = january;

		} else if (month == Month.FEBRUARY) {

			monthName = february;

		} else if (month == Month.MARCH) {

			monthName = march;

		} else if (month == Month.APRIL) {

			monthName = april;

		} else if (month == Month.MAY) {

			monthName = may;

		} else if (month == Month.JUNE) {

			monthName = june;

		} else if (month == Month.JULY) {

			monthName = july;

		} else if (month == Month.AUGUST) {

			monthName = august;

		} else if (month == Month.SEPTEMBER) {

			monthName = september;

		} else if (month == Month.OCTOBER) {

			monthName = october;

		} else if (month == Month.NOVEMBER) {

			monthName = november;

		} else if (month == Month.DECEMBER) {

			monthName = december;
		}

		if (monthName == null) {

			monthName = month.toString();
		}

		return StringUtils.toSentenceCase(monthName);
	}

	protected void checkAdminAccess(User user) throws AccessDeniedException {

		if (!AccessUtils.checkAccess(user, this)) {
			throw new AccessDeniedException("Blog module admin access denied");
		}
	}

	@WebPublic
	public SimpleForegroundModuleResponse connector(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws TransformerFactoryConfigurationError, TransformerException, IOException, AccessDeniedException {

		this.checkAdminAccess(user);

		this.connector.processRequest(req, res, uriParser, user, moduleDescriptor);

		return null;
	}

	@WebPublic
	public SimpleForegroundModuleResponse file(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		this.checkAdminAccess(user);

		this.connector.processFileRequest(req, res, user, uriParser, moduleDescriptor, sectionInterface, 2, null);

		return null;
	}

	//For backwards compability
	@WebPublic
	public SimpleForegroundModuleResponse feed(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return getRSS(req, res, user, uriParser);
	}

	@WebPublic(alias="rss")
	public SimpleForegroundModuleResponse getRSS(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		if (enableRSSFeed && cachedFeedXML != null) {

			log.info("User " + user + " requesting RSS feed from adress " + req.getRemoteAddr());

			HTTPUtils.sendReponse(this.cachedFeedXML, "application/xml", res);
			return null;
		}

		throw new URINotFoundException(uriParser);
	}

	@Override
	public List<? extends MenuItemDescriptor> getVisibleMenuItems() {

		if (this.showMainMenuitem) {
			return super.getVisibleMenuItems();
		}

		return null;
	}

	@Override
	public Breadcrumb getDefaultBreadcrumb() {

		if (this.showMainMenuitem) {
			return super.getDefaultBreadcrumb();
		}

		return null;
	}

	public List<BlogPost> getLatestPosts(int limit, String blogID) throws SQLException {

		return this.blogPostDAO.getLatestPosts(limit, blogID);
	}

	public int getPostCount(String blogID) throws SQLException {

		return this.blogPostDAO.getPostCount(blogID);
	}

	public int getPostsPerPage() {

		return this.blogPostsPerPage;
	}

	//RSSChannel methods

	@Override
	public Date getPubDate() {

		return new Date();
	}

	@Override
	public Date getLastBuildDate() {

		//In an optimal word this would be calculated from DB or post cache...
		return new Date();
	}

	@Override
	public Integer getTtl() {

		return feedTTL;
	}

	@Override
	public String getLanguage() {

		return feedLanguage;
	}

	@Override
	public String getCopyright() {

		return feedCopyright;
	}

	@Override
	public String getWebmaster() {

		return feedWebmaster;
	}

	@Override
	public String getManagingEditor() {

		return feedManagingEditor;
	}

	@Override
	public Integer getItemsPerChannel() {

		return feedItemsPerChannel;
	}

	@Override
	public String getLink() {

		return feedLink;
	}

	@Override
	public String getDescription() {

		return feedDescription;
	}

	@Override
	public String getTitle() {

		return feedTitle;
	}

	@Override
	public Collection<String> getCategories() {

		try{
			return this.blogPostDAO.getTags(blogID);

		}catch(SQLException e){

			throw new RuntimeException(e);
		}
	}

	@Override
	public String getFeedLink() {

		return feedLink + "/feed";
	}
	
	public ForegroundModuleDescriptor getModuleDescriptor() {
		
		return moduleDescriptor;
	}
}
