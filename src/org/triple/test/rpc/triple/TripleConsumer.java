package org.triple.test.rpc.triple;

import org.triple.rpc.protocol.triple.TripleProtocol;
import org.triple.test.rpc.DemoConsumer;
import org.triple.test.rpc.DemoService;

public class TripleConsumer<T> extends DemoConsumer<T> {
	public TripleConsumer() {
		super(TripleProtocol.PROTOCOL_NAME, TripleProtocol.DEFAULT_TRIPLE_PORT);
	}

	public static void main(String[] args) {

		// 把这个Invoker通过代理工厂转化为服务的代理实现
		DemoService rpcTestServiceProxy = new TripleConsumer<DemoService>().getService(DemoService.class);

		// 代理调用
		String[] keyWords = { "found", "catch", "catched", "know", "teach", "readyEat", "taste" };
		for (String key : keyWords) {
			System.out.println(rpcTestServiceProxy.getWords(key));
		}
	}
}
