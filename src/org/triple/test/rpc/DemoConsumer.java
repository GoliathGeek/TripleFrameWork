/**
 * 
 */
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

/**
 * 抽象DemoConsumer
 * @author Cxl
 * @createTime 2013-4-23 
 */
public abstract class DemoConsumer<T> {
	private Protocol protocol;
	private ProxyFactory proxyFactory = SPIExtension.getExtensionLoader(ProxyFactory.class).getDefaultExtension();
	protected TpURL tpURL;

	public DemoConsumer(String prototolName, int port) {
		TpURL tpURL = new TpURL();
		tpURL.setProtocol(prototolName);
		tpURL.setHost(Constants.LOCALHOST);
		tpURL.setPort(port);
		this.tpURL = tpURL;
		protocol = SPIExtension.getExtensionLoader(Protocol.class).getExtension(prototolName);
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
