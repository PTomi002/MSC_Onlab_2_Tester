package hu.bme.msc.onlab.framework.entity;

import java.net.InetAddress;
import java.util.Map;

import com.google.common.collect.Maps;

public class SUT {

	private final InetAddress host;

	private final int port;

	private final String project;

	private final Map<String, String> urls = Maps.newConcurrentMap();

	private SUT(SUTBuilder builder) {
		this.host = builder.host;
		this.port = builder.port;
		this.project = builder.project;
	}

	public InetAddress getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public Map<String, String> getURLs() {
		return urls;
	}

	public void addSutUrl(String key, String url) {
		urls.put(key, url);
	}

	public String getSutUrl(String key) {
		return urls.get(key);
	}

	public String getProject() {
		return project;
	}

	public static class SUTBuilder {
		private InetAddress host;
		private int port;
		private String project;

		private SUTBuilder() {
		}

		public static SUTBuilder of() {
			return new SUTBuilder();
		}

		public SUTBuilder setHost(InetAddress host) {
			this.host = host;
			return this;
		}

		public SUTBuilder setPort(int port) {
			this.port = port;
			return this;
		}

		public SUTBuilder setProject(String project) {
			this.project = project;
			return this;
		}

		public SUT build() {
			return new SUT(this);
		}
	}
}