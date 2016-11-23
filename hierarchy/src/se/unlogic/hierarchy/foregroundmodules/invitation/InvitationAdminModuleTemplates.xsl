<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/Common.xsl"/>
	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/CKEditor.xsl"/>

	<xsl:variable name="globalscripts">
		/jquery/jquery.js
		/ckeditor/ckeditor.js
		/ckeditor/adapters/jquery.js
		/ckeditor/init.js		
	</xsl:variable>	

	<xsl:template match="Document">	
		<div class="contentitem">
			<xsl:apply-templates select="InvitationList"/>
			<xsl:apply-templates select="AddInvitationType"/>
			<xsl:apply-templates select="UpdateInvitationType"/>
			<xsl:apply-templates select="AddInvitation"/>
			<xsl:apply-templates select="UpdateInvitation"/>			
		</div>
	</xsl:template>
	
	<xsl:template match="InvitationList">
			
		<h1><xsl:value-of select="../module/name"/></h1>
		
		<xsl:apply-templates select="validationError"/>
		
		<h2><xsl:value-of select="$i18n.InvitationTypes"/> (<xsl:value-of select="count(InvitationTypes/InvitationType)"/>)</h2>
		
		<xsl:choose>
			<xsl:when test="InvitationTypes">
				<xsl:apply-templates select="InvitationTypes/InvitationType" mode="list"/>
				<br/>
			</xsl:when>
			<xsl:otherwise>
				<p><xsl:value-of select="$i18n.NoInvitationTypesFound"/></p>
			</xsl:otherwise>
		</xsl:choose>
		
		<br/>
		
		<div class="floatright marginright">
			<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/addInvitationType" title="{$i18n.AddInvitationType}">
				<xsl:value-of select="$i18n.AddInvitationType"/>					
			</a>
		</div>			
		
		
		<div class="clearboth"/>
				
		<h2><xsl:value-of select="$i18n.Invitations"/> (<xsl:value-of select="count(Invitations/Invitation)"/>)</h2>
		
		<xsl:choose>
			<xsl:when test="Invitations">
				<xsl:apply-templates select="Invitations/Invitation" mode="list"/>
				<br/>
			</xsl:when>
			<xsl:otherwise>
				<p><xsl:value-of select="$i18n.NoInvitationsFound"/></p>
			</xsl:otherwise>
		</xsl:choose>
		
		<br/>
		
		<xsl:if test="InvitationTypes">
			<div class="floatright marginright">
				<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/addInvitation" title="{$i18n.AddInvitation}">
					<xsl:value-of select="$i18n.AddInvitation"/>	
					<xsl:text>&#x20;</xsl:text>
					<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/user_add.png"/>				
				</a>
			</div>
			
			<xsl:if test="Invitations/Invitation[not(lastSent)]">
				<div class="floatright marginright clearboth">
					<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/sendunsentinvitations" title="{$i18n.SendUnsentInvitations}">
						<xsl:value-of select="$i18n.SendUnsentInvitations"/>
						<xsl:text>&#x20;</xsl:text>	
						<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/mail_send.png"/>					
					</a>
				</div>			
			</xsl:if>
	
			<xsl:if test="Invitations/Invitation/lastSent">
				<div class="floatright marginright clearboth">
					<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/resendsentinvitations" title="{$i18n.ResendSentInvitations}">
						<xsl:value-of select="$i18n.ResendSentInvitations"/>
						<xsl:text>&#x20;</xsl:text>	
						<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/mail_send.png"/>					
					</a>
				</div>			
			</xsl:if>	
	
		</xsl:if>						
				
	</xsl:template>	
	
	<xsl:template match="InvitationType" mode="list">
		
		<div class="floatleft full marginbottom border">
			<div class="floatleft">
				<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/updateInvitationType/{invitationTypeID}" title="{$i18n.ShowUpdateInvitationType}: {name}">
					
					<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/mail.png"/>
					
					<xsl:text>&#x20;</xsl:text>
					
					<xsl:value-of select="name"/>			
				</a>			
			</div>
			<div class="floatright marginright">
				<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/updateInvitationType/{invitationTypeID}" title="{$i18n.ShowUpdateInvitationType}: {name}">
					<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/mail_edit.png"/>
				</a>
				<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/deleteInvitationType/{invitationTypeID}" title="{$i18n.DeleteInvitationType}: {name}?" onclick="return confirm('{$i18n.DeleteInvitationType} &quot;{name}&quot;?')">
					<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/mail_delete.png"/>
				</a>
			</div>				
		</div>		
	</xsl:template>
	
	<xsl:template match="Invitation" mode="list">
		
		<div class="floatleft full marginbottom border">
			<div class="floatleft">
				<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/updateInvitation/{invitationID}" title="{$i18n.ShowUpdateInvitationFor}: {firstname} {lastname} ({email})">
					
					<xsl:choose>
						<xsl:when test="sendCount > 0">
							<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/user_check.png" title="{$i18n.ThisInvitationHasBeenSent} {sendCount} {$i18n.times}. {$i18n.TheLastOneWasSent} {lastSent}."/>
						</xsl:when>
						<xsl:otherwise>
							<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/user_disabled.png" title="{$i18n.ThisInvitationHasNotBeenSentYet}"/>
						</xsl:otherwise>
					</xsl:choose>
					
					<xsl:text>&#x20;</xsl:text>
					
					<xsl:value-of select="firstname"/>
					
					<xsl:text>&#x20;</xsl:text>
					
					<xsl:value-of select="lastname"/>
					
					<xsl:text>&#x20;</xsl:text>
					
					(<xsl:value-of select="email"/>)			
				</a>			
			</div>
			<div class="floatright marginright">
				<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/sendInvitation/{invitationID}" title="{$i18n.SendInvitationTo}: {firstname} {fastname} ({email})">
					<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/mail_send.png"/>
				</a>			
				<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/updateInvitation/{invitationID}" title="{$i18n.ShowUpdateInvitationFor}: {firstname} {lastname} ({email})">
					<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/user_edit.png"/>
				</a>
				<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/deleteInvitation/{invitationID}" title="{$i18n.DeleteInvitationFor}: {firstname} {lastname} ({email})?" onclick="return confirm('{$i18n.DeleteInvitationFor}: &quot;{firstname} {lastname} ({email})&quot;?')">
					<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/user_delete.png"/>
				</a>
			</div>				
		</div>		
	</xsl:template>
	
	<xsl:template match="AddInvitationType">		
		
		<h1><xsl:value-of select="$i18n.AddInvitationType"/></h1>
		
		<xsl:apply-templates select="validationException/validationError"/>
		
		<form method="POST" action="{/document/requestinfo/uri}">

			<xsl:call-template name="manipulateInvitationType"/>
			
			<div align="right">
				<input type="submit" value="{$i18n.AddInvitationType}"/>			
			</div>
		</form>
	</xsl:template>
	
	<xsl:template match="UpdateInvitationType">		
		
		<h1><xsl:value-of select="$i18n.UpdateInvitationType"/></h1>
		
		<xsl:apply-templates select="validationException/validationError"/>
		
		<form method="POST" action="{/document/requestinfo/uri}">
			
			<xsl:call-template name="manipulateInvitationType"/>
			
			<div align="right">
				<input type="submit" value="{$i18n.SaveChanges}"/>			
			</div>
		</form>
	</xsl:template>		
	
	<xsl:template name="manipulateInvitationType">
	
		<table class="full">
			<tr>
				<td width="35%"><xsl:value-of select="$i18n.Name"/>:</td>
				<td>
					<xsl:call-template name="createTextField">
						<xsl:with-param name="name" select="'name'" />
						<xsl:with-param name="element" select="InvitationType" />
					</xsl:call-template>					
				</td>
			</tr>						
			<tr>
				<td><xsl:value-of select="$i18n.Subject"/>:</td>
				<td>
					<xsl:call-template name="createTextField">
						<xsl:with-param name="name" select="'subject'" />
						<xsl:with-param name="element" select="InvitationType" />
					</xsl:call-template>
				</td>
			</tr>
			<tr>
				<td><xsl:value-of select="$i18n.SenderName"/>:</td>
				<td>
					<xsl:call-template name="createTextField">
						<xsl:with-param name="name" select="'senderName'" />
						<xsl:with-param name="element" select="InvitationType" />
					</xsl:call-template>				
				</td>
			</tr>
			<tr>
				<td><xsl:value-of select="$i18n.SenderEmailAddress"/>:</td>
				<td>
					<xsl:call-template name="createTextField">
						<xsl:with-param name="name" select="'senderEmail'" />
						<xsl:with-param name="element" select="InvitationType" />
					</xsl:call-template>				
				</td>
			</tr>
		</table>
			
		<br/>	
			
		<h2><xsl:value-of select="$i18n.Message"/></h2>	
			
		<xsl:call-template name="createTextArea">
			<xsl:with-param name="name" select="'message'"/>
			<xsl:with-param name="class" select="'fckeditor'"/>
			<xsl:with-param name="element" select="InvitationType" />		
		</xsl:call-template>			
		
		<br/>
			
		<fieldset>
			<legend><xsl:value-of select="$i18n.Tags"/></legend>
			<table class="full">
				<tr>
					<td width="35%"><xsl:value-of select="$i18n.RecipientFirstname"/>:</td>
					<td>$recipient-firstname</td>					
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.RecipientLastname"/>:</td>
					<td>$recipient-lastname</td>					
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.RecipientEmail"/>:</td>
					<td>$recipient-email</td>					
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.InvitationLink"/>:</td>
					<td>$invitation-link</td>					
				</tr>															
			</table>
		</fieldset>			
			
		<br/>	
			
		<h2><xsl:value-of select="$i18n.registrationText"/></h2>			
		
		<xsl:call-template name="createTextArea">
			<xsl:with-param name="name" select="'registrationText'"/>
			<xsl:with-param name="class" select="'fckeditor'"/>
			<xsl:with-param name="element" select="InvitationType" />		
		</xsl:call-template>			
		
		<br/>
		
		<h2><xsl:value-of select="$i18n.registeredText"/></h2>
		
		<xsl:call-template name="createTextArea">
			<xsl:with-param name="name" select="'registeredText'"/>
			<xsl:with-param name="class" select="'fckeditor'"/>
			<xsl:with-param name="element" select="InvitationType" />		
		</xsl:call-template>
		
		<xsl:call-template name="initializeFCKEditor">
			<xsl:with-param name="basePath"><xsl:value-of select="/Document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/Document/module/sectionID"/>/<xsl:value-of select="/Document/module/moduleID"/>/ckeditor/</xsl:with-param>
			<xsl:with-param name="customConfig">config.js</xsl:with-param>
			<xsl:with-param name="editorContainerClass">fckeditor</xsl:with-param>
			<xsl:with-param name="editorHeight">400</xsl:with-param>
			<xsl:with-param name="contentsCss">
				<xsl:if test="/Document/cssPath">
					<xsl:value-of select="/Document/cssPath"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>		
		
		<br/>
		
		<xsl:apply-templates select="Groups"/>	
	
	</xsl:template>
	
	<xsl:template match="AddInvitation">		
		
		<h1><xsl:value-of select="$i18n.AddInvitation"/></h1>
		
		<xsl:apply-templates select="validationException/validationError"/>
		
		<form method="POST" action="{/document/requestinfo/uri}">
			
			<xsl:call-template name="manipulateInvitation"/>
				
			<table class="full">						
				<tr>
					<td colspan="2">
						<input type="checkbox" name="send" value="true">
							<xsl:if test="requestparameters/parameter[name='send']">
								<xsl:attribute name="checked"/>
							</xsl:if>
						</input>
						<xsl:text>&#x20;</xsl:text>
						<xsl:text><xsl:value-of select="$i18n.SendInvitationImmediately"/></xsl:text>
					</td>
				</tr>					
			</table>
		
			
			<div align="right">
				<input type="submit" value="{$i18n.AddInvitation}"/>			
			</div>
		</form>
	</xsl:template>
	
	<xsl:template name="addExtraInvitationFields" />
	
	<xsl:template match="UpdateInvitation">		
		
		<h1><xsl:value-of select="$i18n.UpateInvitation"/></h1>
		
		<xsl:apply-templates select="validationException/validationError"/>
		
		<form method="POST" action="{/document/requestinfo/uri}">

			<xsl:call-template name="manipulateInvitation"/>

			<table class="full">
				<tr>
					<td width="35%"><xsl:value-of select="$i18n.Link"/>:</td>
					<td>
						<a href="{invitationURL}" title="{$i18n.InvitationLink}">
							<xsl:value-of select="invitationURL"/>
						</a>
					</td>						
				</tr>														
			</table>
		
			
			<div align="right">
				<input type="submit" value="{$i18n.SaveChanges}"/>			
			</div>
		</form>
	</xsl:template>	
	
	<xsl:template name="manipulateInvitation">
	
		<table class="full">
			<tr>
				<td width="35%"><xsl:value-of select="$i18n.Firstname"/>:</td>
				<td>
					<xsl:call-template name="createTextField">
						<xsl:with-param name="name" select="'firstname'" />
						<xsl:with-param name="element" select="Invitation" />
					</xsl:call-template>			
				</td>
			</tr>
			<tr>
				<td><xsl:value-of select="$i18n.Lastname"/>:</td>
				<td>
					<xsl:call-template name="createTextField">
						<xsl:with-param name="name" select="'lastname'" />
						<xsl:with-param name="element" select="Invitation" />
					</xsl:call-template>
				</td>
			</tr>				
			<tr>
				<td><xsl:value-of select="$i18n.Email"/>:</td>
				<td>
					<xsl:call-template name="createTextField">
						<xsl:with-param name="name" select="'email'" />
						<xsl:with-param name="element" select="Invitation" />
					</xsl:call-template>
				</td>
			</tr>
			<tr>
				<td><xsl:value-of select="$i18n.InvitationType"/>:</td>
				<td>
					<xsl:call-template name="createDropdown">
						<xsl:with-param name="name" select="'invitationType'" />
						<xsl:with-param name="element" select="InvitationTypes/InvitationType"/>
						<xsl:with-param name="valueElementName" select="'invitationTypeID'"/>
						<xsl:with-param name="labelElementName" select="'name'"/>
						<xsl:with-param name="selectedValue" select="Invitation/InvitationType/invitationTypeID"/>
					</xsl:call-template>
				</td>
			</tr>
			<xsl:call-template name="addExtraInvitationFields" />
		</table>	
	
	</xsl:template>
	
	<xsl:template match="Groups">
		<h2><xsl:value-of select="$i18n.Groups"/></h2>

		<div class="scrolllist border">			
			<xsl:apply-templates select="group"/>
		</div>
		
		<br/>
	</xsl:template>
	
	<xsl:template match="group">
		<div class="floatleft full border marginbottom marginleft margintop marginright">
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
						<xsl:when test="../../InvitationType">
							<xsl:if test="../../InvitationType/groupIDs[groupID=$groupID]">
								<xsl:attribute name="checked"/>
							</xsl:if>								
						</xsl:when>
					</xsl:choose>
				</input>
			</div>				
		</div>
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
					<xsl:otherwise>
						<xsl:text><xsl:value-of select="$i18n.ValidationError.UnknownFieldError"/></xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:text>&#x20;</xsl:text>
				
				<xsl:choose>
					<xsl:when test="fieldName = 'name'">
						<xsl:value-of select="$i18n.name"/><xsl:text>!</xsl:text>
					</xsl:when>
					<xsl:when test="fieldName = 'subject'">
						<xsl:value-of select="$i18n.subject"/><xsl:text>!</xsl:text>
					</xsl:when>	
					<xsl:when test="fieldName = 'message'">
						<xsl:value-of select="$i18n.message"/><xsl:text>!</xsl:text>
					</xsl:when>
					<xsl:when test="fieldName = 'senderName'">
						<xsl:value-of select="$i18n.senderName"/><xsl:text>!</xsl:text>
					</xsl:when>																
					<xsl:when test="fieldName = 'senderEmail'">
						<xsl:value-of select="$i18n.senderEmailAddress"/><xsl:text>!</xsl:text>
					</xsl:when>
					<xsl:when test="fieldName = 'email'">
						<xsl:value-of select="$i18n.email"/><xsl:text>!</xsl:text>
					</xsl:when>
					<xsl:when test="fieldName = 'firstname'">
						<xsl:value-of select="$i18n.firstname"/><xsl:text>!</xsl:text>
					</xsl:when>																
					<xsl:when test="fieldName = 'lastname'">
						<xsl:value-of select="$i18n.lastname"/><xsl:text>!</xsl:text>
					</xsl:when>
					<xsl:when test="fieldName = 'registrationText'">
						<xsl:value-of select="$i18n.registrationText"/><xsl:text>!</xsl:text>
					</xsl:when>																
					<xsl:when test="fieldName = 'registeredText'">
						<xsl:value-of select="$i18n.registeredText"/><xsl:text>!</xsl:text>
					</xsl:when>																																																		
					<xsl:otherwise>
						<xsl:value-of select="fieldName"/><xsl:text>!</xsl:text>
					</xsl:otherwise>
				</xsl:choose>			
			</p>
		</xsl:if>
		
		<xsl:if test="messageKey">
			<p class="error">
				<xsl:choose>
					<xsl:when test="messageKey='UpdateFailedInvitationNotFound'">
						<xsl:value-of select="$i18n.ValidationError.Message.InvitationNotFound"/><xsl:text>!</xsl:text>
					</xsl:when>
					<xsl:when test="messageKey='UpdateFailedInvitationTypeNotFound'">
						<xsl:value-of select="$i18n.ValidationError.Message.InvitationTypeNotFound"/><xsl:text>!</xsl:text>
					</xsl:when>					
					<xsl:when test="messageKey='DeleteFailedInvitationNotFound'">
						<xsl:value-of select="$i18n.ValidationError.Message.InvitationNotFound"/><xsl:text>!</xsl:text>
					</xsl:when>
					<xsl:when test="messageKey='DeleteFailedInvitationTypeNotFound'">
						<xsl:value-of select="$i18n.ValidationError.Message.InvitationTypeNotFound"/><xsl:text>!</xsl:text>
					</xsl:when>
					<xsl:when test="messageKey='EmailAlreadyTaken'">
						<xsl:value-of select="$i18n.ValidationError.Message.EmailAlreadyInvited"/>
					</xsl:when>											
					<xsl:otherwise>
						<xsl:value-of select="$i18n.ValidationError.Message.UnknownError"/>
					</xsl:otherwise>
				</xsl:choose>
			</p>
		</xsl:if>
		
	</xsl:template>
						
</xsl:stylesheet>