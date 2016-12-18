package hu.bme.msc.onlab.trhandlertool.driver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//At driver level using ResponseDto is not sufficient
public class PingDriver {
	private static final int DEFAULT_TIMEOUT = 3;

	private static final Logger LOGGER = LoggerFactory.getLogger(PingDriver.class);

	private InetAddress host;

	private int port;

	public PingDriver(InetAddress host, int port) {
		this.host = host;
		this.port = port;
	}

	public boolean ping() {
		return ping(Duration.ofSeconds(DEFAULT_TIMEOUT));
	}

	public boolean ping(Duration timeout) {
		LOGGER.info("Ping host " + host.getHostAddress() + ":" + port + " with timeout: " + timeout.toMillis());
		try (Socket socket = new Socket()) {
			socket.connect(new InetSocketAddress(host, port), (int) timeout.toMillis());
			return true;
		} catch (IOException e) {
			LOGGER.error("Cold not ping target!", e);
			return false; // Either timeout or unreachable or failed DNS lookup.
		}
	}
}
