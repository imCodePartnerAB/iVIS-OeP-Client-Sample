/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.imagegallery.beans;

import java.io.Serializable;
import java.sql.Blob;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import se.unlogic.standardutils.xml.XMLUtils;

public class Picture implements Serializable {

	private static final long serialVersionUID = -9183080212551664330L;
	private Integer pictureID;
	private String filename;
	private Blob smallThumb;
	private Blob mediumThumb;
	private Integer galleryID;

	public Picture(){
		super();
	}


	public Integer getPictureID() {
		return pictureID;
	}

	public void setPictureID(Integer pictureID) {
		this.pictureID = pictureID;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Blob getSmallThumb() {
		return smallThumb;
	}

	public void setSmallThumb(Blob smallThumb) {
		this.smallThumb = smallThumb;
	}

	public Blob getMediumThumb() {
		return mediumThumb;
	}

	public void setMediumThumb(Blob mediumThumb) {
		this.mediumThumb = mediumThumb;
	}

	public Integer getGalleryID() {
		return galleryID;
	}

	public void setGalleryID(Integer galleryID) {
		this.galleryID = galleryID;
	}


	public Node toXML(Document doc) {

		Element imageElement = doc.createElement("image");

		if(this.pictureID != null){
			imageElement.appendChild(XMLUtils.createElement("pictureID", pictureID.toString(), doc));
		}

		if(this.filename != null){
			imageElement.appendChild(XMLUtils.createElement("filename", filename.toString(), doc));
		}

		if(this.galleryID != null){
			imageElement.appendChild(XMLUtils.createElement("galleryID", galleryID.toString(), doc));
		}

		return imageElement;
	}


	@Override
	public String toString() {
		return this.filename + "(pictureID: " + pictureID + ")";
	}

}
