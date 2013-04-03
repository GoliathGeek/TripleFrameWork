package org.triple.rpc.protocol.triple;

import org.triple.common.Constants;
import org.triple.common.TpURL;
import org.triple.rpc.exception.RpcException;
import org.triple.rpc.protocol.AbstractProtocol;

/**
 * TODO
 * @author Cxl
 * @createTime 2013-4-3 
 */
public class TripleProtocol extends AbstractProtocol {

	@Override
	public int getDefaultPort() {
		return Constants.DEFAULT_TRIPLE_PORT;
	}

	@Override
	protected <T> Runnable doExport(T proxy, Class<T> serviceClass, TpURL tpURL) throws RpcException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected <T> T doRefer(Class<T> type, TpURL tpURL) throws RpcException {
		// TODO Auto-generated method stub
		return null;
	}

}
