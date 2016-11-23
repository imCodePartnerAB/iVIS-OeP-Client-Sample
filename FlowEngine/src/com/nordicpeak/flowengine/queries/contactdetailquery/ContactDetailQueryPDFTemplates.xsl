<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	
	<xsl:template match="Document">	
		
		<xsl:apply-templates select="ShowQueryValues"/>
		
	</xsl:template>
		
	<xsl:template match="ShowQueryValues">
		
		<div class="query">
			
			<a name="query{ContactDetailQueryInstance/QueryInstanceDescriptor/QueryDescriptor/queryID}"/>
			
			<h2>
				<xsl:value-of select="ContactDetailQueryInstance/QueryInstanceDescriptor/QueryDescriptor/name"/>
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
					<strong><xsl:value-of select="$i18n.FirstnameAndLastname" /></strong><br/>
					<xsl:value-of select="ContactDetailQueryInstance/firstname" /><xsl:text>&#160;</xsl:text><xsl:value-of select="ContactDetailQueryInstance/lastname" />
				</div>
				
				<xsl:if test="ContactDetailQueryInstance/address">
					
					<div class="floatleft fifty bigmarginbottom">
						<strong><xsl:value-of select="$i18n.Address" /></strong><br/>
						<xsl:call-template name="printValue">
							<xsl:with-param name="value" select="ContactDetailQueryInstance/address" />
						</xsl:call-template>
					</div>					
					
					<div class="floatleft fifty bigmarginbottom">
						<strong><xsl:value-of select="$i18n.ZipCode" /><xsl:text>&#160;</xsl:text><xsl:value-of select="$i18n.And" /><xsl:text>&#160;</xsl:text><xsl:value-of select="$i18n.PostalAddress" /></strong><br/>
						<xsl:call-template name="printValue">
							<xsl:with-param name="value" select="ContactDetailQueryInstance/zipCode" />
						</xsl:call-template>
						<xsl:text>&#160;</xsl:text>
						<xsl:call-template name="printValue">
							<xsl:with-param name="value" select="ContactDetailQueryInstance/postalAddress" />
						</xsl:call-template>
					</div>
						
				</xsl:if>
				
				<xsl:if test="ContactDetailQueryInstance/phone">
						
					<div class="floatleft fifty bigmarginbottom">
						<strong><xsl:value-of select="$i18n.Phone" /></strong><br/>
						<xsl:call-template name="printValue">
							<xsl:with-param name="value" select="ContactDetailQueryInstance/phone" />
						</xsl:call-template>
					</div>
				
				</xsl:if>
				
				<xsl:if test="ContactDetailQueryInstance/email">
			
					<div class="floatleft fifty bigmarginbottom">
						<strong><xsl:value-of select="$i18n.Email" /></strong><br/>
						<xsl:call-template name="printValue">
							<xsl:with-param name="value" select="ContactDetailQueryInstance/email" />
						</xsl:call-template>
					</div>
				
				</xsl:if>
				
				<xsl:if test="ContactDetailQueryInstance/mobilePhone">
			
					<div class="floatleft fifty bigmarginbottom">
						<strong><xsl:value-of select="$i18n.MobilePhone" /></strong><br/>
						<xsl:call-template name="printValue">
							<xsl:with-param name="value" select="ContactDetailQueryInstance/mobilePhone" />
						</xsl:call-template>
					</div>
				
				</xsl:if>
				<!-- 
				<div class="floatleft full">
					<strong><xsl:value-of select="$i18n.ChooseContactChannels" /></strong><br/>
					
					<xsl:if test="ContactDetailQueryInstance/email">
						<xsl:value-of select="$i18n.ContactByEmail" /><br/>
					</xsl:if>
					
					<xsl:if test="ContactDetailQueryInstance/contactBySMS = 'true'">
						<xsl:value-of select="$i18n.ContactBySMS" /><br/>
					</xsl:if>
				</div>
				 -->
			</div>
					
		</div>
		
	</xsl:template>		

	<xsl:template name="printValue">
		
		<xsl:param name="value" />
		
		<xsl:choose>
			<xsl:when test="$value">
				<xsl:value-of select="$value"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>-</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:template>

</xsl:stylesheet>