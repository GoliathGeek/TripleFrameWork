package org.triple.test.extension;

import org.triple.common.extension.Adaptive;
import org.triple.common.extension.SPI;

@Adaptive
@SPI(value = "ifacewrapdefault")
public interface IfaceWarp {
	public String sayHello();

	public String testAdaptive(Param param);
}
