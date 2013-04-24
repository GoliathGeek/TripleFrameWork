/**
 * 
 */
package org.triple.test.rpc;

import org.triple.rpc.protocol.triple.TripleProtocol;

/**
 * TODO
 * @author Cxl
 * @createTime 2013-4-24 
 */
public class DemoServer {

	public static void main(String[] args) {
		new BasicProvider<DemoService>(TripleProtocol.PROTOCOL_NAME).exportService(DemoService.class);
	}

}
