package org.triple.rpc.cluster;

import org.triple.common.extension.Adaptive;
import org.triple.rpc.Invoker;
import org.triple.rpc.exception.RpcException;

/**
 * 将一个 Invoker List 伪装成一个 Invoker，隐藏了loadbalance
 * 参考doc/按权重进行随机访问负载均衡例子.txt
 * @author Cxl
 * @createTime 2013-4-2 
 */
public interface Cluster {

	/**
	 * TODO
	 * @param directory
	 * @return
	 * @throws RpcException
	 * @author Cxl
	 * @createTime 2013-4-2
	 */
	@Adaptive
	<T> Invoker<T> join(Directory<T> directory) throws RpcException;

}