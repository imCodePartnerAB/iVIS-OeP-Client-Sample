package se.unlogic.hierarchy.backgroundmodules.blog;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.backgroundmodules.AnnotatedBackgroundModule;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleBackgroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.utils.ModuleUtils;
import se.unlogic.hierarchy.foregroundmodules.blog.BlogModule;
import se.unlogic.hierarchy.foregroundmodules.blog.beans.BlogPost;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.validation.PositiveStringIntegerValidator;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.URIParser;

public class LatestBlogPostsModule extends AnnotatedBackgroundModule {

	protected WeakReference<BlogModule> blogModuleReference;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Nr of posts", description = "The number of blog posts to show", required = true, formatValidator = PositiveStringIntegerValidator.class)
	protected int nrOfPosts = 3;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Blog id", description = "The id of the blog", required = true)
	protected String blogID = "default";

	@Override
	public void init(BackgroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		if(this.blogModuleReference == null || this.blogModuleReference.get() == null) {
			this.blogModuleReference = new WeakReference<BlogModule>(this.getBlogModule());
		}
	}

	@Override
	public void update(BackgroundModuleDescriptor moduleDescriptor, DataSource dataSource) throws Exception {

		super.update(moduleDescriptor, dataSource);

		if (this.blogModuleReference == null || this.blogModuleReference.get() == null) {
			this.blogModuleReference = new WeakReference<BlogModule>(this.getBlogModule());
		}
	}

	@Override
	public BackgroundModuleResponse processBackgroundRequest(HttpServletRequest req, User user, URIParser uriParser) throws Exception {

		Document doc = this.createDocument(req, uriParser, user);
		Element blogPostsElement = doc.createElement("BlogPosts");
		doc.getFirstChild().appendChild(blogPostsElement);

		BlogModule blogModule = null;

		if (this.blogModuleReference == null || (blogModule = this.blogModuleReference.get()) == null) {

			if((blogModule = this.getBlogModule()) == null) {

				log.debug("No blog module found, ignoring request");
				return null;

			} else {

				this.blogModuleReference = new WeakReference<BlogModule>(blogModule);// Cache weak reference
			}
		}

		int blogPostCount = blogModule.getPostCount(this.blogID);
		int postsPerPage = blogModule.getPostsPerPage();

		List<BlogPost> blogPosts = blogModule.getLatestPosts(this.nrOfPosts > blogPostCount ? blogPostCount : this.nrOfPosts, this.blogID);
		int page = 1;
		int ticks = 0;

		if (!CollectionUtils.isEmpty(blogPosts)) {

			blogPostsElement.appendChild(XMLUtils.createElement("blogURL", req.getContextPath() + blogModule.getFullAlias(), doc));

			for (BlogPost blogPost : blogPosts) {

				if (++ticks > postsPerPage) {
					++page;
					ticks = 1;
				}
				Element blogPostElement = blogPost.toXML(doc);
				blogPostElement.appendChild(XMLUtils.createElement("URL", req.getContextPath() + blogModule.getFullAlias() + (page > 1 ? "/page/" + page : ""), doc));
				blogPostsElement.appendChild(blogPostElement);
			}
		}

		return new SimpleBackgroundModuleResponse(doc);
	}

	public Document createDocument(HttpServletRequest req, URIParser uriParser, User user) {

		Document doc = XMLUtils.createDomDocument();
		Element document = doc.createElement("Document");
		document.appendChild(this.moduleDescriptor.toXML(doc));
		document.appendChild(XMLUtils.createElement("contextpath", req.getContextPath(), doc));
		doc.appendChild(document);
		return doc;
	}

	private BlogModule getBlogModule() {

		Map<ForegroundModuleDescriptor,BlogModule> blogModules = new HashMap<ForegroundModuleDescriptor, BlogModule>();

		ModuleUtils.findForegroundModules(BlogModule.class, true, true, this.systemInterface.getRootSection(), blogModules);
		for(Entry<ForegroundModuleDescriptor, BlogModule> entry : blogModules.entrySet()) {
			if(this.blogID.equals(entry.getKey().getMutableSettingHandler().getString("blogID"))) {
				return entry.getValue();
			}
		}
		return null;
	}

}
