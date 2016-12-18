package hu.bme.msc.onlab.framework.eventhandling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventHolder {
	private static final Logger LOGGER = LoggerFactory.getLogger(EventHolder.class);
	
	private EventHolder() {
		// Not implemented yet.
	}
	
	public void hello() {
		LOGGER.info("Hello!");
	}
}
