<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="../../core/utils/xsl/Common.xsl"/>

	<xsl:template match="Document">
	
		<xsl:choose>
			<xsl:when test="List">
				<xsl:apply-templates select="List"/>
			</xsl:when>
			<xsl:otherwise>
				<div class="contentitem">			
					<xsl:apply-templates select="AddServer"/>
					<xsl:apply-templates select="UpdateServer"/>
					<xsl:apply-templates select="UpdateServerDrive"/>
				</div>
			</xsl:otherwise>
		</xsl:choose>			
	</xsl:template>
		
	<xsl:template match="List">
				
		<xsl:choose>
			<xsl:when test="Servers">
				<xsl:apply-templates select="Servers/Server"/>
			</xsl:when>
			<xsl:otherwise>
				<div class="contentitem">
				
					<h1><xsl:value-of select="/Document/module/name"/></h1>
					
					<p><xsl:value-of select="$List.NoServersFound"/></p>
					
					<div class="floatright">
						<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/addserver">
							<xsl:value-of select="$AddServer"/>
							<img class="alignbottom" align="center" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/add.png"/>
						</a>
					</div>
				</div>				
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>			
	
	<xsl:template match="Server">
		
		<div class="contentitem">
			<h1>
				<div class="floatright">
					<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/updateserver/{serverID}" title="{$Server.EditServer} {name}">
						<img class="alignbottom" align="center" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pen.png"/>
					</a>
					<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/deleteserver/{serverID}" onclick="return confirm('{$Server.DeleteServer} {name}?')" title="{$Server.DeleteServer} {name}?">
						<img class="alignbottom" align="center" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/delete.png"/>
					</a>			
				</div>
				<xsl:value-of select="name"/>
			</h1>
			
			<xsl:choose>
				<xsl:when test="Drives">
					<xsl:apply-templates select="Drives/ServerDrive"/>
				</xsl:when>
				<xsl:when test="timeout = 'true'">
					<p><xsl:value-of select="$Server.ConnectionTimeout"/></p>
				</xsl:when>
				<xsl:when test="unableToConnect = 'true'">
					<p><xsl:value-of select="$Server.UnableToConnect"/></p>
				</xsl:when>				
				<xsl:otherwise>
					<p><xsl:value-of select="$Server.NoDrivesFound"/></p>
				</xsl:otherwise>
			</xsl:choose>
		
			<div class="floatright">
				<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/addserver">
					<xsl:value-of select="$AddServer"/>
					<img class="alignbottom" align="center" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/add.png"/>
				</a>
				<br/>
				<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/resetalarms">
					<xsl:value-of select="$Server.ResetAlarms"/>
					<img class="alignbottom" align="center" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/reload.png"/>
				</a>
			</div>
		</div>
	
	</xsl:template>
	
	<xsl:template match="ServerDrive">
	
		<table class="full">
			<tr>
				<td style="vertical-align: middle;">
					<table class="border full">
						<tr>
							<th><xsl:value-of select="$ServerDrive.Device"/>:</th>
							<th>
								<div class="floatright">
									<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/updatedrive/{driveID}">
										<img class="alignbottom" align="center" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pen.png"/>
									</a>
									<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/deletedrive/{driveID}" onclick="return confirm('Delete drive {device} from server {../../name}?')">
										<img class="alignbottom" align="center" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/delete.png"/>
									</a>								
								</div>
								
								<xsl:value-of select="device"/>								
							</th>
						</tr>
						<tr>
							<td><xsl:value-of select="$ServerDrive.Temperature"/>:</td>
							<td>
								<xsl:choose>
									<xsl:when test="Drive/driveState='OK'">
										<xsl:value-of select="Drive/temp"/><xsl:text>°</xsl:text>
									</xsl:when>
									<xsl:when test="Drive/driveState='SLEEPING'">
										<xsl:value-of select="$ServerDrive.DriveSleeping"/>
									</xsl:when>
									<xsl:when test="Drive/driveState='ERROR'">
										<xsl:value-of select="$ServerDrive.DriveError"/>
									</xsl:when>
									<xsl:when test="Drive/driveState='UNKNOWN'">
										<xsl:value-of select="$ServerDrive.UnknownDriveState"/>
									</xsl:when>									
								</xsl:choose>
							</td>
						</tr>
						<tr>
							<td><xsl:value-of select="$ServerDrive.Type"/>:</td>
							<td><xsl:value-of select="Drive/type"/></td>
						</tr>
						
						<xsl:if test="maxTemp">
							<tr>
								<td><xsl:value-of select="$ServerDrive.MaxAllowedTemp"/>:</td>
								<td><xsl:value-of select="maxTemp"/></td>
							</tr>						
						</xsl:if>
						
						<xsl:if test="minTemp">
							<tr>
								<td><xsl:value-of select="$ServerDrive.MinAllowedTemp"/>:</td>
								<td><xsl:value-of select="minTemp"/></td>
							</tr>						
						</xsl:if>												
						
						<xsl:if test="lastAlarm">
							<tr>
								<th><xsl:value-of select="$ServerDrive.LastAlarm"/>:</th>
								<th><xsl:value-of select="lastAlarm"/></th>
							</tr>						
						</xsl:if>
						
					</table>				
				</td>
				<td class="text-align-center">
					<xsl:choose>
						<xsl:when test="Drive/temp">
							<img class="alignbottom" align="center" src="{/Document/requestinfo/currentURI}/{/Document/module/alias}/gauage/{Drive/temp}"/>
						</xsl:when>
						<xsl:otherwise>
							<img class="alignbottom" align="center" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/temp_gauage_disabled.png" width="{/Document/gaugeWidth}"/>
						</xsl:otherwise>
					</xsl:choose>				
				</td>
			</tr>
		</table>
		
	</xsl:template>
		
	<xsl:template match="AddServer">
		<h1><xsl:value-of select="$AddServer.Title"/></h1>
		
		<xsl:apply-templates select="validationException/validationError"/>
		
		<form method="POST" action="{/Document/requestinfo/uri}">
			<table class="full">
				<tr>
					<td width="150px"><xsl:value-of select="$Server.Name"/>:</td>
					<td>
						<xsl:call-template name="createTextField">
							<xsl:with-param name="name" select="'name'"/>							
						</xsl:call-template>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$Server.Host"/>:</td>
					<td>
						<xsl:call-template name="createTextField">
							<xsl:with-param name="name" select="'host'"/>							
						</xsl:call-template>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$Server.Port"/>:</td>
					<td>
						<xsl:call-template name="createTextField">
							<xsl:with-param name="name" select="'port'"/>
							<xsl:with-param name="value" select="'7634'"/>						
						</xsl:call-template>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$Server.Monitor"/>:</td>
					<td>
						<xsl:call-template name="createCheckbox">
							<xsl:with-param name="name" select="'monitor'"/>							
						</xsl:call-template>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$Server.MissingDriveWarning"/>:</td>
					<td>
						<xsl:call-template name="createCheckbox">
							<xsl:with-param name="name" select="'missingDriveWarning'"/>							
						</xsl:call-template>
					</td>
				</tr>
			</table>
				
			<div class="floatright">
				<input type="submit" value="{$AddServer.Submit}"/>
			</div>
		</form>	
	</xsl:template>
		
	<xsl:template match="UpdateServer">
		<h1><xsl:value-of select="$UpdateServer.Title"/> <xsl:text> </xsl:text> <xsl:value-of select="Server/name"/></h1>
		
		<xsl:apply-templates select="validationException/validationError"/>
		
		<form method="POST" action="{/Document/requestinfo/uri}">
			<table class="full">
				<tr>
					<td width="150px"><xsl:value-of select="$Server.Name"/>:</td>
					<td>
						<xsl:call-template name="createTextField">
							<xsl:with-param name="name" select="'name'"/>
							<xsl:with-param name="element" select="Server"/>						
						</xsl:call-template>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$Server.Host"/>:</td>
					<td>
						<xsl:call-template name="createTextField">
							<xsl:with-param name="name" select="'host'"/>
							<xsl:with-param name="element" select="Server"/>								
						</xsl:call-template>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$Server.Port"/>:</td>
					<td>
						<xsl:call-template name="createTextField">
							<xsl:with-param name="name" select="'port'"/>
							<xsl:with-param name="element" select="Server"/>								
						</xsl:call-template>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$Server.Monitor"/>:</td>
					<td>
						<xsl:call-template name="createCheckbox">
							<xsl:with-param name="name" select="'monitor'"/>
							<xsl:with-param name="element" select="Server"/>								
						</xsl:call-template>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$Server.MissingDriveWarning"/>:</td>
					<td>
						<xsl:call-template name="createCheckbox">
							<xsl:with-param name="name" select="'missingDriveWarning'"/>
							<xsl:with-param name="element" select="Server"/>								
						</xsl:call-template>
					</td>
				</tr>
			</table>
				
			<div class="floatright">
				<input type="submit" value="{$UpdateServer.Submit}"/>
			</div>
		</form>	
	</xsl:template>		
		
	<xsl:template match="UpdateServerDrive">
		<h1><xsl:value-of select="$UpdateServerDrive.Title"/> <xsl:text> </xsl:text> <xsl:value-of select="ServerDrive/device"/> on server <xsl:value-of select="ServerDrive/Server/name"/></h1>
		
		<xsl:apply-templates select="validationException/validationError"/>
		
		<form method="POST" action="{/Document/requestinfo/uri}">
			<table class="full">
				<tr>
					<th width="150px"><xsl:value-of select="$ServerDrive.Device"/>:</th>
					<th>
						<xsl:value-of select="ServerDrive/device"/>
					</th>
				</tr>
				<tr>
					<th width="150px"><xsl:value-of select="$ServerDrive.Type"/>:</th>
					<th>
						<xsl:value-of select="ServerDrive/type"/>
					</th>
				</tr>				
				<tr>
					<td><xsl:value-of select="$ServerDrive.MaxAllowedTemp"/>:</td>
					<td>
						<xsl:call-template name="createTextField">
							<xsl:with-param name="name" select="'maxTemp'"/>
							<xsl:with-param name="element" select="ServerDrive"/>								
						</xsl:call-template>
					</td>
				</tr>
				<tr>
					<td><xsl:value-of select="$ServerDrive.MinAllowedTemp"/>:</td>
					<td>
						<xsl:call-template name="createTextField">
							<xsl:with-param name="name" select="'minTemp'"/>
							<xsl:with-param name="element" select="ServerDrive"/>								
						</xsl:call-template>
					</td>
				</tr>
			</table>
				
			<div class="floatright">
				<input type="submit" value="{$UpdateServerDrive.Submit}"/>
			</div>
		</form>	
	</xsl:template>			
		
	<xsl:template match="validationError">
		<xsl:if test="fieldName and validationErrorType and not(messageKey)">
			<p class="error">
				<xsl:choose>
					<xsl:when test="validationErrorType='RequiredField'">
						<xsl:value-of select="$validationError.requiredField"/>
					</xsl:when>
					<xsl:when test="validationErrorType='InvalidFormat'">
						<xsl:value-of select="$validationError.invalidFormat"/>
					</xsl:when>
					<xsl:when test="validationErrorType='TooShort'">
						<xsl:value-of select="$validationError.tooShort" />
					</xsl:when>
					<xsl:when test="validationErrorType='TooLong'">
						<xsl:value-of select="$validationError.tooLong" />
					</xsl:when>		
					<xsl:otherwise>
						<xsl:value-of select="$validationError.unknownError"/>
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:text>&#x20;</xsl:text>
				
				<xsl:choose>
					<xsl:when test="fieldName = 'minTemp'">
						<xsl:value-of select="$validationError.field.minTemp"/>
					</xsl:when>
					<xsl:when test="fieldName = 'maxTemp'">
						<xsl:value-of select="$validationError.field.maxTemp"/>
					</xsl:when>
					<xsl:when test="fieldName = 'name'">
						<xsl:value-of select="$validationError.field.name"/>
					</xsl:when>
					<xsl:when test="fieldName = 'host'">
						<xsl:value-of select="$validationError.field.host"/>
					</xsl:when>
					<xsl:when test="fieldName = 'port'">
						<xsl:value-of select="$validationError.field.port"/>
					</xsl:when>															
					<xsl:otherwise>
						<xsl:value-of select="fieldName"/>
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:text>!</xsl:text>		
			</p>
		</xsl:if>
		
		<xsl:if test="messageKey">
			<p class="error">
				<xsl:choose>
					<xsl:when test="messageKey='UpdateFailedServerNotFound'">
						<xsl:value-of select="$validationError.message.ServerNotFound"/>
					</xsl:when>
					<xsl:when test="messageKey='UpdateFailedServerDriveNotFound'">
						<xsl:value-of select="$validationError.message.DriveNotFound"/>
					</xsl:when>									
					<xsl:otherwise>
						<xsl:text><xsl:value-of select="$validationError.message.unknownFault"/></xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</p>
		</xsl:if>
		
	</xsl:template>	
					
</xsl:stylesheet>