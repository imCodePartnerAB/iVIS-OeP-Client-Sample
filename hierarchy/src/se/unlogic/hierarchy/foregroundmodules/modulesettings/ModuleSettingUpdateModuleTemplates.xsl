<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/Common.xsl"/>
	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/CKEditor.xsl" />

	<xsl:variable name="globalscripts">
		/jquery/jquery.js
		/ckeditor/ckeditor.js
		/ckeditor/adapters/jquery.js
		/ckeditor/init.js
	</xsl:variable>

	<xsl:variable name="scripts">
		/dtree/dtree.js	
	</xsl:variable>	

	<xsl:variable name="links">
		/dtree/dtree.css
	</xsl:variable>

	<xsl:template match="Document">
		<div class="contentitem">
		
			<xsl:apply-templates select="ListSettings"/>
			<xsl:apply-templates select="SettingsSaved"/>
			<xsl:apply-templates select="SectionTree"/>
			<xsl:apply-templates select="SelectSettingDescriptors"/>
		</div>				
	</xsl:template>
	
	<xsl:template match="SettingsSaved">
	
		<h1><xsl:value-of select="$i18n.settingsSaved.title"/></h1>
		
		<xsl:choose>
			<xsl:when test="ModuleNotUpdated">
				<p>
					<xsl:value-of select="$i18n.settingsSaved.nutNotReloadedMessage"/>
				</p>
			</xsl:when>
			<xsl:otherwise>
				<p>
					<xsl:value-of select="$i18n.settingsSaved.message"/>
				</p>
			</xsl:otherwise>
		</xsl:choose>
		
		<p>
			<a href="{/document/requestinfo/uri}">
				<xsl:value-of select="$i18n.showSettings"/>
			</a>
		</p>
		
	</xsl:template>	
	
	<xsl:template match="SelectSettingDescriptors">
	
		<h1><xsl:value-of select="$i18n.selectAllowedSettings"/></h1>
	
		<form method="POST" action="{/document/requestinfo/uri}">
		
			<table class="border full">
				<tr>
					<th></th>
					<th><xsl:value-of select="$i18n.id"/></th>
					<th><xsl:value-of select="$i18n.name"/></th>
					<th><xsl:value-of select="$i18n.type"/></th>
					<th width="16px"></th>
				</tr>
			
				<xsl:apply-templates select="SettingDescriptors/settingDescriptor" mode="list"/>
			</table>
		
			<div class="floatright margintop">
				<input type="submit" value="{$i18n.save}" />
			</div>	
		</form>
	
	</xsl:template>
	
	<xsl:template match="ListSettings">
	
		<h1><xsl:value-of select="/Document/module/name"/></h1>
		
		<xsl:choose>
			<xsl:when test="ModuleNotConfigured">
				<p><xsl:value-of select="$i18n.ModuleNotConfigured"/></p>
			</xsl:when>
			<xsl:when test="ModuleNotFound">
				<p><xsl:value-of select="$i18n.ModuleNotFound"/></p>
			</xsl:when>
			<xsl:when test="NoSettingsNotFound">
				<p><xsl:value-of select="$i18n.NoSettingsNotFound"/></p>
			</xsl:when>
			<xsl:when test="ConfiguredSettingsNotFound">
				<p><xsl:value-of select="$i18n.ConfiguredSettingsNotFound"/></p>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="ValidationErrors/validationError" />
				
				<form method="POST" action="{/document/requestinfo/uri}">
					<table class="full">
						<xsl:apply-templates select="SettingDescriptors/settingDescriptor" />
						<xsl:call-template name="initFCKEditor" /> 
					</table>
					
					<div class="floatright">
						<input type="submit" value="{$i18n.save}" />
					</div>	
				</form>
			</xsl:otherwise>								
		</xsl:choose>
	
		<xsl:if test="/Document/IsAdmin">
			<div class="floatright marginright clearboth margintop">
				<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/selectmodule" title="{$i18n.selectModule}">
					<xsl:value-of select="$i18n.selectModule"/>
					<xsl:text>&#x20;</xsl:text>
					<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/cog.png"/>
				</a>
			</div>
			
			<div class="floatright marginright clearboth margintop">
				<xsl:if test="/Document/moduleID and /Document/ModuleType">
					<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/selectsettings?moduleID={/Document/moduleID}&amp;moduletype={/Document/ModuleType}" title="{$i18n.selectAllowedSettings}">
						<xsl:value-of select="$i18n.selectAllowedSettings"/>
						<xsl:text>&#x20;</xsl:text>
						<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/spanner.png"/>
					</a>				
				</xsl:if>			
			</div>
		</xsl:if>
	
	</xsl:template>	
	
	<xsl:template match="SectionTree">	
		
		<h1><xsl:value-of select="$i18n.selectModule"/></h1>		
		
		<xsl:choose>
			<xsl:when test="ModuleNotFound">
				<p class="error"><xsl:value-of select="$i18n.ModuleNotFound"/></p>
			</xsl:when>
			<xsl:when test="NoSettingsFound">
				<p class="error"><xsl:value-of select="$i18n.NoSettingsNotFound"/></p>
			</xsl:when>			
		</xsl:choose>
		
		<div class="dtree">
			<p><a href="javascript: systemtree{/Document/module/moduleID}.openAll();"><xsl:value-of select="$i18n.expandAll" /></a> | <a href="javascript: systemtree{/Document/module/moduleID}.closeAll();"><xsl:value-of select="$i18n.collapseAll" /></a></p>

			<script type="text/javascript">
				systemtree<xsl:value-of select="/Document/module/moduleID"/> = new dTree('systemtree<xsl:value-of select="/Document/module/moduleID"/>','<xsl:value-of select="/Document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/Document/module/sectionID"/>/<xsl:value-of select="/Document/module/moduleID"/>/dtree/');
				systemtree<xsl:value-of select="/Document/module/moduleID"/>.icon.root = '<xsl:value-of select="/Document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/Document/module/sectionID"/>/<xsl:value-of select="/Document/module/moduleID"/>/dtree/img/globe.gif';
				<xsl:apply-templates select="section" mode="systemtree"/>
				
				document.write(systemtree<xsl:value-of select="/Document/module/moduleID"/>);
			</script>
		</div>				
	</xsl:template>
	
	<xsl:template match="section" mode="systemtree">
	
		<xsl:variable name="parentSectionID">
			<xsl:choose>
				<xsl:when test="parentSectionID">
					<xsl:text>section</xsl:text>
					<xsl:value-of select="parentSectionID"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>-1</xsl:text>
				</xsl:otherwise>
			</xsl:choose>			
		</xsl:variable>
	
		<xsl:variable name="name">
            <xsl:call-template name="common_js_escape">
               <xsl:with-param name="text" select="name" />
            </xsl:call-template>			
		</xsl:variable>
		
		<xsl:variable name="nameWithoutQuotes">
			<xsl:call-template name="replace-substring">
				<xsl:with-param name="from" select="'&quot;'"/>
				<xsl:with-param name="to" select="''"/>
				<xsl:with-param name="value">
					<xsl:call-template name="replace-substring">
						<xsl:with-param name="from">&apos;</xsl:with-param>
						<xsl:with-param name="to" select="''"/>
						<xsl:with-param name="value" select="name" />
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>			
		</xsl:variable>		
		
		<xsl:variable name="description">
            <xsl:call-template name="common_js_escape">
               <xsl:with-param name="text" select="description" />
            </xsl:call-template>			
		</xsl:variable>	
		
		systemtree<xsl:value-of select="/Document/module/moduleID"/>.add('section<xsl:value-of select="sectionID"/>','<xsl:value-of select="$parentSectionID"/>','<xsl:value-of select="$name"/>','','<xsl:value-of select="$description"/>','','<xsl:value-of select="/Document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/Document/module/sectionID"/>/<xsl:value-of select="/Document/module/moduleID"/>/dtree/img/folder.gif','<xsl:value-of select="/Document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/Document/module/sectionID"/>/<xsl:value-of select="/Document/module/moduleID"/>/dtree/img/folderopen.gif','',	'');

		<xsl:apply-templates select="Subsections/section" mode="systemtree"/>
		
		<xsl:apply-templates select="Modules/module" mode="dtree"/>
	</xsl:template>
	
	<xsl:template match="module" mode="dtree">	
	
		<xsl:variable name="name">
            <xsl:call-template name="common_js_escape">
               <xsl:with-param name="text" select="name" />
            </xsl:call-template>			
		</xsl:variable>
		
		<xsl:variable name="nameWithoutQuotes">
			<xsl:call-template name="replace-substring">
				<xsl:with-param name="from" select="'&quot;'"/>
				<xsl:with-param name="to" select="''"/>
				<xsl:with-param name="value">
					<xsl:call-template name="replace-substring">
						<xsl:with-param name="from">&apos;</xsl:with-param>
						<xsl:with-param name="to" select="''"/>
						<xsl:with-param name="value" select="name" />
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>			
		</xsl:variable>
		
		<xsl:variable name="description">
            <xsl:call-template name="common_js_escape">
               <xsl:with-param name="text" select="description" />
            </xsl:call-template>			
		</xsl:variable>		
	
		<xsl:variable name="icontype">
			<xsl:choose>
				<xsl:when test="moduleType = 'BACKGROUND'">
					<xsl:text>_b</xsl:text>
				</xsl:when>
				<xsl:when test="moduleType = 'FILTER'">
					<xsl:text>_f</xsl:text>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>	
	
		<xsl:variable name="icon">
			<xsl:text>cog</xsl:text>
			<xsl:value-of select="$icontype"/>
			<xsl:text>.png</xsl:text>
		</xsl:variable>
	
		<xsl:variable name="link">
			<xsl:value-of select="/Document/requestinfo/currentURI"/>/<xsl:value-of select="/Document/module/alias"/>/selectsettings?moduleID=<xsl:value-of select="moduleID"/>&amp;moduletype=<xsl:value-of select="moduleType"/>
		</xsl:variable>
	
		systemtree<xsl:value-of select="/Document/module/moduleID"/>.add('<xsl:value-of select="moduleType"/>.module<xsl:value-of select="moduleID"/>','section<xsl:value-of select="../../sectionID"/>','<xsl:value-of select="$name"/>','<xsl:value-of select="$link"/>','<xsl:value-of select="$description"/>','','<xsl:value-of select="/Document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/Document/module/sectionID"/>/<xsl:value-of select="/Document/module/moduleID"/><xsl:text>/pics/</xsl:text><xsl:value-of select="$icon"/>','','','');
	</xsl:template>
	
	<xsl:template name="common_js_escape">
		<!-- required -->
		<xsl:param name="text"/>
		<xsl:variable name="tmp">		
			<xsl:call-template name="replace-substring">
				<xsl:with-param name="from" select="'&quot;'"/>
				<xsl:with-param name="to">\"</xsl:with-param>
				<xsl:with-param name="value">
					<xsl:call-template name="replace-substring">
						<xsl:with-param name="from">&apos;</xsl:with-param>
						<xsl:with-param name="to">\'</xsl:with-param>
						<xsl:with-param name="value" select="$text" />
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>	
		</xsl:variable>
		<xsl:value-of select="$tmp" />
	</xsl:template>
	
	<xsl:template name="replace-substring">
	      <xsl:param name="value" />
	      <xsl:param name="from" />
	      <xsl:param name="to" />
	      <xsl:choose>
	         <xsl:when test="contains($value,$from)">
	            <xsl:value-of select="substring-before($value,$from)" />
	            <xsl:value-of select="$to" />
	            <xsl:call-template name="replace-substring">
	               <xsl:with-param name="value" select="substring-after($value,$from)" />
	               <xsl:with-param name="from" select="$from" />
	               <xsl:with-param name="to" select="$to" />
	            </xsl:call-template>
	         </xsl:when>
	         <xsl:otherwise>
	            <xsl:value-of select="$value" />
	         </xsl:otherwise>
	      </xsl:choose>
	</xsl:template>
	
	<xsl:template match="validationError">
		<xsl:if test="fieldName and validationErrorType and not(messageKey)">
			<p class="error">
				<xsl:choose>
					<xsl:when test="validationErrorType='RequiredField'">
						<xsl:value-of select="$i18n.validation.requiredField"/>
					</xsl:when>
					<xsl:when test="validationErrorType='InvalidFormat'">
						<xsl:value-of select="$i18n.validation.invalidFormat"/>
					</xsl:when>
					<xsl:when test="validationErrorType='TooShort'">
						<xsl:value-of select="$i18n.validation.tooShort" />
					</xsl:when>
					<xsl:when test="validationErrorType='TooLong'">
						<xsl:value-of select="$i18n.validation.tooLong" />
					</xsl:when>		
					<xsl:otherwise>
						<xsl:value-of select="$i18n.validation.unknownError"/>
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:text>&#x20;</xsl:text>
				
				<xsl:value-of select="fieldName"/>	
			</p>
		</xsl:if>
		
		<xsl:if test="messageKey">
			<p class="error">
				<xsl:value-of select="$i18n.unknownFault"/>
			</p>
		</xsl:if>
		
	</xsl:template>
		
	<xsl:template match="settingDescriptor" mode="list">
	
		<xsl:variable name="id" select="id"/>
	
		<tr>
			<td>
				<input type="checkbox" name="id" value="{id}">
					
					<xsl:if test="../../SelectedSettings/ID = $id">
						<xsl:attribute name="checked">true</xsl:attribute>
					</xsl:if>
				
				</input>
			</td>
			<td><xsl:value-of select="id"/></td>
			<td><xsl:value-of select="name"/></td>
			<td><xsl:value-of select="displayType"/></td>
			<td>
				<a href="#" onclick="alert('{description}');">
					<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/info.png" alt="{description}" title="{description}"/>
				</a>
			</td>
		</tr>
	
	</xsl:template>		
		
	<xsl:template match="settingDescriptor">
		
		<xsl:choose>
			<xsl:when test="displayType='TEXTFIELD'">
				<tr>
					<td>
						<xsl:value-of select="name"/>
						<xsl:text> </xsl:text>
						<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/info.png" alt="{description}" title="{description}"/>
						
						<a href="javascript:void(0);" onclick="javascript:document.getElementById('modulesetting.{id}').value='{defaultValue}';" title="{$i18n.settingDescriptor.resetDefualtValue}">
							<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/reload.png" alt="{$i18n.settingDescriptor.resetDefualtValue}" title="{$i18n.settingDescriptor.resetDefualtValue}"/>
						</a>:
					</td>	
					<td>
						<xsl:variable name="id" select="id"/>
					
						<xsl:variable name="requestid">modulesetting.<xsl:value-of select="id"/></xsl:variable>
					
						<input type="text" name="modulesetting.{id}" id="modulesetting.{id}" size="40">
							<xsl:attribute name="value">
								<xsl:choose>
									<xsl:when test="../../requestparameters/parameter[name=$requestid]">
										<xsl:value-of select="../../requestparameters/parameter[name=$requestid]/value"/>
									</xsl:when>
									<xsl:when test="../../module/settings/setting[id=$id]/value">
										<xsl:value-of select="../../module/settings/setting[id=$id]/value"/>
									</xsl:when>									
									<xsl:otherwise>
										<xsl:value-of select="defaultValue"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</input>		
					</td>
				</tr>				
			</xsl:when>
			<xsl:when test="displayType='PASSWORD'">
				<tr>
					<td><xsl:value-of select="name"/><xsl:text> </xsl:text><img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/info.png" alt="information" title="{description}"/>:</td>
					<td>
						<xsl:variable name="id" select="id"/>
					
						<xsl:variable name="requestid">modulesetting.<xsl:value-of select="id"/></xsl:variable>
					
						<input type="password" name="modulesetting.{id}" size="40">
							<xsl:attribute name="value">
								<xsl:choose>
									<xsl:when test="../../requestparameters/parameter[name=$requestid]">
										<xsl:value-of select="../../requestparameters/parameter[name=$requestid]/value"/>
									</xsl:when>
									<xsl:when test="../../module/settings/setting[id=$id]/value">
										<xsl:value-of select="../../module/settings/setting[id=$id]/value"/>
									</xsl:when>									
									<xsl:otherwise>
										<xsl:value-of select="defaultValue"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</input>		
					</td>
				</tr>				
			</xsl:when>
			<xsl:when test="displayType='TEXTAREA'">
				<tr>
					<td colspan="2">
						<xsl:value-of select="name"/>
						<xsl:text> </xsl:text>
						<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/info.png" alt="information" title="{description}"/>
						
						<a href="javascript:void(0);" onclick="javascript:document.getElementById('modulesetting.{id}').value=document.getElementById('hiddenmodulesetting.{id}').value;" title="{$i18n.settingDescriptor.resetDefualtValue}">
							<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/reload.png" alt="{$i18n.settingDescriptor.resetDefualtValue}" title="{$i18n.settingDescriptor.resetDefualtValue}"/>
						</a>:
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<xsl:variable name="id" select="id"/>
					
						<xsl:variable name="requestid">modulesetting.<xsl:value-of select="id"/></xsl:variable>
					
						<input type="hidden" id="hiddenmodulesetting.{id}" value="{defaultValue}"/>
					
						<textarea name="modulesetting.{id}" id="modulesetting.{id}" class="medium full">
						
							<xsl:call-template name="replaceEscapedLineBreak">
								<xsl:with-param name="text">
									<xsl:choose>
										<xsl:when test="../../requestparameters/parameter[name=$requestid]">
											<xsl:value-of select="../../requestparameters/parameter[name=$requestid]/value"/>
										</xsl:when>
										<xsl:when test="../../module/settings/setting[id=$id]/value">
											<xsl:value-of select="../../module/settings/setting[id=$id]/value"/>
										</xsl:when>									
										<xsl:otherwise>
											<xsl:value-of select="defaultValue"/>
										</xsl:otherwise>
									</xsl:choose>								
								</xsl:with-param>
							</xsl:call-template>						
						</textarea>		
					</td>
				</tr>				
			</xsl:when>
			<xsl:when test="displayType='HTML_EDITOR'">
				<tr>
					<td colspan="2">
						<xsl:value-of select="name"/>
						<xsl:text> </xsl:text>
						<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/info.png" alt="information" title="{description}"/>
						
						<!--
						
						TODO!
						
						<a href="javascript:void(0);" onclick="javascript:document.getElementById('modulesetting.{id}').value=document.getElementById('hiddenmodulesetting.{id}').value;" title="{$settingDescriptor.resetDefualtValue}">
							<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/reload.png" alt="{$settingDescriptor.resetDefualtValue}" title="{$settingDescriptor.resetDefualtValue}"/>
						</a>:
						
						-->
						
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<xsl:variable name="id" select="id"/>
					
						<xsl:variable name="requestid">modulesetting.<xsl:value-of select="id"/></xsl:variable>
					
						<input type="hidden" id="hiddenmodulesetting.{id}" value="{defaultValue}"/>
					
						<textarea name="modulesetting.{id}" id="modulesetting.{id}" class="fckeditor">
						
							<xsl:call-template name="replaceEscapedLineBreak">
								<xsl:with-param name="text">
									<xsl:choose>
										<xsl:when test="../../requestparameters/parameter[name=$requestid]">
											<xsl:value-of select="../../requestparameters/parameter[name=$requestid]/value"/>
										</xsl:when>
										<xsl:when test="../../module/settings/setting[id=$id]/value">
											<xsl:value-of select="../../module/settings/setting[id=$id]/value"/>
										</xsl:when>									
										<xsl:otherwise>
											<xsl:value-of select="defaultValue"/>
										</xsl:otherwise>
									</xsl:choose>								
								</xsl:with-param>
							</xsl:call-template>						
						</textarea>		
					</td>
				</tr>				
			</xsl:when>			
			<xsl:when test="displayType='CHECKBOX'">
				<tr>
					<td colspan="2">
							
						<xsl:variable name="id" select="id"/>
					
						<xsl:variable name="requestid">modulesetting.<xsl:value-of select="id"/></xsl:variable>
					
						<input type="checkbox" name="modulesetting.{id}" value="true">
							<xsl:choose>
								<xsl:when test="../../requestparameters">
									<xsl:if test="../../requestparameters/parameter[name=$requestid]/value">
										<xsl:attribute name="checked"/>
									</xsl:if>								
								</xsl:when>
								<xsl:when test="../../module/settings/setting[id=$id]/value">
									<xsl:if test="../../module/settings/setting[id=$id]/value = 'true'">
										<xsl:attribute name="checked"/>
									</xsl:if>									
								</xsl:when>
								<xsl:otherwise>
									<xsl:if test="defaultValue = 'true'">
										<xsl:attribute name="checked"/>
									</xsl:if>
								</xsl:otherwise>
							</xsl:choose>						
						</input>
						
						<xsl:value-of select="name"/><xsl:text> </xsl:text><img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/info.png" alt="information" title="{description}"/>		
					</td>
				</tr>			
			</xsl:when>
			<xsl:when test="displayType='RADIOBUTTON'">
			
				<xsl:variable name="id" select="id"/>
				<xsl:variable name="requestid">modulesetting.<xsl:value-of select="id"/></xsl:variable>
			
				<tr>
					<td><xsl:value-of select="name"/><xsl:text> </xsl:text><img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/info.png" alt="information" title="{description}"/>:</td>
					<td>
						<xsl:if test="required='false'">
							<label for="{id}.00">
								<input type="radio" name="{$requestid}" id="{id}.00" value="">
									<xsl:choose>
										<xsl:when test="../../requestparameters/parameter[name=$requestid]/value = ''">
											<xsl:attribute name="checked"/>								
										</xsl:when>
										<xsl:when test="not(../../module/settings/setting[id=$id]/value) and (not(defaultValue) or defaultValue = '')">
											<xsl:attribute name="checked"/>								
										</xsl:when>
									</xsl:choose>				
								</input>
								<xsl:text>Ej satt</xsl:text>
							</label>
							<br/>								
						</xsl:if>				
					
						<xsl:apply-templates select="allowedValues/valueDescriptor" mode="radiobutton"/>
					</td>
				</tr>
			</xsl:when>
			<xsl:when test="displayType='DROPDOWN'">
				
				<xsl:variable name="id" select="id"/>
				<xsl:variable name="requestid">modulesetting.<xsl:value-of select="id"/></xsl:variable>
			
				<tr>
					<td><xsl:value-of select="name"/><xsl:text> </xsl:text><img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/info.png" alt="information" title="{description}"/>:</td>
					<td>
						<select name="{$requestid}">
							
							<xsl:if test="required='false'">
								<option value="">
									<xsl:choose>
										<xsl:when test="../../requestparameters/parameter[name=$requestid]/value = ''">
											<xsl:attribute name="selected"/>								
										</xsl:when>
										<xsl:when test="not(../../module/settings/setting[id=$id]/value) and (not(defaultValue) or defaultValue = '')">
											<xsl:attribute name="selected"/>								
										</xsl:when>
									</xsl:choose>	
								
									<xsl:value-of select="$i18n.settingDescriptor.notSet"/>
								</option>								
							</xsl:if>					
						
							<xsl:apply-templates select="allowedValues/valueDescriptor" mode="select"/>
						</select>
					</td>
				</tr>				
			</xsl:when>
			<xsl:when test="displayType='MULTILIST'">
				<tr>
					<td><xsl:value-of select="name"/><xsl:text> </xsl:text><img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/info.png" alt="information" title="{description}"/>:</td>
					<td>
						<select name="modulesetting.{id}" multiple="true" size="8">						
							<xsl:apply-templates select="allowedValues/valueDescriptor" mode="select"/>
						</select>
					</td>
				</tr>				
			</xsl:when>			
			<xsl:otherwise>
				<tr>
					<td colspan="2"><p class="error"><xsl:value-of select="$i18n.settingDescriptor.unknownTypePart1"/> "<xsl:value-of select="name"/>" <xsl:value-of select="$i18n.settingDescriptor.unknownTypePart2"/></p></td>
				</tr>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="valueDescriptor" mode="radiobutton">
	
		<xsl:variable name="id" select="../../id"/>
		<xsl:variable name="requestid">modulesetting.<xsl:value-of select="../../id"/></xsl:variable>
		<xsl:variable name="currentValue" select="value"/>
	
		<label for="{../../id}.{position()}">
			<input type="radio" name="{$requestid}" id="{../../id}.{position()}" value="{value}">
				<xsl:choose>
					<xsl:when test="../../../../requestparameters">
						<xsl:if test="../../../../requestparameters/parameter[name=$requestid]/value = $currentValue">
							<xsl:attribute name="checked"/>
						</xsl:if>								
					</xsl:when>
					<xsl:when test="../../../../module/settings/setting[id=$id]/value">
						<xsl:if test="../../../../module/settings/setting[id=$id]/value = $currentValue">
							<xsl:attribute name="checked"/>
						</xsl:if>							
					</xsl:when>
					<xsl:when test="../../defaultValue = value">
						<xsl:attribute name="checked"/>
					</xsl:when>
				</xsl:choose>				
			</input>
			<xsl:value-of select="name"/>
		</label>
		<br/>
	</xsl:template>
	
	<xsl:template match="valueDescriptor" mode="select">
	
		<xsl:variable name="id" select="../../id"/>
		<xsl:variable name="requestid">modulesetting.<xsl:value-of select="../../id"/></xsl:variable>
		<xsl:variable name="currentValue" select="value"/>
	
		<option value="{value}">
			<xsl:choose>
					<xsl:when test="../../../../requestparameters">
						<xsl:if test="../../../../requestparameters/parameter[name=$requestid]/value = $currentValue">
							<xsl:attribute name="selected"/>
						</xsl:if>								
					</xsl:when>
					<xsl:when test="../../../../module/settings/setting[id=$id]/value">
						<xsl:if test="../../../../module/settings/setting[id=$id]/value = $currentValue">
							<xsl:attribute name="selected"/>
						</xsl:if>									
					</xsl:when>
					<xsl:when test="../../defaultValue = value">
						<xsl:attribute name="selected"/>
					</xsl:when>
			</xsl:choose>		
		
			<xsl:value-of select="name"/>
		</option>
	</xsl:template>	
				
	<xsl:template name="initFCKEditor">
		
		<!-- Call global CKEditor init template -->
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
		
	</xsl:template>								
</xsl:stylesheet>