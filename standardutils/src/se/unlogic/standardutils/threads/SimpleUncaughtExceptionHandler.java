package se.unlogic.standardutils.threads;

import java.lang.Thread.UncaughtExceptionHandler;


public class SimpleUncaughtExceptionHandler implements UncaughtExceptionHandler {

	protected Thread thread;
	protected Throwable throwable;

	public void uncaughtException(Thread t, Throwable e) {

		this.thread = t;
		this.throwable = e;
	}

	public Thread getThread() {

		return thread;
	}


	public Throwable getThrowable() {

		return throwable;
	}
}
