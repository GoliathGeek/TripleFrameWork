/**
 * 
 */
package org.triple.test.rpc;

import org.triple.common.Constants;
import org.triple.rpc.protocol.triple.TripleProtocol;

/**
 * TODO
 * @author Cxl
 * @createTime 2013-4-24 
 */
public class DemoClient {

	public static void main(String[] args) {

		DemoService remoteService = new BasicConsumer<DemoService>(TripleProtocol.PROTOCOL_NAME, Constants.LOCALHOST)
				.getService(DemoService.class);

		String[] keyWords = { "found", "catch", "catched", "know", "teach", "readyEat", "taste" };
		for (String key : keyWords) {
			System.out.println(remoteService.getWords(key));
		}
	}
}
