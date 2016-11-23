/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.emailutils.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public class StopableEmailHandler implements EmailHandler {

	private static final EmailSenderComparator COMPARATOR = new EmailSenderComparator();

	private final ArrayList<EmailSender> emailSenders = new ArrayList<EmailSender>();

	private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private final ReadLock readLock = readWriteLock.readLock();
	private final WriteLock writeLock = readWriteLock.writeLock();
	private boolean stopped = false;

	/* (non-Javadoc)
	 * @see se.unlogic.utils.email.EmailHandler#addSender(se.unlogic.utils.email.EmailSender)
	 */
	public boolean addSender(EmailSender emailSender) {

		if (stopped) {

			throw new IllegalStateException("EmailHandler has been shutdown, no new email senders can be added!");

		} else if (emailSender == null) {
			throw new NullPointerException("EmailSender cannot be null!");
		}

		try {
			writeLock.lock();

			if (!this.emailSenders.contains(emailSender)) {

				this.emailSenders.add(emailSender);

				Collections.sort(this.emailSenders, COMPARATOR);

				return true;
			}

			return false;

		} finally {
			writeLock.unlock();
		}
	}

	/* (non-Javadoc)
	 * @see se.unlogic.utils.email.EmailHandler#removeSender(se.unlogic.utils.email.EmailSender)
	 */
	public boolean removeSender(EmailSender emailSender) {

		if (stopped) {

			throw new IllegalStateException("EmailHandler has been shutdown!");

		} else if (emailSender == null) {

			throw new NullPointerException("emailSender cannot be null!");
		}

		try {
			writeLock.lock();

			if (this.emailSenders.contains(emailSender)) {

				this.emailSenders.remove(emailSender);

				return true;
			}

			return false;

		} finally {
			writeLock.unlock();
		}
	}

	public void removeSenders() {

		try {
			writeLock.lock();

			for (EmailSender emailSender : emailSenders) {

				this.emailSenders.remove(emailSender);
			}

		} finally {
			writeLock.unlock();
		}

	}

	public synchronized void stop() {

		try {
			writeLock.lock();

			if (!stopped) {

				this.stopped = true;
			}

		} finally {
			writeLock.unlock();
		}
	}

	public synchronized void start() {

		try {
			writeLock.lock();

			if (stopped) {

				this.stopped = false;
			}

		} finally {
			writeLock.unlock();
		}
	}

	public int getSenderCount() {

		try {
			readLock.lock();

			return this.emailSenders.size();

		} finally {
			readLock.unlock();
		}

	}

	/* (non-Javadoc)
	 * @see se.unlogic.utils.email.EmailHandler#send(se.unlogic.utils.email.Email)
	 */
	public void send(Email email) throws NoEmailSendersFoundException, UnableToProcessEmailException {

		if (stopped) {

			throw new IllegalStateException("EmailHandler has been shutdown!");
		}

		try {
			readLock.lock();

			if (this.emailSenders.isEmpty()) {
				throw new NoEmailSendersFoundException();
			}

			for (EmailSender emailSender : emailSenders) {

				if (emailSender.send(email)) {
					return;
				}
			}

			throw new UnableToProcessEmailException();

		} finally {
			readLock.unlock();
		}
	}

	public boolean hasSenders() {

		try {
			readLock.lock();

			return !this.emailSenders.isEmpty();

		} finally {
			readLock.unlock();
		}
	}
}
