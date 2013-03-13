/**
 * 
 */
package org.triple.test.extension;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.triple.common.extension.AdaptiveManager;
import org.triple.common.extension.SPIExtension;

/**
 * @description TODO
 * @author Cxl
 * @createTime 2013-3-8
 */
public class SPIExtensionTest {

	private static SPIExtension<IfaceWarp> oSPIExtension = SPIExtension.getExtensionLoader(IfaceWarp.class);

	/**
	 * @description TODO
	 * @throws java.lang.Exception
	 * @author Cxl
	 * @createTime 2013-3-8
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @description TODO
	 * @throws java.lang.Exception
	 * @author Cxl
	 * @createTime 2013-3-8
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.triple.common.extension.SPIExtension#getExtension(java.lang.String)}.
	 */
	@Test
	public void testGetExtension() {
		IfaceWarp ifaceA = oSPIExtension.getExtension("ifacewrapa");
		System.out.println(ifaceA.sayHello());
		printSplit();
	}

	/**
	 * Test method for {@link org.triple.common.extension.SPIExtension#getDefaultExtension()}.
	 */
	@Test
	public void testGetDefaultExtension() {
		IfaceWarp ifaceDefault = oSPIExtension.getDefaultExtension();
		System.out.println(ifaceDefault.sayHello());
		printSplit();
	}

	/**
	 * Test method for {@link org.triple.common.extension.SPIExtension#getAdaptiveExtension()}.
	 */
	@Test
	public void testGetAdaptiveExtension() {
		SPIExtension.getExtensionLoader(AdaptiveManager.class).setDefault("cglib");
		IfaceWarp ifaceAdaptive = oSPIExtension.getAdaptiveExtension();
		Param param = new Param();
		param.setStrInfo("a");
		// param.setStrInfo("b");
		// param.setStrInfo("c");
		System.out.println(ifaceAdaptive.testAdaptive(param));
		System.out.println(ifaceAdaptive.sayHello());

		System.out.println("================================");

		AdaptiveManager<?> adaptiveManager = SPIExtension.getExtensionLoader(AdaptiveManager.class)
				.getDefaultExtension();
		adaptiveManager.registerAnalyst(Param.class, new ParamAnalystOverWrite());
		param.setStrInfo("a");
		System.out.println(ifaceAdaptive.testAdaptive(param));
		printSplit();
	}

	private void printSplit() {
		System.out.println("-----------------------------------------------------");
	}
}
