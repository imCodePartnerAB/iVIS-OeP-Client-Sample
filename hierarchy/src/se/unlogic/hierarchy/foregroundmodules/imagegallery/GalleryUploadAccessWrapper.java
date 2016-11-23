/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.imagegallery;

import java.util.Collection;

import se.unlogic.hierarchy.core.interfaces.AccessInterface;
import se.unlogic.hierarchy.foregroundmodules.imagegallery.beans.Gallery;

public class GalleryUploadAccessWrapper implements AccessInterface {

	private final Gallery gallery;

	public GalleryUploadAccessWrapper(Gallery gallery) {
		super();
		this.gallery = gallery;
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
		return gallery.getAllowedUploadGroupIDs();
	}

	@Override
	public Collection<Integer> getAllowedUserIDs() {
		return gallery.getAllowedUploadUserIDs();
	}

}
