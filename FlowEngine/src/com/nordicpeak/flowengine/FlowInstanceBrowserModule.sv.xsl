<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	
	<xsl:import href="BaseFlowBrowserModule.sv.xsl"/>
	
	<xsl:include href="FlowInstanceBrowserModuleTemplates.xsl"/>
	
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>
	
	<xsl:variable name="i18n.ShowFlow">Ans�k</xsl:variable>
	<xsl:variable name="i18n.UncategorizedFlows">�vriga e-tj�nster</xsl:variable>
	<xsl:variable name="i18n.ShowLongDescription">Visa mer information</xsl:variable>
	<xsl:variable name="i18n.HideLongDescription">D�lj information</xsl:variable>
	<xsl:variable name="i18n.FlowDisabled">Ej tillg�nglig p� grund av underh�llsarbete</xsl:variable>
	<xsl:variable name="i18n.NoFlowsFound">Inga publicerade e-tj�nster hittades.</xsl:variable>
	
	<xsl:variable name="i18n.SearchHints">T.ex. bygglov hus, dagisplats, l�mna synpunkt</xsl:variable>
	<xsl:variable name="i18n.SearchTitle">S�k e-tj�nst</xsl:variable>
	<xsl:variable name="i18n.RecommendedSearches">Rekommenderade s�kningar</xsl:variable>
	<xsl:variable name="i18n.SearchDone">S�kningen �r klar</xsl:variable>
	<xsl:variable name="i18n.close">st�ng</xsl:variable>
	<xsl:variable name="i18n.Hits.Part1">gav</xsl:variable>
	<xsl:variable name="i18n.Hits.Part2">tr�ffar</xsl:variable>
	<xsl:variable name="i18n.MostPopular">Popul�rast</xsl:variable>
	<xsl:variable name="i18n.Uncategorized">�vriga</xsl:variable>
	
	<xsl:variable name="i18n.FlowTypeFilter">Filtrera</xsl:variable>
	<xsl:variable name="i18n.ShowAll">Visa alla</xsl:variable>
	
	<xsl:variable name="i18n.AuthenticationRequired">Denna e-tj�nst kr�ver inloggning.</xsl:variable>
	<xsl:variable name="i18n.Description">Beskrivning</xsl:variable>
	<xsl:variable name="i18n.ChecklistTitle">F�ljande beh�vs f�r e-tj�nsten</xsl:variable>
	<xsl:variable name="i18n.StartFlow">Starta e-tj�nsten</xsl:variable>
	<xsl:variable name="i18n.StepDescriptionTitle">Du kommer g� igenom f�ljande steg</xsl:variable>
	
	<xsl:variable name="i18n.MostUsedFLows">Mest anv�nda e-tj�nsterna</xsl:variable>
	<xsl:variable name="i18n.AllFlows">Alla e-tj�nster</xsl:variable>
	
	<xsl:variable name="i18n.Questions">Fr�gor om e-tj�nsten</xsl:variable>
	<xsl:variable name="i18n.Responsible">Personuppgiftsansvarig</xsl:variable>
	<xsl:variable name="i18n.SendMailTo">Skicka mail till</xsl:variable>
	
	<xsl:variable name="i18n.SortFlowTypes">Sortera per kategori</xsl:variable>
	
</xsl:stylesheet>
