package org.triple.rpc.cluster;

import org.triple.common.TpURL;
import org.triple.rpc.Invocation;
import org.triple.rpc.Invoker;
import org.triple.rpc.Result;
import org.triple.rpc.exception.RpcException;

/**
 * TODO
 * @author Cxl
 * @createTime 2013-4-28 
 */
public class AbstractClusterInvoker<T> implements Invoker<T> {

	@Override
	public TpURL getTpURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAvailable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Class<T> getInterface() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result invoke(Invocation invocation) throws RpcException {
		// TODO Auto-generated method stub
		return null;
	}

}
