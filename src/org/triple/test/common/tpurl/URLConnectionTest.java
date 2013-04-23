package org.triple.test.common.tpurl;
import java.io.IOException;
import java.net.URL;

 
public class URLConnectionTest {
	

	public static void main(String[] args) throws IOException {
		URL url = new URL("triple://127.0.0.1:20890?iface=A");
		 url.openConnection();
	}
}
