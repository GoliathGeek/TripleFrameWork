package org.triple.common.extension.adaptive;

import java.lang.reflect.Method;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

import org.triple.common.extension.SPIExtension;

@SuppressWarnings("unchecked")
public class JavassistAdaptiveManager<T> extends AbstractAdaptiveManager<T> {

	public JavassistAdaptiveManager() {
		super.loadAnalysts();
	}

	@Override
	public T createAdaptiveExtensionProxy(final Class<T> iFaceType) {
		checkAnnotation(iFaceType);
		ProxyFactory factory = new ProxyFactory();
		factory.setInterfaces(new Class[] { iFaceType });
		Class<?> proxyClass = factory.createClass();
		MethodHandler mi = new MethodHandler() {
			public Object invoke(Object self, Method m, Method proceed, Object[] params) throws Throwable {
				Object extension = null;
				for (Object param : params) {
					Class<?> paramClass = param.getClass();
					if (ADAPTIVE_ANALYST.containsKey(paramClass)) {
						String extensionKey = ADAPTIVE_ANALYST.get(paramClass).getExtensionKey(param);
						extension = SPIExtension.getExtensionLoader(iFaceType).getExtension(extensionKey);
						break;
					}
				}
				if (extension == null) {
					extension = SPIExtension.getExtensionLoader(iFaceType).getDefaultExtension();
				}
				return m.invoke(extension, params);
			}
		};

		T proxyInstance = null;
		try {
			proxyInstance = (T) proxyClass.newInstance();
			((Proxy) proxyInstance).setHandler(mi);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return proxyInstance;
	}

}
