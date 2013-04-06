package org.triple.test.rpc;

import org.triple.common.Constants;
import org.triple.common.TpURL;
import org.triple.common.extension.SPIExtension;
import org.triple.rpc.Protocol;
import org.triple.rpc.ProxyFactory;
import org.triple.rpc.protocol.triple.TripleProtocol;

public class RpcClient {
	public static void main(String[] args) {
		String path = "triple://" + Constants.LOCALHOST + ":" + TripleProtocol.DEFAULT_TRIPLE_PORT;

		// triple
		Protocol protocol = SPIExtension.getExtensionLoader(Protocol.class).getDefaultExtension();
		// javassist
		ProxyFactory proxyFactory = SPIExtension.getExtensionLoader(ProxyFactory.class).getDefaultExtension();
		
		RpcTestService rpcTestService = proxyFactory.getProxy(protocol.refer(RpcTestService.class, new TpURL()));
		String[] keyWords = { "found", "catch", "catched", "know", "teach", "readyEat", "taste" };
		for (String key : keyWords) {
			System.out.println(rpcTestService.getWords(key));
		}
	}
}
