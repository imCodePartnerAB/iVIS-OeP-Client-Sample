<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="ToggleModuleTemplates.xsl"/>
	
	<xsl:variable name="i18n.ModuleNotConfigured">This module is not configured yet.</xsl:variable>
	<xsl:variable name="i18n.ModuleNotFound">The requested module was not found.</xsl:variable>
	<xsl:variable name="i18n.NoSettingsNotFound">No settings found.</xsl:variable>
	<xsl:variable name="i18n.ConfiguredSettingsNotFound">None of the configured settings where found.</xsl:variable>
	<xsl:variable name="i18n.selectModule">Select module</xsl:variable>
	<xsl:variable name="i18n.selectAllowedSettings">Select allowed settings</xsl:variable>
	<xsl:variable name="i18n.id">ID</xsl:variable>
	<xsl:variable name="i18n.name">Name</xsl:variable>
	<xsl:variable name="i18n.type">Type</xsl:variable>
    <xsl:variable name="i18n.save">Save</xsl:variable>
    <xsl:variable name="i18n.status">Status</xsl:variable>
	<xsl:variable name="i18n.enable">Start</xsl:variable>
    <xsl:variable name="i18n.disable">Stop</xsl:variable>
	<xsl:variable name="i18n.enabled">Started</xsl:variable>
    <xsl:variable name="i18n.disabled">Stopped</xsl:variable>
	<xsl:variable name="i18n.settingsSaved.title">Settings saved</xsl:variable>
	<xsl:variable name="i18n.settingsSaved.nutNotReloadedMessage">Settings successfully saved but the system was unable to apply them.</xsl:variable>
	<xsl:variable name="i18n.settingsSaved.message">Settings successfully saved and applied.</xsl:variable>
	<xsl:variable name="i18n.showSettings">Show settings.</xsl:variable>
</xsl:stylesheet>
