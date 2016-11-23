/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.imagegallery.daos;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.daos.BaseDAO;
import se.unlogic.hierarchy.foregroundmodules.imagegallery.beans.Gallery;
import se.unlogic.hierarchy.foregroundmodules.imagegallery.beans.Picture;
import se.unlogic.hierarchy.foregroundmodules.imagegallery.populators.PicturePopulator;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.dao.querys.ArrayListQuery;
import se.unlogic.standardutils.dao.querys.ObjectQuery;
import se.unlogic.standardutils.dao.querys.UpdateQuery;
import se.unlogic.standardutils.populators.IntegerPopulator;

public class PictureDao extends BaseDAO {

	private static PicturePopulator Populator = new PicturePopulator();

	public PictureDao(DataSource ds) {
		super(ds);
	}

	public ArrayList<Picture> findByGalleryID(Integer galleryID, boolean smallThumbs, boolean mediumThumbs) throws SQLException {

		ArrayListQuery<Picture> query = null;

		if (smallThumbs && mediumThumbs) {
			query = new ArrayListQuery<Picture>(this.dataSource.getConnection(), true, "SELECT * FROM pictures WHERE galleryID = ? ORDER BY name", Populator);
		} else if (smallThumbs && !mediumThumbs) {
			query = new ArrayListQuery<Picture>(this.dataSource.getConnection(), true, "SELECT pictureID, filename, smallThumb, galleryID FROM pictures WHERE galleryID = ? ORDER BY name", Populator);
		} else if (mediumThumbs && !smallThumbs) {
			query = new ArrayListQuery<Picture>(this.dataSource.getConnection(), true, "SELECT pictureID, filename, mediumThumb, galleryID FROM pictures WHERE galleryID = ? ORDER BY name", Populator);
		} else {
			query = new ArrayListQuery<Picture>(this.dataSource.getConnection(), true, "SELECT pictureID, filename, galleryID FROM pictures WHERE galleryID = ? ORDER BY name", Populator);
		}
		query.setInt(1, galleryID);

		return query.executeQuery();
	}

	public Picture get(Integer pictureID, boolean smallThumb, boolean mediumThumb) throws SQLException {

		String queryString = null;

		if (smallThumb && mediumThumb) {
			queryString = "SELECT * FROM pictures WHERE pictureID = ?";
		} else if (smallThumb && !mediumThumb) {
			queryString = "SELECT pictureID, filename, smallThumb, galleryID FROM pictures WHERE pictureID = ?";
		} else if (mediumThumb && !smallThumb) {
			queryString = "SELECT pictureID, filename, mediumThumb, galleryID FROM pictures WHERE pictureID = ?";
		} else {
			queryString = "SELECT pictureID, filename, galleryID FROM pictures WHERE pictureID = ?";
		}

		ObjectQuery<Picture> query = new ObjectQuery<Picture>(this.dataSource.getConnection(), true, queryString, Populator);

		query.setInt(1, pictureID);

		return query.executeQuery();
	}

	public Integer getPictureIDByFilenameAndGallery(String filename, Gallery gallery) throws SQLException {

		String queryString = "SELECT pictureID FROM pictures WHERE filename = ? AND galleryID = ?";

		ObjectQuery<Integer> query = new ObjectQuery<Integer>(this.dataSource.getConnection(), true, queryString, IntegerPopulator.getPopulator());

		query.setString(1, filename);
		query.setInt(2, gallery.getGalleryID());

		return query.executeQuery();
	}

	public Picture getByFilename(String filename, Integer galleryID, boolean smallThumb, boolean mediumThumb) throws SQLException {

		String queryString = null;

		if (smallThumb && mediumThumb) {
			queryString = "SELECT * FROM pictures WHERE filename = ? AND galleryID = ?";
		} else if (smallThumb && !mediumThumb) {
			queryString = "SELECT pictureID, filename, smallThumb, galleryID FROM pictures WHERE filename = ? AND galleryID = ?";
		} else if (mediumThumb && !smallThumb) {
			queryString = "SELECT pictureID, filename, mediumThumb, galleryID FROM pictures WHERE filename = ? AND galleryID = ?";
		} else {
			queryString = "SELECT pictureID, filename, galleryID FROM pictures WHERE filename = ? AND galleryID = ?";
		}

		ObjectQuery<Picture> query = new ObjectQuery<Picture>(this.dataSource.getConnection(), true, queryString, Populator);

		query.setString(1, filename);
		query.setInt(2, galleryID);

		return query.executeQuery();
	}

	public void set(Picture picture) throws SQLException {

		// TODO update instead...

		TransactionHandler transactionHandler = new TransactionHandler(this.dataSource);

		try {

			ObjectQuery<Integer> pictureQuery = transactionHandler.getObjectQuery("SELECT pictureID from pictures WHERE galleryID = ? AND filename = ?", IntegerPopulator.getPopulator());

			pictureQuery.setInt(1, picture.getGalleryID());
			pictureQuery.setString(2, picture.getFilename());

			Integer pictureID = pictureQuery.executeQuery();

			// Check if thumbnails already exists for this file
			if (pictureID != null) {

				// Update
				UpdateQuery updateQuery = transactionHandler.getUpdateQuery("UPDATE pictures SET smallThumb = ?, mediumThumb = ? WHERE pictureID = ?");

				updateQuery.setBlob(1, picture.getSmallThumb());
				updateQuery.setBlob(2, picture.getMediumThumb());
				updateQuery.setInt(3, pictureID);

				updateQuery.executeUpdate();

			} else {
				// Insert
				UpdateQuery insertQuery = transactionHandler.getUpdateQuery("INSERT INTO pictures VALUES (null,?,?,?,?)");

				insertQuery.setString(1, picture.getFilename());
				insertQuery.setBlob(2, picture.getSmallThumb());
				insertQuery.setBlob(3, picture.getMediumThumb());
				insertQuery.setInt(4, picture.getGalleryID());

				insertQuery.executeUpdate();
			}

			transactionHandler.commit();

		} finally {
			if (transactionHandler != null && !transactionHandler.isClosed()) {
				transactionHandler.abort();
			}
		}
	}

	public void delete(Picture picture) throws SQLException {

		UpdateQuery query = new UpdateQuery(this.dataSource.getConnection(), true, "DELETE FROM pictures WHERE pictureID = ?");

		query.setInt(1, picture.getPictureID());

		query.executeUpdate();
	}

	public void clear() throws SQLException {
		new UpdateQuery(this.dataSource.getConnection(), true, "DELETE FROM pictures").executeUpdate();
	}
}
