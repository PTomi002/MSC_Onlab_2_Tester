package hu.bme.msc.onlab.framework.util;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.internal.Maps;

import hu.bme.msc.onlab.framework.eventhandling.EventListener;

public class EventListenerHolder {
	private static final Logger LOGGER = LoggerFactory.getLogger(EventListenerHolder.class);

	// Initialisation-on-demand holder idiom - thread safe
	private static class EventListenerHolderWrapper {
		static final EventListenerHolder INSTANCE = new EventListenerHolder();

		private EventListenerHolderWrapper() {
		}
	}

	private Map<String, EventListener> eventListeners = Collections.synchronizedMap(Maps.newHashMap());

	public static EventListenerHolder getInstance() {
		return EventListenerHolderWrapper.INSTANCE;
	}

	public void addEventListener(String eventListenerClass) {
		try {
			LOGGER.info("Trying to add " + eventListenerClass);
			Class<?> eventListener = Class.forName(eventListenerClass);
			eventListeners.put(eventListenerClass, (EventListener) eventListener.newInstance());
		} catch (ClassNotFoundException e) {
			LOGGER.warn("Class does not exists on the classpath: " + eventListenerClass, e);
		} catch (Exception e) {
			LOGGER.error("Could not instantiate class: " + eventListenerClass, e);
		}
	}

	public void runAll() {
		eventListeners.entrySet().stream().map(Map.Entry::getValue).forEach(eventListener -> {
			LOGGER.info("Running: " + eventListener);
			eventListener.start();
		});
	}

	public void stopAll() {
		eventListeners.entrySet().stream().map(Map.Entry::getValue).forEach(eventListener -> {
			LOGGER.info("Stopping: " + eventListener);
			eventListener.stop();
		});
	}
}
