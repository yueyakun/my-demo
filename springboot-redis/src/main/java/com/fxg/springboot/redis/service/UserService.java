package com.fxg.springboot.redis.service;

import com.fxg.springboot.redis.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
//@CacheConfig(cacheManager = "redisCacheManager")
public class UserService {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Cacheable(value = "userCache",key = "#id")
	public User getUserById(Integer id) {
		logger.info("执行查询接口，id：{}",id);
		User user = new User();
		user.setId(id);
		user.setName("小灰灰");
		return user;
	}

	/**
	 * 更新/保存
	 * @param user
	 */
	@CachePut(value = "userCache", key = "#user.id")
	public User saveUser(User user){
		//更新数据库
		logger.info("执行更新接口，user:{}",user.toString());
		return user;
	}

	/**
	 * 删除
	 * @param id
	 */
	@CacheEvict(value = "userCache",key = "#id")
	public void deleteUser(Integer id){
		//删除数据库中数据
		logger.info("执行删除接口，id:{}",id);
	}
}
