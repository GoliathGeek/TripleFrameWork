package org.triple.rpc.proxy;

import java.lang.reflect.InvocationTargetException;

import org.triple.common.TpURL;
import org.triple.rpc.Invocation;
import org.triple.rpc.Invoker;
import org.triple.rpc.Result;
import org.triple.rpc.RpcResult;
import org.triple.rpc.exception.RpcException;

public abstract class AbstractProxyInvoker<T> implements Invoker<T> {

	private T proxy;

	private Class<T> iface;

	private TpURL tpURL;

	protected AbstractProxyInvoker(T proxy, Class<T> iface, TpURL tpURL) {
		this.iface = iface;
		this.proxy = proxy;
		this.tpURL = tpURL;
	}

	public Class<T> getInterface() {
		return iface;
	}

	public Result invoke(Invocation invocation) throws RpcException {
		try {
			return new RpcResult(doInvoke(proxy, invocation.getMethodName(), invocation.getParameterTypes(),
					invocation.getArguments()));
		} catch (InvocationTargetException e) {
			return new RpcResult(e.getTargetException());
		} catch (Throwable e) {
			throw new RpcException("Failed to invoke remote proxy method " + invocation.getMethodName() + " to "
					+ getTpURL() + ", cause: " + e.getMessage(), e);
		}
	}

	public TpURL getTpURL() {
		return tpURL;
	}

	public boolean isAvailable() {
		return true;
	}

	public void destroy() {
	}

	protected abstract Object doInvoke(T proxy, String methodName, Class<?>[] parameterTypes, Object[] arguments)
			throws Exception;

}
