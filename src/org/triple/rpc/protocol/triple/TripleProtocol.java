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

	@Override
	protected <T> Runnable doExport(T proxy, Class<T> serviceClass, TpURL tpURL) throws RpcException {
		try {
			ServerSocket serverSocket = new ServerSocket(DEFAULT_TRIPLE_PORT);
			while (true) {
				// 启动一个socket 服务
				// 准备监听
				Socket socket = serverSocket.accept();
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected <T> T doRefer(Class<T> type, TpURL tpURL) throws RpcException {
		// 返回type的动态代理类实例
		// 请求
		// 发送请求 将Invocation 序列化 
	
		return null;
	}

}
