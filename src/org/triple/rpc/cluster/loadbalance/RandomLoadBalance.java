package org.triple.rpc.cluster.loadbalance;

import java.util.List;
import java.util.Random;

import org.triple.common.TpURL;
import org.triple.rpc.Invocation;
import org.triple.rpc.Invoker;

public class RandomLoadBalance extends AbstractLoadBalance {
	private final Random random = new Random();

	@Override
	protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, TpURL tpURL, Invocation invocation) {

		int weight = 0;
		for (Invoker<T> invoker : invokers) {
			weight += this.getWeight(invoker, invocation);
		}

		int factor = random.nextInt(weight);
		for (Invoker<T> invoker : invokers) {
			weight = weight - this.getWeight(invoker, invocation);
			if (weight < factor) {
				return invoker;
			}
		}
		return invokers.get(random.nextInt(invokers.size()));
	}
}
