package se.unlogic.purecaptcha;

public class CaptchaCode {

	protected String code;
	protected long timestamp;

	public CaptchaCode(String code) {

		super();
		this.code = code;
		this.timestamp = System.currentTimeMillis();
	}

	public String getCode() {

		return code;
	}

	public long getTimestamp() {

		return timestamp;
	}

}
