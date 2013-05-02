package org.triple.test.rpc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MutiProvider<T> {
	private int serverNum;

	public MutiProvider(int serverNum) {
		this.serverNum = serverNum;
	}

	public class ServerRunner implements Callable<BasicProvider<T>> {
		private int port;
		private String protocolName;
		private Class<T> serviceClass;

		public ServerRunner(String protocolName, int port, Class<T> serviceClass) {
			this.protocolName = protocolName;
			this.port = port;
			this.serviceClass = serviceClass;
		}

		@Override
		public BasicProvider<T> call() throws Exception {
			BasicProvider<T> basicProvider = new BasicProvider<T>(protocolName, port);
			basicProvider.exportService(serviceClass);
			return basicProvider;
		}
	}

	private ExecutorService executor;
	private List<Future<BasicProvider<T>>> futureList = new ArrayList<Future<BasicProvider<T>>>();

	public void runMutiServer(String protocolName, int startPort, Class<T> serviceClass) {
		executor = Executors.newFixedThreadPool(serverNum);
		for (int i = 0; i < serverNum; i++) {
			Future<BasicProvider<T>> future = executor
					.submit(new ServerRunner(protocolName, startPort, serviceClass));
			futureList.add(future);
			startPort++;
		}
	}

	public void ShutDown() {
		for (Future<BasicProvider<T>> future : futureList) {
			try {
				BasicProvider<T> basicProvider = future.get();
				basicProvider.unExportService();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		executor.shutdown();
	}
}
