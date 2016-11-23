<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>
	
	<xsl:include href="SiteProfilesAdminModuleTemplates.xsl"/>
	
	<xsl:variable name="i18n.Profiles">Profiler</xsl:variable>
	<xsl:variable name="i18n.NoProfilesFound">Inga profiler hittades</xsl:variable>
	<xsl:variable name="i18n.Name">Namn</xsl:variable>
	<xsl:variable name="i18n.Design">Design</xsl:variable>
	<xsl:variable name="i18n.Domains">Dom�ner</xsl:variable>
	<xsl:variable name="i18n.AddProfile">L�gg till profil</xsl:variable>
	<xsl:variable name="i18n.UpdateProfile">Uppdatera profilen</xsl:variable>
	<xsl:variable name="i18n.DeleteProfile">Ta bort profilen</xsl:variable>
	<xsl:variable name="i18n.Add">L�gg till</xsl:variable>
	<xsl:variable name="i18n.SaveChanges">Spara �ndringar</xsl:variable>
	<xsl:variable name="i18n.ChooseDesign">V�lj design</xsl:variable>
	<xsl:variable name="i18n.ValidationError.DomainAlreadyInUse.part1">Dom�nen </xsl:variable>
	<xsl:variable name="i18n.ValidationError.DomainAlreadyInUse.part2"> anv�nds redan av profilen </xsl:variable>
	<xsl:variable name="i18n.ValidationError.DomainAlreadyInUse.part3">.</xsl:variable>
	<xsl:variable name="i18n.UpdateGlobalSettings">Uppdatera globala inst�llningar</xsl:variable>
	<xsl:variable name="i18n.ProfileSettings">Modul inst�llningar</xsl:variable>
	
	
	<xsl:variable name="i18n.ValidationError.RequiredField">Du m�ste fylla i f�ltet</xsl:variable>
	<xsl:variable name="i18n.ValidationError.InvalidFormat">Felaktigt format p� f�ltet</xsl:variable>
	<xsl:variable name="i18n.ValidationError.TooLong">F�r l�ngt v�rde p� f�ltet</xsl:variable>
	<xsl:variable name="i18n.ValidationError.TooShort">F�r kort v�rde p� f�ltet</xsl:variable>
	<xsl:variable name="i18n.ValidationError.UnknownValidationErrorType">Ett ok�nt fel har uppst�tt</xsl:variable>
	<xsl:variable name="i18n.ValidationError.UnknownFault">Ett ok�nt fel har uppst�tt</xsl:variable>	
	
	<xsl:variable name="i18n.NoSettingsFound">Inga inst�llningar hittades</xsl:variable>
</xsl:stylesheet>
