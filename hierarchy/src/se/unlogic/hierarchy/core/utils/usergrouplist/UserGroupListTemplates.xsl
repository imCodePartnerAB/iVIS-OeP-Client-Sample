<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1" />

<!-- 	<xsl:variable name="scripts"> -->
<!-- 		/js/UserGroupList.js -->
<!-- 	</xsl:variable> -->

<!-- 	<xsl:variable name="links"> -->
<!-- 		/css/UserGroupList.css -->
<!-- 	</xsl:variable> -->

	<xsl:template name="UserList">
		<xsl:param name="name" select="null"/>
		<xsl:param name="requestparameters" select="requestparameters"/>
		<xsl:param name="users" select="null"/>
		<xsl:param name="connectorURL"/>
		<xsl:param name="placeholder" select="$i18n.SearchUsers"/>
		<xsl:param name="showEmail" select="false()"/>
		<xsl:param name="showUsername" select="false()"/>
		<xsl:param name="document" select="/Document"/>
		
		<ul class="list-style-type-none margintop usergroup-list" id="{$name}-list">
		
			<input type="hidden" name="prefix" disabled="disabled" value="{$name}"/>
			<input type="hidden" name="type" disabled="disabled" value="user"/>
			<input type="hidden" name="connectorURL" disabled="disabled" value="{$connectorURL}"/>
			
			<xsl:choose>
				<xsl:when test="$requestparameters">
					<xsl:apply-templates select="$requestparameters/parameter[name=$name]/value" mode="user">
						<xsl:with-param name="requestparameters" select="$requestparameters"/>
						<xsl:with-param name="prefix" select="$name"/>
						<xsl:with-param name="document" select="$document"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="$users/user" mode="ajaxlist">
						<xsl:with-param name="prefix" select="$name"/>
						<xsl:with-param name="showEmail" select="$showEmail"/>
						<xsl:with-param name="showUsername" select="$showUsername"/>
						<xsl:with-param name="document" select="$document"/>
					</xsl:apply-templates>
				</xsl:otherwise>
			</xsl:choose>
					
			
					
			<li id="{$name}-template" class="hidden show-email-{$showEmail} show-username-{$showUsername}">
				
				<input type="hidden" name="{$name}" disabled="disabled"/>
				<input type="hidden" name="{$name}-name" disabled="disabled"/>

				<img class="vertical-align-middle" src="{$document/requestinfo/contextpath}/static/f/{$document/module/sectionID}/{$document/module/moduleID}/pics/user.png" alt="" />
				
				<xsl:text>&#x20;</xsl:text>
				
				<span class="text"/>

				<div class="floatright">
					<a class="delete" href="#" title="{$i18n.DeleteUser}:">
						<img class="vertical-align-middle" src="{$document/requestinfo/contextpath}/static/f/{$document/module/sectionID}/{$document/module/moduleID}/pics/user_delete.png" alt="{$i18n.DeleteUser}" />
					</a>
				</div>
			</li>
			
		</ul>
		
		<xsl:call-template name="searchField">
			<xsl:with-param name="connectorURL" select="$connectorURL" />
			<xsl:with-param name="prefix" select="$name" />
			<xsl:with-param name="placeholder" select="$placeholder" />
		</xsl:call-template>
	
		<br/>
		
	</xsl:template>
	
	<xsl:template name="ReadOnlyUserList">
	
		<xsl:param name="users" select="null" />
		<xsl:param name="showEmail" select="false()" />
		<xsl:param name="showUsername" select="false()"/>
		<xsl:param name="document" select="/Document"/>
	
		<ul class="list-style-type-none margintop readonly-usergroup-list">
			
			<xsl:apply-templates select="$users/user" mode="readonly">
				<xsl:with-param name="showEmail" select="$showEmail"/>
				<xsl:with-param name="showUsername" select="$showUsername"/>
				<xsl:with-param name="document" select="$document"/>
			</xsl:apply-templates>
			
		</ul>
	
	</xsl:template>
	
	<xsl:template match="value" mode="user">
	
		<xsl:param name="requestparameters"/>
		<xsl:param name="prefix"/>
		<xsl:param name="document"/>
	
		<xsl:variable name="userID" select="."/>
	
		<xsl:variable name="name" select="$requestparameters/parameter[name=concat($prefix,'-name', $userID)]/value"/>
		
		<xsl:if test="$name != ''">
	
			<li id="{$prefix}_{.}" class="{$prefix}-list-entry">
				
				<input type="hidden" name="{$prefix}" value="{.}"/>
				<input type="hidden" name="{$prefix}-name{.}" value="{$name}"/>
				
				<img class="vertical-align-middle" src="{$document/requestinfo/contextpath}/static/f/{$document/module/sectionID}/{$document/module/moduleID}/pics/user.png" alt="" />
				
				<xsl:text>&#x20;</xsl:text>
				
				<span class="text">
					<xsl:value-of select="$name"/>	
				</span>			
				
				<div class="floatright">
					<a class="delete" href="#" title="{$i18n.DeleteUser}: {$name}">
						<img class="vertical-align-middle" src="{$document/requestinfo/contextpath}/static/f/{$document/module/sectionID}/{$document/module/moduleID}/pics/user_delete.png" alt="{$i18n.DeleteUser}" />
					</a>
				</div>
			</li>
		
		</xsl:if>
	
	</xsl:template>
	
	<xsl:template match="user" mode="ajaxlist">
	
		<xsl:param name="prefix"/>
		<xsl:param name="showUsername" />
		<xsl:param name="showEmail" />
		<xsl:param name="document"/>
	
		<xsl:variable name="name">
		
			<xsl:value-of select="firstname"/>
			
			<xsl:text>&#x20;</xsl:text>
			
			<xsl:value-of select="lastname"/>
			
			<xsl:if test="$showUsername and username">
				
				<xsl:text>&#x20;(</xsl:text>
				<xsl:value-of select="username"/>
									
			</xsl:if>
					
			<xsl:if test="$showEmail and email">
			
				<xsl:choose>
					<xsl:when test="$showUsername and username">
					
						<xsl:text>,&#x20;</xsl:text>
						
					</xsl:when>
					<xsl:otherwise>
					
						<xsl:text>&#x20;(</xsl:text>
						
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:value-of select="email" />
				
			</xsl:if>
			
			<xsl:if test="($showUsername and username) or ($showUsername and username)">
				<xsl:text>)</xsl:text>
			</xsl:if>
					
		</xsl:variable>
	
		<li id="{$prefix}_{userID}" class="{$prefix}-list-entry">
			
			<input type="hidden" name="{$prefix}" value="{userID}"/>
			<input type="hidden" name="{$prefix}-name{userID}" value="{$name}"/>
			
			<img class="vertical-align-middle" src="{$document/requestinfo/contextpath}/static/f/{$document/module/sectionID}/{$document/module/moduleID}/pics/user.png" alt="" />
			
			<xsl:text>&#x20;</xsl:text>
			
			<span class="text">
				<xsl:value-of select="$name"/>
			</span>			
			
			<div class="floatright">
				<a class="delete" href="#" title="{$i18n.DeleteUser}: {$name}">
					<img class="vertical-align-middle" src="{$document/requestinfo/contextpath}/static/f/{$document/module/sectionID}/{$document/module/moduleID}/pics/user_delete.png" alt="{$i18n.DeleteUser}" />
				</a>
			</div>
		</li>
	
	</xsl:template>	
	
	<xsl:template match="user" mode="readonly">
	
	 	<xsl:param name="showUsername" />
		<xsl:param name="showEmail" />
		<xsl:param name="document"/>
	
		<li>
			
			<xsl:variable name="name">
		
				<xsl:value-of select="firstname"/>
				
				<xsl:text>&#x20;</xsl:text>
				
				<xsl:value-of select="lastname"/>
				
			</xsl:variable>
			
			<img class="vertical-align-middle" src="{$document/requestinfo/contextpath}/static/f/{$document/module/sectionID}/{$document/module/moduleID}/pics/user.png" alt="" />
			
			<xsl:text>&#x20;</xsl:text>
			
			<span class="text">
				
				<xsl:value-of select="$name"/>
				
				<xsl:if test="$showUsername and username">
				
					<xsl:text>&#x20;(</xsl:text>
					<xsl:value-of select="username"/>
										
				</xsl:if>
				
				<xsl:if test="$showEmail and email">
				
					<xsl:choose>
						<xsl:when test="$showUsername and username">
						
							<xsl:text>,&#x20;</xsl:text>
							
						</xsl:when>
						<xsl:otherwise>
						
							<xsl:text>&#x20;(</xsl:text>
							
						</xsl:otherwise>
					</xsl:choose>
					
					<xsl:value-of select="email" />
					
				</xsl:if>
				
				<xsl:if test="($showUsername and username) or ($showEmail and email)">
					<xsl:text>)</xsl:text>
				</xsl:if>
				
			</span>
			
		</li>
	
	</xsl:template>
	
	<xsl:template name="GroupList">
		<xsl:param name="name" select="null"/>
		<xsl:param name="requestparameters" select="requestparameters"/>
		<xsl:param name="groups" select="null"/>
		<xsl:param name="connectorURL"/>
		<xsl:param name="document" select="/Document"/>
		<xsl:param name="placeholder" select="$i18n.SearchGroups"/>
	
		<ul class="list-style-type-none margintop usergroup-list" id="{$name}-list">
		
			<input type="hidden" name="prefix" disabled="disabled" value="{$name}"/>
			<input type="hidden" name="type" disabled="disabled" value="group"/>
			<input type="hidden" name="connectorURL" disabled="disabled" value="{$connectorURL}"/>
			
			<xsl:choose>
				<xsl:when test="$requestparameters">
					<xsl:apply-templates select="$requestparameters/parameter[name=$name]/value" mode="group">
						<xsl:with-param name="requestparameters" select="$requestparameters"/>
						<xsl:with-param name="prefix" select="$name"/>
						<xsl:with-param name="document" select="$document"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="$groups/group" mode="ajaxlist">
						<xsl:with-param name="prefix" select="$name"/>
						<xsl:with-param name="document" select="$document"/>
					</xsl:apply-templates>
				</xsl:otherwise>
			</xsl:choose>
					
			<li id="{$name}-template" class="hidden">
				
				<input type="hidden" name="{$name}" disabled="disabled"/>
				<input type="hidden" name="{$name}-name" disabled="disabled"/>

				<img class="vertical-align-middle" src="{$document/requestinfo/contextpath}/static/f/{$document/module/sectionID}/{$document/module/moduleID}/pics/group.png" alt="" />
				
				<xsl:text>&#x20;</xsl:text>
				
				<span class="text"/>

				<div class="floatright">
					<a class="delete" href="#" title="{$i18n.DeleteGroup}:">
						<img class="vertical-align-middle" src="{$document/requestinfo/contextpath}/static/f/{$document/module/sectionID}/{$document/module/moduleID}/pics/group_delete.png" alt="{$i18n.DeleteGroup}" />
					</a>
				</div>
			</li>
			
		</ul>
		
		<xsl:call-template name="searchField">
			<xsl:with-param name="connectorURL" select="$connectorURL" />
			<xsl:with-param name="prefix" select="$name" />
			<xsl:with-param name="placeholder" select="$placeholder" />
		</xsl:call-template>
	
		<br/>
		
	</xsl:template>
	
	<xsl:template name="ReadOnlyGroupList">
	
		<xsl:param name="groups" select="null" />
		<xsl:param name="document" select="/Document"/>
	
		<ul class="list-style-type-none margintop readonly-usergroup-list">
			
			<xsl:apply-templates select="$groups/group" mode="readonly" >
				<xsl:with-param name="document" select="$document"/>
			</xsl:apply-templates>
			
		</ul>
	
	</xsl:template>
	
	<xsl:template match="value" mode="group">
	
		<xsl:param name="requestparameters"/>
		<xsl:param name="prefix"/>
		<xsl:param name="document"/>
	
		<xsl:variable name="groupID" select="."/>
	
		<xsl:variable name="name" select="$requestparameters/parameter[name=concat($prefix,'-name',$groupID)]/value"/>
	
		<li id="{$prefix}_{.}" class="{$prefix}-group-list-entry">
			
			<input type="hidden" name="{$prefix}" value="{.}"/>
			<input type="hidden" name="{$prefix}-name{.}" value="{$name}"/>
			
			<img class="vertical-align-middle" src="{$document/requestinfo/contextpath}/static/f/{$document/module/sectionID}/{$document/module/moduleID}/pics/group.png" alt="" />
			
			<xsl:text>&#x20;</xsl:text>
			
			<span class="text">
				<xsl:value-of select="$name"/>	
			</span>			
			
			<div class="floatright">
				<a class="delete" href="#" title="{$i18n.DeleteGroup}: {$name}">
					<img class="vertical-align-middle" src="{$document/requestinfo/contextpath}/static/f/{$document/module/sectionID}/{$document/module/moduleID}/pics/group_delete.png" alt="{$i18n.DeleteGroup}" />
				</a>
			</div>
		</li>
	
	</xsl:template>
	
	<xsl:template match="group" mode="ajaxlist">
	
		<xsl:param name="prefix"/>
		<xsl:param name="document"/>
	
		<xsl:variable name="name">
		
			<xsl:value-of select="name"/>
					
		</xsl:variable>
	
		<li id="{$prefix}_{groupID}" class="{$prefix}-list-entry">
			
			<input type="hidden" name="{$prefix}" value="{groupID}"/>
			<input type="hidden" name="{$prefix}-name{groupID}" value="{$name}"/>
			
			<img class="vertical-align-middle" src="{$document/requestinfo/contextpath}/static/f/{$document/module/sectionID}/{$document/module/moduleID}/pics/group.png" alt="" />
			
			<xsl:text>&#x20;</xsl:text>
			
			<span class="text">
				<xsl:value-of select="$name"/>
			</span>			
			
			<div class="floatright">
				<a class="delete" href="#" title="{$i18n.DeleteGroup}: {$name}">
					<img class="vertical-align-middle" src="{$document/requestinfo/contextpath}/static/f/{$document/module/sectionID}/{$document/module/moduleID}/pics/group_delete.png" alt="{$i18n.DeleteGroup}" />
				</a>
			</div>
		</li>
	
	</xsl:template>	
	
	<xsl:template match="group" mode="readonly">
		<xsl:param name="document"/>
	
		<li>
			
			<xsl:variable name="name">
		
				<xsl:variable name="name">
			
				<xsl:value-of select="name"/>
				
				<xsl:if test="description">
					
					<xsl:text>&#x20;</xsl:text>
					
					<xsl:text>(</xsl:text>
						<xsl:value-of select="description"/>
					<xsl:text>)</xsl:text>
										
				</xsl:if>
					
			</xsl:variable>
					
		</xsl:variable>
			
			<img class="vertical-align-middle" src="{$document/requestinfo/contextpath}/static/f/{$document/module/sectionID}/{$document/module/moduleID}/pics/user.png" alt="" />
			
			<xsl:text>&#x20;</xsl:text>
			
			<span class="text">
				<xsl:value-of select="$name"/>
			</span>
			
		</li>
	
	</xsl:template>
	
	<xsl:template name="searchField">
	
		<xsl:param name="connectorURL"/>
		<xsl:param name="prefix"/>
		<xsl:param name="placeholder" select="''"/>

		<xsl:if test="$connectorURL">
	
			<div class="ui-widget">
				<xsl:call-template name="createTextField">
					<xsl:with-param name="id">
						<xsl:value-of select="$prefix" />
						<xsl:value-of select="'-search'" />
					</xsl:with-param>
					<xsl:with-param name="class" select="'full border-box'" />
					<xsl:with-param name="width" select="''" />
					<xsl:with-param name="placeholder" select="$placeholder" />
				</xsl:call-template>
			</div>
	
		</xsl:if>

	</xsl:template>
	
</xsl:stylesheet>

