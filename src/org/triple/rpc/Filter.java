package org.triple.rpc;

import org.triple.rpc.exception.RpcException;

/**
 * Invoker执行的Filter ,比如 通过Filter 来验证权限，检查运行时间等
 * @author Cxl
 * @createTime 2013-4-2 
 */
public interface Filter {

	/**
	 * 依赖于 invoker.invoke(invocation),Filter动作的实现
	 * @param invoker
	 * @param invocation
	 * @return
	 * @throws RpcException
	 * @author Cxl
	 * @createTime 2013-4-2
	 */
	Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException;

}