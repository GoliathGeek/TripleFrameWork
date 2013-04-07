package org.triple.rpc;

import org.triple.common.TpURL;
import org.triple.common.extension.Adaptive;
import org.triple.common.extension.SPI;
import org.triple.rpc.exception.RpcException;

/**
 * 这是一个核心类，用于对外暴露服务，不通种的protocol应该采用不同的port
 * @author Cxl
 * @createTime 2013-4-2 
 */
@SPI("triple")
public interface Protocol {

	/**
	 * 获取默认的端口
	 * @return
	 * @author Cxl
	 * @createTime 2013-4-2
	 */
	int getDefaultPort();

	/**
	 * Invoker【这个是P端的invoker】,进行暴露动作，将一个包含实际执行实例(代理对象)的Invoker进行发布动作
	 * 采用注册中心时，这个方法被执行后  可以在注册中心发现对应的信息
	 * @param invoker
	 * @return
	 * @throws RpcException
	 * @author Cxl
	 * @createTime 2013-4-2
	 */
	<T> Exporter<T> export(Invoker<T> invoker) throws RpcException;

	/**
	 * Invoker【这个是C端的invoker】, 对iface 进行处理，得到一个能通过TpURL进行调用的Invoker
	 * @param iface
	 * @param tpURL
	 * @return
	 * @throws RpcException
	 * @author Cxl
	 * @createTime 2013-4-2
	 */
	<T> Invoker<T> refer(Class<T> iface, TpURL tpURL) throws RpcException;

	/**
	 * 将一个protocol 摧毁，关闭服务，关闭端口
	 * @author Cxl
	 * @createTime 2013-4-2
	 */
	void destroy();

}