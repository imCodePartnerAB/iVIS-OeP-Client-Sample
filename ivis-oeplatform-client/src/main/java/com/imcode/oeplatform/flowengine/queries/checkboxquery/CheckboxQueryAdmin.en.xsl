<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="classpath://com/nordicpeak/flowengine/queries/common/xsl/QueryAdminCommon.sv.xsl"/>
	<xsl:include href="CheckboxQueryAdminTemplates.xsl"/>
	
	<xsl:variable name="java.queryTypeName">iVIS checkbox query</xsl:variable>
	<xsl:variable name="java.countText">Quantity</xsl:variable>
	<xsl:variable name="java.alternativesText">Alternative</xsl:variable>
	
	<xsl:variable name="i18n.CheckboxQueryNotFound">The required question was not found!</xsl:variable>
	<xsl:variable name="i18n.MinChecked">Min number of selected options</xsl:variable>
	<xsl:variable name="i18n.MaxChecked">Max number of selected options</xsl:variable>
	<xsl:variable name="i18n.minChecked">min number of selected options</xsl:variable>
	<xsl:variable name="i18n.maxChecked">max number of selected options</xsl:variable>
	<xsl:variable name="i18n.MinCheckedBiggerThanMaxChecked">Minimum number of selected options may not be greater than the max!</xsl:variable>
	<xsl:variable name="i18n.MaxCheckedToBig">Max chosen options may not exceed the number of options!</xsl:variable>
	<xsl:variable name="i18n.MinCheckedToBig">Minimum number of selected options can not exceed the number of options!</xsl:variable>
	
	<xsl:variable name="i18n.ToFewAlternatives1Min">You must create at least one option for the question!</xsl:variable>
	
</xsl:stylesheet>
