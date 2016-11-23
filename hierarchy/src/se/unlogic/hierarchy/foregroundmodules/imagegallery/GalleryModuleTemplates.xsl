<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="../../core/utils/xsl/Common.xsl"/>
	
	<xsl:variable name="scripts">
		/jquery/jquery.js
		/js/confirmDelete.js
		/js/gallery.js
		/js/enableUpload.js
		/js/hideshow.js
	</xsl:variable>	

	<xsl:template match="document">		
		
		<div id="GalleryModule" class="contentitem">
			<xsl:apply-templates select="showGallery" />
			<xsl:apply-templates select="showImage" />
			<xsl:apply-templates select="updateGallery" />
			<xsl:apply-templates select="galleries" />
			<xsl:apply-templates select="addGallery" />
			<xsl:apply-templates select="addImages" />
		</div>
		
	</xsl:template>
	
	<xsl:template match="galleries">
	
		<div class="listGalleries">
	
		    <h1><xsl:value-of select="/document/module/name"/></h1>
		    	<xsl:if test="/document/isAdmin='true'">
			        <table>
			            <tr>
			                <td>
			        			<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/addGallery" title="{$i18n.galleries.addGallery.title}">
			        			<xsl:value-of select="$i18n.galleries.addGallery"/><xsl:text>&#x20;&#x20;</xsl:text></a>
			        		</td>
			        		<td><xsl:text>&#x20;&#x20;|&#x20;&#x20;</xsl:text></td>
			                <td>
			        			<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/checkForNewImages" title="{$i18n.galleries.scanForNewImages.title}">
			        				<xsl:value-of select="$i18n.galleries.scanForNewImages"/>
			        			</a>
			                </td>
			        		<td><xsl:text>&#x20;&#x20;|&#x20;&#x20;</xsl:text></td>
			                <td>
			        			<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/regenerateThumbs" title="{$i18n.galleries.regenerateThumbs.title}">
			        				<xsl:value-of select="$i18n.galleries.regenerateThumbs"/>
			        			</a>
			                </td>		                
			            </tr>
			        </table>	    	
		    	</xsl:if>
	
				<div class="full">
					<xsl:choose>
						<xsl:when test="gallery">
							<div class="full floatleft bordertop" />
							<xsl:apply-templates select="gallery" mode="list"/> 
						</xsl:when>
						<xsl:otherwise>
							<p><xsl:value-of select="$i18n.galleries.noGalleriesFound"/></p>
						</xsl:otherwise>
					</xsl:choose>
				</div>
			
			</div>
			 		 
	</xsl:template>
	
	<xsl:template match="gallery" mode="list">
		<div class="full floatleft borderbottom">
			<xsl:if test="/document/isAdmin='true'">				
				<div class="margintop floatright">
					<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/regenerateGalleryThubms/{alias}" Title="{$i18n.gallery.regenerateThumbs.title} &quot;{name}&quot;">
		 			<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/reload.png" alt=""/></a>				
					<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/updateGallery/{alias}" Title="{$i18n.gallery.updateGallery.title} &quot;{name}&quot;">
		 			<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/pen.png" alt=""/></a>
		 			<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/deleteGallery/{alias}" Title="{$i18n.gallery.deleteGallery.title} &quot;{name}&quot;" onclick="return confirmDelete('{$i18n.gallery.deleteGallery.popup} &quot;{name}&quot;?')">
		 			<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/delete.png" alt=""/></a>
				</div>
			</xsl:if>				
			
			<xsl:if test="randomFile">
				<div class="floatleft marginright margintop marginbottom" style="margin-right: 10px; margin-top: 5px; margin-bottom: 5px">
					<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/gallery/{alias}" Title="{$i18n.gallery.thumblink.title}">
			 		<img src="{/document/requestinfo/currentURI}/{/document/module/alias}/smallThumb/{alias}/{randomFile}" alt=""/></a>
			 	</div>			
			</xsl:if>

		 	<div class="marginleft">
					<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/gallery/{alias}" Title="{gallery.headerlink.title}">
					<h3><xsl:value-of select="name" /> (<xsl:value-of select="numPics" /><xsl:text>&#x20;</xsl:text><xsl:value-of select="$i18n.gallery.pictures"/>) </h3></a>
					<p>
						<xsl:call-template name="replaceLineBreak">
							<xsl:with-param name="string" select="description"/>
						</xsl:call-template>					
					</p>
			</div>
		</div>
	</xsl:template>
	

	<xsl:template match="showGallery">
		
		<div class="showGallery">
		
			<form action="{/document/requestinfo/currentURI}/{/document/module/alias}/deleteImage/{gallery/alias}" method="post">
	                <h1><xsl:value-of select="gallery/name"/> (<xsl:value-of select="gallery/numPics"/><xsl:text>&#x20;</xsl:text><xsl:value-of select="$i18n.showGallery.pictures"/>)</h1>
	                    <table>
	                        <tr>
	                            <td>
	                    			<xsl:value-of select="$i18n.showGallery.page"/><xsl:text>&#x20;</xsl:text><strong><xsl:value-of select="gallery/currentPage"/></strong><xsl:text>&#x20;</xsl:text><xsl:value-of select="$i18n.showGallery.pagecount"/><xsl:text>&#x20;</xsl:text><xsl:value-of select="gallery/pages"/>
	                    		</td>
	                    		<td><xsl:text>&#x20;&#x20;|&#x20;&#x20;</xsl:text></td>
	                            <td>
	                    			<xsl:choose>
										<xsl:when test="gallery/prevPage">
											<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/gallery/{gallery/alias}/{gallery/prevPage}" title="{$i18n.showGallery.previousLink.title}" >
											<img alt="{$i18n.showGallery.previousImage.alt}" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/left.gif" /><xsl:text>&#x20;&#x20;</xsl:text><xsl:value-of select="$i18n.showGallery.previousLink.text"/></a>
										</xsl:when>
										<xsl:otherwise>
											<div class="disabledtext"><img alt="{$i18n.showGallery.previousImage.alt}" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/left_light.gif" /><xsl:text>&#x20;&#x20;</xsl:text><xsl:value-of select="$i18n.showGallery.previousLink.text"/></div>
										</xsl:otherwise>
									</xsl:choose>
	                            </td>
	                            <td><xsl:text>&#x20;&#x20;|&#x20;&#x20;</xsl:text></td>
	                            <td>
	                                <xsl:choose>
										<xsl:when test="gallery/nextPage">
											<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/gallery/{gallery/alias}/{gallery/nextPage}" title="{$i18n.showGallery.nextLink.title}" >
											<xsl:value-of select="$i18n.showGallery.nextLink.text"/><xsl:text>&#x20;&#x20;</xsl:text><img alt="{$i18n.showGallery.nextImage.alt}" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/right.gif" /></a>
										</xsl:when>
										<xsl:otherwise>
											<div class="disabledtext"><xsl:value-of select="$i18n.showGallery.nextLink.text"/><xsl:text>&#x20;&#x20;</xsl:text><img alt="{$i18n.showGallery.nextImage.alt}" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/right_light.gif" /></div>
										</xsl:otherwise>
									</xsl:choose>
	                            </td>
	                            <td><xsl:text>&#x20;&#x20;|&#x20;&#x20;</xsl:text></td>
	                            
	                            <td>
									<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/download/{gallery/alias}" title="{$i18n.showGallery.downloadLink.title}" >
										<xsl:value-of select="$i18n.showGallery.downloadLink.text"/><xsl:text>&#x20;&#x20;</xsl:text><img alt="{$i18n.showGallery.downloadImage.alt}" class="alignbottom" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/zip.png" />
									</a>
	                            </td>
	                            <td><xsl:text>&#x20;&#x20;|&#x20;&#x20;</xsl:text></td>                            
	                            
	                            <xsl:if test="/document/isAdmin='true' or hasUploadAccess='true'">
		                            <td>
		                            	<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/addImages/{gallery/alias}" title="{$i18n.showGallery.addImagesLink.title}" >
										<xsl:value-of select="$i18n.showGallery.addImagesLink.text"/></a>
		                            </td>
	                            	<td><xsl:text>&#x20;&#x20;|&#x20;&#x20;</xsl:text></td>
	                            </xsl:if>
	                            
	                            <xsl:if test="/document/isAdmin='true'">
		                            <td>
	    	                        	<a id="pictureSelectionButton" href="javascript:togglePicSelection('{$i18n.showGallery.select.title}','{$i18n.showGallery.selectAll.title}','{$i18n.showGallery.deselectAll.title}');"><xsl:value-of select="$i18n.showGallery.select.title"/></a>
	        	                    </td>
	            	                <td><xsl:text>&#x20;&#x20;|&#x20;&#x20;</xsl:text></td>
	                            </xsl:if>
	                            
	                            <td>
	                            	<a href="{/document/requestinfo/currentURI}/{/document/module/alias}" title="{$i18n.showGallery.showAllGalleriesLink.title}" >
									<xsl:value-of select="$i18n.showGallery.showAllGalleriesLink.text"/></a>
	                            </td>
	                        </tr>
	                    </table>
	                    
	                    <div class="full floatleft">
		                    <xsl:choose>
								<xsl:when test="gallery/files/file">
									<xsl:apply-templates select="gallery/files/file" mode="list"/>
								</xsl:when>
								<xsl:otherwise>
									<p><xsl:value-of select="$i18n.showGallery.noImagesInGallery"/><xsl:text>&#x20;</xsl:text><xsl:value-of select="gallery/name"/></p>
								</xsl:otherwise>
							</xsl:choose>
						</div>
						
						 <xsl:if test="/document/isAdmin='true'">
							<div id="picSelectionSubmitButton" class="full floatright" style="display: none">
								<input type="submit" value="{$i18n.showGallery.deleteSelectedPictures.title}" onclick="return confirm('{$i18n.showGallery.deleteSelectedPictures.confirm}');" style="float: right;" />
							</div>
						</xsl:if>
				</form>
			
			</div>
			
	</xsl:template>
	
	<xsl:template match="gallery/files/file" mode="list">
		<div class="floatleft margintop marginbottom marginleft marginright">
		 	<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/image/{../../alias}/{filename}" Title="{$i18n.file.link.title}">
		 	<img src="{/document/requestinfo/currentURI}/{/document/module/alias}/smallThumb/{../../alias}/{filename}" alt=""/></a>
		 	
		 	<xsl:if test="/document/isAdmin='true'">
		 		<div style="display: none">
		 			<input type="checkbox" name="delete" value="{filename}" />
		 		</div>
		 	</xsl:if>
		</div>
	</xsl:template>
	
	<xsl:template match="showImage">
		<h1><xsl:value-of select="gallery/name"/><xsl:text>&#x20;</xsl:text>(<xsl:value-of select="gallery/numPics"/><xsl:text>&#x20;</xsl:text><xsl:value-of select="$i18n.showImage.pictures"/>)</h1>
         
         <xsl:if test="/document/isAdmin='true'">
	         <div class="floatright">
				<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/deleteImage/{gallery/alias}/{gallery/file/filename}" Title="{$i18n.showImage.deleteImageLink.title} &quot;{gallery/file/filename}&quot;" onclick="return confirmDelete('{$i18n.showImage.deleteImageLink.popup} &quot;{gallery/file/filename}&quot;?')">
				<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/delete.png" alt=""/></a>
			 </div>
         </xsl:if>
         
         <table>
         	<tr>
            	<td>
             		<xsl:value-of select="gallery/file/filename"/><xsl:text>&#x20;</xsl:text><xsl:value-of select="$i18n.showImage.picture"/><xsl:text>&#x20;</xsl:text><strong><xsl:value-of select="gallery/currentPic"/></strong><xsl:text>&#x20;</xsl:text><xsl:value-of select="$i18n.showImage.pictureCount"/><xsl:text>&#x20;</xsl:text><xsl:value-of select="gallery/numPics"/>
             	</td>
             	<td><xsl:text>&#x20;&#x20;|&#x20;&#x20;</xsl:text></td>
                <td>
             		<xsl:choose>
						<xsl:when test="gallery/prevImage">
							<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/image/{gallery/alias}/{gallery/prevImage}" title="{$i18n.showImage.previousLink.title}" >
							<img alt="{$i18n.showImage.previousImage.alt}" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/left.gif" /><xsl:text>&#x20;&#x20;</xsl:text><xsl:value-of select="$i18n.showImage.previousLink.text"/></a>
						</xsl:when>
						<xsl:otherwise>
							<div class="disabledtext"><img alt="{$i18n.showImage.previousImage.alt}" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/left_light.gif" /><xsl:text>&#x20;&#x20;</xsl:text><xsl:value-of select="$i18n.showImage.previousLink.text"/></div>
						</xsl:otherwise>
					</xsl:choose>
               	</td>
                <td><xsl:text>&#x20;&#x20;|&#x20;&#x20;</xsl:text></td>
                <td>
	                <xsl:choose>
						<xsl:when test="gallery/nextImage">
							<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/image/{gallery/alias}/{gallery/nextImage}" title="{$i18n.showImage.nextLink.title}" >
							<xsl:value-of select="$i18n.showImage.nextLink.text"/><xsl:text>&#x20;&#x20;</xsl:text><img alt="{$i18n.showImage.nextImage.alt}" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/right.gif" /></a>
						</xsl:when>
						<xsl:otherwise>
							<div class="disabledtext"><xsl:value-of select="$i18n.showImage.nextLink.text"/><xsl:text>&#x20;&#x20;</xsl:text><img alt="{$i18n.showImage.nextImage.alt}" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/right_light.gif" /></div>
						</xsl:otherwise>
					</xsl:choose>
                </td>
                <td><xsl:text>&#x20;&#x20;|&#x20;&#x20;</xsl:text></td>
                <td>
                    <a href="{/document/requestinfo/currentURI}/{/document/module/alias}/gallery/{gallery/alias}/{currentPage}" title="{$i18n.showImage.showThumbsLink.title}" ><xsl:value-of select="$i18n.showImage.showThumbsLink.text"/></a>
                </td>
                <td><xsl:text>&#x20;&#x20;|&#x20;&#x20;&#x20;&#x20;</xsl:text></td>
             </tr>
		</table>
		
        <div class="full marginleft">
        	<xsl:choose>
				<xsl:when test="gallery/file">
					<xsl:apply-templates select="gallery/file"/> 
				</xsl:when>
			</xsl:choose>			
     	</div>        	
	</xsl:template>
	
	<xsl:template match="comment">
		<div class="floatleft bordertop full">
			<div class="marginleft">
				<xsl:if test="/document/isAdmin='true'">
					<div class="margintop floatright">
			 			<a href="javascript:hideShow('showComment_{commentID}');hideShow('updateComment_{commentID}');" Title="{$i18n.comment.updateCommentLink.title}">
			 			<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/pen.png" alt=""/></a>			 			
			 			<a href="{/document/requestinfo/currentURI}/{/document/module/alias}/deleteComment/{commentID}" Title="{$i18n.comment.deleteCommentLink.title}">
			 			<img src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/delete.png" alt=""/></a>
					</div>
				</xsl:if>
				<div id="showComment_{commentID}">
					<p>
						<xsl:call-template name="replaceLineBreak">
							<xsl:with-param name="string" select="comment"/>
						</xsl:call-template>
					</p>
				</div>
				<xsl:if test="/document/isAdmin='true'">
					<div id="updateComment_{commentID}" style="display: none;">
						<form method="POST" action="{/document/requestinfo/currentURI}/{/document/module/alias}/updateComment/{commentID}">
							<textarea name="comment" class="full">
								<xsl:call-template name="replace-string">
									<xsl:with-param name="text" select="comment"/>
									<xsl:with-param name="from" select="'&#13;'"/>
									<xsl:with-param name="to" select="''"/>
								</xsl:call-template>						
							</textarea>
							
							<div class="floatright">
								<input type="submit" value="{$i18n.comment.submit}"/>
							</div>
							<br/>
							<br/>					
						</form>
					</div>
				</xsl:if>				
			</div>
			<div class="floatright">
				<i>
					<xsl:choose>
						<xsl:when test="user">
							<xsl:value-of select="user" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$i18n.comment.anonymousUser"/>
						</xsl:otherwise>
					</xsl:choose>
				</i>
				<br/>
				<xsl:value-of select="date" />
			</div>
		</div>
	</xsl:template>
	
	<xsl:template match="updateGallery">	
		<form name="updateForm" method="post" action="{/document/requestinfo/currentURI}/{/document/module/alias}/updateGallery/{gallery/alias}">
                <h1><xsl:value-of select="$i18n.updateGallery.header"/><xsl:text>&#x20;</xsl:text><xsl:value-of select="gallery/name"/></h1>
				<div class="full">
					<xsl:apply-templates select="validationException/validationError" />
					<p>
						<xsl:value-of select="$i18n.updateGallery.name"/><xsl:text>:</xsl:text>
						<br />
						<input type="text" name="name" size="54">
							<xsl:attribute name="value">
								<xsl:choose>
									<xsl:when test="requestparameters/parameter[name='name']/value">
										<xsl:value-of select="requestparameters/parameter[name='name']/value"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="gallery/name"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</input>
					</p>
					<p>
						<xsl:value-of select="$i18n.updateGallery.description"/><xsl:text>:</xsl:text>
						<br/>
						<textarea name="description" class="full medium">
							<xsl:choose>
								<xsl:when test="requestparameters/parameter[name='description']/value">
									<xsl:call-template name="replace-string">
										<xsl:with-param name="text" select="requestparameters/parameter[name='description']/value"/>
										<xsl:with-param name="from" select="'&#13;'"/>
										<xsl:with-param name="to" select="''"/>
									</xsl:call-template>									
								</xsl:when>
								<xsl:otherwise>
									<xsl:call-template name="replace-string">
										<xsl:with-param name="text" select="gallery/description"/>
										<xsl:with-param name="from" select="'&#13;'"/>
										<xsl:with-param name="to" select="''"/>
									</xsl:call-template>								
								</xsl:otherwise>
							</xsl:choose>
						</textarea>
					</p>
					<xsl:choose>
						<xsl:when test="not(path)">
							<p>
							<xsl:value-of select="$i18n.updateGallery.path"/><xsl:text>:</xsl:text>
							<br/>
							<input type="text" name="url" size="54">
								<xsl:attribute name="value">
									<xsl:choose>
										<xsl:when test="requestparameters/parameter[name='url']/value">
											<xsl:value-of select="requestparameters/parameter[name='url']/value" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="gallery/url"/>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:attribute>
							</input>
							</p>
						</xsl:when>
						<xsl:otherwise>
							<input type="hidden" name="url" size="54" value="{gallery/url}"/>
						</xsl:otherwise>
					</xsl:choose>
					<h2><xsl:value-of select="$i18n.updateGallery.permissions"/></h2>
					<fieldset>
						<legend><xsl:value-of select="$i18n.updateGallery.readAccess"/></legend>

						<table>
							<tr>
								<td colspan="2">
									<input type="checkbox" name="adminAccess">
										<xsl:choose>
											<xsl:when test="requestparameters">
												<xsl:if test="requestparameters/parameter[name='adminAccess']">
													<xsl:attribute name="checked">true</xsl:attribute>
												</xsl:if>	
											</xsl:when>
											<xsl:otherwise>
												<xsl:if test="gallery/adminAccess='true'">
													<xsl:attribute name="checked">true</xsl:attribute>
												</xsl:if>
											</xsl:otherwise>
										</xsl:choose>					
									</input><xsl:value-of select="$i18n.updateGallery.adminUsers"/><br/>								
								
									<input type="checkbox" name="userAccess">
										<xsl:choose>
											<xsl:when test="requestparameters">
												<xsl:if test="requestparameters/parameter[name='userAccess']">
													<xsl:attribute name="checked">true</xsl:attribute>
												</xsl:if>
											</xsl:when>
											<xsl:otherwise>
												<xsl:if test="gallery/userAccess='true'">
													<xsl:attribute name="checked">true</xsl:attribute>
												</xsl:if>
											</xsl:otherwise>
										</xsl:choose>
									</input><xsl:value-of select="$i18n.updateGallery.loggedInUsers"/><br/>							
								
									<input type="checkbox" name="anonymousAccess">
										<xsl:choose>
											<xsl:when test="requestparameters">
												<xsl:if test="requestparameters/parameter[name='anonymousAccess']">
													<xsl:attribute name="checked">true</xsl:attribute>
												</xsl:if>
											</xsl:when>
											<xsl:otherwise>
												<xsl:if test="gallery/anonymousAccess='true'">
													<xsl:attribute name="checked">true</xsl:attribute>
												</xsl:if>
											</xsl:otherwise>
										</xsl:choose>
									</input><xsl:value-of select="$i18n.updateGallery.anonymousUsers"/><br/>				
								</td>
							</tr>
						</table>
						<xsl:apply-templates select="groups"/>
						<xsl:apply-templates select="users"/>						
					</fieldset>

					<fieldset>
						<legend><xsl:value-of select="$i18n.updateGallery.uploadAccess"/></legend>
						
						<xsl:apply-templates select="groups" mode="upload"/>
						<xsl:apply-templates select="users" mode="upload"/>							
					</fieldset>

					<div class="floatright">
						<input type="submit" value="{$i18n.updateGallery.submit}"/>			
					</div>
				</div>
		</form>		
	</xsl:template>
	
	<xsl:template match="addGallery">	
		<form name="addGallery" method="post" action="{/document/requestinfo/currentURI}/{/document/module/alias}/addGallery" enctype="multipart/form-data">
            <h1><xsl:value-of select="$i18n.addGallery.header"/></h1>                    
            <div class="full">
				<xsl:apply-templates select="validationException/validationError"/>
				<p>
					<xsl:value-of select="$i18n.addGallery.name"/><xsl:text>:</xsl:text>
					<br />
					<input type="text" name="name" size="54" value="{requestparameters/parameter[name='name']/value}"/>
				</p>
				<p>
					<xsl:value-of select="$i18n.addGallery.description"/><xsl:text>:</xsl:text>
					<br/>
					<textarea name="description" class="full medium">
						<xsl:call-template name="replace-string">
							<xsl:with-param name="text" select="requestparameters/parameter[name='description']/value"/>
							<xsl:with-param name="from" select="'&#13;'"/>
							<xsl:with-param name="to" select="''"/>
						</xsl:call-template>
					</textarea>
				</p>
				<xsl:choose>
					<xsl:when test="not(path)">
						<p>
							<xsl:value-of select="$i18n.addGallery.path"/><xsl:text>:</xsl:text>
							<br/>
							<input type="text" name="url" size="54" value="{requestparameters/parameter[name='url']/value}"/>
						</p>
					</xsl:when>
					<xsl:otherwise>
						<input type="hidden" name="url" size="54" value="{path}"/>
						<input type="hidden" name="autogeneratedirs" size="54" value="true"/>
					</xsl:otherwise>
				</xsl:choose>
				<p>
					<input type="checkbox" name="uploadCheck" onclick="document.addGallery.zipFile.disabled=!this.checked">
						<xsl:if test="requestparameters/parameter[name='uploadCheck'][value='on']">
							<xsl:attribute name="checked">true</xsl:attribute>
						</xsl:if>
					</input>
					<xsl:value-of select="$i18n.addGallery.uploadFiles"/><br />
					<input type="file" name="zipFile" size="59" disabled="true" value="{requestparameters/parameter[name='zipFile']/value}"/>
					<xsl:if test="requestparameters/parameter[name='uploadCheck'][value='on']">
						<script>
							enableUpload('true');
						</script>
					</xsl:if>
				</p>

				<h2><xsl:value-of select="$i18n.addGallery.permissions"/></h2>
				<fieldset>
					<legend><xsl:value-of select="$i18n.addGallery.readAccess"/></legend>
					<table>
						<tr>
							<td colspan="2">
								<input type="checkbox" name="adminAccess">
									<xsl:if test="requestparameters/parameter[name='adminAccess']">
										<xsl:attribute name="checked">true</xsl:attribute>
									</xsl:if>						
								</input><xsl:value-of select="$i18n.addGallery.adminUsers"/><br/>						
														
								<input type="checkbox" name="userAccess">
									<xsl:if test="requestparameters/parameter[name='userAccess']">
										<xsl:attribute name="checked">true</xsl:attribute>
									</xsl:if>
								</input><xsl:value-of select="$i18n.addGallery.loggedInUsers"/><br/>
								
								<input type="checkbox" name="anonymousAccess">
									<xsl:if test="requestparameters/parameter[name='anonymousAccess']">
										<xsl:attribute name="checked">true</xsl:attribute>
									</xsl:if>
								</input><xsl:value-of select="$i18n.addGallery.anonymousUsers"/><br/>					
							</td>
						</tr>
					</table>
					<xsl:apply-templates select="groups"/>
					<xsl:apply-templates select="users"/>										
				</fieldset>
				
				<fieldset>
					<legend><xsl:value-of select="$i18n.addGallery.uploadAccess"/></legend>
					
					<xsl:apply-templates select="groups" mode="upload"/>
					<xsl:apply-templates select="users" mode="upload"/>							
				</fieldset>				
				
				<div class="floatright">
					<input type="submit" value="{$i18n.addGallery.submit}"/>			
				</div>
			</div>
		</form>
	</xsl:template>
	
	<xsl:template match="groups">
		<h3><xsl:value-of select="$i18n.groups.header"/></h3>

		<div class="scrolllist">			
			<xsl:apply-templates select="group"/>
		</div>
		
		<br/>
	</xsl:template>	
	
	<xsl:template match="users">
		<h3><xsl:value-of select="$i18n.users.header"/></h3>
		
		<div class="scrolllist">			
			<xsl:apply-templates select="user"/>
		</div>
		
		<br/>
	</xsl:template>
	
	<xsl:template match="groups" mode="upload">
		<h3><xsl:value-of select="$i18n.groups.header"/></h3>

		<div class="scrolllist">			
			<xsl:apply-templates select="group" mode="upload"/>
		</div>
		
		<br/>
	</xsl:template>	
	
	<xsl:template match="users" mode="upload">
		<h3><xsl:value-of select="$i18n.users.header"/></h3>
		
		<div class="scrolllist">			
			<xsl:apply-templates select="user" mode="upload"/>
		</div>
		
		<br/>
	</xsl:template>	
	
	<xsl:template match="group">
		<div class="floatleft full border marginbottom">
			<div class="floatleft">
				<xsl:choose>
					<xsl:when test="enabled='true'">
						<img class="alignbottom" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/group.png"/>
					</xsl:when>
					<xsl:otherwise>
						<img class="alignbottom" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/group_disabled.png"/>
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:text>&#x20;</xsl:text>
				
				<xsl:value-of select="name"/>			
			</div>
			<div class="floatright marginright">
				
				<xsl:variable name="groupID" select="groupID"/>
			
				<input type="checkbox" name="group" value="{groupID}">
					<xsl:choose>
						<xsl:when test="../../requestparameters">
							<xsl:if test="../../requestparameters/parameter[name='group'][value=$groupID]">
								<xsl:attribute name="checked"/>
							</xsl:if>	 			
						</xsl:when>
						<xsl:when test="../../gallery">
							<xsl:if test="../../gallery/allowedGroupIDs[groupID=$groupID]">
								<xsl:attribute name="checked"/>
							</xsl:if>								
						</xsl:when>					
					</xsl:choose>
				</input>
			</div>				
		</div>
	</xsl:template>
	
	<xsl:template match="user">
		<div class="floatleft full border marginbottom">
			<div class="floatleft">
				<xsl:choose>
					<xsl:when test="enabled='true'">
						<img class="alignbottom" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/user.png"/>
					</xsl:when>
					<xsl:otherwise>
						<img class="alignbottom" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/user_disabled.png"/>
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:text>&#x20;</xsl:text>
				
				<xsl:value-of select="firstname"/>
				
				<xsl:text>&#x20;</xsl:text>
				
				<xsl:value-of select="lastname"/>
				
				<xsl:text>&#x20;</xsl:text>
				
				<xsl:text>(</xsl:text>
					<xsl:value-of select="username"/>
				<xsl:text>)</xsl:text>			
			</div>
			<div class="floatright marginright">
				
				<xsl:variable name="userID" select="userID"/>
			
				<input type="checkbox" name="user" value="{userID}">
					<xsl:choose>
						<xsl:when test="../../requestparameters">
							<xsl:if test="../../requestparameters/parameter[name='user'][value=$userID]">
								<xsl:attribute name="checked"/>
							</xsl:if>			
						</xsl:when>						
						<xsl:when test="../../gallery">
							<xsl:if test="../../gallery/allowedUserIDs[userID=$userID]">
								<xsl:attribute name="checked"/>
							</xsl:if>								
						</xsl:when>							
					</xsl:choose>
				</input>
			</div>				
		</div>
	</xsl:template>
	
	<xsl:template match="group" mode="upload">
		<div class="floatleft full border marginbottom">
			<div class="floatleft">
				<xsl:choose>
					<xsl:when test="enabled='true'">
						<img class="alignbottom" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/group.png"/>
					</xsl:when>
					<xsl:otherwise>
						<img class="alignbottom" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/group_disabled.png"/>
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:text>&#x20;</xsl:text>
				
				<xsl:value-of select="name"/>			
			</div>
			<div class="floatright marginright">
				
				<xsl:variable name="groupID" select="groupID"/>
			
				<input type="checkbox" name="uploadgroup" value="{groupID}">
					<xsl:choose>
						<xsl:when test="../../requestparameters">
							<xsl:if test="../../requestparameters/parameter[name='uploadgroup'][value=$groupID]">
								<xsl:attribute name="checked"/>
							</xsl:if>	 			
						</xsl:when>
						<xsl:when test="../../gallery">
							<xsl:if test="../../gallery/allowedUploadGroupIDs[groupID=$groupID]">
								<xsl:attribute name="checked"/>
							</xsl:if>								
						</xsl:when>					
					</xsl:choose>
				</input>
			</div>				
		</div>
	</xsl:template>
	
	<xsl:template match="user" mode="upload">
		<div class="floatleft full border marginbottom">
			<div class="floatleft">
				<xsl:choose>
					<xsl:when test="enabled='true'">
						<img class="alignbottom" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/user.png"/>
					</xsl:when>
					<xsl:otherwise>
						<img class="alignbottom" src="{/document/requestinfo/contextpath}/static/f/{/document/module/sectionID}/{/document/module/moduleID}/pics/user_disabled.png"/>
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:text>&#x20;</xsl:text>
				
				<xsl:value-of select="firstname"/>
				
				<xsl:text>&#x20;</xsl:text>
				
				<xsl:value-of select="lastname"/>
				
				<xsl:text>&#x20;</xsl:text>
				
				<xsl:text>(</xsl:text>
					<xsl:value-of select="username"/>
				<xsl:text>)</xsl:text>			
			</div>
			<div class="floatright marginright">
				
				<xsl:variable name="userID" select="userID"/>
			
				<input type="checkbox" name="uploaduser" value="{userID}">
					<xsl:choose>
						<xsl:when test="../../requestparameters">
							<xsl:if test="../../requestparameters/parameter[name='uploaduser'][value=$userID]">
								<xsl:attribute name="checked"/>
							</xsl:if>			
						</xsl:when>						
						<xsl:when test="../../gallery">
							<xsl:if test="../../gallery/allowedUploadUserIDs[userID=$userID]">
								<xsl:attribute name="checked"/>
							</xsl:if>								
						</xsl:when>							
					</xsl:choose>
				</input>
			</div>				
		</div>
	</xsl:template>	
	
	<xsl:template match="addImages">	
		<form name="addImages" method="post" action="{/document/requestinfo/currentURI}/{/document/module/alias}/addImages/{gallery/alias}" enctype="multipart/form-data">
            <h1><xsl:value-of select="$i18n.addImages.header"/></h1>                    
            <div class="full">
				<xsl:apply-templates select="validationException/validationError"/>
				<p>
					<xsl:value-of select="$i18n.addImages.text"/><br />
					<input type="file" name="fileUpload" size="59" value="{requestparameters/parameter[name='file']/value}"/>
				</p>
				<p>
					<xsl:value-of select="$i18n.addImages.diskThreshold"/><xsl:text> </xsl:text><xsl:value-of select="diskThreshold"/> MB
				</p>
				<div class="floatright">
					<input type="submit" value="{$i18n.addImages.submit}"/>			
				</div>
			</div>
		</form>
	</xsl:template>
	
	<xsl:template match="validationError">
		<xsl:if test="fieldName and validationErrorType">
			<p class="error">
				<xsl:choose>
					<xsl:when test="validationErrorType='RequiredField'">
						<xsl:value-of select="$i18n.validationError.RequiredField"/>
					</xsl:when>
					<xsl:when test="validationErrorType='InvalidFormat'">
						<xsl:value-of select="$i18n.validationError.InvalidFormat"/>
					</xsl:when>
					<xsl:when test="validationErrorType='Other'">
						<xsl:value-of select="$i18n.validationError.Other"/>
					</xsl:when>	
					<xsl:otherwise>
						<xsl:value-of select="$i18n.validationError.unknownValidationErrorType"/>
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:text>&#x20;</xsl:text>
				
				<xsl:choose>
					<xsl:when test="fieldName = 'name'">
						<xsl:value-of select="$i18n.validationError.field.name"/>!
					</xsl:when>
					<xsl:when test="fieldName = 'description'">
						<xsl:value-of select="$i18n.validationError.field.description"/>!
					</xsl:when>
					<xsl:when test="fieldName = 'commentText'">
						<xsl:value-of select="$i18n.validationError.field.comment"/>!
					</xsl:when>
					<xsl:when test="fieldName = 'url'">
						<xsl:value-of select="$i18n.validationError.field.url"/>!
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="fieldName"/>
					</xsl:otherwise>
				</xsl:choose>	
			</p>
		</xsl:if>
	
		<xsl:if test="messageKey">
			<p class="error">
				<xsl:choose>
					<xsl:when test="messageKey='BadFileFormat'">
						<xsl:value-of select="$i18n.validationError.messageKey.BadFileFormat"/>
					</xsl:when>
					<xsl:when test="messageKey='FileSizeLimitExceeded'">
						<xsl:value-of select="$i18n.validationError.messageKey.FileSizeLimitExceeded"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$i18n.validationError.unknownMessageKey"/>
					</xsl:otherwise>
				</xsl:choose>
			</p>
		</xsl:if>
	
		<xsl:apply-templates select="message"/>	
	</xsl:template>
		
	<xsl:template match="gallery/file">
		 <a href="{/document/requestinfo/currentURI}/{/document/module/alias}/getImage/{../alias}/{filename}" Target="_blank" Title="{$i18n.gallery.file.showFullImageLink.title}">
		 <img src="{/document/requestinfo/currentURI}/{/document/module/alias}/mediumThumb/{../alias}/{filename}" alt=""/></a>

		 <div class="full">
			<div>
				<div class="floatleft">
					<table>
				 		<tr>
							<td>
								<b> <xsl:value-of select="$i18n.gallery.file.comments"/> (<xsl:choose>
									<xsl:when test="comments/commentsNum">
										<xsl:value-of select="comments/commentsNum"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:text>0</xsl:text>
									</xsl:otherwise>
								</xsl:choose>)</b>
							</td>
							
							<xsl:choose>
								<xsl:when test="comments/commentsNum > 0">
									<td><xsl:text>&#x20;&#x20;|&#x20;&#x20;</xsl:text></td>
									<td>    
										<form name="viewComments" method="post" action="{/document/requestinfo/currentURI}/{/document/module/alias}/image/{../alias}/{filename}">
						    				<input type="hidden" name="viewComments" />	
							    			<xsl:choose>
								    			<xsl:when test="comments/showAll">
								    					<a href="javascript:viewComments('false')" title="Dölj kommentarer"><xsl:value-of select="$i18n.gallery.file.hide.comments"/></a>
								    			</xsl:when>
								    			<xsl:otherwise>
									    				<a href="javascript:viewComments('true')" title="Visa alla kommentarer"><xsl:value-of select="$i18n.gallery.file.show.comments"/></a>
								    			</xsl:otherwise>
							    			</xsl:choose>
						    			</form>
									</td>
								</xsl:when>
							</xsl:choose>
				 		</tr>
					</table>
				</div>
				<br /><br />
			</div>
			<xsl:choose>
				<xsl:when test="comments/commentsNum > 0">
					<xsl:apply-templates select="comments/comment"/>
				</xsl:when>
				<xsl:otherwise>
					<div class="bordertop full">
						<p><xsl:value-of select="$i18n.gallery.file.noComments"/></p>
					</div>
				</xsl:otherwise>
			</xsl:choose>
		</div>
		
		<xsl:if test="commentsAllowed or /document/isAdmin='true'">
			<form name="addCommentText" method="post">
				<xsl:apply-templates select="validationException/validationError" />
				<xsl:value-of select="$i18n.gallery.file.addcomment"/>
				<textarea name="commentText" rows="2" class="full"/>
				<div class="floatright">
					<input type="submit" value="{$i18n.gallery.file.submit}"/>
				</div>
			</form>
		</xsl:if>
	</xsl:template>			
		
</xsl:stylesheet>

