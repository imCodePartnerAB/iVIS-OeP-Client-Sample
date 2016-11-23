<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:template match="Document">	
		
		<xsl:choose>
			<xsl:when test="UserList">
		
				<div class="contentitem">
					<xsl:apply-templates select="UserList"/>
				</div>
			
			</xsl:when>
			<xsl:otherwise>
			
				<xsl:apply-imports/>
			
			</xsl:otherwise>
		</xsl:choose>
		

	</xsl:template>
	
	<xsl:template match="UserList">	

		<h1><xsl:value-of select="/Document/module/name"/></h1>

		<xsl:choose>
			<xsl:when test="GroupAccess">
			
				<xsl:value-of select="$i18n.GroupAccess.message"/>
				
				<xsl:apply-templates select="GroupAccess/group" mode="groupaccess"/>

				<div class="clearboth">
					<br/>
				</div>
			
				<xsl:apply-templates select="Users/user" mode="list"/>
			
				<xsl:if test="canAddUser='true'">
					<div class="floatright marginright">
						<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/listtypes" title="{$i18n.addUser}">
							<xsl:value-of select="$i18n.addUser"/>
							<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/user_add.png"/>
						</a>
					</div>
				</xsl:if>			
			
			</xsl:when>
			<xsl:otherwise>
				<p class="error">
					<xsl:value-of select="$i18n.NoGroupAccess"/>
				</p>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>	
	
	<xsl:template match="group" mode="groupaccess">
	
		<xsl:value-of select="name"/>
				
		<xsl:if test="position() != last()">
			<xsl:text>, </xsl:text>
		</xsl:if>
	
	</xsl:template>
	
	<xsl:template match="validationError[messageKey='AtLeastOneGroupRequired']">
	
		<p class="error">
			<xsl:value-of select="$i18n.AtLeastOneGroupRequired"/>
		</p>
		
	</xsl:template>		
</xsl:stylesheet>