package org.triple.rpc.cluster;

import java.util.List;

import org.triple.common.Node;
import org.triple.rpc.Invocation;
import org.triple.rpc.Invoker;
import org.triple.rpc.exception.RpcException;

/**
 * 这是一个虚拟的Node，主要功能为list,通过Invocation的属性获取到一组可以用来执行此Invocation的Invoker
 * 参考doc/按权重进行随机访问负载均衡例子.txt
 * @author Cxl
 * @createTime 2013-4-2 
 */
public interface Directory<T> extends Node {
    
    /**
     * get service Iface.
     * @return
     * @author Cxl
     * @createTime 2013-4-2
     */
    Class<T> getInterface();

    /**
     *  list invokers
     * @param invocation
     * @return
     * @throws RpcException
     * @author Cxl
     * @createTime 2013-4-2
     */
    List<Invoker<T>> list(Invocation invocation) throws RpcException;
    
}