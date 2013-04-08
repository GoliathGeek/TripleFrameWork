package org.triple.rpc;

import org.triple.common.TpURL;
import org.triple.common.extension.SPI;
import org.triple.rpc.exception.RpcException;

/**
 * 代理工厂
 * @author Cxl
 * @createTime 2013-4-2 
 */
@SPI("javassist")
public interface ProxyFactory {

	/**
	 * 获取invoker的代理实现
	 * @param invoker
	 * @return Proxy
	 * @throws RpcException
	 * @author Cxl
	 * @createTime 2013-4-2
	 */
	<T> T getProxy(Invoker<T> invoker) throws RpcException;

	/**
	 * create invoker 把一个instance 变身成为代理执行的Invoker
	 * @param instance
	 * @param type
	 * @param url
	 * @return ProxyInvoker
	 * @throws RpcException
	 * @author Cxl
	 * @createTime 2013-4-2
	 */
	<T> Invoker<T> createProxyInvoker(T instance, Class<T> type, TpURL tpURL) throws RpcException;

}