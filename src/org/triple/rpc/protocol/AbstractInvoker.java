package org.triple.rpc.protocol;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.triple.common.TpURL;
import org.triple.rpc.Invocation;
import org.triple.rpc.Invoker;
import org.triple.rpc.Result;
import org.triple.rpc.RpcInvocation;
import org.triple.rpc.RpcResult;
import org.triple.rpc.exception.RpcException;

public abstract class AbstractInvoker<T> implements Invoker<T> {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private final Class<T> type;

	private final TpURL tpURL;

	private final Map<String, String> attachment;

	private volatile boolean available = true;

	private volatile boolean destroyed = false;

	public AbstractInvoker(Class<T> type, TpURL url) {
		this(type, url, (Map<String, String>) null);
	}

	public AbstractInvoker(Class<T> type, TpURL url, String[] keys) {
		this(type, url, convertAttachment(url, keys));
	}

	public AbstractInvoker(Class<T> type, TpURL url, Map<String, String> attachment) {
		if (type == null)
			throw new IllegalArgumentException("service type == null");
		if (url == null)
			throw new IllegalArgumentException("service url == null");
		this.type = type;
		this.tpURL = url;
		this.attachment = attachment == null ? null : Collections.unmodifiableMap(attachment);
	}

	public TpURL getTpURL() {
		return this.tpURL;
	}

	private static Map<String, String> convertAttachment(TpURL tpUrl, String[] keys) {
		if (keys == null || keys.length == 0) {
			return null;
		}
		Map<String, String> attachment = new HashMap<String, String>();
		for (String key : keys) {
			String value = tpUrl.readParam(key);
			if (value != null && value.length() > 0) {
				attachment.put(key, value);
			}
		}
		return attachment;
	}

	public Class<T> getInterface() {
		return type;
	}

	public TpURL getUrl() {
		return tpURL;
	}

	public boolean isAvailable() {
		return available;
	}

	protected void setAvailable(boolean available) {
		this.available = available;
	}

	public void destroy() {
		if (isDestroyed()) {
			return;
		}
		destroyed = true;
		setAvailable(false);
	}

	public boolean isDestroyed() {
		return destroyed;
	}

	public String toString() {
		return getInterface() + " -> " + (getUrl() == null ? "" : getUrl().toString());
	}

	public Result invoke(Invocation inv) throws RpcException {
		if (destroyed) {
			throw new RpcException("Rpc invoker for service " + this + " is DESTROYED, can not be invoked any more!");
		}
		RpcInvocation invocation = (RpcInvocation) inv;
		invocation.setInvoker(this);
		if (attachment != null && attachment.size() > 0) {
			invocation.addAttachmentsIfAbsent(attachment);
		}
		/*
		Map<String, String> context = RpcContext.getContext().getAttachments();
		if (context != null) {
			invocation.addAttachmentsIfAbsent(context);
		}
		if (getUrl().getMethodParameter(invocation.getMethodName(), Constants.ASYNC_KEY, false)) {
			invocation.setAttachment(Constants.ASYNC_KEY, Boolean.TRUE.toString());
		}
		RpcUtils.attachInvocationIdIfAsync(getUrl(), invocation);
		*/

		// 实际的执行，封装异常
		try {
			return doInvoke(invocation);
		} catch (InvocationTargetException e) { // biz exception
			Throwable te = e.getTargetException();
			if (te == null) {
				return new RpcResult(e);
			} else {
				if (te instanceof RpcException) {
					((RpcException) te).setCode(RpcException.BIZ_EXCEPTION);
				}
				return new RpcResult(te);
			}
		} catch (RpcException e) {
			if (e.isBiz()) {
				return new RpcResult(e);
			} else {
				throw e;
			}
		} catch (Throwable e) {
			return new RpcResult(e);
		}
	}

	/**
	 * 这个方法实现具体的调用动作
	 * @param invocation
	 * @return
	 * @throws Throwable
	 * @author Cxl
	 * @createTime 2013-4-7
	 */
	protected abstract Result doInvoke(Invocation invocation) throws Throwable;

}