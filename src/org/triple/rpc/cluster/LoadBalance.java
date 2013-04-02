package org.triple.rpc.cluster;

import java.util.List;

import org.triple.common.TpURL;
import org.triple.rpc.Invocation;
import org.triple.rpc.Invoker;
import org.triple.rpc.exception.RpcException;

/**
 * 负载均衡  
 * 从一组Invoker 中选取出一个Invoker的算法实现
 * 如 Random的方式，参考doc/按权重进行随机访问负载均衡例子.txt
 * @author Cxl
 * @createTime 2013-4-2 
 */
public interface LoadBalance {

	<T> Invoker<T> select(List<Invoker<T>> invokers, TpURL tpURL, Invocation invocation) throws RpcException;

}