/**
 * 
 */
package org.triple.test.rpc;


/**
 * TODO
 * @author Cxl
 * @createTime 2013-4-24 
 */
public class DemoServer {

	public static void main(String[] args) {
		String protocolName = "rmi";
		// String protocolName = "triple";
		new BasicProvider<DemoService>(protocolName).exportService(DemoService.class);
	}

}
