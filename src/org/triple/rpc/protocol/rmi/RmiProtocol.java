/**
 * 
 */
package org.triple.rpc.protocol.rmi;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.rmi.RemoteException;

import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import org.springframework.remoting.rmi.RmiServiceExporter;
import org.triple.common.TpURL;
import org.triple.rpc.exception.RpcException;
import org.triple.rpc.protocol.AbstractProtocolProxy;

/**
 * 基于Spring 的rmi实现
 * @author Cxl
 * @createTime 2013-4-23 
 */
@SuppressWarnings("unchecked")
public class RmiProtocol extends AbstractProtocolProxy {
	public static final String PROTOCOL_NAME = "rmi";
	public static final  int DEFAULT_PORT = 1099;

	/* (non-Javadoc)
	 * @see org.triple.rpc.Protocol#getProtocolName()
	 */
	@Override
	public String getProtocolName() {
		return PROTOCOL_NAME;
	}

	/* (non-Javadoc)
	 * @see org.triple.rpc.Protocol#getDefaultPort()
	 */
	@Override
	public int getDefaultPort() {
		return DEFAULT_PORT;
	}

	/* (non-Javadoc)
	 * @see org.triple.rpc.protocol.AbstractProtocolProxy#doExport(java.lang.Object, java.lang.Class, org.triple.common.TpURL)
	 */
	@Override
	protected <T> Runnable doExport(T proxy, Class<T> serviceClass, TpURL tpURL) throws RpcException {
		final RmiServiceExporter rmiServiceExporter = new RmiServiceExporter();
		rmiServiceExporter.setRegistryPort(tpURL.getPort());
		rmiServiceExporter.setServiceName(serviceClass.getName());
		rmiServiceExporter.setServiceInterface(serviceClass.getInterfaces()[0]);
		rmiServiceExporter.setService(proxy);
		try {
			rmiServiceExporter.afterPropertiesSet();
		} catch (RemoteException e) {
			throw new RpcException(e.getMessage(), e);
		}
		return new Runnable() {
			public void run() {
				try {
					rmiServiceExporter.destroy();
				} catch (Throwable e) {
					logger.warn(e.getMessage(), e);
				}
			}
		};
	}

	/* (non-Javadoc)
	 * @see org.triple.rpc.protocol.AbstractProtocolProxy#doRefer(java.lang.Class, org.triple.common.TpURL)
	 */
	@Override
	protected <T> T doRefer(Class<T> serviceType, TpURL tpURL) throws RpcException {
		final RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
		rmiProxyFactoryBean.setServiceUrl(tpURL.buildIdentityString());
		rmiProxyFactoryBean.setServiceInterface(serviceType.getInterfaces()[0]);
		rmiProxyFactoryBean.setCacheStub(true);
		rmiProxyFactoryBean.setLookupStubOnStartup(true);
		rmiProxyFactoryBean.setRefreshStubOnConnectFailure(true);
		rmiProxyFactoryBean.afterPropertiesSet();
		return (T) rmiProxyFactoryBean.getObject();
	}

	protected int getErrorCode(Throwable e) {
		if (e instanceof RemoteAccessException) {
			e = e.getCause();
		}
		if (e != null && e.getCause() != null) {
			Class<?> cls = e.getCause().getClass();
			// 是根据测试Case发现的问题，对RpcException.setCode进行设置
			if (SocketTimeoutException.class.equals(cls)) {
				return RpcException.TIMEOUT_EXCEPTION;
			} else if (IOException.class.isAssignableFrom(cls)) {
				return RpcException.NETWORK_EXCEPTION;
			} else if (ClassNotFoundException.class.isAssignableFrom(cls)) {
				return RpcException.SERIALIZATION_EXCEPTION;
			}
		}
		return super.getErrorCode(e);
	}

}
