package org.triple.rpc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 * @author Cxl
 * @createTime 2013-4-3 
 */
@SuppressWarnings("serial")
public class RpcResult implements Result, Serializable {

	private Object result;

	private Throwable exception;

	private Map<String, String> attachments = new HashMap<String, String>();

	public RpcResult() {
	}

	public RpcResult(Object result) {
		this.result = result;
	}

	public RpcResult(Throwable exception) {
		this.exception = exception;
	}

	public Object recreate() throws Throwable {
		if (exception != null) {
			throw exception;
		}
		return result;
	}

	/**
	 * @deprecated Replace to getValue()
	 * @see com.alibaba.dubbo.rpc.RpcResult#getValue()
	 */
	@Deprecated
	public Object getResult() {
		return getValue();
	}

	/**
	 * @deprecated Replace to setValue()
	 * @see com.alibaba.dubbo.rpc.RpcResult#setValue()
	 */
	@Deprecated
	public void setResult(Object result) {
		setValue(result);
	}

	public Object getValue() {
		return result;
	}

	public void setValue(Object value) {
		this.result = value;
	}

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable e) {
		this.exception = e;
	}

	public boolean hasException() {
		return exception != null;
	}

	public Map<String, String> getAttachments() {
		return attachments;
	}

	public String getAttachment(String key) {
		return attachments.get(key);
	}

	public String getAttachment(String key, String defaultValue) {
		String result = attachments.get(key);
		if (result == null || result.length() == 0) {
			result = defaultValue;
		}
		return result;
	}

	public void setAttachments(Map<String, String> map) {
		if (map != null && map.size() > 0) {
			attachments.putAll(map);
		}
	}

	public void setAttachment(String key, String value) {
		attachments.put(key, value);
	}

	@Override
	public String toString() {
		return "RpcResult [result=" + result + ", exception=" + exception + "]";
	}
}