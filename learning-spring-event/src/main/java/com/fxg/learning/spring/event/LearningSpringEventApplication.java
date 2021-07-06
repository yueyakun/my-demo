package com.fxg.learning.spring.event;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude={DruidDataSourceAutoConfigure.class})
@EnableTransactionManagement
@MapperScan("com.fxg.learning.spring.event.mapper")
@EnableAsync
public class LearningSpringEventApplication {

	public static void main(String[] args) {
		SpringApplication.run(LearningSpringEventApplication.class, args);
	}

}
