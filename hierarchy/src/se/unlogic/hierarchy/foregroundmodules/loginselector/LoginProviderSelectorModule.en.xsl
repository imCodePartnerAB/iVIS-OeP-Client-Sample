<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="LoginProviderSelectorModuleTemplates.xsl"/>
	
	<xsl:variable name="i18n.NoLoginProviderAvailable.Title">Login</xsl:variable>
	<xsl:variable name="i18n.NoLoginProviderAvailable.Message">There are currently no login methods available.</xsl:variable>
	
	<xsl:variable name="i18n.ConfigureModule">Configure login providers</xsl:variable>
	<xsl:variable name="i18n.Configure.Title">Settings</xsl:variable>
	<xsl:variable name="i18n.SaveChanges">Save changes</xsl:variable>
	<xsl:variable name="i18n.NoLoginProvidersFoundInLoginHandler">No login providers found in login handler</xsl:variable>
	<xsl:variable name="i18n.Description">Description</xsl:variable>
	<xsl:variable name="i18n.ButtonText">Button text</xsl:variable>
	
	<xsl:variable name="i18n.ValidationErrorsPresent">There is one or more validation errors in the form below.</xsl:variable>
	
	<xsl:variable name="i18n.validationError.requiredField">This field is required</xsl:variable>
	<xsl:variable name="i18n.validationError.invalidFormat">The content in this field has an invalid format</xsl:variable>
	<xsl:variable name="i18n.validationError.tooShort">The content in this field is too short</xsl:variable>
	<xsl:variable name="i18n.validationError.tooLong">The content in this field is too long</xsl:variable>
	<xsl:variable name="i18n.validationError.unknownError">There is an unknown validation error with this field</xsl:variable>
	<xsl:variable name="i18n.SortIndex">Sort index</xsl:variable>
</xsl:stylesheet>
