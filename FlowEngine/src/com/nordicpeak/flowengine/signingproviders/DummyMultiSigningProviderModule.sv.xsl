<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="DummyMultiSigningProviderModuleTemplates.xsl"/>
	
	<xsl:variable name="i18n.Name">Namn</xsl:variable>
	<xsl:variable name="i18n.SocialSecurityNumber">Personnummer</xsl:variable>
	<xsl:variable name="i18n.Email">E-post</xsl:variable>
	<xsl:variable name="i18n.Signature">Signerat</xsl:variable>
	<xsl:variable name="i18n.No">Nej</xsl:variable>
	<xsl:variable name="i18n.SigningStatus.Description">Ditt ärende väntar på att en eller flera personer ska signera det. I tabellen nedan ser du aktuell status gällande signeringen av detta ärende.</xsl:variable>
	
	<xsl:variable name="i18n.SignFlowInstance">Signering av ärende</xsl:variable>
	<xsl:variable name="i18n.AlreadySignedFlowInstanceMessage">Du skrev under detta ärende den</xsl:variable>
	<xsl:variable name="i18n.SignFlowInstanceMessage">har bett dig signera detta ärende.</xsl:variable>
	<xsl:variable name="i18n.DownloadFlowInstancePDF">Hämta ärendet i PDF format.</xsl:variable>
	<xsl:variable name="i18n.SigningLinkMessage">Ett e-post meddelande har skickats till samtliga personer i listan ovan. Om det inte mottagit detta meddelande eller du vill påminna dem så kan du be dem besöka adressen nedan för att signera detta ärende.</xsl:variable>
	<xsl:variable name="i18n.SignFlowInstanceButton">Signera ärende</xsl:variable>
</xsl:stylesheet>
