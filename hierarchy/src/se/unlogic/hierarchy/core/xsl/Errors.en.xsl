<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="ErrorTemplates.xsl"/>
	
	<!-- Naming template.mode.field.type -->
	
	<xsl:variable name="i18n.errors.footer" select="'Contact the administrator for more information'"/>

	<xsl:variable name="i18n.separateTransformationFailed.header" select="'An error has occurred'"/>
	<xsl:variable name="i18n.separateTransformationFailed.text1" select="'Transformation of the response from the module'"/>
	<xsl:variable name="i18n.separateTransformationFailed.text2" select="'failed.'"/>
	<xsl:variable name="i18n.separateTransformationFailed.text3" select="'The error that occured was:'"/>
	
	<xsl:variable name="i18n.separateTransformationWithoutStylesheet.header" select="'An error has occurred'"/>
	<xsl:variable name="i18n.separateTransformationWithoutStylesheet.text" select="'No XSLT stylesheet was found for the module'"/>
	
	<xsl:variable name="i18n.invalidModuleResonse.header" select="'An error has occurred'"/>
	<xsl:variable name="i18n.invalidModuleResonse.text1" select="'The module'"/>
	<xsl:variable name="i18n.invalidModuleResonse.text2" select="'returned an invalid response.'"/>
	
	<xsl:variable name="i18n.noModuleResponse.header" select="'The page did not generate a response'"/>
	<xsl:variable name="i18n.noModuleResponse.text1" select="'The page with the address'"/>
	<xsl:variable name="i18n.noModuleResponse.text2" select="'did not generate a response.'"/>
	
	<xsl:variable name="i18n.URINotFoundException.header" select="'Page not found'"/>
	<xsl:variable name="i18n.URINotFoundException.text1" select="'The page with the adress'"/>
	<xsl:variable name="i18n.URINotFoundException.text2" select="'was not found.'"/>
	
	<xsl:variable name="i18n.unhandledModuleException.header" select="'An error has occurred'"/>
	<xsl:variable name="i18n.unhandledModuleException.text1" select="'An error has occurred in the module'"/>
	<xsl:variable name="i18n.unhandledModuleException.text2" select="'The error that occured was:'"/>
	
	<xsl:variable name="i18n.accessDeniedException.header" select="'Access denied!'"/>
	<xsl:variable name="i18n.accessDeniedException.text" select="'You do not have access to this page.'"/>
	
	<xsl:variable name="i18n.ModuleConfigurationException.header" select="'Invalid configuration!'"/>
	<xsl:variable name="i18n.ModuleConfigurationException.text" select="'The requested module is not properly configured.'"/>	
	
	<xsl:variable name="i18n.ProtocolRedirectException.header" select="'An error has occurred'"/>
	<xsl:variable name="i18n.ProtocolRedirectException.text" select="'Unable to redirect you to the correct protocol'"/>		
	
	<xsl:variable name="i18n.SectionDefaultURINotSetException.header">First page not available</xsl:variable>
	<xsl:variable name="i18n.SectionDefaultURINotSetException.text">This section has no first page set.</xsl:variable>
	
	<xsl:variable name="i18n.SectionDefaultURINotFoundException.header">First page not found</xsl:variable>
	<xsl:variable name="i18n.SectionDefaultURINotFoundException.text">The first page in this section was not found.</xsl:variable>
</xsl:stylesheet>
