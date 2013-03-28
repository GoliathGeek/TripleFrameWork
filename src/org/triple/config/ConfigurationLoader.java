package org.triple.config;

/**
 * @File   		: ConfigurationLoader.java
 * @ClassName   : ConfigurationLoader 
 * @Author 		: TheoneFx
 * @Date   		: 2013-3-28 上午11:13:50
 * @Version		: v1.0
 * @Description : 定义配置加载的抽象行为
 */
public interface ConfigurationLoader {
	TripleConfig load(String... strings);
}
