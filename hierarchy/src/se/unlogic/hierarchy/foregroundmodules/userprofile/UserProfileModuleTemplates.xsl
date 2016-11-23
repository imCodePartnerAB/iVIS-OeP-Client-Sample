<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/Common.xsl"/>

	<xsl:template match="Document">	
		<div class="contentitem">
			<h1><xsl:value-of select="module/name"/></h1>
			<xsl:apply-templates select="UpdateUser"/>
			<xsl:apply-templates select="UserNotFound"/>
		</div>
	</xsl:template>
	
	<xsl:template match="UserNotFound">
	
		<p><xsl:value-of select="$i18n.UserNotFound"/></p>
	
	</xsl:template>
	
	<xsl:template match="UpdateUser">		
		
		<xsl:apply-templates select="validationException/validationError"/>
		
		<xsl:if test="UserUpdated">
			<p><strong><xsl:value-of select="$i18n.UserUpdatedMessage"/></strong></p>
		</xsl:if>
		
		<xsl:if test="UserNotMutable">
			<p><xsl:value-of select="$i18n.UserNotMutable"/></p>
		</xsl:if>		
		
		<form method="POST" action="{/document/requestinfo/uri}" name="userform">
			<table>
			
				<xsl:if test="FirstnameFieldMode != 'HIDDEN'">
					<tr>
						<td>
							<xsl:value-of select="$i18n.firstname"/>
							
							<xsl:if test="FirstnameFieldMode = 'REQUIRED'">
								<span class="required">*</span>
							</xsl:if>
							
							<xsl:text>:</xsl:text>
						</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'firstname'"/>
								<xsl:with-param name="size" select="40"/>
								<xsl:with-param name="element" select="user"/>
								
								<xsl:with-param name="disabled">
									<xsl:if test="FirstnameFieldMode = 'DISABLED' or UserNotMutable">true</xsl:if>
								</xsl:with-param>
								
							</xsl:call-template>
						</td>
					</tr>
				</xsl:if>
				
				<xsl:if test="LastnameFieldMode != 'HIDDEN'">
					<tr>
						<td>
							<xsl:value-of select="$i18n.lastname"/>
							
							<xsl:if test="LastnameFieldMode = 'REQUIRED'">
								<span class="required">*</span>
							</xsl:if>
							
							<xsl:text>:</xsl:text>
						</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'lastname'"/>
								<xsl:with-param name="size" select="40"/>
								<xsl:with-param name="element" select="user"/>
								
								<xsl:with-param name="disabled">
									<xsl:if test="LastnameFieldMode = 'DISABLED' or UserNotMutable">true</xsl:if>
								</xsl:with-param>
								
							</xsl:call-template>
						</td>
					</tr>
				</xsl:if>			
			
				<xsl:if test="UsernameFieldMode != 'HIDDEN'">
					<tr>
						<td>
							<xsl:value-of select="$i18n.username"/>
							
							<xsl:if test="UsernameFieldMode = 'REQUIRED'">
								<span class="required">*</span>
							</xsl:if>
							
							<xsl:text>:</xsl:text>
						</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'username'"/>
								<xsl:with-param name="size" select="40"/>
								<xsl:with-param name="element" select="user"/>
								
								<xsl:with-param name="disabled">
									<xsl:if test="UsernameFieldMode = 'DISABLED' or UserNotMutable">true</xsl:if>
								</xsl:with-param>
								
							</xsl:call-template>
						</td>
					</tr>
				</xsl:if>
				
				<xsl:if test="AllowPasswordChanging and not(UserNotMutable)">
	
					<tr>
						<td colspan="2"><input name="changepassword" value="true" type="checkbox" onclick="document.userform.password.disabled=!this.checked;document.userform.passwordconfirmation.disabled=!this.checked"/><xsl:value-of select="$i18n.changePassword" /></td>
					</tr>
			
					<tr>
						<td><xsl:value-of select="$i18n.password"/>:</td>
						<td>
							<input type="password" name="password" size="40">
								<xsl:attribute name="disabled">true</xsl:attribute>
							</input>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.passwordConfirmation"/>:</td>
						<td>
							<input type="password" name="passwordconfirmation" size="40">
								<xsl:attribute name="disabled">true</xsl:attribute>
							</input>
						</td>
					</tr>
				</xsl:if>
				
				<xsl:if test="EmailFieldMode != 'HIDDEN'">				
					<tr>
						<td>
							<xsl:value-of select="$i18n.emailAddress"/>
							
							<xsl:if test="EmailFieldMode = 'REQUIRED'">
								<span class="required">*</span>
							</xsl:if>
							
							<xsl:text>:</xsl:text>
						</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'email'"/>
								<xsl:with-param name="size" select="40"/>
								<xsl:with-param name="element" select="user"/>
								
								<xsl:with-param name="disabled">
									<xsl:if test="EmailFieldMode = 'DISABLED' or UserNotMutable">true</xsl:if>
								</xsl:with-param>								
							</xsl:call-template>					
						</td>
					</tr>
				</xsl:if>
				
				<xsl:apply-templates select="AttrbuteDescriptors/AttributeDescriptor"/>
			</table>
						
			<br/>
			
			<div align="right">
				<input type="submit" value="{$i18n.saveChanges}"/>			
			</div>
		</form>
	</xsl:template>	
	
	<xsl:template match="AttributeDescriptor">
		
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
				
				<xsl:if test="AttributeMode = 'REQUIRED'">
					<span class="required">*</span>
				</xsl:if>
				
				<xsl:text>:</xsl:text>
			</td>
			<td>
				<xsl:call-template name="createTextField">
					<xsl:with-param name="name"><xsl:value-of select="'attribute-'"/><xsl:value-of select="Name"/></xsl:with-param>
					<xsl:with-param name="requestparameters" select="../../requestparameters"/>
					<xsl:with-param name="size" select="40"/>
					<xsl:with-param name="value" select="../../user/Attributes/Attribute[Name = $name]/Value"/>
					<xsl:with-param name="disabled" select="AttributeMode = 'DISABLED' or ../../UserNotMutable"/>					
				</xsl:call-template>				
			</td>
		</tr>
		
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
					<xsl:when test="displayName">
						<xsl:value-of select="displayName"/>
					</xsl:when>
					<xsl:when test="fieldName = 'firstname'">
						<xsl:value-of select="$i18n.firstname"/>
					</xsl:when>
					<xsl:when test="fieldName = 'lastname'">
						<xsl:value-of select="$i18n.lastname"/>
					</xsl:when>	
					<xsl:when test="fieldName = 'username'">
						<xsl:value-of select="$i18n.username"/>
					</xsl:when>
					<xsl:when test="fieldName = 'password'">
						<xsl:value-of select="$i18n.password"/>
					</xsl:when>																
					<xsl:when test="fieldName = 'email'">
						<xsl:value-of select="$i18n.email"/>
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
					<xsl:when test="messageKey='UsernameAlreadyTaken'">
						<xsl:value-of select="$i18n.usernameAlreadyTaken"/><xsl:text>!</xsl:text>
					</xsl:when>
					<xsl:when test="messageKey='EmailAlreadyTaken'">
						<xsl:value-of select="$i18n.emailAlreadyTaken"/><xsl:text>!</xsl:text>
					</xsl:when>
					<xsl:when test="messageKey='PasswordConfirmationMissMatch'">
						<xsl:value-of select="$i18n.passwordConfirmationMissMatch"/><xsl:text>!</xsl:text>
					</xsl:when>									
					<xsl:otherwise>
						<xsl:value-of select="$i18n.unknownFault"/>
					</xsl:otherwise>
				</xsl:choose>
			</p>
		</xsl:if>
	</xsl:template>				
</xsl:stylesheet>