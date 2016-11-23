package se.unlogic.standardutils.pool;


public class PoolFullException extends RuntimeException {

	private static final long serialVersionUID = -3974643924830367383L;

	public PoolFullException(String message) {

		super(message);
	}

}
