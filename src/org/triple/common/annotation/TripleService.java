package org.triple.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务的注解，基于这个注解的服务讲会由Triple控制
 * @author Goliath
 * @createTime 2013-4-2
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface TripleService {
	/**
	 * 服务名
	 * @return
	 * @author Goliath
	 * @createTime 2013-4-2 
	 */
	String serviceName() default "";

	/**
	 * 超时时间  小于等于0为不超时
	 * @return
	 * @author Goliath
	 * @createTime 2013-4-2 
	 */
	int timeOut() default 0;

	/**
	 * 权重
	 * @return
	 * @author Goliath
	 * @createTime 2013-4-2 
	 */
	int weight() default 100;
}
