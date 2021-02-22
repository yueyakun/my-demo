package com.fxg.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;

/**
 * Web 相关配置
 */
@Configuration("default-fxg-web-config")
public class WebConfig implements WebMvcConfigurer {


	@Bean
	public StringHttpMessageConverter stringConverter() {
		StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
		stringConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_PLAIN));
		return stringConverter;
	}
}
