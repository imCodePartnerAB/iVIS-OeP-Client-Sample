<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="classpath://com/nordicpeak/flowengine/queries/common/xsl/QueryCommon.sv.xsl"/>
	<xsl:include href="classpath://com/nordicpeak/flowengine/queries/basemapquery/BaseMapQueryCommon.sv.xsl"/>
	<xsl:include href="MultiGeometryMapQueryTemplates.xsl"/>
	
	<xsl:variable name="i18n.CantContactMapServer">Kartan kunde inte laddas. Ingen kontakt med kartservern, kontakta administrat�ren</xsl:variable>
	
	<xsl:variable name="i18n.PropertyUnitDesignation">Fastighetsbeteckning</xsl:variable>
	<xsl:variable name="i18n.Coordinates">Koordinater</xsl:variable>

	<xsl:variable name="i18n.GeometryNotValid">Geometrin du ritat ut �r inte giltig. F�rs�k igen.</xsl:variable>
	<xsl:variable name="i18n.RetrievingPUD">H�mtar fastighet...</xsl:variable>
	<xsl:variable name="i18n.ZoomScaleButton">Ta mig till r�tt zoomniv�</xsl:variable>
	<xsl:variable name="i18n.PUDInfoDialogText.Part1">Du har valt fastighet</xsl:variable>
	<xsl:variable name="i18n.PUDInfoDialogText.Part2">Klicka p� Bekr�fta om fastigheten �r korrekt och vill g� vidare. Klicka p� Avbryt f�r att v�lja en ny fastighet.</xsl:variable>
	<xsl:variable name="i18n.Done">Bekr�fta</xsl:variable>
	<xsl:variable name="i18n.Cancel">Avbryt</xsl:variable>
	
	<xsl:variable name="i18n.SearchToolDescription">S�k via fastighet, adress eller ort</xsl:variable>
	<xsl:variable name="i18n.SearchCoordinateToolDescription">S�k via koordinat</xsl:variable>
	
	<xsl:variable name="i18n.PUDRequired">Du m�ste s�tta ut p� kartan vilken fastighet ans�kan avser.</xsl:variable>
	
	<xsl:variable name="i18n.UnknownValidationError">Ett ok�nt valideringsfel har uppst�tt.</xsl:variable>
	<xsl:variable name="i18n.UnkownErrorMessageTitle">Ov�ntat fel</xsl:variable>
	<xsl:variable name="i18n.UnkownErrorMessage">Ett ov�ntat fel intr�ffade n�r fastigheten skulle h�mtas. Kontakta administrat�ren.</xsl:variable>
	<xsl:variable name="i18n.NoPUDFoundMessageTitle">Kan inte hitta n�gon fastighet</xsl:variable>
	<xsl:variable name="i18n.NoPUDFoundMessage">Det g�r inte att hitta n�gon fastighet p� den angivna positionen.</xsl:variable>

</xsl:stylesheet>
