<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	
	<xsl:import href="UserAdminModule.en.xsl"/>
	
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="GroupAccessUserAdminModuleTemplates.xsl"/>
	
	<xsl:variable name="i18n.GroupAccess.message">You have access to administrate users which are members of the following group(s):</xsl:variable>
	<xsl:variable name="i18n.NoGroupAccess">You do not have access to administrate any users.</xsl:variable>
	<xsl:variable name="i18n.AtLeastOneGroupRequired">The user has be a members of at least one group.</xsl:variable>
</xsl:stylesheet>
