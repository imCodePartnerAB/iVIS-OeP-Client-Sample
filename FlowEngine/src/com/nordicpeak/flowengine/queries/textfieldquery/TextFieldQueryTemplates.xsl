<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/Common.xsl"/>

	<xsl:variable name="globalscripts">
		/jquery/jquery.js
	</xsl:variable>

	<xsl:variable name="scripts">
		/js/textfieldquery.js
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
							<xsl:if test="TextFieldQueryInstance/QueryInstanceDescriptor/queryState = 'VISIBLE_REQUIRED'">
								<xsl:text>required</xsl:text>
							</xsl:if>
						</xsl:attribute>
						<xsl:value-of select="TextFieldQueryInstance/QueryInstanceDescriptor/QueryDescriptor/name"/>
					</h2>
					
					<xsl:call-template name="createUpdateButton">
						<xsl:with-param name="queryID" select="TextFieldQueryInstance/TextFieldQuery/queryID" />
					</xsl:call-template>
					
					<xsl:if test="TextFieldQueryInstance/TextFieldQuery/description">
						<span class="italic">
							<xsl:if test="/Document/useCKEditorForDescription = 'true'"><xsl:attribute name="class">italic html-description</xsl:attribute></xsl:if>
							<xsl:value-of select="TextFieldQueryInstance/TextFieldQuery/description" disable-output-escaping="yes" />
						</span>		
					</xsl:if>
					
				</div>
				
				<fieldset>
					<xsl:apply-templates select="TextFieldQueryInstance/TextFieldQuery/Fields/TextField" mode="show"/>
				</fieldset>
				
			</article>
			
		</div>
		
	</xsl:template>		
		
	<xsl:template match="ShowQueryForm">
	
		<xsl:variable name="queryID" select="concat('query_', TextFieldQueryInstance/TextFieldQuery/queryID)" />
	
		<div class="query" id="{$queryID}">
		
			<xsl:if test="EnableAjaxPosting">
				<xsl:attribute name="class">query enableAjaxPosting</xsl:attribute>
			</xsl:if>
			
			<xsl:if test="ValidationErrors/validationError">
				<div id="{$queryID}-validationerror" class="validationerrors" />
			</xsl:if>
	
			<article>
			
				<div class="heading-wrapper">
					
					<h2>
						<xsl:attribute name="class">
							<xsl:if test="TextFieldQueryInstance/QueryInstanceDescriptor/queryState = 'VISIBLE_REQUIRED'">
								<xsl:text>required</xsl:text>
							</xsl:if>
						</xsl:attribute>
						<xsl:value-of select="TextFieldQueryInstance/QueryInstanceDescriptor/QueryDescriptor/name"/>
					</h2>
					
					<xsl:if test="TextFieldQueryInstance/TextFieldQuery/helpText">		
						<xsl:apply-templates select="TextFieldQueryInstance/TextFieldQuery/helpText" />
					</xsl:if>
					
					<xsl:if test="TextFieldQueryInstance/TextFieldQuery/description">
						<span class="italic">
							<xsl:if test="/Document/useCKEditorForDescription = 'true'"><xsl:attribute name="class">italic html-description</xsl:attribute></xsl:if>
							<xsl:value-of select="TextFieldQueryInstance/TextFieldQuery/description" disable-output-escaping="yes" />
						</span>		
					</xsl:if>
				
				</div>
				
				<fieldset>		
					<xsl:apply-templates select="TextFieldQueryInstance/TextFieldQuery/Fields/TextField" />
				</fieldset>
		
			</article>
			
		</div>
		
		<script type="text/javascript">$(document).ready(function(){initTextFieldQuery('<xsl:value-of select="TextFieldQueryInstance/TextFieldQuery/queryID" />');});</script>
		
	</xsl:template>
	
	<xsl:template match="TextField">

		<xsl:variable name="labelFieldID" select="labelFieldID"/>

		<xsl:variable name="class">
			<xsl:if test="../../../../ValidationErrors/validationError[fieldName = $labelFieldID]">
				<xsl:text>invalid input-error</xsl:text>
			</xsl:if>
		</xsl:variable>

		<div class="split break {$class}">
			
			<xsl:if test="../../layout = 'FLOAT'">
				<xsl:attribute name="class">
					<xsl:text>split </xsl:text> 
					<xsl:value-of select="$class" />
				</xsl:attribute>
			</xsl:if>
			
			<label>
				<xsl:if test="required = 'true'">
					<xsl:attribute name="class">required</xsl:attribute>
				</xsl:if>
				<xsl:value-of select="label"/>
			</label>
	
			<xsl:variable name="size">
				<xsl:choose>
					<xsl:when test="width">
						<xsl:value-of select="width"/>
					</xsl:when>
					<xsl:otherwise>
						<!-- Default size, maybe a future module setting? -->
						<xsl:text>50</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
	
			<xsl:call-template name="createTextField">
				<xsl:with-param name="name">
					<xsl:value-of select="'q'"/>
					<xsl:value-of select="../../queryID"/>
					<xsl:value-of select="'_field'"/>
					<xsl:value-of select="textFieldID"/>
				</xsl:with-param>
				<xsl:with-param name="title" select="label"/>
				<xsl:with-param name="value" select="../../../Values/TextFieldValue[TextField/textFieldID = $textFieldID]/value"/>
				<xsl:with-param name="requestparameters" select="../../../../requestparameters"/>
				<xsl:with-param name="size" select="$size"/>
				<xsl:with-param name="class" select="$class"/>
			</xsl:call-template>
			
			<xsl:apply-templates select="../../../../ValidationErrors/validationError[fieldName = $textFieldID]"/>
		
		</div>	
	
	</xsl:template>
	
	<xsl:template match="TextField" mode="show">

		<xsl:variable name="odd" select="(position() mod 2) = 0" />

		<div>
			<xsl:if test="../../layout = 'FLOAT'">
				<xsl:attribute name="class">
					<xsl:text>split</xsl:text>
					<xsl:if test="$odd"> odd</xsl:if>
				</xsl:attribute>
			</xsl:if>
			<strong class="block">
				<xsl:value-of select="label"/>
				<xsl:if test="required = 'true'">
					<xsl:text> *</xsl:text>
				</xsl:if>
			</strong>
			
			<xsl:variable name="textFieldID" select="textFieldID"/>
			<xsl:variable name="value" select="../../../Values/TextFieldValue[TextField/textFieldID = $textFieldID]/value"/>
			
			<xsl:choose>
				<xsl:when test="$value">
					<xsl:value-of select="$value"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>-</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			
		</div>
	
	</xsl:template>	
	
	<xsl:template match="validationError[validationErrorType = 'RequiredField']">
		
		<i data-icon-after="!" title="{$i18n.RequiredField}"></i>
		
	</xsl:template>	
	
	<xsl:template match="validationError[validationErrorType = 'TooLong']">
		
		<i data-icon-after="!">
			<xsl:attribute name="title">
				<xsl:value-of select="$i18n.TooLongFieldContent.part1"/>
				<xsl:value-of select="currentLength"/>
				<xsl:value-of select="$i18n.TooLongFieldContent.part2"/>
				<xsl:value-of select="maxLength"/>
				<xsl:value-of select="$i18n.TooLongFieldContent.part3"/>
			</xsl:attribute>
		</i>
		
	</xsl:template>
	
	<xsl:template match="validationError[validationErrorType = 'InvalidFormat']">
		
		<i data-icon-after="!">
			<xsl:attribute name="title">
				<xsl:choose>
					<xsl:when test="invalidFormatMessage">
						<xsl:value-of select="invalidFormatMessage"/>
					</xsl:when>
					<xsl:otherwise>
							<xsl:value-of select="$i18n.InvalidFormat"/>				
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
		</i>
		
	</xsl:template>	
		
	<xsl:template match="validationError">
		
		<i data-icon-after="!" title="{$i18n.UnknownValidationError}"></i>
		
	</xsl:template>
	
</xsl:stylesheet>