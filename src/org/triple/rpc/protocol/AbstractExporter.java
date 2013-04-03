package org.triple.rpc.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.triple.rpc.Exporter;
import org.triple.rpc.Invoker;

public abstract class AbstractExporter<T> implements Exporter<T> {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private final Invoker<T> invoker;

	private volatile boolean unexported = false;

	public AbstractExporter(Invoker<T> invoker) {
		if (invoker == null)
			throw new IllegalStateException("service invoker == null");
		if (invoker.getInterface() == null)
			throw new IllegalStateException("service type == null");
		if (invoker.getTpURL() == null)
			throw new IllegalStateException("service url == null");
		this.invoker = invoker;
	}

	public Invoker<T> getInvoker() {
		return invoker;
	}

	public void unexport() {
		if (unexported) {
			return;
		}
		unexported = true;
		getInvoker().destroy();
	}

	public String toString() {
		return getInvoker().toString();
	}

}