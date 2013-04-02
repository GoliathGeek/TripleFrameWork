package org.triple.rpc;

import java.net.URL;

import org.triple.common.extension.SPI;
import org.triple.rpc.exception.RpcException;

/**
 * TODO
 * @author Cxl
 * @createTime 2013-4-2 
 */
@SPI("javassist")
public interface ProxyFactory {

	/**
	 * get proxy 获取invoker的实际执行对象
	 * @param invoker
	 * @return Proxy
	 * @throws RpcException
	 * @author Cxl
	 * @createTime 2013-4-2
	 */
	<T> T getProxy(Invoker<T> invoker) throws RpcException;

	/**
	 * create invoker 创建一个代理执行的Invoker
	 * @param proxy
	 * @param type
	 * @param url
	 * @return ProxyInvoker
	 * @throws RpcException
	 * @author Cxl
	 * @createTime 2013-4-2
	 */
	<T> Invoker<T> createProxyInvoker(T proxy, Class<T> type, URL url) throws RpcException;

}