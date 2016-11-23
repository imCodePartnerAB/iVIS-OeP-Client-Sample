package se.unlogic.standardutils.db.tableversionhandler;

public class TableUpgradeException extends Exception {

	private static final long serialVersionUID = -6207429697372697474L;

	public TableUpgradeException() {

		super();
	}

	public TableUpgradeException(String message, Throwable cause) {

		super(message, cause);
	}

	public TableUpgradeException(String message) {

		super(message);
	}

	public TableUpgradeException(Throwable cause) {

		super(cause);
	}

}
