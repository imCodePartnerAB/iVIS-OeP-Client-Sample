<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="ThreadInfoModuleTemplates.xsl"/>
	
	<xsl:variable name="i18n.activeThreds">Active threads:</xsl:variable>
	<xsl:variable name="i18n.threadCount">Thread count:</xsl:variable>
	<xsl:variable name="i18n.threadList">Thread list</xsl:variable>
	
	<xsl:variable name="i18n.priority">Priority:</xsl:variable>
	<xsl:variable name="i18n.Alive">Alive:</xsl:variable>
	<xsl:variable name="i18n.daemon">Daemon:</xsl:variable>
	<xsl:variable name="i18n.intrerrupted">Interrupted:</xsl:variable>
	<xsl:variable name="i18n.stacktrace">Stacktrace:</xsl:variable>
	<xsl:variable name="i18n.at">at</xsl:variable>
	
	<xsl:variable name="i18n.interruptThread">Interrupt thread</xsl:variable>
	<xsl:variable name="i18n.stopThread">Stop thread</xsl:variable>
	
	<xsl:variable name="i18n.UnableToStopThreadNoNameSpecified">Unable to stop thread, no name parameter specified.</xsl:variable>
	<xsl:variable name="i18n.UnableToStopThreadNotFound">Unable to stop thread, thread not found.</xsl:variable>
	<xsl:variable name="i18n.UnableToInterruptThreadNoNameSpecified">Unable to interrupt thread, no name parameter specified.</xsl:variable>
	<xsl:variable name="i18n.UnableToInterruptThreadNotFound">Unable to interrupt thread, thread not found.</xsl:variable>
	<xsl:variable name="i18n.threadStopped.part1">Thread </xsl:variable>
	<xsl:variable name="i18n.threadStopped.part2"> stopped.</xsl:variable>
	<xsl:variable name="i18n.threadIntrerrupted.part1">Thread </xsl:variable>
	<xsl:variable name="i18n.threadIntrerrupted.part2"> interrupted.</xsl:variable>

</xsl:stylesheet>
