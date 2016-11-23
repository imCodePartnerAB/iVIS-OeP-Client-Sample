<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/Common.xsl"/>

	<xsl:variable name="globalscripts">
		/jquery/jquery.js
		/jquery/jquery-ui.js
	</xsl:variable>

	<xsl:variable name="scripts">
		/js/UserGroupList.js
	</xsl:variable>
	
	<xsl:variable name="links">
		/css/UserGroupList.css
	</xsl:variable>

	<xsl:template match="Document">
		<div class="contentitem">			
			<xsl:apply-templates select="Groups"/>
			<xsl:apply-templates select="AddGroup"/>
			<xsl:apply-templates select="UpdateGroup"/>
			<xsl:apply-templates select="ShowGroup"/>
			<xsl:apply-templates select="SetGroupUsers"/>
		</div>			
	</xsl:template>
	
	<xsl:template match="Groups">
				
		<h1><xsl:value-of select="../module/name"/> (<xsl:value-of select="count(group)"/>)</h1>
		
		<xsl:apply-templates select="validationError"/>
		
		<xsl:choose>
			<xsl:when test="group">
				<xsl:apply-templates select="group" mode="list"/>
				<br/>
			</xsl:when>
			<xsl:otherwise>
				<p><xsl:value-of select="$noGroupsFound"/></p>
			</xsl:otherwise>
		</xsl:choose>
		
		<br/><br/>
		
		<xsl:if test="canAddGroup='true'">
			<div class="floatright marginright">
				<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/add" title="{$addGroup}">
					<xsl:value-of select="$addGroup"/>
					<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/group_add.png"/>
				</a>
			</div>		
		</xsl:if>
		
	</xsl:template>	
	
	<xsl:template match="group" mode="list">
		
		<div class="floatleft full marginbottom border">
			<div class="floatleft">
				<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/show/{groupID}" title="{$viewGroup}: {name}">
					
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
				</a>			
			</div>
			
			<xsl:apply-templates select="." mode="adminLinks"/>
		</div>		
	</xsl:template>
	
	<xsl:template match="group" mode="adminLinks">
	
		<div class="floatright marginright">
			<xsl:choose>
				<xsl:when test="isMutable='true'">
					<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/update/{groupID}" title="{$editGroup}: {name}">
						<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/group_edit.png"/>
					</a>
					<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/delete/{groupID}" title="{$deleteGroup}: {name}" onclick="return confirm('{$removeGroup}: {name}?')">
						<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/group_delete.png"/>
					</a>				
				</xsl:when>
				<xsl:otherwise>
					<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/setusers/{groupID}" title="{$limitedEditGroup}: {name}">
						<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/group_edit.png"/>
					</a>
					<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/group_delete_gray.png" title="{$groupCannotBeDeleted}"/>				
				</xsl:otherwise>
			</xsl:choose>
		</div>	
	
	</xsl:template>
	
	<xsl:template match="AddGroup">		
		
		<h1><xsl:value-of select="$addGroup"/></h1>
		
		<xsl:apply-templates select="validationException/validationError"/>
		
		<form method="POST" action="{/document/requestinfo/uri}">
			<table>
				<tr>
					<td><xsl:value-of select="$name"/>:</td>
					<td><input type="text" name="name" size="40" value="{requestparameters/parameter[name='name']/value}"/></td>
				</tr>
				<tr>
					<td><xsl:value-of select="$description"/>:</td>
					<td><input type="text" name="description" size="40" value="{requestparameters/parameter[name='description']/value}"/></td>
				</tr>					
				<tr>
					<td colspan="2">
						<input type="checkbox" name="enabled" value="true">
							<xsl:if test="requestparameters/parameter[name='enabled']">
								<xsl:attribute name="checked"/>
							</xsl:if>
						</input>
						<xsl:text>&#x20;</xsl:text>
						<xsl:value-of select="$enabled"/>
					</td>
				</tr>		
			</table>
			
			<xsl:if test="AttributeDescriptors">
			
				<br/>
				
				<h2><xsl:value-of select="$attributes"/></h2>
				
				<table>
									
					<xsl:apply-templates select="AttributeDescriptors/AttributeDescriptor" mode="update"/>
					
				</table>
				
			</xsl:if>
						
			<br/>
			
			<xsl:call-template name="Users"/>
			
			<xsl:call-template name="AddEditFormData"/>
			
			<div align="right">
				<input type="submit" value="{$addGroup}"/>			
			</div>
		</form>
	</xsl:template>
	
	<xsl:template match="UpdateGroup">		
		
		<h1><xsl:value-of select="$updateGroup"/><xsl:text> </xsl:text><xsl:value-of select="group/name"/></h1>
		
		<xsl:apply-templates select="validationException/validationError"/>
		
		<form method="POST" action="{/document/requestinfo/uri}">
			<table>
				<tr>
					<td><xsl:value-of select="$name"/>:</td>
					<td>
						<input type="text" name="name" size="40">
							<xsl:attribute name="value">
								<xsl:choose>
									<xsl:when test="requestparameters">
										<xsl:value-of select="requestparameters/parameter[name='name']/value"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="group/name"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</input>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$description"/></td>
					<td>
						<input type="text" name="description" size="40">
							<xsl:attribute name="value">
								<xsl:choose>
									<xsl:when test="requestparameters">
										<xsl:value-of select="requestparameters/parameter[name='description']/value"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="group/description"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</input>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<input type="checkbox" name="enabled" value="true">
							<xsl:choose>
								<xsl:when test="requestparameters">
									<xsl:if test="requestparameters/parameter[name='enabled']">
										<xsl:attribute name="checked"/>
									</xsl:if>
								</xsl:when>
								<xsl:when test="group/enabled='true'">
									<xsl:attribute name="checked"/>
								</xsl:when>
							</xsl:choose>
						</input>
						<xsl:text>&#x20;</xsl:text>
						<xsl:value-of select="$enabled"/>
					</td>
				</tr>						
			</table>
			
			<xsl:if test="AttributeDescriptors">
			
				<br/>
				
				<h2><xsl:value-of select="$attributes"/></h2>
				
				<table>
									
					<xsl:apply-templates select="AttributeDescriptors/AttributeDescriptor" mode="update"/>
					
				</table>
				
			</xsl:if>		
			
			<br/>
			
			<xsl:call-template name="Users"/>
			
			<xsl:call-template name="AddEditFormData"/>
			
			<div align="right">
				<input type="submit" value="{$saveChanges}"/>			
			</div>
		</form>
	</xsl:template>	
	
	<xsl:template name="AddEditFormData"/>
	
	<xsl:template match="Attribute" mode="update">
		
		<xsl:param name="requestparameters"/>
		
		<xsl:variable name="name" select="."/>
		
		<tr>
			<td>
				<xsl:value-of select="."/>
			</td>
			<td>
				<xsl:call-template name="createTextField">
					<xsl:with-param name="name"><xsl:value-of select="'attribute-'"/><xsl:value-of select="."/></xsl:with-param>
					<xsl:with-param name="requestparameters" select="$requestparameters"/>
					<xsl:with-param name="size" select="40"/>
					<xsl:with-param name="value" select="../../group/Attributes/Attribute[Name = $name]/Value"/>
				</xsl:call-template>				
			</td>
		</tr>
		
	</xsl:template>
	
	<xsl:template match="SetGroupUsers">		
		
		<h1><xsl:value-of select="$updateGroup"/><xsl:text> </xsl:text><xsl:value-of select="group/name"/></h1>
		
		<p>
			<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/info.png"/>
			<xsl:value-of select="$groupCannotBeEditedInfo"/>
		</p>
		
		<form method="POST" action="{/document/requestinfo/uri}">
			<table>
				<tr>
					<td><xsl:value-of select="$name"/>:</td>
					<td>
						<xsl:value-of select="group/name"/>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$description"/></td>
					<td>
						<xsl:value-of select="group/description"/>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<input type="checkbox" name="enabled" value="true" disabled="true">
							<xsl:if test="group/enabled='true'">
								<xsl:attribute name="checked"/>
							</xsl:if>
						</input>
						<xsl:text>&#x20;</xsl:text>
						<xsl:value-of select="$enabled"/>
					</td>
				</tr>						
			</table>
			
			<br/>
			
			<xsl:apply-templates select="Users"/>
			
			<div align="right">
				<input type="submit" value="{$saveChanges}"/>			
			</div>
		</form>
	</xsl:template>		
	
	<xsl:template match="ShowGroup">		
		
		<h1><xsl:value-of select="group/name"/></h1>
		
		<xsl:apply-templates select="group" mode="adminLinks"/>
		
		<table>
			<tr>
				<td><xsl:value-of select="$name"/>:</td>
				<td>
					<xsl:value-of select="group/name"/>
				</td>
			</tr>
			<tr>
				<td><xsl:value-of select="$description"/>:</td>
				<td>
					<xsl:value-of select="group/description"/>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<input type="checkbox" name="enabled" value="true" disabled="true">
						<xsl:if test="group/enabled='true'">
							<xsl:attribute name="checked"/>
						</xsl:if>
					</input>
					<xsl:text>&#x20;</xsl:text>
					<xsl:value-of select="$enabled"/>
				</td>
			</tr>						
		</table>
		
		<xsl:if test="group/Attributes and AttributeDescriptors">
			
			<br/>
			
			<h2><xsl:value-of select="$attributes"/></h2>
			
			<table class="border">
				<tr>
					<th><xsl:value-of select="$name"/></th>
					<th><xsl:value-of select="$value"/></th>
				</tr>
				
				<xsl:apply-templates select="AttributeDescriptors/AttributeDescriptor" mode="show"/>
				
			</table>
			
		</xsl:if>
				
		<br/>
		
		<xsl:apply-templates select="GroupUsers"/>
		
		<xsl:call-template name="AddShowFormData"/>
		
	</xsl:template>	
	
	<xsl:template name="AddShowFormData"/>	
	
	<xsl:template match="Attribute" mode="show">
		
		<tr>
			<td><xsl:value-of select="Name"/></td>
			<td><xsl:value-of select="Value"/></td>
		</tr>
	
	</xsl:template>
	
	<xsl:template name="Users">
		<h2><xsl:value-of select="$members"/></h2>
		
		<xsl:call-template name="UserList">
			<xsl:with-param name="connectorURL">
				<xsl:value-of select="/Document/requestinfo/currentURI"/>
				<xsl:text>/</xsl:text>
				<xsl:value-of select="/Document/module/alias"/>
				<xsl:text>/users</xsl:text>
			</xsl:with-param>
			<xsl:with-param name="name" select="'user'"/>
			<xsl:with-param name="users" select="GroupUsers" />
		</xsl:call-template>
		
		<br/>
	</xsl:template>
	
	<xsl:template match="GroupUsers">
		<h2>
			<xsl:value-of select="$members"/>
			<xsl:text>&#x20;(</xsl:text>
			<xsl:value-of select="count(user)"/>
			<xsl:text>)</xsl:text>
		</h2>
		
		<div>			
			<xsl:apply-templates select="user" mode="show"/>
		</div>
		
		<br/>
	</xsl:template>
	
	<xsl:template match="user" mode="show">
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
		</div>
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
					<xsl:with-param name="value" select="../../group/Attributes/Attribute[Name = $name]/Value"/>
					<xsl:with-param name="disabled" select="AttributeMode = 'DISABLED'"/>					
				</xsl:call-template>				
			</td>
		</tr>
		
	</xsl:template>	
	
	<xsl:template match="AttributeDescriptor" mode="show">
		
		<xsl:variable name="name" select="Name"/>
		
		<xsl:variable name="value" select="../../group/Attributes/Attribute[Name = $name]/Value"/>
		
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
	
	<xsl:template match="validationError">
		<xsl:if test="fieldName and validationErrorType and not(messageKey)">
			<p class="error">
				<xsl:choose>
					<xsl:when test="validationErrorType='RequiredField'">
						<xsl:value-of select="$validation.requiredField"/>
					</xsl:when>
					<xsl:when test="validationErrorType='InvalidFormat'">
						<xsl:value-of select="$validation.invalidFormat"/>
					</xsl:when>
					<xsl:when test="validationErrorType='TooShort'">
						<xsl:value-of select="$validation.tooShort" />
					</xsl:when>
					<xsl:when test="validationErrorType='TooLong'">
						<xsl:value-of select="$validation.tooLong" />
					</xsl:when>									
					<xsl:otherwise>
						<xsl:value-of select="$validation.unknownError"/>
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:text>&#x20;</xsl:text>
				
				<xsl:choose>
					<xsl:when test="displayName">
						<xsl:value-of select="displayName"/>
					</xsl:when>
					<xsl:when test="fieldName = 'name'">
						<xsl:value-of select="$name"/>
					</xsl:when>
					<xsl:when test="fieldName = 'description'">
						<xsl:value-of select="$description"/>
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
					<xsl:when test="messageKey='RequestedGroupNotFound'">
						<xsl:value-of select="$groupToShowNotFound"/>
					</xsl:when>					
					<xsl:when test="messageKey='UpdateFailedGroupNotFound'">
						<xsl:value-of select="$groupToUpdateNotFound"/>
					</xsl:when>
					<xsl:when test="messageKey='DeleteFailedGroupNotFound'">
						<xsl:value-of select="$groupToRemoveNotFound"/>
					</xsl:when>					
					<xsl:otherwise>
						<xsl:value-of select="$unknownFault"/>
					</xsl:otherwise>
				</xsl:choose>
			</p>
		</xsl:if>
		
	</xsl:template>				
</xsl:stylesheet>