<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:import href="UserAdminModule.sv.xsl"/>
	
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="GroupAccessUserAdminModuleTemplates.xsl"/>
	
	<xsl:variable name="i18n.GroupAccess.message">Du har behörighet att administrera användare som är med i följande grupp(er): </xsl:variable>
	<xsl:variable name="i18n.NoGroupAccess">Du har inte behörighet att administrera några användare.</xsl:variable>
	<xsl:variable name="i18n.AtLeastOneGroupRequired">Användaren måste vara medlem i minst en grupp.</xsl:variable>
</xsl:stylesheet>
