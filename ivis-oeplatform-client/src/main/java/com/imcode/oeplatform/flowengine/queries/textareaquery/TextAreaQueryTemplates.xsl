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
				
				<xsl:call-template name="createDepandendTextArea">
					<xsl:with-param name="name">
						<xsl:value-of select="'q'"/>
						<xsl:value-of select="TextAreaQueryInstance/TextAreaQuery/queryID"/>
						<xsl:value-of select="'_value'"/>
					</xsl:with-param>
					<xsl:with-param name="title" select="TextAreaQueryInstance/TextAreaQuery/name"/>
					<xsl:with-param name="value" select="TextAreaQueryInstance/value"/>
					<xsl:with-param name="width" select="'98%'"/>

					<xsl:with-param name="dependsOn" select="TextAreaQueryInstance/TextAreaQuery/dependsOn"/>
					<xsl:with-param name="dependencySourceName" select="TextAreaQueryInstance/TextAreaQuery/dependencySourceName"/>
					<xsl:with-param name="dependencyFieldName" select="TextAreaQueryInstance/TextAreaQuery/dependencyFieldName"/>
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

	<xsl:template name="createDepandendTextArea">
		<xsl:param name="id" select="null"/>
		<xsl:param name="name" select="null"/>
		<xsl:param name="title" select="''"/>
		<xsl:param name="disabled" select="null"/>
		<xsl:param name="maxlength" select="null"/>
		<xsl:param name="readonly" select="null"/>
		<xsl:param name="onclick" select="null"/>
		<xsl:param name="onfocus" select="null"/>
		<xsl:param name="class" select="''"/>
		<xsl:param name="element" select="null"/>
		<xsl:param name="separateListValues" select="null"/>
		<xsl:param name="value" select="null"/>
		<xsl:param name="placeholder" select="null"/>
		<xsl:param name="rows" select="10"/>
		<xsl:param name="cols" select="''" />
		<xsl:param name="width" select="'100%'" />
		<xsl:param name="requestparameters" select="requestparameters"/>
		<xsl:param name="dependsOn" select="'false'"/>
		<xsl:param name="dependencySourceName" select="''"/>
		<xsl:param name="dependencyFieldName" select="''"/>


		<textarea rows="{$rows}">

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

			<xsl:if test="$disabled">
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

			<xsl:if test="$onclick">
				<xsl:attribute name="onclick">
					<xsl:value-of select="$onclick"/>
				</xsl:attribute>
			</xsl:if>

			<xsl:if test="$onfocus">
				<xsl:attribute name="onfocus">
					<xsl:value-of select="$onfocus"/>
				</xsl:attribute>
			</xsl:if>

			<xsl:if test="$class != ''">
				<xsl:attribute name="class">
					<xsl:value-of select="$class"/>
				</xsl:attribute>
			</xsl:if>

			<xsl:if test="$width != ''">
				<xsl:attribute name="style">
					<xsl:value-of select="concat('width: ',$width)"/>
				</xsl:attribute>
			</xsl:if>

			<xsl:if test="$placeholder">
				<xsl:attribute name="placeholder">
					<xsl:value-of select="$placeholder"/>
				</xsl:attribute>
			</xsl:if>

			<xsl:if test="$cols != ''">
				<xsl:attribute name="cols">
					<xsl:value-of select="$cols" />
				</xsl:attribute>
			</xsl:if>

			<xsl:choose>
				<xsl:when test="$requestparameters">
					<xsl:call-template name="replace-string">
						<xsl:with-param name="text" select="$requestparameters/parameter[name=$name]/value"/>
						<xsl:with-param name="from" select="'&#13;'"/>
						<xsl:with-param name="to" select="''"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="$element and $separateListValues">
					<xsl:for-each select="$element">
						<xsl:value-of select="."/>
						<xsl:if test="not(position() = last())">
							<xsl:text>&#13;</xsl:text>
						</xsl:if>
					</xsl:for-each>
				</xsl:when>
				<xsl:when test="$element/*[name()=$name]">
					<xsl:call-template name="replace-string">
						<xsl:with-param name="text" select="$element/*[name()=$name]"/>
						<xsl:with-param name="from" select="'&#13;'"/>
						<xsl:with-param name="to" select="''"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$value"/>
				</xsl:otherwise>
			</xsl:choose>

			<xsl:if test="$title != ''">
				<xsl:attribute name="title" >
					<xsl:value-of select="$title"/>
				</xsl:attribute>
			</xsl:if>

		</textarea>

	</xsl:template>


</xsl:stylesheet>