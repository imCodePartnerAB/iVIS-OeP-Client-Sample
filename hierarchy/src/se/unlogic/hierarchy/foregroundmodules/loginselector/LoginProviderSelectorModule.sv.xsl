<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="LoginProviderSelectorModuleTemplates.xsl"/>
	
	<xsl:variable name="i18n.NoLoginProviderAvailable.Title">Inloggning</xsl:variable>
	<xsl:variable name="i18n.NoLoginProviderAvailable.Message">Det finns för närvarande inga inloggningsmetoder tillgängliga.</xsl:variable>

	<xsl:variable name="i18n.ConfigureModule">Välj inloggningsmetoder</xsl:variable>
	<xsl:variable name="i18n.Configure.Title">Inställningar</xsl:variable>
	<xsl:variable name="i18n.SaveChanges">Spara ändringar</xsl:variable>
	<xsl:variable name="i18n.NoLoginProvidersFoundInLoginHandler">Inga inloggningsmoduler hittades.</xsl:variable>
	<xsl:variable name="i18n.Description">Beskrivning</xsl:variable>
	<xsl:variable name="i18n.ButtonText">Text på knapp</xsl:variable>
	
	<xsl:variable name="i18n.ValidationErrorsPresent">Det finns ett eller flera valideringsfel i formuläret nedan</xsl:variable>
	
	<xsl:variable name="i18n.validationError.requiredField">Det här fältet är obligatoriskt</xsl:variable>
	<xsl:variable name="i18n.validationError.invalidFormat">Innehållet i det här fältet har felaktigt format</xsl:variable>
	<xsl:variable name="i18n.validationError.tooShort">Innehållet i det här fältet är för kort</xsl:variable>
	<xsl:variable name="i18n.validationError.tooLong">Innehållet i det här fältet är för långt</xsl:variable>
	<xsl:variable name="i18n.validationError.unknownError">Det uppstod ett okänt valideringsfel kopplat till det här fältet</xsl:variable>
	<xsl:variable name="i18n.SortIndex">Sorteringsindex</xsl:variable>
</xsl:stylesheet>
