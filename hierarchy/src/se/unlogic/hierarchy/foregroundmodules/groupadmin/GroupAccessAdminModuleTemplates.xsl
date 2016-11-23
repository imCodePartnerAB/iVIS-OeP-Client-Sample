<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>
	
	<xsl:template name="AddEditFormData">
		
		<xsl:apply-templates select="Users" mode="admins"/>
	
	</xsl:template>
	
	<xsl:template match="Users" mode="admins">
	
		<div class="clearboth"/>
		
		<br/>
	
		<h2><xsl:value-of select="$i18n.GroupAdministrators"/></h2>
		
		<div class="scrolllist">			
			<xsl:apply-templates select="user" mode="admin"/>
		</div>
		
		<br/>
		
	</xsl:template>
		
	<xsl:template match="user" mode="admin">
	
		<div class="floatleft full border marginbottom">
			<div class="floatleft">
				<xsl:choose>
					<xsl:when test="enabled='true'">
						<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/user.png"/>
					</xsl:when>
					<xsl:otherwise>
						<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/user_disabled.png"/>
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:text>&#x20;</xsl:text>
				
				<xsl:value-of select="firstname"/>
				
				<xsl:text>&#x20;</xsl:text>
				
				<xsl:value-of select="lastname"/>
				
				<xsl:if test="username">
					<xsl:text>&#x20;</xsl:text>
					
					<xsl:text>(</xsl:text>
						<xsl:value-of select="username"/>
					<xsl:text>)</xsl:text>
				</xsl:if>		
			</div>
			<div class="floatright marginright">
				
				<xsl:variable name="userID" select="userID"/>
			
				<input type="checkbox" name="admin_user" value="{userID}">
					<xsl:choose>
						<xsl:when test="../../requestparameters">
							<xsl:if test="../../requestparameters/parameter[name='user'][value=$userID]">
								<xsl:attribute name="checked"/>
							</xsl:if>						
						</xsl:when>
						<xsl:when test="../../group">
							<xsl:if test="../../AdminUsers/user[userID=$userID]">
								<xsl:attribute name="checked"/>
							</xsl:if>								
						</xsl:when>
					</xsl:choose>
					
					<xsl:if test="isMutable='false'">
						<xsl:attribute name="disabled"/>
					</xsl:if>						
				</input>
			</div>				
		</div>
		
	</xsl:template>	
	
	<xsl:template name="AddShowFormData">
		
		<xsl:apply-templates select="AdminUsers"/>
	
	</xsl:template>	
	
	<xsl:template match="AdminUsers">

		<div class="clearboth"/>
		
		<br/>

		<h2>
			<xsl:value-of select="$i18n.GroupAdministrators"/>
			<xsl:text>&#x20;(</xsl:text>
			<xsl:value-of select="count(user)"/>
			<xsl:text>)</xsl:text>
		</h2>
		
		<div>			
			<xsl:apply-templates select="user" mode="show"/>
		</div>
		
		<br/>
		
	</xsl:template>	
	
</xsl:stylesheet>