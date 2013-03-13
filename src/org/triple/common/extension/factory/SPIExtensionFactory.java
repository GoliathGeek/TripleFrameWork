package org.triple.common.extension.factory;

import org.triple.common.extension.ExtensionFactory;
import org.triple.common.extension.SPIExtension;

public class SPIExtensionFactory implements ExtensionFactory {

	@Override
	public <T> T getExtension(Class<T> ifaceType, String name) {
		SPIExtension<T> oSPIExtension = SPIExtension.getExtensionLoader(ifaceType);
		if (oSPIExtension.hasExtension(name)) {
			return oSPIExtension.getExtension(name);
		}
		return null;
	}

	@Override
	public <T> T getDefaultExtension(Class<T> ifaceType) {
		SPIExtension<T> oSPIExtension = SPIExtension.getExtensionLoader(ifaceType);
		return oSPIExtension.getDefaultExtension();
	}

	@Override
	public <T> T getAdaptiveExtension(Class<T> ifaceType) {
		SPIExtension<T> oSPIExtension = SPIExtension.getExtensionLoader(ifaceType);
		return oSPIExtension.getAdaptiveExtension();
	}

}
