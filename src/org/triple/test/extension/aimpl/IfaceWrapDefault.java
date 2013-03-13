package org.triple.test.extension.aimpl;

import org.triple.test.extension.IfaceWarp;
import org.triple.test.extension.Param;

public class IfaceWrapDefault implements IfaceWarp {

	@Override
	public String sayHello() {
		// TODO Auto-generated method stub
		return "Hello World Default";
	}

	@Override
	public String testAdaptive(Param param) {
		// TODO Auto-generated method stub
		return "adaptive Default";
	}

}
