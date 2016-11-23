<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:variable name="scripts">
		/js/askBeforeRedirect.js
	</xsl:variable>	

	<xsl:template match="document">
		<div class="contentitem">
			<xsl:apply-templates select="pageAdminModule"/>	
			<xsl:apply-templates select="page"/>
		</div>
	</xsl:template>
	
	<xsl:template match="page">		
		<xsl:value-of select="text" disable-output-escaping="yes"/>		
	</xsl:template>
	
	<xsl:template match="pageAdminModule">

		<div class="floatright">
			<!-- <a href="{/document/requestinfo/contextpath}{/document/requestinfo/servletpath}{section/fullAlias}/{module/alias}/export/{../page/pageID}"><img src="{/document/requestinfo/contextpath}/pics/exportpage.gif"/></a> -->
			
			<a href="{/document/requestinfo/contextpath}{section/fullAlias}/{module/alias}/firstpage/{../page/pageID}" title="{$pageAdminModule.setFirstpage}">
				<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/alias}/pics/page_1.png"/>
			</a>
			
			<a href="{/document/requestinfo/contextpath}{section/fullAlias}/{module/alias}/add/{../page/sectionID}" title="{$pageAdminModule.addlinktitle}">
				<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/alias}/pics/page_add.png"/>
			</a>			
			
			<a href="{/document/requestinfo/contextpath}{section/fullAlias}/{module/alias}/move/{../page/pageID}" title="{$pageAdminModule.movelinktitle} {../page/name}">
				<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/alias}/pics/page_move.png"/>
			</a>
			
			<a href="{/document/requestinfo/contextpath}{section/fullAlias}/{module/alias}/copy/{../page/pageID}" title="{$pageAdminModule.copylinktitle} {../page/name}">
				<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/alias}/pics/page_copy.png"/>
			</a>			
			
			<a href="{/document/requestinfo/contextpath}{section/fullAlias}/{module/alias}/update/{../page/pageID}" title="{$pageAdminModule.updatelinktitle} {../page/name}">
				<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/alias}/pics/page_edit.png"/>
			</a>
			
			<a href="javascript:askBeforeRedirect('{$pageAdminModule.deletepagepopup}','{/document/requestinfo/contextpath}{section/fullAlias}/{module/alias}/delete/{../page/pageID}?returnto=section');"  title="{$pageAdminModule.deletepagetitle} {../page/name}">
				<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/alias}/pics/page_delete.png"/>
			</a>
		</div>
	</xsl:template>	
</xsl:stylesheet>