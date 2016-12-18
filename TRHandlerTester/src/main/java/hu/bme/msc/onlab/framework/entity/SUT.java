package hu.bme.msc.onlab.framework.entity;

import java.net.InetAddress;
import java.util.Map;

import com.google.common.collect.Maps;

public class SUT {

	public SUT(InetAddress host) {
		this.host = host;
	}

	private InetAddress host;

	private int port;

	private String project;

	private Map<String, String> URLs = Maps.newConcurrentMap();

	public synchronized InetAddress getHost() {
		return host;
	}

	public synchronized int getPort() {
		return port;
	}

	public synchronized SUT setPort(int port) {
		this.port = port;
		return this;
	}

	public synchronized Map<String, String> getURLs() {
		return URLs;
	}

	public synchronized void addSutUrl(String key, String url) {
		URLs.put(key, url);
	}

	public synchronized String getProject() {
		return project;
	}

	public synchronized SUT setProject(String project) {
		this.project = project;
		return this;
	}

	public synchronized String getSutUrl(String key) {
		return URLs.get(key);
	}
}