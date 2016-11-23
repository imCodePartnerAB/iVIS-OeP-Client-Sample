<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>
	
	<xsl:include href="QueryAdminCommonTemplates.xsl"/>
	
	<xsl:variable name="i18n.UpdateQuery">Uppdatera fr�ga</xsl:variable>
	
	<xsl:variable name="i18n.Name">Namn</xsl:variable>
	<xsl:variable name="i18n.name">namn</xsl:variable>
	<xsl:variable name="i18n.Description">Beskrivning</xsl:variable>
	<xsl:variable name="i18n.description">beskrivning</xsl:variable>
	<xsl:variable name="i18n.HelpText">Hj�lptext</xsl:variable>
	<xsl:variable name="i18n.helpText">Hj�lptext</xsl:variable>
	<xsl:variable name="i18n.MoveAlternative">Flytta alternativ</xsl:variable>
	<xsl:variable name="i18n.DeleteAlternative">�r du s�ker p� att du vill ta bort alternativet</xsl:variable>
	<xsl:variable name="i18n.AddAlternative">L�gg till alternativ</xsl:variable>
	<xsl:variable name="i18n.Alternative">Alternativ</xsl:variable>
	<xsl:variable name="i18n.SaveChanges">Spara �ndringar</xsl:variable>
	
	<xsl:variable name="i18n.defaultQueryState">standardl�ge</xsl:variable>
	<xsl:variable name="i18n.DefaultQueryState.title">Standardl�ge</xsl:variable>
	
	<xsl:variable name="i18n.DefaultQueryState.description">V�lj vilket standardl�ge som fr�gan skall ha.</xsl:variable>
	<xsl:variable name="i18n.QueryState.VISIBLE">Valfri</xsl:variable>
	<xsl:variable name="i18n.QueryState.VISIBLE_REQUIRED">Obligatorisk</xsl:variable>
	<xsl:variable name="i18n.QueryState.HIDDEN">Dold</xsl:variable>
	
	<xsl:variable name="i18n.FreeTextAlternative">Fritextalternativ</xsl:variable>
	<xsl:variable name="i18n.FreeTextAlternativeDescription">V�lj om fr�gan skall ha ett fritextalternativ</xsl:variable>
	<xsl:variable name="i18n.freeTextAlternative">fritextalternativ</xsl:variable>
	
	<xsl:variable name="i18n.RequiredField">Du m�ste fylla i f�ltet</xsl:variable>
	<xsl:variable name="i18n.AlternativeRequired">Du m�ste fylla i ett namn f�r alternativ</xsl:variable>
	<xsl:variable name="i18n.ToFewAlternatives">Du m�ste skapa minst 2 alternativ f�r fr�gan!</xsl:variable>
	<xsl:variable name="i18n.UnknownValidationError">Ett ok�nt valideringsfel har uppst�tt!</xsl:variable>
	<xsl:variable name="i18n.TooLongFieldContent">F�r l�ngt inneh�ll i f�ltet</xsl:variable>
	<xsl:variable name="i18n.InvalidFormat">Ogiltigt format p� inneh�llet i f�ltet</xsl:variable>
	
	<xsl:variable name="i18n.exportQuery.title">Exportera fr�ga</xsl:variable>
	<xsl:variable name="i18n.exportQuery.description">V�lj om fr�gan skall inkluderas i den XML som skickas till andra system.</xsl:variable>
	<xsl:variable name="i18n.exportQuery">Exportera fr�gan</xsl:variable>
	<xsl:variable name="i18n.xsdElementName">XML-elementnamn</xsl:variable>
</xsl:stylesheet>
