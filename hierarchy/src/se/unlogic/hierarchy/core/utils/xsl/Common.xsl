<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	
	<xsl:template name="createTextField">
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
		
		<input type="{$type}">
		
			<xsl:if test="$id">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/> 
				</xsl:attribute>
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
	
	<xsl:template name="createHiddenField">
		<xsl:param name="id" select="null"/>
		<xsl:param name="name" select="null"/>
		<xsl:param name="class" select="null"/>
		<xsl:param name="disabled" select="null"/>
		<xsl:param name="value" select="''"/>
		<xsl:param name="element" select="null"/>
		<xsl:param name="requestparameters" select="requestparameters"/>
		
		<input type="hidden">
		
			<xsl:if test="$id">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/> 
				</xsl:attribute>
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
			
			<xsl:if test="$disabled">
				<xsl:attribute name="disabled">
					<xsl:value-of select="'true'"/> 
				</xsl:attribute>
			</xsl:if>
		
			<xsl:attribute name="value">
				<xsl:choose>
					<xsl:when test="$requestparameters">
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
			
		</input>		
	</xsl:template>
	
	<xsl:template name="createPasswordField">
		<xsl:param name="id" select="null"/>
		<xsl:param name="name" select="null"/>
		<xsl:param name="title" select="''"/>
		<xsl:param name="class" select="''"/>
		<xsl:param name="element" select="null"/>
		<xsl:param name="size" select="''"/>
		<xsl:param name="width" select="''"/>
		<xsl:param name="value" select="''"/>
		<xsl:param name="requestparameters" select="requestparameters"/>
		<xsl:param name="disabled" select="null"/>
		
		<input id="{$id}" type="password">
		
			<xsl:if test="$name">
				<xsl:attribute name="name">
					<xsl:value-of select="$name"/> 
				</xsl:attribute>
			</xsl:if>
		
			<xsl:choose>
				<xsl:when test="$size = '' and $width = ''">
					<xsl:attribute name="style">
						<xsl:value-of select="'width: 99%;'"/>
					</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:if test="$size != ''">
						<xsl:attribute name="size">
							<xsl:value-of select="$size"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="$width != ''">
						<xsl:attribute name="style">
							<xsl:text>width: </xsl:text><xsl:value-of select="$width"/><xsl:text>;</xsl:text>
						</xsl:attribute>
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
		
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
			
			<xsl:if test="$class != ''">
				<xsl:attribute name="class">
					<xsl:value-of select="$class"/>
				</xsl:attribute>	
			</xsl:if>
			
			<xsl:if test="$disabled">
				<xsl:attribute name="disabled">
					<xsl:value-of select="'true'"/> 
				</xsl:attribute>
			</xsl:if>			
			
		</input>		
	</xsl:template>	
	
	<xsl:template name="createDropdown">
		<xsl:param name="id" select="null"/>
		<xsl:param name="name" select="null"/>
		<xsl:param name="simple" select="null"/>
		<xsl:param name="class" select="null"/>
		<xsl:param name="rel" select="null"/>
		<xsl:param name="title" select="''"/>
		<xsl:param name="disabled" select="null"/>
		<xsl:param name="valueElementName" select="null"/>
		<xsl:param name="labelPrefix" select="null"/>
		<xsl:param name="labelElementName" select="null"/>
		<xsl:param name="labelElementName2" select="null"/>
		<xsl:param name="element" />
		<xsl:param name="selectedValue" select="''" />
		<xsl:param name="width" select="null"/>
		<xsl:param name="requestparameters" select="requestparameters"/>
		<xsl:param name="addEmptyOption" select="null"/>
		
		<select>
			<xsl:if test="$class">
				<xsl:attribute name="class">
					<xsl:value-of select="$class"/> 
				</xsl:attribute>
			</xsl:if>
			
			<xsl:if test="$rel">
				<xsl:attribute name="rel">
					<xsl:value-of select="$rel"/> 
				</xsl:attribute>
			</xsl:if>
		
			<xsl:if test="$name">
				<xsl:attribute name="name">
					<xsl:value-of select="$name"/> 
				</xsl:attribute>
			</xsl:if>
			
			<xsl:if test="$id">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/>
				</xsl:attribute>
			</xsl:if>
			
			<xsl:if test="$disabled">
				<xsl:attribute name="disabled">
					<xsl:value-of select="'true'"/>
				</xsl:attribute>
			</xsl:if>
				
			<xsl:if test="$width">
				<xsl:attribute name="style">
					<xsl:value-of select="concat('width: ',$width)"/>
				</xsl:attribute>
			</xsl:if>
			
			<xsl:if test="$addEmptyOption">
				<option value=""><xsl:value-of select="$addEmptyOption"/></option>
			</xsl:if>
			
			<xsl:choose>
				<xsl:when test="$simple">
				
					<xsl:for-each select="$element">
						<option value="{.}">
							<xsl:choose>
								<xsl:when test="$requestparameters">
									<xsl:if test="$requestparameters/parameter[name=$name]/value = .">
										<xsl:attribute name="selected"/>
									</xsl:if>
								</xsl:when>
								<xsl:otherwise>
									<xsl:if test=". = $selectedValue">
										<xsl:attribute name="selected" />
									</xsl:if>
								</xsl:otherwise>
							</xsl:choose>
							
							<xsl:if test="$labelPrefix">
								<xsl:value-of select="$labelPrefix"/>
							</xsl:if>
							
							<xsl:apply-templates select="."/>
						</option>
					</xsl:for-each>
										
				</xsl:when>
				<xsl:otherwise>
				
					<xsl:variable name="paramValue" select="$requestparameters/parameter[name=$name]/value"/>
				
					<xsl:for-each select="$element">
						<option value="{*[name()=$valueElementName]}">
							<xsl:choose>
								<xsl:when test="$requestparameters">
									<xsl:if test="$paramValue = *[local-name()=$valueElementName]">
										<xsl:attribute name="selected"/>
									</xsl:if>
								</xsl:when>
								<xsl:otherwise>
									<xsl:if test="*[name()=$valueElementName] = $selectedValue">
										<xsl:attribute name="selected" />
									</xsl:if>
								</xsl:otherwise>
							</xsl:choose>
							
							<xsl:if test="$labelPrefix">
								<xsl:value-of select="$labelPrefix"/>
							</xsl:if>
							
							<xsl:value-of select="*[name()=$labelElementName]" />
							
							<xsl:if test="$labelElementName2">
							
								<xsl:text>&#x20;</xsl:text>
								<xsl:value-of select="*[name()=$labelElementName2]" />
							</xsl:if>
							
						</option>
					</xsl:for-each>
					
				</xsl:otherwise>
			</xsl:choose>			
								
			<xsl:if test="$title != ''">			
				<xsl:attribute name="title" >
					<xsl:value-of select="$title"/>
				</xsl:attribute>
			</xsl:if>
			
		</select>
		
	</xsl:template>
	
	<xsl:template name="createMultipleDropdown">
		<xsl:param name="id" select="null"/>
		<xsl:param name="name" select="null"/>
		<xsl:param name="class" select="null"/>
		<xsl:param name="title" select="null"/>
		<xsl:param name="disabled" select="null"/>
		<xsl:param name="valueElementName" select="null"/>
		<xsl:param name="labelElementName" select="null"/>
		<xsl:param name="labelElementName2" select="null"/>
		<xsl:param name="element" />
		<xsl:param name="selectedElements" select="null" />
		<xsl:param name="selectedValues" select="null" />
		<xsl:param name="width" select="null"/>
		<xsl:param name="requestparameters" select="requestparameters"/>
		<xsl:param name="addEmptyOption" select="null"/>
		
		<select multiple="multiple">
			<xsl:if test="$class">
				<xsl:attribute name="class">
					<xsl:value-of select="$class"/> 
				</xsl:attribute>
			</xsl:if>
		
			<xsl:if test="$name">
				<xsl:attribute name="name">
					<xsl:value-of select="$name"/> 
				</xsl:attribute>
			</xsl:if>
			
			<xsl:if test="$id">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/>
				</xsl:attribute>
			</xsl:if>
			
			<xsl:if test="$disabled">
				<xsl:attribute name="disabled">
					<xsl:value-of select="'true'"/>
				</xsl:attribute>
			</xsl:if>
				
			<xsl:if test="$width">
				<xsl:attribute name="style">
					<xsl:value-of select="concat('width: ',$width)"/>
				</xsl:attribute>
			</xsl:if>
			
			<xsl:if test="$title">			
				<xsl:attribute name="title" >
					<xsl:value-of select="$title"/>
				</xsl:attribute>
			</xsl:if>
			
			<xsl:if test="$addEmptyOption">
				<option value=""><xsl:value-of select="$addEmptyOption"/></option>
			</xsl:if>
			
				
			<xsl:for-each select="$element">
				<xsl:variable name="currentValue" select="*[name()=$valueElementName]"/>
				<option value="{*[name()=$valueElementName]}">
					<xsl:choose>
						<xsl:when test="$requestparameters">
							<xsl:if test="$requestparameters/parameter[name=$name]/value = *[name()=$valueElementName]">
								<xsl:attribute name="selected"/>
							</xsl:if>
						</xsl:when>
						<xsl:when test="$selectedValues">
							<xsl:for-each select="$selectedValues">
								<xsl:if test=". = $currentValue">
									<xsl:attribute name="selected" />
								</xsl:if>
							</xsl:for-each>
						</xsl:when>
						<xsl:otherwise>
							<xsl:for-each select="$selectedElements">
								<xsl:if test="*[name()=$valueElementName] = $currentValue">
									<xsl:attribute name="selected" />
								</xsl:if>
							</xsl:for-each>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:value-of select="*[name()=$labelElementName]" />
							
					<xsl:if test="$labelElementName2">
					
						<xsl:text>&#x20;</xsl:text>
						<xsl:value-of select="*[name()=$labelElementName2]" />
					</xsl:if>
							
				</option>
			</xsl:for-each>
		</select>
		
	</xsl:template>
	
	<xsl:template name="createTextArea">
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
		
		<textarea rows="{$rows}">		
		
			<xsl:if test="$id">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/>
				</xsl:attribute>
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

	<xsl:template name="createCheckbox">
		<xsl:param name="id" select="null"/>
		<xsl:param name="name" select="null"/>
		<xsl:param name="elementName" select="$name"/>
		<xsl:param name="title" select="''"/>
		<xsl:param name="element" select="null"/>
		<xsl:param name="value" select="'true'"/>
		<xsl:param name="checked" select="null"/>
		<xsl:param name="requestparameters" select="requestparameters"/>
		<xsl:param name="disabled" select="null"/>
		<xsl:param name="onclick" select="null"/>
		<xsl:param name="class" select="''"/>
		
		<input type="checkbox" value="{$value}">
		
			<xsl:if test="$id">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/>
				</xsl:attribute>
			</xsl:if>
		
			<xsl:if test="$name">
				<xsl:attribute name="name">
					<xsl:value-of select="$name"/> 
				</xsl:attribute>
			</xsl:if>
			
			<xsl:if test="$onclick">
				<xsl:attribute name="onclick">
					<xsl:value-of select="$onclick"/>
				</xsl:attribute> 
			</xsl:if>
			
			<xsl:if test="$disabled">
				<xsl:attribute name="disabled">
					<xsl:value-of select="'true'"/>
				</xsl:attribute>
			</xsl:if>
			
			<xsl:if test="$class != ''">
				<xsl:attribute name="class">
					<xsl:value-of select="$class"/>
				</xsl:attribute>	
			</xsl:if>
		
			<xsl:choose>
				<xsl:when test="$requestparameters">
					<xsl:if test="$requestparameters/parameter[name=$name][value = $value]">
						<xsl:attribute name="checked"/>
					</xsl:if>
				</xsl:when>
				<xsl:when test="$element/*[name()=$elementName] = $value">
					<xsl:attribute name="checked"/>
				</xsl:when>
				<xsl:when test="$checked = 'true' and not($requestparameters) and not($element)">
					<xsl:attribute name="checked"/>
				</xsl:when>
			</xsl:choose>
			
			<xsl:if test="$title != ''">			
				<xsl:attribute name="title" >
					<xsl:value-of select="$title"/>
				</xsl:attribute>
			</xsl:if>
			
		</input>		
	</xsl:template>

	<xsl:template name="createRadio">
		
		<xsl:param name="name" select="null"/>
		<xsl:param name="elementName" select="$name"/>
		<xsl:param name="title" select="''"/>
		<xsl:param name="element" select="null"/>
		<xsl:param name="value" select="'true'"/>
		<xsl:param name="checked" select="null"/>
		<xsl:param name="id" select="null"/>
		<xsl:param name="onclick" select="null"/>
		<xsl:param name="requestparameters" select="requestparameters"/>
		<xsl:param name="disabled" select="null"/>
		<xsl:param name="class" select="''"/>
		
		<input type="radio" value="{$value}">
			
			<xsl:if test="$name">
				<xsl:attribute name="name">
					<xsl:value-of select="$name"/> 
				</xsl:attribute>
			</xsl:if>
			
			<xsl:if test="$id">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/>
				</xsl:attribute> 
			</xsl:if>

			<xsl:if test="$onclick">
				<xsl:attribute name="onclick">
					<xsl:value-of select="$onclick"/>
				</xsl:attribute> 
			</xsl:if>
			
			<xsl:if test="$disabled">
				<xsl:attribute name="disabled">
					<xsl:value-of select="'true'"/>
				</xsl:attribute>
			</xsl:if>
		
			<xsl:if test="$class != ''">
				<xsl:attribute name="class">
					<xsl:value-of select="$class"/>
				</xsl:attribute>	
			</xsl:if>
		
			<xsl:choose>
				<xsl:when test="$requestparameters">
					<xsl:if test="$requestparameters/parameter[name=$name][value = $value]">
						<xsl:attribute name="checked"/>
					</xsl:if>
				</xsl:when>
				<xsl:when test="$element/*[name()=$elementName] = $value">
					<xsl:attribute name="checked"/>
				</xsl:when>
				<xsl:when test="$checked and not($requestparameters) and not($element)">
					<xsl:attribute name="checked"/>
				</xsl:when>
			</xsl:choose>
			
			<xsl:if test="$title != ''">			
				<xsl:attribute name="title" >
					<xsl:value-of select="$title"/>
				</xsl:attribute>
			</xsl:if>
			
		</input>		
	</xsl:template>

	<xsl:template name="replace-string">
            <xsl:param name="text" />
            <xsl:param name="from" />
            <xsl:param name="to" />

            <xsl:choose>
                  <xsl:when test="contains($text, $from)">
                        <xsl:variable name="before" select="substring-before($text, $from)" />
                        <xsl:variable name="after" select="substring-after($text, $from)" />
                        <xsl:variable name="prefix" select="concat($before, $to)" />
                        
                        <xsl:value-of select="$before" />
                        <xsl:value-of select="$to" />

                        <xsl:call-template name="replace-string">
                              <xsl:with-param name="text" select="$after" />
                              <xsl:with-param name="from" select="$from" />
                              <xsl:with-param name="to" select="$to" />
                        </xsl:call-template>
                  </xsl:when>

                  <xsl:otherwise>
                        <xsl:value-of select="$text" />
                  </xsl:otherwise>
            </xsl:choose>
    </xsl:template>
    
    <xsl:template name="shorten-string">
        <xsl:param name="text" />
    	<xsl:param name="length" />
        <xsl:param name="padding" select="''" />
        
        <xsl:choose>
        	<xsl:when test="string-length($text) &gt; $length">
        		<xsl:value-of select="concat(substring($text,0,$length+1),$padding)" />
        	</xsl:when>
        	<xsl:otherwise>
        		<xsl:value-of select="$text"/>
        	</xsl:otherwise>
        </xsl:choose>
    </xsl:template>
      
	<xsl:template name="replaceLineBreak">
	    <xsl:param name="string"/>
	    <xsl:choose>
	        <xsl:when test="contains($string,'&#13;')">
	            <xsl:value-of select="substring-before($string,'&#13;')"/>
            	<br/>
	            <xsl:call-template name="replaceLineBreak">
	                <xsl:with-param name="string" select="substring-after($string,'&#13;')"/>
	            </xsl:call-template>
	        </xsl:when>
	        <xsl:otherwise>
	            <xsl:value-of select="$string"/>
	        </xsl:otherwise>
	    </xsl:choose>
	</xsl:template>
	
	<xsl:template name="removeLineBreak">
	    <xsl:param name="string"/>
	    <xsl:choose>
	        <xsl:when test="contains($string,'&#13;')">
	            <xsl:value-of select="substring-before($string,'&#13;')"/>
	            <xsl:call-template name="removeLineBreak">
	                <xsl:with-param name="string" select="substring-after($string,'&#13;')"/>
	            </xsl:call-template>
	        </xsl:when>
	        <xsl:otherwise>
	            <xsl:value-of select="$string"/>
	        </xsl:otherwise>
	    </xsl:choose>
	</xsl:template>
	
	<xsl:template name="createdBy">
		<xsl:param name="user"/>
		<xsl:param name="userNotFoundPhrase"/>
		<xsl:param name="showUsername" select="'false'"/>
		<xsl:choose>
			<xsl:when test="not($user)">
				<xsl:value-of select="$userNotFoundPhrase"/>
			</xsl:when>
			<xsl:when test="$showUsername = 'false'">
				<xsl:value-of select="$user/firstname"/>
				<xsl:text>&#x20;</xsl:text>
				<xsl:value-of select="$user/lastname"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$user/username"/>
			</xsl:otherwise>
		</xsl:choose>	
	</xsl:template>
	
	<xsl:template name="toLowerCase">
	
		<xsl:param name="string" />
		<xsl:param name="start" select="null"/>
		
		<xsl:choose>
			<xsl:when test="$start > 1">
				<xsl:variable name="index" select="$start - 1"/>
				<xsl:value-of select="concat(substring($string,1,$index),translate(substring($string,$start), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'))" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="translate($string, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')" />
			</xsl:otherwise>
		</xsl:choose>
	
	</xsl:template>
	
	<xsl:template name="toUpperCase">
	
		<xsl:param name="string" />
		<xsl:param name="start" select="null"/>
		
		<xsl:choose>
			<xsl:when test="$start > 1">
				<xsl:variable name="index" select="$start - 1"/>
				<xsl:value-of select="concat(substring($string,1,$index),translate(substring($string,$start), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'))" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="translate($string, 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')" />
			</xsl:otherwise>
		</xsl:choose>
	
	</xsl:template>
	
	
	<xsl:template name="createDateField">
		<xsl:param name="id" />
		<xsl:param name="name" />
		<xsl:param name="class"/>
		<xsl:param name="requestparameters" select="requestparameters"/>
		<xsl:param name="timestamp" select="''"/>
		<xsl:param name="size" select="'10'"/>
		<xsl:param name="icon" select="null"/>
		<xsl:param name="timestampID" select="null"/>
		<xsl:param name="disabled" select="null"/>
		<xsl:variable name="newclass">
			<xsl:choose>
				<xsl:when test="$timestampID">
					<xsl:value-of select="concat($class,concat(' timestamp timestampID_',$timestampID))" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$class"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="createTextField">
			<xsl:with-param name="name" select="$name"/>
			<xsl:with-param name="class" select="$newclass"/>
			<xsl:with-param name="id" select="$id"/>
			<xsl:with-param name="disabled" select="$disabled"/>
			<xsl:with-param name="value" select="substring($timestamp,1,10)"/>
			<xsl:with-param name="size" select="$size"/>
			<xsl:with-param name="requestparameters" select="$requestparameters"/>
		</xsl:call-template>
		<xsl:if test="$icon">
			<xsl:text>&#x20;</xsl:text>
			<img id="{$id}_icon" class="alignbottom" src="{$icon}">
				<xsl:attribute name="class">
				<xsl:choose>
					<xsl:when test="$disabled">
						alignbottom
					</xsl:when>
					<xsl:otherwise>
						alignbottom pointer active
					</xsl:otherwise>
				</xsl:choose>
				</xsl:attribute>
			</img>
		</xsl:if>
		<xsl:if test="$timestampID">
			<xsl:call-template name="createHiddenField">
				<xsl:with-param name="id" select="$timestampID"/>
				<xsl:with-param name="name" select="$timestampID"/>
				<xsl:with-param name="disabled" select="$disabled"/>
				<xsl:with-param name="value" select="$timestamp"/>
				<xsl:with-param name="requestparameters" select="$requestparameters"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="createHourDropdown">
		<xsl:param name="id" select="null"/>
		<xsl:param name="name"/>
		<xsl:param name="element"/>
		<xsl:param name="class"/>
		<xsl:param name="disabled" select="null"/>
		<xsl:param name="valueElementName"/>
		<xsl:param name="labelElementName"/>
		<xsl:param name="requestparameters" select="requestparameters"/>
		<xsl:param name="timestamp" select="''"/>
		<xsl:param name="timestampID" select="null"/>
		<xsl:variable name="newclass">
			<xsl:choose>
				<xsl:when test="$timestampID">
					<xsl:value-of select="concat($class,concat(' timestamp timestampID_',$timestampID))" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$class"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="createDropdown">
			<xsl:with-param name="name" select="$name"/>
			<xsl:with-param name="class" select="$newclass"/>
			<xsl:with-param name="id" select="$id"/>
			<xsl:with-param name="disabled" select="$disabled"/>
			<xsl:with-param name="element" select="$element"/>
			<xsl:with-param name="valueElementName" select="$valueElementName"/>
			<xsl:with-param name="labelElementName" select="$labelElementName"/>
			<xsl:with-param name="selectedValue" select="substring($timestamp,12,2)"/>
			<xsl:with-param name="requestparameters" select="$requestparameters"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="createMinuteDropdown">
		<xsl:param name="id" select="null"/>
		<xsl:param name="name"/>
		<xsl:param name="element"/>
		<xsl:param name="class"/>
		<xsl:param name="disabled" select="null"/>
		<xsl:param name="valueElementName"/>
		<xsl:param name="labelElementName"/>
		<xsl:param name="requestparameters" select="requestparameters"/>
		<xsl:param name="timestamp" select="''"/>
		<xsl:param name="timestampID" select="null"/>
		<xsl:variable name="newclass">
			<xsl:choose>
				<xsl:when test="$timestampID">
					<xsl:value-of select="concat($class,concat(' timestamp timestampID_',$timestampID))" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$class"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="createDropdown">
			<xsl:with-param name="name" select="$name"/>
			<xsl:with-param name="class" select="$newclass"/>
			<xsl:with-param name="id" select="$id"/>
			<xsl:with-param name="disabled" select="$disabled"/>
			<xsl:with-param name="element" select="$element"/>
			<xsl:with-param name="valueElementName" select="$valueElementName"/>
			<xsl:with-param name="labelElementName" select="$labelElementName"/>
			<xsl:with-param name="selectedValue" select="substring($timestamp,15,2)"/>
			<xsl:with-param name="requestparameters" select="$requestparameters"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="createSecondDropdown">
		<xsl:param name="id"/>
		<xsl:param name="name"/>
		<xsl:param name="element"/>
		<xsl:param name="class"/>
		<xsl:param name="disabled" select="null"/>
		<xsl:param name="valueElementName"/>
		<xsl:param name="labelElementName"/>
		<xsl:param name="requestparameters" select="requestparameters"/>
		<xsl:param name="timestamp" select="''"/>
		<xsl:param name="timestampID" select="null"/>
		<xsl:variable name="newclass">
			<xsl:choose>
				<xsl:when test="$timestampID">
					<xsl:value-of select="concat($class,concat(' timestamp timestampID_',$timestampID))" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$class"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="createDropdown">
			<xsl:with-param name="name" select="$name"/>
			<xsl:with-param name="class" select="$newclass"/>
			<xsl:with-param name="id" select="$id"/>
			<xsl:with-param name="disabled" select="$disabled"/>
			<xsl:with-param name="element" select="$element"/>
			<xsl:with-param name="valueElementName" select="$valueElementName"/>
			<xsl:with-param name="labelElementName" select="$labelElementName"/>
			<xsl:with-param name="selectedValue" select="substring($timestamp,18,2)"/>
			<xsl:with-param name="requestparameters" select="$requestparameters"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="replaceEscapedLineBreak">

            <xsl:param name="text" />
            <xsl:variable name="from" select="'&#13;'"/>
            <xsl:variable name="to" select="''"/>

            <xsl:choose>
                  <xsl:when test="contains($text, $from)">
                        <xsl:variable name="before" select="substring-before($text, $from)" />
                        <xsl:variable name="after" select="substring-after($text, $from)" />
                        <xsl:variable name="prefix" select="concat($before, $to)" />
                        
                        <xsl:value-of select="$before" />
                        <xsl:value-of select="$to" />

                        <xsl:call-template name="replaceEscapedLineBreak">
                              <xsl:with-param name="text" select="$after" />
                        </xsl:call-template>
                  </xsl:when>

                  <xsl:otherwise>
                        <xsl:value-of select="$text" />
                  </xsl:otherwise>
            </xsl:choose>
      </xsl:template>
      
      <xsl:template name="replaceLineBreaksWithParagraph">
		    <xsl:param name="string"/>
		    <xsl:choose>
		        <xsl:when test="contains($string,'&#13;')">
		            <p><xsl:value-of select="substring-before($string,'&#13;')" /></p>
		            <xsl:call-template name="replaceLineBreaksWithParagraph">
		                <xsl:with-param name="string" select="substring-after($string,'&#13;')"/>
		            </xsl:call-template>
		        </xsl:when>
		        <xsl:otherwise>
		            <p><xsl:value-of select="$string"/></p>
		        </xsl:otherwise>
		    </xsl:choose>
	</xsl:template>
     
	<xsl:template name="replaceLineBreaksAndLinks">
	    <xsl:param name="string"/>
	    <xsl:choose>
	        <xsl:when test="contains($string,'&#13;')">
	            <xsl:call-template name="createHyperlinks">
	            	<xsl:with-param name="string" select="substring-before($string,'&#13;')"/>
	            </xsl:call-template>	        
            	<br/>
	            <xsl:call-template name="replaceLineBreaksAndLinks">
	                <xsl:with-param name="string" select="substring-after($string,'&#13;')"/>
	            </xsl:call-template>
	        </xsl:when>
	        <xsl:otherwise>
	            <xsl:call-template name="createHyperlinks">
	            	<xsl:with-param name="string" select="$string"/>
	            </xsl:call-template>
	        </xsl:otherwise>
	    </xsl:choose>
	</xsl:template>	
	
	<xsl:template name="createHyperlinks">
		<xsl:param name="string" />

		<xsl:variable name="http">
			<xsl:choose>
				<xsl:when test="contains($string, 'ftp://')">
					<xsl:text>ftp://</xsl:text>
				</xsl:when>			
				<xsl:when test="contains($string, 'http://')">
					<xsl:text>http://</xsl:text>
				</xsl:when>
				<xsl:when test="contains($string, 'https://')">
					<xsl:text>https://</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>false</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="$http = 'false'">
				<!-- No links, output string -->
				<xsl:value-of select="$string" />
			</xsl:when>
			<xsl:otherwise>
				<!-- Links detected, replace them -->

				<!-- Set up variables -->
				<xsl:variable name="before" select="substring-before($string, $http)" />
				<xsl:variable name="after" select="substring-after($string, $http)" />
				<xsl:variable name="url" select="concat($http, substring-before($after,' '))" />
				<xsl:variable name="rest" select="substring-after($string, $url)" />

				<!-- Output the text -->
				<xsl:value-of select="$before" />

				<xsl:choose>
					<!-- If the url is at then end, $rest doesn't work -->
					<xsl:when test="substring-after($url,$http) != ''">
						<a href="{$url}" rel="nofollow">
							<xsl:value-of select="$url" />
						</a>
						<!-- Check the remainder for links -->
						<xsl:call-template name="createHyperlinks">
							<xsl:with-param name="string" select="$rest" />
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<a href="{$url}{$after}" rel="nofollow">
							<xsl:value-of select="$url" />
							<xsl:value-of select="$after" />
						</a>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>	     
     
</xsl:stylesheet>