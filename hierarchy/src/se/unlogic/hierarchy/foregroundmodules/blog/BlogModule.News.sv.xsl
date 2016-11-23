<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	
	<xsl:import href="BlogModuleTemplates.xsl"/>
	
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1" />

	<xsl:include href="BlogModuleTemplates.News.xsl" />
	<xsl:include href="BlogModule.Common.sv.xsl" />
	
	<xsl:variable name="tagBundleMenuitemDescription">Visa alla nyheter märkta med taggen </xsl:variable>
	<xsl:variable name="archiveBundleDescription">Nyhetsarkiv</xsl:variable>
	<xsl:variable name="archiveBundleMenuitemDescription">Visa alla nyheter från </xsl:variable>
	<xsl:variable name="addBlogPostBreadcrumbText">Lägg till nyhet</xsl:variable>
	<xsl:variable name="updateBlogPostBreadcrumbText">"Redigera nyhet </xsl:variable>
	
	<xsl:variable name="BlogPost.olderNews">Äldre nyheter</xsl:variable>
	<xsl:variable name="BlogPost.newerNews">Nyare nyheter</xsl:variable>
	
	
</xsl:stylesheet>