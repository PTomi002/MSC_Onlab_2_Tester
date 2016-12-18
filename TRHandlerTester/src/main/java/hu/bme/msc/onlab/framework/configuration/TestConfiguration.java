package hu.bme.msc.onlab.framework.configuration;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestConfiguration extends Properties {
	protected static final Logger LOGGER = LoggerFactory.getLogger(TestConfiguration.class);

	private static final long serialVersionUID = 8643649087294809034L;

	// Initialization-on-demand holder idiom - thread safe
	private static class TestConfigurationHolder {
		static final TestConfiguration INSTANCE = new TestConfiguration();
		
		private TestConfigurationHolder() {
			// Hiding the constructor
		}
	}

	private TestConfiguration() {
	}
	
	public static TestConfiguration getInstance() {
		return TestConfigurationHolder.INSTANCE;
	}

	public Integer getIntegerProperty(String key) {
		final String numberString = getProperty(key);
		Integer number = null;

		if (!StringUtils.isEmpty(numberString)) {
			try {
				number = Integer.parseInt(numberString);
			} catch (NumberFormatException e) {
				LOGGER.warn("Could not parse string (" + numberString + ") to integer!", e);
			}
		}

		return number;
	}
}
