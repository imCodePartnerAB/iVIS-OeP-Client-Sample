<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="classpath://com/nordicpeak/flowengine/queries/common/xsl/QueryCommon.sv.xsl"/>
	<xsl:include href="LinkedDropDownQueryTemplates.xsl"/>

	<xsl:variable name="i18n.RequiredQuery">This question is mandatory!</xsl:variable>
	<xsl:variable name="i18n.UnknownValidationError">An unknown validation error has occurred!</xsl:variable>
</xsl:stylesheet>
