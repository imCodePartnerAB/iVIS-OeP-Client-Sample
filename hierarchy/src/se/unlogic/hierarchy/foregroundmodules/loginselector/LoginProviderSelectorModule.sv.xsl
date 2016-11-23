<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="LoginProviderSelectorModuleTemplates.xsl"/>
	
	<xsl:variable name="i18n.NoLoginProviderAvailable.Title">Inloggning</xsl:variable>
	<xsl:variable name="i18n.NoLoginProviderAvailable.Message">Det finns f�r n�rvarande inga inloggningsmetoder tillg�ngliga.</xsl:variable>

	<xsl:variable name="i18n.ConfigureModule">V�lj inloggningsmetoder</xsl:variable>
	<xsl:variable name="i18n.Configure.Title">Inst�llningar</xsl:variable>
	<xsl:variable name="i18n.SaveChanges">Spara �ndringar</xsl:variable>
	<xsl:variable name="i18n.NoLoginProvidersFoundInLoginHandler">Inga inloggningsmoduler hittades.</xsl:variable>
	<xsl:variable name="i18n.Description">Beskrivning</xsl:variable>
	<xsl:variable name="i18n.ButtonText">Text p� knapp</xsl:variable>
	
	<xsl:variable name="i18n.ValidationErrorsPresent">Det finns ett eller flera valideringsfel i formul�ret nedan</xsl:variable>
	
	<xsl:variable name="i18n.validationError.requiredField">Det h�r f�ltet �r obligatoriskt</xsl:variable>
	<xsl:variable name="i18n.validationError.invalidFormat">Inneh�llet i det h�r f�ltet har felaktigt format</xsl:variable>
	<xsl:variable name="i18n.validationError.tooShort">Inneh�llet i det h�r f�ltet �r f�r kort</xsl:variable>
	<xsl:variable name="i18n.validationError.tooLong">Inneh�llet i det h�r f�ltet �r f�r l�ngt</xsl:variable>
	<xsl:variable name="i18n.validationError.unknownError">Det uppstod ett ok�nt valideringsfel kopplat till det h�r f�ltet</xsl:variable>
	<xsl:variable name="i18n.SortIndex">Sorteringsindex</xsl:variable>
</xsl:stylesheet>
