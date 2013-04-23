/**
 * 
 */
package org.triple.test.rpc.rmi;

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
import org.triple.rpc.protocol.rmi.RmiProtocol;
import org.triple.test.rpc.DemoService;

/**
 * TODO
 * @author Cxl
 * @createTime 2013-4-23 
 */
public class ProviderDemo {

	public static void main(String[] args) {

		// 生成tpURL
		TpURL tpURL = new TpURL();
		tpURL.setProtocol("rmi");
		tpURL.setHost(Constants.LOCALHOST);
		tpURL.setPort(RmiProtocol.DEFAULT_TRIPLE_PORT);
		Map<String, String> params = new HashMap<String, String>();
		params.put("iface", "org.triple.test.rpc.DemoService");
		tpURL.setParams(params);

		// Protocol rmi
		Protocol protocol = SPIExtension.getExtensionLoader(Protocol.class).getExtension("rmi");
		// ProxyFactory javassist
		ProxyFactory proxyFactory = SPIExtension.getExtensionLoader(ProxyFactory.class).getDefaultExtension();

		// 对一个实际的执行对象进行包装，变身Invoker
		Invoker<DemoService> invoker = proxyFactory.createProxyInvoker(new DemoService(), DemoService.class, tpURL);

		// 把这个Invoker 发布出去 得到一个Exporter
		Exporter<DemoService> exporter = protocol.export(invoker);

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
