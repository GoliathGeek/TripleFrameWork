package org.triple.rpc.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.triple.common.TpURL;
import org.triple.common.util.ConcurrentHashSet;
import org.triple.rpc.Exporter;
import org.triple.rpc.Invocation;
import org.triple.rpc.Invoker;
import org.triple.rpc.Protocol;
import org.triple.rpc.ProxyFactory;
import org.triple.rpc.Result;
import org.triple.rpc.exception.RpcException;

@SuppressWarnings("unchecked")
public abstract class AbstractProtocol implements Protocol {
	private ProxyFactory proxyFactory;

	private final List<Class<?>> rpcExceptions = new CopyOnWriteArrayList<Class<?>>();;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected final Set<Invoker<?>> invokers = new ConcurrentHashSet<Invoker<?>>();

	protected final ConcurrentMap<String, Exporter<?>> exporterMap = new ConcurrentHashMap<String, Exporter<?>>();

	public void destroy() {
		for (Invoker<?> invoker : invokers) {
			if (invoker != null) {
				invokers.remove(invoker);
				try {
					if (logger.isInfoEnabled()) {
						logger.info("Destroy reference: " + invoker.getTpURL());
					}
					invoker.destroy();
				} catch (Throwable t) {
					logger.warn(t.getMessage(), t);
				}
			}
		}
		for (String key : new ArrayList<String>(exporterMap.keySet())) {
			Exporter<?> exporter = exporterMap.remove(key);
			if (exporter != null) {
				try {
					if (logger.isInfoEnabled()) {
						logger.info("Unexport service: " + exporter.getInvoker().getTpURL());
					}
					exporter.unexport();
				} catch (Throwable t) {
					logger.warn(t.getMessage(), t);
				}
			}
		}
	}

	private String getServiceKey(TpURL tpURL) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
		final String serviceKey = getServiceKey(invoker.getTpURL());
		Exporter<T> exporter = (Exporter<T>) exporterMap.get(serviceKey);
		if (exporter != null) {
			return exporter;
		}
		final Runnable runnable = doExport(proxyFactory.getProxy(invoker), invoker.getInterface(), invoker.getTpURL());
		exporter = new AbstractExporter<T>(invoker) {
			public void unexport() {
				super.unexport();
				exporterMap.remove(serviceKey);
				if (runnable != null) {
					try {
						runnable.run();
					} catch (Throwable t) {
						logger.warn(t.getMessage(), t);
					}
				}
			}
		};
		return (Exporter<T>) exporterMap.putIfAbsent(serviceKey, exporter);
	}

	@Override
	public <T> Invoker<T> refer(final Class<T> type, final TpURL tpURL) throws RpcException {
		final Invoker<T> target = proxyFactory.createProxyInvoker(doRefer(type, tpURL), type, tpURL);
		Invoker<T> invoker = new AbstractInvoker<T>(type, tpURL) {
			@Override
			protected Result doInvoke(Invocation invocation) throws Throwable {
				try {
					Result result = target.invoke(invocation);
					Throwable e = result.getException();
					if (e != null) {
						for (Class<?> rpcException : rpcExceptions) {
							if (rpcException.isAssignableFrom(e.getClass())) {
								throw getRpcException(type, tpURL, invocation, e);
							}
						}
					}
					return result;
				} catch (RpcException e) {
					if (e.getCode() == RpcException.UNKNOWN_EXCEPTION) {
						e.setCode(getErrorCode(e.getCause()));
					}
					throw e;
				} catch (Throwable e) {
					throw getRpcException(type, tpURL, invocation, e);
				}
			}

		};
		invokers.add(invoker);
		return invoker;
	}

	private RpcException getRpcException(Class<?> type, TpURL tpURL, Invocation invocation, Throwable e) {
		RpcException re = new RpcException("Failed to invoke remote service: " + type + ", method: "
				+ invocation.getMethodName() + ", cause: " + e.getMessage(), e);
		re.setCode(getErrorCode(e));
		return re;
	}

	private int getErrorCode(Throwable e) {
		return RpcException.UNKNOWN_EXCEPTION;
	}

	protected abstract <T> Runnable doExport(T proxy, Class<T> serviceClass, TpURL tpURL) throws RpcException;

	protected abstract <T> T doRefer(Class<T> type, TpURL tpURL) throws RpcException;
}
