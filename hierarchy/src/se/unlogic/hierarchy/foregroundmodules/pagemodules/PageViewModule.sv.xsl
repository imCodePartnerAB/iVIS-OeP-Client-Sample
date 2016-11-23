<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1" />

	<xsl:include href="PageViewModuleTemplates.xsl" />
	
	<!-- Naming template.mode.field.type -->
	
	<xsl:variable name="pageAdminModule.addlinktitle" select="'Lägg till en ny sida i den här sektionen'" />
	<xsl:variable name="pageAdminModule.movelinktitle" select="'Flytta sidan:'" />
	<xsl:variable name="pageAdminModule.copylinktitle" select="'Kopiera sidan:'" />
	<xsl:variable name="pageAdminModule.updatelinktitle" select="'Redigera sidan:'" />
	<xsl:variable name="pageAdminModule.deletepagetitle" select="'Ta bort sidan:'" />
	<xsl:variable name="pageAdminModule.deletepagepopup" select="'Ta bort sidan?'" />
	<xsl:variable name="pageAdminModule.setFirstpage" select="'Gör den här sidan till förstasida i den här sektionen'" />

</xsl:stylesheet>