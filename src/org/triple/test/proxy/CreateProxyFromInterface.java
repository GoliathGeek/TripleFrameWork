package org.triple.test.proxy;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class CreateProxyFromInterface {

	public static void main(String[] args) {
		Enhancer en = new Enhancer();
		en.setSuperclass(TestInterface.class);
		en.setCallback(new MethodInterceptor() {

			@Override
			public Object intercept(Object arg0, Method arg1, Object[] arg2, MethodProxy arg3) throws Throwable {
				System.out.println(arg1.getName());
				return "result from proxy object";
			}
		});
		TestInterface bean = (TestInterface) en.create();
		Object result = bean.doTest();
		System.out.println(result);
	}
}
