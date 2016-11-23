/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.registration;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.exceptions.UnableToDeleteUserException;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.standardutils.dao.AnnotatedDAO;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.QueryOperators;
import se.unlogic.standardutils.dao.QueryParameterFactory;
import se.unlogic.standardutils.dao.SimpleAnnotatedDAOFactory;
import se.unlogic.standardutils.dao.querys.UpdateQuery;
import se.unlogic.standardutils.db.DBUtils;
import se.unlogic.standardutils.db.tableversionhandler.TableUpgradeException;
import se.unlogic.standardutils.db.tableversionhandler.TableVersionHandler;
import se.unlogic.standardutils.db.tableversionhandler.UpgradeResult;
import se.unlogic.standardutils.db.tableversionhandler.XMLDBScriptProvider;
import se.unlogic.standardutils.string.BeanTagSourceFactory;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.string.TagReplacer;
import se.unlogic.standardutils.time.MillisecondTimeUnits;

public abstract class AnnotatedConfirmationRegistrationModule<UserType extends User> extends BaseRegistrationModule<UserType,AnnotatedConfirmation> {

	protected String availableConfirmationTags;

	protected BeanTagSourceFactory<AnnotatedConfirmation> confirmationTagSourceFactory = new BeanTagSourceFactory<AnnotatedConfirmation>(AnnotatedConfirmation.class);

	private AnnotatedDAO<AnnotatedConfirmation> confirmationDAO;

	private QueryParameterFactory<AnnotatedConfirmation, Timestamp> addedQueryParameterFactory;
	private QueryParameterFactory<AnnotatedConfirmation, String> linkIDQueryParameterFactory;

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		confirmationTagSourceFactory.addAllFields("$confirmation.");

		availableConfirmationTags = userTagSourceFactory.getAvailableTags() + ", " + confirmationTagSourceFactory.getAvailableTags() + ", " + CONFIRMATION_LINK + ", " + CONFIRMATION_TIMEOUT;
	}

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		createDBTables(dataSource);

		SimpleAnnotatedDAOFactory annotatedDAOFactory = new SimpleAnnotatedDAOFactory(dataSource);

		this.confirmationDAO = annotatedDAOFactory.getDAO(AnnotatedConfirmation.class);

		this.addedQueryParameterFactory = confirmationDAO.getParamFactory("added", Timestamp.class);
		this.linkIDQueryParameterFactory = confirmationDAO.getParamFactory("linkID", String.class);
	}

	protected void createDBTables(DataSource dataSource) throws SQLException, IOException, TableUpgradeException, SAXException, ParserConfigurationException {

		if(TableVersionHandler.getTableGroupVersion(dataSource, AnnotatedConfirmationRegistrationModule.class.getName()) == null){

			if (!DBUtils.tableExists(dataSource, "registrationcofirmations")) {

				log.info("Creating registrationcofirmations table in datasource " + dataSource);

				String sql = StringUtils.readStreamAsString(AnnotatedConfirmationRegistrationModule.class.getResourceAsStream("dbscripts/RegistrationCofirmationsTable.sql"));

				new UpdateQuery(dataSource.getConnection(), true, sql).executeUpdate();

			}else if(DBUtils.getTableColumnCount(dataSource, "registrationcofirmations") == 3)	{

				log.info("Applying upgrade script 1 to registrationcofirmations table in datasource " + dataSource);

				String sql = StringUtils.readStreamAsString(AnnotatedConfirmationRegistrationModule.class.getResourceAsStream("dbscripts/UpgradeScript-1.sql"));

				new UpdateQuery(dataSource.getConnection(), true, sql).executeUpdate();
			}
		}

		//Automatic table version handling
		UpgradeResult upgradeResult = TableVersionHandler.upgradeDBTables(dataSource, AnnotatedConfirmationRegistrationModule.class.getName(), new XMLDBScriptProvider(AnnotatedConfirmationRegistrationModule.class.getResourceAsStream("dbscripts/DB script.xml")));

		if(upgradeResult.isUpgrade()){

			log.info(upgradeResult.toString());
		}
	}

	protected abstract BeanTagSourceFactory<UserType> createUserBeanTagSourceFactory();

	@Override
	protected void addConfirmation(AnnotatedConfirmation confirmation) throws SQLException {

		confirmationDAO.add(confirmation);
	}

	@Override
	protected void deleteConfirmation(AnnotatedConfirmation confirmation) throws SQLException {

		this.confirmationDAO.delete(confirmation);
	}

	@Override
	protected void deleteOldConfirmations() {

		HighLevelQuery<AnnotatedConfirmation> query = new HighLevelQuery<AnnotatedConfirmation>();

		query.addParameter(this.addedQueryParameterFactory.getParameter(new Timestamp(System.currentTimeMillis() - (this.confirmationTimeout * MillisecondTimeUnits.DAY)),QueryOperators.SMALLER_THAN));

		try {
			List<AnnotatedConfirmation> oldConfirmations = this.confirmationDAO.getAll(query);

			if(oldConfirmations != null){

				for(AnnotatedConfirmation confirmation : oldConfirmations){

					log.info("Deleting timed out confirmation " + confirmation);

					this.confirmationDAO.delete(confirmation);

					UserType user = this.findUserByID(confirmation.getUserID());

					if(user != null && !user.isEnabled()){

						log.info("Deleting disabled user " + user);

						try {
							this.deleteUser(user);

						} catch (UnableToDeleteUserException e) {

							log.error("Error deleting disabled user " + user,e);
						}
					}
				}
			}

		} catch (SQLException e) {

			log.error("Error deleting old confirmations",e);

		}
	}

	@Override
	protected AnnotatedConfirmation getConfirmation(String id) throws SQLException {

		HighLevelQuery<AnnotatedConfirmation> query = new HighLevelQuery<AnnotatedConfirmation>();

		query.addParameter(linkIDQueryParameterFactory.getParameter(id));

		return this.confirmationDAO.get(query);
	}

	@Override
	protected TagReplacer getConfirmationTagReplacer(UserType newUser, AnnotatedConfirmation confirmation, HttpServletRequest req) {

		TagReplacer tagReplacer = new TagReplacer();

		tagReplacer.addTagSource(userTagSourceFactory.getTagSource(newUser));
		tagReplacer.addTagSource(confirmationTagSourceFactory.getTagSource(confirmation));

		return tagReplacer;
	}

	@Override
	protected String getConfirmationTags() {

		return availableConfirmationTags;
	}

	@Override
	protected AnnotatedConfirmation createConfirmation(Integer userID, String linkID, String remoteHost) {

		return new AnnotatedConfirmation(userID, linkID, remoteHost);
	}
}
