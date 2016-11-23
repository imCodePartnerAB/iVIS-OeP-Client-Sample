/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.mailsenders.persisting.daos.mysql;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.sql.DataSource;
import javax.sql.rowset.serial.SerialBlob;

import se.unlogic.emailutils.framework.Attachment;
import se.unlogic.emailutils.framework.BlobAttachment;
import se.unlogic.emailutils.framework.Email;
import se.unlogic.hierarchy.core.daos.BaseDAO;
import se.unlogic.hierarchy.foregroundmodules.mailsenders.persisting.QueuedEmail;
import se.unlogic.hierarchy.foregroundmodules.mailsenders.persisting.daos.MailDAO;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.dao.querys.ArrayListQuery;
import se.unlogic.standardutils.dao.querys.ObjectQuery;
import se.unlogic.standardutils.dao.querys.UpdateQuery;
import se.unlogic.standardutils.populators.BlobPopulator;
import se.unlogic.standardutils.populators.IntegerPopulator;
import se.unlogic.standardutils.populators.StringPopulator;
import se.unlogic.standardutils.populators.UUIDPopulator;
import se.unlogic.standardutils.populators.annotated.AnnotatedResultSetPopulator;


public class MySQLMailDAO extends BaseDAO implements MailDAO {

	private static final AnnotatedResultSetPopulator<QueuedEmail> POPULATOR = new AnnotatedResultSetPopulator<QueuedEmail>(QueuedEmail.class, new UUIDPopulator());

	private static final String REPLYTO = "email_replyto";
	private static final String RECIPIENTS = "email_recipients";
	private static final String BCCRECIPIENTS = "email_bcc_recipients";
	private static final String CCRECIPIENTS = "email_cc_recipients";

	public MySQLMailDAO(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public void add(Email email) throws SQLException {

		TransactionHandler transactionHandler = null;

		try{
			transactionHandler = new TransactionHandler(dataSource);

			UpdateQuery query = transactionHandler.getUpdateQuery("INSERT INTO emails VALUES (?,null,?,?,?,?,?,?,null,null)");

			UUID emailID = UUID.randomUUID();

			query.setString(1, emailID.toString());
			query.setString(2, email.getSenderName());
			query.setString(3, email.getSenderAddress());
			query.setString(4, email.getCharset());
			query.setString(5, email.getMessageContentType());
			query.setString(6, email.getSubject());
			query.setString(7, email.getMessage());

			query.executeUpdate();

			if(email.getReplyTo() != null){

				for(String address : email.getReplyTo()){

					this.addToSubtable(REPLYTO,address, transactionHandler,emailID);
				}
			}

			if(email.getRecipients() != null){

				for(String address : email.getRecipients()){

					this.addToSubtable(RECIPIENTS,address, transactionHandler,emailID);
				}
			}

			if(email.getBccRecipients() != null){

				for(String address : email.getBccRecipients()){

					this.addToSubtable(BCCRECIPIENTS,address, transactionHandler,emailID);
				}
			}

			if(email.getCcRecipients() != null){

				for(String address : email.getCcRecipients()){

					this.addToSubtable(CCRECIPIENTS,address, transactionHandler,emailID);
				}
			}

			if(email.getAttachments() != null){

				for(Attachment attachment : email.getAttachments()){

					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

					try {
						attachment.getMimeBodyPart().writeTo(byteArrayOutputStream);

						this.addAttachment(new SerialBlob(byteArrayOutputStream.toByteArray()),emailID,transactionHandler);

					} catch (IOException e) {

						throw new RuntimeException(e);

					} catch (MessagingException e) {

						throw new RuntimeException(e);
					}
				}
			}

			transactionHandler.commit();

		}finally{
			TransactionHandler.autoClose(transactionHandler);
		}
	}

	private void addAttachment(SerialBlob blob, UUID emailID, TransactionHandler transactionHandler) throws SQLException {

		UpdateQuery query = transactionHandler.getUpdateQuery("INSERT INTO email_attachments VALUES (?,?,?)");

		query.setString(1, UUID.randomUUID().toString());
		query.setString(2, emailID.toString());
		query.setBlob(3, blob);

		query.executeUpdate();

	}

	private void addToSubtable(String tableName, String address, TransactionHandler transactionHandler, UUID emailID) throws SQLException {

		UpdateQuery query = transactionHandler.getUpdateQuery("INSERT INTO " + tableName + " VALUES (?,?,?)");

		query.setString(1, UUID.randomUUID().toString());
		query.setString(2, emailID.toString());
		query.setString(3, address);

		query.executeUpdate();
	}

	@Override
	public void delete(QueuedEmail email) throws SQLException {

		UpdateQuery query = new UpdateQuery(dataSource, "DELETE FROM emails WHERE emailID = ?");

		query.setString(1, email.getEmailID().toString());

		query.executeUpdate();
	}

	@Override
	public QueuedEmail get(long resendIntervall, int databaseID) throws SQLException {

		TransactionHandler transactionHandler = null;

		try{
			transactionHandler = new TransactionHandler(dataSource);

			ObjectQuery<QueuedEmail> emailQuery = transactionHandler.getObjectQuery("SELECT * FROM emails WHERE owner IS NULL AND (lastSent IS NULL OR lastSent < ?)", POPULATOR);

			emailQuery.setTimestamp(1, new Timestamp(System.currentTimeMillis() - resendIntervall));

			QueuedEmail email = emailQuery.executeQuery();

			if(email != null){

				UpdateQuery lockQuery = transactionHandler.getUpdateQuery("UPDATE emails SET owner = ? WHERE emailID = ?");

				lockQuery.setInt(1, databaseID);
				lockQuery.setString(2, email.getEmailID().toString());

				lockQuery.executeUpdate();

				email.setRecipients(this.getSubtable(RECIPIENTS, email.getEmailID(),transactionHandler));
				email.setCcRecipients(this.getSubtable(CCRECIPIENTS, email.getEmailID(),transactionHandler));
				email.setBccRecipients(this.getSubtable(BCCRECIPIENTS, email.getEmailID(),transactionHandler));
				email.setReplyTo(this.getSubtable(REPLYTO, email.getEmailID(),transactionHandler));
				email.setAttachments(this.getAttachments(email.getEmailID(),transactionHandler));

				transactionHandler.commit();

				return email;
			}else{
				transactionHandler.abort();
			}

		}finally{
			TransactionHandler.autoClose(transactionHandler);
		}

		return null;
	}

	private ArrayList<Attachment> getAttachments(UUID emailID, TransactionHandler transactionHandler) throws SQLException {

		ArrayListQuery<Blob> query = transactionHandler.getArrayListQuery("SELECT data FROM email_attachments WHERE emailID = ?", BlobPopulator.getPopulator());

		query.setString(1, emailID.toString());

		List<Blob> blobs = query.executeQuery();

		if(blobs != null){

			ArrayList<Attachment> attachments = new ArrayList<Attachment>(blobs.size());

			for(Blob blob : blobs){

				try {
					attachments.add(new BlobAttachment(blob));

				} catch (MessagingException e) {

					log.error("Unable to parse attachment for email with ID " + emailID, e);

				}catch(SQLException e){

					log.error("Unable to parse attachment for email with ID " + emailID, e);
				}
			}

			if(!attachments.isEmpty()){
				return attachments;
			}

		}

		return null;
	}

	private ArrayList<String> getSubtable(String tableName, UUID emailID, TransactionHandler transactionHandler) throws SQLException {

		ArrayListQuery<String> query = transactionHandler.getArrayListQuery("SELECT address FROM " + tableName + " WHERE emailID = ?", StringPopulator.getPopulator());

		query.setString(1, emailID.toString());

		return query.executeQuery();
	}

	@Override
	public long getMailCount() throws SQLException {

		return new ObjectQuery<Integer>(dataSource, "SELECT COUNT(emailID) FROM emails", IntegerPopulator.getPopulator()).executeQuery();
	}

	@Override
	public void updateAndRelease(QueuedEmail email) throws SQLException {

		UpdateQuery query = new UpdateQuery(dataSource, "UPDATE emails SET owner = NULL, resendCount = ?, lastSent = ? WHERE emailID = ?");

		query.setInt(1, email.getResendCount());
		query.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
		query.setString(3, email.getEmailID().toString());

		query.executeUpdate();
	}

	@Override
	public void releaseAll(int databaseID) throws SQLException {

		UpdateQuery query = new UpdateQuery(dataSource, "UPDATE emails SET owner = NULL WHERE owner = ?");

		query.setInt(1, databaseID);

		query.executeUpdate();
	}

}
