package se.unlogic.standardutils.threads;



public class ThreadUtils {

	public static void run(Runnable runnable, String threadName, boolean daemon){

		Thread thread = new Thread(runnable, threadName);

		thread.setName(threadName);

		thread.setDaemon(daemon);

		thread.start();
	}

	public static void runAndWait(Runnable runnable, String threadName, boolean daemon) throws InterruptedException, Throwable{

		Thread thread = new Thread(runnable, threadName);

		thread.setName(threadName);

		thread.setDaemon(daemon);

		SimpleUncaughtExceptionHandler handler = new SimpleUncaughtExceptionHandler();

		thread.setUncaughtExceptionHandler(handler);

		thread.start();

		thread.join();

		if(handler.getThrowable() != null){

			throw handler.getThrowable();
		}
	}

	public static void sleep(long millis){

		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {}
	}
	
	@SuppressWarnings("deprecation")
	public static boolean killThread(String name){
		
		Thread thread = getThread(name);
		
		if(thread != null){
			
			thread.stop();
			return true;
		}
		
		return false;
	}
	
	public static boolean interruptThread(String name){
		
		Thread thread = getThread(name);
		
		if(thread != null){
			
			thread.interrupt();
			return true;
		}
		
		return false;
	}
	
	public static Thread getThread(String name){
		
		for(Thread thread : Thread.getAllStackTraces().keySet()){
			
			if(thread.getName().equals(name)){
				
				return thread;
			}
		}
		
		return null;
	}
}
