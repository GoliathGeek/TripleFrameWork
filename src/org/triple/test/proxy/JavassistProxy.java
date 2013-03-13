package org.triple.test.proxy;

import java.lang.reflect.Method;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

public class JavassistProxy {

	public static void main(String[] args) {
		ProxyFactory factory = new ProxyFactory();
		factory.setInterfaces(new Class[] { TestInterface.class });

		factory.setFilter(new MethodFilter() {
			@Override
			public boolean isHandled(Method m) {
				if (m.getName().equals("doTest")) {
					return true;
				}
				return false;
			}
		});

		Class<?> proxyClass = factory.createClass();
		MethodHandler mi = new MethodHandler() {
			public Object invoke(Object self, Method m, Method proceed, Object[] args) throws Throwable {
				System.out.println("Name: " + m.getName());
				return "result from proxy object";
			}
		};
		try {
			TestInterface testIFace = (TestInterface) proxyClass.newInstance();
			((Proxy) testIFace).setHandler(mi);
			System.out.println(testIFace.doTest());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
