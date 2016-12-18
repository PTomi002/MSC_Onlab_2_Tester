package hu.bme.msc.onlab.framework;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemOutputPrintStream extends PrintStream {

	private static final Logger LOGGER = LoggerFactory.getLogger("SystemOut");
	
	public SystemOutputPrintStream(OutputStream out) {
		super(out);
	}

	@Override
	public void println(String message) {
		Optional.ofNullable(message).ifPresent(msg -> LOGGER.info(msg));
	}
}
