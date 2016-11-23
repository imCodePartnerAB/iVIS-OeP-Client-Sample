<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="classpath://com/nordicpeak/flowengine/queries/common/xsl/QueryAdminCommon.sv.xsl"/>
	<xsl:include href="PUDQueryAdminTemplates.xsl"/>
	
	<xsl:variable name="java.queryTypeName">Ange fastighetsbeteckning</xsl:variable>
	<xsl:variable name="java.lmUserSettingName">Anv�ndare lanm�teriet</xsl:variable>
	<xsl:variable name="java.lmUserSettingDescription">Anv�ndare som anv�nds f�r anrop mot lantm�teriet via Search LM</xsl:variable>
	<xsl:variable name="java.searchPrefixSettingName">S�kprefix</xsl:variable>
	<xsl:variable name="java.searchPrefixSettingDescription">S�kprefix som anv�nds vid anrop mot lantm�teriet via Serch LM</xsl:variable>
	
	<xsl:variable name="i18n.PUDQueryNotFound">Den beg�rda fr�gan hittades inte!</xsl:variable>
	<xsl:variable name="i18n.AllowedSearchServices">S�ktj�nster</xsl:variable>
	<xsl:variable name="i18n.AllowedSearchServicesDescription">V�lj vilka s�ktj�nster som skall finnas tillg�nglig f�r den h�r fr�gan</xsl:variable>
	<xsl:variable name="i18n.NoSearchService">Du m�ste v�lja minst en s�ktj�nst</xsl:variable>
	
	<xsl:variable name="i18n.PUD">S�k via fastighetsbeteckning</xsl:variable>
	<xsl:variable name="i18n.Address">S�k via adress</xsl:variable>
	
</xsl:stylesheet>
