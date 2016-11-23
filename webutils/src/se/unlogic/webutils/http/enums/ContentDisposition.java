package se.unlogic.webutils.http.enums;


public enum ContentDisposition {

	ATTACHMENT("attachment"),
	INLINE("inline");
	
	private final String value;
	
	private ContentDisposition(String value){
	
		this.value = value;
	}
	
	public String getValue(){
		
		return value;
	}
}
