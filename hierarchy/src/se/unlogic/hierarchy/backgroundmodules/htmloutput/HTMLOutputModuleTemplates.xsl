<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>
	
	<xsl:template match="Document">
		<div class="contentitem {cssClass}">
			
			<xsl:if test="settingsURL">
				<div class="floatright">
					<a href="{/Document/requestinfo/contextpath}{settingsURL}?redirect={/Document/requestinfo/uri}" title="{$i18n.UpdateLinkTitle}">
						<img src="{/Document/requestinfo/contextpath}/static/b/{/Document/module/sectionID}/{/Document/module/moduleID}/images/edit.png"/>
					</a>
				</div>
			</xsl:if>
					
			<xsl:value-of select="HTML" disable-output-escaping="yes" />
			
		</div>
	</xsl:template>
</xsl:stylesheet>