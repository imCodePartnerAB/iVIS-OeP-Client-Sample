<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="classpath://com/nordicpeak/flowengine/queries/common/xsl/QueryAdminCommon.sv.xsl"/>
	<xsl:include href="FileUploadQueryAdminTemplates.xsl"/>
	
	<xsl:variable name="java.queryTypeName">Filuppladdningsfr�ga</xsl:variable>
	
	<xsl:variable name="java.pdfAttachmentDescriptionPrefix">En fil fr�n fr�ga:</xsl:variable>
	
	<xsl:variable name="i18n.FileUploadQueryNotFound">Den beg�rda fr�gan hittades inte!</xsl:variable>
	<xsl:variable name="i18n.AllowedFileExtensions">Till�tna filtyper (ange en filtyp per rad)</xsl:variable>
	<xsl:variable name="i18n.MaxFileCount">Antal filer som f�r bifogas</xsl:variable>
	<xsl:variable name="i18n.MaxFileSize">St�rsta datam�ngd som f�r bifogas i MB</xsl:variable>
	<xsl:variable name="i18n.MaxAllowedFileSize.Part1">som standard</xsl:variable>
	<xsl:variable name="i18n.MaxAllowedFileSize.Part2">MB</xsl:variable>
	<xsl:variable name="i18n.maxFileCount">antal filer som f�r bifogas</xsl:variable>
	<xsl:variable name="i18n.maxFileSize">st�rsta datam�ngd som f�r bifogas i MB</xsl:variable>
	
	
</xsl:stylesheet>
