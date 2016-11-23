<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/Common.xsl"/>

	<xsl:variable name="globalscripts">
		/jquery/jquery.js
	</xsl:variable>

	<xsl:variable name="scripts">
		/js/radiobuttonquery.js
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
							<xsl:if test="RadioButtonQueryInstance/QueryInstanceDescriptor/queryState = 'VISIBLE_REQUIRED'">
								<xsl:text>required</xsl:text>
							</xsl:if>
						</xsl:attribute>
						<xsl:value-of select="RadioButtonQueryInstance/QueryInstanceDescriptor/QueryDescriptor/name"/>
					</h2>
					
					<xsl:call-template name="createUpdateButton">
						<xsl:with-param name="queryID" select="RadioButtonQueryInstance/RadioButtonQuery/queryID" />
					</xsl:call-template>
					
					<xsl:if test="RadioButtonQueryInstance/RadioButtonQuery/description">
						<span class="italic">
							<xsl:if test="/Document/useCKEditorForDescription = 'true'"><xsl:attribute name="class">italic html-description</xsl:attribute></xsl:if>
							<xsl:value-of select="RadioButtonQueryInstance/RadioButtonQuery/description" disable-output-escaping="yes" />
						</span>		
					</xsl:if>
					
				</div>
				
				<xsl:choose>
					<xsl:when test="RadioButtonQueryInstance/RadioButtonAlternative">
						<xsl:value-of select="RadioButtonQueryInstance/RadioButtonAlternative/name"/>
					</xsl:when>
					<xsl:when test="RadioButtonQueryInstance/freeTextAlternative">
						<xsl:value-of select="RadioButtonQueryInstance/freeTextAlternative"/>
					</xsl:when>
				</xsl:choose>
				
			</article>
		
		</div>		
	
	</xsl:template>
		
	<xsl:template match="ShowQueryForm">
	
		<xsl:variable name="queryID" select="concat('query_', RadioButtonQueryInstance/RadioButtonQuery/queryID)" />
	
		<div class="query" id="{$queryID}">
		
			<xsl:attribute name="class">
				<xsl:text>query</xsl:text>
				<xsl:if test="RadioButtonQueryInstance/RadioButtonQuery/freeTextAlternative">
					<xsl:text> hasFreeTextAlternative</xsl:text>
				</xsl:if>
				<xsl:if test="EnableAjaxPosting"><xsl:text> enableAjaxPosting</xsl:text></xsl:if>
			</xsl:attribute>
		
			<a name="{$queryID}" />
		
			<xsl:if test="ValidationErrors/validationError">
				<div id="{$queryID}-validationerrors" class="validationerrors">
					<div class="info-box error">
						<xsl:apply-templates select="ValidationErrors/validationError"/>
						<div class="marker"></div>
					</div>
				</div>
			</xsl:if>
	
			<article>
			
				<xsl:if test="ValidationErrors/validationError">
					<xsl:attribute name="class">error</xsl:attribute>
				</xsl:if>
			
				<div class="heading-wrapper">
					
					<h2>
						<xsl:attribute name="class">
							<xsl:if test="RadioButtonQueryInstance/QueryInstanceDescriptor/queryState = 'VISIBLE_REQUIRED'">
								<xsl:text>required</xsl:text>
							</xsl:if>
						</xsl:attribute>
						<xsl:value-of select="RadioButtonQueryInstance/QueryInstanceDescriptor/QueryDescriptor/name"/>
					</h2>
					
					<xsl:if test="RadioButtonQueryInstance/RadioButtonQuery/helpText">		
						<xsl:apply-templates select="RadioButtonQueryInstance/RadioButtonQuery/helpText" />
					</xsl:if>
					
					<xsl:if test="RadioButtonQueryInstance/RadioButtonQuery/description">
						<span class="italic">
							<xsl:if test="/Document/useCKEditorForDescription = 'true'"><xsl:attribute name="class">italic html-description</xsl:attribute></xsl:if>
							<xsl:value-of select="RadioButtonQueryInstance/RadioButtonQuery/description" disable-output-escaping="yes" />
						</span>		
					</xsl:if>
				
				</div>
				
				<xsl:apply-templates select="RadioButtonQueryInstance/RadioButtonQuery/Alternatives/RadioButtonAlternative" mode="form"/>
		
				<xsl:if test="RadioButtonQueryInstance/RadioButtonQuery/freeTextAlternative">
				
					<xsl:variable name="freeTextAlternativeName">
						<xsl:value-of select="'q'"/>
						<xsl:value-of select="RadioButtonQueryInstance/RadioButtonQuery/queryID"/>
						<xsl:value-of select="'_alternative'"/>
					</xsl:variable>
				
					<div class="alternative">
				
						<xsl:call-template name="createRadio">
							<xsl:with-param name="id" select="concat($freeTextAlternativeName, '_freeTextAlternative')" />
							<xsl:with-param name="name" select="$freeTextAlternativeName" />
							<xsl:with-param name="class" select="'freeTextAlternative'" />
							<xsl:with-param name="value" select="'freeTextAlternative'" />
							<xsl:with-param name="checked" select="RadioButtonQueryInstance/freeTextAlternative" />
						</xsl:call-template>
						<xsl:text>&#x20;</xsl:text>	
						<label for="{concat($freeTextAlternativeName, '_freeTextAlternative')}" class="radio"><xsl:value-of select="RadioButtonQueryInstance/RadioButtonQuery/freeTextAlternative" /></label>
				
					</div>
				
					<div class="freeTextAlternative hidden">
						<xsl:call-template name="createTextField">
							<xsl:with-param name="id" select="concat($freeTextAlternativeName,'Value')" />
							<xsl:with-param name="name" select="concat($freeTextAlternativeName,'Value')" />
							<xsl:with-param name="value" select="RadioButtonQueryInstance/freeTextAlternative" />
							<xsl:with-param name="disabled" select="disabled" />
						</xsl:call-template>
					</div>
					
				</xsl:if>
				
			</article>
		
		</div>
		
		<script type="text/javascript">$(document).ready(function(){initRadioButtonQuery('<xsl:value-of select="RadioButtonQueryInstance/RadioButtonQuery/queryID" />');});</script>
		
	</xsl:template>
	
	<xsl:template match="RadioButtonAlternative" mode="form">
	
		<div class="alternative">

			<xsl:variable name="radioID">
				<xsl:value-of select="'q'"/>
				<xsl:value-of select="../../queryID"/>
				<xsl:value-of select="'_alternative'"/>
				<xsl:value-of select="alternativeID" />
			</xsl:variable>
		
			<xsl:variable name="alternativeID" select="alternativeID"/>
		
			<xsl:call-template name="createRadio">
				<xsl:with-param name="id" select="$radioID" />
				<xsl:with-param name="name">
					<xsl:value-of select="'q'"/>
					<xsl:value-of select="../../queryID"/>
					<xsl:value-of select="'_alternative'"/>
				</xsl:with-param>
				<xsl:with-param name="value" select="alternativeID"/>
				<xsl:with-param name="elementName" select="'alternativeID'" />
				<xsl:with-param name="element" select="../../../RadioButtonAlternative[alternativeID = $alternativeID]" />
				<xsl:with-param name="requestparameters" select="../../../../requestparameters"/>
			</xsl:call-template>
			<label for="{$radioID}" class="radio"><xsl:value-of select="name" /></label>
				
		</div>
	
	</xsl:template>
	
	<xsl:template match="validationError[messageKey = 'RequiredQuery']">
		
		<span>
			<strong data-icon-before="!">
			<xsl:value-of select="$i18n.RequiredQuery"/>
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