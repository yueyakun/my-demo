package com.fxg.archetype;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableAsync
@EnableScheduling
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableTransactionManagement
@EnableConfigurationProperties
public class MyApplication {

	/**
	 * 配置文件路径, 以','分割的字符串. 配置采用覆盖式, 当有多个配置路径, 且包含相同配置属性时, 后者会覆盖前者.
	 * (windows环境下 /home/...以当前磁盘为根目录)
	 */
	public final static String CONFIG_FILES_PATH = "configFilesPath";
	private static final String WINDOWS = "windows";
	private static final String LINUX = "linux";


	public static void main(String[] args) {
		// 默认配置位置
		String configFilesPath = "classpath:application.yml";
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains(WINDOWS)) {
			// 开发环境配置文件位置，优先级比类路径下的高
			String userDir = System.getProperty("user.dir");
			String devPath = userDir + "\\config\\application.yml";
			devPath = "file:" + devPath.substring(devPath.indexOf(":") + 1).replaceAll("\\\\", "/");
			configFilesPath = String.join(",", configFilesPath, devPath);
		} else {
			// 生产环境配置文件位置，优先级比类路径下的高
			String prodPath = "file:/web-service/house-service/application.yml";
			configFilesPath = String.join(",", configFilesPath, prodPath);
		}
		System.setProperty(CONFIG_FILES_PATH, configFilesPath);
		SpringApplication.run(MyApplication.class, "--spring.config.location=" + configFilesPath);
	}

}
