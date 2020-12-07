package com.fxg.springboot.redis.tests;

import com.fxg.springboot.redis.domain.User;
import com.fxg.springboot.redis.service.UserService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedisCacheTest {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	private UserService userService;
	@Autowired
	private RedisCacheManager redisCacheManager;


	@Test
	public void test1() throws Exception {
		logger.info("第一次查询");
		User user1 = userService.getUserById(99);//第一次查询
		logger.info("第一次查询结果：{}", user1);

		logger.info("第二次查询");
		User user2 = userService.getUserById(99);//第二次查询
		logger.info("第二次查询结果：{}", user2);

		Thread.sleep(6000);//等待6秒，等缓存过期

		logger.info("缓存过期后再次查询");
		User user3 = userService.getUserById(99);//缓存过期后再次查询
		logger.info("缓存过期后再次查询结果：{}", user3);

		userService.saveUser(new User(99, "红太狼"));//更新缓存

		logger.info("更新缓存后查询");
		User user4 = userService.getUserById(99);//更新缓存后再次查询
		logger.info("更新缓存后再次查询结果：{}", user4);

		userService.deleteUser(99);//删除缓存

		logger.info("删除缓存后查询");
		User user5 = userService.getUserById(99);//删除缓存后再次查询
		logger.info("删除缓存后再次查询结果：{}", user5);

		logger.info(redisCacheManager.getCacheNames().toString());
		logger.info(redisCacheManager.toString());
	}
}
