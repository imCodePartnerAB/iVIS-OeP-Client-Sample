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

				<!--<h1>Begin</h1>-->
				<fieldset>
					<xsl:apply-templates select="TextFieldQueryInstance/TextFieldQuery/Fields/TextField" />
				</fieldset>
				<!--<h1>End</h1>-->

			</article>
			
		</div>
		
		<script type="text/javascript">$(document).ready(function(){initTextFieldQuery('<xsl:value-of select="TextFieldQueryInstance/TextFieldQuery/queryID" />');});</script>
		
	</xsl:template>
	
	<xsl:template match="TextField">

		<xsl:variable name="textFieldID" select="textFieldID"/>

		<xsl:variable name="class">
			<xsl:if test="../../../../ValidationErrors/validationError[fieldName = $textFieldID]">
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
	
			<xsl:call-template name="createDepandendTextField">
				<xsl:with-param name="name">
					<xsl:value-of select="'q'"/>
					<xsl:value-of select="../../queryID"/>
					<xsl:value-of select="'_field'"/>
					<xsl:value-of select="textFieldID"/>
				</xsl:with-param>
				<xsl:with-param name="type" select="'hidden'"/>
				<xsl:with-param name="title" select="label"/>
				<xsl:with-param name="value" select="../../../Values/TextFieldValue[TextField/textFieldID = $textFieldID]/value"/>
				<xsl:with-param name="requestparameters" select="../../../../requestparameters"/>
				<xsl:with-param name="size" select="$size"/>
				<xsl:with-param name="class" select="$class"/>

				<xsl:with-param name="dependsOn" select="dependsOn"/>
				<xsl:with-param name="dependencySourceName" select="dependencySourceName"/>
				<xsl:with-param name="dependencyFieldName" select="dependencyFieldName"/>
			</xsl:call-template>

			<xsl:call-template name="createDivField">
				<!--<xsl:with-param name="name">-->
					<!--<xsl:value-of select="'q'"/>-->
					<!--<xsl:value-of select="../../queryID"/>-->
					<!--<xsl:value-of select="'_field'"/>-->
					<!--<xsl:value-of select="textFieldID"/>-->
				<!--</xsl:with-param>-->
				<!--<xsl:with-param name="type" select="hidden"/>-->
				<xsl:with-param name="title" select="label"/>
				<xsl:with-param name="value" select="../../../Values/TextFieldValue[TextField/textFieldID = $textFieldID]/value"/>
				<xsl:with-param name="requestparameters" select="../../../../requestparameters"/>
				<xsl:with-param name="size" select="$size"/>
				<xsl:with-param name="class" select="$class"/>

				<xsl:with-param name="dependsOn" select="dependsOn"/>
				<xsl:with-param name="dependencySourceName" select="dependencySourceName"/>
				<xsl:with-param name="dependencyFieldName" select="dependencyFieldName"/>
			</xsl:call-template>


			<xsl:apply-templates select="../../../../ValidationErrors/validationError[fieldName = $textFieldID]"/>
		
		</div>	
	
	</xsl:template>

	<xsl:template name="createDivField">
		<xsl:param name="id" select="null"/>
		<xsl:param name="name" select="null"/>
		<xsl:param name="type" select="'text'"/>
		<xsl:param name="class" select="null"/>
		<xsl:param name="disabled" select="null"/>
		<xsl:param name="maxlength" select="null"/>
		<xsl:param name="readonly" select="null"/>
		<xsl:param name="title" select="''"/>
		<xsl:param name="placeholder" select="null"/>
		<xsl:param name="value" select="''"/>
		<xsl:param name="element" select="null"/>
		<xsl:param name="size" select="''"/>
		<xsl:param name="min" select="''"/>
		<xsl:param name="max" select="''"/>
		<xsl:param name="width" select="'99%'"/>
		<xsl:param name="requestparameters" select="requestparameters"/>
		<xsl:param name="dependsOn" select="'false'"/>
		<xsl:param name="dependencySourceName" select="''"/>
		<xsl:param name="dependencyFieldName" select="''"/>
		<p class="p1">
			<span class="s1">
				<xsl:if test="$dependsOn = 'true'">
					<xsl:if test="$dependencySourceName">
						<xsl:attribute name="data-dependency-source">
							<xsl:value-of select="$dependencySourceName"/>
						</xsl:attribute>
					</xsl:if>

					<xsl:if test="$dependencyFieldName">
						<xsl:attribute name="data-dependency-field">
							<xsl:value-of select="$dependencyFieldName"/>
						</xsl:attribute>
					</xsl:if>
				</xsl:if>

				<!--<xsl:choose>-->
					<!--<xsl:when test="$requestparameters">-->
						<!--<xsl:value-of select="$requestparameters/parameter[name=$name]/value"/>-->
					<!--</xsl:when>-->
					<!--<xsl:when test="$element/*[name()=$name]">-->
						<!--<xsl:value-of select="$element/*[name()=$name]"/>-->
					<!--</xsl:when>-->
					<!--<xsl:otherwise>-->
						<xsl:value-of select="$value"/>
					<!--</xsl:otherwise>-->
				<!--</xsl:choose>-->
			</span>
		</p>
		
	</xsl:template>
	<xsl:template name="createDepandendTextField">
		<xsl:param name="id" select="null"/>
		<xsl:param name="name" select="null"/>
		<xsl:param name="type" select="'text'"/>
		<xsl:param name="class" select="null"/>
		<xsl:param name="disabled" select="null"/>
		<xsl:param name="maxlength" select="null"/>
		<xsl:param name="readonly" select="null"/>
		<xsl:param name="title" select="''"/>
		<xsl:param name="placeholder" select="null"/>
		<xsl:param name="value" select="''"/>
		<xsl:param name="element" select="null"/>
		<xsl:param name="size" select="''"/>
		<xsl:param name="min" select="''"/>
		<xsl:param name="max" select="''"/>
		<xsl:param name="width" select="'99%'"/>
		<xsl:param name="requestparameters" select="requestparameters"/>
		<xsl:param name="dependsOn" select="'false'"/>
		<xsl:param name="dependencySourceName" select="''"/>
		<xsl:param name="dependencyFieldName" select="''"/>

		<input type="{$type}">

			<xsl:if test="$id">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/>
				</xsl:attribute>
			</xsl:if>

			<xsl:if test="$dependsOn = 'true'">
				<xsl:if test="$dependencySourceName">
					<xsl:attribute name="data-dependency-source">
						<xsl:value-of select="$dependencySourceName"/>
					</xsl:attribute>
				</xsl:if>

				<xsl:if test="$dependencyFieldName">
					<xsl:attribute name="data-dependency-field">
						<xsl:value-of select="$dependencyFieldName"/>
					</xsl:attribute>
				</xsl:if>
			</xsl:if>

			<xsl:if test="$name">
				<xsl:attribute name="name">
					<xsl:value-of select="$name"/>
				</xsl:attribute>
			</xsl:if>

			<xsl:if test="$class">
				<xsl:attribute name="class">
					<xsl:value-of select="$class"/>
				</xsl:attribute>
			</xsl:if>

			<xsl:if test="$disabled != ''">
				<xsl:attribute name="disabled">
					<xsl:value-of select="'true'"/>
				</xsl:attribute>
			</xsl:if>

			<xsl:if test="$maxlength">
				<xsl:attribute name="maxlength">
					<xsl:value-of select="$maxlength"/>
				</xsl:attribute>
			</xsl:if>

			<xsl:if test="$readonly">
				<xsl:attribute name="readonly">
					<xsl:value-of select="'true'"/>
				</xsl:attribute>
			</xsl:if>

			<xsl:if test="$size != ''">
				<xsl:attribute name="size">
					<xsl:value-of select="$size"/>
				</xsl:attribute>
			</xsl:if>

			<xsl:if test="$size = '' and $width != ''">
				<xsl:attribute name="style">
					<xsl:text>width: </xsl:text><xsl:value-of select="$width"/><xsl:text>;</xsl:text>
				</xsl:attribute>
			</xsl:if>

			<xsl:if test="$type = 'number' and $min != ''">
				<xsl:attribute name="min">
					<xsl:value-of select="$min"/>
				</xsl:attribute>
			</xsl:if>

			<xsl:if test="$type = 'number' and $max != ''">
				<xsl:attribute name="max">
					<xsl:value-of select="$max"/>
				</xsl:attribute>
			</xsl:if>

			<xsl:attribute name="value">
				<xsl:choose>
					<xsl:when test="not($disabled) and $requestparameters">
						<xsl:value-of select="$requestparameters/parameter[name=$name]/value"/>
					</xsl:when>
					<xsl:when test="$element/*[name()=$name]">
						<xsl:value-of select="$element/*[name()=$name]"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$value"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>

			<xsl:if test="$title != ''">
				<xsl:attribute name="title" >
					<xsl:value-of select="$title"/>
				</xsl:attribute>
			</xsl:if>

			<xsl:if test="$placeholder">
				<xsl:attribute name="placeholder" >
					<xsl:value-of select="$placeholder"/>
				</xsl:attribute>
			</xsl:if>

		</input>
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