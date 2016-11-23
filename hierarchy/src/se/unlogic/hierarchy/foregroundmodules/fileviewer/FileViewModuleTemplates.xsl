<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:template match="Document">	
		<div class="contentitem">

			<h1><xsl:value-of select="module/name"/></h1>		
		
			<xsl:apply-templates select="NoFilePathSet"/>
			<xsl:apply-templates select="FileNotFound"/>
			<xsl:apply-templates select="FileIsDirectory"/>
			<xsl:apply-templates select="UnableToAccessFile"/>
			<xsl:apply-templates select="File"/>			
		</div>
	</xsl:template>
	
	<xsl:template match="FileNotFound">
		
		<p class="error">
			<xsl:value-of select="$FileNotFound.errorMessage"/>
		</p>
		
	</xsl:template>
	
	<xsl:template match="FileIsDirectory">
		
		<p class="error">
			<xsl:value-of select="$FileIsDirectory.errorMessage"/>
		</p>
		
	</xsl:template>
	
	<xsl:template match="UnableToAccessFile">
		
		<p class="error">
			<xsl:value-of select="$UnableToAccessFile.errorMessage"/>
		</p>
		
	</xsl:template>
	
	<xsl:template match="File">
		
		<xsl:if test="FilePath">
			<h2>
				<xsl:value-of select="FilePath"/> (<xsl:value-of select="count(Lines/Line)"/><xsl:text> </xsl:text><xsl:value-of select="$File.lines"/>)			
			</h2>
			
			<!-- <xsl:text>&#x20;</xsl:text>	-->
		</xsl:if>
		
		<code>
			<xsl:apply-templates select="Lines/Line"/>
		</code>
		
	</xsl:template>
		
	<xsl:template match="Line">
		
		<xsl:value-of select="."/><br/>
		
	</xsl:template>	
		
</xsl:stylesheet>