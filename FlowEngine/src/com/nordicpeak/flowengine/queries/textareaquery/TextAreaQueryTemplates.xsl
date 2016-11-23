<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/Common.xsl"/>

	<xsl:variable name="globalscripts">
		/jquery/jquery.js
	</xsl:variable>

	<xsl:variable name="scripts">
		/js/textareaquery.js
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
							<xsl:if test="TextAreaQueryInstance/QueryInstanceDescriptor/queryState = 'VISIBLE_REQUIRED'">
								<xsl:text>required</xsl:text>
							</xsl:if>
						</xsl:attribute>
						<xsl:value-of select="TextAreaQueryInstance/QueryInstanceDescriptor/QueryDescriptor/name"/>
					</h2>
					
					<xsl:call-template name="createUpdateButton">
						<xsl:with-param name="queryID" select="TextAreaQueryInstance/TextAreaQuery/queryID" />
					</xsl:call-template>
					
					<xsl:if test="TextAreaQueryInstance/TextAreaQuery/description">
						<span class="italic">
							<xsl:value-of select="TextAreaQueryInstance/TextAreaQuery/description" disable-output-escaping="yes" />
						</span>		
					</xsl:if>
					
				</div>
				
				<xsl:call-template name="replaceLineBreaksWithParagraph">
					<xsl:with-param name="string" select="TextAreaQueryInstance/value"/>
				</xsl:call-template>
				
			</article>
				
		</div>
		
	</xsl:template>	
		
	<xsl:template match="ShowQueryForm">
	
		<xsl:variable name="queryID" select="concat('query_', TextAreaQueryInstance/TextAreaQuery/queryID)" />
	
		<div class="query" id="{$queryID}">
			
			<xsl:if test="EnableAjaxPosting">
				<xsl:attribute name="class">query enableAjaxPosting</xsl:attribute>
			</xsl:if>
			
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
							<xsl:if test="TextAreaQueryInstance/QueryInstanceDescriptor/queryState = 'VISIBLE_REQUIRED'">
								<xsl:text>required</xsl:text>
							</xsl:if>
						</xsl:attribute>
						<xsl:value-of select="TextAreaQueryInstance/QueryInstanceDescriptor/QueryDescriptor/name"/>
					</h2>
					
					<xsl:if test="TextAreaQueryInstance/TextAreaQuery/helpText">		
						<xsl:apply-templates select="TextAreaQueryInstance/TextAreaQuery/helpText" />
					</xsl:if>
					
					<xsl:if test="TextAreaQueryInstance/TextAreaQuery/description">
						<span class="italic">
							<xsl:if test="/Document/useCKEditorForDescription = 'true'"><xsl:attribute name="class">italic html-description</xsl:attribute></xsl:if>
							<xsl:value-of select="TextAreaQueryInstance/TextAreaQuery/description" disable-output-escaping="yes" />
						</span>		
					</xsl:if>
				
				</div>
				
				<xsl:call-template name="createTextArea">
					<xsl:with-param name="name">
						<xsl:value-of select="'q'"/>
						<xsl:value-of select="TextAreaQueryInstance/TextAreaQuery/queryID"/>
						<xsl:value-of select="'_value'"/>
					</xsl:with-param>
					<xsl:with-param name="title" select="TextAreaQueryInstance/TextAreaQuery/name"/>
					<xsl:with-param name="value" select="TextAreaQueryInstance/value"/>
					<xsl:with-param name="width" select="'98%'"/>
				</xsl:call-template>
				
			</article>			
		
		</div>
		
	</xsl:template>
	
	<xsl:template match="validationError[messageKey = 'TooLongFieldContent']">
		
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
		
		<span>
			<strong data-icon-before="!">
				<xsl:value-of select="$i18n.RequiredField"/>
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