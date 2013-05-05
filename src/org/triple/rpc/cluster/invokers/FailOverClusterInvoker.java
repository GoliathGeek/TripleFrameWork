package org.triple.rpc.cluster.invokers;

import java.util.List;

import org.triple.common.Constants;
import org.triple.rpc.Invocation;
import org.triple.rpc.Invoker;
import org.triple.rpc.Result;
import org.triple.rpc.cluster.Directory;
import org.triple.rpc.cluster.LoadBalance;
import org.triple.rpc.exception.RpcException;

public class FailOverClusterInvoker<T> extends AbstractClusterInvoker<T> {

	public FailOverClusterInvoker(Directory<T> directory) {
		super(directory);
	}

	@Override
	protected Result doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadbalance) {
		if (invokers == null || invokers.size() == 0) {
			throw new RpcException("Failed to invoke the method " + invocation.getMethodName() + " in the service "
					+ getInterface().getName() + ". ");
		}
		int retryTime = Integer.parseInt(this.directory.getTpURL().readParam(Constants.RETRY_TIME));
		if (retryTime <= 0) {
			retryTime = 1;
		}
		RpcException lastRpcException = null;
		for (int i = 0; i < retryTime; i++) {
			Invoker<T> invoker = loadbalance.select(invokers, this.directory.getTpURL(), invocation);
			try {
				Result result = invoker.invoke(invocation);
				return result;
			} catch (RpcException e) {
				lastRpcException = e;
			} catch (Throwable e) {
				lastRpcException = new RpcException(e.getMessage(), e);
			}
		}
		throw lastRpcException;
	}
}
