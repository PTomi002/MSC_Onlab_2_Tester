package hu.bme.msc.onlab.framework.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Event {
	private static final Logger LOGGER = LoggerFactory.getLogger(Event.class);

	private Event() {
		// Not implemented yet.
	}

	public void hello() {
		LOGGER.info("Hello!");
	}
}
