<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	
	<xsl:template match="Document">	
		
		<xsl:apply-templates select="ShowQueryValues"/>
		
	</xsl:template>
		
	<xsl:template match="ShowQueryValues">
		
		<div class="query">
			
			<a name="query{ManualMultiSignQueryInstance/QueryInstanceDescriptor/QueryDescriptor/queryID}"/>
			
			<h2>
				<xsl:value-of select="ManualMultiSignQueryInstance/QueryInstanceDescriptor/QueryDescriptor/name"/>
			</h2>
			
			<xsl:if test="Description">
				
				<xsl:choose>
					<xsl:when test="isHTMLDescription = 'true'">
						<xsl:value-of select="Description" disable-output-escaping="yes"/>
					</xsl:when>
					<xsl:otherwise>
						<p>
							<xsl:value-of select="Description" disable-output-escaping="yes"/>
						</p>
					</xsl:otherwise>
				</xsl:choose>
				
			</xsl:if>
			
			<div class="full display-table bigmarginbottom">
				
				<div class="floatleft fifty bigmarginbottom">
					<strong><xsl:value-of select="$i18n.Name" /></strong><br/>
					<xsl:value-of select="ManualMultiSignQueryInstance/SigningParties/SigningParty/name" />
				</div>
				
				<div class="floatleft fifty bigmarginbottom">
					<strong><xsl:value-of select="$i18n.SocialSecurityNumber" /></strong><br/>
					<xsl:value-of select="ManualMultiSignQueryInstance/SigningParties/SigningParty/socialSecurityNumber" />
				</div>
				
				<div class="floatleft fifty bigmarginbottom">
					<strong><xsl:value-of select="$i18n.Email" /></strong><br/>
					<xsl:value-of select="ManualMultiSignQueryInstance/SigningParties/SigningParty/email" />
				</div>
				
			</div>
					
		</div>
		
	</xsl:template>		

</xsl:stylesheet>