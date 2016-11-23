<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="HDDTempModuleTemplates.xsl"/>

	<xsl:variable name="defaultTemperatureEmailSubject">Temperature alarm for drive $drive.device in server $server.name!</xsl:variable>
	<xsl:variable name="defaultTemperatureEmailMessage">Temperature $driveTemp.temp° detected for drive $drive.device in $server.name ($server.host)!

The max allowed temperature for this drive is: $drive.maxTemp°
The min allowed temperature for this drive is: $drive.minTemp°

No further warnings will be sent for this drive until you reset the alarms in the HDD temp module.

/HDD temp module	
	</xsl:variable>
	<xsl:variable name="defaultMissingDriveEmailSubject">Drive $drive.device missing from server $server.name!</xsl:variable>
	<xsl:variable name="defaultMissingDriveEmailMessage">No temperature data received for $drive.device in $server.name ($server.host)!

No further warnings will be sent for this drive until you reset the alarms in the HDD temp module.

/HDD temp module
	</xsl:variable>

	<xsl:variable name="List.NoServersFound">No servers found.</xsl:variable>
	<xsl:variable name="AddServer">Add server</xsl:variable>
	
	<xsl:variable name="Server.ConnectionTimeout">Connection timeout.</xsl:variable>
	<xsl:variable name="Server.UnableToConnect">Unable to connect to server.</xsl:variable>
	<xsl:variable name="Server.NoDrivesFound">No drives found.</xsl:variable>
	<xsl:variable name="Server.ResetAlarms">Reset all alarms</xsl:variable>
	
	<xsl:variable name="Server.EditServer">Edit server</xsl:variable>
	<xsl:variable name="Server.DeleteServer">Delete server</xsl:variable>
	
	<xsl:variable name="Server.Name">Name</xsl:variable>
	<xsl:variable name="Server.Host">Host</xsl:variable>
	<xsl:variable name="Server.Port">Port</xsl:variable>
	<xsl:variable name="Server.Monitor">Monitor</xsl:variable>
	<xsl:variable name="Server.MissingDriveWarning">Missing drive warning</xsl:variable>
	
	<xsl:variable name="ServerDrive.Device">Device</xsl:variable>
	<xsl:variable name="ServerDrive.Temperature">Temperature</xsl:variable>
	<xsl:variable name="ServerDrive.DriveSleeping">Drive is sleeping</xsl:variable>
	<xsl:variable name="ServerDrive.DriveError">Error reading drive temperature</xsl:variable>
	<xsl:variable name="ServerDrive.UnknownDriveState">Unknown drive state</xsl:variable>
	<xsl:variable name="ServerDrive.Type">Type</xsl:variable>
	<xsl:variable name="ServerDrive.MaxAllowedTemp">Max allowed temp</xsl:variable>
	<xsl:variable name="ServerDrive.MinAllowedTemp">Min allowed temp</xsl:variable>
	<xsl:variable name="ServerDrive.LastAlarm">Last alarm</xsl:variable>
	
	<xsl:variable name="AddServer.Title">Add server</xsl:variable>
	<xsl:variable name="AddServer.Submit">Add server</xsl:variable>
	
	<xsl:variable name="UpdateServer.Title">Edit server</xsl:variable>	
	<xsl:variable name="UpdateServer.Submit">Save changes</xsl:variable>
	
	<xsl:variable name="UpdateServerDrive.Title">Edit drive</xsl:variable>	
	<xsl:variable name="UpdateServerDrive.Submit">Save changes</xsl:variable>	
	
	<xsl:variable name="validationError.requiredField" select="'You need to fill in the field'" />
	<xsl:variable name="validationError.invalidFormat" select="'Invalid value in field'" />
	<xsl:variable name="validationError.tooShort" select="'Too short content in field'" />
	<xsl:variable name="validationError.tooLong" select="'Too long content in field'" />		
	<xsl:variable name="validationError.unknownError" select="'Unknown problem validating field'" />	

	<xsl:variable name="validationError.field.minTemp">min allowed temp</xsl:variable>
	<xsl:variable name="validationError.field.maxTemp">max allowed temp</xsl:variable>
	<xsl:variable name="validationError.field.name">name</xsl:variable>
	<xsl:variable name="validationError.field.host">host</xsl:variable>
	<xsl:variable name="validationError.field.port">port</xsl:variable>

	<xsl:variable name="validationError.message.ServerNotFound">The requested server could not be found</xsl:variable>
	<xsl:variable name="validationError.message.DriveNotFound">The requested drive could not be found</xsl:variable>
	<xsl:variable name="validationError.message.unknownFault">An unknown error has occurred!</xsl:variable>				
</xsl:stylesheet>