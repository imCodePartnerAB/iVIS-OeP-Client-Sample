package se.unlogic.hierarchy.foregroundmodules.test;

import java.io.Serializable;


public class TestEvent implements Serializable{

	private static final long serialVersionUID = -5042371872486105543L;
	
	private long sent;
	private String value;

	
	public long getSent() {
	
		return sent;
	}

	
	public void setSent(long sent) {
	
		this.sent = sent;
	}

	
	public String getValue() {
	
		return value;
	}

	
	public void setValue(String value) {
	
		this.value = value;
	}


	@Override
	public String toString() {

		return value + " (sent: " + sent + ")";
	}
}
