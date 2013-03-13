package org.triple.common.extension;

/**
 * @description extension adaptive Manager
 * @author Cxl
 * @createTime 2013-3-12 
 */
@SPI(value = "cglib")
public interface AdaptiveManager<T> {

	/**
	 * @description get a Extension adaptive proxy , if proxy instance not exist , create instance
	 * @param iFaceType
	 * @return adaptive proxy
	 * @author Cxl
	 * @createTime 2013-3-12
	 */
	public T createAdaptiveExtensionProxy(Class<T> iFaceType);

	/**
	 * @description regist a AdaptiveAnalyst , this operate will overwrite defintion in analyst.default
	 * @param paramType
	 * @param analyst
	 * @author Cxl
	 * @createTime 2013-3-12
	 */
	public void registerAnalyst(Class<?> paramType, AdaptiveAnalyst analyst);
}
