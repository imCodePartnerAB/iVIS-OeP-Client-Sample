<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="ToggleModuleTemplates.xsl"/>

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
    <xsl:variable name="i18n.status">Status</xsl:variable>
	<xsl:variable name="i18n.enable">Starta</xsl:variable>
    <xsl:variable name="i18n.disable">Stoppa</xsl:variable>
	<xsl:variable name="i18n.enabled">Startad</xsl:variable>
    <xsl:variable name="i18n.disabled">Stoppad</xsl:variable>
	<xsl:variable name="i18n.settingsSaved.title">Inst�llningar sparade</xsl:variable>
	<xsl:variable name="i18n.settingsSaved.nutNotReloadedMessage">Inst�llningar sparade men ej till�mpade p� grund av systemfel.</xsl:variable>
	<xsl:variable name="i18n.settingsSaved.message">Inst�llningar sparade och till�mpade.</xsl:variable>
	<xsl:variable name="i18n.showSettings">Visa inst�llningar</xsl:variable>
</xsl:stylesheet>
