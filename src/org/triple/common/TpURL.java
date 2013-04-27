package org.triple.common;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.triple.common.util.StringUtils;

@SuppressWarnings("serial")
public class TpURL implements Serializable {

	private String protocol;
	private String username;
	private String password;
	private String host;
	private int port;
	private String path;

	private Map<String, String> params = new HashMap<String, String>();

	private volatile transient String identity;
	private volatile transient String string;

	public TpURL() {
	}

	public TpURL(String path) {
	}

	public TpURL(String protocol, String username, String password, String host, int port, String path,
			Map<String, String> params) {
		if ((username == null || username.length() == 0) && password != null && password.length() > 0) {
			throw new IllegalArgumentException("Invalid url, password without username!");
		}
		this.setProtocol(protocol);
		this.username = username;
		this.password = password;
		this.host = host;
		this.port = port < 0 ? 0 : port;
		this.path = path;
		// trim the beginning "/"
		while (path != null && path.startsWith("/")) {
			path = path.substring(1);
		}
		if (params == null) {
			params = new HashMap<String, String>();
		} else {
			params = new HashMap<String, String>(params);
		}
		this.params = Collections.unmodifiableMap(params);
	}

	public String getPath() {
		if(this.path==null){
			path = this.buildPath();
		}
		return path;
	}

	private String buildPath() {
		StringBuffer builder = new StringBuffer();
		if(this.protocol!=null){
			builder.append(this.protocol);
		}
		if(this.host!=null){
			builder.append("://");
			builder.append(this.host);
		}
		if(this.port>0){
			builder.append(":");
			builder.append(this.port);
		}
		return builder.toString();
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public void addParam(String key, String value) {
		this.params.put(key, value);
	}

	public String readParam(String key) {
		return params == null ? null : params.get(key);
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String buildIdentityString() {
		if (this.identity != null) {
			return this.identity;
		}
		return buildIdentity();
	}

	public String toString() {
		if (this.string != null) {
			return this.string;
		}
		return buildString();
	}

	private String buildString() {
		return null;
	}

	private String buildIdentity() {
		StringBuffer builder = new StringBuffer();
		if (!StringUtils.isBlank(this.protocol)) {
			builder.append(protocol);
			builder.append("://");
		}
		if (!StringUtils.isBlank(this.username)) {
			builder.append(this.username);
			if (!StringUtils.isBlank(this.password)) {
				builder.append(":");
				builder.append(this.password);
			}
			builder.append("@");
		}
		if (!StringUtils.isBlank(this.host)) {
			builder.append(this.host);
			if (this.port > 0) {
				builder.append(":");
				builder.append(this.port);
			}
		}
		if(params.get("iface")!=null){
			builder.append("/");
			builder.append(params.get("iface"));
		}
		return builder.toString();
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

}
