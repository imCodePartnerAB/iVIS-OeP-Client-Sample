package se.unlogic.log4jutils.logging;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.TriggeringEventEvaluator;


public class AllTriggeringEventEvaluator implements TriggeringEventEvaluator {

	public boolean isTriggeringEvent(LoggingEvent arg0) {

		return true;
	}

}
