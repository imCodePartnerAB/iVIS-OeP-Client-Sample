/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.fileuploadutils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

public class SafeDiskFileItemFactory extends DiskFileItemFactory {

	protected ArrayList<FileItem> itemList = new ArrayList<FileItem>();

	public SafeDiskFileItemFactory() {

		super();
	}

	public SafeDiskFileItemFactory(int sizeThreshold, File repository) {

		super(sizeThreshold, repository);
	}

	@Override
	public FileItem createItem(String fieldName, String contentType, boolean isFormField, String fileName) {

		FileItem item = super.createItem(fieldName, contentType, isFormField, fileName);

		if(item != null && !item.isFormField()){
			this.itemList.add(item);
		}

		return item;

	}

	public void deleteFiles(){
		if(!this.itemList.isEmpty()){

			Iterator<FileItem> iterator = this.itemList.iterator();

			while(iterator.hasNext()){
				FileItem item = iterator.next();
				item.delete();
				iterator.remove();
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		this.deleteFiles();
		super.finalize();
	}
}
