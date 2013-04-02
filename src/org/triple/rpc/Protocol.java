package org.triple.rpc;

import java.net.URL;

import org.triple.common.extension.Adaptive;
import org.triple.rpc.exception.RpcException;

/**
 * 这是一个核心类，用于对外暴露服务，不通种的protocol应该采用不同的port
 * @author Cxl
 * @createTime 2013-4-2 
 */
public interface Protocol {

	/**
	 * 获取默认的端口
	 * @return
	 * @author Cxl
	 * @createTime 2013-4-2
	 */
	int getDefaultPort();

	/**
	 * 把一个可以运行的invoker【这个是P端的invoker】,进行暴露动作
	 * 采用注册中心时，这个方法被执行后  可以在注册中心发现对应的信息
	 * @param invoker
	 * @return
	 * @throws RpcException
	 * @author Cxl
	 * @createTime 2013-4-2
	 */
	@Adaptive
	<T> Exporter<T> export(Invoker<T> invoker) throws RpcException;

	/**
	 * 通过Url连接，获取到一个 iface 对应invoker【这个是P端的invoker】
	 * @param type
	 * @param url
	 * @return
	 * @throws RpcException
	 * @author Cxl
	 * @createTime 2013-4-2
	 */
	@Adaptive
	<T> Invoker<T> refer(Class<T> type, URL url) throws RpcException;

	/**
	 * 将一个protocol 摧毁，关闭服务，关闭端口
	 * @author Cxl
	 * @createTime 2013-4-2
	 */
	void destroy();

}