package com.yue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication()
public class RabbitApplication {
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(RabbitApplication.class, args);

	}
}
