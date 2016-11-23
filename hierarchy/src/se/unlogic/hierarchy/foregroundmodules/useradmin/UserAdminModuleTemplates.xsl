<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:template match="Document">	
		<div class="contentitem">
			<xsl:apply-templates select="UserStatistics"/>
			<xsl:apply-templates select="ShowLetter"/>
			<xsl:apply-templates select="ShowUser"/>
			<xsl:apply-templates select="AddUser"/>
			<xsl:apply-templates select="UpdateUser"/>
			<xsl:apply-templates select="ListUserTypes"/>
		</div>
	</xsl:template>
	
	<xsl:template match="ListUserTypes">
	
		<h1><xsl:value-of select="$i18n.SelectUserType"/></h1>
	
		<p>
			<xsl:value-of select="$i18n.SelectUserType.Description"/>
		</p>
	
		<xsl:apply-templates select="UsersTypeDescriptors/UserTypeDescriptor"/>
	
	</xsl:template>	
	
	<xsl:template match="UserTypeDescriptor">
	
		<p>
			<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/add/{UserTypeID}">
				<xsl:value-of select="Name"/>
			</a>
		</p>
	
	</xsl:template>
	
	<xsl:template match="UserStatistics">
		<h1><xsl:value-of select="../module/name"/></h1>
		
		<xsl:apply-templates select="Letters"/>
		
		<xsl:apply-templates select="validationError"/>
		
		<fieldset>
			<legend><xsl:value-of select="$i18n.statistics"/></legend>
			<table>
				<tr>
					<td><xsl:value-of select="$i18n.totalUserCount"/>:</td>
					<td><xsl:value-of select="userCount"/></td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.disabledUserCount"/>:</td>
					<td><xsl:value-of select="disabledUserCount"/></td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.totalGroupCount"/>:</td>
					<td><xsl:value-of select="groupCount"/></td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.disabledGroupCount"/>:</td>
					<td><xsl:value-of select="disabledGroupCount"/></td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.userProviderCount"/>:</td>
					<td><xsl:value-of select="userProviderCount"/></td>
				</tr>												
			</table>
		</fieldset>
		
		<xsl:if test="canAddUser='true'">
			<div class="floatright marginright">
				<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/listtypes" title="{$i18n.addUser}">
					<xsl:value-of select="$i18n.addUser"/>
					<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/user_add.png"/>
				</a>
			</div>
			
			<div class="floatright marginright clearboth">
				<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/email-list" title="{$i18n.downloadEmailList}">
					<xsl:value-of select="$i18n.downloadEmailList"/>
					<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/mail.png"/>
				</a>
			</div>					
		</xsl:if>
				
	</xsl:template>
	
	<xsl:template match="ShowLetter">
	
		<h1><xsl:value-of select="../module/name"/>  (<xsl:value-of select="currentLetter"/>)</h1>
		
		<xsl:apply-templates select="Letters"/>
		
		<xsl:choose>
			<xsl:when test="Users">
				<br/>
				<xsl:apply-templates select="Users/user" mode="list"/>
				<br/>
			</xsl:when>
			<xsl:otherwise>
				<p><xsl:value-of select="$i18n.noUsersFound"/></p>
			</xsl:otherwise>
		</xsl:choose>	
	
		<xsl:if test="canAddUser='true'">
			<div class="floatright marginright">
				<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/listtypes" title="{$i18n.addUser}">
					<xsl:value-of select="$i18n.addUser"/>
					<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/user_add.png"/>
				</a>
			</div>		
		</xsl:if>
	
	</xsl:template>
	
	<xsl:template match="Letters">
	
		<div class="floatright">
			<xsl:choose>
				<xsl:when test="filteringField = 'FIRSTNAME'">
					<xsl:value-of select="$i18n.firstnameIndex" />
				</xsl:when>
				<xsl:when test="filteringField = 'LASTNAME'">
					<xsl:value-of select="$i18n.lastnameIndex" />
				</xsl:when>
				<xsl:when test="filteringField = 'USERNAME'">
					<xsl:value-of select="$i18n.usernameIndex" />
				</xsl:when>
				<xsl:when test="filteringField = 'EMAIL'">
					<xsl:value-of select="$i18n.emailIndex" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$i18n.unknownIndex" />
				</xsl:otherwise>
			</xsl:choose>
			
			<xsl:text>: </xsl:text><xsl:apply-templates select="Letter"/>
		</div>
		
		<br/>
	</xsl:template>
	
	<xsl:template match="Letter">
	
		<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/letter/{.}">
			<xsl:if test="../../currentLetter = .">
				<xsl:attribute name="class">border</xsl:attribute>
			</xsl:if>
			<xsl:value-of select="."/>
		</a>
		
		<xsl:text> </xsl:text>
	
	</xsl:template>
	
	<xsl:template match="ShowUser">		
		
		<xsl:apply-templates select="user" mode="adminLinks">
			<xsl:with-param name="allowAdminAdministration" select="allowAdminAdministration"/>
			<xsl:with-param name="allowUserSwitching" select="allowUserSwitching"/>
		</xsl:apply-templates>
		
		<h1><xsl:value-of select="user/firstname"/> <xsl:text>&#x20;</xsl:text> <xsl:value-of select="user/lastname"/></h1>
		
			<xsl:choose>
				<xsl:when test="ViewFragment">
					
					<xsl:value-of select="ViewFragment/HTML" disable-output-escaping="yes"/>
					
				</xsl:when>
				<xsl:otherwise>
		
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
					
					<br/>
					
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
					
					<xsl:if test="user/Attributes">
						
						<br/>
						
						<h2><xsl:value-of select="$i18n.attributes"/></h2>
						
						<table class="border">
							<tr>
								<th><xsl:value-of select="$i18n.name"/></th>
								<th><xsl:value-of select="$i18n.value"/></th>
							</tr>
							
							<xsl:apply-templates select="user/Attributes/Attribute" mode="show"/>
							
						</table>
						
					</xsl:if>					
					
					<xsl:apply-templates select="user/groups" mode="show"/>		
				
				</xsl:otherwise>
			</xsl:choose>
		
	</xsl:template>	
		
	<xsl:template match="Attribute" mode="show">
		
		<tr>
			<td><xsl:value-of select="Name"/></td>
			<td><xsl:value-of select="Value"/></td>
		</tr>
	
	</xsl:template>		
		
	<xsl:template match="user" mode="list">
		
		<div class="floatleft full marginbottom border">
			<div class="floatleft">
				<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/show/{userID}" title="{$i18n.viewUser}: {firstname} {lastname}">
					
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
				</a>			
			</div>

			<xsl:apply-templates select="." mode="adminLinks">
				<xsl:with-param name="allowAdminAdministration" select="../../allowAdminAdministration"/>
				<xsl:with-param name="allowUserSwitching" select="../../allowUserSwitching"/>
			</xsl:apply-templates>
		</div>		
	</xsl:template>
	
	<xsl:template match="user" mode="adminLinks">

			<xsl:param name="allowAdminAdministration"/>
			<xsl:param name="allowUserSwitching"/>

			<div class="floatright marginright">
				
				<xsl:if test="$allowUserSwitching = 'true'">
					<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/switch/{userID}" title="{$i18n.switchToUser}: {firstname} {lastname}">
						<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/switch.png"/>
					</a>				
				</xsl:if>
			
				<xsl:choose>
					<xsl:when test="admin='true' and $allowAdminAdministration = 'false'">
						<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/user_edit_locked.png" title="{$i18n.userUpdatedLocked}"/>
						<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/user_delete_locked.png" title="{$i18n.userDeletedLocked}"/>					
					</xsl:when>
					<xsl:otherwise>
					
						<xsl:choose>
							<xsl:when test="hasFormProvider = 'true'">
								<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/update/{userID}" title="{$i18n.editUser}: {firstname} {lastname}">
									<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/user_edit.png"/>
								</a>						
							</xsl:when>
							<xsl:otherwise>
								<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/user_edit_gray.png" title="{$i18n.userCannotBeUpdated}"/>
							</xsl:otherwise>
						</xsl:choose>						
					
						<xsl:choose>
							<xsl:when test="isMutable='true'">
								<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/delete/{userID}" onclick="return confirm('{$i18n.deleteUser}: {firstname} {lastname}?')" title="{$i18n.removeUser}: {firstname} {lastname}">
									<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/user_delete.png"/>
								</a>							
							</xsl:when>
							<xsl:otherwise>
								<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/user_delete_gray.png" title="{$i18n.userCannotBeDeleted}"/>
							</xsl:otherwise>
						</xsl:choose>					
					
					</xsl:otherwise>
					
				</xsl:choose>
			</div>	
	
	</xsl:template>
	
	<xsl:template match="AddUser">		
		
		<h1><xsl:value-of select="$i18n.addUser"/></h1>
		
		<xsl:apply-templates select="validationError"/>
		
		<form method="POST" action="{/document/requestinfo/uri}">

			<xsl:value-of select="ViewFragment/HTML" disable-output-escaping="yes"/>
			
			<div align="right">
				<input type="submit" value="{$i18n.addUser}"/>			
			</div>
		</form>
	</xsl:template>
	
	<xsl:template match="UpdateUser">		
		
		<h1><xsl:value-of select="$i18n.updateUser" /><xsl:text> </xsl:text><xsl:value-of select="user/firstname"/> <xsl:text>&#x20;</xsl:text> <xsl:value-of select="user/lastname"/></h1>
		
		<xsl:apply-templates select="validationError"/>
				
		<form method="POST" action="{/document/requestinfo/uri}" name="userform">

			<xsl:value-of select="ViewFragment/HTML" disable-output-escaping="yes"/>
			
			<div align="right">
				<input type="submit" value="{$i18n.saveChanges}"/>			
			</div>
		</form>
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
					<xsl:when test="messageKey='NoFormAddableUserTypesAvailable'">
						<xsl:value-of select="$i18n.NoFormAddableUserTypesAvailable"/><xsl:text>!</xsl:text>
					</xsl:when>
					<xsl:when test="messageKey='RequestedUserTypeNotFound'">
						<xsl:value-of select="$i18n.RequestedUserTypeNotFound"/><xsl:text>!</xsl:text>
					</xsl:when>
					<xsl:when test="messageKey='UpdateFailedUserNotUpdatable'">
						<xsl:value-of select="$i18n.UpdateFailedUserNotUpdatable"/><xsl:text>!</xsl:text>
					</xsl:when>
					<xsl:when test="messageKey='DeleteFailedException'">
						<xsl:value-of select="$i18n.DeleteFailedException"/><xsl:text>!</xsl:text>
					</xsl:when>																				
					<xsl:otherwise>
						<xsl:value-of select="$i18n.unknownFault"/>
					</xsl:otherwise>
				</xsl:choose>
			</p>
		</xsl:if>
		
	</xsl:template>				
</xsl:stylesheet>