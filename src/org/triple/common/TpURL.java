package org.triple.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class TpURL implements Serializable {
	private String path;
	private String portocol;
	private String host;
	private String port;
	private Map<String, String> params;

	public TpURL() {
		params = new HashMap<String, String>();
	}

	public String toString() {
		return path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPortocol() {
		return portocol;
	}

	public void setPortocol(String portocol) {
		this.portocol = portocol;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public String readParam(String key) {
		return params == null ? null : params.get(key);
	}
}
