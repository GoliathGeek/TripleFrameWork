package org.triple.rpc;

import org.triple.common.Node;
import org.triple.rpc.exception.RpcException;

public interface Invoker<T> extends Node {

	/**
	 * get Service Interface class
	 * @return
	 * @author Cxl
	 * @createTime 2013-4-1
	 */
	public Class<T> getInterface();

	/**
	 * invoke invocation 
	 * @param invocation
	 * @return result
	 * @throws RpcException
	 */
	public Result invoke(Invocation invocation) throws RpcException;

}