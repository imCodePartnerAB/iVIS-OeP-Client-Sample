<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>
	
	<xsl:include href="BaseMapQueryCommonTemplates.xsl"/>

	<xsl:variable name="java.startExtentSettingName">Start extent</xsl:variable>
	<xsl:variable name="java.startExtentSettingDescription">Start position for kartor (kommaspearerad lista med koordinater t.ex. 608114,6910996,641846,6932596) </xsl:variable>
	<xsl:variable name="java.lmUserSettingName">Anv�ndare lanm�teriet</xsl:variable>
	<xsl:variable name="java.lmUserSettingDescription">Anv�ndare som anv�nds f�r anrop mot lantm�teriet via Search LM</xsl:variable>
	<xsl:variable name="java.pdfAttachmentDescriptionPrefix">En fil fr�n fr�ga:</xsl:variable>
	<xsl:variable name="java.pdfAttachmentFilename">Kartbild 1 till $scale.png</xsl:variable>
	<xsl:variable name="java.searchPrefixSettingName">S�kprefix</xsl:variable>
	<xsl:variable name="java.searchPrefixSettingDescription">S�kprefix som anv�nds vid anrop mot lantm�teriet via Search LM</xsl:variable>

	<xsl:variable name="i18n.UnableToValidatePUD">Det gick inte att verifiera den angivna fastigheten, kontakta administrat�ren.</xsl:variable>
	<xsl:variable name="i18n.PUDNotValid">Den angivna fastigheten hittades inte hos lantm�teriet. F�rs�k igen.</xsl:variable>
	<xsl:variable name="i18n.InCompleteMapQuerySubmit">Det saknas information f�r den h�r fr�gan f�r att kunna g� vidare. F�rs�k igen.</xsl:variable>
	<xsl:variable name="i18n.UnableToGeneratePNG">Det gick inte att skapa kartbild. F�rs�k igen.</xsl:variable>
	
	<xsl:variable name="i18n.StartInstruction">Startmeddelande</xsl:variable>
	<xsl:variable name="i18n.startInstruction">startmeddelande</xsl:variable>
	<xsl:variable name="i18n.StartInstructionButton">B�rja</xsl:variable>
	<xsl:variable name="i18n.StartInstructionDescription">Meddelande som visas i dialogruta innan anv�ndaren b�rjat interagera med kartan</xsl:variable>
	
	<xsl:variable name="i18n.DimensionAndAngleSettings">M�tt- &amp; vinkelinst�llningar</xsl:variable>
	
</xsl:stylesheet>