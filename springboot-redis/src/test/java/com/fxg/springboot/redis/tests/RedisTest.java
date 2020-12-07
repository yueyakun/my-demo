package com.fxg.springboot.redis.tests;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedisTest {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	private RedisTemplate<String, String> stringRedisTemplate;
	@Autowired
	private RedisTemplate<String, Serializable> redisTemplate;


	@Test
	public void test1() throws Exception {
		logger.info(stringRedisTemplate.toString());
		logger.info(redisTemplate.toString());

		Boolean test1 = stringRedisTemplate.opsForValue().setIfAbsent("test", "test1", 5, TimeUnit.SECONDS);
		System.out.println(test1);

		System.out.println(stringRedisTemplate.opsForValue().get("test"));

		Boolean test2 = stringRedisTemplate.opsForValue().setIfAbsent("test", "test2", 5, TimeUnit.SECONDS);
		System.out.println(test2);

		Thread.sleep(5000);

		System.out.println(stringRedisTemplate.opsForValue().get("test"));
	}
}
