package org.triple.rpc.cluster.clusters;

import org.triple.rpc.Invoker;
import org.triple.rpc.cluster.Cluster;
import org.triple.rpc.cluster.Directory;
import org.triple.rpc.cluster.invokers.FailOverClusterInvoker;
import org.triple.rpc.exception.RpcException;

public class FailOverCluster implements Cluster {

	@Override
	public <T> Invoker<T> join(Directory<T> directory) throws RpcException {
		return new FailOverClusterInvoker<T>(directory);
	}

}
