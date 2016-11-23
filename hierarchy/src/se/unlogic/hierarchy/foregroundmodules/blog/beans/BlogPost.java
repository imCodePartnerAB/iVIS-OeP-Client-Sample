/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.blog.beans;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.standardutils.annotations.WebPopulate;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.date.DateUtils;
import se.unlogic.standardutils.rss.RSSItem;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.string.Stringyfier;
import se.unlogic.standardutils.xml.XMLUtils;

public class BlogPost implements RSSItem, Serializable {

	private static final long serialVersionUID = -534789322203347389L;

	//This tag is missing the first <p> tag because of the odd default formating rules used by the default in CKEditor...
	public static final String SPLIT_TAG = "split-post</p>";

	@DAOManaged
	private Integer postID;

	@WebPopulate(required = true, maxLength = 255)
	@DAOManaged
	private String alias;

	@DAOManaged
	private Timestamp added;

	@DAOManaged
	private Timestamp updated;

	@WebPopulate(required = true, maxLength = 255)
	@DAOManaged
	private String title;

	@WebPopulate(required = true, maxLength = 16777216)
	@DAOManaged
	private String message;

	@DAOManaged(columnName = "posterID", dontUpdateIfNull = true)
	private User poster;

	@DAOManaged(columnName = "editorID", dontUpdateIfNull = true)
	private User editor;

	@DAOManaged
	private int readCount;

	@DAOManaged
	private boolean split;

	private String link;
	private Stringyfier<User> userStringyfier;

	private List<Comment> comments;
	private Collection<String> tags;

	public Integer getPostID() {

		return postID;
	}

	public void setPostID(Integer postID) {

		this.postID = postID;
	}

	public Timestamp getAdded() {

		return added;
	}

	public void setAdded(Timestamp added) {

		this.added = added;
	}

	public Timestamp getUpdated() {

		return updated;
	}

	public void setUpdated(Timestamp updated) {

		this.updated = updated;
	}

	@Override
	public String getTitle() {

		return title;
	}

	public void setTitle(String title) {

		this.title = title;
	}

	public String getMessage() {

		return message;
	}

	public void setMessage(String text) {

		this.message = text;
	}

	public String getAlias() {

		return alias;
	}

	public void setAlias(String alias) {

		this.alias = alias;
	}

	public User getPoster() {

		return poster;
	}

	public void setPoster(User poster) {

		this.poster = poster;
	}

	public User getEditor() {

		return editor;
	}

	public void setEditor(User editor) {

		this.editor = editor;
	}

	public List<Comment> getComments() {

		return comments;
	}

	public void setComments(List<Comment> comments) {

		this.comments = comments;
	}

	public Collection<String> getTags() {

		return tags;
	}

	public void setTags(Collection<String> tags) {

		this.tags = tags;
	}

	public Element toXML(Document doc) {

		Element blogPostElement = doc.createElement("BlogPost");

		blogPostElement.appendChild(XMLUtils.createElement("postID", this.postID.toString(), doc));

		blogPostElement.appendChild(XMLUtils.createElement("alias", this.alias, doc));

		blogPostElement.appendChild(XMLUtils.createElement("added", DateUtils.DATE_TIME_FORMATTER.format(this.added), doc));

		if(this.updated != null){
			blogPostElement.appendChild(XMLUtils.createElement("updated", DateUtils.DATE_TIME_FORMATTER.format(this.updated), doc));
		}

		blogPostElement.appendChild(XMLUtils.createElement("title", this.title, doc));

		if(split){

			int splitTagIndex = message.indexOf(SPLIT_TAG);

			int lastOpenParagraphTag = this.message.substring(0, splitTagIndex).lastIndexOf("<p>");

			int splitIndex;

			if(lastOpenParagraphTag != 1){

				splitIndex = lastOpenParagraphTag;

			}else{

				splitIndex = splitTagIndex;
			}

			blogPostElement.appendChild(XMLUtils.createCDATAElement("textPart1", this.message.substring(0, splitIndex), doc));

			if(message.length() > (splitTagIndex + SPLIT_TAG.length())){

				blogPostElement.appendChild(XMLUtils.createCDATAElement("textPart2", this.message.substring(splitTagIndex + SPLIT_TAG.length()), doc));
			}

		}else{

			blogPostElement.appendChild(XMLUtils.createCDATAElement("textPart1", this.message, doc));
		}

		blogPostElement.appendChild(XMLUtils.createCDATAElement("message", this.message, doc));

		if(this.poster != null){

			Element posterElement = doc.createElement("poster");
			posterElement.appendChild(poster.toXML(doc));

			blogPostElement.appendChild(posterElement);
		}

		if(this.editor != null){
			Element editorElement = doc.createElement("editor");
			editorElement.appendChild(editor.toXML(doc));

			blogPostElement.appendChild(editorElement);
		}

		if(this.comments != null){

			Element commentsElement = doc.createElement("comments");
			blogPostElement.appendChild(commentsElement);

			for(Comment comment : this.comments){
				commentsElement.appendChild(comment.toXML(doc));
			}
		}

		if(tags != null){

			Element tagsElement = doc.createElement("tags");
			blogPostElement.appendChild(tagsElement);

			for(String tag : tags){
				tagsElement.appendChild(XMLUtils.createCDATAElement("tag", tag, doc));
			}
		}

		XMLUtils.appendNewElement(doc, blogPostElement, "readCount", this.readCount);

		return blogPostElement;
	}

	/**
	 * @return the readCount
	 */
	public int getReadCount() {

		return readCount;
	}

	/**
	 * @param readCount the readCount to set
	 */
	public void setReadCount(int readCount) {

		this.readCount = readCount;
	}

	/**
	 * @return the split
	 */
	public boolean isSplit() {

		return split;
	}

	/**
	 * @param split the split to set
	 */
	public void setSplit(boolean split) {

		this.split = split;
	}

	@Override
	public String toString() {

		return this.title + " (ID: " + this.postID + ")";
	}

	//RSSItem methods

	@Override
	public Collection<String> getCategories() {

		return getTags();
	}

	@Override
	public String getAuthor() {

		if(editor != null){

			return userStringyfier.format(editor);

		}else if(poster != null){

			return userStringyfier.format(poster);
		}

		return null;
	}

	@Override
	public String getGuid() {

		return link;
	}

	@Override
	public Date getPubDate() {

		return updated == null ? added : updated;
	}

	@Override
	public String getDescription() {

		String description;

		if(split){

			int splitTagIndex = this.message.indexOf(SPLIT_TAG);

			int lastOpenParagraphTag = this.message.substring(0, splitTagIndex).lastIndexOf("<p>");

			int splitIndex;

			if(lastOpenParagraphTag != 1){

				splitIndex = lastOpenParagraphTag;

			}else{

				splitIndex = splitTagIndex;
			}

			description = this.message.substring(0, splitIndex);

		}else{

			description = this.message;
		}

		description = StringUtils.removeHTMLTags(description);

		return description;
	}

	@Override
	public String getLink() {

		return link;
	}

	public void setLink(String blogURL) {

		this.link = blogURL + "/post/" + this.getAlias();
	}

	@Override
	public String getCommentsLink() {

		return link + "#comments";
	}

	public void setUserStringyfier(Stringyfier<User> userStringyfier) {

		this.userStringyfier = userStringyfier;
	}
}
