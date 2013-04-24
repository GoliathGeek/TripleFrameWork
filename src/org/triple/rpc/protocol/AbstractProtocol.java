package org.triple.rpc.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.triple.common.Constants;
import org.triple.common.TpURL;
import org.triple.common.extension.SPIExtension;
import org.triple.common.util.ConcurrentHashSet;
import org.triple.common.util.StringUtils;
import org.triple.rpc.Exporter;
import org.triple.rpc.Invocation;
import org.triple.rpc.Invoker;
import org.triple.rpc.Protocol;
import org.triple.rpc.ProxyFactory;
import org.triple.rpc.exception.RpcException;

/**
 * 抽象 Protocol
 * @author Cxl
 * @createTime 2013-4-8 
 */
public abstract class AbstractProtocol implements Protocol {
	protected final List<Class<?>> rpcExceptions = new CopyOnWriteArrayList<Class<?>>();

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected final Set<Invoker<?>> invokers = new ConcurrentHashSet<Invoker<?>>();

	protected final ConcurrentMap<String, Exporter<?>> exporterMap = new ConcurrentHashMap<String, Exporter<?>>();
	protected ProxyFactory proxyFactory = SPIExtension.getExtensionLoader(ProxyFactory.class).getDefaultExtension();

	/* (non-Javadoc)
	 * @see org.triple.rpc.Protocol#destroy()
	 */
	public void destroy() {
		for (Invoker<?> invoker : invokers) {
			if (invoker != null) {
				invokers.remove(invoker);
				try {
					if (logger.isInfoEnabled()) {
						logger.info("Destroy reference: " + invoker.getTpURL());
					}
					invoker.destroy();
				} catch (Throwable t) {
					logger.warn(t.getMessage(), t);
				}
			}
		}
		for (String key : new ArrayList<String>(exporterMap.keySet())) {
			Exporter<?> exporter = exporterMap.remove(key);
			if (exporter != null) {
				try {
					if (logger.isInfoEnabled()) {
						logger.info("Unexport service: " + exporter.getInvoker().getTpURL());
					}
					exporter.unexport();
				} catch (Throwable t) {
					logger.warn(t.getMessage(), t);
				}
			}
		}
	}

	/**
	 * 通过tpURL 对一个服务类生成一个 servicekey 标识码
	 * @param tpURL
	 * @return
	 * @throws RuntimeException
	 * @author Cxl
	 * @createTime 2013-4-8
	 */
	protected String getServiceKey(TpURL tpURL) throws RuntimeException {
		String protocol = tpURL.getProtocol();
		Map<String, String> params = tpURL.getParams();
		String iface = params.get(Constants.TPURL_IFACE);
		if (StringUtils.isBlank(iface)) {
			throw new RuntimeException(Constants.TPURL_IFACE + " can not be null , please check tpurl : " + tpURL);
		}
		/*	String paramTypes = params.get(Constants.TPURL_PARAMTYPE);
		String[] paramTypeArr = StringUtils.isBlank(paramTypes) ? new String[] {} : paramTypes.split("\\.");*/
		return getServiceKey(protocol, iface);
	}

	public RpcException getRpcException(Class<?> type, TpURL tpURL, Invocation invocation, Throwable e) {
		RpcException re = new RpcException("Failed to invoke remote service: " + type + ", method: "
				+ invocation.getMethodName() + ", cause: " + e.getMessage(), e);
		re.setCode(getErrorCode(e));
		return re;
	}

	private String getServiceKey(String protocol, String type) {

		/*
		 String paramStr = "";if (paramTypeArr != null && paramTypeArr.length > 0) {
			for (String paramType : paramTypeArr) {
				paramStr = paramStr + Constants.UNION_CHAR + paramType;
			}
		}*/
		return protocol + Constants.UNION_CHAR + type;
		/*+ (StringUtils.isBlank(paramStr) ? "" : Constants.UNION_CHAR + paramStr);*/
	}

	protected int getErrorCode(Throwable e) {
		return RpcException.UNKNOWN_EXCEPTION;
	}

	public Exporter<?> getExporter(String protocolName, Class<?> type) {
		String typeName = type.getName();

		String serviceKey = this.getServiceKey(protocolName, typeName);
		return exporterMap.get(serviceKey);
	}
}
