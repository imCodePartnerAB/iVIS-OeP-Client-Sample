/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.imagegallery.daos;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.daos.BaseDAO;
import se.unlogic.hierarchy.foregroundmodules.imagegallery.beans.Gallery;
import se.unlogic.hierarchy.foregroundmodules.imagegallery.populators.GalleryPopulator;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.dao.querys.ArrayListQuery;
import se.unlogic.standardutils.dao.querys.IntegerKeyCollector;
import se.unlogic.standardutils.dao.querys.ObjectQuery;
import se.unlogic.standardutils.dao.querys.UpdateQuery;
import se.unlogic.standardutils.populators.IntegerPopulator;

public class GalleryDao extends BaseDAO {

	private static GalleryPopulator Populator = new GalleryPopulator();

	public GalleryDao(DataSource ds) {
		super(ds);
	}

	public Gallery get(Integer galleryID) throws SQLException {

		Connection connection = null;

		try {

			connection = this.dataSource.getConnection();
			String queryString = null;

			queryString = "SELECT * FROM galleries WHERE galleryID = ?";

			ObjectQuery<Gallery> query = new ObjectQuery<Gallery>(connection, false, queryString, Populator);

			query.setInt(1, galleryID);

			Gallery gallery = query.executeQuery();

			if (gallery != null) {
				this.getGalleryGroups(gallery, connection);
				this.getGalleryUsers(gallery, connection);
				this.getGalleryUploadGroups(gallery, connection);
				this.getGalleryUploadUsers(gallery, connection);
			}

			return gallery;

		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
			}
		}

	}

	public Gallery get(String alias) throws SQLException {

		Connection connection = null;

		try {
			connection = this.dataSource.getConnection();

			String queryString = "SELECT * FROM galleries WHERE alias = ?";

			ObjectQuery<Gallery> query = new ObjectQuery<Gallery>(connection, false, queryString, Populator);

			query.setString(1, alias);

			Gallery gallery = query.executeQuery();

			if (gallery != null) {
				this.getGalleryGroups(gallery, connection);
				this.getGalleryUsers(gallery, connection);
				this.getGalleryUploadGroups(gallery, connection);
				this.getGalleryUploadUsers(gallery, connection);
			}

			return gallery;

		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
			}
		}
	}

	public ArrayList<Gallery> getAll() throws SQLException {

		Connection connection = null;

		try {
			connection = this.dataSource.getConnection();
			ArrayListQuery<Gallery> query = new ArrayListQuery<Gallery>(connection, false, "SELECT * FROM galleries ORDER BY name", Populator);

			ArrayList<Gallery> galleryList = query.executeQuery();

			if (galleryList != null) {
				for (Gallery gallery : galleryList) {
					this.getGalleryGroups(gallery, connection);
					this.getGalleryUsers(gallery, connection);
					this.getGalleryUploadGroups(gallery, connection);
					this.getGalleryUploadUsers(gallery, connection);
				}
			}

			return galleryList;

		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
			}
		}

	}

	public Integer add(Gallery gallery) throws SQLException {

		Integer generatedID = null;

		TransactionHandler transactionHandler = null;

		try {

			transactionHandler = new TransactionHandler(dataSource);
			UpdateQuery query = transactionHandler.getUpdateQuery("INSERT INTO galleries VALUES (null,?,?,?,?,?,?,?)");

			query.setString(1, gallery.getName());
			query.setString(2, gallery.getDescription());
			query.setString(3, gallery.getAlias());
			query.setString(4, gallery.getUrl());
			query.setBoolean(5, gallery.allowsAnonymousAccess());
			query.setBoolean(6, gallery.allowsUserAccess());
			query.setBoolean(7, gallery.allowsAdminAccess());
			
	        IntegerKeyCollector keyCollector = new IntegerKeyCollector();
	        
	        query.executeUpdate(keyCollector);
			
			generatedID = keyCollector.getKeyValue();

			gallery.setGalleryID(generatedID);

			if (gallery.getAllowedUserIDs() != null && !gallery.getAllowedUserIDs().isEmpty()) {
				this.setUsers(transactionHandler, gallery);
			}

			if (gallery.getAllowedGroupIDs() != null && !gallery.getAllowedGroupIDs().isEmpty()) {
				this.setGroups(transactionHandler, gallery);
			}

			if (gallery.getAllowedUploadUserIDs() != null && !gallery.getAllowedUploadUserIDs().isEmpty()) {
				this.setUploadUsers(transactionHandler, gallery);
			}

			if (gallery.getAllowedUploadGroupIDs() != null && !gallery.getAllowedUploadGroupIDs().isEmpty()) {
				this.setUploadGroups(transactionHandler, gallery);
			}

			transactionHandler.commit();
		} finally {
			if (transactionHandler != null && !transactionHandler.isClosed()) {
				transactionHandler.abort();
			}
		}

		return generatedID;
	}

	public void delete(Gallery gallery) throws SQLException {

		UpdateQuery query = new UpdateQuery(this.dataSource.getConnection(), true, "DELETE FROM galleries WHERE galleryID = ?");

		query.setInt(1, gallery.getGalleryID());

		query.executeUpdate();
	}

	public void update(Gallery gallery) throws SQLException {

		TransactionHandler transactionHandler = null;

		try {

			transactionHandler = new TransactionHandler(dataSource);
			UpdateQuery query = transactionHandler.getUpdateQuery("UPDATE galleries SET name = ?, description = ?, url = ?, alias = ?, anonymousAccess = ?, userAccess = ?, adminAccess = ?  WHERE galleryID = ?");

			query.setString(1, gallery.getName());
			query.setString(2, gallery.getDescription());
			query.setString(3, gallery.getUrl());
			query.setString(4, gallery.getAlias());
			query.setBoolean(5, gallery.allowsAnonymousAccess());
			query.setBoolean(6, gallery.allowsUserAccess());
			query.setBoolean(7, gallery.allowsAdminAccess());
			query.setInt(8, gallery.getGalleryID());

			query.executeUpdate();

			this.deleteUsers(transactionHandler, gallery);

			if (gallery.getAllowedUserIDs() != null && !gallery.getAllowedUserIDs().isEmpty()) {
				this.setUsers(transactionHandler, gallery);
			}

			this.deleteGroups(transactionHandler, gallery);

			if (gallery.getAllowedGroupIDs() != null && !gallery.getAllowedGroupIDs().isEmpty()) {
				this.setGroups(transactionHandler, gallery);
			}

			this.deleteUploadUsers(transactionHandler, gallery);

			if (gallery.getAllowedUploadUserIDs() != null && !gallery.getAllowedUploadUserIDs().isEmpty()) {
				this.setUploadUsers(transactionHandler, gallery);
			}

			this.deleteUploadGroups(transactionHandler, gallery);

			if (gallery.getAllowedUploadGroupIDs() != null && !gallery.getAllowedUploadGroupIDs().isEmpty()) {
				this.setUploadGroups(transactionHandler, gallery);
			}

			transactionHandler.commit();
		} finally {
			if (transactionHandler != null && !transactionHandler.isClosed()) {
				transactionHandler.abort();
			}
		}

	}

	private void deleteGroups(TransactionHandler transactionHandler, Gallery gallery) throws SQLException {

		UpdateQuery query = transactionHandler.getUpdateQuery("DELETE FROM gallerygroups WHERE galleryID = ?");

		query.setInt(1, gallery.getGalleryID());

		query.executeUpdate();
	}

	private void deleteUploadGroups(TransactionHandler transactionHandler, Gallery gallery) throws SQLException {

		UpdateQuery query = transactionHandler.getUpdateQuery("DELETE FROM galleryuploadgroups WHERE galleryID = ?");

		query.setInt(1, gallery.getGalleryID());

		query.executeUpdate();
	}

	private void setGroups(TransactionHandler transactionHandler, Gallery gallery) throws SQLException {

		for (Integer groupID : gallery.getAllowedGroupIDs()) {
			UpdateQuery query = transactionHandler.getUpdateQuery("INSERT INTO gallerygroups VALUES (?,?)");

			query.setInt(1, gallery.getGalleryID());
			query.setInt(2, groupID);

			query.executeUpdate();
		}

	}

	private void setUploadGroups(TransactionHandler transactionHandler, Gallery gallery) throws SQLException {

		for (Integer groupID : gallery.getAllowedUploadGroupIDs()) {
			UpdateQuery query = transactionHandler.getUpdateQuery("INSERT INTO galleryuploadgroups VALUES (?,?)");

			query.setInt(1, gallery.getGalleryID());
			query.setInt(2, groupID);

			query.executeUpdate();
		}

	}

	private void deleteUsers(TransactionHandler transactionHandler, Gallery gallery) throws SQLException {

		UpdateQuery query = transactionHandler.getUpdateQuery("DELETE FROM galleryusers WHERE galleryID = ?");

		query.setInt(1, gallery.getGalleryID());

		query.executeUpdate();
	}

	private void deleteUploadUsers(TransactionHandler transactionHandler, Gallery gallery) throws SQLException {

		UpdateQuery query = transactionHandler.getUpdateQuery("DELETE FROM galleryuploadusers WHERE galleryID = ?");

		query.setInt(1, gallery.getGalleryID());

		query.executeUpdate();
	}

	private void setUsers(TransactionHandler transactionHandler, Gallery gallery) throws SQLException {

		for (Integer userID : gallery.getAllowedUserIDs()) {
			UpdateQuery query = transactionHandler.getUpdateQuery("INSERT INTO galleryusers VALUES (?,?)");

			query.setInt(1, gallery.getGalleryID());
			query.setInt(2, userID);

			query.executeUpdate();
		}

	}

	private void setUploadUsers(TransactionHandler transactionHandler, Gallery gallery) throws SQLException {

		for (Integer userID : gallery.getAllowedUploadUserIDs()) {
			UpdateQuery query = transactionHandler.getUpdateQuery("INSERT INTO galleryuploadusers VALUES (?,?)");

			query.setInt(1, gallery.getGalleryID());
			query.setInt(2, userID);

			query.executeUpdate();
		}

	}

	private void getGalleryGroups(Gallery gallery, Connection connection) throws SQLException {

		ArrayListQuery<Integer> query = new ArrayListQuery<Integer>(connection, false, "SELECT groupID from gallerygroups WHERE galleryID = ?", IntegerPopulator.getPopulator());

		query.setInt(1, gallery.getGalleryID());

		gallery.setAllowedGroupIDs(query.executeQuery());
	}

	private void getGalleryUploadGroups(Gallery gallery, Connection connection) throws SQLException {

		ArrayListQuery<Integer> query = new ArrayListQuery<Integer>(connection, false, "SELECT groupID from galleryuploadgroups WHERE galleryID = ?", IntegerPopulator.getPopulator());

		query.setInt(1, gallery.getGalleryID());

		gallery.setAllowedUploadGroupIDs(query.executeQuery());
	}

	private void getGalleryUsers(Gallery gallery, Connection connection) throws SQLException {

		ArrayListQuery<Integer> query = new ArrayListQuery<Integer>(connection, false, "SELECT userID from galleryusers WHERE galleryID = ?", IntegerPopulator.getPopulator());

		query.setInt(1, gallery.getGalleryID());

		gallery.setAllowedUserIDs(query.executeQuery());
	}

	private void getGalleryUploadUsers(Gallery gallery, Connection connection) throws SQLException {

		ArrayListQuery<Integer> query = new ArrayListQuery<Integer>(connection, false, "SELECT userID from galleryuploadusers WHERE galleryID = ?", IntegerPopulator.getPopulator());

		query.setInt(1, gallery.getGalleryID());

		gallery.setAllowedUploadUserIDs(query.executeQuery());
	}
}
