package org.triple.common.extension.adaptive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.triple.common.Constants;
import org.triple.common.extension.Adaptive;
import org.triple.common.extension.AdaptiveAnalyst;
import org.triple.common.extension.AdaptiveManager;

/**
 * @description Extension adaptive analyst  
 * @author Cxl
 * @createTime 2013-3-12 
 */
public abstract class AbstractAdaptiveManager<T> implements AdaptiveManager<T> {

	private static final String DEFAULT_ANALYST_FILENAME = "analyst.default";

	private static final String TRIPLE_ANALYST_DIRECTORY = Constants.TRIPLE_DIRECTORY + "analyst/";

	/**
	 * adaptive container
	 */
	public static final ConcurrentMap<Class<?>, AdaptiveAnalyst> ADAPTIVE_ANALYST = new ConcurrentHashMap<Class<?>, AdaptiveAnalyst>();

	protected void loadAnalysts() {

		if (ADAPTIVE_ANALYST.isEmpty()) {
			loadAnalystFile();
		}
	}

	/**
	 * @description TODO
	 * @author Cxl
	 * @createTime 2013-3-12
	 */
	private void loadAnalystFile() {
		ClassLoader classLoader = this.getClass().getClassLoader();
		BufferedReader br = null;
		try {
			URL url = classLoader.getResource(TRIPLE_ANALYST_DIRECTORY + DEFAULT_ANALYST_FILENAME);
			br = new BufferedReader(new InputStreamReader(url.openStream(), Constants.DEFAULT_ENCODING));
			String lineStr = null;
			int lineNum = 0;
			while ((lineStr = br.readLine()) != null) {
				lineNum++;
				lineStr = lineStr.trim();
				if (lineStr.startsWith("#")) {
					continue;
				}
				String[] strArr = lineStr.split("=");
				if (strArr.length != 2) {
					throw new IllegalStateException("AnalystFile :" + DEFAULT_ANALYST_FILENAME
							+ " has a bad config at line " + lineNum);
				}
				String paramClassStr = strArr[0];
				String AnalystClassStr = strArr[1];
				try {
					Class<?> paramClass = Class.forName(paramClassStr);
					Class<?> analystClass = Class.forName(AnalystClassStr);
					if (!AdaptiveAnalyst.class.isAssignableFrom(analystClass)) {
						throw new IllegalStateException("Class : " + AnalystClassStr + " doesn't implements "
								+ AdaptiveAnalyst.class.getName());
					}
					AdaptiveAnalyst adaptiveAnalyst = (AdaptiveAnalyst) analystClass.newInstance();
					ADAPTIVE_ANALYST.put(paramClass, adaptiveAnalyst);
				} catch (ClassNotFoundException e) {
					throw new IllegalStateException("Class definition not found " + e);
				} catch (InstantiationException e) {
					throw new IllegalStateException("AnalystClass :" + AnalystClassStr
							+ " must have a No-args constructor " + e);
				} catch (IllegalAccessException e) {
					throw new IllegalStateException("AnalystClass :" + AnalystClassStr
							+ " must have a public constructor " + e);
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException("load default analyst file failed :" + e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void checkAnnotation(Class<?> iFaceType) {
		if (!iFaceType.isAnnotationPresent(Adaptive.class)) {
			throw new IllegalStateException("Annotation Adaptive not found in Interface : " + iFaceType.getName());
		}
	}

	@Override
	public void registerAnalyst(Class<?> paramType, AdaptiveAnalyst analyst) {
		ADAPTIVE_ANALYST.put(paramType, analyst);
	}
}
