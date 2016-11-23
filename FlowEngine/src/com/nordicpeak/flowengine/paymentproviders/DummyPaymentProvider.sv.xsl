<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="DummyPaymentProviderTemplates.xsl"/>
	
	<xsl:variable name="i18n.PaymentFailedTitle">Betalning misslyckades</xsl:variable>
	<xsl:variable name="i18n.PaymentFailed">Ett fel intr�ffade d� du skulle betala ans�kan, f�rs�k igen!</xsl:variable>
	
	<xsl:variable name="i18n.Payment">Betalning</xsl:variable>
	<xsl:variable name="i18n.PaymentDescription">Nedan ser du en sammanst�llning p� kostnaderna som din ans�kan medf�r</xsl:variable>
	<xsl:variable name="i18n.ChoosePayment">V�lj hur du vill betala</xsl:variable>
	
	<xsl:variable name="i18n.CreditCard">Kreditkort</xsl:variable>
	<xsl:variable name="i18n.Invoice">Faktura</xsl:variable>
	<xsl:variable name="i18n.Visa">Visa</xsl:variable>
	<xsl:variable name="i18n.MasterCard">Mastercard</xsl:variable>
	
	<xsl:variable name="i18n.Description">Beskrivning</xsl:variable>
	<xsl:variable name="i18n.Quantity">Antal</xsl:variable>
	<xsl:variable name="i18n.UnitPrice">Pris</xsl:variable>
	<xsl:variable name="i18n.Amount">Belopp</xsl:variable>
	<xsl:variable name="i18n.Total">Totalt</xsl:variable>
	<xsl:variable name="i18n.TotalSum">SUMMA ATT BETALA</xsl:variable>
	
	<xsl:variable name="i18n.UnknownValidationError">Ett ok�nt valideringsfel har uppst�tt!</xsl:variable>
	
</xsl:stylesheet>
