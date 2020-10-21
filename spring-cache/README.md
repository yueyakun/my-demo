---
title: Spring 基于注释的缓存框架
date: 2020-10-20 19:30:00
categories: Cache
tags:
 - 缓存
 - Cache
 - Spring Cache
---



## 概述

Spring Cache 是 Spring 在 3.1 版本引入的基于注解的缓存框架，它本质上不是一个具体的缓存实现方案， 而是一个对缓存使用方式的抽象。它基于 AOP 技术，使用简单的几个注解就可以实现缓存数据的功能。

Spring Cache 的核心接口是 org.springframework.cache.Cache 和 org.springframework.cache.CacheManager。
Cache 是各种缓存框架接入 Spring Cache 的基础。
CacheManager 是我们管理缓存的一个入口，它里面一般维护着一个数据结构来存放不同的 Cache。

不同的缓存技术有不同的 Cache 和 CacheManager 实现，比如，ConcurrentMapCacheManager、CaffeineCacheManager。
不同的 CacheManager 一般使用的都是使用 ConcurrentHashMap 数据结构来维护缓存，比如，ConcurrentMapCacheManager 和 CaffeineCacheManager 都是使用 ConcurrentHashMap 存储缓存。

## 不同缓存技术及其对应的 CacheManager

|CacheManager|描述|
|-|-|
|SimpleCacheManager|spring.context 包提供，可以自己指定缓存实现|
|ConcurrentMapCacheManager|spring.context 包提供，缓存实现为 ConcurrentHashMap|
|NoOpCacheManager|一个空的实现，不会实际存储缓存|
|EhCacheCacheManager|以 EhCache 作为缓存技术|
|GuavaCacheManager|使用 google guava 作为缓存技术|
|CaffeineCacheManager|使用 Caffeine Cache 作为缓存技术，为替代 Guava 而生，详情请看[上一篇博客](https://blog.fengxiuge.top/2020/2020-10-14-caffeine-cache.html)|
|JCacheCacheManager|使用 JCache 标准的实现作为缓存技术|
|RedisCacheManager|使用 Redis 作为缓存技术|

## 缓存依赖

Spring Boot 默认的 CacheManager 是 ConcurrentMapCacheManager，
CacheManager 可以配置多个，但是其中一个要用 @Primary 注解指明优先级。
默认情况下用 ConcurrentMapCache 不需要引入任何其他缓存框架的依赖

```
<!--Spring Cache 基础依赖-->
<dependency>    
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>

<!--使用 Caffeine Cache-->
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
<version>2.6.2</version></dependency>

<!--使用 Caffeine Cache-->
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
<version>2.6.2</version></dependency>

<!--使用 ehcache-->
<dependency>
     <groupId>net.sf.ehcache</groupId>
     <artifactId>ehcache</artifactId>
<version>2.6.2</version></dependency>

<!--使用 redis-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
<version>2.6.2</version></dependency>
```

## application 配置

```
spring.cache.type= ＃缓存的技术类型，可选 generic,ehcache,jcache,redis,guava,simple,none,caffeine等
spring.cache.cache-names= ＃应用程序启动创建缓存的名称，必须将所有注释为@Cacheable缓存name（或value）罗列在这里或者在配置类中配置，否者会报异常。
#以下根据不同缓存技术选择配置
spring.cache.ehcache.config= ＃EHCache的配置文件位置
spring.caffeine.spec= ＃caffeine类型创建缓存的规范。查看CaffeineSpec了解更多关于规格格式的细节
```

## 相关注解

### @EnableCaching

用在在启动类或者配置类上，用来开启 Spring 缓存。

### @Cacheable

用在查询方法上，会自动将查询结果放入缓存，下次查询时先从缓存中取，取不到在执行查询方法。
下面是 @Cacheable 注解主要属性和作用的说明。

```

public @interface Cacheable {

    /**
     * 要使用的cache的名字
     */
    @AliasFor("cacheNames")
    String[] value() default {};

    /**
     * 同value()，决定要使用那个/些缓存
     */
    @AliasFor("value")
    String[] cacheNames() default {};

    /**
     * 使用SpEL表达式来设定缓存的key，如果不设置默认方法上所有参数都会作为key的一部分
     */
    String key() default "";

    /**
     * 用来生成key，与key()不可以共用
     */
    String keyGenerator() default "";

    /**
     * 设定要使用的cacheManager，必须先设置好cacheManager的bean，这是使用该bean的名字，如不指定，默认是加了@Primary的
     */
    String cacheManager() default "";

    /**
     * 使用cacheResolver来设定使用的缓存，用法同cacheManager，但是与cacheManager不可以同时使用
     */
    String cacheResolver() default "";

    /**
     * 使用SpEL表达式设定出发缓存的条件，在方法执行前生效
     */
    String condition() default "";

    /**
     * 使用SpEL设置出发缓存的条件，这里是方法执行完生效，所以条件中可以有方法执行后的value
     */
    String unless() default "";

    /**
     * 用于同步的，在缓存失效（过期不存在等各种原因）的时候，如果多个线程同时访问被标注的方法
     * 则只允许一个线程通过去执行方法
     */
    boolean sync() default false;

}
```

### @CachePut

用在修改或保存方法上，会自动将返回结果放入缓存。主要属性和作用与 @Cacheable 类似。

**注意：修改或者保存方法一定会执行。**


### @CacheEvict

用在删除方法上，自动删除缓存中对用的数据。主要属性和作用与 @Cacheable 类似。

### @CacheConfig 

配合上面三个注解使用，用在 Component 类上，在类级别设置一些缓存相关的共同配置。

## 使用

1. 创建一个简单的 Spring Boot 项目


2. 引入如下依赖，并在启动类添加 @EnableCaching 注解

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
    <exclusions>
        <exclusion>
            <groupId>org.junit.vintage</groupId>
            <artifactId>junit-vintage-engine</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<!--Spring Cache 基础依赖-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

3. 创建 Domain 类

```
package com.fxg.spring.cache.Domain;

public class User {

    private Integer id; 
    private String name; 

    public User() {       } 
    
    public User(Integer id, String name) { 
        this.id = id;
        this.name = name; 
    } 
    public Integer getId() { 
        return id;
    }
    
    public void setId(Integer id) { 
        this.id = id;
    }
    public String getName() {
        return name; 
    }
    
    public void setName(String name) { 
        this.name = name; 
    } 

    @Override
    public String toString() {
    return "User{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
```

4. 创建 Service 类和接口

```
@Service
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
        /更新数据库
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

5. 创建测试类

```
package com.fxg.spring.cache;import com.fxg.spring.cache.Domain.User;
import com.fxg.spring.cache.service.UserService;
import org.junit.jupiter.api.Test;import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

@SpringBootTest
public class UserCacheTests {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private UserService userService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    public void testUserCache() throws Exception { 

        logger.info("第一次查询");   
        User user1 = userService.getUserById(99);//第一次查询 
        logger.info("第一次查询结果：{}", user1);

        logger.info("第二次查询");
        User user2 = userService.getUserById(99);//第二次查询 
        logger.info("第二次查询结果：{}", user2); 

        logger.info("更新缓存后查询");  
        User user4 = userService.getUserById(99);//更新缓存后再次查询  
        logger.info("更新缓存后再次查询结果：{}", user4);   

        userService.deleteUser(new User(99, ""));//删除缓存   

        logger.info("删除缓存后查询");     
        User user5 = userService.getUserById(99);//删除缓存后再次查询     
        logger.info("删除缓存后再次查询结果：{}", user5);    

        logger.info(cacheManager.getCacheNames().toString()); 
        logger.info(cacheManager.toString());  
    }
}
```

6. 启动测试类，运行结果如下图

![](https://github.com/yueyakun2017/my-demo/blob/main/spring-cache/2020-10-20-spring-cache-1.png)

从运行结果可以看出

    * 第一次查询走了查询接口，第二次查询时缓存生效所以没有走查询接口。
    
    * 执行更新接口后再进行查询，没有走查询接口，且查询到的数据是更新后的，说明更新接口的结果也被更新到缓存中了。
    
    * 执行删除接口后再进行查询，走了查询接口，说明删除时也同时删除了缓存中的数据。
    
    * 如果不自己配置 Cache Manager，默认情况下的 CacheManager 是 ConcurrentMapCacheManager。

## 整合 Caffeine Cache

经过前面的实验我们基本知道了 Spring Cache 相关注解的用法，
下面我们用 Caffeine Cache 代替默认的 ConcurrentHashMap 缓存。

1. 引入依赖

```
<!--使用 Caffeine Cache-->
<dependency> 
    <groupId>com.github.ben-manes.caffeine</groupId> 
    <artifactId>caffeine</artifactId>  
    <version>2.6.2</version>
</dependency>
```
引入依赖后再次运行测试类，可以看到此时的 Cache Manager 已经变成了 CaffeineCacheManager。

2. 增加配置类

```
@Configuration
public class CacheConfig {   

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());   
    
    /**     
    * 创建CaffeineCacheManager 
    * 初始化一些key存入    
    *     
    * @return caffeineCacheManager   
    */    
    @Bean
    //     @Primary  
    public CaffeineCacheManager caffeineCacheManager() {    
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();        
        caffeineCacheManager.setCacheNames(Arrays.asList("userCache"));    
        caffeineCacheManager.setCaffeine(
            Caffeine.newBuilder()          
            .recordStats()                     
            .expireAfterWrite(5, TimeUnit.SECONDS)                   
            .maximumSize(1000)
        );     
        return caffeineCacheManager;   
    }
}
```

3. 测试类中增加缓存过期相关代码

```
@Test
public void testUserCache() throws Exception {  

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

    logger.info(cacheManager.getCacheNames().toString());    
    logger.info(cacheManager.toString());
}
```

4. 运行结果如下图


![](https://github.com/yueyakun2017/my-demo/blob/main/spring-cache/2020-10-20-spring-cache-1.png)

从运行结果可以看出

    * 第一次查询走了查询接口，第二次查询时缓存生效所以没有走查询接口。
    
    * 执行更新接口后再进行查询，没有走查询接口，且查询到的数据是更新后的，说明更新接口的结果也被更新到缓存中了。
    
    * 等缓存过期后再进行查询，走了查询接口，说明 Caffeine 的定时驱逐策略生效。
    
    * 执行删除接口后再进行查询，走了查询接口，说明删除时也同时删除了缓存中的数据。
    
    * ache Manager 已经由默认情况下的 ConcurrentMapCacheManager 变成了 CaffeineCacheManager。
    
## 总结

在项目中引入 Spring Cache 可以帮助我们很方便的实现缓存开发，再加上 Caffeine 的加持，轻松实现高性能单机缓存。测试代码已上[GitHub](https://github.com/yueyakun2017/my-demo)

另外再补充一点 SPEL 的知识，方便定义缓存的 key 和 condition 时查看。

Spring Cache提供了一些供我们使用的SpEL上下文数据

|名称|位置|描述|示例|
|-|-|-|-|
|methodName|root对象|当前被调用的方法名|#root.methodname|
|method|root对象|当前被调用的方法|#root.method.name|
|target|root对象|当前被调用的目标对象实例|#root.target|
|targetClass|root对象|当前被调用的目标对象的类|#root.targetClass|
|args|root对象|当前被调用的方法名|#root.args[0]|
|caches|root对象|当前被调用的方法名|#root.caches[0].name|
|Argument Name|执行上下文|当前被调用的方法的参数，如findArtisan(Artisan artisan),可以通过#artsian.id获得参数|#artsian.id|
|result|执行上下文|方法执行后的返回值（仅当方法执行后的判断有效，如 unless cacheEvict的beforeInvocation=false）|#result|

注意：

1. 当我们要使用root对象的属性作为key时我们也可以将“#root”省略，因为Spring默认使用的就是root对象的属性。 如
    
    @Cacheable(key = "targetClass + methodName +#p0")
    
2. 使用方法参数时我们可以直接使用“#参数名”或者“#p参数index”。 如：
    
    @Cacheable(value="userCache", key="#id")
    @Cacheable(value="userCache", key="#p0")
    
SpEL的运算符

|类型|运算符|
|-|-|
|关系|<，>，<=，>=，==，!=，lt，gt，le，ge，eq，ne|
|算数|+，- ，* ，/，%，^|
|逻辑|&&，||，!，and，or，not，between，instanceof|
|条件|?: (ternary)，?: (elvis)|
|正则|matches|
|其他|?.，?[…]，![…]，^[…]，$[…]|
