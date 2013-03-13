package org.triple.common;

import java.net.URL;

public interface Node {

	/**
	 * @description TODO
	 * @return
	 * @author Cxl
	 * @createTime 2013-3-4 下午5:03:05
	 */
	URL getUrl();

	boolean isAvailable();

	void destroy();

}