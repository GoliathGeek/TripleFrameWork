package org.triple.test.rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.triple.common.extension.SPIExtension;
import org.triple.rpc.Protocol;

public class MutiDemoServer {

	public static void main(String[] args) {
		// String protocolName = "rmi";
		String protocolName = "triple";
		int defaultPort = SPIExtension.getExtensionLoader(Protocol.class).getExtension(protocolName).getDefaultPort();
		int serverNum = 4;
		MutiProvider<DemoService> mutiProvider = new MutiProvider<DemoService>(serverNum);
		mutiProvider.runMutiServer(protocolName, defaultPort, DemoService.class);
		System.out.println("input 'stopServer' to stop provider service");
		BufferedReader control = new BufferedReader(new InputStreamReader(System.in));
		String command = null;
		try {
			while ((command = control.readLine()) != null) {
				if (command.equals("stopServer")) {
					mutiProvider.ShutDown();
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
