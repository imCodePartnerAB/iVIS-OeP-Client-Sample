package se.unlogic.standardutils.time;

import se.unlogic.standardutils.xml.GeneratedElementable;


public class SplitTime extends GeneratedElementable{

	protected int milliseconds;
	protected int seconds;
	protected int minutes;
	protected int hours;

	public SplitTime(long time){

		milliseconds = (int)(time % 1000);
		seconds = (int)((time/1000) % 60);
		minutes = (int)((time/60000) % 60);
		hours = (int)((time/3600000) % 24);
	}

	public SplitTime(int milliseconds, int seconds, int minutes, int hours) {

		super();
		this.milliseconds = milliseconds;
		this.seconds = seconds;
		this.minutes = minutes;
		this.hours = hours;
	}

	public int getMilliseconds() {

		return milliseconds;
	}

	public int getSeconds() {

		return seconds;
	}

	public int getMinutes() {

		return minutes;
	}

	public int getHours() {

		return hours;
	}
}
