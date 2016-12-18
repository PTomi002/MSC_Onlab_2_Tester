package hu.bme.msc.onlab.framework.eventhandling;

import org.testng.SkipException;

public interface EventListener {
	public void start() throws SkipException;
	
	public void stop();
}
