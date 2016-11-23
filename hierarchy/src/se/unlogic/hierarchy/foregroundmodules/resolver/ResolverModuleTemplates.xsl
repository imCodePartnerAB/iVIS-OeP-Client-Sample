<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="../../core/utils/xsl/Common.xsl"/>

	<xsl:template match="Document">
		<div class="contentitem">			
			<h1><xsl:value-of select="/Document/module/name"/></h1>
			
			<form method="GET">
				<fieldset>
					<legend><xsl:value-of select="$ResolveHost"/></legend>
					
					<p>
						<xsl:value-of select="$Host"/>:
						
						<input type="text" name="host" value="{Host}"/>
					</p>
					
					<xsl:choose>
						<xsl:when test="ResolvedHost">
						
							<p>
								<xsl:value-of select="$Host"/>
								<xsl:text> </xsl:text>
								<xsl:value-of select="Host"/>
								<xsl:text> </xsl:text>
								<xsl:value-of select="$HostResolvedTo"/>
								<xsl:text> </xsl:text>
								<xsl:value-of select="ResolvedHost"/>
							</p>
						
						</xsl:when>
						<xsl:when test="UnableToResolveHost">
						
							<p class="error">
								<xsl:value-of select="$UnableToResolveHost"/>
								<xsl:text> </xsl:text>
								<xsl:value-of select="Host"/>
							</p>
						
						</xsl:when>
					</xsl:choose>
					
					<div class="floatright">
						<input type="submit" value="{$Resolve}"/>
					</div>
				</fieldset>
			</form>
			<br/>
			
			<form method="GET">
				<fieldset>
					<legend><xsl:value-of select="$ResolveIP"/></legend>
					
					<p>
						<xsl:value-of select="$IP"/>:
						
						<input type="text" name="ip" value="{IP}"/>
					</p>
					
					<xsl:choose>
						<xsl:when test="ResolvedIP">
						
							<p>
								<xsl:value-of select="$IP"/>
								<xsl:text> </xsl:text>
								<xsl:value-of select="IP"/>
								<xsl:text> </xsl:text>
								<xsl:value-of select="$IPResolvedTo"/>
								<xsl:text> </xsl:text>
								<xsl:value-of select="ResolvedIP"/>
							</p>
						
						</xsl:when>
						<xsl:when test="UnableToResolveIP">
						
							<p class="error">
								<xsl:value-of select="$UnableToResolveIP"/>
								<xsl:text> </xsl:text>
								<xsl:value-of select="IP"/>
							</p>
						
						</xsl:when>
					</xsl:choose>
					
					<div class="floatright">
						<input type="submit" value="{$Resolve}"/>
					</div>				
				</fieldset>			
			</form>
			
		</div>			
	</xsl:template>
							
</xsl:stylesheet>