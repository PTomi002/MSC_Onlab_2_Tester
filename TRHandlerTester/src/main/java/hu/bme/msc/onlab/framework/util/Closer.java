package hu.bme.msc.onlab.framework.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Closer {

	private static final Logger LOGGER = LoggerFactory.getLogger(Closer.class);
	
	private Closer() {
	}
	
	public static void close(List<Closeable> objects) {
		if (objects != null) {
			objects.stream().forEach((object) -> close(object));
		}
	}

	public static void close(Closeable object) {
		if (object != null) {
			try {
				object.close();
			} catch (IOException e) {
				LOGGER.warn("Could not close Closeable object!", e);
			}
		}
	}
}
