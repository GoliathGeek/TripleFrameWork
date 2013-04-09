package org.triple.test.rpc.triple.serverdemo;

//echoServer.java

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
	private int port = 7890;
	private ServerSocket serverSocket;

	public EchoServer() throws IOException {
		serverSocket = new ServerSocket(port);
		System.out.println("服务器已启动！");
	}

	public String echo(String msg) {
		return "echo:" + msg;
	}

	private ObjectOutputStream getWriter(Socket socket) throws IOException {
		OutputStream socketOut = socket.getOutputStream();
		return new ObjectOutputStream(socketOut);
	}

	private ObjectInputStream getReader(Socket socket) throws IOException {
		InputStream socketIn = socket.getInputStream();
		return new ObjectInputStream(socketIn);
	}

	public void server() {
		while (true) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
				System.out.println("New connection accepted" + socket.getInetAddress() + ":" + socket.getPort());
				ObjectOutputStream oos = getWriter(socket);
				ObjectInputStream ois = getReader(socket);
				
				String obj = (String) ois.readObject();
				if (obj != null) {
					System.out.println(obj);
					oos.writeObject(echo(obj));
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
	}

	public static void main(String[] args) throws IOException {
		new EchoServer().server();
	}
}