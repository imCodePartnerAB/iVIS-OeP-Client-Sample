<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="../../core/utils/xsl/Common.xsl"/>
	
	<xsl:template match="Document">
		
		<div class="contentitem">
			<xsl:apply-templates select="List"/>
			<xsl:apply-templates select="AddDataSource"/>
			<xsl:apply-templates select="UpdateDataSource"/>
		</div>

	</xsl:template>

	<xsl:template match="List">
	
		<h1><xsl:value-of select="/Document/module/name"/></h1>
	
		<xsl:apply-templates select="validationError"/>
	
		<xsl:apply-templates select="datasource"/>

		<div class="floatright">
			<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/add" title="{$i18n.addDataSourceLink}">
				<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/database_add.png"/><xsl:value-of select="$i18n.addDataSourceLink"/>
			</a>
		</div>

	</xsl:template>

	<xsl:template match="datasource">

		<div class="floatleft full marginbottom border">
			<div class="floatleft">
	
				<xsl:choose>
					<xsl:when test="@cached='true' and @db='true'">
						<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/stop/{dataSourceID}" title="{$i18n.stopDataSource}">
							<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/database_check.png"/>
						</a>
					</xsl:when>
					<xsl:when test="@cached='true' and @db='false'">
						<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/stop/{dataSourceID}" title="{$i18n.stopDataSourceNotInDB}">
							<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/database_check_varning.png"/>
						</a>
					</xsl:when>
					<xsl:when test="enabled='false'">
						<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/database_disabled.png" title="{$i18n.disabledDataSource}"/>
					</xsl:when>													
					<xsl:otherwise>
						<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/start/{dataSourceID}" title="{$i18n.cacheDataSource}">
							<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/database.png"/>
						</a>
					</xsl:otherwise>
				</xsl:choose>

				<xsl:text>&#x20;</xsl:text>

				<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/update/{dataSourceID}" title="{$i18n.editDataSource}: {name}">
					<xsl:value-of select="name"/>		
				</a>		
			</div>
			<div class="floatright marginright">
				<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/copy/{dataSourceID}" title="{$i18n.copyDataSource}: {name}">
					<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/database_copy.png"/>
				</a>			
				<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/update/{dataSourceID}" title="{$i18n.editDataSource}: {name}">
					<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/database_edit.png"/>
				</a>
				<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/delete/{dataSourceID}" onclick="javascript:return confirm('{$i18n.deleteDataSource}: {name}?')" title="{$i18n.deleteDataSource}: {name}?">
					<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/database_delete.png"/>
				</a>
			</div>				
		</div>
	</xsl:template>	
	
	<xsl:template match="AddDataSource">

		<script 
			type="text/javascript"
			src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/dsadmin.js">
		</script>	

		<h1><xsl:value-of select="$i18n.AddDataSource.title"/><xsl:text> </xsl:text><xsl:value-of select="datasource/name"/></h1>

		<xsl:apply-templates select="validationException/validationError"/>

		<form method="POST" action="{/Document/requestinfo/uri}">
			<table class="full">
				<tbody>
					<tr>
						<td><xsl:value-of select="$i18n.name"/>:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'name'"/>
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.type"/>:</td>
						<td>
							<xsl:call-template name="createRadio">
								<xsl:with-param name="name" select="'type'"/>
								<xsl:with-param name="value" select="'SystemManaged'"/>
								<xsl:with-param name="checked" select="'true'"/>
								<xsl:with-param name="id" select="'radiobutton'"/>
								<xsl:with-param name="onclick" select="'hideShowParamTable()'"/>
							</xsl:call-template>
							<xsl:value-of select="$i18n.type.SystemManaged"/>
							<br/>
							<xsl:call-template name="createRadio">
								<xsl:with-param name="name" select="'type'"/>
								<xsl:with-param name="value" select="'ContainerManaged'"/>
								<xsl:with-param name="onclick" select="'hideShowParamTable()'"/>							
							</xsl:call-template>
							<xsl:value-of select="$i18n.type.ContainerManaged"/>						
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.enabled"/>:</td>
						<td>
							<xsl:call-template name="createCheckbox">
								<xsl:with-param name="name" select="'enabled'"/>
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.url"/>:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'url'"/>
							</xsl:call-template>
						</td>
					</tr>
				</tbody>
				<tbody id="subtable">				
					<xsl:if test="requestparameters/parameter[name='type']/value='ContainerManaged'">
						<xsl:attribute name="style">display:none;</xsl:attribute>
					</xsl:if>
				
					<tr>
						<td><xsl:value-of select="$i18n.driver"/><img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/info.png" title="{$i18n.driverInfo}"/>:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'driver'"/>
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.username"/>:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'username'"/>
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.password"/>:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'password'"/>
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.defaultCatalog"/>:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'defaultCatalog'"/>
							</xsl:call-template>
						</td>
					</tr>					
					<tr>
						<td><xsl:value-of select="$i18n.logAbandoned"/><img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/info.png" title="{$i18n.logAbandonedInfo}"/>:</td>
						<td>
							<xsl:call-template name="createCheckbox">
								<xsl:with-param name="name" select="'logAbandoned'"/>
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.removeAbandoned"/><img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/info.png" title="{$i18n.removeAbandonedInfo}"/>:</td>
						<td>
							<xsl:call-template name="createCheckbox">
								<xsl:with-param name="name" select="'removeAbandoned'"/>
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.removeTimeout"/><img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/info.png" title="{$i18n.removeAbandonedTimeoutInfo}"/>:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'removeTimeout'"/>
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.testOnBorrow"/><img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/info.png" title="{$i18n.testOnBorrowInfo}"/>:</td>
						<td>
							<xsl:call-template name="createCheckbox">
								<xsl:with-param name="name" select="'testOnBorrow'"/>
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.validationQuery"/>:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'validationQuery'"/>
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.maxActive"/><img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/info.png" title="{$i18n.maxActiveInfo}"/>:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'maxActive'"/>
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.maxIdle"/><img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/info.png" title="{$i18n.maxIdleInfo}"/>:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'maxIdle'"/>
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.minIdle"/><img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/info.png" title="{$i18n.minIdleInfo}"/>:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'minIdle'"/>
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.maxWait"/><img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/info.png" title="{$i18n.maxWaitInfo}"/>:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'maxWait'"/>
							</xsl:call-template>
						</td>
					</tr>
				</tbody>			
			</table>			

			<br/>

			<div class="floatright">
				<input type="submit" value="{$i18n.add}"/>
			</div>
		</form>		
		
	</xsl:template>	
	
	<xsl:template match="UpdateDataSource">
		
		<script 
			type="text/javascript"
			src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/dsadmin.js">
		</script>			
		
		<h1><xsl:value-of select="$i18n.UpdateDataSource.title"/><xsl:text> </xsl:text><xsl:value-of select="datasource/name"/></h1>
		
		<xsl:apply-templates select="validationException/validationError"/>
		
		<form method="POST" action="{/Document/requestinfo/uri}">
			<table class="full">
				<tbody>
					<tr>
						<td><xsl:value-of select="$i18n.name"/>:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'name'"/>
								<xsl:with-param name="element" select="datasource"/>
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.type"/>:</td>
						<td>
							<xsl:call-template name="createRadio">
								<xsl:with-param name="name" select="'type'"/>
								<xsl:with-param name="value" select="'SystemManaged'"/>
								<xsl:with-param name="element" select="datasource"/>
								<xsl:with-param name="id" select="'radiobutton'"/>
								<xsl:with-param name="onclick" select="'hideShowParamTable()'"/>
							</xsl:call-template>
							<xsl:value-of select="$i18n.type.SystemManaged"/>
							<br/>
							<xsl:call-template name="createRadio">
								<xsl:with-param name="name" select="'type'"/>
								<xsl:with-param name="value" select="'ContainerManaged'"/>
								<xsl:with-param name="element" select="datasource"/>
								<xsl:with-param name="onclick" select="'hideShowParamTable()'"/>
							</xsl:call-template>
							<xsl:value-of select="$i18n.type.ContainerManaged"/>						
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.enabled"/>:</td>
						<td>
							<xsl:call-template name="createCheckbox">
								<xsl:with-param name="name" select="'enabled'"/>
								<xsl:with-param name="element" select="datasource"/>
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.url"/>:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'url'"/>
								<xsl:with-param name="element" select="datasource"/>
							</xsl:call-template>
						</td>
					</tr>
				</tbody>
				<tbody id="subtable">
				
					<xsl:choose>
						<xsl:when test="requestparameters">
							<xsl:if test="requestparameters/parameter[name='type']/value='ContainerManaged'">
								<xsl:attribute name="style">display:none;</xsl:attribute>
							</xsl:if>
						</xsl:when>
						<xsl:otherwise>
							<xsl:if test="datasource/type='ContainerManaged'">
								<xsl:attribute name="style">display:none;</xsl:attribute>
							</xsl:if>
						</xsl:otherwise>
					</xsl:choose>
				
					<tr>
						<td><xsl:value-of select="$i18n.driver"/><img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/info.png" title="{$i18n.driverInfo}"/>:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'driver'"/>
								<xsl:with-param name="element" select="datasource"/>
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.username"/>:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'username'"/>
								<xsl:with-param name="element" select="datasource"/>
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.password"/>:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'password'"/>
								<xsl:with-param name="element" select="datasource"/>
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.defaultCatalog"/>:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'defaultCatalog'"/>
								<xsl:with-param name="element" select="datasource"/>
							</xsl:call-template>
						</td>
					</tr>					
					<tr>
						<td><xsl:value-of select="$i18n.logAbandoned"/><img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/info.png" title="{$i18n.logAbandonedInfo}"/>:</td>
						<td>
							<xsl:call-template name="createCheckbox">
								<xsl:with-param name="name" select="'logAbandoned'"/>
								<xsl:with-param name="element" select="datasource"/>
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.removeAbandoned"/><img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/info.png" title="{$i18n.removeAbandonedInfo}"/>:</td>
						<td>
							<xsl:call-template name="createCheckbox">
								<xsl:with-param name="name" select="'removeAbandoned'"/>
								<xsl:with-param name="element" select="datasource"/>
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.removeTimeout"/><img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/info.png" title="{$i18n.removeAbandonedTimeoutInfo}"/>:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'removeTimeout'"/>
								<xsl:with-param name="element" select="datasource"/>
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.testOnBorrow"/><img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/info.png" title="{$i18n.testOnBorrowInfo}"/>:</td>
						<td>
							<xsl:call-template name="createCheckbox">
								<xsl:with-param name="name" select="'testOnBorrow'"/>
								<xsl:with-param name="element" select="datasource"/>
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.validationQuery"/>:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'validationQuery'"/>
								<xsl:with-param name="element" select="datasource"/>
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.maxActive"/><img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/info.png" title="{$i18n.maxActiveInfo}"/>:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'maxActive'"/>
								<xsl:with-param name="element" select="datasource"/>
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.maxIdle"/><img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/info.png" title="{$i18n.maxIdleInfo}"/>:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'maxIdle'"/>
								<xsl:with-param name="element" select="datasource"/>
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.minIdle"/><img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/info.png" title="{$i18n.minIdleInfo}"/>:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'minIdle'"/>
								<xsl:with-param name="element" select="datasource"/>
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td><xsl:value-of select="$i18n.maxWait"/><img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/info.png" title="{$i18n.maxWaitInfo}"/>:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'maxWait'"/>
								<xsl:with-param name="element" select="datasource"/>
							</xsl:call-template>
						</td>
					</tr>
				</tbody>
			</table>
	
			<br/>
	
			<div class="floatright">
				<input type="submit" value="{$i18n.saveChanges}"/>
			</div>
		</form>		
		
	</xsl:template>
	
	<xsl:template match="validationError">
		<xsl:if test="fieldName and validationErrorType and not(messageKey)">
			<p class="error">
				<xsl:choose>
					<xsl:when test="validationErrorType='RequiredField'">
						<xsl:value-of select="$i18n.validationError.requiredField"/>
					</xsl:when>
					<xsl:when test="validationErrorType='InvalidFormat'">
						<xsl:value-of select="$i18n.validationError.invalidFormat"/>
					</xsl:when>
					<xsl:when test="validationErrorType='TooShort'">
						<xsl:value-of select="$i18n.validationError.tooShort" />
					</xsl:when>
					<xsl:when test="validationErrorType='TooLong'">
						<xsl:value-of select="$i18n.validationError.tooLong" />
					</xsl:when>		
					<xsl:otherwise>
						<xsl:value-of select="$i18n.validationError.unknownError"/>
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:text>&#x20;</xsl:text>
				
				<xsl:choose>
					<xsl:when test="fieldName = 'url'">
						<xsl:value-of select="$i18n.url" />
					</xsl:when>
					<xsl:when test="fieldName = 'type'">
						<xsl:value-of select="$i18n.type" />
					</xsl:when>
					<xsl:when test="fieldName = 'driver'">
						<xsl:value-of select="$i18n.driver" />
					</xsl:when>
					<xsl:when test="fieldName = 'username'">
						<xsl:value-of select="$i18n.username" />
					</xsl:when>
					<xsl:when test="fieldName = 'password'">
						<xsl:value-of select="$i18n.password" />
					</xsl:when>
					<xsl:when test="fieldName = 'maxActive'">
						<xsl:value-of select="$i18n.maxActive" />
					</xsl:when>
					<xsl:when test="fieldName = 'maxIdle'">
						<xsl:value-of select="$i18n.maxIdle" />
					</xsl:when>
					<xsl:when test="fieldName = 'minIdle'">
						<xsl:value-of select="$i18n.minIdle" />
					</xsl:when>
					<xsl:when test="fieldName = 'maxWait'">
						<xsl:value-of select="$i18n.maxWait" />
					</xsl:when>
					<xsl:when test="fieldName = 'removeTimeout'">
						<xsl:value-of select="$i18n.removeTimeout" />
					</xsl:when>
					<xsl:when test="fieldName = 'validationQuery'">
						<xsl:value-of select="$i18n.validationQuery" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="fieldName" />
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:text>!</xsl:text>		
			</p>
		</xsl:if>
		
		<xsl:if test="messageKey">
			<p class="error">
				<xsl:choose>
					<xsl:when test="messageKey='UpdateFailedDataSourceNotFound'">
						<xsl:value-of select="$i18n.validationError.message.UpdateFailedDataSourceNotFound"/>
					</xsl:when>
					<xsl:when test="messageKey='DeleteFailedDataSourceNotFound'">
						<xsl:value-of select="$i18n.validationError.message.DeleteFailedDataSourceNotFound"/>
					</xsl:when>
					<xsl:when test="messageKey='CopyFailedDataSourceNotFound'">
						<xsl:value-of select="$i18n.validationError.message.CopyFailedDataSourceNotFound"/>
					</xsl:when>					
					<xsl:when test="messageKey='driverNotAvailable'">
						<xsl:value-of select="$i18n.validationError.message.DriverNotAvailable"/>
					</xsl:when>
					<xsl:when test="messageKey='nameNotBound'">
						<xsl:value-of select="$i18n.validationError.message.NameNotBound"/>
					</xsl:when>
					<xsl:when test="messageKey='datasourceNotStarted'">
						<xsl:value-of select="$i18n.validationError.message.DataSourceCannotStart"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$i18n.validationError.message.unknownFault"/>
					</xsl:otherwise>
				</xsl:choose>
			</p>
		</xsl:if>
	</xsl:template>		
	
</xsl:stylesheet>	