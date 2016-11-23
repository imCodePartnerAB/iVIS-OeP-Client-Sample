<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:template match="Document">	
		<div class="contentitem">
			<h1><xsl:value-of select="module/name"/></h1>
			
			<fieldset>
				<legend>
					<img src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/cpu.png"/>
					<xsl:text> </xsl:text>
					<xsl:value-of select="$i18n.CPU"/>
				</legend>
				<p>
					<xsl:value-of select="$i18n.AvailableProcessors"/>
					<xsl:text>: </xsl:text>
					<xsl:value-of select="AvailableProcessors"/>
				</p>								
			</fieldset>
			
			<fieldset>
				<legend>
					<img src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/info.png"/>
					<xsl:text> </xsl:text>
					<xsl:value-of select="$i18n.OS"/>
				</legend>
				
				<table>
					<tr>
						<td>
							<xsl:value-of select="$i18n.Name"/>
							<xsl:text>:</xsl:text>
						</td>
						<td>
							<xsl:value-of select="Name"/>
						</td>
					</tr>
					<tr>
						<td>
							<xsl:value-of select="$i18n.Version"/>
							<xsl:text>:</xsl:text>
						</td>
						<td>
							<xsl:value-of select="Version"/>
						</td>
					</tr>
					<tr>
						<td>
							<xsl:value-of select="$i18n.Arch"/>
							<xsl:text>:</xsl:text>
						</td>
						<td>
							<xsl:value-of select="Arch"/>
						</td>
					</tr>										
				</table>																
			</fieldset>			
			
			<fieldset>
				<legend>
					<img src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/class.png"/>
					<xsl:text> </xsl:text>
					<xsl:value-of select="$i18n.ClassLoading"/>
				</legend>
				
				<table>
					<tr>
						<td>
							<xsl:value-of select="$i18n.LoadedClassCount"/>
							<xsl:text>:</xsl:text>
						</td>
						<td>
							<xsl:value-of select="LoadedClassCount"/>
						</td>
					</tr>
					<tr>
						<td>
							<xsl:value-of select="$i18n.UnloadedClassCount"/>
							<xsl:text>:</xsl:text>
						</td>
						<td>
							<xsl:value-of select="UnloadedClassCount"/>
						</td>
					</tr>
					<tr>
						<td>
							<xsl:value-of select="$i18n.TotalLoadedClassCount"/>
							<xsl:text>:</xsl:text>
						</td>
						<td>
							<xsl:value-of select="TotalLoadedClassCount"/>
						</td>
					</tr>										
				</table>																
			</fieldset>				
						
			<fieldset>
				<legend>
					<img src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/trash.gif"/>
					<xsl:text> </xsl:text>
					<xsl:value-of select="$i18n.GarbageCollection"/>
				</legend>
				
				<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/gc" title="{$i18n.RunGC}">
					<xsl:value-of select="$i18n.RunGC"/>
				</a>
																				
			</fieldset>				
			
			<xsl:apply-templates select="MemoryUsage"/>
		</div>	
	</xsl:template>
		
	<xsl:template match="MemoryUsage">
	
		<fieldset>
			<legend>
				<img src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/ram.png"/>
				<xsl:text> </xsl:text>
				<xsl:value-of select="Name"/>
				
				<xsl:if test="Type">
					<xsl:text> (</xsl:text>
					<xsl:value-of select="Type"/>
					<xsl:text>)</xsl:text>
				</xsl:if>
			</legend>
			
			<table class="full">
				<tr>
					<td style="vertical-align: middle;">				
						<table class="full">
							<tr>
								<td width="15">
									<xsl:value-of select="$i18n.Init"/>
									<xsl:text>:</xsl:text>
								</td>
								<td>
									<xsl:value-of select="Init"/>
									<xsl:text> </xsl:text>
									<xsl:value-of select="$i18n.MB"/>
								</td>
							</tr>
							<tr>
								<td>
									<xsl:value-of select="$i18n.Used"/>
									<xsl:text>:</xsl:text>
								</td>
								<td>
									<xsl:value-of select="Used"/>
									<xsl:text> </xsl:text>
									<xsl:value-of select="$i18n.MB"/>						
								</td>
							</tr>
							<tr>
								<td>
									<xsl:value-of select="$i18n.Committed"/>
									<xsl:text>:</xsl:text>
								</td>
								<td>
									<xsl:value-of select="Committed"/>
									<xsl:text> </xsl:text>
									<xsl:value-of select="$i18n.MB"/>						
								</td>
							</tr>
							<tr>
								<td>
									<xsl:value-of select="$i18n.Max"/>
									<xsl:text>:</xsl:text>
								</td>
								<td>
									<xsl:value-of select="Max"/>
									<xsl:text> </xsl:text>
									<xsl:value-of select="$i18n.MB"/>
								</td>
							</tr>												
						</table>					
					</td>
					<td class="text-align-center seventy">
						<xsl:variable name="precentUsed" select="(Used div Max)*100"/>
						<img src="{/Document/requestinfo/currentURI}/{/Document/module/alias}/gauage/{$precentUsed}"/>
					</td>
				</tr>
			</table>
		</fieldset>

	</xsl:template>	
</xsl:stylesheet>