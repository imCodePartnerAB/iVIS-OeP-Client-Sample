<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/Common.xsl"/>

	<xsl:template match="Document">	

		<xsl:apply-templates select="ShowUser"/>
		<xsl:apply-templates select="AddUser"/>
		<xsl:apply-templates select="UpdateUser"/>

	</xsl:template>
		
	<xsl:template match="ShowUser">		
				
		<table>
			<tr>
				<td><xsl:value-of select="$i18n.username" />:</td>
				<td>
					<xsl:value-of select="user/username"/>
				</td>
			</tr>			
			<tr>
				<td><xsl:value-of select="$i18n.firstname" />:</td>
				<td>
					<xsl:value-of select="user/firstname"/>
				</td>
			</tr>
			<tr>
				<td><xsl:value-of select="$i18n.lastname" />:</td>
				<td>
					<xsl:value-of select="user/lastname"/>
				</td>
			</tr>
			<tr>
				<td><xsl:value-of select="$i18n.emailAddress" />:</td>
				<td>
					<xsl:value-of select="user/email"/>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<input type="checkbox" name="enabled" value="true" disabled="true">
						<xsl:if test="user/enabled='true'">
							<xsl:attribute name="checked"/>
						</xsl:if>
					</input>
					<xsl:text>&#x20;</xsl:text>
					<xsl:value-of select="$i18n.enabled" />
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<input type="checkbox" name="admin" value="true" disabled="true">
						<xsl:if test="user/admin='true'">
							<xsl:attribute name="checked"/>
						</xsl:if>
					</input>
					<xsl:text>&#x20;</xsl:text>
					<xsl:value-of select="$i18n.administrator" />
				</td>
			</tr>										
		</table>
		
		<table>
			<tr>
				<td><xsl:value-of select="$i18n.accountCreated" />:</td>
				<td><xsl:value-of select="user/added"/></td>
			</tr>
			<tr>
				<td><xsl:value-of select="$i18n.lastLogin" />:</td>
				<td><xsl:value-of select="user/lastLogin"/></td>
			</tr>				
		</table>
		
		<br/>
				
		<xsl:if test="user/Attributes and AttrbuteDescriptors">
			
			<br/>
			
			<h2><xsl:value-of select="$i18n.attributes"/></h2>
			
			<table class="border">
				<tr>
					<th><xsl:value-of select="$i18n.name"/></th>
					<th><xsl:value-of select="$i18n.value"/></th>
				</tr>
				
				<xsl:apply-templates select="AttrbuteDescriptors/AttributeDescriptor" mode="show"/>
				
			</table>
			
		</xsl:if>
		
		<xsl:apply-templates select="user/groups" mode="show"/>		
				
	</xsl:template>	
						
	<xsl:template match="AddUser">		
		
		<xsl:call-template name="EditUser">
			<xsl:with-param name="mode" select="'add'"/>
		</xsl:call-template>

	</xsl:template>
	
	<xsl:template match="UpdateUser">		
		
		<xsl:call-template name="EditUser">
			<xsl:with-param name="mode" select="'update'"/>
		</xsl:call-template>

	</xsl:template>	
	
	<xsl:template name="EditUser">
	
		<xsl:param name="mode"/>
	
		<xsl:apply-templates select="validationException/validationError"/>
		
		<table>
			
			<tr>
				<td><xsl:value-of select="$i18n.username"/>:</td>
				<td>
					<xsl:call-template name="createTextField">
						<xsl:with-param name="name" select="'username'"/>
						<xsl:with-param name="size" select="20"/>
						<xsl:with-param name="element" select="user"/>
					</xsl:call-template>
				</td>
			</tr>
			
			<xsl:if test="$mode = 'update'">
				<tr>
					<td colspan="2"><input type="checkbox" onclick="document.userform.password.disabled=!this.checked;document.userform.passwordconfirmation.disabled=!this.checked"/><xsl:value-of select="$i18n.changePassword" /></td>
				</tr>				
			</xsl:if>
	
			<tr>
				<td><xsl:value-of select="$i18n.password"/>:</td>
				<td>
					<input type="password" name="password" size="20">
						
						<xsl:if test="$mode = 'update'">
							
							<xsl:attribute name="disabled">true</xsl:attribute>
							
						</xsl:if>
												
					</input>
				</td>
			</tr>
			<tr>
				<td><xsl:value-of select="$i18n.passwordConfirmation"/>:</td>
				<td>
					<input type="password" name="passwordconfirmation" size="20">
					
						<xsl:if test="$mode = 'update'">
							
							<xsl:attribute name="disabled">true</xsl:attribute>
							
						</xsl:if>
					
					</input>
				</td>
			</tr>
			
			<tr>
				<td><xsl:value-of select="$i18n.firstname"/>:</td>
				<td>
					<xsl:call-template name="createTextField">
						<xsl:with-param name="name" select="'firstname'"/>
						<xsl:with-param name="size" select="20"/>
						<xsl:with-param name="element" select="user"/>
					</xsl:call-template>
				</td>
			</tr>
			<tr>
				<td><xsl:value-of select="$i18n.lastname"/>:</td>
				<td>
					<xsl:call-template name="createTextField">
						<xsl:with-param name="name" select="'lastname'"/>
						<xsl:with-param name="size" select="20"/>
						<xsl:with-param name="element" select="user"/>
					</xsl:call-template>
				</td>
			</tr>
			
			<tr>
				<td><xsl:value-of select="$i18n.emailAddress"/>:</td>
				<td>
					<xsl:call-template name="createTextField">
						<xsl:with-param name="name" select="'email'"/>
						<xsl:with-param name="size" select="20"/>
						<xsl:with-param name="element" select="user"/>
					</xsl:call-template>					
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<xsl:call-template name="createCheckbox">
						<xsl:with-param name="name" select="'enabled'"/>
						<xsl:with-param name="element" select="user"/>							
					</xsl:call-template>
					<xsl:text>&#x20;</xsl:text>
					<xsl:value-of select="$i18n.enabled"/>
				</td>
			</tr>
			
			<xsl:if test="AllowAdminFlagAccess">
				<tr>
					<td colspan="2">
						<xsl:call-template name="createCheckbox">
							<xsl:with-param name="name" select="'admin'"/>
							<xsl:with-param name="element" select="user"/>							
						</xsl:call-template>
					
						<xsl:text>&#x20;</xsl:text>
						<xsl:value-of select="$i18n.administrator"/>
					</td>
				</tr>
			</xsl:if>
					
		</table>
		
		<xsl:if test="AttrbuteDescriptors">
			
			<br/>
			
			<h2><xsl:value-of select="$i18n.attributes"/></h2>
			
			<table>
								
				<xsl:apply-templates select="AttrbuteDescriptors/AttributeDescriptor" mode="update"/>
				
			</table>
			
		</xsl:if>		
		
		<br/>
		
		<xsl:apply-templates select="Groups"/>	
	
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
					<xsl:with-param name="disabled" select="AttributeMode = 'DISABLED'"/>
				</xsl:call-template>				
			</td>
		</tr>
		
	</xsl:template>	
	
	<xsl:template match="AttributeDescriptor" mode="show">
		
		<xsl:variable name="name" select="Name"/>
		
		<xsl:variable name="value" select="../../user/Attributes/Attribute[Name = $name]/Value"/>
		
		<xsl:if test="$value">
		
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
				</td>
				<td><xsl:value-of select="$value"/></td>
			</tr>		
		
		</xsl:if>
	
	</xsl:template>	
	
	<xsl:template match="Groups">
		<h2><xsl:value-of select="$i18n.groups" /></h2>

		<div class="scrolllist">			
			<xsl:apply-templates select="group"/>
		</div>
		
		<br/>
	</xsl:template>
	
	<xsl:template match="groups" mode="show">
		
		<br/>
		
		<h2><xsl:value-of select="$i18n.groups" /></h2>

		<div>			
			<xsl:apply-templates select="group" mode="show"/>
		</div>
		
		<br/>
	</xsl:template>
	
	<xsl:template match="group">
		<div class="floatleft full border marginbottom">
			<div class="floatleft">
				<xsl:choose>
					<xsl:when test="enabled='true'">
						<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/group.png"/>
					</xsl:when>
					<xsl:otherwise>
						<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/group_disabled.png"/>
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:text>&#x20;</xsl:text>
				
				<xsl:value-of select="name"/>			
			</div>
			<div class="floatright marginright">
				
				<xsl:variable name="groupID" select="groupID"/>
			
				<input type="checkbox" name="group" value="{groupID}">
					<xsl:choose>
						<xsl:when test="../../requestparameters">
							<xsl:if test="../../requestparameters/parameter[name='group'][value=$groupID]">
								<xsl:attribute name="checked"/>
							</xsl:if>						
						</xsl:when>
						<xsl:when test="../../user">
							<xsl:if test="../../user/groups/group[groupID=$groupID]">
								<xsl:attribute name="checked"/>
							</xsl:if>								
						</xsl:when>
					</xsl:choose>
				</input>
			</div>				
		</div>
	</xsl:template>	
	
	<xsl:template match="group" mode="show">
		<div class="floatleft full border marginbottom">
			<div class="floatleft">
				<xsl:choose>
					<xsl:when test="enabled='true'">
						<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/group.png"/>
					</xsl:when>
					<xsl:otherwise>
						<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/group_disabled.png"/>
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:text>&#x20;</xsl:text>
				
				<xsl:value-of select="name"/>			
			</div>				
		</div>
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
						<xsl:value-of select="$i18n.emailAddress"/>
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
					<xsl:when test="messageKey='RequestedUserNotFound'">
						<xsl:value-of select="$i18n.requestedUserNotFound"/><xsl:text>!</xsl:text>
					</xsl:when>				
					<xsl:when test="messageKey='UpdateFailedUserNotFound'">
						<xsl:value-of select="$i18n.userToUpdateNotFound"/><xsl:text>!</xsl:text>
					</xsl:when>
					<xsl:when test="messageKey='DeleteFailedUserNotFound'">
						<xsl:value-of select="$i18n.userToRemoveNotFound"/><xsl:text>!</xsl:text>
					</xsl:when>
					<xsl:when test="messageKey='UsernameAlreadyTaken'">
						<xsl:value-of select="$i18n.usernameAlreadyTaken"/><xsl:text>!</xsl:text>
					</xsl:when>
					<xsl:when test="messageKey='EmailAlreadyTaken'">
						<xsl:value-of select="$i18n.emailAlreadyTaken"/><xsl:text>!</xsl:text>
					</xsl:when>
					<xsl:when test="messageKey='PasswordConfirmationMissMatch'">
						<xsl:value-of select="$i18n.passwordConfirmationMissMatch"/><xsl:text>!</xsl:text>
					</xsl:when>
					<xsl:when test="messageKey='UnableToAddUsers'">
						<xsl:value-of select="$i18n.unableToAddUsers"/><xsl:text>!</xsl:text>
					</xsl:when>																												
					<xsl:otherwise>
						<xsl:value-of select="$i18n.unknownFault"/>
					</xsl:otherwise>
				</xsl:choose>
			</p>
		</xsl:if>
		
	</xsl:template>				
</xsl:stylesheet>