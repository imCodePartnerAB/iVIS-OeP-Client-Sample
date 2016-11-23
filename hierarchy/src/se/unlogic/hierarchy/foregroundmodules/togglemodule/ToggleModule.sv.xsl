<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="ToggleModuleTemplates.xsl"/>

	<xsl:variable name="i18n.ModuleNotConfigured">Den här modulen är inte konfigurerad än.</xsl:variable>
	<xsl:variable name="i18n.ModuleNotFound">Den begärda modulen hittades inte.</xsl:variable>
	<xsl:variable name="i18n.NoSettingsNotFound">Inga inställningar hittades för den valda modulen.</xsl:variable>
	<xsl:variable name="i18n.ConfiguredSettingsNotFound">Ingen av de valda inställningarna hittades.</xsl:variable>
	<xsl:variable name="i18n.selectModule">Välj modul</xsl:variable>
	<xsl:variable name="i18n.selectAllowedSettings">Välj tillåtna inställningar</xsl:variable>
	<xsl:variable name="i18n.id">ID</xsl:variable>
	<xsl:variable name="i18n.name">Namn</xsl:variable>
	<xsl:variable name="i18n.type">Typ</xsl:variable>
    <xsl:variable name="i18n.save">Spara</xsl:variable>
    <xsl:variable name="i18n.status">Status</xsl:variable>
	<xsl:variable name="i18n.enable">Starta</xsl:variable>
    <xsl:variable name="i18n.disable">Stoppa</xsl:variable>
	<xsl:variable name="i18n.enabled">Startad</xsl:variable>
    <xsl:variable name="i18n.disabled">Stoppad</xsl:variable>
	<xsl:variable name="i18n.settingsSaved.title">Inställningar sparade</xsl:variable>
	<xsl:variable name="i18n.settingsSaved.nutNotReloadedMessage">Inställningar sparade men ej tillämpade på grund av systemfel.</xsl:variable>
	<xsl:variable name="i18n.settingsSaved.message">Inställningar sparade och tillämpade.</xsl:variable>
	<xsl:variable name="i18n.showSettings">Visa inställningar</xsl:variable>
</xsl:stylesheet>
