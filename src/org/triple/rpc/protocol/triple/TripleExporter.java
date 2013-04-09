package org.triple.rpc.protocol.triple;

import org.triple.rpc.Invoker;
import org.triple.rpc.protocol.AbstractExporter;

public class TripleExporter<T> extends AbstractExporter<T> {

	public TripleExporter(Invoker<T> invoker) {
		super(invoker);
	}

}
