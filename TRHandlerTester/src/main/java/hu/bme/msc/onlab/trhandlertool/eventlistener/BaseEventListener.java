package hu.bme.msc.onlab.trhandlertool.eventlistener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseEventListener {
	protected final String name;

	protected final Logger LOGGER;

	public BaseEventListener(String name) {
		this.name = name;
		LOGGER = LoggerFactory.getLogger(name);
	}
	
}
