package org.triple.test.rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.triple.common.Constants;
import org.triple.common.TpURL;
import org.triple.common.extension.SPIExtension;
import org.triple.rpc.Exporter;
import org.triple.rpc.Invoker;
import org.triple.rpc.Protocol;
import org.triple.rpc.ProxyFactory;
import org.triple.rpc.exception.RpcException;

/**
 * 抽象DemoProvider
 * @author Cxl
 * @createTime 2013-4-23 
 */
public class DemoProvider<T> {
	protected Protocol protocol;
	protected ProxyFactory proxyFactory = SPIExtension.getExtensionLoader(ProxyFactory.class).getDefaultExtension();
	protected TpURL tpURL;
	Exporter<DemoService> exporter;

	public DemoProvider(String prototolName, int port) {
		TpURL tpURL = new TpURL();
		tpURL.setProtocol(prototolName);
		tpURL.setHost(Constants.LOCALHOST);
		tpURL.setPort(port);
		this.tpURL = tpURL;
		protocol = SPIExtension.getExtensionLoader(Protocol.class).getExtension(prototolName);

	}

	public void start(Class<T> serviceClass) {
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("iface", serviceClass.getName());
		tpURL.setParams(params);
		
		// 对一个实际的执行对象进行包装，变身Invoker
		Invoker<T> invoker = null;
		try {
			invoker = proxyFactory.createProxyInvoker(serviceClass.newInstance(), serviceClass, tpURL);
		} catch (RpcException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		// 把这个Invoker 发布出去 得到一个Exporter
		Exporter<T> exporter = protocol.export(invoker);
		System.out.println("input 'stopServer' to stop provider service");
		BufferedReader control = new BufferedReader(new InputStreamReader(System.in));
		String command = null;
		try {
			while ((command = control.readLine()) != null) {
				if (command.equals("stopServer")) {
					exporter.unexport();
					protocol.destroy();
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("ProviderDemo 将在triple最近一次请求完成后停止");
	}

}
