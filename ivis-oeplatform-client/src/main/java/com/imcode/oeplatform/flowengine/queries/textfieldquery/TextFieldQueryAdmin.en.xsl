<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="classpath://com/nordicpeak/flowengine/queries/common/xsl/QueryAdminCommon.sv.xsl"/>
	<xsl:include href="TextFieldQueryAdminTemplates.xsl"/>
	
	<xsl:variable name="java.queryTypeName">iVIS text field query</xsl:variable>
	<xsl:variable name="java.fieldLayoutNewLine">One column</xsl:variable>
	<xsl:variable name="java.fieldLayoutFloat">Two columns</xsl:variable>
	
	<xsl:variable name="i18n.BaseInfo">Basic Information</xsl:variable>
	<xsl:variable name="i18n.Layout">Layout</xsl:variable>
	
	<xsl:variable name="i18n.TextFieldQueryNotFound">The required question was not found!</xsl:variable>
	<xsl:variable name="i18n.TextFields">Text fields</xsl:variable>
	<xsl:variable name="i18n.AddTextField">Add text fields</xsl:variable>
	<xsl:variable name="i18n.UpdateTextField">Update text fields</xsl:variable>
	<xsl:variable name="i18n.UpdateBaseInformation">Update basic Information</xsl:variable>
	<xsl:variable name="i18n.Add">Add</xsl:variable>
	<xsl:variable name="i18n.Done">Save</xsl:variable>
	<xsl:variable name="i18n.SortTextFields.Title">Sort text field</xsl:variable>
	<xsl:variable name="i18n.Label">Label</xsl:variable>
	<xsl:variable name="i18n.label">label</xsl:variable>
	<xsl:variable name="i18n.Width">Width</xsl:variable>
	<xsl:variable name="i18n.width">width</xsl:variable>
	<xsl:variable name="i18n.Required">Mandatory</xsl:variable>
	<xsl:variable name="i18n.MaxLength">Permissible length of text content</xsl:variable>
	<xsl:variable name="i18n.maxLength">allowed length of text content</xsl:variable>
	<xsl:variable name="i18n.FormatValidator">Validator</xsl:variable>
	<xsl:variable name="i18n.InvalidFormatMessage">Validation Message</xsl:variable>
	<xsl:variable name="i18n.invalidFormatMessage">validation message</xsl:variable>
	<xsl:variable name="i18n.SortTextFields">Sort text field issue</xsl:variable>
	<xsl:variable name="i18n.DependsOn">Depends on</xsl:variable>
	<xsl:variable name="i18n.DependencySourceName">Dependency source</xsl:variable>
	<xsl:variable name="i18n.DependencyFieldName">Dependency field</xsl:variable>

	<xsl:variable name="i18n.DeleteTextField.Confirm">Remove the text field</xsl:variable>
	<xsl:variable name="i18n.DeleteTextField">Remove the text field</xsl:variable>

	<!--<xsl:variable name="i18n.xsdElementName">XML-name</xsl:variable>-->
	<!--<xsl:variable name="i18n.exportQuery">Export question</xsl:variable>-->

</xsl:stylesheet>
