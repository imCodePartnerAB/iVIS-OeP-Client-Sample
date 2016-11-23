<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="classpath://com/nordicpeak/flowengine/queries/common/xsl/QueryAdminCommon.sv.xsl"/>
	<xsl:include href="TextAreaQueryAdminTemplates.xsl"/>
	
	<xsl:variable name="java.queryTypeName">Textareafr�ga</xsl:variable>
	
	<xsl:variable name="i18n.MaxLength">Till�ten l�ngd p� textinneh�ll</xsl:variable>
	<xsl:variable name="i18n.maxLength">till�ten l�ngd p� textinneh�ll</xsl:variable>
	
	<xsl:variable name="i18n.TextAreaQueryNotFound">Den beg�rda fr�gan hittades inte!</xsl:variable>
	<xsl:variable name="i18n.MaxLengthToBig">Maxl�ngden f�r textinneh�llet kan vara h�gst 65535!</xsl:variable>

	<xsl:variable name="i18n.DependsOn">Depends on</xsl:variable>
	<xsl:variable name="i18n.DependencySourceName">Dependency source</xsl:variable>
	<xsl:variable name="i18n.DependencyFieldName">Dependency field</xsl:variable>

	
</xsl:stylesheet>
