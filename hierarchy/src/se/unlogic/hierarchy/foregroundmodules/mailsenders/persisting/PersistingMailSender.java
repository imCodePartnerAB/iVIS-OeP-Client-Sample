/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.mailsenders.persisting;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import se.unlogic.emailutils.framework.Email;
import se.unlogic.emailutils.framework.EmailSender;
import se.unlogic.emailutils.framework.SimpleAuthenticator;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.beans.SettingDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.hierarchy.foregroundmodules.mailsenders.direct.EmailCounter;
import se.unlogic.hierarchy.foregroundmodules.mailsenders.persisting.daos.MailDAO;
import se.unlogic.hierarchy.foregroundmodules.mailsenders.persisting.daos.MailDAOFactory;
import se.unlogic.standardutils.db.tableversionhandler.TableUpgradeException;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.time.MillisecondTimeUnits;
import se.unlogic.standardutils.validation.StringIntegerValidator;
import se.unlogic.standardutils.validation.StringLongValidator;
import se.unlogic.webutils.http.URIParser;

public class PersistingMailSender extends AnnotatedForegroundModule implements Runnable, EmailSender, EmailCounter {

	private static final String DEFAULT_DAO_FACTORY_CLASS = "se.unlogic.hierarchy.foregroundmodules.mailsenders.persisting.daos.mysql.MySQLMailDAOFactory";
	private static final ArrayList<SettingDescriptor> SETTINGDESCRIPTORS = new ArrayList<SettingDescriptor>();

	static {
		//Basic stuff
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("daoFactoryClass", "DAO Factory", "The class of the DAO factory to use", true, DEFAULT_DAO_FACTORY_CLASS, null));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("host", "Server", "The hostname of your SMTP mail server ex. \"smtp.myisp.com\"", true, "", null));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("port", "Port", "The port number of your SMTP mail server usally 25 (allowed values are 1 - 65535)", true, "25", new StringIntegerValidator(1, 65535)));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createCheckboxSetting("useAuth", "Use authentication", "Controls wheter or not username and password should be used when sending mails to the SMTP mail server", false));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("username", "Username", "The username to be used when authentication with the SMTP server", false, "", null));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("password", "Password", "The password to be used when authentication with the SMTP server", false, "", null));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("connectionTimeout", "Connection timeout", "The connection timeout in milliseconds when connecting to the SMTP mail server (default is 10000 ms)", true, "10000", new StringIntegerValidator(1, null)));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("socketTimeout", "Socket timeout", "The socket timeout in milliseconds when sending mails (default is 600000 ms, 10 minutes)", false, MillisecondTimeUnits.MINUTE * 10 + "", new StringIntegerValidator(1, null)));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("priority", "Priority", "Sets the priority of this e-mail sender compared to other e-mail senders (higher value means higher priority)", true, "0", new StringIntegerValidator(0, null)));

		//Thread pool stuff
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("poolSize", "Max pool size", "The maximum number of threads to allow in the pool", true, "10", new StringIntegerValidator(1, null)));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("maxQueueSize", "Queue size", "The size of the queue in RAM where emails are cached from DB waiting to be sent. The thread pool gets it jobs from this queue so it should always be bigger than the maximum number allowed threads in the threadpool (default is 50 emails)", true, "50", new StringIntegerValidator(1, null)));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("queueFullInterval", "Queue full Interval", "Controls many ms the thread that fetches e-mails from DB should wait when the queue in RAM is full (default is 3000 ms)", true, "3000", new StringLongValidator(0l, null)));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("noWorkInterval", "No work interval", "Controls many ms the thread that fetches e-mails from DB should wait when there are no jobs in the DB (default is 10000 ms)", true, "10000", new StringLongValidator(0l, null)));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("exceptionInterval", "DB error interval", "Controls many ms the thread that fetches e-mails from DB should wait when there is a problem reading from the databse (default is 60000 ms)", true, "60000", new StringLongValidator(0l, null)));

		//General stuff
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("maxExceptionCount", "DB error count", "Controls many errors reading from the DB that are allowed before the module stops sending mails and shuts down the thread pool", true, "5", new StringIntegerValidator(0, null)));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("maxResendCount", "Resend count", "Controls how many times e-mails are resent in case of failed delivery", true, "3", new StringIntegerValidator(0, null)));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("warningResendCount", "Warning resend count", "Controls after how many retries sms sender should send error log message", true, "2", new StringIntegerValidator(0, null)));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("resendInterval", "Resend interval", "Controls how many minutes to wait before resending e-mails in case of failed delivery", true, "30", new StringIntegerValidator(0, null)));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("shutdownTimeout", "Shutdown timeout", "How many ms to wait for the thread pool to finish on shutdown", true, "60000", new StringIntegerValidator(0, null)));
	}

	private String daoFactoryClass = "se.unlogic.hierarchy.foregroundmodules.mailsenders.persisting.daos.mysql.MySQLMailDAOFactory";

	private ThreadPoolExecutor threadPoolExecutor;
	private MailDAO mailDAO;
	private Thread workFetcherThread;

	private boolean started;
	protected Session session;

	protected long mailsSent;

	@ModuleSetting
	protected String host;

	@ModuleSetting
	protected int port = 25;

	@ModuleSetting
	protected boolean useAuth;

	@ModuleSetting
	protected String username;

	@ModuleSetting
	protected String password;

	@ModuleSetting
	protected int connectionTimeout = 10000;

	@ModuleSetting
	protected Integer socketTimeout = MillisecondTimeUnits.MINUTE * 10;

	@ModuleSetting
	protected int priority = 0;

	@ModuleSetting
	protected int poolSize = 10;

	@ModuleSetting
	protected int maxQueueSize = 50;

	//TODO add module setting for DB queue size alarm

	//Workfetcher configuration
	@ModuleSetting
	protected long queueFullInterval = 3000;

	@ModuleSetting
	protected long noWorkInterval = 10000;

	@ModuleSetting
	protected long exceptionInterval = 60000;

	@ModuleSetting
	protected int maxExceptionCount = 5;

	//General configuration
	private int exceptionCount = 0;

	@ModuleSetting
	protected int maxResendCount = 3;

	@ModuleSetting
	protected int warningResendCount = 2;

	@ModuleSetting
	protected int resendInterval = 30;

	@ModuleSetting
	protected long shutdownTimeout = 60000;

	@ModuleSetting
	protected Integer databaseID;

	private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private final ReadLock readLock = readWriteLock.readLock();
	private final WriteLock writeLock = readWriteLock.writeLock();

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		this.databaseID = moduleDescriptor.getModuleID();

		super.init(moduleDescriptor, sectionInterface, dataSource);

		this.setup();
	}

	@Override
	public void update(ForegroundModuleDescriptor moduleDescriptor, DataSource dataSource) throws Exception {

		try {
			writeLock.lock();

			this.daoFactoryClass = moduleDescriptor.getMutableSettingHandler().getString("daoFactoryClass");

			//Check if the datasource, daofactory or databaseID has changed
			if ((!this.databaseID.equals(moduleDescriptor.getMutableSettingHandler().getInt("databaseID")) || this.maxQueueSize != moduleDescriptor.getMutableSettingHandler().getInt("maxQueueSize") || dataSource != this.dataSource || (daoFactoryClass == null && !this.daoFactoryClass.equals(DEFAULT_DAO_FACTORY_CLASS)) || (daoFactoryClass != null && !daoFactoryClass.equals(this.daoFactoryClass))) && this.started) {
				this.shutDown();
			}

			super.update(moduleDescriptor, dataSource);

			this.setup();
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public void unload() throws Exception {

		try {
			writeLock.lock();

			if (this.started) {
				this.shutDown();
			}

			super.unload();

		} finally {
			writeLock.unlock();
		}
	}

	private void shutDown() {

		log.info("Shutting down worker fethcer thread and thread pool...");

		this.sectionInterface.getSystemInterface().getEmailHandler().removeSender(this);

		this.started = false;

		if (workFetcherThread.isAlive() && Thread.currentThread() != workFetcherThread) {
			try {
				this.workFetcherThread.interrupt();
				this.workFetcherThread.join();
			} catch (InterruptedException e) {
				log.error("Interrupted while waiting for work fetcher thread to stop");
			}
		}

		this.threadPoolExecutor.purge();
		this.threadPoolExecutor.shutdown();

		try {
			this.threadPoolExecutor.awaitTermination(shutdownTimeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			log.error("Error waiting for thread pool to shutdown", e);
		}

		try {
			this.mailDAO.releaseAll(this.databaseID);
		} catch (SQLException e) {

			log.error("Unable to release taken mails", e);
		}

		this.exceptionCount = 0;

		log.info("Work fether thread and thread pool stopped, all queued jobs released");
	}

	@SuppressWarnings("unchecked")
	protected void setup() throws TableUpgradeException, SAXException, ParserConfigurationException {

		if (StringUtils.isEmpty(host)) {

			if (started) {
				this.shutDown();
			}

			log.warn("Module " + this.moduleDescriptor + " not properly configured (no SMTP server host set), not accepting mail");

		} else {

			try {
				Properties props = new Properties();
				props.put("mail.smtp.host", this.host);
				props.put("mail.smtp.port", this.port);
				props.put("mail.smtp.connectiontimeout", this.connectionTimeout);

				if (socketTimeout != null) {
					props.put("mail.smtp.timeout", this.socketTimeout);
				}

				if (!useAuth) {
					this.session = Session.getInstance(props);
				} else {
					this.session = Session.getInstance(props, new SimpleAuthenticator(username, password));
				}

				if (started) {

					this.threadPoolExecutor.setCorePoolSize(poolSize);
					this.threadPoolExecutor.setMaximumPoolSize(poolSize);

				} else {

					Class<MailDAOFactory> daoFactoryClass = (Class<MailDAOFactory>) Class.forName(this.daoFactoryClass);

					MailDAOFactory daoFactory = daoFactoryClass.newInstance();

					daoFactory.init(dataSource);

					this.mailDAO = daoFactory.getMailDAO();

					try {
						this.mailDAO.releaseAll(this.databaseID);
					} catch (SQLException e) {

						log.error("Unable to release taken e-mails", e);
					}

					this.threadPoolExecutor = new ThreadPoolExecutor(poolSize, poolSize, 5000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(this.maxQueueSize));
					this.started = true;
					this.workFetcherThread = new Thread(this, "Work fetcher thread for module " + moduleDescriptor.toString());
					this.workFetcherThread.start();
				}

				this.sectionInterface.getSystemInterface().getEmailHandler().addSender(this);

				log.info("Module " + this.moduleDescriptor + " ready to process mail");

			} catch (ClassNotFoundException e) {

				log.error("Unable to create MailDAOFactory", e);

			} catch (InstantiationException e) {

				log.error("Unable to create MailDAOFactory", e);

			} catch (IllegalAccessException e) {

				log.error("Unable to create MailDAOFactory", e);

			} catch (SQLException e) {

				log.error("Unable to create MailDAOFactory", e);

			} catch (IOException e) {

				log.error("Unable to create MailDAOFactory", e);
			}
		}
	}

	@Override
	public List<SettingDescriptor> getSettings() {

		List<? extends SettingDescriptor> superSettings = super.getSettings();

		if (superSettings != null) {
			ArrayList<SettingDescriptor> combinedSettings = new ArrayList<SettingDescriptor>();

			combinedSettings.addAll(superSettings);
			combinedSettings.addAll(SETTINGDESCRIPTORS);
			combinedSettings.add(SettingDescriptor.createTextFieldSetting("databaseID", "Database ID", "The ID that this module uses to lock e-mails in the database (note don't change unless know what you're doing!)", true, "3", new StringIntegerValidator(1, null)));

			return combinedSettings;
		} else {
			return SETTINGDESCRIPTORS;
		}
	}

	@Override
	public boolean send(Email email) {

		try {
			readLock.lock();

			if (started) {
				try {
					log.info("Adding email " + email + " to DB");
					this.mailDAO.add(email);
					return true;

				} catch (SQLException e) {

					log.error("Unable to queue email " + email, e);

				} catch (RuntimeException e) {

					log.error("Unable to queue email " + email, e);
				}
			} else {

				log.warn("Module " + this.moduleDescriptor + " is not properly configured, refusing to send mail " + email);
			}

		} finally {
			readLock.unlock();
		}

		return false;
	}

	@Override
	public void run() {

		log.info("Work fetcher thread started");

		boolean wasFull = false;

		while (started) {

			int queueSize = this.threadPoolExecutor.getQueue().size();

			if (queueSize >= this.maxQueueSize) {

				wasFull = true;

				//Queue full sleep for while
				try {

					log.debug("Queue is full, sleeping " + this.queueFullInterval + " ms");
					Thread.sleep(this.queueFullInterval);

				} catch (InterruptedException e) {
					log.debug("Work fetcher thread interrupted while sleeping due to full queue");
				}

			} else {

				try {
					if (wasFull && this.threadPoolExecutor.getQueue().isEmpty()) {

						log.warn("Queue starvation, increase queue size, reduce thread pool size or lower queue full sleep Interval for optimum performance");
					}

					if (wasFull) {
						wasFull = false;
					}

					//Queue is NOT full check if there are any jobs in db
					//TODO get multiple e-mail here to speed things up!
					QueuedEmail email = this.mailDAO.get((long) this.resendInterval * (long) MillisecondTimeUnits.MINUTE, databaseID);

					if (email == null) {

						//No jobs in DB sleep for a while
						try {

							log.debug("No jobs found in DB, sleeping " + this.noWorkInterval + " ms");
							Thread.sleep(this.noWorkInterval);

						} catch (InterruptedException e) {

							log.debug("Work fetcher thread interrupted while sleeping due to no jobs in DB");
						}

					} else {

						log.debug("Putting email " + email + " on queue");

						try {
							this.threadPoolExecutor.execute(new EmailJob(email, session, this.maxResendCount, warningResendCount, mailDAO, this));

						} catch (RejectedExecutionException e) {

							log.warn("Error putting email " + email + " on queue.", e);

							try {
								this.mailDAO.updateAndRelease(email);

							} catch (Exception e2) {

								log.error("Error releasing email " + email, e2);
							}
						}
					}

				} catch (Throwable e) {

					log.error("Unable to get emails from database", e);

					this.exceptionCount++;

					if (exceptionCount >= maxExceptionCount) {

						log.error("Maximum number of allowed exceptions (" + this.maxExceptionCount + ") in work fethcer thread has been reached, killing worker thread and shutting down thread pool");

						this.shutDown();

					} else {
						//Error sleep for a while
						try {
							Thread.sleep(this.exceptionInterval);
						} catch (InterruptedException ex) {}
					}
				}
			}
		}

		log.info("Work fetcher thread stopping");
	}

	@Override
	public synchronized void incrementMailsSent() {

		this.mailsSent++;
	}

	@Override
	public SimpleForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		try {
			readLock.lock();

			StringBuilder stringBuilder = new StringBuilder();

			stringBuilder.append("<div class=\"contentitem\">");
			stringBuilder.append("<h1>" + this.moduleDescriptor.getName() + "</h1>");
			stringBuilder.append("<p>Mails sent: " + this.mailsSent + "</p>");

			if (started) {
				stringBuilder.append("<p>Current status: ready!</p>");
				stringBuilder.append("<p>Active threads: " + this.threadPoolExecutor.getActiveCount() + "</p>");
				//stringBuilder.append("<p>Max threads used: " + this.threadPoolExecutor.getLargestPoolSize() + "</p>");
				stringBuilder.append("<p>Pool size: " + this.threadPoolExecutor.getCorePoolSize() + "</p>");
				stringBuilder.append("<p>Queue size: " + this.threadPoolExecutor.getQueue().size() + "</p>");
				stringBuilder.append("<p>Mails in DB: " + this.mailDAO.getMailCount() + "</p>");
			} else {
				stringBuilder.append("<p>Current status: <b>not ready</b></p>");
			}

			stringBuilder.append("</div>");

			return new SimpleForegroundModuleResponse(stringBuilder.toString(), moduleDescriptor.getName(), this.getDefaultBreadcrumb());
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public int getPriority() {

		return this.priority;
	}
}
