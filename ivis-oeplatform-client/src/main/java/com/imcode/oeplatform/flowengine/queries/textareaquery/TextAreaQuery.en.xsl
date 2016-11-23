<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="classpath://com/nordicpeak/flowengine/queries/common/xsl/QueryCommon.sv.xsl"/>
	<xsl:include href="TextAreaQueryTemplates.xsl"/>
	
	<xsl:variable name="i18n.RequiredField">This field is mandatory!</xsl:variable>
	<xsl:variable name="i18n.TooLongFieldContent.part1">The content of this field is </xsl:variable>
	<xsl:variable name="i18n.TooLongFieldContent.part2"> characters which exceed the maximum limit of </xsl:variable>
	<xsl:variable name="i18n.TooLongFieldContent.part3"> character</xsl:variable>
	<xsl:variable name="i18n.UnknownValidationError">An unknown validation errors have occurred:</xsl:variable>
</xsl:stylesheet>
