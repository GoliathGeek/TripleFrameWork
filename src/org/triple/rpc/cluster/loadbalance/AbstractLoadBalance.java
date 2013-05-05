package org.triple.rpc.cluster.loadbalance;

import java.util.List;

import org.triple.common.Constants;
import org.triple.common.TpURL;
import org.triple.common.util.StringUtils;
import org.triple.rpc.Invocation;
import org.triple.rpc.Invoker;
import org.triple.rpc.cluster.LoadBalance;
import org.triple.rpc.exception.RpcException;

public abstract class AbstractLoadBalance implements LoadBalance {

	@Override
	public <T> Invoker<T> select(List<Invoker<T>> invokers, TpURL tpURL, Invocation invocation) throws RpcException {
		if (invokers == null || invokers.size() == 0) {
			return null;
		}
		if (invokers.size() == 1) {
			return invokers.get(0);
		}
		return doSelect(invokers, tpURL, invocation);
	}

	protected int getWeight(Invoker<?> invoker, Invocation invocation) {
		if (!StringUtils.isBlank(invoker.getTpURL().readParam("WEIGHT"))) {
			return Integer.parseInt(invoker.getTpURL().readParam("WEIGHT"));
		}
		return Constants.DEFAULT_WEIGHT;
	}

	protected abstract <T> Invoker<T> doSelect(List<Invoker<T>> invokers, TpURL tpURL, Invocation invocation);
}
