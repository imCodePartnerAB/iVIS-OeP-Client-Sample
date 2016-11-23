<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="classpath://com/nordicpeak/flowengine/queries/common/xsl/QueryAdminCommon.sv.xsl"/>
	<xsl:include href="classpath://com/nordicpeak/flowengine/queries/basemapquery/BaseMapQueryCommon.sv.xsl"/>
	<xsl:include href="PUDMapQueryAdminTemplates.xsl"/>
	
	<xsl:variable name="java.queryTypeName">Markera punkt p� karta</xsl:variable>
	
	<xsl:variable name="i18n.MinimumScale">Minsta skala f�r att markera punkt p� kartan</xsl:variable>
	<xsl:variable name="i18n.minimumScale">minsta skala f�r att markera punkt p� kartan</xsl:variable>
	
	<xsl:variable name="i18n.PUDMapQueryNotFound">Den beg�rda fr�gan hittades inte!</xsl:variable>
	
</xsl:stylesheet>
