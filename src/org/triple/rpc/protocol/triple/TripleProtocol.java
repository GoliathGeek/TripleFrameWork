package org.triple.rpc.protocol.triple;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.triple.common.TpURL;
import org.triple.rpc.Exporter;
import org.triple.rpc.Invoker;
import org.triple.rpc.exception.RpcException;
import org.triple.rpc.protocol.AbstractProtocol;

/**
 * TODO
 * @author Cxl
 * @createTime 2013-4-3
 */
@SuppressWarnings("unchecked")
public class TripleProtocol extends AbstractProtocol {
	public static String PROTOCOL_NAME = "triple";
	public static int DEFAULT_PORT = 20890;
	private boolean serverStarted;
	private Map<Integer, TripleServer> serverContainer = new ConcurrentHashMap<Integer, TripleServer>();

	/* (non-Javadoc)
	 * @see org.triple.rpc.Protocol#getProtocolName()
	 */
	@Override
	public String getProtocolName() {
		return PROTOCOL_NAME;
	}

	/* (non-Javadoc)
	 * @see org.triple.rpc.Protocol#getDefaultPort()
	 */
	@Override
	public int getDefaultPort() {
		return DEFAULT_PORT;
	}

	@Override
	public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
		// 根据tpURL获取标识码
		final String serviceKey = getServiceKey(invoker.getTpURL());
		// 通过标识码在缓存中找相应的Exporter
		Exporter<T> exporter = (Exporter<T>) exporterMap.get(serviceKey);
		if (exporter != null) {
			return exporter;
		}
		TpURL tpURL = invoker.getTpURL();
		int port = tpURL.getPort();
		if (port == 0) {
			port = DEFAULT_PORT;
		}
		if (!serverStarted) {
			if (!serverContainer.containsKey(port)) {
				serverContainer.put(port, new TripleServer(this, port));
				serverContainer.get(port).start();
			}
		}
		Exporter<T> tripleExporter = new TripleExporter<T>(invoker);
		exporterMap.putIfAbsent(serviceKey, tripleExporter);
		return tripleExporter;
	}

	@Override
	public <T> Invoker<T> refer(Class<T> type, TpURL tpURL) throws RpcException {
		return new TripleInvoker<T>(type, tpURL);
	}

	public void destroy() {
		for (TripleServer tripleServer : serverContainer.values()) {
			tripleServer.stopServer();
		}
		super.destroy();
	}

}
