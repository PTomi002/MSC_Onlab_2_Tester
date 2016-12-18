package hu.bme.msc.onlab.trhandlertool.eventlistener;

import org.testng.SkipException;

import hu.bme.msc.onlab.framework.eventhandling.EventListener;

public class NotificationJmsEventListener extends BaseEventListener implements EventListener {

	public NotificationJmsEventListener(String name, String url) {
		super(name);
		LOGGER.info("Openwire URL: " + url);
	}

	@Override
	public void start() throws SkipException {
		// Not implemented yet.
	}

	@Override
	public void stop() {
		// Not implemented yet.
	}
}
