<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	
	<xsl:import href="BaseFlowModule.sv.xsl"/>
	
	<xsl:import href="BaseFlowBrowserModuleTemplates.xsl"/>
	
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>
	
	<xsl:variable name="i18n.FlowDisabled">Den h�r e-tj�nsten �r f�r tillf�llet inte tillg�nglig</xsl:variable>
	<xsl:variable name="i18n.FlowNoLongerAvailable">E-tj�nsten �r inte l�ngre tillg�nglig, kontakta administrat�ren f�r mer information.</xsl:variable>
	<xsl:variable name="i18n.FlowNoLongerPublished">E-tj�nsten �r inte l�ngre publicerad i systemet, kontakta administrat�ren f�r mer information.</xsl:variable>
	
	<xsl:variable name="i18n.RequestedFlowNotFound">Den beg�rda e-tj�nsten hittades inte.</xsl:variable>
	<xsl:variable name="i18n.RequestedFlowInstanceNotFound">Den beg�rda ans�kan hittades inte.</xsl:variable>
	<xsl:variable name="i18n.InvalidLinkRequested">Den beg�rda e-tj�nsten hittades inte.</xsl:variable>		
	
	<xsl:variable name="i18n.FlowInstanceNoLongerAvailable">Den h�r e-tj�nsten �r inte tillg�nglig l�gre.</xsl:variable>
	<xsl:variable name="i18n.ErrorGettingFlowInstanceManager">Ett fel uppstod n�r ans�kan skulle �ppnas.</xsl:variable>
	<xsl:variable name="i18n.FlowInstanceErrorDataSaved">Ett fel uppstod i e-tj�nsten, din ans�kan har sparats.</xsl:variable>
	<xsl:variable name="i18n.FlowInstanceErrorDataNotSaved">Ett fel uppstod i e-tj�nsten.</xsl:variable>
	<xsl:variable name="i18n.FlowInstanceManagerClosed">Den h�r kopian p� ans�kan har st�ngts.</xsl:variable>
	
	<xsl:variable name="i18n.CancelFlowInstanceConfirm">�r du s�ker p� att du vill avbryta �rendet</xsl:variable>
	<xsl:variable name="i18n.DeleteFlowInstanceConfirm">�r du s�ker p� att du vill ta bort �rendet</xsl:variable>
	
	<xsl:variable name="i18n.overview">�rende�versikt</xsl:variable>
	<xsl:variable name="i18n.showInstance">Visa �rende</xsl:variable>
	<xsl:variable name="i18n.updateInstance">�ndra �rende</xsl:variable>
	<xsl:variable name="i18n.cancelInstance">Avbryt �rende</xsl:variable>
	
	<xsl:variable name="i18n.updateStatus">�ndra status</xsl:variable>
	<xsl:variable name="i18n.updateManagers">�ndra handl�ggare</xsl:variable>
	<xsl:variable name="i18n.deleteInstance">Ta bort �rende</xsl:variable>
	<xsl:variable name="i18n.bookmarkInstance">Flagga �rende</xsl:variable>
</xsl:stylesheet>
