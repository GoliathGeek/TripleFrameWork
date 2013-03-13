package org.triple.common.extension;

@SPI(value = "spi")
public interface ExtensionFactory {

	<T> T getExtension(Class<T> ifaceType, String name);

	<T> T getDefaultExtension(Class<T> ifaceType);

	<T> T getAdaptiveExtension(Class<T> ifaceType);
}
