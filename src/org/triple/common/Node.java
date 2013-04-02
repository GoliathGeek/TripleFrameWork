package org.triple.common;

public interface Node {

	TpURL getTpURL();

	boolean isAvailable();

	void destroy();

}