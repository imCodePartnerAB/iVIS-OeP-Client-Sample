package se.unlogic.purecaptcha;

import java.io.IOException;
import java.net.SocketException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CaptchaHandler {

	protected Captcha captcha;
	protected String sessionAttribute;
	protected long validationTimeout;
	protected boolean caseSensitive;

	public CaptchaHandler(Captcha captcha, String sessionAttribute, long validationTimeout, boolean caseSensitive) {

		super();
		this.captcha = captcha;
		this.sessionAttribute = sessionAttribute;
		this.validationTimeout = validationTimeout;
		this.caseSensitive = caseSensitive;
	}

	public void getCaptchaImage(HttpServletRequest req, HttpServletResponse res) {

		try {
			CaptchaImage captchaImage = captcha.generateCaptchaImage();

			req.getSession(true).setAttribute(sessionAttribute, new CaptchaCode(captchaImage.getCode()));

			// Set standard HTTP/1.1 no-cache headers.
			res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, proxy-revalidate");
			res.setHeader("Pragma", "no-cache");
			res.setDateHeader("Expires", 0);
			res.setContentType("image/jpeg");

			ImageIO.write(captchaImage.getBufferedImage(), "jpg", res.getOutputStream());

			res.getOutputStream().flush();
			res.getOutputStream().close();

		} catch (IllegalArgumentException e) {
			return;
		} catch (SocketException e) {
			return;
		} catch (IOException e) {
			return;
		} catch (IllegalStateException e) {
			return;
		}
	}

	public boolean isValidCode(HttpServletRequest req, String code){

		try{

			HttpSession session = req.getSession();

			if(session == null){

				return false;
			}

			CaptchaCode captchaCode = (CaptchaCode)session.getAttribute(sessionAttribute);

			if(captchaCode == null){

				return false;

			}

			session.removeAttribute(sessionAttribute);

			if(captchaCode.getTimestamp() < (System.currentTimeMillis() - validationTimeout)){

				//Timestamp has expired
				return false;
			}

			if(caseSensitive && captchaCode.getCode().equals(code)){

				return true;

			}else if(!caseSensitive && captchaCode.getCode().equalsIgnoreCase(code)){

				return true;
			}

		} catch (IllegalStateException e) {}

		return false;
	}
}
