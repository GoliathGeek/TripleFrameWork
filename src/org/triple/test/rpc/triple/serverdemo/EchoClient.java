package org.triple.test.rpc.triple.serverdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class EchoClient {
	private String host = "127.0.0.1";
	private int port = 7890;
	private Socket socket;

	public EchoClient() throws IOException {
		socket = new Socket(host, port);
	}

	private ObjectOutputStream getWriter(Socket socket) throws IOException {
		return new ObjectOutputStream(socket.getOutputStream());
	}

	private ObjectInputStream getReader(Socket socket) throws IOException {
		return new ObjectInputStream(socket.getInputStream());
	}

	public void talk() throws IOException {
		try {
			ObjectInputStream ois = getReader(socket);
			ObjectOutputStream oos = getWriter(socket);
			BufferedReader localReader = new BufferedReader(new InputStreamReader(System.in));
			String msg = null;
			while ((msg = localReader.readLine()) != null) {
				oos.writeObject(msg);
				System.out.println(ois.readObject());
				if (msg.equals("bye"))
					break;
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

	public static void main(String[] args) throws IOException {
		new EchoClient().talk();
	}
}
