<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="AuthifySigningProviderTemplates.xsl"/>
	
	<xsl:variable name="java.signingMessage">Du signerar nu $flow.name med �rendenummer $flowInstance.flowInstanceID. Ans�kan har f�ljande unika nyckel: $hash</xsl:variable>
	
	<xsl:variable name="i18n.SigningHeader">V�lj n�gon av f�ljande e-legitimationer att signera med</xsl:variable>
	<xsl:variable name="i18n.BankID">BankID</xsl:variable>
	<xsl:variable name="i18n.MobileBankID">Mobilt BankID</xsl:variable>
	<xsl:variable name="i18n.Nordea">Nordea</xsl:variable>
	<xsl:variable name="i18n.Telia">Telia</xsl:variable>
	
	<xsl:variable name="i18n.SigningFailedTitle">Signeringen misslyckades</xsl:variable>
	<xsl:variable name="i18n.SigningFailed">Ett fel intr�ffade d� du skulle signera ans�kan, f�rs�k igen!</xsl:variable>
	<xsl:variable name="i18n.SSNNotMatching">Den e-legitimation du loggade in med �r inte samma som du signerade med, f�rs�k igen!</xsl:variable>
	<xsl:variable name="i18n.UnknownValidationError">Ett ok�nt fel intr�ffade vid signeringen</xsl:variable>
	
</xsl:stylesheet>
