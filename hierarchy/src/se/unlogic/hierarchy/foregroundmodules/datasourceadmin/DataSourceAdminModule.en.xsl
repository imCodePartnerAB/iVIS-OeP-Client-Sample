<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>
	
	<xsl:include href="DataSourceAdminModuleTemplates.xsl"/>
	
	<xsl:variable name="copySuffix"> (copy)</xsl:variable>
	
	<xsl:variable name="addDataSourceBreadcrumbText">Add data source</xsl:variable>
	<xsl:variable name="updateDataSourceBreadcrumbText">Edit data source: </xsl:variable>
	
	<xsl:variable name="i18n.copyDataSource">Copy data source</xsl:variable>
	<xsl:variable name="i18n.editDataSource">Edit data source</xsl:variable>
	<xsl:variable name="i18n.deleteDataSource">Delete data source</xsl:variable>
	
	<xsl:variable name="i18n.stopDataSource">This data source is currently started, click here to stop it</xsl:variable>
	<xsl:variable name="i18n.stopDataSourceNotInDB">This data source is currently cached, click here to stop it (warning this data source was not found in the database so you may not be able to start it again if you stop it!)</xsl:variable>
	<xsl:variable name="i18n.cacheDataSource">This data source is currently stopped, click here to start it</xsl:variable>
	<xsl:variable name="i18n.disabledDataSource">This data source is currently disabled and can therefore not be started</xsl:variable>
	
	<xsl:variable name="i18n.addDataSourceLink">Add data source</xsl:variable>
	
	<xsl:variable name="i18n.AddDataSource.title">Add data source</xsl:variable>
	<xsl:variable name="i18n.UpdateDataSource.title">Update data source</xsl:variable>
	<xsl:variable name="i18n.name">Name</xsl:variable>
	<xsl:variable name="i18n.type">Type</xsl:variable>
	<xsl:variable name="i18n.type.SystemManaged">System managed</xsl:variable>
	<xsl:variable name="i18n.type.ContainerManaged">Container managed</xsl:variable>
	<xsl:variable name="i18n.enabled">Enabled</xsl:variable>
	<xsl:variable name="i18n.url">URL</xsl:variable>
	<xsl:variable name="i18n.driver">Driver</xsl:variable>
	<xsl:variable name="i18n.username">Username</xsl:variable>
	<xsl:variable name="i18n.password">Password</xsl:variable>
	<xsl:variable name="i18n.defaultCatalog">Default catalog</xsl:variable>
	<xsl:variable name="i18n.logAbandoned">Log abandoned connections</xsl:variable>
	<xsl:variable name="i18n.removeAbandoned">Remove abandoned connections</xsl:variable>
	<xsl:variable name="i18n.removeTimeout">Remove timeout</xsl:variable>
	<xsl:variable name="i18n.testOnBorrow">Test on borrow</xsl:variable>
	<xsl:variable name="i18n.validationQuery">Validation query</xsl:variable>
	<xsl:variable name="i18n.maxActive">Max active connections</xsl:variable>
	<xsl:variable name="i18n.maxIdle">Max idle connections</xsl:variable>
	<xsl:variable name="i18n.minIdle">Min idle connections</xsl:variable>
	<xsl:variable name="i18n.maxWait">Max wait for connection</xsl:variable>

	<xsl:variable name="i18n.driverInfo" select="'Enter the class name. Class must be loaded and accessible on classpath.'"/>
	<xsl:variable name="i18n.logAbandonedInfo" select="'Logging (stack trace) of code abandoning database connections.'"/>
	<xsl:variable name="i18n.removeAbandonedInfo" select="'Recycle abandoned database connections when available connections are scarse'"/>
	<xsl:variable name="i18n.removeAbandonedTimeoutInfo" select="'Number of seconds a database connection is allowed to be idle before considered abandoned.'"/>
	<xsl:variable name="i18n.testOnBorrowInfo" select="'Validation of database connections before use.'"/>
 	<xsl:variable name="i18n.maxActiveInfo" select="'Maximum number of database connections in pool. Set -1 for unlimited number.'"/>
 	<xsl:variable name="i18n.maxIdleInfo" select="'Maximum number of idle connections allowed in pool. Set -1 for unlimited number.'"/>
 	<xsl:variable name="i18n.minIdleInfo" select="'Minimum number of idle connections in pool.'"/>
	<xsl:variable name="i18n.maxWaitInfo" select="'Maximum wait time (in milliseconds) for available database connection. Set -1 for unlimited wait time.'"/>
	
	<xsl:variable name="i18n.add">Add</xsl:variable>
	<xsl:variable name="i18n.saveChanges">Save changes</xsl:variable>
	
	<xsl:variable name="i18n.validationError.requiredField" select="'You need to fill in the field'"/>
	<xsl:variable name="i18n.validationError.invalidFormat" select="'Invalid value in field'"/>
	<xsl:variable name="i18n.validationError.tooShort" select="'Too short content in field'"/>
	<xsl:variable name="i18n.validationError.tooLong" select="'Too long content in field'"/>		
	<xsl:variable name="i18n.validationError.unknownError" select="'Unknown problem validating field'"/>
	
	<xsl:variable name="i18n.validationError.message.CopyFailedDataSourceNotFound">Copy failed, the requested data source was not found!</xsl:variable>
	<xsl:variable name="i18n.validationError.message.UpdateFailedDataSourceNotFound">Update failed, the requested data source was not found!</xsl:variable>
	<xsl:variable name="i18n.validationError.message.DeleteFailedDataSourceNotFound">Delete failed, the requested data source was not found!</xsl:variable>
	<xsl:variable name="i18n.validationError.message.DriverNotAvailable">Data source driver not found (disable the datasource to ignore this warning)</xsl:variable>
	<xsl:variable name="i18n.validationError.message.NameNotBound">Data source name (Url) is not bound in context  (disable the datasource to ignore this warning)</xsl:variable>
	<xsl:variable name="i18n.validationError.message.DataSourceCannotStart">Data source did not start. Check settings</xsl:variable>
	<xsl:variable name="i18n.validationError.message.unknownFault" select="'An unknown error has occured!'"/>
</xsl:stylesheet>
