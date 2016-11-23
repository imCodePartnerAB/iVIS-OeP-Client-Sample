<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="ModuleSettingUpdateModuleTemplates.xsl"/>
	
	<!-- Naming template.mode.field.type -->
	<xsl:variable name="i18n.expandAll" select="'F�ll ut alla'"/>
	<xsl:variable name="i18n.collapseAll" select="'St�ng alla'"/>
	<xsl:variable name="i18n.validation.requiredField" select="'Du m�ste fylla i f�ltet'"/>
	<xsl:variable name="i18n.validation.invalidFormat" select="'Felaktigt format p� f�ltet'"/>
	<xsl:variable name="i18n.validation.tooShort" select="'F�r kort inneh�ll i f�ltet'"/>
	<xsl:variable name="i18n.validation.tooLong" select="'F�r l�ngt inneh�ll i f�ltet'"/>		
	<xsl:variable name="i18n.validation.unknownError" select="'Ok�nt fel p� f�ltet'"/>
	<xsl:variable name="i18n.unknownFault" select="'Ett ok�nt fel har uppst�tt'"/>

	<xsl:variable name="i18n.settingDescriptor.notSet" select="'Ej satt'"/>
	<xsl:variable name="i18n.settingDescriptor.unknownTypePart1" select="'Inst�llningen'"/>
	<xsl:variable name="i18n.settingDescriptor.unknownTypePart2" select="'har ett ok�nt format'"/>
	<xsl:variable name="i18n.settingDescriptor.resetDefualtValue" select="'�terst�ll standardv�rde'"/>

	<xsl:variable name="i18n.ModuleNotConfigured">Den h�r modulen �r inte konfigurerad �n.</xsl:variable>
	<xsl:variable name="i18n.ModuleNotFound">Den beg�rda modulen hittades inte.</xsl:variable>
	<xsl:variable name="i18n.NoSettingsNotFound">Inga inst�llningar hittades f�r den valda modulen.</xsl:variable>
	<xsl:variable name="i18n.ConfiguredSettingsNotFound">Ingen av de valda inst�llningarna hittades.</xsl:variable>
	<xsl:variable name="i18n.selectModule">V�lj modul</xsl:variable>
	<xsl:variable name="i18n.selectAllowedSettings">V�lj till�tna inst�llningar</xsl:variable>
	<xsl:variable name="i18n.id">ID</xsl:variable>
	<xsl:variable name="i18n.name">Namn</xsl:variable>
	<xsl:variable name="i18n.type">Typ</xsl:variable>
	<xsl:variable name="i18n.save">Spara</xsl:variable>
	<xsl:variable name="i18n.settingsSaved.title">Inst�llningar sparade</xsl:variable>
	<xsl:variable name="i18n.settingsSaved.nutNotReloadedMessage">Inst�llningar sparade men ej till�mpade p� grund systemfel.</xsl:variable>
	<xsl:variable name="i18n.settingsSaved.message">Inst�llningar sparade och till�mpade.</xsl:variable>
	<xsl:variable name="i18n.showSettings">Visa inst�llningar</xsl:variable>
</xsl:stylesheet>
