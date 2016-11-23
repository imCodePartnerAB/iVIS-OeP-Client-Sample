/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.mailsenders.persisting;

import java.sql.SQLException;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import se.unlogic.emailutils.framework.EmailConverter;
import se.unlogic.hierarchy.foregroundmodules.mailsenders.direct.EmailCounter;
import se.unlogic.hierarchy.foregroundmodules.mailsenders.persisting.daos.MailDAO;

public class EmailJob implements Runnable {

	private static Logger log = Logger.getLogger(EmailJob.class);

	private final QueuedEmail email;
	private final Session session;
	private final int maxResendCount;
	private final int warningResendCount;
	private final MailDAO mailDAO;
	private final EmailCounter emailCounter;

	public EmailJob(QueuedEmail email, Session session, int maxResendCount, int warningResendCount,	MailDAO mailDAO, EmailCounter emailCounter) {
		this.email = email;
		this.session = session;
		this.maxResendCount = maxResendCount;
		this.warningResendCount = warningResendCount;
		this.mailDAO = mailDAO;
		this.emailCounter = emailCounter;
	}

	@Override
	public void run() {

		if(this.checkResendCount(email)){
			try {

				MimeMessage message = EmailConverter.convert(email, session);

				log.info("Sending email " + email);

				Transport.send(message);

				log.debug("Sent mail " + email);

				this.emailCounter.incrementMailsSent();

				try {
					this.mailDAO.delete(email);
				} catch (SQLException e) {
					log.error("Unable to delete email " + email + " after sending",e);
				}

			} catch (Throwable e) {

				log.warn("Error sending email " + email, e);

				email.setResendCount(email.getResendCount() + 1);

				if(this.checkResendCount(email)){

					try {
						this.mailDAO.updateAndRelease(email);
					} catch (SQLException e1) {
						log.error("Error updating resend count of email " + email,e);
					}
				}
			}
		}
	}

	private boolean checkResendCount(QueuedEmail email) {

		if(email.getResendCount() == warningResendCount) {
			
			log.error("Unable to send email " + email + " after " + email.getResendCount() + " retries");
		}
		
		if(email.getResendCount() >= maxResendCount){
			
			//TODO add option to send warning here
			log.info("Email " + email + " has been resent " + email.getResendCount() + " time(s), deleting...");

			try {
				this.mailDAO.delete(email);
			} catch (SQLException e) {
				log.error("Unable to delete email " + email,e);
			}

			return false;
		}

		return true;
	}
}
