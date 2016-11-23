<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1" />

	<xsl:include href="PageViewModuleTemplates.xsl" />
	
	<!-- Naming template.mode.field.type -->
	
	<xsl:variable name="pageAdminModule.addlinktitle" select="'L�gg till en ny sida i den h�r sektionen'" />
	<xsl:variable name="pageAdminModule.movelinktitle" select="'Flytta sidan:'" />
	<xsl:variable name="pageAdminModule.copylinktitle" select="'Kopiera sidan:'" />
	<xsl:variable name="pageAdminModule.updatelinktitle" select="'Redigera sidan:'" />
	<xsl:variable name="pageAdminModule.deletepagetitle" select="'Ta bort sidan:'" />
	<xsl:variable name="pageAdminModule.deletepagepopup" select="'Ta bort sidan?'" />
	<xsl:variable name="pageAdminModule.setFirstpage" select="'G�r den h�r sidan till f�rstasida i den h�r sektionen'" />

</xsl:stylesheet>