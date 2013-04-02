package org.triple.rpc.proxy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.triple.common.util.StringUtils;
import org.triple.rpc.Invoker;
import org.triple.rpc.ProxyFactory;
import org.triple.rpc.exception.RpcException;

/**
 * RPC 的代理
 * @author Goliath
 * @createTime 2013-4-2
 */
public abstract class AbstractProxyFactory implements ProxyFactory {

	private static final Map<String, Object> PROXY_CONTAINER = new ConcurrentHashMap<String, Object>();

	@SuppressWarnings("unchecked")
	public <T> T getProxy(Invoker<T> invoker) throws RpcException {
		String iface = invoker.getTpURL().readParam("iface");
		if (iface != null && StringUtils.isNotBlank(iface)) {
			if (PROXY_CONTAINER.containsKey(iface)) {
				return (T) PROXY_CONTAINER.get(iface);
			} else {
				Class<?> ifaceClazz = null;
				try {
					ifaceClazz = Class.forName(iface);
					return getProxy(invoker, ifaceClazz);
				} catch (ClassNotFoundException e) {
					throw new IllegalArgumentException("ifaceClazz :" + iface + " can not be found when  create Proxy ");
				}
			}
		} else {
			throw new IllegalArgumentException("iface can not be found in tpurl's params ,please check tpurl :"
					+ invoker.getTpURL());
		}
	}

	public abstract <T> T getProxy(Invoker<T> invoker, Class<?> interfaces);
}
