package se.unlogic.standardutils.operation;

public interface ProgressListener {

	public void incrementCurrentPosition();

	public void incrementCurrentPosition(long value);

}