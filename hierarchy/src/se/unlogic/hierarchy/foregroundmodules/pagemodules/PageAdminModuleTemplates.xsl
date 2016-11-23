<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1" />

	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/Common.xsl" />
	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/CKEditor.xsl" />

	<xsl:variable name="globalscripts">
		/jquery/jquery.js
		/jquery/jquery-ui.js
		/ckeditor/ckeditor.js
		/ckeditor/adapters/jquery.js
		/ckeditor/init.js
	</xsl:variable>	

	<xsl:variable name="scripts">
		/dtree/dtree.js
		/js/askBeforeRedirect.js
		/js/UserGroupList.js
	</xsl:variable>	

	<xsl:variable name="links">
		/dtree/dtree.css
		/css/UserGroupList.css
	</xsl:variable>

	<xsl:template match="document">
		<div class="contentitem">
			<xsl:apply-templates select="addPageForm"/>
			<xsl:apply-templates select="updatePageForm"/>
			<xsl:apply-templates select="movePageForm"/>
			<xsl:apply-templates select="copyPageForm"/>
			<xsl:apply-templates select="preview"/>		
			<xsl:apply-templates select="sections"/>
		</div>
	</xsl:template>
	
	<xsl:template match="sections">	
		
		<h1><xsl:value-of select="/document/module/name"/></h1>		
		
		<div class="dtree">
			<p><a href="javascript: pagetree{/document/module/moduleID}.openAll();"><xsl:value-of select="$expandAll" /></a> | <a href="javascript: pagetree{/document/module/moduleID}.closeAll();"><xsl:value-of select="$collapseAll" /></a></p>

			<script type="text/javascript">
				pagetree<xsl:value-of select="/document/module/moduleID"/> = new dTree('pagetree<xsl:value-of select="/document/module/moduleID"/>','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/');
				pagetree<xsl:value-of select="/document/module/moduleID"/>.icon.root = '<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/img/globe.gif';
				<xsl:apply-templates select="section" mode="pagetree"/>
				
				document.write(pagetree<xsl:value-of select="/document/module/moduleID"/>);
			</script>
		</div>				
	</xsl:template>
	
	<xsl:template match="section" mode="pagetree">
	
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
		
		<xsl:variable name="description">
            <xsl:call-template name="common_js_escape">
               <xsl:with-param name="text" select="description" />
            </xsl:call-template>			
		</xsl:variable>			

		pagetree<xsl:value-of select="/document/module/moduleID"/>.add('section<xsl:value-of select="sectionID"/>','<xsl:value-of select="$parentSectionID"/>','<xsl:value-of select="$name"/>','','<xsl:value-of select="$description"/>','','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/img/folder.gif','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/img/folderopen.gif','','<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/dtree/img/empty.gif"/><a href="{/document/requestinfo/currentURI}/{/document/module/alias}/add/{sectionID}" title="{$addPageInSection}: {$name}"><img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/page_add.png"/></a>');

		<xsl:apply-templates select="subsections/section" mode="pagetree"/>
		
		<xsl:apply-templates select="pages/page" mode="dtree"/>
	</xsl:template>
	
	<xsl:template match="page" mode="dtree">	
	
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
	
		<xsl:choose>
			<xsl:when test="enabled='true'">
				pagetree<xsl:value-of select="/document/module/moduleID"/>.add('page<xsl:value-of select="pageID"/>','section<xsl:value-of select="sectionID"/>','<xsl:value-of select="$name"/>','<xsl:value-of select="/document/requestinfo/currentURI"/>/<xsl:value-of select="/document/module/alias"/>/show/<xsl:value-of select="pageID"/>','<xsl:value-of select="$description"/>','','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/><xsl:text>/pics/page.png</xsl:text>','','','<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/dtree/img/empty.gif"/><a href="{/document/requestinfo/currentURI}/{/document/module/alias}/move/{pageID}" title="{$movePage}: {$name}"><img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/page_move.png"/></a><a href="{/document/requestinfo/currentURI}/{/document/module/alias}/copy/{pageID}" title="{$copyPage}: {$name}"><img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/page_copy.png"/></a><a href="{/document/requestinfo/currentURI}/{/document/module/alias}/update/{pageID}" title="{$editPage}: {$name}"><img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/page_edit.png"/></a><a href="javascript:askBeforeRedirect(\'{$deletePage}: {$nameWithoutQuotes}?\',\'{/document/requestinfo/currentURI}/{/document/module/alias}/delete/{pageID}\');" title="{$deletePage}: {$name}"><img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/page_delete.png"/></a>');
			</xsl:when>
			<xsl:otherwise>
				pagetree<xsl:value-of select="/document/module/moduleID"/>.add('page<xsl:value-of select="pageID"/>','section<xsl:value-of select="sectionID"/>','<xsl:value-of select="$name"/>','<xsl:value-of select="/document/requestinfo/currentURI"/>/<xsl:value-of select="/document/module/alias"/>/show/<xsl:value-of select="pageID"/>','<xsl:value-of select="$description"/>','','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/><xsl:text>/pics/page_disabled.png</xsl:text>','','','<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/dtree/img/empty.gif"/><a href="{/document/requestinfo/currentURI}/{/document/module/alias}/move/{pageID}" title="{$movePage}: {$name}"><img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/page_move.png"/></a><a href="{/document/requestinfo/currentURI}/{/document/module/alias}/copy/{pageID}" title="{$copyPage}: {$name}"><img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/page_copy.png"/></a><a href="{/document/requestinfo/currentURI}/{/document/module/alias}/update/{pageID}" title="{$editPage}: {$name}"><img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/page_edit.png"/></a><a href="javascript:askBeforeRedirect(\'{$deletePage}: {$nameWithoutQuotes}?\',\'{/document/requestinfo/currentURI}/{/document/module/alias}/delete/{pageID}\');" title="{$deletePage}: {$name}"><img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/page_delete.png"/></a>');	
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="updatePageForm">
		
		<h1><xsl:value-of select="$editingOfPage"/> "<xsl:value-of select="page/name"/>" <xsl:value-of select="$inSection"/> "<xsl:value-of select="section/name"/>"</h1>
		
		<xsl:apply-templates select="validationException/validationError"/>
		
		<form method="post" action="{/document/requestinfo/uri}" accept-charset="ISO-8859-1">
			
			<div class="floatleft full bigmarginbottom">
				<table width="100%">
					<tr>
						<td><xsl:value-of select="$name"/>:</td>
						<td>
							<input type="text" name="name" size="40">
								<xsl:attribute name="value">
									<xsl:choose>
										<xsl:when test="requestparameters/parameter[name='name']">
											<xsl:value-of select="requestparameters/parameter[name='name']/value"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="page/name"/>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:attribute>
							</input>					
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$description"/>:</td>
						<td>
							<input type="text" name="description" size="40">
								<xsl:attribute name="value">
									<xsl:choose>
										<xsl:when test="requestparameters/parameter[name='description']">
											<xsl:value-of select="requestparameters/parameter[name='description']/value"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="page/description"/>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:attribute>
							</input>					
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$alias"/></td>
						<td>
							<input type="text" name="alias" size="40">
								<xsl:attribute name="value">
									<xsl:choose>
										<xsl:when test="requestparameters/parameter[name='alias']">
											<xsl:value-of select="requestparameters/parameter[name='alias']/value"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="page/alias"/>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:attribute>
							</input>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<h2><xsl:value-of select="$content"/></h2>
							
							<textarea class="fckeditor" name="text" rows="20">
								<xsl:choose>
									<xsl:when test="requestparameters/parameter[name='text']">
										<xsl:call-template name="removeLineBreak">
											<xsl:with-param name="string">
												<xsl:value-of select="requestparameters/parameter[name='text']/value"/>
											</xsl:with-param>
										</xsl:call-template>
									</xsl:when>
									<xsl:otherwise>
										<xsl:call-template name="removeLineBreak">
											<xsl:with-param name="string">
												<xsl:value-of select="page/text"/>
											</xsl:with-param>
										</xsl:call-template>
									</xsl:otherwise>
								</xsl:choose>
							</textarea>
							
							<xsl:call-template name="initFCKEditor" />
						</td>
					</tr>
				</table>
			</div>
			
			<h2><xsl:value-of select="$additionalSettings"/></h2>
			
			<div class="floatleft full bigmarginbottom">
				<table>
					<tr>
						<td>
							<input type="checkbox" name="enabled">
								
								<xsl:choose>
									<xsl:when test="requestparameters">
										<xsl:if test="requestparameters/parameter[name='enabled']">
											<xsl:attribute name="checked"/>
										</xsl:if>
									</xsl:when>
									<xsl:when test="page/enabled='true'">
										<xsl:attribute name="checked"/>
									</xsl:when>
								</xsl:choose>						
							</input>
												
							<xsl:value-of select="$activatePage"/>
						</td>
					</tr>
				
					<tr>
						<td>
							<input type="checkbox" name="visibleInMenu">
								<xsl:choose>
									<xsl:when test="requestparameters">
										<xsl:if test="requestparameters/parameter[name='visibleInMenu']">
											<xsl:attribute name="checked"/>
										</xsl:if>
									</xsl:when>
									<xsl:when test="page/visibleInMenu='true'">
										<xsl:attribute name="checked"/>
									</xsl:when>
								</xsl:choose>
							</input><xsl:value-of select="$showInMenu"/>
						</td>
					</tr>
					
					<tr>
						<td>
							<input type="checkbox" name="breadCrumb">
								<xsl:choose>
									<xsl:when test="requestparameters">
										<xsl:if test="requestparameters/parameter[name='breadCrumb']">
											<xsl:attribute name="checked"/>
										</xsl:if>
									</xsl:when>
									<xsl:when test="page/breadCrumb='true'">
										<xsl:attribute name="checked"/>
									</xsl:when>
								</xsl:choose>
							</input><xsl:value-of select="$showBreadCrumb"/>
						</td>
					</tr>
			</table>
			</div>
			
			<h2><xsl:value-of select="$access"/></h2>
			
			<div class="floatleft full bigmarginbottom">
				<table>
					<tr>
						<td colspan="2">
							<input type="checkbox" name="adminAccess" value="true">
								<xsl:choose>
									<xsl:when test="requestparameters">
										<xsl:if test="requestparameters/parameter[name='adminAccess']">
											<xsl:attribute name="checked"/>
										</xsl:if>
									</xsl:when>
									<xsl:when test="page/adminAccess='true'">
										<xsl:attribute name="checked"/>
									</xsl:when>
								</xsl:choose>
							</input>
							<xsl:value-of select="$admins"/>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<input type="checkbox" name="userAccess" value="true">
								<xsl:choose>
									<xsl:when test="requestparameters">
										<xsl:if test="requestparameters/parameter[name='userAccess']">
											<xsl:attribute name="checked"/>
										</xsl:if>
									</xsl:when>
									<xsl:when test="page/userAccess='true'">
										<xsl:attribute name="checked"/>
									</xsl:when>
								</xsl:choose>
							</input>
							<xsl:value-of select="$loggedInUsers"/>
						</td>
					</tr>			
					<tr>
						<td colspan="2">
							<input type="checkbox" name="anonymousAccess" value="true">
								<xsl:choose>
									<xsl:when test="requestparameters">
										<xsl:if test="requestparameters/parameter[name='anonymousAccess']">
											<xsl:attribute name="checked"/>
										</xsl:if>
									</xsl:when>
									<xsl:when test="page/anonymousAccess='true'">
										<xsl:attribute name="checked"/>
									</xsl:when>
								</xsl:choose>						
							</input>
							<xsl:value-of select="$nonLoggedInUsers"/>
						</td>
					</tr>
				</table>
			</div>
			
			<xsl:call-template name="groups"/>
			
			<xsl:call-template name="users"/>
			
			<div align="right">
				<input type="submit" value="{$saveChanges}"/>			
			</div>										
		</form>		
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
			<p style="color: red;">
				<xsl:choose>
					<xsl:when test="validationErrorType='RequiredField'">
						<xsl:value-of select="$validationError.requiredField"/>
					</xsl:when>
					<xsl:when test="validationErrorType='InvalidFormat'">
						<xsl:value-of select="$validationError.invalidFormat"/>
					</xsl:when>		
					<xsl:otherwise>
						<xsl:value-of select="$validationError.unknown"/>
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:text> </xsl:text>
				
				<xsl:choose>
					<xsl:when test="fieldName = 'name'">
						<xsl:value-of select="$name"/>!
					</xsl:when>
					<xsl:when test="fieldName = 'description'">
						<xsl:value-of select="$description"/>!
					</xsl:when>
					<xsl:when test="fieldName = 'alias'">
						<xsl:value-of select="$alias"/>!
					</xsl:when>
					<xsl:when test="fieldName = 'text'">
						<xsl:value-of select="$content"/>!
					</xsl:when>																																						
					<xsl:otherwise>
						<xsl:value-of select="fieldName"/>!
					</xsl:otherwise>
				</xsl:choose>			
			</p>
		</xsl:if>
		
		<xsl:if test="messageKey">
			<p style="color: red;">
				<xsl:choose>
					<xsl:when test="messageKey='duplicatePageAlias'">
						<xsl:value-of select="$validationError.duplicateAlias"/>!
					</xsl:when>				
					<xsl:otherwise>
						<xsl:value-of select="$validationError.unknownErrorOccurred"/>
					</xsl:otherwise>
				</xsl:choose>
			</p>
		</xsl:if>
		
	</xsl:template>	
	
	<xsl:template match="addPageForm">
		
		<h1><xsl:value-of select="$addPageInSection"/><xsl:text>&#x20;</xsl:text><xsl:value-of select="section/name"/></h1>
		
		<xsl:apply-templates select="validationException/validationError"/>
		
		<form method="post" action="{/document/requestinfo/uri}" accept-charset="ISO-8859-1">
			
			<div class="floatleft full bigmarginbottom">
				<table width="100%">
					<tr>
						<td><xsl:value-of select="$name"/>:</td>
						<td><input type="text" size="50" maxlength="255" name="name" value="{requestparameters/parameter[name='name']/value}"/></td>
					</tr>
					<tr>
						<td><xsl:value-of select="$description"/>:</td>
						<td><input type="text" size="50" maxlength="255" name="description" value="{requestparameters/parameter[name='description']/value}"/></td>
					</tr>
					<tr>
						<td><xsl:value-of select="$alias"/>:</td>
						<td><input type="text" size="50" maxlength="255" name="alias" value="{requestparameters/parameter[name='alias']/value}"/></td>
					</tr>				
					<tr>
						<td colspan="2">
							<h2><xsl:value-of select="$content"/></h2>							
													
							<textarea class="fckeditor" name="text" rows="20">
								<xsl:value-of select="requestparameters/parameter[name='text']/value"/>
							</textarea>						
							
							<xsl:call-template name="initFCKEditor" /> 
						</td>
					</tr>
				</table>
			</div>
			
			<h2><xsl:value-of select="$additionalSettings"/></h2>
			
			<div class="floatleft full bigmarginbottom">
				<table>
					<tr>
						<td>
							<input type="checkbox" name="enabled">
								
								<xsl:choose>
									<xsl:when test="requestparameters">
										<xsl:if test="requestparameters/parameter[name='enabled']">
											<xsl:attribute name="checked">true</xsl:attribute>
										</xsl:if>								
									</xsl:when>
									<xsl:otherwise>
										<xsl:attribute name="checked">true</xsl:attribute>
									</xsl:otherwise>
								</xsl:choose>
								
							</input><xsl:value-of select="$activatePage"/>
						</td>
					</tr>						
								
					<tr>
						<td>
							<input type="checkbox" name="visibleInMenu">
			
								<xsl:choose>
									<xsl:when test="requestparameters">
										<xsl:if test="requestparameters/parameter[name='visibleInMenu']">
											<xsl:attribute name="checked">true</xsl:attribute>
										</xsl:if>								
									</xsl:when>
									<xsl:otherwise>
										<xsl:attribute name="checked">true</xsl:attribute>
									</xsl:otherwise>
								</xsl:choose>
																						
							</input><xsl:value-of select="$showInMenu"/>
						</td>
					</tr>
					
					<tr>
						<td>
							<input type="checkbox" name="breadCrumb">
								
								<xsl:choose>
									<xsl:when test="requestparameters">
										<xsl:if test="requestparameters/parameter[name='breadCrumb']">
											<xsl:attribute name="checked">true</xsl:attribute>
										</xsl:if>								
									</xsl:when>
									<xsl:otherwise>
										<xsl:attribute name="checked">true</xsl:attribute>
									</xsl:otherwise>
								</xsl:choose>
								
							</input><xsl:value-of select="$showBreadCrumb"/>
						</td>
					</tr>
					
				</table>
			</div>
			
			<h2><xsl:value-of select="$access"/></h2>
			
			<div class="floatleft full bigmarginbottom">
				<table>					
					<tr>
						<td>
							<input type="checkbox" name="adminAccess">
								<xsl:if test="requestparameters/parameter[name='adminAccess']">
									<xsl:attribute name="checked">true</xsl:attribute>
								</xsl:if>						
							</input><xsl:value-of select="$admins"/>
						</td>
					</tr><tr>
						<td>
							<input type="checkbox" name="userAccess">
								<xsl:if test="requestparameters/parameter[name='userAccess']">
									<xsl:attribute name="checked">true</xsl:attribute>
								</xsl:if>
							</input><xsl:value-of select="$loggedInUsers"/>
						</td>
					</tr><tr>
						<td>
							<input type="checkbox" name="anonymousAccess">
								<xsl:if test="requestparameters/parameter[name='anonymousAccess']">
									<xsl:attribute name="checked">true</xsl:attribute>
								</xsl:if>
							</input><xsl:value-of select="$nonLoggedInUsers"/>
						</td>
					</tr>
					
				</table>
			</div>
			
			<xsl:call-template name="groups"/>
			
			<xsl:call-template name="users"/>
			
			<div align="right">
				<input type="submit" value="{$addPage}"/>
			</div>										
		</form>		
	</xsl:template>	
	
	<xsl:template match="preview">
						
		<div class="border border-radius-small padding marginbottom">
		
			<div align="right" style="float:right">
				<!--
				<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/export/{page/pageID}" title="Exporta sidan {page/name} till XML">
					<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/exportpage.gif"/>
				</a>
				 -->
				 
				<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/move/{page/pageID}" title="{$movePage}: {page/name}">
					<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/page_move.png"/>
				</a>
				
				<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/copy/{page/pageID}" title="{$copyPage}: {page/name}">
					<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/page_copy.png"/>
				</a>		 
			 
				<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/update/{page/pageID}" title="{$editPage} {page/name}">
					<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/page_edit.png"/>
				</a>				
			
				<a title="{$deletePage}: {page/name}">
					<xsl:attribute name="href">javascript:askBeforeRedirect('<xsl:value-of select="$deletePage"/> "<xsl:value-of select="page/name"/>?','<xsl:value-of select="/document/requestinfo/currentURI"/>/<xsl:value-of select="/document/module/alias"/>/delete/<xsl:value-of select="page/pageID"/>');</xsl:attribute>					
					<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/page_delete.png" border="0" alt="{$deletePage}"/>
				</a>
			</div>
					
			<strong>
				<xsl:value-of select="$pagePreview"/> "<xsl:value-of select="page/name"/>" <xsl:value-of select="$inSection"/> "<xsl:value-of select="section/name"/>"
			</strong>
			
			<xsl:if test="module">
				<br/>
				<a href="{/document/requestinfo/contextpath}{section/fullAlias}/{module/alias}/{page/alias}"><xsl:value-of select="$showPageOutsideAdminView"/></a>
			</xsl:if>	
		</div>	

		<xsl:value-of select="page/text" disable-output-escaping="yes"/>
	</xsl:template>		
	
	<xsl:template match="movePageForm">
				
		<h1><xsl:value-of select="$movePage"/> "<xsl:value-of select="page/name"/>"</h1>
		<p><xsl:value-of select="$movePageInstruction"/></p>
		
		<div class="dtree">
			<p><a href="javascript:sectiontree{/document/module/moduleID}.openAll();"><xsl:value-of select="$expandAll"/></a> | <a href="javascript:sectiontree{/document/module/moduleID}.closeAll();"><xsl:value-of select="$collapseAll"/></a></p>

			<script type="text/javascript">
				sectiontree<xsl:value-of select="/document/module/moduleID"/> = new dTree('sectiontree<xsl:value-of select="/document/module/moduleID"/>','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/');
				sectiontree<xsl:value-of select="/document/module/moduleID"/>.icon.root = '<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/img/globe.gif';
				<xsl:apply-templates select="sections/section" mode="move"/>
				
				document.write(sectiontree<xsl:value-of select="/document/module/moduleID"/>);
			</script>
		</div>
	</xsl:template>
	
	<xsl:template match="section" mode="move">
	
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
		
		<xsl:variable name="description">
            <xsl:call-template name="common_js_escape">
               <xsl:with-param name="text" select="description" />
            </xsl:call-template>			
		</xsl:variable>			

		sectiontree<xsl:value-of select="/document/module/moduleID"/>.add('section<xsl:value-of select="sectionID"/>','<xsl:value-of select="$parentSectionID"/>','<xsl:value-of select="$name"/>','<xsl:value-of select="/document/requestinfo/currentURI"/>/<xsl:value-of select="/document/module/alias"/>/move/<xsl:value-of select="/document/movePageForm/page/pageID"/>/<xsl:value-of select="sectionID"/>','<xsl:value-of select="$description"/>','','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/img/folder.gif','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/img/folderopen.gif','','');

		<xsl:apply-templates select="subsections/section" mode="move"/>
	</xsl:template>
	
	<xsl:template match="copyPageForm">
				
		<h1><xsl:value-of select="$copyPage"/> "<xsl:value-of select="page/name"/>"</h1>
		<p><xsl:value-of select="$copyPageInstruction"/></p>
		
		<div class="dtree">
			<p><a href="javascript:sectiontree{/document/module/moduleID}.openAll();"><xsl:value-of select="$expandAll"/></a> | <a href="javascript:sectiontree{/document/module/moduleID}.closeAll();"><xsl:value-of select="$collapseAll"/></a></p>

			<script type="text/javascript">
				sectiontree<xsl:value-of select="/document/module/moduleID"/> = new dTree('sectiontree<xsl:value-of select="/document/module/moduleID"/>','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/');
				sectiontree<xsl:value-of select="/document/module/moduleID"/>.icon.root = '<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/img/globe.gif';
				<xsl:apply-templates select="sections/section" mode="copy"/>
				
				document.write(sectiontree<xsl:value-of select="/document/module/moduleID"/>);
			</script>
		</div>
	</xsl:template>
	
	<xsl:template match="section" mode="copy">
	
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
		
		<xsl:variable name="description">
            <xsl:call-template name="common_js_escape">
               <xsl:with-param name="text" select="description" />
            </xsl:call-template>			
		</xsl:variable>			

		sectiontree<xsl:value-of select="/document/module/moduleID"/>.add('section<xsl:value-of select="sectionID"/>','<xsl:value-of select="$parentSectionID"/>','<xsl:value-of select="$name"/>','<xsl:value-of select="/document/requestinfo/currentURI"/>/<xsl:value-of select="/document/module/alias"/>/copy/<xsl:value-of select="/document/copyPageForm/page/pageID"/>/<xsl:value-of select="sectionID"/>','<xsl:value-of select="$description"/>','','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/img/folder.gif','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/img/folderopen.gif','','');

		<xsl:apply-templates select="subsections/section" mode="copy"/>
	</xsl:template>
	
	<xsl:template name="groups" >
		<h3><xsl:value-of select="$groups"/></h3>
		
		<xsl:call-template name="GroupList">
			<xsl:with-param name="connectorURL">
				<xsl:value-of select="/document/requestinfo/currentURI"/>
				<xsl:text>/</xsl:text>
				<xsl:value-of select="/document/module/alias"/>
				<xsl:text>/groups</xsl:text>
			</xsl:with-param>
			<xsl:with-param name="name" select="'group'"/>
			<xsl:with-param name="groups" select="groups" />
			<xsl:with-param name="document" select="/document" />
		</xsl:call-template>
		
		<br/>
	</xsl:template>
	
	<xsl:template name ="users" >
		<h3><xsl:value-of select="$users"/></h3>
		
		<xsl:call-template name="UserList">
			<xsl:with-param name="connectorURL">
				<xsl:value-of select="/document/requestinfo/currentURI"/>
				<xsl:text>/</xsl:text>
				<xsl:value-of select="/document/module/alias"/>
				<xsl:text>/users</xsl:text>
			</xsl:with-param>
			<xsl:with-param name="name" select="'user'"/>
			<xsl:with-param name="users" select="users" />
			<xsl:with-param name="document" select="/document" />
			<xsl:with-param name="showUsername" select="true()" />
		</xsl:call-template>
		
		<br/>
	</xsl:template>
	
	<xsl:template name="initFCKEditor">
		
		<!-- Call global CKEditor init template -->
		<xsl:call-template name="initializeFCKEditor">
			<xsl:with-param name="basePath"><xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/ckeditor/</xsl:with-param>
			<xsl:with-param name="customConfig">config.js</xsl:with-param>
			<xsl:with-param name="editorContainerClass">fckeditor</xsl:with-param>
			<xsl:with-param name="editorHeight">400</xsl:with-param>
			<xsl:with-param name="filebrowserBrowseUri">filemanager/index.html?Connector=<xsl:value-of select="/document/requestinfo/uri"/>/../../connector</xsl:with-param>
			<xsl:with-param name="filebrowserImageBrowseUri">filemanager/index.html?Connector=<xsl:value-of select="/document/requestinfo/uri"/>/../../connector</xsl:with-param>
			<xsl:with-param name="contentsCss">
				<xsl:if test="cssPath">
					<xsl:value-of select="/document/requestinfo/contextpath" /><xsl:value-of select="cssPath"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
		
	</xsl:template>
	
			
</xsl:stylesheet>