package se.unlogic.log4jutils.logging;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

public class SMTPAppender extends org.apache.log4j.net.SMTPAppender {

	private static final AllTriggeringEventEvaluator EVENT_EVALUATOR = new AllTriggeringEventEvaluator();

	private int timeout = 15000;

	public SMTPAppender() {

		setEvaluator(EVENT_EVALUATOR);
	}

	public int getTimeout() {

		return timeout;
	}

	public void setTimeout(int timeout) {

		this.timeout = timeout;
	}

	@Override
	protected Session createSession() {

		Properties props = null;
		try {
			props = new Properties(System.getProperties());
		} catch (SecurityException ex) {
			props = new Properties();
		}

	    if (timeout > 0) { 

	        String timeoutStr = Integer.toString(timeout); 
	        props.setProperty("mail.smtp.connectiontimeout", timeoutStr); 
	        props.setProperty("mail.smtp.timeout", timeoutStr); 
	      } 		
		
		String prefix = "mail.smtp";
		
		if (getSMTPProtocol() != null) {
			props.put("mail.transport.protocol", getSMTPProtocol());
			prefix = "mail." + getSMTPProtocol();
		}
		
		if (getSMTPHost() != null) {
			props.put(prefix + ".host", getSMTPHost());
		}
		
		if (getSMTPPort() > 0) {
			props.put(prefix + ".port", String.valueOf(getSMTPPort()));
		}

		Authenticator auth = null;
		
		if (getSMTPPassword() != null && getSMTPUsername() != null) {
		
			props.put(prefix + ".auth", "true");
			auth = new Authenticator() {

				@Override
				protected PasswordAuthentication getPasswordAuthentication() {

					return new PasswordAuthentication(getSMTPUsername(), getSMTPPassword());
				}
			};
		}
		Session session = Session.getInstance(props, auth);

		if (getSMTPProtocol() != null) {
			session.setProtocolForAddress("rfc822", getSMTPProtocol());
		}
		
		if (getSMTPDebug()) {
			session.setDebug(getSMTPDebug());
		}
		return session;
	}
}
