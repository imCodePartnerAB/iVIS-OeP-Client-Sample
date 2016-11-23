<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="RuntimeInfoModuleTemplates.xsl"/>
	
	<xsl:variable name="i18n.CPU">CPU</xsl:variable>
	<xsl:variable name="i18n.AvailableProcessors">Available processors</xsl:variable>
	<xsl:variable name="i18n.MB">MB</xsl:variable>
	<xsl:variable name="i18n.Init">Init</xsl:variable>
	<xsl:variable name="i18n.Used">Used</xsl:variable>
	<xsl:variable name="i18n.Committed">Committed</xsl:variable>
	<xsl:variable name="i18n.Max">Max</xsl:variable>
	<xsl:variable name="i18n.OS">Operating System</xsl:variable>
	<xsl:variable name="i18n.Name">Name</xsl:variable>
	<xsl:variable name="i18n.Version">Version</xsl:variable>
	<xsl:variable name="i18n.Arch">Architecture</xsl:variable>
	
	<xsl:variable name="i18n.ClassLoading">ClassLoading</xsl:variable>
	<xsl:variable name="i18n.LoadedClassCount">Loaded class count</xsl:variable>
	<xsl:variable name="i18n.UnloadedClassCount">Unloaded class count</xsl:variable>
	<xsl:variable name="i18n.TotalLoadedClassCount">Total loaded class count</xsl:variable>	

	<xsl:variable name="i18n.GarbageCollection">Garbage collection</xsl:variable>
	<xsl:variable name="i18n.RunGC">Run garbage collector</xsl:variable>
	
</xsl:stylesheet>
