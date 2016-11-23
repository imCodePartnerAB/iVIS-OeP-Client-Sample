package se.unlogic.log4jutils.logging;

import org.apache.log4j.RollingFileAppender;


public class RelativeRollingFileAppender extends RollingFileAppender {

	private String pathName;
	
	@Override
	public void setFile(String file) {
		super.setFile(this.getPath() + file);
	}
	 
	public void setPathName(String pathName) {
		
		if(pathName == null){
			throw new NullPointerException("Pathname cannot be null!");
		}else{
			this.pathName = pathName;
		}
	}
	
	public String getPathName() {
		return pathName;
	}
	
	private String getPath(){
		
		if(this.pathName == null){
			throw new RelativePathNameNotSetException();
		}else{
			String path = RelativePathHandler.getPath(pathName);
			
			if(path == null){
				throw new RelativePathNotSetException(pathName);
			}else{
				return path;
			}			
		}
	}
}
