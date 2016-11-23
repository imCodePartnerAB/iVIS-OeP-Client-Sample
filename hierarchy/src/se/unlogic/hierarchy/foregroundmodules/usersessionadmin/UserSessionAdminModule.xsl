<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>
	<xsl:template match="Document">			
		<xsl:apply-templates select="LoggedInUsers"/>
	</xsl:template>
	
	<xsl:template match="LoggedInUsers">
		
		<script 
			type="text/javascript"
			src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/js/hideshow.js">
		</script>		
		
		<div class="contentitem">
			<h1>
				<xsl:value-of select="../module/name"/>
				<xsl:text>&#x20;(</xsl:text>
				<xsl:value-of select="count(user)"/>
				<xsl:text>)</xsl:text>
			</h1>
			
			<xsl:choose>
				<xsl:when test="user">
					<xsl:apply-templates select="user"/>
				</xsl:when>
				<xsl:otherwise>
					<p>Inga inloggade användare hittades</p>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>	
	
	<xsl:template match="user">
		
		<div class="floatleft border full marginbottom">
			<div class="floatleft">
				<a href="javascript:hideShow({position()})" title="Visa/dölj information om användaren {firstname} {lastname}">
					<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/user.png"/>
					
					<xsl:text>&#x20;</xsl:text>
					
					<xsl:value-of select="firstname"/>
					
					<xsl:text>&#x20;</xsl:text>
					
					<xsl:value-of select="lastname"/>
					
					<xsl:text>&#x20;</xsl:text>
					
					<xsl:text>(</xsl:text>
						<xsl:value-of select="username"/>
					<xsl:text>)</xsl:text>				
				</a>			
			</div>
			<div class="floatright">
				<xsl:choose>
					<xsl:when test="SessionInfo/SessionID">
						<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/logoutUser/{userID}/{SessionInfo/SessionID}" title="Logga ut användaren {firstname} {lastname}">
							<img class="alignbottom marginright" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/door.gif"/>
						</a>					
					</xsl:when>
					<xsl:otherwise>
						<img class="alignbottom marginright" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/door_disabled.gif" alt="Det går inte logga ut denna användare då dess session inte är åtkomlig"/>
					</xsl:otherwise>
				</xsl:choose>			
			</div>
			<div class="floatleft full hidden" id="{position()}">
				
				<xsl:if test="SessionInfo/SessionID">
					<h4>Sessions information</h4>
					
					<table>
						<tr>
							<td>Sessions ID:</td>
							<td><xsl:value-of select="SessionInfo/SessionID"/></td>
						</tr>
						<tr>
							<td>Skapad:</td>
							<td><xsl:value-of select="SessionInfo/CreationTime"/></td>
						</tr>
						<tr>
							<td>Senaste åtkomst:</td>
							<td><xsl:value-of select="SessionInfo/LastAccessedTime"/></td>
						</tr>
						<tr>
							<td>Timeout:</td>
							<td><xsl:value-of select="SessionInfo/MaxInactiveInterval"/></td>
						</tr>
					</table>				
				</xsl:if>
				
				<h4>Användar information</h4>
				
				<table>	
					<tr>
						<td>AnvändarID:</td>
						<td><xsl:value-of select="userID"/></td>
					</tr>					
					<tr>
						<td>Förnamn:</td>
						<td><xsl:value-of select="firstname"/></td>
					</tr>
					<tr>
						<td>Efternamn:</td>
						<td><xsl:value-of select="lastname"/></td>
					</tr>
					<tr>
						<td>Användarnamn:</td>
						<td><xsl:value-of select="username"/></td>
					</tr>
					<tr>
						<td>E-post adress:</td>
						<td><xsl:value-of select="email"/></td>
					</tr>
					<tr>
						<td>Administratör:</td>
						<td>
							<input type="checkbox" name="admin">
								<xsl:attribute name="disabled"/>
								<xsl:if test="admin='true'">
									<xsl:attribute name="checked">true</xsl:attribute>
								</xsl:if>							
							</input>								
						</td>
					</tr>																		
					<tr>
						<td>Konto aktiverat:</td>
						<td>
							<input type="checkbox" name="enabled">
								<xsl:attribute name="disabled"/>
								<xsl:if test="enabled='true'">
									<xsl:attribute name="checked">true</xsl:attribute>
								</xsl:if>							
							</input>								
						</td>
					</tr>
					<tr>
						<td>Konto skapat:</td>
						<td><xsl:value-of select="added"/></td>
					</tr>						
					<tr>
						<td>Loggade in:</td>
						<td><xsl:value-of select="currentLogin"/></td>
					</tr>
					<tr>
						<td>Föregående inloggning:</td>
						<td><xsl:value-of select="lastLogin"/></td>
					</tr>																																																	
				</table>							
			</div>					
		</div>		
	</xsl:template>		
</xsl:stylesheet>