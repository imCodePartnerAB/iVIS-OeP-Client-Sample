<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="classpath://com/nordicpeak/flowengine/queries/common/xsl/QueryCommon.sv.xsl"/>
	<xsl:include href="PUDQueryTemplates.xsl"/>
	
	<xsl:variable name="i18n.CantContactSearchService">Ingen kontakt med s�ktj�nsten, kontakta administrat�ren</xsl:variable>

	<xsl:variable name="i18n.PUD">S�k via fastighet</xsl:variable>
	<xsl:variable name="i18n.Address">S�k via adress</xsl:variable>
	
	<xsl:variable name="i18n.PUDPlaceHolder">Skriv din fastighetsbeteckning h�r</xsl:variable>
	<xsl:variable name="i18n.AddressPlaceHolder">Skriv din adress h�r</xsl:variable>
	<xsl:variable name="i18n.SelectedPud">Vald fastighetsbeteckning</xsl:variable>
	<xsl:variable name="i18n.DeleteSelectedPUD">Ta bort vald fastighetsbeteckning</xsl:variable>

	<xsl:variable name="i18n.RequiredQuery">Du m�ste v�lja en korrekt fastighetsbeteckning.</xsl:variable>
	<xsl:variable name="i18n.UnknownValidationError">Ett ok�nt valideringsfel har uppst�tt.</xsl:variable>
	<xsl:variable name="i18n.PUDNotFound">Fastighetsbeteckningen f�r den valda adressen hittades inte. Kontakta administrat�ren.</xsl:variable>
	<xsl:variable name="i18n.ToManyPUDFound">Flera fastighetsbeteckningar hittades f�r den valda adressen. Kontakta administrat�ren.</xsl:variable>
	
	<xsl:variable name="i18n.ServiceErrorMessage">Det �r f�r tillf�llet problem med s�ktj�nsten. Kontakta administrat�ren.</xsl:variable>
	<xsl:variable name="i18n.UnkownErrorMessage">Ett ov�ntat fel har intr�ffat. Kontakta administrat�ren.</xsl:variable>
	<xsl:variable name="i18n.PUDNotValid">Den angivna fastigheten hittades inte hos lantm�teriet. F�rs�k igen.</xsl:variable>

</xsl:stylesheet>
