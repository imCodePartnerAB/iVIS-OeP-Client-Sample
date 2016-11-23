<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="../../core/utils/xsl/Common.xsl"/>

	<xsl:template match="Document">
		<div class="contentitem">			
			<xsl:apply-templates select="AddUser"/>
			<xsl:apply-templates select="UserAdded"/>
			<xsl:apply-templates select="AccountEnabled"/>
		</div>			
	</xsl:template>
		
	<xsl:template match="AddUser">
		<xsl:value-of select="registrationMessage" disable-output-escaping="yes"/>
		
		<xsl:apply-templates select="validationException/validationError"/>
				
		<form id="registrationmoduleform" method="POST" action="{/Document/requestinfo/uri}">
			<table class="full">
				<tr>
					<td width="150px"><xsl:value-of select="$AddUser.firstname"/>:</td>
					<td>
						<xsl:call-template name="createTextField">
							<xsl:with-param name="name" select="'firstname'"/>							
						</xsl:call-template>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$AddUser.lastname"/>:</td>
					<td>
						<xsl:call-template name="createTextField">
							<xsl:with-param name="name" select="'lastname'"/>							
						</xsl:call-template>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$AddUser.username"/>:</td>
					<td>
						<xsl:call-template name="createTextField">
							<xsl:with-param name="name" select="'username'"/>							
						</xsl:call-template>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$AddUser.email"/>:</td>
					<td>
						<xsl:call-template name="createTextField">
							<xsl:with-param name="name" select="'email'"/>							
						</xsl:call-template>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$AddUser.emailConfirmation"/>:</td>
					<td>
						<xsl:call-template name="createTextField">
							<xsl:with-param name="name" select="'emailConfirmation'"/>							
						</xsl:call-template>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$AddUser.password"/>:</td>
					<td>
						<xsl:call-template name="createPasswordField">
							<xsl:with-param name="name" select="'password'"/>							
						</xsl:call-template>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$AddUser.passwordConfirmation"/>:</td>
					<td>
						<xsl:call-template name="createPasswordField">
							<xsl:with-param name="name" select="'passwordConfirmation'"/>							
						</xsl:call-template>
					</td>
				</tr>

				<xsl:apply-templates select="AttrbuteDescriptors/AttributeDescriptor" mode="update"/>

			</table>
			
			<xsl:if test="requireCaptchaConfirmation">
			
				<table class="full">
					<tr>
						<td colspan="2" class="text-align-center">
							<img id="captchaimg" src="{/Document/requestinfo/uri}/captcha"/>
							<br/>
							<a href="javascript:document.getElementById('captchaimg').setAttribute('src','{/Document/requestinfo/uri}/captcha?' + (new Date()).getTime());">
								<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/reload.png"/>
								<xsl:value-of select="$AddUser.regenerateCaptcha"/>
							</a>
						</td>
					</tr>			
				</table>
				
				<table class="full">
					<tr>
						<td width="150px"><xsl:value-of select="$AddUser.captchaConfirmation"/>:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'captchaConfirmation'"/>							
							</xsl:call-template>
						</td>
					</tr>									
				</table>
	
			</xsl:if>
	
			<xsl:if test="requireUserConditionConfirmation">
					
				<table class="full">
					<tr>
						<td width="150px"><xsl:value-of select="$AddUser.userConditions"/>:</td>
						<td>
							<xsl:call-template name="createCheckbox">
								<xsl:with-param name="name" select="'userConditionConfirmation'"/>				
							</xsl:call-template>
							<xsl:text>&#x20;</xsl:text>
							<xsl:value-of select="$AddUser.userConditions.part1" />
							<xsl:text>&#x20;</xsl:text>
							<a href="javascript:void(0);" title="{$AddUser.showUserConditions}" target="_blank" onclick="window.open('{/Document/requestinfo/currentURI}/{/Document/module/alias}/userconditions','{$AddUser.userConditions}','status=0,scrollbars=1,resizable=no,width=400,height=350,top=200,left=450');return false;"><xsl:value-of select="$AddUser.userConditions.part2" /></a>
							<xsl:text>&#x20;</xsl:text>
							<xsl:value-of select="$AddUser.userConditions.part3" />.
						</td>
					</tr>									
				</table>
				
	 		</xsl:if>	
	
			<xsl:apply-templates select="PluginFragments/ViewFragment"/>
	
			<div class="floatright">
				<input type="submit" value="{$AddUser.createAccount}"/>
			</div>
		</form>	
	</xsl:template>
	
	<xsl:template match="ViewFragment">
	
		<br/>
	
		<xsl:value-of select="HTML" disable-output-escaping="yes"/>
	
	</xsl:template>
	
	<xsl:template match="AttributeDescriptor" mode="update">
		
		<xsl:variable name="name" select="Name"/>
		
		<tr>
			<td>
				<xsl:choose>
					<xsl:when test="DisplayName">
						<xsl:value-of select="DisplayName"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="Name"/>
					</xsl:otherwise>
				</xsl:choose>
				
				<!--
				<xsl:if test="AttributeMode = 'REQUIRED'">
					<span class="required">*</span>
				</xsl:if>
				-->
				
				<xsl:text>:</xsl:text>
			</td>
			<td>
				<xsl:call-template name="createTextField">
					<xsl:with-param name="name"><xsl:value-of select="'attribute-'"/><xsl:value-of select="Name"/></xsl:with-param>
					<xsl:with-param name="requestparameters" select="../../requestparameters"/>
					<xsl:with-param name="size" select="40"/>
					<xsl:with-param name="class" select="'attributeField'"/>
					<xsl:with-param name="disabled" select="AttributeMode = 'DISABLED'"/>					
				</xsl:call-template>				
			</td>
		</tr>
		
	</xsl:template>		
	
	<xsl:template match="UserAdded">
		
		<xsl:value-of select="registereredMessage" disable-output-escaping="yes"/>
			
	</xsl:template>	
	
	<xsl:template match="AccountEnabled">
	
		<xsl:value-of select="accountEnabledMessage" disable-output-escaping="yes"/>

	</xsl:template>		

	<xsl:template match="validationError">
		<xsl:if test="fieldName and validationErrorType and not(messageKey)">
			<p class="error">
				<xsl:choose>
					<xsl:when test="validationErrorType='RequiredField'">
						<xsl:value-of select="$validationError.requiredField"/>
					</xsl:when>
					<xsl:when test="validationErrorType='InvalidFormat'">
						<xsl:value-of select="$validationError.invalidFormat"/>
					</xsl:when>
					<xsl:when test="validationErrorType='TooShort'">
						<xsl:value-of select="$validationError.tooShort" />
					</xsl:when>
					<xsl:when test="validationErrorType='TooLong'">
						<xsl:value-of select="$validationError.tooLong" />
					</xsl:when>		
					<xsl:otherwise>
						<xsl:value-of select="$validationError.unknownError"/>
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:text>&#x20;</xsl:text>
				
				<xsl:choose>
					<xsl:when test="displayName">
						<xsl:value-of select="displayName"/>
					</xsl:when>				
					<xsl:when test="fieldName = 'firstname'">
						<xsl:value-of select="$validationError.field.firstname"/>
					</xsl:when>
					<xsl:when test="fieldName = 'lastname'">
						<xsl:value-of select="$validationError.field.lastname"/>
					</xsl:when>
					<xsl:when test="fieldName = 'username'">
						<xsl:value-of select="$validationError.field.username"/>
					</xsl:when>
					<xsl:when test="fieldName = 'email'">
						<xsl:value-of select="$validationError.field.email"/>
					</xsl:when>
					<xsl:when test="fieldName = 'password'">
						<xsl:value-of select="$validationError.field.password"/>
					</xsl:when>															
					<xsl:otherwise>
						<xsl:value-of select="fieldName"/>
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:text>!</xsl:text>		
			</p>
		</xsl:if>
		
		<xsl:if test="messageKey">
			<p class="error">
				<xsl:choose>
					<xsl:when test="messageKey='UsernameAlreadyTaken'">
						<xsl:value-of select="$validationError.message.UsernameAlreadyTaken"/>
					</xsl:when>
					<xsl:when test="messageKey='EmailConfirmationMismatch'">
						<xsl:value-of select="$validationError.message.EmailConfirmationMismatch"/>
					</xsl:when>					
					<xsl:when test="messageKey='EmailAlreadyTaken'">
						<xsl:value-of select="$validationError.message.EmailAlreadyTaken"/>
					</xsl:when>
					<xsl:when test="messageKey='PasswordConfirmationMismatch'">
						<xsl:value-of select="$validationError.message.PasswordConfirmationMismatch"/>
					</xsl:when>
					<xsl:when test="messageKey='InvalidCaptchaConfirmation'">
						<xsl:value-of select="$validationError.message.InvalidCaptchaConfirmation"/>
					</xsl:when>
					<xsl:when test="messageKey='InvalidEmailAddress'">
						<xsl:value-of select="$validationError.message.InvalidEmailAddress"/>
					</xsl:when>
					<xsl:when test="messageKey='UnableToProcessEmail'">
						<xsl:value-of select="$validationError.message.UnableToProcessEmail"/>
					</xsl:when>
					<xsl:when test="messageKey='NoEmailSendersFound'">
						<xsl:value-of select="$validationError.message.NoEmailSendersFound"/>
					</xsl:when>			
					<xsl:when test="messageKey='NoUserConditionConfirmation'">
						<xsl:value-of select="$validationError.message.NoUserConditionConfirmation"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$validationError.message.unknownFault"/>
					</xsl:otherwise>
				</xsl:choose>
			</p>
		</xsl:if>
		
	</xsl:template>	
					
</xsl:stylesheet>