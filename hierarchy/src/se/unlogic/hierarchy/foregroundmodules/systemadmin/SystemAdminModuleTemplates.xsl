<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/Common.xsl"/>
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
			<xsl:apply-templates select="sections"/>
			<xsl:apply-templates select="moveSection"/>
			<xsl:apply-templates select="addSection"/>
			<xsl:apply-templates select="updateSection"/>

			<xsl:apply-templates select="addBackgroundModule"/>		
			<xsl:apply-templates select="updateBackgroundModule"/>
			<xsl:apply-templates select="copyBackgroundModule"/>
			<xsl:apply-templates select="moveBackgroundModule"/>
			
			<xsl:apply-templates select="addForegroundModule"/>		
			<xsl:apply-templates select="updateForegroundModule"/>
			<xsl:apply-templates select="copyForegroundModule"/>
			<xsl:apply-templates select="moveForegroundModule"/>
			
			<xsl:apply-templates select="addFilterModule"/>		
			<xsl:apply-templates select="updateFilterModule"/>
			
			<xsl:apply-templates select="ImportModules"/>			
		</div>				
	</xsl:template>

	<xsl:template match="sections">	
		
		<h1><xsl:value-of select="/document/module/name"/></h1>		
		
		<div class="dtree">
			<p><a href="javascript: systemtree{/document/module/moduleID}.openAll();"><xsl:value-of select="$i18n.expandAll" /></a> | <a href="javascript: systemtree{/document/module/moduleID}.closeAll();"><xsl:value-of select="$i18n.collapseAll" /></a></p>

			<script type="text/javascript">
				systemtree<xsl:value-of select="/document/module/moduleID"/> = new dTree('systemtree<xsl:value-of select="/document/module/moduleID"/>','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/');
				systemtree<xsl:value-of select="/document/module/moduleID"/>.icon.root = '<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/img/globe.gif';
				<xsl:apply-templates select="section" mode="systemtree"/>
				
				document.write(systemtree<xsl:value-of select="/document/module/moduleID"/>);
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
		
		systemtree<xsl:value-of select="/document/module/moduleID"/>.add('section<xsl:value-of select="sectionID"/>','<xsl:value-of select="$parentSectionID"/>','<xsl:value-of select="name"/>','','<xsl:value-of select="$description"/>','','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/img/folder.gif','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/img/folderopen.gif','',
		'<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/dtree/img/empty.gif"/>
		
		<xsl:choose>
			<xsl:when test="@cached='false'">
				<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/startSection/{sectionID}" title="{$i18n.startSection} {$name}">
					<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/stop.png"/>
				</a>				
			</xsl:when>
			<xsl:otherwise>
				<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/stopSection/{sectionID}" title="{$i18n.stopSection} {$name}">
					<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/check.png"/>
				</a>	
			</xsl:otherwise>
		</xsl:choose>
		
		<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/dtree/img/empty.gif"/>
		
		<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/addfmodule/{sectionID}" title="{$i18n.addModuleInSection} {$name}">
			<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/cog_add.png"/>
		</a>
		
		<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/addbmodule/{sectionID}" title="{$i18n.addModuleInSection} {$name}">
			<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/cog_b_add.png"/>
		</a>		
		
		<xsl:if test="not(parentSectionID)">
			
			<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/addfiltermodule/{sectionID}" title="{$i18n.addFilterModule}">
				<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/cog_f_add.png"/>
			</a>	
		</xsl:if>
		
		<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/import/{sectionID}" title="{$i18n.importModulesInSection} {$name}">
			<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/cog_upload.png"/>
		</a>			
		
		<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/addSection/{sectionID}" title="{$i18n.addSubSectionInSection} {$name}">
			<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/sectionadd.gif"/>
		</a>
		
		<xsl:if test="parentSectionID">
			<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/moveSection/{sectionID}" title="{$i18n.moveSection} {$name}">
				<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/sectionmove.gif"/>
			</a>
		</xsl:if>
		
		<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/updateSection/{sectionID}" title="{$i18n.editSection} {$name}">
			<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/pen.png"/>
		</a>
			
		<a href="javascript:askBeforeRedirect(\'Ta bort sektionen: {$nameWithoutQuotes}?\',\'{/document/requestinfo/currentURI}/{/document/module/alias}/deleteSection/{sectionID}\');" title="{$i18n.removeSection} {$name}">
			<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/delete.png"/>	
		</a>');

		<xsl:apply-templates select="subsections/section" mode="systemtree"/>
		
		<xsl:apply-templates select="modules/module" mode="dtree"/>
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
		
			<xsl:choose>
				<xsl:when test="inDatabase='true'">
					<xsl:text>cog</xsl:text>
					<xsl:value-of select="$icontype"/>
					<xsl:text>.png</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>cog</xsl:text>
					<xsl:value-of select="$icontype"/>
					<xsl:text>_warning.png</xsl:text>
				</xsl:otherwise>
			</xsl:choose>

		</xsl:variable>
	
		<xsl:variable name="link">
			<xsl:if test="moduleType = 'FOREGROUND'">
				<xsl:value-of select="/document/requestinfo/contextpath"/><xsl:value-of select="../../fullAlias"/>/<xsl:value-of select="alias"/>
			</xsl:if>
		</xsl:variable>
	
		systemtree<xsl:value-of select="/document/module/moduleID"/>.add('<xsl:value-of select="moduleType"/>.module<xsl:value-of select="moduleID"/>','section<xsl:value-of select="../../sectionID"/>','<xsl:value-of select="$name"/>','<xsl:value-of select="$link"/>','<xsl:value-of select="$i18n.description"/>','','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/><xsl:text>/pics/</xsl:text><xsl:value-of select="$icon"/>','','',
		
		'<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/dtree/img/empty.gif"/>
		
		<xsl:if test="../../@cached='true'">
			<xsl:choose>
				<xsl:when test="cached='false'">
				
					<xsl:choose>
						<xsl:when test="moduleType = 'BACKGROUND'">
							<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/startbmodule/{moduleID}" title="{$i18n.startModule} {$name}">
								<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/stop.png"/>
							</a>						
						</xsl:when>
						<xsl:when test="moduleType = 'FILTER'">
							<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/startfiltermodule/{moduleID}" title="{$i18n.startModule} {$name}">
								<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/stop.png"/>
							</a>						
						</xsl:when>						
						<xsl:otherwise>
							<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/startfmodule/{moduleID}" title="{$i18n.startModule} {$name}">
								<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/stop.png"/>
							</a>						
						</xsl:otherwise>
					</xsl:choose>
				
				</xsl:when>
				<xsl:otherwise>
				
					<xsl:choose>
						<xsl:when test="moduleType = 'BACKGROUND'">
							<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/stopbmodule/{moduleID}" title="{$i18n.stopModule} {$name}">
								<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/check.png"/>
							</a>								
						</xsl:when>
						<xsl:when test="moduleType = 'FILTER'">
							<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/stopfiltermodule/{moduleID}" title="{$i18n.stopModule} {$name}">
								<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/check.png"/>
							</a>								
						</xsl:when>						
						<xsl:otherwise>
						
							<xsl:choose>
								<xsl:when test="moduleID">
									<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/stopfmodule/{moduleID}" title="{$i18n.stopModule} {$name}">
										<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/check.png"/>
									</a>								
								</xsl:when>
								<xsl:otherwise>
									<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/stopvfmodule/{sectionID}/{alias}" title="{$i18n.stopModule} {$name}">
										<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/check.png"/>
									</a>								
								</xsl:otherwise>	
							</xsl:choose>
							
						</xsl:otherwise>
					</xsl:choose>
					
				</xsl:otherwise>
			</xsl:choose>		
		</xsl:if>
		
		<xsl:if test="inDatabase='true'">
		
			<xsl:variable name="linkPrefix">
	
				<xsl:choose>
					<xsl:when test="moduleType = 'BACKGROUND'">
						<xsl:text>b</xsl:text>
					</xsl:when>
					<xsl:when test="moduleType = 'FILTER'">
						<xsl:text>filter</xsl:text>
					</xsl:when>					
					<xsl:otherwise>
						<xsl:text>f</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
	
			</xsl:variable>			
		
			<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/dtree/img/empty.gif"/>
			
			<xsl:if test="moduleType != 'FILTER'">
				<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/move{$linkPrefix}module/{moduleID}" title="{$i18n.moveModule} {$name}">
					<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/cog{$icontype}_move.png"/>
				</a>			
			</xsl:if>
			
			<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/copy{$linkPrefix}module/{moduleID}" title="{$i18n.copyModule} {$name}">
				<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/cog{$icontype}_copy.png"/>
			</a>
			
			<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/update{$linkPrefix}module/{moduleID}" title="{$i18n.editModule} {$name}">
				<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/cog{$icontype}_edit.png"/>
			</a>
			
			<a href="javascript:askBeforeRedirect(\'Ta bort modulen: {$nameWithoutQuotes}?\',\'{/document/requestinfo/currentURI}/{/document/module/alias}/delete{$linkPrefix}module/{moduleID}\');" title="{$i18n.removeModule} {$name}">
				<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/cog{$icontype}_delete.png"/>
			</a>
			
			<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/download{$linkPrefix}module/{moduleID}" title="{$i18n.downloadModuleDescriptor} {$name}">
				<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/download.png"/>
			</a>					
		</xsl:if>
		
		<xsl:text>');</xsl:text>
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
	
			
	
	
	
	<xsl:template match="section" mode="movemodule">
	
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
		
		<xsl:variable name="moduleID">
			<xsl:choose>
				<xsl:when test="/document/moveForegroundModule/module/moduleID">
					<xsl:value-of select="/document/moveForegroundModule/module/moduleID"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="/document/moveBackgroundModule/module/moduleID"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>			

		<xsl:variable name="linkAlias">
			<xsl:choose>
				<xsl:when test="/document/moveForegroundModule">
					<xsl:text>movefmodule</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>movebmodule</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		sectiontree<xsl:value-of select="/document/module/moduleID"/>.add('section<xsl:value-of select="sectionID"/>','<xsl:value-of select="$parentSectionID"/>','<xsl:value-of select="$name"/>','<xsl:value-of select="/document/requestinfo/currentURI"/>/<xsl:value-of select="/document/module/alias"/>/<xsl:value-of select="$linkAlias"/>/<xsl:value-of select="$moduleID"/>/<xsl:value-of select="sectionID"/>','<xsl:value-of select="$i18n.description"/>','','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/img/folder.gif','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/img/folderopen.gif');

		<xsl:apply-templates select="subsections/section" mode="movemodule"/>
	</xsl:template>
	
	<xsl:template match="section" mode="copymodule">
	
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

		<xsl:variable name="moduleID">
			<xsl:choose>
				<xsl:when test="/document/copyForegroundModule/module/moduleID">
					<xsl:value-of select="/document/copyForegroundModule/module/moduleID"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="/document/copyBackgroundModule/module/moduleID"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>	

		<xsl:variable name="linkAlias">
			<xsl:choose>
				<xsl:when test="/document/copyForegroundModule">
					<xsl:text>copyfmodule</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>copybmodule</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		sectiontree<xsl:value-of select="/document/module/moduleID"/>.add('section<xsl:value-of select="sectionID"/>','<xsl:value-of select="$parentSectionID"/>','<xsl:value-of select="$name"/>','<xsl:value-of select="/document/requestinfo/currentURI"/>/<xsl:value-of select="/document/module/alias"/>/<xsl:value-of select="$linkAlias"/>/<xsl:value-of select="$moduleID"/>/<xsl:value-of select="sectionID"/>','<xsl:value-of select="$i18n.description"/>','','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/img/folder.gif','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/img/folderopen.gif');

		<xsl:apply-templates select="subsections/section" mode="copymodule"/>
	</xsl:template>	
	
	<xsl:template match="moveSection">
				
		<h1><xsl:value-of select="$i18n.moveSection" /><xsl:text> </xsl:text><xsl:value-of select="section/name"/></h1>
		<p><xsl:value-of select="$i18n.moveSectionInstruction" /><xsl:text> </xsl:text><xsl:value-of select="section/name"/></p>
		
		<div class="dtree">
			<p><a href="javascript:sectiontree{/document/module/moduleID}.openAll();"><xsl:value-of select="$i18n.expandAll" /></a> | <a href="javascript:sectiontree{/document/module/moduleID}.closeAll();"><xsl:value-of select="$i18n.collapseAll" /></a></p>

			<script type="text/javascript">
				sectiontree<xsl:value-of select="/document/module/moduleID"/> = new dTree('sectiontree<xsl:value-of select="/document/module/moduleID"/>','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/');
				sectiontree<xsl:value-of select="/document/module/moduleID"/>.icon.root = '<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/img/globe.gif';
				<xsl:apply-templates select="sections/section" mode="movesection"/>
				
				document.write(sectiontree<xsl:value-of select="/document/module/moduleID"/>);
			</script>
		</div>
	</xsl:template>
	
	<xsl:template match="section" mode="movesection">
		<xsl:if test="/document/moveSection/section/sectionID != sectionID">
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
	
			sectiontree<xsl:value-of select="/document/module/moduleID"/>.add('section<xsl:value-of select="sectionID"/>','<xsl:value-of select="$parentSectionID"/>','<xsl:value-of select="$name"/>','<xsl:value-of select="/document/requestinfo/currentURI"/>/<xsl:value-of select="/document/module/alias"/>/moveSection/<xsl:value-of select="/document/moveSection/section/sectionID"/>/<xsl:value-of select="sectionID"/>','<xsl:value-of select="$i18n.description"/>','','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/img/folder.gif','<xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/dtree/img/folderopen.gif');
	
			<xsl:apply-templates select="subsections/section" mode="movesection"/>		
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="addSection">
		<h1><xsl:value-of select="$i18n.addSubSectionInSection"/><xsl:text> </xsl:text><xsl:value-of select="section/name"/></h1>
		
		<xsl:apply-templates select="validationException/validationError"/>
		<form method="POST" action="{/document/requestinfo/uri}">
			<table>
				<tr>
					<td><xsl:value-of select="$i18n.name"/></td>
					<td><input type="text" name="name" size="40" value="{requestparameters/parameter[name='name']/value}"/></td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.description"/></td>
					<td><input type="text" name="description" size="40" value="{requestparameters/parameter[name='description']/value}"/></td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.alias"/></td>
					<td><input type="text" name="alias" size="40" value="{requestparameters/parameter[name='alias']/value}"/></td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.requiredProtocol" />:</td>
					<td>
					
					<xsl:call-template name="createDropdown">
						<xsl:with-param name="name" select="'requiredProtocol'" />
						<xsl:with-param name="element" select="protocols/protocol" />
						<xsl:with-param name="valueElementName" select="'value'"/>
						<xsl:with-param name="labelElementName" select="'name'" />
					</xsl:call-template>
				
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<input type="checkbox" name="enabled" value="true">
							<xsl:if test="requestparameters/parameter[name='enabled']">
								<xsl:attribute name="checked"/>
							</xsl:if>
						</input>
						<xsl:value-of select="$i18n.autoStartUponSystemStartAndCacheReload"/>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<input type="checkbox" name="visibleInMenu" value="true">
							<xsl:if test="requestparameters/parameter[name='visibleInMenu']">
								<xsl:attribute name="checked"/>
							</xsl:if>
						</input>
						<xsl:value-of select="$i18n.showSectionInMenu"/>
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
							
						</input><xsl:value-of select="$i18n.showBreadCrumb"/>
					</td>
				</tr>																				
			</table>
			
			<h2><xsl:value-of select="$i18n.defaultAddresses"/></h2>
			
			<table>				
				<tr>
					<td><xsl:value-of select="$i18n.loggedInUsers"/>:</td>
					<td><input type="text" name="userDefaultURI" size="40" value="{requestparameters/parameter[name='userDefaultURI']/value}"/></td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.anonymousUsers"/>:</td>
					<td><input type="text" name="anonymousDefaultURI" size="40" value="{requestparameters/parameter[name='anonymousDefaultURI']/value}"/></td>
				</tr>					
			</table>
			
			<h2><xsl:value-of select="$i18n.access"/></h2>
			
			<table>
				<tr>
					<td colspan="2">
						<input type="checkbox" name="adminAccess" value="true">
							<xsl:if test="requestparameters/parameter[name='adminAccess']">
								<xsl:attribute name="checked"/>
							</xsl:if>
						</input>
						<xsl:value-of select="$i18n.administrators"/>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<input type="checkbox" name="userAccess" value="true">
							<xsl:if test="requestparameters/parameter[name='userAccess']">
								<xsl:attribute name="checked"/>
							</xsl:if>
						</input>
						<xsl:value-of select="$i18n.allLoggedInUsers"/>
					</td>
				</tr>			
				<tr>
					<td colspan="2">
						<input type="checkbox" name="anonymousAccess" value="true">
							<xsl:if test="requestparameters/parameter[name='anonymousAccess']">
								<xsl:attribute name="checked"/>
							</xsl:if>
						</input>
						<xsl:value-of select="$i18n.anonymousUsers"/>
					</td>
				</tr>			
			</table>
			
			<xsl:call-template name="groups"/>
			
			<xsl:call-template name="users"/>
			
			<div align="right">
				<input type="submit" value="{$i18n.addSubSection}"/>
			</div>				
		</form>
	</xsl:template>
	
	<xsl:template match="validationError[messageKey='InvalidFileExtension']">
	
		<p class="error">
			<xsl:value-of select="$i18n.InvalidFileExtension.part1"/>
			<xsl:value-of select="filename"/>
			<xsl:value-of select="$i18n.InvalidFileExtension.part2"/>
		</p>
			
	</xsl:template>	
	
	<xsl:template match="validationError[messageKey='DuplicateModuleID']">
	
		<p class="error">
				
			<xsl:value-of select="$i18n.DuplicateModuleID.part1"/>
			
			<xsl:choose>
				<xsl:when test="moduleType = 'FOREGROUND'">
					<xsl:value-of select="$i18n.foregroundModule"/>
				</xsl:when>
				<xsl:when test="moduleType = 'BACKGROUND'">
					<xsl:value-of select="$i18n.backgroundModule"/>
				</xsl:when>
				<xsl:when test="moduleType = 'FILTER'">
					<xsl:value-of select="$i18n.filterModule"/>
				</xsl:when>
			</xsl:choose>
			
			<xsl:value-of select="$i18n.DuplicateModuleID.part2"/>
			
			<xsl:value-of select="TargetDescriptor/module/name"/>
			
			<xsl:value-of select="$i18n.DuplicateModuleID.part3"/>
			
			<xsl:value-of select="ConflictingDescriptor/module/name"/>
			
			<xsl:value-of select="$i18n.DuplicateModuleID.part4"/>
		</p>
			
	</xsl:template>
	
	<xsl:template match="validationError[messageKey='DuplicateModuleAlias']">
	
		<p class="error">
				
			<xsl:value-of select="$i18n.DuplicateModuleAlias.part1"/>
			
			<xsl:choose>
				<xsl:when test="moduleType = 'FOREGROUND'">
					<xsl:value-of select="$i18n.foregroundModule"/>
				</xsl:when>
				<xsl:when test="moduleType = 'BACKGROUND'">
					<xsl:value-of select="$i18n.backgroundModule"/>
				</xsl:when>
				<xsl:when test="moduleType = 'FILTER'">
					<xsl:value-of select="$i18n.filterModule"/>
				</xsl:when>
			</xsl:choose>
			
			<xsl:value-of select="$i18n.DuplicateModuleAlias.part2"/>
			
			<xsl:value-of select="TargetDescriptor/module/name"/>
			
			<xsl:value-of select="$i18n.DuplicateModuleAlias.part3"/>
			
			<xsl:value-of select="ConflictingDescriptor/module/name"/>
			
			<xsl:value-of select="$i18n.DuplicateModuleAlias.part4"/>
		</p>
			
	</xsl:template>	
	
	<xsl:template match="validationError[messageKey='RequestSizeLimitExceeded']">
	
		<p class="error">
			<xsl:value-of select="$i18n.RequestSizeLimitExceeded.part1"/>
			<xsl:value-of select="actualSize"/>
			<xsl:value-of select="$i18n.RequestSizeLimitExceeded.part2"/>
			<xsl:value-of select="maxAllowedSize"/>
			<xsl:value-of select="$i18n.RequestSizeLimitExceeded.part3"/>
		</p>
			
	</xsl:template>		
	
	<xsl:template match="validationError[messageKey='FileSizeLimitExceeded']">
	
		<p class="error">
			<xsl:value-of select="$i18n.FileSizeLimitExceeded.part1"/>
			<xsl:value-of select="filename"/>
			<xsl:value-of select="$i18n.FileSizeLimitExceeded.part2"/>
			<xsl:value-of select="size"/>
			<xsl:value-of select="$i18n.FileSizeLimitExceeded.part3"/>
			<xsl:value-of select="maxFileSize"/>
			<xsl:value-of select="$i18n.FileSizeLimitExceeded.part4"/>			
		</p>
			
	</xsl:template>	
	
	<xsl:template match="validationError[messageKey='UnableToParseFile']">
	
		<p class="error">
			<xsl:value-of select="$i18n.UnableToParseFile.part1"/>
			<xsl:value-of select="filename"/>
			<xsl:value-of select="$i18n.UnableToParseFile.part2"/>		
		</p>
			
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
				
				<xsl:choose>
					<xsl:when test="fieldName = 'name'">
						<xsl:value-of select="$i18n.name"/>
					</xsl:when>
					<xsl:when test="fieldName = 'description'">
						<xsl:value-of select="$i18n.description"/>
					</xsl:when>
					<xsl:when test="fieldName = 'anonymousDefaultURI'">
						<xsl:value-of select="$i18n.anonymousDefaultURI"/>
					</xsl:when>
					<xsl:when test="fieldName = 'userDefaultURI'">
						<xsl:value-of select="$i18n.userDefaultURI"/>
					</xsl:when>
					<xsl:when test="fieldName = 'adminDefaultURI'">
						<xsl:value-of select="$i18n.adminDefaultURI"/>
					</xsl:when>
					<xsl:when test="fieldName = 'classname'">
						<xsl:value-of select="$i18n.classname"/>
					</xsl:when>
					<xsl:when test="fieldName = 'xslPathType'">
						<xsl:value-of select="$i18n.xslPathType"/>
					</xsl:when>
					<xsl:when test="fieldName = 'xslPath'">
						<xsl:value-of select="$i18n.xslPath"/>
					</xsl:when>					
					<xsl:when test="fieldName = 'dataSourceID'">
						<xsl:value-of select="$i18n.validationError.dataSourceID"/>
					</xsl:when>
					<xsl:when test="fieldName = 'alias'">
						<xsl:value-of select="$i18n.alias"/>
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
					<xsl:when test="messageKey='duplicateModuleAlias'">
						<xsl:value-of select="$i18n.duplicateModuleAlias"/>!
					</xsl:when>
					<xsl:when test="messageKey='duplicateSectionAlias'">
						<xsl:value-of select="$i18n.duplicateSectionAlias"/>!
					</xsl:when>
					<xsl:when test="messageKey='duplicateSectionAlias'">
						<xsl:value-of select="$i18n.duplicateSectionAlias"/>!
					</xsl:when>
					<xsl:when test="messageKey='FilterModuleImportInSubsection'">
						<xsl:value-of select="$i18n.FilterModuleImportInSubsection"/>!
					</xsl:when>
					<xsl:when test="messageKey='NoDescriptorsfound'">
						<xsl:value-of select="$i18n.NoDescriptorsfound"/>!
					</xsl:when>
					<xsl:when test="messageKey='UnableToParseRequest'">
						<xsl:value-of select="$i18n.UnableToParseRequest"/>!
					</xsl:when>														
					<xsl:otherwise>
						<xsl:value-of select="$i18n.unknownFault"/>
					</xsl:otherwise>
				</xsl:choose>
			</p>
		</xsl:if>
		
	</xsl:template>
	
	<xsl:template match="updateSection">
		<h1><xsl:value-of select="$i18n.editSubSection"/><xsl:text> </xsl:text><xsl:value-of select="section/name"/></h1>
		
		<xsl:apply-templates select="validationException/validationError"/>
		<form method="POST" action="{/document/requestinfo/uri}">
			<table>
				<tr>
					<td><xsl:value-of select="$i18n.name"/>:</td>
					<td>
						<input type="text" name="name" size="40">
							<xsl:attribute name="value">
								<xsl:choose>
									<xsl:when test="requestparameters/parameter[name='name']/value">
										<xsl:value-of select="requestparameters/parameter[name='name']/value"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="section/name"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</input>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.description"/>:</td>
					<td>
						<input type="text" name="description" size="40">
							<xsl:attribute name="value">
								<xsl:choose>
									<xsl:when test="requestparameters/parameter[name='description']/value">
										<xsl:value-of select="requestparameters/parameter[name='description']/value"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="section/description"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</input>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.alias"/>:</td>
					<td>
						<input type="text" name="alias" size="40">
							<xsl:attribute name="value">
								<xsl:choose>
									<xsl:when test="requestparameters/parameter[name='alias']/value">
										<xsl:value-of select="requestparameters/parameter[name='alias']/value"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="section/alias"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</input>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.requiredProtocol" />:</td>
					<td>
					
					<xsl:call-template name="createDropdown">
						<xsl:with-param name="name" select="'requiredProtocol'" />
						<xsl:with-param name="element" select="protocols/protocol" />
						<xsl:with-param name="valueElementName" select="'value'"/>
						<xsl:with-param name="labelElementName" select="'name'" />
						<xsl:with-param name="selectedValue">
							<xsl:value-of select="section/requiredProtocol"/>
						</xsl:with-param>
					</xsl:call-template>
					
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
								<xsl:when test="section/enabled='true'">
									<xsl:attribute name="checked"/>
								</xsl:when>
							</xsl:choose>
						</input>
						<xsl:value-of select="$i18n.autoStartUponSystemStartAndCacheReload" />
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<input type="checkbox" name="visibleInMenu" value="true">
							<xsl:choose>
								<xsl:when test="requestparameters">
									<xsl:if test="requestparameters/parameter[name='visibleInMenu']">
										<xsl:attribute name="checked"/>
									</xsl:if>
								</xsl:when>
								<xsl:when test="section/visibleInMenu='true'">
									<xsl:attribute name="checked"/>
								</xsl:when>
							</xsl:choose>
						</input>
						<xsl:value-of select="$i18n.showSectionInMenu"/>
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
								<xsl:when test="section/breadCrumb='true'">
									<xsl:attribute name="checked"/>
								</xsl:when>
							</xsl:choose>
						</input><xsl:value-of select="$i18n.showBreadCrumb"/>
					</td>
				</tr>															
			</table>
			
			<h2><xsl:value-of select="$i18n.defaultAddresses"/></h2>
			
			<table>					
				<tr>
					<td><xsl:value-of select="$i18n.loggedInUsers"/>:</td>
					<td>
						<input type="text" name="userDefaultURI" size="40">
							<xsl:attribute name="value">
								<xsl:choose>
									<xsl:when test="requestparameters/parameter[name='userDefaultURI']/value">
										<xsl:value-of select="requestparameters/parameter[name='userDefaultURI']/value"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="section/userDefaultURI"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</input>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.anonymousUsers"/>:</td>
					<td>
						<input type="text" name="anonymousDefaultURI" size="40">
							<xsl:attribute name="value">
								<xsl:choose>
									<xsl:when test="requestparameters/parameter[name='anonymousDefaultURI']/value">
										<xsl:value-of select="requestparameters/parameter[name='anonymousDefaultURI']/value"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="section/anonymousDefaultURI"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</input>
					</td>
				</tr>				
			</table>
			
			<h2><xsl:value-of select="$i18n.access"/></h2>
			
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
								<xsl:when test="section/adminAccess='true'">
									<xsl:attribute name="checked"/>
								</xsl:when>
							</xsl:choose>
						</input>
						<xsl:value-of select="$i18n.administrators"/>
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
								<xsl:when test="section/userAccess='true'">
									<xsl:attribute name="checked"/>
								</xsl:when>
							</xsl:choose>
						</input>
						<xsl:value-of select="$i18n.loggedInUsers"/>
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
								<xsl:when test="section/anonymousAccess='true'">
									<xsl:attribute name="checked"/>
								</xsl:when>
							</xsl:choose>						
						</input>
						<xsl:value-of select="$i18n.anonymousUsers"/>
					</td>
				</tr>			
			</table>
			
			<xsl:call-template name="groups"/>
			
			<xsl:call-template name="users"/>
			
			<div align="right">
				<input type="submit" value="{$i18n.saveChanges}"/>
			</div>				
		</form>
	</xsl:template>
	
	<xsl:template match="addFilterModule">
		<h1><xsl:value-of select="$i18n.addFilterModule"/></h1>
		
		<xsl:apply-templates select="validationException/validationError"/>
		<form method="POST" action="{/document/requestinfo/uri}">
			<table>
				<tr>
					<td><xsl:value-of select="$i18n.name"/>:</td>
					<td><input type="text" name="name" size="40" value="{requestparameters/parameter[name='name']/value}"/></td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.classnameWholePath"/>:</td>
					<td><input type="text" name="classname" size="40" value="{requestparameters/parameter[name='classname']/value}"/></td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.priority"/>:</td>
					<td><input type="text" name="priority" size="40" value="{requestparameters/parameter[name='priority']/value}"/></td>
				</tr>								
				<tr>
					<td><xsl:value-of select="$i18n.alias" />:</td>
					<td>
						<xsl:call-template name="createTextArea">
							<xsl:with-param name="name" select="'alias'"/>
							<xsl:with-param name="class" select="'medium full'"/>
						</xsl:call-template>												
					</td>
				</tr>										
				<tr>
					<td><xsl:value-of select="$i18n.datasource"/>:</td>
					<td>
						<select name="dataSourceID">
							<xsl:apply-templates select="dataSources">
								<xsl:with-param name="dataSourceID">
									<xsl:value-of select="requestparameters/parameter[name='dataSourceID']/value"/>
								</xsl:with-param>										
							</xsl:apply-templates>
						</select>
					</td>
				</tr>				
				<tr>
					<td colspan="2">
						<input type="checkbox" name="enabled" value="true">
							<xsl:if test="requestparameters/parameter[name='enabled']">
								<xsl:attribute name="checked"/>
							</xsl:if>
						</input>
						<xsl:value-of select="$i18n.autoStartUponSystemStartAndCacheReload"/>
					</td>
				</tr>
			</table>
			
			<br/>
			
			<h2><xsl:value-of select="$i18n.access"/></h2>			
			
			<table>
				<tr>
					<td colspan="2">
						<input type="checkbox" name="adminAccess" value="true">
							<xsl:if test="requestparameters/parameter[name='adminAccess']">
								<xsl:attribute name="checked"/>
							</xsl:if>
						</input>
						<xsl:value-of select="$i18n.administrators"/>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<input type="checkbox" name="userAccess" value="true">
							<xsl:if test="requestparameters/parameter[name='userAccess']">
								<xsl:attribute name="checked"/>
							</xsl:if>
						</input>
						<xsl:value-of select="$i18n.loggedInUsers"/>
					</td>
				</tr>			
				<tr>
					<td colspan="2">
						<input type="checkbox" name="anonymousAccess" value="true">
							<xsl:if test="requestparameters/parameter[name='anonymousAccess']">
								<xsl:attribute name="checked"/>
							</xsl:if>
						</input>
						<xsl:value-of select="$i18n.anonymousUsers"/>
					</td>
				</tr>																							
			</table>
			
			<xsl:call-template name="groups"/>
			
			<xsl:call-template name="users"/>
			
			<div align="right">
				<input type="submit" value="{$i18n.addModule}"/>
			</div>
		</form>
	</xsl:template>
	
	<xsl:template match="updateFilterModule">
		<h1><xsl:value-of select="$i18n.editModule" /><xsl:text> </xsl:text><xsl:value-of select="module/name" /></h1>
		
		<xsl:apply-templates select="validationException/validationError" />
		<form method="POST" action="{/document/requestinfo/uri}" name="form">
			<table>
				<tr>
					<td><xsl:value-of select="$i18n.name" />:</td>
					<td>
						<input type="text" name="name" size="40">
							<xsl:attribute name="value">
								<xsl:choose>
									<xsl:when test="requestparameters/parameter[name='name']/value">
										<xsl:value-of select="requestparameters/parameter[name='name']/value" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="module/name" />
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</input>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.classnameWholePath" />:</td>
					<td>
						<input type="text" name="classname" size="40">
							<xsl:attribute name="value">
								<xsl:choose>
									<xsl:when test="requestparameters/parameter[name='classname']/value">
										<xsl:value-of select="requestparameters/parameter[name='classname']/value" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="module/classname" />
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</input>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.priority" />:</td>
					<td>
						<input type="text" name="priority" size="40">
							<xsl:attribute name="value">
								<xsl:choose>
									<xsl:when test="requestparameters/parameter[name='priority']/value">
										<xsl:value-of select="requestparameters/parameter[name='priority']/value" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="module/priority" />
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</input>
					</td>
				</tr>								
				<tr>
					<td><xsl:value-of select="$i18n.alias" />:</td>
					<td>
						<xsl:call-template name="createTextArea">
							<xsl:with-param name="name" select="'alias'"/>
							<xsl:with-param name="class" select="'medium full'"/>
							<xsl:with-param name="element" select="module/aliases/alias"/>
							<xsl:with-param name="separateListValues" select="'true'"/>
						</xsl:call-template>												
					</td>
				</tr>				
				<tr>
					<td><xsl:value-of select="$i18n.staticPackage" />:</td>
					<td>
						<input type="text" name="staticContentPackage" size="40">
							<xsl:attribute name="value">
								<xsl:choose>
									<xsl:when test="requestparameters/parameter[name='staticContentPackage']/value">
										<xsl:value-of select="requestparameters/parameter[name='staticContentPackage']/value" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="module/staticContentPackage" />
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</input>
					</td>
				</tr>				
				<tr>
					<td><xsl:value-of select="$i18n.datasource" />:</td>
					<td>
						<select name="dataSourceID">
							<xsl:apply-templates select="dataSources">
								<xsl:with-param name="dataSourceID">
									<xsl:choose>
										<xsl:when test="requestparameters/parameter[name='dataSourceID']">
											<xsl:value-of select="requestparameters/parameter[name='dataSourceID']/value" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="module/dataSourceID" />
										</xsl:otherwise>
									</xsl:choose>								
								</xsl:with-param>										
							</xsl:apply-templates>
						</select>
					</td>
				</tr>				
				<tr>
					<td colspan="2">
						<input type="checkbox" name="enabled" value="true">
							<xsl:choose>
								<xsl:when test="requestparameters">
									<xsl:if test="requestparameters/parameter[name='enabled']">
										<xsl:attribute name="checked" />
									</xsl:if>
								</xsl:when>
								<xsl:when test="module/enabled='true'">
									<xsl:attribute name="checked" />
								</xsl:when>
							</xsl:choose>
						</input>
						<xsl:value-of select="$i18n.autoStartUponSystemStartAndCacheReload" />
					</td>
				</tr>																		
			</table>
			
			<h2><xsl:value-of select="$i18n.updateModule.moduleSettings" /></h2>
			
			<xsl:choose>
				<xsl:when test="@started='true' and moduleSettingDescriptors">
					
					<table>
						<xsl:apply-templates select="moduleSettingDescriptors/settingDescriptor" />
						<xsl:call-template name="initFCKEditor" /> 
					</table>
					
				</xsl:when>
				<xsl:when test="@started='true'">
					<p><xsl:value-of select="$i18n.updateModule.moduleHasNoModuleSettings" /></p>
				</xsl:when>
				<xsl:otherwise>
					<p><xsl:value-of select="$i18n.updateModule.moduleNotStarted" /></p>
				</xsl:otherwise>
			</xsl:choose>
						
			<h2><xsl:value-of select="$i18n.access" /></h2>
			
			<table>
				<tr>
					<td colspan="2">
						<input type="checkbox" name="adminAccess" value="true">
							<xsl:choose>
								<xsl:when test="requestparameters">
									<xsl:if test="requestparameters/parameter[name='adminAccess']">
										<xsl:attribute name="checked" />
									</xsl:if>
								</xsl:when>
								<xsl:when test="module/adminAccess='true'">
									<xsl:attribute name="checked" />
								</xsl:when>
							</xsl:choose>
						</input>
						<xsl:value-of select="$i18n.administrators" />
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<input type="checkbox" name="userAccess" value="true">
							<xsl:choose>
								<xsl:when test="requestparameters">
									<xsl:if test="requestparameters/parameter[name='userAccess']">
										<xsl:attribute name="checked" />
									</xsl:if>
								</xsl:when>
								<xsl:when test="module/userAccess='true'">
									<xsl:attribute name="checked" />
								</xsl:when>
							</xsl:choose>
						</input>
						<xsl:value-of select="$i18n.loggedInUsers" />
					</td>
				</tr>			
				<tr>
					<td colspan="2">
						<input type="checkbox" name="anonymousAccess" value="true">
							<xsl:choose>
								<xsl:when test="requestparameters">
									<xsl:if test="requestparameters/parameter[name='anonymousAccess']">
										<xsl:attribute name="checked" />
									</xsl:if>
								</xsl:when>
								<xsl:when test="module/anonymousAccess='true'">
									<xsl:attribute name="checked" />
								</xsl:when>
							</xsl:choose>						
						</input>
						<xsl:value-of select="$i18n.anonymousUsers" />
					</td>
				</tr>
			</table>
			
			<xsl:call-template name="groups"/>
			
			<xsl:call-template name="users"/>
			
			<div align="right">
				<input type="submit" value="{$i18n.saveChanges}" />
			</div>			
		</form>
	</xsl:template>	
	
	<xsl:template match="addBackgroundModule">
		<h1><xsl:value-of select="$i18n.addModuleInSection"/><xsl:text> </xsl:text><xsl:value-of select="section/name"/></h1>
		
		<xsl:apply-templates select="validationException/validationError"/>
		<form method="POST" action="{/document/requestinfo/uri}">
			<table>
				<tr>
					<td><xsl:value-of select="$i18n.name"/>:</td>
					<td><input type="text" name="name" size="40" value="{requestparameters/parameter[name='name']/value}"/></td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.classnameWholePath"/>:</td>
					<td><input type="text" name="classname" size="40" value="{requestparameters/parameter[name='classname']/value}"/></td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.priority"/>:</td>
					<td><input type="text" name="priority" size="40" value="{requestparameters/parameter[name='priority']/value}"/></td>
				</tr>								
				<tr>
					<td><xsl:value-of select="$i18n.alias" />:</td>
					<td>
						<xsl:call-template name="createTextArea">
							<xsl:with-param name="name" select="'alias'"/>
							<xsl:with-param name="class" select="'medium full'"/>
						</xsl:call-template>												
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.slots" />:</td>						
					<td>
						<xsl:call-template name="createTextArea">
							<xsl:with-param name="name" select="'slots'"/>
							<xsl:with-param name="class" select="'medium full'"/>
						</xsl:call-template>												
					</td>						
				</tr>				
				<tr>
					<td><xsl:value-of select="$i18n.staticPackage"/>:</td>
					<td><input type="text" name="staticContentPackage" size="40" value="{requestparameters/parameter[name='staticContentPackage']/value}"/></td>
				</tr>				
				<tr>
					<td><xsl:value-of select="$i18n.datasource"/>:</td>
					<td>
						<select name="dataSourceID">
							<xsl:apply-templates select="dataSources">
								<xsl:with-param name="dataSourceID">
									<xsl:value-of select="requestparameters/parameter[name='dataSourceID']/value"/>
								</xsl:with-param>										
							</xsl:apply-templates>
						</select>
					</td>
				</tr>				
				<tr>
					<td colspan="2">
						<input type="checkbox" name="enabled" value="true">
							<xsl:if test="requestparameters/parameter[name='enabled']">
								<xsl:attribute name="checked"/>
							</xsl:if>
						</input>
						<xsl:value-of select="$i18n.autoStartUponSystemStartAndCacheReload"/>
					</td>
				</tr>
			</table>
			
			<br/>
			
			<h2><xsl:value-of select="$i18n.xslt"/></h2>			
			
			<table>				
				<tr>
					<td><xsl:value-of select="$i18n.pathType"/>:</td>
					<td>
						<select name="xslPathType">
							<xsl:apply-templates select="pathTypes">
								<xsl:with-param name="selectedType"><xsl:value-of select="requestparameters/parameter[name='xslPathType']/value"/></xsl:with-param>
							</xsl:apply-templates>
						</select>					
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.path"/>:</td>
					<td><input type="text" name="xslPath" size="40" value="{requestparameters/parameter[name='xslPath']/value}"/></td>
				</tr>																						
			</table>
			
			<br/>
			
			<h2><xsl:value-of select="$i18n.access"/></h2>			
			
			<table>
				<tr>
					<td colspan="2">
						<input type="checkbox" name="adminAccess" value="true">
							<xsl:if test="requestparameters/parameter[name='adminAccess']">
								<xsl:attribute name="checked"/>
							</xsl:if>
						</input>
						<xsl:value-of select="$i18n.administrators"/>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<input type="checkbox" name="userAccess" value="true">
							<xsl:if test="requestparameters/parameter[name='userAccess']">
								<xsl:attribute name="checked"/>
							</xsl:if>
						</input>
						<xsl:value-of select="$i18n.loggedInUsers"/>
					</td>
				</tr>			
				<tr>
					<td colspan="2">
						<input type="checkbox" name="anonymousAccess" value="true">
							<xsl:if test="requestparameters/parameter[name='anonymousAccess']">
								<xsl:attribute name="checked"/>
							</xsl:if>
						</input>
						<xsl:value-of select="$i18n.anonymousUsers"/>
					</td>
				</tr>																							
			</table>
			
			<xsl:call-template name="groups"/>
			
			<xsl:call-template name="users"/>		
			
			<div align="right">
				<input type="submit" value="{$i18n.addModule}"/>
			</div>
		</form>
	</xsl:template>
	
	<xsl:template match="updateBackgroundModule">
		<h1><xsl:value-of select="$i18n.editModule" /><xsl:text> </xsl:text><xsl:value-of select="module/name" /></h1>
		
		<xsl:apply-templates select="validationException/validationError" />
		<form method="POST" action="{/document/requestinfo/uri}" name="form">
			<table>
				<tr>
					<td><xsl:value-of select="$i18n.name" />:</td>
					<td>
						<input type="text" name="name" size="40">
							<xsl:attribute name="value">
								<xsl:choose>
									<xsl:when test="requestparameters/parameter[name='name']/value">
										<xsl:value-of select="requestparameters/parameter[name='name']/value" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="module/name" />
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</input>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.classnameWholePath" />:</td>
					<td>
						<input type="text" name="classname" size="40">
							<xsl:attribute name="value">
								<xsl:choose>
									<xsl:when test="requestparameters/parameter[name='classname']/value">
										<xsl:value-of select="requestparameters/parameter[name='classname']/value" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="module/classname" />
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</input>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.priority" />:</td>
					<td>
						<input type="text" name="priority" size="40">
							<xsl:attribute name="value">
								<xsl:choose>
									<xsl:when test="requestparameters/parameter[name='priority']/value">
										<xsl:value-of select="requestparameters/parameter[name='priority']/value" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="module/priority" />
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</input>
					</td>
				</tr>									
				<tr>
					<td><xsl:value-of select="$i18n.alias" />:</td>
					<td>
						<xsl:call-template name="createTextArea">
							<xsl:with-param name="name" select="'alias'"/>
							<xsl:with-param name="class" select="'medium full'"/>
							<xsl:with-param name="element" select="module/aliases/alias"/>
							<xsl:with-param name="separateListValues" select="'true'"/>
						</xsl:call-template>												
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.slots" />:</td>						
					<td>
						<xsl:call-template name="createTextArea">
							<xsl:with-param name="name" select="'slots'"/>
							<xsl:with-param name="class" select="'medium full'"/>
							<xsl:with-param name="element" select="module/slots/slot"/>
							<xsl:with-param name="separateListValues" select="'true'"/>
						</xsl:call-template>												
					</td>						
				</tr>				
				<tr>
					<td><xsl:value-of select="$i18n.staticPackage" />:</td>
					<td>
						<input type="text" name="staticContentPackage" size="40">
							<xsl:attribute name="value">
								<xsl:choose>
									<xsl:when test="requestparameters/parameter[name='staticContentPackage']/value">
										<xsl:value-of select="requestparameters/parameter[name='staticContentPackage']/value" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="module/staticContentPackage" />
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</input>
					</td>
				</tr>				
				<tr>
					<td><xsl:value-of select="$i18n.datasource" />:</td>
					<td>
						<select name="dataSourceID">
							<xsl:apply-templates select="dataSources">
								<xsl:with-param name="dataSourceID">
									<xsl:choose>
										<xsl:when test="requestparameters/parameter[name='dataSourceID']">
											<xsl:value-of select="requestparameters/parameter[name='dataSourceID']/value" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="module/dataSourceID" />
										</xsl:otherwise>
									</xsl:choose>								
								</xsl:with-param>										
							</xsl:apply-templates>
						</select>
					</td>
				</tr>				
				<tr>
					<td colspan="2">
						<input type="checkbox" name="enabled" value="true">
							<xsl:choose>
								<xsl:when test="requestparameters">
									<xsl:if test="requestparameters/parameter[name='enabled']">
										<xsl:attribute name="checked" />
									</xsl:if>
								</xsl:when>
								<xsl:when test="module/enabled='true'">
									<xsl:attribute name="checked" />
								</xsl:when>
							</xsl:choose>
						</input>
						<xsl:value-of select="$i18n.autoStartUponSystemStartAndCacheReload" />
					</td>
				</tr>																		
			</table>
			
			<h2><xsl:value-of select="$i18n.updateModule.moduleSettings" /></h2>
			
			<xsl:choose>
				<xsl:when test="@started='true' and moduleSettingDescriptors">
					
					<table>
						<xsl:apply-templates select="moduleSettingDescriptors/settingDescriptor" />
						<xsl:call-template name="initFCKEditor" /> 
					</table>
					
				</xsl:when>
				<xsl:when test="@started='true'">
					<p><xsl:value-of select="$i18n.updateModule.moduleHasNoModuleSettings" /></p>
				</xsl:when>
				<xsl:otherwise>
					<p><xsl:value-of select="$i18n.updateModule.moduleNotStarted" /></p>
				</xsl:otherwise>
			</xsl:choose>
			
			<h2><xsl:value-of select="$i18n.xslt" /></h2>
			
			<table>
				<tr>
					<td><xsl:value-of select="$i18n.pathType" />:</td>
					<td>
						<select name="xslPathType">
							<xsl:apply-templates select="pathTypes">
								<xsl:with-param name="selectedType">
									<xsl:choose>
										<xsl:when test="requestparameters/parameter[name='dataSourceID']">
											<xsl:value-of select="requestparameters/parameter[name='xslPathType']/value" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="module/xslPathType" />
										</xsl:otherwise>
									</xsl:choose>									
								</xsl:with-param>
							</xsl:apply-templates>
						</select>					
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.path" />:</td>
					<td>
						<input type="text" name="xslPath" size="40">
							<xsl:attribute name="value">
								<xsl:choose>
									<xsl:when test="requestparameters/parameter[name='xslPath']/value">
										<xsl:value-of select="requestparameters/parameter[name='xslPath']/value" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="module/xslPath" />
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</input>
					</td>
				</tr>																						
			</table>			
			
			<h2><xsl:value-of select="$i18n.access" /></h2>
			
			<table>
				<tr>
					<td colspan="2">
						<input type="checkbox" name="adminAccess" value="true">
							<xsl:choose>
								<xsl:when test="requestparameters">
									<xsl:if test="requestparameters/parameter[name='adminAccess']">
										<xsl:attribute name="checked" />
									</xsl:if>
								</xsl:when>
								<xsl:when test="module/adminAccess='true'">
									<xsl:attribute name="checked" />
								</xsl:when>
							</xsl:choose>
						</input>
						<xsl:value-of select="$i18n.administrators" />
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<input type="checkbox" name="userAccess" value="true">
							<xsl:choose>
								<xsl:when test="requestparameters">
									<xsl:if test="requestparameters/parameter[name='userAccess']">
										<xsl:attribute name="checked" />
									</xsl:if>
								</xsl:when>
								<xsl:when test="module/userAccess='true'">
									<xsl:attribute name="checked" />
								</xsl:when>
							</xsl:choose>
						</input>
						<xsl:value-of select="$i18n.loggedInUsers" />
					</td>
				</tr>			
				<tr>
					<td colspan="2">
						<input type="checkbox" name="anonymousAccess" value="true">
							<xsl:choose>
								<xsl:when test="requestparameters">
									<xsl:if test="requestparameters/parameter[name='anonymousAccess']">
										<xsl:attribute name="checked" />
									</xsl:if>
								</xsl:when>
								<xsl:when test="module/anonymousAccess='true'">
									<xsl:attribute name="checked" />
								</xsl:when>
							</xsl:choose>						
						</input>
						<xsl:value-of select="$i18n.anonymousUsers" />
					</td>
				</tr>
			</table>
			
			<xsl:call-template name="groups"/>
			
			<xsl:call-template name="users"/>
			
			<div align="right">
				<input type="submit" value="{$i18n.saveChanges}" />
			</div>			
		</form>
	</xsl:template>
	
	<xsl:template match="copyBackgroundModule">
				
		<h1><xsl:value-of select="$i18n.copyModule" /><xsl:text> </xsl:text><xsl:value-of select="module/name" /></h1>
		<p><xsl:value-of select="$i18n.copyModuleInstruction" /><xsl:text> </xsl:text><xsl:value-of select="module/name" /></p>
		
		<div class="dtree">
			<p><a href="javascript:sectiontree{/document/module/moduleID}.openAll();"><xsl:value-of select="$i18n.expandAll" /></a> | <a href="javascript:sectiontree{/document/module/moduleID}.closeAll();"><xsl:value-of select="$i18n.collapseAll" /></a></p>

			<script type="text/javascript">
				sectiontree<xsl:value-of select="/document/module/moduleID" /> = new dTree('sectiontree<xsl:value-of select="/document/module/moduleID" />','<xsl:value-of select="/document/requestinfo/contextpath" />/static/f/<xsl:value-of select="/document/module/sectionID" />/<xsl:value-of select="/document/module/moduleID" />/dtree/');
				sectiontree<xsl:value-of select="/document/module/moduleID" />.icon.root = '<xsl:value-of select="/document/requestinfo/contextpath" />/static/f/<xsl:value-of select="/document/module/sectionID" />/<xsl:value-of select="/document/module/moduleID" />/dtree/img/globe.gif';
				<xsl:apply-templates select="sections/section" mode="copymodule" />
				
				document.write(sectiontree<xsl:value-of select="/document/module/moduleID" />);
			</script>
		</div>
	</xsl:template>
	
	<xsl:template match="moveBackgroundModule">
				
		<h1><xsl:value-of select="$i18n.moveModule" /><xsl:text> </xsl:text><xsl:value-of select="module/name" /></h1>
		<p><xsl:value-of select="$i18n.moveModuleInstruction" /><xsl:text> </xsl:text><xsl:value-of select="module/name" /></p>
		
		<div class="dtree">
			<p><a href="javascript:sectiontree{/document/module/moduleID}.openAll();"><xsl:value-of select="$i18n.expandAll" /></a> | <a href="javascript:sectiontree{/document/module/moduleID}.closeAll();"><xsl:value-of select="$i18n.collapseAll" /></a></p>

			<script type="text/javascript">
				sectiontree<xsl:value-of select="/document/module/moduleID" /> = new dTree('sectiontree<xsl:value-of select="/document/module/moduleID" />','<xsl:value-of select="/document/requestinfo/contextpath" />/static/f/<xsl:value-of select="/document/module/sectionID" />/<xsl:value-of select="/document/module/moduleID" />/dtree/');
				sectiontree<xsl:value-of select="/document/module/moduleID" />.icon.root = '<xsl:value-of select="/document/requestinfo/contextpath" />/static/f/<xsl:value-of select="/document/module/sectionID" />/<xsl:value-of select="/document/module/moduleID" />/dtree/img/globe.gif';
				<xsl:apply-templates select="sections/section" mode="movemodule" />
				
				document.write(sectiontree<xsl:value-of select="/document/module/moduleID" />);
			</script>
		</div>
	</xsl:template>	
	
	<xsl:template match="addForegroundModule">
		<h1><xsl:value-of select="$i18n.addModuleInSection"/><xsl:text> </xsl:text><xsl:value-of select="section/name"/></h1>
		
		<xsl:apply-templates select="validationException/validationError"/>
		<form method="POST" action="{/document/requestinfo/uri}">
			<table>
				<tr>
					<td><xsl:value-of select="$i18n.name"/>:</td>
					<td><input type="text" name="name" size="40" value="{requestparameters/parameter[name='name']/value}"/></td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.description"/>:</td>
					<td><input type="text" name="description" size="40" value="{requestparameters/parameter[name='description']/value}"/></td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.classnameWholePath"/>:</td>
					<td><input type="text" name="classname" size="40" value="{requestparameters/parameter[name='classname']/value}"/></td>
				</tr>				
				<tr>
					<td><xsl:value-of select="$i18n.alias"/>:</td>
					<td><input type="text" name="alias" size="40" value="{requestparameters/parameter[name='alias']/value}"/></td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.staticPackage"/>:</td>
					<td><input type="text" name="staticContentPackage" size="40" value="{requestparameters/parameter[name='staticContentPackage']/value}"/></td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.requiredProtocol" />:</td>
					<td>
					
					<xsl:call-template name="createDropdown">
						<xsl:with-param name="name" select="'requiredProtocol'" />
						<xsl:with-param name="element" select="protocols/protocol" />
						<xsl:with-param name="valueElementName" select="'value'"/>
						<xsl:with-param name="labelElementName" select="'name'" />
					</xsl:call-template>
					
					</td>
				</tr>				
				<tr>
					<td><xsl:value-of select="$i18n.datasource"/>:</td>
					<td>
						<select name="dataSourceID">
							<xsl:apply-templates select="dataSources">
								<xsl:with-param name="dataSourceID">
									<xsl:value-of select="requestparameters/parameter[name='dataSourceID']/value"/>
								</xsl:with-param>										
							</xsl:apply-templates>
						</select>
					</td>
				</tr>					
				<tr>
					<td colspan="2">
						<input type="checkbox" name="enabled" value="true">
							<xsl:if test="requestparameters/parameter[name='enabled']">
								<xsl:attribute name="checked"/>
							</xsl:if>
						</input>
						<xsl:value-of select="$i18n.autoStartUponSystemStartAndCacheReload"/>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<input type="checkbox" name="visibleInMenu" value="true">
							<xsl:if test="requestparameters/parameter[name='visibleInMenu']">
								<xsl:attribute name="checked"/>
							</xsl:if>
						</input>
						<xsl:value-of select="$i18n.showModuleInMenu"/>
					</td>
				</tr>
			</table>
			
			<br/>
			
			<h2><xsl:value-of select="$i18n.xslt"/></h2>			
			
			<table>				
				<tr>
					<td><xsl:value-of select="$i18n.pathType"/>:</td>
					<td>
						<select name="xslPathType">
							<xsl:apply-templates select="pathTypes">
								<xsl:with-param name="selectedType"><xsl:value-of select="requestparameters/parameter[name='xslPathType']/value"/></xsl:with-param>
							</xsl:apply-templates>
						</select>					
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.path"/>:</td>
					<td><input type="text" name="xslPath" size="40" value="{requestparameters/parameter[name='xslPath']/value}"/></td>
				</tr>																						
			</table>
			
			<br/>
			
			<h2><xsl:value-of select="$i18n.access"/></h2>			
			
			<table>
				<tr>
					<td colspan="2">
						<input type="checkbox" name="adminAccess" value="true">
							<xsl:if test="requestparameters/parameter[name='adminAccess']">
								<xsl:attribute name="checked"/>
							</xsl:if>
						</input>
						<xsl:value-of select="$i18n.administrators"/>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<input type="checkbox" name="userAccess" value="true">
							<xsl:if test="requestparameters/parameter[name='userAccess']">
								<xsl:attribute name="checked"/>
							</xsl:if>
						</input>
						<xsl:value-of select="$i18n.loggedInUsers"/>
					</td>
				</tr>			
				<tr>
					<td colspan="2">
						<input type="checkbox" name="anonymousAccess" value="true">
							<xsl:if test="requestparameters/parameter[name='anonymousAccess']">
								<xsl:attribute name="checked"/>
							</xsl:if>
						</input>
						<xsl:value-of select="$i18n.anonymousUsers"/>
					</td>
				</tr>																							
			</table>
			
			<xsl:call-template name="groups"/>
			
			<xsl:call-template name="users"/>		
			
			<div align="right">
				<input type="submit" value="{$i18n.addModule}"/>
			</div>
		</form>
	</xsl:template>
	
	<xsl:template match="updateForegroundModule">
		<h1><xsl:value-of select="$i18n.editModule" /><xsl:text> </xsl:text><xsl:value-of select="module/name" /></h1>
		
		<xsl:apply-templates select="validationException/validationError" />
		<form method="POST" action="{/document/requestinfo/uri}" name="form">
			<table>
				<tr>
					<td><xsl:value-of select="$i18n.name" />:</td>
					<td>
						<input type="text" name="name" size="40">
							<xsl:attribute name="value">
								<xsl:choose>
									<xsl:when test="requestparameters/parameter[name='name']/value">
										<xsl:value-of select="requestparameters/parameter[name='name']/value" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="module/name" />
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</input>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.description" />:</td>
					<td>
						<input type="text" name="description" size="40">
							<xsl:attribute name="value">
								<xsl:choose>
									<xsl:when test="requestparameters/parameter[name='description']/value">
										<xsl:value-of select="requestparameters/parameter[name='description']/value" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="module/description" />
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</input>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.classnameWholePath" />:</td>
					<td>
						<input type="text" name="classname" size="40">
							<xsl:attribute name="value">
								<xsl:choose>
									<xsl:when test="requestparameters/parameter[name='classname']/value">
										<xsl:value-of select="requestparameters/parameter[name='classname']/value" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="module/classname" />
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</input>
					</td>
				</tr>				
				<tr>
					<td><xsl:value-of select="$i18n.alias" />:</td>
					<td>
						<input type="text" name="alias" size="40">
							<xsl:attribute name="value">
								<xsl:choose>
									<xsl:when test="requestparameters/parameter[name='alias']/value">
										<xsl:value-of select="requestparameters/parameter[name='alias']/value" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="module/alias" />
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</input>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.staticPackage" />:</td>
					<td>
						<input type="text" name="staticContentPackage" size="40">
							<xsl:attribute name="value">
								<xsl:choose>
									<xsl:when test="requestparameters/parameter[name='staticContentPackage']/value">
										<xsl:value-of select="requestparameters/parameter[name='staticContentPackage']/value" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="module/staticContentPackage" />
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</input>
					</td>
				</tr>				
				<tr>
					<td><xsl:value-of select="$i18n.datasource" />:</td>
					<td>
						<select name="dataSourceID">
							<xsl:apply-templates select="dataSources">
								<xsl:with-param name="dataSourceID">
									<xsl:choose>
										<xsl:when test="requestparameters/parameter[name='dataSourceID']">
											<xsl:value-of select="requestparameters/parameter[name='dataSourceID']/value" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="module/dataSourceID" />
										</xsl:otherwise>
									</xsl:choose>								
								</xsl:with-param>										
							</xsl:apply-templates>
						</select>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.requiredProtocol" />:</td>
					<td>
						<xsl:call-template name="createDropdown">
							<xsl:with-param name="name" select="'requiredProtocol'" />
							<xsl:with-param name="element" select="protocols/protocol" />
							<xsl:with-param name="valueElementName" select="'value'"/>
							<xsl:with-param name="labelElementName" select="'name'" />
							<xsl:with-param name="selectedValue">
							 	<xsl:value-of select="module/requiredProtocol"/>
							</xsl:with-param>
						</xsl:call-template>
					</td>
				</tr>				
				<tr>
					<td colspan="2">
						<input type="checkbox" name="enabled" value="true">
							<xsl:choose>
								<xsl:when test="requestparameters">
									<xsl:if test="requestparameters/parameter[name='enabled']">
										<xsl:attribute name="checked" />
									</xsl:if>
								</xsl:when>
								<xsl:when test="module/enabled='true'">
									<xsl:attribute name="checked" />
								</xsl:when>
							</xsl:choose>
						</input>
						<xsl:value-of select="$i18n.autoStartUponSystemStartAndCacheReload" />
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<input type="checkbox" name="visibleInMenu" value="true">
							<xsl:choose>
								<xsl:when test="requestparameters">
									<xsl:if test="requestparameters/parameter[name='visibleInMenu']">
										<xsl:attribute name="checked" />
									</xsl:if>
								</xsl:when>
								<xsl:when test="module/visibleInMenu='true'">
									<xsl:attribute name="checked" />
								</xsl:when>
							</xsl:choose>
						</input>
						<xsl:value-of select="$i18n.showModuleInMenu" />
					</td>
				</tr>																		
			</table>
			
			<h2><xsl:value-of select="$i18n.updateModule.moduleSettings" /></h2>
			
			<xsl:choose>
				<xsl:when test="@started='true' and moduleSettingDescriptors">
					
					<table>
						<xsl:apply-templates select="moduleSettingDescriptors/settingDescriptor" />
						<xsl:call-template name="initFCKEditor" /> 
					</table>
					
				</xsl:when>
				<xsl:when test="@started='true'">
					<p><xsl:value-of select="$i18n.updateModule.moduleHasNoModuleSettings" /></p>
				</xsl:when>
				<xsl:otherwise>
					<p><xsl:value-of select="$i18n.updateModule.moduleNotStarted" /></p>
				</xsl:otherwise>
			</xsl:choose>
			
			<h2><xsl:value-of select="$i18n.xslt" /></h2>
			
			<table>
				<tr>
					<td><xsl:value-of select="$i18n.pathType" />:</td>
					<td>
						<select name="xslPathType">
							<xsl:apply-templates select="pathTypes">
								<xsl:with-param name="selectedType">
									<xsl:choose>
										<xsl:when test="requestparameters/parameter[name='dataSourceID']">
											<xsl:value-of select="requestparameters/parameter[name='xslPathType']/value" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="module/xslPathType" />
										</xsl:otherwise>
									</xsl:choose>									
								</xsl:with-param>
							</xsl:apply-templates>
						</select>					
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$i18n.path" />:</td>
					<td>
						<input type="text" name="xslPath" size="40">
							<xsl:attribute name="value">
								<xsl:choose>
									<xsl:when test="requestparameters/parameter[name='xslPath']/value">
										<xsl:value-of select="requestparameters/parameter[name='xslPath']/value" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="module/xslPath" />
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</input>
					</td>
				</tr>																						
			</table>			
			
			<h2><xsl:value-of select="$i18n.access" /></h2>
			
			<table>
				<tr>
					<td colspan="2">
						<input type="checkbox" name="adminAccess" value="true">
							<xsl:choose>
								<xsl:when test="requestparameters">
									<xsl:if test="requestparameters/parameter[name='adminAccess']">
										<xsl:attribute name="checked" />
									</xsl:if>
								</xsl:when>
								<xsl:when test="module/adminAccess='true'">
									<xsl:attribute name="checked" />
								</xsl:when>
							</xsl:choose>
						</input>
						<xsl:value-of select="$i18n.administrators" />
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<input type="checkbox" name="userAccess" value="true">
							<xsl:choose>
								<xsl:when test="requestparameters">
									<xsl:if test="requestparameters/parameter[name='userAccess']">
										<xsl:attribute name="checked" />
									</xsl:if>
								</xsl:when>
								<xsl:when test="module/userAccess='true'">
									<xsl:attribute name="checked" />
								</xsl:when>
							</xsl:choose>
						</input>
						<xsl:value-of select="$i18n.loggedInUsers" />
					</td>
				</tr>			
				<tr>
					<td colspan="2">
						<input type="checkbox" name="anonymousAccess" value="true">
							<xsl:choose>
								<xsl:when test="requestparameters">
									<xsl:if test="requestparameters/parameter[name='anonymousAccess']">
										<xsl:attribute name="checked" />
									</xsl:if>
								</xsl:when>
								<xsl:when test="module/anonymousAccess='true'">
									<xsl:attribute name="checked" />
								</xsl:when>
							</xsl:choose>						
						</input>
						<xsl:value-of select="$i18n.anonymousUsers" />
					</td>
				</tr>
			</table>
			
			<xsl:call-template name="groups"/>
			
			<xsl:call-template name="users"/>
			
			<div align="right">
				<input type="submit" value="{$i18n.saveChanges}" />
			</div>			
		</form>
	</xsl:template>
	
	<xsl:template match="copyForegroundModule">
				
		<h1><xsl:value-of select="$i18n.copyModule" /><xsl:text> </xsl:text><xsl:value-of select="module/name" /></h1>
		<p><xsl:value-of select="$i18n.copyModuleInstruction" /><xsl:text> </xsl:text><xsl:value-of select="module/name" /></p>
		
		<div class="dtree">
			<p><a href="javascript:sectiontree{/document/module/moduleID}.openAll();"><xsl:value-of select="$i18n.expandAll" /></a> | <a href="javascript:sectiontree{/document/module/moduleID}.closeAll();"><xsl:value-of select="$i18n.collapseAll" /></a></p>

			<script type="text/javascript">
				sectiontree<xsl:value-of select="/document/module/moduleID" /> = new dTree('sectiontree<xsl:value-of select="/document/module/moduleID" />','<xsl:value-of select="/document/requestinfo/contextpath" />/static/f/<xsl:value-of select="/document/module/sectionID" />/<xsl:value-of select="/document/module/moduleID" />/dtree/');
				sectiontree<xsl:value-of select="/document/module/moduleID" />.icon.root = '<xsl:value-of select="/document/requestinfo/contextpath" />/static/f/<xsl:value-of select="/document/module/sectionID" />/<xsl:value-of select="/document/module/moduleID" />/dtree/img/globe.gif';
				<xsl:apply-templates select="sections/section" mode="copymodule" />
				
				document.write(sectiontree<xsl:value-of select="/document/module/moduleID" />);
			</script>
		</div>
	</xsl:template>
	
	<xsl:template match="moveForegroundModule">
				
		<h1><xsl:value-of select="$i18n.moveModule" /><xsl:text> </xsl:text><xsl:value-of select="module/name" /></h1>
		<p><xsl:value-of select="$i18n.moveModuleInstruction" /><xsl:text> </xsl:text><xsl:value-of select="module/name" /></p>
		
		<div class="dtree">
			<p><a href="javascript:sectiontree{/document/module/moduleID}.openAll();"><xsl:value-of select="$i18n.expandAll" /></a> | <a href="javascript:sectiontree{/document/module/moduleID}.closeAll();"><xsl:value-of select="$i18n.collapseAll" /></a></p>

			<script type="text/javascript">
				sectiontree<xsl:value-of select="/document/module/moduleID" /> = new dTree('sectiontree<xsl:value-of select="/document/module/moduleID" />','<xsl:value-of select="/document/requestinfo/contextpath" />/static/f/<xsl:value-of select="/document/module/sectionID" />/<xsl:value-of select="/document/module/moduleID" />/dtree/');
				sectiontree<xsl:value-of select="/document/module/moduleID" />.icon.root = '<xsl:value-of select="/document/requestinfo/contextpath" />/static/f/<xsl:value-of select="/document/module/sectionID" />/<xsl:value-of select="/document/module/moduleID" />/dtree/img/globe.gif';
				<xsl:apply-templates select="sections/section" mode="movemodule" />
				
				document.write(sectiontree<xsl:value-of select="/document/module/moduleID" />);
			</script>
		</div>
	</xsl:template>
	
	<xsl:template match="ImportModules">
		
		<h1>
			<xsl:value-of select="$i18n.importModulesInSection"/>
			<xsl:text> </xsl:text><xsl:value-of select="section/name"/>
		</h1>
		
		<xsl:apply-templates select="validationException/validationError"/>
		
		<form method="POST" action="{/document/requestinfo/uri}" enctype="multipart/form-data">

			<div class="floatleft full marginbottom">
				<div class="floatleft twenty">
					<xsl:value-of select="$i18n.descriptors" />
					<xsl:text>:</xsl:text>
				</div>
				<div class="floatleft eighty">				
					<input type="file" multiple="true" name="descriptors"/>	
				</div>
			</div>

			<xsl:if test="Started = 'true'">
			
				<div class="floatleft full marginbottom">
					<div class="floatleft twenty">
						<xsl:value-of select="$i18n.startMode" />
						<xsl:text>:</xsl:text>
					</div>
					<div class="floatleft eighty">				
						<select name="startMode">
							<option value="">
								<xsl:value-of select="$i18n.startNoModules" />
							</option>
							<option value="AUTO_START">
								<xsl:value-of select="$i18n.startEnabledModules" />
							</option>
							<option value="ALL">
								<xsl:value-of select="$i18n.startAllModules" />
							</option>														
						</select>
					</div>
				</div>			
					
			</xsl:if>
			
			<div class="floatleft full marginbottom">
				<div class="floatleft twenty">
					<xsl:value-of select="$i18n.preserveModuleIDs" />
					<xsl:text>:</xsl:text>
				</div>
				<div class="floatleft eighty">				
					<input type="checkbox" name="preserveModuleIDs"/>
				</div>
			</div>			
			
			<div class="floatleft full marginbottom">
				<div class="floatleft twenty">
					<xsl:value-of select="$i18n.preserveDataSourceIDs" />
					<xsl:text>:</xsl:text>
				</div>
				<div class="floatleft eighty">				
					<input type="checkbox" name="preserveDataSourceIDs"/>
				</div>
			</div>			
			
			<div align="right">
				<input type="submit" value="{$i18n.importModules}"/>
			</div>				
		</form>
	</xsl:template>	
	

	
	<xsl:template match="dataSources">
		<xsl:param name="dataSourceID"/>
		
		<option value="">
			<xsl:if test="$dataSourceID=''">
				<xsl:attribute name="SELECTED"/>
			</xsl:if>
			<xsl:value-of select="$i18n.dataSources.defaultDataSource"/>
		</option>		
		
		<xsl:apply-templates select="datasource">
			<xsl:with-param name="dataSourceID"><xsl:value-of select="$dataSourceID"/></xsl:with-param>
		</xsl:apply-templates>		
	</xsl:template>
	
	<xsl:template match="datasource">
		<xsl:param name="dataSourceID"/>
	
		<option value="{dataSourceID}">
			<xsl:if test="dataSourceID=$dataSourceID">
				<xsl:attribute name="SELECTED"/>
			</xsl:if>
			<xsl:value-of select="name"/>
		</option>
	</xsl:template>
	
	<xsl:template match="pathTypes">
		<xsl:param name="selectedType"/>
		
		<option value="">
			<xsl:if test="$selectedType=''">
				<xsl:attribute name="SELECTED"/>
			</xsl:if>
			<xsl:value-of select="$i18n.noStyleSheet" />
		</option>		
		
		<xsl:apply-templates select="pathType">
			<xsl:with-param name="selectedType"><xsl:value-of select="$selectedType"/></xsl:with-param>
		</xsl:apply-templates>		
	</xsl:template>
	
	<xsl:template match="pathType">
		<xsl:param name="selectedType"/>
	
		<option value="{.}">
			<xsl:if test=".=$selectedType">
				<xsl:attribute name="SELECTED"/>
			</xsl:if>
			<xsl:value-of select="."/>
		</option>
	</xsl:template>
	
	
	
	<xsl:template match="settingDescriptor">
		
		<xsl:choose>
			<xsl:when test="displayType='TEXTFIELD'">
				<tr>
					<td>
						<xsl:value-of select="name"/>
						<xsl:text> </xsl:text>
						<img class="alignbottom" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/info.png" alt="{description}" title="{description}"/>
						
						<a href="javascript:void(0);" onclick="javascript:document.getElementById('modulesetting.{id}').value='{defaultValue}';" title="{$i18n.settingDescriptor.resetDefualtValue}">
							<img class="alignbottom" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/reload.png" alt="{$i18n.settingDescriptor.resetDefualtValue}" title="{$i18n.settingDescriptor.resetDefualtValue}"/>
						</a>:
					</td>	
					<td>
						<xsl:variable name="id" select="id"/>
					
						<xsl:variable name="requestid">modulesetting.<xsl:value-of select="id"/></xsl:variable>
					
						<input type="text" name="modulesetting.{id}" id="modulesetting.{id}" size="40" class="ninety">
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
					<td><xsl:value-of select="name"/><xsl:text> </xsl:text><img class="alignbottom" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/info.png" alt="information" title="{description}"/>:</td>
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
						<img class="alignbottom" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/info.png" alt="information" title="{description}"/>
						
						<a href="javascript:void(0);" onclick="javascript:document.getElementById('modulesetting.{id}').value=document.getElementById('hiddenmodulesetting.{id}').value;" title="{$i18n.settingDescriptor.resetDefualtValue}">
							<img class="alignbottom" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/reload.png" alt="{$i18n.settingDescriptor.resetDefualtValue}" title="{$i18n.settingDescriptor.resetDefualtValue}"/>
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
											
											<xsl:choose>
												<xsl:when test="splitOnLineBreak = 'true'">
												
													<xsl:apply-templates select="../../module/settings/setting[id=$id]/value" mode="textarea-line"/>
												
												</xsl:when>
												<xsl:otherwise>
													
													<xsl:value-of select="../../module/settings/setting[id=$id]/value"/>
																									
												</xsl:otherwise>
											</xsl:choose>
											
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
						<img class="alignbottom" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/info.png" alt="information" title="{description}"/>
						
						<!--
						
						TODO!
						
						<a href="javascript:void(0);" onclick="javascript:document.getElementById('modulesetting.{id}').value=document.getElementById('hiddenmodulesetting.{id}').value;" title="{$i18n.settingDescriptor.resetDefualtValue}">
							<img class="alignbottom" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/reload.png" alt="{$i18n.settingDescriptor.resetDefualtValue}" title="{$i18n.settingDescriptor.resetDefualtValue}"/>
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
						
						<xsl:value-of select="name"/><xsl:text> </xsl:text><img class="alignbottom" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/info.png" alt="information" title="{description}"/>		
					</td>
				</tr>			
			</xsl:when>
			<xsl:when test="displayType='RADIOBUTTON'">
			
				<xsl:variable name="id" select="id"/>
				<xsl:variable name="requestid">modulesetting.<xsl:value-of select="id"/></xsl:variable>
			
				<tr>
					<td><xsl:value-of select="name"/><xsl:text> </xsl:text><img class="alignbottom" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/info.png" alt="information" title="{description}"/>:</td>
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
					<td><xsl:value-of select="name"/><xsl:text> </xsl:text><img class="alignbottom" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/info.png" alt="information" title="{description}"/>:</td>
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
					<td><xsl:value-of select="name"/><xsl:text> </xsl:text><img class="alignbottom" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/info.png" alt="information" title="{description}"/>:</td>
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

	<xsl:template name="groups" >
		<h3><xsl:value-of select="$i18n.groups"/></h3>
		
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
		<h3><xsl:value-of select="$i18n.users"/></h3>
		
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
	
	<xsl:template match="slot | alias">
	
		<xsl:value-of select="."/>
		
		<xsl:if test="not(position() = last())">
			<xsl:text>
			</xsl:text>
		</xsl:if>
	
	</xsl:template>
	
	<xsl:template name="initFCKEditor">
		
		<!-- Call global CKEditor init template -->
		<xsl:call-template name="initializeFCKEditor">
			<xsl:with-param name="basePath"><xsl:value-of select="/document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/document/module/sectionID"/>/<xsl:value-of select="/document/module/moduleID"/>/ckeditor/</xsl:with-param>
			<xsl:with-param name="customConfig">config.js</xsl:with-param>
			<xsl:with-param name="editorContainerClass">fckeditor</xsl:with-param>
			<xsl:with-param name="editorHeight">400</xsl:with-param>
			<xsl:with-param name="contentsCss">
				<xsl:if test="/document/cssPath">
					<xsl:value-of select="/document/requestinfo/contextpath"/><xsl:value-of select="/document/cssPath"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
		
	</xsl:template>	
	
	<xsl:template match="value" mode="textarea-line">
		
		<xsl:if test="position() != 1">
			<xsl:text>&#xa;</xsl:text>
		</xsl:if>
	
		<xsl:value-of select="."/>
	
	</xsl:template>
								
</xsl:stylesheet>