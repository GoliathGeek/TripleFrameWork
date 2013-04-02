package org.triple.common.util;

/**
 * @File   		: StringUtils.java
 * @ClassName   : StringUtils 
 * @Author 		: 陈曦
 * @Date   		: 2012-2-6 上午10:37:37
 * @Version		: v2.0
 * @Description : String工具类
 */
public class StringUtils {

	public static boolean isBlank(CharSequence cs) {
		if (null == cs)
			return true;
		int length = cs.length();
		for (int i = 0; i < length; i++) {
			if (!(Character.isWhitespace(cs.charAt(i))))
				return false;
		}
		return true;
	}

	public static boolean isNotBlank(CharSequence s) {
		return !isBlank(s);
	}

}
