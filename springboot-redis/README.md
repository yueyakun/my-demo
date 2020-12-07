---
title: Spring Boot 集成 Redis
date: 2020-12-04 19:30:00
categories: Java
tags:
 - Redis
---


## Redis 简介

*Redis是一个使用ANSI C编写的开源、支持网络、基于内存、可选持久性（英语：Durability_database_systems）的键值对存储数据库 —— 维基百科*

随着微服务的普及，Redis 的使用也越来越普遍，比如分布式缓存、分布式锁、Session 存储\共享等。
Spring Boot 对 Redis 提供了很好的支持，基本实现了免配置开箱即用，当然 Spring Boot 也留给用户足够的自定义空间。

## Redis 的两种 Java 客户端：jedis 和 lettus

Spring Boot 2.x 之前的版本默认客户端是 jedis，2.x 以及之后的版本默认客户端是 lettuce。

### pom 依赖

```
<!--Redis-->
<dependency>    
    <groupId>org.springframework.boot</groupId>    
    <artifactId>spring-boot-starter-data-redis</artifactId>
    <!-- 默认的redis客户端是lettuce，想切换成jedis需要在这里排除lettuce包，然后引入jedis的依赖包即可-->
    <!--<exclusions>-->
        <!--<exclusion>-->
            <!--<groupId>io.lettuce</groupId>-->  
            <!--<artifactId>lettuce-core</artifactId>-->   
        <!--</exclusion>-->  
    <!--</exclusions>-->
</dependency>

<!--jedis客户端的依赖包-->
<!--<dependency>-->   
    <!--<groupId>redis.clients</groupId>--> 
    <!--<artifactId>jedis</artifactId>-->
<!--</dependency>-->
```

### application.properties配置文件

```
################ Redis 基础配置 ############## 
# Redis数据库索引（默认为0） 
spring.redis.database=0 
# Redis服务器地址 
spring.redis.host=127.0.0.1 
# Redis服务器连接端口 
spring.redis.port=6379 
# Redis服务器连接密码（默认为空） 
spring.redis.password=zwqh 
# 链接超时时间 单位 ms（毫秒） 
spring.redis.timeout=3000 
################ Redis lettuce client线程池设置 ############## 
# 连接池最大连接数（使用负值表示没有限制） 默认 8 
spring.redis.lettuce.pool.max-active=8 
# 连接池最大阻塞等待时间（使用负值表示没有限制） 默认 -1 
spring.redis.lettuce.pool.max-wait=-1 
# 连接池中的最大空闲连接 默认 8 
spring.redis.lettuce.pool.max-idle=8 
# 连接池中的最小空闲连接 默认 0 
spring.redis.lettuce.pool.min-idle=0

################ Redis jedis client线程池设置 ############## 
spring.redis.lettuce.pool.max-active=8 
spring.redis.lettuce.pool.max-wait=-1 
spring.redis.lettuce.pool.max-idle=8 
spring.redis.lettuce.pool.min-idle=0
```

### RedisTemplate

Spring Boot 提供了 RedisTemplate 来执行 Redis 操作。这是一个泛型类，默认实现只有 RedisTemplate<String,String> 类，这个类也是我们可直接使用的，
如果需要存储其他的泛型 value，就必须自己定义一个 RedisTemplate。下面是一个通用的 RedisTemplate 配置：

```
@Bean
public RedisTemplate<String, Serializable> redisTemplate(LettuceConnectionFactory connectionFactory) {   
    RedisTemplate<String, Serializable> redisTemplate = new RedisTemplate<>();    
    redisTemplate.setKeySerializer(new StringRedisSerializer());     
    redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());    
    redisTemplate.setConnectionFactory(connectionFactory);    
    return redisTemplate;
}
```
ok，配置完这些我们就能在 Spring Boot 项目中愉快的使用 RedisTemplate 进行各种操作了。针对 Redis 的五种数据类型，
RedisTemplate 提供了以下五个 API 来获取对应的操作入口。

    操作字符串：redisTemplate.opsForValue()
    操作 Hash：redisTemplate.opsForHash()
    操作 List：redisTemplate.opsForList()
    操作 Set：redisTemplate.opsForSet()
    操作 ZSet：redisTemplate.opsForZSet()

API 都比较多，在此不一一列举。

## Redis 分布式锁

分布式锁不是简单的在 Redis 中设置一个 K、V 的事，它是一个复杂的问题，有很多方面的问题需要考虑，
[如何优雅地用Redis实现分布式锁](http://www.redis.cn/articles/20181020004.html) 这篇文章非常详细的讲解了几种分布式锁实现方案，
在这里我就不多作说明了。项目中很少会自己动手搞一个 Redis 分布式锁，使用 Redisson 开箱即用的分布式锁才是王道。
关于 Redisson 的使用，后面再另外写一篇博客介绍。

## Redis 分布式缓存

[Spring 基于注释的缓存框架](https://blog.fengxiuge.top/2020/2020-10-20-spring-cache.html)
这篇博客已经很详细的讲解了 Spring 缓存框架的一些基础知识以及整合 Caffeine Cache 的一些用法。
现在我使用 Redis 代替 Caffeine Cache 再来跑一下之前的测试方法。

### pom文件加入 Spring Cache 依赖

```
<dependency>  
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

### 配置 RedistMapCacheManager

```
@Bean
//@Qualifier("redisCacheManager")
public RedisCacheManager cacheManager(LettuceConnectionFactory connectionFactory) {    

RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(5)); // 设置缓存有效期5秒   
RedisCacheManager redisCacheManager = RedisCacheManager.builder(connectionFactory).cacheDefaults(redisCacheConfiguration).build();    

return redisCacheManager;
```

### 创建测试 Service 类

```
package com.fxg.springboot.redis.service;

import com.fxg.springboot.redis.domain.User;import org.slf4j.Logger;
import org.slf4j.LoggerFactory;i
mport org.springframework.cache.annotation.CacheConfig;
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
```

### 创建测试类

```
package com.fxg.springboot.redis.tests;

import com.fxg.springboot.redis.domain.User;
import com.fxg.springboot.redis.service.UserService;import org.junit.jupiter.api.Test;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import java.io.Serializable;import java.util.concurrent.TimeUnit;

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
        logger.info("删除缓存后再次查询结果：{}",user5);          

        logger.info(redisCacheManager.getCacheNames().toString());    
        logger.info(redisCacheManager.toString());
    }
}
```

### 测试结果

```
2020-12-04 18:12:18.170 [main] INFO  c.f.s.redis.tests.RedisCacheTest - 第一次查询
2020-12-04 18:12:21.650 [main] INFO  c.f.s.redis.service.UserService - 执行查询接口，id：99
2020-12-04 18:12:21.719 [main] INFO  c.f.s.redis.tests.RedisCacheTest - 第一次查询结果：User{id=99, name='小灰灰'}
2020-12-04 18:12:21.721 [main] INFO  c.f.s.redis.tests.RedisCacheTest - 第二次查询
2020-12-04 18:12:21.771 [main] INFO  c.f.s.redis.tests.RedisCacheTest - 第二次查询结果：User{id=99, name='小灰灰'}
2020-12-04 18:12:27.774 [main] INFO  c.f.s.redis.tests.RedisCacheTest - 缓存过期后再次查询
2020-12-04 18:12:27.812 [main] INFO  c.f.s.redis.service.UserService - 执行查询接口，id：99
2020-12-04 18:12:27.833 [main] INFO  c.f.s.redis.tests.RedisCacheTest - 缓存过期后再次查询结果：User{id=99, name='小灰灰'}
2020-12-04 18:12:27.833 [main] INFO  c.f.s.redis.service.UserService - 执行更新接口，user:User{id=99, name='红太狼'}
2020-12-04 18:12:27.880 [main] INFO  c.f.s.redis.tests.RedisCacheTest - 更新缓存后查询
2020-12-04 18:12:27.925 [main] INFO  c.f.s.redis.tests.RedisCacheTest - 更新缓存后再次查询结果：User{id=99, name='红太狼'}
2020-12-04 18:12:27.925 [main] INFO  c.f.s.redis.service.UserService - 执行删除接口，id:99
2020-12-04 18:12:27.969 [main] INFO  c.f.s.redis.tests.RedisCacheTest - 删除缓存后查询
2020-12-04 18:12:27.995 [main] INFO  c.f.s.redis.service.UserService - 执行查询接口，id：99
2020-12-04 18:12:28.024 [main] INFO  c.f.s.redis.tests.RedisCacheTest - 删除缓存后再次查询结果：User{id=99, name='小灰灰'}
2020-12-04 18:12:28.025 [main] INFO  c.f.s.redis.tests.RedisCacheTest - [userCache]
2020-12-04 18:12:28.026 [main] INFO  c.f.s.redis.tests.RedisCacheTest - org.springframework.data.redis.cache.RedisCacheManager@798deee8
```

从运行结果可以看出

    * 第一次查询走了查询接口，第二次查询时缓存生效所以没有走查询接口。

    * 执行更新接口后再进行查询，没有走查询接口，且查询到的数据是更新后的，说明更新接口的结果也被更新到缓存中了。

    * 等缓存过期后再进行查询，走了查询接口，说明 Caffeine 的定时驱逐策略生效。

    * 执行删除接口后再进行查询，走了查询接口，说明删除时也同时删除了缓存中的数据。

    * Cache Manager 已经由默认情况下的 ConcurrentMapCacheManager 变成了 RedisCacheManager。

## 总结

测试代码已上传 [GitHub](https://github.com/yueyakun2017/my-demo)。