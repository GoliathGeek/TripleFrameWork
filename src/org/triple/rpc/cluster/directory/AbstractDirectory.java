package org.triple.rpc.cluster.directory;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.triple.common.TpURL;
import org.triple.rpc.Invocation;
import org.triple.rpc.Invoker;
import org.triple.rpc.cluster.Directory;
import org.triple.rpc.cluster.Router;
import org.triple.rpc.exception.RpcException;


public abstract class AbstractDirectory<T> implements Directory<T> {
	  // 日志输出
    private static final Logger logger = LoggerFactory.getLogger(AbstractDirectory.class);
	private Class<T> serviceClass;
	private volatile boolean destroyed = false;
	private TpURL tpURL;
	private volatile List<Router> routers;

	public AbstractDirectory(Class<T> serviceClass, TpURL tpURL, List<Router> routers) {
		this.serviceClass = serviceClass;
		this.tpURL = tpURL;
		this.routers = routers;
	}

	@Override
	public Class<T> getInterface() {
		return serviceClass;
	}

	@Override
	public List<Invoker<T>> list(Invocation invocation) throws RpcException {
		if (isDestroyed()) {
			throw new RpcException("Directory already destroyed .url: " + tpURL);
		}
		List<Invoker<T>> invokers = doList(invocation);
        if (routers != null && routers.size() > 0) {
            for (Router router: routers){
                try {
                        invokers = router.route(invokers, tpURL, invocation);
                } catch (Throwable t) {
                    logger.error("Failed to execute router: " + tpURL + ", cause: " + t.getMessage(), t);
                }
            }
        }
        return invokers;
	}
	
	@Override
	public TpURL getTpURL() {
		return tpURL;
	}


	public abstract List<Invoker<T>> doList(Invocation invocation);

	public boolean isDestroyed() {
		return destroyed;
	}

	public void setDestroyed(boolean destroyed) {
		this.destroyed = destroyed;
	}
}
