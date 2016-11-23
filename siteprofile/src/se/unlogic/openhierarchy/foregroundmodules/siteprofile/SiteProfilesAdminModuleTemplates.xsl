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
			<xsl:apply-templates select="ListProfiles"/>
			<xsl:apply-templates select="AddProfile"/>
			<xsl:apply-templates select="UpdateProfile"/>
			<xsl:apply-templates select="UpdateGlobalSettings"/>
		</div>
	</xsl:template>
			
	<xsl:template match="ListProfiles">
			
		<h1><xsl:value-of select="$i18n.Profiles" /></h1>
		
		<table class="full coloredtable bigmarginbottom">
			<thead>	
				<tr>
					<th width="16"></th>
					<th><xsl:value-of select="$i18n.Name" /></th>
					<th><xsl:value-of select="$i18n.Design" /></th>
					<th><xsl:value-of select="$i18n.Domains" /></th>
					<th width="32" />
				</tr>
			</thead>
			<tbody>
				<xsl:choose>
					<xsl:when test="not(Profiles/Profile)">
						<tr>
							<td></td>
							<td colspan="5">
								<xsl:value-of select="$i18n.NoProfilesFound" />
							</td>
						</tr>					
					</xsl:when>
					<xsl:otherwise>
						
						<xsl:apply-templates select="Profiles/Profile" mode="list"/>
						
					</xsl:otherwise>
				</xsl:choose>			
			</tbody>
		</table>
		
		<div class="floatright marginright">
			<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/addprofile" title="{$i18n.AddProfile}">
				<xsl:value-of select="$i18n.AddProfile"/>
				<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/add.png"/>
			</a>
		</div>
		
		<div class="floatright marginright clearboth">
			<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/globalsettings" title="{$i18n.UpdateGlobalSettings}">
				<xsl:value-of select="$i18n.UpdateGlobalSettings"/>
				<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/spanner.png"/>
			</a>
		</div>		
		
	</xsl:template>
	
	<xsl:template match="Profile" mode="list">
		
		<tr>
			<td>
				<img src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/organization.png" />
			</td>
			<td>
				<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/updateprofile/{profileID}" title="{$i18n.UpdateProfile}: {name}">
					<xsl:value-of select="name" />
				</a>
			</td>
			<td>
				<xsl:value-of select="design" />
			</td>
			<td>
				<xsl:value-of select="count(domains/domain)" />
			</td>
			<td>
				<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/updateprofile/{profileID}" title="{$i18n.UpdateProfile}: {name}">
					<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/pen.png"/>
				</a>
				<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/deleteprofile/{profileID}" onclick="return confirm('{$i18n.DeleteProfile}: {name}?')" title="{$i18n.DeleteProfile}: {name}">
					<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/delete.png"/>
				</a>
			</td>
		</tr>
				
	</xsl:template>
	
	<xsl:template match="AddProfile">
			
		<h1><xsl:value-of select="$i18n.AddProfile" /></h1>
		
		<form method="post" action="{/Document/requestinfo/uri}">
		
			<xsl:call-template name="showProfileForm" />
		
			<div class="floatright">
				<input type="submit" value="{$i18n.Add}" />
			</div>
		
		</form>
		
	</xsl:template>
	
	<xsl:template match="UpdateProfile">
			
		<h1><xsl:value-of select="$i18n.UpdateProfile" /></h1>
		
		<form method="post" action="{/Document/requestinfo/uri}">
		
			<xsl:call-template name="showProfileForm" />
		
			<div class="floatright">
				<input type="submit" value="{$i18n.SaveChanges}" />
			</div>
		
		</form>
		
	</xsl:template>
	
	<xsl:template name="showProfileForm">
			
		<xsl:apply-templates select="validationException/validationError" />
	
		<div class="floatleft full bigmarginbottom">
			<label for="name" class="floatleft full">
				<xsl:value-of select="$i18n.Name" />
			</label>
			<div class="floatleft full">
				<xsl:call-template name="createTextField">
					<xsl:with-param name="id" select="'name'"/>
					<xsl:with-param name="name" select="'name'"/>
					<xsl:with-param name="element" select="Profile" />          
				</xsl:call-template>
			</div>
		</div>
				
		<div class="floatleft full bigmarginbottom">
			<label for="designName" class="floatleft full">
				<xsl:value-of select="$i18n.Design" />
			</label>
			<div class="floatleft full">
				<xsl:call-template name="createDropdown">
					<xsl:with-param name="id" select="'design'"/>
					<xsl:with-param name="name" select="'design'"/>
					<xsl:with-param name="valueElementName" select="'name'" />
					<xsl:with-param name="labelElementName" select="'name'" />
					<xsl:with-param name="element" select="Designs/Design"/>
					<xsl:with-param name="selectedValue" select="Profile/design" />
					<xsl:with-param name="addEmptyOption" select="$i18n.ChooseDesign" />	
				</xsl:call-template>
			</div>
		</div>
		
		<div class="floatleft full bigmarginbottom">
			<label for="domains" class="floatleft full">
				<xsl:value-of select="$i18n.Domains" />
			</label>
			<div class="floatleft full">
				<xsl:call-template name="createTextArea">
					<xsl:with-param name="id" select="'domains'"/>
					<xsl:with-param name="name" select="'domains'"/>
					<xsl:with-param name="separateListValues" select="true()"/>
					<xsl:with-param name="element" select="Profile/domains/domain" />          
				</xsl:call-template>
			</div>
		</div>
	
		<xsl:if test="SettingDescriptors">
		
			<h2><xsl:value-of select="$i18n.ProfileSettings"/></h2>
		
			<xsl:apply-templates select="SettingDescriptors/SettingDescriptor">
				<xsl:with-param name="requestparameters" select="requestparameters"/>
				<xsl:with-param name="values" select="settings"/>
			</xsl:apply-templates>
		
			<xsl:if test="SettingDescriptors/SettingDescriptor[FormElement='HTML_EDITOR']">

				<xsl:call-template name="initializeFCKEditor">
					<xsl:with-param name="basePath"><xsl:value-of select="/Document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/Document/module/sectionID"/>/<xsl:value-of select="/Document/module/moduleID"/>/ckeditor/</xsl:with-param>
					<xsl:with-param name="customConfig">config.js</xsl:with-param>
					<xsl:with-param name="editorContainerClass">htmleditor</xsl:with-param>
					<xsl:with-param name="editorHeight">150</xsl:with-param>
					<xsl:with-param name="contentsCss"><xsl:if test="/Document/cssPath"><xsl:value-of select="/Document/cssPath"/></xsl:if></xsl:with-param>
				</xsl:call-template>
			
			</xsl:if>
		
		</xsl:if>
	
	</xsl:template>
	
	<xsl:template match="UpdateGlobalSettings">
			
		<h1><xsl:value-of select="$i18n.UpdateGlobalSettings" /></h1>
		
		<xsl:apply-templates select="ValidationErrors/validationError" />
		
		<form method="post" action="{/Document/requestinfo/uri}">
		
			<xsl:choose>
				<xsl:when test="SettingDescriptors">
		
					<xsl:apply-templates select="SettingDescriptors/SettingDescriptor">
						<xsl:with-param name="requestparameters" select="requestparameters"/>
						<xsl:with-param name="values" select="settings"/>
					</xsl:apply-templates>
				
					<xsl:if test="SettingDescriptors/SettingDescriptor[FormElement='HTML_EDITOR']">
		
						<xsl:call-template name="initializeFCKEditor">
							<xsl:with-param name="basePath"><xsl:value-of select="/Document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/Document/module/sectionID"/>/<xsl:value-of select="/Document/module/moduleID"/>/ckeditor/</xsl:with-param>
							<xsl:with-param name="customConfig">config.js</xsl:with-param>
							<xsl:with-param name="editorContainerClass">htmleditor</xsl:with-param>
							<xsl:with-param name="editorHeight">150</xsl:with-param>
							<xsl:with-param name="contentsCss"><xsl:if test="/Document/cssPath"><xsl:value-of select="/Document/cssPath"/></xsl:if></xsl:with-param>
						</xsl:call-template>
					
					</xsl:if>
								
				</xsl:when>
				<xsl:otherwise>
				
					<xsl:value-of select="$i18n.NoSettingsFound" />		
				
				</xsl:otherwise>
			</xsl:choose>
		
			<div class="floatright">
				<input type="submit" value="{$i18n.SaveChanges}" />
			</div>
		
		</form>
		
	</xsl:template>	
	
	<xsl:template match="SettingDescriptor[FormElement='HTML_EDITOR']">
	
		<xsl:param name="requestparameters"/>
		<xsl:param name="values"/>

		<xsl:variable name="ID">setting-<xsl:value-of select="ID"/></xsl:variable>
		<xsl:variable name="settingID"><xsl:value-of select="ID"/></xsl:variable>
	
		<div class="floatleft full bigmarginbottom">
			<label for="{$ID}" class="floatleft full">
				<xsl:value-of select="Name"/>
				<xsl:text>&#160;</xsl:text>
				<img class="alignmiddle" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/info.png" title="{Description}"/>
			</label>
			<div class="floatleft full">
				<xsl:call-template name="createTextArea">
					<xsl:with-param name="id" select="$ID"/>
					<xsl:with-param name="name" select="$ID"/>
					<xsl:with-param name="class" select="'htmleditor'"/>
					<xsl:with-param name="requestparameters" select="$requestparameters"/>
					<xsl:with-param name="value">
					
						<xsl:variable name="value" select="$values/setting[id=$settingID]/value"/>
					
						<xsl:choose>
							<xsl:when test="$value">
								<xsl:value-of select="$value"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="DefaultValues/Value"/>
							</xsl:otherwise>
						</xsl:choose>
					
					</xsl:with-param> 					            
				</xsl:call-template>
			</div>
		</div>		
	
	</xsl:template>	
	
	<xsl:template match="SettingDescriptor[FormElement='TEXT_AREA']">
	
		<xsl:param name="requestparameters"/>
		<xsl:param name="values"/>

		<xsl:variable name="ID">setting-<xsl:value-of select="ID"/></xsl:variable>
		<xsl:variable name="settingID"><xsl:value-of select="ID"/></xsl:variable>
	
		<div class="floatleft full bigmarginbottom">
			<label for="{$ID}" class="floatleft full">
				<xsl:value-of select="Name"/>
				<xsl:text>&#160;</xsl:text>
				<img class="alignmiddle" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/info.png" title="{Description}"/>
			</label>
			<div class="floatleft full">
				<xsl:call-template name="createTextArea">
					<xsl:with-param name="id" select="$ID"/>
					<xsl:with-param name="name" select="$ID"/>
					<!-- <xsl:with-param name="separateListValues" select="true()"/> -->
					<xsl:with-param name="element" select="$values/setting[id=$settingID]/value" /> 
					<xsl:with-param name="requestparameters" select="$requestparameters"/>
					<xsl:with-param name="value">
					
						<xsl:variable name="value" select="$values/setting[id=$settingID]/value"/>
					
						<xsl:choose>
							<xsl:when test="$value">
								<xsl:value-of select="$value"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="DefaultValues/Value"/>
							</xsl:otherwise>
						</xsl:choose>
					
					</xsl:with-param> 					         
				</xsl:call-template>
			</div>
		</div>		
	
	</xsl:template>
	
	<xsl:template match="SettingDescriptor[FormElement='TEXT_FIELD']">
	
		<xsl:param name="requestparameters"/>
		<xsl:param name="values"/>

		<xsl:variable name="ID">setting-<xsl:value-of select="ID"/></xsl:variable>
		<xsl:variable name="settingID"><xsl:value-of select="ID"/></xsl:variable>
	
		<div class="floatleft full bigmarginbottom">
			<label for="{$ID}" class="floatleft full">
				<xsl:value-of select="Name"/>
				<xsl:text>&#160;</xsl:text>
				<img class="alignmiddle" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/info.png" title="{Description}"/>
			</label>
			<div class="floatleft full">
				<xsl:call-template name="createTextField">
					<xsl:with-param name="id" select="$ID"/>
					<xsl:with-param name="name" select="$ID"/>
					<!-- <xsl:with-param name="separateListValues" select="true()"/> -->
					<xsl:with-param name="element" select="$values/setting[id=$settingID]/value" /> 
					<xsl:with-param name="requestparameters" select="$requestparameters"/>
					<xsl:with-param name="value">
					
						<xsl:variable name="value" select="$values/setting[id=$settingID]/value"/>
					
						<xsl:choose>
							<xsl:when test="$value">
								<xsl:value-of select="$value"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="DefaultValues/Value"/>
							</xsl:otherwise>
						</xsl:choose>
					
					</xsl:with-param> 					         
				</xsl:call-template>
			</div>
		</div>		
	
	</xsl:template>		
	
	<xsl:template match="SettingDescriptor[FormElement='PASSWORD']">
	
		<xsl:param name="requestparameters"/>
		<xsl:param name="values"/>

		<xsl:variable name="ID">setting-<xsl:value-of select="ID"/></xsl:variable>
		<xsl:variable name="settingID"><xsl:value-of select="ID"/></xsl:variable>
	
		<div class="floatleft full bigmarginbottom">
			<label for="{$ID}" class="floatleft full">
				<xsl:value-of select="Name"/>
				<xsl:text>&#160;</xsl:text>
				<img class="alignmiddle" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/info.png" title="{Description}"/>
			</label>
			<div class="floatleft full">
				<xsl:call-template name="createPasswordField">
					<xsl:with-param name="id" select="$ID"/>
					<xsl:with-param name="name" select="$ID"/>
					<!-- <xsl:with-param name="separateListValues" select="true()"/> -->
					<xsl:with-param name="element" select="$values/setting[id=$settingID]/value" /> 
					<xsl:with-param name="requestparameters" select="$requestparameters"/>
					<xsl:with-param name="value">
					
						<xsl:variable name="value" select="$values/setting[id=$settingID]/value"/>
					
						<xsl:choose>
							<xsl:when test="$value">
								<xsl:value-of select="$value"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="DefaultValues/Value"/>
							</xsl:otherwise>
						</xsl:choose>
					
					</xsl:with-param> 					         
				</xsl:call-template>
			</div>
		</div>		
	
	</xsl:template>		
	
	<xsl:template match="SettingDescriptor">
	
		<p class="error">Setting <xsl:value-of select="ID"/> has an unsupported form element value of <xsl:value-of select="FormElement"/></p>
	
	</xsl:template>		
	
	<xsl:template match="validationError[messageKey='DomainAlreadyInUse']">
		<p class="error">
			<xsl:value-of select="$i18n.ValidationError.DomainAlreadyInUse.part1" />
			<xsl:value-of select="domain" />
			<xsl:value-of select="$i18n.ValidationError.DomainAlreadyInUse.part2" />
			<xsl:value-of select="profileName" />
			<xsl:value-of select="$i18n.ValidationError.DomainAlreadyInUse.part3" />
		</p>
	</xsl:template>
	
	<xsl:template match="validationError">
	
		<xsl:if test="fieldName and validationErrorType and not(messageKey)">
			<p class="error">
				<xsl:choose>
					<xsl:when test="validationErrorType='RequiredField'">
						<xsl:value-of select="$i18n.ValidationError.RequiredField" />
					</xsl:when>
					<xsl:when test="validationErrorType='InvalidFormat'">
						<xsl:value-of select="$i18n.ValidationError.InvalidFormat" />
					</xsl:when>
					<xsl:when test="validationErrorType='TooShort'">
						<xsl:value-of select="$i18n.ValidationError.TooShort" />
					</xsl:when>
					<xsl:when test="validationErrorType='TooLong'">
						<xsl:value-of select="$i18n.ValidationError.TooLong" />
					</xsl:when>							
					<xsl:otherwise>
						<xsl:value-of select="$i18n.ValidationError.UnknownValidationErrorType" />
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:text>&#x20;</xsl:text>
				
				<xsl:choose>
					<xsl:when test="displayName">
						<xsl:value-of select="displayName"/>
					</xsl:when>				
					<xsl:when test="fieldName = 'name'">
						<xsl:value-of select="$i18n.Name"/>
					</xsl:when>
					<xsl:when test="fieldName = 'design'">
						<xsl:value-of select="$i18n.Design"/>
					</xsl:when>
					<xsl:when test="fieldName = 'domains'">
						<xsl:value-of select="$i18n.Domains"/>
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
				<xsl:value-of select="$i18n.ValidationError.UnknownFault" />
			</p>
		</xsl:if>
		
	</xsl:template>			
</xsl:stylesheet>