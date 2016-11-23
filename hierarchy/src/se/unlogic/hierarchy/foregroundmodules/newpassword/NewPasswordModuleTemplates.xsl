<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/Common.xsl"/>
	
	<xsl:template match="Document">
		<div class="contentitem">			
			<xsl:apply-templates select="NewPasswordForm"/>
			<xsl:apply-templates select="NewPasswordSent"/>
		</div>			
	</xsl:template>
	
	<xsl:template match="NewPasswordForm">
	
		<xsl:value-of select="newPasswordFormMessage" disable-output-escaping="yes"/>
		
		<xsl:apply-templates select="validationException/validationError"/>
				
		<form id="newpasswordmoduleform" method="POST" action="{/Document/requestinfo/uri}">
			<table class="full">

				<xsl:if test="requireUsername">
					<tr>
						<td width="150px"><xsl:value-of select="$i18n.username"/>:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'username'"/>							
							</xsl:call-template>
						</td>
					</tr>
				</xsl:if>
				
				<tr>
					<td width="150px"><xsl:value-of select="$i18n.email"/>:</td>
					<td>
						<xsl:call-template name="createTextField">
							<xsl:with-param name="name" select="'email'"/>							
						</xsl:call-template>
					</td>
				</tr>
			</table>
			
			<xsl:if test="requireCaptchaConfirmation">
			
				<table class="full">
					<tr>
						<td colspan="2" class="text-align-center">
							<img id="captchaimg" src="{/Document/requestinfo/uri}/captcha"/>
							<br/>
							<a href="javascript:document.getElementById('captchaimg').setAttribute('src','{/Document/requestinfo/uri}/captcha?' + (new Date()).getTime());">
								<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/reload.png"/>
								<xsl:value-of select="$i18n.NewPasswordForm.regenerateCaptcha"/>
							</a>
						</td>
					</tr>			
				</table>
				
				<table class="full">
					<tr>
						<td width="150px"><xsl:value-of select="$i18n.NewPasswordForm.captchaConfirmation"/>:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'captchaConfirmation'"/>							
							</xsl:call-template>
						</td>
					</tr>									
				</table>
	
			</xsl:if>
	
			<div class="floatright">
				<input type="submit" value="{$i18n.NewPasswordForm.submit}"/>
			</div>	
		</form>
	
	</xsl:template>	
	
	<xsl:template match="NewPasswordSent">
	
		<xsl:value-of select="newPasswordSentMessage" disable-output-escaping="yes"/>
	
	</xsl:template>
	
	<xsl:template match="validationError">
		<xsl:if test="fieldName and validationErrorType and not(messageKey)">
			<p class="error">
				<xsl:choose>
					<xsl:when test="validationErrorType='RequiredField'">
						<xsl:value-of select="$i18n.validation.requiredField" />
					</xsl:when>
					<xsl:when test="validationErrorType='InvalidFormat'">
						<xsl:value-of select="$i18n.validation.invalidFormat" />
					</xsl:when>
					<xsl:when test="validationErrorType='TooShort'">
						<xsl:value-of select="$i18n.validation.tooShort" />
					</xsl:when>
					<xsl:when test="validationErrorType='TooLong'">
						<xsl:value-of select="$i18n.validation.tooLong" />
					</xsl:when>														
					<xsl:otherwise>
						<xsl:value-of select="$i18n.validation.unknownError" />
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:text>&#x20;</xsl:text>
				
				<xsl:choose>
					<xsl:when test="fieldName = 'email'">
						<xsl:value-of select="$i18n.email"/>
					</xsl:when>
					<xsl:when test="fieldName = 'username'">
						<xsl:value-of select="$i18n.username"/>
					</xsl:when>																																					
					<xsl:otherwise>
						<xsl:value-of select="fieldName"/>
					</xsl:otherwise>
				</xsl:choose>			
			</p>
		</xsl:if>
		
		<xsl:if test="messageKey">
			<p class="error">
				<xsl:choose>
					<xsl:when test="messageKey='UserNotFound'">
						<xsl:value-of select="$i18n.UserNotFound"/><xsl:text>!</xsl:text>
					</xsl:when>				
					<xsl:when test="messageKey='UserNotMutable'">
						<xsl:value-of select="$i18n.UserNotMutable"/><xsl:text>!</xsl:text>
					</xsl:when>
					<xsl:when test="messageKey='UserNotEnabled'">
						<xsl:value-of select="$i18n.UserNotEnabled"/><xsl:text>!</xsl:text>
					</xsl:when>					
					<xsl:when test="messageKey='UnableToUpdateUser'">
						<xsl:value-of select="$i18n.UnableToUpdateUser"/><xsl:text>!</xsl:text>
					</xsl:when>
					<xsl:when test="messageKey='ErrorSendingMail'">
						<xsl:value-of select="$i18n.ErrorSendingMail"/><xsl:text>!</xsl:text>
					</xsl:when>
					<xsl:when test="messageKey='InvalidCaptchaConfirmation'">
						<xsl:value-of select="$i18n.InvalidCaptchaConfirmation" />
					</xsl:when>																															
					<xsl:otherwise>
						<xsl:value-of select="$i18n.unknownFault"/>
					</xsl:otherwise>
				</xsl:choose>
			</p>
		</xsl:if>
		
	</xsl:template>		
				
</xsl:stylesheet>