package se.unlogic.standardutils.rss;

import java.util.Collection;
import java.util.Date;

public class SimpleRSSItem implements RSSItem{

	private String title;
	private String link;
	private String description;
	private Date pubDate;
	private String guid;
	private String comments;
	private String author;
	private Collection<String> categories;


	public String getTitle() {

		return title;
	}

	public void setTitle(String title) {

		this.title = title;
	}


	public String getLink() {

		return link;
	}

	public void setLink(String link) {

		this.link = link;
	}


	public String getDescription() {

		return description;
	}

	public void setDescription(String description) {

		this.description = description;
	}


	public Date getPubDate() {

		return pubDate;
	}

	public void setPubDate(Date pubDate) {

		this.pubDate = pubDate;
	}


	public String getGuid() {

		return guid;
	}

	public void setGuid(String guid) {

		this.guid = guid;
	}


	public String getCommentsLink() {

		return comments;
	}

	public void setComments(String comments) {

		this.comments = comments;
	}


	public String getAuthor() {

		return author;
	}

	public void setAuthor(String author) {

		this.author = author;
	}


	public Collection<String> getCategories() {

		return categories;
	}

	public void setCategories(Collection<String> categories) {

		this.categories = categories;
	}

}
