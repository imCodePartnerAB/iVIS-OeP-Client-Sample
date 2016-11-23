<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:template match="Document">	
		<div class="contentitem">
			
			<xsl:choose>
				<xsl:when test="LoggedOut/Message">
					<xsl:value-of select="LoggedOut/Message" disable-output-escaping="yes"/>
				</xsl:when>
				<xsl:otherwise>
					<h1><xsl:value-of select="$i18n.header"/></h1>
					<p><xsl:value-of select="$i18n.text"/></p>				
				</xsl:otherwise>
			</xsl:choose>

		</div>
	</xsl:template>

</xsl:stylesheet>