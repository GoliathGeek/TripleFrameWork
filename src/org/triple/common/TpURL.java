package org.triple.common;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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

	public static TpURL createTpURL(String urlStr) {
		if (urlStr == null || (urlStr = urlStr.trim()).length() == 0) {
			throw new IllegalArgumentException("url == null");
		}
		String protocol = null;
		String username = null;
		String password = null;
		String host = null;
		int port = 0;
		String path = null;
		Map<String, String> parameters = null;
		int i = urlStr.indexOf("?"); // seperator between body and parameters
		if (i >= 0) {
			String[] parts = urlStr.substring(i + 1).split("\\&");
			parameters = new HashMap<String, String>();
			for (String part : parts) {
				part = part.trim();
				if (part.length() > 0) {
					int j = part.indexOf('=');
					if (j >= 0) {
						parameters.put(part.substring(0, j), part.substring(j + 1));
					} else {
						parameters.put(part, part);
					}
				}
			}
			urlStr = urlStr.substring(0, i);
		}
		i = urlStr.indexOf("://");
		if (i >= 0) {
			if (i == 0)
				throw new IllegalStateException("url missing protocol: \"" + urlStr + "\"");
			protocol = urlStr.substring(0, i);
			urlStr = urlStr.substring(i + 3);
		} else {
			// case: file:/path/to/file.txt
			i = urlStr.indexOf(":/");
			if (i >= 0) {
				if (i == 0)
					throw new IllegalStateException("url missing protocol: \"" + urlStr + "\"");
				protocol = urlStr.substring(0, i);
				urlStr = urlStr.substring(i + 1);
			}
		}

		i = urlStr.indexOf("/");
		if (i >= 0) {
			path = urlStr.substring(i + 1);
			urlStr = urlStr.substring(0, i);
		}
		i = urlStr.indexOf("@");
		if (i >= 0) {
			username = urlStr.substring(0, i);
			int j = username.indexOf(":");
			if (j >= 0) {
				password = username.substring(j + 1);
				username = username.substring(0, j);
			}
			urlStr = urlStr.substring(i + 1);
		}
		i = urlStr.indexOf(":");
		if (i >= 0 && i < urlStr.length() - 1) {
			port = Integer.parseInt(urlStr.substring(i + 1));
			urlStr = urlStr.substring(0, i);
		}
		if (urlStr.length() > 0)
			host = urlStr;
		return new TpURL(protocol, username, password, host, port, path, parameters);
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
		this.params = params;
	}

	public String getPath() {
		if (this.path == null) {
			path = this.buildPath();
		}
		return path;
	}

	private String buildPath() {
		StringBuffer builder = new StringBuffer();
		if (this.protocol != null) {
			builder.append(this.protocol);
		}
		if (this.host != null) {
			builder.append("://");
			builder.append(this.host);
		}
		if (this.port > 0) {
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
		if (this.params == null) {
			this.params = params;
		} else {
			for(Entry<String,String> entry : params.entrySet()){
				addParam(entry.getKey(),entry.getValue());
			}
		}
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
		StringBuffer builder = new StringBuffer();
		if (!StringUtils.isBlank(this.protocol)) {
			builder.append(protocol);
			builder.append("://");
		}
		if (!StringUtils.isBlank(this.host)) {
			builder.append(this.host);
			if (this.port > 0) {
				builder.append(":");
				builder.append(this.port);
			}
		}
		if (params.size() > 0) {
			builder.append("?");
			for (Entry<String, String> entry : params.entrySet()) {
				builder.append(entry.getKey());
				builder.append("=");
				builder.append(entry.getValue());
				builder.append("&");
			}
			builder.deleteCharAt(builder.length() - 1);
		}
		return builder.toString();
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
		if (params.get("iface") != null) {
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
