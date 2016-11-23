package se.unlogic.emailutils.framework;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;


public class ByteArrayDatasource implements DataSource{

	private final byte[] byteArray;
	private final String contentType;
	private final String filename;

	public ByteArrayDatasource(byte[] byteArray, String contentType, String filename) {

		super();
		this.byteArray = byteArray;
		this.contentType = contentType;
		this.filename = filename;
	}

	public String getContentType() {

		return contentType;
	}

	public InputStream getInputStream() throws IOException {

		return new ByteArrayInputStream(byteArray);
	}

	public String getName() {

		return filename;
	}

	public OutputStream getOutputStream() throws IOException {

		throw new RuntimeException("Operation not supported");
	}
}
