package org.triple.common;

public interface Node {

	public TpURL getTpURL();

	public boolean isAvailable();

	public void destroy();

}