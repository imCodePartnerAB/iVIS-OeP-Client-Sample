package se.unlogic.log4jutils.logging;

import java.util.ArrayList;

import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.helpers.LogLog;

import se.unlogic.standardutils.time.MillisecondTimeUnits;

public class ThrottledSMTPAppender extends SMTPAppender {

	private long timeFrameMillis = 1 * MillisecondTimeUnits.MINUTE;
	private int maxEmails = 3;
	private long sleepTimeMillis = 5 * MillisecondTimeUnits.MINUTE;

	private Long enteredSleep;
	private ArrayList<Long> timestamps = new ArrayList<Long>(5);
	private String throttledMessage = "Limit reached throttling messages";

	@Override
	protected boolean checkEntryConditions() {

		if (super.checkEntryConditions()) {

			long timeNowMillis = System.currentTimeMillis();

			if (enteredSleep == null || enteredSleep < (timeNowMillis - sleepTimeMillis)) {

				if (enteredSleep != null) {
					enteredSleep = null;
				}

				if (timestamps.size() < maxEmails) {

					timestamps.add(timeNowMillis);

				} else {
					
					if ((timestamps.get(0) + timeFrameMillis) > timeNowMillis) {
						enteredSleep = timeNowMillis;
						timestamps.clear();
						sendThrottledMessage();
						return false;

					}
					
					timestamps.remove(0);
					timestamps.add(timeNowMillis);

				}

				return true;
			}
		}

		return false;
	}

	protected void sendThrottledMessage() {

		try {
			MimeMessage message = new MimeMessage(createSession());
			addressMessage(message);
			message.setSubject(getSubject());
			message.setContent(throttledMessage, layout.getContentType());
			Transport.send(message);
		} catch (Exception e) {
			LogLog.error("Error occured while sending e-mail notification.", e);
		}
	}

	public long getTimeFrameMillis() {

		return timeFrameMillis;
	}

	public void setTimeFrameMillis(long timeFrameMillis) {

		this.timeFrameMillis = timeFrameMillis;
	}

	public int getMaxEmails() {

		return maxEmails;
	}

	public void setMaxEmails(int maxEmails) {

		this.maxEmails = maxEmails;
	}

	public long getSleepTimeMillis() {

		return sleepTimeMillis;
	}

	public void setSleepTimeMillis(long sleepTimeMillis) {

		this.sleepTimeMillis = sleepTimeMillis;
	}

	public Long getEnteredSleep() {

		return enteredSleep;
	}

	public void setEnteredSleep(Long enteredSleep) {

		this.enteredSleep = enteredSleep;
	}

	public ArrayList<Long> getTimestamps() {

		return timestamps;
	}

	public void setTimestamps(ArrayList<Long> timestamps) {

		this.timestamps = timestamps;
	}

	public String getThrottledMessage() {

		return throttledMessage;
	}

	public void setThrottledMessage(String throttledMessage) {

		this.throttledMessage = throttledMessage;
	}

}
