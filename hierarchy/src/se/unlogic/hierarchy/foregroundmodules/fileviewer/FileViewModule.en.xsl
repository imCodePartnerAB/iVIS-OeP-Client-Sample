<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="FileViewModuleTemplates.xsl" />
	
	<!-- Naming template.mode.field.type -->
	
	<xsl:variable name="FileNotFound.errorMessage" select="'The specified file was not found, check module configuration!'" />
	<xsl:variable name="FileIsDirectory.errorMessage" select="'The specified file is a directory, check module configuration!'" />
	<xsl:variable name="UnableToAccessFile.errorMessage" select="'Unable to access the specified file, check module configuration!'" />
	<xsl:variable name="File.lines" select="'lines'" />

</xsl:stylesheet>