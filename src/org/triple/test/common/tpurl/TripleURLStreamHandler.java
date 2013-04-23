package org.triple.test.common.tpurl;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class TripleURLStreamHandler extends URLStreamHandler {

	@Override
	protected URLConnection openConnection(URL u) throws IOException {
		return null;
	}

}
