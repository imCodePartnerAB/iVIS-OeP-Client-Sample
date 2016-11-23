<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/Common.xsl"/>

	<xsl:variable name="globalscripts">
		/jquery/jquery.js
	</xsl:variable>
	
	<xsl:variable name="scripts">
		/js/pudquery.js
	</xsl:variable>

	<xsl:variable name="links">
		/css/pudquery.css
	</xsl:variable>

	<xsl:template match="Document">	
		
		<xsl:apply-templates select="ShowQueryValues"/>
		<xsl:apply-templates select="ShowQueryForm"/>
		
		<script language="javascript" type="text/javascript">
			pudQueryLanguage = {
				'CANT_CONTACT_SEARCHSERVICE' : '<xsl:value-of select="$i18n.CantContactSearchService" />',
				'SERVICE_ERROR_MESSAGE' : '<xsl:value-of select="$i18n.ServiceErrorMessage" />',
				'PUD_NOT_FOUND' : '<xsl:value-of select="$i18n.PUDNotFound" />',
				'TO_MANY_PUD_FOUND' : '<xsl:value-of select="$i18n.ToManyPUDFound" />',
				'UNKOWN_ERROR_MESSAGE' : '<xsl:value-of select="$i18n.UnkownErrorMessage" />',
			};
		</script>
		
	</xsl:template>
	
	<xsl:template match="ShowQueryValues">
	
		<xsl:variable name="queryID" select="concat('query_', PUDQueryInstance/PUDQuery/queryID)" />
		
		<div class="query" id="{$queryID}">
		
			<article>
				
				<div class="heading-wrapper">
					
					<h2>
						<xsl:attribute name="class">
							<xsl:if test="PUDQueryInstance/QueryInstanceDescriptor/queryState = 'VISIBLE_REQUIRED'">
								<xsl:text>required</xsl:text>
							</xsl:if>
						</xsl:attribute>
						<xsl:value-of select="PUDQueryInstance/QueryInstanceDescriptor/QueryDescriptor/name"/>
					</h2>
					
					<xsl:call-template name="createUpdateButton">
						<xsl:with-param name="queryID" select="PUDQueryInstance/PUDQuery/queryID" />
					</xsl:call-template>
					
					<xsl:if test="PUDQueryInstance/PUDQuery/description">
						<span class="italic">
							<xsl:if test="/Document/useCKEditorForDescription = 'true'"><xsl:attribute name="class">italic html-description</xsl:attribute></xsl:if>
							<xsl:value-of select="PUDQueryInstance/PUDQuery/description" disable-output-escaping="yes" />
						</span>		
					</xsl:if>
					
				</div>
				
				<xsl:value-of select="PUDQueryInstance/propertyUnitDesignation" />
				
			</article>
				
			<script type="text/javascript">$(document).ready(function(){initPUDQuery('<xsl:value-of select="PUDQueryInstance/PUDQuery/queryID" />');});</script>
		
		</div>
		
	</xsl:template>	
		
	<xsl:template match="ShowQueryForm">
	
		<xsl:variable name="shortQueryID" select="concat('q', PUDQueryInstance/PUDQuery/queryID)" />
	
		<xsl:variable name="queryID" select="concat('query_', PUDQueryInstance/PUDQuery/queryID)" />
	
		<div class="query pudquery" id="{$queryID}">
		
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
							<xsl:if test="PUDQueryInstance/QueryInstanceDescriptor/queryState = 'VISIBLE_REQUIRED'">
								<xsl:text>required</xsl:text>
							</xsl:if>
						</xsl:attribute>
						<xsl:value-of select="PUDQueryInstance/QueryInstanceDescriptor/QueryDescriptor/name"/>
					</h2>
					
					<xsl:if test="PUDQueryInstance/PUDQuery/helpText">		
						<xsl:apply-templates select="PUDQueryInstance/PUDQuery/helpText" />
					</xsl:if>
					
					<xsl:if test="PUDQueryInstance/PUDQuery/description">
						<span class="italic">
							<xsl:if test="/Document/useCKEditorForDescription = 'true'"><xsl:attribute name="class">italic html-description</xsl:attribute></xsl:if>
							<xsl:value-of select="PUDQueryInstance/PUDQuery/description" disable-output-escaping="yes" />
						</span>	
					</xsl:if>
				
				</div>
		
				<div class="search-select">
					<select name="{$shortQueryID}_searchservice">
						<xsl:if test="PUDQueryInstance/PUDQuery/AllowedSearchServices[allowedSearchService='PUD']">
							<option value="{/Document/requestinfo/contextpath}{/Document/fullAlias}/searchpud" data-search-service="PUD"><xsl:value-of select="$i18n.PUD" /></option>
						</xsl:if>
						<xsl:if test="PUDQueryInstance/PUDQuery/AllowedSearchServices[allowedSearchService='ADDRESS']">
							<option value="{/Document/requestinfo/contextpath}{/Document/fullAlias}/searchaddress" data-search-service="ADDRESS"><xsl:value-of select="$i18n.Address" /></option>
						</xsl:if>
					</select>
				</div>
				
				<div class="search-input">
					<xsl:call-template name="createTextField">
						<xsl:with-param name="id" select="concat($shortQueryID, '_searchInput')" />
						<xsl:with-param name="name" select="concat($shortQueryID, '_searchInput')" />
					</xsl:call-template>
				</div>
				
				<div class="selected-pud">
					<xsl:if test="not(PUDQueryInstance/propertyUnitDesignation)">
						<xsl:attribute name="class">selected-pud hidden</xsl:attribute>
					</xsl:if>
					<xsl:call-template name="createHiddenField">
						<xsl:with-param name="id" select="concat($shortQueryID, '_propertyUnitDesignation')" />
						<xsl:with-param name="name" select="concat($shortQueryID, '_propertyUnitDesignation')" />
						<xsl:with-param name="value" select="PUDQueryInstance/propertyUnitDesignation"/>
					</xsl:call-template>
					<p>
						<strong><xsl:value-of select="$i18n.SelectedPud" /><xsl:text>:&#160;</xsl:text></strong>
						<span class="pud"><xsl:value-of select="PUDQueryInstance/propertyUnitDesignation" /></span>
						<i data-icon-after="t" title="{$i18n.DeleteSelectedPUD}"></i>
					</p>
				</div>
				
			</article>
		
			<script type="text/javascript">$(document).ready(function(){initPUDQuery('<xsl:value-of select="PUDQueryInstance/PUDQuery/queryID" />');});</script>
			
		</div>
		
	</xsl:template>
	
	<xsl:template match="validationError[messageKey='RequiredQuery']">
	
		<span>
			<strong data-icon-before="!">
				<xsl:value-of select="$i18n.RequiredQuery" />
			</strong>
		</span>
		
	</xsl:template>
	
	<xsl:template match="validationError[messageKey='PUDNotValid']">
	
		<span>
			<strong data-icon-before="!">
				<xsl:value-of select="$i18n.PUDNotValid" />
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