<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	
	<xsl:template match="errors">
		<div class="contentitem">
			<!-- Core errors -->
			<xsl:apply-templates select="separateTransformationFailed"/>
			<xsl:apply-templates select="separateTransformationWithoutStylesheet" />
			<xsl:apply-templates select="invalidModuleResonse" />
			<xsl:apply-templates select="noModuleResponse" />
			
			<!-- Hierarchy exceptions -->
			
			<xsl:apply-templates select="URINotFoundException" />
			<xsl:apply-templates select="unhandledModuleException" />
			<xsl:apply-templates select="accessDeniedException" />
			<xsl:apply-templates select="ModuleConfigurationException" />
			<xsl:apply-templates select="ProtocolRedirectException" />
			<xsl:apply-templates select="SectionDefaultURINotSetException" />
			<xsl:apply-templates select="SectionDefaultURINotFoundException" />
			
			<p><xsl:value-of select="$i18n.errors.footer"/></p>
		</div>
	</xsl:template>

	<xsl:template match="separateTransformationFailed">
		<h1><xsl:value-of select="$i18n.separateTransformationFailed.header"/></h1>
		<p>
			<xsl:value-of select="$i18n.separateTransformationFailed.text1"/> <xsl:text>&#x20;</xsl:text> <xsl:value-of select="module/name" /> <xsl:text>&#x20;</xsl:text> <xsl:value-of select="$i18n.separateTransformationFailed.text2"/>
			<br />
			<xsl:value-of select="$i18n.separateTransformationFailed.text3"/> <xsl:text>&#x20;</xsl:text> <xsl:value-of select="exception" />			
		</p>
	</xsl:template>
	
	<xsl:template match="separateTransformationWithoutStylesheet">
		<h1><xsl:value-of select="$i18n.separateTransformationWithoutStylesheet.header"/></h1>
		<p>
			<xsl:value-of select="$i18n.separateTransformationWithoutStylesheet.text"/> <xsl:text>&#x20;</xsl:text> <xsl:value-of select="module/name" />.
		</p>
	</xsl:template>
	
	<xsl:template match="invalidModuleResonse">
		<h1><xsl:value-of select="$i18n.invalidModuleResonse.header"/></h1>
		<p>
			<xsl:value-of select="$i18n.invalidModuleResonse.text1"/> <xsl:text>&#x20;</xsl:text> <xsl:value-of select="module/name" /> <xsl:text>&#x20;</xsl:text> <xsl:value-of select="$i18n.invalidModuleResonse.text2"/>
		</p>
	</xsl:template>	

	<xsl:template match="noModuleResponse">
		<h1><xsl:value-of select="$i18n.noModuleResponse.header"/></h1>
		<p>
			<xsl:value-of select="$i18n.noModuleResponse.text1"/> <xsl:text>&#x20;</xsl:text> <xsl:value-of select="/document/requestinfo/url" /> <xsl:text>&#x20;</xsl:text> <xsl:value-of select="$i18n.noModuleResponse.text2"/>
		</p>
	</xsl:template>	

	<xsl:template match="URINotFoundException">
		<h1><xsl:value-of select="$i18n.URINotFoundException.header"/></h1>
		<p>
			<xsl:value-of select="$i18n.URINotFoundException.text1"/> <xsl:text>&#x20;</xsl:text> <xsl:value-of select="/document/requestinfo/url" /> <xsl:text>&#x20;</xsl:text> <xsl:value-of select="$i18n.URINotFoundException.text2"/>
		</p>
	</xsl:template>
	
	<xsl:template match="unhandledModuleException">
		<h1><xsl:value-of select="$i18n.unhandledModuleException.header"/></h1>
		<p>
			<xsl:value-of select="$i18n.unhandledModuleException.text1"/> <xsl:text>&#x20;</xsl:text> <xsl:value-of select="module/name" />.
			<br />
			<xsl:value-of select="$i18n.unhandledModuleException.text2"/> <xsl:text>&#x20;</xsl:text> <xsl:value-of select="throwable" />
		</p>
	</xsl:template>	
	
	<xsl:template match="accessDeniedException">
		<h1><xsl:value-of select="$i18n.accessDeniedException.header"/></h1>
		<p>
			<xsl:value-of select="$i18n.accessDeniedException.text"/>
		</p>
	</xsl:template>
	
	<xsl:template match="ModuleConfigurationException">
		<h1><xsl:value-of select="$i18n.ModuleConfigurationException.header"/></h1>
		<p>
			<xsl:value-of select="$i18n.ModuleConfigurationException.text"/>
		</p>
		<xsl:if test="message">
			<p>
				<xsl:value-of select="message"/>
			</p>
		</xsl:if>
	</xsl:template>		
	
	<xsl:template match="ProtocolRedirectException">
		<h1><xsl:value-of select="$i18n.ProtocolRedirectException.header"/></h1>
		<p>
			<xsl:value-of select="$i18n.ProtocolRedirectException.text"/>
		</p>
	</xsl:template>
		
	<xsl:template match="SectionDefaultURINotSetException">
		<h1><xsl:value-of select="$i18n.SectionDefaultURINotSetException.header"/></h1>
		<p>
			<xsl:value-of select="$i18n.SectionDefaultURINotSetException.text"/>
		</p>
	</xsl:template>
	
	<xsl:template match="SectionDefaultURINotFoundException">
		<h1><xsl:value-of select="$i18n.SectionDefaultURINotFoundException.header"/></h1>
		<p>
			<xsl:value-of select="$i18n.SectionDefaultURINotFoundException.text"/>
		</p>
	</xsl:template>
					
</xsl:stylesheet>