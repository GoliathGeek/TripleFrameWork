package org.triple.rpc.cluster;

import java.util.List;

import org.triple.common.TpURL;
import org.triple.rpc.Invocation;
import org.triple.rpc.Invoker;
import org.triple.rpc.exception.RpcException;

/**
 * TODO
 * @author Goliath
 * @createTime 2013-5-5
 */
public interface Router extends Comparable<Router> {

    /**
     * TODO
     * @return
     * @author Goliath
     * @createTime 2013-5-5 
     */
    TpURL getUrl();

	/**
	 * TODO
	 * @param invokers
	 * @param url
	 * @param invocation
	 * @return
	 * @throws RpcException
	 * @author Goliath
	 * @createTime 2013-5-5 
	 */
	<T> List<Invoker<T>> route(List<Invoker<T>> invokers, TpURL url, Invocation invocation) throws RpcException;

}