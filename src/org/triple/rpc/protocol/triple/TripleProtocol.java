package org.triple.rpc.protocol.triple;

import java.io.IOException;
import java.net.ServerSocket;

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
	public static final int DEFAULT_TRIPLE_PORT = 20890;
	private boolean serverStarted;
	private ServerSocket serverSocket;
	@Override
	public int getDefaultPort() {
		return DEFAULT_TRIPLE_PORT;
	}

	protected <T> Runnable doExport(T proxy, Class<T> serviceClass, TpURL tpURL) throws RpcException {
		try {
			final TripleServer tripleServer = new TripleServer();
			tripleServer.start();
			return new Runnable() {
				@Override
				public void run() {
					tripleServer.stopServer();
					System.out.println("服务关闭了");
				}
			};
		} catch (IOException e) {
			throw new RpcException(e);
		}
	}

	protected <T> T doRefer(Class<T> type, TpURL tpURL) throws RpcException {
		// 联机模拟
		// 生成动态代理类(包含发送请求 ，接收返回的功能)

		// 单机模拟
		try {
			return type.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
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
		
		if(!serverStarted){
			try {
				openServer();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Exporter tripleExporter = new TripleExporter(invoker);
		exporterMap.putIfAbsent(serviceKey, tripleExporter);
		return tripleExporter;
	}

	private void openServer() throws IOException {
		TripleServer tripleServer = new TripleServer();
		tripleServer.start();
	}

	@Override
	public <T> Invoker<T> refer(Class<T> type, TpURL tpURL) throws RpcException {
		return  new TripleInvoker(type, tpURL);
	}

}
