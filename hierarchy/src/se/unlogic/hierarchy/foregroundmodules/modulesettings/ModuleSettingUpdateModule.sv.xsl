<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="ModuleSettingUpdateModuleTemplates.xsl"/>
	
	<!-- Naming template.mode.field.type -->
	<xsl:variable name="i18n.expandAll" select="'Fäll ut alla'"/>
	<xsl:variable name="i18n.collapseAll" select="'Stäng alla'"/>
	<xsl:variable name="i18n.validation.requiredField" select="'Du måste fylla i fältet'"/>
	<xsl:variable name="i18n.validation.invalidFormat" select="'Felaktigt format på fältet'"/>
	<xsl:variable name="i18n.validation.tooShort" select="'För kort innehåll i fältet'"/>
	<xsl:variable name="i18n.validation.tooLong" select="'För långt innehåll i fältet'"/>		
	<xsl:variable name="i18n.validation.unknownError" select="'Okänt fel på fältet'"/>
	<xsl:variable name="i18n.unknownFault" select="'Ett okänt fel har uppstått'"/>

	<xsl:variable name="i18n.settingDescriptor.notSet" select="'Ej satt'"/>
	<xsl:variable name="i18n.settingDescriptor.unknownTypePart1" select="'Inställningen'"/>
	<xsl:variable name="i18n.settingDescriptor.unknownTypePart2" select="'har ett okänt format'"/>
	<xsl:variable name="i18n.settingDescriptor.resetDefualtValue" select="'Återställ standardvärde'"/>

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
	<xsl:variable name="i18n.settingsSaved.title">Inställningar sparade</xsl:variable>
	<xsl:variable name="i18n.settingsSaved.nutNotReloadedMessage">Inställningar sparade men ej tillämpade på grund systemfel.</xsl:variable>
	<xsl:variable name="i18n.settingsSaved.message">Inställningar sparade och tillämpade.</xsl:variable>
	<xsl:variable name="i18n.showSettings">Visa inställningar</xsl:variable>
</xsl:stylesheet>
