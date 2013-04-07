package org.triple.rpc.protocol.triple;

public class TripleServer extends Thread {

	private volatile boolean runflag = true;

	@Override
	public void run() {
		System.out.println("服务启动了");
		while (runflag) {
			try {
				Thread.sleep(100);
				System.out.println("服务监听中");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void stopServer() {
		this.runflag = false;
	}
}
