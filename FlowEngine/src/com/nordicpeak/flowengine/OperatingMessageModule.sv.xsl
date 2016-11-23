<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="OperatingMessageModuleTemplates.xsl"/>
	
	<xsl:variable name="i18n.AddOperatingMessage">L�gg till driftmeddelande</xsl:variable>
	<xsl:variable name="i18n.UpdateOperatingMessage">�ndra driftmeddelande</xsl:variable>
	<xsl:variable name="i18n.Message">Meddelande</xsl:variable>
	<xsl:variable name="i18n.Publish">Publiceras</xsl:variable>
	<xsl:variable name="i18n.UnPublish">Avpubliceras</xsl:variable>
	<xsl:variable name="i18n.StartDate">Datum fr�n</xsl:variable>
	<xsl:variable name="i18n.EndDate">Datum till</xsl:variable>
	<xsl:variable name="i18n.StartTime">Tid fr�n</xsl:variable>
	<xsl:variable name="i18n.EndTime">Tid till</xsl:variable>
	<xsl:variable name="i18n.FlowFamilies">Antal e-tj�nster</xsl:variable>
	<xsl:variable name="i18n.DisableFlows">Inaktivera e-tj�nst</xsl:variable>
	<xsl:variable name="i18n.NoOperatingMessagesFound">Inga driftmeddelanden hittades</xsl:variable>
	<xsl:variable name="i18n.All">Alla</xsl:variable>
	<xsl:variable name="i18n.Yes">Ja</xsl:variable>
	<xsl:variable name="i18n.No">Nej</xsl:variable>
	<xsl:variable name="i18n.DeleteOperatingMessageConfirm">�r du s�ker p� att du vill ta bort driftmeddelandet</xsl:variable>
	<xsl:variable name="i18n.DeleteOperatingMessageTitle">Ta bort driftmeddelande</xsl:variable>
	
	<xsl:variable name="i18n.Submit">L�gg till</xsl:variable>
	<xsl:variable name="i18n.SaveChanges">Spara �ndringar</xsl:variable>
	
	<xsl:variable name="i18n.Validation.RequiredField" select="'Du m�ste fylla i f�ltet'"/>
	<xsl:variable name="i18n.Validation.InvalidFormat" select="'Felaktigt format p� f�ltet'"/>
	<xsl:variable name="i18n.Validation.TooShort" select="'F�r kort inneh�ll i f�ltet'"/>
		
	<xsl:variable name="i18n.Validation.UnknownError" select="'Ok�nt fel p� f�ltet'"/>
	<xsl:variable name="i18n.UnknownFault" select="'Ett ok�nt fel intr�ffade'"/>
	
	<xsl:variable name="i18n.Global">Visa driftmeddelandet f�r alla e-tj�nster</xsl:variable>
	<xsl:variable name="i18n.ChooseFlowFamilies">Visa driftmeddelandet f�r specifika e-tj�nster</xsl:variable>
	<xsl:variable name="i18n.DisableFlowsLabel">Inaktivera e-tj�nsterna under perioden f�r driftmeddelandet</xsl:variable>
	
	<xsl:variable name="i18n.message">meddelande</xsl:variable>
	<xsl:variable name="i18n.startDate">datum fr�n</xsl:variable>
	<xsl:variable name="i18n.startTime">tid fr�n</xsl:variable>
	<xsl:variable name="i18n.endTime">datum till</xsl:variable>
	<xsl:variable name="i18n.disabledFlows">inaktivera e-tj�nsterna</xsl:variable>
	<xsl:variable name="i18n.Validation.EndTimeBeforeStartTime">Slutdatum kan inte ligga f�re startdatum</xsl:variable>
	<xsl:variable name="i18n.Validation.DaysBetweenToSmall">Sluttid kan inte ligga f�re starttid</xsl:variable>
	<xsl:variable name="i18n.Validation.NoFlowFamilyChoosen">Du har inte valt n�gra e-tj�nster</xsl:variable>
	
	<xsl:variable name="i18n.Validation.TooLong.Part1">Inneh�llet i f�ltet </xsl:variable>
	<xsl:variable name="i18n.Validation.TooLong.Part2"> �r </xsl:variable>
	<xsl:variable name="i18n.Validation.TooLong.Part3"> tecken, vilket �verskrider maxgr�nsen p� </xsl:variable>
	<xsl:variable name="i18n.Validation.TooLong.Part4"> tecken</xsl:variable>
</xsl:stylesheet>
