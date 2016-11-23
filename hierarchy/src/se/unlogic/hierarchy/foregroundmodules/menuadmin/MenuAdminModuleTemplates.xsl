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

	<xsl:template match="document">
		<div class="contentitem">			
			<xsl:apply-templates select="sections"/>
			<xsl:apply-templates select="sortMenu"/>
			<xsl:apply-templates select="addMenuItem"/>
			<xsl:apply-templates select="updateMenuItem"/>
			<xsl:apply-templates select="moveMenuItem"/>
		</div>
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
					<xsl:when test="validationErrorType='TooLong'">
						<xsl:value-of select="$validationError.tooLong"/>
					</xsl:when>											
					<xsl:otherwise>
						<xsl:value-of select="$validationError.unknown"/>
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:text>&#x20;</xsl:text>
				
				<xsl:choose>
					<xsl:when test="fieldName = 'name'">
						<xsl:value-of select="$name"/>
					</xsl:when>
					<xsl:when test="fieldName = 'description'">
						<xsl:value-of select="$description"/>
					</xsl:when>
					<xsl:when test="fieldName = 'itemtype'">
						<xsl:value-of select="$type"/>
					</xsl:when>																																						
					<xsl:otherwise>
						<xsl:value-of select="fieldName"/><xsl:text>!</xsl:text>
					</xsl:otherwise>
				</xsl:choose>			
			</p>
		</xsl:if>
		
		<xsl:if test="messageKey">
			<p style="color: red;">
				<xsl:value-of select="$validationError.unknownErrorOccurred"/>
			</p>
		</xsl:if>
		
	</xsl:template>	
	
	<xsl:template match="updateMenuItem">
		<script 
			type="text/javascript"
			src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/js/menuadmin.js">
		</script>	
	
		<h1><xsl:value-of select="$editMenu"/></h1>
		
		<xsl:apply-templates select="validationException/validationError"/>
				
		<form method="post">		
			<h2><xsl:value-of select="$type"/></h2>
			
			<label>
				<input type="radio" name="itemtype" value="BLANK" onClick="hideShowParamTable()" id="blankRadioButton">				
					<xsl:choose>
						<xsl:when test="requestparameters">
							<xsl:if test="requestparameters/parameter[name='itemtype']/value='BLANK'">
								<xsl:attribute name="checked"/>
							</xsl:if>						
						</xsl:when>
						<xsl:otherwise>
							<xsl:if test="menuitem/itemType='BLANK'">
								<xsl:attribute name="checked"/>
							</xsl:if>						
						</xsl:otherwise>
					</xsl:choose>						
				</input>
				<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/blank.gif"/>
				<xsl:value-of select="$whitespace"/>
			</label>
			
			<br/>
			
			<label>
				<input type="radio" name="itemtype" value="MENUITEM" onClick="hideShowParamTable()">
					<xsl:choose>
						<xsl:when test="requestparameters">
							<xsl:if test="requestparameters/parameter[name='itemtype']/value='MENUITEM'">
								<xsl:attribute name="checked"/>
							</xsl:if>						
						</xsl:when>
						<xsl:otherwise>
							<xsl:if test="menuitem/itemType='MENUITEM'">
								<xsl:attribute name="checked"/>
							</xsl:if>						
						</xsl:otherwise>
					</xsl:choose>										
				</input>
				<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/menuitem.gif"/>
				<xsl:value-of select="$regularMenu"/>
			</label>
			
			<br/>
			
			<label>
				<input type="radio" name="itemtype" value="TITLE" onClick="hideShowParamTable()">
					<xsl:choose>
						<xsl:when test="requestparameters">
							<xsl:if test="requestparameters/parameter[name='itemtype']/value='TITLE'">
								<xsl:attribute name="checked"/>
							</xsl:if>						
						</xsl:when>
						<xsl:otherwise>
							<xsl:if test="menuitem/itemType='TITLE'">
								<xsl:attribute name="checked"/>
							</xsl:if>						
						</xsl:otherwise>
					</xsl:choose>											
				</input>
				<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/title.gif"/>
				<xsl:value-of select="$heading"/>
			</label>
			
			<br/>
			
			<label>
				<input type="radio" name="itemtype" value="SECTION" onClick="hideShowParamTable()">
					<xsl:choose>
						<xsl:when test="requestparameters">
							<xsl:if test="requestparameters/parameter[name='itemtype']/value='SECTION'">
								<xsl:attribute name="checked"/>
							</xsl:if>						
						</xsl:when>
						<xsl:otherwise>
							<xsl:if test="menuitem/itemType='SECTION'">
								<xsl:attribute name="checked"/>
							</xsl:if>						
						</xsl:otherwise>
					</xsl:choose>											
				</input>
				<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/subsection.gif"/>
				<xsl:value-of select="$section"/>
			</label>
			
			<div id="paramDiv">
				<xsl:choose>
					<xsl:when test="requestparameters">
						<xsl:if test="requestparameters/parameter[name='itemtype']/value='BLANK'">
							<xsl:attribute name="style">display:none;</xsl:attribute>
						</xsl:if>						
					</xsl:when>
					<xsl:otherwise>
						<xsl:if test="menuitem/itemType='BLANK'">
							<xsl:attribute name="style">display:none;</xsl:attribute>
						</xsl:if>						
					</xsl:otherwise>
				</xsl:choose>
					
				<h2><xsl:value-of select="$parameters"/></h2>		
			
				<table>
					<tr>
						<td><xsl:value-of select="$name"/>:</td>
						<td>
							<input type="text" name="name" size="55">
								<xsl:attribute name="value">
									<xsl:choose>
										<xsl:when test="requestparameters">
											<xsl:value-of select="requestparameters/parameter[name='name']/value"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="menuitem/name"/>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:attribute>							
							</input>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$description"/>:</td>
						<td>
							<input type="text" name="description" size="55">
								<xsl:attribute name="value">
									<xsl:choose>
										<xsl:when test="requestparameters">
											<xsl:value-of select="requestparameters/parameter[name='description']/value"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="menuitem/description"/>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:attribute>							
							</input>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$address"/>:</td>
						<td>
							<input type="text" name="url" size="55">
								<xsl:attribute name="value">
									<xsl:choose>
										<xsl:when test="requestparameters">
											<xsl:value-of select="requestparameters/parameter[name='url']/value"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="menuitem/url"/>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:attribute>							
							</input>
						</td>
					</tr>
				</table>			
			</div>
			
			<h2><xsl:value-of select="$access"/></h2>

			<table>
				<tr>
					<td colspan="2">
						<input type="checkbox" name="anonymousAccess">
							<xsl:choose>
								<xsl:when test="requestparameters">
									<xsl:if test="requestparameters/parameter[name='anonymousAccess']">
										<xsl:attribute name="checked">true</xsl:attribute>
									</xsl:if>
								</xsl:when>
								<xsl:otherwise>
									<xsl:if test="menuitem/anonymousAccess='true'">
										<xsl:attribute name="checked">true</xsl:attribute>
									</xsl:if>
								</xsl:otherwise>
							</xsl:choose>						
						</input><xsl:value-of select="$nonLoggedInUsers"/><br/>
						
						<input type="checkbox" name="userAccess">
							<xsl:choose>
								<xsl:when test="requestparameters">
									<xsl:if test="requestparameters/parameter[name='userAccess']">
										<xsl:attribute name="checked">true</xsl:attribute>
									</xsl:if>
								</xsl:when>
								<xsl:otherwise>
									<xsl:if test="menuitem/userAccess='true'">
										<xsl:attribute name="checked">true</xsl:attribute>
									</xsl:if>
								</xsl:otherwise>
							</xsl:choose>
						</input><xsl:value-of select="$loggedInUsers"/><br/>
						
						<input type="checkbox" name="adminAccess">
							<xsl:choose>
								<xsl:when test="requestparameters">
									<xsl:if test="requestparameters/parameter[name='adminAccess']">
										<xsl:attribute name="checked">true</xsl:attribute>
									</xsl:if>
								</xsl:when>
								<xsl:otherwise>
									<xsl:if test="menuitem/adminAccess='true'">
										<xsl:attribute name="checked">true</xsl:attribute>
									</xsl:if>
								</xsl:otherwise>
							</xsl:choose>				
						</input><xsl:value-of select="$admins"/><br/>					
					</td>
				</tr>
			</table>
			
			<xsl:call-template name="addUserGroupFields"/>				
			
			<div align="right">
				<input type="submit" value="{$saveChanges}"/>
			</div>
		</form>
	</xsl:template>	
	
	<xsl:template match="addMenuItem">
		<script 
			type="text/javascript"
			src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/js/menuadmin.js">
		</script>	
	
		<h1><xsl:value-of select="$addMenuInSection"/><xsl:text> </xsl:text>"<xsl:value-of select="section/name"/>"</h1>
		
		<xsl:apply-templates select="validationException/validationError"/>
				
		<form method="post">		
			<h2><xsl:value-of select="$type"/></h2>
			
			<label>
				<input type="radio" name="itemtype" value="BLANK" onClick="hideShowParamTable()" id="blankRadioButton">
					<xsl:if test="requestparameters/parameter[name='itemtype']/value='BLANK'">
						<xsl:attribute name="checked"/>
					</xsl:if>						
				</input>
				<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/blank.gif"/>
				<xsl:value-of select="$whitespace"/>
			</label>
			
			<br/>
			
			<label>
				<input type="radio" name="itemtype" value="MENUITEM" onClick="hideShowParamTable()">
					<xsl:if test="requestparameters/parameter[name='itemtype']/value='MENUITEM' or not(requestparameters/parameter[name='itemtype'])">
						<xsl:attribute name="checked"/>
					</xsl:if>						
				</input>
				<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/menuitem.gif"/>
				<xsl:value-of select="$regularMenu"/>
			</label>
			
			<br/>
			
			<label>
				<input type="radio" name="itemtype" value="TITLE" onClick="hideShowParamTable()">
					<xsl:if test="requestparameters/parameter[name='itemtype']/value='TITLE'">
						<xsl:attribute name="checked"/>
					</xsl:if>						
				</input>
				<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/title.gif"/>
				<xsl:value-of select="$heading"/>
			</label>
			
			<br/>
			
			<label>
				<input type="radio" name="itemtype" value="SECTION" onClick="hideShowParamTable()">
					<xsl:if test="requestparameters/parameter[name='itemtype']/value='SECTION'">
						<xsl:attribute name="checked"/>
					</xsl:if>						
				</input>
				<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/subsection.gif"/>
				<xsl:value-of select="$section"/>
			</label>
			
			<div id="paramDiv">
				<h2><xsl:value-of select="$parameters"/></h2>		
			
				<table>
					<tr>
						<td><xsl:value-of select="$name"/>:</td>
						<td>
							<input type="text" name="name" value="{requestparameters/parameter[name='name']/value}" size="55"/>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$description"/>:</td>
						<td>
							<input type="text" name="description" value="{requestparameters/parameter[name='description']/value}" size="55"/>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$address"/>:</td>
						<td>
							<input type="text" name="url" value="{requestparameters/parameter[name='url']/value}" size="55"/>
						</td>
					</tr>
				</table>			
			</div>
			
			<h2><xsl:value-of select="$access"/></h2>

			<table>
				<tr>
					<td colspan="2">
						<input type="checkbox" name="anonymousAccess">
							<xsl:if test="requestparameters/parameter[name='anonymousAccess']">
								<xsl:attribute name="checked">true</xsl:attribute>
							</xsl:if>
						</input><xsl:value-of select="$nonLoggedInUsers"/><br/>
						
						<input type="checkbox" name="userAccess">
							<xsl:if test="requestparameters/parameter[name='userAccess']">
								<xsl:attribute name="checked">true</xsl:attribute>
							</xsl:if>
						</input><xsl:value-of select="$loggedInUsers"/><br/>
						
						<input type="checkbox" name="adminAccess">
							<xsl:if test="requestparameters/parameter[name='adminAccess']">
								<xsl:attribute name="checked">true</xsl:attribute>
							</xsl:if>						
						</input><xsl:value-of select="$admins"/><br/>					
					</td>
				</tr>
			</table>
			
			<xsl:call-template name="addUserGroupFields"/>
			
			<div align="right">
				<input type="submit" value="{$add}"/>
			</div>
		</form>
	</xsl:template>

	<xsl:template name="addUserGroupFields">
	
		<h3><xsl:value-of select="$users"/></h3>
	
		<xsl:call-template name="UserList">
			<xsl:with-param name="connectorURL">
				<xsl:value-of select="/document/requestinfo/currentURI"/>
				<xsl:text>/</xsl:text>
				<xsl:value-of select="/document/module/alias"/>
				<xsl:text>/users</xsl:text>
			</xsl:with-param>
			<xsl:with-param name="name" select="'user'"/>
			<xsl:with-param name="users" select="Users" />
			<xsl:with-param name="document" select="/document" />
		</xsl:call-template>	
	
		<h3><xsl:value-of select="$groups"/></h3>
	
		<xsl:call-template name="GroupList">
			<xsl:with-param name="connectorURL">
				<xsl:value-of select="/document/requestinfo/currentURI"/>
				<xsl:text>/</xsl:text>
				<xsl:value-of select="/document/module/alias"/>
				<xsl:text>/groups</xsl:text>
			</xsl:with-param>
			<xsl:with-param name="name" select="'group'"/>
			<xsl:with-param name="groups" select="Groups" />
			<xsl:with-param name="document" select="/document" />
		</xsl:call-template>	
	
	</xsl:template>

	<xsl:template match="sortMenu">
	
		<link 
			rel="stylesheet"
			type="text/css"
			href="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/dbx/menu-dbx.css"
			media="screen, projection"/>		
		
		<script 
			type="text/javascript"
			src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/dbx/menu-dbx.js">
		</script>
		
		<script
			type="text/javascript" 	src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/dbx/menu-dbx-key.js">
		</script>
	
	
		<h1><xsl:value-of select="$sortMenuInSection"/><xsl:text> </xsl:text><xsl:value-of select="section/name"/></h1>
		
		<xsl:choose>
			<xsl:when test="menuitems/menuitem">				
				<xsl:apply-templates select="menuitems"/>
				
				<form method="POST" action="{/document/requestinfo/uri}">
					<input type="hidden" name="itempositions" id="itempositions" size="50"/>
					<div class="floatright clearboth">
						<input type="submit" value="{$saveChanges}"/>
					</div>
				</form>							
			</xsl:when>
			<xsl:otherwise>
				<p><xsl:value-of select="$noMenuesFound"/></p>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>	

	<xsl:template match="menuitems">
		<div id="menu-outer" class="floatleft">
			<div class="dbx-group" id="menusorter">
				<xsl:apply-templates select="menuitem | bundle"/>
			</div>
		</div>
	</xsl:template>
	
	<xsl:template match="menuitem | bundle">
		
		<div class="dbx-box border">

			<p class="dbx-handle background hover">
				<xsl:choose>
					<xsl:when test="name">
						<xsl:attribute name="title"><xsl:value-of select="name"/></xsl:attribute>
						<!-- <img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/menuicon.gif" style="margin-right: 8px" />  -->
						<xsl:value-of select="name"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="title">[<xsl:value-of select="$whitespace"/>]</xsl:attribute>
						[<xsl:value-of select="$whitespace"/>]
					</xsl:otherwise>			
				</xsl:choose>
				<xsl:if	test="menuItemID">
					<font class="required">*</font>
				</xsl:if>	
			</p>
			<ul class="dbx-content lightbackground">

				<xsl:if	test="description">
					<li class="lightbackground"><b><xsl:value-of select="description"/></b></li>
				</xsl:if>
				
				<xsl:if	test="url">
					<li class="lightbackground"><xsl:apply-templates select="url"/></li>
				</xsl:if>

				<li class="lightbackground"><b><xsl:value-of select="$visibleTo"/>:</b></li>
				
				<xsl:if	test="anonymousAccess='true'">
					<li class="lightbackground"><xsl:value-of select="$nonLoggedInUsers"/></li>
				</xsl:if>
				
				<xsl:if	test="userAccess='true'">
					<li class="lightbackground"><xsl:value-of select="$loggedInUsers"/></li>
				</xsl:if>
				
				<xsl:if	test="adminAccess='true'">
					<li class="lightbackground"><xsl:value-of select="$admins"/></li>
				</xsl:if>	
			</ul>
		</div>
	</xsl:template>

	<xsl:template match="url">
		<a>
			<xsl:choose>
				<xsl:when test="../urlType='RELATIVE_FROM_CONTEXTPATH'">
					<xsl:attribute name="href"><xsl:value-of select="/document/requestinfo/contextpath"/><xsl:value-of select="../url"/></xsl:attribute>
				</xsl:when>
				<xsl:when test="../urlType='FULL'">
					<xsl:attribute name="href"><xsl:value-of select="../url"/></xsl:attribute>
				</xsl:when>
			</xsl:choose>
			<xsl:attribute name="title"><xsl:value-of select="../description"/></xsl:attribute>
			<xsl:value-of select="$link"/>
		</a>
	</xsl:template>

	<xsl:template match="sections">	
		<link rel="StyleSheet" href="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/dtree/dtree.css" type="text/css"/>
		<script type="text/javascript" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/dtree/dtree.js"></script>
		<script src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/js/askBeforeRedirect.js"/>	
		
		<h1><xsl:value-of select="/document/module/name"/></h1>		
		
		<div class="dtree">
			<p><a href="javascript: menutree{/document/module/moduleID}.openAll();"><xsl:value-of select="$expandAll"/></a> | <a href="javascript: menutree{/document/module/moduleID}.closeAll();"><xsl:value-of select="$collapseAll"/></a></p>

			<script type="text/javascript">
				menutree<xsl:value-of select="/document/module/moduleID"/> = new dTree('menutree<xsl:value-of select="/document/module/moduleID"/>','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/');
				menutree<xsl:value-of select="/document/module/moduleID"/>.icon.root = '<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/img/globe.gif';
				<xsl:apply-templates select="section" mode="menutree"/>
				
				document.write(menutree<xsl:value-of select="/document/module/moduleID"/>);
			</script>
		</div>				
	</xsl:template>
	
	<xsl:template match="section" mode="menutree">
	
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

		menutree<xsl:value-of select="/document/module/moduleID"/>.add('section<xsl:value-of select="sectionID"/>','<xsl:value-of select="$parentSectionID"/>','<xsl:value-of select="$name"/>','','<xsl:value-of select="$description"/>','','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/img/folder.gif','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/img/folderopen.gif','',
		
		'<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/dtree/img/empty.gif"/>
		
		<xsl:choose>
			<xsl:when test="@cached='true'">
				<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/addMenuItem/{sectionID}" title="{$addMenuInSection}: {$name}">
					<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/menuitemadd.gif"/>
				</a>
				<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/sortMenu/{sectionID}" title="{$sortMenuInSection}: {$name}">
					<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/sortmenu.gif"/>
				</a>							
			</xsl:when>
			<xsl:otherwise>
				<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/warning.png" alt="{$thisSectionIsNotStarted}" title="{$thisSectionIsNotStarted}"/>			
			</xsl:otherwise>
		</xsl:choose>');

		<xsl:apply-templates select="subsections/section" mode="menutree"/>
		
		<xsl:apply-templates select="menuitems/menuitem | menuitems/bundle" mode="dtree"/>
	</xsl:template>
	
	<xsl:template match="menuitem | bundle" mode="dtree">	
	
		<xsl:variable name="name">
			<xsl:choose>
				<xsl:when test="name">
		            <xsl:call-template name="common_js_escape">
		               <xsl:with-param name="text" select="name" />
		            </xsl:call-template>				
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$whitespace"/>
				</xsl:otherwise>
			</xsl:choose>			
		</xsl:variable>
		
		<xsl:variable name="nameWithoutQuotes">
			<xsl:choose>
				<xsl:when test="name">
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
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$whitespace"/>
				</xsl:otherwise>
			</xsl:choose>						
		</xsl:variable>
		
		<xsl:variable name="description">
            <xsl:call-template name="common_js_escape">
               <xsl:with-param name="text" select="description" />
            </xsl:call-template>			
		</xsl:variable>		
		
		<xsl:variable name="icon">
			<xsl:choose>
				<xsl:when test="itemType='BLANK'">
					<xsl:text>blank.gif</xsl:text>				
				</xsl:when>
				<xsl:when test="itemType='MENUITEM'">
					<xsl:text>menuitem.gif</xsl:text>				
				</xsl:when>
				<xsl:when test="itemType='TITLE'">
					<xsl:text>title.gif</xsl:text>				
				</xsl:when>
				<xsl:when test="itemType='SECTION'">
					<xsl:text>subsection.gif</xsl:text>				
				</xsl:when>												
			</xsl:choose>			
		</xsl:variable>
		
		menutree<xsl:value-of select="/document/module/moduleID"/>.add('menuitem<xsl:value-of select="menuIndex"/>-section<xsl:value-of select="../../sectionID"/>','section<xsl:value-of select="../../sectionID"/>','<xsl:value-of select="$name"/>','','<xsl:value-of select="$description"/>','','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/pics/<xsl:value-of select="$icon"/>','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/pics/<xsl:value-of select="$icon"/>','',
		
		'<xsl:if test="virtualMenuItem">
			<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/dtree/img/empty.gif"/>
			
			<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/moveMenuItem/{virtualMenuItem/menuItemID}" title="{$moveMenu} {$name} {$toAnotherSection}">
				<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/move.gif"/>
			</a>
			
			<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/updateMenuItem/{virtualMenuItem/menuItemID}" title="{$editMenu}: {$name}">
				<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/pen.png"/>
			</a>
			
			<a href="javascript:askBeforeRedirect(\'Ta bort menyalternativ: {$nameWithoutQuotes}?\',\'{/document/requestinfo/currentURI}/{/document/module/alias}/deleteMenuItem/{virtualMenuItem/menuItemID}\');" title="{$removeMenu}: {$name}">
				<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/delete.png"/>
			</a>		
		</xsl:if>');
		
		<xsl:variable name="source">
			<xsl:choose>
				<xsl:when test="virtualMenuItem">
					<xsl:value-of select="virtualMenu"/>				
				</xsl:when>
				<xsl:when test="moduleMenuItem">
					<xsl:variable name="moduleID" select="moduleMenuItem/moduleID"/>
					<xsl:value-of select="theModule"/><xsl:text> </xsl:text><xsl:value-of select="../../modules/module[moduleID=$moduleID]/name"/><xsl:text> (Modul ID: </xsl:text><xsl:value-of select="moduleMenuItem/moduleID"/><xsl:text>, unikt ID: </xsl:text><xsl:value-of select="moduleMenuItem/uniqueID"/><xsl:text>)</xsl:text>				
				</xsl:when>
				<xsl:when test="sectionMenuItem">
					<xsl:value-of select="theSection"/><xsl:text> </xsl:text><xsl:value-of select="name"/><xsl:text> (sektions ID: </xsl:text><xsl:value-of select="sectionMenuItem/subSectionID"/><xsl:text>)</xsl:text>				
				</xsl:when>
				<xsl:when test="name() = 'bundle'">
					<xsl:value-of select="theBundle"/><xsl:text> </xsl:text><xsl:value-of select="name" /> <xsl:text> (</xsl:text><xsl:value-of select="count(menuitems/menuitem)" /><xsl:text> menyalternativ)</xsl:text>
					
					<xsl:variable name="moduleID" select="moduleID"/>
					
					<xsl:text> </xsl:text> <xsl:value-of select="fromModule"/> <xsl:text> </xsl:text><xsl:value-of select="../../modules/module[moduleID=$moduleID]/name"/><xsl:text> (Modul ID: </xsl:text><xsl:value-of select="moduleID"/><xsl:text>, unikt ID: </xsl:text><xsl:value-of select="uniqueID"/><xsl:text>)</xsl:text>								
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="unknown"/>
				</xsl:otherwise>											
			</xsl:choose>			
		</xsl:variable>			
		
		
		menutree<xsl:value-of select="/document/module/moduleID"/>.add('menuitem<xsl:value-of select="menuIndex"/>-section<xsl:value-of select="../../sectionID"/>-source','menuitem<xsl:value-of select="menuIndex"/>-section<xsl:value-of select="../../sectionID"/>','<xsl:value-of select="source"/>: <xsl:value-of select="$source"/>','','<xsl:value-of select="phrase1"/>','','','','','');
		
		<xsl:variable name="url">
			<xsl:if test="url and urlType">
				<xsl:choose>
					<xsl:when test="urlType='RELATIVE_FROM_CONTEXTPATH'">
						<xsl:value-of select="/document/requestinfo/contextpath"/><xsl:value-of select="url"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="url"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>	
		</xsl:variable>	
		
		<xsl:if test="$url != ''">
			menutree<xsl:value-of select="/document/module/moduleID"/>.add('menuitem<xsl:value-of select="menuIndex"/>-section<xsl:value-of select="../../sectionID"/>-link','menuitem<xsl:value-of select="menuIndex"/>-section<xsl:value-of select="../../sectionID"/>','<xsl:value-of select="link"/>: <xsl:value-of select="$url"/>','<xsl:value-of select="$url"/>','<xsl:value-of select="phrase2"/>','','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/pics/link.gif','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/pics/link.gif','','');
		</xsl:if>
						
		<xsl:if test="name() = 'bundle' and count(menuitems/menuitem) > 0">
			
			menutree<xsl:value-of select="/document/module/moduleID"/>.add('menuitem<xsl:value-of select="menuIndex"/>-section<xsl:value-of select="../../sectionID"/>-bundleitems','menuitem<xsl:value-of select="menuIndex"/>-section<xsl:value-of select="../../sectionID"/>','<xsl:value-of select="menu"/>','','<xsl:value-of select="phrase3"/>','','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/img/folder.gif','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/img/folderopen.gif','','');
		
			<xsl:apply-templates select="menuitems/menuitem" mode="bundle"/>
		</xsl:if>													
	</xsl:template>	

	<xsl:template match="menuitem" mode="bundle">	
	
		<xsl:variable name="name">
			<xsl:choose>
				<xsl:when test="name">
		            <xsl:call-template name="common_js_escape">
		               <xsl:with-param name="text" select="name" />
		            </xsl:call-template>				
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="whitespace"/>
				</xsl:otherwise>
			</xsl:choose>			
		</xsl:variable>
		
		<xsl:variable name="nameWithoutQuotes">
			<xsl:choose>
				<xsl:when test="name">
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
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="whitespace"/>
				</xsl:otherwise>
			</xsl:choose>						
		</xsl:variable>
		
		<xsl:variable name="description">
            <xsl:call-template name="common_js_escape">
               <xsl:with-param name="text" select="description" />
            </xsl:call-template>			
		</xsl:variable>		
		
		<xsl:variable name="icon">
			<xsl:choose>
				<xsl:when test="itemType='BLANK'">
					<xsl:text>blank.gif</xsl:text>				
				</xsl:when>
				<xsl:when test="itemType='MENUITEM'">
					<xsl:text>menuitem.gif</xsl:text>				
				</xsl:when>
				<xsl:when test="itemType='TITLE'">
					<xsl:text>title.gif</xsl:text>				
				</xsl:when>
				<xsl:when test="itemType='SECTION'">
					<xsl:text>subsection.gif</xsl:text>				
				</xsl:when>												
			</xsl:choose>			
		</xsl:variable>
		
		menutree<xsl:value-of select="/document/module/moduleID"/>.add('bundle-menuitem<xsl:value-of select="position()"/>-section<xsl:value-of select="../../sectionID"/>-bundle<xsl:value-of select="../../menuIndex"/>','menuitem<xsl:value-of select="../../menuIndex"/>-section<xsl:value-of select="../../sectionID"/>-bundleitems','<xsl:value-of select="$name"/>','','<xsl:value-of select="$description"/>','','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/pics/<xsl:value-of select="$icon"/>','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/pics/<xsl:value-of select="$icon"/>','','');
		
		<xsl:variable name="url">
			<xsl:if test="url and urlType">
				<xsl:choose>
					<xsl:when test="urlType='RELATIVE_FROM_CONTEXTPATH'">
						<xsl:value-of select="/document/requestinfo/contextpath"/><xsl:value-of select="url"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="url"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>	
		</xsl:variable>	
		
		<xsl:if test="$url != ''">
			menutree<xsl:value-of select="/document/module/moduleID"/>.add('bundle-menuitem<xsl:value-of select="position()"/>-section<xsl:value-of select="../../sectionID"/>-bundle<xsl:value-of select="../../menuIndex"/>-link','bundle-menuitem<xsl:value-of select="position()"/>-section<xsl:value-of select="../../sectionID"/>-bundle<xsl:value-of select="../../menuIndex"/>','<xsl:value-of select="link"/>: <xsl:value-of select="$url"/>','<xsl:value-of select="$url"/>','<xsl:value-of select="phrase2"/>','','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/pics/link.gif','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/pics/link.gif','','');
		</xsl:if>													
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
	
	<xsl:template match="moveMenuItem">
		<link rel="StyleSheet" href="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/dtree/dtree.css" type="text/css"/>
		<script type="text/javascript" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/dtree/dtree.js"></script>
		
		<h1><xsl:value-of select="$moveMenu" /></h1>
		<p><xsl:value-of select="$moveMenuInstruction" /></p>
		
		<div class="dtree">
			<p><a href="javascript:sectiontree{/document/module/moduleID}.openAll();"><xsl:value-of select="$expandAll" /></a> | <a href="javascript:sectiontree{/document/module/moduleID}.closeAll();"><xsl:value-of select="$collapseAll" /></a></p>

			<script type="text/javascript">
				sectiontree<xsl:value-of select="/document/module/moduleID"/> = new dTree('sectiontree<xsl:value-of select="/document/module/moduleID"/>','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/');
				sectiontree<xsl:value-of select="/document/module/moduleID"/>.icon.root = '<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/img/globe.gif';
				<xsl:apply-templates select="section" mode="sectiontree"/>
				
				document.write(sectiontree<xsl:value-of select="/document/module/moduleID"/>);
			</script>
		</div>
	</xsl:template>
	
	<xsl:template match="section" mode="sectiontree">
	
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

		sectiontree<xsl:value-of select="/document/module/moduleID"/>.add('section<xsl:value-of select="sectionID"/>','<xsl:value-of select="$parentSectionID"/>','<xsl:value-of select="$name"/>','<xsl:value-of select="/document/requestinfo/currentURI"/>/<xsl:value-of select="/document/module/alias"/>/moveMenuItem/<xsl:value-of select="/document/moveMenuItem/menuitem/virtualMenuItem/menuItemID"/>/<xsl:value-of select="sectionID"/>','<xsl:value-of select="$description"/>','','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/img/folder.gif','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/img/folderopen.gif','','<xsl:if test="@cached='false'"><img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/warning.gif" alt="{$thisSectionIsNotStarted}!" title="{$thisSectionIsNotStarted}!"/></xsl:if>');

		<xsl:apply-templates select="subsections/section" mode="sectiontree"/>
	</xsl:template>
						
</xsl:stylesheet>