package org.triple.common;

import java.net.URL;

public interface Node {

	URL getUrl();

	boolean isAvailable();

	void destroy();

}