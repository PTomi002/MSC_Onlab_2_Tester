package hu.bme.msc.onlab.framework;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemErrorPrintStream extends PrintStream{
	private static final Logger LOGGER = LoggerFactory.getLogger("SystemErr");
	
	public SystemErrorPrintStream(OutputStream out) {
		super(out);
	}

	@Override
	public void println(String message) {
		Optional.ofNullable(message).ifPresent(msg -> LOGGER.error(msg));
	}
}
