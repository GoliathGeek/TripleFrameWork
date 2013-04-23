package org.triple.test.rpc.triple;

import org.triple.rpc.protocol.triple.TripleProtocol;
import org.triple.test.rpc.DemoProvider;
import org.triple.test.rpc.DemoService;

public class TripleProvider<T> extends DemoProvider<T> {

	public TripleProvider() {
		super(TripleProtocol.PROTOCOL_NAME, TripleProtocol.DEFAULT_TRIPLE_PORT);
	}

	public static void main(String[] args) {
		new TripleProvider<DemoService>().start(DemoService.class);
	}
}
