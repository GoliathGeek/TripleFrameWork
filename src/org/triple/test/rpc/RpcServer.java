package org.triple.test.rpc;

import java.util.HashMap;
import java.util.Map;

import org.triple.common.Constants;
import org.triple.common.TpURL;
import org.triple.common.extension.SPIExtension;
import org.triple.rpc.Exporter;
import org.triple.rpc.Invoker;
import org.triple.rpc.Protocol;
import org.triple.rpc.ProxyFactory;
import org.triple.rpc.protocol.triple.TripleProtocol;

public class RpcServer {

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

		// 对一个实际的执行对象进行包装，变身Invoker
		Invoker<RpcTestService> invoker = proxyFactory.createProxyInvoker(new RpcTestService(), RpcTestService.class,
				tpURL);
		
		// 把这个Invoker 发布出去 得到一个Exporter 
		Exporter<RpcTestService> exporter = protocol.export(invoker);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Exporter 销毁
		exporter.unexport();
	}
}
