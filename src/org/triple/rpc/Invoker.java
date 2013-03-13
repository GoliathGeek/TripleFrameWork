package org.triple.rpc;

import org.triple.common.Node;
import org.triple.rpc.exception.RpcException;

public interface Invoker<T> extends Node {

	/**
	 * get service interface.
	 * 
	 * @return service interface.
	 */
	Class<T> getInterface();

	/**
	 * invoke.
	 * 
	 * @param invocation
	 * @return result
	 * @throws RpcException
	 */
	Result invoke(Invocation invocation) throws RpcException;

}