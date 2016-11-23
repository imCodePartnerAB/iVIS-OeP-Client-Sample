package se.unlogic.purecaptcha;

import java.awt.image.BufferedImage;

public class CaptchaImage {

	protected final BufferedImage bufferedImage;
	protected final String code;

	public CaptchaImage(BufferedImage bufferedImage, String code) {

		super();
		this.bufferedImage = bufferedImage;
		this.code = code;
	}

	public BufferedImage getBufferedImage() {

		return bufferedImage;
	}

	public String getCode() {

		return code;
	}
}
