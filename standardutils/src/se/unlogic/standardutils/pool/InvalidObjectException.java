package se.unlogic.standardutils.pool;


public class InvalidObjectException extends RuntimeException {

	private static final long serialVersionUID = 12042994226368903L;

	public InvalidObjectException() {

		super();
	}

	public InvalidObjectException(String message, Throwable cause) {

		super(message, cause);
	}

	public InvalidObjectException(String message) {

		super(message);
	}

	public InvalidObjectException(Throwable cause) {

		super(cause);
	}

}
