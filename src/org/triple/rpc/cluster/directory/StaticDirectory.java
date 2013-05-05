package org.triple.rpc.cluster.directory;

import java.util.List;

import org.triple.common.TpURL;
import org.triple.rpc.Invocation;
import org.triple.rpc.Invoker;
import org.triple.rpc.cluster.Router;

public class StaticDirectory<T> extends AbstractDirectory<T> {

	private List<Invoker<T>> invokerList;

	public StaticDirectory(List<Invoker<T>> invokerList, Class<T> serviceClass) {
		this(invokerList, serviceClass, null, null);
	}

	public StaticDirectory(List<Invoker<T>> invokerList, Class<T> serviceClass, TpURL tpURL) {
		this(invokerList, serviceClass, tpURL, null);
	}

	public StaticDirectory(List<Invoker<T>> invokerList, Class<T> serviceClass, TpURL tpURL, List<Router> routers) {
		super(serviceClass, tpURL == null ? invokerList.get(0).getTpURL() : tpURL, routers);
		this.invokerList = invokerList;
	}

	@Override
	public boolean isAvailable() {
		for (Invoker<T> invoker : invokerList) {
			if (invoker.isAvailable()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void destroy() {
		super.setDestroyed(true);
	}

	@Override
	public List<Invoker<T>> doList(Invocation invocation) {
		return invokerList;
	}

}
