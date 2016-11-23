<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1" />

	<xsl:include href="LogoutModuleTemplates.xsl" />
	
	<xsl:variable name="i18n.header" select="'Logged out'" />
	<xsl:variable name="i18n.text" select="'You have successfully been logged out.'" />
	
</xsl:stylesheet>