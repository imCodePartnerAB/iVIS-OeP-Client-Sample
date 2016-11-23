package se.unlogic.standardutils.rss;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.standardutils.date.PooledSimpleDateFormat;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.xml.XMLUtils;

public class RSSGenerator {

	public static final PooledSimpleDateFormat DATE_FORMATTER = new PooledSimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.US, TimeZone.getTimeZone("GMT"));

	protected RSSChannel channel;
	
	protected int rssItemDescriptionMaxLength = 247;

	public RSSGenerator(RSSChannel channel) {

		super();
		setDescriptor(channel);
	}
	
	public RSSGenerator(RSSChannel channel, int rssItemDescriptionMaxLength) {

		super();
		setDescriptor(channel);
		this.rssItemDescriptionMaxLength = rssItemDescriptionMaxLength;
	}

	public RSSChannel getDescriptor() {

		return channel;
	}

	public void setDescriptor(RSSChannel channel) {

		if(channel == null){

			throw new NullPointerException("RssDescriptor cannot be null");
		}

		this.channel = channel;
	}

	public Document getXmlRss(List<? extends RSSItem> items){

		Document doc = XMLUtils.createDomDocument();
		Element rssElement = doc.createElement("rss");
		rssElement.setAttribute("version", "2.0");
		rssElement.setAttribute("xmlns:atom", "http://www.w3.org/2005/Atom");
		doc.appendChild(rssElement);
		Element channelElement = doc.createElement("channel");
		rssElement.appendChild(channelElement);

		Element atomLinkElement = doc.createElement("atom:link");
		atomLinkElement.setAttribute("href", channel.getFeedLink());
		atomLinkElement.setAttribute("rel", "self");
		atomLinkElement.setAttribute("type", "application/rss+xml");
		channelElement.appendChild(atomLinkElement);

		XMLUtils.appendNewElement(doc, channelElement, "title", this.channel.getTitle());
		XMLUtils.appendNewElement(doc, channelElement, "link", this.channel.getLink());
		XMLUtils.appendNewElement(doc, channelElement, "description", this.channel.getDescription());
		XMLUtils.appendNewElement(doc, channelElement, "ttl", this.channel.getTtl());
		XMLUtils.appendNewElement(doc, channelElement, "webMaster", this.channel.getWebmaster());
		XMLUtils.appendNewElement(doc, channelElement, "managingEditor", this.channel.getManagingEditor());
		XMLUtils.appendNewElement(doc, channelElement, "copyright", this.channel.getCopyright());

		if(channel.getLastBuildDate() != null){
			XMLUtils.appendNewElement(doc, channelElement, "lastBuildDate", DATE_FORMATTER.format(channel.getLastBuildDate()));
		}

		if(channel.getPubDate() != null){
			XMLUtils.appendNewElement(doc, channelElement, "pubDate", DATE_FORMATTER.format(channel.getPubDate()));
		}

		XMLUtils.appendNewElement(doc, channelElement, "language", channel.getLanguage());

		Collection<String> categories = channel.getCategories();

		if (categories != null) {

			for (String category : categories) {

				XMLUtils.appendNewElement(doc, channelElement, "category", category);
			}
		}

		if(items != null){

			for(RSSItem item : items){

				Element itemElement = doc.createElement("item");

				// Required
				XMLUtils.appendNewElement(doc, itemElement, "title", item.getTitle());
				XMLUtils.appendNewElement(doc, itemElement, "link", item.getLink());
				
				String description = item.getDescription();
				
				if(rssItemDescriptionMaxLength > 0) {
					description = StringUtils.substring(description, rssItemDescriptionMaxLength, "...");
				}

				XMLUtils.appendNewElement(doc, itemElement, "description", description);
				
				// Optional
				if(item.getPubDate() != null){
					XMLUtils.appendNewElement(doc, itemElement, "pubDate", DATE_FORMATTER.format(item.getPubDate()));
				}

				XMLUtils.appendNewElement(doc, itemElement, "guid", item.getLink());
				XMLUtils.appendNewElement(doc, itemElement, "comments", item.getCommentsLink());

				if (item.getAuthor() != null) {
					XMLUtils.appendNewElement(doc, itemElement, "author", item.getAuthor());
				}

				if (item.getCategories() != null) {

					for (String category : item.getCategories()) {
						XMLUtils.appendNewElement(doc, itemElement, "category", category);
					}
				}

				channelElement.appendChild(itemElement);
			}
		}

		return doc;
	}

	public String getStringRss(List<? extends RSSItem> items, String encoding) throws TransformerFactoryConfigurationError, TransformerException{

		return XMLUtils.toString(this.getXmlRss(items), "ISO-8859-1", true);
	}
}
