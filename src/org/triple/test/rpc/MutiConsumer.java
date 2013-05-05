package org.triple.test.rpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.triple.common.TpURL;
import org.triple.common.extension.SPIExtension;
import org.triple.rpc.Invoker;
import org.triple.rpc.Protocol;
import org.triple.rpc.ProxyFactory;
import org.triple.rpc.cluster.Directory;

public class MutiConsumer<T> {
	private String[] paths;
	private ProxyFactory proxyFactory = SPIExtension.getExtensionLoader(ProxyFactory.class).getDefaultExtension();

	public MutiConsumer(String[] paths) {
		this.paths = paths;
	}

	public T getService(Class<T> serviceClass) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("iface", serviceClass.getName());
		List<Invoker<T>> invokerList = new ArrayList<Invoker<T>>();
		for (String path : paths) {
			TpURL tpURL = new TpURL(path);
			tpURL.setParams(params);
			Protocol protocol = SPIExtension.getExtensionLoader(Protocol.class).getExtension(tpURL.getProtocol());
			// 生成一个具有请求tpURL功能的Invoker
			invokerList.add(protocol.refer(serviceClass, tpURL));
		}

		Directory<T> directory;

		Invoker<T> clusterInvoker = null;

		// 把这个Invoker通过代理工厂转化为服务的代理实现
		return proxyFactory.getProxy(clusterInvoker);
	}

}
