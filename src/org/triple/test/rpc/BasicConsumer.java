/**
 * 
 */
package org.triple.test.rpc;

import java.util.HashMap;
import java.util.Map;

import org.triple.common.TpURL;
import org.triple.common.extension.SPIExtension;
import org.triple.rpc.Invoker;
import org.triple.rpc.Protocol;
import org.triple.rpc.ProxyFactory;

public class BasicConsumer<T> {
	
	private TpURL tpURL;
	private Protocol protocol;
	private String protocolName;
	private ProxyFactory proxyFactory = SPIExtension.getExtensionLoader(ProxyFactory.class).getDefaultExtension();

	public BasicConsumer(String protocolName, String host) {
		this.initProtocol(protocolName);
		this.initTpURL(host, this.protocol.getDefaultPort());
	}

	public BasicConsumer(String prototolName, String host, int port) {
		this.initProtocol(protocolName);
		this.initTpURL(host, port);
	}

	private void initProtocol(String protocolName) {
		this.protocolName = protocolName;
		protocol = SPIExtension.getExtensionLoader(Protocol.class).getExtension(protocolName);
	}

	private void initTpURL(String host, int port) {
		protocol = SPIExtension.getExtensionLoader(Protocol.class).getExtension(protocolName);
		TpURL tpURL = new TpURL();
		tpURL.setProtocol(protocolName);
		tpURL.setHost(host);
		tpURL.setPort(port);
		this.tpURL = tpURL;
	}

	public T getService(Class<T> serviceClass) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("iface", serviceClass.getName());
		tpURL.setParams(params);

		// 先生成一个具有请求tpURL功能的Invoker
		Invoker<T> invoker = protocol.refer(serviceClass, tpURL);
		// 把这个Invoker通过代理工厂转化为服务的代理实现
		return proxyFactory.getProxy(invoker);
	}
}
