package se.unlogic.hierarchy.foregroundmodules.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import se.unlogic.standardutils.time.MillisecondTimeUnits;

public class RetryLimiter {

	private long lockoutTime;
	private int retries;
	private int retryInterval;

	protected Map<String, LockRecord> failedUsers = null;

	public RetryLimiter(boolean activated, int lockoutTime, int retries, int retryInterval) {
		
		this.lockoutTime = lockoutTime * MillisecondTimeUnits.SECOND;
		this.retries = retries;
		this.retryInterval = retryInterval;
		
		this.failedUsers = new HashMap<String, LockRecord>();
	}

	public synchronized void registerAuthSuccess(String username) {

		failedUsers.remove(username);
	}

	public boolean registerAuthFailure(String username) {

		LockRecord lockRecord = null;

		synchronized(this){
			if(!failedUsers.containsKey(username)){

				lockRecord = new LockRecord();
				failedUsers.put(username, lockRecord);
			}else{
				
				lockRecord = failedUsers.get(username);
			}
		}
		
		return lockRecord.registerFailure(System.currentTimeMillis(), retries, retryInterval, lockoutTime);
	}
	
	public boolean isLocked(String username) {

		LockRecord lockRecord = null;

		synchronized(this){
			if(!failedUsers.containsKey(username)){

				lockRecord = new LockRecord();
				failedUsers.put(username, lockRecord);
			}else{
				
				lockRecord = failedUsers.get(username);
			}
		}
		
		return lockRecord.isLocked(System.currentTimeMillis(), lockoutTime);
	}

	public long getLockoutTime() {

		return lockoutTime;
	}

	public void setLockoutTime(int lockoutTime) {

		this.lockoutTime = lockoutTime * MillisecondTimeUnits.SECOND;
	}
	
	public int getRemainingLockoutTime(String username) {
		LockRecord lockRecord = null;

		synchronized(this){
			lockRecord = failedUsers.get(username);
		}
		
		return lockRecord.getRemainingLockoutTime(lockoutTime, System.currentTimeMillis());
	}

	public int getRetries() {

		return retries;
	}

	public void setRetries(int retries) {

		this.retries = retries;
	}

	public int getRetryInterval() {

		return retryInterval;
	}

	public void setRetryInterval(int retryInterval) {

		this.retryInterval = retryInterval;
	}

	protected static class LockRecord {

		private ArrayList<Long> failureTimes = new ArrayList<Long>();
		private long lockTime = 0;

		public ArrayList<Long> getFailureTimes() {

			return failureTimes;
		}

		public boolean registerFailure(long currentTime, int retries, int retryInterval, long lockoutTime) {

			failureTimes.add(currentTime);

			return purgeAndCheck(currentTime, retries, retryInterval, lockoutTime);
		}

		public boolean isLocked(long currentTime, long lockoutTime) {
			if((currentTime - lockTime) <= lockoutTime){
				return true;
			}
			
			return false;
		}
		
		public int getRemainingLockoutTime(long lockoutTime, long currentTime) {

			long timePassed = currentTime - lockTime;
			long timeLeft = lockoutTime - timePassed;

			int timeLeftMinutes = (int)(timeLeft / MillisecondTimeUnits.SECOND / 60);
			if(timeLeftMinutes == 0){
				timeLeftMinutes = 1;
			}
			
			return timeLeftMinutes;
		}

		private boolean purgeAndCheck(long currentTime, int retries, int retryInterval, long lockoutTime) {

			if(isLocked(currentTime, lockoutTime)){
				return true;
			}

			for(int i = 0; i < failureTimes.size(); i++){
				if((retryInterval * MillisecondTimeUnits.SECOND) < (currentTime - failureTimes.get(i))){
					failureTimes.remove(i);
				}
			}

			if(retries <= failureTimes.size()){
				lockTime = System.currentTimeMillis();

				return true;
			}
			
			return false;
		}
	}
}
