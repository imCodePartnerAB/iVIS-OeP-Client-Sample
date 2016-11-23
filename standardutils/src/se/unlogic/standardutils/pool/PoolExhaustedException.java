package se.unlogic.standardutils.pool;


public class PoolExhaustedException extends RuntimeException {

	private static final long serialVersionUID = 3824608482574610194L;

	public PoolExhaustedException(String message) {

		super(message);
	}
}
