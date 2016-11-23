/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.blog.beans;

import java.sql.Timestamp;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.standardutils.annotations.WebPopulate;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.date.DateUtils;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.xml.XMLUtils;

public class Comment {

	@DAOManaged
	private Integer commentID;

	@DAOManaged
	private Timestamp added;

	@DAOManaged
	private Timestamp updated;

	@DAOManaged
	@WebPopulate(required=true,maxLength=16777216)
	private String message;

	@DAOManaged(columnName="posterID")
	private User poster;

	@DAOManaged(columnName="editorID")
	private User editor;

	@DAOManaged
	@WebPopulate(required=true,maxLength=255)
	private String posterName;

	@DAOManaged
	@WebPopulate(required=true,maxLength=255,populatorID="email")
	private String posterEmail;

	@DAOManaged
	@WebPopulate(maxLength=255,populatorID="url")
	private String posterWebsite;

	@DAOManaged
	private Integer postID;

	public Integer getCommentID() {
		return commentID;
	}

	public void setCommentID(Integer commentID) {
		this.commentID = commentID;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String content) {
		this.message = content;
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

	public String getPosterName() {
		return posterName;
	}

	public void setPosterName(String posterName) {
		this.posterName = posterName;
	}

	public String getPosterEmail() {
		return posterEmail;
	}

	public void setPosterEmail(String posterEmail) {
		this.posterEmail = posterEmail;
	}

	public String getPosterWebsite() {
		return posterWebsite;
	}

	public void setPosterWebsite(String posterWebsite) {
		this.posterWebsite = posterWebsite;
	}

	public Integer getPostID() {
		return postID;
	}

	public void setPostID(Integer blogPostID) {
		this.postID = blogPostID;
	}

	public Element toXML(Document doc) {

		Element commentElement = doc.createElement("Comment");

		commentElement.appendChild(XMLUtils.createElement("commentID", this.commentID + "", doc));
		commentElement.appendChild(XMLUtils.createElement("added", DateUtils.DATE_TIME_FORMATTER.format(this.added), doc));

		if (this.updated != null) {
			commentElement.appendChild(XMLUtils.createElement("updated", DateUtils.DATE_TIME_FORMATTER.format(this.updated), doc));
		}

		commentElement.appendChild(XMLUtils.createCDATAElement("message", message, doc));

		if (this.poster != null) {

			Element posterElement = doc.createElement("poster");
			posterElement.appendChild(poster.toXML(doc));

			commentElement.appendChild(posterElement);
		}

		if (this.editor != null) {
			Element editorElement = doc.createElement("editor");
			editorElement.appendChild(editor.toXML(doc));

			commentElement.appendChild(editorElement);
		}

		if (this.posterName != null) {
			commentElement.appendChild(XMLUtils.createCDATAElement("posterName", this.posterName, doc));
		}

		if (this.posterEmail != null) {
			commentElement.appendChild(XMLUtils.createCDATAElement("posterEmail", this.posterEmail, doc));
		}

		if (this.posterWebsite != null) {
			commentElement.appendChild(XMLUtils.createCDATAElement("posterWebsite", this.posterWebsite, doc));
		}

		commentElement.appendChild(XMLUtils.createElement("postID", this.postID.toString(), doc));

		return commentElement;
	}

	@Override
	public String toString() {

		return StringUtils.substring(message, 30, "...") + " (ID: " + this.commentID + ")";
	}
}
