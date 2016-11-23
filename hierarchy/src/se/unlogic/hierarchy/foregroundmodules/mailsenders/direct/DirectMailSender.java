/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.mailsenders.direct;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import se.unlogic.emailutils.framework.Email;
import se.unlogic.emailutils.framework.EmailConverter;
import se.unlogic.emailutils.framework.EmailSender;
import se.unlogic.emailutils.framework.SimpleAuthenticator;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.beans.SettingDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.MutableSettingHandler;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.StringIntegerValidator;
import se.unlogic.webutils.http.URIParser;

public class DirectMailSender extends AnnotatedForegroundModule implements EmailSender, EmailCounter {

	private static final ArrayList<SettingDescriptor> SETTINGDESCRIPTORS = new ArrayList<SettingDescriptor>();

	static {
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("host", "Server", "The hostname of your SMTP mail server ex. \"smtp.myisp.com\"", true, "", null));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("port", "Port", "The port number of your SMTP mail server usally 25 (allowed values are 1 - 65535)", true, "25", new StringIntegerValidator(1,65535)));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createCheckboxSetting("secure", "Secure channel", "Use a secure connection", false));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createCheckboxSetting("startTLS", "TLS", "Start TLS", false));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createCheckboxSetting("useAuth", "Use authentication", "Controls wheter or not username and password should be used when sending mails to the SMTP mail server", false));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("username", "Username", "The username to be used when authentication with the SMTP server", false, "", null));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createPasswordFieldSetting("password", "Password", "The password to be used when authentication with the SMTP server", false, "", null));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("connectionTimeout", "Connection timeout", "The connection timeout in milliseconds when connecting to the SMTP mail server (default is 10000 ms)", true, "10000", new StringIntegerValidator(1,null)));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("socketTimeout", "Socket timeout", "The socket timeout in milliseconds when sending mails", false, "", new StringIntegerValidator(1,null)));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("priority", "Priority", "Sets the priority of this E-mail sender compared to other E-mail senders (higher value means higher priority)", true, "0", new StringIntegerValidator(0,null)));
	}

	protected long mailsSent;

	@ModuleSetting
	protected String host;

	@ModuleSetting
	protected Integer port = 25;

	@ModuleSetting
	protected Boolean useAuth;

	@ModuleSetting
	protected String username;

	@ModuleSetting
	protected String password;

	@ModuleSetting
	protected Integer connectionTimeout = 10000;

	@ModuleSetting
	protected Integer socketTimeout;

	@ModuleSetting
	protected int priority = 0;

	@ModuleSetting
	protected Boolean secure = false;

	@ModuleSetting
	protected Boolean startTLS = false;

	private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private final ReadLock readLock = readWriteLock.readLock();
	private final WriteLock writeLock = readWriteLock.writeLock();

	protected Session session;

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		this.setup(moduleDescriptor.getMutableSettingHandler());
	}

	@Override
	public void update(ForegroundModuleDescriptor moduleDescriptor, DataSource dataSource) throws Exception {
		super.update(moduleDescriptor, dataSource);

		this.setup(moduleDescriptor.getMutableSettingHandler());
	}

	protected void setup(MutableSettingHandler mutableSettingHandler) {

		try {
			writeLock.lock();
		} finally {

			if (StringUtils.isEmpty(host)) {

				this.session = null;

				this.sectionInterface.getSystemInterface().getEmailHandler().removeSender(this);

				log.info("Module " + this.moduleDescriptor + " not properly configured, not accepting mail");
			} else {

				Properties props = new Properties();
				props.put("mail.smtp.host", this.host);
				props.put("mail.smtp.port", this.port);
				props.put("mail.smtp.connectiontimeout", this.connectionTimeout);

				if (socketTimeout != null) {
					props.put("mail.smtp.timeout", this.socketTimeout);
				}

				if(secure) {
					props.put("mail.smtp.ssl", "true");
				}

				if(startTLS) {
					props.put("mail.smtp.starttls.enable", "true");
				}

				if (!useAuth) {
					this.session = Session.getInstance(props);
				} else {
					props.put("mail.smtp.auth", "true");
					this.session = Session.getInstance(props, new SimpleAuthenticator(username, password));
				}

				this.sectionInterface.getSystemInterface().getEmailHandler().addSender(this);

				log.info("Module " + this.moduleDescriptor + " ready to process mail");
			}

			writeLock.unlock();
		}
	}

	@Override
	public void unload() throws Exception {

		try {
			writeLock.lock();

			this.sectionInterface.getSystemInterface().getEmailHandler().removeSender(this);

			super.unload();
		} finally {

			writeLock.unlock();
		}
	}

	@Override
	public List<SettingDescriptor> getSettings() {

		List<? extends SettingDescriptor> superSettings = super.getSettings();

		if (superSettings != null) {
			ArrayList<SettingDescriptor> combinedSettings = new ArrayList<SettingDescriptor>();

			combinedSettings.addAll(superSettings);
			combinedSettings.addAll(SETTINGDESCRIPTORS);

			return combinedSettings;
		} else {
			return SETTINGDESCRIPTORS;
		}
	}

	@Override
	public boolean send(Email email) {

		try {
			readLock.lock();

			if (session != null) {

				try {
					MimeMessage message = EmailConverter.convert(email, session);

					Transport.send(message);

					log.info("Email " + email + " sent");

					this.incrementMailsSent();

					return true;

				} catch (MessagingException e) {
					log.error("Error sending email " + email + " using module " + this.moduleDescriptor + ", " + e);
				} catch (RuntimeException e) {
					log.error("Error sending email " + email + " using module " + this.moduleDescriptor, e);
				}
			}

			return false;

		} finally {
			readLock.unlock();
		}
	}

	@Override
	public synchronized void incrementMailsSent() {
		this.mailsSent++;
	}

	@Override
	public SimpleForegroundModuleResponse defaultMethod(HttpServletRequest req,	HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("<div class=\"contentitem\">");
		stringBuilder.append("<h1>" + this.moduleDescriptor.getName() + "</h1>");
		stringBuilder.append("<p>Mails sent: " + this.mailsSent + "</p>");

		if (session != null) {
			stringBuilder.append("<p>Current status: ready!</p>");
		} else {
			stringBuilder.append("<p>Current status: <b>not ready</b></p>");
		}

		stringBuilder.append("</div>");

		return new SimpleForegroundModuleResponse(stringBuilder.toString(), moduleDescriptor.getName(), this.getDefaultBreadcrumb());
	}

	@Override
	public int getPriority() {
		return this.priority;
	}
}
