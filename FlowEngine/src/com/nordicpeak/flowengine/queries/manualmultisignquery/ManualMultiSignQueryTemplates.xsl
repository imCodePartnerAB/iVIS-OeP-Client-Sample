<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/Common.xsl"/>

 	<xsl:variable name="globalscripts">
		/jquery/jquery.js
	</xsl:variable>

	<xsl:variable name="scripts">
		/js/manualmultisignquery.js
	</xsl:variable>

	<xsl:template match="Document">	
		
		<xsl:apply-templates select="ShowQueryValues"/>
		<xsl:apply-templates select="ShowQueryForm"/>
		
	</xsl:template>
	
	<xsl:template match="ShowQueryValues">
		
		<div class="query">
			
			<article>
				
				<div class="heading-wrapper">
					<h2>
						<xsl:attribute name="class">
							<xsl:if test="ManualMultiSignQueryInstance/QueryInstanceDescriptor/queryState = 'VISIBLE_REQUIRED'">
								<xsl:text>required</xsl:text>
							</xsl:if>
						</xsl:attribute>
						<xsl:value-of select="ManualMultiSignQueryInstance/QueryInstanceDescriptor/QueryDescriptor/name"/>
					</h2>
					
					<xsl:call-template name="createUpdateButton">
						<xsl:with-param name="queryID" select="ManualMultiSignQueryInstance/ManualMultiSignQuery/queryID" />
					</xsl:call-template>
					
					<xsl:if test="ManualMultiSignQueryInstance/ManualMultiSignQuery/description">
						<span class="italic">
							<xsl:value-of select="ManualMultiSignQueryInstance/ManualMultiSignQuery/description" disable-output-escaping="yes" />
						</span>		
					</xsl:if>
					
				</div>
				
				<fieldset>
					
					<div class="split">
						<strong class="block"><xsl:value-of select="$i18n.Name" /></strong>
						<xsl:value-of select="ManualMultiSignQueryInstance/SigningParties/SigningParty/name" />
					</div>
					
					<div class="split odd">
						<strong class="block"><xsl:value-of select="$i18n.SocialSecurityNumber" /></strong>
						<xsl:value-of select="ManualMultiSignQueryInstance/SigningParties/SigningParty/socialSecurityNumber" />
					</div>
					
					<div class="split">
						<strong class="block"><xsl:value-of select="$i18n.Email" /></strong>
						<xsl:value-of select="ManualMultiSignQueryInstance/SigningParties/SigningParty/email" />
					</div>					
					
				</fieldset>
				
				
			</article>
			
		</div>
		
	</xsl:template>	
	
	<xsl:template match="ShowQueryForm">
	
		<xsl:variable name="shortQueryID" select="concat('q', ManualMultiSignQueryInstance/ManualMultiSignQuery/queryID)" />
	
		<xsl:variable name="queryID" select="concat('query_', ManualMultiSignQueryInstance/ManualMultiSignQuery/queryID)" />
	
		<div class="query" id="{$queryID}">
			
			<xsl:if test="EnableAjaxPosting">
				<xsl:attribute name="class">query enableAjaxPosting</xsl:attribute>
			</xsl:if>
			
			<a name="{$queryID}" />
			
			<xsl:if test="ValidationErrors/validationError">
				<div id="{$queryID}-validationerrors" class="validationerrors"/>
			</xsl:if>			
		
			<article>
			
				<div class="heading-wrapper">
					
					<h2>
						<xsl:attribute name="class">
							<xsl:if test="ManualMultiSignQueryInstance/QueryInstanceDescriptor/queryState = 'VISIBLE_REQUIRED'">
								<xsl:text>required</xsl:text>
							</xsl:if>
						</xsl:attribute>
						<xsl:value-of select="ManualMultiSignQueryInstance/QueryInstanceDescriptor/QueryDescriptor/name"/>
					</h2>
					
					<xsl:if test="ManualMultiSignQueryInstance/ManualMultiSignQuery/helpText">		
						<xsl:apply-templates select="ManualMultiSignQueryInstance/ManualMultiSignQuery/helpText" />
					</xsl:if>
					
					<xsl:if test="ManualMultiSignQueryInstance/ManualMultiSignQuery/description">
						<span class="italic">
							<xsl:if test="/Document/useCKEditorForDescription = 'true'"><xsl:attribute name="class">italic html-description</xsl:attribute></xsl:if>
							<xsl:value-of select="ManualMultiSignQueryInstance/ManualMultiSignQuery/description" disable-output-escaping="yes" />
						</span>		
					</xsl:if>
				
				</div>
				
				<fieldset>
					
					<div class="split">
					
						<xsl:variable name="fieldName" select="concat($shortQueryID, '_name')" />
					
						
						<xsl:if test="ValidationErrors/validationError[fieldName = $fieldName]">
							<xsl:attribute name="class">
								<xsl:text>split invalid input-error</xsl:text>
							</xsl:attribute>
						</xsl:if>							
					
						<label for="{$fieldName}"><xsl:value-of select="$i18n.Name" /></label>
						<xsl:call-template name="createTextField">
							<xsl:with-param name="id" select="$fieldName" />
							<xsl:with-param name="name" select="$fieldName" />
							<xsl:with-param name="title" select="$i18n.Name"/>
							<xsl:with-param name="size" select="50"/>
							<xsl:with-param name="value">
								<xsl:value-of select="ManualMultiSignQueryInstance/SigningParties/SigningParty/name" />							
							</xsl:with-param>
						</xsl:call-template>
						
						<xsl:apply-templates select="ValidationErrors/validationError[fieldName = $fieldName]"/>
						
					</div>
					
					<div class="split">
						
						<xsl:variable name="fieldName" select="concat($shortQueryID, '_email')" />
					
						<xsl:if test="ValidationErrors/validationError[fieldName = $fieldName]">
							<xsl:attribute name="class">
								<xsl:text>split invalid input-error</xsl:text>
							</xsl:attribute>
						</xsl:if>
					
						<label for="{$fieldName}"><xsl:value-of select="$i18n.Email" /></label>
						<xsl:call-template name="createTextField">
							<xsl:with-param name="id" select="$fieldName" />
							<xsl:with-param name="name" select="$fieldName" />
							<xsl:with-param name="title" select="$i18n.Email"/>
							<xsl:with-param name="size" select="50"/>
							<xsl:with-param name="value">
								<xsl:value-of select="ManualMultiSignQueryInstance/SigningParties/SigningParty/email" />							
							</xsl:with-param>
						</xsl:call-template>
						
						<xsl:apply-templates select="ValidationErrors/validationError[fieldName = $fieldName]"/>
						
					</div>
					
					<div class="split">
						
						<xsl:variable name="fieldName" select="concat($shortQueryID, '_socialSecurityNumber')" />
					
						<xsl:if test="ValidationErrors/validationError[fieldName = $fieldName]">
							<xsl:attribute name="class">
								<xsl:text>split invalid input-error</xsl:text>
							</xsl:attribute>
						</xsl:if>
					
						<label for="{$fieldName}"><xsl:value-of select="$i18n.SocialSecurityNumber" /></label>
						<xsl:call-template name="createTextField">
							<xsl:with-param name="id" select="$fieldName" />
							<xsl:with-param name="name" select="$fieldName" />
							<xsl:with-param name="title" select="$i18n.SocialSecurityNumber"/>
							<xsl:with-param name="size" select="50"/>
							<xsl:with-param name="value">
								<xsl:value-of select="ManualMultiSignQueryInstance/SigningParties/SigningParty/socialSecurityNumber" />							
							</xsl:with-param>
						</xsl:call-template>
						
						<xsl:apply-templates select="ValidationErrors/validationError[fieldName = $fieldName]"/>
						
					</div>
						
				</fieldset>
				
			</article>
		
		</div>
		
		<script type="text/javascript">$(document).ready(function(){initManualMultiSignQuery('<xsl:value-of select="ManualMultiSignQueryInstance/ManualMultiSignQuery/queryID" />');});</script>
		
	</xsl:template>
	
	<xsl:template match="validationError[validationErrorType = 'TooLong']">
		
		<span>
			<strong data-icon-before="!">
				<xsl:value-of select="$i18n.TooLongFieldContent.part1"/>
				<xsl:value-of select="currentLength"/>
				<xsl:value-of select="$i18n.TooLongFieldContent.part2"/>
				<xsl:value-of select="maxLength"/>
				<xsl:value-of select="$i18n.TooLongFieldContent.part3"/>			
			</strong>
		</span>
		
	</xsl:template>		
	
	<xsl:template match="validationError[messageKey = 'RequiredField']">
		
		<i data-icon-after="!" title="{$i18n.RequiredField}">AAAAAAAAAAAAAAAA</i>
		
	</xsl:template>
	
	<xsl:template match="validationError[validationErrorType = 'RequiredField']">
		
		<i data-icon-after="!" title="{$i18n.RequiredField}"></i>
		
	</xsl:template>
	
	<xsl:template match="validationError[validationErrorType = 'InvalidFormat']">
		
		<i data-icon-after="!" title="{$i18n.InvalidFormat}"></i>
		
	</xsl:template>		
	
	<xsl:template match="validationError">
		
		<span>
			<strong data-icon-before="!">
				<xsl:value-of select="$i18n.UnknownValidationError"/>
			</strong>
		</span>
		
	</xsl:template>	
		
</xsl:stylesheet>