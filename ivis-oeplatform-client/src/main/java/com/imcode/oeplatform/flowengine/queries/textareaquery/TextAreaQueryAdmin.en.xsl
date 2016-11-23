<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="classpath://com/nordicpeak/flowengine/queries/common/xsl/QueryAdminCommon.sv.xsl"/>
	<xsl:include href="TextAreaQueryAdminTemplates.xsl"/>
	
	<xsl:variable name="java.queryTypeName">iVIS textarea query</xsl:variable>
	
	<xsl:variable name="i18n.MaxLength">Permissible length of text conten</xsl:variable>
	<xsl:variable name="i18n.maxLength">allowed length of text content</xsl:variable>
	
	<xsl:variable name="i18n.TextAreaQueryNotFound">The required question was not found!</xsl:variable>
	<xsl:variable name="i18n.MaxLengthToBig">Maximum length cannot be bigger than 65535!</xsl:variable>

	<xsl:variable name="i18n.DependsOn">Depends on</xsl:variable>
	<xsl:variable name="i18n.DependencySourceName">Dependency source</xsl:variable>
	<xsl:variable name="i18n.DependencyFieldName">Dependency field</xsl:variable>


</xsl:stylesheet>
