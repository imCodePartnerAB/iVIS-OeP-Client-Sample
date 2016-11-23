package com.nordicpeak.flowengine.integration.callback;

public class IntegrationAttachment {

	private String filename;

	private Long size;

	private String encodedData;

	public String getFilename() {

		return filename;
	}

	public void setFilename(String filename) {

		this.filename = filename;
	}

	public Long getSize() {

		return size;
	}

	public void setSize(Long size) {

		this.size = size;
	}

	public String getEncodedData() {

		return encodedData;
	}

	public void setEncodedData(String encodedData) {

		this.encodedData = encodedData;
	}
}
