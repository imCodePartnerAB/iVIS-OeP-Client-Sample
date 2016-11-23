<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/Common.xsl"/>

	<xsl:template match="Document">	
		<div class="contentitem">
			<xsl:apply-templates select="Register"/>
			<xsl:apply-templates select="Registered"/>			
		</div>
	</xsl:template>
		
	<xsl:template match="Register">		
				
		<xsl:value-of select="RegistrationText" disable-output-escaping="yes"/>
		
		<xsl:apply-templates select="validationException/validationError"/>
		
		<form method="post" action="{/document/requestinfo/uri}">
			<table>
				<tr>
					<td><xsl:value-of select="$i18n.Firstname"/>:</td>
					<td><xsl:value-of select="Invitation/firstname"/></td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.Lastname"/>:</td>
					<td><xsl:value-of select="Invitation/lastname"/></td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.Email"/>:</td>
					<td><xsl:value-of select="Invitation/email"/></td>
				</tr>			
				<tr>
					<td><xsl:value-of select="$i18n.Username"/>:</td>
					<td><input type="text" name="username" size="40" value="{requestparameters/parameter[name='username']/value}"/></td>
				</tr>						
				<tr>
					<td><xsl:value-of select="$i18n.Password"/>:</td>
					<td><input type="password" name="password" size="40" value="{requestparameters/parameter[name='password']/value}"/></td>
				</tr>																		
			</table>
			
			<div align="right">
				<input type="submit" value="{$i18n.CreateAccount}"/>			
			</div>
		</form>
	</xsl:template>
	
	<xsl:template match="Registered">		
				
		<xsl:value-of select="RegisteredText" disable-output-escaping="yes"/>
		
	</xsl:template>
		
	<xsl:template match="validationError">
		<xsl:if test="fieldName and validationErrorType and not(messageKey)">
			<p class="error">
				<xsl:choose>
					<xsl:when test="validationErrorType='RequiredField'">
						<xsl:text><xsl:value-of select="$i18n.ValidationError.RequiredField"/></xsl:text>
					</xsl:when>
					<xsl:when test="validationErrorType='InvalidFormat'">
						<xsl:text><xsl:value-of select="$i18n.ValidationError.InvalidFieldValueFormat"/></xsl:text>
					</xsl:when>
					<xsl:when test="validationErrorType='TooShort'">
						<xsl:value-of select="$i18n.ValidationError.TooShort" />
					</xsl:when>
					<xsl:when test="validationErrorType='TooLong'">
						<xsl:value-of select="$i18n.ValidationError.TooLong" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text><xsl:value-of select="i18n.ValidationError.UnknownFieldError"/></xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:text>&#x20;</xsl:text>
				
				<xsl:choose>
					<xsl:when test="fieldName = 'username'">
						<xsl:text><xsl:value-of select="$i18n.username"/></xsl:text>
					</xsl:when>
					<xsl:when test="fieldName = 'password'">
						<xsl:text><xsl:value-of select="$i18n.password"/></xsl:text>
					</xsl:when>																																												
					<xsl:otherwise>
						<xsl:value-of select="fieldName"/><xsl:text></xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>!</xsl:text>			
			</p>
		</xsl:if>
		
		<xsl:if test="messageKey">
			<p class="error">
				<xsl:choose>
					<xsl:when test="messageKey='UsernameAlreadyTaken'">
						<xsl:value-of select="$i18n.ValidationError.Message.UsernameAlreadyTaken"/><xsl:text>!</xsl:text>
					</xsl:when>											
					<xsl:otherwise>
						<xsl:value-of select="$i18n.ValidationError.Message.UnknownError"/><xsl:text>!</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</p>
		</xsl:if>
	</xsl:template>
		
</xsl:stylesheet>