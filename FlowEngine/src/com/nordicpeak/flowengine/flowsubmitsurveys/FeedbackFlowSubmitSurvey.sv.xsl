<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="FeedbackFlowSubmitSurveyTemplates.xsl"/>
	
	<xsl:variable name="java.chartDataTitle">Hur n�jd �r du med din upplevelse av tj�nsten?</xsl:variable>
	
	<xsl:variable name="i18n.FeedbackSurveyTitle">Hur n�jd �r du med din upplevelse av tj�nsten</xsl:variable>
	<xsl:variable name="i18n.VeryDissatisfied">Mycket missn�jd</xsl:variable>
	<xsl:variable name="i18n.Dissatisfied">Missn�jd</xsl:variable>
	<xsl:variable name="i18n.Neither">Varken eller</xsl:variable>
	<xsl:variable name="i18n.Satisfied">N�jd</xsl:variable>
	<xsl:variable name="i18n.VerySatisfied">Mycket n�jd</xsl:variable>
	<xsl:variable name="i18n.Unkown">Ok�nt</xsl:variable>
	<xsl:variable name="i18n.LeaveComment">L�mna en kommentar</xsl:variable>
	<xsl:variable name="i18n.Comment">Kommentar</xsl:variable>
	<xsl:variable name="i18n.Send">Skicka</xsl:variable>
	<xsl:variable name="i18n.CommentPlaceHolder">L�mna g�rna en kommentar till ditt betyg</xsl:variable>
	<xsl:variable name="i18n.FeedbackSurveySuccess">Ditt betyg �r registrerat, tack f�r din medverkan</xsl:variable>
	<xsl:variable name="i18n.NoAnswer">Du m�ste v�lja ett betyg f�r att kunna skicka in</xsl:variable>
	<xsl:variable name="i18n.ShowComments">Visa betyg med kommentarer</xsl:variable>
	<xsl:variable name="i18n.HideComments">D�lj betyg med kommentarer</xsl:variable>
	<xsl:variable name="i18n.Answer">Betyg</xsl:variable>
	
	<xsl:variable name="i18n.NoFlowFeedbackSurveys">Den h�r versionen av e-tj�nsten har �nnu inga betyg</xsl:variable>

	<xsl:variable name="i18n.validationError.RequiredField">Du m�ste fylla i f�ltet</xsl:variable>
	<xsl:variable name="i18n.validationError.InvalidFormat">Felaktigt format p� f�ltet</xsl:variable>
	<xsl:variable name="i18n.validationError.TooLong">F�r l�ngt v�rde p� f�ltet</xsl:variable>
	<xsl:variable name="i18n.validationError.TooShort">F�r kort v�rde p� f�ltet</xsl:variable>
	<xsl:variable name="i18n.validationError.Other">Ett ok�nt fel</xsl:variable>
	<xsl:variable name="i18n.validationError.unknownValidationErrorType">Ett ok�nt fel har uppst�tt</xsl:variable>
	<xsl:variable name="i18n.validationError.unknownMessageKey">Ett ok�nt fel har uppst�tt</xsl:variable>

</xsl:stylesheet>
