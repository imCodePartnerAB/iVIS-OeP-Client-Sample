<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/Common.xsl"/>

	<xsl:variable name="globalscripts">
		/jquery/jquery.js
	</xsl:variable>
	
	<xsl:variable name="scripts">
		/js/multigeometrymapquery.js
	</xsl:variable>

	<xsl:variable name="links">
		/basemapquery/staticcontent/css/basemapquery.css
		/css/multigeometrymapquery.css
	</xsl:variable>

	<xsl:template match="Document">	
		
		<xsl:apply-templates select="ShowQueryValues"/>
		<xsl:apply-templates select="ShowQueryForm"/>
	
		<xsl:call-template name="setMapClientInstanceLanguages" />

		<script type="text/javascript">
			multiGeometryMapQueryLanguage = {
				'RETRIEVING_PUD' : '<xsl:value-of select="$i18n.RetrievingPUD" />',
				'ZOOMSCALE_BUTTON': '<xsl:value-of select="$i18n.ZoomScaleButton" />',
				'UNKOWN_ERROR_MESSAGE_TITLE' :  '<xsl:value-of select="$i18n.UnkownErrorMessageTitle" />',
				'UNKOWN_ERROR_MESSAGE' : '<xsl:value-of select="$i18n.UnkownErrorMessage" />',
				'NO_PUD_FOUND_MESSAGE_TITLE' : '<xsl:value-of select="$i18n.NoPUDFoundMessageTitle" />',
				'NO_PUD_FOUND_MESSAGE' : '<xsl:value-of select="$i18n.NoPUDFoundMessage" />',
				'NO_PUD_FOUND_MESSAGE' : '<xsl:value-of select="$i18n.NoPUDFoundMessage" />'
			};
		</script>
		
	</xsl:template>
	
	<xsl:template match="ShowQueryValues">
	
		<xsl:variable name="shortQueryID" select="concat('q', MultiGeometryMapQueryInstance/MultiGeometryMapQuery/queryID)" />
	
		<xsl:variable name="queryID" select="concat('query_', MultiGeometryMapQueryInstance/MultiGeometryMapQuery/queryID)" />
		
		<div class="query" id="{$queryID}">
		
			<article>
				
				<div class="heading-wrapper">
					
					<h2>
						<xsl:attribute name="class">
							<xsl:if test="MultiGeometryMapQueryInstance/QueryInstanceDescriptor/queryState = 'VISIBLE_REQUIRED'">
								<xsl:text>required</xsl:text>
							</xsl:if>
						</xsl:attribute>
						<xsl:value-of select="MultiGeometryMapQueryInstance/QueryInstanceDescriptor/QueryDescriptor/name"/>
					</h2>
					
					<xsl:call-template name="createUpdateButton">
						<xsl:with-param name="queryID" select="MultiGeometryMapQueryInstance/MultiGeometryMapQuery/queryID" />
					</xsl:call-template>
					
					<xsl:if test="MultiGeometryMapQueryInstance/MultiGeometryMapQuery/description">
						<span class="italic">
							<xsl:if test="/Document/useCKEditorForDescription = 'true'"><xsl:attribute name="class">italic html-description</xsl:attribute></xsl:if>
							<xsl:value-of select="MultiGeometryMapQueryInstance/MultiGeometryMapQuery/description" disable-output-escaping="yes" />
						</span>		
					</xsl:if>
				
				</div>
		
				<xsl:choose>
					<xsl:when test="/Document/previewMode = 'WEB_MAP'">
		
						<script type="text/javascript">mapClientScriptLocationFallback = '<xsl:value-of select="/Document/mapScriptURL" />';</script>
				
						<div id="{$shortQueryID}_search" class="mapquery-searchwrapper" />
						<div id="{$shortQueryID}_objectconfigtool" class="mapquery-objectconfigwrapper" />
				
						<div id="{$shortQueryID}" class="mapquery" />
				
						<div class="mapquery-pudinfo-wrapper">
							<div id="{$shortQueryID}_pudinfo" class="hidden">
								<xsl:value-of select="$i18n.PropertyUnitDesignation" /><xsl:text>:&#160;</xsl:text><span />
							</div>
						</div>
						
						<xsl:for-each select="MultiGeometryMapQueryInstance/geometries/Geometry">
							
							<input type="hidden" name="{$shortQueryID}_geometry"><xsl:attribute name="value"><xsl:value-of select="geometry" /><xsl:if test="config">#<xsl:value-of select="config" /></xsl:if></xsl:attribute></input>
							
						</xsl:for-each>
						
						<input id="{$shortQueryID}_propertyUnitGeometry" type="hidden" name="{$shortQueryID}_propertyUnitGeometry" value="{MultiGeometryMapQueryInstance/propertyUnitGeometry}" />
						<input id="{$shortQueryID}_propertyUnitDesignation" type="hidden" name="{$shortQueryID}_propertyUnitDesignation" value="{MultiGeometryMapQueryInstance/propertyUnitDesignation}" />
						<input id="{$shortQueryID}_extent" type="hidden" name="{$shortQueryID}_extent" value="{MultiGeometryMapQueryInstance/extent}" />
						<input id="{$shortQueryID}_epsg" type="hidden" name="{$shortQueryID}_epsg" value="{MultiGeometryMapQueryInstance/epsg}" />
						<input id="{$shortQueryID}_baseLayer" type="hidden" name="{$shortQueryID}_baseLayer" value="{MultiGeometryMapQueryInstance/visibleBaseLayer}" />
						
						<script src="{/Document/mapScriptURL}" type="text/javascript" />
				
					</xsl:when>
					<xsl:otherwise>
						
						<img src="{queryRequestURL}?mapimage=true" class="full bigmargintop" alt="{MultiGeometryMapQueryInstance/propertyUnitGeometry}" />
					
						<div class="mapquery-pudinfo-wrapper">
							<xsl:value-of select="$i18n.PropertyUnitDesignation" /><xsl:text>:&#160;</xsl:text><span><xsl:value-of select="MultiGeometryMapQueryInstance/propertyUnitDesignation" /></span>
						</div>
					
					</xsl:otherwise>
				</xsl:choose>
				
			</article>
				
			<xsl:if test="/Document/previewMode = 'WEB_MAP'">	
				
				<script type="text/javascript">
					
					multiGeometryMapQueryMinScales['<xsl:value-of select="$shortQueryID" />'] = '<xsl:value-of select="MultiGeometryMapQueryInstance/MultiGeometryMapQuery/minimumScale" />';
						
					$(document).ready(function(){
						initMultiGeometryMapQuery('<xsl:value-of select="MultiGeometryMapQueryInstance/MultiGeometryMapQuery/queryID" />', '<xsl:value-of select="/Document/requestinfo/contextpath" /><xsl:value-of select="/Document/fullAlias" />', '<xsl:value-of select="/Document/startExtent" />', '<xsl:value-of select="/Document/lmUser" />', true);
					});
					
				</script>
			
			</xsl:if>
		
		</div>
		
	</xsl:template>	
		
	<xsl:template match="ShowQueryForm">
	
		<xsl:variable name="shortQueryID" select="concat('q', MultiGeometryMapQueryInstance/MultiGeometryMapQuery/queryID)" />
	
		<xsl:variable name="queryID" select="concat('query_', MultiGeometryMapQueryInstance/MultiGeometryMapQuery/queryID)" />
	
		<div class="query" id="{$queryID}">
		
			<a name="{$queryID}" />
	
			<div id="{$shortQueryID}-validationerrors">
				<xsl:if test="ValidationErrors/validationError">
					<div id="{$queryID}-validationerrors" class="validationerrors">
						<div class="info-box error">
							<xsl:apply-templates select="ValidationErrors/validationError" />
							<div class="marker"></div>
						</div>
					</div>
				</xsl:if>
			</div>
	
			<article>
			
				<xsl:if test="ValidationErrors/validationError">
					<xsl:attribute name="class">error</xsl:attribute>
				</xsl:if>
			
				<div class="heading-wrapper">
					
					<h2>
						<xsl:attribute name="class">
							<xsl:if test="MultiGeometryMapQueryInstance/QueryInstanceDescriptor/queryState = 'VISIBLE_REQUIRED'">
								<xsl:text>required</xsl:text>
							</xsl:if>
						</xsl:attribute>
						<xsl:value-of select="MultiGeometryMapQueryInstance/QueryInstanceDescriptor/QueryDescriptor/name"/>
					</h2>
					
					<xsl:if test="MultiGeometryMapQueryInstance/MultiGeometryMapQuery/helpText">		
						<xsl:apply-templates select="MultiGeometryMapQueryInstance/MultiGeometryMapQuery/helpText" />
					</xsl:if>
					
					<xsl:if test="MultiGeometryMapQueryInstance/MultiGeometryMapQuery/description">
						<span class="italic">
							<xsl:if test="/Document/useCKEditorForDescription = 'true'"><xsl:attribute name="class">italic html-description</xsl:attribute></xsl:if>
							<xsl:value-of select="MultiGeometryMapQueryInstance/MultiGeometryMapQuery/description" disable-output-escaping="yes" />
						</span>		
					</xsl:if>
				
				</div>
		
				<xsl:if test="MultiGeometryMapQueryInstance/MultiGeometryMapQuery/startInstruction">
					<div id="{$shortQueryID}_startinstruction" class="mapquery-startinstruction">
						<xsl:value-of select="MultiGeometryMapQueryInstance/MultiGeometryMapQuery/startInstruction" disable-output-escaping="yes" />
						<a class="btn btn-blue"><xsl:value-of select="$i18n.StartInstructionButton" /></a>
					</div>
				</xsl:if>
		
				<script type="text/javascript">mapClientScriptLocationFallback = '<xsl:value-of select="/Document/mapScriptURL" />';</script>
		
				<div class="mapquery-searchwrapper">
		
					<div id="{$shortQueryID}_search" class="searchpud mapquery-searchtool">
						<h3><xsl:value-of select="$i18n.SearchToolDescription" /></h3>
					</div>
			
					<div id="{$shortQueryID}_searchcoordinate" class="searchcoordinate mapquery-searchtool">
						<h3><xsl:value-of select="$i18n.SearchCoordinateToolDescription" /></h3>
					</div>
		
				</div>
		
				<div id="{$shortQueryID}" class="mapquery">
					
					<div id="{$shortQueryID}_objectconfigtool" class="mapquery-objectconfigwrapper" />
					<div id="{$shortQueryID}_puddialogtemplate" class="puddialog-hidden">
						<xsl:value-of select="$i18n.PUDInfoDialogText.Part1" /><xsl:text>:&#160;</xsl:text><span /><br />
						<xsl:value-of select="$i18n.PUDInfoDialogText.Part2" />
						<div>
							<a class="btn btn-blue done-btn"><xsl:value-of select="$i18n.Done" /></a>
							<a class="btn btn-blue cancel-btn"><xsl:value-of select="$i18n.Cancel" /></a>
						</div>
					</div>
				</div>
		
				<div class="mapquery-pudinfo-wrapper">
					<div id="{$shortQueryID}_pudinfo" class="hidden">
						<xsl:value-of select="$i18n.PropertyUnitDesignation" /><xsl:text>:&#160;</xsl:text><span />
					</div>
					<div id="{$shortQueryID}_coordinatesinfo" class="hidden">
						<xsl:value-of select="$i18n.Coordinates" /><xsl:text>:&#160;</xsl:text><span />
					</div>
				</div>
				
				<xsl:for-each select="MultiGeometryMapQueryInstance/geometries/Geometry">
					
					<input type="hidden" name="{$shortQueryID}_geometry"><xsl:attribute name="value"><xsl:value-of select="geometry" /><xsl:if test="config">#<xsl:value-of select="config" /></xsl:if></xsl:attribute></input>
					
				</xsl:for-each>
				
				<input id="{$shortQueryID}_propertyUnitGeometry" type="hidden" name="{$shortQueryID}_propertyUnitGeometry" value="{MultiGeometryMapQueryInstance/propertyUnitGeometry}" />
				<input id="{$shortQueryID}_propertyUnitDesignation" type="hidden" name="{$shortQueryID}_propertyUnitDesignation" value="{MultiGeometryMapQueryInstance/propertyUnitDesignation}" />
				<input id="{$shortQueryID}_extent" type="hidden" name="{$shortQueryID}_extent" value="{MultiGeometryMapQueryInstance/extent}" />
				<input id="{$shortQueryID}_epsg" type="hidden" name="{$shortQueryID}_epsg" value="{MultiGeometryMapQueryInstance/epsg}" />
				<input id="{$shortQueryID}_baseLayer" type="hidden" name="{$shortQueryID}_baseLayer" value="{MultiGeometryMapQueryInstance/visibleBaseLayer}" />
				
				<script src="{/Document/mapScriptURL}" type="text/javascript" />
				
			</article>
		
			<script type="text/javascript">
				
				multiGeometryMapQueryMinScales['<xsl:value-of select="$shortQueryID" />'] = '<xsl:value-of select="MultiGeometryMapQueryInstance/MultiGeometryMapQuery/minimumScale" />';
				
				$(document).ready(function(){
					initMultiGeometryMapQuery('<xsl:value-of select="MultiGeometryMapQueryInstance/MultiGeometryMapQuery/queryID" />', '<xsl:value-of select="/Document/requestinfo/contextpath" /><xsl:value-of select="/Document/fullAlias" />', '<xsl:value-of select="/Document/startExtent" />', '<xsl:value-of select="/Document/lmUser" />', false);
				});
				
			</script>
			
		</div>
		
	</xsl:template>
	
	<xsl:template match="validationError[messageKey='GeometriesRequired']">
		
		<span>
			<strong data-icon-before="!">
				<xsl:value-of select="/Document/requiredQueryMessage" />
			</strong>
		</span>
		
	</xsl:template>
	
	<xsl:template match="validationError[messageKey='GeometryNotValid']">
	
		<span>
			<strong data-icon-before="!">
				<xsl:value-of select="$i18n.GeometryNotValid" />
			</strong>
		</span>
		
	</xsl:template>
	
	<xsl:template match="validationError[messageKey='PUDRequired']">
	
		<span>
			<strong data-icon-before="!">
				<xsl:value-of select="$i18n.PUDRequired" />
			</strong>
		</span>
		
	</xsl:template>
	
	<xsl:template match="validationError[messageKey='GeometryRequired']">
	
		<span>
			<strong data-icon-before="!">
				<xsl:value-of select="/Document/requiredQueryMessage" />
			</strong>
		</span>
		
	</xsl:template>
	
	<xsl:template match="validationError[messageKey='GeometryNotValid']">
	
		<span>
			<strong data-icon-before="!">
				<xsl:value-of select="$i18n.GeometryNotValid" />
			</strong>
		</span>
		
	</xsl:template>
	
	<xsl:template match="validationError[messageKey='RequiredQuery']">
	
		<span>
			<strong data-icon-before="!">
				<xsl:value-of select="/Document/requiredQueryMessage" />
			</strong>
		</span>
		
	</xsl:template>
	
	<xsl:template match="validationError">
	
		<span>
			<strong data-icon-before="!">
				<xsl:value-of select="$i18n.UnknownValidationError"/>
			</strong>
		</span>
		
	</xsl:template>
	
</xsl:stylesheet>