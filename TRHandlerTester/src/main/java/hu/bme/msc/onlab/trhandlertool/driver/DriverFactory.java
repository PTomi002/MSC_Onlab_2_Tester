package hu.bme.msc.onlab.trhandlertool.driver;

import java.net.InetAddress;

import hu.bme.msc.onlab.framework.entity.SUT;

public final class DriverFactory {
	private DriverFactory () {
	}
	
	public static PingDriver getPingDriver(SUT sut){
		return getPingDriver(sut.getHost(), sut.getPort());
	}
	
	public static PingDriver getPingDriver(InetAddress host, int port){
		return new PingDriver(host, port);
	}
}
