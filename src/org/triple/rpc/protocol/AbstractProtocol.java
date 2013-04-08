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
import org.triple.rpc.Result;
import org.triple.rpc.exception.RpcException;

/**
 * 抽象 Protocol
 * @author Cxl
 * @createTime 2013-4-8 
 */
@SuppressWarnings("unchecked")
public abstract class AbstractProtocol implements Protocol {
	private ProxyFactory proxyFactory = SPIExtension.getExtensionLoader(ProxyFactory.class).getDefaultExtension();

	private final List<Class<?>> rpcExceptions = new CopyOnWriteArrayList<Class<?>>();

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected final Set<Invoker<?>> invokers = new ConcurrentHashSet<Invoker<?>>();

	protected final ConcurrentMap<String, Exporter<?>> exporterMap = new ConcurrentHashMap<String, Exporter<?>>();

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
	private String getServiceKey(TpURL tpURL) throws RuntimeException {
		String protocol = tpURL.getPortocol();
		Map<String, String> params = tpURL.getParams();
		String iface = params.get(Constants.TPURL_IFACE);
		if (StringUtils.isBlank(iface)) {
			throw new RuntimeException(Constants.TPURL_IFACE + " can not be null , please check tpurl : " + tpURL);
		}
		String paramType = params.get(Constants.TPURL_PARAMTYPE);
		return protocol + Constants.UNION_CHAR + iface + Constants.UNION_CHAR + (paramType == null ? "" : paramType);
	}

	/* (non-Javadoc)
	 * @see org.triple.rpc.Protocol#export(org.triple.rpc.Invoker)
	 */
	@Override
	public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
		// 根据tpURL获取标识码
		final String serviceKey = getServiceKey(invoker.getTpURL());
		// 通过标识码在缓存中找相应的Exporter
		Exporter<T> exporter = (Exporter<T>) exporterMap.get(serviceKey);
		if (exporter != null) {
			return exporter;
		}
		// 若不存在则新生成
		// 生成 invoker 对应 具有服务实际执行能力的实体类代理
		T expertProxy = proxyFactory.getProxy(invoker);
		// 发布Invoker 返回一个 取消发布的命令
		final Runnable unexportCommand = doExport(expertProxy, invoker.getInterface(), invoker.getTpURL());
		exporter = new AbstractExporter<T>(invoker) {
			public void unexport() {
				super.unexport();
				// 从已发布Exporter的缓存中去除
				exporterMap.remove(serviceKey);
				if (unexportCommand != null) {
					try {
						// 执行取消发布的命令
						unexportCommand.run();
					} catch (Throwable t) {
						logger.warn(t.getMessage(), t);
					}
				}
			}
		};
		// 放入缓存
		exporterMap.putIfAbsent(serviceKey, exporter);
		return exporter;
	}

	/* (non-Javadoc)
	 * @see org.triple.rpc.Protocol#refer(java.lang.Class, org.triple.common.TpURL)
	 */
	@Override
	public <T> Invoker<T> refer(final Class<T> type, final TpURL tpURL) throws RpcException {
		// 获得一个 服务类 对应的 remote 请求代理 
		T referProxy = doRefer(type, tpURL);
		//  将这个请求代理进行包装，生成一个Invoker 具有远程调用外部service的能力
		final Invoker<T> target = proxyFactory.createProxyInvoker(referProxy, type, tpURL);
		// 生成一个AbstractInvoker 含有封装执行异常 ， invocation 处理的功能 可以理解成一个静态代理
		// target.invoke 将会在 AbstractInvoker.invoke 方法中被执行
		Invoker<T> invoker = new AbstractInvoker<T>(type, tpURL) {
			@Override
			protected Result doInvoke(Invocation invocation) throws Throwable {
				try {
					Result result = target.invoke(invocation);
					// 处理异常 存在异常时将异常抛出，由AbstractInvoker捕获并重建 Result
					Throwable e = result.getException();
					if (e != null) {
						for (Class<?> rpcException : rpcExceptions) {
							if (rpcException.isAssignableFrom(e.getClass())) {
								throw getRpcException(type, tpURL, invocation, e);
							}
						}
					}
					return result;
				} catch (RpcException e) {
					if (e.getCode() == RpcException.UNKNOWN_EXCEPTION) {
						e.setCode(getErrorCode(e.getCause()));
					}
					throw e;
				} catch (Throwable e) {
					throw getRpcException(type, tpURL, invocation, e);
				}
			}

		};
		invokers.add(invoker);
		return invoker;
	}

	private RpcException getRpcException(Class<?> type, TpURL tpURL, Invocation invocation, Throwable e) {
		RpcException re = new RpcException("Failed to invoke remote service: " + type + ", method: "
				+ invocation.getMethodName() + ", cause: " + e.getMessage(), e);
		re.setCode(getErrorCode(e));
		return re;
	}

	private int getErrorCode(Throwable e) {
		return RpcException.UNKNOWN_EXCEPTION;
	}

	/**
	 * 实际的发布动作，返回的是取消发布命令
	 * @param proxy
	 * @param serviceClass
	 * @param tpURL
	 * @return
	 * @throws RpcException
	 * @author Cxl
	 * @createTime 2013-4-8
	 */
	protected abstract <T> Runnable doExport(T proxy, Class<T> serviceClass, TpURL tpURL) throws RpcException;

	/**
	 * 通过服务类，请求地址得到远程调用的代理实现
	 * @param type
	 * @param tpURL
	 * @return
	 * @throws RpcException
	 * @author Cxl
	 * @createTime 2013-4-8
	 */
	protected abstract <T> T doRefer(Class<T> type, TpURL tpURL) throws RpcException;
}
