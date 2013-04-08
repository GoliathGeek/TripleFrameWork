package org.triple.test.rpc;

import java.util.HashMap;
import java.util.Map;

import org.triple.common.Constants;
import org.triple.common.TpURL;
import org.triple.common.extension.SPIExtension;
import org.triple.rpc.Invoker;
import org.triple.rpc.Protocol;
import org.triple.rpc.ProxyFactory;
import org.triple.rpc.protocol.triple.TripleProtocol;

public class RpcClient {
	public static void main(String[] args) {
		// String path = "triple://" + Constants.LOCALHOST + ":" + TripleProtocol.DEFAULT_TRIPLE_PORT;
		// TpURL tpURL = new TpURL(path,RpcTestService.class);
		
		// 生成tpURL
		TpURL tpURL = new TpURL();
		tpURL.setPortocol("triple");
		tpURL.setHost(Constants.LOCALHOST);
		tpURL.setPort(TripleProtocol.DEFAULT_TRIPLE_PORT);
		Map<String, String> params = new HashMap<String, String>();
		params.put("iface", "org.triple.test.rpc.RpcTestService");
		tpURL.setParams(params);
		
		// Protocol triple
		Protocol protocol = SPIExtension.getExtensionLoader(Protocol.class).getDefaultExtension();
		// ProxyFactory javassist
		ProxyFactory proxyFactory = SPIExtension.getExtensionLoader(ProxyFactory.class).getDefaultExtension();

		// 先生成一个具有请求tpURL功能的Invoker
		Invoker<RpcTestService> invoker = protocol.refer(RpcTestService.class, tpURL);
		// 把这个Invoker通过代理工厂转化为服务的代理实现
		RpcTestService rpcTestServiceProxy = proxyFactory.getProxy(invoker);
		
		// 代理调用
		String[] keyWords = { "found", "catch", "catched", "know", "teach", "readyEat", "taste" };
		for (String key : keyWords) {
			System.out.println(rpcTestServiceProxy.getWords(key));
		}
	}
}
