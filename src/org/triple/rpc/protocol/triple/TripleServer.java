package org.triple.rpc.protocol.triple;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.triple.rpc.Invocation;
import org.triple.rpc.Invoker;
import org.triple.rpc.Result;

public class TripleServer extends Thread {
	private ServerSocket serverSocket;
	private volatile boolean runflag = true;
	private TripleProtocol tripleProtocol;

	public TripleServer(TripleProtocol tripleProtocol) {
		this.tripleProtocol = tripleProtocol;
	}

	public void run() {
		try {
			serverSocket = new ServerSocket(tripleProtocol.getDefaultPort());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("服务启动了");
		while (runflag) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				Invocation invocation = (Invocation) ois.readObject();
				if (invocation != null) {
					oos.writeObject(doInvoke(invocation));
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					if (socket != null)
						socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("服务关闭了");
	}

	private Result doInvoke(Invocation invocation) {
		Class<?> type = invocation.getType();
		/*Object[] params = invocation.getArguments();
		Class<?>[] paramTypeArr = new Class<?>[params.length];
		for (int i = 0; i < params.length; i++) {
			paramTypeArr[i] = params[i].getClass();
		}*/
		Invoker<?> invoker = this.tripleProtocol.getExporter(TripleProtocol.PROTOCOL_NAME, type).getInvoker();
		return invoker.invoke(invocation);
	}

	public void stopServer() {
		System.out.println("服务即将关闭");
		this.runflag = false;
	}
}
