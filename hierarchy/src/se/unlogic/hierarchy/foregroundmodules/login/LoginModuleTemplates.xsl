<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:template match="document">
		<div class="contentitem loginModule">	
			<xsl:apply-templates select="LoginFailed"/>
			<xsl:apply-templates select="AccountDisabled"/>
			<xsl:apply-templates select="AccountLocked"/>
			<xsl:apply-templates select="Login"/>
		</div>		
	</xsl:template>
	
	<xsl:template match="Login">
		<h1><xsl:value-of select="$Login.header"/></h1>
		
		<xsl:call-template name="LoginForm"/>
	</xsl:template>
	 
	<xsl:template match="AccountDisabled">
		<h1><xsl:value-of select="$AccountDisabled.header"/></h1>
		
		<p class="error"><xsl:value-of select="$AccountDisabled.text"/></p>
	</xsl:template>		
	
	<xsl:template match="LoginFailed">
		<h1><xsl:value-of select="$LoginFailed.header"/></h1>
		
		<p class="error"><xsl:value-of select="$LoginFailed.text"/></p>
		
		<xsl:call-template name="LoginForm"/>
	</xsl:template>
	
	<xsl:template match="AccountLocked">
		<h1><xsl:value-of select="$AccountLocked.header"/></h1>
		
		<p class="error"><xsl:value-of select="$AccountLocked.text.part1"/><xsl:value-of select="."/><xsl:value-of select="$AccountLocked.text.part2"/></p>
		
		<xsl:call-template name="LoginForm"/>
	</xsl:template>	
	
	<xsl:template name="LoginForm">
		<form id="loginmoduleform" method="post" ACCEPT-CHARSET="ISO-8859-1" action="{/document/uri}">
		
			<input type="hidden" name="redirect" value="{/document/redirect}"/>
		
			<table>
				<tr>
					<td><xsl:value-of select="$LoginForm.username"/></td>
					<td><input type="text" name="username" size="40"/></td>				
				</tr>
				<tr>
					<td><xsl:value-of select="$LoginForm.password"/></td>
					<td><input type="password" name="password" size="40"/></td>				
				</tr>
				<tr>
					<td colspan="2" align="right" class="text-align-right"><input type="submit" value="{$LoginForm.submit}"/></td>
				</tr>
				
				<xsl:if test="/document/newPasswordModuleAlias or /document/registrationModuleAlias">

					<tr>
						<td colspan="2" align="center" class="text-align-center">
							<xsl:if test="/document/newPasswordModuleAlias">
								<a id="newPasswordLink" href="{/document/requestinfo/contextpath}{/document/newPasswordModuleAlias}">
									<img class="alignbottom marginleft" src="{/document/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/user_unlocked.png" />
									<xsl:text>&#160;</xsl:text>
									<xsl:value-of select="$requestNewPassword"/>
								</a>
								<xsl:text>&#160;&#160;&#160;&#160;</xsl:text>
							</xsl:if>
							<xsl:if test="/document/registrationModuleAlias">
								<a id="registrationLink" href="{/document/requestinfo/contextpath}{/document/registrationModuleAlias}">
									<img class="alignbottom marginleft" src="{/document/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/user_add.png" />
									<xsl:text>&#160;</xsl:text>
									<xsl:value-of select="$createNewAccount"/>
								</a>
							</xsl:if>
						</td>
					</tr>
		
				</xsl:if>					
											
			</table>
		</form>
					
	</xsl:template>	
</xsl:stylesheet>