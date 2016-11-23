package se.unlogic.log4jutils.levels;

import org.apache.log4j.Level;


public enum LogLevel {

	  OFF(Level.OFF),
	  FATAL(Level.FATAL),
	  ERROR(Level.ERROR),
	  WARN(Level.WARN),
	  INFO(Level.INFO),
	  DEBUG(Level.DEBUG),
	  TRACE(Level.TRACE),
	  ALL(Level.ALL);
	  
	  private final Level level;
	  
	  private LogLevel(Level level){
		  
		  this.level = level;
	  }
	  
	  public Level getLevel(){
		  
		  return level;
	  }
}
