package org.triple.rpc;

/**
 * 执行参数
 * @author Cxl
 * @createTime 2013-4-2 
 */
public interface Invocation {

	public Class<?> getType();
	
	/**
	 * get method name.
	 * 
	 * @serial
	 * @return method name.
	 */
	public String getMethodName();

	/**
	 * get parameter types.
	 * 
	 * @serial
	 * @return parameter types.
	 */
	public Class<?>[] getParameterTypes();

	/**
	 * get arguments.
	 * 
	 * @serial
	 * @return arguments.
	 */
	public Object[] getArguments();

	/**
	 * get attachments.
	 * 
	 * @serial
	 * @return attachments.
	 */
	// Map<String, String> getAttachments();

	/**
	 * get attachment by key.
	 * 
	 * @serial
	 * @return attachment value.
	 */
	// String getAttachment(String key);

	/**
	 * get attachment by key with default value.
	 * 
	 * @serial
	 * @return attachment value.
	 */
	// String getAttachment(String key, String defaultValue);

	/**
	 * get the invoker in current context.
	 * 
	 * @transient
	 * @return invoker.
	 */
	public Invoker<?> getInvoker();

}