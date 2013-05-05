package org.triple.rpc.cluster.invokers;

import java.util.List;

import org.triple.common.Constants;
import org.triple.common.TpURL;
import org.triple.common.extension.SPIExtension;
import org.triple.common.util.StringUtils;
import org.triple.rpc.Invocation;
import org.triple.rpc.Invoker;
import org.triple.rpc.Result;
import org.triple.rpc.cluster.Directory;
import org.triple.rpc.cluster.LoadBalance;
import org.triple.rpc.exception.RpcException;

/**
 * TODO
 * @author Cxl
 * @createTime 2013-4-28 
 */
public abstract class AbstractClusterInvoker<T> implements Invoker<T> {

	protected Directory<T> directory;
	private volatile boolean destroyed = false;

	public AbstractClusterInvoker(Directory<T> directory) {
		this.directory = directory;
	}

	@Override
	public TpURL getTpURL() {
		return directory.getTpURL();
	}

	@Override
	public boolean isAvailable() {
		return directory.isAvailable();
	}

	@Override
	public void destroy() {
		directory.destroy();
		destroyed = true;
	}

	@Override
	public Class<T> getInterface() {
		return directory.getInterface();
	}

	@Override
	public Result invoke(Invocation invocation) throws RpcException {
		if (destroyed) {
			throw new RpcException(" ClusterInvoker is destory : " + directory.getTpURL());
		}
		List<Invoker<T>> invokers = directory.list(invocation);
		LoadBalance loadbalance;
		TpURL tpURL = directory.getTpURL();
		String balanceType = tpURL.readParam(Constants.LOADBALANCE);
		if (StringUtils.isBlank(balanceType)) {
			loadbalance = SPIExtension.getExtensionLoader(LoadBalance.class).getDefaultExtension();
		} else {
			loadbalance = SPIExtension.getExtensionLoader(LoadBalance.class).getExtension(balanceType);
		}
		return doInvoke(invocation, invokers, loadbalance);
	}

	protected abstract Result doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadbalance)
			throws RpcException;

}
