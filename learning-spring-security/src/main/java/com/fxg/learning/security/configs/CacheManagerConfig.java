package com.fxg.learning.security.configs;

//import com.github.benmanes.caffeine.cache.Caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 配置本地缓存，使用 Caffeine Cache
 */
@Configuration
public class CacheManagerConfig {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	//	/**
	//	 * 创建普通Cache Manager
	//	 * 初始化一些key存入
	//	 *
	//	 * @return cacheManager
	//	 */
	//	@Bean
	//	@Primary
	//	public CacheManager cacheManager() {
	//		SimpleCacheManager cacheManager = new SimpleCacheManager();
	//
	//		List<CaffeineCache> caches = new ArrayList<>();
	//
	//		caches.add(new CaffeineCache("userCache", Caffeine.newBuilder()
	//				.expireAfterWrite(5, TimeUnit.SECONDS)
	//				.maximumSize(1000)
	////				.removalListener((k, v, c) -> logger.info("缓存过期-缓存名称：userCache，key:{},value:{},cause:{}", k, v, c))
	//				.build()));
	//
	//		cacheManager.setCaches(caches);
	//		return cacheManager;
	//	}


	/**
	 * 创建普通CaffeineCacheManager
	 * 初始化一些key存入
	 *
	 * @return caffeineCacheManager
	 */
	@Bean
	//	@Primary
	public CaffeineCacheManager caffeineCacheManager() {

		CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();

		caffeineCacheManager.setCacheNames(Arrays.asList("userCache"));
		caffeineCacheManager.setCaffeine(
				Caffeine.newBuilder().recordStats().expireAfterWrite(5, TimeUnit.SECONDS).maximumSize(1000));

		return caffeineCacheManager;
	}


}
