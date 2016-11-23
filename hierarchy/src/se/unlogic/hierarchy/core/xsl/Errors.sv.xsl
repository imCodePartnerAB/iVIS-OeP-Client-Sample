<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="ErrorTemplates.xsl"/>
	
	<!-- Naming template.mode.field.type -->
	
	<xsl:variable name="i18n.errors.footer" select="'Kontakta administrat�ren f�r mer information.'"/>

	<xsl:variable name="i18n.separateTransformationFailed.header" select="'Ett fel har intr�ffat'"/>
	<xsl:variable name="i18n.separateTransformationFailed.text1" select="'Tranformeringen av svaret fr�n modulen'"/>
	<xsl:variable name="i18n.separateTransformationFailed.text2" select="'misslyckades.'"/>
	<xsl:variable name="i18n.separateTransformationFailed.text3" select="'Felet som uppstod var:'"/>
	
	<xsl:variable name="i18n.separateTransformationWithoutStylesheet.header" select="'Ett fel har intr�ffat'"/>
	<xsl:variable name="i18n.separateTransformationWithoutStylesheet.text" select="'Ingen stilmall hittades f�r modulen'"/>
	
	<xsl:variable name="i18n.invalidModuleResonse.header" select="'Ett fel har intr�ffat'"/>
	<xsl:variable name="i18n.invalidModuleResonse.text1" select="'Modulen'"/>
	<xsl:variable name="i18n.invalidModuleResonse.text2" select="'genererade ett ogiltigt svar.'"/>
	
	<xsl:variable name="i18n.noModuleResponse.header" select="'Sidan genererade inget svar'"/>
	<xsl:variable name="i18n.noModuleResponse.text1" select="'Sidan med adressen'"/>
	<xsl:variable name="i18n.noModuleResponse.text2" select="'genererade inget svar.'"/>
	
	<xsl:variable name="i18n.URINotFoundException.header" select="'Sidan hittades inte'"/>
	<xsl:variable name="i18n.URINotFoundException.text1" select="'Sidan med adressen'"/>
	<xsl:variable name="i18n.URINotFoundException.text2" select="'hittades inte, kontrollera adressen.'"/>
	
	<xsl:variable name="i18n.unhandledModuleException.header" select="'Ett fel har intr�ffat'"/>
	<xsl:variable name="i18n.unhandledModuleException.text1" select="'Ett fel uppstod i modulen'"/>
	<xsl:variable name="i18n.unhandledModuleException.text2" select="'Felet som uppstod var:'"/>
	
	<xsl:variable name="i18n.accessDeniedException.header" select="'�tkomst nekad!'"/>
	<xsl:variable name="i18n.accessDeniedException.text" select="'Du har inte beh�righet att komma �t den h�r sidan.'"/>
	
	<xsl:variable name="i18n.ModuleConfigurationException.header" select="'Felaktig konfiguration!'"/>
	<xsl:variable name="i18n.ModuleConfigurationException.text" select="'Den beg�rda modulen �r inte korrekt konfigurerad.'"/>
	
	<xsl:variable name="i18n.ProtocolRedirectException.header" select="'Ett fel har intr�ffat'"/>
	<xsl:variable name="i18n.ProtocolRedirectException.text" select="'Det gick inte att vidarebefordra dig till det beg�rda protokollet'"/>	
	
	<xsl:variable name="i18n.SectionDefaultURINotSetException.header">F�rstasida saknas</xsl:variable>
	<xsl:variable name="i18n.SectionDefaultURINotSetException.text">Den h�r sektionen har ingen f�rstasida satt.</xsl:variable>
	
	<xsl:variable name="i18n.SectionDefaultURINotFoundException.header">F�rstasidan hittades inte</xsl:variable>
	<xsl:variable name="i18n.SectionDefaultURINotFoundException.text">F�rstasidan f�r den h�r sektionen hittades inte.</xsl:variable>
</xsl:stylesheet>
