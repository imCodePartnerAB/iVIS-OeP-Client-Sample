<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/Common.xsl"/>

	<xsl:variable name="globalscripts">
		/jquery/jquery.js
	</xsl:variable>

	<xsl:variable name="scripts">
		/js/jquery.blockui.js
		/js/flowengine.helpdialog.js
		/js/flowengine.js
		/js/flowengine.step-navigator.js
		/js/jquery.expander.min.js
		/js/flowinstancebrowser.js
	</xsl:variable>

	<xsl:variable name="links">
		/css/flowengine.css
	</xsl:variable>

	<xsl:template match="Document">	
		
		<div id="FlowBrowser" class="contentitem">
			<xsl:apply-templates select="ShowFlowOverview"/>
			<xsl:apply-templates select="ShowFlowTypes"/>
			<xsl:apply-templates select="FlowInstanceManagerForm"/>
			<xsl:apply-templates select="FlowInstanceManagerPreview"/>
			<xsl:apply-templates select="FlowInstanceManagerSubmitted"/>
			<xsl:apply-templates select="SigningForm"/>	
			<xsl:apply-templates select="MultiSigningStatusForm"/>
			<xsl:apply-templates select="StandalonePaymentForm"/>
			<xsl:apply-templates select="InlinePaymentForm"/>
		</div>
		
	</xsl:template>

	<xsl:template match="FlowInstanceManagerForm">
	
		<xsl:if test="FlowInstance/Status/contentType != 'NEW'">
			<xsl:call-template name="showFlowInstanceControlPanel">
				<xsl:with-param name="flowInstance" select="FlowInstance" />
				<xsl:with-param name="view" select="'FLOWINSTANCE'" />
			</xsl:call-template>
		</xsl:if>	
	
		<xsl:apply-imports/>
	
	</xsl:template>
	
	<xsl:template match="FlowInstanceManagerPreview">
	
		<xsl:if test="FlowInstance/Status/contentType != 'NEW'">
			<xsl:call-template name="showFlowInstanceControlPanel">
				<xsl:with-param name="flowInstance" select="FlowInstance" />
				<xsl:with-param name="view" select="'FLOWINSTANCE'" />
			</xsl:call-template>
		</xsl:if>	
	
		<xsl:apply-imports/>
	
	</xsl:template>
				
	<xsl:template match="ShowFlowOverview">
		
		<script type="text/javascript">
			userFavouriteModuleURI = '<xsl:value-of select="/Document/requestinfo/contextpath" /><xsl:value-of select="userFavouriteModuleAlias" />';
		</script>
		
		<xsl:apply-templates select="Flow" mode="overview" />
		
	</xsl:template>
	
	<xsl:template match="Flow" mode="overview">
		
		<xsl:variable name="flowID" select="flowID" />
		<xsl:variable name="flowFamilyID" select="FlowFamily/flowFamilyID" />
		<xsl:variable name="operatingMessage" select="../OperatingMessage" />

		<xsl:variable name="isInternal">
			<xsl:if test="not(externalLink)">true</xsl:if>
		</xsl:variable>
		
		<section class="no-pad-tablet">
		
				<div class="section-inside">
	  				<div class="heading-wrapper">
	  					<figure>
		  					<img alt="" src="{/Document/requestinfo/currentURI}/{/Document/module/alias}/icon/{flowID}" />
		  				</figure>
		  				<div class="heading">
	  						<h1 id="flow_{$flowID}" class="xl"><xsl:value-of select="name" />
	  							<xsl:if test="../loggedIn and ../userFavouriteModuleAlias">
	  								<i id="flowFamily_{FlowFamily/flowFamilyID}" data-icon-after="*" class="xl favourite">
	  									<xsl:if test="not(../UserFavourite[FlowFamily/flowFamilyID = $flowFamilyID])">
											<xsl:attribute name="class">xl favourite gray</xsl:attribute>
										</xsl:if>
	  								</i>
	  							</xsl:if>
	  						</h1>
	  						<xsl:if test="not(../loggedIn) and requireAuthentication = 'true'">
	  							<span data-icon-before="u" class="marginleft"><xsl:value-of select="$i18n.AuthenticationRequired" /></span>
	  						</xsl:if>
						</div>
						<xsl:if test="$operatingMessage/global = 'false'">
							<section class="modal warning floatleft clearboth border-box full" style=""><i class="icon" style="font-size: 16px; margin-right: 4px; color: rgb(199, 52, 52);">!</i><xsl:value-of select="$operatingMessage/message" /></section>
						</xsl:if>
	  				</div>
	  				<div class="description">
	  					<a class="btn btn-light btn-inline btn-readmore">L�S MER</a>
	  					<xsl:choose>
	  						<xsl:when test="longDescription"><xsl:value-of select="longDescription" disable-output-escaping="yes" /></xsl:when>
	  						<xsl:otherwise><xsl:value-of select="shortDescription" disable-output-escaping="yes" /></xsl:otherwise>
	  					</xsl:choose>
	  					<xsl:if test="FlowFamily/contactName or FlowFamily/ownerName">
							<div class="about-flow">
								<xsl:if test="FlowFamily/contactName">
									<div class="inner">
										<h2 class="h1"><xsl:value-of select="$i18n.Questions" /></h2>
										<xsl:value-of select="FlowFamily/contactName" />
										<xsl:if test="FlowFamily/contactEmail">
											<br/><a href="mailto:{FlowFamily/contactEmail}" title="{$i18n.SendMailTo}: {FlowFamily/contactEmail}"><xsl:value-of select="FlowFamily/contactEmail" /></a>
										</xsl:if>
										<xsl:if test="FlowFamily/contactPhone">
											<br /><xsl:value-of select="FlowFamily/contactPhone" />
										</xsl:if>
									</div>
								</xsl:if>
								<xsl:if test="FlowFamily/ownerName">
									<div class="inner">
										<h2 class="h1"><xsl:value-of select="$i18n.Responsible" /></h2>
										<xsl:value-of select="FlowFamily/ownerName" />
										<xsl:if test="FlowFamily/ownerEmail">
											<br /><a href="mailto:{FlowFamily/ownerEmail}" title="{$i18n.SendMailTo}: {FlowFamily/ownerEmail}"><xsl:value-of select="FlowFamily/ownerEmail" /></a>
										</xsl:if>
									</div>
								</xsl:if>
							</div>
						</xsl:if>
	  				</div>
	  				
 				</div>
 				
 			<xsl:variable name="isDisabled" select="$operatingMessage and $operatingMessage/disableFlows = 'true'" />
 			
  			<div class="aside-inside">
  				<div class="section yellow">
  					<xsl:if test="Checks/check">
	  					<h2 class="bordered"><xsl:value-of select="$i18n.ChecklistTitle" /></h2>
	  					<ul class="checklist">
	  						<xsl:apply-templates select="Checks/check" mode="overview" />
	  					</ul>
  					</xsl:if>
  					<div class="btn-wrapper hide-tablet">
  						<xsl:choose>
  							<xsl:when test="$isInternal = 'true'">
  								<xsl:choose>
  									<xsl:when test="$isDisabled"><a class="btn btn-green xl disabled" href="javascript:void(0)" title="{$operatingMessage/message}"><xsl:value-of select="$i18n.StartFlow" /></a></xsl:when>
  									<xsl:otherwise><a class="btn btn-green xl" href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/flow/{flowID}"><xsl:value-of select="$i18n.StartFlow" /></a></xsl:otherwise>
  								</xsl:choose>
  							</xsl:when>
  							<xsl:otherwise>
  								<a class="btn btn-green xl" href="{externalLink}">
  								
  									<xsl:if test="$isInternal != 'true' and ../openExternalFlowsInNewWindow = 'true'">
		  								<xsl:attribute name="data-icon-after">e</xsl:attribute>
										<xsl:attribute name="target">_blank</xsl:attribute>
		  							</xsl:if>
		  							<xsl:if test="$isDisabled">
		  								<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
		  								<xsl:attribute name="class">btn btn-green xl disabled</xsl:attribute>
		  							</xsl:if>
  									<xsl:value-of select="$i18n.StartFlow" />
  								
  								</a>
  							</xsl:otherwise>
  						</xsl:choose>
  					</div>
  				</div>
  			</div>
  			
  			<xsl:if test="$isInternal = 'true'">
  			
	  			<div class="section-full no-pad-tablet">
	  				<h2 class="h1 hide-tablet"><xsl:value-of select="$i18n.StepDescriptionTitle" />:</h2>
	  				
	  				<xsl:variable name="submitText">
						<xsl:choose>
							<xsl:when test="requireSigning = 'true'"><xsl:value-of select="$i18n.signAndSubmit" /></xsl:when>
							<xsl:otherwise><xsl:value-of select="$i18n.submit" /></xsl:otherwise>
						</xsl:choose>			
					</xsl:variable>
	  				
	  				<div class="service-navigator-wrap summary">
	  					<div>
	  					
	  						<a data-icon-after="&lt;" href="#" class="js-prev disabled">
			  					<span><xsl:value-of select="$i18n.Previous" /></span>
			  				</a>
	  						
	  						<ul class="service-navigator primary navigated">
	  						
			  					<xsl:apply-templates select="Steps/Step" mode="overview">
			  						<xsl:with-param name="flowDisabled" select="$isDisabled" />
			  					</xsl:apply-templates>
			  					
			  					<xsl:variable name="stepCount" select="count(Steps/Step)" />
			  					
			  					<xsl:choose>
			  						<xsl:when test="usePreview = 'true'">
			  							<li>
					  						<span data-step="{$stepCount + 1}"><xsl:value-of select="$i18n.preview" /></span>
					  					</li>
					  					<li>
					  						<span data-step="{$stepCount + 2}"><xsl:value-of select="$submitText" /></span>
					  					</li>
			  						</xsl:when>
			  						<xsl:otherwise>
			  							<li>
					  						<span data-step="{$stepCount + 1}"><xsl:value-of select="$submitText" /></span>
					  					</li>
			  						</xsl:otherwise>
			  					</xsl:choose>
		  					
		  					</ul>
		  					
		  					<a data-icon-after="&gt;" href="#" class="js-next">
			  					<span><xsl:value-of select="$i18n.Next" /></span>
			  				</a>
		  					
	  					</div>
	  					
	  				</div>
	  			</div>
	  		
	  		</xsl:if>
	  		
		</section>
	
		<xsl:if test="../showRelatedFlows = 'true'">
	
			<xsl:variable name="flowTypeID" select="FlowType/flowTypeID" />
		
			<xsl:apply-templates select="FlowType">
				<xsl:with-param name="flows" select="../FlowTypeFlows/Flow[FlowType/flowTypeID = $flowTypeID]" />
			</xsl:apply-templates>
	
		</xsl:if>
	
	</xsl:template>
	
	<xsl:template match="check" mode="overview">
	
		<li><xsl:value-of select="." /></li>
	
	</xsl:template>
	
	<xsl:template match="Step" mode="overview">
		
		<xsl:param name="flowDisabled" />
		
		<li>
			<xsl:if test="position() = 1">
				<xsl:attribute name="class">begin</xsl:attribute>
			</xsl:if>
			<span data-step="{position()}"><xsl:value-of select="name" /></span>
			
			<xsl:if test="position() = 1">
				<xsl:choose>
					<xsl:when test="$flowDisabled"><a class="btn btn-green btn-inline hide-desktop disabled" href="javascript:void(0)"><xsl:value-of select="$i18n.StartFlow" /></a></xsl:when>
					<xsl:otherwise><a class="btn btn-green btn-inline hide-desktop" href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/flow/{../../flowID}"><xsl:value-of select="$i18n.StartFlow" /></a></xsl:otherwise>
				</xsl:choose>
				
			</xsl:if>
		</li>
		
	</xsl:template>
	
	<xsl:template match="ShowFlowTypes">
		
		<script type="text/javascript">
			searchFlowURI = '<xsl:value-of select="/Document/requestinfo/currentURI" />/<xsl:value-of select="/Document/module/alias" />/search';
			userFavouriteModuleURI = '<xsl:value-of select="/Document/requestinfo/contextpath" /><xsl:value-of select="userFavouriteModuleAlias" />';
		</script>
		
		<xsl:if test="validationError">
			<xsl:apply-templates select="validationError" />
		</xsl:if>
		
		<section class="no-shadow-btm">
	  				
			<div class="search-wrapper">
				<h2 class="h1"><xsl:value-of select="$i18n.SearchTitle" /></h2>
				<div class="search">
					<div class="input-wrapper">
						<input type="text" placeholder="{$i18n.SearchHints}" name="search" class="noborder" id="search" />
						<!-- <div class="symbol"><i class="xl">r</i></div> -->
						<input type="button" value="s" class="btn btn-search" onclick="searchFlow()" />
					</div>
				</div>
			</div>
			<xsl:if test="recommendedTags">
				<div class="tags-wrapper">
					<div class="tags">
						<h2 class="h1"><xsl:value-of select="$i18n.RecommendedSearches" /></h2>
						<ul>
							<xsl:apply-templates select="recommendedTags/Tag" />
						</ul>
					</div>
				</div>
			</xsl:if>
			
		</section>
		
		<section class="search-results">
			<div class="info">
				<!-- <span class="message"><i>c</i><xsl:value-of select="$i18n.SearchDone" />.</span> -->
				<span class="close"><a href="#"><xsl:value-of select="$i18n.close" /> <i>x</i></a></span>
			</div>
			<h2 class="h1 search-results-title"><span class="title" /><xsl:text>&#160;</xsl:text><xsl:value-of select="$i18n.Hits.Part1" /><xsl:text>&#160;</xsl:text><span class="hits" /><xsl:text>&#160;</xsl:text><xsl:value-of select="$i18n.Hits.Part2" /></h2>
			<ul class="previews" />
		</section>
		
		<xsl:if test="FlowType">
			
			<section>
				<div class="filter-wrapper">
					<div class="hr-divider popularflows-divider">
						<span class="label"><xsl:value-of select="$i18n.SortFlowTypes" /></span>
					</div>
					<a class="btn btn-dark xl filter-btn" data-icon-after="_"><xsl:value-of select="$i18n.FlowTypeFilter" /></a>
					<div class="filters">
						<xsl:apply-templates select="FlowType" mode="filter" />
					</div>
				</div>
			</section>
			
			<xsl:apply-templates select="FlowType" />
				
		</xsl:if>
				
	</xsl:template>
	
	<xsl:template match="Tag">
	
		<li><a href="#"><xsl:value-of select="." /></a></li>
		
	</xsl:template>
	
	<xsl:template match="FlowType" mode="filter">
		
		<xsl:variable name="flowTypeID" select="flowTypeID" />
		
		<a class="btn btn-xs btn-light btn-inline" href="#flowtype{flowTypeID}"><xsl:value-of select="name" /><xsl:text>&#160;(</xsl:text><xsl:value-of select="count(../Flow[FlowType/flowTypeID = $flowTypeID])" /><xsl:text>)</xsl:text></a>
		
	</xsl:template>
	
	<xsl:template match="FlowType">
		
		<xsl:param name="flowTypeID" select="flowTypeID" />
		<xsl:param name="flows" select="../Flow[FlowType/flowTypeID = $flowTypeID]" />
		<xsl:param name="useCategoryFilter" select="../useCategoryFilter" />
		
		<section id="flowtype_{flowTypeID}" class="accordion" data-filter="flowtype{flowTypeID}">
			
			<xsl:if test="position() = 1">
				<xsl:attribute name="class">accordion first</xsl:attribute>
			</xsl:if>
			
			<div class="heading-wrapper">
				
				<h2 class="h1">
					<xsl:value-of select="name" />
					<a class="btn btn-light accordion-toggler count"></a>
				</h2>
				
				<xsl:if test="$flows and $useCategoryFilter">
					
					<div class="select-wrapper">
						<div class="select-box category-select" id="select_{flowTypeID}">
							<span class="text"><xsl:value-of select="$i18n.MostPopular" /></span>
							<span class="arrow">_</span>
							<div class="options">
								<ul>
									<xsl:apply-templates select="Categories/Category">
										<xsl:with-param name="flows" select="$flows" />
									</xsl:apply-templates>
									<xsl:variable name="uncategorizedFlows" select="$flows[not(Category)]" />
									<xsl:if test="$uncategorizedFlows">
										<li id="uncategorized">
											<a href="#">
												<span class="text"><xsl:value-of select="$i18n.Uncategorized" /></span>
												<span class="count"><xsl:value-of select="count($uncategorizedFlows)" /></span>
											</a>
										</li>
									</xsl:if>
									<li id="popular" class="selected">
										<a href="#">
											<span class="text"><xsl:value-of select="$i18n.MostPopular" /></span>
											<span class="count"><i>*</i></span>
										</a>
									</li>
								</ul>
							</div>
						</div>
					</div>
				
				</xsl:if>
				
			</div>
			
			<xsl:choose>
				<xsl:when test="$useCategoryFilter">
					
					<ul class="previews">
						<xsl:choose>
							<xsl:when test="not($flows)">
								<li><div class="inner"><h2><xsl:value-of select="$i18n.NoFlowsFound"/></h2></div></li>
							</xsl:when>
							<xsl:otherwise>
								<xsl:apply-templates select="$flows" />
							</xsl:otherwise>
						</xsl:choose>
					</ul>
					
				</xsl:when>
				<xsl:otherwise>
					
					<xsl:variable name="popularFlows" select="$flows[popular]" />
					
					<div class="popularflows-wrapper">
				
						<div class="hr-divider popularflows-divider">
							<span class="label"><xsl:value-of select="$i18n.MostUsedFLows" /></span>
						</div>
				
						<ul class="previews popularflows-list">
						</ul>
					
					</div>
				
					<div class="allflows-wrapper hidden">
				
						<div class="hr-divider allflows-divider">
							<span class="label"><xsl:value-of select="$i18n.AllFlows" /></span>
						</div>
					
						<ul class="previews allflows-list">
							<xsl:choose>
								<xsl:when test="not($flows)">
									<li><div class="inner"><h2><xsl:value-of select="$i18n.NoFlowsFound"/></h2></div></li>
								</xsl:when>
								<xsl:otherwise>
									<xsl:apply-templates select="$flows" />
								</xsl:otherwise>
							</xsl:choose>
						</ul>
				
					</div>
				
					<a class="footer-button show-all-link"><xsl:value-of select="$i18n.ShowAll" /></a>
					
				</xsl:otherwise>
			</xsl:choose>
			
		</section>
		
	</xsl:template>
	
	<xsl:template match="Category">
		
		<xsl:param name="flows" />
		
		<xsl:variable name="categoryID" select="categoryID" />
		<xsl:variable name="categoryFlows" select="$flows[Category/categoryID = $categoryID]" />
		
		<xsl:if test="$categoryFlows">
			
			<li id="category_{$categoryID}">
				<a href="#">
					<span class="text"><xsl:value-of select="name" /></span>
					<span class="count"><xsl:value-of select="count($flows[Category/categoryID = $categoryID])" /></span>
				</a>
			</li>

		</xsl:if>
		
	</xsl:template>
	
	<xsl:template match="Flow">
		
		<xsl:variable name="flowID" select="flowID" />
		
		<li id="flow_{flowID}">
			
			<xsl:attribute name="class">
				
				<xsl:choose>
					<xsl:when test="Category">
						<xsl:text> category_</xsl:text><xsl:value-of select="Category/categoryID" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text> uncategorized</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:if test="enabled = 'false'"> disabled</xsl:if>
	 			<xsl:if test="popular = 'true'"> popular</xsl:if>
			</xsl:attribute>
			
			<xsl:variable name="flowFamilyID" select="FlowFamily/flowFamilyID" />
			
			<xsl:choose>
				<xsl:when test="enabled = 'true'">
					<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/overview/{FlowFamily/flowFamilyID}">
						<div class="inner">
							<figure><img src="{/Document/requestinfo/currentURI}/{/Document/module/alias}/icon/{flowID}" alt="" /></figure>
							<div>
								<h2>
									<xsl:value-of select="name" />
									<xsl:choose>
										<xsl:when test="//loggedIn">
											<xsl:if test="//userFavouriteModuleAlias">
												<i id="flowFamily_{FlowFamily/flowFamilyID}" data-icon-after="*" class="favourite">
													<xsl:if test="not(//UserFavourite[FlowFamily/flowFamilyID = $flowFamilyID])">
														<xsl:attribute name="class">favourite gray</xsl:attribute>
													</xsl:if>
												</i>
											</xsl:if>
										</xsl:when>
										<xsl:when test="requireAuthentication = 'true'">
											<i data-icon-before="u" title="{$i18n.AuthenticationRequired}" class="marginleft"></i>
										</xsl:when>
									</xsl:choose>
								</h2>
								<span class="description"><xsl:value-of select="shortDescription" disable-output-escaping="yes" /></span>
							</div>
						</div>
					</a>
				</xsl:when>
				<xsl:otherwise>
					<div class="inner">
						<figure><img src="{/Document/requestinfo/currentURI}/{/Document/module/alias}/icon/{flowID}" width="65" alt="" /></figure>
						<div>
							<h2><xsl:value-of select="name" /><b class="hidden">(<xsl:value-of select="$i18n.FlowDisabled" />)</b></h2>
							<span class="description">
								<b>(<xsl:value-of select="$i18n.FlowDisabled" />)</b><br/>
								<xsl:value-of select="shortDescription" disable-output-escaping="yes" />
							</span>
						</div>
					</div>
				</xsl:otherwise>
			</xsl:choose>
			
		</li>
		
	</xsl:template>		
			
</xsl:stylesheet>