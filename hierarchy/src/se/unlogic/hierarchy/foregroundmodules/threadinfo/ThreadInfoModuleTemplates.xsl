<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:template match="document">	
		<div class="contentitem">
			<h1><xsl:value-of select="module/name"/></h1>
			
			<xsl:apply-templates select="validationError"/>
			
			<xsl:apply-templates select="interrupted"/>
			<xsl:apply-templates select="stopped"/>
			
			<p><xsl:value-of select="$i18n.activeThreds"/> <xsl:value-of select="activeThreads"/></p>
			<p><xsl:value-of select="$i18n.threadCount"/> <xsl:value-of select="count(threads/thread)"/></p>
		</div>			
		
		<hr class="noscreen" />	
			
		<xsl:apply-templates select="threads"/>
	</xsl:template>
		
	<xsl:template match="threads">
		<div class="contentitem">
			<h1><xsl:value-of select="$i18n.threadList"/></h1>
			
			<xsl:apply-templates select="thread"/>
		</div>
	</xsl:template>
	
	<xsl:template match="thread">
		<fieldset>
			<legend><xsl:value-of select="name"/></legend>
			
			<div class="floatright">
				<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/interrupt?name={name}" onclick="return confirm('{$i18n.interruptThread}: {name}?')" title="{$i18n.interruptThread}: {name}">
					<img class="alignbottom" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/warning.png"/>
				</a>
				
				<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/stop?name={name}" onclick="return confirm('{$i18n.stopThread}: {name}?')" title="{$i18n.stopThread}: {name}">
					<img class="alignbottom" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/stop.png"/>
				</a>
			</div>
			
			<p><xsl:value-of select="$i18n.priority"/> <xsl:value-of select="priority"/></p>
			<p><xsl:value-of select="$i18n.Alive"/> <xsl:value-of select="alive"/></p>
			<p><xsl:value-of select="$i18n.daemon"/> <xsl:value-of select="daemon"/></p>
			<p><xsl:value-of select="$i18n.intrerrupted"/> <xsl:value-of select="interrupted"/></p>
			<p><xsl:value-of select="$i18n.stacktrace"/></p>
			<xsl:apply-templates select="stacktrace/stackTraceElement"/>		
		</fieldset>
	</xsl:template>
	
	<xsl:template match="stackTraceElement">
		<p><xsl:value-of select="$i18n.at"/> <xsl:value-of select="className"/>.<xsl:value-of select="methodName"/>(<xsl:value-of select="fileName"/>:<xsl:value-of select="lineNumber"/>)</p>
	</xsl:template>
	
	<xsl:template match="validationError[messageKey='UnableToStopThreadNoNameSpecified']">
	
		<p class="error">
			<xsl:value-of select="$i18n.UnableToStopThreadNoNameSpecified"/>
		</p>
			
	</xsl:template>	
	
	<xsl:template match="validationError[messageKey='UnableToStopThreadNotFound']">
	
		<p class="error">
			<xsl:value-of select="$i18n.UnableToStopThreadNotFound"/>
		</p>
			
	</xsl:template>
	
	<xsl:template match="validationError[messageKey='UnableToInterruptThreadNoNameSpecified']">
	
		<p class="error">
			<xsl:value-of select="$i18n.UnableToInterruptThreadNoNameSpecified"/>
		</p>
			
	</xsl:template>
	
	<xsl:template match="validationError[messageKey='UnableToInterruptThreadNotFound']">
	
		<p class="error">
			<xsl:value-of select="$i18n.UnableToInterruptThreadNotFound"/>
		</p>
			
	</xsl:template>	
	
	<xsl:template match="stopped">
	
		<p class="bold">
			<xsl:value-of select="$i18n.threadStopped.part1"/>
			<xsl:value-of select="."/>
			<xsl:value-of select="$i18n.threadStopped.part2"/>
		</p>
			
	</xsl:template>
	
	<xsl:template match="interrupted">
	
		<p class="bold">
			<xsl:value-of select="$i18n.threadIntrerrupted.part1"/>
			<xsl:value-of select="."/>
			<xsl:value-of select="$i18n.threadIntrerrupted.part2"/>
		</p>
			
	</xsl:template>	
	
</xsl:stylesheet>