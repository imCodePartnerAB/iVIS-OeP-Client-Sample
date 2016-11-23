<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/Common.xsl"/>

	<xsl:variable name="globalscripts">
		/jquery/jquery.js
	</xsl:variable>

	<xsl:variable name="scripts">
		/js/dropdownquery.js
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
							<xsl:if test="LinkedDropDownQueryInstance/QueryInstanceDescriptor/queryState = 'VISIBLE_REQUIRED'">
								<xsl:text>required</xsl:text>
							</xsl:if>
							<xsl:if test="LinkedDropDownQueryInstance/LinkedDropDownQuery/description">
								<xsl:text> hasDescription</xsl:text>
							</xsl:if>
						</xsl:attribute>
						<xsl:value-of select="LinkedDropDownQueryInstance/QueryInstanceDescriptor/QueryDescriptor/name"/>
					</h2>
					
					<xsl:call-template name="createUpdateButton">
						<xsl:with-param name="queryID" select="LinkedDropDownQueryInstance/LinkedDropDownQuery/queryID" />
					</xsl:call-template>
					
					<xsl:if test="LinkedDropDownQueryInstance/LinkedDropDownQuery/description">
						<span class="italic">
							<xsl:if test="/Document/useCKEditorForDescription = 'true'"><xsl:attribute name="class">italic html-description</xsl:attribute></xsl:if>
							<xsl:value-of select="LinkedDropDownQueryInstance/LinkedDropDownQuery/description" disable-output-escaping="yes" />
						</span>		
					</xsl:if>
					
				</div>
				
				<xsl:choose>
						<xsl:when test="LinkedDropDownQueryInstance/LinkedDropDownAlternative">
							<xsl:value-of select="LinkedDropDownQueryInstance/LinkedDropDownAlternative/name"/>
						</xsl:when>
						<!--<xsl:when test="LinkedDropDownQueryInstance/entityClassname">-->
							<!--<xsl:value-of select="LinkedDropDownQueryInstance/entityClassname"/>-->
						<!--</xsl:when>-->
					</xsl:choose>
				
			</article>
		
		</div>	
	
	</xsl:template>		
		
	<xsl:template match="ShowQueryForm">

		<xsl:variable name="queryID" select="concat('query_', LinkedDropDownQueryInstance/LinkedDropDownQuery/queryID)" />
	
		<div class="query" id="{$queryID}">
	
			<xsl:attribute name="class">
				<xsl:text>query</xsl:text>
				<xsl:if test="LinkedDropDownQueryInstance/LinkedDropDownQuery/freeTextAlternative">
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
							<xsl:if test="LinkedDropDownQueryInstance/QueryInstanceDescriptor/queryState = 'VISIBLE_REQUIRED'">
								<xsl:text>required</xsl:text>
							</xsl:if>
						</xsl:attribute>
						<xsl:value-of select="LinkedDropDownQueryInstance/QueryInstanceDescriptor/QueryDescriptor/name"/>
					</h2>
					
					<xsl:if test="LinkedDropDownQueryInstance/LinkedDropDownQuery/helpText">
						<xsl:apply-templates select="LinkedDropDownQueryInstance/LinkedDropDownQuery/helpText" />
					</xsl:if>
					
					<xsl:if test="LinkedDropDownQueryInstance/LinkedDropDownQuery/description">
						<span class="italic">
							<xsl:if test="/Document/useCKEditorForDescription = 'true'"><xsl:attribute name="class">italic html-description</xsl:attribute></xsl:if>
							<xsl:value-of select="LinkedDropDownQueryInstance/LinkedDropDownQuery/description" disable-output-escaping="yes" />
						</span>		
					</xsl:if>
				
				</div>
				
				<fieldset>
					
					<xsl:variable name="dropDownName">
						<xsl:value-of select="'q'"/>
						<xsl:value-of select="LinkedDropDownQueryInstance/LinkedDropDownQuery/queryID"/>
						<xsl:value-of select="'_alternative'"/>
					</xsl:variable>

					<div class="split">
						<script type="text/javascript"> var dataSource<xsl:value-of select="LinkedDropDownQueryInstance/LinkedDropDownQuery/queryID" /> = <xsl:value-of select="LinkedDropDownQueryInstance/LinkedDropDownQuery/entities" />;</script>

						<xsl:variable name="selectedAlternativeID" select="LinkedDropDownQueryInstance/LinkedDropDownAlternative/alternativeID" />
					
						<select name="{$dropDownName}">

							<xsl:if test="LinkedDropDownQueryInstance/QueryInstanceDescriptor/QueryDescriptor/exported = 'true'">
								<xsl:if test="LinkedDropDownQueryInstance/QueryInstanceDescriptor/QueryDescriptor/xsdElementName">
									<xsl:attribute name="data-element-name">
										<xsl:value-of select="LinkedDropDownQueryInstance/QueryInstanceDescriptor/QueryDescriptor/xsdElementName"/>
									</xsl:attribute>
								</xsl:if>
							</xsl:if>
							
							<option value=""><xsl:value-of select="LinkedDropDownQueryInstance/LinkedDropDownQuery/shortDescription"/></option>
							
							<xsl:for-each select="LinkedDropDownQueryInstance/LinkedDropDownQuery/Alternatives/LinkedDropDownAlternative">
								<option value="{alternativeID}">
									<xsl:choose>
										<xsl:when test="requestparameters">
											<xsl:if test="requestparameters/parameter[name=$dropDownName]/value = alternativeID">
												<xsl:attribute name="selected"/>
											</xsl:if>
										</xsl:when>
										<xsl:otherwise>
											<xsl:if test="$selectedAlternativeID = alternativeID">
												<xsl:attribute name="selected" />
											</xsl:if>
										</xsl:otherwise>
									</xsl:choose>
									
									<xsl:value-of select="name" />
									
								</option>
							</xsl:for-each>
	
							<!--<xsl:if test="LinkedDropDownQueryInstance/LinkedDropDownQuery/freeTextAlternative">-->
							<!-- -->
								<!--<option value="freeTextAlternative">-->
									<!-- -->
									<!--<xsl:choose>-->
										<!--<xsl:when test="requestparameters">-->
											<!--<xsl:if test="requestparameters/parameter[name=$dropDownName]/value = 'freeTextAlternative'">-->
												<!--<xsl:attribute name="selected"/>-->
											<!--</xsl:if>-->
										<!--</xsl:when>-->
										<!--<xsl:otherwise>-->
											<!--<xsl:if test="LinkedDropDownQueryInstance/entityClassname">-->
												<!--<xsl:attribute name="selected" />-->
											<!--</xsl:if>-->
										<!--</xsl:otherwise>-->
									<!--</xsl:choose>	-->
										<!-- -->
									<!--<xsl:value-of select="LinkedDropDownQueryInstance/LinkedDropDownQuery/freeTextAlternative" />-->
																<!-- -->
								<!--</option>-->
							<!-- -->
							<!--</xsl:if>-->
							
						</select>
			
					</div>
			
					<!--<div class="entityClassname hidden">-->
						<!--<xsl:call-template name="createTextField">-->
							<!--<xsl:with-param name="id" select="concat($dropDownName,'Value')" />-->
							<!--<xsl:with-param name="name" select="concat($dropDownName,'Value')" />-->
							<!--<xsl:with-param name="value" select="LinkedDropDownQueryInstance/entityClassname" />-->
							<!--<xsl:with-param name="disabled" select="disabled" />-->
						<!--</xsl:call-template>-->
					<!--</div>-->
				
				</fieldset>
				
			</article>
	
		</div>
		<!--<H1>1<xsl:value-of select="LinkedDropDownQueryInstance/MutableQueryInstanceDescriptor/flowInstanceID" /></H1>-->
		<!--<H1>2<xsl:value-of select="LinkedDropDownQueryInstance/QueryInstanceDescriptor/flowInstanceID" /></H1>-->
		<!--<H1>3<xsl:value-of select="LinkedDropDownQueryInstance/queryInstanceDescriptor/flowInstanceID" /></H1>-->
		<xsl:variable name="flowInstanceID">
			<xsl:choose>
				<xsl:when test="LinkedDropDownQueryInstance/QueryInstanceDescriptor/flowInstanceID">
					<xsl:value-of select="LinkedDropDownQueryInstance/QueryInstanceDescriptor/flowInstanceID"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'null'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!--<H1>4-->
			<!--<xsl:choose>-->
				<!--<xsl:when test="LinkedDropDownQueryInstance/QueryInstanceDescriptor/flowInstanceID">-->
					<!--<xsl:value-of select="LinkedDropDownQueryInstance/QueryInstanceDescriptor/flowInstanceID"/>-->
				<!--</xsl:when>-->
				<!--<xsl:otherwise>-->
					<!--null-->
				<!--</xsl:otherwise>-->
			<!--</xsl:choose>-->
		<!--</H1>-->
		<script type="text/javascript">$(document).ready(function(){initDropDownQuery('<xsl:value-of select="LinkedDropDownQueryInstance/LinkedDropDownQuery/queryID" />', '<xsl:value-of select="$flowInstanceID" />');});</script>
		
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