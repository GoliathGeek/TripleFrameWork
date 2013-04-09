package org.triple.rpc.protocol.triple;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.triple.common.TpURL;
import org.triple.rpc.Invocation;
import org.triple.rpc.Result;
import org.triple.rpc.RpcResult;
import org.triple.rpc.protocol.AbstractInvoker;

public class TripleInvoker<T> extends AbstractInvoker<T> {

	public TripleInvoker(Class<T> type, TpURL tpURL) {
		super(type, tpURL);
	}

	@Override
	protected Result doInvoke(Invocation invocation) throws Throwable {
		Socket socket = new Socket(tpURL.getHost(), tpURL.getPort());
		Result result = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(invocation);
			result = (Result) ois.readObject();
		} catch (IOException e) {
			result = operateException(e);
		} catch (ClassNotFoundException e) {
			result = operateException(e);
		} finally {
			try {
				if (socket != null)
					socket.close();
			} catch (IOException e) {
				result = operateException(e);
			}
		}
		return result;
	}

	private Result operateException(Throwable e) {
		Result result = new RpcResult(e);
		return result;
	}
}
