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
	private int port;

	public TripleServer(TripleProtocol tripleProtocol, int port) {
		this.tripleProtocol = tripleProtocol;
		this.port = port;
	}

	public void run() {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("server started at port:" + port);
		while (runflag) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				Invocation invocation = (Invocation) ois.readObject();
				if (invocation != null) {
					System.out.println("port:"+this.port+" get a call" );
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
		// System.out.println(invocation.getInvoker().getInterface());
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
		System.out.println("TripleServer will close after last call");
		this.runflag = false;
	}
}
