package org.triple.common.extension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.triple.common.Constants;
import org.triple.common.util.Holder;

public class SPIExtension<T> {

	// extension config dir

	private static final String TRIPLE_EXTENSION_DIRECTORY = Constants.TRIPLE_DIRECTORY + "extension/";

	// for all

	/**
	 * definition container
	 */
	private static final ConcurrentMap<Class<?>, SPIExtension<?>> EXTENSION_LOADERS = new ConcurrentHashMap<Class<?>, SPIExtension<?>>();

	/**
	 * instance container
	 */
	private static final ConcurrentMap<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<Class<?>, Object>();

	public static ExtensionFactory extensionFactory = SPIExtension.getExtensionLoader(ExtensionFactory.class)
			.getDefaultExtension();

	// for one

	/**
	 * extension interface Type with SPI annotation
	 */
	private final Class<T> iFaceType;

	private String defaultExtensionName;

	private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<Map<String, Class<?>>>();

	private final ConcurrentMap<String, Holder<T>> cachedInstances = new ConcurrentHashMap<String, Holder<T>>();

	private final ConcurrentMap<Class<?>, String> cachedNames = new ConcurrentHashMap<Class<?>, String>();

	private final Holder<Object> cachedAdaptiveInstance = new Holder<Object>();

	@SuppressWarnings("unchecked")
	public static <T> SPIExtension<T> getExtensionLoader(Class<T> iFaceType) {
		if (iFaceType == null)
			throw new IllegalArgumentException("Extension type == null");
		if (!iFaceType.isInterface()) {
			throw new IllegalArgumentException("Extension type(" + iFaceType + ") is not interface!");
		}
		if (!iFaceType.isAnnotationPresent(SPI.class)) {
			throw new IllegalArgumentException("Extension type(" + iFaceType + ") is not extension, because WITHOUT @"
					+ SPI.class.getSimpleName() + " Annotation!");
		}
		SPIExtension<T> loader = (SPIExtension<T>) EXTENSION_LOADERS.get(iFaceType);
		if (loader == null) {
			EXTENSION_LOADERS.putIfAbsent(iFaceType, new SPIExtension<T>(iFaceType));
			loader = (SPIExtension<T>) EXTENSION_LOADERS.get(iFaceType);
		}
		return loader;
	}

	private SPIExtension(Class<T> iFaceType) {
		this.iFaceType = iFaceType;
	}

	/**
	 * @description get extension instance by extension name
	 * @param name
	 * @return  Extension instance
	 * @author Cxl
	 * @createTime 2013-3-8
	 */
	public T getExtension(String name) {
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Extension name is null");
		if ("default".equals(name)) {
			return getDefaultExtension();
		}
		Holder<T> holder = cachedInstances.get(name);
		if (holder == null) {
			cachedInstances.putIfAbsent(name, new Holder<T>());
			holder = cachedInstances.get(name);
		}
		T instance = holder.get();
		if (instance == null) {
			synchronized (holder) {
				instance = holder.get();
				if (instance == null) {
					instance = createExtension(name);
					holder.set(instance);
				}
			}
		}
		return instance;
	}

	public void setDefault(String extensionName) {
		this.defaultExtensionName = extensionName;
	}

	public T getDefaultExtension() {
		getExtensionClasses();
		if (null == defaultExtensionName || defaultExtensionName.length() == 0 || "true".equals(defaultExtensionName)) {
			return null;
		}
		return getExtension(defaultExtensionName);
	}

	public boolean hasExtension(String name) {
		if (cachedNames.containsValue(name)) {
			return true;
		}
		return false;
	}

	/**
	 * @description if extension interface type has @Adaptive  , this method will return a adaptive proxy instance . 
	 * @return  proxy instace
	 * @author Cxl
	 * @createTime 2013-3-8
	 */
	@SuppressWarnings("unchecked")
	public T getAdaptiveExtension() {
		if (!hasAdaptiveAnnotation()) {
			throw new IllegalStateException("Adaptive annotation not found in class : " + iFaceType);
		}
		Object instance = cachedAdaptiveInstance.get();
		if (instance == null) {
			synchronized (cachedAdaptiveInstance) {
				instance = cachedAdaptiveInstance.get();
				if (instance == null) {
					try {
						instance = createAdaptiveExtension();
						cachedAdaptiveInstance.set(instance);
					} catch (Throwable t) {
						throw new IllegalStateException("fail to create adaptive instance: " + t.toString(), t);
					}
				}
			}
		}
		return (T) instance;
	}

	private boolean hasAdaptiveAnnotation() {
		if (iFaceType.isAnnotationPresent(Adaptive.class) || hasAnnotaionOnMethod()) {
			return true;
		}
		return false;
	}

	private boolean hasAnnotaionOnMethod() {
		for (Method m : iFaceType.getMethods()) {
			if (m.isAnnotationPresent(Adaptive.class)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private T createAdaptiveExtension() {
		try {
			AdaptiveManager<T> adaptiveProxyCreater = SPIExtension.getExtensionLoader(AdaptiveManager.class)
					.getDefaultExtension();
			T adaptiveExtensionInstance = adaptiveProxyCreater.createAdaptiveExtensionProxy(iFaceType);
			return adaptiveExtensionInstance;
		} catch (Exception e) {
			throw new IllegalStateException("Can not create adaptive extenstion " + iFaceType + ", cause: "
					+ e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private T createExtension(String name) {
		Class<?> clazz = getExtensionClasses().get(name);
		if (clazz == null) {
			throw new IllegalStateException("Error when load extension class(interface: " + iFaceType);
		}
		try {
			T instance = (T) EXTENSION_INSTANCES.get(clazz);
			if (instance == null) {
				EXTENSION_INSTANCES.putIfAbsent(clazz, (T) clazz.newInstance());
				instance = (T) EXTENSION_INSTANCES.get(clazz);
			}
			injectExtension(instance);
			/*Set<Class<?>> wrapperClasses = cachedWrapperClasses;
			if (wrapperClasses != null && wrapperClasses.size() > 0) {
				for (Class<?> wrapperClass : wrapperClasses) {
					instance = injectExtension((T) wrapperClass.getConstructor(iFaceType).newInstance(instance));
				}
			}*/
			return instance;
		} catch (Throwable t) {
			throw new IllegalStateException("Extension instance(name: " + name + ", class: " + iFaceType
					+ ")  could not be instantiated: " + t.getMessage(), t);
		}
	}

	private T injectExtension(T instance) {
		try {
			if (extensionFactory != null) {
				for (Method method : instance.getClass().getMethods()) {
					if (method.getName().startsWith("set") && method.getParameterTypes().length == 1
							&& Modifier.isPublic(method.getModifiers())) {
						Class<?> pt = method.getParameterTypes()[0];
						try {
							String property = method.getName().length() > 3 ? method.getName().substring(3, 4)
									.toLowerCase()
									+ method.getName().substring(4) : "";
							Object object = extensionFactory.getExtension(pt, property);
							if (object != null) {
								method.invoke(instance, object);
							}
						} catch (Exception e) {
							throw new IllegalStateException("fail to inject via method " + method.getName()
									+ " of interface " + iFaceType.getName() + ": " + e.getMessage(), e);
						}
					}
				}
			}
		} catch (Exception e) {
			throw new IllegalStateException();
		}
		return instance;
	}

	private Map<String, Class<?>> getExtensionClasses() {
		Map<String, Class<?>> classes = cachedClasses.get();
		if (classes == null) {
			synchronized (cachedClasses) {
				classes = cachedClasses.get();
				if (classes == null) {
					classes = loadExtensionClasses();
					cachedClasses.set(classes);
				}
			}
		}
		return classes;
	}

	// 此方法已经getExtensionClasses方法同步过。
	private Map<String, Class<?>> loadExtensionClasses() {
		final SPI defaultAnnotation = iFaceType.getAnnotation(SPI.class);
		if (defaultAnnotation != null) {
			String value = defaultAnnotation.value().trim();
			if (value != null && value.length() > 0) {
				if (defaultExtensionName == null) {
					defaultExtensionName = value;
				}
			}
		}

		Map<String, Class<?>> extensionClasses = new HashMap<String, Class<?>>();
		loadExtensionFile(extensionClasses, TRIPLE_EXTENSION_DIRECTORY);
		return extensionClasses;
	}

	private void loadExtensionFile(Map<String, Class<?>> extensionClasses, String dir) {
		String fileName = iFaceType.getName();
		ClassLoader classLoader = iFaceType.getClassLoader();
		Enumeration<URL> urls;
		try {
			urls = classLoader.getResources(dir + fileName);

			while (urls.hasMoreElements()) {

				URL url = urls.nextElement();
				BufferedReader br = null;
				try {
					int lineNum = 0;
					br = new BufferedReader(new InputStreamReader(url.openStream(), Constants.DEFAULT_ENCODING));
					String lineStr = null;
					while ((lineStr = br.readLine()) != null) {
						lineNum++;
						lineStr = lineStr.trim();
						if (lineStr.startsWith("#")) {
							continue;
						}
						String[] strArr = lineStr.split("=");
						if (strArr.length != 2) {
							throw new IllegalStateException("ExtensionFile :" + fileName + " has a bad config at line "
									+ lineNum);
						}
						String extensionName = strArr[0].trim();
						String className = strArr[1].trim();
						Class<?> extensionClass = Class.forName(className);
						if (!iFaceType.isAssignableFrom(extensionClass)) {
							throw new IllegalStateException("Error when load extension class(interface: " + iFaceType
									+ ", class line: " + extensionClass.getName() + "), class "
									+ extensionClass.getName() + "is not subtype of interface.");
						} else {
							// cachedWrapperClasses.add(extensionClass);
							cachedNames.put(extensionClass, extensionName);
							extensionClasses.put(extensionName, extensionClass);
						}
					}
				} catch (UnsupportedEncodingException e) {
					throw new IllegalStateException("Unsupported encoding Exception when load ExtensionFile :"
							+ fileName + ",  default Encodeing : " + Constants.DEFAULT_ENCODING);
				} catch (IOException e) {
					throw new IllegalStateException("IO Exception when load ExtensionFile :" + fileName + ","
							+ e.getMessage());
				} finally {
					if (br != null) {
						br.close();
					}
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			throw new IllegalStateException("IO Exception when get ExtensionFile path :" + dir + fileName + ","
					+ e.getMessage());
		}
	}
}
