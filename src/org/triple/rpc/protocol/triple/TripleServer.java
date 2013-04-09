package org.triple.rpc.protocol.triple;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.triple.rpc.Invocation;
import org.triple.rpc.Result;

public class TripleServer extends Thread{
	private ServerSocket serverSocket;
	private volatile boolean runflag = true;

	public TripleServer() throws IOException {
		serverSocket = new ServerSocket(TripleProtocol.DEFAULT_TRIPLE_PORT);

	}

	public void run() {
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
		Class<?> type = invocation.getClass();
		Object[] params = invocation.getArguments();
		return null;
	}

	public void stopServer() {
		this.runflag = false;
	}
}
