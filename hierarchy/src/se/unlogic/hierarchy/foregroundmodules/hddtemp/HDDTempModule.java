/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.hddtemp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.emailutils.framework.InvalidEmailAddressException;
import se.unlogic.emailutils.framework.NoEmailSendersFoundException;
import se.unlogic.emailutils.framework.SimpleEmail;
import se.unlogic.emailutils.framework.UnableToProcessEmailException;
import se.unlogic.emailutils.validation.StringEmailValidator;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.annotations.XSLVariable;
import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.beans.SettingDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.beans.ValueDescriptor;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.utils.CRUDCallback;
import se.unlogic.hierarchy.core.utils.IntegerBasedCRUD;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.hierarchy.foregroundmodules.hddtemp.cruds.HDDCRUD;
import se.unlogic.standardutils.dao.AnnotatedDAO;
import se.unlogic.standardutils.dao.AnnotatedDAOWrapper;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.LowLevelQuery;
import se.unlogic.standardutils.dao.SimpleAnnotatedDAOFactory;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.dao.querys.UpdateQuery;
import se.unlogic.standardutils.db.DBUtils;
import se.unlogic.standardutils.db.tableversionhandler.TableVersionHandler;
import se.unlogic.standardutils.db.tableversionhandler.UpgradeResult;
import se.unlogic.standardutils.db.tableversionhandler.XMLDBScriptProvider;
import se.unlogic.standardutils.hddtemp.Drive;
import se.unlogic.standardutils.hddtemp.HDDTempUtils;
import se.unlogic.standardutils.image.ImageUtils;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.string.BeanTagSourceFactory;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.string.TagReplacer;
import se.unlogic.standardutils.time.MillisecondTimeUnits;
import se.unlogic.standardutils.timer.RunnableTimerTask;
import se.unlogic.standardutils.validation.PositiveStringIntegerValidator;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.populators.annotated.AnnotatedRequestPopulator;


public class HDDTempModule extends AnnotatedForegroundModule implements CRUDCallback<User>, Runnable{

	private static final BeanTagSourceFactory<Server> SERVER_TAG_SOURCE_FACTORY = new BeanTagSourceFactory<Server>(Server.class);
	private static final BeanTagSourceFactory<ServerDrive> SERVER_DRIVE_TAG_SOURCE_FACTORY = new BeanTagSourceFactory<ServerDrive>(ServerDrive.class);
	private static final BeanTagSourceFactory<Drive> DRIVE_TAG_SOURCE_FACTORY = new BeanTagSourceFactory<Drive>(Drive.class);

	private static final String AVAILABLE_TAGS;

	static{
		SERVER_TAG_SOURCE_FACTORY.addAllFields("$server.", "drives");
		SERVER_DRIVE_TAG_SOURCE_FACTORY.addAllFields("$drive.", "server","driveTemp");
		DRIVE_TAG_SOURCE_FACTORY.addAllFields("$driveTemp.","device");

		AVAILABLE_TAGS = StringUtils.toCommaSeparatedString(SERVER_TAG_SOURCE_FACTORY.getTagsSet()) + "," + StringUtils.toCommaSeparatedString(SERVER_DRIVE_TAG_SOURCE_FACTORY.getTagsSet()) + "," + StringUtils.toCommaSeparatedString(DRIVE_TAG_SOURCE_FACTORY.getTagsSet());
	}

	private static final Field SERVER_DRIVE_RELATION = ReflectionUtils.getField(Server.class, "drives");

	private static final HighLevelQuery<Server> GET_ALL_SERVERS_WITH_DRIVES = new HighLevelQuery<Server>(SERVER_DRIVE_RELATION);

	private HighLevelQuery<Server> getServersWithMonitoringEnabled;

	private AnnotatedDAO<Server> serverDAO;
	private AnnotatedDAO<ServerDrive> serverDriveDAO;

	private IntegerBasedCRUD<Server,HDDTempModule> serverCRUD;
	private IntegerBasedCRUD<ServerDrive,HDDTempModule> serverDriveCRUD;

	@XSLVariable
	private String defaultTemperatureEmailSubject = "This string should be set by your XSL stylesheet";

	@XSLVariable
	private String defaultTemperatureEmailMessage = "This string should be set by your XSL stylesheet";

	@XSLVariable
	private String defaultMissingDriveEmailSubject = "This string should be set by your XSL stylesheet";

	@XSLVariable
	private String defaultMissingDriveEmailMessage = "This string should be set by your XSL stylesheet";

	@ModuleSetting
	@TextFieldSettingDescriptor(name="Connection timeout",description="The timeout in milliseconds when connecting to a HDDTemp server",required=true,formatValidator=PositiveStringIntegerValidator.class)
	private int connectionTimeout = 5000;

	@ModuleSetting
	@TextFieldSettingDescriptor(name="Monitor interval",description="How often monitored servers should be checked (in minutes)",required=true,formatValidator=PositiveStringIntegerValidator.class)
	private int monitorInterval = 2;

	@ModuleSetting
	@TextFieldSettingDescriptor(name="Gauge width",description="Width of generated PNG gauges",required=true,formatValidator=PositiveStringIntegerValidator.class)
	private int gaugeWidth = 140;

	@ModuleSetting
	@TextFieldSettingDescriptor(name="Sender name",description="The name of the sender used in warning messages",required=true)
	private String senderName = "HDD temp module";

	@ModuleSetting
	@TextFieldSettingDescriptor(name="Sender address",description="The address from which warning messages are sent",required=true,formatValidator=StringEmailValidator.class)
	private String senderAdress = "admin@somesite";

	@XSLVariable(name="defaultTemperatureEmailSubject")
	@ModuleSetting
	private String temperatureEmailSubject;

	@XSLVariable(name="defaultTemperatureEmailMessage")
	@ModuleSetting
	private String temperatureEmailMessage;

	@XSLVariable(name="defaultMissingDriveEmailSubject")
	@ModuleSetting
	private String missingDriveEmailSubject;

	@XSLVariable(name="defaultMissingDriveEmailMessage")
	@ModuleSetting
	private String missingDriveEmailMessage;

	@ModuleSetting(allowsNull=true)
	protected List<Integer> groupIDs;

	@ModuleSetting(allowsNull=true)
	protected List<Integer> userIDs;

	private Timer timer;

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		setupTimer();
	}

	@Override
	public void update(ForegroundModuleDescriptor moduleDescriptor, DataSource dataSource) throws Exception {

		super.update(moduleDescriptor, dataSource);
		setupTimer();
	}

	@Override
	public void unload() throws Exception {

		this.stopTimer();

		super.unload();
	}

	private synchronized void setupTimer() throws SQLException {

		this.stopTimer();

		if(this.userIDs != null || this.groupIDs != null){

			log.info("Drive monitoring timer started with " + this.monitorInterval + " minute interval");

			this.timer = new Timer("Time for foreground module " + moduleDescriptor,true);

			timer.schedule(new RunnableTimerTask(this), 5000, this.monitorInterval * MillisecondTimeUnits.MINUTE);
		}
	}

	private synchronized void stopTimer(){

		if(timer != null){
			log.info("Drive monitoring timer stopped");
			timer.cancel();
			timer = null;
		}
	}

	public boolean isTimerStarted(){

		return this.timer != null;
	}

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		log.debug("Checking for hdd temp tables in datasource " + dataSource);

		if(TableVersionHandler.getTableGroupVersion(dataSource, HDDTempModule.class.getName()) == null){

			if (!DBUtils.tableExists(dataSource, "hddtempservers")) {

				log.info("Creating hddtempservers table in datasource " + dataSource);

				String sql = StringUtils.readStreamAsString(this.getClass().getResourceAsStream("dbscripts/hddtempservers.sql"));

				new UpdateQuery(dataSource.getConnection(), true, sql).executeUpdate();

			}

			if (!DBUtils.tableExists(dataSource, "hddtempdrives")) {

				log.info("Creating hddtempdrives table in datasource " + dataSource);

				String sql = StringUtils.readStreamAsString(this.getClass().getResourceAsStream("dbscripts/hddtempdrives.sql"));

				new UpdateQuery(dataSource.getConnection(), true, sql).executeUpdate();

			}
		}

		//New style able version handling
		UpgradeResult upgradeResult = TableVersionHandler.upgradeDBTables(dataSource, HDDTempModule.class.getName(), new XMLDBScriptProvider(this.getClass().getResourceAsStream("dbscripts/DB script.xml")));

		if(upgradeResult.isUpgrade()){

			log.info(upgradeResult.toString());
		}

		SimpleAnnotatedDAOFactory daoFactory = new SimpleAnnotatedDAOFactory(dataSource);

		serverDAO = daoFactory.getDAO(Server.class);
		serverDriveDAO = daoFactory.getDAO(ServerDrive.class);

		this.serverCRUD = new HDDCRUD<Server>(new AnnotatedDAOWrapper<Server,Integer>(serverDAO,"serverID",Integer.class), new AnnotatedRequestPopulator<Server>(Server.class), "Server", "server", this);
		this.serverDriveCRUD = new HDDCRUD<ServerDrive>(new ServerDriveDAOWrapper(serverDriveDAO,"driveID"), new AnnotatedRequestPopulator<ServerDrive>(ServerDrive.class), "ServerDrive", "server drive", this);

		this.getServersWithMonitoringEnabled = new HighLevelQuery<Server>(SERVER_DRIVE_RELATION);

		this.getServersWithMonitoringEnabled.addParameter(serverDAO.getParamFactory("monitor", boolean.class).getParameter(true));
	}

	@Override
	public ForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return this.list(req, res, user, uriParser, null);
	}

	public ForegroundModuleResponse list(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, List<ValidationError> validationErrors) throws Exception {

		log.info("User " + user + " listing servers and drive temperatures");

		List<Server> servers = this.getServersAndTemperatures();

		Document doc = this.createDocument(req, uriParser);

		Element listElement = doc.createElement("List");
		doc.getFirstChild().appendChild(listElement);

		XMLUtils.append(doc, listElement, "Servers", servers);

		if(validationErrors != null){

			XMLUtils.append(doc, listElement, validationErrors);
		}

		return new SimpleForegroundModuleResponse(doc, this.getDefaultBreadcrumb());
	}

	@WebPublic(alias="addserver")
	public ForegroundModuleResponse addServer(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception{

		return this.serverCRUD.add(req, res, user, uriParser);
	}

	@WebPublic(alias="updateserver")
	public ForegroundModuleResponse updateServer(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception{

		return this.serverCRUD.update(req, res, user, uriParser);
	}

	@WebPublic(alias="deleteserver")
	public ForegroundModuleResponse deleteServer(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception{

		return this.serverCRUD.delete(req, res, user, uriParser);
	}

	@WebPublic(alias="updatedrive")
	public ForegroundModuleResponse updateDrive(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception{

		return this.serverDriveCRUD.update(req, res, user, uriParser);
	}

	@WebPublic(alias="deletedrive")
	public ForegroundModuleResponse deleteDrive(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception{

		return this.serverDriveCRUD.delete(req, res, user, uriParser);
	}

	@WebPublic(alias="resetalarms")
	public ForegroundModuleResponse resetAlarms(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception{

		log.info("User " + user + " reseting alarms");

		LowLevelQuery<ServerDrive> query = new LowLevelQuery<ServerDrive>();

		query.setSql("UPDATE hddtemp_drives SET lastAlarm = ?");

		query.addParameter(null);

		this.serverDriveDAO.update(query);

		this.redirectToDefaultMethod(req, res);

		return null;
	}

	@WebPublic(alias="gauage")
	public ForegroundModuleResponse generateGauge(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException, URINotFoundException{

		Integer temperature;

		if(uriParser.size() == 3 && (temperature = NumberUtils.toInt(uriParser.get(2))) != null){

			if(temperature < 0){

				temperature = 0;

			}else if(temperature > 59){

				temperature = 59;
			}

			BufferedImage bufferedImage = ImageIO.read(HDDTempModule.class.getResource("resources/temp_gauage.png"));

			Graphics2D graphics = bufferedImage.createGraphics();

			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			Polygon polygon = new Polygon();

			polygon.addPoint(100, 15);
			polygon.addPoint(96, 100);
			polygon.addPoint(104, 100);

			AffineTransform transformer = AffineTransform.getRotateInstance((((temperature*6)+180)*Math.PI)/180, 100, 100);

			Shape shape = transformer.createTransformedShape(polygon);


			if(temperature < 40){

				graphics.setColor(Color.GREEN);

			}else if(temperature >= 40 && temperature < 45){

				graphics.setColor(Color.YELLOW);

			}else{

				graphics.setColor(Color.RED);
			}


			graphics.fill(shape);

			bufferedImage = ImageUtils.scaleImageByWidth(bufferedImage, gaugeWidth, Image.SCALE_SMOOTH, BufferedImage.TYPE_INT_ARGB);

			res.setContentType("image/png");

			try {
				ImageIO.write(bufferedImage, ImageUtils.PNG, res.getOutputStream());

			} catch (Exception e) {

				log.info("Error " + e + " sending generated gauage to user " + user + " requesting from " + req.getRemoteAddr());
			}

			return null;
		}

		throw new URINotFoundException(uriParser);
	}

	private List<Server> getServersAndTemperatures() throws SQLException, IOException {

		TransactionHandler transactionHandler = null;

		try{

			boolean hasChanges = false;

			transactionHandler = this.serverDAO.createTransaction();

			List<Server> servers = this.serverDAO.getAll(GET_ALL_SERVERS_WITH_DRIVES);

			if(servers != null){

				for(Server server : servers){

					List<Drive> driveTemps;

					try {
						driveTemps = HDDTempUtils.getHddTemp(server.getHost(), server.getPort(), connectionTimeout);

					} catch (java.net.SocketTimeoutException e) {

						server.setTimeout(true);
						continue;

					}catch(IOException e){

						server.setUnableToConnect(true);
						continue;
					}

					if(driveTemps != null){

						for(Drive drive : driveTemps){

							ServerDrive serverDrive = getServerDrive(server,drive.getDevice());

							if(serverDrive == null){

								log.info("New drive " + drive.getDevice() + " detected on server " + server + ", adding drive to database...");

								serverDrive = new ServerDrive(drive);
								serverDrive.setServer(server);

								this.serverDriveDAO.add(serverDrive);

								serverDrive.setServer(null);

								if(server.getDrives() == null){
									server.setDrives(new ArrayList<ServerDrive>());
								}

								server.getDrives().add(serverDrive);

								hasChanges = true;
							}

							serverDrive.setDriveTemp(drive);
						}
					}
				}
			}

			if(hasChanges){
				transactionHandler.commit();
			}

			return servers;
		}finally{

			TransactionHandler.autoClose(transactionHandler);
		}
	}

	private ServerDrive getServerDrive(Server server, String device) {

		if(server.getDrives() != null){

			for(ServerDrive serverDrive: server.getDrives()){

				if(serverDrive.getDevice().equals(device)){

					return serverDrive;
				}
			}
		}

		return null;
	}

	public Document createDocument(HttpServletRequest req, URIParser uriParser) {

		Document doc = XMLUtils.createDomDocument();
		Element document = doc.createElement("Document");
		document.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser));
		document.appendChild(this.sectionInterface.getSectionDescriptor().toXML(doc));
		document.appendChild(this.moduleDescriptor.toXML(doc));

		XMLUtils.appendNewElement(doc, document, "gaugeWidth", gaugeWidth);

		doc.appendChild(document);
		return doc;
	}

	@Override
	public Document createDocument(HttpServletRequest req, URIParser uriParser, User user) {

		return this.createDocument(req, uriParser);
	}

	@Override
	public String getTitlePrefix() {

		return this.moduleDescriptor.getName();
	}

	@Override
	public List<SettingDescriptor> getSettings() {

		ArrayList<SettingDescriptor> settingDescriptors = new ArrayList<SettingDescriptor>();

		List<? extends SettingDescriptor> superSettings = super.getSettings();

		if(superSettings != null){

			settingDescriptors.addAll(superSettings);
		}

		// Generate group multilist settingdescriptor
		ArrayList<ValueDescriptor> groupValueDescriptors = new ArrayList<ValueDescriptor>();

		for (Group group : systemInterface.getGroupHandler().getGroups(false)) {
			groupValueDescriptors.add(new ValueDescriptor(group.getName(), group.getGroupID().toString()));
		}

		settingDescriptors.add(SettingDescriptor.createMultiListSetting("groupIDs", "Monitoring groups", "Groups that will be notified in the event of an alarm", false, null, groupValueDescriptors));

		// Generate user multilist settingdescriptor
		ArrayList<ValueDescriptor> userValueDescriptors = new ArrayList<ValueDescriptor>();

		for (User user : this.sectionInterface.getSystemInterface().getUserHandler().getUsers(false, false)) {
			userValueDescriptors.add(new ValueDescriptor(user.getFirstname() + " " + user.getLastname(), user.getUserID().toString()));
		}

		settingDescriptors.add(SettingDescriptor.createMultiListSetting("userIDs", "Monitoring users", "Users that will be notified in the event of an alarm", false, null, userValueDescriptors));

		settingDescriptors.add(SettingDescriptor.createTextFieldSetting("temperatureEmailSubject", "Temperature alarm subject", "The subject of e-mail messages sent when a temperature alarm is triggered. Available tags are: " + AVAILABLE_TAGS, true, this.defaultTemperatureEmailSubject, null));
		settingDescriptors.add(SettingDescriptor.createTextAreaSetting("temperatureEmailMessage", "Temperature alarm message", "The message of e-mail messages sent when a temperature alarm is triggered. Available tags are: " + AVAILABLE_TAGS, true, this.defaultTemperatureEmailMessage, null));

		settingDescriptors.add(SettingDescriptor.createTextFieldSetting("missingDriveEmailSubject", "Missing drive alarm subject", "The subject of e-mail messages sent when a missing drive alarm is triggered. Available tags are: " + AVAILABLE_TAGS, true, this.defaultMissingDriveEmailSubject, null));
		settingDescriptors.add(SettingDescriptor.createTextAreaSetting("missingDriveEmailMessage", "Missing drive alarm message", "The message of e-mail messages sent when a temperature alarm is triggered. Available tags are: " + AVAILABLE_TAGS, true, this.defaultMissingDriveEmailMessage, null));

		return settingDescriptors;
	}

	@Override
	public void run() {

		try {
			List<Server> servers = this.serverDAO.getAll(getServersWithMonitoringEnabled);

			if(servers != null){

				for(Server server : servers){

					if(server.getDrives() != null){

						List<Drive> driveTemps;

						try {
							driveTemps = HDDTempUtils.getHddTemp(server.getHost(), server.getPort(), connectionTimeout);

						} catch (java.net.SocketTimeoutException e) {

							log.info("Unable to get drive temperatures from server " + server + " (" + e + ")");
							continue;

						} catch (IOException e) {

							log.info("Unable to get drive temperatures from server " + server + " (" + e + ")");
							continue;
						}

						if(driveTemps != null){

							for(ServerDrive serverDrive : server.getDrives()){

								Iterator<Drive> iterator = driveTemps.iterator();

								while(iterator.hasNext()){

									Drive drive = iterator.next();

									if(serverDrive.getDevice().equals(drive.getDevice())){

										serverDrive.setDriveTemp(drive);
										iterator.remove();
										break;
									}
								}
							}
						}

						for(ServerDrive serverDrive : server.getDrives()){

							if(serverDrive.getLastAlarm() != null){

								continue;
							}

							if(server.getMissingDriveWarning() && serverDrive.getDriveTemp() == null){

								log.info("Detected missing drive " + serverDrive + " on server " + server);

								serverDrive.setServer(server);

								this.sendMissingDriveWarning(serverDrive);

								serverDrive.setLastAlarm(new Timestamp(System.currentTimeMillis()));

								this.serverDriveDAO.update(serverDrive);

							}else if(serverDrive.getDriveTemp() != null && serverDrive.getDriveTemp().getTemp() != null){

								if(serverDrive.getMaxTemp() != null && serverDrive.getDriveTemp().getTemp() > serverDrive.getMaxTemp()){

									log.info("Detected overheated drive " + serverDrive + " on server " + server);

									serverDrive.setServer(server);

									this.sendDriveTempWarning(serverDrive);

									serverDrive.setLastAlarm(new Timestamp(System.currentTimeMillis()));

									this.serverDriveDAO.update(serverDrive);

								}else if(serverDrive.getMinTemp() != null && serverDrive.getDriveTemp().getTemp() < serverDrive.getMinTemp()){

									log.info("Detected underheated drive " + serverDrive + " on server " + server);

									serverDrive.setServer(server);

									this.sendDriveTempWarning(serverDrive);

									serverDrive.setLastAlarm(new Timestamp(System.currentTimeMillis()));

									this.serverDriveDAO.update(serverDrive);
								}
							}
						}
					}
				}

			}else{
				log.debug("No servers with monitoring enabled found");
			}

		} catch (SQLException e) {

			log.error("Unable to get servers with monitoring enabled from DB",e);

		}catch (RuntimeException e){

			log.error("Error in drive monitoring thread, stopping timer",e);
			this.timer.cancel();
		}
	}

	private void sendDriveTempWarning(ServerDrive serverDrive) {

		TagReplacer tagReplacer = this.getTagReplacer(serverDrive);

		String subject = tagReplacer.replace(temperatureEmailSubject);
		String message = tagReplacer.replace(temperatureEmailMessage);

		this.sendMessages(subject,message);
	}

	private void sendMissingDriveWarning(ServerDrive serverDrive) {

		TagReplacer tagReplacer = this.getTagReplacer(serverDrive);

		String subject = tagReplacer.replace(missingDriveEmailSubject);
		String message = tagReplacer.replace(missingDriveEmailMessage);

		this.sendMessages(subject,message);
	}

	private void sendMessages(String subject, String message) {

		if(this.userIDs != null){

			for(Integer userID : userIDs){

				User user = this.systemInterface.getUserHandler().getUser(userID, false, false);

				if(user != null){

					this.sendUserMessage(user,subject,message);
				}
			}
		}

		if(this.groupIDs != null){

			List<Group> groups = this.systemInterface.getGroupHandler().getGroups(groupIDs, false);

			for(Group group : groups){

				List<User> users = this.systemInterface.getUserHandler().getUsersByGroup(group.getGroupID(), false, false);

				if(users != null){

					for(User user : users){

						this.sendUserMessage(user,subject,message);
					}
				}
			}
		}
	}

	private void sendUserMessage(User user, String subject, String message) {

		try {
			SimpleEmail email = new SimpleEmail();

			email.setSenderAddress(senderAdress);
			email.setSenderName(senderName);
			email.setSubject(subject);
			email.setMessage(message);
			email.addRecipient(user.getEmail());

			log.info("Sending warning message \"" + subject + "\" to user " + user);

			this.systemInterface.getEmailHandler().send(email);

		} catch (InvalidEmailAddressException e) {

			log.error("Unable to send warning message to user " + user,e);

		} catch (NoEmailSendersFoundException e) {

			log.error("Unable to send warning message to user " + user,e);

		} catch (UnableToProcessEmailException e) {

			log.error("Unable to send warning message to user " + user,e);
		}
	}

	private TagReplacer getTagReplacer(ServerDrive serverDrive) {

		TagReplacer tagReplacer = new TagReplacer();

		tagReplacer.addTagSource(SERVER_TAG_SOURCE_FACTORY.getTagSource(serverDrive.getServer()));
		tagReplacer.addTagSource(SERVER_DRIVE_TAG_SOURCE_FACTORY.getTagSource(serverDrive));

		if(serverDrive.getDriveTemp() != null){

			tagReplacer.addTagSource(DRIVE_TAG_SOURCE_FACTORY.getTagSource(serverDrive.getDriveTemp()));
		}

		return tagReplacer;
	}
}
