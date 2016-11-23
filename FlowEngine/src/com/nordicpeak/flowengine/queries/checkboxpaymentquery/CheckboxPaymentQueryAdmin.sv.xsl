<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="classpath://com/nordicpeak/flowengine/queries/common/xsl/QueryAdminCommon.sv.xsl"/>
	<xsl:include href="CheckboxPaymentQueryAdminTemplates.xsl"/>
	
	<xsl:variable name="java.queryTypeName">Betalningsfr�ga - Kryssrutor</xsl:variable>
	<xsl:variable name="java.countText">Antal</xsl:variable>
	<xsl:variable name="java.alternativesText">Alternativ</xsl:variable>
	
	<xsl:variable name="i18n.CheckboxQueryNotFound">Den beg�rda fr�gan hittades inte!</xsl:variable>
	<xsl:variable name="i18n.MinChecked">Minst antal valda alternativ</xsl:variable>
	<xsl:variable name="i18n.MaxChecked">Max antal valda alternativ</xsl:variable>
	<xsl:variable name="i18n.minChecked">minst antal valda alternativ</xsl:variable>
	<xsl:variable name="i18n.maxChecked">max antal valda alternativ</xsl:variable>
	<xsl:variable name="i18n.MinCheckedBiggerThanMaxChecked">Minst antal valda alternativ f�r inte vara st�rre �n max!</xsl:variable>
	<xsl:variable name="i18n.MaxCheckedToBig">Max antal valda alternativ f�r inte �verstiga antalet alternativ!</xsl:variable>
	<xsl:variable name="i18n.MinCheckedToBig">Minst antal valda alternativ f�r inte �verstiga antalet alternativ!</xsl:variable>
	
	<xsl:variable name="i18n.ToFewAlternatives1Min">Du m�ste skapa minst 1 alternativ f�r fr�gan!</xsl:variable>
	
</xsl:stylesheet>
