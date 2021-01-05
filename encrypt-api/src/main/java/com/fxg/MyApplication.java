package com.fxg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@SpringBootApplication
@EnableAspectJAutoProxy
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
			String userDir = System.getProperty("user.home");//系统用户目录
			//			String userDir = System.getProperty("user.dir");//项目所在目录
			String devPath = userDir + "\\my-localhost-config-center\\encrypt-api\\application.yml";
			devPath = devPath.replaceAll("\\\\", "/");
			configFilesPath = String.join(",", configFilesPath, devPath);
		} else {
			// 生产环境配置文件位置，优先级比类路径下的高
			String prodPath = "file:/web-service/encrypt-api/application.yml";//此demo项目没必要配
			configFilesPath = String.join(",", configFilesPath, prodPath);
		}
		System.setProperty(CONFIG_FILES_PATH, configFilesPath);
		SpringApplication.run(MyApplication.class, "--spring.config.location=" + configFilesPath);

	}

}
