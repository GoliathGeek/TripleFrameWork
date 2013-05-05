package org.triple.common;

import java.util.regex.Pattern;

public class Constants {

	public static final String DEFAULT_ENCODING = "utf-8";

	public static final String TRIPLE_DIRECTORY = "META-INF/triple/";

	public static final Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");

	public static final String TPURL_IFACE = "iface";

	public static final String TPURL_PROTOCAL = "protocol";

	public static final String TPURL_PARAMTYPE = "paramtype";

	public static final String UNION_CHAR = "_";

	public static final String LOCALHOST = "127.0.0.1";

	public static final int DEFAULT_WEIGHT = 100;

	public static String LOADBALANCE = "loadbalance";

	public static String RETRY_TIME = "retrytime";
}
