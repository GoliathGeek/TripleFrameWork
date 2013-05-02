package org.triple.test.rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.triple.common.extension.SPIExtension;
import org.triple.rpc.Protocol;

public class MutiDemoServer {

	public static void main(String[] args) {
		String protocolName = "triple";
		int defaultPort = SPIExtension.getExtensionLoader(Protocol.class).getExtension(protocolName).getDefaultPort();
		int serverNum = 4;
		MutiProvider<DemoService> mudiProvider = new MutiProvider<DemoService>(serverNum);
		mudiProvider.runMutiServer(protocolName, defaultPort, DemoService.class);
		System.out.println("input 'stopServer' to stop provider service");
		BufferedReader control = new BufferedReader(new InputStreamReader(System.in));
		String command = null;
		try {
			while ((command = control.readLine()) != null) {
				if (command.equals("stopServer")) {
					mudiProvider.ShutDown();
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
