/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.imagegallery.populators;

import java.sql.ResultSet;
import java.sql.SQLException;

import se.unlogic.hierarchy.foregroundmodules.imagegallery.beans.Picture;
import se.unlogic.standardutils.dao.BeanResultSetPopulator;

public class PicturePopulator implements BeanResultSetPopulator<Picture> {
		
	@Override
	public Picture populate(ResultSet rs) throws SQLException {
	
		Picture picture = new Picture();	
		
		picture.setPictureID(rs.getInt("pictureID"));
		picture.setFilename(rs.getString("filename"));
		picture.setGalleryID(rs.getInt("galleryID"));
		
		int columnCount = rs.getMetaData().getColumnCount();
		
		if(columnCount > 3){
			
			for(int columnIndex = 3;columnIndex < columnCount; columnIndex++){
				
				if(rs.getMetaData().getColumnName(columnIndex).equals("smallThumb")){
					picture.setSmallThumb(rs.getBlob(columnIndex));
				}else if(rs.getMetaData().getColumnName(columnIndex).equals("mediumThumb")){
					picture.setMediumThumb(rs.getBlob("mediumThumb"));
				}
			}
		}

		return picture ;
	}
}
