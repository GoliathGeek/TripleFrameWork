package org.triple.rpc.proxy.javassist;

import java.lang.reflect.Method;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

import org.triple.common.TpURL;
import org.triple.rpc.Invoker;
import org.triple.rpc.RpcInvocation;
import org.triple.rpc.exception.RpcException;
import org.triple.rpc.proxy.AbstractProxyFactory;
import org.triple.rpc.proxy.AbstractProxyInvoker;

/**
 * jvavassist的代理实现
 * @author Cxl
 * @createTime 2013-4-8 
 */
@SuppressWarnings("unchecked")
public class JavassistProxyFactory extends AbstractProxyFactory {

	private ProxyFactory factory = new ProxyFactory();

	/* (non-Javadoc)
	 * @see org.triple.rpc.proxy.AbstractProxyFactory#getProxy(org.triple.rpc.Invoker, java.lang.Class)
	 */
	@Override
	public <T> T getProxy(final Invoker<T> invoker, Class<?> service) {
		factory.setSuperclass(service);
		Class<?> proxyClass = factory.createClass();

		MethodHandler mi = new MethodHandler() {
			public Object invoke(Object self, Method m, Method proceed, Object[] params) throws Throwable {
				String methodName = m.getName();
				if (checkMethodProxy(methodName, params)) {
					return m.invoke(self, params);
				}
				// 封装 method params 为 RpcInvocation
				// 把返回的Result 进行recreate处理
				return invoker.invoke(new RpcInvocation(m, params)).recreate();
			}
		};

		T proxyInstance = null;
		try {
			proxyInstance = (T) proxyClass.newInstance();
			((Proxy) proxyInstance).setHandler(mi);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return proxyInstance;
	}

	/* (non-Javadoc)
	 * @see org.triple.rpc.ProxyFactory#createProxyInvoker(java.lang.Object, java.lang.Class, org.triple.common.TpURL)
	 */
	@Override
	public <T> Invoker<T> createProxyInvoker(T proxy, Class<T> iface, TpURL tpURL) throws RpcException {

		return new AbstractProxyInvoker<T>(proxy, iface, tpURL) {

			/* (non-Javadoc)
			 * @see org.triple.rpc.proxy.AbstractProxyInvoker#doInvoke(java.lang.Object, java.lang.String, java.lang.Class<?>[], java.lang.Object[])
			 */
			@Override
			protected Object doInvoke(T proxy, String methodName, Class<?>[] parameterTypes, Object[] arguments)
					throws Exception {
				return proxy.getClass().getMethod(methodName, parameterTypes).invoke(proxy, arguments);
			}
		};
	}

}
