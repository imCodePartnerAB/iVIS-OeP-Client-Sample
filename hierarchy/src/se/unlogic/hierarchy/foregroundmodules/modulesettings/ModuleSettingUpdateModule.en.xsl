<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="ModuleSettingUpdateModuleTemplates.xsl"/>
	<!-- Naming template.mode.field.type -->
	
	<xsl:variable name="i18n.expandAll" select="'Expand all'"/>
	<xsl:variable name="i18n.collapseAll" select="'Collapse all'"/>

	<xsl:variable name="i18n.validation.requiredField" select="'You must fill in the field'"/>
	<xsl:variable name="i18n.validation.invalidFormat" select="'Incorrect format in field'"/>
	<xsl:variable name="i18n.validation.tooShort" select="'Too short content in field '"/>
	<xsl:variable name="i18n.validation.tooLong" select="'Too long content in field'"/>		
	<xsl:variable name="i18n.validation.unknownError" select="'Unknown error in field'"/>
	
	<xsl:variable name="i18n.unknownFault" select="'An unknown fault has occurred'"/>

	<xsl:variable name="i18n.settingDescriptor.notSet" select="'Not set'"/>
	<xsl:variable name="i18n.settingDescriptor.unknownTypePart1" select="'The setting'"/>
	<xsl:variable name="i18n.settingDescriptor.unknownTypePart2" select="'has an unknown format'"/>
	<xsl:variable name="i18n.settingDescriptor.resetDefualtValue" select="'Reset default value'"/>
	
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
	<xsl:variable name="i18n.settingsSaved.title">Settings saved</xsl:variable>
	<xsl:variable name="i18n.settingsSaved.nutNotReloadedMessage">Settings successfully saved but the system was unable to apply them.</xsl:variable>
	<xsl:variable name="i18n.settingsSaved.message">Settings successfully saved and applied.</xsl:variable>
	<xsl:variable name="i18n.showSettings">Show settings.</xsl:variable>
</xsl:stylesheet>
