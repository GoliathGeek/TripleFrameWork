package org.triple.common.extension.adaptive;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.triple.common.extension.SPIExtension;

@SuppressWarnings("unchecked")
public class GglibAdaptiveManager<T> extends AbstractAdaptiveManager<T> {

	public GglibAdaptiveManager() {
		super.loadAnalysts();
	}

	@Override
	public T createAdaptiveExtensionProxy(final Class<T> iFaceType) {
		checkAnnotation(iFaceType);
		Enhancer en = new Enhancer();
		en.setSuperclass(iFaceType);
		en.setCallback(new MethodInterceptor() {

			@Override
			public Object intercept(Object arg0, Method method, Object[] params, MethodProxy arg3) throws Throwable {
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
				return method.invoke(extension, params);
			}
		});
		return (T) en.create();
	}

}
