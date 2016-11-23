<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>
	
	<xsl:include href="SiteProfilesAdminModuleTemplates.xsl"/>
	
	<xsl:variable name="i18n.Profiles">Profiler</xsl:variable>
	<xsl:variable name="i18n.NoProfilesFound">Inga profiler hittades</xsl:variable>
	<xsl:variable name="i18n.Name">Namn</xsl:variable>
	<xsl:variable name="i18n.Design">Design</xsl:variable>
	<xsl:variable name="i18n.Domains">Domäner</xsl:variable>
	<xsl:variable name="i18n.AddProfile">Lägg till profil</xsl:variable>
	<xsl:variable name="i18n.UpdateProfile">Uppdatera profilen</xsl:variable>
	<xsl:variable name="i18n.DeleteProfile">Ta bort profilen</xsl:variable>
	<xsl:variable name="i18n.Add">Lägg till</xsl:variable>
	<xsl:variable name="i18n.SaveChanges">Spara ändringar</xsl:variable>
	<xsl:variable name="i18n.ChooseDesign">Välj design</xsl:variable>
	<xsl:variable name="i18n.ValidationError.DomainAlreadyInUse.part1">Domänen </xsl:variable>
	<xsl:variable name="i18n.ValidationError.DomainAlreadyInUse.part2"> används redan av profilen </xsl:variable>
	<xsl:variable name="i18n.ValidationError.DomainAlreadyInUse.part3">.</xsl:variable>
	<xsl:variable name="i18n.UpdateGlobalSettings">Uppdatera globala inställningar</xsl:variable>
	<xsl:variable name="i18n.ProfileSettings">Modul inställningar</xsl:variable>
	
	
	<xsl:variable name="i18n.ValidationError.RequiredField">Du måste fylla i fältet</xsl:variable>
	<xsl:variable name="i18n.ValidationError.InvalidFormat">Felaktigt format på fältet</xsl:variable>
	<xsl:variable name="i18n.ValidationError.TooLong">För långt värde på fältet</xsl:variable>
	<xsl:variable name="i18n.ValidationError.TooShort">För kort värde på fältet</xsl:variable>
	<xsl:variable name="i18n.ValidationError.UnknownValidationErrorType">Ett okänt fel har uppstått</xsl:variable>
	<xsl:variable name="i18n.ValidationError.UnknownFault">Ett okänt fel har uppstått</xsl:variable>	
	
	<xsl:variable name="i18n.NoSettingsFound">Inga inställningar hittades</xsl:variable>
</xsl:stylesheet>
