/**
 * 
 */
package org.triple.test.rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * TODO
 * @author Cxl
 * @createTime 2013-4-24 
 */
public class BasicDemoServer {

	public static void main(String[] args) {
		//String protocolName = "rmi";
		String protocolName = "triple";
		BasicProvider<DemoService> basicProvider = new BasicProvider<DemoService>(protocolName);
		basicProvider.exportService(DemoService.class);

		System.out.println("input 'stopServer' to stop provider service");
		BufferedReader control = new BufferedReader(new InputStreamReader(System.in));
		String command = null;
		try {
			while ((command = control.readLine()) != null) {
				if (command.equals("stopServer")) {
					basicProvider.unExportService();
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
