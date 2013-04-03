package org.triple.rpc;

/**
 * 通过protocal生成一个Exporter ，这个Exporter含有可以实际执行的Invoker
 * @author Cxl
 * @createTime 2013-4-2 
 */
public interface Exporter<T> {

	/**
	 * get invoker.
	 * 
	 * @return invoker
	 */
	Invoker<T> getInvoker();

	/**
	 * <code>
	 *     getInvoker().destroy();
	 * </code>
	 */
	void unexport();

}