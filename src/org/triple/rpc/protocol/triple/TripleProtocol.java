package org.triple.rpc.protocol.triple;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.triple.common.TpURL;
import org.triple.rpc.exception.RpcException;
import org.triple.rpc.protocol.AbstractProtocol;

/**
 * TODO
 * @author Cxl
 * @createTime 2013-4-3
 */
public class TripleProtocol extends AbstractProtocol {
	public static final int DEFAULT_TRIPLE_PORT = 20890;

	@Override
	public int getDefaultPort() {
		return DEFAULT_TRIPLE_PORT;
	}

	/* (non-Javadoc)
	 * @see org.triple.rpc.protocol.AbstractProtocol#doExport(java.lang.Object, java.lang.Class, org.triple.common.TpURL)
	 */
	@Override
	protected <T> Runnable doExport(T proxy, Class<T> serviceClass, TpURL tpURL) throws RpcException {
		final TripleServer serverThread = new TripleServer();
		serverThread.start();
		return new Runnable(){
			@Override
			public void run() {
				serverThread.stopServer();
				System.out.println("服务关闭了");
			}
		};
	}

	/* (non-Javadoc)
	 * @see org.triple.rpc.protocol.AbstractProtocol#doRefer(java.lang.Class, org.triple.common.TpURL)
	 */
	@Override
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

}
