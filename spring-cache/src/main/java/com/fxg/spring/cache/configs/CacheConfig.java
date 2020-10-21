package com.fxg.spring.cache.configs;

//import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.cache.caffeine.CaffeineCache;
//import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@EnableCaching()
@Configuration
public class CacheConfig {

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


//	/**
//	 * 创建普通CaffeineCacheManager
//	 * 初始化一些key存入
//	 *
//	 * @return caffeineCacheManager
//	 */
//	@Bean
////	@Primary
//	public CaffeineCacheManager caffeineCacheManager() {
//
//		CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
//
//		caffeineCacheManager.setCacheNames(Arrays.asList("userCache"));
//		caffeineCacheManager.setCaffeine(Caffeine.newBuilder()
//				.recordStats()
//				.expireAfterWrite(5, TimeUnit.SECONDS)
//				.maximumSize(1000));
//
//		return caffeineCacheManager;
//	}


}
